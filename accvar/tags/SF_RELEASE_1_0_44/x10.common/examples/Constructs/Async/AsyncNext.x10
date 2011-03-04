/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.lang.Object;
import harness.x10Test;

/**
 * Testing the ability to assign to the field of an object
 * at place here a reference to an object at place here.next().
 *
 * @author vj
 */
public class AsyncNext extends x10Test {

	public boolean run() {
		final place Other = here.next();
		final T t = new T();
		finish async (Other) {
			final T t1 = new T();
			async (t) t.val = t1;
		}
		return t.val.location == Other;
	}

	public static void main(String[] args) {
		new AsyncNext().execute();
	}

	static class T {
		Object val;
	}
}

