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

/*
 * Simple cyclic SPMD barrier implementation.
 * Performs much better than Clock when applicable.
 *
 * final class SPMDBarrierTest {
 *     static tasks = 8;
 *     static iterations = 100000;
 *
 *     public static def main(Array[String]) {
 *         val time = System.nanoTime();
 *         finish {
 *             val b = new x10.util.concurrent.SPMDBarrier(tasks);
 *             for (var i:Int=0; i<tasks; ++i) {
 *                 async {
 *                     b.register();
 *                     for (var j:Int=0; j<iterations; ++j)
 *                         b.advance();
 *                 }
 *             }
 *         }
 *         Runtime.println("barrier time: " + (System.nanoTime() - time) / 1e9 / iterations + "s");
 *     }
 * }
 *
 */

package x10.util.concurrent;

public final class SPMDBarrier(count:Int) {
    private val alive:AtomicInteger = new AtomicInteger(count);
    private val workers = new Rail[Runtime.Worker](count);
    private var index:Int = 0;

    /* constructs an SPMDBarrier for the given task count */
    /* does not implicitly register caller task */
    public def this(count:Int) {
        property(count);
        assert (Runtime.NTHREADS >= count) : "SPMDBarrier constructor invoked with task count greater than Runtime.NTHREADS";
    }
    
    /* register caller task with the barrier */
    public def register() {
        workers(index++) = Runtime.worker();
    }

    /* blocks until all tasks have called advance */
    public def advance() {
        if (alive.decrementAndGet() == 0) {
            alive.set(count);
            val me = Runtime.worker();
            for (var i:Int=0; i<count; ++i)
                if (workers(i) != me) workers(i).unpark();
        } else {
            Runtime.Worker.park();
        }
    }
}
