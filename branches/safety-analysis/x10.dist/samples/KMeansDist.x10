import x10.io.Console;
import x10.util.Random;
import x10.runtime.PlaceLocalStorage;
import x10.runtime.PlaceLocalHandle;

public class KMeansDist {

    static val DIM=2, CLUSTERS=4, POINTS=2000, ITERATIONS=50;

    static val points_region = [0,0]..[POINTS-1,DIM-1];

    public static def main (args : Rail[String]!) {
        val rnd = PlaceLocalStorage.createDistributedObject[Random](Dist.makeUnique(), () => new Random(0));
        val local_curr_clusters = PlaceLocalStorage.createDistributedObject[Rail[Float]](Dist.makeUnique(), 
	                                                                                 () => Rail.make[Float](CLUSTERS*DIM, (i:Int) => 0 as Float));
        val local_new_clusters = PlaceLocalStorage.createDistributedObject[Rail[Float]](Dist.makeUnique(),
									                () =>  Rail.make[Float](CLUSTERS*DIM, (i:Int) => 0 as Float));
        val local_cluster_counts = PlaceLocalStorage.createDistributedObject[Rail[Int]](Dist.makeUnique(), 
                                                                                        ()=> Rail.make[Int](CLUSTERS, (i:Int) => 0));

        val points_dist = Dist.makeBlock(points_region, 0);
        val points = Array.makeVal[Float](points_dist, (p:Point)=>rnd.get().nextFloat());

        val central_clusters = Rail.make[Float](CLUSTERS*DIM, (i:Int) => {
            val p = Point.make([i/DIM, i%DIM]);
            return at (points_dist(p)) points(p);
        });

        val central_cluster_counts = Rail.make[Int](CLUSTERS, (i:Int) => 0);

        for (i in 1..ITERATIONS) {

            Console.OUT.println("Iteration: "+i);

            // have to create a valrail so that it will be serialised
            val central_clusters_copy = ValRail.make(CLUSTERS*DIM, (i:Int) => central_clusters(i));

            for (var j:Int=0 ; j<CLUSTERS ; ++j) {
                local_cluster_counts.get()(j) = 0;
            }

            finish {
                // reset state
                for (d in points_dist.places()) async(d) {
                    for (var j:Int=0 ; j<DIM*CLUSTERS ; ++j) {
                        local_curr_clusters.get()(j) = central_clusters_copy(j);
                        local_new_clusters.get()(j) = 0;
                    }
                    for (var j:Int=0 ; j<CLUSTERS ; ++j) {
                        local_cluster_counts.get()(j) = 0;
                    }
                }
            }

            finish {
                // compute new clusters and counters
                for (var p_:Int=0 ; p_<POINTS ; ++p_) {
                    val p = p_;
                    async (points_dist(p,0)) {
                        var closest:Int = -1;
                        var closest_dist:Float = Float.MAX_VALUE;
                        for (var k:Int=0 ; k<CLUSTERS ; ++k) { 
                            var dist : Float = 0;
                            for (var d:Int=0 ; d<DIM ; ++d) { 
                                val tmp = points(Point.make(p,d)) - local_curr_clusters.get()(k*DIM+d);
                                dist += tmp * tmp;
                            }
                            if (dist < closest_dist) {
                                closest_dist = dist;
                                closest = k;
                            }
                        }
                        for (var d:Int=0 ; d<DIM ; ++d) { 
                            local_new_clusters.get()(closest*DIM+d) += points(Point.make(p,d));
                        }
                        local_cluster_counts.get()(closest)++;
                    }
                }
            }

            for (var j:Int=0 ; j<DIM*CLUSTERS ; ++j) {
                central_clusters(j) = 0;
            }

            for (var j:Int=0 ; j<CLUSTERS ; ++j) {
                central_cluster_counts(j) = 0;
            }

            finish {
                for (d in points_dist.places()) async(d) {
                    // have to create valrails for serialisation
                    val local_new_clusters_copy =
                        ValRail.make(CLUSTERS*DIM, (i:Int) => local_new_clusters.get()(i));
                    val local_cluster_counts_copy =
                        ValRail.make(CLUSTERS, (i:Int) => local_cluster_counts.get()(i));
                    at (Place.FIRST_PLACE) atomic {
                        for (var j:Int=0 ; j<DIM*CLUSTERS ; ++j) {
                            central_clusters(j) += local_new_clusters.get()(j);
                        }
                        for (var j:Int=0 ; j<CLUSTERS ; ++j) {
                            central_cluster_counts(j) += local_cluster_counts.get()(j);
                        }
                    }
                }
            }

            for (var k:Int=0 ; k<CLUSTERS ; ++k) { 
                for (var d:Int=0 ; d<DIM ; ++d) { 
                    central_clusters(k*DIM+d) /= central_cluster_counts(k);
                }
            }

            // TEST FOR CONVERGENCE
            var b:Boolean = true;
            for (var j:Int=0 ; j<CLUSTERS*DIM ; ++j) { 
                if (Math.abs(central_clusters_copy(j)-central_clusters(j))>0.0001) {
                    b = false;
                    break;
                }
            }
            if (b) break;

        }

        for (var d:Int=0 ; d<DIM ; ++d) { 
            for (var k:Int=0 ; k<CLUSTERS ; ++k) { 
                if (k>0)
                    Console.OUT.print(" ");
                Console.OUT.print(central_clusters(k*DIM+d));
            }
            Console.OUT.println();
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
