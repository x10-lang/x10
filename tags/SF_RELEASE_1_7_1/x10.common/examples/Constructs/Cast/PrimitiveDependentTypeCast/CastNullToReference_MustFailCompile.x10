/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks null litteral can't be cast to a non-nullable type.
 * Issue: null is not an instanceof x10.lang.Object, but would be one of nullable<x10.lang.Object>
 * @author vcave
 **/
 public class CastNullToReference_MustFailCompile extends x10Test {

	public boolean run() {
		x10.lang.Object obj = (x10.lang.Object) null;
		return false;
	}

	public static void main(String[] args) {
		new CastNullToReference_MustFailCompile().execute();
	}
}