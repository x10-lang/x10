package x10.runtime;

import x10.lang.Activity;
import x10.lang.Future;
import x10.lang.Place;
import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;

/**
 * A LocalPlace_c is an implementation of a place
 * that runs on this Java Virtual Machine.  In the
 * future we will have RemotePlaces that refer to
 * Places on other machines.
 * 
 * <p>
 * 
 * Possible problem: using the current implementation
 * I somehow doubt that the VM will automatically shutdown
 * once there are no more activities (since the threads
 * in the pool will still be live and this class will
 * still be reachable!).  
 * 
 * The "obvious" solution in Java would be to wrap the main method
 * of the X10 app and put a 'shutdown' call into the finally
 * clause -- but that would not be right for X10 since we could
 * have spawned other asyncs that are still running.  Adding a
 * test if there are (globally!) still any asyncs left will be
 * tricky, especially considering that we don't want to do this
 * in a way that might be expensive (i.e. permanently send messages
 * around asking 'are you done yet' while the application is still
 * running sounds like a particularly bad idea). 
 *
 * @author Christian Grothoff
 */
class LocalPlace_c extends PooledExecutor
    implements Place {

    private final ThreadRegistry reg_;
    
    private final ActivityInformationProvider aip_;
    
    LocalPlace_c(final ThreadRegistry reg,
                 final ActivityInformationProvider aip) {
	super(new LinkedQueue());
	this.reg_ = reg;
        this.aip_ = aip;
	this.setThreadFactory(new ThreadFactory() {
		public Thread newThread(final Runnable cmd) {
		    Thread t = new Thread(cmd);
		    reg.registerThread(t, LocalPlace_c.this);
		    return t;
		}
	    });
	this.setMinimumPoolSize(Configuration.PLACE_MINIMAL_THREAD_POOL_SIZE);
	this.setKeepAliveTime(Configuration.PLACE_THREAD_KEEPALIVE_TIME);
    }
    
    /**
     * Shutdown this place, the current X10 runtime will exit.
     * Currently performs a 'soft' shutdown by allowing the
     * thread pool to complete the existing activities.  I'm not
     * sure if that's the semantics that we would want, ideally
     * we would like to assert that there are no activities pending
     * at this point.
     */
    public void shutdown() {
        this.shutdownAfterProcessingCurrentlyQueuedTasks();
    }

    /**
     * Run the given activity asynchronously.
     */
    public void runAsync(final Activity a) {
        final Activity i = aip_.getCurrentActivity();
        final StartSignal startSignal = new StartSignal();
        try {
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
        } catch (InterruptedException ie) {
            throw new Error(ie); // should never happen!
        }    
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
        try {
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
        } catch (InterruptedException ie) {
            throw new Error(ie); // should never happen!
        }
        return result;
    }
    
    static class StartSignal {
        boolean go;
    }    
    
} // end of LocalPlace_c