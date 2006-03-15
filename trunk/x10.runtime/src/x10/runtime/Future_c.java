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
	 * CountDownLatch for signalling and wait -- can be replaced with a boolean latch
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
		cdl.countDown();
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
		cdl.countDown();
	}

	/*
	 * @see x10.runtime.Activity.Result#force()
	 */
	public Object force() {
		Thread t = Thread.currentThread();
		boolean incCount = false;
		if (cdl.getCount() != 0) {
			((PoolRunner) t).addPoolNew();
			incCount = true;
		}

		try {
			cdl.await();
		} catch (InterruptedException ie) {
			System.err.println("Future_c::force - unexpected exception e" + ie);
			throw new Error(ie); // this should never happen...
		}

		if (incCount)
			((PoolRunner) t).getPlace().decNumBlocked();

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
