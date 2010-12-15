import x10.util.Timer;
import clocked.*;

public class Stream {

    const MEG = 1024*1024;
    const alpha = 3.0D;

    const NUM_TIMES = 40;

    const DEFAULT_SIZE = MEG * 2;

    const NUM_PLACES = 64; //Place.MAX_PLACES;

    public static def main(args:Rail[String]!){here == Place.FIRST_PLACE} {
    	val start_time = System.currentTimeMillis(); 
     finish {
        val clk = Clock.make();
        val opV = Boolean.&;
    	val opT = Math.noOp.(Double, Double);
        shared var verified: boolean @ Clocked [Boolean] (clk, opV, true) = true;
        val times= Rail.make[double @ Clocked [Double] (clk, opT, 0.0)](NUM_TIMES, (int)=>0.0D);
        val N0 = args.length>0? int.parseInt(args(0)) : DEFAULT_SIZE;
        val N = (N0 as long) * NUM_PLACES;
        val localSize =  N0;

        Console.OUT.println("localSize=" + localSize);

         

            for (var pp:int=0; pp<NUM_PLACES; pp++) {

                val p = pp;

                 async (here) clocked (clk) {

                    val a: Rail[double]! = Rail.make[double](localSize);
                    val b: Rail[double]! = Rail.make[double](localSize);
                    val c: Rail[double]! = Rail.make[double](localSize);

                    for (var i:int=0; i<localSize; i++) {
                        b(i) = 1.5 * (p*localSize+i);
                        c(i) = 2.5 * (p*localSize+i);
                    }

                    val beta = alpha;
		    var start:double = 0.0D;
                    for (var j:int=0; j<NUM_TIMES; j++) {
                        if (p==0)  {start = -now();}
                        for (var i:int=0; i<localSize; i++)
                            a(i) = b(i) + beta*c(i);
                        if (p==0)  {times(j) = start + now();}
                    }

                    // verification
                    for (var i:int=0; i<localSize; i++)
                        if (a(i) != b(i) + alpha*c(i))
                           
                                verified = false;
                            
                }
            }
        
        next;
        var min:double = 1000000;
        for (var j:int=0; j<NUM_TIMES; j++) {
        	
            if (times(j) < min)
                min = times(j);
        }
        printStats(N, min);
        Console.OUT.println("Result is " + (verified ? "verified." : "NOT verified."));
      }
    val compute_time = (System.currentTimeMillis() - start_time);
    Console.OUT.println( compute_time + " ");
    }

    static def now():double = Timer.nanoTime() * 1e-9;

    static def printStats(N:long, time:double) {
        val size = (3*8*N/MEG);
        val rate = (3*8*N) / (1.0E9*time);
        Console.OUT.println("Number of places=" + NUM_PLACES);
        Console.OUT.println("Size of arrays: " + size +" MB (total)" + size/NUM_PLACES + " MB (per place)");
        Console.OUT.println("Min time: " + time + " rate=" + rate + " GB/s");
    }
}

                    
