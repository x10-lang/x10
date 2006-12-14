/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import java.util.Random;
import java.lang.Integer;
import harness.x10Test;

/**
 * Sequential version of Edmiston's algorithm for Sequence Alignment.
 * This code is an X10 port of the Edmiston_Sequential.c program written by
 * Sirisha Muppavarapu (sirisham@cs.unm.edu), U. New Mexico.
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 */
public class Edmiston_Sequential1 extends x10Test {
	const int iGapPen = 2;
	const int iMatch = 0;
	const int iMisMatch = -1;
	const int EXPECTED_CHECKSUM = 549;
	const Random rand = new Random(1L);

	/*
	 * NOTE: the first two methods, findMin() and findEditDistance(), contain the core of the computation.
	 * All remaining methods are helper functions that are unchaned in the serial & parallel versions
	 */
	/**
	 * Function which determines the minimum value among North, NorthWest
	 * West elements of the EditDistance Matrix for the given values of
	 * Row and Column Numbers
	 */
	static int findMin(int iRow, int iCol, char[] cSeq1, char[] cSeq2, int[.] iEditDist) {
		int iNorth, iWest, iNorthWest;

		if (iCol == 0) {
			if (iRow == 0) // iCol == 0 && iRow == 0
				return 0;
			else // iCol == 0 && iRow > 0
				return iEditDist[iRow-1,iCol]+iGapPen;
		}
		else { // iCol > 0
			if (iRow == 0) // iRow == 0 && iCol > 0
				return iEditDist[iRow,iCol-1]+iGapPen;
			else { // iCol > 0 && iRow > 0
				iNorth = iEditDist[iRow-1,iCol]+iGapPen;
				iWest = iEditDist[iRow,iCol-1]+iGapPen;
				iNorthWest = iEditDist[iRow-1,iCol-1]+isMatch(cSeq1[iRow-1], cSeq2[iCol-1]);
				return MIN3(iNorth, iWest, iNorthWest);
			}
		}
	}

	static int[.] findEditDist(char cSeq1[], char cSeq2[]) {
		final int iMatRowSize = cSeq1.length;
		final int iMatColSize = cSeq2.length;
		int[.] iEditDist;

		System.out.println("cSeq1 = " + new String(cSeq1));
		System.out.println("cSeq2 = " + new String(cSeq2));

		System.out.println("iMatRowSize = " + iMatRowSize);
		System.out.println("iMatColSize = " + iMatColSize);

		iEditDist = initEditDistance(iMatRowSize, iMatColSize);

		System.out.println("The EditDistance Matrix before computation is = ");
		printEditDistance(cSeq1, cSeq2, iEditDist);

		for (int i = 0; i <= iMatRowSize; i++) {
			for (int j = 0; j <= iMatColSize; j++) {
				iEditDist[i,j] = findMin(i, j, cSeq1, cSeq2, iEditDist);
			}
		}

		return iEditDist;
	}

	/*
	 * START OF HELPER FUNCTIONS (INCLUDING MAIN PROGRAM)
	 */

	private static int MIN(int a, int b) { return ((a) < (b) ? (a) : (b)); }

	private static int MIN3(int a, int b, int c) { return MIN(MIN((a), (b)), (c)); }

	/**
	 * Create the EditDistance Matrix
	 */
	static int[.] initEditDistance(int iMatRowSize, int iMatColSize) {
		region R = [0:iMatRowSize, 0:iMatColSize];
		dist D = R->here; // local distribution
		int[.] iEditDist = new int[D];
		return iEditDist;
	} // initEditDistance()

	/**
	 * Print the Edit Distance Matrix
	 */
	static void printEditDistance(char[] cSeq1, char[] cSeq2, int[.] iEditDist)
	{
		final int iMatRowSize = cSeq1.length;
		final int iMatColSize = cSeq2.length;

		System.out.println("Matrix EditDistance is:");

		System.out.print("        - ");
		for (int j = 1; j <= iMatColSize; j++)
			System.out.print("   " + cSeq2[j-1] + " ");

		System.out.println();

		for (int i = 0; i <= iMatRowSize; i++) {
			if (i == 0)
				System.out.print("   - ");
			else
				System.out.print("   " + cSeq1[i-1] + " ");
			for (int j = 0; j <= iMatColSize; j++) {
				String s = new Integer(iEditDist[i,j]).toString();
				System.out.print(" "); // Print a leading blank
				for (int k = 0 ; k < 3 - s.length() ; k++)
					System.out.print(" "); // Print padding blanks
				System.out.print(s); // Print distance (assume length is <= 3 chars)
				System.out.print(" "); // Print trailing blank
			}
			System.out.println();
		}
	} // printEditDistance()

	/**
	 * Function to generate a random sequence and initialize it as the
	 * input sequence
	 */
	static void initSeq(char[] cDest) {
		for (int i = 0 ; i< cDest.length ; i++) {
			// Randomly select one of 'A', 'C', 'G', 'T' and assign to cDest[i]
			final boolean bit1 = rand.nextBoolean();
			final boolean bit2 = rand.nextBoolean();
			cDest[i] = bit1 ? (bit2 ? 'A' : 'C') : (bit2 ? 'G' : 'T');
		}
	}

	static void printSeq(char[] cSeq) {
		System.out.println("The Given String is = " + new String(cSeq));
	}

	/**
	 * Function to determine if the characters of the input sequences
	 * match at that particular index
	 */
	static int isMatch(char cChar1, char cChar2) {
		return (cChar1 == cChar2) ? iMatch : iMisMatch;
	}

	/* The main method which implements the Edmiston's Algorithms */
	public boolean run() {
		final int iMatRowSize = 10;
		final int iMatColSize = 10;

		char[] cSeq1 = new char[iMatRowSize];
		char[] cSeq2 = new char[iMatColSize];

		initSeq(cSeq1);
		initSeq(cSeq2);

		printSeq(cSeq1);
		printSeq(cSeq2);

		int[.] iEditDist = findEditDist(cSeq1, cSeq2);

		System.out.println("Inside main(): The Edit Distance Matrix After Computation is = ");
		printEditDistance(cSeq1, cSeq2, iEditDist);

		return iEditDist.sum() == EXPECTED_CHECKSUM;
	}

	/*
	 * END OF HELPER FUNCTIONS
	 */

	public static void main(String[] args) {
		new Edmiston_Sequential1().execute();
	}
}

