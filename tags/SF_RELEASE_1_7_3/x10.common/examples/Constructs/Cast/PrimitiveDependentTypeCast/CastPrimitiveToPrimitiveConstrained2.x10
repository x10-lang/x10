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
public class CastPrimitiveToPrimitiveConstrained2 extends x10Test {

	public boolean run() {
	
		int (: self == 0) i = 0;
		int j = 0;
		i = (int (: self == 0)) j;

		return true;
	}

	public static void main(String[] args) {
		new CastPrimitiveToPrimitiveConstrained2().execute();
	}

}
 