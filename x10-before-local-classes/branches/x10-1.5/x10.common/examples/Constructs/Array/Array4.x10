/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for X10 arrays -- tests arrays passed as parameters and stored in fields.
 */
public class Array4 extends x10Test {
	int[.] ia;

	public Array4() {}

	public Array4(int[.] ia) {
		this.ia = ia;
	}

	private boolean runtest() {
		ia[1,1] = 42;
		return 42 == ia[1,1];
	}

	/**
	 * Run method for the array. Returns true iff the test succeeds.
	 */
	public boolean run() {
		return (new Array4(new int[[1:10,1:10]->here])).runtest();
	}

	public static void main(String[] args) {
		new Array4().execute();
	}
}

