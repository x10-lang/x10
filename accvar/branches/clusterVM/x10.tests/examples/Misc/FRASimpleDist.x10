import x10.compiler.Native;
import x10.compiler.NativeRep;

import x10.io.Console;

import x10.util.Timer;
import x10.runtime.NativeRuntime;

value LocalTable {
    
    val a: Rail[long];
    val mask: int;
    
    def this(size:int) {
        mask = size-1;
        a = Rail.makeVar[long](size, (i:nat)=>i as long);
    }
    
    public def update(ran:long) {
        //a(ran&mask as int) ^= ran;
        val index = ran&mask as int;
        a(index) = a(index) ^ ran;
    }
}

class FRASimpleDist {

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
        tables: ValRail[LocalTable]
    ) {
        finish for (var p:int=0; p<Place.MAX_PLACES; p++) {
            val valp = p;
            async (Place.places(p)) {
                var ran:long = HPCC_starts(valp*(NUM_UPDATES/NUM_PLACES));
                for (var i:long=0; i<NUM_UPDATES/NUM_PLACES; i++) {
                    val placeId = ((ran>>logLocalTableSize) & PLACE_ID_MASK) as int;
                    val valran = ran;
                    val table = tables(placeId);
                    async (Place.places(placeId)) {
                        table.update(valran);
                    }
                    ran = (ran << 1) ^ (ran<0L ? POLY : 0L);
                }
            }
        }
    }


    public def run():boolean {
        if ((NUM_PLACES & (NUM_PLACES-1)) > 0) {
            println("The number of places must be a power of 2.");
            return false;
        }

        // calculate the size of update array (must be a power of 2)
        val logLocalTableSize = 12;
        val localTableSize = 1<<logLocalTableSize;
        val tableSize = localTableSize*NUM_PLACES;
        val NUM_UPDATES = 4*tableSize;

        // create local tables
	val tableMaker = (x:Int)=> 
	    at(Place.places(x)){new LocalTable(localTableSize)};
        val tables = Rail.makeVal[LocalTable](Place.MAX_PLACES, tableMaker);

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
        println("CPU time used  = "+cpuTime+" seconds");
        println(GUPs+" Billion(10^9) Updates per second (GUP/s)");

        // repeat for testing.
        randomAccessUpdate(NUM_UPDATES, logLocalTableSize, tables);
	val result = Array.make[Int](Dist.makeUnique(), (Point)=>0);
        for ((i):Point(1) in  0..Place.MAX_PLACES-1) {
            val table = tables(i);
            async (Place.places(i)) {
                for ((j):Point(1) in 0 .. table.a.length-1)
                    if (table.a(j) != j) {
			result(i)++;
			println("Found error at j=" + j + " " + table.a(j));
		    }
            }
        }
	return result.reduce(Int.+,0)==0;
    }

    static def now() = Timer.nanoTime() * 1e-9D;

    static def println(s:String) = x10.io.Console.OUT.println(s);

}
