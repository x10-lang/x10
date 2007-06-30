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
 * @author vcave
 **/
public class CastPrimitiveConstrainedToPrimitiveConstrained extends x10Test {

	public boolean run() {
		
		try { 
			int (: self == 0) i = 0;
			int (: self == 0) j = 0;
			i =  (int (: self == 0)) j;
		}catch(Throwable e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new CastPrimitiveConstrainedToPrimitiveConstrained().execute();
	}

}
 