import x10.util.Timer;
import x10.compiler.Immediate;

class RandomAccessOrig {

    const POLY = 0x0000000000000007L;
    const PERIOD = 1317624576693539401L;
    const NTASKS = 8;

    // Utility routine to start random number generator at Nth step
    static def HPCC_starts(var n:Long): Long {
        var i:Int, j:Int;
        val m2 = Rail.make[Long](64);
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

    static def runBenchmark(rails: ValRail[Rail[Long]],
        logLocalTableSize: Int, numUpdates: Long) {
        val mask = (1<<logLocalTableSize)-1;
        val local_updates = numUpdates / NTASKS;
        finish for ((p) in 0..NTASKS-1) {
            async 
            @Immediate finish {
                var ran:Long = HPCC_starts(p*(numUpdates/NTASKS));

                for (var i:Long=0 ; i<local_updates ; ++i) {
                    val task_id = ((ran>>logLocalTableSize) & (NTASKS-1)) as Int;
                    val index = (ran & mask as Int);
                    val update = ran;
                   
                    val dest = task_id;
                    val rail = rails(task_id) as Rail[Long]!;
                    @Immediate async {
                        atomic rail(index) ^= update;
                    } 
                    ran = (ran << 1) ^ (ran<0L ? POLY : 0L);
                }
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

    public static def main(args:Rail[String]!) {

        if ((NTASKS & (NTASKS-1)) > 0) {
            Console.ERR.println("The number of tasks must be a power of 2.");
            return;
        }

        var logLocalTableSize_ : Int = 12;
        var updates_ : Int = 4;

        // parse arguments
        for (var i:Int=0 ; i<args.length ; ) {
            if (args(i).equals("-m")) {
                i++;
                if (i >= args.length) {
                    if (here.id==0)
                        Console.ERR.println("Too few cmdline params.");
                    help(true);
                    return;
                }
                logLocalTableSize_ = Int.parseInt(args(i++));
            } else if (args(i).equals("-u")) {
                i++;
                if (i >= args.length) {
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
        val tableSize = (localTableSize as Long)*NTASKS;
        val numUpdates = updates_*tableSize;

        // create local rails
        val rails_ = Rail.make[Rail[Long]!](NTASKS); //, (p:Int) => null);
        
        finish for ((p) in 0..NTASKS-1) {
            async {
                val tmp = Rail.make(localTableSize, (i:Int)=>i as Long);
                 rails_(p) = tmp as Rail[Long]!;
            }
        }
        val rails = rails_ as ValRail[Rail[Long]];

        // print some info
        Console.OUT.println("Main table size:   2^"+logLocalTableSize+"*"+NTASKS
                                       +" == "+tableSize+" words");
        Console.OUT.println("Number of tasks:  " + NTASKS);
        Console.OUT.println("Number of updates: " + numUpdates);

        // time it
        var cpuTime:Double = -Timer.nanoTime() * 1e-9D;
        runBenchmark(rails, logLocalTableSize, numUpdates);
        cpuTime += Timer.nanoTime() * 1e-9D;

        // print statistics
        val GUPs = (cpuTime > 0.0 ? 1.0 / cpuTime : -1.0) * numUpdates / 1e9;
        Console.OUT.println("CPU time used: "+cpuTime+" seconds");
        Console.OUT.println(GUPs+" Billion(10^9) Updates per second (GUP/s)");

        // repeat for testing.
        runBenchmark(rails, logLocalTableSize, numUpdates);
        finish for ((i) in 0..NTASKS-1) {
            async {
                val rail = rails(i) as Rail[Long]!;
                var err:Int = 0;
                for (var j:Int=0; j<rail.length; j++)
                    if (rail(j) != j) err++;
                Console.OUT.println("Found " + err + " errors.");
            }
        }
    }
}

