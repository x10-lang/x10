/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.runtime.kernel.InterruptedException;
import x10.runtime.kernel.Lock;
import x10.runtime.kernel.Runnable;
import x10.runtime.kernel.Thread;

/**
 * @author tardieu
 */
public value Runtime {
	// TODO place check/place-cast of null?
	// TODO runNow
	// TODO configurable pools 
	
	/**
	 * One monitor per place
	 */
 	private const monitors = Rail.makeVal[Monitor](Place.MAX_PLACES, (id:nat)=>new Monitor());

	/**
	 * The common thread pool
	 */
	private const pool = new Pool(x10.runtime.kernel.Runtime.INIT_THREADS_PER_PLACE);

	/**
	 * Notify the thread pool that one activity is about to block
	 */
	static def threadBlockedNotification(): void {
		pool.increase();
    }

	/**
	 * Notify the thread pool that one activity has unblocked
	 */
	static def threadUnblockedNotification(): void {
		pool.decrease();
    }

	/**
	 * Return the current activity
	 */
	private static def current(): Activity {
		return Thread.currentThread().activity() as Activity;
	}

	/**
	 * Return the place of the current activity
	 */
	public static def here(): Place {
		return Thread.currentThread().place() as Place;
	}

	/**
	 * Return the clock phases of the current activity
	 */
	static def clocks(): Clocks {
		return current().clocks();
	}

	/**
	 * Run the main activity in a finish
	 */
	public static def runMain(activity: Activity): void {
		try {
			val state = new FinishState();
			activity.finishState(state);
			pool.execute(new Job(activity, Place.FIRST_PLACE));
			state.waitForFinish();
		} catch (t: Throwable) {
			t.printStackTrace();
		}
	}

	/**
	 * Run an async
	 */
	public static def runAsync(activity: Activity, o: Object): void {
		activity.finishState(current().finishState());
		val place = o instanceof Place ? o as Place : location(o); 
		pool.execute(new Job(activity, place));
	}

	/**
	 * Run a future
	 */
	public static def runFuture[T](future_c: Future_c[T], o: Object): Future[T] {
		runAsync(future_c, o);
		return future_c;
	}

	/**
	 * Now
	 */
	public static def runNow(activity: Activity, o: Object): void {
		throw new RuntimeException("now not implemented");
	}

	/**
	 * Compute location
	 */
	private static def location(o: Object): Place {
		if (o instanceof Ref) return (o as Ref).location;
		return here;
	}

	/**
	 * Place check
	 */
	public static def placeCheck(p: Place, o: Object): Object {
//		if (null != o && location(o) != p) {
//			throw new BadPlaceException("object=" + o + " access at place=" + p);
//		}
		return o;
	}

	/**
	 * Lock current place
	 */
    public static def lock(): void {
    	monitors(here().id).lock();
    }

	/**
	 * Wait on current place lock 
	 * Must be called while holding the place lock
	 */	 
    public static def await(): void {
    	monitors(here().id).await();
    }
	
	/**
	 * Unlock current place
	 * Notify all
	 */
    public static def release(): void {
    	monitors(here().id).unparkAll();
    	monitors(here().id).unlock();
    }

	/**
	 * Sleep for the specified number of milliseconds.
	 * [IP] NOTE: Unlike Java, x10 sleep() simply exits when interrupted.
	 * @param millis the number of milliseconds to sleep
	 * @return true if completed normally, false if interrupted
	 */
	public static def sleep(millis: long): boolean {
		try {
			threadBlockedNotification();
			Thread.sleep(millis);
			return true;
		} catch (e: InterruptedException) {
			return false;
		} finally {
			threadUnblockedNotification();
		}
	}

	/**
	 * Next statement = next on all clocks in parallel.
	 */
	public static def next(): void {
		current().next();
	}

	/**
	 * Start executing current activity synchronously 
	 * (i.e. within a finish statement).
	 */
	public static def startFinish(): void {
		current().startFinish();
	}

	/**
	 * Suspend until all activities spawned during this finish 
	 * operation have terminated. Throw an exception if any
	 * async terminated abruptly. Otherwise continue normally.
	 * Should only be called by the thread executing the current activity.
	 */
	public static def stopFinish(): void {
		current().stopFinish();
	}

	/** 
	 * Push the exception thrown while executing s in a finish s, 
	 * onto the finish state.
	 */
	public static def pushException(t: Throwable): void  {
		current().pushException(t);
	}
}
