/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Ensures char arrays are implemented.
 */
public class Array3Char extends x10Test {

	public boolean run() {
		char[.] ia = new char[[1:10,1:10]->here];
		ia[1,1] = 'a';
		return ('a' == ia[1,1]);
	}

	public static void main(String[] args) {
		new Array3Char().execute();
	}
}

