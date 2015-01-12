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

/**
 * Checks comparison between a primitive and a boxed one
 *
 * @author vcave
 */
public class ObjectEqualsPrimitive extends x10Test {

	public def run(): boolean = {
		var x: Any = 2+1;
		var res: boolean = 3==x;
		res &= x==3;
		return res;
	}

	public static def main(Rail[String])  {
		new ObjectEqualsPrimitive().execute();
	}
}
