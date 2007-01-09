/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checkink for constraint
 * Note: The cast should not be inlined to avoid several execution of j+=1
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_3 extends x10Test {

	public boolean run() {
		int j = -1;
		int (: self == 0) i = 0;
		i = (int (: self == 0)) (j+=1);
		return ((j==0) && (i==0));
	}

	public static void main(String[] args) {
		new NumericExpressionToPrimitiveDepType_3().execute();
	}

}
 