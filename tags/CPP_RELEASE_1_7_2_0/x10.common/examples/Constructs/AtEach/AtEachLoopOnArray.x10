/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for an ateach loop on an array.
 *
 * @author vj
 */
public class AtEachLoopOnArray extends x10Test {
	boolean success = true;

	public boolean run() {
		final double[.] A = new double[[0:10]->here] (point [i]) { return i; };

		finish ateach (point [i]: A)
			if (A[i] != i)
				async (this) atomic { success = false; }

		return success;
	}

	public static void main(String[] args) {
		new AtEachLoopOnArray().execute();
	}
}

