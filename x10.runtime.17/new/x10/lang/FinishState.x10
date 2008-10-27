/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
 
package x10.lang;

/** The state associated with a finish operation. Records the count of 
 * active activities associated with this finish, and the stack
 * of exceptions thrown by activities associated with this finish that 
 * have already terminated abruptly. An activity maintains a stack of FinishStates.
 * 
 * @author vj May 17, 2005
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: use ModCountDownLatch (modified version of JCU CountDownLatch) instead of synchronized updates to finishCount.
 * @author tardieu 
 */
public class FinishState {

	/**
	 * The Exception Stack is used to collect exceptions 
	 * issued when activities associated with this finish state terminate abruptly. 
	 * This Object is lazily created 
	 **/
	private var exceptions: Stack[Throwable];

	/** Keep track of current number of activities associated with this finish state **/
	private val latch = new ModCountDownLatch(0);

	/**
	 * This method returns only when all spawned activity registered with this 
	 * FinishState have terminated either normally or abruptly.
	 * Additionnaly, a notification is sent to the place where 
	 * the activity reside when the activity is blocked and unblock.
	 */
	public def waitForFinish(): void {
		if (latch.getCount() > 0) {
			here.threadBlockedNotification();
			try { latch.await(); } catch (z: InterruptedException) {}
			here.threadUnblockedNotification();
		}
	}

	/** 
	 * An activity created under this finish has been created. Increment the count
	 * associated with the finish.
	 */
	public def notifySubActivitySpawn(): void {
		latch.updateCount();
	}

	/** 
	 * An activity created under this finish has terminated. Decrement the count
	 * associated with the finish and notify the parent activity if it is waiting.
	 */
	public def notifySubActivityTermination(): void {
		latch.countDown();
	}

	/** 
	 * Push an exception onto the stack. Do not decrement
	 * finishCount --- this exception was thrown by
	 * inline code, not a spawned activity.
	 */
	public def pushException(t: Throwable): void {
		atomic { // HACK atomic method does compile properly
			if (null == exceptions) exceptions = new Stack[Throwable]();
			exceptions.push(t);
		}
	}

	/**
	 * Return the stack of exceptions, if any, recorded for this finish.
	 */
	public def exceptions(): Stack[Throwable] {
		return exceptions;
	}
}
