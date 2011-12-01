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
import x10.compiler.PerProcess;
import x10.compiler.Pragma;
import x10.compiler.StackAllocate;

import x10.io.CustomSerialization;
import x10.io.SerialData;

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
 * 
 * @author tardieu
 */
public final class Runtime {

    // Debug print methods

    @Native("java", "java.lang.System.err.println(#any)")
    @Native("c++", "x10aux::system_utils::println(x10aux::to_string(#any)->c_str())")
    public native static def println(any:Any):void;

    @Native("java", "java.lang.System.err.println()")
    @Native("c++", "x10aux::system_utils::println(\"\")")
    public native static def println():void;

    @Native("java", "java.lang.System.err.printf(#fmt, #t)")
    @Native("c++", "x10aux::system_utils::printf(#fmt, #t)")
    public native static def printf[T](fmt:String, t:T):void;

    // Native runtime interface

    /**
     * Send active message to another place.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runClosureAt(#id, #body)")
    @Native("c++", "x10aux::run_closure_at(#id, #body)")
    public static native def x10rtSendMessage(id:Int, body:()=>void):void;

    /**
     * Send async to another place.
     * This is a special case of x10rtSendMessage where the active message consists in
     * creating an activity at the destination place with the specified body and finish state
     * and pushing this activity onto the deque of the active worker.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runAsyncAt(#id, #body, #finishState)")
    @Native("c++", "x10aux::run_async_at(#id, #body, #finishState)")
    public static native def x10rtSendAsync(id:Int, body:()=>void, finishState:FinishState):void;

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
    @Native("java","x10.runtime.impl.java.Runtime.eventProbe()")
    public static native def x10rtProbe():void;

    /**
     * Return a deep copy of the parameter.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.<#T$box>deepCopy(#o)")
    @Native("c++", "x10aux::deep_copy<#T >(#o)")
    public static native def deepCopy[T](o:T):T;

    // Memory management

    @Native("c++", "x10::lang::Object::dealloc_object((x10::lang::Object*)#o.operator->())")
    public static def deallocObject(o:Object):void {}

    @Native("c++", "x10aux::dealloc(#o.operator->())")
    public static def dealloc[T](o:()=>T):void {}

    @Native("c++", "x10aux::dealloc(#o.operator->())")
    public static def dealloc(o:()=>void):void {}

    // Environment variables

    @PerProcess static env = Configuration.loadEnv();

    @PerProcess public static STRICT_FINISH = Configuration.strict_finish();
    @PerProcess public static NTHREADS = Configuration.nthreads();
    @PerProcess public static MAX_THREADS = Configuration.max_threads();
    @PerProcess public static STATIC_THREADS = Configuration.static_threads();
    @PerProcess public static WARN_ON_THREAD_CREATION = Configuration.warn_on_thread_creation();

    // Runtime state

    @PerProcess static staticMonitor = new Monitor();
    @PerProcess public static atomicMonitor = new Monitor();
    @PerProcess static pool = new Pool();
    @PerProcess static finishStates = new FinishState.FinishStates();

    // Work-stealing runtime
    
    public static def wsInit():void {
        pool.wsBlockedContinuations = new Deque();
    }
    
    public static def wsFIFO():Deque {
        return worker().wsfifo;
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
     * A mortal object is collected when there are no remaining local refs even if remote refs might still exist
     */
    public interface Mortal {}

    static class Semaphore {
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

    public final static class Worker extends Thread implements CustomSerialization {
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
            x10rtProbe();
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

    static class Pool {
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

        public def wrapNativeThread():Worker {
            lock.lock();
            val i = size++;
            dead++; // native threads should terminate on their own
            lock.unlock();
            if (i >= MAX_THREADS) {
                println(here+": TOO MANY THREADS... ABORTING");
                System.exit(1);
            }
            if (WARN_ON_THREAD_CREATION) {
                println(here+": WARNING: A new OS-level thread was discovered (there are now "+size+" threads).");
            }
            val worker = new Worker(i, false);
            workers(i) = worker;
            return worker;
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
                x10rtProbe();
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
            x10rtInit();

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
                        x10rtSendMessage(i, ()=> @x10.compiler.RemoteInvocation {pool.latch.release();});
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
    public static def runAsync(place:Place, clocks:Rail[Clock], body:()=>void):void {
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
            x10rtSendMessage(place.id, closure);
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
            x10rtSendAsync(place.id, body, state); // optimized case
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
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        if (place.id == hereInt()) {
            execute(new Activity(deepCopy(body), FinishState.UNCOUNTED_FINISH));
        } else {
            val closure = ()=> @x10.compiler.RemoteInvocation { execute(new Activity(body, FinishState.UNCOUNTED_FINISH)); };
            x10rtSendMessage(place.id, closure);
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
        at(place) async {
            activity().clockPhases = clockPhases;
            try {
                body();
                val closure = ()=> @x10.compiler.RemoteInvocation { 
                    val me2 = (box as GlobalRef[RemoteControl{self==me,me!=null}]{home==here})();
                    me2.clockPhases = clockPhases;
                    me2.release();
                };
                x10rtSendMessage(box.home.id, closure);
                dealloc(closure);
            } catch (e:Throwable) {
                val closure = ()=> @x10.compiler.RemoteInvocation { 
                    val me2 = (box as GlobalRef[RemoteControl{self==me,me!=null}]{home==here})();
                    me2.e = e;
                    me2.clockPhases = clockPhases;
                    me2.release();
                };
                x10rtSendMessage(box.home.id, closure);
                dealloc(closure);
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
            	// TODO the second deep copy is needed only if eval makes its result escaped (it is very rare).
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
                val closure = ()=> @x10.compiler.RemoteInvocation { 
                    val me2 = (box as GlobalRef[Remote[T]{self==me,me!=null}]{home==here})();
                    // me2 has type Box[T{box.home==here}]... weird
                    me2.t = new Box[T{box.home==here}](result as T{box.home==here});
                    me2.clockPhases = clockPhases;
                    me2.release();
                };
                x10rtSendMessage(box.home.id, closure);
                dealloc(closure);
            } catch (e:Throwable) {
                val closure = ()=> @x10.compiler.RemoteInvocation { 
                    val me2 = (box as GlobalRef[Remote[T]{self==me,me!=null}]{home==here})();
                    me2.e = e;
                    me2.clockPhases = clockPhases;
                    me2.release();
                };
                x10rtSendMessage(box.home.id, closure);
                dealloc(closure);
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

    public static def wrapNativeThread():Worker {
        return pool.wrapNativeThread();
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
