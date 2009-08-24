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
import x10.util.Stack;

import x10.util.concurrent.atomic.AtomicInteger;

import x10.io.Console;

/**
 * @author tardieu
 */
public value Runtime {
	// TODO place-cast of null?

	private def this():Runtime {}
	
	const latch = new RootFinish();

	private const finishTables = ValRail.make[HashMap[RID, FinishState]](Place.MAX_PLACES, (Int)=>new HashMap[RID, FinishState]());
	    
	private const finishLock = new Lock();
	
	const finishCount = new AtomicInteger(0);
	
    static def putFinish(finishState:FinishState):Void {
        if (finishState.rid().id == -1) {
            finishLock.lock();
            if (finishState.rid().id == -1) {
                val rootFinish = finishState as RootFinish;
                rootFinish.rid = new RID(here, finishCount.getAndIncrement());
                finishTables(here.id).put(rootFinish.rid, rootFinish);
            }
            finishLock.unlock();
        }
    }
    
    static def findFinish(rid:RID):FinishState {
        finishLock.lock();
        val finishState = finishTables(here.id).getOrElse(rid, null);
        if (null != finishState) {
            finishLock.unlock();
            return finishState;
        }
        val remoteFinish = new RemoteFinish(rid);
        finishTables(here.id).put(rid, remoteFinish);
        finishLock.unlock();
        return remoteFinish;
    }
    
    static def findRoot(rid:RID):RootFinish {
        finishLock.lock();
        val finishState = finishTables(here.id).getOrElse(rid, null);
        finishLock.unlock();
        return finishState as RootFinish;
    }
    
    static def removeRoot(rootFinish:RootFinish):Void{
        if (rootFinish.rid.id != -1) {
            finishLock.lock();
            finishTables(here.id).remove(rootFinish.rid);
            finishLock.unlock();
        }
    }
    
	/**
	 * Master thread running the runtime
	 */
	const master = Thread.currentThread();
	
	/**
	 * Listener thread should process incoming messages instead of parking
	 */
	const listener = (master.loc() != 0 || !NativeRuntime.local(NativeRuntime.MAX_PLACES - 1)) ? master : null;

	// thread pool
	
	/**
	 * One thread pool per node
	 */
	const pool = Pool.make(NativeRuntime.INIT_THREADS);

	/**
	 * A hueristic estimate of the amount of unscheduled activities
	 * currently available to worker threads in the pool.
	 * Intended for use in heuristics that control async spawning
	 * based on the current amount of surplus work.
	 */
	public static def surplusActivityCount():int = pool.pendingActivityCount();
    
	// current activity, current place

	/**
	 * Return the current activity
	 */
	private static def current():Activity = pool.worker().activity();
	
	/**
	 * Return the current place
	 */
	public static def here():Place = Thread.currentThread().location;

	const PRINT_STATS = false;

	// main
	
	/**
	 * Run main activity in a finish
	 */
	public static def start(body:()=>Void):Void {
		try {
			if (master.loc() == 0) {
				pool.execute(new Activity(body, latch, true));
				while (pool.worker().loop(latch, true));
				if (!NativeRuntime.local(Place.MAX_PLACES - 1)) {
					val c = ()=>Runtime.quit();
					for (var i:Int=1; i<Place.MAX_PLACES; i++) {
						NativeRuntime.runAt(i, c);						
					}
				}
				latch.waitForFinish(false);
			} else {
				while (pool.worker().loop(latch, true));
//				rootFinish.finishState.waitForFinish(false);
			}
		} finally {
			pool.release();
			if (PRINT_STATS) {
				NativeRuntime.println("ASYNC SENT AT PLACE " + here.id +" = " + NativeRuntime.getAsyncsSent());
				NativeRuntime.println("ASYNC RECV AT PLACE " + here.id +" = " + NativeRuntime.getAsyncsReceived());
			}
		}
	}

	static def quit():Void {
		latch.set();
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
			pool.execute(new Activity(body, state, clocks, phases));
		} else {
		    putFinish(state);
	        val rid = state.rid();
            val c = ()=>pool.execute(Activity.make(body, rid, clocks, phases));
			NativeRuntime.runAt(place.id, c);
		}
	}

	public static def runAsync(place:Place, body:()=>Void):Void {
		val state = currentState();
		state.notifySubActivitySpawn(place);
		val ok = safe();
		if (place.id == Thread.currentThread().loc()) {
			pool.execute(new Activity(body, state, ok));
		} else {
		    putFinish(state);
	        val rid = state.rid();
            if (ok) {
                NativeRuntime.runAt(place.id, ()=>pool.execute(Activity.make(body, rid, true)));
            } else {
                NativeRuntime.runAt(place.id, ()=>pool.execute(Activity.make(body, rid, false)));
            }
		}
	}

	public static def runAsync(clocks:ValRail[Clock], body:()=>Void):Void {
		val state = currentState();
		val phases = Rail.makeVal[Int](clocks.length, (i:Nat)=>(clocks(i) as Clock_c).register_c());
		state.notifySubActivitySpawn(here);
		pool.execute(new Activity(body, state, clocks, phases));
	}

	public static def runAsync(body:()=>Void):Void {
		val state = currentState();
		state.notifySubActivitySpawn(here);
		pool.execute(new Activity(body, state, safe()));
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
	 * Place check
	 */
	public static def placeCheck(p:Place, o:Object):Object {
		if (NativeRuntime.PLACE_CHECKS && null != o && o instanceof Ref && !(o instanceof Worker) && (o as Ref).location.id != p.id) {
			NativeRuntime.println("BAD PLACE EXCEPTION");
			throw new BadPlaceException("object=" + (at ((o as Ref).location) o.toString()) + " access at place=" + p);
		}
		return o;
	}


	// atomic, await, when

    public static def newMonitor(id:Int):Monitor {
    	val loc = master.loc();
    	val box = new GrowableRail[Monitor]();
        val c = ()=>{
    		val monitor = new Monitor();
    		NativeRuntime.runAtLocal(loc, ()=>box.add(monitor));
    	};
    	NativeRuntime.runAtLocal(id, c);
    	return box(0);
    }

	/**
	 * One monitor per place in the current node
	 */
	private const monitors = Rail.makeVal[Monitor](NativeRuntime.MAX_PLACES,
		(id:Nat)=> NativeRuntime.local(id) ? newMonitor(id) : null); 
 	 		
	/**
	 * Lock current place
	 * not reentrant!
	 */
    public static def lock():Void {
    	monitors(Thread.currentThread().loc()).lock();
    }

	/**
	 * Wait on current place lock 
	 * Must be called while holding the place lock
	 */	 
    public static def await():Void {
    	monitors(Thread.currentThread().loc()).park();
    }
	
	/**
	 * Unlock current place
	 * Notify all
	 */
    public static def release():Void {
    	val loc = Thread.currentThread().loc();
		monitors(loc).unpark();
		monitors(loc).unlock();
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
			pool.increase();
			Thread.sleep(millis);
			pool.decrease(1);
			return true;
		} catch (e:InterruptedException) {
			pool.decrease(1);
			return false;
		}
	}

	
	// clocks

	/**
	 * Return the clock phases for the current activity
	 */
	static def clockPhases():ClockPhases {
		val activity = current();
		if (null == activity.clockPhases) activity.clockPhases = new ClockPhases();
		return activity.clockPhases;
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
		val activity = current();
		if (null == activity.finishStack || activity.finishStack.isEmpty()) return activity.finishState;
		return activity.finishStack.peek();	
	}

	/**
	 * Start executing current activity synchronously 
	 * (i.e. within a finish statement).
	 */
	public static def startFinish():Void {
		val activity = current();
		if (null == activity.finishStack) activity.finishStack = new Stack[FinishState]();
		activity.finishStack.push(new RootFinish());
	}

	/**
	 * Suspend until all activities spawned during this finish 
	 * operation have terminated. Throw an exception if any
	 * async terminated abruptly. Otherwise continue normally.
	 * Should only be called by the thread executing the current activity.
	 */
	public static def stopFinish():Void {
		val finishState = current().finishStack.pop();
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
		val activity = current();
		return activity.safe && (null == activity.clockPhases);
	}
}

// vim:shiftwidth=4:tabstop=4:expandtab
