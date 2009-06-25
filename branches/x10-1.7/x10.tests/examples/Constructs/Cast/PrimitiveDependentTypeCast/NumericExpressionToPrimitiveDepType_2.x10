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

	public def run(): boolean = {
		var j: int = -1;
		var i: int{self == 0} = 0;
		try {
			// j is incremented after the test is done;
			i = (j++) as int{self == 0};
		} catch (e: ClassCastException) {
			return (j==0) && (i==0);
		}

		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new NumericExpressionToPrimitiveDepType_2().execute();
	}

}
