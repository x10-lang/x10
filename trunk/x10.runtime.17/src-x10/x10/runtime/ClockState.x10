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

	atomic def register(ph:Int):Void {
		++count;
		if (-ph != phase) ++alive;
	}

	atomic def resume():Void {
		if (--alive == 0) {
			alive = count;
			++phase;
		}
	}
	
	def next(ph:Int):Void {
		if (ph > 0) resume();
		val abs = Math.abs(ph);
		await (abs < phase);
	}

	atomic def drop(ph:Int):Void {
		--count;
		if (-ph != phase) resume();
	}
}
