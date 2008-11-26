/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

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
 	private val stack = new StackThread();

	/**
	 * Lock
	 */
    def lock(): void {
    	lock.lock();
    }

    /**
     * Park calling thread
     * Does not increment blocked thread count
	 * Must be called while holding the lock
	 * Must not be called while holding the lock more than once
	 */
    def park(): void {
    	val thread = Thread.currentThread();
    	stack.push(thread);
    	while (stack.search(thread) != -1) {
       		unlock();
    		Thread.park();
       		lock();
		}
    }

    /**
     * Park calling thread
     * Increment blocked thread count
	 * Must be called while holding the lock
	 * Must not be called while holding the lock more than once
	 */
    def await(): void {
		// notify runtime thread is about to block
    	Runtime.threadBlockedNotification();

		park();
		
		// notify runtime thread is running again
    	Runtime.threadUnblockedNotification();
    }

	/**
	 * Unpark one thread
	 * Must be called while holding the lock
	 */
    def unpark(): void {
    	if (!stack.isEmpty()) Thread.unpark(stack.pop());
    }
    
 	/**
	 * Unpark every thread
	 * Must be called while holding the lock
	 */
    def unparkAll(): void {
    	while (!stack.isEmpty()) Thread.unpark(stack.pop());
    }
    
	/**
	 * Unlock
	 */
    def unlock(): void {
    	lock.unlock();
    }
}
