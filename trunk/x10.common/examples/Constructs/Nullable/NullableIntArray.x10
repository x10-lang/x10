/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a nullable int[] can be recognized by the compiler.
 *
 * @author igor Added 02/22/2006
 */
public class NullableIntArray extends x10Test {

	public boolean run() {
		final nullable<int[]> A = { 3, 2, 1 };
		int v = (A[1] == 2) ? 1 : 0;
		return v == 1;
	}

	public static void main(String[] args) {
		new NullableIntArray().execute();
	}
}

