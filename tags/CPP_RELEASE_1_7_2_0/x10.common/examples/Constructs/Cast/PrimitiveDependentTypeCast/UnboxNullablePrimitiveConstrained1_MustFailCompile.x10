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
 * Issue: deptype type information helps detects wrong cast at compile time.
 * @author vcave
 **/
 public class UnboxNullablePrimitiveConstrained1_MustFailCompile extends x10Test {

	public boolean run() {
		boolean res2 = false;
		
		nullable<int(:self==4)> ni = 4;

		// should fail compile
		try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
			int(:self==3) case2a = (int(:self==3)) ni; 
		} catch (ClassCastException e) {
			res2 = true;
		}

		return res2;
	}
	

	public static void main(String[] args) {
		new UnboxNullablePrimitiveConstrained1_MustFailCompile().execute();
	}
}