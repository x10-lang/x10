/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * When the async place is not specified, it is take to be 'here'.
 *
 * @author Kemal Ebcioglu 4/2005
 */
public class AsyncTest5 extends x10Test {

	public boolean run() {
		final int[.] A = new int[dist.factory.unique()];
		chk(place.MAX_PLACES >= 2);
		finish async chk(A.distribution[0] == here);
		// verify unique distribution
		for (point [i]: A)
			for (point [j]: A)
				chk(implies(A.distribution[i] == A.distribution[j],
							i == j));

		// verify async S is async(here)S
		finish ateach (point [i]: A) {
			async { atomic A[i] += i;
				chk(A.distribution[i] == here);
				async(this) async chk(A.distribution[0] == here);
			}
		}
		finish ateach (point [i]: A) {
			chk(A[i] == i);
		}
		return true;
	}

	static boolean implies(boolean x, boolean y) {
		return (!x) | y;
	}

	public static void main(String[] args) {
		new AsyncTest5().execute();
	}
}

