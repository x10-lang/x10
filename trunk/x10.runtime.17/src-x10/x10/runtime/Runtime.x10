/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.runtime.kernel.InterruptedException;
import x10.runtime.kernel.Thread;

/**
 * @author tardieu
 */
public value Runtime {
	// TODO place-cast of null?
	// TODO runNow
	// TODO configurable pools 
	
	/**
	 * One monitor per place
	 */
 	private const monitors = Rail.makeVal[Monitor](Place.MAX_PLACES,
 		(id:Nat)=>Runtime.remote[Monitor](Place.place(id), ()=>new Monitor()));
 		
	/**
	 * The common thread pool
	 */
	private const pool = new Pool(Int.getInteger("x10.INIT_THREADS_PER_PLACE", 3));

// 	private const pools = Rail.makeVal[Pool](Place.MAX_PLACES,
//		(id:Nat)=>Runtime.remote[Pool](Place.place(id), ()=>new Pool(Int.getInteger("x10.INIT_THREADS_PER_PLACE", 3))));

	/**
	 * Notify the thread pool that one activity is about to block
	 */
	static def threadBlockedNotification():Void {
		at (Place.FIRST_PLACE) pool.increase();
    }

	/**
	 * Notify the thread pool that one activity has unblocked
	 */
	static def threadUnblockedNotification():Void {
		at (Place.FIRST_PLACE) pool.decrease();
    }

	/**
	 * Return the current activity
	 */
	static def current():Activity = Thread.currentThread().activity() as Activity;
	
	/**
	 * Return the location of the current activity.
	 */
	static def currentPlace():Place = current().location;
	
	/**
	 * Return the clock phases of the current activity
	 */
	static def clockPhases():ClockPhases = current().clockPhases();

	/**
	 * Return the current place
	 */
	public static def here():Place = Thread.currentThread().place() as Place;

	/**
	 * Set the current place
	 * Return the former place
	 */
	public static def here(place:Place):Place {
		val thread = Thread.currentThread();
		val p = thread.place() as Place;
		thread.place(place);
		return p;		
	}

	/**
	 * Run the main activity in a finish
	 */
	public static def start(body:()=>Void):Void {
		try {
			val activity = new Activity(body);
			Thread.currentThread().activity(activity);
			val state = new FinishState();
			state.notifySubActivitySpawn();
			activity.finishStack.push(state);
			activity.run();
			state.waitForFinish();
		} catch (t:Throwable) {
			t.printStackTrace();
		}
	}

	/**
	 * Run an async
	 */
	public static def runAsync(clocks:ValRail[Clock_c], body:()=>Void, place:Place):Void {
		at (currentPlace()) {
			val state = current().finishStack.peek();
			val phases = Rail.makeVal[Int](clocks.length, (i:Nat)=>clocks(i).phase_c());
			state.notifySubActivitySpawn();
			at (place) {
				val activity = new Activity(body, clocks, phases);
				activity.finishStack.push(state);
				at (Place.FIRST_PLACE) pool.execute(activity);
			}
		}
	}

	/**
	 * Run a future
	 */
	public static def runFuture[T](eval:()=>T, place:Place):Future[T] {
		val futur = Runtime.remote[Future_c[T]](place, ()=>new Future_c[T](eval));
		runAsync(Rail.makeVal[Clock_c](0), ()=>futur.run(), place);
		return futur;
	}

	const PLACE_CHECKS = !Boolean.getBoolean("x10.NO_PLACE_CHECKS");

	/**
	 * Compute location
	 */
	private static def location(o:Object):Place {
		if (o instanceof Ref) return (o as Ref).location;
		return here;
	}

	/**
	 * Place check
	 */
	public static def placeCheck(p:Place, o:Object):Object {
		if (PLACE_CHECKS && null != o && location(o) != p) {
			throw new BadPlaceException("object=" + o + " access at place=" + p);
		}
		return o;
	}

	/**
	 * Remote computation
	 */
    static def remote[T](location:Object, eval:()=>T):T {
    	val ret = here;
    	val box = Rail.makeVar[T](1);
    	at (location instanceof Place ? location as Place :(location as Ref).location) {
    		val result = eval();
    		at (ret) box(0) = result; 
    	}
    	return box(0);
    }

	/**
	 * Lock current place
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

	/**
	 * Next statement = next on all clocks in parallel.
	 */
	public static def next():Void {
		at (currentPlace()) current().clockPhases.next();
	}

	/**
	 * Start executing current activity synchronously 
	 * (i.e. within a finish statement).
	 */
	public static def startFinish():Void {
		at (currentPlace()) current().finishStack.push(new FinishState());
	}

	/**
	 * Suspend until all activities spawned during this finish 
	 * operation have terminated. Throw an exception if any
	 * async terminated abruptly. Otherwise continue normally.
	 * Should only be called by the thread executing the current activity.
	 */
	public static def stopFinish():Void {
		at (currentPlace()) current().finishStack.pop().waitForFinish();
	}

	/** 
	 * Push the exception thrown while executing s in a finish s, 
	 * onto the finish state.
	 */
	public static def pushException(t:Throwable):Void  {
		at (currentPlace()) current().finishStack.peek().pushException(t);
	}
}
