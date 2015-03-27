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

/**
 * Should report the type mismatch in the comparison in the loop, and fail
 * to compile gracefully.
 *
 * @author Bin Xin (xinb@cs.purdue.edu)
 */
public class NullableComparison extends x10Test {

	public static N: long = 6;

	public def run(): boolean {
        // [DC] this appears to be a perfectly reasonable program that should execute correctly
		val objList = new Rail[Empty](N);
		val obj: Empty = new Empty();
		var i: long = N - 1;
		while (i > 0 && (obj != objList(i))) {
			i--;
		}
		if (i > 0)
			return false;
		return true;
	}

	public static def main(Rail[String]) {
		new NullableComparison().execute();
	}
}
