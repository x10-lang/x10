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
 * Test for for loop with x10 for (point p: D) syntax
 *
 * @author kemal, 12/2004
 */
public class ForLoop extends x10Test {

	public static N: long = 100;
	var nActivities: int = 0n;

	public def run(): boolean = {
		val r = Region.make(0, N-1);
		val P0 = here;
		val d = r->P0;

		if (!d.region.equals(r)) return false;
		if (d.region.min(0) != 0L) return false;
		if (d.region.max(0) != N-1) return false;

		//Ensure iterator works in lexicographic order
		var n: long = 0;
		var prev: long = d.region.min(0)-1;
		for (p in d) {
			n += p(0);
			if (prev+1 != p(0)) return false;
			prev = p(0);
			if (P0 != d(p)) return false;
		}
		if (n != N*(N-1)/2) return false;

		// now iterate over a region
		n = 0;
		prev = r.min(0)-1;
		for (val p: Point in r) {
			n += p(0);
			if (prev+1 != p(0)) return false;
			prev = p(0);
		}
		if (n != N*(N-1)/2) return false;
		return true;
	}

	public static def main(Rail[String]) {
		new ForLoop().execute();
	}
}
