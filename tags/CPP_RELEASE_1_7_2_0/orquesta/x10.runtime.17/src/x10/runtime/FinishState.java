/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on May 17, 2005
 *
 * 
 */
package x10.runtime;

import java.util.Stack;

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

	/** The Exception Stack is used to collect exceptions 
	 * issued when activities associated with this finish state terminate abruptly. 
	 * This Object is lazily created 
	 **/
	private Stack<Throwable> finish_;

	/**not really needed, used in toString(). **/
	protected Activity parent; 

	/** Keep track of current number of activities associated with this finish state **/
	protected ModCountDownLatch mcdl = new ModCountDownLatch(0);

	/** 
	 * Create a new finish state for the given activity.
	 */
	public FinishState(Activity activity) {
		super();
		parent = activity;
	}

	/**
	 * This method returns only when all spawned activity registered with this 
	 * FinishState have terminated either normally or abruptly.
	 * Additionnaly, a notification is sent to the place where 
	 * the activity reside when the activity is blocked and unblock.
	 */
	public void waitForFinish() {
		if (mcdl.getCount() > 0) {
			// Need to wait till count becomes zero
			PoolRunner activityRunner = (PoolRunner) Thread.currentThread();
			activityRunner.getPlace().threadBlockedNotification(); // Notify runtime that thread executing current activity will be blocked
			try {
				mcdl.await();
			} catch (InterruptedException z) {
			}
			activityRunner.getPlace().threadUnblockedNotification(); // Notify runtime that thread executing current activity has become unblocked
		}
		
		if (VMInterface.ABSTRACT_EXECUTION_STATS) {
			// Update abstract execution statististics before exiting from finisj
			x10.runtime.Runtime.getCurrentActivity().maxCritPathOps(mcdl.getCritPathOps());
			if (VMInterface.ABSTRACT_EXECUTION_TIMES) {
				x10.runtime.Runtime.getCurrentActivity().maxCritPathTime(mcdl.getIdealTime());
				x10.runtime.Runtime.getCurrentActivity().setResumeTime();
			}
		}
	}


	/** 
	 * An activity created under this finish has been created. Increment the count
	 * associated with the finish.
	 */
	public void notifySubActivitySpawn() {
		mcdl.updateCount();
		if (Report.should_report(Report.ACTIVITY, 5)) {
			Report.report(5, " updating " + toString());
		}
	}

	/** 
	 * Push an exception onto the stack. Do not decrement
	 * finishCount --- this exception was thrown by
	 * inline code, not a spawned activity.
	 * @param t Thrown exception
	 */
	public void pushException(Throwable t) {
		synchronized(this) {
			this.getFinishStack().push(t);
		}
	}

	/**
	 * Create a new stack or return current one.
	 * @return the finish stak
	 */
	private Stack<Throwable> getFinishStack() {
		if(finish_ == null)
			finish_ = new Stack<Throwable>();
		return finish_;
	}
	
	/** 
	 * An activity created under this finish has terminated. Decrement the count
	 * associated with the finish and notify the parent activity if it is waiting.
	 */
	public void notifySubActivityTermination() {
		mcdl.countDown();
	}

	/** 
	 * An activity created under this finish has terminated abruptly. 
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
