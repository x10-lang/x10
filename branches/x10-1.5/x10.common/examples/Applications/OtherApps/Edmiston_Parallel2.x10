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
 * Parallel version of Edmiston's algorithm for Sequence Alignment.
 * This code is an X10 port of the Edmiston_Sequential.c program written by
 * Sirisha Muppavarapu (sirisham@cs.unm.edu), U. New Mexico.
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 * @author Kemal Ebcioglu (kemal@us.ibm.com)
 */
public class Edmiston_Parallel2 extends x10Test {
	const int gapPen = 2;
	const int match = 0;
	const int misMatch = -1;
	const int EXPECTED_CHECKSUM = 549;
	const char[] aminoAcids = { 'A', 'C', 'G', 'T' };

	/**
	 * Edmiston's algorithm
	 */
	public boolean run() {

		final int N = 10;
		final int M = 10;
		final char value[.] c1 = new char value[[0:N]->here]
			(point[i]) { return (i == 0) ? '-' : randomChar(i); };
		final char value[.] c2 = new char value[[0:M]->here]
			(point[i]) { return (i == 0) ? '-' : randomChar(N+i); };
		final dist D = dist.factory.block([0:N,0:M]);
		final dist(:rank==D.rank) Dinner = D|[1:N,1:M];
		final dist Dboundary = D-Dinner;
		//  Boundary of e is initialized to:
		//  0     1*gapPen     2*gapPen     3*gapPen ...
		//  1*gapPen ...
		//  2*gapPen ...
		//  3*gapPen ...
		//  ...
		final istructInt[.] e = new istructInt[D] (point [i,j]) {
			final istructInt t = new istructInt();
			if (Dboundary.contains([i,j])) t.wr(gapPen*(i+j));
			return t;
		};

		finish ateach (point [i,j]: Dinner)
			e[i,j].wr(min(rdElem(e, i-1, j)+gapPen,
						rdElem(e, i, j-1)+gapPen,
						rdElem(e, i-1, j-1)
						+(c1[i] == c2[j] ? match : misMatch)));

		pr(c1, c2, e, "Edit distance matrix:");

		return checkSum(e) == EXPECTED_CHECKSUM;
	}

	/**
	 * read e[i,j] remotely,
	 * after waiting for it to be written.
	 */
	static int rdElem(final istructInt[.] e, final int i, final int j) {
		return future(e.distribution[i,j]) { e[i,j].rd() }.force();
	}

	/**
	 * returns the minimum of x y and z.
	 */
	static int min(int x, int y, int z) {
		int t = (x < y) ? x : y;
		return (t < z) ? t : z;
	}

	/**
	 * Function to generate the i'th random character
	 */
	static char randomChar(int i) {
		// Randomly select one of 'A', 'C', 'G', 'T'
		int n = 0;
		final Random  rand = new Random(1L);
		// find i'th random number.
		// TODO: need to pre-compute random numbers and re-use
		for (point [k]: [1:i]) n = nextChoice(rand);
		return aminoAcids[n];
	}

	static int nextChoice(Random rand) {
		int k1 = rand.nextBoolean() ? 0 : 1;
		int k2 = rand.nextBoolean() ? 0 : 1;
		return k1*2+k2;
	}

	/**
	 * Find the sum of a istructInt array
	 */
	static int checkSum(final istructInt[.] e) {
		int sum = 0;
		for (point [i,j]: e) sum += rdElem(e, i, j);
		return sum;
	}

	/**
	 * Print the Edit Distance Matrix
	 */
	static void pr(final char value[.] c1, final char value[.] c2, final istructInt[.] e, final String s)
	{
		final int N = c1.region.high();
		final int M = c2.region.high();
		final int K = 4; // padding amount

		System.out.println(s);

		System.out.print(" "+pad(' ', K));
		for (point [j]: c2) System.out.print(" "+pad(c2[j], K));
		System.out.println();

		for (point [i]: [0:N]) {
			System.out.print(" "+pad(c1[i], K));
			for (point [j]: [0:M]) System.out.print(" "+pad(rdElem(e, i, j), K));
			System.out.println();
		}
	}

	/**
	 * right justify an integer in a field of n blanks
	 */
	static String pad(int x, int n) {
		String s = ""+x;
		while (s.length() < n) s = " "+s;
		return s;
	}

	/**
	 * right justify a character in a field of n blanks
	 */
	static String pad(char x, int n) {
		String s = ""+x;
		while (s.length() < n) s = " "+s;
		return s;
	}

	public static void main(String[] args) {
		new Edmiston_Parallel2().execute();
	}

	/**
	 * Single assignment synchronization buffer,
	 * like an i-structure in a data flow machine.
	 * All readers will wait until write occurs.
	 */
	static class istructInt {
		int val;
		boolean filled = false;
		int rd() {
			int t;
			when (filled) { t = val; }
			return t;
		}
		atomic void wr(int v) {
			if (filled) throw new Error();
			filled = true;
			val = v;
		}
	}
}

