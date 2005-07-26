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
 */
public class FinishState {

	protected Stack/* <Throwable> */ finish_ = new Stack();

	protected int finishCount = 0;
	protected Activity parent;
	protected boolean parentWaiting = false;
	/**
	 * 
	 */
	public FinishState( Activity activity ) {
		super();
		parent = activity;
	}
	
	public /*myThread*/ synchronized void waitForFinish() {
		if (finishCount == 0 ) return;
		parentWaiting = true;
		while (finishCount > 0) {
			try {
				this.wait();
			} catch (InterruptedException z) {
				// What should i do?
			}
		}
		parentWaiting = false;
	}
	
	public /*myThread*/ synchronized void notifySubActivitySpawn() {
		finishCount++;
		if (Report.should_report("activity", 5)) {
			Report.report(5, " updating " + toString());
		}
	}

	/** Push an exception onto the stack. Do not decrement
	 * finishCount --- this exception was thrown by
	 * inline code, not a spawned activity.
	 * @param t
	 */
    public /*myThread*/ synchronized void pushException( Throwable t) {
    	finish_.push(t);
    }
    
    /** Called by an activity that has terminated.
     * 
     *
     */
    public /*someThread*/ synchronized void notifySubActivityTermination() {
		finishCount--;
		if (parentWaiting && finishCount==0)
			this.notifyAll();
			
	}
    public /*someThread*/ synchronized void notifySubActivityTermination(Throwable t) {
    	finish_.push(t);
    	notifySubActivityTermination();
    }
   
    public /*myThread*/ synchronized Stack exceptions() {
    	return finish_;
    }
    public synchronized String toString() {
    	return "<FinishState " + hashCode() + " " + finishCount + "," 
		+ parent.shortString()+"," + finish_ +">";
    }

}
