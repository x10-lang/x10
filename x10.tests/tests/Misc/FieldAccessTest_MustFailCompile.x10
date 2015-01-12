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
 * This produces an infinite loop for the type checker, on 06/25/06
 */
public class FieldAccessTest_MustFailCompile extends x10Test {
	public var n: Any;

	/**
	 * The method is deliberately type-incorrect.
	 * It should return nullable Any.
	 * The problem is that this incorrect program causes the compiler to loop.
	 */
	public def n(): FieldAccessTest_MustFailCompile = n; // ERR

	public def run()  = true;

	public static def main(var args: Rail[String]): void = {
		new FieldAccessTest_MustFailCompile().execute();
	}
}
