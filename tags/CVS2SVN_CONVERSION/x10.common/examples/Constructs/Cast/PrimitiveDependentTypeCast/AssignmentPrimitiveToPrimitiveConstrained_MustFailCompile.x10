/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Check primitive variable assignment to primitive dependent type.
 * Issue: Variable j is statically known as an int. Conversion from int to int(:c) is forbidden.
 * @author vcave
 **/
public class AssignmentPrimitiveToPrimitiveConstrained_MustFailCompile extends x10Test {

	public boolean run() {
		
		try { 
			int (: self == 0) i = 0;
			int j = 0;
			// Even if j equals zero, types are not compatible
			// A cast would be necessary to check conversion validity
			i = j;
		}catch(Throwable e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new AssignmentPrimitiveToPrimitiveConstrained_MustFailCompile().execute();
	}

}
 