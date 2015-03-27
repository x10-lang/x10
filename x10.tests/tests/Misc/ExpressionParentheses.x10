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
 * Expression parentheses test.
 */
public class ExpressionParentheses extends x10Test {

	var x: UInt = 0x80000000un;
	var n: UInt = 16un;
	var z: UInt;

	public def run(): boolean {
		z = ((x ^ (x>>8) ^ (x>>31)) & (n-1un));
		if (z != 1un) return false;
		z = ((x | (n-1un)) & 1un);
		if (z != 1un) return false;
		return true;
	}

	public static def main(var args: Rail[String]): void {
		new ExpressionParentheses().execute();
	}
}
