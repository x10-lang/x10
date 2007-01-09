/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import java.util.Random;
import harness.x10Test;

/**
 * Array bounds test - 1D.
 *
 * Randomly generate 1D arrays and indices.
 *
 * See if the array index out of bounds exception occurs
 * in the right conditions.
 *
 * @author kemal 1/2005
 */
public class ArrayBounds1D extends x10Test {

	public boolean run() {
		final int COUNT = 100;
		final int L = 10;
		final int K = 3;
		for(int n = 0; n < COUNT; n++) {
			int i = ranInt(-L-K, L+K);
			int lb1 = ranInt(-L, L);
			int ub1 = ranInt(lb1-1, L); // include empty reg.
			boolean withinBounds = arrayAccess(lb1, ub1, i);
			chk(iff(withinBounds,
						i>=lb1 && i<=ub1));
		}
		return true;
	}

	/**
	 * create a[lb1..ub1] then access a[i], return true iff
	 * no array bounds exception occurred
	 */
	private static boolean arrayAccess(int lb1, int ub1, int i) {
		//pr(lb1+" "+ub1+" "+i);
		int[.] a =new int[[lb1:ub1]->here];

		boolean withinBounds = true;
		try {
			a[i] = (int) 0xabcdef07L;
			chk(a[i] == (int) 0xabcdef07L);
		} catch (ArrayIndexOutOfBoundsException e) {
			withinBounds = false;
		}
		//pr(lb1+" "+ub1+" "+i+" "+withinBounds);

		return withinBounds;
	}

	// utility methods after this point

	/**
	 * print a string
	 */
	private static void pr(String s) {
		System.out.println(s);
	}

	/**
	 * true iff (x if and only if y)
	 */
	private static boolean iff(boolean x, boolean y) {
		return x == y;
	}

	public static void main(String[] args) {
		new ArrayBounds1D().execute();
	}
}

