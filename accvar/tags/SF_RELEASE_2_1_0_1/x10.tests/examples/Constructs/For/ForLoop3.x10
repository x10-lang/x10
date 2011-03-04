/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Test for for loop with x10 for (point p: 1:N) syntax.
 *
 * @author kemal, 1/2005
 */
public class ForLoop3 extends x10Test {

	public static N: int = 100;

	public def run(): boolean = {
		//Ensure iterator works in lexicographic order
		var n: int = 0;
		var prev: int = -1;
		for (val p: Point in 0..N-1->here) {
			n += p(0);
			if (prev+1 != p(0)) return false;
			prev = p(0);
		}
		if (n != N*(N-1)/2) return false;

		// now iterate over a region
		n = 0;
		prev = -1;
		for (val p: Point in 0..N-1->here) {
			n += p(0);
			if (prev+1 != p(0)) return false;
			prev = p(0);
		}
		if (n != N*(N-1)/2) return false;
		return true;
	}

	public static def main(var args: Array[String](1)): void = {
		new ForLoop3().execute();
	}
}
