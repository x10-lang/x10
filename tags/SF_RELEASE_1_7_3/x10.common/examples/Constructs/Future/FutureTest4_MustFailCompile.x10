/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests that free variables used in e in future { e }
 * are declared final.
 *
 * Expected result: must fail at compile time.
 *
 * @author kemal 4/2005
 */
public class FutureTest4_MustFailCompile extends x10Test {

	const int N = 8;

	/**
	 * testing free variables in future expression
	 */
	public boolean run() {
		final int[.] A = new int[dist.factory.block([0:N-1, 0:N-1])]
			(point [i,j]) { return N*i+j; };
		int x;
		int s;
		x = 0;
		s = 0;
		for (int i = 0; i < N; i++) {
			s += i;
			//=== >compiler error: i, s not final
			x += future(A.distribution[i,s%N]) { A[i,s%N] }.force();
		}
		System.out.println("x = "+x);
		if (x != 252) return false;
		x = 0;
		s = 0;
		for (int i = 0; i < N; i++) {
			s += i;
			{ final int I = i; final int S = s;
				// no compiler error
				x += future(A.distribution[I,S%N]) { A[I,S%N] }.force();
			}
		}
		System.out.println("x = "+x);
		return (x == 252);
	}

	public static void main(String[] args) {
		new FutureTest4_MustFailCompile().execute();
	}
}

