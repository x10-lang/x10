/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks Unboxing from a primitive constrained cast works.
 * @author vcave
 **/
 public class UnboxPrimitiveConstrained extends x10Test {

	public boolean run() {
		boolean res1 = true;
		
		int(:self==4) ni = 4;
		
		// test 1 to primitive
		// (int) <-- int(:c)
		int case1b = (int) ni;

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
		nullable<int> case3a = (nullable<int>) ni; // no check

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
	

	public static void main(String[] args) {
		new UnboxPrimitiveConstrained().execute();
	}
}