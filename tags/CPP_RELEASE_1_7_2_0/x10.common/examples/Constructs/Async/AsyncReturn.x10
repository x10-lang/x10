/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing returns in an async body.
 *
 * @author vj
 */
public class AsyncReturn extends x10Test {

	public boolean run() {
		class T {
			int t;
		}
		final T f = new T();
		f.t = 1;
		final int v = f.t;
		finish async {
			if (v == 1)
				return;
			async (f) {
				atomic {
					f.t = 2;
				}
			}
		}
		return (f.t == 1);
	}

	public static void main(String[] args) {
		new AsyncReturn().execute();
	}
}

