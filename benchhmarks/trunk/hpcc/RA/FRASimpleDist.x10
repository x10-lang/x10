import x10.util.Timer;
import x10.util.Team;
import x10.compiler.Immediate;

class LocalTable {
    val a: Array[long](1);
    val mask: int;

    def this(size:int) {
        mask = size-1;
        a = new Array[long](size, (p:Point(1))=>p(0) as long);
    }
    public def update(ran:long) {
        val index = (ran&mask) as int;
        a(index) = a(index) ^ ran;
    }
}

class FRASimpleDist {

    static POLY = 0x0000000000000007L;
    static PERIOD = 1317624576693539401L;

    // Utility routine to start random number generator at Nth step
    static def HPCC_starts(var n:long): long {
        var i:int, j:int;
        val m2 = new Array[long](64);
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

    static def runBenchmark(gtable: PlaceLocalHandle[LocalTable],
        logLocalTableSize: int, numUpdates: long) {
        val mask = (1<<logLocalTableSize)-1;
        val local_updates = numUpdates / Place.MAX_PLACES;
        finish for (var p:int=0; p<Place.MAX_PLACES; p++) {
            val valp = p;
            at (Place.places(p)) async {
                var ran:long = HPCC_starts(valp*(numUpdates/Place.MAX_PLACES));
                val size = logLocalTableSize;
                val mask1 = mask;
                val mask2 = Place.MAX_PLACES - 1;
                val poly = POLY;
                //Team.WORLD.barrier(here.id);
                for (var i:long=0 ; i<local_updates ; i+=1L) {
                    val place_id = ((ran>>size) as int) & mask2;
                    val ran2 = ran;

                    //val dest = Place(place_id);
                    @Immediate async at(Place.places(place_id)) {
                        gtable().update(ran2);
                    } 

                    ran = (ran << 1) ^ (ran<0L ? poly : 0L);
                }
                //Team.WORLD.barrier(here.id);
            }
        }
    }

    private static def help (err:Boolean) {
        if (here.id!=0) return;
        val out = err ? Console.ERR : Console.OUT;
        out.println("Usage: FRASimpleDist [-m <mem>] [-u <updates>]");
        out.println("where");
        out.println("   <mem> is the log2 size of the local table (default 12)");
        out.println("   <updates> is the number of updates per element (default 4)");
    }

    public static def main(args:Array[String](1)) {

        if ((Place.MAX_PLACES & (Place.MAX_PLACES-1)) > 0) {
            Console.ERR.println("The number of places must be a power of 2.");
            return;
        }

        var logLocalTableSize_ : int = 12;
        var updates_ : int = 4;

        // parse arguments
        for (var i:int=0 ; i<args.size ; ) {
            if (args(i).equals("-m")) {
                i++;
                if (i >= args.size) {
                    if (here.id==0)
                        Console.ERR.println("Too few cmdline params.");
                    help(true);
                    return;
                }
                logLocalTableSize_ = int.parse(args(i++));
            } else if (args(i).equals("-u")) {
                i++;
                if (i >= args.size) {
                    if (here.id==0)
                        Console.ERR.println("Too few cmdline params.");
                    help(true);
                    return;
                }
                updates_ = int.parse(args(i++));
            } else {
                if (here.id==0)
                    Console.ERR.println("Unrecognised cmdline param: \""+args(i)+"\"");
                help(true);
                return;
            }
        }

        // calculate the size of update array (must be a power of 2)
        val logLocalTableSize = logLocalTableSize_;
        val localTableSize = 1<<logLocalTableSize;
        val tableSize = (localTableSize as long)*Place.MAX_PLACES;
        val numUpdates = updates_*tableSize;

        // create local rails
        val gtable = PlaceLocalHandle.make[LocalTable](Dist.makeUnique(), ()=>new LocalTable(localTableSize));

        // print some info
        Console.OUT.println("Main table size:   2^"+logLocalTableSize+"*"+Place.MAX_PLACES
                                       +" == "+tableSize+" words");
        Console.OUT.println("Number of places:  " + Place.MAX_PLACES);
        Console.OUT.println("Number of updates: " + numUpdates);

        // time it
        var cpuTime:Double = -Timer.nanoTime() * 1e-9D;
        runBenchmark(gtable, logLocalTableSize, numUpdates);
        cpuTime += Timer.nanoTime() * 1e-9D;

        // print statistics
        val GUPs = (cpuTime > 0.0 ? 1.0 / cpuTime : -1.0) * numUpdates / 1e9;
        Console.OUT.println("CPU time used: "+cpuTime+" seconds");
        Console.OUT.println(GUPs+" Billion(10^9) Updates per second (GUP/s)");

        // repeat for testing.
        runBenchmark(gtable, logLocalTableSize, numUpdates);
        for (var i:int=0; i<Place.MAX_PLACES; i++) {
            at(Place.places(i)) async {
                var err:int = 0;
                val table = gtable();
                for ([j] in table.a)
                    if (table.a(j) != j) err++;
                Console.OUT.println("Found " + err + " errors.");
            }
        }
    }
}
