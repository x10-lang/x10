/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Use Dekker's test to confirm strong ordering of
 * atomic sections. (Failure of test proves lack of
 * ordering).
 *
 * @author kemal 4/2005
 */
public class AtomicOrdered extends x10Test {

	const int CACHESIZE = 32*1024/4;
	const int LINESIZE = 128/4;
	const int MAX_ASSOC = 8;

	final int[.] A = new int[[0:CACHESIZE*(MAX_ASSOC+2)-1]->here];

	public boolean run() {
		final pair r = new pair();
		finish {
			async(here) {
				finish { } // delay
				atomic A[0] = 1;
				int t;
				atomic t = A[LINESIZE];
				r.v1 = t;
			}
			async(here) {
				finish { } // delay
				atomic A[LINESIZE] = 1;
				int t;
				atomic t = A[0];
				r.v2 = t;
			}
		}
		System.out.println("v1 = "+r.v1+" v2 = "+r.v2);
		// not both could have read the old value
		atomic chk(!(r.v1 == 0 && r.v2 == 0));
		return true;
	}

	public static void main(String[] args) {
		new AtomicOrdered().execute();
	}

	static class pair {
		int v1;
		int v2;
	}
}

