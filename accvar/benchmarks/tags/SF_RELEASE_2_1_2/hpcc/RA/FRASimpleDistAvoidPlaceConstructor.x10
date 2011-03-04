import x10.util.Timer;
import x10.compiler.Immediate;
import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.util.IndexedMemoryChunk;
import x10.util.Box;

@NativeCPPInclude("pgas_collectives.h")

class FRASimpleDistAvoidPlaceConstructor {

    @Native("c++", "__pgasrt_tsp_barrier()")
    static def barrier() {}

    static POLY = 0x0000000000000007L;
    static PERIOD = 1317624576693539401L;

    // Utility routine to start random number generator at Nth step
    static def HPCC_starts(var n:Long): Long {
        var i:Int, j:Int;
        val m2 = new Array[Long](64);
        while (n < 0) n += PERIOD;
        while (n > PERIOD) n -= PERIOD;
        if (n == 0) return 0x1L;
        var temp:Long = 0x1;
        for (i=0; i<64; i++) {
            m2(i) = temp;
            temp = (temp << 1) ^ (temp < 0 ? POLY : 0L);
            temp = (temp << 1) ^ (temp < 0 ? POLY : 0L);
        }
        for (i=62; i>=0; i--) if (((n >> i) & 1) != 0) break;
        var ran:Long = 0x2;
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

    static def runBenchmark(plhimc: PlaceLocalHandle[Box[IndexedMemoryChunk[Long]]{self!=null}],
                            logLocalTableSize: Int, numUpdates: Long) {
        val mask = (1<<logLocalTableSize)-1;
        val local_updates = numUpdates / Place.MAX_PLACES;
        finish for (p in Place.places()) async at (p) {
            var ran:Long = HPCC_starts(here.id*(numUpdates/Place.MAX_PLACES));
            val imc = plhimc()();
            val size = logLocalTableSize;
            val mask1 = mask;
            val mask2 = Place.MAX_PLACES - 1;
            val poly = POLY;
            val here_id = here.id;
            val lu = local_updates;
            barrier();
            for (var i:Long=0 ; i<lu ; i+=1L) {
                val place_id = ((ran>>size) as Int) & mask2;
                val index = (ran as Int) & mask1;
                val update = ran;
                if (place_id==here_id) {
                    imc(index) ^= update;
                } else {
                    @Native("c++", "x10rt_remote_op(place_id, (x10rt_remote_ptr)&(imc->raw()[index]), X10RT_OP_XOR, update);")
                    { imc.getCongruentSibling(Place(place_id)).remoteXor(index, update); }
                }
                ran = (ran << 1) ^ (ran<0L ? poly : 0L);
            }
            barrier();
        }
    }

    private static def help (err:Boolean) {
        if (here.id!=0) return;
        val out = err ? Console.ERR : Console.OUT;
        out.println("Usage: FRASimpleDistAvoidPlaceConstructor [-m <mem>] [-u <updates>]");
        out.println("where");
        out.println("   <mem> is the log2 size of the local table (default 12)");
        out.println("   <updates> is the number of updates per element (default 4)");
    }

    public static def main (args:Array[String]{rank==1}) {

        if ((Place.MAX_PLACES & (Place.MAX_PLACES-1)) > 0) {
            Console.ERR.println("The number of places must be a power of 2.");
            return;
        }

        var logLocalTableSize_ : Int = 12;
        var updates_ : Int = 4;

        // parse arguments
        for (var i:Int=0 ; i<args.size() ; ) {
            if (args(i).equals("-m")) {
                i++;
                if (i >= args.size()) {
                    if (here.id==0)
                        Console.ERR.println("Too few cmdline params.");
                    help(true);
                    return;
                }
                logLocalTableSize_ = Int.parseInt(args(i++));
            } else if (args(i).equals("-u")) {
                i++;
                if (i >= args.size()) {
                    if (here.id==0)
                        Console.ERR.println("Too few cmdline params.");
                    help(true);
                    return;
                }
                updates_ = Int.parseInt(args(i++));
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
        val tableSize = (localTableSize as Long)*Place.MAX_PLACES;
        val numUpdates = updates_*tableSize;

        // create congruent array (same address at each place)
        val plhimc = PlaceLocalHandle.make(Dist.makeUnique(), () => new Box(IndexedMemoryChunk.allocate[Long](localTableSize, 8, true, true)) as Box[IndexedMemoryChunk[Long]]{self!=null});
        finish for (p in Place.places()) async at (p) {
            for ([i] in 0..(localTableSize-1)) plhimc()()(i) = i as Long;
        }

        // print some info
        Console.OUT.println("Main table size:   2^"+logLocalTableSize+"*"+Place.MAX_PLACES
                                       +" == "+tableSize+" words");
        Console.OUT.println("Number of places:  " + Place.MAX_PLACES);
        Console.OUT.println("Number of updates: " + numUpdates);

        // time it
        var cpuTime:Double = -Timer.nanoTime() * 1e-9D;
        runBenchmark(plhimc, logLocalTableSize, numUpdates);
        cpuTime += Timer.nanoTime() * 1e-9D;

        // print statistics
        val GUPs = (cpuTime > 0.0 ? 1.0 / cpuTime : -1.0) * numUpdates / 1e9;
        Console.OUT.println("CPU time used: "+cpuTime+" seconds");
        Console.OUT.println(GUPs+" Billion(10^9) Updates per second (GUP/s)");

        // repeat for testing.
        runBenchmark(plhimc, logLocalTableSize, numUpdates);
        finish for (p in Place.places()) async at (p) {
            var err:Int = 0;
            for ([i] in 0..(localTableSize-1)) 
                if (plhimc()()(i) != i) err++;
            Console.OUT.println(here+": Found " + err + " errors.");
        }
    }
}
