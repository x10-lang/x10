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

import x10.util.Box;
/**
 * Purpose: Checks Unboxing from a primitive constrained cast works.
 * @author vcave
 **/
 public class UnboxPrimitiveConstrained extends x10Test {

	public def run(): boolean = {
		var res1: boolean = true;
		
      var ni: int(4n) = 4n;
		
		// test 1 to primitive
		// (int) <-- int(:c)
		var case1b: int = ni as int;

		// test 2 to primitive constrained
		// must fail compile @see
		// try {
			// (int(:self==3n)) <-- int(:c)
			// not null check when unboxing and deptype check
			// int(:self==3n) case2a = (int(:self==3n)) ni; 
		// } catch (ClassCastException e) {
			// res2 = true;
		// }

		// test 3 to nullable primitive
		// (nullable<int>) <-- int(:c)
		// var case3a: Box[int] = ni as Box[int];  // requires Box to be covariant, and variance was removed 

		// test 4 to nullable primitive constrained
		// must fail compile @see	
		//try {
			// (nullable<int(:self==3)>) <-- int(:c)
		//	nullable<int(:self==3)> case4b = (nullable<int(:self==3)>) ni; //deptype check
		//} catch (ClassCastException e) {
		//	res4 = true;
		//}
		
		return res1;
	}
	

	public static def main(var args: Rail[String]): void = {
		new UnboxPrimitiveConstrained().execute();
	}
}
