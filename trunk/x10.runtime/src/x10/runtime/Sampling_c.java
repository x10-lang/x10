/*
 * Created on Nov 3, 2004
 */
package x10.runtime;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import x10.lang.Place;


/**
 * Class that performs sampling in the background.  The
 * sampling thread is NOT an X10 activity!  Use sampling if
 * you need to periodically check on some values.  Use
 * Statistics if a callback notifies you of changes to a value.
 * 
 * The sampling class is also responsible for periodically
 * dumping data during runtime (if desired).
 * 
 * @author Christian Grothoff
 */
public final class Sampling_c extends Thread {

    /**
     * The Sampling thread.  There can only be one.
     */
    final static Sampling_c _ = new Sampling_c();
    
    /**
     * Activate sampling.  Called by the Runtime during the
     * initialization process.
     */
    static synchronized void boot() { /* triggers static  initializer */ }
    
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
     * Map of Sampler.class objects to the values returned by the
     * corresponding Sampler.activate() methods.
     */
    private final HashMap samples_ = new HashMap(); // <Class,Sampler>
    
    private boolean shutdown;
    
    /**
     * Create the sampler.
     */
    private Sampling_c() {
        Class[] inners = this.getClass().getDeclaredClasses();
        for (int i=0;i<inners.length;i++) {
            Class c = inners[i];
            try {
                Sampler coll = (Sampler) c.newInstance();                
                if (coll.isActive())
                    install(coll);
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
        
    
    public synchronized void run() {
        StringBuffer buf = new StringBuffer();
        long now = System.currentTimeMillis();  
        while (! shutdown) {
            long last = now;
            now = System.currentTimeMillis();
            long delta = now - last;
            if (delta == 0)
                delta = 1; /* make sure we can safely divide by delta */                      
            
            Iterator it = samples_.keySet().iterator();
            while (it.hasNext())
                ((Sampler)(samples_.get(it.next()))).sample(delta);
            
            
            if (Configuration.SAMPLING_OUTPUT_FILE != null) {            
                buf.setLength(0);
                update(buf, delta);
                String s = buf.toString();
                try {
                    DataOutputStream dos = new DataOutputStream
                    (new FileOutputStream(Configuration.SAMPLING_OUTPUT_FILE));
                    dos.writeChars(s);
                    dos.close();
                } catch (IOException io) {
                    throw new Error(io);
                }
            }
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

    public String toString() {
        return samples_.toString(); // for now, may nicer later...
    }

    
    /**
     * Produce a nice sampling output here.  The time since the last
     * sample is "delta" ms.
     * This function is supposed to look at the statistics and produce
     * a nice output, possibly showing the differences between the current
     * and the previous sampling run.
     *   
     * @param buf the string buffer where to write the output to
     *        (via append)
     * @param delta guaranteed to be > 0.
     */
    private void update(StringBuffer buf, long delta) {
        buf.append(Statistics_c._.toString());
        buf.append(this.toString());
    }

    /**
     * Get the Sampler that corresponds to the given class.
     * @param c the class of the Sampler
     * @return null if no such Sampler is installed, otherwise
     *  the object returned by the Sampler's activate method
     */
    public Sampler getSampler(Class c) {
        return (Sampler) samples_.get(c);
    }
    
    /**
     * Base-class for all statistics plugins.
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
         *  the system
         */
        public abstract void sample(long delta);
        
    } // end of Statistics_c.Sampler


    /**
     * Class that keeps track of how many threads are waiting
     * in the thread pool (unassigned to activities) on average
     * and maximum.
     * 
     * @author Christian Grothoff
     */
    public static class ThreadQueueSampler extends Sampler {

        int[] maxThreadQueue;        
        long[] totThreadQueue;
        int[] threadQueueSamples;

        private final LocalPlace_c[] lp;
        
        ThreadQueueSampler() {
            DefaultRuntime_c rt = (DefaultRuntime_c) x10.lang.Runtime._;
            Place[] places  = rt.getPlaces();
            int locals = 0;
            for (int i=places.length-1;i>=0;i--) 
                if (places[i] instanceof LocalPlace_c) 
                    locals++;
            lp = new LocalPlace_c[locals];
            for (int i=places.length-1;i>=0;i--) 
                if (places[i] instanceof LocalPlace_c) 
                    lp[--locals] = (LocalPlace_c) places[i];
            maxThreadQueue = new int[lp.length];
            totThreadQueue = new long[lp.length];
            threadQueueSamples = new int[lp.length];
        }
        
        /**
         * Sample.  The time difference is delta ms. 
         */
        public void sample(long delta) {
            for (int i=lp.length-1;i>=0;i--) {
                LocalPlace_c p = lp[i];
                int ql = 0;
                synchronized(p) {
                    LocalPlace_c.PoolRunner head = p.threadQueue_;
                    while (head != null) {
                        head = head.next;
                        ql++;
                    }
                }
                if (ql > maxThreadQueue[i])
                    maxThreadQueue[i] = ql;
                totThreadQueue[i] += ql;
                threadQueueSamples[i]++;
            }
        }
      
        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("Thread Pool:\n");
            for (int i=lp.length-1;i>=0;i--) {
                buf.append(i);
                if (this.threadQueueSamples[i] > 0) 
                    buf.append(":\tMAX: " + maxThreadQueue[i] + 
                               "\n  \tAVG: " + (this.totThreadQueue[i]/this.threadQueueSamples[i]) + "\n");
                else                           
                    buf.append(" \tNo samples.\n");
            }
            return buf.toString();
        }
    
    } // end of Statistics_c.ActivityCounter
    
} // end of Sampling_c
