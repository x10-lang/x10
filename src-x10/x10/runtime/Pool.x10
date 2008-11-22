/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.Stack;

/**
 * @author tardieu
 */
value Pool {
	/**
	 * Pool lock
	 */
	private val monitor = new Monitor();

	/**
	 * Activities not yet assigned to a worker
	 */
	private val activities = new Stack[Activity]();
	
	/**
	 * counters(0) = pool size, counters(1) = nb of blocked threads
	 * invariant: counters(0) > counters(1)
	 */
	private val counters = Rail.makeVar[Int](2, (nat)=>0);
	
	/** 
	 * Start count threads
	 */
	def this(count:Int) {
		counters(0) = count;
		for(var i:Int = 0; i < count; i++) allocate(i);
	}
	
	/**
	 * Submit a new activity to the pool
	 */
	def execute(activity:Activity):Void {
		NativeRuntime.runAtLocal(activities.location.id, ()=>{ 
			monitor.lock();
			activities.push(activity);
			
			// wake up available worker if any
			monitor.unpark();
	
			monitor.unlock();
		});
	}
		
	/**
	 * Start a new thread
	 */
	private def allocate(id:Int):Void {
		new Thread(()=>this.run(), "thread-" + id.toString()).start();
	}

	/**
	 * Increment number of blocked activities
	 */
	def increase():Void {
		NativeRuntime.runAtLocal(activities.location.id, ()=>{
			monitor.lock();
			if (++counters(1) >= counters(0)) allocate(counters(0)++);
			monitor.unlock();
		});
    }

	/**
	 * Decrement number of blocked activities
	 */
	def decrease():Void {
		NativeRuntime.runAtLocal(activities.location.id, ()=>{
			monitor.lock();
			--counters(1);
			monitor.unlock();
		});
    }

	/**
	 * Worker body
	 */
	private def run():Void {
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
			NativeRuntime.runAtLocal(activity.location.id, ()=>activity.run());
		}
	}
}
