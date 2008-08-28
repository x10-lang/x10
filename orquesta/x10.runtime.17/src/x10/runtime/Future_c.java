/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Sep 30, 2004
 */
package x10.runtime;

import x10.core.fun.Fun_0_0;
import x10.runtime.Future;
import x10.types.Type;
import x10.types.Types;

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
public final class Future_c<T> implements Future<T>, Fun_0_0<T> {

    /**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or RuntimeException
     * (since X10 only has unchecked exceptions).
     */
    private Throwable exception_;

    private T result_;
    private boolean completed_;
    private Type<T> type;

    /**
     * CountDownLatch for signaling and wait -- can be replaced by a boolean latch
     */
    private ModCountDownLatch cdl = new ModCountDownLatch(1);

    public Future_c(Type<T> type) {
        this.type = type;
    }

    public Type<?> rtt_x10$lang$Fun_0_0_U()  { return type; }

    /**
     * Set the result value returned by this async call.
     * @param result
     */
    public void setResult(T result) {
        this.result_ = result;
        this.completed_ = true;
        if (cdl.getCount() > 0) cdl.countDown();
        if ( VMInterface.ABSTRACT_EXECUTION_STATS ) {
            maxCritPathOps(x10.runtime.Runtime.getCurrentActivity().getCritPathOps());
            if ( VMInterface.ABSTRACT_EXECUTION_TIMES ) {
                x10.runtime.Runtime.getCurrentActivity().updateIdealTime();
                maxIdealTime(x10.runtime.Runtime.getCurrentActivity().getCritPathTime());
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
        this.completed_ = true;
        if (cdl.getCount() > 0) cdl.countDown();
        if ( VMInterface.ABSTRACT_EXECUTION_STATS ) {
            maxCritPathOps(x10.runtime.Runtime.getCurrentActivity().getCritPathOps());
            if ( VMInterface.ABSTRACT_EXECUTION_TIMES ) {
                x10.runtime.Runtime.getCurrentActivity().updateIdealTime();
                maxIdealTime(x10.runtime.Runtime.getCurrentActivity().getCritPathTime());
            }
        }
    }

    public T apply() {
        return force();
    }

    /*
     * @see x10.runtime.Future#force()
     */
    public T force() {
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
        if (VMInterface.ABSTRACT_EXECUTION_STATS) {
            x10.runtime.Runtime.getCurrentActivity().maxCritPathOps(getCritPathOps());
            if (VMInterface.ABSTRACT_EXECUTION_TIMES) {
                x10.runtime.Runtime.getCurrentActivity().maxCritPathTime(getIdealTime());
                x10.runtime.Runtime.getCurrentActivity().setResumeTime();
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
     * @see x10.runtime.Future#forced()
     */
    public boolean forced() {
        return completed_;
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

    /**
     * An activity used to implement an X10 future.
     */
    public static abstract class Activity<T> extends x10.runtime.Activity {
        public Type<T> type;
        public Future_c<T> future;
        
        public Activity(Type<T> type) {
            this.type = type;
        }

        /**
         * Wait for the completion of this activity and return the
         * return value.
         */
        public abstract T getResult();

        public abstract void runSource();

        public void runX10Task() {
            try {
                try {
                    startFinish();
                    runSource();
                } catch (Throwable t) {
                    try {
                        pushException(t);
                        stopFinish(); // must throw an exception.
                    } catch (Throwable t1) {
                        // Now nested asyncs have terminated.
                        future.setException(t1);
                        return;
                    }
                }
                stopFinish(); // this may throw an exception if a nested async did.
                // Normal termination.
                future.setResult(getResult());
            } catch (Throwable t) {
                // Now nested asyncs have terminated.
                future.setException(t);
            }
        }

        public void finalizeTermination() {
            super.finalizeTermination();
        }

        public void finalizeTermination(Throwable t) {
            future.setException(t);
            super.finalizeTermination(t);
        }

    } // end of Activity.Expr
}
