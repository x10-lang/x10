/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Constant promotions to arrays: (D n)
 * disjoint union and overlay of arrays
 * array lift, scan and reduce.
 *
 * This one tests arrays of booleans.
 *
 * @author kemal 4/2005
 */
public class ArrayAlgebra2 extends x10Test {

	const int N = 24;
	boolean[.] makeArray(dist D, final boolean k) {
		return new boolean[D] (point p) { return k; };
	}

	public boolean run() {
		final dist D = dist.factory.blockCyclic([0:N-1], 2);
		final dist D01 = D | [0:N/2-1];
		final dist D23 = D | [(N/2):N-1];
		final dist D0 = D | [0:N/4-1];
		final dist D1 = D | [(N/4):N/2-1];
		final dist D2 = D | [(N/2):3*N/4-1];
		final dist D3 = D | [(3*N/4):N-1];
		final boolean[.] ia1 =
			makeArray(D, false).overlay((makeArray(D01, true) || makeArray(D23, false))
					.overlay(makeArray(D3, true)).overlay(makeArray(D0, false)));
		arrEq(ia1 | D0, makeArray(D0, false));
		arrEq(ia1 | D1, makeArray(D1, true));
		arrEq(ia1 | D2, makeArray(D2, false));
		arrEq(ia1 | D3, makeArray(D3, true));
		// We should eventually support the following:
		//chk(ia1.or() == true);
		//chk(ia1.and() == false);
		//chk(ia1.xor() == false);
		//TODO: scan does not need a unit operand
		arrEq(ia1.scan(booleanArray.or,false),
				new boolean[D](point [i])
				{return (ia1 | [0:i]).reduce(booleanArray.or,false);});
		arrEq((makeArray(D0, true) || makeArray(D1,false)).lift(booleanArray.xor,makeArray(D01, true)),
				(makeArray(D0, false) || makeArray(D1,true)));

		// a1 || a2 where a1, a2 are boolean arrays
		//causes ambiguity with array disjoint union
		//arrEq(makeArray(D01, false) | makeArray(D01, false),
		//      makeArray(D01,false) & makeArray(D01, true));

		return true;
	}

	/**
	 * Throw an error iff x and y are not arrays with same
	 * content and dist
	 */
	static void arrEq(final boolean[.] x, final boolean[.] y) {
		chk(x.distribution.equals(y.distribution));
		finish ateach(point p: x) chk(x[p] == y[p]);
	}

	public static void main(String[] args) {
		new ArrayAlgebra2().execute();
	}
}

