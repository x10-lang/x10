/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test to check that unsafe is being parsed correctly.
 */
public class Unsafe extends x10Test {

	public boolean run() {
		region e = region.factory.region(1, 10); //(low, high)
		region r = region.factory.region(new region[] { e, e, e, e } );
		dist d = dist.factory.constant(r, here);

		int [.] x = new int [d]; // ok
		int [.] y = new int [d]; //ok
		int value [.] y1 = new int value [d]; // ok
		int [.] zz = new int[d] (point p) { return 41; }; //bad
		return true;
	}

	public static void main(String[] args) {
		new Unsafe().execute();
	}
}

