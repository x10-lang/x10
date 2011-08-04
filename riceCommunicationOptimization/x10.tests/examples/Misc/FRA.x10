value LocalTable {
    
    val a: Array[long];
    val mask: int;
    
    def this(size:int) {
        mask = size-1;
        a = Array.make[long](0..size-1, ((i):Point) => i as long);
    }
    
    public def update(ran:long) {
        a(ran&mask as int) ^= ran;
    }
}

public class FRA {

    const POLY = 0x0000000000000007L;
    const PERIOD = 1317624576693539401L;
    const NUM_PLACES = Place.MAX_PLACES;
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
        tables: Array[LocalTable]
    ) {
        finish ateach((p):Point in Dist.makeUnique()) {
            var ran:long = HPCC_starts(p*(NUM_UPDATES/NUM_PLACES));
            for (var i:long=0; i<NUM_UPDATES/NUM_PLACES; i++) {
                val placeId = ((ran>>logLocalTableSize) & PLACE_ID_MASK) as int;
                val temp = ran;
                async (Place.places(placeId))
                    tables(here.id).update(temp);
                ran = (ran << 1) ^ (ran<0L ? POLY : 0L);
            }
        }
    }


    public def run():boolean {

        // calculate the size of update array (must be a power of 2)
        val logLocalTableSize = 10;
        val localTableSize = 1<<logLocalTableSize;
        val tableSize = localTableSize*NUM_PLACES;
        val NUM_UPDATES = 4*tableSize;

        // create local tables
        val init = (p:Point) => new LocalTable(localTableSize);
        val tables = Array.make[LocalTable](Dist.makeUnique(),
            (p:Point) => new LocalTable(localTableSize));

        // time it
        var cpuTime:double = -now();  
        randomAccessUpdate(NUM_UPDATES, logLocalTableSize, tables);
        cpuTime += now();

        // repeat for testing.
        randomAccessUpdate(NUM_UPDATES, logLocalTableSize, tables);
	val result = Array.make[Int](Dist.makeUnique(), (Point)=>0);
        for (p:Place in Place.places) async(p) {
            val l = tables(p.id);
            for ((q):Point in l.a) if (l.a(q) != q) result(p.id)++;
        }
	return result.reduce(Int.+,0)==0;
    }

    static def now() = System.nanoTime() * 1e-9D;

}