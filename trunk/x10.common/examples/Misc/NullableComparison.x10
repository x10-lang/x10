/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Should report the type mismatch in the comparison in the loop, and fail
 * to compile gracefully.
 *
 * @author Bin Xin (xinb@cs.purdue.edu)
 */
public class NullableComparison extends x10Test {

	public static final int N = 6;
	public static final nullable<x10.lang.Object>[] objList = new x10.lang.Object[N];

	public boolean run() {
		final x10.lang.Object obj = new x10.lang.Object();
		int i = N - 1;
		while (i > 0 && (obj != objList[i])) {
			i--;
		}
		if (i > 0)
			return false;
		return true;
	}

	public static void main(String[] args) {
		new NullableComparison().execute();
	}
}

