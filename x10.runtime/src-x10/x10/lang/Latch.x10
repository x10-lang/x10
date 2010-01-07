/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * Boolean latch
 * @author tardieu
 */
class Latch extends Monitor implements ()=>Boolean {
    private var state:Boolean = false;

    public def release():Void {
        lock();
        state = true;
        super.release();
    }

    public def await():Void {
        // avoid locking if state == true
        if (!state) {
            lock();
            while (!state) super.await();
            unlock();
        }
    }

    public def apply():Boolean = state; // memory model?
}

// vim:shiftwidth=4:tabstop=4:expandtab
