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
 * Checks 3 (struct) can be compared to null (object)
 * by upcasting to equals on Any.
 *
 * @author vcave
 */
public class NullableObjectEqualsPrimitive extends x10Test {

	public def run(): boolean {
		val x:Any = null;
	        val y:Any = 3;
		val res1 = (3).equals(x);
		val res2 =  y.equals(x);
		return !(res1 || res2);
	}

	public static def main(Rail[String]) {
		new NullableObjectEqualsPrimitive().execute();
	}
}
