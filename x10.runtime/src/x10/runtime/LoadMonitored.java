/*
 * Created on Nov 7, 2004
 */
package x10.runtime;


/**
 * This abstract class is extended by threads that want a notification to be
 * emitted whenever the thread is blocked (on synchronization or wait).
 * This is useful for performance monitoring (to measure the load at a given
 * Place).
 *
 * @author Christian Grothoff
 */
public abstract class LoadMonitored extends Thread {

    /**
     * @param reason why are we blocking (see Sampling class
     *    for possible values)
     * @param info additional info for the blocking (depends on
     *    the reason, i.e. clock ID; 0 otherwise)
     */
    public static void blocked(int reason, int info, Activity related) {
    	Thread t = Thread.currentThread();
    	if (t instanceof LoadMonitored)
    		((LoadMonitored)t).changeRunningStatus(-1);
    	if (Sampling.SINGLETON != null)
    		Sampling.SINGLETON.signalEvent(related,
    				Sampling.EVENT_ID_ACTIVITY_BLOCK,
    				reason,
    				info);        
    }
    
    /**
     * @param reason why are we blocking (see Sampling class
     *    for possible values)
     * @param info additional info for the blocking (depends on
     *    the reason, i.e. clock ID; 0 otherwise)
     */
    public static void unblocked(int reason, int info, Activity related) {
    	Thread t = Thread.currentThread();
    	if (t instanceof LoadMonitored)
    		((LoadMonitored)t).changeRunningStatus(1);
    	if (Sampling.SINGLETON != null)
    		Sampling.SINGLETON.signalEvent(related,
    				Sampling.EVENT_ID_ACTIVITY_UNBLOCK,
    				reason,
    				info);
    }
    
    /**
     * Notify about a change in the 'running' status of a thread.
     * @param delta +1 for thread starts to run (unblocked), -1 for thread is blocked
     */
    public abstract void changeRunningStatus(int delta);

}
