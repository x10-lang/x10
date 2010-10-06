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
import x10.util.Stack;

@Pinned public class Monitor extends Lock {
    public def this() { super(); }

    private def this(Any) {
        throw new UnsupportedOperationException("Cannot deserialize "+typeName());
    }


    static type Thread = Runtime.Thread;

    /**
     * Parked threads
     */
    private val threads = new Stack[Thread]();

    /**
     * Park calling thread
     * Increment blocked thread count
     * Must be called while holding the lock
     * Must not be called while holding the lock more than once
     */
    def await():void {
        Runtime.increaseParallelism();
        val thread = Thread.currentThread();
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
    def release():void {
        val size = threads.size();
        if (size > 0) {
            Runtime.decreaseParallelism(size);
            for (var i:Int = 0; i<size; i++) Runtime.unpark(threads.pop());
        }
        unlock();
    }
}

