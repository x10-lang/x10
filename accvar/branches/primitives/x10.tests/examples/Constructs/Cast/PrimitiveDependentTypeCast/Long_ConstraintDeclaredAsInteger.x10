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
public class Long_ConstraintDeclaredAsInteger extends x10Test {

	 public def run(): boolean = {
		// Constant is inferior to integer max value
		var j: long = 33;
		// The constraint is represented as an integer at runtime.
		var i: long{self == 34} = 34;
		i = (++j) as long{self == 34};
		return ((j == 34) && (i==34));
	}

	public static def main(var args: Rail[String]): void = {
		new  Long_ConstraintDeclaredAsInteger().execute();
	}

}
