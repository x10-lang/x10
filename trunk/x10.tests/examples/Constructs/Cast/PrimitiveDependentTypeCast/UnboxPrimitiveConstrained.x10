/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
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
		
      var ni: int(4) = 4;
		
		// test 1 to primitive
		// (int) <-- int(:c)
		var case1b: int = ni as int;

		// test 2 to primitive constrained
		// must fail compile @see
		// try {
			// (int(:self==3)) <-- int(:c)
			// not null check when unboxing and deptype check
			// int(:self==3) case2a = (int(:self==3)) ni; 
		// } catch (ClassCastException e) {
			// res2 = true;
		// }

		// test 3 to nullable primitive
		// (nullable<int>) <-- int(:c)
		var case3a: Box[int] = ni as Box[int]; // no check

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
