/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * @author tardieu
 */
class ClockState {
	static val FIRST_PHASE = 1;
	
	private var count: int = 1;
	private var alive: int = 1;
	private var phase: int = FIRST_PHASE; 

	atomic def register(ph: int): void {
		++count;
		if (-ph != phase) ++alive;
	}

	atomic def resume(): void {
		if (--alive == 0) {
			alive = count;
			++phase;
		}
	}
	
	def next(ph: int): void {
		val abs: int;
		if (ph < 0) {
			abs = -ph; 
		} else {
			resume();
			abs = ph;
		}
		await (abs < phase);
	}

	atomic def drop(ph: int): void {
		--count;
		if (-ph != phase) resume();
	}
}
