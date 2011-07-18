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
 * Test for for loop on an array.
 *
 * @author vj
 */
public class ForLoopOnArray extends x10Test {

	public static N: int = 3;

	public def run(): boolean = {
		val a = new Array[double](0..10, ([i]: Point): double => i as double);

		for (val [i]: Point in a.region) {
			if (a(i) != i as Double) return false;
		}
		return true;
	}

	public static def main(var args: Array[String](1)): void = {
		new ForLoopOnArray().execute();
	}
}
