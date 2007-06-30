/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Simple test for operator assignment of array elements.
 * Tests post and pre increment/decrement;
 */
public class ArrayOpAssign2 extends x10Test {
	int i = 1;
	int j = 1;
	public boolean run() {
		int[.] ia = new int[[1:10,1:10]];
		ia[i,j] = 1;
		chk(ia[i,j] == 1);
		chk((ia[i,j]++) == 1);
		chk(ia[i,j] == 2);
		chk((ia[i,j]--) == 2);
		chk(ia[i,j] == 1);
		chk((++ia[i,j]) == 2);
		chk(ia[i,j] == 2);
		chk((--ia[i,j]) == 1);
		chk(ia[i,j] == 1);
		return true;
	}

	public static void main(String[] args) {
		new ArrayOpAssign2().execute();
	}
}

