package x10.runtime;

import x10.lang.Activity;

/**
 * This interface is internal to x10.runtime.  It is used
 * to register the threads from the threadpool of the local
 * place.  This registry is then used to later map back the
 * current activity to its place (which is needed if we simulate
 * multiple places on one machine or have two places within the
 * same JVM for SMP machines).
 * 
 * It is also used to signal the beginning and end of activities.
 * 
 * @author Christian Grothoff
 */
public interface ThreadRegistry {

    /**
     * Register the fact that the given thread runs at
     * the given place.
     * 
     * @param t
     * @param p
     */
    void registerThread(Thread t, 
			Place p);

    /**
     * Notifiation that an activity was started.
     * 
     * @param t the thread that runs the activity
     * @param a the activity that is being run
     * @param i the activity that started a (null for boot/main).
     */
    void registerActivityStart(Thread t,
                               Activity a,
                               Activity i);

    /**
     * Notifiation that an activity terminated with an
     * exception.
     * 
     * @param a the activity that died
     * @param i the Error or RuntimeException encountered
     */
    void registerActivityException(Activity a,
                                                  Throwable t);

    /**
     * Notification that an activity completed.
     * 
     * @param t
     * @param a
     */
    void registerActivityStop(Thread t,
                              Activity a);
    
} // end of ThreadRegistry