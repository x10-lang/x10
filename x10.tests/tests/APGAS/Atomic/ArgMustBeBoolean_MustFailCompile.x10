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
 * The argument to when must be boolean
 * @author vj  9/2006
 */
public class ArgMustBeBoolean_MustFailCompile extends x10Test {

        var b: int;

	public def run(): boolean = {
		when(b) {} // ERR: condition must be Boolean. must fail at compile time.
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ArgMustBeBoolean_MustFailCompile().execute();
	}

	
}
