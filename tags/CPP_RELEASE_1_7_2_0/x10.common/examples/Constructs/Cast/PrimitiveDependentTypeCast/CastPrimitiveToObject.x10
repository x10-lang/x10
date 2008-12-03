/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks explicit boxing from a litteral to Object is working.
 * @author vcave
 **/
 public class CastPrimitiveToObject extends x10Test {

	public boolean run() {
		x10.lang.Object obj = ((x10.lang.Object) 3);
		return true;
	}

	public static void main(String[] args) {
		new CastPrimitiveToObject().execute();
	}
}