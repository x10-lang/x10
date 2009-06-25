package x10.runtime.cws;

import java.util.ArrayList;
import java.util.List;
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
	volatile int nextCount=0;
	final AtomicIntegerFieldUpdater<ActiveWorkerCount> nextCountUpdater;
	volatile int phaseNum;
	// List<String> checkinHistory = new ArrayList<String>(), prevHistory=checkinHistory;

	public ActiveWorkerCount(final Runnable barrierAction) {
		super();
		this.barrierAction = barrierAction;
		this.numCheckedOut = 0;
		this.numCheckedOutUpdater = AtomicIntegerFieldUpdater.newUpdater(ActiveWorkerCount.class, "numCheckedOut");
		this.nextCountUpdater = 
			AtomicIntegerFieldUpdater.newUpdater(ActiveWorkerCount.class, "nextCount");
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
	public boolean checkIn(Worker w, int inPhase, boolean haveWork) throws Worker.AdvancePhaseException{
		if (inPhase !=phaseNum) {
			// TODO: Why should this ever happen?
			System.err.println(this + "::Invariant III violation at ACW" + this);
			System.err.println(w + "calls CheckIn with phase " + inPhase + " on " + this);
			System.err.println("cache: " + w.cache.dump() + "nextCache:" +
					w.nextCache.dump());
		//	System.err.println(w + "checkInHistory:");
		//	for (String s : w.checkinHistory) System.err.println("  :" + s);
			Worker.AdvancePhaseException z = new Worker.AdvancePhaseException();
			z.printStackTrace();
			throw z;
		}
		assert inPhase == phaseNum;
		//checkinHistory.add(w.pool.time() + ":" + w + " checks in with haveWork=" + haveWork);
		if (haveWork) nextCountUpdater.getAndIncrement(this);
		final int count = numCheckedOutUpdater.addAndGet(this,-1);
		if ( Worker.reporting)
			System.out.println(Thread.currentThread() + " checks in to " + this);
		if (count == 0) {
			if ( Worker.reporting)
				System.out.println(Thread.currentThread() + " moves barrier up to " + this);
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
			
			if (nextCount > 0) { // Advance the phase.
				
				numCheckedOutUpdater.getAndSet(this, nextCount);
				nextCount=0;
				phaseNum++;
				//prevHistory = checkinHistory;
				//checkinHistory = new ArrayList<String>();
				//checkinHistory.add(w.pool.time() + ":" + this + " starts new phase.");
				// w.advancePhase(phaseNum);
				// return true;
				throw new Worker.AdvancePhaseException();
			} else { // Complete the job
				phaseNum=0;
				//prevHistory = checkinHistory;
				//checkinHistory = new ArrayList<String>();
				//checkinHistory.add(w.pool.time() + " Starting phase with numCheckedOut=" + numCheckedOut);
				if ( Worker.reporting)
					System.out.println(Thread.currentThread() + " completes job." );
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
			System.err.println("Invariant II violation at ACW " + this);
			System.err.println(w + "calls checkOut on " + this);
			System.err.println("cache: " + w.cache.dump() + "nextCache:" + 	w.nextCache.dump());
			
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
		//	checkinHistory.add(w.pool.time() + ": " + w + " has checksOuted from " + this);
		if ( Worker.reporting)
			System.out.println(Thread.currentThread() + " checks out of barrier " + this);

	}


}
