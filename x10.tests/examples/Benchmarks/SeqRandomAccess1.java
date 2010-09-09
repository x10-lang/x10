/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

class SeqRandomAccess1 extends Benchmark {

    //
    // parameters
    //

    static int PARALLELISM = 2;
    static int logLocalTableSize = 16;
    
    double expected() {return 0.0;}
    double operations() {return 1.0 * numUpdates;}

    //
    // the benchmark
    //

    static int localTableSize = 1 << logLocalTableSize;
    static int tableSize = PARALLELISM * localTableSize;
    static int numUpdates = 4 * tableSize;
    static int placeMask = PARALLELISM - 1;

    static long POLY = 0x0000000000000007L;
    static long PERIOD = 1317624576693539401L;
    
    static final class LocalTable {
    
        final long [] a;
        final int mask;

        LocalTable(int size) {
            this.mask = size-1;
            this.a = new long[size];
            for (int i=0; i<size; i++)
                a[i] = i;
        }
        
        final void update(long ran) {
            //a(ran&mask to int) ^= ran;
            int index = (int)(ran&mask);
            a[index] ^= ran;
        }
    }

    LocalTable [] tables = new LocalTable[PARALLELISM];

    {
        for (int i=0; i<PARALLELISM; i++)
            tables[i] = new LocalTable(localTableSize);
    }

    final static long HPCCStarts(long n) {
        int i, j;
        long [] m2 = new long[64];
        while (n < 0) n += PERIOD;
        while (n > PERIOD) n -= PERIOD;
        if (n == 0) return 0x1L;
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
                ran = (ran << 1) ^ (ran < 0 ? POLY : 0);
        }
        return ran;
    }

    final void randomAccessUpdate(LocalTable [] tables) {
        for (int p=0; p<PARALLELISM; p++) {
            long ran = HPCCStarts(p * (numUpdates/PARALLELISM));
            for (long i=0; i<numUpdates/PARALLELISM; i++) {
                int placeId = (int) (((ran>>logLocalTableSize) & placeMask));
                tables[placeId].update(ran);
                ran = (ran << 1) ^ (ran<0L ? POLY : 0L);
            }
        }
    }

    boolean first = true;

    public double once() {

        // do the updates
        randomAccessUpdate(tables);

        // First time through do verfification. The test by design
        // runs without synchronization and is allowed .01*tableSize errors
        if (first) {
            randomAccessUpdate(tables);
            int errors = 0;
            for (int p=0; p<PARALLELISM; p++) {
                LocalTable table = tables[p];
                for (int j=0; j<table.a.length; j++)
                    if (table.a[j] != j)
                        errors++;
            }
            first = false;
            System.out.printf("%d error(s); allowed %d\n", errors, tableSize/100);
            return (double)(errors * 100 / tableSize); // <.01*tableSize counts as 0
        } else
            return 0.0;

    }

    //
    //
    //

    public static void main(String [] args) {
        new SeqRandomAccess1().run();
    }

}
