/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A future test.
 */
public class Future4 extends x10Test {
	public boolean run() {
		future<int> x = future { 46 };
		return (x.force()+1) == 47;
	}

	public static void main(String[] args) {
		new Future4().execute();
	}
}

