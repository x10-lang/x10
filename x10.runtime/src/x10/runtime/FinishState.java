/*
 * Created by vj on May 17, 2005
 *
 * 
 */
package x10.runtime;

import java.util.Stack;

import x10.lang.place;

/** The state associated with a finish operation. Records the count of 
 * active activities associated with this finish, and the stack
 * of exceptions thrown by activities associated with this finish that 
 * have already terminated abruptly. An activity maintains a stack of FinishStates.
 * 
 * This code uses the myThread/someThread annotations.
 * 
 * @see x10.runtime.Activity
 * 
 * @author vj May 17, 2005
 * 
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: use ModCountDownLatch (modified version of JCU CountDownLatch) instead of synchronized updates to finishCount. 
 */
public class FinishState {

	// Exception Stack is lazily created
	private Stack finish_;

	protected Activity parent; // not really needed, used in toString().

	protected ModCountDownLatch mcdl = new ModCountDownLatch(0);

	/** 
	 * Create a new finish state for the given activity.
	 */
	public FinishState(Activity activity) {
		super();
		parent = activity;
	}

	public void waitForFinish() {
		if (mcdl.getCount() == 0)
			return;

		PoolRunner activityRunner = (PoolRunner) Thread.currentThread();
		 activityRunner.getPlace().threadBlockedNotification();

		try {
			mcdl.await();
			if (JITTimeConstants.ABSTRACT_EXECUTION_STATS) {
				x10.lang.Runtime.getCurrentActivity().maxCritPathOps(mcdl.getCritPathOps());
				if (JITTimeConstants.ABSTRACT_EXECUTION_TIMES) {
					x10.lang.Runtime.getCurrentActivity().maxCritPathTime(mcdl.getIdealTime());
					x10.lang.Runtime.getCurrentActivity().setResumeTime();
				}
			}
		} catch (InterruptedException z) {
		}
		activityRunner.getPlace().threadUnblockedNotification();
	}

	public void notifySubActivitySpawn() {
		mcdl.updateCount();
		if (Report.should_report(Report.ACTIVITY, 5)) {
			Report.report(5, " updating " + toString());
		}
	}

	/** Push an exception onto the stack. Do not decrement
	 * finishCount --- this exception was thrown by
	 * inline code, not a spawned activity.
	 * @param t
	 */
	public void pushException(Throwable t) {
		synchronized(this) {
			this.getFinishStack().push(t);
		}
	}

	private Stack getFinishStack() {
		if(finish_ == null)
			finish_ = new Stack();
		return finish_;
	}
	
	/** An activity created under this finish has terminated. Decrement the count
	 * associated with the finish and notify the parent activity if it is waiting.
	 */
	public void notifySubActivityTermination() {
		mcdl.countDown();
	}

	/** An activity created under this finish has terminated abruptly. 
	 * Record the exception, decrement the count associated with the finish
	 * and notify the parent activity if it is waiting.
	 * 
	 * @param t -- The exception thrown by the activity that terminated abruptly.
	 */
	public void notifySubActivityTermination(Throwable t) {
		this.pushException(t);
		notifySubActivityTermination();
	}

	/** Return the stack of exceptions, if any, recorded for this finish.
	 * 
	 * @return -- stack of exceptions recorded for this finish.
	 */
	public Stack exceptions() {
		return finish_;
	}

	/** Return a string to be used in Report messages.
	 */
	public synchronized String toString() {
		return "<FinishState " + hashCode() + " " + mcdl.getCount() + ","
				+ parent.shortString() + "," + finish_ + ">";
	}

}
