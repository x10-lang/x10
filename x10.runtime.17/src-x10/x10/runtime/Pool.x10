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
	// number of hardware threads available to the pool 

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
	 * invariant: size >= block + count
	 * goal: size ~ block + count + threads.size()
	 */
	private val size = Rail.makeVar[Int](1, (nat)=>0);
	private val block = Rail.makeVar[Int](1, (nat)=>0);;

	/**
	 * Reset by pool destructor to stop worker threads.
	 */
	private val go = Rail.makeVar[Boolean](1, (nat)=>true);
	
	/** 
	 * Start count threads
	 */
	def this(count:Int) {
		property(count);
		while (size(0) < count) allocate(size(0)++);
	}
	
	/**
	 * Submit a new activity to the pool
	 */
	def execute(activity:Activity):Void {
		NativeRuntime.runAtLocal(activities.location.id, ()=>{ 
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
		go(0) = false;
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
		NativeRuntime.runAtLocal(activities.location.id, ()=>{ 
			lock.lock();
			if (size(0) < ++block(0) + count) {
				allocate(size(0)++);
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
		NativeRuntime.runAtLocal(activities.location.id, ()=>{ 
			lock.lock();
			block(0) = block(0) - n;
			assert (block(0) >= 0);
			lock.unlock();
		});
    }

	private def unpark():Void {
		if (threads.size() + block(0) + count > size(0)) {
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
		while (go(0)) {
			if (threads.size() + block(0) + count < size(0) || activities.isEmpty()) {
//				NativeRuntime.println("PARK");
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
		lock.unlock();
//		NativeRuntime.println("DONE");
	}


	// debugging code

	def flush():Void {
		val thread = Thread.currentThread();
		val parent = thread.activity();
		while (!activities.isEmpty()) {
			val activity = activities.pop();

			// attach thread to activity
			thread.activity(activity);
			
			// run activity
			NativeRuntime.runAtLocal(activity.location.id, ()=>activity.run());
		}
		thread.activity(parent);
	}
}
