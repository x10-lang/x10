/*
 * Created on Sep 30, 2004
 */
package x10.runtime;

import x10.cluster.ClusterRuntime;
import x10.cluster.X10Runnable;
import x10.cluster.X10Serializer;
import x10.cluster.message.MessageType;
import x10.lang.Future;
import x10.lang.Object;

/**
 * This class encapsulates the return value of a local async
 * call and allows the client to wait for the completion of the
 * async call (force future).
 * 
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author vj
 */
public final class Future_c extends Future {
	//cluster runtime
	public Object force() {
		if(rref == null || ClusterRuntime.isLocal(rref.getPlace())) {
			return force_();
		} else {
			//TODO: run code remotely, waiting for return
			throw new Error("Future_c.force()");
		}
	}
	public void setException(final Throwable t) {
		if(rref == null || ClusterRuntime.isLocal(rref.getPlace())) {
			setException_(t);
		} else {
			//asynchronously is fine
			X10Serializer.serializeCode(rref.getPlace().id, new X10Runnable(MessageType.FUT) {
				public void run() {
					setException_(t);
				}
			});
		}
	}
	public void setResult(final Object res) {
		//System.out.println("Future_c.setResult: "+rref+" "+res.getClass());
		if(rref == null || ClusterRuntime.isLocal(rref.getPlace())) {
			setResult_(res);
		} else {
			//asynchronously is fine
			X10Serializer.serializeCode(rref.getPlace().id, new X10Runnable(MessageType.FUT) {
				public void run() {
					setResult_(res);
				}
			});
		}
	}
	
	
	
    private boolean haveResult_;
       
    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or RuntimeException
     * (since X10 only has unchecked exceptions).
     */
    private Throwable exception_;
    
    private Object result_;
    
    public Future_c() {}  
    
    /**
     * Set the result value returned by this async call.
     * 
     * @param result
     */
    public synchronized void setResult_(Object result) {
        assert (! haveResult_); 
        this.result_ = result;
        this.haveResult_ = true;
        this.notifyAll(); // wake up 'force' if waiting
    }
    
    /**
     * Set the result value returned by this async call. 
     * Caller must ensure that this is called only after the activity
     * associated with the future has finish'ed.
     * 
     * @param result
     */
    public synchronized void setException_(Throwable t) {
        if (haveResult_) {
            t.printStackTrace();
            // FIXME: what should happen here?
        } else {
            this.exception_ = t;
            this.haveResult_ = true;
            this.notifyAll(); // wake up 'force' if waiting
        }
    }
    
    /*
     * @see x10.runtime.Activity.Result#force()
     */
    public synchronized Object force_() {
    	while (! haveResult_) {
    		try {
    			this.wait();
    		} catch (InterruptedException ie) {
    			System.err.println("Future_c::force - unexpected exception e" + ie);
    			throw new Error(ie); // this should never happen...
    		}
    	}
  
    	if (exception_ != null) {
    		if (exception_ instanceof Error)
    			throw (Error) exception_;
    		if (exception_ instanceof RuntimeException)
    			throw (RuntimeException) exception_;
    		assert false;
    	} 
    
    	return result_;
    }
}
