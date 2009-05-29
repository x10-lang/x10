/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

import java.util.concurrent.atomic.AtomicInteger;

import x10.runtime.abstractmetrics.AbstractMetrics;

/**
 * @author Christian Grothoff
 * 
 * @author Raj Barik, Vivek Sarkar 3/6/2006: use X10ThreadPoolExecutor services
 *         in support of JCU implementation. Also keep track of numBlocked =
 *         number of blocked activities in this place. The implementation
 *         ensures that each place's thread pool has at least
 *         numBlocked+INIT_THREADS_PER_PLACE threads.
 */
public abstract class Place implements Comparable, AbstractMetrics {

    /** id property */
    public final/* nat */int id;

    /** id property accessor */
    public int id() {
        return id;
    }

    /**
     * Executor Service which provides the thread pool: starts with # threads =
     * INIT_THREADS_PER_PLACE, and increases the pool as and when the threads
     * block themselves
     */
    protected X10ThreadPoolExecutor threadPoolService;

    /**
     * Number of activities that can possibly wait at a given moment
     */
    protected AtomicInteger nbThreadBlocked = new AtomicInteger(0);

    // X10 activity handling
    public abstract void runAsync(Activity a);

    public abstract void runAsyncInFinish(Activity a);

    // X10 thread pool event
    public abstract int getNbThreadBlocked();

    public abstract void threadBlockedNotification();

    public abstract void threadUnblockedNotification();

    /**
     * We return an Activity.Result here to force the programmer to actually run
     * the future at a place before forcing the result.
     * 
     * The use-case is the following. Suppose we have the x10 code: <code>
     * p = here;
     * Object x = force future(p) { code };
     * </code>
     * The resulting translation to Java would look like this: <code>
     * Place p = Runtime.here(); 
     * Object x = p.runFuture(new Activity.Future() { code }).force();
     * </code>
     * 
     * @param a
     *                reference to the closure that encapsulates the code to run
     * @return the placeholder for the future result.
     */
    public abstract <T> Future<T> runFuture(Future_c.Activity<T> a);

    protected Place(int id) {
        this.id = id;
        startup();
    }

    /**
     * Get threadpool
     */
    public final X10ThreadPoolExecutor getThreadPool() {
        return threadPoolService;
    }

    public final void startup() {
        threadPoolService = new X10ThreadPoolExecutor(Configuration.INIT_THREADS_PER_PLACE);
        customStartup();
    }

    protected void customStartup() {}

    public final void shutdown() {
        customShutdown();
        threadPoolService.shutdownNow();
    }

    /**
     * Shutdown this place, the current X10 runtime will exit.
     */
    public void customShutdown() {}

    /* lexicographical ordering */
    public final int compareTo(java.lang.Object o) {
        assert o instanceof Place;
        Place tmp = (Place) o;

        int res;
        // row major ordering (C conventions)
        if (id < tmp.id)
            res = -1;
        else if (id > tmp.id)
            res = 1;
        else
            res = 0;
        return res;
    }

    public final int hashCode() {
        return id;
    }

    public final boolean equals(Object o) {
        assert o instanceof Place;
        // works because every place has unique id
        return this == o;
    }
    
    public static Place place(int id) {
        return Runtime.place(id);
    }

} // end of Place

