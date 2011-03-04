/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checking constraints.
 * Note: Expression (j+1) does not produce any side effects however inlining is not used.
 * Note: It is a possible optimization (depending expression size) to detect 
 *       there is no side effects and inline the checking.
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_4 extends x10Test {

	public boolean run() {
		int j = -1;
		int (: self == 0) i = 0;
		i = (int (: self == 0)) (j+1);
		return i == 0;
	}

	public static void main(String[] args) {
		new NumericExpressionToPrimitiveDepType_4().execute();
	}

}
 