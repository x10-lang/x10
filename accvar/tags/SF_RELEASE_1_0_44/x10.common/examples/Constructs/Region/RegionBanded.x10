/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
import harness.x10Test;

/**
 * Tests upper and lower triangular, and banded regions.
 *
 * @author kemal 4/2005
 */
public class RegionBanded extends x10Test {

	public boolean run() {
		final region Universe = [0:7,0:7];
		region banded1 = region.factory.banded(8, 1);
		pr("banded1", banded1);
		for (point [i,j]: Universe)
			chk(iff(i == j, banded1.contains([i,j])));

		// region banded2 = region.factory.banded(8, 2);
		// pr("banded2", banded2);
		// not sure if 2nd band is to north or south of diagonal
		// for (point [i,j]: Universe)
		// chk(iff(j == i || j == i+1, banded2.contains([i,j])));

		region banded3 = region.factory.banded(8, 3);
		pr("banded3", banded3);
		for (point [i,j]: Universe)
			chk(iff(j == i-1 || j == i || j == i+1, banded3.contains([i,j])));

		// region banded4 = region.factory.banded(8, 4);
		// pr("banded4", banded4);
		// for (point [i,j]: Universe)
		// chk(iff((j == i-1 || j == i || j == i+1 || j == i+2),
		// banded4.contains([i,j])));

		return true;
	}

	static boolean iff(boolean x, boolean y) {
		return (x == y);
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
		new RegionBanded().execute();
	}
}

