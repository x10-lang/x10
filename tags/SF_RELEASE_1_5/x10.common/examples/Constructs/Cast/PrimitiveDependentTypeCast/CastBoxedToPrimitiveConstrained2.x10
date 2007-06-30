/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks unboxing works properly when dependent type occurs in the cast.
 * Issue: Unboxed object does not meet declared cast constraint.
 * @author vcave
 **/
 public class CastBoxedToPrimitiveConstrained2 extends x10Test {

	public boolean run() {
		try {
			x10.lang.Object obj = 2;
			// At runtime, cast checking code detects unboxed object does
			// not meet constraint.
			int i = (int(:self==3)) obj; 
		} catch (ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new CastBoxedToPrimitiveConstrained2().execute();
	}
}