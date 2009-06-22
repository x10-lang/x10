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


/* Number of updates to table (suggested: 4x number of table entries) */
class RandomAccess {
   
    static double mysecond() {
        return (double) ((double)(System.nanoTime() / 1000) * 1.e-6);
    }

    static final int W = 128; /* Width of current random numbers */
    static final region R = [0:W-1];
    static final dist D = dist.factory.block(R);

    private final static long POLY = 0x0000000000000007;
    private final static long PERIOD = 1317624576693539401;

    /* Utility routine to start random number generator at Nth step */
    static long HPCC_starts(long n) {
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

    static void RandomAccessUpdate(final long TableSize, final long[:self.rect && self.zeroBased && self.rank==1] Table) {
        final long[:self.rect && self.zeroBased && self.rank==1] ran =
            (long[:self.rect && self.zeroBased && self.rank==1]) new long[D];              /* Current random numbers */

        /* Initialize main table */
        for (int i=0; i<TableSize; i++) Table[i] = i;

        /* Perform updates to main table.  The scalar equivalent is:
         *
         *     long ran;
         *     ran = 1;
         *     for (i=0; i<(4 * TableSize); i++) {
         *       ran = (ran << 1) ^ (((long) ran < 0) ? POLY : 0);
         *       table[ran & (TableSize-1)] ^= ran;
         *     }
         */
        for (int j=0; j<W; j++)
            ran[j] = HPCC_starts (((4 * TableSize)/W) * j);

	finish ateach(point p : dist.factory.unique()) {
	    final region myR = (D | here).region;
	    for (int i=0; i<(4 * TableSize)/W; i++) {
                for (int j = myR.rank(0).low(); j <= myR.rank(0).high(); ++j) {
                    ran[j] = (ran[j] << 1) ^ ((long) ran[j] < 0 ? POLY : 0);
                    Table[(int)(ran[j] & (TableSize-1))] ^= ran[j];
                }
	    }
	}
    }

    public static void  main(String[] args) {
        boolean doIO = false;
        double totalMem = 1024.0*1024.0;
        for (int q = 0; q < args.length; ++q) {
            if (args[q].equals("-o")) {
                doIO = true;
            }
            if (args[q].equals("-m")) {
                ++q;
                totalMem = java.lang.Double.parseDouble(args[q]);
            }
        }

        long i;
        long temp;
        double cputime;               /* CPU time to update table */
        long[:self.rect && self.zeroBased && self.rank==1] Table;
        int logTableSize, TableSize;
        double GUPs;

        /* calculate local memory per node for the update table */
        totalMem /= 8;

        /* calculate the size of update array (must be a power of 2) */
        for (totalMem *= 0.5, logTableSize = 0, TableSize = 1;
             totalMem >= 1.0;
             totalMem *= 0.5, logTableSize++, TableSize <<= 1)
            ; /* EMPTY */

        region R = [0:TableSize-1];
        Table = (long[:self.rect && self.zeroBased && self.rank==1]) new long[R];

        /* Print parameters for run */
        if (doIO) {
            System.out.println("Main table size   = 2^" +logTableSize + " = " +
                               TableSize+ " words");
            System.out.println("Number of updates = " + (4*TableSize)+ "");
        }
        RandomAccessUpdate(10, (long[:self.rect && self.zeroBased && self.rank==1]) new long[[0:9]]);//Compile up front???
        /* Begin timing here */
        cputime = -mysecond();

        RandomAccessUpdate( TableSize, Table );

        /* End timed section */
        cputime += mysecond();
    
        /* make sure no division by zero */
        GUPs = (cputime > 0.0 ? 1.0 / cputime : -1.0);
        GUPs *= 1e-9*(4 * TableSize);
        /* Print timing results */
        if (doIO) {
            System.out.println("CPU time used  = "+cputime+" seconds");
            System.out.println(GUPs + " Billion(10^9) Updates    per second [GUP/s]");
        }
        System.out.println(GUPs + " Billion(10^9) Updates    per second [GUP/s]");

        /* Verification of results (in serial or "safe" mode; optional) */
        temp = 0x1;
        for (i=0; i<(4 * TableSize); i++) {
            temp = (temp << 1) ^ (((long) temp < 0) ? POLY : 0);
            Table[(int)(temp & (TableSize-1))] ^= temp;
        }
    
        temp = 0;
        for (int ii=0; ii<TableSize; ii++)
            if (Table[ii] != ii)
                temp++;
    
        if (doIO) {
            System.out.println("Found " +temp+
                               " errors in " +TableSize+ " locations");
        }
        if (temp > 0.01*TableSize) {
            System.err.println("failure");
        }

    }
}
