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
 * Purpose: Checks primitive type assignment of java.
 * @author vcave
 **/
public class PrimitiveAssignment extends x10Test {
	public def run(): boolean = {
		var b: byte = 2y;
		var c: char = 'c';
		var s: short = 10s;
		var j: int = 124n;
		var l: long = 1;
		var f: float = 0;
		var d: double = 0.001;

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new PrimitiveAssignment().execute();
	}
}
