/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several
 *          time while checking for constraint.
 * Note : Tricky test case where 'this.incr().j' is of type field,
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_1 extends x10Test {
	public int j = -1;
	
	public boolean run() {

		int (: self == 0) i = 0;
		i = (int (: self == 0)) incr().j;

		return j == 0;
	}
	
	private NumericExpressionToPrimitiveDepType_1 incr() {
		j++;
		return this;
	}

	public static void main(String[] args) {
		new NumericExpressionToPrimitiveDepType_1().execute();
	}

}
 