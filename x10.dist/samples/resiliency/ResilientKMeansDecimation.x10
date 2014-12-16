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
import x10.util.*;
import x10.compiler.*;

/**
 * Resilient KMeans which supports DeadPlaceException
 * Partially based on KMeansDist.x10
 * @author kawatiya
 * 
 * For Managed X10:
 *   $ x10 ResilientKMeans.x10
 *   $ X10_RESILIENT_MODE=11 X10_NPLACES=4 x10 ResilientKMeans [num_points] [num_clusters]
 * For Native X10:
 *   $ x10c++ ResilientKMeans.x10 -o ResilientKMeans
 *   $ X10_RESILIENT_MODE=11 X10_NPLACES=4 runx10 ResilientKMeans [num_points] [num_clusters]
 */
class ResilientKMeansDecimation {
    
    static val DIM=4N;
    //static val CLUSTERS=4L;
    //static val POINTS=1000000L;
    static val ITERATIONS=10L;
    
    public static def main(args:Rail[String]) {here == Place.FIRST_PLACE} {
        val POINTS = (args.size>=1) ? Long.parseLong(args(0)) : 1000000L;
        val CLUSTERS = (args.size>=2) ? Long.parseLong(args(1)): 4L;
        Console.OUT.println("KMeans: Abstract " + Place.numPlaces()*POINTS + " points in " + DIM + "D space into "
                            + CLUSTERS + " clusters, using " + Place.numPlaces() + " places");
        
        /*
        finish for (p in Place.places()) at (p) {
            Console.OUT.println(here+" running in "+Runtime.getName());
        }
        */

        /*
         * Create a points array randomly at each place.
         * Note: points are contiguous in array p1.x, p1.y, p1.z, p2.x, p2.y, p2.z
         */
        Console.OUT.println("Creating points array...  ");
        val creation_before = System.nanoTime();
        val points_local = PlaceLocalHandle.make[Rail[Float], Random](Place.places(), (p:Place)=>new Random(p.id), (rnd:Random) => new Rail[Float](POINTS*DIM, (i:Long)=>rnd.nextFloat()));
        val creation_after = System.nanoTime();
        Console.OUT.println("-- Took "+(creation_after-creation_before)/1E9+" seconds");
        
        /*
         * Cluster data to be calculated
         * Note: Cordinates of n-th cluster point is [clusters(n*2), clusters(n*2+1)]
         */
        /* Use the first CLUSTERS points as initial cluster values */
        val central_clusters = new Rail[Float](CLUSTERS*DIM, (i:Long) => points_local()(i));
        val old_central_clusters = new Rail[Float](CLUSTERS*DIM);
        val central_cluster_counts = new Rail[Long](CLUSTERS);
        /* GlobalRefs to access the structures from other places */
        val central_clusters_gr = GlobalRef(central_clusters);
        val central_cluster_counts_gr = GlobalRef(central_cluster_counts);
        /* For local calculation */
        val local_curr_clusters = PlaceLocalHandle.make[Rail[Float]](Place.places(), ()=>new Rail[Float](CLUSTERS*DIM));
        val local_new_clusters = PlaceLocalHandle.make[Rail[Float]](Place.places(), ()=>new Rail[Float](CLUSTERS*DIM));
        val local_cluster_counts = PlaceLocalHandle.make[Rail[Long]](Place.places(), ()=>new Rail[Long](CLUSTERS));
        
        /*
         * Calculate KMeans using multiple places
         */
        val compute_before = System.nanoTime();
        for (var iter:Long=0L; iter<ITERATIONS ; iter++) {
            
            Console.OUT.println("==== Iteration: "+iter+" ====");
            
            /* 
             * Deliver the central_clusters
             */
            Console.OUT.println("Distributing clusters...  ");
            val dist_clusters_before = System.nanoTime();
            try {
                finish for (pl in Place.places()) {
                    if (pl.isDead()) continue; // skip the dead place
                    at (pl) async {
                        for (var j:Long=0L ; j<CLUSTERS*DIM ; ++j) {
                            local_curr_clusters()(j) = central_clusters(j);
                            local_new_clusters()(j) = 0f;
                        }
                        for (var j:Long=0L ; j<CLUSTERS ; ++j) {
                            local_cluster_counts()(j) = 0L;
                        }
                    }
                }
            } catch (es:MultipleExceptions) {
                val deadPlaceExceptions = es.getExceptionsOfType[DeadPlaceException]();
                for (dpe in deadPlaceExceptions) {
                    Console.OUT.println("DeadPlaceException thrown from " + dpe.place);
                    // No recovery is necessary, completeness will be checked by number of processed points
                }
                val filtered = es.filterExceptionsOfType[DeadPlaceException]();
                if (filtered != null) throw filtered;
            }
            val dist_clusters_after = System.nanoTime();
            Console.OUT.println("-- Took "+(dist_clusters_after-dist_clusters_before)/1E9+" seconds");

            /* Clear the central_clusters to collect results */
            for (var j:Long=0L ; j<CLUSTERS*DIM ; ++j) {
                old_central_clusters(j) = central_clusters(j);
                central_clusters(j) = 0f;
            }
            for (var j:Long=0L ; j<CLUSTERS ; ++j) {
                central_cluster_counts(j) = 0L;
            }
            
            val points_used = new GlobalRef[Cell[Long]](new Cell[Long](0));

            /* 
             * Compute new clusters and counters at each place
             */
            Console.OUT.println("Computing clusters...  ");
            val compute_clusters_before = System.nanoTime();
            try {
                finish for (pl in Place.places()) {
                    if (pl.isDead()) continue; // skip the dead place
                    
                    /* At place pl, process points [start,end) */
                    at (pl) async {

                        //val actual_work_before = System.nanoTime();
                        for (p in 0..(POINTS-1)) {
                            val points = points_local();
                            var closest:Long = -1L;
                            var closest_dist:Float = Float.MAX_VALUE;
                            for (var k:Long=0L ; k<CLUSTERS ; ++k) {
                                var dist:Float = 0f;
                                for (var d:Long=0L ; d<DIM ; ++d) {
                                    val tmp = points(p*DIM + d) - local_curr_clusters()(k*DIM+d);
                                    dist += tmp * tmp;
                                }
                                if (dist < closest_dist) {
                                    closest_dist = dist;
                                    closest = k;
                                }
                            }
                            for (var d:Long=0L ; d<DIM ; ++d) {
                                local_new_clusters()(closest*DIM+d) += points(p*DIM + d);
                            }
                            local_cluster_counts()(closest)++;
                        } /* for (p) */
                        //val actual_work_after = System.nanoTime();
                        //Console.OUT.println("Actual work at place "+here+" took "+(actual_work_after-actual_work_before)/1E9+" seconds");

                        val msg_back_before = System.nanoTime();
                        /* All assigned points processed, return the local results */
                        val tmp_new_clusters = local_new_clusters();
                        val tmp_cluster_counts = local_cluster_counts();
                        at (Place.FIRST_PLACE) async atomic {
                            for (var j:Long=0L ; j<CLUSTERS*DIM; ++j) {
                                central_clusters_gr()(j) += tmp_new_clusters(j);
                            }
                            for (var j:Long=0L ; j<CLUSTERS ; ++j) {
                                central_cluster_counts_gr()(j) += tmp_cluster_counts(j);
                            }
                            points_used()(points_used()()+POINTS);
                        }
                        //val msg_back_after = System.nanoTime();
                        //Console.OUT.println("Profile: "+prof.bytes+" bytes, "+prof.serializationNanos/1E9+" ser, "+prof.communicationNanos/1E9+" comm");
                        //Console.OUT.println("Message back from place "+here+" took "+(msg_back_after-msg_back_before)/1E9+" seconds");
                    } /* at (pl) async */
                } /* finish for (pl) */
            } catch (es:MultipleExceptions) {
                val deadPlaceExceptions = es.getExceptionsOfType[DeadPlaceException]();
                for (dpe in deadPlaceExceptions) {
                    Console.OUT.println("DeadPlaceException thrown from " + dpe.place);
                    // No recovery is necessary, completeness will be checked by number of processed points
                }
                val filtered = es.filterExceptionsOfType[DeadPlaceException]();
                if (filtered != null) throw filtered;
            }
            val compute_clusters_after = System.nanoTime();
            Console.OUT.println("-- Took "+(compute_clusters_after-compute_clusters_before)/1E9+" seconds.  Used "+points_used()()+" points this iteration.");
            
            /*
             * Compute new cluster values
             */
            for (var k:Long=0L ; k<CLUSTERS ; ++k) { 
                for (var d:Long=0L ; d<DIM ; ++d) { 
                    central_clusters(k*DIM+d) /= central_cluster_counts(k);
                }
            }
            
        } /* for (iter) */
        val compute_after = System.nanoTime();
        Console.OUT.println("Entire computation took "+(compute_after-compute_before)/1E9+" seconds");
        
        /*
         * Print the result
         */
        /*
        Console.OUT.println("---- Result of " + CLUSTERS + " clustering");
        for (var d:Long=0L ; d<DIM ; ++d) { 
            for (var k:Long=0L ; k<CLUSTERS ; ++k) { 
                if (k>0) Console.OUT.print(" ");
                Console.OUT.print(central_clusters(k*DIM+d));
            }
            Console.OUT.println();
        }
        */
    } /* main */
}
