/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * Test for for loop with x10 for (point p: 1:N) syntax.
 *
 * @author kemal, 1/2005
 */
public class ForLoop3 extends x10Test {

	public static N: int = 100n;

	public def run(): boolean = {
		//Ensure iterator works in lexicographic order
		var n: long = 0L;
		var prev: long = -1L;
		for (val p: Point in Region.make(0n, N-1n)->here) {
			n += p(0n);
			if (prev+1 != p(0n)) return false;
			prev = p(0n);
		}
		if (n != N*(N-1n)/2L) return false;

		// now iterate over a region
		n = 0L;
		prev = -1L;
		for (val p: Point in Region.make(0n, N-1n)->here) {
			n += p(0n);
			if (prev+1 != p(0n)) return false;
			prev = p(0n);
		}
		if (n != N*(N-1n)/2L) return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ForLoop3().execute();
	}
}
