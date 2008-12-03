/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that nullable int can be recognized by the compiler.
 * @author vj Added 6/4/2005
 */
public class NullableInt extends x10Test {

	public boolean run() {
		final int[] A = { 3, 2, 1 };
		nullable<int> v = (A[1] == 2) ? null : 0;
		return v == null;
	}

	public static void main(String[] args) {
		new NullableInt().execute();
	}
}

