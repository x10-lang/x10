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
 * Note: Append an 'L' force constraint representation to be a long.
 * @author vcave
 **/
public class Long_ConstraintDeclaredAsLong extends x10Test {

	public def run(): boolean = {
		var j: long = 2147493646L;
		// the constraint is represented as a long
		var i: long{self == 2147493647L} = 2147493647L;
		i = (++j) as (long{self == 2147493647L});
		return ((j == 2147493647L) && (i==2147493647L));
	}

	public static def main(var args: Rail[String]): void = {
		new Long_ConstraintDeclaredAsLong().execute();
	}

}
