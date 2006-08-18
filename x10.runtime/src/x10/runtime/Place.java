package x10.runtime;

import java.util.concurrent.atomic.AtomicInteger;

import x10.lang.Future;
import x10.lang.Object;
import x10.lang.Runtime;
import x10.lang.place;
import x10.runtime.abstractmetrics.AbstractMetrics;


/**
 * @author Christian Grothoff
 * 
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: use X10ThreadPoolExecutor services in support of JCU implementation.
 * Also keep track of numBlocked = number of blocked activities in this place.
 * The implementation ensures that each place's thread pool has at least numBlocked+INIT_THREADS_PER_PLACE threads.
 */
public abstract class Place extends place 
implements Comparable, AbstractMetrics {
	
	/**
	 * Executor Service which provides the thread pool: starts 
	 * with # threads = INIT_THREADS_PER_PLACE, and increases the pool as and when the threads 
	 * block themselves 
	 */
	protected X10ThreadPoolExecutor threadPoolService = new X10ThreadPoolExecutor(Configuration.INIT_THREADS_PER_PLACE);
	
	/**
	 * Number of activities that can possibly wait at a given moment
	 */
	protected AtomicInteger nbThreadBlocked= new AtomicInteger(0);
	
	public abstract void runAsync(Activity a);
	public abstract void runBootAsync(Activity a);
	public abstract X10ThreadPoolExecutor getThreadPool();
	public abstract int getNbThreadBlocked();
	public abstract void threadBlockedNotification();
	public abstract void threadUnblockedNotification();
	
	/**
	 * We return an Activity.Result here to force the programmer
	 * to actually run the future at a place before forcing the
	 * result.
	 * 
	 * The use-case is the following.  Suppose we have the x10 code:
	 * <code>
	 * p = here;
	 * Object x = force future(p) { code };
	 * </code>
	 * The resulting translation to Java would look like this:
	 * <code>
	 * Place p = Runtime.here(); 
	 * Object x = p.runFuture(new Activity.Future() { code }).force();
	 * </code>
	 * 
	 * @param a reference to the closure that encapsulates the code to run
	 * @return the placeholder for the future result.
	 */
	public abstract Future runFuture(Activity.Expr a); 
	
	/**
	 * Shutdown this place, the current X10 runtime will exit.
	 */
	public abstract void shutdown();
	
	public static Place[] places() {
		return Runtime.places();
	}
	
	public static place here() {
		return Runtime.here();
	}
    
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


} // end of Place

