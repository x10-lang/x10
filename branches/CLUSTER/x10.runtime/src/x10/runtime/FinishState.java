/*
 * Created by vj on May 17, 2005
 *
 * 
 */
package x10.runtime;

import java.util.Stack;

import x10.cluster.ClusterRuntime;
import x10.cluster.Debug;
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
 * @xinb
 * 	extension for distributed scenario.
 */
public class FinishState implements FinishStateOps {
	/**
	 * After <code>waitForFinish</code> returns, the life cycle of this object
	 * ends.  Furthur call on this object signifies error.  
	 * For debug purpose. 
	 */
	private boolean isLive = true;
	
	/**
	 * FinishState gets passed between activities. The propagation model needs 
	 * it to behave like a remote object.
	 */
	public X10RemoteRef rref = null;
	
	/**
	 * We can save some cross VM communications, if activities with a shadow
	 * FinishState object spawns new activities.
	 * 
	 * <code>shadowCount</code> the total number of activities running on this 
	 * FinishState object.
	 * 
	 * <code>remoteChild</code> number of activities crossing nodes, and for which
	 * the root FinishState are waiting on.
	 * 
	 * @postcondition shadowCount >= remoteChild   If shadowCount >> remoteChild, we win.
	 */
	private int remoteChild = 0;
	private int shadowCount = 0;
	
	public boolean notShadow() {
		return rref == null || ClusterRuntime.isLocal(rref.getPlace());
	}
	public synchronized void notifySubActivityTermination() {
		assert (isLive);
		if( notShadow()) {
			notifySubActivityTermination_(1);
		} else { //remote call
			shadowCount--;
			if( shadowCount == 0) {
				//System.out.println("FinishState.Termi remote."+this);
				//asynchronously is fine
				final int finishedCnt = remoteChild;
				if(finishedCnt > 1) System.out.println("FinishState.Termi remote: finishedCnt = "+finishedCnt);
				X10Serializer.serializeCode(rref.getPlace().id, new X10Runnable(MessageType.FIN) {
					public void run() {
						notifySubActivityTermination_(finishedCnt);
					}
				});
				
				remoteChild = 0;
			} 
		}
	}
	public synchronized void notifySubActivityTermination(final Throwable t) {
		assert (isLive);
		if( notShadow()) {
			notifySubActivityTermination_(t, 1);
		} else { //remote call
			shadowCount--;
			if( shadowCount == 0) {
				//asynchronously is fine
				final int finishedCnt = remoteChild;
				X10Serializer.serializeCode(rref.getPlace().id, new X10Runnable(MessageType.FIN) {
					public void run() {
						notifySubActivityTermination_(t, finishedCnt);
					}
				});
				
				remoteChild = 0;
			}
		}
	}
	public synchronized void notifySubActivitySpawn() {
		assert (isLive);
		//System.out.println("FinishState.Spawn ."+this);
		if( notShadow()) {
			notifySubActivitySpawn_();
		}/* else { //remote call
			shadowCount ++;
			if( shadowCount == 1 ) {
				//synchronously
				X10Serializer.serializeCodeW(rref.getPlace().id, new HasResult(MessageType.FIN) {
					public void run() {
						notifySubActivitySpawn_();
					}
					public Object getResult() { return null; }
				});
				
				remoteChild = 1;
			}
		}*/
	}
	/**
	 * This is called when an activity is spawn at a node with an existing FinishState object.
	 * When this FinishState object is the real thing, we increase the <code>finishCount</code>
	 * counter; Otherwise, if this is a shadow, we increase the <code>shadowCount</code> 
	 * counter.
	 *
	 */
	public synchronized void notifySubActivitySpawnAtChild(boolean fromRoot) {
		assert (isLive);
		if( notShadow()) {
			if(!fromRoot) notifySubActivitySpawn_();
		} else {
			shadowCount ++;
			if( shadowCount == 1 ) {
				//synchronously remote call
				if(!fromRoot) 
					X10Serializer.serializeCodeW(rref.getPlace().id, new HasResult(MessageType.FIN) {
						public void run() {
							notifySubActivitySpawn_();
						}
						public Object getResult() { return null; }
					});
				
				remoteChild = 1;
			} else {
				if(fromRoot) remoteChild ++;
			}
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
		
		remoteChild = shadowCount = 0;
		isLive = true;
	}
	
	public /*myThread*/ synchronized void waitForFinish() {
		assert (notShadow());
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
		isLive = false; 
	}
	
	private /*someThread*/ synchronized void notifySubActivitySpawn_() {
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
    
    /** An activity created under this finish has terminated. Decrement the count
     * associated with the finish and notify the parent activity if it is waiting.
     */
    private /*someThread*/ synchronized void notifySubActivityTermination_(final int cnt) {
    	//System.out.println("FinishState.Termi ."+this);
		finishCount -= cnt; //finishCount--;
		
		if (parentWaiting && finishCount==0)
			this.notifyAll();
	}
    /** An activity created under this finish has terminated abruptly. 
     * Record the exception, decrement the count associated with the finish
     * and notify the parent activity if it is waiting.
     * 
     * @param t -- The exception thrown by the activity that terminated abruptly.
     */
    private /*someThread*/ synchronized void notifySubActivityTermination_(Throwable t, final int cnt) {
    	finish_.push(t);
    	notifySubActivityTermination_(cnt);
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
    	return "<"+notShadow()+"#FinishState " + (rref == null?null: rref)+","+
    	finishCount+","+shadowCount+","+remoteChild+"," + 
    	finish_ +">";
    }

}
