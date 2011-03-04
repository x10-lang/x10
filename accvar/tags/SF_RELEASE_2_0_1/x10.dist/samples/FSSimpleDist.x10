/**
 * Version of Stream with a collection of local arrays implementing a
 * global array.
 *
 * @seealso Stream
 * @author vj
 * @author bdlucas
 */

import x10.util.Timer;
import x10.io.Console;

public class FSSimpleDist {

    const MEG = 1024*1024;
    const alpha = 3.0D;

    const NUM_TIMES = 10;

    const DEFAULT_SIZE = MEG / 8;

    const NUM_PLACES = Place.MAX_PLACES;

    public static def main(args:Rail[String]!){here == Place.FIRST_PLACE} {
        val verified: Rail[boolean]! = [true];
        val times = Rail.make[double](NUM_TIMES);
        val N0 = args.length>0? int.parseInt(args(0)) : DEFAULT_SIZE;
        val N = N0 * NUM_PLACES;
        val localSize =  N0;

        Console.OUT.println("localSize=" + localSize);

        finish {

            for (var pp:int=0; pp<NUM_PLACES; pp++) {

                val p = pp;
                
                async(Place.places(p)) {
                    
                    val a = Rail.make[double](localSize);
                    val b = Rail.make[double](localSize);
                    val c = Rail.make[double](localSize);
                    
                    for (var i:int=0; i<localSize; i++) {
                        b(i) = 1.5 * (p*localSize+i);
                        c(i) = 2.5 * (p*localSize+i);
                    }
                    
                    for (var j:int=0; j<NUM_TIMES; j++) {
                        if (p==0) times(j) = -now(); 
                        for (var i:int=0; i<localSize; i++)
                            a(i) = b(i) + alpha*c(i);
                        if (p==0) times(j) = times(j) + now();
                    }
                    
                    // verification
                    for (var i:int=0; i<localSize; i++)
                        if (a(i) != b(i) + alpha*c(i)) 
                            async(Place.FIRST_PLACE)
                                verified(0) = false;
                }
            }
        }

        var min:double = 1000000;
        for (var j:int=0; j<NUM_TIMES; j++)
            if (times(j) < min)
                min = times(j);
        printStats(N, min, verified(0));
    }

    static def now():double = Timer.nanoTime() * 1e-9;

    static def printStats(N:int, time:double, verified:boolean) {
        val size = (3*8*N/MEG);
        val rate = (3*8*N) / (1.0E9*time);
        Console.OUT.println("Number of places=" + NUM_PLACES);
        Console.OUT.println("Size of arrays: " + size +" MB (total)" + size/NUM_PLACES + " MB (per place)");
        Console.OUT.println("Min time: " + time + " rate=" + rate + " GB/s");
        Console.OUT.println("Result is " + (verified ? "verified." : "NOT verified."));
    }                                
}
