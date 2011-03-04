/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks Unboxing from a primitive cast works.
 * @author vcave
 **/
 public class UnboxPrimitive extends x10Test {

	public boolean run() {
		boolean res2 = false ,res4 = false;
		
		int ni = 4;
		
		// test 1 to primitive
		// (int) <-- int
		int case1a = (int) ni; // no check
		
		// test 2 to primitive constrained
		try {
			// (int(:self==3)) <-- int
			// check deptype is valid
			int(:self==3) case2a = (int(:self==3)) ni;
		} catch (ClassCastException e) {
			res2 = true;
		}
		
		// test 3 to nullable primitive
		// (nullable<int>) <-- int
		nullable<int> case3a = (nullable<int>) ni; // no check

		// test 4 to nullable primitive constrained
		try {
			// (nullable<int(:self==3)>) <-- int
			// check deptype
			nullable<int(:self==3)> case4b = (nullable<int(:self==3)>) ni; //deptype check
		} catch (ClassCastException e) {
			res4 = true;
		}
		
		return res2 && res4;
	}
	

	public static void main(String[] args) {
		new UnboxPrimitive().execute();
	}
}