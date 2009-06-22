/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks unboxing detects fails if object does not represent a boxed primitive.
 *          (i.e. from x10.compilergenerated.Boxed'Type')
 * Issue: obj is not a boxed primitive which throws a class cast exception.
 * @author vcave
 **/
 public class CastBoxedToPrimitive2 extends x10Test {

	public boolean run() {
		try {
			x10.lang.Object obj = new CastBoxedToPrimitive2();
			int i = (int) obj;
		} catch (ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new CastBoxedToPrimitive2().execute();
	}
}