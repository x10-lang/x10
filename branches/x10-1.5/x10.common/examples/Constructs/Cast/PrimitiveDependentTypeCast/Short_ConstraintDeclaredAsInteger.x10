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
 * Note: Contraint value is stored as an integer
 * @author vcave
 **/
public class Short_ConstraintDeclaredAsInteger extends x10Test {

	public boolean run() {
		short j = -1;
		short (:self == 0) i = 0;
		i = (short (:self == 0)) (++j);
		return ((j==0) && (i==0));
	}

	public static void main(String[] args) {
		new Short_ConstraintDeclaredAsInteger().execute();
	}

}
 