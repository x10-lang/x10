/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing a variable scope problem.
 * The second q's scope does not overlap with the first q.
 */
public class VariableScope extends x10Test {

	public boolean run() {

		final int N = 10;
		region e = region.factory.region(1, N); //(low, high)
		region r = region.factory.region(new region[] { e, e });
		dist d = dist.factory.constant(r, here);
		int n = 0;

		for (point p: e)
			for (point q: e) {
				n++;
			}

		for (point p: d) {
			nullable<point> q = null;
			n++;
		}

		return n == 2 * N * N;
	}

	public static void main(String[] args) {
		new VariableScope().execute();
	}
}

