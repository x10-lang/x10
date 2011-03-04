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
    private var park:Boolean;

    private val lock = new Lock();
    
    private val threads = new Stack[Thread]();
    
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
            val thread = threads.pop();
            if (thread == Runtime.listener) {
                park = false;
            } else {
                Thread.unpark(thread);
            }
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

    private def park():Void {
        val thread = Thread.currentThread();
        threads.push(thread);
        while (threads.contains(thread)) {
            if (thread == Runtime.listener) {
                park = true;
                lock.unlock();
                while (park) NativeRuntime.event_probe();
                lock.lock();
            } else {
                lock.unlock();
                Thread.park();
                lock.lock();
            }
        }
    }
    
    def acquire():Void {
    	lock.lock();
    	while (permits <= 0) park();
   		--permits;
   		lock.unlock();
    }

	def available():Int = permits;
}
