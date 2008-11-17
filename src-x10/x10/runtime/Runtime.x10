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
import x10.util.GrowableRail;

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
	private const pool = new Pool(x10.runtime.kernel.Runtime.INIT_THREADS);

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
	public static def here():Place = Place.place(Thread.currentThread().place());


	// main
	
	/**
	 * Run main activity in a finish
	 */
	public static def start(body:()=>Void):Void {
		try {
			val state = new FinishState();
			val activity = new Activity(body, state);
			Thread.currentThread().activity(activity);
			state.notifySubActivitySpawn();
			activity.run();
			state.waitForFinish();
		} catch (t:Throwable) {
            // temporary measure until we have a new stacktrace mechanism
            Console.OUT.println("The activity did not catch: "+t);
			//t.printStackTrace();
		}
	}


	// at statement -> at expression -> async -> future
	// do not introduce cycles!!!

	/**
	 * Run at statement
	 */
	public static def runAt(place:Place, body:()=>Void):Void {
		x10.runtime.kernel.Runtime.runAt(place.id, body);
	}

	/**
	 * Eval at expression
	 */
    public static def evalAt[T](place:Place, eval:()=>T):T {
    	val ret = here;
    	val box = new GrowableRail[T]();
    	at (place) {
    		val result = eval();
    		at (ret) box(0) = result; 
    	}
    	return box(0);
    }

	/**
	 * Run async
	 */
	public static def runAsync(place:Place, clocks:ValRail[Clock_c], body:()=>Void):Void {
		at (current().location) {
			val state = current().finishStack.peek();
			val phases = Rail.makeVal[Int](clocks.length, (i:Nat)=>clocks(i).phase_c());
			state.notifySubActivitySpawn();
			at (place) pool.execute(new Activity(body, state, clocks, phases));
		}
	}

	/**
	 * Eval future expression
	 */
	public static def evalFuture[T](place:Place, eval:()=>T):Future[T] {
		return at (place) {
			val futur = new Future_c[T](eval);
			async futur.run();
			futur
		};
	}
    
    
    // place checks

	/**
	 * Place check
	 */
	public static def placeCheck(p:Place, o:Object):Object {
		if (x10.runtime.kernel.Runtime.PLACE_CHECKS &&
				null != o && o instanceof Ref && (o to Ref).location != p) {
			throw new BadPlaceException("object=" + o + " access at place=" + p);
		}
		return o;
	}


	// atomic, await, when

	/**
	 * One monitor per place in the current node
	 */
	private const monitors = Rail.makeVal[Monitor](Place.MAX_PLACES,
		(id:Nat)=> x10.runtime.kernel.Runtime.local(id) ? at (Place.place(id)) new Monitor() : null); 
 	 		
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
	static def clockPhases():ClockPhases = at (current().location) current().clockPhases();

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
		at (current().location) current().finishStack.push(new FinishState());
	}

	/**
	 * Suspend until all activities spawned during this finish 
	 * operation have terminated. Throw an exception if any
	 * async terminated abruptly. Otherwise continue normally.
	 * Should only be called by the thread executing the current activity.
	 */
	public static def stopFinish():Void {
		at (current().location) current().finishStack.pop().waitForFinish();
	}

	/** 
	 * Push the exception thrown while executing s in a finish s, 
	 * onto the finish state.
	 */
	public static def pushException(t:Throwable):Void  {
		at (current().location) current().finishStack.peek().pushException(t);
	}
}
