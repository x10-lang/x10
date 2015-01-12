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

/**
 * Simple cyclic SPMD barrier implementation.
 * Performs much better than Clock when applicable.
 * <pre>
 * final class SPMDBarrierTest {
 *     static tasks = 8;
 *     static iterations = 100000;
 *
 *     public static def main(Rail[String]) {
 *         val time = System.nanoTime();
 *         finish {
 *             val b = new x10.util.concurrent.SPMDBarrier(tasks);
 *             for (var i:Int=0; i&lt;tasks; ++i) {
 *                 async {
 *                     b.register();
 *                     for (var j:Int=0; j&lt;iterations; ++j)
 *                         b.advance();
 *                 }
 *             }
 *         }
 *         Runtime.println("barrier time: " + (System.nanoTime() - time) / 1e9 / iterations + "s");
 *     }
 * }
 * </pre>
 */
public final class SPMDBarrier(count:Int) {
    private val alive = new AtomicInteger(count);
    private val workers = new Rail[Runtime.Worker](count);
    private val index = new AtomicInteger(0n);
    private val phase = new AtomicInteger(0n);

    /**
     * Construct an SPMDBarrier for the given task count.
     * Does not implicitly register caller task.
     */
    public def this(count:Int) {
        property(count);
        assert (Runtime.NTHREADS >= count) : "SPMDBarrier constructor invoked with task count greater than Runtime.NTHREADS";
    }
    
    /** Register caller task with the barrier. */
    public def register() {
    	val i = index.getAndIncrement();
    	assert (i < count) : "SPMDBarrier register invoked too many times";
        workers(i) = Runtime.worker();
    }

    /** Block until all tasks have called advance. */
    public def advance() {
        val p = phase.get();
        if (alive.decrementAndGet() == 0n) {
            alive.set(count);
            phase.getAndIncrement();
            val me = Runtime.worker();
            for (var i:Int=0n; i<count; ++i)
                if (workers(i) != me) workers(i).unpark();
        } else {
            while (p == phase.get()) Runtime.Worker.park();
        }
    }
}
