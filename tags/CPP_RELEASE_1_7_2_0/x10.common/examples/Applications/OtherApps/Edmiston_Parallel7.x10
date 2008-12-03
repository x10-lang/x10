/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import java.lang.Integer;
import harness.x10Test;

/**
 * Recursive parallel version with a single clock.
 *
 * This code is an X10 port of the Edmiston_Sequential.c program written by
 * Sirisha Muppavarapu (sirisham@cs.unm.edu), U. New Mexico.
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 * @author Kemal Ebcioglu (kemal@us.ibm.com)
 */
public class Edmiston_Parallel7 extends x10Test {

	public boolean run() {
		final int N = 10; //rows
		final int M = 100; //columns
		final int EXPECTED_CHECKSUM = 101346;
		// generate two random input strings c1 and c2
		Random r = new Random(1);
		RandCharStr c1 = new RandCharStr(r, N);
		RandCharStr c2 = new RandCharStr(r, M);
		// run the user's parallel code with inputs c1 and c2
		EditDistMatrix m = new EditDistMatrix(c1, c2);
		// print result
		m.printMatrix();
		m.verify(EXPECTED_CHECKSUM);
		return true;
	}

	public static void main(String[] args) {
		new Edmiston_Parallel7().execute();
	}

	/**
	 * Operations and data structures related to
	 * an edit distance matrix.
	 */
	static class EditDistMatrix {
		const int gapPen = 2;
		const int match = 0;
		const int misMatch = 1;

		public final int[.] e; // the edit distance matrix
		final RandCharStr c1; // input string 1
		final RandCharStr c2; // input string 2
		final int N; // matrix dimensions
		final int M;

		final dist B; // Block distribution on number of rows

		/**
		 * Create the edit distance matrix using Edmiston's algorithm,
		 * from the input strings cSeq1 and cSeq2.
		 */
		public EditDistMatrix(RandCharStr cSeq1, RandCharStr cSeq2) {
			c1 = cSeq1;
			c2 = cSeq2;
			N = c1.s.length-1;
			M = c2.s.length-1;
			B = dist.factory.block([0:N]);
			// Construct a [block,*] distribution in D
			dist(:rank==2) D = [0:-1,0:-1]->here;  // Initialize to empty distribution
			for (point [i]: [0:N]) D = D||([i:i,0:M]->B[i]);

			// D_inner is the distribution for the
			// inner part of the matrix,
			// with row 0 and column 0 missing.
			final dist(:rank==2) D_inner = D|[1:N,1:M];

			// D_boundary applies to just row 0 and column 0.
			final dist D_boundary = D-D_inner;

			e = new int[D];

			//  Boundary of e is initialized to:
			//  0     1*gapPen     2*gapPen     3*gapPen ...
			//  1*gapPen ...
			//  2*gapPen ...
			//  3*gapPen ...
			//  ...
			finish ateach (point [i,j]: D_boundary) e[i,j] = gapPen*(i+j);

			finish async(B[1]) {
				final clock c = clock.factory.clock();
				async clocked(c) computeRow(1, c);
			}
		}

		void computeRow(final int i, final clock c) {
			computeElement(i, 1);
			next;
			if (i < N) async(B[i+1]) clocked(c) computeRow(i+1, c);
			for (int j = 2 ; j <= M ; j++) {
				computeElement(i, j);
				next;
			}
		}

		void computeElement(final int i, final int j) {
			e[i,j] = min(readElem(i-1, j)+gapPen,
					readElem(i, j-1)+gapPen,
					readElem(i-1, j-1)+
					(c1.s[i] == c2.s[j] ? match : misMatch));
		}

		/**
		 * Return element [i,j] of matrix
		 */
		public int readElem(final int i, final int j) {
			return future (e.distribution[i,j]) { e[i,j] }.force();
		}

		/**
		 * Print the Edit Distance Matrix.
		 */
		public void printMatrix()
		{
			System.out.println("Minimum Matrix EditDistance is: "+readElem(N, M));
			System.out.println("Matrix EditDistance is:");

			System.out.print(pad(' '));
			for (point [j]: [0:M]) System.out.print(pad(c2.s[j]));
			System.out.println();

			for (point [i]: [0:N]) {
				System.out.print(pad(c1.s[i]));
				for (point [j]: [0:M]) System.out.print(pad(readElem(i, j)));
				System.out.println();
			}
		}

		/**
		 *Throw an error if the sum of the elements of e
		 *does not match the expected checksum.
		 */
		public void verify(int expectedCheckSum) {
			int sum = e.sum();
			System.out.println("sum = "+sum);
			chk(sum == expectedCheckSum);
		}

		/*
		 * Utility methods.
		 */

		/**
		 * returns the minimum of x y and z.
		 */
		static int min(int x, int y, int z) {
			int t = (x < y) ? x : y;
			return (t < z) ? t : z;
		}

		/**
		 * Pad a string s on the left with blanks,
		 * to create a string of length at least n.
		 * Then add two blanks to the end and beginning of the string.
		 */
		static String pad0(String s) {
			final int n = 3;
			while (s.length() < n) s = " "+s;
			return " "+s+" ";
		}

		static String pad(int x) { return pad0(""+x); }

		static String pad(char x) { return pad0(""+x); }
	}

	/**
	 * Common random number generator class.
	 */
	static class Random {

		int randomSeed;

		/**
		 * Create a new random number generator with seed x
		 */
		public Random(int x) {
			randomSeed = x;
		}

		/**
		 * Returns the next random number between 0 and 128,
		 * according to this random number generator's sequence.
		 */
		public int nextAsciiNumber() {
			randomSeed = (randomSeed * 1103515245 +12345);
			return (int)(unsigned(randomSeed / 65536) % 128L);
		}

		/**
		 * Convert an int to an unsigned int (C-style).
		 */
		static long unsigned(int x) {
			return ((long)x & 0x00000000ffffffffL);
		}
	}

	/**
	 * A class pertaining to random character arrays (strings).
	 */
	static value class RandCharStr {
		public final char [] s; // the string (character array).

		/**
		 * Create a random character array of
		 * length len from the alphabet A,C,G,T,
		 * using the random number generator r.
		 * The array begins with an extra '-',
		 * thus it will have len+1 characters.
		 */
		public RandCharStr(Random r, int len) {
			s = new char[len+1];
			s[0] = '-';
			int i = 1;
			while (i <= len) {
				int x = r.nextAsciiNumber();
				switch (x) {
					case 65: s[i++] = 'A';  break;
					case 67: s[i++] = 'C';  break;
					case 71: s[i++] = 'G';  break;
					case 84: s[i++] = 'T';  break;
					default:
				}
			}
		}
	}
}

