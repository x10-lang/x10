
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
 * See whether recursion is handled properly. This program
 * should not run forever. In fact the compiler should determine
 * that it cannot compute this type (e.g. by tripping across a pre-determined
 * maximum recursion depth) and then return an unknown type.
 *
 * @author vj 10/10
 */
public class RecursiveTypeDefs_MustFailCompile extends x10Test {
	static type foo = fum;  // ERR: Reached max macro expansion depth with RecursiveTypeDefs_MustFailCompile.foo 
	static type fum = foo;  // ERR: Could not compute type.

	public def run()=true;

	public static def main(var args: Rail[String]): void = {
		new RecursiveTypeDefs_MustFailCompile().execute();
	}
}

