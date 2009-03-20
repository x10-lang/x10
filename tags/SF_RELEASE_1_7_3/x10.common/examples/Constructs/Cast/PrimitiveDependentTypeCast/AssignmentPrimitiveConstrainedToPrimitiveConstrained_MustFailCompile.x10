/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Cast's dependent type constraint must be satisfied by the primitive.
 * Issue: Value to cast does not meet constraint requirement of target type.
 * @author vcave
 **/
public class AssignmentPrimitiveConstrainedToPrimitiveConstrained_MustFailCompile extends x10Test {

	public boolean run() {
		
		try { 
			int (: self == 1) i = 1
			int (: self == 0) j = i;
		}catch(Throwable e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new AssignmentPrimitiveConstrainedToPrimitiveConstrained_MustFailCompile().execute();
	}

}
 