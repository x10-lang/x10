/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing an async spawned to a field access.
 */
public class AsyncFieldAccess extends x10Test {

	T t;
	public boolean run() {
		place Second = place.FIRST_PLACE.next();
		region r = [0:0];
		final dist D = r->Second;
		finish ateach (point p: D) {
			final T NewT = new T();
			async (this) { t = NewT; }
		}
		finish async (t) { atomic t.i = 3; }
		return 3 == future(t){t.i}.force();
	}

	public static void main(String[] args) {
		new AsyncFieldAccess().execute();
	}

	static class T extends x10.lang.Object {
		public int i;
	}
}

