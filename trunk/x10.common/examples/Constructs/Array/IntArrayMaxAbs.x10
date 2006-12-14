/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing the maxAbs function on arrays.
 */
public class IntArrayMaxAbs extends x10Test {

	public boolean run() {
		final dist D = [1:10,1:10]->here;
		final int[.] ia = new int[D];

		finish ateach (point p[i,j]: D) { ia[p] = -i; }

		return ia.maxAbs() == 10;
	}

	public static void main(String[] args) {
		new IntArrayMaxAbs().execute();
	}
}

