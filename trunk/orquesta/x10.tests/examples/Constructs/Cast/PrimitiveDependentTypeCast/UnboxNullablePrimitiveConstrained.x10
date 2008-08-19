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
 * @author vcave
 **/
 public class UnboxNullablePrimitiveConstrained extends x10Test {

	public def run(): boolean = {
		var res1: boolean = false;
		
		var ni: Box[int{self==4}] = 4;
		var nn: Box[int{self==4}] = null;
		
		// test 1 to primitive		
		// (int) <-- nullable<int>
		var case1a: int = ni to int; // not null check

		try {
			// (int) <-- nullable<int>
			var case1b: int = nn to int; // not null check
		} catch (var e: ClassCastException) {
			res1 = true;
		}

		// should fail compile
		// try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
			// int(:self==3) case2a = (int(:self==3)) ni; 
		// } catch (ClassCastException e) {
			// res2 = true;
		// }
		
		// try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
			// int(:self==3) case2b = (int(:self==3)) nn; 
		// } catch (ClassCastException e) {
			// res2 &= true;
		// }
		
		// (nullable<int>) <-- nullable<int>
		var case3a: Box[int] = ni to Box[int]; // no check

		// (nullable<int>) <-- nullable<int> (null)
		var case3b: Box[int] = nn to Box[int]; // no check


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
	

	public static def main(var args: Rail[String]): void = {
		new UnboxNullablePrimitiveConstrained().execute();
	}
}
