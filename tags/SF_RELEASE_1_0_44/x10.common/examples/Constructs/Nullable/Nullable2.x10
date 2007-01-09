/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Casting nullable String to String should not cause
 * exception when the value is not null
 */
public class Nullable2 extends x10Test {
	boolean gotNull;
	public boolean run() {
		gotNull = false;
		try {
			nullable<String> x = "May the force be with you!";
			String y = X.chkForNull(x);
			X.use(y);
		} catch (ClassCastException e) {
			gotNull = true;
		}
		return !gotNull;
	}

	public static void main(String[] args) {
		new Nullable2().execute();
	}

	// to make optimizations difficult
	static class X {
		public static void use(String y) { }

		public static String chkForNull(nullable<String> x) {
			return (String)x;
		}
	}
}

