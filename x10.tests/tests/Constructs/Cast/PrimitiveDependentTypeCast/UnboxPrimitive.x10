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
 * Purpose: Checks Unboxing from a primitive cast works.
 * @author vcave
 * @author vj -- updated for 2.0. There is no autoboxing of structs.
 **/
 public class UnboxPrimitive extends x10Test {

	public def run(): boolean {
		var res2: boolean = false;
        var res4: boolean = false;
		
		var ni: int = 4n;
		
		// test 1 to primitive
		// (int) <-- int
		var case1a: int = ni as int; // no check
		
		// test 2 to primitive constrained
		try {
			// (int(:self==3n)) <-- int
			// check deptype is valid
         val case2a = ni as int(3n);
		} catch (e: ClassCastException) {
			res2 = true;
		}
		
		return res2 ;
	}
	

	public static def main(var args: Rail[String]): void {
		new UnboxPrimitive().execute();
	}
}
