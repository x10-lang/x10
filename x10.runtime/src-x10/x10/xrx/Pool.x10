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

package x10.xrx;

import x10.util.concurrent.Latch;
import x10.util.Random;

class Pool {
    val latch = new Latch();
    val watcher = new Runtime.Watcher();
    var cancelWatcher:Runtime.Watcher = null;

    var wsEnd:Boolean = false;

    val workers = new Workers();

    var wsBlockedContinuations:Deque = null;

    var numDead : Long = 0;

    public def removeThreadLocalContexts() {
        for(var i:Int=0n; i<workers.count; i++) 
            workers.workers(i).removeWorkerContext();
    }

    operator this(n:Int):void {
        // numAllPlaces includes accelerators; if running in a resilient/elastic mode, must assume mutli-place.
        workers.multiplace = Place.numAllPlaces() > 1 || Runtime.RESILIENT_MODE != Configuration.RESILIENT_MODE_NONE; 
        workers.busyWaiting = Runtime.BUSY_WAITING || !Runtime.x10rtBlockingProbeSupport();
        workers(0n) = Runtime.worker();
        workers.count = n;
        for (var i:Int = 1n; i<n; i++) {
            workers(i) = new Worker(i);
        }
        // Create NUM_IMMEDIATE_THREADS dedicated to processing @Immediate asyncs
        if (workers.multiplace) {
            for (j in 1..Runtime.NUM_IMMEDIATE_THREADS) {
                val id = workers.count++;
                workers(id) = new Worker(id, true, "@ImmediateWorker-"+j);
                workers.deadCount++; // ignore immediate threads in dynamic pool-size adjustment
            }
        }
        for (var i:Int = 1n; i<workers.count; i++) {
            workers(i).start();
        }
    }

    def run():void {
        workers(0n)();
        Runtime.join();
    }

    // notify the pool a worker is about to execute a blocking operation
    def increase():void {
        val i = workers.increase();
        if (i > 0n) {
            // if no spare thread is available allocate and start a new thread
            val worker = new Worker(i);
            workers(i) = worker;
            worker.start();
        }
    }

    // create pseudo X10 worker for native thread
    public def wrapNativeThread():Worker {
        val i = workers.promote();
        val worker = new Worker(i, true);
        workers(i) = worker;
        return worker;
    }

    // notify the pool a worker resumed execution after a blocking operation
    def decrease(n:Int):void {
        workers.reduce(n);
    }
 
    // attempt to deal activity to idle worker
    def deal(activity:Activity):Boolean = workers.give(activity);

    // release permit (called by worker upon termination)
    def release(promoted:Boolean):void {
        workers.reclaim(promoted);
        if (workers.count == workers.deadCount) Runtime.pool.watcher.release();
    }

    // scan workers and network for pending activities
    def scan(random:Random, worker:Worker):Activity {
        var activity:Activity = null;
        var next:Int = random.nextInt(workers.count);
        val init:Int = next;
        for (;;) {
            if (null != activity || latch()) return activity;

            // go to sleep if too many threads are running
            activity = workers.yield(worker);
            if (null != activity || latch()) return activity;

            // look for an inbound task that is ready to execute
            activity = workers.inboundTasks.steal() as Activity;
            if (null != activity || latch()) {
                return activity;
            }

            // try the network ourselves
            Runtime.x10rtProbe();
            if (Place.numDead() != numDead) {
                atomic {
                    numDead = Place.numDead();
                    //Runtime.println("Number of dead places now "+numDead);
                    //for (p in Place.places()) {
                    //    if (p.isDead()) Runtime.println("Dead: "+p);
                    //}
                }
                // release any finishes that may have quiesced due to activities vanishing
                Runtime.notifyPlaceDeath();
            }
            activity = worker.poll();
            if (null != activity || latch()) return activity;

            do {
                // try local worker
                if (next < Runtime.MAX_THREADS && null != workers(next)) { // avoid race with increase method
                    activity = workers(next).steal();
                }
                if (null != activity || latch()) return activity;
                if (++next == workers.count) next = 0n;
            } while (next != init);
            activity = workers.inboundTasks.steal() as Activity;
            if (null != activity || latch()) {
                return activity;
            }

            // time to back off
            activity = workers.take(worker);
        }
    }

    def size() = workers.count;

    def flush(e:Long) { workers.flush(e); }
}

// vim:shiftwidth=4:tabstop=4:expandtab
