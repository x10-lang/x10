/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Verifying for (point p[i,j]:R) { S; } loops can accept
 * continue and break statements.
 *
 * Test resulted in branch target not found message
 * as of 5/26/2005
 *
 * @author kemal, 5/2005
 */
public class BreakInForTest extends x10Test {

	const int N = 100;
	region R = [0:N];
	dist D = dist.factory.arbitrary(R);
	int n1 = 91;
	int n2 = 27;

	public boolean run() {
		for (int i = 0; i < N; i++) {
			if ((i+1) % n1 == 0) continue;
			if ((i+1) % n2 == 0) break;
		}
		for (point [i]: D) {
			if ((i+1) % n1 == 0) continue;
			if ((i+1) % n2 == 0) break;
		}
		return true;
	}

	public static void main(String[] args) {
		new BreakInForTest().execute();
	}
}

