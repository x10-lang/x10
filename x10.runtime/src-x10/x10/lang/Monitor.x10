/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.util.Stack;

/**
 * Lock with wait/notify capability implemented using park/unpark
 * @author tardieu
 */
class Monitor extends Lock {
    /**
     * Parked threads
     */
    private val threads = new Stack[X10Thread]();

    /**
     * Park calling thread
     * Increment blocked thread count
     * Must be called while holding the lock
     * Must not be called while holding the lock more than once
     */
    def await():Void {
        Runtime.increaseParallelism();
        val thread = X10Thread.currentThread();
        threads.push(thread);
        while (threads.contains(thread)) {
            unlock();
            Runtime.park();
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
            for (var i:Int = 0; i<size; i++) Runtime.unpark(threads.pop());
        }
        unlock();
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
