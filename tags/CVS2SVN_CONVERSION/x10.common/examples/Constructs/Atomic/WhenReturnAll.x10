/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Returns from all branches of a "when" should work.
 *
 * @author igor, 2/2006
 */
public class WhenReturnAll extends x10Test {

	int test() {
		int ret = 0;
		when (X.t()) {
			return 1;
		} or (X.t()) {
			return 2;
		}
	}

	public boolean run() {
		int x = test();
		return true;
	}

	public static void main(String[] args) {
		new WhenReturnAll().execute();
	}

	static class X {
		static boolean t() { return true; }
	}
}

