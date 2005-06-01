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
 * @see x10.runtime.Activity
 * 
 * @author vj May 17, 2005
 * 
 */
public class FinishState {

	protected Stack/* <Throwable> */ finish_ = new Stack();

	protected int finishCount = 0;
	protected Activity parent;
	/**
	 * 
	 */
	public FinishState( Activity activity ) {
		super();
		parent = activity;
		
	}
	
	public synchronized void notifySubActivityTermination() {
		finishCount--;
		if (finishCount==0)
			synchronized (parent) { // matches wait() in stopFinish.
				parent.notifyAll();
			}
	}
	
	public synchronized void increment() {
		finishCount++;
		if (Report.should_report("activity", 5)) {
			Report.report(5, " updating " + toString());
		}
	}
	
	public synchronized boolean isActive() {
		return finishCount > 0;
	}
    public synchronized void pushException( Throwable t) {
    	finish_.push(t);
    }
    
    public synchronized void notifySubActivityAbruptTermination(Throwable t) {
    	finish_.push(t);
    	notifySubActivityTermination();
    }
    public synchronized boolean terminatedNormally() {
    	return finish_.empty();
    }
    public synchronized Stack exceptions() {
    	return finish_;
    }
    public synchronized String toString() {
    	return "<FinishState " + hashCode() + " " + finishCount + "," 
		+ parent.shortString()+"," + finish_ +">";
    }

}
