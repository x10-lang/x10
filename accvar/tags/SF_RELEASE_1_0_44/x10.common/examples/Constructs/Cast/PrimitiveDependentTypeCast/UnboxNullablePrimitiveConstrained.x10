/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks Unboxing from a nullable primitive constrained cast works.
 * @author vcave
 **/
 public class UnboxNullablePrimitiveConstrained extends x10Test {

	public boolean run() {
		boolean res1 = false;
		
		nullable<int(:self==4)> ni = 4;
		nullable<int(:self==4)> nn = null;
		
		// test 1 to primitive		
		// (int) <-- nullable<int>
		int case1a = (int) ni; // not null check

		try {
			// (int) <-- nullable<int>
			int case1b = (int) nn; // not null check
		} catch (ClassCastException e) {
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
		nullable<int> case3a = (nullable<int>) ni; // no check

		// (nullable<int>) <-- nullable<int> (null)
		nullable<int> case3b = (nullable<int>) nn; // no check


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
	

	public static void main(String[] args) {
		new UnboxNullablePrimitiveConstrained().execute();
	}
}