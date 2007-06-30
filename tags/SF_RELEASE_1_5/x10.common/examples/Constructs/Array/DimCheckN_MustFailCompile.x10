/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This check is not being done by the compiler currently.
import harness.x10Test;

/**
 * This must compile and run fine. Checks that the initializer may not specify
 * the arity of the region.
 *
 * @author vj 12 2006
 */
public class DimCheckN_MustFailCompile extends x10Test {

	public boolean run() {
		final dist(:rank==2) d = [0:2,0:3]->here;
		// Statically d has rank 2, but we initialize the array with 1d points. Bad, bad!
		int [.] a1 = new int[d](point p[i]){ return i; };
		return true;
	}

	public static void main(String[] args) {
		new DimCheckN_MustFailCompile().execute();
	}
}

