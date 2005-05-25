package x10.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import x10.lang.Future;


/**
 * A LocalPlace_c is an implementation of a place
 * that runs on this Java Virtual Machine.  In the
 * future we will have RemotePlaces that refer to
 * Places on other machines.
 * 
 * @author Christian Grothoff
 * @author vj
 */
public class LocalPlace_c extends Place {

    /**
     * Compute the number of simulated cycles spent
     * globally at this point.
     * 
     * this method must not be called in the constructor of 
     * LocalPlace_c, and not before the runtime is initialized 
     * completely.
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
    
    public static void initAllPlaceTimes(Place[] places) {
        long max = 0;
        for (int i=places.length-1;i>=0;i--) {
            assert (places[i] instanceof LocalPlace_c);
            long val = 0;
            LocalPlace_c lp = (LocalPlace_c) places[i];
            val = lp.getSimulatedPlaceTime();
            if (val > max)
                max = val;
        }
        for (int i=places.length-1;i>=0;i--) {
            LocalPlace_c lp = (LocalPlace_c) places[i];
            lp.startBlock = max; 
        }
    }
    
   
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
    
    LocalPlace_c() {
        super();
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
        synchronized (myThreads) {
            for (int i = myThreads.size()-1;i>=0;i--)
                ret += ((PoolRunner)myThreads.get(i)).getThreadRunTime();
        }
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
    	synchronized (this) {
    		shutdown = true;
    		if (Report.should_report("activity", 5)) {
    			Report.report(5, Thread.currentThread() +  "@" + System.currentTimeMillis() 
    					+" shutting down " + threadQueue_);
    			PoolRunner list = threadQueue_;
    			while (list != null) {
    				if (Report.should_report("activity", 5)) {
    					Report.report(5, Thread.currentThread() +  "@" + list.place + ":" + System.currentTimeMillis() 
    						+"  threadpool contains " + list);
    				}
    				list = list.next;
    			}
    		}
    	}
        while (this.threadQueue_ != null) {
        	if (Report.should_report("activity", 5)) {
        		Report.report(5, Thread.currentThread() +  "@" + System.currentTimeMillis() +" shutting down " + threadQueue_);
        	}
        	
            threadQueue_.shutdown();
            
            try {
                threadQueue_.join();
            	if (Report.should_report("activity", 5)) {
            		Report.report(5, Thread.currentThread() +  "@" + System.currentTimeMillis() + " " + threadQueue_ + " shut down.");
            	}
            } catch (InterruptedException ie) {
                throw new Error(ie); // should never happen...
            }
            
            threadQueue_ = threadQueue_.next;
        }
    }

    /**
     * Run the given activity asynchronously.
     * vj 5/17/05. This has been completely revamped,
     * with Activity given much more responsibility for its execution.
     */
    public void runAsync(final Activity a) {
    	runAsync( a, false);
    }
    /**
     * Run this activity asynchronously, as if it is wrapped in a finish.
     * That is, wait for its global termination.
     * @param a
     */
    public void finishAsync( final Activity a) {
    	runAsync( a, true);
    }
    protected void runAsync(final Activity a, final boolean finish) {
   
      //  final StartSignal startSignal = new StartSignal();
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof ActivityRunner) {
        	Activity parent = ((ActivityRunner) currentThread).getActivity();
        	parent.finalizeActivitySpawn(a);
        }
        a.initializeActivity();
        this.execute(new Runnable() {
            public void run() {
            	// Get a thread to run this activity.
                PoolRunner t = (PoolRunner) Thread.currentThread();
                if (Report.should_report("activity", 5)) {
            		Report.report(5, t + " is running " + this);
            	}
 
                // Install the activity.
                t.setActivity(a);
                a.setPlace(LocalPlace_c.this);
                
            /*  synchronized(startSignal) {
                    startSignal.go = true;
                    startSignal.notifyAll();
                }*/
                
                try {
                	if (finish) {
                		a.finishRun();
                	} else {
                		a.run();
                	}
                	a.finalizeTermination();
                } catch (Throwable e) {
                	// e.printStackTrace();
                	// System.err.println("LocalPlace_c::runAsync - unexpected exception " + e);
                	// can never arrive here if finish=true
                	a.finalizeAbruptTermination(e);
                } 
            }
            public String toString() { return "<Executor " + a+">";}
            
        });
        // vj: 5/17 Check why this needs to be done.
        // we now need to wait at least (!) until the 
        // "reg_.registerActivityStart(...)" line has been
        // reached.  Hence we wait on the start signal.
       /*synchronized (startSignal) {
            try {
                while (! startSignal.go) {
                    startSignal.wait();
                }
            } catch (InterruptedException ie) {
                System.err.println("LocalPlace_c::runAsync - unexpected exception " + ie);
                throw new Error(ie); // should never happen!
            }
        }*/
    }
    
    /**
     * Run the given activity asynchronously.  Return a handle that
     * can be used to force the future result.
     */
    public Future runFuture(final Activity.Expr a) {
    	Future_c result = a.future = new Future_c(a);
    	runAsync(a);
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
    protected void execute(Runnable r) {
        PoolRunner t;
        synchronized(this) {
        	if (threadQueue_ == null) {
        		t = new PoolRunner(this);
        		t.setDaemon(false);
        		t.start();
        		if (Report.should_report("activity", 5)) {
            		Report.report(5, Thread.currentThread() +  "@" + System.currentTimeMillis() +"LocalPlace starts " 
            				+ (t.isDaemon() ? "" : "non") + "daemon thread " + t 
							+ "in group " + Thread.currentThread().getThreadGroup() 
							 +".");
            	}
        		
        	} else {
        		t = threadQueue_;
        		threadQueue_ = t.next;
        	}
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
    private synchronized final void repool(PoolRunner r) {
    	if (Report.should_report("activity", 5)) {
    		Report.report(5, Thread.currentThread() +  "@" + System.currentTimeMillis() +" repools (shutdown=" + shutdown + ").");
    	}
    	
        r.next = threadQueue_;
        threadQueue_ = r;
    }
    
    /**
     * Change the 'running' status of a thread.
     * @param delta +1 for thread starts to run (unblocked), -1 for thread is blocked
     */
    private synchronized void changeRunningStatus(int delta) {
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
    final class PoolRunner extends LoadMonitored 
		implements ActivityRunner { // if you do NOT want load monitoring, make 'extend Thread'
        /**
         * For building a linked list of these.
         */
        PoolRunner next;
        private boolean active = true;
        private Runnable job;
        private Activity act;
        private Method ac;
        private Object vmto;
        Place place; // not final, may be set by DefaultRuntime_c.setCurrentPlace(place p)
        
        PoolRunner(Place p) {
            place = p;
            synchronized (myThreads) { myThreads.add(this); }
            try {
                Field vmt = java.lang.Thread.class.getDeclaredField("vmdata");
                vmt.setAccessible(true);
                this.vmto = vmt.get(this); // o is 'VM_Thread'
                this.ac = vmto.getClass().getDeclaredMethod("accumulateCycles", new Class[0]);
                setName("PoolRunner" + hashCode());
            } catch (SecurityException se) {
                // System.out.println("GSPT: " + se);
            } catch (IllegalAccessException iae) {
                // System.out.println("GSPT: " + iae);
            } catch (NoClassDefFoundError ncfe) {
                // System.out.println("GSPT: " + ncfe);
            } catch (NoSuchMethodException ncme) {
                // System.out.println("GSPT: " + ncfe);
            } catch (NoSuchFieldException ncfe) {
                // System.out.println("GSPT: " + ncfe);
            }
        }
        public Activity getActivity() {
        	return act;
        }
        public void setActivity( Activity a) {
        	this.act = a;
        }
    
        /**
         * On JikesRVM, get the total number of CPU cycles spend in this
         * thread.  We use reflection mostly because we want this to
         * still link (!) and compile under other VMs.  
         * @return 0 on error
         */
        private long getThreadRunTime() {
            if (ac == null)
                return 0;
            try {
                // Field vmt = java.lang.Thread.class.getDeclaredField("vmdata");
                // vmt.setAccessible(true);
                // Object o = vmt.get(this); // o is 'VM_Thread'
                // Field trt = o.getClass().getDeclaredField("totalCycles");
                // trt.setAccessible(true);
                // return trt.getLong(o);                
                return ((Long)ac.invoke(vmto, new Object[0])).longValue();                
            } catch (SecurityException se) {
                // System.out.println("GSPT: " + se);
            } catch (IllegalAccessException iae) {
                // System.out.println("GSPT: " + iae);
            } catch (NoClassDefFoundError ncfe) {
                // System.out.println("GSPT: " + ncfe);
            } catch (InvocationTargetException ite) {
                // System.out.println("GSPT: " + ncfe);
            }/* catch (NoSuchFieldException nsfe) {
                // System.out.println("GSPT: " + nsfe);
                // not JikesRVM
            }*/
            return 0;   
        }
        
        synchronized void shutdown() {
            active = false;
            this.notifyAll();
        }
        /**
         * Assign a new job to this runner and notify it of the change so it can start running again.
         * @param r
         */
        synchronized void run(Runnable r) {
            assert job == null;
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
            	if (Report.should_report("activity", 5)) {
            		Report.report(5, Thread.currentThread() +  "@" + place + ":" + System.currentTimeMillis() +" waiting for a job.");
            	}
                while (active && job == null) {
                    try {
                        wait();
                    } catch (InterruptedException ie) {
                        throw new Error(ie);
                    }
                    if (Report.should_report("activity", 5)) {
                		Report.report(5, Thread.currentThread() +  "@" + place + ":" 
                				+ System.currentTimeMillis() +" awakes (shutdown=)" + shutdown + ".");
                	}
                }
                if (job != null) {
                    Runnable j = job;
                    job = null;
                    try {
                        changeRunningStatus(1);
                        if (Report.should_report("activity", 5)) {
                    		Report.report(5, this + "@" + place + ":" + System.currentTimeMillis() + " starts running " + j +".");
                    	}
                        j.run();
                     	if (Report.should_report("activity", 5)) {
                    		Report.report(5, this + "@" + place + ":" + System.currentTimeMillis() + " finished running " + j +".");
                    	}
                     
                    } finally {
                        changeRunningStatus(-1);
                        // act.finalizeTermination();
                    }
                    // notify the LocalPlace_c that we're again available
                    // for more work!
                    synchronized (LocalPlace_c.this) {
                        if (LocalPlace_c.this.shutdown) {
                        	if (Report.should_report("activity", 5)) {
                        		Report.report(5, Thread.currentThread() +  "@" + place + ":" + System.currentTimeMillis() +" shuts down.");
                        	}
                        	
                            return;
                        }
                        repool(this);
                    }
                }
            }
            if (Report.should_report("activity", 5)) {
        		Report.report(5, Thread.currentThread() +  "@" + place + ":" + System.currentTimeMillis() +" shuts down.");
        	}
        }
        
        public String toString() {
        	return "<PoolRunner " + hashCode() + ">";
        			
        }
    } // end of LocalPlace_c.PoolRunner

    
    static class StartSignal {
        boolean go;
    }    
    
} // end of LocalPlace_c