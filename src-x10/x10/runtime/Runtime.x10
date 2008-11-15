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
 		(id:Nat)=>at (Place.place(id)) new Monitor());
 		
	/**
	 * The common thread pool
	 */
	private const pool = new Pool(Int.getInteger("x10.INIT_THREADS_PER_PLACE", 3));

// 	private const pools = Rail.makeVal[Pool](Place.MAX_PLACES,
//		(id:Nat)=>at (Place.place(id)) new Pool(Int.getInteger("x10.INIT_THREADS_PER_PLACE", 3)));

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
	 * Run main activity in a finish
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
	 * Run async
	 */
	public static def runAsync(place:Place, clocks:ValRail[Clock_c], body:()=>Void):Void {
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
	 * Run at statement
	 */
	public static def runAt(place:Place, body:()=>Void):Void {
		val thread = Thread.currentThread();
		val ret = thread.place() as Place;
		thread.place(place);
		try {
			body();
		} finally {
			thread.place(ret);
		}
	}

	/**
	 * Eval future expression
	 */
	public static def evalFuture[T](place:Place, eval:()=>T):Future[T] {
		val futur = at (place) new Future_c[T](eval);
		async (place) futur.run();
		return futur;
	}

	/**
	 * Eval at expression
	 */
    public static def evalAt[T](place:Place, eval:()=>T):T {
    	val ret = here;
    	val box = Rail.makeVar[T](1);
    	at (place) {
    		val result = eval();
    		at (ret) box(0) = result; 
    	}
    	return box(0);
    }

	const PLACE_CHECKS = !Boolean.getBoolean("x10.NO_PLACE_CHECKS");

	/**
	 * Place check
	 */
	public static def placeCheck(p:Place, o:Object):Object {
		if (PLACE_CHECKS && null != o && o instanceof Ref && (o to Ref).location != p) {
			throw new BadPlaceException("object=" + o + " access at place=" + p);
		}
		return o;
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
