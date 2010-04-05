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
 * Test to check that unsafe is being parsed correctly.
 */
public class Unsafe extends x10Test {

	public def run(): boolean = {
		var e: Region = [1..10];
		var r: Region = [e,e,e,e];
		var d: Dist = r->here;

		var x = DistArray.make[int](d); // ok
		var y = DistArray.make[int](d); //ok
		var y1 = DistArray.make[int](d); // ok
		var zz = DistArray.make[int](d, (p: Point): int => 41); // bad
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Unsafe().execute();
	}
}
