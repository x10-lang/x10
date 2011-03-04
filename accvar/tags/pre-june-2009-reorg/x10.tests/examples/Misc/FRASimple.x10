import x10.compiler.Native;
import x10.compiler.NativeRep;

import x10.util.Timer;
import x10.runtime.NativeRuntime;

value LocalTable {
    
    val a: Rail[long];
    val mask: int;
    
    def this(size:int) {
        mask = size-1;
        a = Rail.makeVar[long](size);
        for (var i:int=0; i<size; i++)
            a(i) = i as long;
    }
    
    public def update(ran:long) {
        //a(ran&mask as int) ^= ran;
        val index = ran&mask as int;
        a(index) = a(index) ^ ran;
    }
}


class FRASimple {

    @Native("java", "x10.io.Console.OUT.println(#1)")
    @Native("c++", "printf(\"%s\\n\", (#1)->c_str()); fflush(stdout)")
    public static native def println(x:String):void;

    @Native("java", "x10.io.Console.OUT.printf(#1,#2)")
    @Native("c++", "printf((#1)->c_str(), #2); fflush(stdout)")
    public static native def printf(x:String, o:Object):void;

    const POLY = 0x0000000000000007L;
    const PERIOD = 1317624576693539401L;
    const NUM_PLACES = NativeRuntime.MAX_PLACES;
    const PLACE_ID_MASK = NUM_PLACES-1;

    // Utility routine to start random number generator at Nth step
    static def HPCC_starts(var n:long): long {
        var i:int, j:int;
        val m2 = Rail.makeVar[long](64);
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

    static def randomAccessUpdate(
        NUM_UPDATES: long,
        logLocalTableSize: long,
        tables: Rail[LocalTable]
    ) {
        for (var p:int=0; p<NUM_PLACES; p++) {
            var ran:long = HPCC_starts(p*(NUM_UPDATES/NUM_PLACES));
            for (var i:long=0; i<NUM_UPDATES/NUM_PLACES; i++) {
                val placeId = ((ran>>logLocalTableSize) & PLACE_ID_MASK) as int;
                tables(placeId).update(ran);
                ran = (ran << 1) ^ (ran<0L ? POLY : 0L);
            }
        }
    }


    public static def main(args:Rail[String]) {

        if ((NUM_PLACES & (NUM_PLACES-1)) > 0) {
            println("The number of places must be a power of 2.");
            return;
        }

        // calculate the size of update array (must be a power of 2)
        val logLocalTableSize = args.length > 1 && args(0).equals("-m")?
            int.parseInt(args(1)) : 10;
        val localTableSize = 1<<logLocalTableSize;
        val tableSize = localTableSize*NUM_PLACES;
        val NUM_UPDATES = 4*tableSize;

        // create local tables
        val tables = Rail.makeVar[LocalTable](NUM_PLACES);
        for (var i:int=0; i<NUM_PLACES; i++)
            tables(i) = new LocalTable(localTableSize);
        //println("tables dist " + tables.dist);

        // print some info
        println("Main table size   = 2^" +logLocalTableSize + "*" + NUM_PLACES+" = " + tableSize+ " words");
        println("Number of places = " + NUM_PLACES);
        println("Number of updates = " + NUM_UPDATES);

        // time it
        var cpuTime:double = -now();  
        randomAccessUpdate(NUM_UPDATES, logLocalTableSize, tables);
        cpuTime += now();

        // print statistics
        val GUPs = (cpuTime > 0.0 ? 1.0 / cpuTime : -1.0) * NUM_UPDATES / 1e9;
        printf("CPU time used  = %.2f seconds\n", cpuTime);
        printf("%.6f Billion(10^9) Updates per second (GUP/s)\n", GUPs);

        // repeat for testing.
        randomAccessUpdate(NUM_UPDATES, logLocalTableSize, tables);
        for (var i:int=0; i<NUM_PLACES; i++) {
            val l = tables(i);
            var err:int = 0;
            for (var j:int=0; j<l.a.length; j++)
                if (l.a(j) != j) err++;
            println("Found " + err + " errors.");
        }
    }

    static def now() = Timer.nanoTime() * 1e-9D;

}
