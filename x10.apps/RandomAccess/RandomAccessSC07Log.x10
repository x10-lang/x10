/** HPC Challenge Benchmark
    (c) IBM Corporation, 2007
    @author tong
    @author vj
    Code based on ...
 */
class RandomAccessSC07Log {
    static double mySecond() {
    	return (double) ((double)(System.nanoTime() / 1000) * 1.e-6);
    }

    final static long POLY = 0x0000000000000007;
    final static long PERIOD = 1317624576693539401L;
    final static int NUMPLACES = place.MAX_PLACES;;
    final static int PLACEIDMASK = NUMPLACES-1;

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

    static void RandomAccessUpdate(final int logLocalTableSize, final long[:self.rect && self.zeroBased && self.rank==1] Table) {
    	finish ateach(point [p] : dist.UNIQUE) {
    		final long localTableSize = 1<<logLocalTableSize;
    		final long TableSize = localTableSize*NUMPLACES;
    		final long mask = TableSize-1;
    		final long NumUpdates = 4*localTableSize;
    		
    		long ran = HPCC_starts(p*NumUpdates);
    		for (long i=0; i<NumUpdates; i++) {
		      final long temp=ran;
		      final int index = (int)(temp & mask);
		      async (Table.distribution[index]) Table[index] ^= temp;
		      ran = (ran << 1)^((long) ran < 0 ? POLY : 0);
    		}
    	}
    }

    public static void  main(String[] args) {
    	
    	int  logLocalTableSize= 10;
        for (int q = 0; q < args.length; ++q) {
            if (args[q].equals("-m")) {
                ++q;
                logLocalTableSize = java.lang.Integer.parseInt(args[q]);
            }
        }
        /* calculate the size of update array (must be a power of 2) */
        final int LogLocalTableSize = logLocalTableSize, LocalTableSize = 1<<logLocalTableSize;
        final int TableSize = LocalTableSize*NUMPLACES; //TableSize should be long.
        final region(:zeroBased&&rank==1&&rect) R = [0:TableSize-1]; //can't handle long at this moment
        final dist(:zeroBased&&rank==1&&rect) DD = (dist(:zeroBased&&rank==1&&rect))dist.factory.block(R);
        final long[:self.rect && self.zeroBased && self.rank==1] Table = new long[DD] (point [i]) {return i;};

        System.out.println("Main table size   = 2^" +LogLocalTableSize + "*"+NUMPLACES+" = " + TableSize+ " words");
        System.out.println("Number of updates = " + (4*TableSize)+ "");

        double cpuTime = -mySecond();  /* CPU time to update table */
        RandomAccessUpdate(LogLocalTableSize, Table );
        cpuTime += mySecond();

        double GUPs = (cpuTime > 0.0 ? 1.0 / cpuTime : -1.0)*4*TableSize/1e9;
        System.out.println("CPU time used  = "+cpuTime+" seconds");
        System.out.println(GUPs + " Billion(10^9) Updates    per second [GUP/s]");

        // The RandomAccessUpdate operation is reliable. Repeat it. Now you should get back
        // the original array, since y xor x xor x = y. 
        RandomAccessUpdate( LogLocalTableSize, Table );
        
        /*long temp = new long[dist.UNIQUE](point [p]) {
            long err = 0;
            for(point [q] : (Table | here))
            	if (Table[q] != q) err++;
            return err;
        }.reduce();*/ //fail to compile
        
    	final long [.] ERR = new long [dist.UNIQUE] (point [p]){
		long err = 0;
		for (point [q]: (Table | here))
			if (Table[q] != q) err++;
		return err;
	};
	long temp=0; for (int i=0; i<NUMPLACES; i++) temp+=ERR[i];
        System.out.println("Found " +temp+  " errors in " +TableSize+ " locations");
        if (temp > 0.01*TableSize) System.err.println("failure");
    } 
}

