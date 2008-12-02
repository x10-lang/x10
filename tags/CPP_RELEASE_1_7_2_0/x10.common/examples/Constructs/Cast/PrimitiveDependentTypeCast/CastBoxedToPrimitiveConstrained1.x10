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
 * @author vcave
 **/
 public class CastBoxedToPrimitiveConstrained1 extends x10Test {

	public boolean run() {
		x10.lang.Object obj = 3;
		// an additionnal check is needed to ensure unboxed primitive meet constraints.
		int i = (int(:self==3)) obj;
		return true;
	}

	public static void main(String[] args) {
		new CastBoxedToPrimitiveConstrained1().execute();
	}
}