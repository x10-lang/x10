/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeClass;
import x10.compiler.NativeDef;
import x10.util.HashMap;
import x10.util.Pair;
import x10.util.Random;
import x10.util.Stack;
import x10.util.concurrent.atomic.AtomicInteger;

/**
 * @author tardieu
 */
public final class Runtime {

    @Native("java", "java.lang.System.out.println(#1)")
    @Native("c++", "x10aux::system_utils::println((#1)->toString()->c_str())")
    public native static def println(o:Object) : Void;

    /**
     * Set system exit code
     */
    @Native("java", "x10.runtime.impl.java.Runtime.setExitCode(#1)")
    @Native("c++", "(x10aux::exitCode = (#1))")
    public static def setExitCode(code: int): void {}

    // Configuration options

    @Native("java", "x10.runtime.impl.java.Runtime.PLACE_CHECKS")
    @Native("c++", "(x10_boolean) false")
    public const PLACE_CHECKS = true;

    @Native("java", "x10.runtime.impl.java.Runtime.NO_STEALS")
    @Native("c++", "x10aux::no_steals()")
    public const NO_STEALS = false;

    @Native("java", "x10.runtime.impl.java.Runtime.MAX_PLACES")
    @Native("c++", "x10aux::num_places")
    public const MAX_PLACES = 4;

    @Native("java", "x10.runtime.impl.java.Runtime.MAX_PLACES")
    @Native("c++", "x10aux::num_hosts")
    public const MAX_HOSTS = 4;

    @Native("java", "x10.runtime.impl.java.Runtime.INIT_THREADS")
    @Native("c++", "x10aux::num_threads()")
    public const INIT_THREADS = 1;

    @Native("java", "x10.runtime.impl.java.Runtime.STATIC_THREADS")
    @Native("c++", "x10aux::static_threads()")
    public const STATIC_THREADS = false;

    /**
     * Find number of children under a place.
     * For hosts, this returns the number of accelerators at that host.
     * For accelerators, it returns 0.
     */
    @Native("c++", "x10aux::num_children(#1)")
    public static def numChildren(id:Int) = 0;

    /**
     * Returns whether a place is a host.
     */
    @Native("c++", "x10aux::is_host(#1)")
    public static def isHost(id:Int) = true;

    /**
     * Returns whether a place is an SPE of a Cell CPU.
     */
    @Native("c++", "x10aux::is_spe(#1)")
    public static def isSPE(id:Int) = false;

    /**
     * Returns whether a place is a CUDA GPU.
     */
    @Native("c++", "x10aux::is_cuda(#1)")
    public static def isCUDA(id:Int) = false;

    /**
     * Find parent of a place.
     * For hosts, this returns the host itself.
     * For accelerators, it is the host of the accelerator.
     */
    @Native("c++", "x10aux::parent(#1)")
    public static def parent(id:Int) = id;

    /**
     * Iterate over the children of a place.
     * Use i between 0 and numChildren(p)-1 inclusive.
     * Throws BadPlaceException if i invalid.
     */
    @Native("c++", "x10aux::child(#1,#2)")
    public static def child(p:Int, i:Int):Int { throw new BadPlaceException(); }

    /**
     * Return the index of a given child, within a place.
     * Throws BadPlaceException if given place is not a child.
     */
    @Native("c++", "x10aux::child_index(#1)")
    public static def childIndex(id:Int):Int { throw new BadPlaceException(); }

    /**
     * Run body at place(id).
     * May be implemented synchronously or asynchronously.
     * Body cannot spawn activities, use clocks, or raise exceptions.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runAt(#1, #2)")
    @Native("c++", "x10aux::run_at(#1, #2)")
    public static def runAt(id:Int, body:()=>Void):Void { body(); }

    /**
     * Java: run body synchronously at place(id) in the same node as the current place.
     * C++: run body. (no need for a native implementation)
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runAt(#1, #2)")
    public static def runAtLocal(id:Int, body:()=>Void):Void { body(); }

    /**
     * Return true if place(id) is in the current node.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.local(#1)")
    public static def local(id:Int):Boolean = id == here.id;

    /**
     * Process one incoming message if any (non-blocking).
     */
    @Native("c++", "x10aux::event_probe()")
    public static def event_probe():Void {}

    /** Accessors for native performance counters
     */
    @Native("c++","x10aux::asyncs_sent")
    static def getAsyncsSent() = 0 as Long;

    @Native("c++","x10aux::asyncs_sent = #1")
    static def setAsyncsSent(v:Long) { }

    @Native("c++","x10aux::asyncs_received")
    static def getAsyncsReceived() = 0 as Long;

    @Native("c++","x10aux::asyncs_received = #1")
    static def setAsyncsReceived(v:Long) { }

    @Native("c++","x10aux::serialized_bytes")
    static def getSerializedBytes() = 0 as Long;

    @Native("c++","x10aux::serialized_bytes = #1")
    static def setSerializedBytes(v:Long) { }

    @Native("c++","x10aux::deserialized_bytes")
    static def getDeserializedBytes() = 0 as Long;

    @Native("c++","x10aux::deserialized_bytes = #1")
    static def setDeserializedBytes(v:Long) { }

    @Native("c++", "x10::lang::Object::dealloc_object((x10::lang::Object*)#1.operator->())")
    public static def deallocObject (o:Object) { }

    @Native("c++", "x10aux::dealloc(#4.operator->())")
    public static def dealloc[T] (o:()=>T) { }

    @Native("c++", "x10aux::dealloc(#1.operator->())")
    public static def dealloc (o:()=>Void) { }

    @Native("c++", "x10aux::DeserializationDispatcher::registerHandlers()")
    public static def registerHandlers() {}


    @NativeClass("java", "x10.runtime.impl.java", "Deque")
    @NativeClass("c++", "x10.lang", "Deque")
    public static final class Deque {
        public native def this();

        public native def size():Int;

        public native def poll():Object;

        public native def push(t:Object):Void;

        public native def steal():Object;
    }


    @NativeClass("java", "java.util.concurrent.locks", "ReentrantLock")
    @NativeClass("c++", "x10.lang", "Lock__ReentrantLock")
    public static class Lock {
        public native def this();

        public native def lock():Void;

        public native def tryLock():Void;

        public native def unlock():Void;

        public native def getHoldCount():Int;
    }


    static class Monitor extends Lock {
        /**
         * Parked threads
         */
        private val threads = new Stack[Thread]();

        /**
         * Park calling thread
         * Increment blocked thread count
         * Must be called while holding the lock
         * Must not be called while holding the lock more than once
         */
        def await():Void {
            Runtime.increaseParallelism();
            val thread = Thread.currentThread();
            threads.push(thread);
            while (threads.contains(thread)) {
                unlock();
                Runtime.park();
                lock();
            }
        }

        /**
         * Unpark every thread
         * Decrement blocked thread count
         * Release the lock
         * Must be called while holding the lock
         */
        def release():Void {
            val size = threads.size();
            if (size > 0) {
                Runtime.decreaseParallelism(size);
                for (var i:Int = 0; i<size; i++) Runtime.unpark(threads.pop());
            }
            unlock();
        }
    }


    static class Latch extends Monitor implements ()=>Boolean {
        private var state:Boolean = false;

        public def release():Void {
            lock();
            state = true;
            super.release();
        }

        public def await():Void {
            // avoid locking if state == true
            if (!state) {
                lock();
                while (!state) super.await();
                unlock();
            }
        }

        public def apply():Boolean = state; // memory model?
    }


    static class Semaphore {
        private val lock = new Lock();

        private val threads = new Stack[Thread]();

        private var permits:Int;

        def this(n:Int) {
            permits = n;
        }

        private static def min(i:Int, j:Int):Int = i<j ? i : j;

        def release(n:Int):Void {
            lock.lock();
            permits += n;
            val m = min(permits, min(n, threads.size()));
            for (var i:Int = 0; i<m; i++) {
                threads.pop().unpark();
            }
            lock.unlock();
        }

        def release():Void {
            release(1);
        }

        def reduce(n:Int):Void {
            lock.lock();
            permits -= n;
            lock.unlock();
        }

        def acquire():Void {
            lock.lock();
            val thread = Thread.currentThread();
            while (permits <= 0) {
                threads.push(thread);
                while (threads.contains(thread)) {
                    lock.unlock();
                    Thread.park();
                    lock.lock();
                }
            }
            --permits;
            lock.unlock();
        }

        def available():Int = permits;
    }


    static class ClockPhases extends HashMap[Clock,Int] {
        static def make(clocks:ValRail[Clock], phases:ValRail[Int]):ClockPhases! {
            val clockPhases = new ClockPhases();
            for(var i:Int = 0; i < clocks.length; i++) clockPhases.put(clocks(i), phases(i));
            return clockPhases;
        }

        def register(clocks:ValRail[Clock]) {
            return ValRail.make[Int](clocks.length, (i:Int)=>(clocks(i)).register());
        }

        def next() {
            for(clock:Clock in keySet()) clock.resumeUnsafe();
            for(clock:Clock in keySet()) clock.nextUnsafe();
        }

        def drop() {
           for(clock:Clock in keySet()) clock.dropUnsafe();
        }
    }


    /**
     * A mortal object is garbage collected when there are no remaining local refs even if remote refs might still exist
     */
    public interface Mortal { }


    static interface FinishState {

        /**
         * An activity is spawned under this finish (called by spawner).
         */
        global def notifySubActivitySpawn(place:Place):Void;

        /**
         * An activity is created under this finish (called by spawnee).
         */
        global def notifyActivityCreation():Void;

        /**
         * An activity created under this finish has terminated.
         * Also called be the activity governing the finish when it completes the finish body.
         */
        global def notifyActivityTermination():Void;

        /**
         * Push an exception onto the stack.
         */
        global def pushException(t:Throwable):Void;

        /**
         * Wait for pending subactivities to complete.
         */
        def waitForFinish(safe:Boolean):Void;
    }


    static class FinishStates implements (RootFinish)=>RemoteFinish {

        private val map = new HashMap[RootFinish, RemoteFinish!]();
        private val lock = new Lock();

        public def apply(rootFinish:RootFinish):RemoteFinish {
            lock.lock();
            val finishState = map.getOrElse(rootFinish, null);
            if (null != finishState) {
                lock.unlock();
                return finishState;
            }
            val remoteFinish = new RemoteFinish();
            map.put(rootFinish, remoteFinish);
            lock.unlock();
            return remoteFinish;
        }
    }


    static class RootFinish extends Latch implements FinishState, Mortal {
        private val counts:Rail[Int]!;
        private var exceptions:Stack[Throwable]!;

        def this() {
            val c = Rail.make[Int](Place.MAX_PLACES, (Int)=>0);
            c(here.id) = 1;
            counts = c;
        }

        private def notifySubActivitySpawnLocal(place:Place):Void {
            lock();
            counts(place.parent().id)++;
            unlock();
        }

        private def notifyActivityTerminationLocal():Void {
            lock();
            counts(here.id)--;
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (counts(i) != 0) {
                    unlock();
                    return;
                }
            }
            release();
            unlock();
        }

        private def pushExceptionLocal(t:Throwable):Void {
            lock();
            if (null == exceptions) exceptions = new Stack[Throwable]();
            exceptions.push(t);
            unlock();
        }

        public def waitForFinish(safe:Boolean):Void {
            if (!NO_STEALS && safe) Runtime.join(this);
            await();
            if (null != exceptions) {
                if (exceptions.size() == 1) {
                    val t = exceptions.peek();
                    if (t instanceof Error) {
                        throw t as Error;
                    }
                    if (t instanceof RuntimeException) {
                        throw t as RuntimeException;
                    }
                }
                throw new MultipleExceptions(exceptions);
            }
        }

       def notify(rail:ValRail[Int]!):Void {
            var b:Boolean = true;
            lock();
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                counts(i) += rail(i);
                if (counts(i) != 0) b = false;
            }
            if (b) release();
            unlock();
        }

        def notify2(rail:ValRail[Pair[Int,Int]]!):Void {
            lock();
            for(var i:Int=0; i<rail.length; i++) {
                counts(rail(i).first) += rail(i).second;
            }
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (counts(i) != 0) {
                    unlock();
                    return;
                }
            }
            release();
            unlock();
        }

        def notify(rail:ValRail[Int]!, t:Throwable):Void {
            pushExceptionLocal(t);
            notify(rail);
        }

        def notify2(rail:ValRail[Pair[Int,Int]]!, t:Throwable):Void {
            pushExceptionLocal(t);
            notify2(rail);
        }

        public global def notifySubActivitySpawn(place:Place):Void {
            if (here.equals(home)) {
                (this as RootFinish!).notifySubActivitySpawnLocal(place);
            } else {
                Runtime.proxy(this).notifySubActivitySpawn(place);
            }
        }

        public global def notifyActivityCreation():Void {
            if (!here.equals(home)) Runtime.proxy(this).notifyActivityCreation();
        }

        public global def notifyActivityTermination():Void {
            if (here.equals(home)) {
                (this as RootFinish!).notifyActivityTerminationLocal();
            } else {
                Runtime.proxy(this).notifyActivityTermination(this);
            }
        }

        public global def pushException(t:Throwable):Void {
            if (here.equals(home)) {
                (this as RootFinish!).pushExceptionLocal(t);
            } else {
                Runtime.proxy(this).pushException(t);
            }
        }
    }


    static class RemoteFinish {
        /**
         * The Exception Stack is used to collect exceptions
         * issued when activities associated with this finish state terminate abruptly.
         */
        private var exceptions:Stack[Throwable]!;

        /**
         * The monitor is used to serialize updates to the finish state.
         */
        private val lock = new Lock();

        /**
         * Keep track of the number of activities associated with this finish state.
         */
        private val counts = Rail.make[Int](Place.MAX_PLACES, (Int)=>0) as Rail[Int]!;

        private val message = Rail.make[Int](Place.MAX_PLACES, (Int)=>here.id) as Rail[Int]!;
        private var length:Int = 1;

        private var count:AtomicInteger = new AtomicInteger(0);

        public def notifyActivityCreation():Void {
            count.getAndIncrement();
        }

        /**
         * An activity created under this finish has been created. Increment the count
         * associated with the finish.
         */
        public def notifySubActivitySpawn(place:Place):Void {
            lock.lock();
            if (counts(place.id)++ == 0 && here.id != place.id) {
                message(length++) = place.id;
            }
            lock.unlock();
        }

        /**
         * An activity created under this finish has terminated.
         */
        public def notifyActivityTermination(r:RootFinish):Void {
            lock.lock();
            counts(here.id)--;
            if (count.decrementAndGet() > 0) {
                lock.unlock();
                return;
            }
            val e = exceptions;
            exceptions = null;
            if (2*length > Place.MAX_PLACES) {
                val m = counts as ValRail[Int];
                for (var i:Int=0; i<Place.MAX_PLACES; i++) counts(i) = 0;
                length = 1;
                lock.unlock();
                if (null != e) {
                    val t:Throwable;
                    if (e.size() == 1) {
                        t = e.peek();
                    } else {
                        t = new MultipleExceptions(e);
                    }
                    val closure = () => { (r as RootFinish!).notify(m, t); deallocObject(m); };
                    runAt(r.home.id, closure);
                    dealloc(closure);
                } else {
                    val closure = () => { (r as RootFinish!).notify(m); deallocObject(m); };
                    runAt(r.home.id, closure);
                    dealloc(closure);
                }
                deallocObject(m);
            } else {
                val m = ValRail.make[Pair[Int,Int]](length, (i:Int)=>Pair[Int,Int](message(i), counts(message(i))));
                for (var i:Int=0; i<Place.MAX_PLACES; i++) counts(i) = 0;
                length = 1;
                lock.unlock();
                if (null != e) {
                    val t:Throwable;
                    if (e.size() == 1) {
                        t = e.peek();
                    } else {
                        t = new MultipleExceptions(e);
                    }
                    val closure = () => { (r as RootFinish!).notify2(m, t); deallocObject(m); };
                    runAt(r.home.id, closure);
                    dealloc(closure);
                } else {
                    val closure = () => { (r as RootFinish!).notify2(m) ; deallocObject(m); };
                    runAt(r.home.id, closure);
                    dealloc(closure);
                }
                deallocObject(m);
            }
        }

        /**
         * Push an exception onto the stack.
         */
        public def pushException(t:Throwable):Void {
            lock.lock();
            if (null == exceptions) exceptions = new Stack[Throwable]();
            exceptions.push(t);
            lock.unlock();
        }
    }


    @NativeClass("java", "x10.runtime.impl.java", "Thread")
    @NativeClass("c++", "x10.lang", "Thread")
    final static class Thread {

        /**
         * Allocates new thread in current place
         */
        public native def this(body:()=>Void, name:String);

        public static native def currentThread():Thread!;

        public native def start():Void;

        public native static def sleep(millis:Long):Void throws InterruptedException;

        public native static def sleep(millis:Long, nanos:Int):Void throws InterruptedException;

        public native static def park():Void;

        public native static def parkNanos(nanos:Long):Void;

        public native global def unpark():Void;

        @NativeDef("c++")
        public def worker():Worker! {
            // TODO fix place inconsistency in java backend
            @Native("java", "return ((x10.lang.Runtime.Worker)(__NATIVE_FIELD__.worker()));")
            { return null; }
        }

        public native def worker(worker:Worker!):Void;

        public native def name():String;

        public native def name(name:String):void;

        public native def locInt():Int;
    }


    public static class Activity {
        /**
         * the finish state governing the execution of this activity (may be remote)
         */
        val finishState:FinishState;

        /**
         * safe to run pending jobs while waiting for a finish (temporary)
         */
        val safe:Boolean;

        /**
         * The user-specified code for this activity.
         */
         private val body:()=>Void;

        /**
         * The mapping from registered clocks to phases for this activity.
         * Lazily created.
         */
        var clockPhases:ClockPhases!;

        /**
         * The finish states for the finish statements currently executed by this activity.
         * Lazily created.
         */
        var finishStack:Stack[FinishState!]!;

        /**
         * Create activity.
         */
        def this(body:()=>Void, finishState:FinishState, safe:Boolean) {
            this.finishState = finishState;
            this.safe = safe;
            finishState.notifyActivityCreation();
            this.body = body;
        }

        /**
         * Create clocked activity.
         */
        def this(body:()=>Void, finishState:FinishState, clocks:ValRail[Clock], phases:ValRail[Int]) {
            this(body, finishState, false);
            clockPhases = ClockPhases.make(clocks, phases);
        }

        /**
         * Run activity.
         */
        def run():Void {
        try {
            body();
        } catch (t:Throwable) {
            finishState.pushException(t);
        }
        if (null != clockPhases) clockPhases.drop();
            finishState.notifyActivityTermination();
        dealloc(body);
        }
    }


    final static class Worker implements ()=>Void {
            val latch:Latch!;
        // release the latch to stop the worker

        // bound on loop iterations to help j9 jit
        const BOUND = 100;

        // activity (about to be) executed by this worker
        private var activity:Activity = null;

        // pending activities
        private val queue = new Deque();

        // random number generator for this worker
        private val random:Random!;

        def this(latch:Latch!, p:Int) {
            this.latch = latch;
            random = new Random(p + (p << 8) + (p << 16) + (p << 24));
        }

        // all methods are local

        // return size of the deque
        public def size():Int = queue.size();

        // return activity executed by this worker
        def activity()  = activity;

        // poll activity from the bottom of the deque
        private def poll() = queue.poll() as Activity;

        // steal activity from the top of the deque
        def steal() = queue.steal() as Activity;

        // push activity at the bottom of the deque
        def push(activity:Activity!):Void = queue.push(activity);

        // run pending activities
        public def apply():Void {
            try {
                while (loop(latch, true));
            } catch (t:Throwable) {
                println("Uncaught exception in worker thread");
                t.printStackTrace();
            } finally {
                Runtime.report();
            }
        }

        // run activities while waiting on finish
        def join(latch:Latch!):Void {
            val tmp = activity; // save current activity
            while (loop(latch, false));
            activity = tmp; // restore current activity
        }

        // inner loop to help j9 jit
        private def loop(latch:Latch!, block:Boolean):Boolean {
            for (var i:Int = 0; i < BOUND; i++) {
                if (latch()) return false;
                activity = poll();
                if (activity == null) {
                    activity = Runtime.scan(random, latch, block);
                    if (activity == null) return false;
                }
                Runtime.run(activity);
            }
            return true;
        }
    }


    public static class Pool implements ()=>Void {
        private val latch:Latch!;
        private var size:Int; // the number of workers in the pool

        private var spares:Int = 0; // the number of spare workers in the pool

        private val lock = new Lock();

        private val semaphore = new Semaphore(0);

        // an upper bound on the number of workers
        private const MAX = 1000;

        // the workers in the pool
        private val workers:Rail[Worker!]!;

        // the threads in the pool
        private val threads:Rail[Thread!]!;

        def this(latch:Latch!, size:Int) {
            this.latch = latch;
            this.size = size;
            val workers = Rail.make[Worker!](MAX);
            val threads = Rail.make[Thread!](size);

            // worker for the master thread
            val master = new Worker(latch, 0);
            workers(0) = master;
            threads(0) = Thread.currentThread();
            Thread.currentThread().worker(master);

            // other workers
            for (var i:Int = 1; i<size; i++) {
                val worker = new Worker(latch, i);
                workers(i) = worker;
                threads(i) = new Thread(worker.apply.(), "thread-" + i);
                threads(i).worker(worker);
            }
            this.workers = workers;
            this.threads = threads;
        }

        public def apply():Void {
            val s = size;
            for (var i:Int = 1; i<s; i++) {
                threads(i).start();
            }
            workers(0)();
            while (size > 0) Thread.park();
        }

        // all methods are local

        // notify the pool a worker is about to execute a blocking operation
        def increase():Void {
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
                assert (i < MAX);
                if (i >= MAX) {
                    println("TOO MANY THREADS... ABORTING");
                    System.exit(1);
                }
                val worker = new Worker(latch as Latch!, i);
                workers(i) = worker;
                val thread = new Thread(worker.apply.(), "thread-" + i);
                thread.worker(worker);
                thread.start();
            }
        }

        // notify the pool a worker resumed execution after a blocking operation
        def decrease(n:Int):Void {
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
            size--;
            if (size == 0) threads(0).unpark();
            lock.unlock();
        }

        // scan workers for activity to steal
        def scan(random:Random!, latch:Latch!, block:Boolean):Activity {
            var activity:Activity = null;
            var next:Int = random.nextInt(size);
            for (;;) {
                event_probe();
                if (null != workers(next)) { // avoid race with increase method
                    activity = workers(next).steal();
                }
                if (null != activity || latch()) return activity;
                if (semaphore.available() < 0) {
                    if (block) {
                        semaphore.release();
                        semaphore.acquire();
                    } else {
                        return activity;
                    }
                }
                if (++next == size) next = 0;
            }
        }
    }


    // for debugging
    const PRINT_STATS = false;

    // instance fields

    // per process members
    private val pool:Pool!;

    // per place members
    private val monitor = new Monitor();
    private val finishStates = new FinishStates();

    // constructor

    private def this(pool:Pool!):Runtime {
        this.pool = pool;
    }

    /**
     * The runtime instance associated with each place
     */
    private const runtime = PlaceLocalHandle[Runtime]();

    static def proxy(rootFinish:RootFinish) = runtime().finishStates(rootFinish);

    /**
     * Return the current worker
     */
    private static def worker():Worker! = Thread.currentThread().worker();

    /**
     * Return the current activity
     */
    public static def activity():Activity!
               = worker().activity() as Activity!;

    /**
     * Return the current place
     */
    public static def here():Place = Thread.currentThread().home;

    /**
     * The amount of unscheduled activities currently available to this worker thread.
     * Intended for use in heuristics that control async spawning
     * based on the current amount of surplus work.
     */
    public static def surplusActivityCount():int = worker().size();

    /**
     * Run main activity in a finish
     */
    public static def start(init:()=>Void, body:()=>Void):Void {
        val rootFinish = new RootFinish();
        val pool = new Pool(rootFinish, INIT_THREADS);
        try {
            for (var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (local(i)) {
                    runAtLocal(i, ()=>runtime.set(new Runtime(pool)));
                }
            }
            registerHandlers();
            if (Thread.currentThread().locInt() == 0) {
                execute(new Activity(()=>{finish init(); body();}, rootFinish, true));
                pool();
                if (!local(Place.MAX_PLACES - 1)) {
                    for (var i:Int=1; i<Place.MAX_PLACES; i++) {
                        runAt(i, ()=>{ val w=worker(); w.latch.release()});
                    }
                }
                rootFinish.waitForFinish(false);
            } else {
                pool();
            }
        } finally {
            if (PRINT_STATS) {
                println("ASYNC SENT AT PLACE " + here.id +" = " + getAsyncsSent());
                println("ASYNC RECV AT PLACE " + here.id +" = " + getAsyncsReceived());
            }
        }
    }

    static def report():Void {
        runtime().pool.release();
    }

    // async -> at statement -> at expression -> future
    // do not introduce cycles!!!

    /**
     * Run async
     */
    public static def runAsync(place:Place, clocks:ValRail[Clock], body:()=>Void):Void {
        val state = currentState();
        val phases = clockPhases().register(clocks);
        state.notifySubActivitySpawn(place);
        if (place.id == Thread.currentThread().locInt()) {
            execute(new Activity(body, state, clocks, phases));
        } else {
            val c = ()=>execute(new Activity(body, state, clocks, phases));
            runAt(place.id, c);
        }
    }

    public static def runAsync(place:Place, body:()=>Void):Void {
        val state = currentState();
        state.notifySubActivitySpawn(place);
        val ok = safe();
        if (place.id == Thread.currentThread().locInt()) {
            execute(new Activity(body, state, ok));
        } else {
            var closure:()=>Void;
//            val closure =
//                ok ? (free ? ()=>execute(new Activity(body, state, true, true))
//                           : ()=>execute(new Activity(body, state, true, false)))
//                   : (free ? ()=>execute(new Activity(body, state, false, true))
//                           : ()=>execute(new Activity(body, state, false, false)));
            // Workaround for XTENLANG_614
            if (ok) {
                closure = ()=>execute(new Activity(body, state, true));
            } else {
                closure = ()=>execute(new Activity(body, state, false));
            }
            runAt(place.id, closure);
            dealloc(closure);
        }
    }

    public static def runAsync(clocks:ValRail[Clock], body:()=>Void):Void {
        val state = currentState();
        val phases = clockPhases().register(clocks);
        state.notifySubActivitySpawn(here);
        execute(new Activity(body, state, clocks, phases));
    }

    public static def runAsync(body:()=>Void):Void {
        val state = currentState();
        state.notifySubActivitySpawn(here);
        execute(new Activity(body, state, safe()));
    }

    /**
     * Run at statement
     */
    static class RemoteControl {
        var e:Box[Throwable] = null;
        val latch = new Latch();
    }

    public static def runAt(place:Place, body:()=>Void):Void {
        val box = new RemoteControl();
        async (place) {
            try {
                body();
                async (box) box.latch.release();
            } catch (e:Throwable) {
                async (box) {
                    box.e = e;
                    box.latch.release();
                }
            }
        }
        if (!NO_STEALS && safe()) Runtime.join(box.latch);
        box.latch.await();
        if (null != box.e) {
            val x = box.e as Throwable;
            if (x instanceof Error)
                throw x as Error;
            if (x instanceof RuntimeException)
                throw x as RuntimeException;
        }
    }

    public static def runAt(place:Object, body:()=>Void):Void {
        runAt(place.home, body);
    }

    /**
     * Eval at expression
     */
    static class Remote[T] {
        var t:Box[T] = null;
        var e:Box[Throwable] = null;
        val latch = new Latch();
    }

    public static def evalAt[T](place:Place, eval:()=>T):T {
        val box = new Remote[T]();
        async (place) {
            try {
                val result = eval();
                async (box) {
                    box.t = result;
                    box.latch.release();
                }
            } catch (e:Throwable) {
                async (box) {
                    box.e = e;
                    box.latch.release();
                }
            }
        }
        if (!NO_STEALS && safe()) Runtime.join(box.latch);
        box.latch.await();
        if (null != box.e) {
            val x = box.e as Throwable;
            if (x instanceof Error)
                throw x as Error;
            if (x instanceof RuntimeException)
                throw x as RuntimeException;
        }
        return box.t.value;
    }

    public static def evalAt[T](place:Object, eval:()=>T):T = evalAt[T](place.home, eval);

    /**
     * Eval future expression
     */
    public static def evalFuture[T](place:Place, eval:()=>T):Future[T] {
        val f = at (place) {
        val f1 = new Future[T](eval);
                async f1.run();
                f1
        };
        return f;
    }


    // place checks

    /**
     * Java place check
     */
    public static def placeCheck(p:Place, o:Object):Object {
        if (PLACE_CHECKS && null != o
            && o instanceof Object
            && !(o instanceof Worker)
            && (o as Object!).home.id != p.id) {
            println("BAD PLACE EXCEPTION");
            throw new BadPlaceException("object="
                        + o.toString()
                        + " access at place=" + p);
        }
        return o;
    }


    // atomic, await, when

    /**
     * Lock current place
     * not reentrant!
     */
    public static def lock():Void {
        runtime().monitor.lock();
    }

    /**
     * Wait on current place lock
     * Must be called while holding the place lock
     */
    public static def await():Void {
        runtime().monitor.await();
    }

    /**
     * Unlock current place
     * Notify all
     */
    public static def release():Void {
        runtime().monitor.release();
    }


    // sleep

    /**
     * Sleep for the specified number of milliseconds.
     * [IP] NOTE: Unlike Java, x10 sleep() simply exits when interrupted.
     * @param millis the number of milliseconds to sleep
     * @return true if completed normally, false if interrupted
     */
    public static def sleep(millis:long):Boolean {
        try {
            increaseParallelism();
            Thread.sleep(millis);
            decreaseParallelism(1);
            return true;
        } catch (e:InterruptedException) {
            decreaseParallelism(1);
            return false;
        }
    }


    // clocks

    /**
     * Return the clock phases for the current activity
     */
    static def clockPhases():ClockPhases! {
        val a = activity();
        if (null == a.clockPhases)
            a.clockPhases = new ClockPhases();
        return a.clockPhases;
    }

    /**
     * Next statement = next on all clocks in parallel.
     */
    public static def next():Void = clockPhases().next();


    // finish

    /**
     * Return the innermost finish state for the current activity
     */
    private static def currentState():FinishState {
        val a = activity();
        if (null == a.finishStack || a.finishStack.isEmpty())
            return a.finishState;
        return a.finishStack.peek();
    }

    /**
     * Start executing current activity synchronously
     * (i.e. within a finish statement).
     */
    public static def startFinish():Void {
        val a = activity();
        if (null == a.finishStack)
            a.finishStack = new Stack[FinishState!]();
        a.finishStack.push(new RootFinish());
    }

    /**
     * Suspend until all activities spawned during this finish
     * operation have terminated. Throw an exception if any
     * async terminated abruptly. Otherwise continue normally.
     * Should only be called by the thread executing the current activity.
     */
    public static def stopFinish():Void {
        val a = activity();
        val finishState = a.finishStack.pop();
        finishState.notifyActivityTermination();
        finishState.waitForFinish(safe());
    }

    /**
     * Push the exception thrown while executing s in a finish s,
     * onto the finish state.
     */
    public static def pushException(t:Throwable):Void  {
        currentState().pushException(t);
    }

    private static def safe():Boolean {
        val a = activity();
        return a.safe && (null == a.clockPhases);
    }


    static def scan(random:Random!, latch:Latch!, block:Boolean):Activity {
        return runtime().pool.scan(random, latch, block);
    }


    // submit an activity to the pool
    private static def execute(activity:Activity!):Void {
        runAtLocal(runtime().pool.home.id, ()=>worker().push(activity));
    }

    // notify the pool a worker is about to execute a blocking operation
    static def increaseParallelism():Void {
        if (!STATIC_THREADS) {
            runAtLocal(runtime().pool.home.id, runtime().pool.increase.());
        }
    }

    // notify the pool a worker resumed execution after a blocking operation
    static def decreaseParallelism(n:Int) {
        if (!STATIC_THREADS) {
            runAtLocal(runtime().pool.home.id, ()=>runtime().pool.decrease(n));
        }
    }

    // park current thread
    static def park() {
        if (!STATIC_THREADS) {
            Thread.park();
        } else {
            event_probe();
        }
    }

    // unpark given thread
    static def unpark(thread:Thread) {
        if (!STATIC_THREADS) {
            thread.unpark();
        }
    }

    // run pending activities while waiting on condition
    static def join(latch:Latch!) {
        runAtLocal(runtime().pool.home.id, ()=>worker().join(latch));
    }

    static def run(activity:Activity):Void {
        runAtLocal(activity.home.id, (activity as Activity!).run.());
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
