package x10.runtime;

import java.lang.reflect.Field;
import java.util.ArrayList;

import x10.lang.Activity;
import x10.lang.Future;

/**
 * A LocalPlace_c is an implementation of a place
 * that runs on this Java Virtual Machine.  In the
 * future we will have RemotePlaces that refer to
 * Places on other machines.
 * 
 * @author Christian Grothoff
 */
public class LocalPlace_c extends Place {

    /**
     * Compute the number of simulated cycles spent
     * globally at this point.
     * 
     * @return max of all simulatedPlaceTimes of all places
     */
    public static long systemNow() {
        long max = 0;
        Place[] places = x10.lang.Runtime.places();
        for (int i=places.length-1;i>=0;i--) {
            long val = 0;
            if (places[i] instanceof LocalPlace_c) {
             val = ((LocalPlace_c) places[i]).getSimulatedPlaceTime();
            }
            if (val > max)
                max = val;
        }
        return max;
    }

    
    private final ThreadRegistry reg_;
    
    private final ActivityInformationProvider aip_;

    /**
     * Is this place shutdown?
     */
    boolean shutdown;

    /**
     * How many threads are truely running (not blocked) in this place?
     */
    int runningThreads;
    
    /**
     * Linked list of threads in the thread pool that are not currently
     * assigned to an Activity.  Package scoped to allow sampling.
     */
    PoolRunner threadQueue_;

    /**
     * List of all of the threads of this place.
     */
    final ArrayList myThreads = new ArrayList(); // <PoolRunner>
    
    /**
     * The amount of cycles that this places was blocked waiting
     * for activities at other places to complete.  
     */
    long blockedTime;
    
    /**
     * "global" time at which this place was blocked (that is, all
     * activities at this place were blocked).
     */
    private long startBlock; 
    
    LocalPlace_c(ThreadRegistry reg, ActivityInformationProvider aip) {
        super();
        this.reg_ = reg;
        this.aip_ = aip;
        startBlock = systemNow();
    }
    
    /**
     * Get how many cycles were spent in computation or blocked at this 
     * place so far.  Only (sort of) works on JikesRVM where we can get
     * per-thread cycle counts.
     *
     * @return
     */
    public long getSimulatedPlaceTime() {
        long ret = blockedTime;
        for (int i = myThreads.size()-1;i>=0;i--)
            ret += ((PoolRunner)myThreads.get(i)).getThreadRunTime();
        return ret;
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
                    a.run();
                }
            }, a);
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
        final Future_c result = new Future_c(a);
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
                    a.run();                    
                    result.setResult(a.getResult());
                }
            }, a);
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
    protected synchronized void execute(Runnable r, Activity act) {
        PoolRunner t;
        if (threadQueue_ == null) {
            t = new PoolRunner();
            reg_.registerThread(t, this);
            t.start();
        } else {
            t = threadQueue_;
            threadQueue_ = t.next;
        }
        t.run(r, act);
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
     * Change the 'running' status of a thread.
     * @param delta +1 for thread starts to run (unblocked), -1 for thread is blocked
     */
    synchronized void changeRunningStatus(int delta) {
        if (runningThreads == 0) {
            assert delta > 0;
            this.blockedTime += systemNow() - startBlock;
        }
        runningThreads += delta;
        if (runningThreads == 0) {
            assert delta < 0;
            startBlock = systemNow();
        }
    }
    
    /**
     * Thread in the thread pool that can be used to run multiple
     * activities over time.
     *
     * @author Christian Grothoff
     */
    final class PoolRunner extends LoadMonitored { // if you do NOT want load monitoring, make 'extend Thread'
        /**
         * For building a linked list of these.
         */
        PoolRunner next;
        private boolean active = true;
        private Runnable job;
        private Activity act;
        PoolRunner() {
            myThreads.add(this);
        }
        
        /**
         * On JikesRVM, get the total number of CPU cycles spend in this
         * thread.  We use reflection mostly because we want this to
         * still link (!) and compile under other VMs.  
         * @return 0 on error
         */
        long getThreadRunTime() {
            try {
                Field vmt = java.lang.Thread.class.getDeclaredField("vmdata");
                vmt.setAccessible(true);
                Object o = vmt.get(this); // o is 'VM_Thread'
                Field trt = o.getClass().getDeclaredField("totalCycles");
                trt.setAccessible(true);
                return trt.getLong(o);                
            } catch (SecurityException se) {
                // System.out.println("GSPT: " + se);
            } catch (IllegalAccessException iae) {
                // System.out.println("GSPT: " + iae);
            } catch (NoClassDefFoundError ncfe) {
                // System.out.println("GSPT: " + ncfe);
            } catch (NoSuchFieldException nsfe) {
                // System.out.println("GSPT: " + nsfe);
                // not JikesRVM
            }
            return 0;   
        }
        
        synchronized void shutdown() {
            active = false;
            this.notifyAll();
        }
        /**
         * Assign a new job to this runner and start running!
         * @param r
         */
        synchronized void run(Runnable r, Activity a) {
            assert job == null;
            act = a;
            job = r;
            this.notifyAll();
        }
        /**
         * Change the 'running' status of a thread.
         * @param delta +1 for thread starts to run (unblocked), -1 for thread is blocked
         */
        public void changeRunningStatus(int delta) {
            LocalPlace_c.this.changeRunningStatus(delta);
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
                        changeRunningStatus(1);
                        j.run();
                    } catch (Throwable t) {
                        t.printStackTrace(); // see X10 spec for exceptions...
                    } finally {
                        changeRunningStatus(-1);
                        reg_.registerActivityStop(Thread.currentThread(), 
                                                              act);
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