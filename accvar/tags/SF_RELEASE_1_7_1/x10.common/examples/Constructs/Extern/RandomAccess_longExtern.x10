/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * RandomAccess benchmark.
 *
 * Based on HPCC 0.5beta
 *
 * The external "starts" routine is written in C.
 *
 * @author kemal
 * @author vj approx 7/2004
 * New version, 11/2004
 */
public class RandomAccess_longExtern extends x10Test {

	// Set places.MAX_PLACES to 128 to match original
	// Set LOG2_TABLE_SIZE to 25 to match original

	const int  MAX_PLACES = x10.lang.place.MAX_PLACES;
	const int  LOG2_TABLE_SIZE = 5;
	const int  LOG2_S_TABLE_SIZE = 4;
	const int  TABLE_SIZE = (1<<LOG2_TABLE_SIZE);
	const int  S_TABLE_SIZE = (1<<LOG2_S_TABLE_SIZE);
	const int  N_UPDATES = (4*TABLE_SIZE);
	const int  N_UPDATES_PER_PLACE = (N_UPDATES/MAX_PLACES);
	const int  WORD_SIZE = 64;
	const long POLY = 7;
	const long S_TABLE_INIT = 0x0123456789abcdefL;
	// expected result with LOG2_S_TABLE_SIZE = 5,
	// LOG2_S_TABLE_SIZE = 4
	const long EXPECTED_RESULT = 1973902911463121104L;

	/** Get the value as a table index.
	 */
	int f(long val) { return (int) (val & (TABLE_SIZE-1)); }
	/** Get the value as an index into the small table.
	 */
	int g(long val) { return (int)(val>>>(WORD_SIZE-LOG2_S_TABLE_SIZE)); }

	/** Return the next number following this one.
	 * Actually the next item in the sequence generated
	 * by a primitive polynomial over GF(2).)
	 */
	long nextRandom(long val) { return ((val << 1) ^ (val < 0 ? POLY : 0)); }

	/*
	 * Utility routines to create simple common dists
	 */
	/**
	 * create a simple 1D blocked dist
	 */
	dist block (int arraySize) {
		return dist.factory.block([0:(arraySize-1)]);
	}

	/**
	 * create a unique dist (mapping each i to place i)
	 */
	dist unique () {
		return dist.factory.unique(x10.lang.place.places);
	}

	/**
	 * The random number "starts" routine is in external C code
	 */
	static extern long starts(long n);
	static { System.loadLibrary("RandomAccessLong"); }

	/**
	 * main RandomAccess routine
	 */
	public boolean run() {

		// A small value table that will be copied to all processors
		final long value[.] smallTable =
			new long value[[0:S_TABLE_SIZE-1]->here]
			(point p[i]) { return i*S_TABLE_INIT; };
		// distributed histogram table
		final long[.] table = new long[block(TABLE_SIZE)]
			(point p[i]) { return i; };
		// random number starting seeds for each place
		final long[.] ranStarts = new long[unique()]
			(point p[i]) { return starts(N_UPDATES_PER_PLACE*i); };

		// In all places in parallel, repeatedly generate random indices
		// and do remote atomic updates on corresponding table elements
		finish ateach (point p[i]: ranStarts) {
			long ran = nextRandom(ranStarts[i]);
			for (point q[n]: [1:N_UPDATES_PER_PLACE]) {
				System.out.println("Place "+i+ " iteration "+n);
				final int  j = f(ran);
				final long k = smallTable[g(ran)];
				async(table.distribution[j]) atomic table[j] ^= k;
				ran = nextRandom(ran);
			}
		}
		final long sum = table.sum();
		System.out.println("Check sum = "+sum);
		return sum == EXPECTED_RESULT;
	}

	public static void main(String[] args) {
		new RandomAccess_longExtern().execute();
	}
}

