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

import x10.compiler.Native;
import x10.compiler.Inline;
import x10.compiler.Pragma;
import x10.compiler.StackAllocate;
import x10.compiler.NativeCPPInclude;
import x10.compiler.Immediate;

import x10.io.Serializer;
import x10.io.Deserializer;
import x10.io.Unserializable;
import x10.io.Reader;
import x10.io.Writer;

import x10.util.concurrent.Condition;
import x10.util.concurrent.Latch;
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
    protected static native def x10rtBlockingProbeSupport():Boolean;

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
         * Request a memory allocator with the desired properties,
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

    public static env = Configuration.loadEnv();

    public static STRICT_FINISH = Configuration.strict_finish();
    public static NTHREADS = Configuration.nthreads();
    public static MAX_THREADS = Configuration.max_threads();
    public static STATIC_THREADS = Configuration.static_threads();
    public static STABLE_POOL_SIZE = Configuration.stable_pool_size();
    public static NUM_IMMEDIATE_THREADS = Configuration.num_immediate_threads();
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
        val activity = new Activity(epoch(), job, FinishState.UNCOUNTED_FINISH);
        if (FinishState.UNCOUNTED_FINISH.notifyActivityCreation(here, activity)) {
            pool.workers.submit(activity);
        }
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
                	try {
                    	x10rtSendMessage(j, cl, null);
                	} catch (dpe:DeadPlaceException){
                		// ignore - no need to worry about cancelling anything at a dead place
                	}
                }
                terminate();
            };
            for(var i:Long=numPlaces-1; i>0; i-=32) {
            	try {
                	x10rtSendMessage(i, cl1, null);
            	} catch (dpe:DeadPlaceException) {
            		// handle this portion of the fan-out directly, since the intermediate place is dead
            		val h = i as Int;
            		val cl = ()=> @x10.compiler.RemoteInvocation("start_4") {terminate();};
            		for (var j:Int=Math.max(1n, h-31n); j<h; ++j) {
            			try {
            				x10rtSendMessage(j, cl, null);
            			} catch (dpe2:DeadPlaceException){
            				// ignore - no need to worry about cancelling anything at a dead place
            			}
            		}
            	}
            }
        } else {
            val cl = ()=> @x10.compiler.RemoteInvocation("start_3") {terminate();};
            for (var i:Long=numPlaces-1; i>0; --i) {
            	try {
                	x10rtSendMessage(i, cl, null);
            	} catch (dpe:DeadPlaceException){
            		// ignore - no need to worry about cancelling anything at a dead place
            	}
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
            submitLocalActivity(new Activity(epoch, asyncBody, state, clockPhases));
        } else {
            val src = here;
            val closure = ()=> @x10.compiler.RemoteInvocation("runAsync") { 
                val activity = new Activity(epoch, body, state, clockPhases);
                submitRemoteActivity(epoch, activity, src, state);
            };
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
            submitLocalActivity(new Activity(epoch, asyncBody, state));
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
        submitLocalActivity(new Activity(epoch, body, state, clockPhases));
    }

    public static def runAsync(body:()=>void):void {
        // Do this before anything else
        val a = activity();
        a.ensureNotInAtomic();
        
        val epoch = a.epoch;
        val state = a.finishState();
        state.notifySubActivitySpawn(here);
        submitLocalActivity(new Activity(epoch, body, state));
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

            // Needed to prevent the worker that calls stopFinish
            // from trying to help and improperly scheduling an activity
            // from an unrelated finish.
	    activity().finishState().notifyRemoteContinuationCreated();

            // Spawn asynchronous activity
            val asyncBody = ()=>{
                val deser = new Deserializer(ser);
                val bodyCopy = deser.readAny() as ()=>void;
                bodyCopy();
            };
            submitLocalActivity(new Activity(epoch, asyncBody, FinishState.UNCOUNTED_FINISH));
        } else {
            val src = here;
            val closure = ()=> @x10.compiler.RemoteInvocation("runUncountedAsync") { 
                val activity = new Activity(epoch, body, FinishState.UNCOUNTED_FINISH);
                submitRemoteActivity(epoch, activity, src, FinishState.UNCOUNTED_FINISH);
            };
            x10rtSendMessage(place.id, closure, prof);
            Unsafe.dealloc(closure);
        }
        Unsafe.dealloc(body);
    }


    /**
     * Run @Immediate asyncat
     *
     * This method is only intended to be targeted by the compiler.
     * Therefore it is private.
     *
     * For correct operation, it requires that the body closure be 
     * annotated with @Immediate so that it is given a network id;
     * the compiler respects this invariant.
     */
    private static def runImmediateAsync(place:Place, body:()=>void, prof:Profile):void {
        if (place.id == hereLong()) {
            // copy body (at semantics) and then invoke immediately on current thread
            val copiedBody = Runtime.deepCopy(body, prof);
            try {
                copiedBody();
            } catch (e:CheckedThrowable) {
                if (!Configuration.silenceInternalWarnings()) {
                    println("WARNING: Ignoring uncaught exception in @Immediate async.");
                    e.printStackTrace();
                }
            }
        } else {
            x10rtSendMessage(place.id, body, prof, null);
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

        // Needed to prevent the worker that calls stopFinish
        // from trying to help and improperly scheduling an activity
        // from an unrelated finish.
	activity().finishState().notifyRemoteContinuationCreated();

        submitLocalActivity(new Activity(epoch, body, new FinishState.UncountedFinish()));
    }

    /**
     * a latch with a place for an exception
     */
    static class RemoteControl extends SimpleLatch implements Mortal {
        public def this() { super(); }
        var e:CheckedThrowable = null;
        var clockPhases:Clock.ClockPhases = null;
    }

    /**
     * Subvert X10 and target language exception checking.
     */
    @Native("c++", "::x10aux::throwException(::x10aux::nullCheck(#e))")
    @Native("java", "x10.runtime.impl.java.Runtime.throwCheckedWithoutThrows(#e)")
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
     * Implement the exception/blocking semantics of at(p) S by 
     * combining the fundamental primitives of finish (for blocking)
     * and at (p) async S (for remote task spawning). 
     *
     * We manipulate the finish state, clocks, and exception behavior of
     * the remote async's body to obtain the X10 language semantics.
     */
    public static def runAt(place:Place, body:()=>void, prof:Profile):void {
        Runtime.ensureNotInAtomic();

        // runAt to here is straightforward, execute a
        // deep copy of the body closure synchronously.
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

        // Carefully manipulate the finishState, etc. so that 
        // any asyncs spawned by body are governed by the current finishState
        // not the finishState of the finish we create intenally here to
        // enable blocking on the completion of remote execution of body
        val srcPlace = here;
        val realActivity = activity();
        val finishState = realActivity.finishState();
        val clockPhases = realActivity.clockPhases;
        val ser = new x10.io.Serializer();
        ser.writeAny(body);
        val bytes = ser.toRail();

        finishState.notifyShiftedActivitySpawn(place);
        val realActivityGR = GlobalRef[Activity](clockPhases == null ? null : realActivity);        
        try {
	    @Pragma(Pragma.FINISH_ASYNC) finish @x10.compiler.Profile(prof) at(place) async {
                if (finishState.notifyShiftedActivityCreation(srcPlace, null)) {
                    activity().clockPhases = clockPhases;
                    val syncFinishState = activity().swapFinish(finishState);
                    var exc:CheckedThrowable = null;
                    try {
                        // Actually deserialize and evaluate user body
                        val deser = new x10.io.Deserializer(bytes);
                        val bodyPrime = deser.readAny() as ()=>void;
                        bodyPrime();
                    } catch (e:AtCheckedWrapper) {
                        exc = e.getCheckedCause();
                    } catch (e:WrappedThrowable) {
                        exc = e.getCheckedCause();
                    } catch (e:CheckedThrowable) {
                        exc = e;
                    }

                    // Wind up internal activity created for synchronization
                    try {
                        activity().swapFinish(syncFinishState);

                        // Transmit potentially modified clockPhases back to srcPlace.
                        val finalClockPhases = activity().clockPhases;
                        activity().clockPhases = null;
                        if (finalClockPhases != null) {
                            @Pragma(Pragma.FINISH_ASYNC) finish at (srcPlace) async {
                                realActivityGR().clockPhases = finalClockPhases;
                            }
                        }
                    } catch (e:CheckedThrowable) {
                        // Suppress exceptions during windup of internal activity.
                        // Should not be user-visible.
                    } finally {
                        finishState.notifyShiftedActivityCompletion();
                        if (exc != null) syncFinishState.pushException(exc);
                    }
                }
            }
        } catch (e:MultipleExceptions) {
            // Peel off the layer of ME wrapping caused by the internal finish above.
            if (e.exceptions != null) {
                if (e.exceptions.size == 1) {
                    throwCheckedWithoutThrows(e.exceptions(0));
                } else {
                    // Prioritize DeadPlaceExceptions and rethrow exactly 1 of them 
                    // to maintain the X10 semantics view that an at is a place shift
                    // by the same single activity.
                    for (e2 in e.exceptions) {
                        if (e2 instanceof DeadPlaceException &&
                            (e2 as DeadPlaceException).place == place) {
                            throwCheckedWithoutThrows(e2);
                        }
                    }
                }
            }

            // Unexpected.  Rethrow e to enable debugging.
            throw e;
        } finally {
           realActivityGR.forget();
        }
    }

    static def debug(msg:String) {
        val nsec = System.nanoTime();
        val output = "[nsec=" + nsec + " place=" + here.id + " " + Runtime.activity() + "] " + msg;
        Console.OUT.println(output); Console.OUT.flush();
    }

    /**
     * Blocking execution of a closure via Immediate workers.
     * Unlike the fully-general runAt, the argument closure must
     * comply with the restrictions of @Immediate asyncs and
     * parallelism will not be increased in the source place.
     * This method must not be call from an Immediate worker.
     * The activity/worker calling runImmediateAt will be 'paused'
     * until the remote execution of the argument closure completes
     * (either normally or with an exception which will be thrown again).
     */
    public static def runImmediateAt(dst:Place, cl:()=>void):void {
        val verbose = FinishResilient.verbose;
        if (verbose>=1 && !Configuration.silenceInternalWarnings()) {
            if (Runtime.worker().promoted) {
                debug("DANGER: lowlevelAt called on @Immediate worker!");
                new Exception().printStackTrace();
            }
        }
        if (verbose>=4) debug("---- runImmediateAt called, dst.id=" + dst.id + " ("+here.id+"->"+dst.id+")");
        if (here == dst) {
            if (verbose>=4) debug("---- runImmediateAt locally calling cl()");
            cl();
            if (verbose>=4) debug("---- runImmediateAt locally executed, returning true");
        } else {
            // remote call
            val cond = new Condition();
            val condGR = GlobalRef[Condition](cond); 
            val exc = GlobalRef(new Cell[CheckedThrowable](null));
            if (verbose>=4) debug("---- runImmediateAt initiating remote execution");
            at (dst) @Immediate("finish_resilient_low_level_at_out") async {
                try {
                    if (verbose>=4) debug("---- runImmediateAt(remote) calling cl()");
                    cl();
                    if (verbose>=4) debug("---- runImmediateAt(remote) returned from cl()");
                    at (condGR) @Immediate("finish_resilient_low_level_at_back") async {
                        if (verbose>=4) debug("---- runImmediateAt(home) releasing cond");
                        condGR().release();
                    }
                } catch (t:Exception) {
                    if (verbose>=4) debug("---- runImmediateAt(remote) caught exception="+t);
                    at (condGR) @Immediate("finish_resilient_low_level_at_back_exc") async {
                        if (verbose>=4) debug("---- runImmediateAt(home) setting exc and releasing cond");
                        exc()(t);
                        condGR().release();
                    };
                }
                if (verbose>=4) debug("---- runImmediateAt(remote) finished");
            };
        
            if (verbose>=4) debug("---- runImmediateAt waiting for cond");
            cond.await();
            if (verbose>=4) debug("---- runImmediateAt released from cond");
	    // Unglobalize objects
	    condGR.forget();
            exc.forget();

            val t = exc()();
            if (t != null) {
                if (verbose>=4) debug("---- runImmediateAt throwing exception " + t);
                Runtime.throwCheckedWithoutThrows(t);
            }
            if (verbose>=4) debug("---- runImmediateAt completed");
        }
    }


    /*
     * [GlobalGC] Special version of runAt, which does not use activity, clock, exceptions
     *            Used in GlobalRef.java to implement changeRemoteCount
     * [DC] do not allow profiling of this call: seems it is not for application use?
     *
     * DG: TODO: Get rid of this method by using @Immediate asyncs and/or lowlevel at.
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
     * Implement the exception/blocking semantics of at(p) E by 
     * combining the fundamental primitives of finish (for blocking)
     * and at (p) async S (for remote task spawning). 
     *
     * We manipulate the finish state, clocks, and exception behavior of
     * the remote async's body to obtain the X10 language semantics.
     *
     * NOTE: We push the bulk of the implementation to this non-generic
     *       version to control codespace growth and limit the number 
     *       of actual remote closures (Native X10).
     */
    private static def evalAtImpl(place:Place, eval:()=>Any, prof:Profile):Any {
        Runtime.ensureNotInAtomic();

        // evalAt to here is straightforward, execute a
        // deep copy of the body closure synchronously
        // and return a deep copy of the result.
        if (place.id == hereLong()) {
            try {
                try {
                    val result = deepCopy(eval, prof)();
                    return deepCopy(result, prof);
                } catch (t:AtCheckedWrapper) {
                    throw t.getCheckedCause();
                }
            } catch (t:CheckedThrowable) {
                throwCheckedWithoutThrows(deepCopy(t, null));
            }
        }

        // Carefully manipulate the finishState, etc. so that 
        // any asyncs spawned by eval are governed by the current finishState
        // not the finishState of the finish we create intenally here to
        // enable blocking on the completion of remote execution of eval
        val srcPlace = here;
        val realActivity = activity();
        val finishState = realActivity.finishState();
        val clockPhases = realActivity.clockPhases;
        val ser = new x10.io.Serializer();
        ser.writeAny(eval);
        val bytes = ser.toRail();
        val resultCell = new Cell[Any](null);

        finishState.notifyShiftedActivitySpawn(place);
        val realActivityGR = GlobalRef[Activity](clockPhases == null ? null : realActivity);        
        val resultCellGR = GlobalRef[Cell[Any]](resultCell);
        try {
	    @Pragma(Pragma.FINISH_ASYNC) finish @x10.compiler.Profile(prof) at(place) async {
                if (finishState.notifyShiftedActivityCreation(srcPlace, null)) {
                    activity().clockPhases = clockPhases;
                    val syncFinishState = activity().swapFinish(finishState);
                    var exc:CheckedThrowable = null;
                    var resBytes:Rail[Byte] = null;
                    try {
                        // Actually deserialize and evaluate user eval closure
                        val deser = new x10.io.Deserializer(bytes);
                        val evalPrime = deser.readAny() as ()=>Any;
                        val res = evalPrime();
                        val ser2 = new x10.io.Serializer();
                        ser2.writeAny(res);
                        resBytes = ser2.toRail();
                    } catch (e:AtCheckedWrapper) {
                        exc = e.getCheckedCause();
                    } catch (e:WrappedThrowable) {
                        exc = e.getCheckedCause();
                    } catch (e:CheckedThrowable) {
                        exc = e;
                    }

                    // Wind up internal activity created for synchronization
                    activity().swapFinish(syncFinishState);

                    // Transmit result and potentially modified clockPhases back to srcPlace.
                    val finalClockPhases = activity().clockPhases;
                    activity().clockPhases = null;
                    val resBytes2 = resBytes;
                    try {
                        @Pragma(Pragma.FINISH_ASYNC) finish at (srcPlace) async {
                            if (finalClockPhases != null) {
                                realActivityGR().clockPhases = finalClockPhases;
                            }
                            if (resBytes2 != null) {
                                val deser2 = new x10.io.Deserializer(resBytes2);
                                resultCellGR().set(deser2.readAny());
                            }
                        }
                    } catch (me:MultipleExceptions) {
                        if (me.exceptions != null && me.exceptions.size == 1) {
                            syncFinishState.pushException(me.exceptions(0));
                        } else {
                            syncFinishState.pushException(me);
                        }
                    } finally {
                        finishState.notifyShiftedActivityCompletion();
                        if (exc != null) syncFinishState.pushException(exc);
                    }
                }
            }
        } catch (e:MultipleExceptions) {
            // Peel off the layer of ME wrapping caused by the internal finish above.
            if (e.exceptions != null) {
                if (e.exceptions.size == 1) {
                    throwCheckedWithoutThrows(e.exceptions(0));
                } else {
                    // Prioritize DeadPlaceExceptions and rethrow exactly 1 of them 
                    // to maintain the X10 semantics view that an at is a place shift
                    // by the same single activity.
                    for (e2 in e.exceptions) {
                        if (e2 instanceof DeadPlaceException &&
                            (e2 as DeadPlaceException).place == place) {
                            throwCheckedWithoutThrows(e2);
                        }
                    }
                }
            }

            // Unexpected.  Rethrow e to enable debugging.
            throw e;
        } finally {
           realActivityGR.forget();
           resultCellGR.forget();
        }
    
        // If we get here, the remote evaluation finished normally and we can
        // return the result of the evaluation from resultCell.
        return resultCell();
    }


    /** 
     * Implement the exception/blocking semantics of at(p) E by 
     * combining the fundamental primitives of finish (for blocking)
     * and at (p) async S (for remote task spawning). 
     */
    public static def evalAt[T](place:Place, eval:()=>T, prof:Profile):T {
        // NOTE: We are wrapping a non-generic impl of the core functionality
        //       to control codespace growth and limit the number of actual
        //       remote closures (Native X10).
        val eval2 = ()=>(eval() as Any);
        val r:T = evalAtImpl(place, eval2, prof) as T;
        Unsafe.dealloc(eval2); 
        return r;
    }


    private static def evalImmediateAtImpl(dst:Place, cl:()=>Any):Any {
        val verbose = FinishResilient.verbose;
        if (verbose>=1 && !Configuration.silenceInternalWarnings()) {
            if (Runtime.worker().promoted) {
                debug("DANGER: lowlevelAt called on @Immediate worker!");
                new Exception().printStackTrace();
            }
        }
        if (verbose>=4) debug("---- evalImmediateAt called, dst.id=" + dst.id + " ("+here.id+"->"+dst.id+")");
        if (here == dst) {
            if (verbose>=4) debug("---- evalImmediateAt locally calling cl()");
            val res = cl();
            if (verbose>=4) debug("---- evalImmediateAt locally executed, returning "+res);
            return res;
        }
        
        // remote call
        val cond = new Condition();
        val condGR = GlobalRef[Condition](cond); 
        val exc = GlobalRef(new Cell[CheckedThrowable](null));
        val result = new Cell[Any](null);
        val gresult = GlobalRef(result);
        if (verbose>=4) debug("---- evalImmediateAt remote execution");
        at (dst) @Immediate("finish_resilient_low_level_fetch_out") async {
            try {
                if (verbose>=4) debug("---- evalImmediateAt(remote) calling cl()");
                val r = cl();
                if (verbose>=4) debug("---- evalImmediateAt(remote) returned from cl()");
                at (condGR) @Immediate("finish_resilient_low_level_fetch_back") async {
                    if (verbose>=4) debug("---- evalImmediateAt(home) setting the result and done-flag");
                    gresult()(r); // set the result
                    condGR().release();
                };
            } catch (t:Exception) {
                at (condGR) @Immediate("finish_resilient_low_level_fetch_back_exc") async {
                    if (verbose>=4) debug("---- evalImmediateAt(home) setting exc and relasing cond");
                    exc()(t);
                    condGR().release();
                };
            }
            if (verbose>=4) debug("---- evalImmediateAt(remote) finished");
        };
        
        if (verbose>=4) debug("---- evalImmediateAt waiting for cond");
        cond.await();
        if (verbose>=4) debug("---- evalImmediateAt released from cond");
        val t = exc()();

	// Unglobalize objects
	condGR.forget();
        exc.forget();
	gresult.forget();

        if (t != null) {
            if (verbose>=4) debug("---- evalImmediateAt throwing exception " + t);
            Runtime.throwCheckedWithoutThrows(t);
        }
        if (verbose>=4) debug("---- evalImmediateAt returning value "+result());
        return result();
    }

    public static def evalImmediateAt[T](place:Place, eval:()=>T):T {
        // NOTE: We are wrapping a non-generic impl of the core functionality
        //       to control codespace growth and limit the number of actual
        //       remote closures (Native X10).
        val eval2 = ()=>(eval() as Any);
        val r:T = evalImmediateAtImpl(place, eval2) as T;
        Unsafe.dealloc(eval2); 
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
        if (RESILIENT_MODE == Configuration.RESILIENT_MODE_NONE ||
            RESILIENT_MODE == Configuration.RESILIENT_MODE_X10RT_ONLY) {
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
        } else {
            f = FinishResilient.make(null/*parent*/, null/*latch*/);
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

    static def submitLocalActivity(activity:Activity):void {
        if (activity.epoch < epoch()) throw new DeadPlaceException("Cancelled");
        if (activity.finishState().notifyActivityCreation(here, activity)) {
            if (!pool.deal(activity)) { 
                worker().push(activity);
            }
        }
    }

    // TODO: remove this variant of submitRemoteActivity once all backends and x10rt impls support epochs
    public static def submitRemoteActivity(body:()=>void, src:Place, finishState:FinishState):void {
        submitRemoteActivity(42, body, src, finishState);
    }

    public static def submitRemoteActivity(epoch:Long, body:()=>void, src:Place, finishState:FinishState):void {
        submitRemoteActivity(epoch, new Activity(epoch, body, finishState), src, finishState);
    }

    /**
     * Submit an activity origniating in a remote Place for execution
     * by the local Runtime.
     *
     * IMPORTANT NOTE: This method can be invoked from an @Immediate worker thread,
     *                 therefore execution of this method may not block or pause.
     *                 (Same restrictions as the body of an @Immediate async).
     */
    public static def submitRemoteActivity(epoch:Long, activity:Activity, src:Place, finishState:FinishState):void {
        if (epoch > epoch()) {
            pool.flush(epoch);
        }
        if (epoch == epoch()) {
            if (finishState.notifyActivityCreation(src, activity)) {
                worker().push(activity);
            }
        }
    }

    public static def probe() {
        worker().probe();
    }

    // notify the pool a worker is about to execute a blocking operation
    public static def increaseParallelism():void {
        if (!STATIC_THREADS && !STABLE_POOL_SIZE) {
            pool.increase();
        }
    }

    // notify the pool a worker resumed execution after a blocking operation
    public static def decreaseParallelism(n:Int) {
        if (!STATIC_THREADS && !STABLE_POOL_SIZE)  {
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

    /**
     * Sleep for the specified number of milliseconds.
     * [IP] NOTE: Unlike Java, x10 sleep() simply exits when interrupted.
     * @param millis the number of milliseconds to sleep
     * @return true if completed normally, false if interrupted
     */
    public static def sleep(millis:Long):Boolean {
        try {
            Runtime.increaseParallelism();
            Thread.sleep(millis);
            Runtime.decreaseParallelism(1n);
            return true;
        } catch (e:InterruptedException) {
            Runtime.decreaseParallelism(1n);
            return false;
        }
    }

    /**
     * Sleep for the specified number of milliseconds.
     * @param millis the number of milliseconds to sleep
     * @return true if completed normally, false if interrupted
     */
    public static def threadSleep(millis:Long):Boolean {
        try {
            Thread.sleep(millis);
            return true;
        } catch (e:InterruptedException) {
            return false;
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
