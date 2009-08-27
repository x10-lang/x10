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
    def await():Void {
    	Runtime.increaseParallelism();
    	val thread = Thread.currentThread();
    	threads.push(thread);
    	while (threads.contains(thread)) {
	   		unlock();
   			Thread.park();
   			lock();
		}
    }

 	/**
	 * Unpark every thread
     * Decrement blocked thread count
	 * Release the lock
	 * Must be called while holding the lock
	 */
    def release():Void {
    	val size = threads.size();
    	if (size > 0) {
	    	Runtime.decreaseParallelism(size);
	    	for (var i:Int = 0; i<size; i++) Thread.unpark(threads.pop());
	    }
    	unlock();
    }

	/**
	 * Unlock
	 */
    def unlock():Void {
    	lock.unlock();
    }
}
