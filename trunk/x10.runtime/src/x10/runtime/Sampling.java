/*
 * Created on Nov 3, 2004
 */
package x10.runtime;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import x10.lang.Runtime;

import com.ibm.PERCS.PPEM.traceFormat.PEM;
import com.ibm.PERCS.PPEM.traceFormat.Events.X10;

/**
 * Class that performs sampling in the background and 
 * that gathers various run-time statistics. The
 * sampling thread is NOT an X10 activity!
 * 
 * The sampling class is also responsible for periodically
 * dumping data during runtime (if desired).
 * 
 * @author Christian Grothoff
 */
public final class Sampling extends Thread {

    /**
     * The Sampling thread.  There can only be one.
     */
    public static Sampling SINGLETON;
    
    /**
     * Activate sampling.  Called by the Runtime during the
     * initialization process.
     */
    static synchronized void boot(Runtime rt, Activity boot) { 
        assert (SINGLETON == null);
        SINGLETON = new Sampling(rt, boot);
        SINGLETON.setDaemon(true);
        if (Report.should_report("activity", 3)) {
    		Report.report(3, Thread.currentThread() + " starting Sampling Thread " + SINGLETON);
    	}
        SINGLETON.start();
    }

    /**
     * List of all places.
     */
    private final Place[] places;
    
    final int[] activityCounts;
    
    final Map activityToIdentifier = new WeakHashMap();
    
    final DefaultRuntime_c dr = (DefaultRuntime_c)x10.lang.Runtime.runtime;

    /**
     * Shutdown gathering process.
     */
    static synchronized void shutdown() {
        assert (SINGLETON != null);
        synchronized(SINGLETON) {
            SINGLETON.shutdown = true;
            SINGLETON.interrupt();
        }
        try {   
            SINGLETON.join(); 
        } catch (InterruptedException ie) { 
            throw new Error(ie);
        }       
        SINGLETON = null;
    }
    
    /**
     * Entry types (entry-exit, marker or status).
     * ET for 'total' values and 'ED' for delta-values.
     * EX is for events that happen right at the time
     * of the recording (not at the sample time).
     */
    public static final int SAMPLER = X10.Sampler;
    public static final int EVENT = X10.Event;
    
    /**
     * List of 'marker' IDs.
     */
    public static final int EVENT_ID_CLOCK_ADVANCE = X10.Event_ClockAdvance;   // clock advances
    public static final int EVENT_ID_ACTIVITY_START = X10.Event_ActivityStart;  
    public static final int EVENT_ID_ACTIVITY_END = X10.Event_ActivityEnd;  
    public static final int EVENT_ID_ACTIVITY_BLOCK = X10.Event_ActivityBlock;  
    public static final int EVENT_ID_ACTIVITY_UNBLOCK = X10.Event_ActivityUnblock;
    // events that are not (yet) signaled explicitly
    public static final int EVENT_ID_ATOMIC_ENTRY = 1001;
    public static final int EVENT_ID_ATOMIC_EXIT = 1002;
    
    public static final int SAMPLER_DATA  = X10.Sampler_Data;

    /**
     * List of causes for blocking.
     */
    public static final int CAUSE_ATOMIC = 0;
    public static final int CAUSE_FORCE = 1;
    public static final int CAUSE_CLOCK = 2;
   
    private final int[] atomicEntry;
    private final int[] atomicExit;
    private final int[] blockEntry;
    private final int[] blockExit;
    private final int[] activityStart, localActivityStart, remoteActivityStart;
    private final int[] activityEnd;
    
    /**
     * Map of Sampler.class objects to the values returned by the
     * corresponding Sampler.activate() methods.
     */
    private final HashMap samples_ = new HashMap(); // <Class,Sampler>
    
    private boolean shutdown;

    private final DataOutputStream dos_;

    /**
     * Create the sampler.
     */
    private Sampling(Runtime rt, Activity boot) {
        // avoid cyclic initialization dependency
        // DefaultRuntime_c rt = (DefaultRuntime_c) x10.lang.Runtime.runtime;
        this.places  = rt.getPlaces();
        this.activityCounts = new int[places.length + 1];
        
        // do not generate PE-file if PE-file == null, "", or there are more than 8 places.
        if (Configuration.PE_FILE != null && Configuration.PE_FILE.length() > 0) {
            if (places.length > 8) {
                System.err.println("x10.runtime.Sampling: no PE-trace file support for more than 8 places.");
                dos_ = null;
            } else {
                try {
                    dos_ = new DataOutputStream
                    (new FileOutputStream(Configuration.PE_FILE));
                    dos_.writeByte(PEM.BIG_ENDIAN); 
                    dos_.writeByte(0);
                    dos_.writeByte(0);
                    dos_.writeByte(0);
                    dos_.writeInt(PEM.PEM_TRACE_VERSION); // version
                    dos_.writeInt((8+8+8+8+8+8)/8); // size
                    dos_.writeInt(0);
                    dos_.writeLong(0x4000000000000000L); // 'infinity'
                    dos_.writeLong(1); // ticks per second
                    dos_.writeLong(0); // physical processor
                    dos_.writeLong(System.currentTimeMillis()); // initial timestamp = system time
                } catch (IOException io) {
                    System.err.println(io);
                    io.printStackTrace();
                    throw new Error(io);
                }
            }
        } else 
           dos_ = null;
        
        this.atomicEntry = new int[places.length];
        this.atomicExit = new int[places.length];
        this.blockEntry = new int[places.length];
        this.blockExit = new int[places.length];
        this.activityStart = new int[places.length];
        this.localActivityStart = new int[places.length];
        this.remoteActivityStart = new int[places.length];
        this.activityEnd = new int[places.length];
        
        Class[] inners = this.getClass().getDeclaredClasses();
        for (int i=0;i<inners.length;i++) {
            Class c = inners[i];
            try {
                Object coll = c.newInstance();
                if (coll instanceof Sampler) {
                    Sampler s = (Sampler) coll;
                    if (s.isActive())
                        install(s);
                }
                if (coll instanceof Collector) {
                    Collector s = (Collector) coll;                
                    if (s.isActive())
                        s.activate((DefaultRuntime_c)rt, boot);
                }
            } catch (ClassCastException cce) {
                /* ok, not a Sampler */
            } catch (IllegalAccessException iae) {
                /* oh, well, can happen, ignore! */
            } catch (InstantiationException ie) {
                /* can happen, ignore! */
            }
        }
        
        
    }

    private synchronized void freshActivity(Activity a) {
        DefaultRuntime_c dr = (DefaultRuntime_c) Runtime.runtime;
        Place p = dr.getPlaceOfActivity(a);
        if (p == null) {
            //System.out.println("*New AID for " + a + " is " + activityCounts[places.length]);
            activityToIdentifier.put(a, new Integer(activityCounts[places.length]++));
        } else {
            //System.out.println("New AID for " + a + " is " + activityCounts[p.id]);
            activityToIdentifier.put(a, new Integer(activityCounts[p.id]++));
        }
    }
    
    synchronized int getActivityId(Activity a) {
        Object ret = activityToIdentifier.get(a);
        if (ret == null) {
            freshActivity(a);
            ret = activityToIdentifier.get(a);
        }
        return ((Integer) ret).intValue();
    }
    
    public static String intArrayToString(int a[]) {
    	if (a == null) return "NULL";
    	StringBuffer sb = new StringBuffer("[");
    	for (int i = 0; i < a.length; i++) {
    		sb.append(a[i]);
    		if (i == a.length - 1)
    			break;
    		sb.append(", ");
    	}
    	sb.append("]\n");
    	return sb.toString();
    	
    }
    
    /**
	 * Sample output from Sampling.toString() at the end of the execution of a
	 * single-threaded program: 
	 * 
	 * activityStart[0:MAX_PLACES-1] = [1, 0, 0, 0]
	 * localActivityStart[0:MAX_PLACES-1] = [1, 0, 0, 0]
	 * remoteActivityStart[0:MAX_PLACES-1] = [0, 0, 0, 0]
	 * atomicEntry[0:MAX_PLACES-1] = [0, 0, 0, 0] 
	 * blockEntry[0:MAX_PLACES-1] = [1, 0, 0, 0] 
	 * 
	 * These events arise from X10 initialization and should be
	 * subtracted when looking at application-level statistics
	 */

	public String toString() {
		return 
		"\n**** START OF X10 EXECUTION STATISTICS ****\n"
		+ "activityStart[0:MAX_PLACES-1] = " + intArrayToString(activityStart)
		+ "localActivityStart[0:MAX_PLACES-1] = " + intArrayToString(localActivityStart)
		+ "remoteActivityStart[0:MAX_PLACES-1] = " + intArrayToString(remoteActivityStart)
		// + "activityEnd = " + intArrayToString(activityEnd) +
		+ "atomicEntry[0:MAX_PLACES-1] = " + intArrayToString(atomicEntry)
		+ "atomicExit[0:MAX_PLACES-1] = " + intArrayToString(atomicExit)
		// + "blockEntry[0:MAX_PLACES-1] = " + intArrayToString(blockEntry) 
		// + "blockExit = " + intArrayToString(blockExit)
		+ "**** END OF X10 EXECUTION STATISTICS ****\n"
		;
	}

    public void signalEvent(int event_id) {
        signalEvent(null, 
                          Runtime.getCurrentActivity(),
                          event_id, 0,
                          0, 
                          0);        
    }

    public void signalEvent(int event_id,
            int event_info) {
        signalEvent(null, event_id, event_info); 
    }

    public void signalEvent(Activity related,
            int event_id,
            int event_info) {
        signalEvent(related, 
                Runtime.getCurrentActivity(),
                event_id, event_info, 
                0, 
                0);        
    }

    public void signalEvent(int event_id,
            int cause,
            int causeInfo) {
        signalEvent(null, event_id, cause, causeInfo);
    }

    
    public void signalEvent(Activity related,
                                          int event_id,
                                          int cause,
                                          int causeInfo) {
        signalEvent(related, 
                          Runtime.getCurrentActivity(),
                          event_id, 0, 
                          cause, 
                          causeInfo);        
    }
    
    private void writeHeader(int size, int type, int id) {
        if (size + 8 + 16 >= 8 * 8 * 8)
            throw new Error("XML Event too large, fix PE trace format!");
        assert ((size % 8) == 0); // alignment
        try {
            dos_.writeInt((int) System.currentTimeMillis());
            int larg = (((size+8+16)/8) << 24) | (PEM.Layer.X10 << 20) | (type << 14)| id; 
            dos_.writeInt(larg);
            dos_.writeLong(System.currentTimeMillis());
            dos_.writeLong(LocalPlace_c.systemNow());
        } catch (IOException io) {
            throw new Error(io);
        }
    }
    
    public synchronized void signalEvent(Activity ia,
    		Activity a, 
			int event_id,
			int event_info,
			int cause,
			int causeInfo) {                                                                   
    	Place srcPlace = (ia == null) ? null : dr.getPlaceOfActivity(ia);
    	Place dstPlace = (a == null) ? null : dr.getPlaceOfActivity(a);
    	//System.out.println("SRCP " + srcPlace + " of act " + ia);
    	//System.out.println("DSTP " + dstPlace + " of act " + a);
        try {
            int i = dstPlace == null ? -1 : dstPlace.id;
            int j = srcPlace == null ? -1 : srcPlace.id;
            switch (event_id) {
                case EVENT_ID_CLOCK_ADVANCE:
                    if (dos_ != null) {
                        writeHeader(8, EVENT, event_id);
                        dos_.writeInt(i);
                        dos_.writeInt(event_info); // == clockId
                    }
                    break;
                case EVENT_ID_ACTIVITY_START:                       
                    if (i != -1) {
                        activityStart[i]++;
                        // (VIVEK) Also update localActivityStart[] and remoteActivityStart[]
                        if (i == j) localActivityStart[i]++;
                        else remoteActivityStart[i]++;
                    }
                    if (dos_ != null) {
                        writeHeader(4+4+4+4+4+4, EVENT, event_id);                   
                        
                        dos_.writeInt(getActivityId(ia));
                        dos_.writeInt(j); // src place
                        dos_.writeInt(getActivityId(a));
                        dos_.writeInt(i); // dst place
                        if (ia == a)
                            throw new Error("Activities match!?");
                        if ( (getActivityId(ia) == getActivityId(a)) && (i == j))
                            throw new Error("Activity ids match!?: " + 
                                    getActivityId(ia) + "==" + getActivityId(a));
                        if (dstPlace != null) {
                            //System.out.println("START LOAD("+dstPlace+"): " + ((LocalPlace_c)dstPlace).runningThreads);
                            dos_.writeInt(((LocalPlace_c)dstPlace).runningThreads);
                        } else
                            dos_.writeInt(-1);
                        dos_.writeInt(event_info);
                    }
                    break;
                case EVENT_ID_ACTIVITY_END:
                    if (i != -1)
                        activityEnd[i]++;
                    if (dos_ != null) {
                        writeHeader(4+4+4+4, EVENT, event_id);
                        
                        dos_.writeInt(getActivityId(a)); 
                        dos_.writeInt(i); // dst place
                        if (dstPlace != null) {
                            //System.out.println("END LOAD("+dstPlace+"): " + (((LocalPlace_c)dstPlace).runningThreads-1));
                            dos_.writeInt(((LocalPlace_c)dstPlace).runningThreads-1);
                        } else
                            dos_.writeInt(-1);
                        dos_.writeInt(event_info);
                    }
                    break;
                case EVENT_ID_ACTIVITY_BLOCK:
                    if (i != -1)
                        blockEntry[i]++;
                    if (dos_ != null) {
                        writeHeader(4+4+4+4+4+4, EVENT, event_id);
                        
                        dos_.writeInt(getActivityId(a));
                        dos_.writeInt(i); // dst place
                        if (dstPlace != null) {
                            //System.out.println("BLOCK LOAD("+dstPlace+"): " + ((LocalPlace_c)dstPlace).runningThreads);
                            dos_.writeInt(((LocalPlace_c)dstPlace).runningThreads);
                        } else
                            dos_.writeInt(-1);
                        dos_.writeInt(cause);
                        dos_.writeInt(causeInfo);
                        if (ia == null)
                            dos_.writeInt(0);
                        else
                            dos_.writeInt(getActivityId(ia)); // causeInfoExtra
                    }
                    break;
                case EVENT_ID_ACTIVITY_UNBLOCK:
                    if (i != -1)
                        blockExit[i]++;
                    if (dos_ != null) {
                        writeHeader(4+4+4+4+4+4, EVENT, event_id);
                        
                        dos_.writeInt(getActivityId(a));
                        dos_.writeInt(i); // dst place
                        if (dstPlace != null) {
                            //System.out.println("UNBLOCK LOAD("+dstPlace+"): " + ((LocalPlace_c)dstPlace).runningThreads);
                            dos_.writeInt(((LocalPlace_c)dstPlace).runningThreads);
                        } else
                            dos_.writeInt(-1);
                        dos_.writeInt(cause);
                        dos_.writeInt(causeInfo);
                        if (ia == null)
                            dos_.writeInt(0);
                        else
                            dos_.writeInt(getActivityId(ia)); // causeInfo2
                    }
                    break;
                case EVENT_ID_ATOMIC_ENTRY:
                    if (i != -1)
                        atomicEntry[i]++;
                    // no support to write this event in the trace file yet
                    break;
                case EVENT_ID_ATOMIC_EXIT:
                    if (i != -1)
                        atomicExit[i]++;
                    // no support to write this event in the trace file yet
                    break;
            }        
        } catch (IOException io) {
                throw new Error(io);
        }         
    }
    
    private void delta(int[] now, int[] last) {
        for (int j=now.length-1;j>=0;j--)
            last[j] = now [j] - last[j];
    }
    
    private void copy(int[] now, int[] last) {
        for (int j=now.length-1;j>=0;j--)
            last[j] = now [j];
    }

    /**
     * Periodically dump the statistics out.
     */
    public synchronized void run() {
        long now = System.currentTimeMillis();
        int[] lastAtomicEntryCount = new int[atomicEntry.length];
        int[] lastAtomicExitCount = new int[atomicExit.length];
        int[] lastActivityStartCount = new int[activityStart.length];
        int[] lastActivityEndCount = new int[activityEnd.length];
        int[] lastBlockEntryCount = new int[blockEntry.length];
        int[] lastBlockExitCount = new int[blockExit.length];
        while (! shutdown) {
            long last = now;
            now = System.currentTimeMillis();
            long delta = now - last;
            if (delta == 0)
                delta = 1; /* make sure we can safely divide by delta */                      
            Iterator it = samples_.keySet().iterator();
            while (it.hasNext())
                ((Sampler)(samples_.get(it.next()))).sample(delta);

            // produce delta-values
            delta(atomicEntry, lastAtomicEntryCount);
            delta(atomicExit, lastAtomicExitCount);
            delta(activityStart, lastActivityStartCount);
            delta(activityEnd, lastActivityEndCount);
            delta(blockEntry, lastBlockEntryCount);
            delta(blockExit, lastBlockExitCount);

            // generate event
            if (dos_ != null) {
                try {
                    writeHeader(4+4 + places.length * 8 * 7, SAMPLER, SAMPLER_DATA);
                    dos_.writeInt(places.length);
                    dos_.writeInt((int)delta);
                    for (int i=0;i<places.length;i++)
                        if (this.threadQueueSize != null)
                            dos_.writeInt(this.threadQueueSize[i]);
                        else
                            dos_.writeInt(0);
                    for (int i=0;i<places.length;i++)
                        if (this.loadSamples != null)
                            dos_.writeInt(this.loadSamples[i]);
                        else
                            dos_.writeInt(0);
                    
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(this.atomicEntry[i]);
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(this.atomicExit[i]);
                    
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(this.blockEntry[i]);
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(this.blockExit[i]);
                    
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(this.activityStart[i]);
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(this.activityEnd[i]);
                    
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(lastAtomicEntryCount[i]);
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(lastAtomicExitCount[i]);
                    
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(lastActivityStartCount[i]);
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(lastActivityEndCount[i]);
                    
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(lastBlockEntryCount[i]);
                    for (int i=0;i<places.length;i++) 
                        dos_.writeInt(lastBlockExitCount[i]);
                    
                } catch (IOException io) {
                    throw new Error(io);
                }
            }
            
            // copy values for next round
            copy(atomicEntry, lastAtomicEntryCount);
            copy(atomicExit, lastAtomicExitCount);
            copy(activityStart, lastActivityStartCount);
            copy(activityEnd, lastActivityEndCount);
            copy(blockEntry, lastBlockEntryCount);
            copy(blockExit, lastBlockExitCount);

            try {
                this.wait(Configuration.SAMPLING_FREQUENCY_MS);
            } catch (InterruptedException ie) { }              
        }
    }

    /**
     * Install the given statistics Sampler.
     * 
     * @param c
     */
    public void install(Sampler c) {
        samples_.put(c.getClass(), c);
    }

    /**
     * Base-class for all sampling collectors.  A Collector is a helper
     * class that collects some data from the Runtime.  For example,
     * the Runtime may have callbacks into which the Sampling
     * code needs to hook into.  Collectors are used to implement
     * those callbacks.  Not all Sampling requires collectors.  If the
     * runtime can be instrumented directly to call the signal* functions,
     * Collectors are not required.<p> 
     * 
     * Collectors are used primarily to monitor activity starts and
     * exits since for those callback hooks are already present (for
     * Clocks).  Other Samples can be obtained using hooks in 
     * specially crafted xcd files.
     * 
     * @author Christian Grothoff
     */
    public static abstract class Collector {
        protected Collector() {            
        }
        
        /**
         * Is this plugin active?  Checks with the configuration if this
         * plugin was disabled by the user.
         * @return true if the plugin is enabled
         */
        public boolean isActive() {
            if (Configuration.STATISTICS_DISABLE.equals("all"))
                return false;
            if (Configuration.STATISTICS_DISABLE.equals("none"))
                return true;
            return -1 == Configuration.STATISTICS_DISABLE.lastIndexOf(this.getClass().getName().replaceAll("$", "."));
        }
        /**
         * Return an object that describes the results collected by
         * this collector.  A typical implementation just returns 'this'.
         * @return an Object of which the toString() method prints useful data about 
         *  the system, or null if not available
         */
        public abstract void activate(DefaultRuntime_c rt, Activity  boot);
        
    } // end of Sampling.Collector

    /**
     * Class that keeps track of how many activities are currently running in the system
     * and how many have been spawned overall.
     * 
     * @author Christian Grothoff
     */
    public static class ActivityCounter extends Collector {
        public void activate(final DefaultRuntime_c dr,
                Activity  boot) {
            boot.registerActivitySpawnListener(
             new ActivitySpawnListener() {
                public void notifyActivitySpawn(Activity a,
                                                Activity i) {
                    assert a != i;
                    if (SINGLETON != null)
                        SINGLETON.signalEvent(i,
                                a,
                                Sampling.EVENT_ID_ACTIVITY_START,
                                0,
                                0,
                                0);
                    a.registerActivitySpawnListener(this);
                }
                public void notifyActivityTerminated(Activity a) {
                    if (SINGLETON != null)
                        SINGLETON.signalEvent(null,
                                a,
                                Sampling.EVENT_ID_ACTIVITY_END,
                                0,
                                0,
                                0);
                }                
            });
        }
    } // end of Sampling.ActivityCounter
    

    /**
     * Class that keeps track of how many activities are created in a 
     * remote place.  This is a measure of how much remote communication
     * there is.
     * 
     * @author Christian Grothoff
     */
    public static class InterPlaceCommunicationCounter extends Collector {
    	public void activate(final DefaultRuntime_c dr, Activity  boot) {
    		boot.registerActivitySpawnListener(
    				new ActivitySpawnListener() {
    					public void notifyActivitySpawn(Activity a, Activity i) {
    						assert a != i;
    						a.registerActivitySpawnListener(this);
    					}
    					public void notifyActivityTerminated(Activity a) {
    						// we don't care.
    					}                
    				}
    		);
    		  
    	}
    } // end of Sampling.ActivityCounter
    
    /**
     * Base-class for all Sampling plugins.  A Sampling plugin
     * is useful if some code should be run periodically just after
     * a sampling interval is over and a snapshot of the values
     * will be taken.  Samplers typically update values that
     * would be too costly to keep up-to-date all the time.
     * 
     * @author Christian Grothoff
     */
    public static abstract class Sampler {
        protected Sampler() {            
        }        
        /**
         * Is this plugin active?  Checks with the configuration if this
         * plugin was disabled by the user.
         * @return true if the plugin is enabled
         */
        public boolean isActive() {
            if (Configuration.STATISTICS_DISABLE.equals("all"))
                return false;
            if (Configuration.STATISTICS_DISABLE.equals("none"))
                return true;
            return -1 == Configuration.STATISTICS_DISABLE.lastIndexOf(this.getClass().getName().replaceAll("$", "."));
        }
        /**
         * Sample.  The time difference is delta ms. 
         */
        public abstract void sample(long delta);
        
    } // end of Sampling.Sampler

    int[] threadQueueSize;
    
    /**
     * Class that keeps track of how many threads are waiting
     * in the thread pool (unassigned to activities).
     * 
     * @author Christian Grothoff
     */
    public static class ThreadQueueSampler extends Sampler {
        public void sample(long delta) {
            if (SINGLETON == null)
               return;
            if (SINGLETON.threadQueueSize == null)
                SINGLETON.threadQueueSize = new int[SINGLETON.places.length];
            for (int i=SINGLETON.places.length-1;i>=0;i--) {
                LocalPlace_c p = (LocalPlace_c) SINGLETON.places[i];
                int ql = 0;
                // cvp: this synchronization leads to deadlock.
                // we accept the risk to sample a stale/incorrect value due to a 
                // data race.
                //synchronized(p) {
                    LocalPlace_c.PoolRunner head = p.threadQueue_;
                    while (head != null) {
                        head = head.next;
                        ql++;
                    }
                //}
                SINGLETON.threadQueueSize[i] = ql;
            }
        }
    }
    
    int[] loadSamples;

    /**
     * Class that keeps track of how high the load at a given
     * place is.
     * 
     * @author Christian Grothoff
     */
    public static class LoadSampler extends Sampler {

        public void sample(long delta) {
            if (SINGLETON == null)
                return;
            if (SINGLETON.loadSamples == null)
                SINGLETON.loadSamples = new int[SINGLETON.places.length];
            for (int i=SINGLETON.places.length-1;i>=0;i--) {
                LocalPlace_c p = (LocalPlace_c) SINGLETON.places[i];
                int ql;
                // cvp: this synchronization is non-sense and leads to deadlock.
                // synchronized(p) {
                    ql = p.runningThreads;
                //}
                SINGLETON.loadSamples[i] = ql;
            }
        }
    }

} // end of Sampling
