package x10.runtime.cws;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * An active worker count keeps track of the number of workers that are active.
 * An action is triggered when the counter transitions to zero. 
 * 
 * @author vj 05/27/07
 *
 */
class ActiveWorkerCount {
	volatile int numCheckedOut;
	final AtomicIntegerFieldUpdater<ActiveWorkerCount> numCheckedOutUpdater;
	final Runnable barrierAction;
	volatile int nextCount=0, nextSize=0, lastSize=0;
	final AtomicIntegerFieldUpdater<ActiveWorkerCount> nextCountUpdater, nextSizeUpdater;
	volatile int phaseNum;
	// List<String> checkinHistory = new ArrayList<String>(), prevHistory=checkinHistory;
	final int poolSize;
	public ActiveWorkerCount(final Runnable barrierAction, int poolSize) {
		super();
		this.barrierAction = barrierAction;
		this.poolSize=poolSize;
		this.numCheckedOut = 0;
		this.numCheckedOutUpdater = AtomicIntegerFieldUpdater.newUpdater(ActiveWorkerCount.class, "numCheckedOut");
		this.nextCountUpdater = 
			AtomicIntegerFieldUpdater.newUpdater(ActiveWorkerCount.class, "nextCount");
		this.nextSizeUpdater = 
			AtomicIntegerFieldUpdater.newUpdater(ActiveWorkerCount.class, "nextSize");
	}
	public String toString() {
		return "AWC(phase=" + phaseNum+ ",nextCount="+nextCount + ",numCheckedOut="+numCheckedOut+")";
	}
	/**
	 * A worker checks into the ActiveWorkerCount when it has an empty deque and is hunting for work.
	 * When all workers have checked in, the barrier is reached. Now if on checking in any worker
	 * said that there was still work to do, the phase is incremented. Otherwise computation has
	 * terminated and the barrier action is performed. This will cause pool.activeCount to go to zero
	 * and as the workers are spinning they will realize this and then go into a wait for the next
	 * job coming through.
	 * @require w==Thread.currentThread()
	 * @param haveWork
	 * @return true -- if this call causes the phase to advance. In this case the current thread's phase
	 * is advanced.
	 */
	public boolean checkIn(Worker w, int inPhase, boolean npHasWork, int workSize) 
	throws Worker.AdvancePhaseException{
		assert (! npHasWork) || workSize > 0;
		if (inPhase !=phaseNum) {
			// TODO: Why should this ever happen?
			System.err.println("Invariant III violation at " + this);
			System.err.println(w + "calls CheckIn with phase " + inPhase + " on " + this);
			System.err.println("cache: " + w.cache.dump() 
					+ (w.nextCache != null ? "nextCache:" + w.nextCache.dump() : "")
					);
		//	System.err.println(w + "checkInHistory:");
		//	for (String s : w.checkinHistory) System.err.println("  :" + s);
			Worker.AdvancePhaseException z = new Worker.AdvancePhaseException();
			z.printStackTrace();
			throw z;
		}
		assert inPhase == phaseNum;
		//checkinHistory.add(w.pool.time() + ":" + w + " checks in with haveWork=" + haveWork);
		if (npHasWork) 
			nextCountUpdater.getAndIncrement(this);
		assert nextCount <= poolSize;
		/*if (workSize > 0) { 
			nextSizeUpdater.getAndAdd(this, workSize);
		}*/
		final int count = numCheckedOutUpdater.addAndGet(this,-1);
		if (  Worker.reporting)
			System.out.println(Thread.currentThread() 
					+ " with npHasWork=" + npHasWork + " workSize=" + workSize
					+ " checks in to " + this);
		if (count == 0) {
			
			
			/*int checkedInCount=0,nextCountCheck=0;
			for (Worker worker : w.pool.workers) {
				checkedInCount += worker.checkedIn?1:0;
				nextCountCheck += worker.nextPhaseHasWork?1:0;
			}
			if (checkedInCount !=w.pool.workers.length && nextCountCheck !=nextCount) {
				System.err.println(this + "::Invariant violation I at ACW" + this);
				//for (String s : prevHistory) System.err.println(" prev::" + s);
				//for (String s : checkinHistory) System.err.println(" this::" + s);
				
			}*/
			lastSize=w.nextPhaseSize;
			//nextSize=0;
			if (nextCount > 0) { // Advance the phase.
				
				numCheckedOutUpdater.getAndSet(this, nextCount);
				assert numCheckedOut <= poolSize;
				nextCount=0;
				
				phaseNum++;
				//prevHistory = checkinHistory;
				//checkinHistory = new ArrayList<String>();
				//checkinHistory.add(w.pool.time() + ":" + this + " starts new phase.");
				// w.advancePhase(phaseNum);
				// return true;
				if (  Worker.reporting)
					System.out.println(Thread.currentThread() + " moves barrier up to " + this);
				throw new Worker.AdvancePhaseException();
			} else { // Complete the job
				phaseNum=0;
				//prevHistory = checkinHistory;
				//checkinHistory = new ArrayList<String>();
				//checkinHistory.add(w.pool.time() + " Starting phase with numCheckedOut=" + numCheckedOut);
				if ( Worker.reporting)
					System.err.println(Thread.currentThread() + " completes job." );
				if (barrierAction != null) 	
					barrierAction.run();
			}
		}
		return false;
	}
	/**
	 * A worker checks out only if it has checked in first, and has found work since it last 
	 * checked in. 
	 *
	 */
	public void checkOut(Worker w, int inPhase) throws Worker.AdvancePhaseException{
		if (inPhase !=phaseNum) {
			// TODO: Why should this ever happen?
			System.err.println("Invariant II violation at " + this);
			System.err.println(w + "calls checkOut on " + this);
			System.err.println("cache: " + w.cache.dump() + (w.nextCache != null ? "nextCache:" + 	w.nextCache.dump() : ""));
			
			/** System.err.println(w + "checkInHistory:");
			for (String s : w.checkinHistory) System.err.println("  :" + s);
			System.err.println(this + " phaseHistory:");
				for (String s : prevHistory) System.err.println("  prev::" + s);
				for (String s : checkinHistory) System.err.println("  this::" + s);
			 */
	 
			Worker.AdvancePhaseException z = new Worker.AdvancePhaseException();
			z.printStackTrace();
			throw z;
		}
		numCheckedOutUpdater.addAndGet(this, 1);
		if ( Worker.reporting)
			System.out.println(Thread.currentThread() + " checks out of barrier " + this);
	}
}
