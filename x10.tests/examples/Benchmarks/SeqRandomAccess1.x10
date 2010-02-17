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

import x10.io.Console;

class SeqRandomAccess1 extends Benchmark {

    //
    // parameters
    //

    const PARALLELISM = 2;
    const logLocalTableSize = 16;
    
    def expected() = 0.0;
    def operations() = 1.0 * numUpdates;

    //
    // the benchmark
    //

    const localTableSize = 1 << logLocalTableSize;
    const tableSize = PARALLELISM * localTableSize;
    const numUpdates = 4 * tableSize;
    const placeMask = PARALLELISM - 1;

    const POLY = 0x0000000000000007L;
    const PERIOD = 1317624576693539401L;

    final class LocalTable {
    
        val a: Rail[long]!;
        val mask: int;
        
        def this(size:int) {
            mask = size-1;
            a = Rail.make[long](size, (i:int) => i as long);
        }
        
        final def update(ran:long) {
            //a(ran&mask as int) ^= ran;
            val index = ran&mask as int;
            a(index) = a(index) ^ ran;
        }
    }

    val tables = ValRail.make[LocalTable](PARALLELISM,
        (p:int) => new LocalTable(localTableSize));

    final static def HPCCStarts(var n:long): long {
        var i:int, j:int;
        val m2 = Rail.make[long](64);
        while (n < 0) n += PERIOD;
        while (n > PERIOD) n -= PERIOD;
        if (n == 0) return 0x1L;
        var temp:long = 0x1;
        for (i=0; i<64; i++) {
            m2(i) = temp;
            temp = (temp << 1) ^ (temp < 0 ? POLY : 0L);
            temp = (temp << 1) ^ (temp < 0 ? POLY : 0L);
        }
        for (i=62; i>=0; i--) if (((n >> i) & 1) != 0) break;
        var ran:long = 0x2;
        while (i > 0) {
            temp = 0;
            for (j=0; j<64; j++) if (((ran >> j) & 1) != 0) temp ^= m2(j);
            ran = temp;
            i -= 1;
            if (((n >> i) & 1) != 0)
                ran = (ran << 1) ^ (ran < 0 ? POLY : 0);
        }
        return ran;
    }

    final def randomAccessUpdate(tables: ValRail[LocalTable]) {
        for (var p:int=0; p<PARALLELISM; p++) {
            var ran:long = HPCCStarts(p* (numUpdates/PARALLELISM));
            for (var i:long=0; i<numUpdates/PARALLELISM; i++) {
                val placeId = ((ran>>logLocalTableSize) & placeMask) as int;
                tables(placeId).update(ran);
                ran = (ran << 1) ^ (ran<0L ? POLY : 0L);
            }
        }
    }

    var first:boolean = true;

    public def once() {

        // do the updates
        randomAccessUpdate(tables);

        // First time through do verfification. The test by design
        // runs without synchronization and is allowed .01*tableSize errors
        if (first) {
            randomAccessUpdate(tables);
            var errors:int = 0;
            for (var p:int=0; p<PARALLELISM; p++) {
                val table = tables(p) as LocalTable!;
                for (var j:int=0; j<table.a.length; j++)
                    if (table.a(j) != j)
                        errors++;
            }
            first = false;
            x10.io.Console.OUT.printf("%d error(s); allowed %d\n", errors, tableSize/100);
            return (errors * 100 / tableSize) as double; // <.01*tableSize counts as 0
        } else
            return 0.0;

    }

    //
    //
    //

    public static def main(Rail[String]) {
        new SeqRandomAccess1().execute();
    }

}
