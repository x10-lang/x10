/*
 * Created on Sep 30, 2004
 */
package x10.runtime;

import x10.lang.Activity;
import x10.lang.Future;
import x10.lang.Object;

/**
 * This class encapsulates the return value of a local async
 * call and allows the client to wait for the completion of the
 * async call (force future).
 * 
 * @author Christian Grothoff
 * @author Christoph von PRaun
 */
final class Future_c extends Future {

    private boolean haveResult_;
    
    private final Activity waitFor_;     

    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or RuntimeException
     * (since X10 only has unchecked exceptions).
     */
    private Throwable exception_;
    
    private Object result_;
    
    Future_c(Activity wf) {
        this.waitFor_ = wf;
    }
    
    /**
     * Set the result value returned by this async call.
     * 
     * @param result
     */
    public synchronized void setResult(Object result) {
        assert (! haveResult_); 
        this.result_ = result;
        this.haveResult_ = true;
        this.notifyAll(); // wake up 'force' if waiting
    }
    
    /**
     * Set the result value returned by this async call.
     * 
     * @param result
     */
    public synchronized void setException(RuntimeException t) {
        if (haveResult_) {
            t.printStackTrace();
            // FIXME: what should happen here?
        } else {
            this.exception_ = t;
            this.haveResult_ = true;
            this.notifyAll(); // wake up 'force' if waiting
        }
    }

    public synchronized void setException(Error t) {
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
    public synchronized Object force() {
        LoadMonitored.blocked(Sampling.CAUSE_FORCE, 0, waitFor_);
        try {
            while (! haveResult_) {
                try {
                    this.wait();
                } catch (InterruptedException ie) {
                    throw new Error(ie); // this should never happen...
                }
            }
        } finally {
            LoadMonitored.unblocked(Sampling.CAUSE_FORCE, 0, waitFor_);
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
