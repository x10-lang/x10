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
class Clocks extends HashMap[Clock,int] {
	def register(clock: Clock): void {
		clock._register(this);
	}

	def register(list: List[Clock]): void {
		for(clock: Clock in list) register(clock);
	}

	def next(): void {
		for(clock: Clock in keySet()) clock._resume();
		for(clock: Clock in keySet()) clock._next();
	}

	def drop(): void {
		for(clock: Clock in keySet()) clock._drop();
	}
}
