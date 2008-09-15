/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Tests miscellaneous features together: async, future, atomic,
 * distributed array, dist ops, reduction, scan, lift.
 *
 * @author kemal 12/2004
 */
public class MiscTest1 extends x10Test {

	public const N: int = 50;

	public const NP: int = place.MAX_PLACES;

	public def run(): boolean = {

		final val R: region{rank==1} = [0..NP-1];

		// verify that a blocked dist for
		// (0..MAX_PLACES-1) is a unique dist
		// verify that a cyclic dist for
		// (0..MAX_PLACES-1) is again the same
		final val D: dist{rank==1} = (dist{rank==1})) distmakeBlock(R);
		final val D2: dist = distmakeUnique(place.places);
		final val D3: dist = dist.factory.cyclic(R);

		chk(D.equals(D2));
		chk(D.equals(D3));

		// create zero int array x
		final val x: Array[int]{distribution==D} = new Array[int](D);

		// set x[i] = N*i with N atomic updates
		finish
			for (val pi: point[i] in R) for (val (j): point in [0..N-1]) async(D(pi)) atomic x(pi) += i;


		// ensure sum = N*SUM(int i = 0..NP-1)(i);
		// == N*((NP*(NP-1))/2)
		final val sum: int = x.sum();
		chk(sum == (N*NP*(NP-1)/2));


		// also verify each array elem x[i] == N*i;
		// test D|R restricton and also D-D1

		final val r_inner: region{rank==1} = [1..NP-2];
		final val D_inner: dist{rank==1} = D|r_inner;
		final val D_boundary: dist{rank==1} = D-r_inner;
		finish
			ateach (val pi: point[i] in D_inner) {
				chk(x(pi) == N*i);
				chk(x(i) == N*i);
				chk(D(pi) == D_inner(pi) && D(pi) == here);
			}

		finish
			ateach (val pi: point[i] in D_boundary) {
				chk(x(pi) == N*i);
				chk(D(pi) == D_boundary(pi) && D(pi) == here);
			}

		// test scan
		final val y: Array[int]{distribution==D} = (Array[int]{distribution==D}) x.scan(intArray.add, 0);
		// y[i] == x[i]+y[i-1], for i>0
		finish
			ateach (val pi: point[i] in D) {
				final val pi1: point = [i-1];
				chk(y(pi) == x(pi) + (i == 0 ? 0 : future(D(pi1)){y(pi1)}.force()));
				chk(y(i) == x(i) + (i == 0 ? 0 : future(D(i-1)){y(i-1)}.force()));
			}
		// y[NP-1] == SUM(x[0..NP-1])
		final val pNP_1: point = [NP-1];
		chk(sum == future(D(pNP_1)){y(pNP_1)}.force());
		chk(sum == future(D(NP-1)){y(NP-1)}.force());

		// test lift
		final val z: Array[int]{distribution==D} = (Array[int]{distribution==D}) x.lift(intArray.add, y);

		finish
			ateach (val pi: point in D) chk(z(pi) == x(pi) + y(pi));

		// now write back zeros to x
		x.update(new Array[int](D));

		// ensure x is all zeros
		chk(x.sum() == 0);

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new MiscTest1().execute();
	}
}
