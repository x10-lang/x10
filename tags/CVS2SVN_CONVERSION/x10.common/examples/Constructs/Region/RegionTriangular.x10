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
 *Tests upper and lower triangular, and banded regions.
 *
 * @author kemal 4/2005
 */
public class RegionTriangular extends x10Test {

	public boolean run() {
		final region Universe = [0:7,0:7];
		region upperT = region.factory.upperTriangular(8);
		pr("upperT", upperT);
		for (point [i,j]: Universe)
			chk(iff(i <= j, upperT.contains([i,j])));
		region lowerT = region.factory.lowerTriangular(8);
		pr("lowerT", lowerT);
		for (point [i,j]: Universe)
			chk(iff(i >= j, lowerT.contains([i,j])));
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
		new RegionTriangular().execute();
	}
}

