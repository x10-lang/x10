/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks 3 is an object which can be compared to a nullable
 *
 * @author vcave
 */
public class NullableObjectEqualsPrimitive extends x10Test {

	public boolean run() {
		nullable<x10.lang.Object> x = null;
		boolean res = 3==x;
		res &= x==3;
		return !res;
	}

	public static void main(String[] args) {
		new NullableObjectEqualsPrimitive().execute();
	}
}