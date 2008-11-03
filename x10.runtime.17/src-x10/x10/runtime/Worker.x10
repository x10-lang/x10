/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.runtime.kernel.Runnable;
import x10.runtime.kernel.Thread;

/**
 * Worker thread in thread pool
 * @author tardieu
 */
value Worker(pool: Pool) implements Runnable {
	def this(pool: Pool) = property(pool);

	/**
	 * Main loop
	 */
	public def run(): void {
		val thread = Thread.currentThread();
		
		// no need for termination condition
		// termination of main activity governs program termination
		while (true) {
		
		 	// request new job from pool
			val job = pool.job(thread);
			
			// attach thread to place and activity
			thread.place(job.place);
			thread.activity(job.activity);
			
			// run activity
			job.activity.run();
		}
	}
}
