/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

/**
 * @author tardieu
 */
class Semaphore {
 	private val monitor = new Monitor();

	private var permits:Int;
	
	def this(permits:Int) {
		this.permits = permits;
	}
	
    def release(permits:Int):Void {
    	monitor.lock();
	    this.permits += permits;
	   	if (permits >= 0) monitor.unpark();
    	monitor.unlock();
    }

    def release():Void {
    	release(1);
    }

    def reduce(permits:Int):Void {
    	monitor.lock();
	    this.permits -= permits;
    	monitor.unlock();
    }

    def acquire():Void {
    	monitor.lock();
    	while (permits <= 0) monitor.park();
   		--permits;
   		monitor.unlock();
    }

	def available():Int = permits;
}
