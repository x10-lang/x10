package x10.runtime;

/**
 * This interface is internal to x10.runtime.
 */
interface ThreadRegistry {

    void registerThread(Thread t, 
			Place p);

}