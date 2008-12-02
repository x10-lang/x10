/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Future test.
 */
public class Future0 extends x10Test {
	public boolean run() {
		return ((future { 47 } ).force()) == 47;
	}

	public static void main(String[] args) {
		new Future0().execute();
	}
}

