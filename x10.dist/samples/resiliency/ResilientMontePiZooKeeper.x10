import x10.io.Console;
import x10.util.*;
import x10.compiler.*;

/** A monte pi that runs in non-resilient x10 and throws an artifical PDE */

public class ResilientMontePiZooKeeper {

    static val ITERS = 1000000000l / Place.MAX_PLACES;

    public static def main (args:Rail[String]) {

        val result = GlobalRef(new Cell(Pair[Long,Long](0l, 0l))); // (points_in_circle, points_tested)

        try {
            @Pragma(Pragma.FINISH_RESILIENT_ZOO_KEEPER) finish for (p in Place.places()) async at (p) {
                val rand = new Random(System.nanoTime());
                var total:Long = 0l;
                for (iter in 1..ITERS) {
                    val x = rand.nextDouble();
                    val y = rand.nextDouble();
                    if (x*x + y*y <= 1.0) total++;
                }
                val total_ = total;
                Console.OUT.println("Work done at: "+here);
                async at (result) atomic {
                    result()(Pair(result()().first+total_, result()().second+ITERS));
                }
            }
        } catch (es:MultipleExceptions) {
            es.printStackTrace();
        }

        val pi = (4.0 * result()().first) / result()().second;
        Console.OUT.println("pi = "+pi+"   calculated with "+result()().second+" samples.");
    }

}

// vim: shiftwidth=4:tabstop=4:expandtab
