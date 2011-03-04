/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that dep clauses are cheked when checking statically if a cast can be valid at runtime.
 */
public class IntToInt1 extends x10Test {
	public boolean run() {
		int(:self==0) zero = 0;
		int(:self==1) one = 1;
		int i = (int) one;
		one = (int(:self==1)) i;
		return true;
	}

	public static void main(String[] args) {
		new IntToInt1().execute();
	}


}

