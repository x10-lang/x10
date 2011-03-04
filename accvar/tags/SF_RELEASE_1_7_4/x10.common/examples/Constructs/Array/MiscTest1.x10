/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests miscellaneous features together: async, future, atomic,
 * distributed array, dist ops, reduction, scan, lift.
 *
 * @author kemal 12/2004
 */
public class MiscTest1 extends x10Test {

	const int N = 50;

	const int NP = place.MAX_PLACES;

	public boolean run() {

		final region(:rank==1) R = [0:NP-1];

		// verify that a blocked dist for
		// (0..MAX_PLACES-1) is a unique dist
		// verify that a cyclic dist for
		// (0..MAX_PLACES-1) is again the same
		final dist(:rank==1) D = (dist(:rank==1)) dist.factory.block(R);
		final dist D2 = dist.factory.unique(place.places);
		final dist D3 = dist.factory.cyclic(R);

		chk(D.equals(D2));
		chk(D.equals(D3));

		// create zero int array x
		final int[:distribution==D] x = new int[D];

		// set x[i] = N*i with N atomic updates
		finish
			for (point pi[i]: R)
				for (point [j]: [0:N-1])
					async(D[pi]) atomic x[pi] += i;


		// ensure sum = N*SUM(int i = 0..NP-1)(i);
		// == N*((NP*(NP-1))/2)
		final int sum = x.sum();
		chk(sum == (N*NP*(NP-1)/2));


		// also verify each array elem x[i] == N*i;
		// test D|R restricton and also D-D1

		final region(:rank==1) r_inner = [1:NP-2];
		final dist(:rank==1) D_inner = D|r_inner;
		final dist(:rank==1) D_boundary = D-r_inner;
		finish
			ateach (point pi[i]: D_inner) {
				chk(x[pi] == N*i);
				chk(x[i] == N*i);
				chk(D[pi] == D_inner[pi] && D[pi] == here);
			}

		finish
			ateach (point pi[i]: D_boundary) {
				chk(x[pi] == N*i);
				chk(D[pi] == D_boundary[pi] && D[pi] == here);
			}

		// test scan
		final int[:distribution==D] y = (int[:distribution==D]) x.scan(intArray.add, 0);
		// y[i] == x[i]+y[i-1], for i>0
		finish
			ateach (point pi[i]: D) {
				final point pi1 = [i-1];
				chk(y[pi] == x[pi] + (i == 0 ? 0 : future(D[pi1]){y[pi1]}.force()));
				chk(y[i] == x[i] + (i == 0 ? 0 : future(D[i-1]){y[i-1]}.force()));
			}
		// y[NP-1] == SUM(x[0..NP-1])
		final point pNP_1 = [NP-1];
		chk(sum == future(D[pNP_1]){y[pNP_1]}.force());
		chk(sum == future(D[NP-1]){y[NP-1]}.force());

		// test lift
		final int[:distribution==D] z = (int[:distribution==D]) x.lift(intArray.add, y);

		finish
			ateach (point pi: D) chk(z[pi] == x[pi] + y[pi]);

		// now write back zeros to x
		x.update(new int[D]);

		// ensure x is all zeros
		chk(x.sum() == 0);

		return true;
	}

	public static void main(String[] args) {
		new MiscTest1().execute();
	}
}

