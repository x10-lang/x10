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

import x10.io.Unserializable;
import x10.util.concurrent.SimpleLatch;
import x10.util.Random;

public final class Worker extends Thread implements Unserializable {
    // bound on loop iterations to help j9 jit
    private static BOUND = 100n;

    // activity (about to be) executed by this worker
    var activity:Activity = null;

    // is this worker a promoted java thread?
    val promoted:Boolean;

    // pending activities
    private val queue = new Deque();

    // random number generator for this worker
    private val random:Random;

    //Worker Id for CollectingFinish
    val workerId:Int;

    //Used for 1:1 mapping between WorkStealing Worker and X10 Worker (Temp Soltuion)
    val wsfifo = new Deque();

    def this(workerId:Int) {
        super("X10 worker thread-" + workerId);
        promoted = false;
        this.workerId = workerId;
        random = new Random(workerId + (workerId << 8n) + (workerId << 16n) + (workerId << 24n));
    }

    // Horrible hack for @Immediate dedicated Worker
    // For managed X10, if we pass a String to the superclass constructor it means
    // "create a new thread"  if we don't pass a String it means "make the current thread 
    // be a worker".  
    def this(workerId:Int, promoted:Boolean, name:String) {
        super(name);
        this.promoted = promoted;
        this.workerId = workerId;
        random = new Random(workerId + (workerId << 8n) + (workerId << 16n) + (workerId << 24n));
    }

    def this(workerId:Int, promoted:Boolean) {
        super();
        this.promoted = promoted;
        this.workerId = workerId;
        random = new Random(workerId + (workerId << 8n) + (workerId << 16n) + (workerId << 24n));
        // [DC] Using 'here' as the srcPlace for the new activity causes a cycle:  The managed X10
        // implementation of 'here' uses thread-local storage, and this can create a cycle in the case
        // where access of thread-local storage occurs from a native java thread and triggers the creation
        // of a new Worker.
        // Using Place(0) is OK because the Uncounted finish passed into the activity does not use srcPlace.
        activity = new Activity(Runtime.epoch(), ()=>{}, Place(0), FinishState.UNCOUNTED_FINISH);
    }

    // return size of the deque
    def size():Int = queue.size();

    // return activity executed by this worker
    def activity() = activity;

    // poll activity from the bottom of the deque
    def poll() = queue.poll() as Activity;

    // steal activity from the top of the deque
    def steal() = queue.steal() as Activity;

    // push activity at the bottom of the deque
    def push(activity:Activity):void { queue.push(activity); }

    // run pending activities
    public operator this():void {
        try {
            if (promoted) {
                while (immediatePollLoop());
            } else {
                while (loop());
            }
        } catch (t:CheckedThrowable) {
            if (!Configuration.silenceInternalWarnings()) {
                Runtime.println("Uncaught exception in worker thread");
                t.printStackTrace();
            }
        } finally {
            Runtime.pool.release(promoted);
            if (Runtime.pool.workers.multiplace && Runtime.NUM_IMMEDIATE_THREADS > 0) Runtime.x10rtUnblockProbe();
        }
    }

    // inner loop to help j9 jit
    private def loop():Boolean {
        for (var i:Int = 0n; i < BOUND; i++) {
            activity = poll();
            if (activity == null) {
                activity = Runtime.pool.scan(random, this);
                if (activity == null) return false; // [DC] only happens when Runtime.pool's latch is released
                if (activity.epoch < Runtime.epoch()) continue;
            } else {
                if (activity.epoch < Runtime.epoch() || Runtime.pool.deal(activity())) continue;
            }
            activity.run();
            Unsafe.dealloc(activity);
        }
        return true;
    }

    // inner loop to help j9 jit
    private def immediatePollLoop() {
        for (var i:Int = 0n; i < BOUND; i++) {
            // FIXME: Shutdown race here.
            // Value of latch could change between when we check it
            // and when we call x10rtBlockingProbe
            if (Runtime.pool.latch()) return false;
            Runtime.x10rtBlockingProbe();
            for (var task:Activity = poll(); task != null; task = poll()) {
                if (task.epoch >= Runtime.epoch()) {
                    Runtime.pool.workers.submit(task);
                }
            }
            if (Place.numDead() != Runtime.pool.numDead) {
                atomic {
                    Runtime.pool.numDead = Place.numDead();
                }
                // Schedule an activity to release any finishes that may have 
                // quiesced due to activities vanishing
                Runtime.submitUncounted(()=>{ Runtime.notifyPlaceDeath(); });
            }
        } 
        return !Runtime.pool.latch();
    }

    def probe():void {
        // process all queued activities
        val tmp = activity; // save current activity
        Runtime.x10rtProbe();
        for (;;) {
            activity = poll();
            if (activity == null) {
                activity = Runtime.pool.workers.probeInbound(); // also look for tasks enqueued by Immediate worker threads
            }
            if (activity == null || activity.epoch < Runtime.epoch()) {
                activity = tmp; // restore current activity
                return;
            }
            activity.run();
            Unsafe.dealloc(activity);
        }
    }

    // run activities while waiting on finish
    def join(latch:SimpleLatch):void {
        val tmp = activity; // save current activity
        while (loop2(tmp.epoch, latch));
        activity = tmp; // restore current activity
    }

    // inner loop to help j9 jit
    private def loop2(epoch:Long, latch:SimpleLatch):Boolean {
        for (var i:Int = 0n; i < BOUND; i++) {
            if (epoch < Runtime.epoch() || latch()) return false;
            activity = poll();
            if (activity == null) return false;
            if (activity.epoch < Runtime.epoch()) continue;
            if (Runtime.pool.deal(activity)) continue;
            activity.run();
            Unsafe.dealloc(activity);
        }
        return true;
    }

    // park current worker
    public static def park() {
        if (!Runtime.STATIC_THREADS) {
            Thread.park();
        } else {
            Runtime.probe();
        }
    }

    // unpark worker
    // FIXME remove NoInline when XTENLANG-3397 is fixed
    @x10.compiler.NoInline
    public def unpark() {
        if (!Runtime.STATIC_THREADS) {
            super.unpark();
        }
    }
}

