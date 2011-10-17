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
import x10.compiler.Pinned;
import x10.compiler.Global;
import x10.compiler.PerProcess;
import x10.compiler.Pragma;
import x10.compiler.StackAllocate;

import x10.io.CustomSerialization;
import x10.io.SerialData;

import x10.util.Random;
import x10.util.Stack;
import x10.util.Box;
import x10.util.NoSuchElementException;

import x10.util.concurrent.Lock;
import x10.util.concurrent.Monitor;
import x10.util.concurrent.SimpleLatch;

/**
 * XRX invocation protocol:
 * - Native runtime invokes new Runtime.Worker(0). Returns Worker instance worker0.
 * - Native runtime starts worker0 thread.
 * - Native runtime invokes Runtime.start(...) from worker0 (Thread.currentThread() == worker0).
 * - Runtime.start(...) returns when application code execution has completed.
 * 
 * @author tardieu
 */
@Pinned public final class Runtime {

    // Print methods for debugging

    @Native("java", "java.lang.System.err.println(#any)")
    @Native("c++", "x10aux::system_utils::println(x10aux::to_string(#any)->c_str())")
    public native static def println(any:Any) : void;

    @Native("java", "java.lang.System.err.println()")
    @Native("c++", "x10aux::system_utils::println(\"\")")
    public native static def println() : void;

    @Native("java", "java.lang.System.err.printf(#fmt, #t)")
    @Native("c++", "x10aux::system_utils::printf(#fmt, #t)")
    public native static def printf[T](fmt:String, t:T) : void;

    // Native runtime interface

    @Native("c++", "PLATFORM_MAX_THREADS")
    private static PLATFORM_MAX_THREADS: Int = Int.MAX_VALUE;

    @Native("c++", "DEFAULT_STATIC_THREADS")
    private static DEFAULT_STATIC_THREADS: Boolean = false;

    @Native("java", "x10.runtime.impl.java.Runtime.loadenv()")
    @Native("c++", "x10aux::loadenv()")
    private static native def loadenv():x10.util.HashMap[String,String];

    /**
     * Run body at place(id).
     * May be implemented synchronously or asynchronously.
     * Body cannot spawn activities, use clocks, or raise exceptions.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runClosureAt(#id, #body)")
    @Native("c++", "x10aux::run_closure_at(#id, #body)")
    static def runClosureAt(id:Int, body:()=>void):void { body(); }
    
    @Native("java", "x10.runtime.impl.java.Runtime.runClosureAt(#id, #body)")
    @Native("c++", "x10aux::run_closure_at(#id, #body, #endpoint)")
    static def runClosureAt(id:Int, body:()=>void, endpoint:Int):void { body(); }

    @Native("java", "x10.runtime.impl.java.Runtime.runClosureCopyAt(#id, #body)")
    @Native("c++", "x10aux::run_closure_at(#id, #body)")
    static def runClosureCopyAt(id:Int, body:()=>void):void { body(); }
    
    @Native("java", "x10.runtime.impl.java.Runtime.runClosureCopyAt(#id, #body)")
    @Native("c++", "x10aux::run_closure_at(#id, #body, #endpoint)")
    static def runClosureCopyAt(id:Int, body:()=>void, endpoint:Int):void { body(); }

    @Native("java", "x10.runtime.impl.java.Runtime.runAsyncAt(#id, #body, #finishState)")
    @Native("c++", "x10aux::run_async_at(#id, #body, #finishState)")
    static def runAsyncAt(id:Int, body:()=>void, finishState:FinishState):void { body(); }

    @Native("java", "x10.runtime.impl.java.Runtime.runAsyncAt(#id, #body, #finishState, #endpoint)")
    @Native("c++", "x10aux::run_async_at(#id, #body, #finishState, #endpoint)")
    static def runAsyncAt(id:Int, body:()=>void, finishState:FinishState, endpoint:Int):void { body(); }

    /**
     * Must be called once XRX is initialized prior to sending messages.
     */
    @Native("c++", "x10rt_registration_complete()")
    @Native("java", "x10.x10rt.X10RT.registration_complete()")
    static native def x10rt_registration_complete():void;

    /**
     * Deep copy.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.<#T$box>deepCopy(#o)")
    @Native("c++", "x10aux::deep_copy<#T >(#o)")
    static native def deepCopy[T](o:T):T;

    /**
     * Process one incoming message if any (non-blocking).
     */
    @Native("c++", "x10aux::event_probe()")
    @Native("java","x10.runtime.impl.java.Runtime.eventProbe()")
    static def event_probe():void {}

    // Accessors for native performance counters

    static PRINT_STATS = false;

    @Native("c++","x10aux::asyncs_sent")
    static def getAsyncsSent():Long = 0L;

    @Native("c++","x10aux::asyncs_sent = #v")
    static def setAsyncsSent(v:Long):void { }

    @Native("c++","x10aux::asyncs_received")
    static def getAsyncsReceived():Long = 0L;

    @Native("c++","x10aux::asyncs_received = #v")
    static def setAsyncsReceived(v:Long):void { }

    @Native("c++","x10aux::serialized_bytes")
    static def getSerializedBytes():Long = 0L;

    @Native("c++","x10aux::serialized_bytes = #v")
    static def setSerializedBytes(v:Long):void { }

    @Native("c++","x10aux::deserialized_bytes")
    static def getDeserializedBytes():Long = 0L;

    @Native("c++","x10aux::deserialized_bytes = #v")
    static def setDeserializedBytes(v:Long):void { }

    public static def serializedSize[T](v:T) {
        var r:Long;
        @Native("java", "r = x10.runtime.impl.java.Runtime.serialize(v).length;")
        @Native("c++", "x10aux::serialization_buffer buf; buf.write(v); r = buf.length();")
        { r = -1L; }
        return r;
    }

    public static struct X10RTMessageStats {
        public def this () {
            this.bytesSent = 0;
            this.messagesSent = 0;
            this.bytesReceived = 0;
            this.messagesReceived = 0;
        }
        public def this (bytesSent:Long, messagesSent:Long, bytesReceived:Long, messagesReceived:Long) {
            this.bytesSent = bytesSent;
            this.messagesSent = messagesSent;
            this.bytesReceived = bytesReceived;
            this.messagesReceived = messagesReceived;
        }
        public bytesSent:Long;
        public messagesSent:Long;
        public bytesReceived:Long;
        public messagesReceived:Long;

        public operator + this = this;
        public operator - this = X10RTMessageStats(-bytesSent, -messagesSent, -bytesReceived, -messagesReceived);
        public operator this + (that:X10RTMessageStats) = X10RTMessageStats(bytesSent+that.bytesSent,
                                                                            messagesSent+that.messagesSent,
                                                                            bytesReceived+that.bytesReceived,
                                                                            messagesReceived+that.messagesReceived);
        public operator this - (that:X10RTMessageStats) = this + (-that);

        public def toString () = "[out:"+bytesSent+"/"+messagesSent+" in:"+bytesReceived+"/"+messagesReceived+"]";
    }

    public static struct X10RTStats {
        public def this () {
            this.msg = X10RTMessageStats();
            this.put = X10RTMessageStats();
            this.putCopiedBytesSent = 0;
            this.putCopiedBytesReceived = 0;
            this.get = X10RTMessageStats();
            this.getCopiedBytesSent = 0;
            this.getCopiedBytesReceived = 0;
        }
        public def this (msg:X10RTMessageStats,
                         put:X10RTMessageStats, putCopiedBytesSent:Long, putCopiedBytesReceived:Long,
                         get:X10RTMessageStats, getCopiedBytesSent:Long, getCopiedBytesReceived:Long) {
            this.msg = msg;
            this.put = put;
            this.putCopiedBytesSent = putCopiedBytesSent;
            this.putCopiedBytesReceived = putCopiedBytesReceived;;
            this.get = get;
            this.getCopiedBytesSent = getCopiedBytesSent;
            this.getCopiedBytesReceived = getCopiedBytesReceived;

        }

        public msg:X10RTMessageStats;

        public put:X10RTMessageStats;
        public putCopiedBytesSent:Long;
        public putCopiedBytesReceived:Long;

        public get:X10RTMessageStats;
        public getCopiedBytesSent:Long;
        public getCopiedBytesReceived:Long;

        public operator + this = this;
        public operator - this = X10RTStats(-msg,
                                            -put,-putCopiedBytesSent,-putCopiedBytesReceived,
                                            -get,-getCopiedBytesSent,-getCopiedBytesReceived);
        public operator this + (that:X10RTStats) = X10RTStats(msg+that.msg,
                                                              put+that.put,
                                                              putCopiedBytesSent+that.putCopiedBytesSent,
                                                              putCopiedBytesReceived+that.putCopiedBytesReceived,
                                                              get+that.get,
                                                              getCopiedBytesSent+that.getCopiedBytesSent,
                                                              getCopiedBytesReceived+that.getCopiedBytesReceived);
        public operator this - (that:X10RTStats) = this + (-that);

        public def toString () = 
            "msg:"+msg+
            " put:"+put+
            " putCopiedBytesSent:"+putCopiedBytesSent+
            " putCopiedBytesReceived:"+putCopiedBytesReceived+
            " get:"+get+
            " getCopiedBytesSent:"+getCopiedBytesSent+
            " getCopiedBytesReceived:"+getCopiedBytesReceived;
    }


    /** Fetch the current state of the X10RT-level counters, including Array.asyncCopy (i.e. get/put) information. */
    public static def getX10RTStats () {
        @Native("c++", "return x10aux::get_X10RTStats<x10::lang::Runtime__X10RTStats,x10::lang::Runtime__X10RTMessageStats>();")
        {
            return X10RTStats();
        }
    }

    /** Fetch the current state of the X10RT-level counters, excluding anything related to Array.asyncCopy */
    public static def getX10RTMessageStats () = getX10RTStats().msg;

    // Methods for explicit memory management

    @Native("c++", "x10::lang::Object::dealloc_object((x10::lang::Object*)#o.operator->())")
    public static def deallocObject (o:Object):void { }

    @Native("c++", "x10aux::dealloc(#o.operator->())")
    public static def dealloc[T] (o:()=>T):void { }

    @Native("c++", "x10aux::dealloc(#o.operator->())")
    public static def dealloc (o:()=>void):void { }

    // Configuration options

    private static def x10_strict_finish():Boolean {
        try {
            val v = env.getOrThrow("X10_STRICT_FINISH");
            return !(v.equalsIgnoreCase("false") || v.equalsIgnoreCase("f") || v.equals("0"));
        } catch (NoSuchElementException) {
        }
        return false;
    }

    /**
     * The initial number of worker threads
     */
    private static def x10_nthreads():Int {
        var v:Int = 0;
        try {
            v = Int.parse(env.getOrThrow("X10_NTHREADS"));
        } catch (NoSuchElementException) {
        } catch (NumberFormatException) {
        }
        if (v <= 0) v = 1;
        if (v > PLATFORM_MAX_THREADS) v = PLATFORM_MAX_THREADS;
        return v;
    }

    /**
     * An upper bound on the number of worker threads
     */
    private static def x10_max_threads():Int {
        var v:Int = 0;
        try {
           v = Int.parse(env.getOrThrow("X10_MAX_THREADS"));
       } catch (NoSuchElementException) {
       } catch (NumberFormatException) {
       }
       if (v <= 0) v = NTHREADS;
       if (!STATIC_THREADS && v < 1000) v = 1000;
       if (v > PLATFORM_MAX_THREADS) v = PLATFORM_MAX_THREADS;
       return v;
    }

    private static def x10_static_threads():Boolean {
        try {
            val v = env.getOrThrow("X10_STATIC_THREADS");
            return !(v.equalsIgnoreCase("false") || v.equalsIgnoreCase("f") || v.equals("0"));
        } catch (NoSuchElementException) {
        }
        return DEFAULT_STATIC_THREADS;
    }

    private static def x10_warn_on_thread_creation():Boolean {
        try {
            val v = env.getOrThrow("X10_WARN_ON_THREAD_CREATION");
            return !(v.equalsIgnoreCase("false") || v.equalsIgnoreCase("f") || v.equals("0"));
        } catch (NoSuchElementException) {
        }
        return DEFAULT_STATIC_THREADS;
    }

    /**
     * The number of logical processors available on the host.
     */
    @Native("c++", "x10aux::num_local_cores")
    @Native("java", "java.lang.Runtime.getRuntime().availableProcessors()")
    public native static def availableProcessors():Int;


    @PerProcess static staticMonitor = new Monitor();
    @PerProcess static env = loadenv();
    @PerProcess public static STRICT_FINISH = x10_strict_finish();
    @PerProcess public static NTHREADS = x10_nthreads();
    @PerProcess public static MAX_THREADS = x10_max_threads();
    @PerProcess public static STATIC_THREADS = x10_static_threads();
    @PerProcess public static WARN_ON_THREAD_CREATION = x10_warn_on_thread_creation();

    //Work-Stealing Runtime Related Interface
    
    public static def wsInit():void {
        pool.wsBlockedContinuations = new Deque();
    }
    
    public static def wsFIFO():Deque {
        return worker().wsfifo;
    }
    
    public static def wsProcessEvents():void {
        event_probe();
    }

    /*
     * Run a ws frame in local or remote.
     * The frame is in the body, and should be in the heap
     */
    public static def wsRunAsync(id:Int, body:()=>void):void {
        if (id == hereInt()) {
            val copy = deepCopy(body);
            copy();
            dealloc(copy);
        } else {
            runClosureCopyAt(id, body);
        }
        dealloc(body);
    }

    /* 
     * Run a ws command in local or remote, such as a finish join action, or stop all workers action
     */
    public static def wsRunCommand(id:Int, body:()=>void):void {
        runClosureAt(id, body);
    }

    public static def wsBlock(k:Object) {
        pool.wsBlockedContinuations.push(k);
    }

    public static def wsUnblock() {
        val src = pool.wsBlockedContinuations;
        val dst = wsFIFO();
        var k:Object;
        while ((k = src.poll()) != null) dst.push(k);
    }

    public static def wsEnd() {
        pool.wsEnd = true;
    }

    public static def wsEnded() = pool.wsEnd;

    /**
     * A mortal object is garbage collected when there are no remaining local refs even if remote refs might still exist
     */
    public interface Mortal { }

    @Pinned static class Semaphore {
        private val lock = new Lock();

        private val threads = new Array[Worker](MAX_THREADS);
        private var size:Int = 0;

        private var permits:Int;

        def this(n:Int) {
            permits = n;
        }

        private static def min(i:Int, j:Int):Int = i<j ? i : j;

        def release(n:Int):void {
            lock.lock();
            permits += n;
            val m = min(permits, min(n, size));
            for (var i:Int = 0; i<m; i++) {
                threads(--size).unpark();
                threads(size) = null;
            }
            lock.unlock();
        }

        def release():void {
            release(1);
        }

        def reduce(n:Int):void {
            lock.lock();
            permits -= n;
            lock.unlock();
        }

        def acquire(worker:Worker):void {
            lock.lock();
            while (permits <= 0) {
                val s = size;
                threads(size++) = worker;
                while (threads(s) == worker) {
                    lock.unlock();
                    Worker.park();
                    lock.lock();
                }
            }
            --permits;
            lock.unlock();
        }

        def yield(worker:Worker):void {
            if (permits < 0) {
                release(1);
                acquire(worker);
            }
        }

        def available():Int = permits;
    }

    @Pinned public final static class Worker extends Thread implements CustomSerialization {
        // bound on loop iterations to help j9 jit
        private static BOUND = 100;

        // activity (about to be) executed by this worker
        private var activity:Activity = null;

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
            } catch (t:Throwable) {
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
                deallocObject(activity);
            }
            return true;
        }

        def probe():void {
            // process all queued activities
            val tmp = activity; // save current activity
            event_probe();
            for (;;) {
                activity = poll();
                if (activity == null) {
                    activity = tmp; // restore current activity
                    return;
                }
                activity.run();
                deallocObject(activity);
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
                deallocObject(activity);
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
        	super(a);
        	throw new UnsupportedOperationException("Cannot deserialize "+typeName());
        }
    }

    @Pinned static class Pool {
        val latch = new SimpleLatch();
        
        var wsEnd:Boolean = false;
        
        private var size:Int; // the number of workers in the pool

        private var spares:Int = 0; // the number of spare workers in the pool

        private var dead:Int = 0; // the number of dead workers in the pool

        private val lock = new Lock();

        private val semaphore = new Semaphore(0);

        var wsBlockedContinuations:Deque = null;

        // the workers in the pool
        private val workers = new Array[Worker](MAX_THREADS);

        operator this(n:Int):void {
            size = n;
            workers(0) = worker();
            for (var i:Int = 1; i<n; i++) {
                workers(i) = new Worker(i);
            }
            for (var i:Int = 1; i<n; i++) {
                workers(i).start();
            }
            workers(0)();
            while (size > dead) Worker.park();
        }

        // notify the pool a worker is about to execute a blocking operation
        def increase():void {
            lock.lock();
            if (spares > 0) {
                // if a spare is available increase parallelism
                spares--;
                lock.unlock();
                semaphore.release();
            } else {
                // allocate and start a new worker
                val i = size++;
                lock.unlock();
                if (i >= MAX_THREADS) {
                    println(here+": TOO MANY THREADS... ABORTING");
                    System.exit(1);
                }
                if (WARN_ON_THREAD_CREATION) {
                    println(here+": WARNING: A new OS-level thread was created (there are now "+size+" threads).");
                }
                val worker = new Worker(i);
                workers(i) = worker;
                worker.start();
            }
        }

        // notify the pool a worker resumed execution after a blocking operation
        def decrease(n:Int):void {
            // increase number or spares
            lock.lock();
            spares += n;
            lock.unlock();
            // reduce parallelism
            semaphore.reduce(n);
        }

        // release permit (called by worker upon termination)
        def release() {
            semaphore.release();
            lock.lock();
            dead++;
            if (size == dead) workers(0).unpark();
            lock.unlock();
        }

        // scan workers and network for pending activities
        def scan(random:Random, worker:Worker):Activity {
            var activity:Activity = null;
            var next:Int = random.nextInt(size);
            for (;;) {
                if (null != activity || latch()) return activity;
                // go to sleep if too many threads are running
                semaphore.yield(worker);
                if (null != activity || latch()) return activity;
                // try network
                event_probe();
                activity = worker.poll();
                if (null != activity || latch()) return activity;
                // try random worker
                if (next < MAX_THREADS && null != workers(next)) { // avoid race with increase method
                    activity = workers(next).steal();
                }
                if (++next == size) next = 0;
            }
        }

        def size() = size;
    }

    @PerProcess static pool = new Pool();
    @PerProcess public static atomicMonitor = new Monitor();
    @PerProcess static finishStates = new FinishState.FinishStates();

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
     * Run main activity in a finish
     * @param init Static initializers
     * @param body Main activity
     */
    public static def start(init:()=>void, body:()=>void):void {
        try {
            // initialize thread pool for the current process
            // initialize runtime
            x10rt_registration_complete();

            if (hereInt() == 0) {
                val rootFinish = new FinishState.Finish(pool.latch);
                // in place 0 schedule the execution of the static initializers fby main activity
                execute(new Activity(()=>{finish init(); body();}, rootFinish));

                // wait for thread pool to die
                // (happens when main activity terminates)
                pool(NTHREADS);

                // we need to call waitForFinish here to see the exceptions thrown by main if any
                try {
                    rootFinish.waitForFinish();
                } finally {
                    // root finish has terminated, kill remote processes if any
                    for (var i:Int=1; i<Place.MAX_PLACES; i++) {
                        runClosureAt(i, ()=> @x10.compiler.RemoteInvocation {pool.latch.release();});
                    }
                }
            } else {
                // wait for thread pool to die
                // (happens when a kill signal is received from place 0)
                pool(NTHREADS);
            }
        } finally {
            if (PRINT_STATS) {
                println("ASYNC SENT AT PLACE " + here.id +" = " + getAsyncsSent());
                println("ASYNC RECV AT PLACE " + here.id +" = " + getAsyncsReceived());
            }
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
    public static def runAsync(place:Place, clocks:Rail[Clock], body:()=>void):void {
    	runAsync(place, clocks, body, 0);
    }
    
    public static def runAsync(place:Place, clocks:Rail[Clock], body:()=>void, endpoint:Int):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val state = a.finishState();
        val clockPhases = a.clockPhases().make(clocks);
        state.notifySubActivitySpawn(place);
        if (place.id == hereInt()) {
            execute(new Activity(deepCopy(body), state, clockPhases));
        } else {
            val closure = ()=> @x10.compiler.RemoteInvocation { execute(new Activity(body, state, clockPhases)); };
            runClosureCopyAt(place.id, closure, endpoint);
            dealloc(closure);
        }
        dealloc(body);
    }
    
    public static def runAsync(place:Place, body:()=>void):void {
    	runAsync(place, body, 0);
    }

    public static def runAsync(place:Place, body:()=>void, endpoint:Int):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val state = a.finishState();
        state.notifySubActivitySpawn(place);
        if (place.id == hereInt()) {
            execute(new Activity(deepCopy(body), state));
        } else {
            runAsyncAt(place.id, body, state, endpoint); // optimized case
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
        execute(new Activity(body, state, clockPhases));
    }

    public static def runAsync(body:()=>void):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val state = a.finishState();
        state.notifySubActivitySpawn(here);
        execute(new Activity(body, state));
    }

	public static def runFinish(body:()=>void):void {
	    finish body();
	}

    /**
     * Run @Uncounted asyncat
     */
    public static def runUncountedAsync(place:Place, body:()=>void):void {
    	runUncountedAsync(place, body, 0);
    }
    
    public static def runUncountedAsync(place:Place, body:()=>void, endpoint:Int):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        if (place.id == hereInt()) {
            execute(new Activity(deepCopy(body), FinishState.UNCOUNTED_FINISH));
        } else {
            val closure = ()=> @x10.compiler.RemoteInvocation { execute(new Activity(body, FinishState.UNCOUNTED_FINISH)); };
            runClosureCopyAt(place.id, closure, endpoint);
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
        
        execute(new Activity(body, new FinishState.UncountedFinish()));
    }

    /**
     * a latch with a place for an exception
     */
    static class RemoteControl extends SimpleLatch implements Mortal {
        public def this() { super(); }
        private def this(Any) {
            throw new UnsupportedOperationException("Cannot deserialize "+typeName());
        }
        var e:Throwable = null;
        var clockPhases:Activity.ClockPhases = null;
    }

    /**
     * Run at statement
     */
    public static def runAt(place:Place, body:()=>void):void {
        runAt(place, body, 0);
    }

    public static def runAt(place:Place, body:()=>void, endpoint:Int):void {
        // for now the endpoint parameter is ignored
        Runtime.ensureNotInAtomic();
        if (place.id == hereInt()) {
            try {
                deepCopy(body)();
                return;
            } catch (t:Throwable) {
                throw deepCopy(t);
            }
        }
        @StackAllocate val me = @StackAllocate new RemoteControl();
        val box = GlobalRef(me);
        val clockPhases = activity().clockPhases;
        at(place) async {
            activity().clockPhases = clockPhases;
            try {
                body();
                at(box.home) async {
                    val me2 = box();
                    me2.clockPhases = clockPhases;
                    me2.release();
                }
            } catch (e:Throwable) {
                at(box.home) async {
                    val me2 = box();
                    me2.e = e;
                    me2.clockPhases = clockPhases;
                    me2.release();
                }
            }
            activity().clockPhases = null;
        }
        me.await();
        dealloc(body);
        activity().clockPhases = me.clockPhases;
        if (null != me.e) {
            throw me.e;
        }
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

    /**
     * Eval at expression
     */
    public static def evalAt[T](place:Place, eval:()=>T):T {
        Runtime.ensureNotInAtomic();
        if (place.id == hereInt()) {
            try {
                return deepCopy(deepCopy(eval)());
            } catch (t:Throwable) {
                throw deepCopy(t);
            }
        }
        @StackAllocate val me = @StackAllocate new Remote[T]();
        val box = GlobalRef(me);
        val clockPhases = activity().clockPhases;
        at(place) async {
            activity().clockPhases = clockPhases;
            try {
                val result = eval();
                at(box.home) async {
                    val me2 = box();
                    me2.t = new Box[T](result);
                    me2.clockPhases = clockPhases;
                    me2.release();
                }
            } catch (e:Throwable) {
                at(box.home) async {
                    val me2 = box();
                    me2.e = e;
                    me2.clockPhases = clockPhases;
                    me2.release();
                }
            }
            activity().clockPhases = null;
        }
        me.await();
        dealloc(eval);
        activity().clockPhases = me.clockPhases;
        if (null != me.e) {
            throw me.e;
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
        deallocObject(finishState);
    }

    /**
     * Push the exception thrown while executing s in a finish s,
     * onto the finish state.
     */
    public static def pushException(t:Throwable):void  {
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
    static def execute(activity:Activity):void {
        worker().push(activity);
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
}

// vim:shiftwidth=4:tabstop=4:expandtab
