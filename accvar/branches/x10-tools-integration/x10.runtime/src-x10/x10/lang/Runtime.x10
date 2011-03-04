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
import x10.compiler.Pragma;
import x10.compiler.StackAllocate;
import x10.compiler.TempNoInline_1;

import x10.util.Random;
import x10.util.Stack;
import x10.util.Box;
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

    @Native("java", "java.lang.System.err.println(#1)")
    @Native("c++", "x10aux::system_utils::println(x10aux::to_string(#any)->c_str())")
    public native static def println(any:Any) : void;

    @Native("java", "java.lang.System.err.println()")
    @Native("c++", "x10aux::system_utils::println(\"\")")
    public native static def println() : void;

    @Native("java", "java.lang.System.err.printf(#4, #5)")
    @Native("c++", "x10aux::system_utils::printf(#fmt, #t)")
    public native static def printf[T](fmt:String, t:T) : void;

    // Configuration options

    @Native("java", "x10.runtime.impl.java.Runtime.NO_STEALS")
    @Native("c++", "x10aux::no_steals")
    public static NO_STEALS = false;

    /**
     * The initial number of worker threads
     */
    @Native("java", "x10.runtime.impl.java.Runtime.INIT_THREADS")
    @Native("c++", "x10aux::num_threads")
    public static INIT_THREADS = 1;

    /**
     * An upper bound on the number of worker threads
     */
    @Native("java", "x10.runtime.impl.java.Runtime.MAX_THREADS")
    @Native("c++", "x10aux::max_threads")
    public static MAX_WORKERS = 1000;

    @Native("java", "x10.runtime.impl.java.Runtime.STATIC_THREADS")
    @Native("c++", "x10aux::static_threads")
    public static STATIC_THREADS = false;

    // Native runtime interface

    /**
     * Run body at place(id).
     * May be implemented synchronously or asynchronously.
     * Body cannot spawn activities, use clocks, or raise exceptions.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runClosureAt(#1, #2)")
    @Native("c++", "x10aux::run_closure_at(#id, #body)")
    static def runClosureAt(id:Int, body:()=>void):void { body(); }

    @Native("java", "x10.runtime.impl.java.Runtime.runClosureCopyAt(#1, #2)")
    @Native("c++", "x10aux::run_closure_at(#id, #body)")
    static def runClosureCopyAt(id:Int, body:()=>void):void { body(); }

    @Native("c++", "x10aux::run_async_at(#id, #body, #finishState)")
    static def runAsyncAt(id:Int, body:()=>void, finishState:FinishState):void {
        val closure = ()=> @x10.compiler.RemoteInvocation {execute(body, finishState);};
        runClosureCopyAt(id, closure);
        dealloc(closure);
    }

    // must be called once XRX is initialized prior to sending messages
    @Native("c++", "x10rt_registration_complete()")
    @Native("java", "x10.x10rt.X10RT.registration_complete()")
    static native def x10rt_registration_complete():void;

    //Work-Stealing Runtime Related Interface
    /*
     * Return the WS worker binded to current Thread(Worker).
     * Note, because WS worker could be C++ worker or Java worker. It will only return as Object
     */
    public static def wsWorker():Object {
        return worker().wsWorker;
    }
    
    public static def wsBindWorker(w:Object, i:Int):void {
        if(worker().wsWorker != null && !(here.id == 0 && i == 0)){
            println(here+"[WSRT_ERR]N:1 Thread Binding Request from WS Worker");
        }
        else{
            worker().wsWorker = w;            
        }
    }
    
    public static def wsProcessEvents():void {
        event_probe();
    }
    /*
     * Run a ws frame in local or remote.
     * The frame is in the body, and should be in the heap
     */
    public static def wsRunAsync(id:Int, body:()=>void):void {
        if(id == here.id){
            val closure:()=>void = deepCopy(body);
            closure();
            dealloc(closure);
        }
        else{
            val closure:()=>void = ()=>@x10.compiler.RemoteInvocation {
                body(); //just execute the 
            };
            runClosureCopyAt(id, closure);
            dealloc(closure);
        }
    }
    /* 
     * Run a ws command in local or remote, such as a finish join action, or stop all workers action
     */
    public static def wsRunCommand(id:Int, body:()=>void):void {
        if(id == here.id){
            body();
        }
        else {
            val closure:()=>void = ()=>@x10.compiler.RemoteInvocation {
                body(); //just execute the 
            };
            runClosureAt(id, closure);
            dealloc(closure);
        }
    }
    
    /**
     * Deep copy.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.<#1>deepCopy(#4)")
    @Native("c++", "x10aux::deep_copy<#T >(#o)")
    static native def deepCopy[T](o:T):T;

    /**
     * Process one incoming message if any (non-blocking).
     */
    @Native("c++", "x10aux::event_probe()")
    @Native("java","x10.runtime.impl.java.Runtime.eventProbe()")
    static def event_probe():void {}

    // Accessors for native performance counters

    @Native("c++","x10aux::asyncs_sent")
    static def getAsyncsSent() = 0L;

    @Native("c++","x10aux::asyncs_sent = #v")
    static def setAsyncsSent(v:Long) { }

    @Native("c++","x10aux::asyncs_received")
    static def getAsyncsReceived() = 0L;

    @Native("c++","x10aux::asyncs_received = #v")
    static def setAsyncsReceived(v:Long) { }

    @Native("c++","x10aux::serialized_bytes")
    static def getSerializedBytes() = 0L;

    @Native("c++","x10aux::serialized_bytes = #v")
    static def setSerializedBytes(v:Long) { }

    @Native("c++","x10aux::deserialized_bytes")
    static def getDeserializedBytes() = 0L;

    @Native("c++","x10aux::deserialized_bytes = #v")
    static def setDeserializedBytes(v:Long) { }

    // Methods for explicit memory management

    @Native("c++", "x10::lang::Object::dealloc_object((x10::lang::Object*)#o.operator->())")
    public static def deallocObject (o:Object) { }

    @Native("c++", "x10aux::dealloc(#o.operator->())")
    public static def dealloc[T] (o:()=>T) { }

    @Native("c++", "x10aux::dealloc(#o.operator->())")
    public static def dealloc (o:()=>void) { }


    /**
     * A mortal object is garbage collected when there are no remaining local refs even if remote refs might still exist
     */
    public interface Mortal { }

    @Pinned static class Semaphore {
        private val lock = new Lock();

        private val threads = new Array[Worker](MAX_WORKERS);
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

    @Pinned final static class Worker extends Thread {
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

        var pool:Pool;
        
        //1:1 mapping WorkStealing Worker and X10 Worker
        var wsWorker:Object = null;

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
        def push(activity:Activity):void = queue.push(activity);

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
        @TempNoInline_1
        private def loop():Boolean {
            @TempNoInline_1
            for (var i:Int = 0; i < BOUND; i++) {
                activity = poll();
                if (activity == null) {
                    activity = pool.scan(random, this);
                    if (activity == null) return false;
                }
                activity.run();
            }
            return true;
        }

        @TempNoInline_1
        def probe():void {
            @TempNoInline_1
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
            }
        }

        // run activities while waiting on finish
        def join(latch:SimpleLatch):void {
            val tmp = activity; // save current activity
            while (loop2(latch));
            activity = tmp; // restore current activity
        }

        // inner loop to help j9 jit
        @TempNoInline_1
        private def loop2(latch:SimpleLatch):Boolean {
            @TempNoInline_1
            for (var i:Int = 0; i < BOUND; i++) {
                if (latch()) return false;
                activity = poll();
                if (activity == null) return false;
//                if (activity.finishState().simpleLatch() != latch) {
//                    push(activity);
//                    return false;
//                }
                activity.run();
            }
            return true;
        }

        // park current worker
        public static def park() {
            if (!STATIC_THREADS) {
                Thread.park();
            } else {
                probe();
            }
        }

        // unpark worker
        public def unpark() {
            if (!STATIC_THREADS) {
                super.unpark();
            }
        }
    }

    @Pinned static class Pool {
        val latch:SimpleLatch;

        private var size:Int; // the number of workers in the pool

        private var spares:Int = 0; // the number of spare workers in the pool

        private var dead:Int = 0; // the number of dead workers in the pool

        private val lock = new Lock();

        private val semaphore = new Semaphore(0);

        // the workers in the pool
        private val workers:Array[Worker](1){rail};

        def this(size:Int) {
            this.size = size;
            this.latch = new SimpleLatch();
            val workers = new Array[Worker](MAX_WORKERS);

            // main worker
            workers(0) = worker();

            // other workers
            for (var i:Int = 1; i<size; i++) {
                workers(i) = new Worker(i);
            }
            this.workers = workers;
        }

        operator this():void {
            val s = size;
            for (var i:Int = 1; i<s; i++) {
                workers(i).pool = this;
                workers(i).start();
            }
            workers(0).pool = this;
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
                if (i >= MAX_WORKERS) {
                    println("TOO MANY THREADS... ABORTING");
                    System.exit(1);
                }
                val worker = new Worker(i);
                worker.pool = this;
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
                // try network
                event_probe();
                activity = worker.poll();
                if (null != activity || latch()) return activity;
                // try random worker
                if (next < MAX_WORKERS && null != workers(next)) { // avoid race with increase method
                    activity = workers(next).steal();
                }
                if (++next == size) next = 0;
            }
        }

        def size() = size;
    }

    // static fields

    static PRINT_STATS = false;

    // runtime instance associated with each place
    public static runtime = PlaceLocalHandle[Runtime]();

    // instance fields

    // per process members
    transient val pool:Pool;

    // per place members
    private transient val atomicMonitor:Monitor;
    private transient val staticMonitor:Monitor;
    public transient val finishStates:FinishState.FinishStates;

    // constructor

    private def this(pool:Pool):Runtime {
        this.pool = pool;
        this.atomicMonitor = new Monitor();
        this.staticMonitor = new Monitor();
        this.finishStates = new FinishState.FinishStates();
    }

    /**
     * Return the current worker
     */
    static def worker():Worker = Thread.currentThread() as Worker;

    /**
     * Return the current worker id
     */
    public static def workerId():Int = worker().workerId;

    /**
     * Return the number of workers currently in the pool
     * (can increase, cannot decrease)
     */
    public static def poolSize():Int = runtime().pool.size();

    /**
     * Return the current activity
     */
    static def activity():Activity = worker().activity();

    /**
     * Return the current place
     */
    @Native("c++", "x10::lang::Place_methods::_make(x10aux::here)")
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
        // initialize thread pool for the current process
        val pool = new Pool(INIT_THREADS);

        try {
            // initialize runtime
            runtime.set(new Runtime(pool));
            x10rt_registration_complete();

            if (hereInt() == 0) {
                val rootFinish = new FinishState.Finish(pool.latch);
                // in place 0 schedule the execution of the static initializers fby main activity
                execute(new Activity(()=>{finish init(); body();}, rootFinish));

                // wait for thread pool to die
                // (happens when main activity terminates)
                pool();

                // root finish has terminated, kill remote processes if any
                for (var i:Int=1; i<Place.MAX_PLACES; i++) {
                    runClosureAt(i, ()=> @x10.compiler.RemoteInvocation {runtime().pool.latch.release();});
                }

                // we need to call waitForFinish here to see the exceptions thrown by main if any
                rootFinish.waitForFinish();
            } else {
                // wait for thread pool to die
                // (happens when a kill signal is received from place 0)
                pool();
            }
        } finally {
            if (PRINT_STATS) {
                println("ASYNC SENT AT PLACE " + here.id +" = " + getAsyncsSent());
                println("ASYNC RECV AT PLACE " + here.id +" = " + getAsyncsReceived());
            }
        }
    }

    // async at, async, at statement, and at expression implementation
    // at is implemented using async at
    // async at and at must make a copy of the closure parameter (local or remote)
    // async at and at should dealloc the closure parameter
    // async must not copy or dealloc the closure parameter
    
    /**
     * Run async at
     */
    public static def runAsync(place:Place, clocks:Array[Clock](1){rail}, body:()=>void):void {
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
            runClosureCopyAt(place.id, closure);
            dealloc(closure);
        }
        dealloc(body);
    }

    public static def runAsync(place:Place, body:()=>void):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val state = a.finishState();
        state.notifySubActivitySpawn(place);
        if (place.id == hereInt()) {
            execute(new Activity(deepCopy(body), state));
        } else {
            runAsyncAt(place.id, body, state); // optimized case
        }
        dealloc(body);
    }
    
    /**
     * Run async
     */
    public static def runAsync(clocks:Array[Clock](1){rail}, body:()=>void):void {
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
     * Run @Uncounted async at
     */
    public static def runUncountedAsync(place:Place, body:()=>void):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        if (place.id == hereInt()) {
            execute(new Activity(deepCopy(body), FinishState.UNCOUNTED_FINISH));
        } else {
            val closure = ()=> @x10.compiler.RemoteInvocation { execute(new Activity(body, FinishState.UNCOUNTED_FINISH)); };
            runClosureCopyAt(place.id, closure);
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
        async at(place) {
            activity().clockPhases = clockPhases;
            try {
                body();
                async at(box.home) {
                    val me2 = box();
                    me2.clockPhases = clockPhases;
                    me2.release();
                }
            } catch (e:Throwable) {
                async at(box.home) {
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
        private def this(Any) {
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
        async at(place) {
            activity().clockPhases = clockPhases;
            try {
                val result = eval();
                async at(box.home) {
                    val me2 = box();
                    me2.t = new Box[T](result);
                    me2.clockPhases = clockPhases;
                    me2.release();
                }
            } catch (e:Throwable) {
                async at(box.home) {
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
        runtime().staticMonitor.lock();
    }

    public static def StaticInitBroadcastDispatcherAwait() {
        runtime().staticMonitor.await();
    }

    public static def StaticInitBroadcastDispatcherUnlock() {
        runtime().staticMonitor.unlock();
    }

    public static def StaticInitBroadcastDispatcherNotify() {
        runtime().staticMonitor.release();
    }

    // atomic and when

    public static def enterAtomic() {
        runtime().atomicMonitor.lock();
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
        runtime().atomicMonitor.release();
    }

    public static def awaitAtomic():void {
        runtime().atomicMonitor.await();
    }

    // clocks

    /**
     * Next statement = next on all clocks in parallel.
     */
    @Native("cuda", "__syncthreads()")
    public static def next():void {
        ensureNotInAtomic();
        activity().clockPhases().next();
    }

    /**
     * Resume statement = resume on all clocks in parallel.
     */
    public static def resume():void = activity().clockPhases().resume();

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
        (state as FinishState.CollectingFinish[T]).accept(t,workerId());
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
    static def increaseParallelism():void {
        if (!STATIC_THREADS) {
            runtime().pool.increase();
        }
    }

    // notify the pool a worker resumed execution after a blocking operation
    static def decreaseParallelism(n:Int) {
        if (!STATIC_THREADS) {
            runtime().pool.decrease(n);
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
