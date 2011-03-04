/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks an overflow is statically detected when a constant is to assign.
 * Issue: At compile time we can infer the integer constant has been overflowed,
  *       and throw an exception as constraint is not meet.
 * @author vcave
 
 **/
public class Long_ConstraintDeclaredAsIntegerOverFlow2_MustFailCompile extends x10Test {

	 public boolean run() {
		boolean result = false;
		final long notAnInt = 2147493648L ;
		final int(:self == -2147473648) b = (int(:self== -2147473648)) notAnInt;
		System.out.println("" + " bound=" + b
				+ " notAnInt=" + notAnInt);
		try {
		//  this time constraint is a long but value to assign is an overflowed integer
		// Hence at compile time we can state contraint value is != from constant.
		long(:self==2147493648L) l3 = (long(:self==2147493648L)) b;
		} catch (ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new  Long_ConstraintDeclaredAsIntegerOverFlow2_MustFailCompile().execute();
	}

}
 