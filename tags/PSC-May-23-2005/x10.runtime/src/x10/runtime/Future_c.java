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
 * @author Christoph von Praun
 */
final class Future_c extends Future  {

    private boolean haveResult_;
    
    private final Activity.Expr waitFor_;     

    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or RuntimeException
     * (since X10 only has unchecked exceptions).
     */
    private Throwable exception_;
    
    private Object result_;
    
    private final Clock clock_;
    
    private final Activity starter_;
    
    Future_c(Activity.Expr wf) {
        this.waitFor_ = wf;
        this.clock_ = new Clock((ActivityInformationProvider) x10.lang.Runtime.runtime, this);
        this.starter_ = ((ActivityInformationProvider) x10.lang.Runtime.runtime).getCurrentActivity();
    }
    
    Clock getClock() {
        return clock_;
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
        try {
            while (! haveResult_) {
                try {
                    this.wait();
                } catch (InterruptedException ie) {
                    System.out.println("Future_c::force - unexpected exception e" + ie);
                    throw new Error(ie); // this should never happen...
                }
            }
        } finally {
            try {
                // x10.lang.Runtime.doNext(); // CVP that will make ClocktTest10 fail
                clock_.resume(starter_);
                clock_.doNext(starter_);
                clock_.drop(starter_);
            } catch (java.lang.RuntimeException re) {
                ((x10.runtime.DefaultRuntime_c)x10.lang.Runtime.runtime).registerActivityException(waitFor_, re);
            } catch (java.lang.Error er) {
                ((x10.runtime.DefaultRuntime_c)x10.lang.Runtime.runtime).registerActivityException(waitFor_, er);
            } catch (Throwable t) {
                exception_ = t;
            } finally {
                if (exception_ == null) {
                    java.lang.Throwable t = ((x10.runtime.DefaultRuntime_c)x10.lang.Runtime.runtime).getFinishExceptions(waitFor_);
                    if (t != null)
                        exception_ = t;
                }
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
