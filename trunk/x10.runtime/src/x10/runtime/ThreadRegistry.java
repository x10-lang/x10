package x10.runtime;

/**
 * This interface is internal to x10.runtime.  It is used
 * to register the threads from the threadpool of the local
 * place.  This registry is then used to later map back the
 * current activity to its place (which is needed if we simulate
 * multiple places on one machine or have two places within the
 * same JVM for SMP machines).
 * 
 * @author Christian Grothoff
 */
interface ThreadRegistry {

    void registerThread(Thread t, 
			Place p);

} // end of ThreadRegistry