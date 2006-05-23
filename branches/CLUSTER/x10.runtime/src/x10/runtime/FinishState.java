/*
 * Created by vj on May 17, 2005
 *
 * 
 */
package x10.runtime;

import java.util.Stack;

import x10.cluster.ClusterRuntime;
import x10.cluster.HasResult;
import x10.cluster.X10RemoteRef;
import x10.cluster.X10Runnable;
import x10.cluster.X10Serializer;
import x10.cluster.message.MessageType;

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
public class FinishState implements FinishStateOps {
	/**
	 * FinishState gets passed between activities. The propagation model needs 
	 * it to behave like a remote object.
	 */
	public X10RemoteRef rref = null;
	public void notifySubActivityTermination() {
		//System.out.println("FinishState.notifyActivityTermination ...");
		if(rref == null || ClusterRuntime.isLocal(rref.getPlace())) {
			notifySubActivityTermination_();
		} else { //remote call
			//asynchronously is fine
			X10Serializer.serializeCode(rref.getPlace().id, new X10Runnable(MessageType.FIN) {
				public void run() {
					notifySubActivityTermination_();
				}
			});
		}
	}
	public void notifySubActivityTermination(final Throwable t) {
		if(rref == null || ClusterRuntime.isLocal(rref.getPlace())) {
			notifySubActivityTermination_(t);
		} else { //remote call
			//asynchronously is fine
			X10Serializer.serializeCode(rref.getPlace().id, new X10Runnable(MessageType.FIN) {
				public void run() {
					notifySubActivityTermination_(t);
				}
			});
		}
	}
	public void notifySubActivitySpawn() {
		if(rref == null || ClusterRuntime.isLocal(rref.getPlace())) {
			notifySubActivitySpawn_();
		} else { //remote call
			//synchronously
			X10Serializer.serializeCodeW(rref.getPlace().id, new HasResult(MessageType.FIN) /*Runnable()*/ {
				public void run() {
					notifySubActivitySpawn_();
				}
				public Object getResult() { return null; }
			});
		}
	}

	protected Stack/* <Throwable> */ finish_ = new Stack();

	protected int finishCount = 0;
	protected Activity parent; // not really needed, used in toString().
	protected boolean parentWaiting = false; // optimization.
	
	/** 
	 * Create a new finish state for the given activity.
	 */
	public FinishState( Activity activity ) {
		super();
		parent = activity;
		
		//export rmi
		/*
		try {
			UnicastRemoteObject.exportObject(this);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		*/
	}
	
	public /*myThread*/ synchronized void waitForFinish() {
		if (finishCount == 0 ) return;
		parentWaiting = true;
		if (finishCount > 0) {			
			while (finishCount > 0) {
				try {
					this.wait();
				} catch (InterruptedException z) {
					// What should i do?
				}
			}			
		}
		parentWaiting = false;
	}
	
	private /*someThread*/ synchronized void notifySubActivitySpawn_() {
		finishCount++;
		
		//System.out.println("FinsihState.notifySubActivitySpawn: new ... "+this+" "+finishCount);
		
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
    private /*someThread*/ synchronized void notifySubActivityTermination_() {    	
		finishCount--;
		
		//System.out.println("FinsihState.notifyActivityTermination_: terminating ..."+this+" "+finishCount);
		
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
    private /*someThread*/ synchronized void notifySubActivityTermination_(Throwable t) {
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
		+ (parent == null? "null" : parent.shortString())+"," + finish_ +">";
    }

}
