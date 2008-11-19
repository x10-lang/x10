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
 * Latch with ++ and -- capabilities
 * @author tardieu
 */
class ModCountDownLatch {
 	/**
 	 * Instance monitor
 	 */
	private val monitor = new Monitor();

	/**
	 * The value of the latch
	 */
	private var count: nat;
	
	/**
	 * Return latch with specified initial value
	 */
	def this(count: nat) {
		if (count < 0) throw new IllegalArgumentException("count < 0 in ModCountDownLatch");
		this.count = count;
	}
	
	/**
	 * Increment value
	 */
    def updateCount(): void {
    	monitor.lock();
	    ++count;
    	monitor.unlock();
    }

	/**
	 * Decrement value
	 * Unblock blocked threads if value reaches 0
	 */
    def countDown(): void {
    	// avoid locking if count == 0
    	if (count > 0) {
	    	monitor.lock();
		   	if (count > 0) --count;
		   	if (count == 0) monitor.unparkAll();
	   		monitor.unlock();
	   	}
    }

	/**
	 * Block until value reaches 0
	 */
    def await(): void {
    	// avoid locking if count == 0
    	if (count > 0) {
	    	monitor.lock();
	    	while (count > 0) monitor.await();
    		monitor.unlock();
		}
    }
	
	/**
	 * Return current value
	 */
    def getCount(): nat {
		return count;
    }
}
