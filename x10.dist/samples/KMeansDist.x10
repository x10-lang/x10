/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import x10.regionarray.*;
import x10.io.Console;
import x10.util.Random;

/**
 * A low performance formulation of KMeans using DistArray
 * and fine-grained asyncs.
 *
 * For a scalable, high-performance version of this benchmark see
 * KMeans.x10 in the X10 Benchmarks (separate download from x10-lang.org)
 */
public class KMeansDist {

    static val DIM=2;
    static val CLUSTERS=4;
    static val POINTS=2000;
    static val ITERATIONS=50;

    static val points_region = Region.make(0..(POINTS-1), 0..(DIM-1));

    public static def main (Rail[String]) {
        val rnd = PlaceLocalHandle.make[Random](Place.places(), () => new Random(0));
        val local_curr_clusters = 
            PlaceLocalHandle.make[Rail[Float]](Place.places(), () => new Rail[Float](CLUSTERS*DIM));
        val local_new_clusters = 
            PlaceLocalHandle.make[Rail[Float]](Place.places(), () =>  new Rail[Float](CLUSTERS*DIM));
        val local_cluster_counts = 
            PlaceLocalHandle.make[Rail[Int]](Place.places(), ()=> new Rail[Int](CLUSTERS));

        val points_dist = Dist.makeBlock(points_region, 0);
        val points = DistArray.make[Float](points_dist, (p:Point)=>rnd().nextFloat());

        val central_clusters = new Rail[Float](CLUSTERS*DIM, (i:long) => {
            val p = Point.make(i/DIM, i%DIM);
            return at (points_dist(p)) points(p);
        });

	val old_central_clusters = new Rail[Float](CLUSTERS*DIM);

        val central_cluster_counts = new Rail[Int](CLUSTERS);

        for (i in 1..ITERATIONS) {

            Console.OUT.println("Iteration: "+i);

            for (j in 0..(CLUSTERS-1)) {
                local_cluster_counts()(j) = 0n;
            }

            finish {
                // reset state
                for (d in points_dist.places()) at (d) async {
                    for (j in 0..(DIM*CLUSTERS-1)) {
                        local_curr_clusters()(j) = central_clusters(j);
                        local_new_clusters()(j) = 0f;
                    }
                    for (j in 0..(CLUSTERS-1)) {
                        local_cluster_counts()(j) = 0n;
                    }
                }
            }

            finish {
                // compute new clusters and counters
                for (p in 0..(POINTS-1)) {
                    at (points_dist(p,0)) async {
                        var closest:Long = -1;
                        var closest_dist:Float = Float.MAX_VALUE;
                        for (k in 0..(CLUSTERS-1)) { 
                            var dist : Float = 0;
                            for (d in 0..(DIM-1)) { 
                                val tmp = points(Point.make(p,d)) - local_curr_clusters()(k*DIM+d);
                                dist += tmp * tmp;
                            }
                            if (dist < closest_dist) {
                                closest_dist = dist;
                                closest = k;
                            }
                        }
			atomic {
                            for (d in 0..(DIM-1)) { 
                                local_new_clusters()(closest*DIM+d) += points(Point.make(p,d));
                            }
                            local_cluster_counts()(closest)++;
                        }
                    }
                }
            }


            for (j in 0..(DIM*CLUSTERS-1)) {
                old_central_clusters(j) = central_clusters(j);
                central_clusters(j) = 0f;
            }

            for (j in 0..(CLUSTERS-1)) {
                central_cluster_counts(j) = 0n;
            }

            finish {
                val central_clusters_gr = GlobalRef(central_clusters);
                val central_cluster_counts_gr = GlobalRef(central_cluster_counts);
                val there = here;
                for (d in points_dist.places()) at (d) async {
                    // access PlaceLocalHandles 'here' and then data will be captured by at and transfered to 'there' for accumulation
                    val tmp_new_clusters = local_new_clusters();
                    val tmp_cluster_counts = local_cluster_counts();
                    at (there) atomic {
                        for (j in 0..(DIM*CLUSTERS-1)) {
                            central_clusters_gr()(j) += tmp_new_clusters(j);
                        }
                        for (j in 0..(CLUSTERS-1)) {
                            central_cluster_counts_gr()(j) += tmp_cluster_counts(j);
                        }
                    }
                }
            }

            for (k in 0..(CLUSTERS-1)) { 
                for (d in 0..(DIM-1)) { 
                    central_clusters(k*DIM+d) /= central_cluster_counts(k);
                }
            }

            // TEST FOR CONVERGENCE
            var b:Boolean = true;
            for (j in 0..(CLUSTERS*DIM-1)) { 
                if (Math.abs(old_central_clusters(j)-central_clusters(j))>0.0001) {
                    b = false;
                    break;
                }
            }
            if (b) break;

        }

        for (d in 0..(DIM-1)) { 
            for (k in 0..(CLUSTERS-1)) { 
                if (k>0)
                    Console.OUT.print(" ");
                Console.OUT.print(central_clusters(k*DIM+d));
            }
            Console.OUT.println();
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
