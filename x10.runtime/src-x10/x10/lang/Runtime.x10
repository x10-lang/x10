/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.util.HashMap;
import x10.util.Random;
import x10.util.Stack;

import x10.util.concurrent.atomic.AtomicInteger;

import x10.io.Console;

/**
 * @author tardieu
 */
public final class Runtime {

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
    private const runtime = PlaceLocalHandle.createHandle[Runtime]();

    static def proxy(rootFinish:RootFinish) = runtime().finishStates(rootFinish);

    /**
     * Return the current worker
     */
    private static def worker():Worker! = X10Thread.currentThread().worker();

    /**
     * Return the current activity
     */
    public static def activity():Activity!
               = worker().activity() as Activity!;

    /**
     * Return the current place
     */
    public static def here():Place = X10Thread.currentThread().home;

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
        val pool = new Pool(rootFinish, NativeRuntime.INIT_THREADS);
        try {
            for (var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (NativeRuntime.local(i)) {
                    NativeRuntime.runAtLocal(i, ()=>runtime.set(new Runtime(pool)));
                }
            }
            NativeRuntime.registerHandlers();
            if (X10Thread.currentThread().locInt() == 0) {
                execute(new Activity(()=>{finish init(); body();}, rootFinish, true));
                pool();
                if (!NativeRuntime.local(Place.MAX_PLACES - 1)) {
                    for (var i:Int=1; i<Place.MAX_PLACES; i++) {
                        NativeRuntime.runAt(i, ()=>{ val w=worker(); w.latch.release()});
                    }
                }
                rootFinish.waitForFinish(false);
            } else {
                pool();
            }
        } finally {
            if (PRINT_STATS) {
                NativeRuntime.println("ASYNC SENT AT PLACE " + here.id +" = " + NativeRuntime.getAsyncsSent());
                NativeRuntime.println("ASYNC RECV AT PLACE " + here.id +" = " + NativeRuntime.getAsyncsReceived());
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
        if (place.id == X10Thread.currentThread().locInt()) {
            execute(new Activity(body, state, clocks, phases));
        } else {
            val c = ()=>execute(new Activity(body, state, clocks, phases));
            NativeRuntime.runAt(place.id, c);
        }
    }

    public static def runAsync(place:Place, body:()=>Void):Void {
        val state = currentState();
        state.notifySubActivitySpawn(place);
        val ok = safe();
        if (place.id == X10Thread.currentThread().locInt()) {
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
            NativeRuntime.runAt(place.id, closure);
            NativeRuntime.dealloc(closure);
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
        if (!NativeRuntime.NO_STEALS && safe()) Runtime.join(box.latch);
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
        if (!NativeRuntime.NO_STEALS && safe()) Runtime.join(box.latch);
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
        if (NativeRuntime.PLACE_CHECKS && null != o
            && o instanceof Object
            && !(o instanceof Worker)
            && (o as Object!).home.id != p.id) {
            NativeRuntime.println("BAD PLACE EXCEPTION");
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
            X10Thread.sleep(millis);
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
        NativeRuntime.runAtLocal(runtime().pool.home.id, ()=>worker().push(activity));
    }

    // notify the pool a worker is about to execute a blocking operation
    static def increaseParallelism():Void {
        if (!NativeRuntime.STATIC_THREADS) {
            NativeRuntime.runAtLocal(runtime().pool.home.id, runtime().pool.increase.());
        }
    }

    // notify the pool a worker resumed execution after a blocking operation
    static def decreaseParallelism(n:Int) {
        if (!NativeRuntime.STATIC_THREADS) {
            NativeRuntime.runAtLocal(runtime().pool.home.id, ()=>runtime().pool.decrease(n));
        }
    }

    // park current thread
    static def park() {
        if (!NativeRuntime.STATIC_THREADS) {
            X10Thread.park();
        } else {
            NativeRuntime.event_probe();
        }
    }

    // unpark given thread
    static def unpark(thread:X10Thread) {
        if (!NativeRuntime.STATIC_THREADS) {
            thread.unpark();
        }
    }

    // run pending activities while waiting on condition
    static def join(latch:Latch!) {
        NativeRuntime.runAtLocal(runtime().pool.home.id, ()=>worker().join(latch));
    }

    static def run(activity:Activity):Void {
        NativeRuntime.runAtLocal(activity.home.id, (activity as Activity!).run.());
    }

    public static def setExitCode(code: int): void {
        NativeRuntime.setExitCode(code);
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
