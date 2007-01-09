/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Shows a constraint value may be overflowed.
 * Issue: Declared constraint is an overflowed integer, which makes assignment fail at runtime.
 * @author vcave, vj
 **/
public class Long_ConstraintDeclaredAsIntegerOverflow_MustFailCompile extends x10Test {

	 private final long overIntMax = ((long) java.lang.Integer.MAX_VALUE) + 10000;
	 
	 public boolean run() {
		try {
			System.out.println(java.lang.Integer.MAX_VALUE);
			// This value cannot fit in an integer, so the compiler must flag an error.
			long(:self==2147493647) l2 = (long(:self==2147493647)) overIntMax;
		} catch (ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new  Long_ConstraintDeclaredAsIntegerOverflow_MustFailCompile().execute();
	}

}
 