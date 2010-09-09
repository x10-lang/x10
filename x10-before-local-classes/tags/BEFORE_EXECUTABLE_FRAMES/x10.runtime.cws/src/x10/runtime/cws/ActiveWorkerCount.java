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
	final AtomicIntegerFieldUpdater<ActiveWorkerCount> updater;
	final Runnable barrierAction;
	
	public ActiveWorkerCount() {
		this(null);
	}
	public ActiveWorkerCount(final Runnable barrierAction) {
		super();
		this.barrierAction = barrierAction;
		this.numCheckedOut = 0;
		this.updater = AtomicIntegerFieldUpdater.newUpdater(ActiveWorkerCount.class, "numCheckedOut");
	}
	
	public void checkIn() {
		final int count = updater.addAndGet(this, -1);
		if (count ==0) {
			if (Worker.reporting)
				System.out.println(Thread.currentThread() + " moves barrier up." );
			if (barrierAction != null)
				barrierAction.run();
		}
	}
	
	public void checkOut() {
		updater.addAndGet(this, 1);
	}
	
	public int getNumberCheckedOut() {
		return numCheckedOut;
	}
}
