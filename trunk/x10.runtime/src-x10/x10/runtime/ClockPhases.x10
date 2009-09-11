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
	def register(clocks:ValRail[Clock]{self.at(here)}, phases:ValRail[Int]{self.at(here)}):Void {
		for(var i:Int = 0; i < clocks.length; i++) 
		    this.put(clocks(i) as Clock_c, phases(i));
	}

	def next():Void {
		for(clock:Clock_c in keySet()) clock.resume_c();
		for(clock:Clock_c in keySet()) clock.next_c();
	}

	def drop():Void {
		for(clock:Clock_c in keySet()) clock.drop_c();
	}
}
