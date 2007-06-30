/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Ensures long arrays are implemented.
 */
public class Array3Long extends x10Test {

	public boolean run() {
		long[.] ia = new long[[1:10,1:10]->here];
		ia[1,1] = 42L;
		return 42L == ia[1,1];
	}

	public static void main(String[] args) {
		new Array3Long().execute();
	}
}

