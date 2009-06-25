/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: Checks Unboxing from a primitive cast works.
 * @author vcave
 **/
 public class UnboxPrimitive extends x10Test {

	public def run(): boolean = {
		var res2: boolean = falsevar res4: boolean = false;
		
		var ni: int = 4;
		
		// test 1 to primitive
		// (int) <-- int
		var case1a: int = ni to int; // no check
		
		// test 2 to primitive constrained
		try {
			// (int(:self==3)) <-- int
			// check deptype is valid
			var case2a: int{self==3} = ni to int{self==3};
		} catch (var e: ClassCastException) {
			res2 = true;
		}
		
		// test 3 to nullable primitive
		// (nullable<int>) <-- int
		var case3a: Box[int] = ni to Box[int];; // no check

		// test 4 to nullable primitive constrained
		try {
			// (nullable<int(:self==3)>) <-- int
			// check deptype
			var case4b: Box[int{self==3}] = ni to Box[int{self==3}]; //deptype check
		} catch (var e: ClassCastException) {
			res4 = true;
		}
		
		return res2 && res4;
	}
	

	public static def main(var args: Rail[String]): void = {
		new UnboxPrimitive().execute();
	}
}
