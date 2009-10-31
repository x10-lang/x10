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
			val case1b = nn as int; // not null check
		} catch (e: ClassCastException) {
			res1 = true;
		} catch (e: NullPointerException) {
			res1 = true;
		}

		
		// test 2 to primitive constrained
		try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
			val case2a = ni.value as int{self==3};
		} catch (e: ClassCastException) {
			res2 = true;
		}
		

		try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
			val case2b = nn.value as int{self==3};
		} catch (e: ClassCastException) {
			res2 &= true;
		} catch (e: NullPointerException ) {
			res2 &=true;
		}
		

		return res1 && res2;
	}
	

	public static def main(var args: Rail[String]): void = {
		new UnboxNullablePrimitive().execute();
	}
}
