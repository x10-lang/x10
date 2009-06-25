/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of ++j
 * Note: Contraint value is stored as an integer
 * @author vcave
 **/
public class Short_ConstraintDeclaredAsInteger extends x10Test {

	public def run(): boolean = {
		var j: short = -1;
		var i: short{self == 0} = 0;
		i = (++j) as short{self == 0};
		return ((j==0) && (i==0));
	}

	public static def main(var args: Rail[String]): void = {
		new Short_ConstraintDeclaredAsInteger().execute();
	}

}
