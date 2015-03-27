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
 * Purpose: Checks null litteral can't be cast to a struct type.
 * @author vcave
 **/
 public class CastNullToReference_MustFailCompile extends x10Test {

	public def run(): boolean {
		var obj:x10.lang.Any = null as Any; // ok in 1.7
		var v:x10.lang.Complex = null as Complex; // ERR not allowed in 2.0 
		return false;
	}

	public static def main(var args: Rail[String]): void {
		new CastNullToReference_MustFailCompile().execute();
	}
}
