/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.lang;

import x10.compiler.Pinned;
import x10.compiler.Global;

@Pinned public class Latch extends Monitor implements ()=>Boolean {

    public def this() { super(); }

    private def this(Any) {
        throw new UnsupportedOperationException("Cannot deserialize "+typeName());
    }

    private var state:boolean = false;

    public def release():void {
        lock();
        state = true;
        super.release();
    }

    public def await():void {
        // avoid locking if state == true
        Runtime.ensureNotInAtomic();
        if (!state) {
            lock();
            while (!state) super.await();
                unlock();
            }
        }

    public def apply():boolean = state; // memory model?
}
