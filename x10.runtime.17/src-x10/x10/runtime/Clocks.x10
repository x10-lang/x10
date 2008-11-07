/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.HashMap;

/**
 * @author tardieu
 */
class Clocks extends HashMap[Clock_c,Int] {
	def register(clock: Clock_c): void {
		clock.register_c(this);
	}

	def register(rail: Rail[Clock_c]): void {
		for(clock: Clock_c in rail) register(clock);
	}

	def next(): void {
		for(clock: Clock_c in keySet()) clock.resume_c();
		for(clock: Clock_c in keySet()) clock.next_c();
	}

	def drop(): void {
		for(clock: Clock_c in keySet()) clock.drop_c();
	}
}
