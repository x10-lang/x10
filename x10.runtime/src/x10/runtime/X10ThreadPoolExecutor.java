package x10.runtime;

import java.util.concurrent.*;
import java.util.*;
import java.lang.*;

/**
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: extension of JCU ThreadPoolExecutor to allow for adding threads to the pool based on the
 * number of X10 activities that are blocked.
 */

public class X10ThreadPoolExecutor extends ThreadPoolExecutor {
		

	/** Fix nThreads to 1 for best results */
	public X10ThreadPoolExecutor ( int nThreads ) { 
		super ( 3*nThreads,3*nThreads, 
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue (),
				new X10ThreadFactory ()); 
                prestartAllCoreThreads();
	}
	
	/** Should it be atomic? 
	 *  Call this method when you are blocked on finish, force, next*/
	
	public synchronized void addPool () {
		int poolSize=getCorePoolSize();
	
		setCorePoolSize ( poolSize + 1 );
		setMaximumPoolSize ( getMaximumPoolSize () + 1);
		
	}


	/**
	 * Call this method when you are out of the blocked operation
	 */

	public void removePool () {
		setCorePoolSize ( getCorePoolSize () - 1 );
		setMaximumPoolSize ( getMaximumPoolSize () - 1);
	}
	
	
	/**
	 * Call this method to check the size of the queue
	 */
	public int queueSize() {
		return getQueue().size();
	}
	
	/**
	 * Add to threads if queue is non-empty
	 * successful addition returns +1 else -1
	 */
	public int addPoolIfQueuePositive() {
		if(queueSize() > 0) {
			addPool();
			return 1;
		}
		return -1;
	}
	
	/** TODO */
	public void beforeExecute (Thread t, Runnable r) {
		
	}
	
	/** TODO */
	public void afterExecute (Thread t, Runnable r) {
		
	}

}
