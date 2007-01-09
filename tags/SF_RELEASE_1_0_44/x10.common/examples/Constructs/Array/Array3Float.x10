/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Ensures float arrays are implemented.
 */
public class Array3Float extends x10Test {

	public boolean run() {
		float[.] ia = new float[[1:10,1:10]->here];
		ia[1,1] = 42.0F;
		return (42.0F == ia[1,1]);
	}

	public static void main(String[] args) {
		new Array3Float().execute();
	}
}

