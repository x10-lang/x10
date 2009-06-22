/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test multiple exceptions.
 *
 * Behavior of conjunctive finish:
 *
 * If any child activity does not terminate,
 * parent will not terminate, even if other children threw exceptions.
 *
 * Desired behavior of test: must time out.
 *
 * @author kemal 5/2005
 */
public class Exceptions3_MustFailTimeout extends x10Test {

	const int MAXINT = 2147483647;
	void memoryHog() { byte[] a = new byte[MAXINT]; X.use(a); }
	public boolean run() {
		final int M = 4;
		try {
			finish {
				ateach (point [i]: dist.factory.unique()) {
					foreach (point [j]: [1:M])
						memoryHog();
				}
				async(here) await(!X.t());
			}
		} catch (x10.lang.MultipleExceptions me) {
		}
		return true;
	}

	public static void main(String[] args) {
		new Exceptions3_MustFailTimeout().execute();
	}

	static class X {
		static void use (byte[] x) { }
		static boolean t() { return true; }
	}
}

