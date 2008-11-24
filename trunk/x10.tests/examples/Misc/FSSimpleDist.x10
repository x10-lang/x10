/**
 * Version of Stream with a collection of local arrays implementing a
 * global array.
 *
 * @seealso Stream
 * @author vj
 * @author bdlucas
 */

public class FSSimpleDist {

    const MEG = 1024*1024;
    const alpha = 3.0D;

    //const NUM_TIMES = 10;
    const NUM_TIMES = 1;

    //const DEFAULT_SIZE = 2*MEG;
    const DEFAULT_SIZE = MEG / 8;

    public static def main(args:Rail[String]) {

        val verified: Rail[boolean] = [true];
        val times = Rail.makeVar[double](NUM_TIMES);
        val N0 = args.length>0? int.parseInt(args(0)) : DEFAULT_SIZE;
        val N = N0 * Place.MAX_PLACES;
        val localSize =  N0;

        System.out.println("localSize=" + localSize);

        finish async {

            val clock = Clock.make();
            
            for (var pp:int=0; pp<Place.MAX_PLACES; pp++) {

                val p = pp;

                async(Place.places(p)) {

                    val a = Rail.makeVar[double](localSize);
                    val b = Rail.makeVar[double](localSize);
                    val c = Rail.makeVar[double](localSize);
                    
                    for (var i:int=0; i<localSize; i++) {
                        b(i) = 1.5 * (p*localSize+i);
                        c(i) = 2.5 * (p*localSize+i);
                    }
                    
                    for (var j:int=0; j<NUM_TIMES; j++) {
                        if (p==0) times(j) = -now(); 
                        for (var i:int=0; i<localSize; i++)
                            a(i) = b(i) + alpha*c(i);
                        next; 
                        if (p==0) times(j) += now();
                    }
                    
                    // verification
                    for (var i:int=0; i<localSize; i++)
                        if (a(i) != b(i) + alpha*c(i)) 
                            async(Place.FIRST_PLACE) clocked (clock)
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

    static def now():double = System.nanoTime() * 1e-9;

    static def printStats(N:int, time:double, verified:boolean) {
        val size = (3*8*N/MEG);
        val rate = (3*8*N) / (1.0E9*time);
        System.out.println("Number of places=" + Place.MAX_PLACES);
        System.out.println("Size of arrays: " + size +" MB (total)" + size/Place.MAX_PLACES + " MB (per place)");
        System.out.println("Min time: " + time + " rate=" + rate + " GB/s");
        System.out.println("Result is " + (verified ? "verified." : "NOT verified."));
    }                                
}
