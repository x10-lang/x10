/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

/**
 * @author tardieu
 */
class ClockState {
	const FIRST_PHASE = 1;
	
	private var count:Int = 1;
	private var alive:Int = 1;
	private var phase:Int = FIRST_PHASE; 

	global def register(ph:Int):Void {
		at (this) atomic {
		  ++count;
		  if (-ph != phase) ++alive;
		}
	}

	global def resume():Void {
		at (this) {
		  atomic 
		    if (--alive == 0) {
			   alive = count;
			   ++phase;
		    }
		}
	}
	
	global def next(ph:Int):Void {
		at (this) {
		  if (ph > 0) resume();
		  val abs = Math.abs(ph);
		  await (abs < phase);
		}
	}

	global def drop(ph:Int):Void {
		at (this) atomic {
		  --count;
		  if (-ph != phase) resume();
		}
	}
}
