package x10.runtime;

import x10.lang.Activity;
import x10.lang.Future;
import x10.lang.Place;

/**
 * A LocalPlace_c is an implementation of a place
 * that runs on this Java Virtual Machine.  In the
 * future we will have RemotePlaces that refer to
 * Places on other machines.
 * 
 * @author Christian Grothoff
 */
class LocalPlace_c
    implements Place {

    private final ThreadRegistry reg_;
    
    private final ActivityInformationProvider aip_;

    /**
     * Is this place shutdown?
     */
    boolean shutdown;
    
    /**
     * Linked list of threads in the thread pool that are not currently
     * assigned to an Activity.  Package scoped to allow sampling.
     */
    PoolRunner threadQueue_;
    
    LocalPlace_c(ThreadRegistry reg, ActivityInformationProvider aip) {
        super();
        this.reg_ = reg;
        this.aip_ = aip;
    }
    
    
    /**
     * Shutdown this place, the current X10 runtime will exit.    Assumes
     * that all activities have already completed.  Threads beloging
     * to activities that are not done at this point will be left
     * running (which is probably a good policy, least for the thread
     * that calls shutdown :-).
     */
    public synchronized void shutdown() {
        shutdown = true;
        while (this.threadQueue_ != null) {
            threadQueue_.shutdown();
            try {
                threadQueue_.join();
            } catch (InterruptedException ie) {
                throw new Error(ie); // should never happen...
            }
            threadQueue_ = threadQueue_.next;
        }
    }

    /**
     * Run the given activity asynchronously.
     */
    public void runAsync(final Activity a) {
        final Activity i = aip_.getCurrentActivity();
        assert i != a;
        final StartSignal startSignal = new StartSignal();
        this.execute(new Runnable() {
                public void run() {
                    Thread t = Thread.currentThread();
                    reg_.registerActivityStart(t, a, i);
                    synchronized(startSignal) {
                        startSignal.go = true;
                        startSignal.notifyAll();
                    }
                    try {
                        a.run();
                    } finally {
                        reg_.registerActivityStop(t, a);
                    }
                }
            });
        // we now need to wait at least (!) until the 
        // "reg_.registerActivityStart(...)" line has been
        // reached.  Hence we wait on the start signal.
        synchronized (startSignal) {
            try {
                while (! startSignal.go) {
                    startSignal.wait();
                }
            } catch (InterruptedException ie) {
                throw new Error(ie); // should never happen!
            }
        }
    }

    /**
     * Run the given activity asynchronously.  Return a handle that
     * can be used to force the future result.
     */
    public Future runFuture(final Activity.Expr a) {
        final Future_c result = new Future_c();
        final Activity i = aip_.getCurrentActivity();
        assert i != a;
        this.execute(new Runnable() {
                public void run() {
                    Thread t = Thread.currentThread();
                    reg_.registerActivityStart(t, a, i);
                    try {
                        a.run();                    
                        result.setResult(a.getResult());
                    } finally {
                        reg_.registerActivityStop(t, a);
                    }
                }
            });
        return result;
    }

    /**
     * Run the given Runnable using one of the threads in the thread pool.
     * Note that the activity is guaranteed to be assigned a thread 
     * right away, the pool can never be exhausted (the size is infinite).
     * 
     * @param r the activity to run
     * @throws InterruptedException
     */
    protected synchronized void execute(Runnable r) {
        PoolRunner t;
        if (threadQueue_ == null) {
            t = new PoolRunner();
            reg_.registerThread(t, this);
            t.start();
        } else {
            t = threadQueue_;
            threadQueue_ = t.next;
        }
        t.run(r);
    }
     
    /**
     * Method called by a PoolRunner to add a thread back
     * to the thread pool (the PoolRunner is done running
     * the job).
     * 
     * @param r
     */
    synchronized final void repool(PoolRunner r) {
        r.next = threadQueue_;
        threadQueue_ = r;
    }
    
    /**
     * Thread in the thread pool that can be used to run multiple
     * activities over time.
     *
     * @author Christian Grothoff
     */
    final class PoolRunner extends Thread {
        /**
         * For building a linked list of these.
         */
        PoolRunner next;
        private boolean active = true;
        private Runnable job;
        synchronized void shutdown() {
            active = false;
            this.notifyAll();
        }
        /**
         * Assign a new job to this runner and start running!
         * @param r
         */
        synchronized void run(Runnable r) {
            assert job == null;
            job = r;
            this.notifyAll();
        }
        /**
         * Run jobs until shutdown is called.
         */
        public synchronized void run() {
            while (active) {
                while (active && job == null) {
                    try {
                        wait();
                    } catch (InterruptedException ie) {
                        throw new Error(ie);
                    }
                }
                if (job != null) {
                    Runnable j = job;
                    job = null;
                    try {
                        j.run();
                    } catch (Throwable t) {
                        t.printStackTrace(); // see X10 spec for exceptions...
                    }
                    // notify the LocalPlace_c that we're again available
                    // for more work!
                    synchronized (LocalPlace_c.this) {
                        if (LocalPlace_c.this.shutdown)
                            return;
                        else
                            repool(this);
                    }
                }
            }
        }
    } // end of LocalPlace_c.PoolRunner

    
    static class StartSignal {
        boolean go;
    }    
    
} // end of LocalPlace_c