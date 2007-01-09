/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a nullable int[.] can be recognized by the compiler, and the variable can be accessed
 * via indexing to get at its elements.
 *
 * @author vj 
 */
public class NullableArray3 extends x10Test {

	public boolean run() {
		final nullable<int[.]> A = new int[[1:10,1:10]] (point [i,j]) { return i+j;};
		int v = (A[1,1] == 2) ? 1 : 0;
		return v == 1;
	}

	public static void main(String[] args) {
		new NullableArray3().execute();
	}
}

