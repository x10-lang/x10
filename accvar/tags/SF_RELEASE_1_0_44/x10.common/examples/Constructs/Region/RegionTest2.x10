/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for the empty region.
 */
public class RegionTest2 extends x10Test {

	public boolean run() {
		region reg = region.factory.region(0, -1); // [0..-1]

		int sum = 0;
		for (point p: reg) sum++;
		return sum == 0;
	}

	public static void main(String[] args) {
		new RegionTest2().execute();
	}
}

