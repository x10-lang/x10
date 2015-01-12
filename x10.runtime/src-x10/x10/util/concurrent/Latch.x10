/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.util.concurrent;

import x10.compiler.Pinned;
import x10.xrx.Runtime;

/**
 * Boolean latch.
 * Inherited look/unlock/tryLock method from superclass can be used.
 */
@Pinned public class Latch extends Monitor implements ()=>Boolean {
    /**
     * Latch is initially unset
     */
    public def this() { super(); }

    private def this(Any) {
        throw new UnsupportedOperationException("Cannot deserialize "+typeName());
    }

    private var state:Boolean = false;

    /**
     * Set the latch
     */
    public def release():void {
        lock();
        state = true;
        super.release();
    }

    /**
     * Wait for the latch to be set
     * Return instantly if it already is
     */
    public def await():void {
        Runtime.ensureNotInAtomic();
        // avoid locking if state == true
        if (!state) {
            lock();
            while (!state) super.await();
                unlock();
            }
        }

    /**
     * Check the latch state without blocking
     */
    public operator this():Boolean = state;
}
