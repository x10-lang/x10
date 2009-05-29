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
 * Issue: assignment is illegal as constraint is not meet.
 * @author vcave
 **/
public class AssignmentLitteralPrimitiveToPrimitiveConstrained_MustFailCompile extends x10Test {

	public boolean run() {
		
		try { 
			int (: self == 0) i = 0;
			i = 1;
		}catch(Throwable e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new AssignmentLitteralPrimitiveToPrimitiveConstrained_MustFailCompile().execute();
	}

}
 