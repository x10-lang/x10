import x10.io.Console;
import x10.util.Random;

public class KMeans {

    static val DIM=2, CLUSTERS=4, POINTS=2000, ITERATIONS=50;

    public static def main (args : Rail[String]) {

        val rnd = new Random(0);

        val points = Rail.makeVal(DIM*POINTS, (i:Int)=>rnd.nextFloat());

        var clusters : Rail[Float] = Rail.makeVar(CLUSTERS*DIM, (i:Int)=>points(i));

        for (i in 1..ITERATIONS) {

            Console.OUT.println("Iteration: "+i);

            val new_clusters = Rail.makeVar(CLUSTERS*DIM, (i:Int)=>0 as Float);

            val count = Rail.makeVar(CLUSTERS, (i:Int)=>0);

            // PART 1
            for (var p:Int=0 ; p<POINTS ; ++p) { 
                var closest:Int = -1;
                var closest_dist:Float = Float.MAX_VALUE;
                for (var k:Int=0 ; k<CLUSTERS ; ++k) { 
                    var dist : Float = 0;
                    for (var d:Int=0 ; d<DIM ; ++d) { 
                        val tmp = points(p*DIM+d) - clusters(k*DIM+d);
                        dist += tmp * tmp;
                    }
                    if (dist < closest_dist) {
                        closest_dist = dist;
                        closest = k;
                    }
                }
                for (var d:Int=0 ; d<DIM ; ++d) { 
                    new_clusters(closest*DIM+d) += points(p*DIM+d);
                }
                count(closest)++;
            }

            // PART 2
            for (var k:Int=0 ; k<CLUSTERS ; ++k) { 
                for (var d:Int=0 ; d<DIM ; ++d) { 
                    new_clusters(k*DIM+d) /= count(k);
                }
            }

            // TEST FOR CONVERGENCE
            var b:Boolean = true;
            for (var j:Int=0 ; j<clusters.length ; ++j) { 
                if (clusters(j)!=new_clusters(j)) {
                    b = false;
                    break;
                }
            }
            if (b) break;

            clusters = new_clusters;

        }

        for (var d:Int=0 ; d<DIM ; ++d) { 
            for (var k:Int=0 ; k<CLUSTERS ; ++k) { 
                if (k>0)
                    Console.OUT.print(" ");
                Console.OUT.print(clusters(k*DIM+d));
            }
            Console.OUT.println();
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
