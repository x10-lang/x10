/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for for loop on an array.
 *
 * @author vj
 */
public class ForLoopOnArray extends x10Test {

	const int N = 3;

	public boolean run() {
		double[.] a = new double[[0:10]] (point [i]) { return i; };

		for (point [i]: a) {
			if (a[i] != i) return false;
		}
		return true;
	}

	public static void main(String[] args) {
		new ForLoopOnArray().execute();
	}
}

