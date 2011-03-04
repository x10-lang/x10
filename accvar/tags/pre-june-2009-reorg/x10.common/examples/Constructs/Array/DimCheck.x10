/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * This must compile and run fine. Checks that the initializer may not specify
 * the arity of the region.

 * @author vj 12 2006
 */

public class DimCheck extends x10Test {

	public boolean run() {
		int [.] a1 = new int[[0:2,0:3]->here](point p){ return p[0]; };
		return true;
	}

	public static void main(String[] args) {
		new DimCheck().execute();
	}
}

