/*
 * Created on Nov 3, 2004
 */
package x10.runtime;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class that performs sampling in the background.  The
 * sampling thread is NOT an X10 activity!
 * 
 * @author Christian Grothoff
 */
public final class Sampling_c extends Thread {

    /**
     * The Sampling thread.  There can only be one.
     */
    private static Sampling_c _;
    
    /**
     * Activate sampling.  Called by the Runtime during the
     * initialization process.
     */
    static synchronized void boot() {
        assert _ == null; 
        _ = new Sampling_c(); 
    }
    
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
        _ = null; 
     }
    
    private boolean shutdown;
    
    /**
     * Create the sampler.
     */
    private Sampling_c() {
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
            try {
                this.wait(Configuration.SAMPLING_FREQUENCY_MS);
            } catch (InterruptedException ie) {               
            }
        }
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
        // for now, just the totals:
        buf.append(Statistics_c._.toString());
        //  TODO: add diff stats here (deltas!)
    }

    
} // end of Sampling_c
