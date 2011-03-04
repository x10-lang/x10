
/*
 * A pool of workers for executing X10 programs.
 * 
 * Large portions of code adapted from Doug Lea's jsr166y library, which code
 * carries the header: 
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 * 
 * The design of this library is based on the Cilk runtime, developed by the Cilk
 * group at MIT.
 * 
 */
package x10.runtime.cws;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
     * Tracks whether pool is running, shutdown, etc. Modified
     * only under lock, but volatile to allow concurrent reads.
     */
    private volatile int runState;

    // Values for runState
    static final int RUNNING    = 0;
    static final int SHUTDOWN   = 1;
    static final int STOP       = 2;
    static final int TERMINATED = 3;
  
    AtomicInteger joinCount = new AtomicInteger();

    /**
     * The pool of threads. Currently, all threads are created
     * upon construction. However, all usages of workers array
     * are prepared to see null entries, allowing alternative
     * schemes in which workers are added more lazily.
     */
     final Worker[] workers;

    
    /**
     * The number of jobs that are currently executing in the pool.
     * This does not include those jobs that are still in job queue
     * waiting to be taken by workers.
     */
    int activeJobs = 0;

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
     * The current job being executed by workers in the pool.
     * Set by the worker that retrieves the job from the pool.
     * Unset by the worker that detects quiescence.
     */
    volatile Job currentJob;

    ActiveWorkerCount barrier;
    
    /**
     * Creates a ForkJoinPool with a pool size equal to the number of
     * processors available on the system.
     */
    public Pool() {
        this(Runtime.getRuntime().availableProcessors());
        
    }

    long time() {
    	if (currentJob==null) return 0L;
    	return ((System.nanoTime() - currentJob.startTime)/1000000);
    }
    
    /**
     * Creates a ForkJoinPool with the indicated number
     * of Worker threads.
     */
    public interface WorkerMaker {
    	Worker makeWorker(Pool p, int index);
    }
    public static final WorkerMaker defaultMaker = new WorkerMaker() { 
    	public Worker makeWorker(Pool p, int index) {
    		return new Worker(p,index);
    	}
    };
    public Pool(int poolSize) {
    	this(poolSize, defaultMaker);
    }
    public Pool(final int poolSize, final WorkerMaker m) {
        if (poolSize <= 0) throw new IllegalArgumentException();
        Worker.workers = workers = new Worker[poolSize];
        barrier = new ActiveWorkerCount(new Runnable() { 
        	public void run() {
        		
        		Job job = currentJob;
        		//System.out.println();
        		//System.out.println(Thread.currentThread() + " in barrier w/ " + job);
        		currentJob=null;
        		//printStats();
        		if (job != null && job.requiresGlobalQuiescence()) {
        			job.completed();
        		}
        		
        		
        	}
        }, poolSize);
        lock.lock();
        try {
            for (int i = 0; i < poolSize; ++i) {
                Worker r = m.makeWorker(this, i);
                workers[i] = r;
            }
            for (int i = 0; i < poolSize; ++i) {
            	workers[i].start();
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
    AtomicInteger activeOnJob = new AtomicInteger();
    public int activeOnJob() {
    	return activeOnJob.intValue();
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
    
    public  void submit(Job job) {
    	job.startTime = System.nanoTime();
        addJob(job);
    }
    public void invoke(Job job) {
    	submit(job);
    	try {
			job.waitForCompletion();
			currentJob=null;
		} catch (InterruptedException z) {}
		job.completionTime = System.nanoTime();
    }

    /**
     * Enqueue an externally submitted task
     */
    private void addJob(Job job) {
        final ReentrantLock lock = this.lock;
        boolean ok;
        lock.lock();
        try {
            if (ok = (runState == RUNNING)) {
                jobs.add(job);
                work.signalAll();
            }
        } finally {
            lock.unlock();
        }
        if (!ok)
            throw new RejectedExecutionException();
    }

    /**
     * Returns the total number of tasks stolen from one thread's work
     * queue by another. This value is only an approximation,
     * obtained by iterating across all threads in the pool, but may
     * be useful for monitoring and tuning fork/join programs.
     * @return the number of steals.
     */
    public long getStealCount() {
        long sum = 0;
        for (int i = 0; i < workers.length; ++i) {
            Worker t = workers[i];
            if (t != null) 
                sum += t.stealCount;
        }
        return sum;
    }
    /**
     * Returns the total number of steal attempts made by all workers.
     * This value is only an approximation,
     * obtained by iterating across all threads in the pool, but may
     * be useful for monitoring and tuning fork/join programs.
     * @return the number of steal attempts.
     */
    public long getStealAttempts() {
    	long sum = 0;
        for (int i = 0; i < workers.length; ++i) {
            Worker t = workers[i];
            if (t != null) 
                sum += t.stealAttempts;
        }
        return sum;
    }
    public void printStats() {
    	long stealCount = getStealCount();
    	
    	System.out.print("stealCount=" + stealCount + " ");
    	for (Worker w : workers) {
    		//System.out.println(w + ".stealCount=" + w.stealCount ); 
    		w.stealCount=0;
    	}
    }
    
    public void initFrameGenerator(Worker.FrameGenerator fg) {
        for (int i = 0; i < workers.length; ++i) {
            Worker t = workers[i];
            t.setFrameGenerator(fg);
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
    
    final void jobCompleted() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (--activeJobs <= 0 && runState == SHUTDOWN && jobs.isEmpty())
                tryTerminate();
        } finally {
            lock.unlock();
        }
    }
    public void tryTerminate() {
    	// do nothing.
    }
    
    /**
     * External submission queue.  "Jobs" are tasks submitted to the
     * pool, not internally generated.
     */
    private final JobQueue jobs = new JobQueue();
    /**
     * Returns a job to run, or null if none available.
     */
    final Job getJob() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
        	Job task = jobs.poll();
        	if (task == null && activeJobs ==0) {
        		work.await();
        		task=jobs.poll();
        	}
        	if (task !=null) {
        		++ activeJobs;
        		currentJob = task;
        		work.signalAll();
        	}
        	return task;
        } catch (InterruptedException e) {
        		return null; // ignore interrupt.
        } finally {
        	lock.unlock();
        }
    }
    /**
     * A JobQueue is a simple array-based circular queue.
     * Basically a stripped-down variant of ArrayDeque
     */
    static final class JobQueue {
        static final int INITIAL_JOBQUEUE_CAPACITY = 64;
        Job[] elements = (Job[]) new Job[INITIAL_JOBQUEUE_CAPACITY];
        int head;
        int tail;

        boolean isEmpty() {
            return head == tail;
        }

        void add(Job e) {
            elements[tail] = e;
            if ( (tail = (tail + 1) & (elements.length - 1)) == head)
                doubleCapacity();
        }

        Job poll() {
            int h = head;
            Job result = elements[h]; 
            if (result != null) {
                elements[h] = null;
                head = (h + 1) & (elements.length - 1);
            }
            return result;
        }
        
        void doubleCapacity() {
            int p = head;
            int n = elements.length;
            int r = n - p; 
            int newCapacity = n << 1;
            if (newCapacity < 0)
                throw new IllegalStateException("Job queue capacity exceeded");
            Job[] a = (Job[]) new Job[newCapacity];
            System.arraycopy(elements, p, a, 0, r);
            System.arraycopy(elements, 0, a, r, p);
            elements = a;
            head = 0;
            tail = n;
        }
    }
    /**
     * Returns true if all threads are currently idle.
     * @return true is all threads are currently idle
     */
    public boolean isQuiescent() {
        for (int i = 0; i < workers.length; ++i) {
            Worker t = workers[i];
            if (t != null && t.isActive())
                return false;
        }
        return true;
    }
}
