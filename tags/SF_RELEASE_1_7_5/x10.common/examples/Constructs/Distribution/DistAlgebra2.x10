/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Given two horizontal strip regions R1 and R2 and a vertical strip
 * region R3 going over R1 and R2 in an 8*8 grid, this test case tests
 * algebraic operations on regions and dists.
 *
 * <code>
          01234567
        0 ++++++++
        1 ++++++++
        2
        3
        4 ********
        5 ********
        6
        7

          01234567
        0     $$
        1     $$
        2     $$
        3     $$
        4     $$
        5     $$
        6     $$
        7     $$

        + region R1
        * region R2
        $ region R3

 * </code>
 *
 * @author kemal 4/2005
 */
public class DistAlgebra2 extends x10Test {

	public boolean run() {
		final dist P = dist.factory.unique();
		final region(:rank==2) R1 = [0:1,0:7]; // horizontal strip
		pr("R1", R1);
		final region(:rank==2) R2 = [4:5,0:7]; // horizontal strip
		pr("R2", R2);
		final region(:rank==2) R3 = [0:7,4:5]; // vertical strip
		pr("R3", R3);
		final region(:rank==2) R1orR2 = (R1 || R2);
		pr("R1orR2", R1orR2);
		final region(:rank==2) R1orR2andR3 = R1orR2 && R3;
		pr("R1orR2andR3", R1orR2andR3);
		chk(R1orR2andR3.equals([0:1,4:5] || [4:5,4:5]));
		chk(R1orR2.contains(R1orR2andR3) && R3.contains(R1orR2andR3));
		final region(:rank==2) R1orR2orR3 = R1 || R2 || R3;
		pr("R1orR2orR3", R1orR2orR3);
		chk(R1orR2orR3.equals([0:1,0:7] || [4:5,0:7] ||
					[2:3,4:5] || [6:7,4:5]));
		chk(R1orR2orR3.contains(R1) &&  R1orR2orR3.contains(R2) &&
				R1orR2orR3.contains(R3));
		final region(:rank==2) R1orR2minusR3 = R1orR2 - R3;
		pr("R1orR2minusR3", R1orR2minusR3);
		chk(R1orR2minusR3.equals([0:1,0:3] || [0:1,6:7] ||
					[4:5,0:3] || [4:5,6:7]));
		chk(R1orR2.contains(R1orR2minusR3) && R1orR2minusR3.disjoint(R3));

		//Constant dist of R1||R2||R3
		final dist(:rank==2) DR1orR2orR3 = (dist(:rank==2)) dist.factory.constant(R1orR2orR3, P[3]);
		pr("DR1orR2orR3", DR1orR2orR3);
		for (point [i,j]: DR1orR2orR3) {
			chk(DR1orR2orR3[i,j] == P[3]);
		}

		//Check range restriction to a place
		final int np = place.MAX_PLACES;
		for (point [k]: [0:np-1]) {
			final dist DR1orR2orR3Here = (DR1orR2orR3 | P[k]);
			pr("DR1orR2orR3Here("+k+")", DR1orR2orR3Here);
			for (point [i,j]: DR1orR2orR3) {
				chk(iff(DR1orR2orR3[i,j] == P[k],
							DR1orR2orR3Here.contains([i,j]) &&
							DR1orR2orR3Here[i,j] == P[k]));
			}
		}

		//DR1orR2andR3 is restriction of DR1orR2orR3 to (R1||R2)&&R3
		final dist(:rank==2) DR1orR2andR3 = DR1orR2orR3 | R1orR2andR3;
		pr("DR1orR2andR3", DR1orR2andR3);
		//DR1orR2minusR3 is restr. of DR1orR2orR3 to (R1||R2)-R3
		final dist(:rank==2) DR1orR2minusR3 = DR1orR2orR3 | R1orR2minusR3;
		pr("DR1orR2minusR3", DR1orR2minusR3);
		final dist(:rank==2) TD1 = DR1orR2orR3 - DR1orR2minusR3;
		pr("TD1", TD1);
		final dist(:rank==2) DR3 = DR1orR2orR3 | R3;
		pr("DR3", DR3);
		chk(TD1.equals(DR3));

		//intersection with common mapping
		//on common points
		final dist(:rank==2) TD2 = (DR1orR2minusR3 && DR1orR2orR3);
		pr("TD2", TD2);
		chk(TD2.equals(DR1orR2minusR3));

		// testing overlay with common mapping on common points
		final dist DR1orR2 = DR1orR2orR3 | R1orR2;
		pr("DR1orR2", DR1orR2);
		final dist TD3 = (DR1orR2.overlay(DR3));
		pr("TD3", TD3);
		chk(TD3.equals(DR1orR2orR3));

		//disjoint union
		final dist TD4 = DR1orR2andR3 || DR1orR2minusR3;
		pr("TD4", TD4);
		chk(TD4.equals(DR1orR2));

		// overlay with common points not
		// necessarily mapping to same place
		final dist TD9 =
			dist.factory.constant(R1orR2andR3, P[0]);
		pr("TD9", TD9);
		final dist(:rank==2) Doverlay = (dist(:rank==2)) DR1orR2orR3.overlay(TD9);
		pr("Doverlay", Doverlay);
		for (point [i,j]: Doverlay) {
			if (R1orR2andR3.contains([i,j])) {
				chk(Doverlay[i,j] == P[0] && TD9[i,j] == P[0]);
			} else {
				chk(Doverlay[i,j] == DR1orR2orR3[i,j]);
			}
		}

		// intersection with common points
		// not necessarily mapping to same place

		// if a point is common and maps to same place
		// in both dists,
		// the point is included in the intersection
		// with the same mapping.
		// Otherwise, the point is not included in
		// intersection.

		final dist(:rank==2) Dintersect = DR1orR2orR3&&Doverlay;
		pr("Dintersect", Dintersect);
		for (point [i,j]: [0:7,0:7]) {
			chk(
					(DR1orR2orR3.contains([i,j]) &&
					 Doverlay.contains([i,j]) &&
					 DR1orR2orR3[i,j] == Doverlay[i,j]) ?

					(Dintersect.contains([i,j]) &&
					 Dintersect[i,j] == Doverlay[i,j]) :

					!Dintersect.contains([i,j])
			   );
		}

		return true;
	}

	static boolean iff(boolean x, boolean y) {
		return (x == y);
	}

	static void pr(String s, dist d) {
		System.out.println();
		System.out.println("printing dist "+s);
		int k = 0;
		final int N = 8;
		for (point [i,j]: [0:N-1,0:N-1]) {
			System.out.print(" "+(d.contains([i,j]) ? (""+d[i,j].id) : "."));
			if ((++k) % N == 0) System.out.println();
		}
	}

	static void pr(String s, region r) {
		System.out.println();
		System.out.println("printing region "+s);
		int k = 0;
		final int N = 8;
		for (point [i,j]: [0:N-1,0:N-1]) {
			System.out.print(" "+(r.contains([i,j]) ? "+" : "."));
			if ((++k) % N == 0) System.out.println();
		}
	}

	public static void main(String[] args) {
		new DistAlgebra2().execute();
	}
}

