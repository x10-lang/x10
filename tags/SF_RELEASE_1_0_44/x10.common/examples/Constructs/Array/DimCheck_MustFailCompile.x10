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
 * Dimensionality of array initializer must be checked.
 *
 * @author vj 12 2006
 */
public class DimCheck_MustFailCompile extends x10Test {

	public boolean run() {
		// The compiler should check the type of the distribution in the array constructor.
		// If  the type does not specify a constraint on the arity of the underlying region
		// then the initializer cannot specify the arity of the point. Otherwise the arity of the
		// point must be the same as the arity of the distribution.
		int [.] a1 = new int[[0:2,0:3]->here](point p[i]){ return i; };
		System.out.println(a1);
		return true;
	}

	public static void main(String[] args) {
		new DimCheck_MustFailCompile().execute();
	}
}

