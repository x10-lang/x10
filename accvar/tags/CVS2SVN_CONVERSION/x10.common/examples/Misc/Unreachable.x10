/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test resulted in unreachable statement message
 * as of 4/20/2005.
 *
 * @author Mandana Vaziri
 * @author kemal 4/2005
 */
public class Unreachable extends x10Test {

	final int N = 10;
	final region R = [0:N];
	final dist D = dist.factory.arbitrary(R);

	void test() {
		async (D[0]) {
			return;
		}
	}

	public boolean run() {
		finish test();
		return true;
	}

	public static void main(String[] args) {
		new Unreachable().execute();
	}
}

