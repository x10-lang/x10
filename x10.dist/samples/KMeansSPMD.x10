/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

import x10.array.*;
import x10.util.Random;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.Team;

/**
 * A SPMD formulation of KMeans clustering.
 *
 * Team operations are used for inter-Place coordination
 * and efficient global communication.
 *
 * Intra-place concurrency is exposed via Foreach.
 *
 * Points are stored in each Place in an Array_2[Float].
 *
 * For the highest throughput and most scalable implementation of
 * the KMeans algorithm in X10, please see KMeans.x10 in the
 * X10 Benchmarks Suite (separate download from x10-lang.org).
 */
public class KMeansSPMD {

    public static def printPoints (clusters:Array_2[Float]) {
        for (d in 0..(clusters.numElems_2-1)) {
            for (k in 0..(clusters.numElems_1-1)) {
                if (k>0)
                    Console.OUT.print(" ");
                Console.OUT.print(clusters(k,d).toString());
            }
            Console.OUT.println();
        }
    }

    public static def main (args:Rail[String]) {here == Place.FIRST_PLACE } {
        val opts = new OptionsParser(args, [
            Option("q","quiet","just print time taken"),
            Option("v","verbose","print out each iteration"),
            Option("h","help","this information")
        ], [
            Option("i","iterations","quit after this many iterations"),
            Option("c","clusters","number of clusters to find"),
            Option("d","dim","number of dimensions"),
            Option("n","num","quantity of points")
        ]);
        if (opts.filteredArgs().size!=0L) {
            Console.ERR.println("Unexpected arguments: "+opts.filteredArgs());
            Console.ERR.println("Use -h or --help.");
            System.setExitCode(1n);
            return;
        }
        if (opts("-h")) {
            Console.OUT.println(opts.usage(""));
            return;
        }

        val numClusters = opts("-c",4);
        val numPoints = opts("-n", 2000);
        val iterations = opts("-i",50);
        val dim = opts("-d", 4);
        val verbose = opts("-v");

        Console.OUT.println("points: "+numPoints+" clusters: "+numClusters+" dim: "+dim);

        val pg = Place.places();
        val pointsPerPlace = numPoints / pg.size();
        val initPoints = (p:Place) => {
            val rand = new x10.util.Random(p.id);
            val pts = new Array_2[Float](pointsPerPlace, dim, (Long,Long)=> rand.nextFloat());
            pts
        };

        val clusters = computeClusters(pg, initPoints, dim, numClusters, iterations, verbose);

        Console.OUT.println("\nFinal results:");
        printPoints(clusters);
    }

    static def computeClusters(pg:PlaceGroup, initPoints:(Place)=>Array_2[Float], dim:Long,
                               numClusters:Long, iterations:Long, verbose:Boolean):Array_2[Float] {
        val ans = new Array_2[Float](numClusters, dim);
        val ansRef = GlobalRail(ans.raw());

        val team = pg.equals(Place.places()) ? Team.WORLD : Team(pg);
        finish {
            for (h in pg) at (h) async {
                var computeTime:Long = 0;
                var commTime:Long = 0;
                var barrierTime:Long = 0;

                // Initialize points for this place
                val myPoints = initPoints(here);

                // Set initial cluster centroids to average of first k points in each place.
                val clusters  = new Array_2[Float](numClusters, dim, (i:long, j:long)=>myPoints(i,j));
                team.allreduce(myPoints.raw(), 0, clusters.raw(), 0, numClusters*dim, Team.ADD);
                for ([i,j] in clusters.indices()) {
                    clusters(i,j) /= pg.size();
                }
                if (here.id==0 && verbose) {
                    Console.OUT.println("Initial clusters: ");
                    printPoints(clusters);
                }

                val clusterCounts = new Rail[Int](numClusters);
                val oldClusters = new Array_2[Float](clusters);

                val startTime = System.currentTimeMillis();

                barrierTime -= System.nanoTime();
                team.barrier();
                barrierTime += System.nanoTime();

                main_loop: for (iter in 0..(iterations-1)) {
                    Array.copy(clusters, oldClusters);

                    clusters.raw().clear();
                    clusterCounts.clear();

                    computeTime -= System.nanoTime();
                    for (p in 0..(myPoints.numElems_1-1)) {
                        var closest:Long = -1;
                        var closestDist:Float = Float.MAX_VALUE;
                        for (k in 0..(numClusters-1)) {
                            var dist : Float = 0;
                            for (d in 0..(dim-1)) { 
                                val tmp = myPoints(p,d) - oldClusters(k,d);
                                dist += tmp * tmp;
                            }
                            if (dist < closestDist) {
                                closestDist = dist;
                                closest = k;
                            }
                        }
                        for (d in 0..(dim-1)) {
                            clusters(closest,d) += myPoints(p,d);
                        }
                        clusterCounts(closest)++;
                    }
                    computeTime += System.nanoTime();

                    commTime -= System.nanoTime();
                    team.allreduce(clusters.raw(), 0L, clusters.raw(), 0L, clusters.raw().size, Team.ADD);
                    team.allreduce(clusterCounts, 0L, clusterCounts, 0L, clusterCounts.size, Team.ADD);
                    commTime += System.nanoTime();

                    for (k in 0..(numClusters-1)) {
                        for (d in 0..(dim-1)) clusters(k,d) /= clusterCounts(k);
                    }

                    if (here.id==0 && verbose) {
                        Console.OUT.println("Iteration: "+iter);
                        printPoints(clusters);
                    }

                    // TEST FOR CONVERGENCE
                    for (j in 0..(numClusters*dim-1)) {
                        if (true/*||Math.abs(clusters_old(j)-clusters(j))>0.0001*/) continue main_loop;
                    }

                    break;

                } // main_loop

                Console.OUT.printf("%d: computation %.3f s communication %.3f s (barrier %.3f s)\n", 
                    here.id, computeTime/1E9, commTime/1E9, barrierTime/1E9);

                team.barrier();

                if (here.id == 0) {
                    finish Rail.asyncCopy(clusters.raw(), 0, ansRef, 0, clusters.raw().size);
                }

            } // async
        } // finish

        return ans;
    }

}

// vim: shiftwidth=4:tabstop=4:expandtab
