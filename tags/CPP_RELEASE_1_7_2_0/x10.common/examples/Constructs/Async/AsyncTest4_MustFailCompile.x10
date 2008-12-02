/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Only final free variables can be passed to async body.
 *
 * @author kemal 4/2005
 */
public class AsyncTest4_MustFailCompile extends x10Test {

	const int N = 20;

	public boolean run() {
		int s = 0;
		for (int i = 0; i < N; i++) {
			//==> compiler error expected here
			finish async(here) System.out.println("s="+s+" i="+i);
			s += i;
		}
		// no compiler error here
		s = 0;
		for (int i = 0; i < N; i++) {
			{
				final int i1 = i;
				final int s1 = s;
				finish async(here) System.out.println("s1="+s1+" i1="+i1);
			}
			s += i;
		}
		final int y;
		//==> Compiler error expected here
		finish async(here) { async(here) y = 3; }
		System.out.println("y="+y);
		return true;
	}

	public static void main(String[] args) {
		new AsyncTest4_MustFailCompile().execute();
	}
}

