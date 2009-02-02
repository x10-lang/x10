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
value Pool(count:Int) {
	// count: number of hardware threads available to the pool 

	// the mutable state of the pool
	private final static class That {
		var size:Int = 0; // number of threads in the thread pool
		var blocked:Int = 0; // number of blocked threads in the thread pool
		var live:Boolean = true; // reset to force threads to return
	}
	private val that = new That();

	// invariant: size >= blocked + count
	// goal: size ~ blocked + count + threads.size()

 	/**
 	 * Pool lock
 	 */
 	private val lock = new Lock();
 	
 	/**
 	 * Parked threads
 	 */
 	private val threads = new Stack[Thread]();

	/**
	 * Pending activities
	 */
	private val activities = new Stack[Activity]();
	

	/** 
	 * Start count threads
	 */
	def this(count:Int) {
		property(count);
		while (that.size < count) allocate(that.size++);
	}
	
	/**
	 * Submit a new activity to the pool
	 */
	def execute(activity:Activity):Void {
		NativeRuntime.runAtLocal(that.location.id, ()=>{
			lock.lock();
			activities.push(activity);
			unpark();
			lock.unlock();
		});
	}
		
	/**
	 * Reclaim the thread pool 
	 */
	def quit():Void {
		lock.lock();
		that.live = false;
    	while (!threads.isEmpty()) Thread.unpark(threads.pop());
    	lock.unlock();
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
//		NativeRuntime.println("INCREASE");
		NativeRuntime.runAtLocal(that.location.id, ()=>{ 
			lock.lock();
			if (that.size < ++that.blocked + count) {
				allocate(that.size++);
			} else {
				unpark();
			}
			lock.unlock();
		});
    }

	/**
	 * Decrement number of blocked activities
	 */
	def decrease(n:Int):Void {
//		NativeRuntime.println("DECREASE " + n.toString());
		NativeRuntime.runAtLocal(that.location.id, ()=>{ 
			lock.lock();
			that.blocked = that.blocked - n;
			assert (that.blocked >= 0);
			lock.unlock();
		});
    }

	private def unpark():Void {
		if (threads.size() + that.blocked + count > that.size) {
//			NativeRuntime.println("UNPARK");
	    	Thread.unpark(threads.pop());
		}
	}

	/**
	 * Worker body
	 */
	private def run():Void {
		val thread = Thread.currentThread();
		lock.lock();
		while (that.live) loop(thread);
		lock.unlock();
//		NativeRuntime.println("DONE");
	}
	
	// must keep the run and loop methods appart
	// must keep the two nested loop with the upper bound on the inner loop
	// to get consistent jit performance on j9

	private def loop(thread:Thread):Void {
		var i:Int = 0;
		while (that.live && i++ < 1000) {
			if (threads.size() + that.blocked + count < that.size || activities.isEmpty()) {
	//			NativeRuntime.println("PARK");
		    	threads.push(thread);
		    	while (threads.search(thread) != -1) {
			   		lock.unlock();
		   			Thread.park();
		   			lock.lock();
				}
			} else {
				val activity = activities.pop();
				lock.unlock();
				// attach thread to activity
				thread.activity(activity);
				// run activity
				NativeRuntime.runAtLocal(activity.location.id, ()=>activity.run());
				lock.lock();
			}
		}
	}
}
