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

import x10.base.Place;
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
 * TODO:
 * - dump into PEM format
 * - generate 'correct' XML output
 * 
 * @author Christian Grothoff
 */
public final class Sampling extends Thread {

    /**
     * The Sampling thread.  There can only be one.
     */
    public final static Sampling _ = new Sampling();
    
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
        synchronized(_) {
            _.shutdown = true;
            _.interrupt();
        }
        try {   
            _.join(); 
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
    public static final int ET_EE = X10.EESampler;
    public static final int ET_S = X10.Sampler;
    public static final int ED_EE = X10.EDSampler;
    public static final int EX_M = X10.Event;
    
    /**
     * List of entry-exit IDs.
     */
    public static final int EE_ID_ATOMIC = X10.EESampler_Atomic;               // lock->unlock
    public static final int EE_ID_ACTIVITY_RUN = X10.EESampler_ActivityRun;     // start->finish (number of activities started/completed)
    public static final int EE_ID_ACTIVITY_BLOCK = X10.EESampler_Block; // block->unblock (not used, we're using load instead)
    public static final int ED_ID_ATOMIC = X10.EDSampler_Atomic;               // lock->unlock
    public static final int ED_ID_ACTIVITY_RUN = X10.EDSampler_Atomic;     // start->finish (number of activities started/completed)
    public static final int ED_ID_ACTIVITY_BLOCK = X10.EDSampler_Block; // block->unblock (not used, we're using load instead)
    
    /**
     * List of 'marker' IDs (single, unrelated events in time).
     */
    public static final int M_ID_LOCAL_CALL = 0; // 'call'
    public static final int M_ID_REMOTE_CALL = 1; // 'call'
    public static final int M_ID_CLOCK_ADVANCE = 2;   // clock advances

    /**
     * List of status values (values that capture the current
     * state of the system, but that have no clear relation
     * with entry-exit events; for example, memory used
     * or active threads in the thread pool).
     */
    public static final int S_ID_THREAD_QUEUE_SIZE = 0;
    public static final int S_ID_LOAD = 1; // number of threads that are currently running (non-blocked)
   
    private final int[][] entryCount;
    
    private final int[][] exitCount;
    
    private final int[][] eventCount;

    private final int[][] statusValue;
    
    private final HashMap eeNames = new HashMap();
    private final HashMap mNames = new HashMap();
    private final HashMap sNames = new HashMap();
    
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
        DefaultRuntime_c rt = (DefaultRuntime_c) x10.lang.Runtime.getRuntime();
        this.places  = rt.getPlaces();
        try {
            dos = new DataOutputStream
                (new FileOutputStream(Configuration.PE_FILE));
            dos.writeByte(PEM.BIG_ENDIAN); 
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeInt(PEM.PEM_TRACE_VERSION); // version
            dos.writeInt(1+3+4+4+4+8+8+8+8); // size
            dos.writeInt(0);
            dos.writeLong(0x4000000000000000L); // 'infinity'
            dos.writeLong(1); // ticks per second
            dos.writeLong(System.currentTimeMillis()); // initial timestamp = system time
        } catch (IOException io) {
            System.err.println(io);
            io.printStackTrace();
            System.exit(-1);
        }
        Field[] fields = this.getClass().getFields();
        int maxEE = 0;
        int maxM = 0;
        int maxS = 0;
        try {
            for (int i=0;i<fields.length;i++) {
                Field f = fields[i];
                if (f.getName().startsWith("EE_ID") && Modifier.isStatic(f.getModifiers())) {
                    int val = f.getInt(null) + 1;
                    eeNames.put(new Integer(val-1), f.getName());
                    if (val > maxEE)
                        maxEE = val;
                }
                if (f.getName().startsWith("M_ID") && Modifier.isStatic(f.getModifiers())) {
                    int val = f.getInt(null) + 1;
                    mNames.put(new Integer(val-1),f.getName());
                    if (val > maxM)
                        maxM = val;
                }
                if (f.getName().startsWith("S_ID") && Modifier.isStatic(f.getModifiers())) {
                    int val = f.getInt(null) + 1;
                    sNames.put(new Integer(val-1), f.getName());
                    if (val > maxS)
                        maxS = val;
                }
                
            }
        } catch (IllegalAccessException iae) {
            throw new Error(iae);
        }
        this.entryCount = new int[places.length][maxEE];
        this.exitCount = new int[places.length][maxEE];
        this.eventCount = new int[places.length][maxM];
        this.statusValue = new int[places.length][maxS];
        
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
        this.start(); // auto-start!
    }

    public String toString() {
        return samples_.toString(); // for now, may nicer later...
    }

    public void signalEntry(int ee_id) {
        signalEntry(Runtime.here(), ee_id);
    }
    
    public void signalExit(int ee_id) {
        signalExit(Runtime.here(), ee_id);
    }
    
    public void signalEvent(int m_id) {
        signalEvent(Runtime.here(), m_id);        
    }
    
    public synchronized void signalEntry(Place p,
                                                                 int ee_id) {
        for (int i=places.length-1;i>=0;i--)
            if (p == places[i])
                _.entryCount[i][ee_id]++;
    }
    
    public synchronized void signalExit(Place p, int ee_id) {
        for (int i=places.length-1;i>=0;i--)
            if (p == places[i])
                _.exitCount[i][ee_id]++;        
    }
    
    public synchronized void signalEvent(Place p, int m_id) {
        for (int i=places.length-1;i>=0;i--)
            if (p == places[i]) {
                _.eventCount[i][m_id]++;
                recordEvent(m_id, EX_M, i);
            }
    }
    
    public void setStatus(int s_id, int value) {
        setStatus(Runtime.here(), s_id, value);
    }
    
    public void updateStatus(int s_id,
                                            int value) {
        updateStatus(Runtime.here(), s_id, value);
    }
    
    public synchronized void setStatus(Place p, 
                                                              int s_id, 
                                                              int value) {        
        for (int i=places.length-1;i>=0;i--)
            if (p == places[i])
                _.statusValue[i][s_id] = value;
    }
    
    public synchronized void updateStatus(Place p, 
                                                                   int s_id,
                                                                   int value) {
        for (int i=places.length-1;i>=0;i--)
            if (p == places[i])
                _.statusValue[i][s_id] += value;
    }
    
    private void writeHeader(int size, int type, int id) {
        try {
            dos.writeInt((int) System.currentTimeMillis());
            int larg = ((size+8) << 24) | (PEM.Layer.X10 << 20) | (type << 14)| id; 
            dos.writeInt(larg);
        } catch (IOException io) {
            throw new Error(io);
        }
    }
    
    /**
     * Record a single event.
     * @param id the id of the event (i.e. call, clock advance)
     * @param type the event category (marker or status)
     * @param pid the place id (where the event took place)
     */
    private synchronized void recordEvent(int id, int type, int pid) {
        assert (type == EX_M);
        writeHeader(8+4+4, type, id);
        try {
            dos.writeLong(System.currentTimeMillis()); // actual event time
            dos.writeInt(pid); // place id (where)            
            dos.writeInt(0); // for 64-bit alignment
        } catch (IOException io) {
            throw new Error(io);
        }
    }

    /**
     * Record event data for all places (not for entry-exit type events)
     * @param eventData the event data to record
     * @param id which index in event data to record (event id, i.e. remote calls)
     * @param type what is the event category (ET or ED, M or S, but not EE)
     */
    private synchronized void recordEvent(int[][] eventData,
                                          int id, int type) {
        assert (type != ET_EE) && (type != ED_EE);
        writeHeader(8+8+eventData.length*8, type, id);
        try {
            dos.writeLong(System.currentTimeMillis()); // sampling time 
            dos.writeInt(eventData.length);            
            dos.writeInt(0);
            for (int i=0;i<eventData.length;i++) 
                dos.writeInt(i);            
            for (int i=0;i<eventData.length;i++) 
                dos.writeInt(eventData[i][id]);            
        } catch (IOException io) {
            throw new Error(io);
        }
    }
    
    /**
     * Record event data for all places of an entry-exit type of event
     * @param entryData the event data to record
     * @param exitData the event data to record
     * @param id which index in event data to record (event id, i.e. remote calls)
     * @param type what is the event category (ET_EE or ED_EE)
     */
    private void recordEntryExit(int id, int type, 
                                 int[][] entryData,
                                 int[][] exitData) {
        writeHeader(4+4+8+4+entryData.length*8, type, id);
        try {
            dos.writeLong(System.currentTimeMillis());  // sampling time
            dos.writeInt(entryData.length);            
            dos.writeInt(0);
            for (int i=0;i<entryData.length;i++) {
                dos.writeInt(entryData[i][id]);
                dos.writeInt(exitData[i][id]);
            }
        } catch (IOException io) {
            throw new Error(io);
        }
    }

    private void delta(int[][] now, int[][] last) {
        for (int i=now.length-1;i>=0;i--)
            for (int j=now[i].length-1;j>=0;j--)
                last[i][j] = now [i][j] - last[i][j];
    }
    
    private void copy(int[][] now, int[][] last) {
        for (int i=now.length-1;i>=0;i--)
            for (int j=now[i].length-1;j>=0;j--)
                last[i][j] = now [i][j];
    }

    /**
     * Periodically dump the statistics out.
     */
    public synchronized void run() {
        long now = System.currentTimeMillis();
        int[][] lastEntryCount = new int[entryCount.length][entryCount[0].length];
        int[][] lastExitCount = new int[exitCount.length][exitCount[0].length];
        while (! shutdown) {
            long last = now;
            now = System.currentTimeMillis();
            long delta = now - last;
            if (delta == 0)
                delta = 1; /* make sure we can safely divide by delta */                      
            Iterator it = samples_.keySet().iterator();
            while (it.hasNext())
                ((Sampler)(samples_.get(it.next()))).sample(delta);

            for (int i=0;i<entryCount[0].length;i++)
                recordEntryExit(i, ET_EE, entryCount, exitCount);
            for (int i=0;i<statusValue[0].length;i++)
                recordEvent(statusValue, i, ET_S);
            // produce delta-values
            delta(entryCount, lastEntryCount);
            delta(exitCount, lastExitCount);
            // record delta-values
            for (int i=0;i<entryCount[0].length;i++)
                recordEntryExit(i, ED_EE, lastEntryCount, lastExitCount);
            // copy values for next round
            copy(entryCount, lastEntryCount);
            copy(exitCount, lastExitCount);

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
            final DefaultRuntime_c dr = (DefaultRuntime_c)x10.lang.Runtime.getRuntime();
            dr.registerActivitySpawnListener(dr.getCurrentActivity(),
                                             new ActivitySpawnListener() {
                public void notifyActivitySpawn(Activity a,
                                                Activity i) {
                    assert a != i;
                    _.signalEntry(EE_ID_ACTIVITY_RUN);
                    dr.registerActivitySpawnListener(a, this);
                }
                public void notifyActivityTerminated(Activity a) {
                    _.signalExit(EE_ID_ACTIVITY_RUN);
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
            final DefaultRuntime_c dr = (DefaultRuntime_c)x10.lang.Runtime.getRuntime();
            dr.registerActivitySpawnListener(dr.getCurrentActivity(),
                                             new ActivitySpawnListener() {
                public void notifyActivitySpawn(Activity a,
                                                Activity i) {
                    assert a != i;
                    if (dr.getPlaceOfActivity(a) == dr.getPlaceOfActivity(i))
                        _.signalEvent(M_ID_LOCAL_CALL);
                    else
                        _.signalEvent(M_ID_REMOTE_CALL);
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

    /**
     * Class that keeps track of how many threads are waiting
     * in the thread pool (unassigned to activities).
     * 
     * @author Christian Grothoff
     */
    public static class ThreadQueueSampler extends Sampler {
        public void sample(long delta) {
            for (int i=_.places.length-1;i>=0;i--) {
                LocalPlace_c p = (LocalPlace_c) _.places[i];
                int ql = 0;
                synchronized(p) {
                    LocalPlace_c.PoolRunner head = p.threadQueue_;
                    while (head != null) {
                        head = head.next;
                        ql++;
                    }
                }
                _.setStatus(p, S_ID_THREAD_QUEUE_SIZE, ql);
            }
        }
    }
    

    /**
     * Class that keeps track of how high the load at a given
     * place is.
     * 
     * @author Christian Grothoff
     */
    public static class LoadSampler extends Sampler {
        public void sample(long delta) {
            for (int i=_.places.length-1;i>=0;i--) {
                LocalPlace_c p = (LocalPlace_c) _.places[i];
                int ql;
                synchronized(p) {
                    ql = p.runningThreads;
                }
                _.setStatus(p, S_ID_LOAD, ql);
            }
        }
    }

} // end of Sampling
