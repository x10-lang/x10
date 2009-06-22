/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for atomic method qualifier.
 */
public class AtomicMethodTest extends x10Test {

	long val = 0;
	const int N = 1000;
	long startCount = 0;
	long endCount = 0;
	atomic void body() {
		startCount = this.val;
		for (int i = 0; i < N; i++) this.val++;
		endCount = this.val;
	}

	public boolean run() {
		async(this) body();
		for (long i = 0; i < N*100; i++) {
			boolean b; // temp
			atomic { this.val = i; b = (endCount != 0); }
			if (b) break;
		}
		// assuming atomics follow program order
		boolean b; // temp;
		atomic { b = (startCount + N == endCount); }
		return b;
	}

	public static void main(String[] args) {
		new AtomicMethodTest().execute();
	}
}

