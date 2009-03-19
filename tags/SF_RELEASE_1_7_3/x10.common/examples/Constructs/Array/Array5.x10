/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing int[] method parameters and fields.
 */
public class Array5 extends x10Test {
	int[] ia;

	public Array5() {}

	public Array5(int[] ia) {
		this.ia = ia;
	}

	private boolean runtest() {
		ia[0] = 42;
		return 42 == ia[0];
	}

	public boolean run() {
		int[] temp = new int[1];
		temp[0] = 43;
		return (new Array5(temp)).runtest();
	}

	public static void main(String[] args) {
		new Array5().execute();
	}
}

