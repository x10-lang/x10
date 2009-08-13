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
 * Lock with wait/notify capability implemented using park/unpark
 * @author tardieu
 */
class Monitor {
 	/**
 	 * Set to false to interrupt listener thread
 	 */
   	private var park:Boolean;

 	/**
 	 * Instance lock
 	 */
 	private val lock = new Lock();
 	
 	/**
 	 * Parked threads
 	 */
 	private val threads = new Stack[Thread]();

	/**
	 * Lock
	 */
    def lock():Void {
    	lock.lock();
    }

    /**
     * Park calling thread
     * Increment blocked thread count
	 * Must be called while holding the lock
	 * Must not be called while holding the lock more than once
	 */
    def park():Void {
    	Runtime.pool.increase();
    	val thread = Thread.currentThread();
    	threads.push(thread);
    	while (threads.contains(thread)) {
	   		if (thread == Runtime.listener) {
	   			park = true;
		   		unlock();
	   			while (park) NativeRuntime.event_probe();
	   			lock();
			} else {
		   		unlock();
	   			Thread.park();
	   			lock();
			}
		}
    }

 	/**
	 * Unpark every thread
     * Decrement blocked thread count
	 * Must be called while holding the lock
	 */
    def unpark():Void {
    	val size = threads.size();
    	if (size > 0) {
	    	Runtime.pool.decrease(size);
	    	park = false;
	    	for (var i:Int = 0; i<size; i++) Thread.unpark(threads.pop());
	    }
    }

	/**
	 * Unlock
	 */
    def unlock():Void {
    	lock.unlock();
    }
}
