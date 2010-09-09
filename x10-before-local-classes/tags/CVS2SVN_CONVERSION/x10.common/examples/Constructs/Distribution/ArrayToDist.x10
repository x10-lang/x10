/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests conversion of arrays to regions/dists.
 *
 * @author kemal 3/2005
 */
public class ArrayToDist extends x10Test {

	const int N = 4;
	public boolean run() {
		final region R = [0:N-1,0:N-1];
		final dist D = dist.factory.block(R);
		final int[.] A1 = new int[D](point p[i,j]) { return f(i, j); };
		final foo[.] A2 = new foo[D](point p[i,j]) { return new foo(f(i, j)); };
		for (point p[i,j]: A1)
			chk(f(i, j) == future(A1.distribution[i,j]) { A1[i,j] }.force(), "1");
		finish foreach (point p[i,j]: A1)
			chk(f(i, j) == future(A1.distribution[i,j]) { A1[i,j] }.force(), "2");
		finish ateach (point p[i,j]: A1)
			chk(f(i, j) == A1[i,j], "3");

		for (point p[i,j]: A2)
			chk(f(i, j) == future(A2.distribution[i,j]) { A2[i,j].val }.force(), "4");
		finish foreach (point p[i,j]: A2)
			chk(f(i, j) == future(A2.distribution[i,j]) { A2[i,j].val }.force(), "5");
		finish ateach (point p[i,j]: A2)
			chk(f(i, j) == A2[i,j].val, "6");

		return true;
	}

	static int f(int i, int j) {
		return N * i + j;
	}

	public static void main(String[] args) {
		new ArrayToDist().execute();
	}

	static class foo {
		public int val;
		public foo(int x) { this.val = x; }
	}
}

