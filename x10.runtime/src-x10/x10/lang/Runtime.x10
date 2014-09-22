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

package x10.lang;

import x10.compiler.Native;
import x10.compiler.Inline;
import x10.compiler.Pragma;
import x10.compiler.StackAllocate;
import x10.compiler.NativeCPPInclude;

import x10.io.Serializer;
import x10.io.Deserializer;
import x10.io.Unserializable;
import x10.io.Reader;
import x10.io.Writer;

import x10.util.Random;
import x10.util.Box;

import x10.util.concurrent.Condition;
import x10.util.concurrent.Latch;
import x10.util.concurrent.Lock;
import x10.util.concurrent.Monitor;
import x10.util.concurrent.SimpleLatch;


/**
 * XRX invocation protocol:
 * - Native runtime invokes new Runtime.Worker(0). Returns Worker instance worker0.
 * - Native runtime starts worker0 thread.
 * - Native runtime invokes Runtime.start(...) from worker0 (Thread.currentThread() == worker0).
 * - Runtime.start(...) returns when application code execution has completed.
 */
@NativeCPPInclude("x10/lang/RuntimeNatives.h")
public final class Runtime {

    // Debug print methods

    @Native("java", "java.lang.System.err.println(#any)")
    @Native("c++", "::x10::lang::RuntimeNatives::println(::x10aux::to_string(#any)->c_str())")
    public native static def println(any:Any):void;

    @Native("java", "java.lang.System.err.println()")
    @Native("c++", "::x10::lang::RuntimeNatives::println(\"\")")
    public native static def println():void;

    @Native("java", "java.lang.System.err.printf(#fmt, #t)")
    @Native("c++", "::x10::lang::RuntimeNatives::printf(#fmt, #t)")
    public native static def printf[T](fmt:String, t:T):void;


    /** Get a string that identifies the X10 execution environment of this place.
     * The string is undefined but should be helpful for debugging, typically containing the pid and hostname.
     * On java it is equivalent to java.lang.management.ManagementFactory.getRuntimeMXBean().getName().
     */
    @Native("java", "java.lang.management.ManagementFactory.getRuntimeMXBean().getName()")
    @Native("c++", "::x10aux::runtime_name()")
    public native static def getName() : String;

    // Native runtime interface

    /**
     * Send active message to another place.
     * @param id The id of the message handler
     * @param msgBody The body of the active message
     * @param prof A profile object to use for this message (may be null)
     */
    public static def x10rtSendMessage(id:Long, msgBody:()=>void, prof:Profile):void {
        x10rtSendMessage(id, msgBody, prof, null);
    }

    /**
     * Send active message to another place.
     * @param id The id of the message handler
     * @param msgBody The body of the active message
     * @param prof A profile object to use for this message (may be null)
     * @param preSendAction A closure to evaluate at the current place immediately
     *            before sending the message (but after msgBody is serialized) (may be null).
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runClosureAt((int)(#id), #msgBody, #prof, #preSendAction)")
    @Native("c++", "::x10aux::run_closure_at((x10_int)#id, #msgBody, #prof, #preSendAction)")
    public static native def x10rtSendMessageInternal(id:Long, msgBody:()=>void, prof:Profile, preSendAction:()=>void):void;

    public static def x10rtSendMessage(id:Long, msgBody:()=>void, prof:Profile, preSendAction:()=>void):void {
        var body:()=>void = msgBody;
        if (CANCELLABLE) {
            val epoch = epoch();
            if (activity() != null && activity().epoch < epoch) throw new DeadPlaceException("Cancelled");
            body = ()=> {
                if (epoch > epoch()) pool.flush(epoch);
                if (epoch == epoch()) msgBody();
            };
        }
        x10rtSendMessageInternal(id, body, prof, preSendAction);
    }

    /**
     * Send async to another place.
     * This is a special case of x10rtSendMessage where the active message consists in
     * creating an activity at the destination place with the specified body and finish state
     * and pushing this activity onto the deque of the active worker.
     * @param id The id of the message handler
     * @param body The body of the remote activity
     * @param finishState The governing FinishState
     * @param prof A profile object to use for this message (may be null)
     * @param preSendAction A closure to evaluate at the current place immediately
     *            before sending the message (but after finishState and body are serialized)
     *            (may be null)
     */
    // TODO add epoch to c++
    @Native("java", "x10.runtime.impl.java.Runtime.runAsyncAt((long)(#epoch), (int)(#id), #body, #finishState, #prof, #preSendAction)")
    @Native("c++", "::x10aux::run_async_at((x10_long)(#id), #body, #finishState, #prof, #preSendAction)")
    public static native def x10rtSendAsyncInternal(epoch:Long, id:Long, body:()=>void, finishState:FinishState, 
                                            prof:Profile, preSendAction:()=>void):void;

    public static def x10rtSendAsync(id:Long, body:()=>void, finishState:FinishState, 
                                            prof:Profile, preSendAction:()=>void):void {
        val epoch = epoch();
        if (CANCELLABLE) {
            if (activity() != null && activity().epoch < epoch) throw new DeadPlaceException("Cancelled");
        }
        x10rtSendAsyncInternal(epoch, id, body, finishState, prof, preSendAction);
    }

                                                    /**
     * Process one incoming active message if any (non-blocking).
     */
    @Native("c++", "::x10aux::event_probe()")
    @Native("java", "x10.runtime.impl.java.Runtime.eventProbe()")
    public static native def x10rtProbe():void;

    @Native("c++", "x10rt_blocking_probe_support()")
    @Native("java", "x10.x10rt.X10RT.blockingProbeSupport()")
    private static native def x10rtBlockingProbeSupport():Boolean;

    @Native("c++", "::x10aux::blocking_probe()")
    @Native("java", "x10.runtime.impl.java.Runtime.blockingProbe()")
    public static native def x10rtBlockingProbe():void;

    @Native("c++", "::x10aux::unblock_probe()")
    @Native("java", "x10.runtime.impl.java.Runtime.unblockProbe()")
    public static native def x10rtUnblockProbe():void;
    
    /**
     * Process one incoming active message if any (non-blocking).
     */
    @Native("c++", "::x10aux::event_probe()")
    @Native("java", "x10.runtime.impl.java.Runtime.eventProbe()")
    public static native def wsProcessEvents():void;

    /**
     * Return a deep copy of the object graph rooted at o.
     */
    public static def deepCopy[T](o:T, prof:Profile):T {
        val start = prof != null ? System.nanoTime() : 0;

        @StackAllocate val ser = @StackAllocate new Serializer();
        ser.writeAny(o);
        @StackAllocate val deser = @StackAllocate new Deserializer(ser);
        val copy:T = deser.readAny() as T;

        if (prof != null) {
            val end = System.nanoTime();
            prof.serializationNanos += (end-start);
            prof.bytes += ser.dataBytesWritten();
        }

        return copy;
    }

    public static def deepCopy[T](o:T):T = deepCopy[T](o, null);

    // Memory management

    /**
     * Encapsulates the properties of the different memory
     * allocators available to the program.  In addition to
     * a default allocator, the program may be able to use
     * specialized allocators that allocate into a pool of 
     * large pages or that support congruent allocation.
     */
    public static class MemoryAllocator {
        public static val DEFAULT_ALLOCATOR = new MemoryAllocator(false, false);
        private static val ALLOC_HC = hugePagesAvailable() && congruentAvailable() ? new MemoryAllocator(true, true) : null;
        private static val ALLOC_H = hugePagesAvailable() ? new MemoryAllocator(true, false) : null;
        private static val ALLOC_C = congruentAvailable() ? new MemoryAllocator(false, true) : null;

        private val hugePages:Boolean;
        private val congruent:Boolean;
        
        private def this(h:Boolean, c:Boolean) {
            hugePages = h; 
            congruent = c;
        }

        @Native("java", "false")
        @Native("c++", "::x10aux::congruent_huge")
        public static native def hugePagesAvailable():Boolean;

        @Native("java", "false")
        @Native("c++", "true")
        public static native def congruentAvailable():Boolean;

        /**
         * Request a memory allocator with the desried properties,
         * the closest available allocator (which may be the default 
         * allocator) will be returned.
         * 
         * @param hugePages allocate from pool of large pages?
         * @param congurent allocate from congruent memory?
         */
        public static def requestAllocator(hugePages:Boolean, congruent:Boolean):MemoryAllocator {
            if (congruent && hugePages && ALLOC_HC != null) return ALLOC_HC;
	    if (congruent && ALLOC_C != null) return ALLOC_C;
            if (hugePages && ALLOC_H != null) return ALLOC_H;
            return DEFAULT_ALLOCATOR;
        }

        /**
         * Acquire a memory allocator with the required properties.
         * If one is not available, an OutOfMemoryError will be thrown.
         * 
         * @param hugePages allocate from pool of large pages?
         * @param congurent allocate from congruent memory?
         */
        public static def requireAllocator(hugePages:Boolean, congruent:Boolean):MemoryAllocator {
            if (congruent && hugePages) {
                if (ALLOC_HC == null) throw new OutOfMemoryError("Required Memory Allocator unavailable");
                return ALLOC_HC;
            }
	    if (congruent) {
                if (ALLOC_C == null) throw new OutOfMemoryError("Required Memory Allocator unavailable");
                return ALLOC_C;
            }

            if (hugePages) {
                if (ALLOC_H == null) throw new OutOfMemoryError("Required Memory Allocator unavailable");
                return ALLOC_H;
            }
            return DEFAULT_ALLOCATOR;
        }
    }

    // Environment variables

    static env = Configuration.loadEnv();

    public static STRICT_FINISH = Configuration.strict_finish();
    public static NTHREADS = Configuration.nthreads();
    public static MAX_THREADS = Configuration.max_threads();
    public static STATIC_THREADS = Configuration.static_threads();
    public static WARN_ON_THREAD_CREATION = Configuration.warn_on_thread_creation();
    public static BUSY_WAITING = Configuration.busy_waiting();
    public static CANCELLABLE = Configuration.cancellable();
    public static RESILIENT_MODE = Configuration.resilient_mode();

    // External process execution

    /**
     * Executes the specified command in a separate process.
     * The returned InputStreamReader is connected to the standard output
     * of the new process.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.execForRead(#command)")
    @Native("c++", "::x10::lang::RuntimeNatives::execForRead(::x10aux::to_string(#command)->c_str())")
    public static native def execForRead(command:String):Reader{self!=null};

    /**
     * Executes the specified command in a separate process.
     * The returned OutputStreamWriter is connected to the standard input
     * of the new process.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.execForWrite(#command)")
    @Native("c++", "::x10::lang::RuntimeNatives::execForWrite(::x10aux::to_string(#command)->c_str())")
    public static native def execForWrite(command:String):Writer{self!=null};
            
    // Runtime state

    static staticMonitor = new Monitor();
    public static atomicMonitor = new Monitor();
    static pool = new Pool();
    static finishStates = new FinishState.FinishStates();

    // Work-stealing runtime
    
    public static def wsInit():void {
        pool.wsBlockedContinuations = new Deque();
    }
    
    public static def wsFIFO():Deque {
        return worker().wsfifo;
    }
    
    public static def wsBlock(k:Any) {
        pool.wsBlockedContinuations.push(k);
    }

    public static def wsUnblock() {
        val src = pool.wsBlockedContinuations;
        val dst = wsFIFO();
        var k:Any;
        while ((k = src.poll()) != null) dst.push(k);
    }

    public static def wsEnd() {
        pool.wsEnd = true;
    }

    public static def wsEnded() = pool.wsEnd;

    /**
     * A mortal object is collected when there are no remaining local refs even if remote refs might still exist
     */
    public interface Mortal {}

    static final class Workers {
        var epoch:Long = 42;

        val lock = new Lock(); // master lock for all thread pool adjustments

        // every x10 thread (including promoted native threads)
        private val workers = new Rail[Worker](MAX_THREADS);

        // parked x10 threads (parkedCount == spareCount + idleCount)
        val parkedWorkers = new Rail[Worker](MAX_THREADS);

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
            if (new_count > MAX_THREADS) {
                if (new_count < MAX_THREADS + 100 || (new_count % 100 == 0)) {
                    println(here+": TOO MANY THREADS (there are now "+new_count+" threads).");
                }
                throw new InternalError(here+": TOO MANY THREADS (there are now "+new_count+" threads).");
            }
            if (WARN_ON_THREAD_CREATION) {
                println(here+": WARNING: A new OS-level thread was discovered (there are now "+new_count+" threads).");
                println("NOTE: The following stack trace is not an error, but to help identify the origin of the new OS-level thread.");
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
            if (multiplace && busyWaiting && (idleCount - spareNeeded >= NTHREADS - 1)) return null;
            lock.lock();
//            if(pool.workers(0n).promoted) {
                val task = pool.workers(0n).steal();
                if(task != null) {
                    lock.unlock();
                    return task;
                }
//            }
            convert();
            if (multiplace && busyWaiting && (idleCount >= NTHREADS - 1)) {
                lock.unlock();
                return null;
            }
            if (multiplace && !busyWaiting && !probing) {
                probing = true;
                lock.unlock();
                x10rtBlockingProbe();
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
                pool.workers(0n).push(activity);
                lock.unlock();
                if (p && multiplace) x10rtUnblockProbe();
                return;
            }
            val i = spareCount + --idleCount;
            val worker = parkedWorkers(i);
            worker.activity = activity;
            parkedWorkers(i) = null;
            lock.unlock();
            worker.unpark();
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
                if (p && multiplace) x10rtUnblockProbe();
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
        def reclaim():void {
            lock.lock();
            deadCount++;
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
            if (p && multiplace) x10rtUnblockProbe();
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
            }
            finishStates.clear(e);
            val p = probing;
            lock.unlock();
            if (p && multiplace) x10rtUnblockProbe();
            for (var i:Int=0n; i<count; i++) {
                if (workers(i) != null) workers(i).unpark();
            }
        }
    }

    public final static class Worker extends Thread implements Unserializable {
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
            activity = new Activity(epoch(), ()=>{}, Place(0), FinishState.UNCOUNTED_FINISH);
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
                while (loop());
            } catch (t:CheckedThrowable) {
                println("Uncaught exception in worker thread");
                t.printStackTrace();
            } finally {
                pool.release();
            }
        }

        // inner loop to help j9 jit
        private def loop():Boolean {
            for (var i:Int = 0n; i < BOUND; i++) {
                activity = poll();
                if (activity == null) {
                    activity = pool.scan(random, this);
                    if (activity == null) return false; // [DC] only happens when pool's latch is released
                    if (activity.epoch < epoch()) continue;
                } else {
                    if (activity.epoch < epoch() || pool.deal(activity())) continue;
                }
                activity.run();
                Unsafe.dealloc(activity);
            }
            return true;
        }

        def probe():void {
            // process all queued activities
            val tmp = activity; // save current activity
            x10rtProbe();
            for (;;) {
                activity = poll();
                if (activity == null || activity.epoch < epoch()) {
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
                if (epoch < epoch() || latch()) return false;
                activity = poll();
                if (activity == null) return false;
                if (activity.epoch < epoch()) continue;
                if (pool.deal(activity)) continue;
//                if (activity.finishState().simpleLatch() != latch) {
//                    push(activity);
//                    return false;
//                }
                activity.run();
                Unsafe.dealloc(activity);
            }
            return true;
        }

        // park current worker
        public static def park() {
            if (!STATIC_THREADS) {
                Thread.park();
            } else {
                Runtime.probe();
            }
        }

        // unpark worker
        // FIXME remove NoInline when XTENLANG-3397 is fixed
        @x10.compiler.NoInline
        public def unpark() {
            if (!STATIC_THREADS) {
                super.unpark();
            }
        }
    }

    static class Pool {
        val latch = new Latch();
        val watcher = new Watcher();
        var cancelWatcher:Watcher = null;
        
        var wsEnd:Boolean = false;

        val workers = new Workers();

        var wsBlockedContinuations:Deque = null;

        var numDead : Long = 0;

        public def removeThreadLocalContexts() {
            for(var i:Int=0n; i<workers.count; i++) 
            	workers.workers(i).removeWorkerContext();
        }

        operator this(n:Int):void {
            workers.multiplace = Place.numAllPlaces()>1; // numAllPlaces includes accelerators
            workers.busyWaiting = BUSY_WAITING || !x10rtBlockingProbeSupport() || 
                !(RESILIENT_MODE==Configuration.RESILIENT_MODE_NONE || RESILIENT_MODE==Configuration.RESILIENT_MODE_X10RT_ONLY);
            workers(0n) = worker();
            workers.count = n;
            for (var i:Int = 1n; i<n; i++) {
                workers(i) = new Worker(i);
            }
            for (var i:Int = 1n; i<n; i++) {
                workers(i).start();
            }
        }

        def run():void {
            workers(0n)();
            join();
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
        def release():void {
            workers.reclaim();
            if (workers.count == workers.deadCount) pool.watcher.release();
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
                // try network
                x10rtProbe();
                if (Place.numDead() != numDead) {
                    atomic {
                        numDead = Place.numDead();
                        //Runtime.println("Number of dead places now "+numDead);
                        //for (p in Place.places()) {
                        //    if (p.isDead()) Runtime.println("Dead: "+p);
                        //}
                    }
                    // release any finishes that may have quiesced due to activities vanishing
                    notifyPlaceDeath();
                }
                activity = worker.poll();
                if (null != activity || latch()) return activity;
                do {
                    // try local worker
                    if (next < MAX_THREADS && null != workers(next)) { // avoid race with increase method
                        activity = workers(next).steal();
                    }
                    if (null != activity || latch()) return activity;
                    if (++next == workers.count) next = 0n;
                } while (next != init);
                // time to back off
                activity = workers.take(worker);
            }
        }

        def size() = workers.count;

        def flush(e:Long) { workers.flush(e); }
    }


    /**
     * Return the current worker
     */
    public static def worker():Worker = Thread.currentThread() as Worker;

    /**
     * Return the current worker id
     */
    public static def workerId():Int = worker().workerId;

    /**
     * Return the number of workers currently in the pool
     * (can increase, cannot decrease)
     */
    public static def poolSize():Int = pool.size();

    /**
     * Return the current activity
     */
    public static def activity():Activity = worker().activity();

    /**
     * Return the current place
     */
    @Native("c++", "::x10::lang::Place::_make(::x10aux::here)")
    @Native("java", "x10.x10rt.X10RT.here()")
    public native static def home():Place;

    /**
     * Return the id of the current place
     * @Deprecated("Use hereLong()")
     */
    @Native("c++", "::x10aux::here")
    @Native("java", "x10.x10rt.X10RT.hereId()")
    public static def hereInt():Int = here.id as Int;

    @Native("c++", "((x10_long)::x10aux::here)")
    @Native("java", "((long)x10.x10rt.X10RT.hereId())")
    public static def hereLong():Long = here.id;


    /**
     * The amount of unscheduled activities currently available to this worker thread.
     * Intended for use in heuristics that control async spawning
     * based on the current amount of surplus work.
     */
    public static def surplusActivityCount():Int = worker().size();

    private static val processStartNanos_ = new Cell[Long](0);
    public static def processStartNanos() = processStartNanos_();

    public static final class Watcher extends Condition implements Unserializable {
        private var t:Exception = null;

        public def raise(t:Exception):void { this.t = t; }
        
        /**
         * Wait for job to complete.
         * Block calling thread if not an X10 thread.
         * Rethrow MultipleException collected by root finish for the job if any.
         * May be called multiple times by a unique thread.
         */
        public def await():void {
            if(! Thread.isX10WorkerThread()) {
                super.await();
            } else {
                Runtime.increaseParallelism();
                super.await();
                Runtime.decreaseParallelism(1n);
            }
            if (null != t) throw t;
        }

        /**
         * Wait for job to complete with a timeout.
         * Block calling thread if not an X10 thread.
         * Rethrow MultipleException collected by root finish for the job if any.
         * May be called multiple times by a unique thread.
         */
        public def await(timeout:Long):void {
            if(! Thread.isX10WorkerThread()) {
                super.await(timeout);
            } else {
                Runtime.increaseParallelism();
                super.await(timeout);
                Runtime.decreaseParallelism(1n);
            }
            if (null != t) throw t;
        }

        /**
         * Request cooperative cancellation of job.
         * Non-blocking.
         * Not implemented (TODO).
         */
        public def cancel():void {}
    }

    /**
     * Submit uncounted job (possibly distributed) at current place to XRX.
     * Job is to be executed asynchronously be the XRX pool.
     * @param job Job being submitted
     */
    public static def submitUncounted(job:()=>void):void {
        pool.workers.submit(new Activity(epoch(), job, here, FinishState.UNCOUNTED_FINISH));
    }

    /**
     * Submit job (possibly distributed) at current place to XRX.
     * Job is to be executed asynchronously be the XRX pool under an implicit root finish.
     * @param job Job being submitted
     * @return the Watcher object associated with the job
     */
    public static def submit(job:()=>void):Watcher {
        val watcher = new Watcher();
        val wrapper = ()=>{ try { finish async job(); } catch (t:Exception) { watcher.raise(t); } finally { watcher.release(); } };
        submitUncounted(wrapper);
        return watcher;
    }

    /**
     * Start XRX at the current place.
     * @param n Number of threads to spawn in the XRX pool
     * Must be called in each place exactly once.
     */
    public static def start(n:Int):void {
        processStartNanos_(System.nanoTime());
        pool(n+1n);
    }

    public static def start():void {
        start(NTHREADS);
    }
    
    /**
     * Wait for XRX to terminate at the current place.
     * Block calling thread.
     * XRX termination should be requested first by calling terminate().
     * Rethrow MultipleException collected by XRX if any.
     * join() at place 0 will rethrow the MultipleException collected by the root finish if any.
     * May be called multiple times (idempotent).
     */
    public static def join():void {
        pool.watcher.await();
    }

    /**
     * Start XRX, run single job at place 0 (possibly distributed) under an implicit root finish, and terminate XRX.
     * @param job Main job to execute at place 0 (argument is ignored in other places)
     * Helper method for the X10 compiler. Not used in XRX library mode.
     * Must be called in each place exactly once.
     * Calling thread is added to the XRX pool for the duration of the execution (blocking call).
     * NTHREADS-1n are added to the pool for the duration of the execution.
     * join() should be called in each place after this call.
     * join() at place 0 will rethrow the MultipleException collected by the root finish if any.
     */
    public static def start(job:()=>void):void {
        start(NTHREADS-1n);
        if (hereLong() == 0) {
            val wrapper = ()=>{ try { finish async job(); } catch (t:Exception) { pool.watcher.raise(t); } finally { terminateAll(); } };
            submitUncounted(wrapper);
        }
        pool.run();
    }

    /**
     * Request XRX to release its resources (TODO) and threads at the current place.
     * Should be called in each place.
     * No job should be submitted to XRX past this call.
     * May be called multiple times (idempotent).
     * join() should be called in each place after this call.
     */
    public static def terminate() {
        pool.latch.release();
    }

    /**
     * Request XRX to release its resources (TODO) and threads in every place.
     * Must be called from place 0 (TODO: lift place-0 restriction).
     * Must be submitted to XRX (not called directly) possibly as the last step of a longer job.
     * No job should be submitted to XRX past this call.
     */
    public static def terminateAll() {
        val numPlaces = Place.numPlaces();
        if (numPlaces >= 1024) {
            val cl1 = ()=> @x10.compiler.RemoteInvocation("start_1") {
                val h = hereInt();
                val cl = ()=> @x10.compiler.RemoteInvocation("start_2") {terminate();};
                for (var j:Int=Math.max(1n, h-31n); j<h; ++j) {
                    x10rtSendMessage(j, cl, null);
                }
                terminate();
            };
            for(var i:Long=numPlaces-1; i>0; i-=32) {
                x10rtSendMessage(i, cl1, null);
            }
        } else {
            val cl = ()=> @x10.compiler.RemoteInvocation("start_3") {terminate();};
            for (var i:Long=numPlaces-1; i>0; --i) {
                x10rtSendMessage(i, cl, null);
            }
        }
        terminate();
    }

    public static def terminateAllJob() {
        submitUncounted(()=>{terminateAll();});
    }

    static def cancelWave() {
        val epoch = epoch() + 1;
        pool.flush(epoch);
        activity().epoch = epoch; // back to the future
        // touch every place with the current epoch
        finish for (p in Place.places()) {
            try {
                at(p) async {}
            } catch (DeadPlaceException) {}
        }
    }

    /*
     * Cancel all jobs.
     * 
     * Cancellation might fail, i.e., waiting on the watcher may throw an exception
     * if places are lost during cancellation or due to multiple concurrent cancellation calls.
     * 
     * If a place is added while cancellation is in progress, the runtime may end up in an incosistent
     * state were the added place does not belong to the same epoch as the existing places.
     */
    public static def cancelAll():Watcher {
        val watcher = new Watcher();
        pool.cancelWatcher = watcher;
        val wrapper = ()=>{ try { cancelWave(); } catch (t:Exception) { watcher.raise(t); } finally { watcher.release(); } };
        submitUncounted(wrapper);
        return watcher;
    }

    // asyncat, async, at statement, and at expression implementation
    // at is implemented using asyncat
    // asyncat and at must make a copy of the closure parameter (local or remote)
    // asyncat at and at should dealloc the closure parameter
    // async must not copy or dealloc the closure parameter
    
    /**
     * Run asyncat
     */
    public static def runAsync(place:Place, clocks:Rail[Clock], body:()=>void, prof:Profile):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val epoch = a.epoch;
        val state = a.finishState();
        val clockPhases = a.clockPhases().make(clocks);
        if (place.id == hereLong()) {
            // Synchronous serialization
	    val start = prof != null ? System.nanoTime() : 0;
            val ser = new Serializer();
            ser.writeAny(body);
            if (prof != null) {
                val end = System.nanoTime();
                prof.serializationNanos += (end-start);
                prof.bytes += ser.dataBytesWritten();
            }

            // Spawn asynchronous activity
            state.notifySubActivitySpawn(place);
            val asyncBody = ()=>{
                val deser = new Deserializer(ser);
                val bodyCopy = deser.readAny() as ()=>void;
                bodyCopy();
            };
            executeLocal(new Activity(epoch, asyncBody, here, state, clockPhases));
        } else {
            val src = here;
            val closure = ()=> @x10.compiler.RemoteInvocation("runAsync") { pushActivity(new Activity(epoch, body, src, state, clockPhases)); };
            val preSendAction = ()=> { state.notifySubActivitySpawn(place); };
            x10rtSendMessage(place.id, closure, prof, preSendAction);
            Unsafe.dealloc(closure);
        }
        Unsafe.dealloc(body);
    }
    
    public static def runAsync(place:Place, body:()=>void, prof:Profile):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val epoch = a.epoch;
        val state = a.finishState();
        if (place.id == hereLong()) {
            // Synchronous serialization
	    val start = prof != null ? System.nanoTime() : 0;
            val ser = new Serializer();
            ser.writeAny(body);
            if (prof != null) {
                val end = System.nanoTime();
                prof.serializationNanos += (end-start);
                prof.bytes += ser.dataBytesWritten();
            }

            // Spawn asynchronous activity
            state.notifySubActivitySpawn(place);
            val asyncBody = ()=>{
                val deser = new Deserializer(ser);
                val bodyCopy = deser.readAny() as ()=>void;
                bodyCopy();
            };
            executeLocal(new Activity(epoch, asyncBody, here, state));
        } else {
            val preSendAction = ()=>{ state.notifySubActivitySpawn(place); };
            x10rtSendAsync(place.id, body, state, prof, preSendAction); // optimized case
        }
        Unsafe.dealloc(body);
    }
    
    /**
     * Run async
     */
    public static def runAsync(clocks:Rail[Clock], body:()=>void):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val epoch = a.epoch;
        val state = a.finishState();
        val clockPhases = a.clockPhases().make(clocks);
        state.notifySubActivitySpawn(here);
        executeLocal(new Activity(epoch, body, here, state, clockPhases));
    }

    public static def runAsync(body:()=>void):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val epoch = a.epoch;
        val state = a.finishState();
        state.notifySubActivitySpawn(here);
        executeLocal(new Activity(epoch, body, here, state));
    }

	public static def runFinish(body:()=>void):void {
	    finish body();
	}

    /**
     * Run @Uncounted asyncat
     */
    public static def runUncountedAsync(place:Place, body:()=>void, prof:Profile):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val epoch = a.epoch;
        if (place.id == hereLong()) {
            // Synchronous serialization
	    val start = prof != null ? System.nanoTime() : 0;
            val ser = new Serializer();
            ser.writeAny(body);
            if (prof != null) {
                val end = System.nanoTime();
                prof.serializationNanos += (end-start);
                prof.bytes += ser.dataBytesWritten();
            }

            // Spawn asynchronous activity
            val asyncBody = ()=>{
                val deser = new Deserializer(ser);
                val bodyCopy = deser.readAny() as ()=>void;
                bodyCopy();
            };
            executeLocal(new Activity(epoch, asyncBody, here, FinishState.UNCOUNTED_FINISH));
        } else {
            // [DC] passing FIRST_PLACE instead of the correct src, since UNCOUNTED_FINISH does not use this value
            // and it saves sending some bytes over the network
            val closure = ()=> @x10.compiler.RemoteInvocation("runUncountedAsync") { pushActivity(new Activity(epoch, body, Place.FIRST_PLACE, FinishState.UNCOUNTED_FINISH)); };
            x10rtSendMessage(place.id, closure, prof);
            Unsafe.dealloc(closure);
        }
        Unsafe.dealloc(body);
    }

    /**
     * Run @Uncounted async
     */
    public static def runUncountedAsync(body:()=>void):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();

        val epoch = a.epoch;
        executeLocal(new Activity(epoch, body, here, new FinishState.UncountedFinish()));
    }

    /**
     * a latch with a place for an exception
     */
    static class RemoteControl extends SimpleLatch implements Mortal {
        public def this() { super(); }
        var e:CheckedThrowable = null;
        var clockPhases:Activity.ClockPhases = null;
    }

    /** Subvert X10 and target language exception checking.
     */
    @Native("c++", "::x10aux::throwException(::x10aux::nullCheck(#e))")
    @Native("java", "java.lang.Thread.currentThread().stop(#e)")
    static native def throwCheckedWithoutThrows (e:CheckedThrowable) : void;

    /**
     * Transparently wrap checked exceptions at the root of an at desugared closure, and unpack later.
     */
    static class AtCheckedWrapper extends Exception {
        public def this(cause:CheckedThrowable) { super(cause); }
    }

    /**
      * Used in codegen at the root of an at closure, upon catching something that is not below Exception or Error.
      * Has a return type to avoid post compile errors.  The function never returns but it may still
      * be called like this: return wrapAtChecked[Int](e).  That will satisfy the checking for return
      * statements in the calling function.
      */
    public static def wrapAtChecked[T] (caught:CheckedThrowable) : T {
        // Only wrap if necessary
        if (caught instanceof Exception) throw caught as Exception;
        if (caught instanceof Error) throw caught as Error;
        throw new AtCheckedWrapper(caught);
    }
 
    /**
     * When a checked exception can escape an 'at', we inject the exception into the calling activity using
     * a sleazy trick that the javac exception checker does not know about.  This call forces it to believe
     * that a given exception may be raised by the at.
     */
    //public static def pretendToThrow[T] () { T<: CheckedThrowable } : void throws T { }
    // work-around for XTENLANG-3086 is in CheckedThrowable.x10

    /** Run an at statement in non-resilient X10 by using a local latch. */
    public static def runAtNonResilient(place:Place, body:()=>void, prof:Profile):void {
        Runtime.ensureNotInAtomic();
        if (place.id == hereLong()) {
            try {
                try {
                    deepCopy(body, prof)();
                    return;
                } catch (t:AtCheckedWrapper) {
                    throw t.getCheckedCause();
                }
            } catch (t:CheckedThrowable) {
                throwCheckedWithoutThrows(deepCopy(t, null));
            }
        }
        @StackAllocate val me = @StackAllocate new RemoteControl();
        val box:GlobalRef[RemoteControl] = GlobalRef(me as RemoteControl);
        val clockPhases = activity().clockPhases;
        @StackAllocate val ser = @StackAllocate new x10.io.Serializer();
        ser.writeAny(body);
        val bytes = ser.toRail();
        @x10.compiler.Profile(prof) at(place) async {
            activity().clockPhases = clockPhases;
            try {
                try {
                    // We use manual deserialization to get correct handling of exceptions
                    @StackAllocate val deser = @StackAllocate new x10.io.Deserializer(bytes);
                    val bodyPrime = deser.readAny() as ()=>void;
                    bodyPrime();
                    val closure = ()=> @x10.compiler.RemoteInvocation("runAt_1") { 
                        val me2 = (box as GlobalRef[RemoteControl]{home==here})();
                        me2.clockPhases = clockPhases;
                        me2.release();
                    };
                    x10rtSendMessage(box.home.id, closure, null);
                    Unsafe.dealloc(closure);
                } catch (e:AtCheckedWrapper) {
                    throw e.getCheckedCause();
                }
            } catch (e:CheckedThrowable) {
                val closure = ()=> @x10.compiler.RemoteInvocation("runAt_2") { 
                    val me2 = (box as GlobalRef[RemoteControl]{home==here})();
                    me2.e = e;
                    me2.clockPhases = clockPhases;
                    me2.release();
                };
                x10rtSendMessage(box.home.id, closure, null);
                Unsafe.dealloc(closure);
            }
            activity().clockPhases = null;
        }
        me.await();
        Unsafe.dealloc(body);
        Unsafe.dealloc(bytes);
        activity().clockPhases = me.clockPhases;
        if (null != me.e) {
            throwCheckedWithoutThrows(me.e);
        }
    }

    /**
     * Run at statement
     */
    public static def runAt(place:Place, body:()=>void, prof:Profile):void {
        activity().finishState().runAt(place, body, prof);
    }

    /*
     * [GlobalGC] Special version of runAt, which does not use activity, clock, exceptions
     *            Used in GlobalRef.java to implement changeRemoteCount
     * [DC] do not allow profiling of this call: seems it is not for application use?
     */
    public static def runAtSimple(place:Place, body:()=>void, toWait:Boolean):void {
        //Console.ERR.println("Runtime.runAtSimple: place=" + place + " toWait=" + toWait);
        if (place.id == hereLong()) {
                deepCopy(body, null)(); // deepCopy and apply
                return;
        }
      if (toWait) { // synchronous exec
        @StackAllocate val me = @StackAllocate new RemoteControl();
        val box:GlobalRef[RemoteControl] = GlobalRef(me as RemoteControl);
        val latchedBody = () => @x10.compiler.RemoteInvocation("runAtSimple_1") {
                body();
                val closure = ()=> @x10.compiler.RemoteInvocation("runAtSimple_2") { 
                    val me2 = (box as GlobalRef[RemoteControl]{home==here})();
                    me2.release();
                };
                x10rtSendMessageInternal(box.home.id, closure, null, null);
                Unsafe.dealloc(closure);
            };
        x10rtSendMessageInternal(place.id, latchedBody, null, null);
        Unsafe.dealloc(latchedBody);
        me.await(); // wait until body is executed at remote place
      } else { // asynchronous exec
        val simpleBody = () => @x10.compiler.RemoteInvocation("runAtSimple_3") { body(); };
        x10rtSendMessageInternal(place.id, simpleBody, null, null);
        Unsafe.dealloc(simpleBody);
        // *not* wait until body is executed at remote place
      }
        Unsafe.dealloc(body);
    }

    /**
     * a latch with a place for an exception and return value
     */
    static class Remote[T] extends RemoteControl {
        public def this() { super(); }
        var t:Box[T] = null;
    }

    /** A class that is used to receive profiling information during various runtime communication constructs.
     * These counters are incremented for each information, so the same profile object can be used in multiple
     * operations to calculate a total cost.  Otherwise, call reset between operations.
     *
     * @see x10.compiler.Profile
     */
    public static class Profile {
        /** Number of bytes that were serialized. */
        public var bytes:Long;
        /** Time spent serializing. */
        public var serializationNanos:Long;
        /** Time spent sending the message (does not include time spent waiting for the completion notification). */
        public var communicationNanos:Long;
        /** Set counters to zero. */
        public def reset() { bytes = 0; serializationNanos = 0; communicationNanos = 0; }
    }

    /**
     * Eval at expression
     */
    public static def evalAtNonResilient(place:Place, eval:()=>Any, prof:Profile):Any {
        Runtime.ensureNotInAtomic();
        if (place.id == hereLong()) {
            try {
                try {
                    val result = deepCopy(eval,prof)();
                    return deepCopy(result,prof);
                } catch (t:AtCheckedWrapper) {
                    throw t.getCheckedCause();
                }
            } catch (t:CheckedThrowable) {
                throwCheckedWithoutThrows(deepCopy(t, null));
            }
        }
        @StackAllocate val me = @StackAllocate new Remote[Any]();
        val box = GlobalRef(me as Remote[Any]);
        val clockPhases = activity().clockPhases;
        @StackAllocate val ser = @StackAllocate new x10.io.Serializer();
        ser.writeAny(eval);
        val bytes = ser.toRail();
        @x10.compiler.Profile(prof) at(place) async {
            activity().clockPhases = clockPhases;
            try {
                try {
                    // We use manual deserialization to get correct handling of exceptions
                    @StackAllocate val deser = @StackAllocate new x10.io.Deserializer(bytes);
                    val evalPrime = deser.readAny() as ()=>Any;
                    val result = evalPrime();
                    @StackAllocate val ser2 = @StackAllocate new x10.io.Serializer();
                    ser2.writeAny(result);
                    val bytes2 = ser2.toRail();
                    val closure = ()=> @x10.compiler.RemoteInvocation("evalAt_1") { 
                        val me2 = (box as GlobalRef[Remote[Any]]{home==here})();
                        // me2 has type Box[T{box.home==here}]... weird
                        me2.clockPhases = clockPhases;
                        @StackAllocate val deser2 = @StackAllocate new x10.io.Deserializer(bytes2);
                        try {
                            val resultPrime = deser2.readAny();
                            me2.t = new Box[Any](resultPrime as Any);
                        } catch (e:CheckedThrowable) {
                            me2.e = e;
                        }
                        me2.release();
                    };
                    x10rtSendMessage(box.home.id, closure, null);
                    Unsafe.dealloc(closure);
                } catch (t:AtCheckedWrapper) {
                    throw t.getCheckedCause();
                }
            } catch (e:CheckedThrowable) {
                val closure = ()=> @x10.compiler.RemoteInvocation("evalAt_2") { 
                    val me2 = (box as GlobalRef[Remote[Any]]{home==here})();
                    me2.e = e;
                    me2.clockPhases = clockPhases;
                    me2.release();
                };
                x10rtSendMessage(box.home.id, closure, null);
                Unsafe.dealloc(closure);
            }
            activity().clockPhases = null;
        }
        me.await();
        Unsafe.dealloc(eval);
        activity().clockPhases = me.clockPhases;
        if (null != me.e) {
            throwCheckedWithoutThrows(me.e);
        }
        return me.t.value;
    }

    /**
     * Eval at expression
     */
    public static def evalAt[T](place:Place, body:()=>T, prof:Profile):T {
        val body2 = ()=>(body() as Any);
        val r = activity().finishState().evalAt(place, body, prof) as T;
        Unsafe.dealloc(body2); // optimisation since we always run with gc, no need to put it in a finally
        return r;
    }

    // initialization of static fields in c++ backend

    public static def StaticInitBroadcastDispatcherLock() {
        staticMonitor.lock();
    }

    public static def StaticInitBroadcastDispatcherAwait() {
        staticMonitor.await();
    }

    public static def StaticInitBroadcastDispatcherUnlock() {
        staticMonitor.unlock();
    }

    public static def StaticInitBroadcastDispatcherNotify() {
        staticMonitor.release();
    }

    // atomic and when

    public static def enterAtomic() {
        atomicMonitor.lock();
        val a = activity();
        if (a != null)
           a.pushAtomic();
    }

    public static def ensureNotInAtomic() {
        val a = activity();
        if (a != null)
           a.ensureNotInAtomic();
    }

    public static def exitAtomic() {
        val a = activity();
        if (a != null)
           a.popAtomic();
        if (null != pool.wsBlockedContinuations) wsUnblock();
        atomicMonitor.release();
    }

    public static def exitWSWhen(b:Boolean) {
        val a = activity();
        if (a != null)
           a.popAtomic();
        if (b) {
            wsUnblock();
            atomicMonitor.release();
        } else {
            atomicMonitor.unlock();
        }
    }

    public static def awaitAtomic():void {
        atomicMonitor.await();
    }

    // finish
    static def makeDefaultFinish():FinishState {
        if (RESILIENT_MODE==Configuration.RESILIENT_MODE_NONE || RESILIENT_MODE==Configuration.RESILIENT_MODE_X10RT_ONLY) {
            return new FinishState.Finish();
        } else {
            return FinishResilient.make(null/*parent*/, null/*latch*/);
        }
    }

    static def notifyPlaceDeath() : void {
        if (CANCELLABLE) {
            if (pool.cancelWatcher != null) {
                pool.cancelWatcher.raise(new DeadPlaceException());
                pool.cancelWatcher.release();
            }
        }
        if (RESILIENT_MODE == Configuration.RESILIENT_MODE_NONE) {
            // This case seems occur naturally on shutdown, so transparently ignore it.
            // The launcher is responsible for tear-down in the case of place death, nothing we need to do.
        } else if (RESILIENT_MODE == Configuration.RESILIENT_MODE_X10RT_ONLY) {
            // Nothing to do at the XRX level in this mode.
        } else {
            FinishResilient.notifyPlaceDeath();
        }
    }

    /**
     * Start executing current activity synchronously
     * (i.e. within a finish statement).
     */
    public static def startFinish():FinishState {
        return activity().swapFinish(makeDefaultFinish());
    }

    public static def startFinish(pragma:Int):FinishState {
        val f:FinishState;
        switch (pragma) {
        case Pragma.FINISH_ASYNC:
            f = new FinishState.FinishAsync(); break;
        case Pragma.FINISH_HERE:
            f = new FinishState.FinishHere(); break;
        case Pragma.FINISH_SPMD:
            f = new FinishState.FinishSPMD(); break;
        case Pragma.FINISH_LOCAL:
            f = new FinishState.LocalFinish(); break;
        case Pragma.FINISH_DENSE:
            f = new FinishState.DenseFinish(); break;
        default: 
            f = makeDefaultFinish();
        }
        return activity().swapFinish(f);
    }

    public static def startLocalFinish():FinishState {
        return activity().swapFinish(new FinishState.LocalFinish());
    }

    public static def startSimpleFinish():FinishState {
        return activity().swapFinish(new FinishState.Finish());
    }

    /**
     * Suspend until all activities spawned during this finish
     * operation have terminated. Throw an exception if any
     * async terminated abruptly. Otherwise continue normally.
     * Should only be called by the thread executing the current activity.
     */
    public static def stopFinish(f:FinishState):void {
        val a = activity();
        val finishState = a.swapFinish(f);
        finishState.waitForFinish();
        Unsafe.dealloc(finishState);
    }

    /**
     * Push the exception thrown while executing s in a finish s,
     * onto the finish state.
     */
    public static def pushException(t:CheckedThrowable):void  {
    	activity().finishState().pushException(t);
    }
    public static def startCollectingFinish[T](r:Reducible[T]) {
        return activity().swapFinish(new FinishState.CollectingFinish[T](r));
    }

    public static def makeOffer[T](t:T) {
        val state = activity().finishState();
//      Console.OUT.println("Place(" + here.id + ") Runtime.makeOffer: received " + t);
        (state as FinishState.CollectingFinish[T]).accept(t,workerId()); //Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
    }

    public static def stopCollectingFinish[T](f:FinishState):T {
        val state = activity().swapFinish(f);
        return (state as FinishState.CollectingFinish[T]).waitForFinishExpr();
    }

    // submit an activity to the pool
    static def pushActivity(activity:Activity):void {
        worker().push(activity);
    }

    static def executeLocal(activity:Activity):void {
        if (activity().epoch < epoch()) throw new DeadPlaceException("Cancelled");
        dealOrPush(activity);
    }

    static def dealOrPush(activity:Activity):void {
        if (!pool.deal(activity)) pushActivity(activity);
    }

    // TODO: remove this once all backends and x10rt impls have epochs
    public static def execute(body:()=>void, src:Place, finishState:FinishState):void {
        pushActivity(new Activity(42, body, src, finishState));
    }

    public static def execute(epoch:Long, body:()=>void, src:Place, finishState:FinishState):void {
        if (epoch > epoch()) {
            pool.flush(epoch);
        }
        if (epoch == epoch()) pushActivity(new Activity(epoch, body, src, finishState));
    }

    public static def probe() {
        worker().probe();
    }

    // notify the pool a worker is about to execute a blocking operation
    public static def increaseParallelism():void {
        if (!STATIC_THREADS) {
            pool.increase();
        }
    }

    // notify the pool a worker resumed execution after a blocking operation
    public static def decreaseParallelism(n:Int) {
        if (!STATIC_THREADS) {
            pool.decrease(n);
        }
    }

    public static def removeThreadLocalContexts() {
        pool.removeThreadLocalContexts();
    }
    
    public static def wrapNativeThread():Worker {
        return pool.wrapNativeThread();
    }

    public static def epoch() = pool.workers.epoch;
}

// vim:shiftwidth=4:tabstop=4:expandtab
