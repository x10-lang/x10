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
class ClockPhases extends HashMap[Clock_c,Int] {
	def register(clock:Clock_c, phase:Int):Void {
		clock.register_c(this, phase);
	}

	def register(clocks:ValRail[Clock_c], phases:ValRail[Int]):Void {
		for(var i:Int = 0; i < clocks.length; i++) register(clocks(i), phases(i));
	}

	def next():Void {
		for(clock:Clock_c in keySet()) clock.resume_c();
		for(clock:Clock_c in keySet()) clock.next_c();
	}

	def drop():Void {
		for(clock:Clock_c in keySet()) clock.drop_c();
	}
}
