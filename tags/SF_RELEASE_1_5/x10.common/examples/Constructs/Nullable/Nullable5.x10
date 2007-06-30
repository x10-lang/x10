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
 * should not cause a class cast exception
 * if T is itself nullable S.
 *
 * nullable nullable T should be treated as identical to nullable T.
 *
 * @author kemal, 12/2004
 */
public class Nullable5 extends x10Test {
	public boolean run() {
		boolean gotNull = false;
		try {
			nullable<nullable<Nullable5>> x = X.mynull();
			nullable<Nullable5>y = (nullable<Nullable5>) x;
			// x and y are of the same type
			if (x == y) X.use(y);
			// y and use2 argument are of the same type
			X.use2(y);
		} catch (ClassCastException e) {
			gotNull = true;
		}
		return !gotNull;
	}

	public static void main(String[] args) {
		new Nullable5().execute();
	}

	static class X {
		public static void use(nullable<Nullable5> y) { }
		public static void use2(nullable<nullable<nullable<Nullable5> > > y) { }
		public static nullable<nullable<Nullable5> > mynull() { return null; }
	}
}

