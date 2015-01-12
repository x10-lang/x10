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
import x10.util.Box;
/**
 * Checks 3 is an object which can be compared to a nullable
 *
 * @author vcave
 */
public class NullableObjectEqualsPrimitive2_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var x: Box[Any] = null;
		// This use of == must generate a compiler error
		var res1: boolean = 3==x; // ERR
		var res2: boolean = x==3; // ERR
		return !res1 && !res2;
	}

	public static def main(Rail[String]) {
		new NullableObjectEqualsPrimitive2_MustFailCompile().execute();
	}
}

