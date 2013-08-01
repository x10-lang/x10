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

import x10.compiler.Native;
import x10.compiler.Inline;
import x10.compiler.Pragma;
import x10.compiler.StackAllocate;
import x10.compiler.NativeCPPInclude;

import x10.io.Unserializable;
import x10.io.Reader;
import x10.io.Writer;

import x10.util.Random;
import x10.util.Box;
import x10.util.HashSet;
import x10.util.ArrayList;

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
    @Native("c++", "x10::lang::RuntimeNatives::println(x10aux::to_string(#any)->c_str())")
    public native static def println(any:Any):void;

    @Native("java", "java.lang.System.err.println()")
    @Native("c++", "x10::lang::RuntimeNatives::println(\"\")")
    public native static def println():void;

    @Native("java", "java.lang.System.err.printf(#fmt, #t)")
    @Native("c++", "x10::lang::RuntimeNatives::printf(#fmt, #t)")
    public native static def printf[T](fmt:String, t:T):void;


    /** Get a string that identifies the X10 execution environment of this place.
     * The string is undefined but should be helpful for debugging, typically containing the pid and hostname.
     * On java it is equivalent to java.lang.management.ManagementFactory.getRuntimeMXBean().getName().
     */
    @Native("java", "java.lang.management.ManagementFactory.getRuntimeMXBean().getName()")
    @Native("c++", "x10aux::runtime_name()")
    public native static def getName() : String;

    // Native runtime interface

    /**
     * Send active message to another place.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runClosureAt((int)(#id), #body, #prof)")
    @Native("c++", "x10aux::run_closure_at((x10_int)#id, #body, #prof)")
    public static native def x10rtSendMessage(id:Long, body:()=>void, prof:Profile):void;

    /**
     * Send async to another place.
     * This is a special case of x10rtSendMessage where the active message consists in
     * creating an activity at the destination place with the specified body and finish state
     * and pushing this activity onto the deque of the active worker.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runAsyncAt((int)(#id), #body, #finishState, #prof)")
    @Native("c++", "x10aux::run_async_at((x10_long)(#id), #body, #finishState, #prof)")
    public static native def x10rtSendAsync(id:Long, body:()=>void, finishState:FinishState, prof:Profile):void;

    /**
     * Complete X10RT initialization.
     */
    @Native("c++", "x10rt_registration_complete()")
    @Native("java", "x10.x10rt.X10RT.registration_complete()")
    public static native def x10rtInit():void;

    /**
     * Process one incoming active message if any (non-blocking).
     */
    @Native("c++", "x10aux::event_probe()")
    @Native("java", "x10.runtime.impl.java.Runtime.eventProbe()")
    public static native def x10rtProbe():void;

    @Native("c++", "x10aux::blocking_probe()")
    @Native("java", "x10.runtime.impl.java.Runtime.blockingProbe()")
    public static native def x10rtBlockingProbe():void;

    /**
     * Process one incoming active message if any (non-blocking).
     */
    @Native("c++", "x10aux::event_probe()")
    @Native("java", "x10.runtime.impl.java.Runtime.eventProbe()")
    public static native def wsProcessEvents():void;

    /**
     * Return a deep copy of the parameter.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.<#T$box>deepCopy(#o, #prof)")
    @Native("c++", "x10aux::deep_copy<#T >(#o, #prof)")
    public static native def deepCopy[T](o:T, prof:Profile):T;

    public static def deepCopy[T](o:T) = deepCopy[T](o, null);

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

        private val hugePages:boolean;
        private val congruent:boolean;
        
        private def this(h:boolean, c:boolean) {
            hugePages = h; 
            congruent = c;
        }

        @Native("java", "false")
        @Native("c++", "x10aux::congruent_huge")
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
        public static def requestAllocator(hugePages:boolean, congruent:boolean):MemoryAllocator {
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
        public static def requireAllocator(hugePages:boolean, congruent:boolean):MemoryAllocator {
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
    public static RESILIENT_PLACE_ZERO = Configuration.envOrElse("X10_RESILIENT_PLACE_ZERO", false);
    public static RESILIENT_ZOO_KEEPER = Configuration.envOrElse("X10_RESILIENT_ZOO_KEEPER", false);

    // External process execution

    /**
     * Executes the specified command in a separate process.
     * The returned InputStreamReader is connected to the standard output
     * of the new process.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.execForRead(#command)")
    @Native("c++", "x10::lang::RuntimeNatives::execForRead(x10aux::to_string(#command)->c_str())")
    public static native def execForRead(command:String):Reader{self!=null};

    /**
     * Executes the specified command in a separate process.
     * The returned OutputStreamWriter is connected to the standard input
     * of the new process.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.execForWrite(#command)")
    @Native("c++", "x10::lang::RuntimeNatives::execForWrite(x10aux::to_string(#command)->c_str())")
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
        val lock = new Lock(); // master lock for all thread pool adjustments

        // every x10 thread (including promoted native threads)
        val workers = new Rail[Worker](MAX_THREADS);

        // parked x10 threads (parkedCount == spareCount + idleCount)
        val parkedWorkers = new Rail[Worker](MAX_THREADS);

        var count:Int = 0; // count every x10 threads (including promoted native threads)
        var spareCount:Int = 0; // spare thread count
        var idleCount:Int = 0; // idle thread count
        var deadCount:Int = 0; // dead thread count
        var spareNeeded:Int = 0; // running threads - NTHREADS

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
            if (spareNeeded > 0) {
                spareNeeded--;
                lock.unlock();
                return 0;
            } else if (spareCount > 0) {
                // resume spare thread
                val i = --spareCount + idleCount;
                val worker = parkedWorkers(i);
                parkedWorkers(i) = null;
                lock.unlock();
                worker.unpark();
                return 0;
            } else {
                // start new thread
                val i = count++;
                lock.unlock();
                check(i);
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
            check(i);
            return i;
        }

        // check max thread count has not been reached
        def check(i:Int):void {
            if (i >= MAX_THREADS) {
                println(here+": TOO MANY THREADS");
                throw new InternalError(here+": TOO MANY THREADS");
            }
            if (WARN_ON_THREAD_CREATION) {
                println(here+": WARNING: A new OS-level thread was discovered (there are now "+i+" threads).");
            }
        }

        // convert idle threads to spare as needed
        def convert() {
            while (spareNeeded > 0 && idleCount > 0) {
                spareNeeded--;
                idleCount--;
                spareCount++;
            }
        }

        // park if spare needed -> spare thread
        def yield(worker:Worker):Activity {
            if (spareNeeded <= 0) return null;
            lock.lock();
            convert();
            if (spareNeeded <= 0) {
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
            if (BUSY_WAITING) return null;
            if (idleCount - spareNeeded >= NTHREADS - 1) return null; // better safe than sorry
            lock.lock();
            convert();
            if (idleCount >= NTHREADS - 1) {
                lock.unlock();
                return null;
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

        // deal to idle worker if any
        // return true on success
        def give(activity:Activity):Boolean {
            if (BUSY_WAITING) return false;
            if (idleCount - spareNeeded <= 0) return false;
            lock.lock();
            convert();
            if (idleCount <= 0) {
                lock.unlock();
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
            while (idleCount > 0) {
                val i = spareCount + --idleCount;
                val worker = parkedWorkers(i);
                parkedWorkers(i) = null;
                worker.unpark();
            }
            if (spareCount > 0) {
                val worker = parkedWorkers(--spareCount);
                parkedWorkers(spareCount) = null;
                worker.unpark();
            }
            lock.unlock();
        }

        public operator this(i:Int) = workers(i);
        public operator this(i:Int)=(worker:Worker) { workers(i) = worker; }
    }

    public final static class Worker extends Thread implements Unserializable {
        // bound on loop iterations to help j9 jit
        private static BOUND = 100;

        // activity (about to be) executed by this worker
        var activity:Activity = null;

        // pending activities
        private val queue = new Deque();

        // random number generator for this worker
        private val random:Random;

        //Worker Id for CollectingFinish
        val workerId:Int;

        //Used for 1:1 mapping between WorkStealing Worker and X10 Worker (Temp Soltuion)
        val wsfifo = new Deque();

        def this(workerId:Int) {
            super("thread-" + workerId);
            this.workerId = workerId;
            random = new Random(workerId + (workerId << 8) + (workerId << 16) + (workerId << 24));
        }

        def this(workerId:Int, dummy:Boolean) {
            super();
            this.workerId = workerId;
            random = new Random(workerId + (workerId << 8) + (workerId << 16) + (workerId << 24));
            // [DC] Using 'here' as the srcPlace for the new activity causes a cycle:  The managed X10
            // implementation of 'here' uses thread-local storage, and this can create a cycle in the case
            // where access of thread-local storage occurs from a native java thread and triggers the creation
            // of a new Worker.
            // Using Place(0) is OK because the Uncounted finish passed into the activity does not use srcPlace.
            activity = new Activity(()=>{}, Place(0), FinishState.UNCOUNTED_FINISH);
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
            for (var i:Int = 0; i < BOUND; i++) {
                activity = poll();
                if (activity == null) {
                    activity = pool.scan(random, this);
                    if (activity == null) return false; // [DC] only happens when pool's latch is released
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
                if (activity == null) {
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
            while (loop2(latch));
            activity = tmp; // restore current activity
        }

        // inner loop to help j9 jit
        private def loop2(latch:SimpleLatch):Boolean {
            for (var i:Int = 0; i < BOUND; i++) {
                if (latch()) return false;
                activity = poll();
                if (activity == null) return false;
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
        public def unpark() {
            if (!STATIC_THREADS) {
                super.unpark();
            }
        }
    }

    static class SuspendedWhen {
       var cond: () => Boolean;
       var act: Activity;
       def this(cond: () => Boolean, act: Activity) {
          this.cond = cond;
          this.act = act;
       }
    }
        
    static class Pool {
        val latch = new SimpleLatch();
        
        var wsEnd:Boolean = false;

        private val workers = new Workers();
        
        var wsBlockedContinuations:Deque = null;
        
        //var suspendedWhens = new Deque();
        var suspendedWhens1: HashSet[SuspendedWhen] = new HashSet[SuspendedWhen]();
        var suspendedWhens2: HashSet[SuspendedWhen] = new HashSet[SuspendedWhen]();

        var numDead : Long = 0;

        operator this(n:Int):void {
            workers.count = n;
            workers(0) = worker();
            for (var i:Int = 1; i<n; i++) {
                workers(i) = new Worker(i);
            }
            for (var i:Int = 1; i<n; i++) {
                workers(i).start();
            }
            workers(0)();
            while (workers.count > workers.deadCount) Worker.park();
        }

        // notify the pool a worker is about to execute a blocking operation
        def increase():void {
            val i = workers.increase();
            if (i > 0) {
                // if no spare thread is available allocate and start a new thread
                val worker = new Worker(i);
                workers(i) = worker;
                worker.start();
            }
        }

        // create pseudo X10 worker for native thread
        public def wrapNativeThread():Worker {
            val i = workers.promote();
            val worker = new Worker(i, false);
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
            if (workers.count == workers.deadCount) workers(0).unpark();
        }

        def checkSuspendedWhens() {
           enterAtomic();
           if (pool.suspendedWhens1.isEmpty()) {
              exitAtomicOnly();
              return;
           }
           val change = checkSuspendedWhens1();
           if (change)
              checkSuspendedWhens2();
           exitAtomicOnly();
        }

        // scan workers and network for pending activities
        def scan(random:Random, worker:Worker):Activity {
            var activity:Activity = null;
            var next:Int = random.nextInt(workers.count);
            var i:Int = 2;
            for (;;) {
                if (null != activity || latch()) return activity;
                // go to sleep if too many threads are running
                activity = workers.yield(worker);
                if (null != activity || latch()) return activity;

                checkSuspendedWhens();

                // try network
                x10rtProbe();
                if (Place.numDead() != numDead) {
                    // atomic wakes up ResilientFinish...
                    atomic {
                        numDead = Place.numDead();
                        //Runtime.println("Number of dead places now "+numDead);
                        //for (p in Place.places()) {
                        //    if (p.isDead()) Runtime.println("Dead: "+p);
                        //}
                    }
                    // release the pool
                    if (here.id == 0l) Runtime.rootFinish.notifyPlaceDeath();
                }
                activity = worker.poll();
                if (null != activity || latch()) return activity;
                // try random worker
                if (next < MAX_THREADS && null != workers(next)) { // avoid race with increase method
                    activity = workers(next).steal();
                }
                if (++next == workers.count) next = 0;
                if (i-- == 0) {
                    if (null != activity || latch()) return activity;
                    activity = workers.take(worker);
                    i = 2;
                }
            }
        }

        def size() = workers.count;
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
    static def activity():Activity = worker().activity();

    /**
     * Return the current place
     */
    @Native("c++", "x10::lang::Place::_make(x10aux::here)")
    public static def home():Place = Thread.currentThread().home();

    /**
     * Return the id of the current place
     * @Deprecated("Use hereLong()")
     */
    @Native("c++", "x10aux::here")
    public static def hereInt():int = here.id as Int;

    @Native("c++", "((x10_long)x10aux::here)")
    public static def hereLong():Long = here.id;


    /**
     * The amount of unscheduled activities currently available to this worker thread.
     * Intended for use in heuristics that control async spawning
     * based on the current amount of surplus work.
     */
    public static def surplusActivityCount():int = worker().size();

    /** The finish state that manages the 'main' activity and sub activities. */
    private static rootFinish = makeDefaultFinish(pool.latch);

    /**
     * Run main activity in a finish.
     * @param init Static initializers
     * @param body Main activity
     */
    public static def start(body:()=>void):void {
        // initialize thread pool for the current process
        // initialize runtime
        x10rtInit();

        if (hereInt() == 0) {
            // [DC] at this point: rootFinish has an implicit notifySubActivitySpawn and notifyActivityBegin
            // (due to constructor initialising counters appropriately)
            // do not need to alter rootFinish in activity constructor
            executeLocal(new Activity(body, here, rootFinish, false));

            // [DC] during call to pool(NTHREADS), queued activity runs, and eventually calls rootFinish.notifyActivityTermination
            // this ultimately triggers the return of pool(NTHREADS)

            // [DC] therefore, this call blocks until body has finished executing
            pool(NTHREADS);

            // [DC] at this point, rootFinish has quiescent (counters are zero)

            // we need to call waitForFinish here to see the exceptions thrown by main if any
            try {
                rootFinish.waitForFinish();
                // [DC] finish counters may now be negative due to implicit call to notifyActivityTermination inside waitForFinish
            } finally {
                // root finish has terminated, kill remote processes if any
                if (Place.MAX_PLACES >= 1024) {
                    val cl1 = ()=> @x10.compiler.RemoteInvocation("start_1") {
                        val h = hereInt();
                        val cl = ()=> @x10.compiler.RemoteInvocation("start_2") {pool.latch.release();};
                        for (var j:Int=Math.max(1, h-31); j<h; ++j) {
                            x10rtSendMessage(j, cl, null);
                        }
                        pool.latch.release();
                    };
                    for(var i:Long=Place.MAX_PLACES-1L; i>0L; i-=32L) {
                        x10rtSendMessage(i, cl1, null);
                    }
                } else {
                    val cl = ()=> @x10.compiler.RemoteInvocation("start_3") {pool.latch.release();};
                    for (var i:Long=Place.MAX_PLACES-1L; i>0L; --i) {
                        x10rtSendMessage(i, cl, null);
                    }
                }
            }
        } else {
            // wait for thread pool to die
            // (happens when a kill signal is received from place 0)
            pool(NTHREADS);
        }
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
        
        val state = a.finishState();
        val clockPhases = a.clockPhases().make(clocks);
        state.notifySubActivitySpawn(place);
        if (place.id == hereLong()) {
            executeLocal(new Activity(deepCopy(body, prof), here, state, clockPhases));
        } else {
            val src = here;
            val closure = ()=> @x10.compiler.RemoteInvocation("runAsync") { execute(new Activity(body, src, state, clockPhases)); };
            x10rtSendMessage(place.id, closure, prof);
            Unsafe.dealloc(closure);
        }
        Unsafe.dealloc(body);
    }
    
    public static def runAsync(place:Place, body:()=>void, prof:Profile):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val state = a.finishState();
        state.notifySubActivitySpawn(place);
        if (place.id == hereLong()) {
            executeLocal(new Activity(deepCopy(body, prof), here, state));
        } else {
            x10rtSendAsync(place.id, body, state, prof); // optimized case
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
        
        val state = a.finishState();
        val clockPhases = a.clockPhases().make(clocks);
        state.notifySubActivitySpawn(here);
        executeLocal(new Activity(body, here, state, clockPhases));
    }

    public static def runAsync(body:()=>void):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val state = a.finishState();
        state.notifySubActivitySpawn(here);
        executeLocal(new Activity(body, here, state));
    }

    public static def runAsyncWhen(condition:()=>Boolean, body:()=>void) {

      val thisAct = initAsync(body);
      val suspendedWhen = new SuspendedWhen(condition, thisAct);

      enterAtomic();
      val c = condition();
      if (c)
         pool.suspendedWhens1.add(suspendedWhen);
      else
         pool.suspendedWhens2.add(suspendedWhen);
      exitAtomicOnly();

//      val thisAtomicAct = new Activity(()=> { atomic body(); }, here, state);
//      executeLocal(thisAtomicAct);
    }

    public static def initAsync(block: ()=>void): Activity {
    	  // The usual initial steps:
      val a = activity();
      a.ensureNotInAtomic();

      val state = a.finishState();
      state.notifySubActivitySpawn(here);

      return new Activity(block, here, state);
    }


    //public static def runAsyncWait(
    	// futures: ArrayList[Future[Any]],
    	// block: ()=>void) {
    	//   return FTask.asyncWait(futures, block);
    //}

    //public static def runAsyncWait(
    	// future: Future[Any],
    	// block: ()=>void) {
    	//   return FTask.asyncWait(future, block);
    //}
    
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
        
        if (place.id == hereLong()) {
            executeLocal(new Activity(deepCopy(body, prof), here, FinishState.UNCOUNTED_FINISH));
        } else {
            // [DC] passing FIRST_PLACE instead of the correct src, since UNCOUNTED_FINISH does not use this value
            // and it saves sending some bytes over the network
            val closure = ()=> @x10.compiler.RemoteInvocation("runUncountedAsync") { execute(new Activity(body, Place.FIRST_PLACE, FinishState.UNCOUNTED_FINISH)); };
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
        
        executeLocal(new Activity(body, here, new FinishState.UncountedFinish()));
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
    @Native("c++", "x10aux::throwException(x10aux::nullCheck(#e))")
    @Native("java", "java.lang.Thread.currentThread().stop(#e)")
    static native def throwCheckedWithoutThrows (e:CheckedThrowable) : void;

    /**
     * Transparently wrap checked exceptions at the root of an at desugared closure, and unpack later.
     */
    static class AtCheckedWrapper extends Exception {
        public def this(cause: CheckedThrowable) { super(cause); }
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
        @x10.compiler.Profile(prof) at(place) async {
            activity().clockPhases = clockPhases;
            try {
                try {
                    body();
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
                x10rtSendMessage(box.home.id, closure, null);
                Unsafe.dealloc(closure);
            };
        x10rtSendMessage(place.id, latchedBody, null);
        Unsafe.dealloc(latchedBody);
        me.await(); // wait until body is executed at remote place
      } else { // asynchronous exec
        val simpleBody = () => @x10.compiler.RemoteInvocation("runAtSimple_3") { body(); };
        x10rtSendMessage(place.id, simpleBody, null);
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
                    // TODO the second deep copy is needed only if eval makes its result escaped (it is very rare).
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
        @x10.compiler.Profile(prof) at(place) async {
            activity().clockPhases = clockPhases;
            try {
                try {
                    val result = eval();
                    val closure = ()=> @x10.compiler.RemoteInvocation("evalAt_1") { 
                        val me2 = (box as GlobalRef[Remote[Any]]{home==here})();
                        // me2 has type Box[T{box.home==here}]... weird
                        me2.t = new Box[Any](result as Any);
                        me2.clockPhases = clockPhases;
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

     public static def exitAtomicOnly() {
        val a = activity();
        if (a != null)
           a.popAtomic();
        if (null != pool.wsBlockedContinuations) wsUnblock();
        atomicMonitor.release();
     }

    public static def exitAtomic() {
        val a = activity();
        if (a != null)
           a.popAtomic();
        if (null != pool.wsBlockedContinuations) wsUnblock();
        
        // ------------------------
        // Recheck suspended whens
        checkSuspendedWhens2();
        // Keep the atomic block lock so that 
        // the evaluation of conditions and 
        // the execution of bodies happen atomically. 
        atomicMonitor.release();
    }

    public static def checkSuspendedWhens1(): Boolean {
        var change: Boolean = false;
        val newSet = new HashSet[SuspendedWhen]();
        val iter = pool.suspendedWhens1.iterator();
        while (iter.hasNext()) {
           val sw = iter.next();
           if (sw.cond()) {
              val act = sw.act;
              act.run();
              Unsafe.dealloc(act);
              change = true;
           } else
             newSet.add(sw);
        }
        pool.suspendedWhens1 = newSet;
        return change;
    }

    public static def checkSuspendedWhens2() {
        val newSet = new HashSet[SuspendedWhen]();
        val iter = pool.suspendedWhens2.iterator();
        while (iter.hasNext()) {
           val sw = iter.next();
           if (sw.cond()) {
              pool.suspendedWhens1.add(sw);
//              val act = sw.act;
//              act.run();
//              Unsafe.dealloc(act);
           } else
              newSet.add(sw);
        }
        pool.suspendedWhens2 = newSet;
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
        if (RESILIENT_PLACE_ZERO) {
            return new FinishState.FinishResilientPlaceZero(null);
        } else if (RESILIENT_ZOO_KEEPER) {
            return new FinishState.FinishResilientZooKeeper(null);
        } else {
            return new FinishState.Finish();
        }
    }
    static def makeDefaultFinish(latch:SimpleLatch):FinishState {
        if (RESILIENT_PLACE_ZERO) {
            return new FinishState.FinishResilientPlaceZero(latch);
        } else if (RESILIENT_ZOO_KEEPER) {
            return new FinishState.FinishResilientZooKeeper(latch);
        } else {
            return new FinishState.Finish(latch);
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
        case Pragma.FINISH_RESILIENT_PLACE_ZERO:
            f = new FinishState.FinishResilientPlaceZero(null); break;
        case Pragma.FINISH_RESILIENT_ZOO_KEEPER:
            f = new FinishState.FinishResilientZooKeeper(null); break;
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
    	val e = Exception.ensureException(t);
    	activity().finishState().pushException(e);
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
    static def execute(activity:Activity):void {
        worker().push(activity);
    }

    static def executeLocal(activity:Activity):void {
        if (!pool.deal(activity)) worker().push(activity);
    }

    public static def executeLocalInWorker(activity:Activity, worker: Worker):void {
        if (!pool.deal(activity)) worker.push(activity);
    }

    // submit 
    public static def execute(body:()=>void, src:Place, finishState:FinishState):void {
        execute(new Activity(body, src, finishState));
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

    public static def wrapNativeThread():Worker {
        return pool.wrapNativeThread();
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
