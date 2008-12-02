/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for dists.
 */
public class DistributionTest1 extends x10Test {

	public boolean run() {
		region r = region.factory.region(0, 100); //(low, high)
		final region R = region.factory.region(new region[] { r, r } );
		final dist D = dist.factory.constant(R, here);
		return ((D[0,0] == here) &&
				(D.rank == 2) &&
				(R.rank == 2) &&
				(R.rank(1).high() - R.rank(1).low() + 1 == 101));
	}

	public static void main(String[] args) {
		new DistributionTest1().execute();
	}
}

