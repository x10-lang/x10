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

import x10.util.Box;
/**
 * Purpose: Checks Unboxing from a nullable primitive cast works.
 * @author vcave
 **/
 public class UnboxNullablePrimitive extends x10Test {

	public def run(): boolean = {
		var res1: boolean = false;
        var res2: boolean = false;
        var res4: boolean = false;
		
      var ni: Box[int] = new Box[Int](4n);
		var nn: Box[int] = null;
		
		// test 1 to primitive
		// (int) <-- nullable<int>
      var case1a: int = ni.value as int; // not null check

		try {
			// (int) <-- nullable<int>
         val case1b = nn.value as int; // not null check
		} catch (e: ClassCastException) {
			res1 = true;
		} catch (e: NullPointerException) {
			res1 = true;
		}

		
		// test 2 to primitive constrained
		try {
			// (int(:self==3n)) <-- nullable<int>
			// not null check when unboxing and deptype check
         val case2a = ni.value as int(3n);
		} catch (e: ClassCastException) {
			res2 = true;
		}
		

		try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
         val case2b = nn.value as int(3n);
		} catch (e: ClassCastException) {
			res2 &= true;
		} catch (e: NullPointerException ) {
			res2 &=true;
		}
		

		return res1 && res2;
	}
	

   public static def main(Rail[String]) {
		new UnboxNullablePrimitive().execute();
	}
}
