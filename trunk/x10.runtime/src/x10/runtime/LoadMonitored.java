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
abstract class LoadMonitored extends Thread {

    public static void blocked(Thread t) {
        if (t instanceof LoadMonitored)
            ((LoadMonitored)t).changeRunningStatus(-1);
    }
    
    public static void unblocked(Thread t) {
        if (t instanceof LoadMonitored)
            ((LoadMonitored)t).changeRunningStatus(1);
    }
    
    /**
     * Notify about a change in the 'running' status of a thread.
     * @param delta +1 for thread starts to run (unblocked), -1 for thread is blocked
     */
    public abstract void changeRunningStatus(int delta);

}
