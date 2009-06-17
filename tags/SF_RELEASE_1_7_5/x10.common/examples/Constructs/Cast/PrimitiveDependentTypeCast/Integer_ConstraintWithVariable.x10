/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks constraint's variable are resolved.
 * @author vcave
 **/
public class Integer_ConstraintWithVariable extends x10Test {

	public boolean run() {
		final int(:self==0) iconstraint = 0;
		// constraint's variable must be final
		// hence these two types should be equivalent
		int (:self == iconstraint) i1 =  0;
		int(:self==iconstraint) i2 = (int(:self==iconstraint)) 0;
		return true;
	}

	public static void main(String[] args) {
		new Integer_ConstraintWithVariable().execute();
	}


}
 