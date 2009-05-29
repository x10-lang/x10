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

	var x: int = Int.MIN_VALUE;
	var n: int = 16;
	var z: int;

	public def run(): boolean = {
		z = ((x ^ (x>>>8) ^ (x>>>31)) & (n-1));
		if (z != 1) return false;
		z = ((x | (n-1)) & 1);
		if (z != 1) return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ExpressionParentheses().execute();
	}
}
