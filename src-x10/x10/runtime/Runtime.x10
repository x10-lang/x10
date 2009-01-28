/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.GrowableRail;
import x10.util.Stack;

import x10.io.Console;

/**
 * @author tardieu
 */
public value Runtime {
	// TODO place-cast of null?
	
	private def this():Runtime {}
	
	/**
	 * Master thread running the runtime/pool/root activity
	 */
	private const master = Thread.currentThread();
	
	// x10_probe
	
	/**
	 * Listener thread should process incoming messages instead of parking
	 */
	const listener = (NativeRuntime.local(0) && !NativeRuntime.local(NativeRuntime.MAX_PLACES - 1)) ? master : null;
	

	// thread pool
	
	/**
	 * One thread pool per node
	 */
	private const pool = new Pool(NativeRuntime.INIT_THREADS - 1);

	/**
	 * Notify the thread pool that one activity is about to block
	 */
	static def threadBlockedNotification():Void {
		pool.increase();
    }

	/**
	 * Notify the thread pool that one activity has unblocked
	 */
	static def threadUnblockedNotification():Void {
		pool.decrease();
    }
    
    
	// current activity, current place

	/**
	 * Return the current activity
	 */
	private static def current():Activity = Thread.currentThread().activity() as Activity;
	
	/**
	 * Return the current place
	 */
	public static def here():Place = Thread.currentThread().location;


	// main
	
	/**
	 * Run main activity in a finish
	 */
	public static def start(body:()=>Void):Void {
// temporary: printStackTrace call moved to Main template (native code) 
//		try {
			if (master.loc() == 0) {
				val rootFinish = new FinishState();
				val activity = new Activity(body, rootFinish, "root");
				master.activity(activity);
				activity.now();
				rootFinish.waitForFinish();
				pool.destruct();
				//NativeRuntime.println("Root activity completed");
			} else {
				NativeRuntime.event_loop();
			}
//		} catch (t:Throwable) {
//			t.printStackTrace();
//		}
	}


	// async -> at statement -> at expression -> future
	// do not introduce cycles!!!

	/**
	 * Run async
	 */
	public static def runAsync(place:Place, clocks:ValRail[Clock], body:()=>Void, name:String):Void {
		val state = currentState();
		val phases = Rail.makeVal[Int](clocks.length, (i:Nat)=>(clocks(i) as Clock_c).register_c());
		state.notifySubActivitySpawn();
		if (place.id == Thread.currentThread().loc()) {
			pool.execute(new Activity(body, state, clocks, phases, name));
		} else {
            val c = ()=>pool.execute(new Activity(body, state, clocks, phases, name));
			NativeRuntime.runAt(place.id, c);
		}
	}

	public static def runAsync(place:Place, body:()=>Void, name:String):Void {
		val state = currentState();
		state.notifySubActivitySpawn();
		if (place.id == Thread.currentThread().loc()) {
			pool.execute(new Activity(body, state, name));
		} else {
            val c = ()=>pool.execute(new Activity(body, state, name));
			NativeRuntime.runAt(place.id, c);
		}
	}

	public static def runAsync(clocks:ValRail[Clock], body:()=>Void, name:String):Void {
		val state = currentState();
		val phases = Rail.makeVal[Int](clocks.length, (i:Nat)=>(clocks(i) as Clock_c).register_c());
		state.notifySubActivitySpawn();
		pool.execute(new Activity(body, state, clocks, phases, name));
	}

	public static def runAsync(body:()=>Void, name:String):Void {
		val state = currentState();
		state.notifySubActivitySpawn();
		pool.execute(new Activity(body, state, name));
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
	public static def evalFuture[T](place:Place, eval:()=>T, name:String):Future[T] {
		val futur = at (place) new Future_c[T](eval, name);
		async (place) futur.run();
		return futur;
	}
    
    
    // place checks

	/**
	 * Place check
	 */
	public static def placeCheck(p:Place, o:Object):Object {
		if (NativeRuntime.PLACE_CHECKS && null != o && o instanceof Ref && (o to Ref).location.id != p.id) {
			throw new BadPlaceException("object=" + (at ((o to Ref).location) o.toString()) + " access at place=" + p);
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
    	monitors(Thread.currentThread().loc()).await();
    }
	
	/**
	 * Unlock current place
	 * Notify all
	 */
    public static def release():Void {
    	val loc = Thread.currentThread().loc();
		monitors(loc).unparkAll();
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
			pool.decrease();
			return true;
		} catch (e:InterruptedException) {
			pool.decrease();
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
		activity.finishStack.push(new FinishState());
	}

	/**
	 * Suspend until all activities spawned during this finish 
	 * operation have terminated. Throw an exception if any
	 * async terminated abruptly. Otherwise continue normally.
	 * Should only be called by the thread executing the current activity.
	 */
	public static def stopFinish():Void {
		current().finishStack.pop().waitForFinish();
	}

	/** 
	 * Push the exception thrown while executing s in a finish s, 
	 * onto the finish state.
	 */
	public static def pushException(t:Throwable):Void  {
		currentState().pushException(t);
	}
}

// vim:shiftwidth=4:tabstop=4:expandtab
