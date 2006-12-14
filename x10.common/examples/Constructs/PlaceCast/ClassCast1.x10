/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks whether a cast of null to a non-nullable type
 * throws a ClassCastException.
 * @author vj 6/4/2005
 */
public value class ClassCast1 extends x10Test {

	public boolean run() {
		try {
			final nullable<ClassCast1>[] A = { null, new ClassCast1() };
			nullable<ClassCast1> val = (A[0] == null) ? null : A[1];
			return new ClassCast1() == (ClassCast1) val; // should throw a ClassCastException
		} catch (ClassCastException e) { // Per Sec 11.4.1, v 0.409 of the manual
			return true;
		}
	}

	public static void main(String[] args) {
		new ClassCast1().execute();
	}
}

