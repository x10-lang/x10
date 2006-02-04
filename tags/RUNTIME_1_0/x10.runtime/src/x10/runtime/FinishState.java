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
	protected Activity parent; // not really needed, used in toString().
	protected boolean parentWaiting = false; // optimization.
	
	/** Create a new finish state for the given activity.
	 * 
	 */
	public FinishState( Activity activity ) {
		super();
		parent = activity;
	}
	
	public /*myThread*/ synchronized void waitForFinish() {
		if (finishCount == 0 ) return;
		parentWaiting = true;
		if (finishCount > 0) {			
			LoadMonitored.blocked(x10.runtime.Sampling.CAUSE_FINISH, 0, null);
			while (finishCount > 0) {
				try {
					this.wait();
				} catch (InterruptedException z) {
					// What should i do?
				}
			}		
			LoadMonitored.unblocked(x10.runtime.Sampling.CAUSE_FINISH, 0, null);		
		}
		parentWaiting = false;
	}
	
	public /*someThread*/ synchronized void notifySubActivitySpawn() {
		finishCount++;
		// new Error().printStackTrace();
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
    
    /** An activity created under this finish has terminated. Decrement the count
     * associated with the finish and notify the parent activity if it is waiting.
     */
    public /*someThread*/ synchronized void notifySubActivityTermination() {
		finishCount--;
    	// new Error().printStackTrace();
		if (parentWaiting && finishCount==0)
			this.notifyAll();
	}
    /** An activity created under this finish has terminated abruptly. 
     * Record the exception, decrement the count associated with the finish
     * and notify the parent activity if it is waiting.
     * 
     * @param t -- The exception thrown by the activity that terminated abruptly.
     */
    public /*someThread*/ synchronized void notifySubActivityTermination(Throwable t) {
    	finish_.push(t);
    	notifySubActivityTermination();
    }
   
    /** Return the stack of exceptions, if any, recorded for this finish.
     * 
     * @return -- stack of exceptions recorded for this finish.
     */
    public /*myThread*/ synchronized Stack exceptions() {
    	return finish_;
    }
    
    /** Return a string to be used in Report messages.
     */
    public synchronized String toString() {
    	return "<FinishState " + hashCode() + " " + finishCount + "," 
		+ parent.shortString()+"," + finish_ +">";
    }

}
