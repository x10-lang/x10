/*
 * Created on Nov 7, 2004
 */
package x10.runtime;

/**
 * This interface is implemented by threads that want a notification to be
 * emitted whenever the thread is blocked (on synchronization or wait).
 * This is useful for performance monitoring (to measure the load at a given
 * Place).
 *
 * @author Christian Grothoff
 */
public abstract class LoadMonitored extends Thread {

    public static final boolean ON = true;
    
    /**
     * @param reason why are we blocking (see Sampling class
     *    for possible values)
     * @param info additional info for the blocking (depends on
     *    the reason, i.e. clock ID; 0 otherwise)
     */
    public static void blocked(int reason, int info) {
        if (ON) {
            Sampling.SINGLETON.signalEvent(Sampling.EVENT_ID_ACTIVITY_BLOCK,
                                                                reason,
                                                                info);
            Thread t = Thread.currentThread();
            if (t instanceof LoadMonitored)
                ((LoadMonitored)t).changeRunningStatus(-1);
        }
    }
    
    /**
     * @param reason why are we blocking (see Sampling class
     *    for possible values)
     * @param info additional info for the blocking (depends on
     *    the reason, i.e. clock ID; 0 otherwise)
     */
    public static void unblocked(int reason, int info) {
        if (ON) {
            Sampling.SINGLETON.signalEvent(Sampling.EVENT_ID_ACTIVITY_UNBLOCK,
                    reason,
                    info);
            Thread t = Thread.currentThread();
            if (t instanceof LoadMonitored)
                ((LoadMonitored)t).changeRunningStatus(1);
        }
    }
    
    /**
     * Notify about a change in the 'running' status of a thread.
     * @param delta +1 for thread starts to run (unblocked), -1 for thread is blocked
     */
    public abstract void changeRunningStatus(int delta);

}
