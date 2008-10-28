/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * @author Christian Grothoff
 * @author Raj Barik, Vivek Sarkar
 * @author tardieu
 */
public value Place(id: nat) {
	//TODO hashCode and equals methods
	//TODO place check/place-cast of null?

    public const MAX_PLACES = Runtime.MAX_PLACES;
	public const places = Rail.makeVal[Place](MAX_PLACES, ((id: nat) => new Place(id)));
    public const FIRST_PLACE = place(0);

	// native thread pools
	
	@NativeRep("java", "x10.runtime.X10ThreadPoolExecutor")
	private static class Executor {
		native def this(placeId: int);
	
		@Native("java", "#0.increasePoolSize()")
		native def increasePoolSize(): void;	
	
		@Native("java", "#0.decreasePoolSize()")
		native def decreasePoolSize(): void;	
	
		@Native("java", "#0.execute(#1)")
		native def execute(r: Runnable): void;	
	}
	
    /**
     * Executor Service which provides the thread pool
     */
	private val executor: Executor;

	public def threadBlockedNotification(): void {
		executor.increasePoolSize();
    }

	public def threadUnblockedNotification(): void {
		executor.decreasePoolSize();
    }

	// constructor

	private def this(id: nat) {
		property(id);
		executor = new Executor(id);
	}
	
	// running activities
	
	public static def runMain(activity: Activity): void {
		try {
			activity.startFinish();
			activity.finishState().notifySubActivitySpawn();
			FIRST_PLACE.executor.execute(activity);
			activity.stopFinish();
		} catch (t: Throwable) {
			t.printStackTrace();
		}
	}
	
	public def runAsync(activity: Activity): void {
		activity.setFinishState(Activity.current().finishState());
		activity.finishState().notifySubActivitySpawn();
		executor.execute(activity);
	}

	//HACK code generation fails if method is not static
	public static def runFuture[T](p: Place, future_c: Future_c[T]): Future[T] {
		p.runAsync(future_c);
		return future_c;
	}

	// place arithmetics

	public static def place(id: nat): Place {
		return Place.places(id);
	}

    public def next(): Place { return next(1); }

    public def prev(): Place { return next(-1); }

    public def prev(i: int): Place { return next(-i); }
    
    public def next(i: int): Place {
        // -1 % n == -1, not n-1, so need to add n
        val k = (id + i % MAX_PLACES + MAX_PLACES) % MAX_PLACES;
        return Place.place(k);
	}
	
	public def isFirst(): boolean {
		return id == 0;
	}

	public def isLast(): boolean {
		return id == MAX_PLACES - 1;
	}
	
	// locations
	
	private static def location(o: Object): Place {
		if (o instanceof Ref) return (o as Ref).location;
		return here;
	}
	
	public static def asPlace(o: Object): Place {
		if (o instanceof Place)	return o as Place;
		return location(o);
	}

	public def placeCheck(o: Object): Object {
//		if (null != o && location(o) != this) {
//			throw new BadPlaceException("object=" + o + " access at place=" + this);
//		}
		return o;
	}
}    
