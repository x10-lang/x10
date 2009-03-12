/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks Unboxing from a nullable primitive cast works.
 * @author vcave
 **/
 public class UnboxNullablePrimitive extends x10Test {

	public def run(): boolean = {
		var res1: boolean = false;
                var res2: boolean = false;
                var res4: boolean = false;
		
		var ni: Box[int] = 4;
		var nn: Box[int] = null;
		
		// test 1 to primitive
		// (int) <-- nullable<int>
		var case1a: int = ni as int; // not null check

		try {
			// (int) <-- nullable<int>
			var case1b: int = nn as int; // not null check
		} catch (e: ClassCastException) {
			res1 = true;
		}

		
		// test 2 to primitive constrained
		try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
			var case2a: int{self==3} = ni as int{self==3};
		} catch (e: ClassCastException) {
			res2 = true;
		}
		

		try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
			var case2b: int{self==3} = nn as int{self==3};
		} catch (var e: ClassCastException) {
			res2 &= true;
		}
		
		// test 3 to nullable primitive		
		// (nullable<int>) <-- nullable<int>
		var case3a: Box[int] = ni as Box[int]; // no check

		// (nullable<int>) <-- nullable<int> (null)
		var case3b: Box[int] = nn as Box[int]; // no check


		// test 4 to nullable primitive constrained
		try {
			// (nullable<int(:self==3)>) <-- nullable<int>
			var case4b: Box[int{self==3}] = ni as Box[int{self==3}]; //deptype check
		} catch (var e: ClassCastException) {
			res4 = true;
		}
		
		try {
			// (nullable<int(:self==3)>) <-- nullable<int> (null)
			var case4b: Box[int{self==3}] = nn as Box[int{self==3}]; //deptype check
		} catch (var e: ClassCastException) {
			res4 &= true;
		}

		return res1 && res2 && res4;
	}
	

	public static def main(var args: Rail[String]): void = {
		new UnboxNullablePrimitive().execute();
	}
}
