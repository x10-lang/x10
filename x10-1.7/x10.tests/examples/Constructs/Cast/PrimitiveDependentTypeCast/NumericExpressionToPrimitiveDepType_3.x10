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

	public def run(): boolean = {
		var j: int = -1;
		var i: int{self == 0} = 0;
		i = (j+=1) as int{self == 0};
		return ((j==0) && (i==0));
	}

	public static def main(var args: Rail[String]): void = {
		new NumericExpressionToPrimitiveDepType_3().execute();
	}

}
