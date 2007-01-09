/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
// Limitation. This is a limitation of the current release.
// The self constraint in a cast to long is not being checked at runtime.

/**
 * Purpose: Shows a constraint value may be overflowed.
 * Issue: Declared constraint is an overflowed integer, which makes assignment fail at runtime.
 * @author vcave
 **/
public class Long_ConstraintDeclaredAsInteger_OverFlow extends x10Test {

	 private final long overIntMax = ((long) java.lang.Integer.MAX_VALUE) + 10000;
	 
	 public boolean run() {
		try {
			final int bound = (int) overIntMax;
			final int(:self == bound) bound2 =  bound;
			System.out.println("bound=" + bound);
			long(:self==bound2) l2 = (long(:self==bound2)) overIntMax;
		} catch (ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new  Long_ConstraintDeclaredAsInteger_OverFlow().execute();
	}

}
 