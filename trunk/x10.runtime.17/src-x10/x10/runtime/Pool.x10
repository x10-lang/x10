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
import x10.util.Stack;

/**
 * @author tardieu
 */
class Pool implements Runnable {
	/**
	 * Instance lock
	 */
	private val monitor = new Monitor();

	/**
	 * Activities not yet assigned to a worker
	 */
	private val activities = new Stack[Activity]();
	
	/**
	 * Pool size
	 */
	private var count:Int;
	
	/** 
	 * Number of blocked activities in the pool
	 */
	private var busy:Int = 0;
	
	/** 
	 * Start count threads
	 */
	def this(count:Int) {
		this.count = count;
		for(var i:Int = 0; i < count; i++) allocate(i);
	}
	
	/**
	 * Submit a new activity to the pool
	 */
	def execute(activity:Activity):Void {
		monitor.lock();
		activities.push(activity);
		
		// wake up available worker if any
		monitor.unpark();

		monitor.unlock();
	}
		
	/**
	 * Start a new thread
	 */
	def allocate(id:Int):Void {
		new Thread(location, this, "thread-" + id.toString()).start();
	}

	/**
	 * Increment number of blocked activities
	 */
	def increase():Void {
		monitor.lock();
		if (++busy >= count) allocate(count++);
		monitor.unlock();
    }

	/**
	 * Decrement number of blocked activities
	 */
	def decrease():Void {
		monitor.lock();
		--busy;
		monitor.unlock();
    }

	/**
	 * Worker body
	 */
	public def run():Void {
		val thread = Thread.currentThread();
		
		// no need for termination condition
		// termination of main activity governs program termination
		while (true) {
			monitor.lock();

			// park worker until one activity can be executed
			while (activities.isEmpty()) monitor.park();

			// pop activity
			val activity = activities.pop();
			monitor.unlock();
		
			// attach thread to activity
			thread.activity(activity);
			
			// run activity
			at (activity.location) activity.run();
		}
	}
}
