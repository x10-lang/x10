/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.Random;

/**
 * @author tardieu
 */
final class Worker(pool:Pool) implements ()=>Void {
	// pool this worker belongs to
	
	// bound on loop iterations to help j9 jit
	const BOUND = 100;

	// activity (about to be) executed by this worker 
	private var activity:Activity = null;

	// pending activities
	private val queue = new Deque();

	// random number generator for this worker
	private val random:Random;
	
	def this(pool:Pool, p:Int) {
		property(pool);
		random = new Random(p + (p << 8) + (p << 16) + (p << 24));
	}

	// all methods are local

	// return size of the deque
	public def size():Int = queue.size();

	// return activity executed by this worker
	def activity():Activity = activity;

	// poll activity from the bottom of the deque
	def poll():Activity = queue.poll() as Activity;

	// steal activity from the top of the deque
	def steal():Activity = queue.steal() as Activity;

	// push activity at the bottom of the deque
	def push(activity:Activity):Void {
		queue.push(activity);
	} 

	// run pending activities
	public def apply():Void {
		try {
			while (loop(Runtime.latch, true));
		} catch (t:Throwable) {
			NativeRuntime.println("Uncaught exception in worker thread");
			t.printStackTrace();
		} finally {
			pool.release();
		}
	}
		
	// run activities while waiting on finish 
	def join(latch:Latch):Void {
		val tmp = activity; // save current activity
		while (loop(latch, false));
		activity = tmp; // restore current activity
	}

	// inner loop to help j9 jit
	def loop(latch:Latch, block:Boolean):Boolean {
		for (var i:Int = 0; i < BOUND; i++) {
			if (latch.get()) return false;
			activity = poll();
			if (activity == null) {
				activity = pool.scan(random, latch, block);
				if (activity == null) return false;
			}
			NativeRuntime.runAtLocal(activity.location.id, ()=>activity.run());
		}
		return true;
	}
}
