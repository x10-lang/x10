/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test multiple exceptions with multiple children running out of memory.
 *
 * Parent must wait until all children are finished even if
 * any child threw an out-of-memory exception.
 *
 * Note that this test case times-out if memoryHog()
 * shows more extensive memory-consuming behavior than here.
 *
 * @author kemal 4/2005
 */
public class Exceptions2 extends x10Test {

	const int MAXINT = 2147483647;
	const int N = 1000000;
	final java.util.Set a = new java.util.HashSet();
	void memoryHog() { byte[] a = new byte[MAXINT]; X.use(a); }
	public boolean run() {
		final long N = 1000000L;
		final int M = 4;
		final int MIN_MSG_SIZE = 100;
		try {
			finish {
				ateach (point [i]: dist.factory.unique()) {
					foreach (point [j]: [1:M])
						memoryHog();
				}
				async(here) { for (long i = 0; i < N; i++) { Node2 x = new Node2(); X.use(x); } }
			}
			return false;
		} catch (x10.lang.MultipleExceptions me) {
			// Ensure that message is informative
			if ((me.toString()).length() <= MIN_MSG_SIZE) return false;
			//me.printStackTrace();
			return true;
		}
	}

	public static void main(String[] args) {
		new Exceptions2().execute();
	}

	static class Node2 {
		int data;
		nullable<Node2> next;
	}

	static class X {
		static void use(Node2 x) { }
		static void use(byte[] x) { }
	}
}

