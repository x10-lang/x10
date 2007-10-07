package x10.runtime.cws;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * An active worker count keeps track of the number of workers that are active.
 * An action is triggered when the counter transitions to zero. 
 * 
 * @author vj 05/27/07
 *
 */
public class ActiveWorkerCount {
	volatile int numCheckedOut;
	final AtomicIntegerFieldUpdater<ActiveWorkerCount> numCheckedOutUpdater;
	final Runnable barrierAction;
	volatile int nextCount=0;
	final AtomicIntegerFieldUpdater<ActiveWorkerCount> nextCountUpdater;
	volatile int phaseNum;


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
	 * @param haveWork
	 */
	public boolean checkIn(Worker w, int inPhase, boolean haveWork) {
		if (inPhase != phaseNum) return false;
		if (haveWork) nextCountUpdater.getAndIncrement(this);
		final int count = numCheckedOutUpdater.addAndGet(this,-1);
		if ( Worker.reporting)
			System.out.println(Thread.currentThread() + " checks in to " + this);
		if (count == 0) {
			if ( Worker.reporting)
				System.out.println(Thread.currentThread() + " moves barrier up to " + this);

			if (nextCount > 0) {
				numCheckedOut=nextCount;
				nextCount=0;
				phaseNum++;
				w.advancePhase(phaseNum);
				return true;
			} else {
				phaseNum=0;
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
	public void checkOut() {
		numCheckedOutUpdater.addAndGet(this, 1);
		if ( Worker.reporting)
			System.out.println(Thread.currentThread() + " checks out of barrier " + this);

	}


}
