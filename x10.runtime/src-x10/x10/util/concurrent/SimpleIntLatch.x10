 /*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.util.concurrent;

import x10.compiler.Pinned;

@Pinned public class SimpleIntLatch extends Lock {
    public def this() { super(); }

    static type Worker = Runtime.Worker;

    private var worker:Worker = null;
    private var value:Int = 0n;

    // can only be called once
    public def await():void {
        if (value != 0n) return;
        lock();
        if (value != 0n) {
            unlock();
            return;
        }
        Runtime.increaseParallelism(); // likely to be blocked for a while
        worker = Runtime.worker();
        while (value == 0n) {
            unlock();
            Worker.park();
            lock();
        }
        unlock();
    }

    public operator this()=(v:Int):void { set(v); }
    public def set(v:Int):void {
        lock();
        if (value != 0n) {
            unlock();
            return;
        }
        value = v;
        if (worker != null) {
            Runtime.decreaseParallelism(1n);
            worker.unpark();
        }
        unlock();
    }

    public operator this():Int = value;
}
