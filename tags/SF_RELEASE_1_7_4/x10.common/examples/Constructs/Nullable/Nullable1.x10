/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Casting nullable String to String when value is null
 * should cause a class cast exception
 */
public class Nullable1 extends x10Test {
	public boolean run() {
		boolean gotNull = false;
		try {
			nullable<String> x = null;
			String y = (String)x;
			X.use(y);
		} catch (ClassCastException e) {
			gotNull = true;
		}
		return gotNull;
	}

	public static void main(String[] args) {
		new Nullable1().execute();
	}

	static class X {
		public static void use(String y) { }
	}
}

