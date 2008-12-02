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

	public boolean run() {
		boolean res1 = false, res2 = false, res4 = false;
		
		nullable<int> ni = 4;
		nullable<int> nn = null;
		
		// test 1 to primitive
		// (int) <-- nullable<int>
		int case1a = (int) ni; // not null check

		try {
			// (int) <-- nullable<int>
			int case1b = (int) nn; // not null check
		} catch (ClassCastException e) {
			res1 = true;
		}

		
		// test 2 to primitive constrained
		try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
			int(:self==3) case2a = (int(:self==3)) ni; 
		} catch (ClassCastException e) {
			res2 = true;
		}
		

		try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
			int(:self==3) case2b = (int(:self==3)) nn; 
		} catch (ClassCastException e) {
			res2 &= true;
		}
		
		// test 3 to nullable primitive		
		// (nullable<int>) <-- nullable<int>
		nullable<int> case3a = (nullable<int>) ni; // no check

		// (nullable<int>) <-- nullable<int> (null)
		nullable<int> case3b = (nullable<int>) nn; // no check


		// test 4 to nullable primitive constrained
		try {
			// (nullable<int(:self==3)>) <-- nullable<int>
			nullable<int(:self==3)> case4b = (nullable<int(:self==3)>) ni; //deptype check
		} catch (ClassCastException e) {
			res4 = true;
		}
		
		try {
			// (nullable<int(:self==3)>) <-- nullable<int> (null)
			nullable<int(:self==3)> case4b = (nullable<int(:self==3)>) nn; //deptype check
		} catch (ClassCastException e) {
			res4 &= true;
		}

		return res1 && res2 && res4;
	}
	

	public static void main(String[] args) {
		new UnboxNullablePrimitive().execute();
	}
}