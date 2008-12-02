/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Converting nullable future int to future int causes
 * exception when value is null.
 */
public class NullableFuture2 extends x10Test {
	public boolean run() {
		boolean gotNull = false;
		nullable<future<int>> x;
		if (!X.t()) {
			x = future { 42 };
		} else {
			x = null;
		}
		try {
			X.use(((future<int>)x).force());
		} catch (ClassCastException e) {
			gotNull = true;
		}
		return gotNull;
	}

	public static void main(String[] args) {
		new NullableFuture2().execute();
	}

	static class X {
		public static boolean t() { return true; }
		public static void use(int x) { }
	}
}

