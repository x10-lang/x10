/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class MissingReturn_MustFailCompile extends x10Test {

	boolean foo() {
	}

	public boolean run() {
		return true;
	}

	public static void main(String args[]) {
		new MissingReturn_MustFailCompile().execute();
	}
}

