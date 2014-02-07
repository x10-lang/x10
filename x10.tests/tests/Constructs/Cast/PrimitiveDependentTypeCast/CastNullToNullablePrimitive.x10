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
 * Purpose: Checks cast if litteral null to primitive nullable works.
 * Note: The compiler drops the cast operation, as no checking is needed.
 * @author vcave
 **/
 public class CastNullToNullablePrimitive extends x10Test {

	public def run(): boolean = {
		// Expression type changes to /*nullable*/BoxedInteger
      var i: x10.util.Box[int] = null as x10.util.Box[int];
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastNullToNullablePrimitive().execute();
	}
}
