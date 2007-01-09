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
 *
 * This version uses clocks for lockstep operation on successive waves.
 */
public class Edmiston_Parallel4 extends x10Test {
	const int N = 10;
	const int M = 10;
	const int EXPECTED_CHECKSUM = 549;

	/**
	 * main run method
	 */
	public boolean run() {
		charStr c1 = new charStr(N, 0);
		charStr c2 = new charStr(M, N);
		editDistMatrix m = new editDistMatrix(c1, c2);
		m.pr("Edit distance matrix:");
		m.verify(EXPECTED_CHECKSUM);
		return true;
	}

	public static void main(String[] args) {
		new Edmiston_Parallel4().execute();
	}

	/**
	 * Operations and distributed data structures related to
	 * an edit distance matrix
	 */
	static class editDistMatrix {
		const int gapPen = 2;
		const int match = 0;
		const int misMatch = -1;

		final int[.] e; // the edit distance matrix
		final charStr c1;
		final charStr c2;
		final int N;
		final int M;

		/**
		 * Create edit distance matrix with Edmiston's algorithm
		 */
		public editDistMatrix(charStr cSeq1, charStr cSeq2) {
			c1 = cSeq1;
			c2 = cSeq2;
			N = c1.s.region.high();
			M = c2.s.region.high();
			final dist(:rank==2) D = (dist(:rank==2)) dist.factory.block([0:N,0:M]);
			final dist(:rank==2) Dinner = D|[1:N,1:M];
			final dist Dboundary = D-Dinner;
			//  Boundary of e is initialized to:
			//  0     1*gapPen     2*gapPen     3*gapPen ...
			//  1*gapPen ...
			//  2*gapPen ...
			//  3*gapPen ...
			//  ...
			e = new int[D] (point [i,j])
			{ return Dboundary.contains([i,j]) ? gapPen*(i+j) : 0; };

			// Now compute the edit distance matrix.
			// The activity for each array element (i,j) waits for (i+j)-3
			// clock ticks (for the wavefront to reach it),
			// then consumes the data produced by previous wave(s) and
			// produces this wave's data.
			// For example, the array elements (1,2),(2,1) will execute 1 next,
			// and (1,3),(2,2),(3,1) will execute 2 nexts,
			// before starting their actual computation.
			finish async {
				final clock c = clock.factory.clock();
				ateach (point [i,j]: Dinner) clocked(c) {
					for (point [k]: [3:(i+j)]) next; //wait for my wave
					e[i,j] = min(rd(i-1, j)+gapPen,
							rd(i, j-1)+gapPen,
							rd(i-1, j-1)
							+(c1.s[i] == c2.s[j] ? match : misMatch));
					c.drop(); //immediately de-register with clock, so future
					//waves will not need to wait for me
				}
			}
		}

		/**
		 * Find the sum of the elements of the edit distance matrix
		 */
		int checkSum() {
			int sum = 0;
			for (point [i,j]: e) sum += rd(i, j);
			return sum;
		}

		/**
		 * Verify that the edit distance matrix has the expected
		 * checksum.
		 */
		public void verify(int expectedChecksum) {
			if (checkSum() != expectedChecksum) throw new Error();
		}

		/**
		 * Print the Edit Distance Matrix
		 */
		public void pr(final String s)
		{
			final int K = 4; // padding amount

			System.out.println(s);

			System.out.print(" "+pad(' ', K));
			for (point [j]: [0:M]) System.out.print(" "+pad(c2.s[j], K));
			System.out.println();

			for (point [i]: [0:N]) {
				System.out.print(" "+pad(c1.s[i], K));
				for (point [j]: [0:M]) System.out.print(" "+pad(rd(i, j), K));
				System.out.println();
			}
		}

		/*
		 * Utility methods.
		 */

		/**
		 * possibly remote read of e[i,j]
		 */
		int rd(final int i, final int j) {
			return future(e.distribution[i,j]) { e[i,j] }.force();
		}

		/**
		 * returns the minimum of x y and z.
		 */
		static int min(int x, int y, int z) {
			int t = (x < y) ? x : y;
			return (t < z) ? t : z;
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
	}

	/**
	 * A random character array consisting of the letters ACTG
	 * and beginning with -
	 */
	static value class charStr {
		final char value[.] s;
		const char[] aminoAcids = { 'A', 'C', 'G', 'T' };
		public charStr(final int siz, final int randomStart) {
			s = new char value[[0:siz]->here]
				(point [i]) { return i == 0 ? '-' : randomChar(randomStart+i); };
		}

		/**
		 * Function to generate the i'th random character
		 */
		private static char randomChar(int i) {
			// Randomly select one of 'A', 'C', 'G', 'T'
			int n = 0;
			final Random  rand = new Random(1L);
			// find i'th random number.
			// TODO: need to pre-compute random numbers and re-use
			for (point [k]: [1:i]) n = nextChoice(rand);
			return aminoAcids[n];
		}

		private static int nextChoice(Random rand) {
			int k1 = rand.nextBoolean() ? 0 : 1;
			int k2 = rand.nextBoolean() ? 0 : 1;
			return k1*2+k2;
		}
	}
}

