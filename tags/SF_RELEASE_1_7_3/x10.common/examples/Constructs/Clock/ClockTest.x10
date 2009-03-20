/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for clock.  Does not do anything
 * interesting.  Only possible failure is to not
 * compile or hang.
 */
public class ClockTest extends x10Test {

	public boolean run() {
		clock c = clock.factory.clock();
		next;
		c.resume();
		c.drop();
		return true;
	}

	public static void main(String[] args) {
		new ClockTest().execute();
	}
}

