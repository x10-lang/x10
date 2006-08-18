/*
 * Created on Sep 30, 2004
 */
package x10.runtime;

import x10.lang.Future;
import x10.lang.Object;
import x10.lang.place;

/**
 * This class encapsulates the return value of a local async
 * call and allows the client to wait for the completion of the
 * async call (force future).
 * 
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author vj
 * 
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: use ModCountDownLatch (modified version of JCU CountDownLatch) to implement force().
 * (Note that ModCountDownLatch supports a counting semaphore, as needed by finish, but it also suffices
 * for the binary semaphore functionality needed by futures.)
 */
public final class Future_c extends Future {

    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or RuntimeException
     * (since X10 only has unchecked exceptions).
     */
    private Throwable exception_;

    private Object result_;

    /**
     * CountDownLatch for signalling and wait -- can be replaced by a boolean latch
     */
    private ModCountDownLatch cdl = new ModCountDownLatch(1);

    public Future_c() {
    }

    /**
     * Set the result value returned by this async call.
     * 
     * @param result
     */
    public void setResult(Object result) {
	this.result_ = result;
	if (cdl.getCount() > 0) cdl.countDown();
	if ( JITTimeConstants.ABSTRACT_EXECUTION_STATS ) {
		maxCritPathOps(x10.lang.Runtime.getCurrentActivity().getCritPathOps());
		if ( JITTimeConstants.ABSTRACT_EXECUTION_TIMES ) {
			x10.lang.Runtime.getCurrentActivity().updateIdealTime();
			maxIdealTime(x10.lang.Runtime.getCurrentActivity().getCritPathTime());
		}
	}
    }

    /**
     * Set the result value returned by this async call. 
     * Caller must ensure that this is called only after the activity
     * associated with the future has finish'ed.
     * 
     * @param result
     */
    public void setException(Throwable t) {
	this.exception_ = t;
	if (cdl.getCount() > 0) cdl.countDown();
	if ( JITTimeConstants.ABSTRACT_EXECUTION_STATS ) {
		maxCritPathOps(x10.lang.Runtime.getCurrentActivity().getCritPathOps());
		if ( JITTimeConstants.ABSTRACT_EXECUTION_TIMES ) {
			x10.lang.Runtime.getCurrentActivity().updateIdealTime();
			maxIdealTime(x10.lang.Runtime.getCurrentActivity().getCritPathTime());
		}
	}
	}

    /*
     * @see x10.runtime.Activity.Result#force()
     */
    public Object force() {
    	PoolRunner t = (PoolRunner) Thread.currentThread();
	if (cdl.getCount() > 0) {
		((PoolRunner) t).getPlace().threadBlockedNotification();
	    try {
		cdl.await();
	    } catch (InterruptedException ie) {
		System.err.println("Future_c::force - unexpected exception e" + ie);
		throw new Error(ie); // this should never happen...
	    }
	    ((PoolRunner) t).getPlace().threadUnblockedNotification();
	}
	if (JITTimeConstants.ABSTRACT_EXECUTION_STATS) {
		x10.lang.Runtime.getCurrentActivity().maxCritPathOps(getCritPathOps());
		if (JITTimeConstants.ABSTRACT_EXECUTION_TIMES) {
			x10.lang.Runtime.getCurrentActivity().maxCritPathTime(getIdealTime());
			x10.lang.Runtime.getCurrentActivity().setResumeTime();
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
    
	/*
	 * critPathOps keeps track of operations defined by user by calls to x10.lang.perf.addLocalOps()
	 */
    private long critPathOps = 0; // Current critical path length for this latch (in user-defined operations)
	
	synchronized public void maxCritPathOps(long n) {
		critPathOps = Math.max(critPathOps, n);
	}
	
    /**
     * getCritPathOps() should only be called after a next operation has succeeded
     * (That's why it need not be a synchronized method.)
     */
    public long getCritPathOps() { return critPathOps; }
    
    private long curIdealTime = 0; // Current "ideal" execution time for this latch (assuming unbounded resources)
	
	synchronized public void maxIdealTime(long t) { curIdealTime = Math.max(curIdealTime, t); }

    /**
     * getIdealTime() should only be called after a next operation has succeeded
     * (That's why it need not be a synchronized method.)
     */
    public long getIdealTime() { return curIdealTime; }

}
