/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test the syntax for creating an array of arrays.
 *
 * @author igor, 12/2005
 */
public class ArrayOfArraysShorthand extends x10Test {

	public boolean run() {
		final region r1 = [0:7];
		final region r2 = [0:9];
		final region r = [r1, r2];
		final int value [.][.] ia = new int value [r1][r2];
		for (point [i,j]: r) chk(ia[i][j] == null);
		return true;
	}

	public static void main(String[] args) {
		new ArrayOfArraysShorthand().execute();
	}
}

