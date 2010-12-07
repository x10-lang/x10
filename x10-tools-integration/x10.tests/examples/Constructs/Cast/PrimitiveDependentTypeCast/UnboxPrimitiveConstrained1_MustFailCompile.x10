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
 * Purpose: Checks Unboxing from a primitive constrained cast works.
 * Issue: deptype type information helps detects wrong cast at compile time.
 * @author vcave
 **/
 public class UnboxPrimitiveConstrained1_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var res2: boolean = false;
		
      var ni: int(4) = 4;
		
		try {
			// (int(:self==3)) <-- int(:c)
			// not null check when unboxing and deptype check
         var case2a: int(3) = ni as int(3); // ERR
		} catch (e: ClassCastException) {
			res2 = true;
		}

		return res2;
	}

	public static def main(var args: Array[String](1)): void = {
		new UnboxPrimitiveConstrained1_MustFailCompile().execute();
	}
}
