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
 * Test resulted in unreachable statement message
 * in the compiler, as of 7/29/2005.
 *
 * @author Armando Solar-Lezama
 */
public class Unreachable3_MustFailTimeout extends x10Test {

	public def run(): boolean = {
		async {
			while (true) { }
		}
		return true;
	}

	public static def main(var args: Array[String](1)): void = {
		new Unreachable3_MustFailTimeout().execute();
	}
}
