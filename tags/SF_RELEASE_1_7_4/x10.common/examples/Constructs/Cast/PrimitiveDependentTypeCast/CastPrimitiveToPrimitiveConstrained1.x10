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
public class CastPrimitiveToPrimitiveConstrained1 extends x10Test {

	public boolean run() {
		
		try { 
			int (: self == 0) i = 0;
			int j = 1;
			i = (int (: self == 0)) j;
		}catch(ClassCastException e) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		new CastPrimitiveToPrimitiveConstrained1().execute();
	}

}
 