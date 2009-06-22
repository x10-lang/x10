/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing complex async bodies.
 *
 * @author Kemal Ebcioglu 4/2005
 */
public class AsyncTest2 extends x10Test {

	public boolean run() {
		final int NP = place.MAX_PLACES;
		final int[.] A = new int[dist.factory.unique()];
		finish
			for (point [k]: [0:NP-1])
				async (A.distribution[k])
					ateach (point [i]: A) atomic A[i] += i;
		finish ateach (point [i]: A) { chk(A[i] == i*NP); }

		return true;
	}

	public static void main(String[] args) {
		new AsyncTest2().execute();
	}
}

