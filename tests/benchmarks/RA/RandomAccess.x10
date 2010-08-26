/*(C) Copyright IBM Corp. 2007*/
package ra;
class RandomAccess {
	static double mySecond() { return (double) ((double)(System.nanoTime() / 1000) * 1.e-6);}
	const long POLY = 0x0000000000000007, PERIOD = 1317624576693539401L;
	const int NUM_PLACES = place.MAX_PLACES, PLACE_ID_MASK = NUM_PLACES-1;
	/* Utility routine to start random number generator at Nth step */
	static long HPCC_starts(long n) {
		int i, j;
		long[] m2 = new long[64];
		while (n < 0) n += PERIOD;
		while (n > PERIOD) n -= PERIOD;
		if (n == 0) return 0x1;
		long temp = 0x1;
		for (i=0; i<64; i++) {
			m2[i] = temp;
			temp = (temp << 1) ^ (temp < 0 ? POLY : 0L);
			temp = (temp << 1) ^ (temp < 0 ? POLY : 0L);
		}
		for (i=62; i>=0; i--) if (((n >> i) & 1) != 0) break;
		long ran = 0x2;
		while (i > 0) {
			temp = 0;
			for (j=0; j<64; j++) if (((ran >> j) & 1) != 0) temp ^= m2[j];
			ran = temp;
			i -= 1;
			if (((n >> i) & 1) != 0)
				ran = (ran << 1) ^ ((long) ran < 0 ? POLY : 0);
		}
		return ran;
	}
	static void RandomAccessUpdate(final int logLocalTableSize, final long[:rail] Table) {
		finish ateach(point [p] : dist.UNIQUE) {
			final long localTableSize=1<<logLocalTableSize,
			           TableSize=localTableSize*NUM_PLACES,
			           mask=TableSize-1,
			           NumUpdates=4*localTableSize;
			long ran=HPCC_starts(p*NumUpdates);
			for (long i=0; i<NumUpdates; i++) {
				final long temp=ran;
				final int index = (int)(temp & mask);
				@aggregate async (dist.UNIQUE[index/(int)(TableSize/NUM_PLACES)])
				    atomic Table[index] ^= temp;
				ran = (ran << 1)^((long) ran < 0 ? POLY : 0);
			}
		}
	}
	public static void  main(String[] args) {
		int  logLocalTableSize= 10;
		if (args.length > 1 && args[0].equals("-m")) {
			logLocalTableSize = java.lang.Integer.parseInt(args[1]);
		}
		/* calculate the size of update array (must be a power of 2) */
		final int LogLocalTableSize = logLocalTableSize;
		final long LocalTableSize = 1<<logLocalTableSize,
		           TableSize = LocalTableSize*NUM_PLACES;
		final region(:rail) R = [0:(int)TableSize-1];
		final dist(:rail) D = dist.factory.block(R);
		final long[:rail] Table = new long[D] (point [i]) {return i;};
		System.out.println("Main table size   = 2^" +LogLocalTableSize + "*"
				+NUM_PLACES+" = " + TableSize+ " words");
		System.out.println("Number of places = " + NUM_PLACES);
		System.out.println("Number of updates = " + (4*TableSize)+ "");
		double cpuTime = -mySecond();  
		RandomAccessUpdate(LogLocalTableSize, Table );
		cpuTime += mySecond();
		double GUPs = (cpuTime > 0.0 ? 1.0 / cpuTime : -1.0)*4*TableSize/1e9;
		System.out.println("CPU time used  = "+cpuTime+" seconds");
		System.out.println(GUPs + " Billion(10^9) Updates per second [GUP/s]");
		RandomAccessUpdate( LogLocalTableSize, Table ); // repeat for testing.
		final long[.] t  = new long[dist.UNIQUE](point [p]) {
			long err = 0;
			for(point [q]:Table.distribution|here) if (Table[q] != q) err++;
			return err;
		};
		long temp = t.sum();
		System.out.println("Found " +temp+  " errors in " +TableSize+ " locations.");
		if (temp > 0.01*TableSize) System.err.println("Failure.");
	}
}