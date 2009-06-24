/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;


import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.*;

public class ModCountDownLatch {
    /**
     * @author Raj Barik, Vivek Sarkar
     * 3/6/2006: extension of JCU CountDownLatch to allow for increments (so as to support finish).
     * Use AQS state to represent the count.
     */
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }
        
        // Update the state
        public void updateCount() {
        	// Increment count -- loop till the update is successful
        	// Modeled after tryReleaseShared()
            for (;;) {
                int c = getState();
                int nextc = c+1;
                if (compareAndSetState(c, nextc)) return; // Success!
            }       	
        }

        public int tryAcquireShared(int acquires) {
            return getState() == 0? 1 : -1;
        }

        public boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

    private final Sync sync;
    /**
     * Constructs a <tt>CountDownLatch</tt> initialized with the given
     * count.
     *
     * @param count the number of times {@link #countDown} must be invoked
     * before threads can pass through {@link #await}.
     *
     * @throws IllegalArgumentException if <tt>count</tt> is less than zero.
     */
    public ModCountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.sync = new Sync(count);
    }

    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is {@link Thread#interrupt interrupted}.
     *
     * <p>If the current {@link #getCount count} is zero then this method
     * returns immediately.
     * <p>If the current {@link #getCount count} is greater than zero then
     * the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until one of two things happen:
     * <ul>
     * <li>The count reaches zero due to invocations of the
     * {@link #countDown} method; or
     * <li>Some other thread {@link Thread#interrupt interrupts} the current
     * thread.
     * </ul>
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@link Thread#interrupt interrupted} while waiting,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * @throws InterruptedException if the current thread is interrupted
     * while waiting.
     */
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is {@link Thread#interrupt interrupted},
     * or the specified waiting time elapses.
     *
     * <p>If the current {@link #getCount count} is zero then this method
     * returns immediately with the value <tt>true</tt>.
     *
     * <p>If the current {@link #getCount count} is greater than zero then
     * the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until one of three things happen:
     * <ul>
     * <li>The count reaches zero due to invocations of the
     * {@link #countDown} method; or
     * <li>Some other thread {@link Thread#interrupt interrupts} the current
     * thread; or
     * <li>The specified waiting time elapses.
     * </ul>
     * <p>If the count reaches zero then the method returns with the
     * value <tt>true</tt>.
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@link Thread#interrupt interrupted} while waiting,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * <p>If the specified waiting time elapses then the value <tt>false</tt>
     * is returned.
     * If the time is
     * less than or equal to zero, the method will not wait at all.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the <tt>timeout</tt> argument.
     * @return <tt>true</tt> if the count reached zero and <tt>false</tt>
     * if the waiting time elapsed before the count reached zero.
     *
     * @throws InterruptedException if the current thread is interrupted
     * while waiting.
     */
    public boolean await(long timeout, TimeUnit unit)
        throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads if
     * the count reaches zero.
     * <p>If the current {@link #getCount count} is greater than zero then
     * it is decremented. If the new count is zero then all waiting threads
     * are re-enabled for thread scheduling purposes.
     * <p>If the current {@link #getCount count} equals zero then nothing
     * happens.
     */
    public void countDown() {
    	if ( VMInterface.ABSTRACT_EXECUTION_STATS ) {
		    // Method finalizeTermination() in Activity.java ensures that the activity's ideal execution ops & time values are updated 
		    // before the call to notifySubActivityTermination() occurs
    		maxCritPathOps(x10.runtime.Runtime.getCurrentActivity().getCritPathOps());
    		if ( VMInterface.ABSTRACT_EXECUTION_TIMES )
    			maxIdealTime(x10.runtime.Runtime.getCurrentActivity().getCritPathTime());
    	}
        sync.releaseShared(1);
    }
    
	/*
	 * critPathOps keeps track of operations defined by user by calls to x10.lang.perf.addLocalOps()
	 */
    private long critPathOps = 0; // Current critical path length for this latch (in user-defined operations)
	
	synchronized public void maxCritPathOps(long n) {
		critPathOps = Math.max(critPathOps, n);
	}

    /**
     * getCritPathOps() shoudl only be called after latch has counted down to zero.
     * (That's why it need not be a synchronized method.)
     */
    public long getCritPathOps() { return critPathOps; }
    
    private long curIdealTime = 0; // Current "ideal" execution time for this latch (assuming unbounded resources)
	
	synchronized public void maxIdealTime(long t) { curIdealTime = Math.max(curIdealTime, t); }

    /**
     * getIdealTime() shoudl only be called after latch has counted down to zero.
     * (That's why it need not be a synchronized method.)
     */
    public long getIdealTime() { return curIdealTime; }
	
   
    /**
     * Updates the counter of the synch variable dynamically
     * Update only if the counter value is > 0
     * @return -1 for error
     * @return current set counter value 
     */
    public void updateCount() {
    	sync.updateCount();
    }

    /**
     * Returns the current count.
     * <p>This method is typically used for debugging and testing purposes.
     * @return the current count.
     */
    public long getCount() {
        return sync.getCount();
    }

    /**
     * Returns a string identifying this latch, as well as its state.
     * The state, in brackets, includes the String
     * &quot;Count =&quot; followed by the current count.
     * @return a string identifying this latch, as well as its
     * state
     */
    public String toString() {
        return super.toString() + "[Count = " + sync.getCount() + "]";
    }

}
