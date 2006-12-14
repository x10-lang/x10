/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for distribution restriction.
 */
public class Restrict extends x10Test {

	public boolean run() {
		region r = region.factory.region(0, 100); //(low, high)
		final region R = region.factory.region(new region[] { r, r } );
		dist d = dist.factory.constant(R, here);
		region R2 = (d | here).region;
		return (R.size() == R2.size());
	}

	public static void main(String[] args) {
		new Restrict().execute();
	}
}

