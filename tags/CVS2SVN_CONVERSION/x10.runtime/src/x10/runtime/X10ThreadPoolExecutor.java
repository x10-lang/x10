/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
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
		super ( nThreads,nThreads, 
				1000L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue (),
				new X10ThreadFactory ()); 
                prestartAllCoreThreads();
	}
	
	/** Should it be atomic? 
	 *  Call this method when you are blocked on finish, force, next*/
	
	public synchronized void increasePoolSize () {
		setCorePoolSize ( getCorePoolSize() + 1 );
		setMaximumPoolSize ( getMaximumPoolSize () + 1);
	}


	/**
	 * Call this method when you are out of the blocked operation
	 */

	public synchronized void decreasePoolSize () {
	// TODO this code is likely to crash some non regression test
	//		setCorePoolSize ( getCorePoolSize () - 1 );
	//		setMaximumPoolSize ( getMaximumPoolSize () - 1);
	}
	
	
	/**
	 * Call this method to check the size of the queue
	 */
	public int queueSize() {
		return getQueue().size();
	}
	
	/** TODO */
	public void beforeExecute (Thread t, Runnable r) {
		super.beforeExecute(t,r);
	}
	
	/** TODO */
	public void afterExecute (Runnable r, Throwable t) {
		super.afterExecute(r,t);	
	}
}
