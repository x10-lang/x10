/*
 * This code has been contributed by the DARPA HPCS program.  Contact
 * David Koester <dkoester@mitre.org> or Bob Lucas <rflucas@isi.edu>
 * if you have questions.
 *
 * GUPS (Giga UPdates per Second) is a measurement that profiles the memory
 * architecture of a system and is a measure of performance similar to MFLOPS.
 * The HPCS HPCchallenge RandomAccess benchmark is intended to exercise the
 * GUPS capability of a system, much like the LINPACK benchmark is intended to
 * exercise the MFLOPS capability of a computer.  In each case, we would
 * expect these benchmarks to achieve close to the "peak" capability of the
 * memory system. The extent of the similarities between RandomAccess and
 * LINPACK are limited to both benchmarks attempting to calculate a peak system
 * capability.
 *
 * GUPS is calculated by identifying the number of memory locations that can be
 * randomly updated in one second, divided by 1 billion (1e9). The term "randomly"
 * means that there is little relationship between one address to be updated and
 * the next, except that they occur in the space of one half the total system
 * memory.  An update is a read-modify-write operation on a table of 64-bit words.
 * An address is generated, the value at that address read from memory, modified
 * by an integer operation (add, and, or, xor) with a literal value, and that
 * new value is written back to memory.
 *
 * We are interested in knowing the GUPS performance of both entire systems and
 * system subcomponents --- e.g., the GUPS rating of a distributed memory
 * multiprocessor the GUPS rating of an SMP node, and the GUPS rating of a
 * single processor.  While there is typically a scaling of FLOPS with processor
 * count, a similar phenomenon may not always occur for GUPS.
 *
 * For additional information on the GUPS metric, the HPCchallenge RandomAccess
 * Benchmark,and the rules to run RandomAccess or modify it to optimize
 * performance -- see http://icl.cs.utk.edu/hpcc/
 *
 */

/*
 * This file contains the computational core of the single cpu version
 * of GUPS.  The inner loop should easily be vectorized by compilers
 * with such support.
 *
 * This core is used by both the single_cpu and star_single_cpu tests.
 */

/**
(C) Copyright IBM Corp. 2006
**/


/* Number of updates to table (suggested: 4x number of table entries)
 *
 * This is a distributed implementation of RandomAccess, trying to be the X10
 * version of GUPS.cpp by Hanhong as close as possible in the sense that it is
 * at least algorithmically equivalanet to GUPS.cpp without aggregation
 * (aggregate=false).
 *
 * The number of places must be a power of 2 which can be specified as
 * a command line argument. The other two arguments are flags for detailed output and
 * embarrassing parallelism.
 * Modified:
 *          May 03, 2007: add the verification method implemented in Hanhong's C++ code.
 * Author: Tong Wen
 */

public final class RandomAccess_Dist {

	public static final class localTable {
		public final long[:self.rect && self.zeroBased && self.rank==1] array;
		public final long tableSize;
		public final long mask;
		public localTable(long size) {
			tableSize=size;
			mask=tableSize-1;
			array=(long[:self.rect && self.zeroBased && self.rank==1]) new long[[0:(int)mask]];
		}

		public void update(long ran) {
			array[(int)(ran & mask)] ^= ran;
		}

		//for verification defined in Hanhong's C++ code
		public void verify(long ran) {
			array[(int)(ran & mask)]++;
		}
	}

	public static double mysecond() {
		return (double) ((double)(System.nanoTime() / 1000) * 1.e-6);
	}

	private final static long POLY = 0x0000000000000007;
	private final static long PERIOD = 1317624576693539401L;

	private final static dist UNIQUE=dist.UNIQUE;
	private final static int NUMPLACES=place.MAX_PLACES;
	private final static int PLACEIDMASK=NUMPLACES-1;
	/*
	 * VERIFY = 0 Update
	 *        = 1 Parallel verification using verify()
	 *        = 2 Udpate with sequential verification using Verify()
	 */
	private final static int UPDATE = 0;
	private final static int VERIFICATION_P = 1;
	private final static int UPDATE_AND_VERIFICATION = 2;

	//private final  int VERIFY = UPDATE;

	/* Utility routine to start random number generator at Nth step */
	public static long HPCC_starts(long n) {
		int i, j;
		long[] m2 = new long[64];
		long temp, ran;

		while (n < 0) n += PERIOD;
		while (n > PERIOD) n -= PERIOD;
		if (n == 0) return 0x1;

		temp = 0x1;
		for (i=0; i<64; i++) {
			m2[i] = temp;
			temp = (temp << 1) ^ ((long) temp < 0 ? POLY : 0);
			temp = (temp << 1) ^ ((long) temp < 0 ? POLY : 0);
		}

		for (i=62; i>=0; i--)
			if (((n >> i) & 1) != 0)
				break;

		ran = 0x2;
		while (i > 0) {
			temp = 0;
			for (j=0; j<64; j++)
				if (((ran >> j) & 1) != 0)
					temp ^= m2[j];
			ran = temp;
			i -= 1;
			if (((n >> i) & 1) != 0)
				ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
		}

		return ran;
	}

	//Rigorous verification, but using for loop. Slow for large data set
	/*
	private static void Verify(final long LogTableSize, final boolean Embarrassing,
			final localTable [:self.rect && self.zeroBased && self.rank==1] Table) {

		System.out.println("Verifying result ...");
		final long TableSize=(1<<LogTableSize);
		final long numUpdates=TableSize*4;

		finish for(point [p] : UNIQUE) {
			long ran=HPCC_starts (p*numUpdates);
			for (long i=0; i<numUpdates; i++) {
				final int placeID;
				if (Embarrassing)
					placeID=p;
				else
					placeID=(int)((ran>>LogTableSize) & PLACEIDMASK);
				ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
				final long temp=ran;
				//async (UNIQUE[placeID]) Table[placeID].update(temp);
				async (UNIQUE[placeID]) {
					Table[placeID].array[(int)(temp & Table[placeID].mask)] ^= temp;
				}
			}
		}
		final long [] SUM = new long [NUMPLACES];
		finish ateach(point [p] : UNIQUE) {
			long sum=0;
			for (int i=0;i<Table[p].tableSize;i++) sum+=Table[p].array[i];
			//System.out.println("local sum at "+p+" is "+sum+" (correct=0)");
			final long temp = sum;
			async (UNIQUE[0]) SUM[p]=temp;
		}
		long globalSum=0;
		for (int i=0; i<NUMPLACES; i++) globalSum+=SUM[i];
		System.out.println("   global sum is "+globalSum+" (correct=0)");
	}
	*/

	//the verification method implemented in Hanhong's C++ code
	/*
	private static void verify(final long LogTableSize, final boolean Embarrassing,
			final localTable [:self.rect && self.zeroBased && self.rank==1] Table) {

		final long TableSize=(1<<LogTableSize);
		final long numUpdates=TableSize*4*NUMPLACES;

		final long [] SUM = new long [NUMPLACES];
		finish ateach(point [p] : UNIQUE) {
			long sum=0;
			for (int i=0;i<Table[p].tableSize;i++) sum+=Table[p].array[i];
			//System.out.println("local sum at "+p+" is "+sum+" (correct=0)");
			final long temp = sum;
			async (UNIQUE[0]) SUM[p]=temp;
		}
		long globalSum=0;
		for (int i=0; i<NUMPLACES; i++) globalSum+=SUM[i];
		double missedUpdateRate = (globalSum-numUpdates)/numUpdates*100;
		System.out.println("  the rate of missed updates  "+ missedUpdateRate+ "%");
	}
	*/
	/*
	private static void RandomAccessUpdate(final long LogTableSize, final boolean Embarrassing,
			final localTable [:self.rect && self.zeroBased && self.rank==1] Table) {

		// Initialize main table
		final long TableSize=(1<<LogTableSize);
		final long numUpdates=TableSize*4;

		System.out.println ("Is the mode of update verification? "+VERIFY);
		if (VERIFY)
			finish ateach(point [p] : UNIQUE) {
				long ran=HPCC_starts (p*numUpdates);
				for (long i=0; i<numUpdates; i++) {
					final int placeID;
					if (Embarrassing)
						placeID=p;
					else
						placeID=(int)((ran>>LogTableSize) & PLACEIDMASK);
					ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
					final long temp=ran;
					async (UNIQUE[placeID]) Table[placeID].verify(temp);
					//Table[placeID].array[(int)(temp & Table[placeID].mask)]++;
				}
			}
		else
			finish ateach(point [p] : UNIQUE) {
				long ran=HPCC_starts (p*numUpdates);
				for (long i=0; i<numUpdates; i++) {
					final int placeID;
					if (Embarrassing)
						placeID=p;
					else
						placeID=(int)((ran>>LogTableSize) & PLACEIDMASK);
					ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
					final long temp=ran;
					async (UNIQUE[placeID]) Table[placeID].update(temp);
				}
			}
	}
	*/

	public static void  main(String[] args) {
		if ((NUMPLACES & (NUMPLACES-1)) > 0) {
			System.out.println("the number of places must be a power of 2!");
			System.exit(-1);
		}
		int VERIFY = UPDATE;
		boolean doIO = false, embarrassing=false;
		int  logTableSize= 10; //20
		for (int q = 0; q < args.length; ++q) {
			if (args[q].equals("-o")) {
				doIO = true;
			}
			if (args[q].equals("-e")) {
				embarrassing = true;
			}
			if (args[q].equals("-m")) {
				++q;
				logTableSize = java.lang.Integer.parseInt(args[q]);
			}
			if (args[q].equals("-v")) {
				++q;
				VERIFY = java.lang.Integer.parseInt(args[q])%3;
			}
		}

		//long[:self.rect && self.zeroBased && self.rank==1] Table;
		final long tableSize=(1<<logTableSize);
		final long numUpdates=tableSize*4*NUMPLACES;

		final  localTable [:self.rect && self.zeroBased && self.rank==1] Table=
			(localTable [:self.rect && self.zeroBased && self.rank==1])
			new localTable [UNIQUE] (point p) { return new localTable(tableSize); };

		//finish ateach (point p:UNIQUE) Table[p]=new localTable(tableSize);

		double GUPs;
		double cputime;               /* CPU time to update table */

		/* Print parameters for run */
		if (doIO) {
			System.out.println("Distributed table size   = 2^" +logTableSize + "*"+NUMPLACES+" = " +
					tableSize*NUMPLACES+ " words");
			System.out.println("Number of total updates = " + (4*tableSize*NUMPLACES)+ "");
		}

		/* Begin timing here */
		cputime = -mysecond();

		//RandomAccessUpdate(logTableSize, embarrassing, Table );

		/* Perform updates to main table.  The scalar equivalent is:
		 *
		 *     long ran;
		 *     ran = 1;
		 *     for (i=0; i<(4 * TableSize); i++) {
		 *       ran = (ran << 1) ^ (((long) ran < 0) ? POLY : 0);
		 *       table[ran & (TableSize-1)] ^= ran;
		 *     }
		 */

		System.out.println ("The mode of update (0 = update; 1 = parallel verification; 2 = update and sequential verification): "+VERIFY);

		final long LogTableSize = logTableSize;
		final boolean Embarrassing = embarrassing;
		final long NumUpdates=tableSize*4;

		if (VERIFY == VERIFICATION_P)
			finish ateach(point [p] : UNIQUE) {
				long ran=HPCC_starts (p*NumUpdates);
				for (long i=0; i<NumUpdates; i++) {
					final int placeID;
					if (Embarrassing)
						placeID=p;
					else
						placeID=(int)((ran>>LogTableSize) & PLACEIDMASK);
					final long temp=ran;
					async (UNIQUE[placeID]) Table[placeID].verify(temp);
					ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
				}
			}
		else
			finish ateach(point [p] : UNIQUE) {
				long ran=HPCC_starts (p*NumUpdates);
				for (long i=0; i<NumUpdates; i++) {
					final int placeID;
					if (Embarrassing)
						placeID=p;
					else
						placeID=(int)((ran>>LogTableSize) & PLACEIDMASK);
					final long temp=ran;
					async (UNIQUE[placeID]) Table[placeID].update(temp);
					ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
				}
			}

		/* End timed section */
		cputime += mysecond();

		if (VERIFY == UPDATE_AND_VERIFICATION) {
			//Verify(logTableSize, embarrassing, Table);
			System.out.println("Verifying result by repeating the update sequentially...");

			finish for(point [p] : UNIQUE) {
				long ran=HPCC_starts (p*NumUpdates);
				for (long i=0; i<NumUpdates; i++) {
					final int placeID;
					if (Embarrassing)
						placeID=p;
					else
						placeID=(int)((ran>>LogTableSize) & PLACEIDMASK);
					final long temp=ran;
					//async (UNIQUE[placeID]) Table[placeID].update(temp);
					async (UNIQUE[placeID]) {
						Table[placeID].array[(int)(temp & Table[placeID].mask)] ^= temp;
					}
					ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
				}
			}
		}

		/* make sure no division by zero */
		GUPs = (cputime > 0.0 ? 1.0 / cputime : -1.0);
		GUPs *= 1e-9*(4 * tableSize*NUMPLACES);
		/* Print timing results */
		if (doIO) {
			System.out.println("    CPU time used  = "+cputime+" seconds");
		}
		if (VERIFY == UPDATE) System.out.println("    "+GUPs + " Billion(10^9) Updates    per second [GUP/s]");

		//if (VERIFY) verify(logTableSize, embarrassing, Table);
		if (VERIFY > 0) {
			final long [] SUM = new long [NUMPLACES];
			finish ateach(point [p] : UNIQUE) {
				long sum=0;
				//for (int i=0;i<Table[p].tableSize;i++) sum+=Table[p].array[i];
				for (int i=0;i<tableSize;i++) sum+=Table[p].array[i]; //can't make i be long in X10
				//System.out.println("local sum at "+p+" is "+sum+" (correct=0)");
				final long temp = sum;
				async (UNIQUE[0]) SUM[p]=temp;
			}
			long globalSum=0;
			for (int i=0; i<NUMPLACES; i++) globalSum+=SUM[i];
			if (VERIFY == VERIFICATION_P) {
				double missedUpdateRate = (globalSum-numUpdates)/(double)numUpdates*100;
				System.out.println("    The rate of missed updates  "+ missedUpdateRate+ "%");
			}
			else {
				System.out.println("    The global sum is "+globalSum+" (correct=0)");
			}
		}
	}
}
