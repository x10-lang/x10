/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks primitive type assignment of java.
 * @author vcave
 **/
public class PrimitiveAssignment extends x10Test {
	public boolean run() {
		byte b = 2;
		char c = 'c';
		short s = 10;
		int j = 124;
		long l = 1;
		float f = 0;
		double d = 0.001;

		return true;
	}

	public static void main(String[] args) {
		new PrimitiveAssignment().execute();
	}
}
 

 