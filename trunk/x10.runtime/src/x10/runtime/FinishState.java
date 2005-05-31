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
			synchronized (parent) {
				parent.notifyAll();
			}
	}
	
    public synchronized void pushException( Throwable t) {
    	finish_.push(t);
    }
    
    public synchronized void notifySubActivityAbruptTermination(Throwable t) {
    
    	finish_.push(t);
    	notifySubActivityTermination();
    }
    
    public String toString() {
    	return "<FinishState " + hashCode() + " " + finishCount + "," 
		+ parent.shortString()+"," + finish_ +">";
    }

}
