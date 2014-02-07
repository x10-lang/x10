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
 * Check that an int it generates the correct depclause for its type.
 */
public class IntLitDepType_MustFailCompile extends x10Test {
	public def run(): boolean = {
		var f: int{self==1n} = 2n; // ERR
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new IntLitDepType_MustFailCompile().execute();
	}


}
