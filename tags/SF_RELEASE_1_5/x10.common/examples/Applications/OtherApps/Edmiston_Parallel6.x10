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
 * Parallel version of Edmiston's algorithm for Sequence Alignment
 * This code is an X10 port of the Edmiston_Parallel.c program written by
 * Sirisha Muppavarapu (sirisham@cs.unm.edu), U. New Mexico.
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 * @author Kemal Ebcioglu (kemal@us.ibm.com)
 *
 * This version uses futures for data flow synchronization.
 */
public class Edmiston_Parallel6 extends x10Test {
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
		//  The boundary of e is initialized to:
		//  0     1*gapPen     2*gapPen     3*gapPen ...
		//  1*gapPen ...
		//  2*gapPen ...
		//  3*gapPen ...
		//  ...
		// This is a workaround. Arrays of future<int>'s do not work

		final boxedFutureInt[.] e = new boxedFutureInt[D] (point [i,j]) { return new boxedFutureInt(); };
		for (point [i,j]: Dboundary)
			finish async(e.distribution[i,j])
				e[i,j].val = future(here) { gapPen*(i+j) };

		// There is a race condition in the sense that
		// e[i,j].val must be assigned with the correct future, before
		// e[i+1,j+1].val's future computation
		// tries to do e[i,j].val.force() remotely.
		// Hence the serial computation below.
		// Once the "future linking" is done by the for loop below,
		// the actual computation will occur in parallel,
		// in data flow order.

		for (point [i,j]: Dinner)
			finish async(e.distribution[i,j])
				e[i,j].val =
				future(here) {
					min(rd(e, i-1, j)+gapPen,
							rd(e, i, j-1)+gapPen,
							rd(e, i-1, j-1)+(c1[i] == c2[j] ? match : misMatch)) };

		rd(e, N, M); // ensure all computations end.

		pr(c1, c2, e, "Edit distance matrix:");

		return checkSum(e) == EXPECTED_CHECKSUM;
	}

	static int rd(final boxedFutureInt[.] e, final int i, final int j) {
		return
			future(e.distribution[i,j]) { e[i,j].val.force() }.force();
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
		// TODO: need parallel version of this
		for (point [k]: [1:i]) n = nextChoice(rand);
		return aminoAcids[n];
	}

	static int nextChoice(Random rand) {
		int k1 = rand.nextBoolean() ? 0 : 1;
		int k2 = rand.nextBoolean() ? 0 : 1;
		return k1*2+k2;
	}

	/**
	 * Find the sum of an array
	 */
	static int checkSum(final boxedFutureInt[.] e) {
		int sum = 0;
		for (point [i,j]: e) {
			sum += rd(e, i, j);
		}
		return sum;
	}

	/**
	 * Print the Edit Distance Matrix
	 */
	static void pr(final char value[.] c1, final char value[.] c2, final boxedFutureInt[.] e, final String s)
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
			for (point [j]: [0:M]) System.out.print(" "+pad(rd(e, i, j), K));
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
		new Edmiston_Parallel6().execute();
	}

	// workaround, arrays of future<int>'s do not work.
	static class boxedFutureInt {
		future<int> val = future(this) { 0 };
	}
}

