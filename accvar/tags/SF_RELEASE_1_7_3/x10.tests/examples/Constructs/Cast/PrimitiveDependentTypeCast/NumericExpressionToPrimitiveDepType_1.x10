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
	public var j: int = -1;
	
	public def run(): boolean = {

		var i: int{self == 0} = 0;
		i = incr().j as int{self == 0};

		return j == 0;
	}
	
	private def incr(): NumericExpressionToPrimitiveDepType_1 = {
		j++;
		return this;
	}

	public static def main(var args: Rail[String]): void = {
		new NumericExpressionToPrimitiveDepType_1().execute();
	}

}
