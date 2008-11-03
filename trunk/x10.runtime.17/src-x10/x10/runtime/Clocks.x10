/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.List;
import x10.util.HashMap;

/**
 * @author tardieu
 */
class Clocks extends HashMap[Clock_c,int] {
	def register(clock: Clock): void {
		(clock as Clock_c).register_c(this);
	}

	def register(list: List[Clock]): void {
		for(clock: Clock in list) register(clock);
	}

	def next(): void {
		for(clock: Clock_c in keySet()) clock.resume_c();
		for(clock: Clock_c in keySet()) clock.next_c();
	}

	def drop(): void {
		for(clock: Clock_c in keySet()) clock.drop_c();
	}
}
