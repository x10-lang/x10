/*
 * Created on Nov 3, 2004
 */
package x10.runtime;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import x10.runtime.Place;

import x10.lang.Activity;
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
    public final static Sampling SINGLETON = new Sampling();
    
    /**
     * Activate sampling.  Called by the Runtime during the
     * initialization process.
     */
    static synchronized void boot() { /* triggers static  initializer */ }

    /**
     * List of all places.
     */
    private final Place[] places;
    
    /**
     * Shutdown gathering process.
     */
    static synchronized void shutdown() {
        synchronized(SINGLETON) {
            SINGLETON.shutdown = true;
            SINGLETON.interrupt();
        }
        try {   
            SINGLETON.join(); 
        } catch (InterruptedException ie) { 
            throw new Error(ie);
        }       
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
    public static final int EVENT_ID_ATOMIC_ENTRY = -1;
    public static final int EVENT_ID_ATOMIC_EXIT = -2;
    
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
    private final int[] activityStart;
    private final int[] activityEnd;
    
    /**
     * Map of Sampler.class objects to the values returned by the
     * corresponding Sampler.activate() methods.
     */
    private final HashMap samples_ = new HashMap(); // <Class,Sampler>
    
    private boolean shutdown;

    private DataOutputStream dos;

    /**
     * Create the sampler.
     */
    private Sampling() {
        DefaultRuntime_c rt = (DefaultRuntime_c) x10.lang.Runtime.runtime;
        this.places  = rt.getPlaces();
        try {
            dos = new DataOutputStream
                (new FileOutputStream(Configuration.PE_FILE));
            dos.writeByte(PEM.BIG_ENDIAN); 
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeInt(PEM.PEM_TRACE_VERSION); // version
            dos.writeInt((8+8+8+8+8+8)/8); // size
            dos.writeInt(0);
            dos.writeLong(0x4000000000000000L); // 'infinity'
            dos.writeLong(1); // ticks per second
            dos.writeLong(System.currentTimeMillis()); // initial timestamp = system time
        } catch (IOException io) {
            System.err.println(io);
            io.printStackTrace();
            throw new Error(io);
        }
        this.atomicEntry = new int[places.length];
        this.atomicExit = new int[places.length];
        this.blockEntry = new int[places.length];
        this.blockExit = new int[places.length];
        this.activityStart = new int[places.length];
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
                        s.activate();
                }
            } catch (ClassCastException cce) {
                /* ok, not a Sampler */
            } catch (IllegalAccessException iae) {
                /* oh, well, can happen, ignore! */
            } catch (InstantiationException ie) {
                /* can happen, ignore! */
            }
        }
        
        // sampling shall be done by a daemon thread - if nothing else 
        // runs but daemon threads, then the JVM shall terminate.
        setDaemon(true);
        
        this.start(); // auto-start!
    }

    public String toString() {
        return samples_.toString(); // for now, may nicer later...
    }

    public void signalEvent(int event_id) {
        signalEvent(null, 
                          Runtime.here(),
                          event_id, 0,
                          0, 
                          0);        
    }

    public void signalEvent(int event_id,
                                         int event_info) {
        signalEvent(null, 
                          Runtime.here(),
                          event_id, event_info, 
                          0, 
                          0);        
    }

    public void signalEvent(int event_id,
                                          int cause,
                                          int causeInfo) {
        signalEvent(null, 
                          Runtime.here(),
                          event_id, 0, 
                          cause, 
                          causeInfo);        
    }
    
    private void writeHeader(int size, int type, int id) {
        //System.err.println("WH(" + size + "," +  type + "," + id + ")");
               
        if (size + 8 + 16 >= 8 * 8 * 8)
            throw new Error("XML Event too large, fix PE trace format!");
        assert ((size % 8) == 0); // alignment
        try {
            dos.writeInt((int) System.currentTimeMillis());
            int larg = (((size+8+16)/8) << 24) | (PEM.Layer.X10 << 20) | (type << 14)| id; 
            dos.writeInt(larg);
            dos.writeLong(System.currentTimeMillis());
            dos.writeLong(LocalPlace_c.systemNow());
        } catch (IOException io) {
            throw new Error(io);
        }
    }
    
    public synchronized void signalEvent(Place srcPlace,
                                                                 Place dstPlace, 
                                                                 int event_id,
                                                                 int event_info,
                                                                 int cause,
                                                                 int causeInfo) {
        try {
            for (int i=places.length-1;i>=0;i--) {
                if (dstPlace != places[i]) 
                    continue;
                int j = -1;
                if (srcPlace != null) {
                    for (int k=places.length-1;k>=0;k--) {
                        if (srcPlace == places[k]) {
                               j=k;                        
                               break;
                        }
                    }
                    if (j == -1)
                        throw new Error("Place " + srcPlace + " not in place list!");
                }
                switch (event_id) {
                case EVENT_ID_CLOCK_ADVANCE:
                    writeHeader(8, EVENT, event_id);
                    dos.writeInt(i);
                    dos.writeInt(event_info); // == clockId
                    break;
                case EVENT_ID_ACTIVITY_START:
                    activityStart[i]++;
                    writeHeader(4+4+4+4, EVENT, event_id);
                    dos.writeInt(j); // src place
                    dos.writeInt(i); // dst place
                    dos.writeInt(((LocalPlace_c)dstPlace).runningThreads);
                    dos.writeInt(event_info);
                    break;
                case EVENT_ID_ACTIVITY_END:
                    activityEnd[i]++;
                    writeHeader(4+4+4+4, EVENT, event_id);
                    dos.writeInt(0); // reserved
                    dos.writeInt(i); // dst place
                    dos.writeInt(((LocalPlace_c)dstPlace).runningThreads);
                    dos.writeInt(event_info);
                    break;
                case EVENT_ID_ACTIVITY_BLOCK:
                    blockEntry[i]++;
                    writeHeader(4+4+4+4, EVENT, event_id);
                    dos.writeInt(i); // dst place
                    dos.writeInt(((LocalPlace_c)dstPlace).runningThreads);
                    dos.writeInt(cause);
                    dos.writeInt(causeInfo);
                    break;
                case EVENT_ID_ACTIVITY_UNBLOCK:
                    blockExit[i]++;
                    writeHeader(4+4+4+4, EVENT, event_id);
                    dos.writeInt(i); // dst place
                    dos.writeInt(((LocalPlace_c)dstPlace).runningThreads);
                    dos.writeInt(cause);
                    dos.writeInt(causeInfo);
                    break;
                }
                return;
            }
            throw new Error("Place " + dstPlace + " not in place list!");
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
            try {
                writeHeader(4+4 + places.length * 8 * 7, SAMPLER, SAMPLER_DATA);
                dos.writeInt(places.length);
                dos.writeInt(0); // reserved
                for (int i=0;i<places.length;i++)
                    if (this.threadQueueSize != null)
                        dos.writeInt(this.threadQueueSize[i]);
                    else
                        dos.writeInt(0);
                for (int i=0;i<places.length;i++)
                    if (this.loadSamples != null)
                        dos.writeInt(this.loadSamples[i]);
                    else
                        dos.writeInt(0);
                
                for (int i=0;i<places.length;i++) 
                    dos.writeInt(this.atomicEntry[i]);
                for (int i=0;i<places.length;i++) 
                    dos.writeInt(this.atomicExit[i]);

                for (int i=0;i<places.length;i++) 
                    dos.writeInt(this.blockEntry[i]);
                for (int i=0;i<places.length;i++) 
                    dos.writeInt(this.blockExit[i]);

                for (int i=0;i<places.length;i++) 
                    dos.writeInt(this.activityStart[i]);
                for (int i=0;i<places.length;i++) 
                    dos.writeInt(this.activityEnd[i]);
                
                for (int i=0;i<places.length;i++) 
                    dos.writeInt(lastAtomicEntryCount[i]);
                for (int i=0;i<places.length;i++) 
                    dos.writeInt(lastAtomicExitCount[i]);

                for (int i=0;i<places.length;i++) 
                    dos.writeInt(lastActivityStartCount[i]);
                for (int i=0;i<places.length;i++) 
                    dos.writeInt(lastActivityEndCount[i]);

                for (int i=0;i<places.length;i++) 
                    dos.writeInt(lastBlockEntryCount[i]);
                for (int i=0;i<places.length;i++) 
                    dos.writeInt(lastBlockExitCount[i]);

            } catch (IOException io) {
                throw new Error(io);
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
            } catch (InterruptedException ie) {               
            }
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
        public abstract void activate();
        
    } // end of Sampling.Collector

    /**
     * Class that keeps track of how many activities are currently running in the system
     * and how many have been spawned overall.
     * 
     * @author Christian Grothoff
     */
    public static class ActivityCounter extends Collector {
        public void activate() {
            final DefaultRuntime_c dr = (DefaultRuntime_c)x10.lang.Runtime.runtime;
            dr.registerActivitySpawnListener(dr.getCurrentActivity(),
                                             new ActivitySpawnListener() {
                public void notifyActivitySpawn(Activity a,
                                                Activity i) {
                    assert a != i;
                    SINGLETON.signalEvent(dr.getPlaceOfActivity(i),
                                        dr.getPlaceOfActivity(a),
                                        Sampling.EVENT_ID_ACTIVITY_START,
                                        0,
                                        0,
                                        0);
                    dr.registerActivitySpawnListener(a, this);
                }
                public void notifyActivityTerminated(Activity a) {
                    SINGLETON.signalEvent(null,
                            dr.getPlaceOfActivity(a),
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
        public void activate() {
            final DefaultRuntime_c dr = (DefaultRuntime_c)x10.lang.Runtime.runtime;
            dr.registerActivitySpawnListener(dr.getCurrentActivity(),
                                             new ActivitySpawnListener() {
                public void notifyActivitySpawn(Activity a,
                                                Activity i) {
                    assert a != i;
                    dr.registerActivitySpawnListener(a, this);
                }
                public void notifyActivityTerminated(Activity a) {
                    // we don't care.
                }                
            });
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
