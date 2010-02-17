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
 * 3 should be an int, and ints are objects.
 *
 * @author vj, igor 09/06
 */
public class PrimitiveHasLocation_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var x: int = 3;
		//this must fail compilation
		return x.home==null;
	}

	public static def main(Rail[String]) {
		new PrimitiveHasLocation_MustFailCompile().execute();
	}
}
