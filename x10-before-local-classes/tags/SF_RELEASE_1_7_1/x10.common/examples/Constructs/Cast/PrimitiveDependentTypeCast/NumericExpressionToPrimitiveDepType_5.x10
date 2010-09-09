/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of ++j
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_5 extends x10Test {

	public boolean run() {
		int j = -1;
		int (: self == 0) i = 0;
		i = (int (: self == 0)) (++j);
		return ((j == 0) && (i==0));
	}

	public static void main(String[] args) {
		new NumericExpressionToPrimitiveDepType_5().execute();
	}

}
 