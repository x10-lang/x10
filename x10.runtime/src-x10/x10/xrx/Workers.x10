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

import x10.util.concurrent.Lock;

final class Workers {
    var epoch:Long = 42;

    val lock = new Lock(); // master lock for all thread pool adjustments

    val inboundTasks = new Deque();

    // every x10 thread (including promoted native threads)
    val workers = new Rail[Worker](Runtime.MAX_THREADS);

    // parked x10 threads (parkedCount == spareCount + idleCount)
    val parkedWorkers = new Rail[Worker](Runtime.MAX_THREADS);

    var count:Int = 0n; // count every x10 threads (including promoted native threads)
    var spareCount:Int = 0n; // spare thread count
    var idleCount:Int = 0n; // idle thread count
    var deadCount:Int = 0n; // dead thread count
    var spareNeeded:Int = 0n; // running threads - NTHREADS
    var multiplace:Boolean = true; // is running with multiple places
    var busyWaiting:Boolean = true; // should busy wait
    var probing:Boolean = false; // is already in bloking probe

    // reduce permits by n
    def reduce(n:Int):void {
        lock.lock();
        spareNeeded += n;
        lock.unlock();
    }

    // increase live thread count
    // return allocated thread index if any
    def increase():Int {
        lock.lock();
        // convert() is needed to make sure we account for idle threads as parked threads
        // otherwise we could end up with no thread probing
        convert();
        if (spareNeeded > 0n) {
            spareNeeded--;
            lock.unlock();
            return 0n;
        } else if (spareCount > 0n) {
            // resume spare thread
            val i = --spareCount + idleCount;
            val worker = parkedWorkers(i);
            parkedWorkers(i) = null;
            lock.unlock();
            worker.unpark();
            return 0n;
        } else {
            // start new thread
            val i = count++;
            lock.unlock();
            check(i+1n);
            return i;
        }
    }

    // promote native thread
    // return thread index
    def promote():Int {
        lock.lock();
        val i = count++;
        deadCount++; // native threads should terminate on their own
        lock.unlock();
        check(i+1n);
        return i;
    }

    // check max thread count has not been reached
    def check(new_count:Int):void {
        if (new_count > Runtime.MAX_THREADS) {
            if (new_count < Runtime.MAX_THREADS + 100 || (new_count % 100 == 0)) {
                Runtime.println(here+": TOO MANY THREADS (there are now "+new_count+" threads).");
            }
            throw new InternalError(here+": TOO MANY THREADS (there are now "+new_count+" threads).");
        }
        if (Runtime.WARN_ON_THREAD_CREATION) {
            Runtime.println(here+": WARNING: A new OS-level thread was discovered (there are now "+new_count+" threads).");
            Runtime.println("NOTE: The following stack trace is not an error, but to help identify the origin of the new OS-level thread.");
            try { throw new Exception(); } catch (e:Exception) { e.printStackTrace(); }
        }
    }

    // convert idle threads to spare as needed
    def convert() {
        while (spareNeeded > 0n && idleCount > 0n) {
            spareNeeded--;
            idleCount--;
            spareCount++;
        }
    }

    // park if spare needed -> spare thread
    def yield(worker:Worker):Activity {
        if (spareNeeded <= 0n) return null;
        lock.lock();
        convert();
        if (spareNeeded <= 0n) {
            lock.unlock();
            return null;
        }
        spareNeeded--;
        val i = spareCount++ + idleCount;
        parkedWorkers(i) = worker;
        while (parkedWorkers(i) == worker) {
            lock.unlock();
            Worker.park();
            lock.lock();
        }
        lock.unlock();
        return worker.activity;
    }

    // park until given work to do -> idle thread
    def take(worker:Worker):Activity {
        if (multiplace && busyWaiting && (idleCount - spareNeeded >= Runtime.NTHREADS - 1)) return null;
        lock.lock();
        val task = inboundTasks.steal() as Activity;
        if(task != null) {
            lock.unlock();
            return task;
        }
        convert();
        if (multiplace && busyWaiting && (idleCount >= Runtime.NTHREADS - 1)) {
            lock.unlock();
            return null;
        }
        if (multiplace && !busyWaiting && !probing && Runtime.NUM_IMMEDIATE_THREADS == 0n) {
            probing = true;
            lock.unlock();
            Runtime.x10rtBlockingProbe();
            lock.lock();
            probing = false;
            lock.unlock();
            return worker.poll();
        }
        val i = spareCount + idleCount++;
        parkedWorkers(i) = worker;
        while (parkedWorkers(i) == worker) {
            lock.unlock();
            Worker.park();
            lock.lock();
        }
        lock.unlock();
        return worker.activity;
    }
    
    def submit(activity:Activity):void {
        lock.lock();
        convert();
        if (idleCount <= 0n) {
            val p = probing;
            inboundTasks.push(activity);
            lock.unlock();
            if (p && multiplace) Runtime.x10rtUnblockProbe();
            return;
        }
        val i = spareCount + --idleCount;
        val worker = parkedWorkers(i);
        worker.activity = activity;
        parkedWorkers(i) = null;
        lock.unlock();
        worker.unpark();
    }

    def probeInbound():Activity {
        lock.lock();
        val task = inboundTasks.steal() as Activity;
        lock.unlock();
        return task;
    }

    // deal to idle worker if any
    // return true on success
    def give(activity:Activity):Boolean {
        if (idleCount - spareNeeded <= 0n && !probing) return false;
        lock.lock();
        convert();
        if (idleCount <= 0n) {
            val p = probing;
            lock.unlock();
            if (p && multiplace) Runtime.x10rtUnblockProbe();
            return false;
        }
        val i = spareCount + --idleCount;
        val worker = parkedWorkers(i);
        worker.activity = activity;
        parkedWorkers(i) = null;
        lock.unlock();
        worker.unpark();
        return true;
    }

    // account for terminated thread
    def reclaim(promoted:Boolean):void {
        lock.lock();
        if (!promoted) deadCount++; // deadCount for promoted thread incremented when thread created.
        while (idleCount > 0n) {
            val i = spareCount + --idleCount;
            val worker = parkedWorkers(i);
            parkedWorkers(i) = null;
            worker.unpark();
        }
        if (spareCount > 0n) {
            val worker = parkedWorkers(--spareCount);
            parkedWorkers(spareCount) = null;
            worker.unpark();
        }
        val p = probing;
        lock.unlock();
        if (multiplace && (p || Runtime.NUM_IMMEDIATE_THREADS > 0)) Runtime.x10rtUnblockProbe();
    }

    public operator this(i:Int) = workers(i);
    public operator this(i:Int)=(worker:Worker) { workers(i) = worker; }

    def flush(e:Long) {
        lock.lock();
        if (e > epoch) {
            epoch = e;
            for (var i:Int=0n; i<count; i++) {
                if (workers(i) != null) while (workers(i).steal() != null);
            }
            while (inboundTasks.steal() != null) {};
        }
        Runtime.finishStates.clear(e);
        val p = probing;
        lock.unlock();
        if (multiplace && (p || Runtime.NUM_IMMEDIATE_THREADS > 0)) Runtime.x10rtUnblockProbe();
        for (var i:Int=0n; i<count; i++) {
            if (workers(i) != null) workers(i).unpark();
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
