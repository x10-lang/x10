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
 * Purpose: Checks Unboxing from a nullable primitive constrained cast works.
 * @author vcave
 **/
 public class UnboxNullablePrimitiveConstrained extends x10Test {

	public def run(): boolean {
		var res1: boolean = false;
		
      var ni: Box[int(4n)] = new Box[Int(4n)](4n);
      var nn: Box[int(4n)] = null;
		
		// test 1 to primitive		
		// (int) <-- nullable<int>
      var case1a: int = ni.value as int; // not null check

		try {
			// (int) <-- nullable<int>
         var case1b: int = nn.value as int; // not null check
		} catch (var e: ClassCastException) {
			res1 = true;
		} catch (e: NullPointerException) {
			res1 = true;
		}

		// should fail compile
		// try {
			// (int(:self==3n)) <-- nullable<int>
			// not null check when unboxing and deptype check
			// int(:self==3n) case2a = (int(:self==3)) ni; 
		// } catch (ClassCastException e) {
			// res2 = true;
		// }
		
		// try {
			// (int(:self==3n)) <-- nullable<int>
			// not null check when unboxing and deptype check
			// int(:self==3) case2b = (int(:self==3)) nn; 
		// } catch (ClassCastException e) {
			// res2 &= true;
		// }
		
		// (nullable<int>) <-- nullable<int>
		//var case3a: Box[int] = ni as Box[int]; // requires Box to be covariant, and variance was removed 

		// (nullable<int>) <-- nullable<int> (null)
		//var case3b: Box[int] = nn as Box[int]; // no check


		// (nullable<int(:self==3)>) <-- nullable<int>
		// check ni != null, and no deptype check as constraint are expressed on both side
		// nullable<int(:self==3)> case4a = (nullable<int(:self==3)>) ni; //deptype check

		// try {
			// (nullable<int(:self==3)>) <-- nullable<int> (null)
			// nullable<int(:self==3)> case4b = (nullable<int(:self==3)>) nn; //deptype check
		// } catch (ClassCastException e) {
			// res4 = true;
		// }

		// return res1 && res2 && res4;
		return res1;
	}
	

	public static def main(Rail[String]) {
		new UnboxNullablePrimitiveConstrained().execute();
	}
}
