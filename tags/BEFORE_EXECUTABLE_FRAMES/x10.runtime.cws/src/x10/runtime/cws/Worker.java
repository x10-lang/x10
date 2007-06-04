/*
 *
 * (C) Copyright IBM Corporation 2007
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.runtime.cws;

import static x10.runtime.cws.Closure.Status.ABORTING;
import static x10.runtime.cws.Closure.Status.READY;
import static x10.runtime.cws.Closure.Status.RETURNING;
import static x10.runtime.cws.Closure.Status.RUNNING;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import x10.runtime.cws.Closure.Status;
import x10.runtime.cws.Job.GloballyQuiescentJob;

/**
 * The worker for Cilk-style work stealing. Instances of this worker
 * are created by Pool. 
 * API note: Code written by users of the work-stealing API is not intended
 * to subclass this class. Such code can invoke only the public methods of this
 * class. Such code must live outside the x10.runtime.cws package.
 * 
 * Code written by library writers that wish to extend work-stealing may 
 * subclass this class. 
 * 
 * Large portions of code adapted from Doug Lea's jsr166y library,which code
 * carries the header: 
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 * 
 * The design of this library is based on the Cilk runtime, developed by the Cilk
 * group at MIT.
 * 
 * @author vj 04/18/07
 *
 */
public class Worker extends Thread {
	protected final Pool pool;
	protected Closure top, bottom;
	final public Cache cache;
	protected Lock lock; // dequeue_lock
	protected Thread lockOwner; // the thread holding the lock.
	protected int randNext;
	protected int index;
	protected  boolean done;
	protected Closure closure;
	
	protected static  Worker[] workers; 
	public  long stealAttempts;
	public  long stealCount;
	public static  boolean reporting = false;
    /**
     * Number of scans since last successfully getting a task. Is
     * <= 0 when busy. Biased to start at -1 when getting a task
     * to cover cases interval between stealing and commencing a
     * task. Set to start at 1 on startup to reflect initial
     * idleness.
     */
    int idleScanCount;

    /**
     * Status tracking sleeps on idle scans. Normally zero,
     * negative while sleeping, positive when awakened.
     */
    volatile int sleepStatus;

    static final AtomicIntegerFieldUpdater<Worker> sleepStatusUpdater =
        AtomicIntegerFieldUpdater.newUpdater(Worker.class, "sleepStatus");

    // values for sleepStatus. Order among values matters.
    static final int AWAKE    =  0;
    static final int SLEEPING = -1;
    static final int WOKEN    =  1;

	 /**
     * Padding to avoid cache-line sharing across workers
     */
    int p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, pa, pb, pc, pd, pe, pf;
    
    /**
     * DL: The number of times to scan for tasks before
     * yileding/sleeping (and thereafter, between sleeps).  Must
     * be a power of two minus 1. Using short sleeps during times
     * when tasks should be available but aren't makes these
     * threads cope better with lags due to GC, dynamic
     * compilation, and multitasking.
     */
    private static final int SCANS_PER_SLEEP = 1;

    /**
     * DL: The amount of time to sleep per empty scan. Sleep durations
     * increase only arithmetically, as a compromise between
     * responsiveness and good citizenship.
     * 
     * The value here arranges that first sleep is approximately
     * the smallest value worth context switching out for on
     * typical platforms.
     */
    private static final long SLEEP_NANOS_PER_SCAN = 0; //(1 << 16);


	/**
	 * Creates new Worker.
	 */
	protected Worker(Pool pool, int index) {
		this.pool = pool;
		this.index = index;
		this.lock = new ReentrantLock();
		this.cache = new Cache(this);
		this.idleScanCount = 1;
		setDaemon(true);
		// Further initialization postponed to init()
	}
	
	
	protected void lock(Thread agent) {
		lock.lock();
		this.lockOwner=agent;
	}
	protected void unlock() {
		this.lockOwner=null;
		lock.unlock();
	}

	protected void setRandSeed(int seed) {
		randNext = seed;
	}
	protected int rand() {
		randNext = randNext*1103515245  + 12345;
		int result = randNext >> 16;
		if (result < 0) result = -result;
		return result;
	}
	/**
	 * Called by thief on victim. In the body of this method
	 * victim==this and thief==Thread.currentThread().
	 * @param thief -- The thread making this invocation.
	 * @return
	 */
	protected Closure steal(Worker thief, boolean retry) {
		final Worker victim = this;
		++stealAttempts;
		/*if (reporting) {
			System.out.println(thief + " attempts to steal from " + victim.index);
		}*/
		
		lock(thief);
		Closure cl=null;
		try {
			cl = peekTop(thief, victim);
			// vj: I believe the victim's ready deque should have only one
			// closure in it.
			//Closure cl1 = peekBottom(thief);
			//assert cl1==cl;
		} catch (Throwable z) {
			unlock();
			// wrap in an error.
			throw new Error(z); 
		}
		if (cl == null) {
			try {
				/*if (reporting) {
					System.out.println(thief + " steal attempt: queue empty on  " + thief.index);
				}*/
				return null;
			} finally {
				lock.unlock();
			}
		}
		
		cl.lock(thief);
		
		Status status = null;
		try {
			status = cl.status();
		} catch (Throwable z) {
			cl.unlock();
			unlock();
			throw new Error(z);
		}
		assert (status == ABORTING || status == READY || status == RUNNING || status == RETURNING);
		if (status == READY) {
			try {
				Closure res = extractTop(thief);
				assert (res == cl);
				thief.checkOut(res);
				if ( reporting) {
					System.out.println(thief + " steals stack[" + (victim.cache.head()-1) + "]= ready " + cl + " from "
							+ victim + " cache=" + victim.cache.dump());
				}
				res.copyFrame(thief);
				return res;
			} finally {
				cl.unlock();
				lock.unlock();
			}
		}
		if (status == RUNNING) {
			boolean b = false;
			try {
				b=cl.dekker(thief);
			} catch (Throwable z) {
				cl.unlock();
				unlock();
				throw new Error(z);
			}
			
			if (b) {
				Closure child = null;
				Closure res = null;
				try {
				// This needs to happen here, before the victim -- after discovering the 
					// theft -- has resumed processing. The protocol requires that the 
					// victim first grab its own lock. So there is no race condition on 
					// the frame.
					cl.copyFrame(thief);
					child = cl.promoteChild(thief, victim);
					
					res = extractTop(thief);
//					 I have work now, so checkout of the barrier.
					thief.checkOutSteal(res, victim);
				
				} finally {
					lock.unlock();
				}
				assert cl==res;
				
				/*if (reporting)
				System.out.println(thief + " Stealing: victim top=" + res + "bottom=" + bottom);*/
				if (  reporting) {
					System.out.println(thief + " steals stack[" + (victim.cache.head()-1) + "]= running " + cl + " from "
							+ victim + " cache=" + victim.cache.dump());
				}
				
				try {
					cl.finishPromote(thief, child);
					
				} finally {
					cl.unlock();
				}
				
				
				return res;
			} 
		}
		// this path taken when status == RETURNING, 
		// status==ABORTING, or status==RUNNING and dekker failed.
		lock.unlock();
		cl.unlock();
		return null;
		
	}

	/* 
	 * Extract the topmost closure in the ready deque of this
	 * worker. May return null.
	 * @aparam ws -- the current thread, i.e. Thread.currentThread()=ws
	 */
	protected Closure extractTop(Thread w) {
		assert lockOwner==w;
		Closure cl = top;
		if (cl == null) {
			assert bottom == null;
			return cl;
		}
		top = cl.nextReady;
		if (cl == bottom) {
			assert cl.nextReady == null;
			bottom = null;
		} else {
			assert cl.nextReady !=null;
			cl.nextReady.prevReady = null;
		}
		cl.ownerReadyQueue = null;
		return cl;
	}
	/**
	 * Return the top of the closure deque, without removing it.
	 * @return top of the closure deque
	 * @param ws -- the current thread, i.e. Thread.currentThread()==ws
	 */
	protected Closure peekTop(Worker agent, Worker subject) {
		assert lockOwner==agent;
		Closure cl = top;
		if (cl == null) {
			assert bottom==null;
			return null;
		}
		assert cl.ownerReadyQueue == subject;
		return cl;
	}
	/**
	 * Return the closure at the bottom fo the deque.
	 * Required: ws = Thread.currentThread()
	 * @return
	 */
	protected Closure extractBottom(Thread ws) {
		assert lockOwner==ws;
		Closure cl=bottom;
		if (cl == null) {
			assert top==null;
			return null;
		}
		assert cl.ownerReadyQueue ==ws;
		bottom = cl.prevReady;
		if (cl == top) {
			assert cl.prevReady==null;
			top=null;
		} else {
			assert cl.prevReady !=null;
			cl.prevReady.nextReady = null;
		}
		cl.ownerReadyQueue=null;
		return cl;
	}
	/**
	 * Peek at the closure at the bottom of the ready deque.
	 * @param ws -- The current thread, i.e. ws == Thread.currentThread().
	 * @return
	 */
	protected Closure peekBottom(Worker ws) {
		assert lockOwner==ws;
		Closure cl = bottom;
		if (cl==null) {
			assert top==null;
			return cl;
		}
		cl.ownerReadyQueue = this;
		return cl;
	}
	/**
	 * Add the given closure to the bottom of the
	 * worker's ready deque.
	 * @parame ws -- the current thread, i.e. ws==Thread.currentThread().
	 *               Note: current thread may not always be a Worker.
	 * @param cl -- the closure to be added.
	 */
	protected void addBottom(Thread ws, Closure cl) {
		
		assert lockOwner==ws;
		assert cl !=null;
		assert cl.ownerReadyQueue == null;
		
		/*if (reporting)
			System.out.println(ws + " adds " + cl + " to " + this + " bottom.");*/
		cl.prevReady = bottom;
		cl.nextReady = null;
		cl.ownerReadyQueue = this;
		bottom = cl;
		if (top == null) {
			top = cl;
		} else {
			cl.prevReady.nextReady = cl;
		}
	}
	
	/**
	 * A slow sync. Must be invoked only by the this Worker, i.e.
	 * Thread.currentThread() == this.
	 * @return true if the closure has to be suspended, false o.w.
	 */
	public boolean sync() {
		lock(this);
		try { 
			Closure t = peekBottom(this);
			t.lock(this);
			try { 
				assert t.status==RUNNING;
				
				// In slow sync. Therefore must be the case
				// that there is no frame on the stack.
				// i.e. this worker has finished executing any children asyncs. 
				// Other workers may still be working on children.
				assert t.cache.atTopOfStack();
				// Execute all completed inlets.
				// Note these are being executed in a single-threaded
				// context since the lock is held.
				t.pollInlets(this);
				// pollInlets may change bottom of queue, due to abort processing.
				assert t == bottom;
				
				if (t.hasChildren()) {
					// Suspend. Now any child that is joining
					// will get to check inlets and if they 
					// are all done, then execute this task
					// in place.
					if (reporting) {
						System.out.println(this + " suspends " + t + "(joincount=" + t.joinCount+")");
					}
					
					t.suspend(this);
					//  Vj: Added this popFrame. Note sure that Cilk does this. Without this
					// caches are left behind with unpopped frames. This interferes with
					// subsequent execution of other closures.
					popFrame();
					return true;
				}
			} finally {
				t.unlock();
			}
			
		} finally {
			unlock();
		}
		return false;
		
	}
	

	/**
	 * Tries to get a non-local task, stealing from other workers and/or the pool
	 * submission queue.
	 * @param mainLoop -- when true, try mainpool for work if stealing doesnt work.
	 * @return -- a task, or null if none is available
	 */
	private Closure getTask(boolean mainLoop) {
		/*if ( reporting) {
			System.out.println(this + " at  " + pool.time() + " looking for work idleScanCount= " + idleScanCount + 
					" checkedIn=" + checkedIn + " job=" + currentJob() + ".");
			System.out.println(this + " " + pool.barrier.numCheckedIn);
		}*/
		Closure cl = null;
		checkIn();
		if (++idleScanCount < 0)
			idleScanCount=1;
		
		/* From DL:
         * Scan through workers array twice starting at random
         * index. The first pass skips over those for which
         * stealTask reports contention. The second retries steals
         * even under contention.
         *
         * While traversing, the first failed attempted steal for
         * which the attempted victim is sleeping is recorded. If
         * a task is found in some other queue, and that queue
         * appears to have more tasks, that sleeper is woken up.
         * This propagates wakeups across workers when new work
         * becomes available to a set of otherwise idle threads.
         */
		Worker[] workers = pool.getWorkers();
		int n = workers.length;
		int idx = rand() % n;
		int origin = idx;
		Worker sleeper = null;
		Worker thief = this;
		boolean retry = false; // first pass skips on contention.
		for (;;) {
			
			Worker victim = workers[idx];
			if (victim != null && victim !=thief) {
				//System.out.println(thief +  " at " + pool.time() + ": tries to steal from " + victim + "...");
				cl = victim.steal(thief, retry);
				if (cl == null) {
					if ((sleeper == null) && // first sleeping worker
							victim.sleepStatus== SLEEPING)
						sleeper=victim;
					
				} else {
					idleScanCount = -1;
					++stealCount;
					if (sleeper !=null)
						sleeper.wakeup();
					
					return cl;
				}
			}
			if (++idx >= n) idx = 0;
			if (idx==origin) {
				if (! retry) 
					retry = true; 
				else 
					break;
				
			}
		}
		return mainLoop? getTaskFromPool(sleeper) : null;
	}
	
    /**
     * Try to get a task from pool. On failure, possibly sleep.
     * On success, try to wake up some other sleeping worker.
     * @param sleeper a worker noticed to be sleeping while scanning
     */
    private Closure getTaskFromPool(Worker sleeper) {
        Closure job = pool.getJob();
        if (job != null) {
            idleScanCount = -1;
            if (sleeper != null)
                sleeper.wakeup();
            checkOut(job);
            return job;
        }
        if (((idleScanCount + 1) & SCANS_PER_SLEEP) == 0) {
            if (sleepStatus == AWAKE &&
                sleepStatusUpdater.compareAndSet(this, AWAKE, SLEEPING)){
            	/*if (reporting)
            		System.out.println(this + " at " + pool.time() + " parking for " 
            				+ (idleScanCount * SLEEP_NANOS_PER_SCAN) + " nanos.");*/
                LockSupport.parkNanos(idleScanCount * SLEEP_NANOS_PER_SCAN);
               /* if (reporting)
                	System.out.println(this + " at " + pool.time() + " unparking.");*/
                if (sleepStatus == WOKEN) // reset count on wakeup
                    idleScanCount = 1;
            }
            sleepStatus = AWAKE;
        }
        return null;
    }
 ;
    boolean checkedIn = true;
    void checkIn() {
    	if (!checkedIn) {
    		/*if (reporting)
    			System.out.println(this + " at " + pool.time() + " checks in. Gotta find me some work!");*/
    		checkedIn = true;
    		pool.barrier.checkIn();
    	}
    }
    void checkOut(Closure cl) {
    	if (! checkedIn) {
    		Worker other = pool.getWorkers()[1-index];
    		/*if (reporting)
    			System.out.println(this + " at " + pool.time() + " tries to check out. checkedIn == false!! other.checkedIn=" 
    					+ other.checkedIn);*/
    		assert false;
    	}
    	if (checkedIn) {
    		/*if (reporting)
    			System.out.println(this + " at " + pool.time() + " checks out. Work to do! " + cl);*/
    		checkedIn = false;
    		pool.barrier.checkOut();
    	}
    }
    void checkOutSteal(Closure cl, Worker victim) {
    	assert victim.lockOwner==this;
    	checkOut(cl);
    }
    private void wakeup() {
        if (sleepStatus == SLEEPING && 
            sleepStatusUpdater.compareAndSet(this, SLEEPING, WOKEN)) {
        	/*if (reporting)
        	System.out.println(this + " at " + pool.time() + " is being unparked.");*/
            LockSupport.unpark(this);
        }
    }
	@Override
	public void run() {
		assert index >= 0;
		setRandSeed(index*162347);
		Closure cl = null; //closure.
		int yields = 0;
		while (!done) {
			
			if (cl == null) {
//				Try geting work from the local queue.
				lock(this);
				try {
					cl = extractBottom(this);
					/*if (((reporting )) && cl !=null) {
						System.out.println(this + " extracts " + cl + ".");
					}*/
				} finally {
					unlock();
				}
			}
			if (cl == null) {
				
				cl = getTask(true);
			}
			
			if (cl !=null) {
				// Found some work! Execute it.
				idleScanCount=-1;
				assert cache == null || cache.empty();
				if ( reporting) {
					System.out.println(this + " executes " + cl +".");
				}
				try {
				Closure cl1 = cl.execute(this);
				if ((reporting && bottom == cl)) {
					System.out.println(this + " completes " + cl + " returns " + cl1 +".");
				}
				cl=cl1;
				} catch (AssertionError z) {
					System.out.println(this + " asertion error when executing " + cl + " " + z);
					throw z;
				}
				
				
				// vj: This avoids having to implement a wrap around.
				// Otherwise, head might increase on a steal, but would
				// never decrease.
				assert cache.empty();
				cache.reset();
			} else {
				yields++;
				Thread.yield();
			}
		}
	}
	/**
	 * Push a frame onto the stack of the current
	 * closure (the closure at the bottom of the deque).
	 * Called by client code in the body of a procedure
	 * which has a spawn async before the first spawn.
	 * @param frame -- the frame to be pushed.
	 */
	public void pushFrame(Frame frame) {
		cache.pushFrame(frame);
	}
	/**
	 * Pop the last frame from the stack.
	 *
	 */
	public void popFrame() {
		cache.popFrame();
	}
	public String toString() {
		return "Worker("+index+")";
	}
	public Closure bottom() {
		return bottom;
	}
	/**
	 * Called by client code on return from a spawn async
	 * invocation. Performs the victim end of Dekker.
	 * @return  -- non null only if the current frame has been stolen
	 *             by a thief since its execution started.
	 *             The value returned is the closure created
	 *             by the invocation of a makeClosure on the child
	 *             frame, provided that it is not null. (In this case
	 *             the closure is also at the bottom of the
	 *             worker's ready queue.) If it is null, then
	 *             currentJob() is returned. (For in this case
	 *             the worker is running part of a globally
	 *             quiescent computation.) 
	 *   
	 *            
	 */
	public Closure interruptCheck() {
		if (! cache.interrupted()) 
			// fast path
			return null;
		
		Closure result = exceptionHandler();
		if (reporting)
			System.out.println(this + " at " + pool.time() + " popFrameCheck: discovers a theft and returns " + result
					+ " cache=" + cache.dump());
		if (result !=null)
			popFrame();
		return result;
	}

	/**
	 * Method to be called by user code after every method invocation that may have
	 * pushed a frame on the frame stack. Detects whether the worker has been mugged. 
	 * If so, throws a StealAbort. This will cause the Java stack to unwind all the way
	 * to the scheduler, which catches and discards this exception.
	 * <p>
	 * If the worker has not been mugged, this method does nothing.
	 * @throws StealAbort
	 */
	public void abortOnSteal() throws StealAbort {
		Closure c = interruptCheck();
		if (c != null) {
			throw StealAbort.abort;
		}
	}
	
	/**
	 * Method to be called by user code after every method invocation that may have
	 * pushed a frame on the frame stack, provided that the method returns an int value.
	 * This value should be passed into this call. If the worker has been mugged, this
	 * value will be squirrelled away in the promoted closure for the victim, 
	 * and from there it will make its way to the stolen frame. 
	 * Also, a StealAbort will be thrown. This will cause the Java stack to unwind all the way
	 * to the scheduler, which catches and discards this exception.
	 * 
	 * @throws StealAbort
	 */
	public void abortOnSteal(int x) throws StealAbort {
		Closure c = interruptCheck();
		if (c != null) {
			c.setResultInt(x);
			throw StealAbort.abort;
		}
	}
	
	/**
	 * @see abortOnSteal(int x)
	 * @param x
	 * @throws StealAbort
	 */
	public void abortOnSteal(double x) throws StealAbort {
		Closure c = interruptCheck();
		if (c != null) {
			c.setResultDouble(x);
			throw StealAbort.abort;
		}
	}
	/**
	 * @see abortOnSteal(int x)
	 * @param x
	 * @throws StealAbort
	 */
	public void abortOnSteal(float x) throws StealAbort {
		Closure c = interruptCheck();
		if (c != null) {
			c.setResultFloat(x);
			throw StealAbort.abort;
		}
	}
	/**
	 * @see abortOnSteal(int x)
	 * @param x
	 * @throws StealAbort
	 */
	public void abortOnSteal(long x) throws StealAbort {
		Closure c = interruptCheck();
		if (c != null) {
			c.setResultLong(x);
			throw StealAbort.abort;
		}
	}
	/**
	 * @see abortOnSteal(int x)
	 * @param x
	 * @throws StealAbort
	 */
	public void abortOnSteal(Object x) throws StealAbort {
		Closure c = interruptCheck();
		if (c != null) {
			c.setResultObject(x);
			throw StealAbort.abort;
		}
	}
	protected Closure exceptionHandler() {
		lock(this);
		try {
			Closure b = bottom;
				assert b !=null;
				b.lock(this);
				try { 
					/*b.pollInlets(this);
					Closure c = bottom;
					assert (c!=null);
					if (b != c) {
						b.unlock();
						b=c;
						b.lock(this);
					}*/
					boolean result = b.handleException(this);
					Closure answer = result?b:null;
					return answer;
				} finally {
					b.unlock();
				}
			
		} finally {
			unlock();
		}
	}
	
	
	public void pushIntUpdatingInPlace( int x) {
		cache.pushIntUpdatingInPlace(x);
	}
	boolean isActive() { 
		return  (! cache.empty()) || idleScanCount <= 0;
	}
	public Closure currentJob() {
		return pool.currentJob;
	}
	
	FrameGenerator fg;
	public interface FrameGenerator {
		Frame make();
	}
	public void setFrameGenerator(FrameGenerator x) {
		fg=x;
	}
}


