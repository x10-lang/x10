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
 * should cause a class cast exception.
 * (unless T is itself nullable S)
 * This is also true for T == Object
 */
public class NullableObject extends x10Test {
	public boolean run() {
		boolean gotNull = false;
		try {
			nullable<x10.lang.Object> x = X.mynull();
			x10.lang.Object y = (x10.lang.Object) x;
			X.use(y);
		} catch (ClassCastException e) {
			gotNull = true;
		}
		return gotNull;
	}

	public static void main(String[] args) {
		new NullableObject().execute();
	}

	static class X {
		public static nullable<x10.lang.Object> mynull() { return null; }
		public static void use(x10.lang.Object y) { }
	}
}

