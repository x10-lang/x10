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
 * User defined type array bounds test - 2D.
 *
 * Randomly generate 2D arrays and indices.
 *
 * See if the array index out of bounds exception occurs
 * in the right conditions.
 *
 * @author kemal 11/2005
 */
public class UserArrayBounds2D extends x10Test {

	public boolean run() {
		final int COUNT = 100;
		final int L = 10;
		final int K = 3;
		for(int n = 0; n < COUNT; n++) {
			int i = ranInt(-L-K, L+K);
			int j = ranInt(-L-K, L+K);
			int lb1 = ranInt(-L, L);
			int lb2 = ranInt(-L, L);
			int ub1 = ranInt(lb1, L);
			int ub2 = ranInt(lb2, L);
			boolean withinBounds = arrayAccess(lb1, ub1, lb2, ub2, i, j);
			chk(iff(withinBounds,
						i>=lb1 && i<=ub1 &&
						j>=lb2 && j<=ub2));
		}
		return true;
	}

	/**
	 * create a[lb1..ub1,lb2..ub2] then access a[i,j], return true iff
	 * no array bounds exception occurred
	 */
	private static boolean arrayAccess(int lb1, int ub1, int lb2, int ub2, int i, int j) {
		//pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+i+" "+j);

		boxedInt[.] a = new boxedInt[[lb1:ub1,lb2:ub2]->here]
			(point [i,j]) {return new boxedInt(0);};

		boolean withinBounds = true;
		try {
			a[i,j] = new boxedInt((int) 0xabcdef07L);
			//pr("assigned");
			chk(a[i,j].equals(new boxedInt((int) 0xabcdef07L)));
		} catch (ArrayIndexOutOfBoundsException e) {
			withinBounds = false;
		}
		//pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+i+" "+j+" "+withinBounds);

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
		new UserArrayBounds2D().execute();
	}

	static class boxedInt extends x10.lang.Object {
		int val;
		public boxedInt(int x) { val = x; }
		public boolean equals(boxedInt other) {
			return this.val == other.val;
		}
	}
}

