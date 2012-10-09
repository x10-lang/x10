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

import x10.io.CustomSerialization;
import x10.io.SerialData;
import x10.io.Reader;
import x10.io.Writer;

import x10.util.Random;
import x10.util.Stack;
import x10.util.Box;

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

    // Native runtime interface

    /**
     * Send active message to another place.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runClosureAt(#id, #body, #prof)")
    @Native("c++", "x10aux::run_closure_at(#id, #body, #prof)")
    public static native def x10rtSendMessage(id:Int, body:()=>void, prof:Profile):void;

    /**
     * Send async to another place.
     * This is a special case of x10rtSendMessage where the active message consists in
     * creating an activity at the destination place with the specified body and finish state
     * and pushing this activity onto the deque of the active worker.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runAsyncAt(#id, #body, #finishState, #prof)")
    @Native("c++", "x10aux::run_async_at(#id, #body, #finishState, #prof)")
    public static native def x10rtSendAsync(id:Int, body:()=>void, finishState:FinishState, prof:Profile):void;

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

    // [DC] didn't understand why this needs to call a destructor
    //@Native("c++", "x10::lang::Object::dealloc_object((x10::lang::Object*)#o.operator->())")
    //public static def deallocObject(o:Object):void {}

    @Native("c++", "x10aux::dealloc(#o)")
    public static def dealloc[T](o:T){ T isref } :void {}

    // Environment variables

    static env = Configuration.loadEnv();

    public static STRICT_FINISH = Configuration.strict_finish();
    public static NTHREADS = Configuration.nthreads();
    public static MAX_THREADS = Configuration.max_threads();
    public static STATIC_THREADS = Configuration.static_threads();
    public static WARN_ON_THREAD_CREATION = Configuration.warn_on_thread_creation();
    public static BUSY_WAITING = Configuration.busy_waiting();

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
        val workers = new Array[Worker](MAX_THREADS);

        // parked x10 threads (parkedCount == spareCount + idleCount)
        val parkedWorkers = new Array[Worker](MAX_THREADS);

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
                println(here+": TOO MANY THREADS... ABORTING");
                System.exit(1);
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

    public final static class Worker extends Thread implements CustomSerialization {
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
            activity = new Activity(()=>{}, FinishState.UNCOUNTED_FINISH);
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
                    if (activity == null) return false;
                }
                activity.run();
                dealloc(activity);
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
                dealloc(activity);
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
                dealloc(activity);
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
        
        /**
         * Serialization of Worker objects is forbidden.
         * @throws UnsupportedOperationException
         */
        public def serialize():SerialData {
        	throw new UnsupportedOperationException("Cannot serialize "+typeName());
        }

        /**
         * Serialization of Worker objects is forbidden.
         * @throws UnsupportedOperationException
         */
        public def this(a:SerialData) {
        	super();
        	throw new UnsupportedOperationException("Cannot deserialize "+typeName());
        }
    }

    static class Pool {
        val latch = new SimpleLatch();
        
        var wsEnd:Boolean = false;

        private val workers = new Workers();

        var wsBlockedContinuations:Deque = null;

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
                // try network
                x10rtProbe();
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
     */
    @Native("c++", "x10aux::here")
    public static def hereInt():int = here.id;

    /**
     * The amount of unscheduled activities currently available to this worker thread.
     * Intended for use in heuristics that control async spawning
     * based on the current amount of surplus work.
     */
    public static def surplusActivityCount():int = worker().size();

    /**
     * Run main activity in a finish.
     * @param init Static initializers
     * @param body Main activity
     */
    public static def start(body:()=>void):void {
        try {
            // initialize thread pool for the current process
            // initialize runtime
            x10rtInit();

            if (hereInt() == 0) {
                val rootFinish = new FinishState.Finish(pool.latch);
                // in place 0 schedule the execution of the main activity
                executeLocal(new Activity(body, rootFinish));

                // wait for thread pool to die
                // (happens when main activity terminates)
                pool(NTHREADS);

                // we need to call waitForFinish here to see the exceptions thrown by main if any
                try {
                    rootFinish.waitForFinish();
                } finally {
                    // root finish has terminated, kill remote processes if any
                    if (Place.MAX_PLACES >= 1024) {
                        val cl1 = ()=> @x10.compiler.RemoteInvocation {
                            val h = hereInt();
                            val cl = ()=> @x10.compiler.RemoteInvocation {pool.latch.release();};
                            for (var j:Int=Math.max(1, h-31); j<h; ++j) {
                                x10rtSendMessage(j, cl, null);
                            }
                            pool.latch.release();
                        };
                        for(var i:Int=Place.MAX_PLACES-1; i>0; i-=32) {
                            x10rtSendMessage(i, cl1, null);
                        }
                    } else {
                        val cl = ()=> @x10.compiler.RemoteInvocation {pool.latch.release();};
                        for (var i:Int=Place.MAX_PLACES-1; i>0; --i) {
                            x10rtSendMessage(i, cl, null);
                        }
                    }
                }
            } else {
                // wait for thread pool to die
                // (happens when a kill signal is received from place 0)
                pool(NTHREADS);
            }
        } finally {
            GlobalCounters.printStats();
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
        if (place.id == hereInt()) {
            executeLocal(new Activity(deepCopy(body, prof), state, clockPhases));
        } else {
            val closure = ()=> @x10.compiler.RemoteInvocation { execute(new Activity(body, state, clockPhases)); };
            x10rtSendMessage(place.id, closure, prof);
            dealloc(closure);
        }
        dealloc(body);
    }
    
    public static def runAsync(place:Place, body:()=>void, prof:Profile):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val state = a.finishState();
        state.notifySubActivitySpawn(place);
        if (place.id == hereInt()) {
            executeLocal(new Activity(deepCopy(body, prof), state));
        } else {
            x10rtSendAsync(place.id, body, state, prof); // optimized case
        }
        dealloc(body);
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
        executeLocal(new Activity(body, state, clockPhases));
    }

    public static def runAsync(body:()=>void):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val state = a.finishState();
        state.notifySubActivitySpawn(here);
        executeLocal(new Activity(body, state));
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
        
        if (place.id == hereInt()) {
            executeLocal(new Activity(deepCopy(body, prof), FinishState.UNCOUNTED_FINISH));
        } else {
            val closure = ()=> @x10.compiler.RemoteInvocation { execute(new Activity(body, FinishState.UNCOUNTED_FINISH)); };
            x10rtSendMessage(place.id, closure, prof);
            dealloc(closure);
        }
        dealloc(body);
    }

    /**
     * Run @Uncounted async
     */
    public static def runUncountedAsync(body:()=>void):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        executeLocal(new Activity(body, new FinishState.UncountedFinish()));
    }

    /**
     * a latch with a place for an exception
     */
    static class RemoteControl extends SimpleLatch implements Mortal {
        public def this() { super(); }
        private def this(Any) {
            throw new UnsupportedOperationException("Cannot deserialize "+typeName());
        }
        var e:CheckedThrowable = null;
        var clockPhases:Activity.ClockPhases = null;
    }

    /** Subvert X10 and target language exception checking.
     */
    @Native("c++", "x10aux::throwException(x10aux::nullCheck(#e))")
    @Native("java", "java.lang.Thread.currentThread().stop(#e)")
    private static native def throwCheckedWithoutThrows (e:CheckedThrowable) : void;

    /**
     * Transparently wrap checked exceptions at the root of an at desugared closure, and unpack later.
     */
    private static class AtCheckedWrapper extends Exception {
        public def this(cause: CheckedThrowable) { super(cause); }
    }

    /**
      * Used in codegen at the root of an at closure, upon catching something that is not below Error
      */
    public static def wrapAtChecked (caught:CheckedThrowable) : void {
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

    /**
     * Run at statement
     */
    public static def runAt(place:Place, body:()=>void, prof:Profile):void {
        Runtime.ensureNotInAtomic();
        if (place.id == hereInt()) {
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
                    val closure = ()=> @x10.compiler.RemoteInvocation { 
                        val me2 = (box as GlobalRef[RemoteControl]{home==here})();
                        me2.clockPhases = clockPhases;
                        me2.release();
                    };
                    x10rtSendMessage(box.home.id, closure, null);
                    dealloc(closure);
                } catch (e:AtCheckedWrapper) {
                    throw e.getCheckedCause();
                }
            } catch (e:CheckedThrowable) {
                val closure = ()=> @x10.compiler.RemoteInvocation { 
                    val me2 = (box as GlobalRef[RemoteControl]{home==here})();
                    me2.e = e;
                    me2.clockPhases = clockPhases;
                    me2.release();
                };
                x10rtSendMessage(box.home.id, closure, null);
                dealloc(closure);
            }
            activity().clockPhases = null;
        }
        me.await();
        dealloc(body);
        activity().clockPhases = me.clockPhases;
        if (null != me.e) {
            throwCheckedWithoutThrows(me.e);
        }
    }

    /*
     * [GlobalGC] Special version of runAt, which does not use activity, clock, exceptions
     *            Used in GlobalRef.java to implement changeRemoteCount
     * [DC] do not allow profiling of this call: seems it is not for application use?
     */
    public static def runAtSimple(place:Place, body:()=>void, toWait:Boolean):void {
        //Console.ERR.println("Runtime.runAtSimple: place=" + place + " toWait=" + toWait);
        if (place.id == hereInt()) {
                deepCopy(body, null)(); // deepCopy and apply
                return;
        }
      if (toWait) { // synchronous exec
        @StackAllocate val me = @StackAllocate new RemoteControl();
        val box:GlobalRef[RemoteControl] = GlobalRef(me as RemoteControl);
        val latchedBody = () => @x10.compiler.RemoteInvocation {
                body();
                val closure = ()=> @x10.compiler.RemoteInvocation { 
                    val me2 = (box as GlobalRef[RemoteControl]{home==here})();
                    me2.release();
                };
                x10rtSendMessage(box.home.id, closure, null);
                dealloc(closure);
            };
        x10rtSendMessage(place.id, latchedBody, null);
        dealloc(latchedBody);
        me.await(); // wait until body is executed at remote place
      } else { // asynchronous exec
        val simpleBody = () => @x10.compiler.RemoteInvocation { body(); };
        x10rtSendMessage(place.id, simpleBody, null);
        dealloc(simpleBody);
        // *not* wait until body is executed at remote place
      }
        dealloc(body);
    }

    /**
     * a latch with a place for an exception and return value
     */
    static class Remote[T] extends RemoteControl {
        public def this() { super(); }
        private def this(SerialData) {
            throw new UnsupportedOperationException("Cannot deserialize "+typeName());
        }
        var t:Box[T] = null;
    }

    public static class Profile {
        public var bytes:Long;
        public var serializationNanos:Long;
        public var communicationNanos:Long;
        public def reset() { bytes = 0; serializationNanos = 0; communicationNanos = 0; }
    }

    /**
     * Eval at expression
     */
    public static def evalAt[T](place:Place, eval:()=>T, prof:Profile):T {
        Runtime.ensureNotInAtomic();
        if (place.id == hereInt()) {
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
        @StackAllocate val me = @StackAllocate new Remote[T]();
        val box = GlobalRef(me as Remote[T]);
        val clockPhases = activity().clockPhases;
        @x10.compiler.Profile(prof) at(place) async {
            activity().clockPhases = clockPhases;
            try {
                try {
                    val result = eval();
                    val closure = ()=> @x10.compiler.RemoteInvocation { 
                        val me2 = (box as GlobalRef[Remote[T]]{home==here})();
                        // me2 has type Box[T{box.home==here}]... weird
                        me2.t = new Box[T{box.home==here}](result as T{box.home==here});
                        me2.clockPhases = clockPhases;
                        me2.release();
                    };
                    x10rtSendMessage(box.home.id, closure, null);
                    dealloc(closure);
                } catch (t:AtCheckedWrapper) {
                    throw t.getCheckedCause();
                }
            } catch (e:CheckedThrowable) {
                val closure = ()=> @x10.compiler.RemoteInvocation { 
                    val me2 = (box as GlobalRef[Remote[T]]{home==here})();
                    me2.e = e;
                    me2.clockPhases = clockPhases;
                    me2.release();
                };
                x10rtSendMessage(box.home.id, closure, null);
                dealloc(closure);
            }
            activity().clockPhases = null;
        }
        me.await();
        dealloc(eval);
        activity().clockPhases = me.clockPhases;
        if (null != me.e) {
            throwCheckedWithoutThrows(me.e);
        }
        return me.t.value;
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

    /**
     * Start executing current activity synchronously
     * (i.e. within a finish statement).
     */
    public static def startFinish():FinishState {
        return activity().swapFinish(new FinishState.Finish());
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
            f = new FinishState.Finish();
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
        dealloc(finishState);
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

    // submit 
    public static def execute(body:()=>void, finishState:FinishState):void {
        execute(new Activity(body, finishState));
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
