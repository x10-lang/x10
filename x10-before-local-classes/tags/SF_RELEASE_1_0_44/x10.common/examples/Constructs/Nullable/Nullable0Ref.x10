/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Casting nullable T to T when the value is null
 * should cause a null pointer exception.
 * (unless T is itself nullable S)
 */
public class Nullable0Ref extends x10Test {
	public boolean run() {
		boolean gotNull = false;
		try {
			nullable<Nullable0Ref> x = X.mynull();
			Nullable0Ref y = (Nullable0Ref) x;
			X.use(y);
		} catch (ClassCastException e) {
			gotNull = true;
		}
		return gotNull;
	}

	public static void main(String[] args) {
		new Nullable0Ref().execute();
	}

	static class X {
		public static nullable<Nullable0Ref> mynull() { return null; }
		public static void use(Nullable0Ref y) { }
	}
}

