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

	 public boolean run() {
		// Constant is inferior to integer max value
		long j = 33;
		// The constraint is represented as an integer at runtime.
		long (: self == 34) i = 34;
		i = (long (: self == 34)) (++j);
		return ((j == 34) && (i==34));
	}

	public static void main(String[] args) {
		new  Long_ConstraintDeclaredAsInteger().execute();
	}

}
 