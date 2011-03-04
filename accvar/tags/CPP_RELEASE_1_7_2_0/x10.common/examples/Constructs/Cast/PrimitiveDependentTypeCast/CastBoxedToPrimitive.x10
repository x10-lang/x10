/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks boxing/unboxing works properly.
 * @author vcave
 **/
 public class CastBoxedToPrimitive extends x10Test {

	public boolean run() {
		// boxed to new BoxedInteger(3)
		x10.lang.Object obj = 3;
		// unboxed as : ((BoxedInteger) obj).intValue()
		int i = (int) obj;
		return true;
	}

	public static void main(String[] args) {
		new CastBoxedToPrimitive().execute();
	}
}