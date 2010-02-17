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
 * Testing quotes in strings.
 */
public class StringTest extends x10Test {

	public var v: int;
	public def this(): StringTest = {
		v = 10;
	}

	public def run(): boolean = {
		var foo: String = "the number is "+v;
		if (!(v == 10 && foo.equals("the number is "+"10"))) return false;
		if (foo.charAt(2) != 'e') return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new StringTest().execute();
	}
}
