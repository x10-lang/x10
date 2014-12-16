/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * Testing an at spawned to a field access.
 */
public class AtFieldAccess extends x10Test {

	var t: T;
	public def run():boolean {
	        val second = Place.places().next(here);
		val r  = Region.makeRectangular(0, 0);
		val D = r->second;
		for (p  in D.region) {
			t = at (D(p)) new T();
		}
		val tt = this.t;
		val ttroot = tt.root;
		at (ttroot) ttroot().i = 3n;
		return 3n == (at(ttroot) ttroot().i);
	}

	public static def main(Rail[String]) {
		new AtFieldAccess().execute();
	}

	static class T {
		private val root = GlobalRef[T](this);
		transient public var i: int;
	}
}
