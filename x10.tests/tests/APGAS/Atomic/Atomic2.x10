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
 * atomic enclosing a void function call
 * that throws an exception.
 *
 * @author kemal 4/2005
 */
public class Atomic2 extends x10Test {

	var x: long = 0;


	public def run(): boolean {
		finish async   atomic x++;
		atomic chk(x == 1);

		var gotException: boolean = false;
		try {
			atomic chk(x == 0);
		} catch (var e: Exception) {
			gotException = true;
		}
		chk(gotException);
		return true;
	}

	public static def main(Rail[String]) {
		new Atomic2().execute();
	}
}
