/*
 * Created on Sep 30, 2004
 */
package x10.runtime;

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
    
    private Object result_;
    
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
    
    /*
     * @see x10.runtime.Activity.Result#force()
     */
    public synchronized Object force() {
        while (! haveResult_) {
            try {
                LoadMonitored.blocked(Sampling.CAUSE_FORCE, 0);
                this.wait();
            } catch (InterruptedException ie) {
                throw new Error(ie); // this should never happen...
            } finally {
                LoadMonitored.unblocked(Sampling.CAUSE_FORCE, 0);
            }
        }
        return result_;
    }
}
