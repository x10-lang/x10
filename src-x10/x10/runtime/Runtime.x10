/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.ArrayList;

import x10.io.Console;

/**
 * @author tardieu
 */
public value Runtime {
	// TODO place-cast of null?
	// TODO runNow
	
	
	// thread pool
	
	/**
	 * One thread pool per node
	 */
	private const pool = new Pool(NativeRuntime.INIT_THREADS);

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
			val activity = new Activity(body, "root");
			Thread.currentThread().activity(activity);
			finish {
				activity.finishStack.peek().notifySubActivitySpawn();
				activity.run();
			}
//		} catch (t:Throwable) {
//			t.printStackTrace();
//		}
	}


	// async -> at expression -> future
	// do not introduce cycles!!!

	/**
	 * Run at statement
	 */
	public static def runAt(place:Place, body:()=>Void):Void {
		throw new RuntimeException("at statement is deprecated");
	}

	/**
	 * Run async
	 */
	public static def runAsync(place:Place, clocks:ValRail[Clock], body:()=>Void, name:String):Void {
		val state = current().finishStack.peek();
		val phases = Rail.makeVal[Int](clocks.length, (i:Nat)=>(clocks(i) as Clock_c).register_c());
		state.notifySubActivitySpawn();
		if (place == here) {
			pool.execute(new Activity(body, state, clocks, phases, name));
		} else {
            val c = () => pool.execute(new Activity(body, state, clocks, phases, name));
			NativeRuntime.runAt(place.id, c);
		}
	}

	/**
	 * Eval at expression
	 */
    public static def evalAt[T](place:Place, eval:()=>T):T {
    	val ret = here;
    	val box = new ArrayList[T]();
    	finish async (place) {
    		val result = eval();
    		async (ret) box.add(result); 
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
		if (NativeRuntime.PLACE_CHECKS &&
				null != o && o instanceof Ref && (o to Ref).location != p) {
			throw new BadPlaceException("object=" + (at ((o to Ref).location) o.toString()) + " access at place=" + p);
		}
		return o;
	}


	// atomic, await, when

    public static def newMonitor(place:Place):Monitor {
    	val ret = here;
    	val box = new ArrayList[Monitor]();
        val c = ()=>{
    		val monitor = new Monitor();
    		NativeRuntime.runAtLocal(ret.id, ()=>{ box.add(monitor); });
    	};
    	NativeRuntime.runAtLocal(place.id, c);
    	return box(0);
    }

	/**
	 * One monitor per place in the current node
	 */
	private const monitors = Rail.makeVal[Monitor](Place.MAX_PLACES,
		(id:Nat)=> NativeRuntime.local(id) ? newMonitor(Place.place(id)) : null); 
 	 		
	/**
	 * Lock current place
	 * not reentrant!
	 */
    public static def lock():Void {
    	monitors(here().id).lock();
    }

	/**
	 * Wait on current place lock 
	 * Must be called while holding the place lock
	 */	 
    public static def await():Void {
    	monitors(here().id).await();
    }
	
	/**
	 * Unlock current place
	 * Notify all
	 */
    public static def release():Void {
		monitors(here().id).unparkAll();
		monitors(here().id).unlock();
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
			threadBlockedNotification();
			Thread.sleep(millis);
			return true;
		} catch (e:InterruptedException) {
			return false;
		} finally {
			threadUnblockedNotification();
		}
	}

	
	// clocks

	/**
	 * Return the clock phases of the current activity
	 */
	static def clockPhases():ClockPhases = current().clockPhases();

	/**
	 * Next statement = next on all clocks in parallel.
	 */
	public static def next():Void =	clockPhases().next();


	// finish

	/**
	 * Start executing current activity synchronously 
	 * (i.e. within a finish statement).
	 */
	public static def startFinish():Void {
		current().finishStack.push(new FinishState());
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
		current().finishStack.peek().pushException(t);
	}
}
