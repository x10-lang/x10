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

@Pinned public class SimpleLatch extends Lock {
    public def this() { super(); }

    static type Worker = Runtime.Worker;

    private var worker:Worker = null;
    private var state:Boolean = false;

    // can only be called once
    public def await():void {
        val activity = Runtime.activity();
        if (activity.epoch < Runtime.epoch()) throw new DeadPlaceException("Cancelled");
        if (state) return;
        lock();
        if (state) {
            unlock();
            return;
        }
        Runtime.increaseParallelism(); // likely to be blocked for a while
        worker = Runtime.worker();
        while (!state) {
            if (activity.epoch < Runtime.epoch()) {
                unlock();
                release();
                throw new DeadPlaceException("Cancelled");
            }
            unlock();
            Worker.park();
            lock();
        }
        unlock();
    }

    public def release():void {
        if (state) return;
        lock();
        if (state) {
            unlock();
            return;
        }
        state = true;
        if (worker != null) {
            Runtime.decreaseParallelism(1n);
            worker.unpark();
        }
        unlock();
    }

    public operator this():Boolean = state;
}
