package x10.runtime.cws;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * An ActiveCyclicBarrier provides an active version of CyclicBarrier. Activities that 
 * coordinate their actions through an ActiveCyclicBarrier are in one of two states:
 * <em>busy</em> or <em>activeWait</em>. Initially all activities start out busy. They may transition
 * to the activeWaiting state by invoking a checkIn() operation. Unlike the await() method
 * of CyclicBarrier, this method does not suspend. The last of N activities to check in triggers
 * the barrier, moving it to the next phase, and executes the barrierAction, if any, associated with the barrier. 
 * Triggering the barrier causes all activities to transition into the busy state.
 * <p>
 * ActiveCyclicBarrier is distinguished from CyclicBarrier in that activities are permitted
 * to revert to the busy state from the activeWaiting state by invoking a checkOut() operation.
 * A checkOut() operation may only be invoked after a checkIn() operation has been performed in the
 * same phase. However, the application must guarantee the global property that no activity
 * can perform a checkOut() if <em>all</em> activities have performed a checkIn().
 * 
 * 
 * @author vj 05/27/07
 *
 */
public class ActiveCyclicBarrier {

	final int parties;
	volatile int numCheckedIn;
	final AtomicIntegerFieldUpdater<ActiveCyclicBarrier> updater;
	
	final Runnable barrierAction;
	long phase=0;
	
	public ActiveCyclicBarrier(int parties) {
		this(parties, null);
	}
	public ActiveCyclicBarrier(int parties, Runnable barrierAction) {
		super();
		this.parties=parties;
		this.barrierAction = barrierAction;
		this.numCheckedIn = parties;
		this.updater = AtomicIntegerFieldUpdater.newUpdater(ActiveCyclicBarrier.class, "numCheckedIn");
	}
	
	/**
	 * Check into the barrier. The activity performing this operation must be in a busy state.
	 * On return from this method will transition to the busy wait in the next phase
	 * (if all activities have checked in), or the activeWait state in this phase. 
	 * If all activities have checked in, this method will invoke the barrierAction associated with 
	 * the barrier if any. Note that the barrierAction is allowed to suspend on an external event.
	 * Once the barrierAction is completed, the phase of the barrier is incremented and all
	 * activities are considered to have entered the busy phase. 
	 * 
	 *
	 */
	public void checkIn() {
		int count = updater.addAndGet(this, 1);
		if (count == parties) {
			if (Worker.reporting)
				System.out.println(Thread.currentThread() + " moves barrier up from " + phase );
			if (barrierAction != null)
				barrierAction.run();
			++phase;
		}
	}
	
	/** Check out from the barrier. The activity must be in activeWait state and transitions
	 * to thebusy State. The application is required to ensure that 
	 * ensure that no checkOut operation is invoked by any activity once
	 * all activities have checked in.
	 * 
	 */
	
	public void checkOut() {
		updater.addAndGet(this, -1);
	}
	
	public long phase() {
		return phase;
	}
	public int getNumberCheckedIn() {
		return numCheckedIn;
		
	}
	public int getParties() {
		return parties;
	}
	/**
	 * Has the barrier advanced beyond phase phi?
	 * @param phi -- the phase to be tested against
	 * @return true  iff the current phase of the barrier is greater than phi.
	 */
	public boolean advanced(long phi) {
		return phase > phi;
	}

}
