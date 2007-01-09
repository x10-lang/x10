/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Ensures byte arrays are implemented.
 */
public class Array3Byte extends x10Test {

	public boolean run() {
		byte[.] ia = new byte[[1:10,1:10]->here];
		ia[1,1] = (byte) 42;
		return (42 == ia[1,1]);
	}

	public static void main(String[] args) {
		new Array3Byte().execute();
	}
}

