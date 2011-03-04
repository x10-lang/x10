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
 * Note: The cast should not be inlined to avoid several execution of j*=2
 * Note: Compiler stores constants in float
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_8 extends x10Test {

	public boolean run() {
		double j = 0.01;
		double (:self == 0.02) i = 0.02;
		i = (double (:self==0.02)) (j *= 2);
		return ((j==0.02) && (i==0.02));
	}

	public static void main(String[] args) {
		new NumericExpressionToPrimitiveDepType_8().execute();
	}

}
 