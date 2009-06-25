/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: Checks Unboxing from a nullable primitive constrained cast works.
 * Issue: deptype type information helps detects wrong cast at compile time.
 * @author vcave
 **/
 public class UnboxNullablePrimitiveConstrained1_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var res2: boolean = false;
		
		var ni: Box[int{self==4}] = 4;

		// should fail compile
		try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
			var case2a: int{self==3} = ni to int{self==3};
		} catch (var e: ClassCastException) {
			res2 = true;
		}

		return res2;
	}
	

	public static def main(var args: Rail[String]): void = {
		new UnboxNullablePrimitiveConstrained1_MustFailCompile().execute();
	}
}
