/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Expression parentheses test.
 */
public class ExpressionParentheses extends x10Test {

	int x = java.lang.Integer.MIN_VALUE;
	int n = 16;
	int z;

	public boolean run() {
		z = ((x ^ (x>>>8) ^ (x>>>31)) & (n-1));
		if (z != 1) return false;
		z = ((x | (n-1)) & 1);
		if (z != 1) return false;
		return true;
	}

	public static void main(String[] args) {
		new ExpressionParentheses().execute();
	}
}

