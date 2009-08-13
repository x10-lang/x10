/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

/**
 * Boolean latch
 * @author tardieu
 */
class Latch extends Monitor {
	private var state:Boolean = false;
	
    def set():Void {
    	lock();
	    state = true;
	    unpark();
    	unlock();
    }

    def await():Void {
    	// avoid locking if state == true
    	if (!state) {
	    	lock();
	    	while (!state) park();
            unlock();
		}
    }
	
    def get():Boolean {
        return state; // memory model?
    }
}
