/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests declaration of arrays, storing in local variables, accessing and
 * updating for 2D arrays.
 */
public class Array3 extends x10Test {

	public boolean run() {
		int[.] ia = new int[[1:10,1:10]->here];
		ia[1,1] = 42;
		return 42 == ia[1,1];
	}

	public static void main(String[] args) {
		new Array3().execute();
	}
}

