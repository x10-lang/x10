
/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package x10.runtime.cws;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;


/**
 * The host and external interface for ForkJoinTasks. A ForkJoinPool
 * manages a group of specialized worker threads that perform
 * ForkJoinTasks. It also provides the entry point for tasks submitted
 * from non-ForkJoinTasks.
 *
 * <p> Class ForkJoinPool does not implement the ExecutorService
 * interface because it only executes ForkJoinTasks, not arbitrary
 * Runnables. However, for the sake of uniformity, it supports all
 * ExecutorService lifecycle control methods (such as shutdown).
 */
public class Pool {
    /*
     * This is an overhauled version of the framework described in "A
     * Java Fork/Join Framework" by Doug Lea, in, Proceedings, ACM
     * JavaGrande Conference, June 2000
     * (http://gee.cs.oswego.edu/dl/papers/fj.pdf). It retains most of
     * the basic structure, but includes a number of algorithmic
     * improvements, along with integration with other
     * java.util.concurrent components.
     */

    /**
     * Main lock protecting access to threads and run state. You might
     * think that having a single lock and condition here wouldn't
     * work so well. But serializing the starting and stopping of
     * worker threads (its main purpose) helps enough in controlling
     * startup effects, contention vs dynamic compilation, etc to be
     * better than alternatives.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Condition triggered when new work is available so workers
     * should awaken if blocked. Also used for timed waits when work
     * is known to be available but repeated steal attempts fail
     */
    private final Condition work = lock.newCondition();

    /**
     * The amount of time to block on "work" condition when a thread
     * repeatedly fails to be able to get or steal a task yet
     * activeJobs is nonzero (thus indicating that there might
     * again be work available in the future). As a rough guide,
     * this should be around twice the time for a full park/unpark
     * context switch.
     */
    private static final long IDLE_SLEEP_NANOS = 100L * 1000 * 1000;

    /**
     * The number of times a worker should call getJob, yielding on
     * failure, before sleeping . This value is a compromise between
     * responsiveness and good citizenship. Too small leads to
     * needless sleeps while other threads are just momentarily
     * delayed in GC etc.  Too big leads to CPU wastage
     */
    private static final int YIELDS_BEFORE_SLEEP = 64;

    /**
     * Tracks whether pool is running, shutdown, etc. Modified
     * only under lock, but volatile to allow concurrent reads.
     */
    private volatile int runState;

    // Values for runState
    static final int RUNNING    = 0;
    static final int SHUTDOWN   = 1;
    static final int STOP       = 2;
    static final int TERMINATED = 3;

    /**
     * The pool of threads. Currently, all threads are created
     * upon construction. However, all usages of workers array
     * are prepared to see null entries, allowing alternative
     * schemes in which workers are added more lazily.
     */
    private final Worker[] workers;

    
    /**
     * The number of jobs that are currently executing in the pool.
     * This does not include those jobs that are still in job queue
     * waiting to be taken by workers.
     */
    private int activeJobs = 0;

    /** The number of workers that have not yet terminated */
    private int runningWorkers = 0;

    /**
     * Condition for awaitTermination. Triggered when
     * activeWorkers reaches zero.
     */
    private final Condition termination = lock.newCondition();

    /**
     * The uncaught exception handler used when any worker
     * abrupty terminates
     */
    private volatile Thread.UncaughtExceptionHandler ueh;

    /**
     * Creates a ForkJoinPool with a pool size equal to the number of
     * processors available on the system.
     */
    public Pool() {
        this(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Creates a ForkJoinPool with the indicated number
     * of Worker threads.
     */
    public Pool(int poolSize) {
        if (poolSize <= 0) throw new IllegalArgumentException();
        Worker.workers = workers = new Worker[poolSize];
        lock.lock();
        try {
            for (int i = 0; i < poolSize; ++i) {
                Worker r = new Worker(this, i);
              
                workers[i] = r;
                r.start();
                ++runningWorkers;
            }
        } finally {
            lock.unlock();
        }
    }
  
    /**
     * Sets the handler for internal worker threads that terminate due
     * to uncaught Errors or other unchecked Throwables encountered
     * while executing tasks. Since such errors are not in generable
     * recoverable, they are not managed by ForkJoinTasks themselves,
     * but instead cause threads to die, invoking the handler, and
     * then being replaced.  In many usage contexts, it is probably a
     * better ideas to install a handler to either shutdown the pool
     * or exit the program when such errors occur.  Unless set, the
     * current default or ThreadGroup handler is used as handler.
     * @param h the new handler
     * @return the old handler, or null if none
     */
    public Thread.UncaughtExceptionHandler setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler h) {
        final ReentrantLock lock = this.lock;
        Thread.UncaughtExceptionHandler old = null;
        lock.lock();
        try {
            old = ueh;
            ueh = h;
            for (int i = 0; i < workers.length; ++i) {
                Worker w = workers[i];
                if (w != null)
                    w.setUncaughtExceptionHandler(h);
            }
        } finally {
            lock.unlock();
        }
        return old;
    }

    /**
     * Returns the number of worker threads in this pool.
     *
     * @return the number of worker threads in this pool
     */
    public int getPoolSize() {
        return workers.length;
    }

    /**
     * Returns <tt>true</tt> if this pool has been shut down.
     *
     * @return <tt>true</tt> if this pool has been shut down
     */
    public boolean isShutdown() {
        return runState >= SHUTDOWN;
    }

    /**
     * Returns <tt>true</tt> if all tasks have completed following shut down.
     * Note that <tt>isTerminated</tt> is never <tt>true</tt> unless
     * either <tt>shutdown</tt> or <tt>shutdownNow</tt> was called first.
     *
     * @return <tt>true</tt> if all tasks have completed following shut down
     */
    public boolean isTerminated() {
        return runState == TERMINATED;
    }

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     */
    public void shutdown() {
        // todo security checks??
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (runState < SHUTDOWN) {
                
                    runState = SHUTDOWN;
                
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Attempts to stop all actively executing tasks, and halts the
     * processing of waiting tasks.
     */
    public void shutdownNow() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
           // tryTerminate();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return <tt>true</tt> if this executor terminated and
     *         <tt>false</tt> if the timeout elapsed before termination
     * @throws InterruptedException if interrupted while waiting
     */
    public boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            for (;;) {
                if (runState == TERMINATED)
                    return true;
                if (nanos <= 0)
                    return false;
                nanos = termination.awaitNanos(nanos);
            }
        } finally {
            lock.unlock();
        }
    }
/*
    private void tryTerminate() { // called only under lock
    	//System.out.println("Trying to terminate...");
        if (runState < STOP) {
            runState = STOP;
            ForkJoinTask<?> task;
            while ((task = jobs.poll()) != null)
                task.cancel();
            for (int i = 0; i < workers.length; ++i) {
                Worker t = workers[i];
                if (t != null) {
                    t.cancelTasks();
                    t.interrupt();
                }
            }
            work.signalAll();
        }
    }

   */
        

    // Internal methods that may be invoked by workers

    final boolean isStopped() {
        return runState >= STOP;
    }

    /**
     * Return the workers array; needed for random-steal by Workers.
     */
    final Worker[] getWorkers() {
        return workers;
    }

   public void submit(Closure c) {
	   Worker ws = workers[0];
	   Thread me = Thread.currentThread();
	   ws.lock(me);
	   try {
		   ws.addBottom(c);
	   } finally {
		   ws.unlock();
	   }
   }
   
    /**
     * Termination callback from dying worker.
     */
    final void workerTerminated(Worker r, int index) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (runState >= STOP) {
                if (--runningWorkers <= 0) {
                    runState = TERMINATED;
                    termination.signalAll();
                }
            }
            else if (index >= 0 && index < workers.length &&
                     workers[index] == r) {
                Worker replacement = new Worker(this, index);
                if (ueh != null)
                    replacement.setUncaughtExceptionHandler(ueh);
                workers[index] = replacement;
                replacement.start();
            }
        } finally {
            lock.unlock();
        }
    }

  
}
