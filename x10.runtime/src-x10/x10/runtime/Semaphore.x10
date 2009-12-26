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
class Semaphore {
    private val lock = new Lock();

    private val threads = new Stack[X10Thread]();

    private var permits:Int;

    def this(n:Int) {
        permits = n;
    }

    private static def min(i:Int, j:Int):Int = i<j ? i : j;

    def release(n:Int):Void {
        lock.lock();
        permits += n;
        val m = min(permits, min(n, threads.size()));
        for (var i:Int = 0; i<m; i++) {
            threads.pop().unpark();
        }
        lock.unlock();
    }

    def release():Void {
        release(1);
    }

    def reduce(n:Int):Void {
        lock.lock();
        permits -= n;
        lock.unlock();
    }

    def acquire():Void {
        lock.lock();
        val thread = X10Thread.currentThread();
        while (permits <= 0) {
            threads.push(thread);
            while (threads.contains(thread)) {
                lock.unlock();
                X10Thread.park();
                lock.lock();
            }
        }
        --permits;
        lock.unlock();
    }

    def available():Int = permits;
}

// vim:shiftwidth=4:tabstop=4:expandtab
