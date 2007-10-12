

/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

//import jsr166y.forkjoin.ForkJoinTask;

/**
 * A synchronization barrier for ForkJoinTasks.  A TaskBarrier is
 * similar in functionality to a {@link
 * java.util.concurrent.CyclicBarrier}, but differs in the following
 * ways.
 *
 * <ol>
 *
 * <li> The number of parties may vary over time.  A task may register
 * to be a party in a barrier at any time (but almost always before
 * being forked), and may deregister upon arriving at the barrier.  As
 * is the case with most other basic synchronization constructs,
 * registration and deregistration affect only internal counts; they
 * do not establish any further internal bookkeeping.
 *
 * <li> TaskBarriers support split-phase operation: The two aspects of
 * coordination, arriving at a barrier (<tt>arrive</tt>) and awaiting
 * others (<tt>awaitCycleAdvance</tt>) are independently supported.
 * Like a <tt>CyclicBarrier</tt>, A <tt>TaskBarrier</tt> may be
 * repeatedly awaited. Each cycle has an associated value, returned
 * from <tt>arrive</tt>, that be used as an argument to
 * <tt>awaitCycleAdvance</tt>. Method <tt>arriveAndAwait</tt>
 * conveniently combines these. (In descriptions below, a task is
 * termed "active" unless it has arrived at a barrier but has not
 * advanced via <tt>awaitCycleAdvance</tt>, or has deregistered.)
 *
 * <li> TaskBarriers may enter a <em>termination</em> state in which
 * all await actions immediately return, indicating (via a negative
 * cycle value) that execution is complete. Termination is triggered
 * by executing the overridable <tt>terminate</tt> method that is
 * executed each time the barrier is tripped.
 *
 * <li> TaskBarriers may be used only by ForkJoinTasks.  Coordination
 * operations (<tt>arriveAndAwait</tt> and <tt>awaitCycleAdvance</tt>)
 * need not block, but instead help other tasks make progress.  While
 * not dynamically enforced, only registered tasks may invoke methods
 * <tt>arrive</tt>, <tt>arriveAndAwait</tt>, and
 * <tt>arriveAndDeregister</tt>. However any ForkJoinTask may invoke
 * <tt>awaitCycleAdvance</tt>. And any caller may invoke status
 * methods such as <tt>getParties</tt> for the sake of monitoring and
 * debugging.
 *
 * </ol>
 *
 * <p> A TaskBarrier may be used to support a style of programming in
 * which a task waits for others to complete, without otherwise
 * needing to keep track of which tasks it is waiting for. This is
 * similar to the "sync" construct in Cilk and "clocks" in X10.
 * Special constructions based on such barriers are available using
 * the <tt>LinkedAsyncAction</tt> and <tt>CyclicAction</tt> classes, but
 * they can be useful in other contexts as well.  For a simple (but
 * not very useful) example, here is a variant of Fibonacci:
 *
 * <pre>
 * class BarrierFibonacci extends RecursiveAction {
 *   int argument, result;
 *   final TaskBarrier parentBarrier;
 *   BarrierFibonacci(int n, TaskBarrier parentBarrier) {
 *     this.argument = n;
 *     this.parentBarrier = parentBarrier;
 *     parentBarrier.register();
 *   }
 *   protected void compute() {
 *     int n = argument;
 *     if (n &lt;= 1)
 *        result = n;
 *     else {
 *        TaskBarrier childBarrier = new TaskBarrier(1);
 *        Fibonacci f1 = new Fibonacci(n - 1, childBarrier);
 *        Fibonacci f2 = new Fibonacci(n - 2, childBarrier);
 *        f1.fork();
 *        f2.fork();
 *        childBarrier.arriveAndAwait();
 *        result = f1.result + f2.result;
 *     }
 *     parentBarrier.arriveAndDeregister();
 *   }
 * }
 * </pre>
 *
 * <p><b>Implementation notes</b>: This implementation restricts the
 * maximum number of registered parties to 65535. Attempts to register
 * additional parties result in IllegalStateExceptions.  
 */
class TaskBarrier {
    /*
     * This class implements a form of X10 "clocks".  Thanks to Vijay
     * Saraswat for the idea of applying it to ForkJoinTasks.
     *
     * Conceptually, a barrier contains three values:
     * nParties -- the number of parties to wait (16 bits)
     * nActive -- the number of parties yet to hit barrier (16 bits)
     * cycle -- the generation of the barrier (32 bits)
     * However, to efficiently maintain atomicity, these values
     * are packed into a single AtomicLong.
     */
    private final AtomicLong state;

    private static final int ushortBits = 16;
    private static final int ushortMask  =  (1 << ushortBits) - 1;

    private static int nActiveOf(long s) {
        return (int)(s & ushortMask);
    }

    private static int nPartiesOf(long s) {
        return (int)(s & (ushortMask << 16)) >>> 16;
    }

    private static int cycleOf(long s) {
        return (int)(s >>> 32);
    }

    private static long stateFor(int cycle, int nParties, int nActive) {
        return (((long)cycle) << 32) | ((nParties << 16) | nActive);
    }

    private static final int TERMINATED = -1;

    /**
     * Trip the barrier, checking for termination
     */
    private void trip(int cycle, int nParties) {
        if (cycle >= 0) {
            if (terminate(cycle, nParties))
                cycle = TERMINATED;
            else if (++cycle < 0)
                cycle = 0; // wrap back positive
        }
        state.set(stateFor(cycle, nParties, nParties));
    }

    /**
     * Creates a new barrier without any initially registered parties.
     */
    public TaskBarrier() {
        this(0);
    }

    /**
     * Creates a new barrier with the given numbers of registered
     * active parties.
     * @param parties the number of parties required to trip barrier.
     * @throws IllegalArgumentException if parties less than zero
     * or greater than the maximum number of parties supported.
     */
    public TaskBarrier(int parties) {
        if (parties < 0 || parties > ushortMask)
            throw new IllegalArgumentException("Too many parties");
        state = new AtomicLong(stateFor(0, parties, parties));
    }

    /**
     * Adds a new active party to the barrier.
     * @return the current barrier cycle number upon registration
     */
    public int register() {
        // increment both parties and active
        for (;;) {
            long s = state.get();
            int nParties = nPartiesOf(s) + 1;
            if (nParties > ushortMask)
                throw new IllegalStateException("Too many parties");
            int nActive = nActiveOf(s) + 1;
            int cycle = cycleOf(s);
            long next = stateFor(cycle, nParties, nActive);
            if (state.compareAndSet(s, next))
                return cycle;
        }
    }

    /**
     * Arrives at the barrier, but does not wait for others.  (You can
     * in turn wait for others via {@link #awaitCycleAdvance}).
     *
     * @return the current barrier cycle number upon entry to
     * this method, or a negative value if terminated;
     */
    public int arrive() {
        // decrement active. If zero, increment cycle and reset active
        for (;;) {
            long s = state.get();
            int cycle = cycleOf(s);
            int nParties = nPartiesOf(s);
            int nActive = nActiveOf(s) - 1;
            if (nActive > 0) {
                long next = stateFor(cycle, nParties, nActive);
                if (state.compareAndSet(s, next))
                    return cycle;
            }
            else {
                trip(cycle, nParties);
                return cycle;
            }
        }
    }

    /**
     * Arrives at the barrier, and deregisters from it.
     *
     * @return the current barrier cycle number upon entry to
     * this method, or a negative value if terminated;
     */
    public int arriveAndDeregister() {
        // Same as arrive except also decrement parties
        for (;;) {
            long s = state.get();
            int cycle = cycleOf(s);
            int nParties = nPartiesOf(s) - 1;
            if (nParties < 0)
                throw new IllegalStateException("Unregistered deregistration");
            int nActive = nActiveOf(s) - 1;
            if (nActive > 0) {
                long next = stateFor(cycle, nParties, nActive);
                if (state.compareAndSet(s, next))
                    return cycle;
            }
            else {
                trip(cycle, nParties);
                return cycle;
            }
        }
    }

    /**
     * Awaits the cycle of the barrier to advance from the given
     * value, by helping other tasks.
     * @param cycle the cycle on entry to this method
     * @return the cycle on exit from this method
     */
    public int awaitCycleAdvance(int cycle) {
        for (;;) {
            int p = getCycle();
            if (p != cycle || p < 0)
                return p;
            /// vj no helping!
           /*ForkJoinTask<?> t = null; // vj -- no helping!!
            if (t != null) {
                p = getCycle();
                if (p != cycle) { // if barrier advanced
                    t.fork();     // push task and exit
                    return p;
                }
                else
                    t.exec();
            }*/
        }
    }

    /**
     * Arrives at the barrier and awaits others. Unlike other
     * arrival methods, this method returns the arrival
     * index of the caller. The caller tripping the barrier
     * returns zero, the previous caller 1, and so on.
     * This enables creation of barrier actions by the task
     * tripping the barrier using constructions of the form:
     * <code>if (b.arriveAndAwait()== 0) action(); b.arriveAndAwait();</code>
     * @return the arrival index
     */
    public int arriveAndAwait() {
        for (;;) {
            long s = state.get();
            int cycle = cycleOf(s);
            int nParties = nPartiesOf(s);
            int nActive = nActiveOf(s) - 1;
            if (nActive > 0) {
                long next = stateFor(cycle, nParties, nActive);
                if (state.compareAndSet(s, next)) {
                    awaitCycleAdvance(cycle);
                    return nActive;
                }
            }
            else {
                trip(cycle, nParties);
                return 0;
            }
        }
    }
    
    AtomicBoolean[] value = new AtomicBoolean[2];
    {  // instance initializer
    	value[0]=new AtomicBoolean(false); 
    	value[1] = new AtomicBoolean(false);
    }
    public boolean arriveAndAwaitData(boolean datum) {
    	
    	 for (;;) {
             long s = state.get();
             int cycle = cycleOf(s);
             int nParties = nPartiesOf(s);
             int nActive = nActiveOf(s) - 1;
             if (nActive > 0) {
            	 if (datum)
            		 value[cycle % 2].getAndSet(true);
                 long next = stateFor(cycle, nParties, nActive);
                 if (state.compareAndSet(s, next)) {
                     awaitCycleAdvance(cycle); 
                     return value[cycle % 2].get(); 
                 }
             }
             else {
            	 value[(cycle+1)%2].getAndSet(false); // initialize for next round.
                 trip(cycle, nParties); 
                 return value[cycle%2].get(); 
             }
         }
    }

    /**
     * Returns the current cycle number. The maximum cycle number is
     * <tt>Integer.MAX_VALUE</tt>, after which it restarts at
     * zero. Upon termination, the cycle number is negative.
     * @return the cycle number, or a negative value if terminated
     */
    public int getCycle() {
        return cycleOf(state.get());
    }

    /**
     * Returns the number of parties registered at this barrier.
     * @return the number of parties
     */
    public int getRegisteredParties() {
        return nPartiesOf(state.get());
    }

    /**
     * Returns the number of parties that have not yet arrived at the
     * current cycle of this barrier.
     * @return the number of active parties
     */
    public int getActiveParties() {
        return nActiveOf(state.get());
    }

    /**
     * Returns true if this barrier has been terminated
     * @return true if this barrier has been terminated
     */
    public boolean isTerminated() {
        return cycleOf(state.get()) < 0;
    }

    /**
     * Overridable method to control termination. This method is
     * invoked whenever the barrier is tripped. If it returns true,
     * then this barrier is set to a final termination state, and
     * subsequent calls to <tt>getCycle</tt> and related methods
     * return negative values.
     * 
     * <p> The default version returns true only when the number
     * of registered parties is zero.
     * @param cycle the cycle count on entering the barrier
     * @param registeredParties the current number of registered
     * parties.
     */
    protected boolean terminate(int cycle, int registeredParties) {
        return registeredParties <= 0;
    }

    /**
     * Returns a string identifying this barrier, as well as its
     * state.  The state, in brackets, includes the String {@code
     * "Cycle ="} followed by the cycle number, {@code "parties ="}
     * followed by the number of registered parties, and {@code
     * "acivie ="} followed by the number of active parties
     *
     * @return a string identifying this barrier, as well as its state
     */
    protected String name() {
    	return super.toString();
    }
    public String toString() {
        long s = state.get();
        return name() + "[Cycle = " + cycleOf(s) + " parties = " + nPartiesOf(s) + " active = " + nActiveOf(s) + "]";
    }

}

