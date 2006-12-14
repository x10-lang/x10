/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * GUPS - RandomAccess_skewed benchmark.
 *
 * version using "long" instead of ranNum.
 *
 * @author kemal
 * @author vj approx 7/2004
 * New version, 11/2004
 */
public class RandomAccess_skewed extends x10Test {
	// Set places.MAX_PLACES to 128 to match original
	// Set LOG2_TABLE_SIZE to 25 to match original

	// place.MAX_PLACES no longer works?
	private  static final int MAX_PLACES = x10.lang.place.MAX_PLACES;
	private  static final int LOG2_TABLE_SIZE = 8;
	private  static final int LOG2_SMALL_TABLE_SIZE = 4;
	private  static final int TABLE_SIZE = (1 << LOG2_TABLE_SIZE);
	private  static final int SMALL_TABLE_SIZE = (1 << LOG2_SMALL_TABLE_SIZE);
	private  static final int N_UPDATES = (10*TABLE_SIZE);
	private  static final int N_UPDATES_PER_PLACE = (N_UPDATES/MAX_PLACES);
	private  static final int WORD_SIZE = 64;
	private  static final long POLY = 7;
	private  static final long SMALL_TABLE_INIT = 0x0123456789abcdefL;
	// expected result with LOG2_SMALL_TABLE_SIZE = 5, MAX_PLACES = 2
	// LOG2_SMALL_TABLE_SIZE = 4
	private static final long EXPECTED_RESULT = 1973902911463121104L;

	// force all async's to go to one place
	private static final boolean SKEWED_DISTRIBUTION = true;
	private static final int HIGHLOAD_PLACE = 1;

	/**
	 * Get the value as a table index.
	 */
	private static int f(long val) {
		return (int)
			(SKEWED_DISTRIBUTION?
			 (val&((TABLE_SIZE/MAX_PLACES)-1))
			 + HIGHLOAD_PLACE*(TABLE_SIZE/MAX_PLACES):
			 (val & (TABLE_SIZE-1)));
	}

	/**
	 * Get the value as an index into the small table.
	 */
	private static int g(long val) {
		return (int)(val >>>
				(WORD_SIZE-LOG2_SMALL_TABLE_SIZE)); }

	/**
	 * Return the next number following this one.
	 * Actually the next item in the sequence generated
	 * by a primitive polynomial over GF(2).)
	 */
	private static long nextRandom(long val) {
		return
			((val<<1) ^ (val<0?POLY:0)); }

	/*
	 * Utility routines to create simple common dists
	 */

	/**
	 * create a simple 1D blocked dist
	 */
	private static dist block (int arraySize) {
		return dist.factory.block([0:(arraySize-1)]);
	}

	/**
	 * create a unique dist (mapping each i to place i)
	 */
	private static dist unique () {
		return dist.factory.unique(x10.lang.place.places);
	}

	/**
	 * create a constant-Here dist
	 */
	private static dist value(int arraySize) {
		return [0:(arraySize-1)]->here;
	}

	// distributed histogram table
	private static final long[.] table = new long[block(TABLE_SIZE)];

	// random number starting seeds for each place
	private static final long[.] ranStarts = new long[unique()];

	// A small value table that will be copied to all processors
	// Used in generating the update value
	private static final long value[.] smallTable = new long value[value(SMALL_TABLE_SIZE)]
		(point p[i]) { return i*SMALL_TABLE_INIT; };

	/**
	 * main GUPS routine
	 */
	static int pause(int n) {
		int s = 0;
		for (int k = 0; k < n; k++) { s = s+k; }
		return s;
	}

	public boolean run() {
		// initialize table
		finish ateach (point p: table.distribution) { table[p] = p[0]; }

		// initialize ranStarts
		finish ateach (point p: ranStarts.distribution)
		{ ranStarts[p] = C.starts(N_UPDATES_PER_PLACE*p[0]); }

		// In all places in parallel,
		// repeatedly generate random table indices
		// and do remote atomic updates on corresponding table elements
		finish ateach (point p : ranStarts.distribution) {
			long ran = nextRandom(ranStarts[p]);
			for (point count: [1:N_UPDATES_PER_PLACE]) {
				final int j = f(ran);
				final long k = smallTable[g(ran)];
				//final int dst = j/(TABLE_SIZE/MAX_PLACES);
				//System.out.println("src = "+p[0]+" dst = "+dst+" ran = "+Long.toHexString(ran)+" j = "+j+" k = "+Long.toHexString(k));
				async(table.distribution[j]) {
					pause(50000);
					atomic { table[j] = table[j]^k; }
				}
				ran = nextRandom(ran);
			}
		}

		long sum = table.sum();
		System.out.println(sum);
		return true;
	}

	public static void main(String[] args) {
		new RandomAccess_skewed().execute();
	}

	/**
	 * C routines to be linked with X10, written in X10 for now
	 */
	static class C {
		// self contained constants for C routines

		private static final long POLY = 0x0000000000000007L;
		private static final long PERIOD = 1317624576693539401L;

		private static long nextRandom(long temp) {
			return (temp << 1) ^ (temp < 0 ? POLY : 0);
		}

		private static boolean getBit(long n, int i) {
			return ((n>>>i)&1) != 0;
		}

		/**
		 * Utility routine to start random number generator at Nth step
		 * (original "starts" routine from RandomAccess).
		 * <code>
		 Functional synopsis:
		 long starts(long n) : =
		 long n1 = for (long t = n; t < 0 return t; next t = t+PERIOD) { };
		 long n2 = for (long t = n1; t > PERIOD return t; next t = t-PERIOD) { };
		 if (n2 == 0) return 0x1;
		 long m2[] = new long[0..63](i) { i == 0 ? 1 : (nextRandom**2)(m2[i-1]); }
		 int lastSetBit = findFirstSatisfying(int j: 62..0)(getBit(n2, j));
		 mutable long ran = 2;
		 for (int i = lastSetBit..1) {
		 long ranXor = Xor(int j: 0..63 where getBit(ran, j))(m2[j]);
		 ran = getBit(n2, i-1) ? nextRandom(ranXor) : ranXor; }
		 return ran;
		 * </code>
		 */
		public static long starts(long n) {
			int i, j;
			long[] m2 = new long[64];
			long temp, ran;

			while (n < 0) n += PERIOD;
			while (n > PERIOD) n -= PERIOD;
			if (n == 0) return 1;

			temp = 1;
			for (i = 0; i < 64; i++) {
				m2[i] = temp;
				temp = nextRandom(temp);
				temp = nextRandom(temp);
			}

			for (i = 62; i >= 0; i--)
				if (getBit(n, i))
					break;

			ran = 2;
			while (i > 0) {
				temp = 0;
				for (j = 0; j < 64; j++)
					if (getBit(ran, j))
						temp ^= m2[j];
				ran = temp;
				i -= 1;
				if (getBit(n, i))
					ran = nextRandom(ran);
			}

			return ran;
		}
	} // end C
}

