/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.GrowableRail;
import x10.util.HashMap;
import x10.util.Random;
import x10.util.Stack;

import x10.util.concurrent.atomic.AtomicInteger;

import x10.io.Console;

/**
 * @author tardieu
 */
public value Runtime {

	// for debugging
	const PRINT_STATS = false;

	// instance fields
	
	// per process members
	private val pool:Pool;
	
	// per place members
	private val monitor = new Monitor();
	private val finishTable = new HashMap[RID, FinishState]();
	private val finishCount = new AtomicInteger(0);
	private val finishLock = new Lock();

	// constructor

	private def this(pool:Pool):Runtime {
		this.pool = pool;
	}
	
	/**
	 * The runtime instance associated with each place
	 */
	private const runtime = PlaceLocalHandle.createHandle[Runtime]();
	
	/**
	 * Return the current runtime
	 */
	private static def runtime():Runtime = runtime.get();

	/**
	 * Return the current worker
	 */
	private static def worker():Worker = Thread.currentThread().worker();
	
	/**
	 * Return the current activity
	 */
	private static def activity():Activity = worker().activity();
	
	/**
	 * Return the current place
	 */
	public static def here():Place = Thread.currentThread().location;

	/**
	 * The amount of unscheduled activities currently available to this worker thread.
	 * Intended for use in heuristics that control async spawning
	 * based on the current amount of surplus work.
	 */
	public static def surplusActivityCount():int = worker().size();
    
	/**
	 * Run main activity in a finish
	 */
	public static def start(body:()=>Void):Void {
		val rootFinish = new RootFinish();
		val pool = new Pool(rootFinish, NativeRuntime.INIT_THREADS);
		try {
			for (var i:Int=0; i<Place.MAX_PLACES; i++) {
				if (NativeRuntime.local(i)) {
					NativeRuntime.runAtLocal(i, ()=>runtime.set(new Runtime(pool)));
				}
			}
			if (Thread.currentThread().loc() == 0) {
				runtime().execute(new Activity(body, rootFinish, true));
				pool();
				if (!NativeRuntime.local(Place.MAX_PLACES - 1)) {
					for (var i:Int=1; i<Place.MAX_PLACES; i++) {
						NativeRuntime.runAt(i, ()=>worker().latch.release());						
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

	
	def putFinish(finishState:FinishState):Void {
        if (finishState.rid().id == -1) {
            finishLock.lock();
            if (finishState.rid().id == -1) {
                val rootFinish = finishState as RootFinish;
                rootFinish.rid = new RID(here, finishCount.getAndIncrement());
                finishTable.put(rootFinish.rid, rootFinish);
            }
            finishLock.unlock();
        }
    }
    
    static def findFinish(rid:RID):FinishState = runtime().findFinish2(rid);
    
    private def findFinish2(rid:RID):FinishState {
        finishLock.lock();
        val finishState = finishTable.getOrElse(rid, null);
        if (null != finishState) {
            finishLock.unlock();
            return finishState;
        }
        val remoteFinish = new RemoteFinish(rid);
        finishTable.put(rid, remoteFinish);
        finishLock.unlock();
        return remoteFinish;
    }
    
    static def findRoot(rid:RID):RootFinish = runtime().findRoot2(rid);
    	
    private def findRoot2(rid:RID):RootFinish {
        finishLock.lock();
        val finishState = finishTable.getOrElse(rid, null);
        finishLock.unlock();
        return finishState as RootFinish;
    }
    
    static def removeRoot(rootFinish:RootFinish):Void = runtime().removeRoot2(rootFinish);
    
    private def removeRoot2(rootFinish:RootFinish):Void{
        if (rootFinish.rid.id != -1) {
            finishLock.lock();
            finishTable.remove(rootFinish.rid);
            finishLock.unlock();
        }
    }



	// async -> at statement -> at expression -> future
	// do not introduce cycles!!!

	/**
	 * Run async
	 */
	public static def runAsync(place:Place, clocks:ValRail[Clock], body:()=>Void):Void {
		val state = currentState();
		val phases = Rail.makeVal[Int](clocks.length, (i:Nat)=>(clocks(i) as Clock_c).register_c());
		state.notifySubActivitySpawn(place);
		if (place.id == Thread.currentThread().loc()) {
			runtime().execute(new Activity(body, state, clocks, phases));
		} else {
		    runtime().putFinish(state);
	        val rid = state.rid();
            val c = ()=>runtime().execute(Activity.make(body, rid, clocks, phases));
			NativeRuntime.runAt(place.id, c);
		}
	}

	public static def runAsync(place:Place, body:()=>Void):Void {
		val state = currentState();
		state.notifySubActivitySpawn(place);
		val ok = safe();
		if (place.id == Thread.currentThread().loc()) {
			runtime().execute(new Activity(body, state, ok));
		} else {
		    runtime().putFinish(state);
	        val rid = state.rid();
            if (ok) {
                NativeRuntime.runAt(place.id, ()=>runtime().execute(Activity.make(body, rid, true)));
            } else {
                NativeRuntime.runAt(place.id, ()=>runtime().execute(Activity.make(body, rid, false)));
            }
		}
	}

	public static def runAsync(clocks:ValRail[Clock], body:()=>Void):Void {
		val state = currentState();
		val phases = Rail.makeVal[Int](clocks.length, (i:Nat)=>(clocks(i) as Clock_c).register_c());
		state.notifySubActivitySpawn(here);
		runtime().execute(new Activity(body, state, clocks, phases));
	}

	public static def runAsync(body:()=>Void):Void {
		val state = currentState();
		state.notifySubActivitySpawn(here);
		runtime().execute(new Activity(body, state, safe()));
	}
	
	/**
	 * Run at statement
	 */
	public static def runAt(place:Place, body:()=>Void):Void {
		finish async (place) body();
	}

	/**
	 * Eval at expression
	 */
    public static def evalAt[T](place:Place, eval:()=>T):T {
    	val ret = here;
    	val box = new GrowableRail[T]();
    	at (place) {
    		val result = eval();
    		at (ret) box.add(result); 
    	}
    	return box(0);
    }

	/**
	 * Eval future expression
	 */
	public static def evalFuture[T](place:Place, eval:()=>T):Future[T] {
		val futur = at (place) new Future_c[T](eval);
		async (place) futur.run();
		return futur;
	}
    
    
    // place checks

	/**
	 * Java place check
	 */
	public static def placeCheck(p:Place, o:Object):Object {
		if (NativeRuntime.PLACE_CHECKS && null != o && o instanceof Ref && !(o instanceof Worker) && (o as Ref).location.id != p.id) {
			NativeRuntime.println("BAD PLACE EXCEPTION");
			throw new BadPlaceException("object=" + (at ((o as Ref).location) o.toString()) + " access at place=" + p);
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
	static def clockPhases():ClockPhases {
		val a = activity();
		if (null == a.clockPhases) a.clockPhases = new ClockPhases();
		return a.clockPhases;
	}

	/**
	 * Next statement = next on all clocks in parallel.
	 */
	public static def next():Void =	clockPhases().next();


	// finish

	/**
	 * Return the innermost finish state for the current activity
	 */
	private static def currentState():FinishState {
		val a = activity();
		if (null == a.finishStack || a.finishStack.isEmpty()) return a.finishState;
		return a.finishStack.peek();	
	}

	/**
	 * Start executing current activity synchronously 
	 * (i.e. within a finish statement).
	 */
	public static def startFinish():Void {
		val a = activity();
		if (null == a.finishStack) a.finishStack = new Stack[FinishState]();
		a.finishStack.push(new RootFinish());
	}

	/**
	 * Suspend until all activities spawned during this finish 
	 * operation have terminated. Throw an exception if any
	 * async terminated abruptly. Otherwise continue normally.
	 * Should only be called by the thread executing the current activity.
	 */
	public static def stopFinish():Void {
		val finishState = activity().finishStack.pop();
		finishState.notifySubActivityTermination();
		(finishState as RootFinish).waitForFinish(safe());
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

	// submit an activity to the pool (global method)
	private def execute(activity:Activity):Void {
        NativeRuntime.runAtLocal(pool.location.id, ()=>{
			worker().push(activity);
		});
	}
	
	static def increaseParallelism() = runtime().increase();
	
	// notify the pool a worker is about to execute a blocking operation (global method)
	private def increase():Void {
		NativeRuntime.runAtLocal(pool.location.id, pool.increase.());
    }

	static def decreaseParallelism(n:Int) = runtime().decrease(n);

	// notify the pool a worker resumed execution after a blocking operation (global method)
	private def decrease(n:Int):Void {
		NativeRuntime.runAtLocal(pool.location.id, ()=>pool.decrease(n));
    }

	// run pending activities while waiting on conditioon (global method)
	static def join(latch:Latch) {
		NativeRuntime.runAtLocal(runtime().pool.location.id, ()=>worker().join(latch));
	}

	static def scan(random:Random, latch:Latch, block:Boolean):Activity {
		return runtime().pool.scan(random, latch, block);
	}

	static def run(activity:Activity):Void {
		NativeRuntime.runAtLocal(activity.location.id, activity.run.());
	}
}

// vim:shiftwidth=4:tabstop=4:expandtab
