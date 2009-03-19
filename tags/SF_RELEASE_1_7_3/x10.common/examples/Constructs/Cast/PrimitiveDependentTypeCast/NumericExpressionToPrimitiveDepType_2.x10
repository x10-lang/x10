/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checking for constraint.
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_2 extends x10Test {

	public boolean run() {
		int j = -1;
		int (: self == 0) i = 0;
		try {
			// j is incremented after the test is done;
			i = (int (: self == 0)) j++;
		} catch (ClassCastException e) {
			return (j==0) && (i==0);
		}

		return false;
	}

	public static void main(String[] args) {
		new NumericExpressionToPrimitiveDepType_2().execute();
	}

}
 