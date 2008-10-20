/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

/**
 * Manages the state of a clock 
 */
public class ClockState {
	public static val FIRST_PHASE = 1;
	
	private var count: int = 1;
	private var alive: int = 1;
	private var phase: int = FIRST_PHASE; 

	public atomic def register(ph: int): void {
		++count;
		if (-ph != phase) ++alive;
	}

	public atomic def resume(): void {
		if (--alive == 0) {
			alive = count;
			++phase;
		}
	}
	
	public def next(ph: int): int {
		val abs: int;
		if (ph < 0) {
			abs = -ph; 
		} else {
			resume();
			abs = ph;
		}
		when (abs < phase) return abs + 1;
	}
	
	public atomic def drop(ph: int): void {
		--count;
		if (-ph != phase) resume();
	}
}
