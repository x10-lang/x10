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
 public class UnboxNullablePrimitiveConstrained2_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var res4: boolean = false;
		
		var ni: Box[int{self==4}] = 4;

		try {
			// (nullable<int(:self==3)>) <-- nullable<int(:c)>
			var case4a: Box[int{self==3}] = ni to Box[int{self==3}];
		} catch (var e: ClassCastException) {
			res4 = true;
		}

		return res4;
	}
	

	public static def main(var args: Rail[String]): void = {
		new UnboxNullablePrimitiveConstrained2_MustFailCompile().execute();
	}
}
