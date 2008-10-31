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
public class Future5 extends x10Test {
	public boolean run() {
		future<int> x = future { 41 };
		int val = x.force();
		return x.forced();
	}

	public static void main(String[] args) {
		new Future5().execute();
	}
}

