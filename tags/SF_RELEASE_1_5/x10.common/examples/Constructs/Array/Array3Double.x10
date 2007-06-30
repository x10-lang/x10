/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Ensures double arrays are implemented.
 */
public class Array3Double extends x10Test {

	public boolean run() {
		double[.] ia = new double[[1:10,1:10]->here];
		ia[1,1] = 42.0D;
		return 42.0D == ia[1,1];
	}

	public static void main(String[] args) {
		new Array3Double().execute();
	}
}

