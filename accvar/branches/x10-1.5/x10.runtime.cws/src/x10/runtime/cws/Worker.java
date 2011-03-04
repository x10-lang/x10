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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import x10.runtime.cws.Closure.Status;

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
	Cache cache;
	Cache nextCache;
	protected Lock lock; // dequeue_lock
	protected Thread lockOwner; // the thread holding the lock.
	protected int randNext;
	public int index;
	protected  boolean done;
	protected Closure closure;
	
	protected static  Worker[] workers; 
	public  long stealAttempts;
	public  long stealCount;
	public static  boolean reporting = false;
	public static long activeTime;
	public static long lastTimeStamp;
	protected Job job;
	protected boolean jobRequiresGlobalQuiescence;
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
    private static final long SLEEP_NANOS_PER_SCAN = (1 << 16);


	/**
	 * Creates new Worker.
TODO: Supposrt lazy initialization of nextCache.
	 */
	protected Worker(Pool pool, int index) {
		this.pool = pool;
		this.index = index;
		this.lock = new ReentrantLock();
		this.cache = new Cache(this);
		//this.nextCache = new Cache(this); // vj: must do this lazily, now that Cache is initializing stack on creation.
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
	static class AdvancePhaseException extends Exception{}
	protected Executable steal(Worker thief, boolean retry) throws AdvancePhaseException {
		++stealAttempts;
		if (jobRequiresGlobalQuiescence)
			return stealFrame(thief, retry);
		assert !jobRequiresGlobalQuiescence;
		final Worker victim = this;
		if (! retry) {
			if (lock.tryLock()) {
				this.lockOwner=thief;
				// u have the lock, continue
			} else {
				// bail on first try -- the lock is contended.
				return null;
			}
		} else {
			// On subsequent tries, wait to grab this lock even if it is contended.
			lock(thief);
		}
		
		Closure cl=null;
		try {
			if (thief.phaseNum != phaseNum) {
				// victim hasnt yet advanced to the next phase. Give up and try again.
				if (reporting)
					System.out.println(thief + " cannot steal from " + victim);
				if (thief.phaseNum < victim.phaseNum) throw new AdvancePhaseException();
				unlock();
				return null;
			}
			cl = peekTop(thief, victim);
			// vj: I believe the victim's ready deque should have only one
			// closure in it.
			//Closure cl1 = peekBottom(thief);
			//assert cl1==cl;
			if (cl==null) {
				unlock();
				return null;
			}
			// do not unlock
		} catch (AdvancePhaseException z) { 
			unlock();
			throw z;
		} catch (Throwable z) {
			// wrap in an error.
			unlock();
			throw new Error(z); 
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
				
			//	thief.checkinHistory.add(pool.time() + ": " + this + " invokes checkout " + s);
				thief.checkOut(res);
				if ( reporting) {
					//String s= "1:: steals stack[" + (victim.cache.head()-1) + "]= ready " + cl + " from "
					//+ victim + " cache=" + victim.cache.dump();
					String s= "1:: steals  " + res + " from " + victim;
					System.err.println(thief + s);
				}
				if (jobRequiresGlobalQuiescence)
				  res.copyFrame(thief);
				return res;
			} finally {
				cl.unlock();
				unlock();
			}
		}
		if (status == RUNNING) {
			boolean b = false;
			try {
				b=cache.dekker(thief);
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
					if (jobRequiresGlobalQuiescence)
					  cl.copyFrame(thief);
					child = cl.promoteChild(thief, victim);
					
					res = extractTop(thief);
//					 I have work now, so checkout of the barrier.
					String s = "3:: steals stack[" + (victim.cache.head()-1) + "]= running " + cl + " from "
					+ victim + " cache=" + victim.cache.dump();
					thief.checkOutSteal(res, victim);
					if (  reporting) {
						System.err.println(thief + s);
					}
				} finally {
					unlock();
				}
				assert cl==res;
				
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
		unlock();
		cl.unlock();
		return null;
		
	}

	/**
	 * Try to grab the lock on the victim, first time using a tryLock, and the
	 * second time waiting if necessary. Check if the thief is in the same phase
	 * as the victim, if not, throw an AdvancePhaseException. Now try to execute
	 * a dekker on the victim. On success, grab the top frame -- its now the thief's.
	 * On failure, return null.
	 * @param thief -- the worker executing this method.
	 * @param retry -- false on first try, true on second try.
	 * @return the Frame on top of the deque on success, null on failure.
	 * @throws AdvancePhaseException
	 */
	protected Executable stealFrame(Worker thief, boolean retry) 
	throws AdvancePhaseException{
		final Worker victim = this;
		++stealAttempts;
		if (! retry) {
			if (lock.tryLock()) {
				this.lockOwner=thief;
				// u have the lock, continue
			} else {
				// bail on first try -- the lock is contended.
				return null;
			}
		} else {
			// On subsequent tries, wait to grab this lock even if it is contended.
			lock(thief);
		}
		try {
			if (thief.phaseNum != phaseNum) {
				// victim hasnt yet advanced to the next phase. Give up and try again.
				//	System.out.println(thief + " cannot steal frame from " + victim + " in different phase."); 
				if (thief.phaseNum < victim.phaseNum) throw new AdvancePhaseException();
				// If the victim is not current, not much you can do, no way to get the victim
				// to advance without excessive synchronization.
				return null;
			} 
			boolean b=cache.dekker(thief);
			if (b) {
				Frame frame = cache.topFrame();
				if (frame == null) {
					System.out.println(thief + " error!! topFrame() is null for " + cache.dump());
				}
				Frame newFrame = frame.copy();
				assert frame !=null;
				if ( reporting) {
					
					String s= " 4:: steals " + frame + " from " + victim
					+ " as " + newFrame + " cache=" + cache.dump() + 
					" thief's cache = " + thief.cache.dump();
					System.err.println(thief + s);
					//new Error(thief.toString()).printStackTrace();
				}
				
				frame = newFrame;
				cache.incHead();
			//I have work now, so checkout of the barrier.
				thief.checkOutSteal(frame, victim);
				return frame;
			}
		} catch (AdvancePhaseException z) { // rethrow
			throw z;
		} catch (Throwable z) {
			z.printStackTrace();
			throw new Error(z);
		} finally {
			unlock();
		}
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
	 * Return the closure at the bottom of the deque.
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
	private Executable getTask(boolean mainLoop) throws AdvancePhaseException {
		// this may throw an exception. This means stop loooking for work
		// in this phase -- the worker has to advance to the next phase.
	
		checkIn();
		assert checkedIn && cache.empty();
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
		Executable cl = null;
		for (;;) {
			
			Worker victim = workers[idx];
			if (victim != null && victim !=thief) {
				//System.out.print(thief +  " at " + pool.time() + ": tries to steal from " 
				//		+ victim.cache.dump() + "...");
				/*cl = (jobRequiresGlobalQuiescence) 
				? victim.stealFrame(thief, retry) 
						: victim.steal(thief,retry);*/
				cl = victim.steal(thief, retry);
				if (cl == null) {
					//System.out.println("fails.");
					if ((sleeper == null) && // first sleeping worker
							victim.sleepStatus== SLEEPING)
						sleeper=victim;
				} else {
					//System.out.println(" gets " +  cl + ".");
					idleScanCount = -1;
					++stealCount;
					if (sleeper !=null) sleeper.wakeup();
					jobRequiresGlobalQuiescence = victim.jobRequiresGlobalQuiescence;
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
		if (mainLoop && pool.activeJobs==0) {
			// reset phase, entering new job.
			if (reporting) 
				System.out.println(Thread.currentThread() + " off to wait for task from pool.");
			lock(this);
			phaseNum = 0;
			unlock();
			Closure c = getTaskFromPool(sleeper);
			if (c !=null) return c;
		}
		

		if (((idleScanCount + 1) & SCANS_PER_SLEEP) == 0) {
			if (sleepStatus == AWAKE &&
					sleepStatusUpdater.compareAndSet(this, AWAKE, SLEEPING)){
				if (reporting)
					System.out.println(this + " goes to sleep.");
				LockSupport.parkNanos(idleScanCount * SLEEP_NANOS_PER_SCAN);
				if (sleepStatus == WOKEN) // reset count on wakeup
				idleScanCount = 1;
				if (reporting)
					System.out.println(this + " wakes from sleep.");
			}
			sleepStatus = AWAKE;
		}
		return null;
	}

	private void setJob(Job currentJob) {
		if (currentJob == null) currentJob = pool.currentJob;
		if (currentJob==null) return;
		jobRequiresGlobalQuiescence = currentJob.requiresGlobalQuiescence();
		if (this.job != currentJob) {
			this.job = currentJob;
			activeTime = 0;
			if (reporting)
				System.out.println(this + ": job " + currentJob 
						+ (jobRequiresGlobalQuiescence? " requires " : " does not require ")
						+ " global quiescence.");
		}
	}
    /**
     * Try to get a task from pool. On failure, possibly sleep.
     * On success, try to wake up some other sleeping worker.
     * @param sleeper a worker noticed to be sleeping while scanning
     */
    private Closure getTaskFromPool(Worker sleeper) {
    	Job job = pool.getJob();
    	if (job != null) {
    		idleScanCount = -1;
    		if (sleeper != null) sleeper.wakeup();
    	//	checkinHistory.add(pool.time() + "2:: " + this + " invokes checkout getting " + job + " from pool.");
    		try {
    		checkOut(job);
    		} catch (AdvancePhaseException z) {
    			System.err.println("Error in getTaskFromPool:");
    			z.printStackTrace();
    		}
    		setJob(job);
    	}
    	return job;
    }
      
 /**
  * Each time a worker starts searching for work, it calls checkIn. However, we must ensure
  * that the nextCount maintained by AWC is incremented at most once per worker per phase (and only if
  * the worker has pending activities for the next phase). 
  * 
  * A worker must check into the AWC only when it has first checked out.
  */
    protected int nextPhaseSize, lastPhaseSize;
    public int lastPhaseSize() { return lastPhaseSize;}
    public void addNextPhaseSize(int i) { 
    	nextPhaseSize += i;
    }
    protected boolean checkedIn = true, nextPhaseHasWork=false;
    public boolean checkedIn() { return checkedIn;}
    public boolean nextPhaseHasWork() { return nextPhaseHasWork; }
    /*void checkIn() throws AdvancePhaseException {
    	if (job !=null) job.onCheckIn(this);
    	if (!checkedIn) {
    		checkedIn = true;
    	//	checkinHistory.add(pool.time() + ":checkedIn false -> true. checkIn called on " + pool.barrier);
    		if (nextPhaseHasWork) {
    		     // if we have already communicated to AWC that this
    			// worker has work for the next phase, we dont need to do this again.
    			pool.barrier.checkIn(this, phaseNum, false, nextPhaseSize);
    		} else {
    		  nextPhaseHasWork = ! (nextCache == null || nextCache.empty());
    		  pool.barrier.checkIn(this, phaseNum, nextPhaseHasWork, nextPhaseSize);
    		}
    		
    	} else 
    		if (nextPhaseSize > 0) {
    			pool.barrier.checkIn(this, phaseNum, false,nextPhaseSize);
    		}
    	nextPhaseSize=0;
    	
    }*/
    void checkIn() throws AdvancePhaseException {
    	if (job !=null) job.onCheckIn(this);
    	if (!checkedIn) {
    		checkedIn = true;
    	//	checkinHistory.add(pool.time() + ":checkedIn false -> true. checkIn called on " + pool.barrier);
    		if (nextPhaseHasWork) {
    		     // if we have already communicated to AWC that this
    			// worker has work for the next phase, we dont need to do this again.
    			pool.barrier.checkIn(this, phaseNum, false, nextPhaseSize);
    		} else {
    		  nextPhaseHasWork = ! (nextCache == null || nextCache.empty());
    		  pool.barrier.checkIn(this, phaseNum, nextPhaseHasWork, nextPhaseSize);
    		}
    		
    	} 
    	nextPhaseSize=0;
    	
    }
    /**
     * Each time a worker finds work, it calls checkout. Called holding the lock for the 
     * victim during the process of stealing.
     * The victim has work, therefore it must be checked out. So this ensures that
     * as long as there is work at least one worker is checked out.
     * @param cl
     */
    void checkOut(Executable cl) throws AdvancePhaseException{
    	
    	//if (checkedIn) {
    		checkedIn = false;
    	//	checkinHistory.add(pool.time() + ":checkedIn true->fase. checkOut() called on " + pool.barrier);
    		pool.barrier.checkOut(this, phaseNum);
    	//}
    }
    void checkOutSteal(Executable cl, Worker victim) throws AdvancePhaseException {
    	assert victim.lockOwner==this;
    
    	// this may be a sleeper ... it may have not participated in the previous
    	// job. So reset job information from victim.
    	if (job != victim.job) {
    		if (reporting)
    			System.out.println(this + " switching jobs on steal...");
    		job = victim.job;
    		jobRequiresGlobalQuiescence = victim.jobRequiresGlobalQuiescence;
    		assert (nextCache == null || nextCache.empty());
    		phaseNum = victim.phaseNum;
    	}
    	if (phaseNum < victim.phaseNum) {
    		assert (nextCache == null || nextCache.empty());
    		if (reporting)
    			System.out.println(this + " moving up to victim's phase...");
    		phaseNum = victim.phaseNum;
    	}
    	checkedIn = false;
    	pool.barrier.checkOut(this, phaseNum);
    }
    private void wakeup() {
        if (sleepStatus == SLEEPING && 
            sleepStatusUpdater.compareAndSet(this, SLEEPING, WOKEN)) {
            LockSupport.unpark(this);
        }
    }
    private int phaseNum=0;
   // List<String> checkinHistory = new ArrayList<String>();
	@Override
	public void run() {
		assert index >= 0;
		setRandSeed(index*162347);
		Executable cl = null; //frame or closure.
		int yields = 0;
		while (!done) {
			setJob(pool.currentJob);
			if (reporting) 
				System.out.println(this + " job is " + job);
			if (cl == null ) {
				// Addition for GlobalQuiescence. Keep executing
				// current frame until dequeue becomes empty.
				if (jobRequiresGlobalQuiescence) {
					Cache cache = this.cache;
					for(;;) {
						Frame f = cache.currentFrameIfStackExists(); // cache.popAndReturnFrame(this);
						
						if (f == null) {
							assert cache.empty();
							// Added this 06/30/2008 so that GlobalQuiecence code
							// does not have to use w.popReturnAndReset() instead of w.popReturn()
						//	cache.reset();
							break;
						}
						// next statement is a customized Frame.execute(w), with no need
						// to push frame on deque.
						try {
							if (reporting)
								System.out.println(this + " starts executing " + f);
							// TODO: Check if cache.resetExceptionPointer() is needed. prolly!!
							f.compute(this);
							if (reporting)
								System.out.println(this + " finishes executing " + f + ".");
							// do not pop this frame. We leave it up to the code in the 
							// frame to pop off the frame.
							//cache.popFrame();
						} catch (StealAbort z) {
						 // do nothing. the exception has done its work
						 // unwinding the Java call stack.
						}
					}
				} else {
					// Try geting work from the local queue.
					// The work extracted will be a closure.
					// cl may be null. When non-null
					// cl is typically RETURNING.
					lock(this);
					try {
						cl = extractBottom(this); 
					} finally {
						unlock();
					}
				}
			}
			if (cl == null) {
				// Steal, or get it from the job.
				if ((reporting)) 
					System.out.println(this + " has no closure. Trying to get one." );
				try {
					
						cl = getTask(true);
						if ((reporting)) 
							System.out.println(this + " found " + cl );
					
				} catch (AdvancePhaseException z) {
					cl=null;
				}
			}
			if (cl !=null) {
				// Found some work! Execute it.
				idleScanCount=-1;
				assert cache == null || cache.empty();
				if (reporting) {
					System.out.println(this + " executes " + cl +". cache=" + cache.dump());
				}
				try {
					long ts = System.nanoTime();

					Executable cl1 = cl.execute(this);
					lastTimeStamp = System.nanoTime();
					activeTime += lastTimeStamp-ts;
					
					if ((reporting && bottom == cl)) {
						System.out.println(this + " completes " + cl + " returns " + cl1
								+ cache.dump() 
								+".");
					}
					cl=cl1;
				} catch (AssertionError z) {
					System.out.println(this + " asertion error when executing " + cl + " " + z);
					throw z;
				}
				// vj: This avoids having to implement a wrap around.
				// Otherwise, head might increase on a steal, but would
				// never decrease.
				//
				if ( ! jobRequiresGlobalQuiescence) {
					assert cache.empty();
					cache.reset();
				}
			} else {
				int bNum = pool.barrier.phaseNum;
				if (bNum > phaseNum) {
					assert phaseNum+1 == bNum || nextCache == null || nextCache.empty();
					//assert ! nextPhaseHasWork;
					advancePhase(bNum, pool.barrier.lastSize);
				} else {
					// TODO: Check if this yields are needed.
					yields++;
					Thread.yield();
				}
			}
		}
	}
	public int phaseNum() { return phaseNum;}
	/**
	 * Invoked by worker when it discovers during its run cycle that the AWC has
	 * advanced.
	 * @param bNum
	 * @param lastSize
	 */
	protected void advancePhase(int bNum, int lastSize) {

		lock(this);
		checkedIn= ! nextPhaseHasWork;
		Cache temp = cache;
		if (nextCache==null) nextCache = new Cache(this);
		cache = nextCache;
		nextCache = temp;
		phaseNum=bNum;
		nextPhaseHasWork=false;
		nextPhaseSize=0;
		lastPhaseSize=lastSize;
	//	checkinHistory = new ArrayList<String>();
	//	checkinHistory.add(pool.time() + "phase " + phaseNum + " checkedIn=" + checkedIn);
		
		if (  reporting) 
			System.out.println(this + " advances to phase "
					+ phaseNum + " lastPhaseSize=" + lastPhaseSize);
		unlock();
	}
	/**
	 * Push a frame onto the stack of the current
	 * closure (the closure at the bottom of the deque).
	 * Called by client code in the body of a procedure
	 * which has a spawn async before the first spawn.
	 * @param frame -- the frame to be pushed.
	 */
	public void pushFrame(Frame frame) {
		if (false && reporting)
			System.out.println(this + " pushes " + frame);
		cache.pushFrame(frame);
	}
	/**
	 * Push a frame onto the stack of the current
	 * closure (the closure at the bottom of the deque).
	 * Called by client code in the body of a procedure
	 * which has a spawn async before the first spawn.
	 * @param frame -- the frame to be pushed.
	 */
	public void pushFrameNext(Frame frame) {
		if (reporting)
			System.out.println(this + " pushes " + frame + " on next deque.");
		if (nextCache == null) nextCache = new Cache(this);
		nextCache.pushFrame(frame);
	}
	/**
	 * Pop the last frame from the stack.
	 *
	 */
	public void popFrame() { cache.popFrame();	}
	/*public String toString() {
		return "Worker("+index+",job=" + job + ",phase=" + phaseNum+",checkedIn=" 
		+ checkedIn + ",nextPhaseSize=" + nextPhaseSize+")";
	}*/
	public String toString() { return "Worker("+index+")";}
	public Closure bottom() { return bottom; }
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
		if (! cache.interrupted()) {
			// fast path
			return null;
		}
		
		Closure result = exceptionHandler();
		if (reporting) { 
			interruptCheckReportSteal(result);
		}
		if (result == null) {
			result = job;
		}
		return result;
	}

	private void interruptCheckReportSteal(Closure result) {
		/*System.out.println(this + " at " + pool.time() 
				+ " interruptCheck: discovers a theft and returns " + result
				+ " cache=" + cache.dump());*/
		System.err.println(this + " at " + pool.time() 
				+ " interruptCheck: discovers a theft on frame " + currentFrame()
				+ " cache=" + cache.dump());
		/*if (result !=null) {
			System.err.println("vj debug before: " + cache.dump());
			popFrame();
			System.err.println("vj debug after: " + cache.dump());
		}*/
	}
	
	/**
	 * Determines if the thread has been interrupted. Resets exception pointer as a side-effect.
	 * @return -- true if the thread is interrupted, false otherwise.
	 * @see -- Worker.steal
	
	public boolean isInterrupted() {
		if (! cache.interrupted())
			return false;
		// Exceptional case. An interruption may have happened.
		// Whether or not it did depends on the result of Dekker.
		lock(this);
		try {
			cache.resetExceptionPointer(this);
			return cache.empty();
		} finally {
			unlock();
		}
		
	}
 */

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
	/**
	 * Invoked by victim after it discovers it has been mugged.
	 * The victim waits until the thief has finished the mugging 
	 * (by waiting to acquire its own lock). Then it resets
	 * the exception pointer. If the cache is empty, 
	 * it returns the current closure. Else it returns null.
	 * @return
	 */
	protected Closure exceptionHandler() {
		lock(this);
		try {
			Closure b = bottom;
			if (b == null) {
				// this can happen in the case of globally quiescent
				// computations after a theft.
				cache.resetExceptionPointer(this);
				if (cache.empty()) return job;
				return null;
			}
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
				cache.resetExceptionPointer(this);
				Status s = b.status;
				assert s == Status.RUNNING || s == Status.RETURNING;
				if (cache.empty()) {
					
					assert b.joinCount==0;
					b.status = Status.RETURNING;
					return b;
				}
				return null;
			} finally {
				b.unlock();
			}
			
		} finally {
			unlock();
		}
	}
	
	public static int getLocalQueueSize() {
		Worker me = (Worker) Thread.currentThread();
		return me.getQueueSize();
	}
	
	public int getQueueSize() {
		return cache.queueSize();
	}
	public void pushIntUpdatingInPlace( int x) {
		cache.pushIntUpdatingInPlace(x);
	}
	public void pushObjectUpdatingInPlace( Object x) {
		cache.pushObjectUpdatingInPlace(x);
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
	public void popFrameAndReset() {
		cache.popFrameAndReset(this);
	}
	public Frame popAndReturnFrame() {
		return cache.popAndReturnFrame(this);
	}
	public Frame currentFrame() {
		return cache.currentFrame();
	}
	public int count;
}


