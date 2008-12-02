/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for regions.
 */
public class RegionTest1 extends x10Test {

	public boolean run() {
		region r = region.factory.region(0, 100); // (low, high)
		region reg = region.factory.region(r, r);

		int sum = 0;
		for (point p: reg) sum += p.get(0) - p.get(1);

		return sum == 0;
	}

	public static void main(String[] args) {
		new RegionTest1().execute();
	}
}

