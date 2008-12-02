/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks regular java cast works.
 * @author vcave
 **/
public class CastLitteralToPrimitive extends x10Test {

	public boolean run() {
		boolean boolt = (boolean) true;
		boolean boolf = (boolean) false;
		byte b = (byte) 1;			
		int i = (int) 1;
		short s = (short) 1;
		long l = (long) 1;
		double d = (double) 1.0;
		float f = (float) 1.0;
		return true;
	}

	public static void main(String[] args) {
		new CastLitteralToPrimitive().execute();
	}
}
 