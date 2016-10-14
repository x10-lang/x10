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

import x10.array.Array;
import x10.array.Array_2;
import x10.util.foreach.Block;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.Pair;
import x10.util.Random;

/**
 * A distributed implementation of KMeans clustering.
 *
 * Intra-place concurrency is exposed via Foreach.
 *
 * Points are stored in each Place in an PlaceLocalHandle[Array_2[Float]].
 *
 * For the highest throughput and most scalable implementation of
 * the KMeans algorithm in X10 for Native X10, see KMeans.x10 in the
 * X10 Benchmarks Suite (separate download from x10-lang.org).
 */
public class KMeans {

    static def printPoints (clusters:Array_2[Float]) {
        for (d in 0..(clusters.numElems_2-1)) {
            for (k in 0..(clusters.numElems_1-1)) {
                if (k>0)
                    Console.OUT.print(" ");
                Console.OUT.print(clusters(k,d).toString());
            }
            Console.OUT.println();
        }
    }

    static def oneStep(points:Array_2[Float], oldClusters:Array_2[Float],
                       dim:Long, numClusters:Long):Pair[Array_2[Float], Rail[Int]] {
        val numPoints = points.numElems_1;
        val clusters = new Array_2[Float](numClusters, dim);
        val clusterCounts = new Rail[Int](numClusters);

        Block.for(mine:LongRange in 0..(numPoints-1)) {
            val scratchClusters = new Array_2[Float](numClusters, dim);
            val scratchClusterCounts = new Rail[Int](numClusters);
            for (p in mine) {
                var closest:Long = -1;
                var closestDist:Float = Float.MAX_VALUE;
                for (k in 0..(numClusters-1)) {
                    var dist : Float = 0;
                    for (d in 0..(dim-1)) {
                        val tmp = points(p,d) - oldClusters(k,d);
                        dist += tmp * tmp;
                    }
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = k;
                    }
                }
                for (d in 0..(dim-1)) {
                    scratchClusters(closest,d) += points(p,d);
                }
                scratchClusterCounts(closest)++;
            }
            atomic {
                for ([i,j] in scratchClusters.indices()) {
                    clusters(i,j) += scratchClusters(i,j);
                }
                for (i in scratchClusterCounts.range()) {
                    clusterCounts(i) += scratchClusterCounts(i);
                }
            }
        }

        return Pair[Array_2[Float],Rail[Int]](clusters, clusterCounts);
    }

    static def computeClusters(pg:PlaceGroup, initPoints:(Place)=>Array_2[Float], dim:Long,
                               numClusters:Long, iterations:Long, epsilon:Float, verbose:Boolean):Array_2[Float] {

        // Initialize points in every Place; make initial centroids the first k points in Place(0).
        val points = PlaceLocalHandle.make[Array_2[Float]](pg, ()=>{ initPoints(here) });
        val clusters = new Array_2[Float](numClusters, dim, (i:Long,j:Long)=> points()(i,j));
        val oldClusters = new Array_2[Float](numClusters, dim);
        val clusterCounts = new Rail[Int](numClusters);

        if (verbose) {
            Console.OUT.println("Initial clusters: ");
            printPoints(clusters);
        }

        for (iter in 0..(iterations-1)) {
            // Prepare for next iteration
            Array.copy(clusters, oldClusters);
            clusters.raw().clear();
            clusterCounts.clear();

            finish {
                for (h in pg) async {
                    val partialResults = at (h) oneStep(points(), oldClusters, dim, numClusters);
                    atomic {
                        for ([i,j] in clusters.indices()) {
                            clusters(i,j) += partialResults.first(i,j);
                        }
                        for (i in clusterCounts.range()) {
                            clusterCounts(i) += partialResults.second(i);
                        }
                    }
                }
            }

            // Normalize to compute new cluster centroids
            for (k in 0..(numClusters-1)) {
                if (clusterCounts(k) > 0) {
                    for (d in 0..(dim-1)) clusters(k,d) /= clusterCounts(k);
                }
            }

            // Test for convergence
            var converged:Boolean = true;
            for ([i,j] in clusters.indices()) {
                if (Math.abs(oldClusters(i,j) - clusters(i,j)) > epsilon) {
                    converged = false;
                    break;
                }
            }
            if (converged) break;

            if (verbose) {
                Console.OUT.println("Iteration: "+iter);
                printPoints(clusters);
            }
        }

        return clusters;
    }

    public static def main (args:Rail[String]) {
        val opts = new OptionsParser(args, [
            Option("q","quiet","just print time taken"),
            Option("v","verbose","print out each iteration"),
            Option("h","help","this information")
        ], [
            Option("i","iterations","quit after this many iterations"),
            Option("c","clusters","number of clusters to find"),
            Option("d","dim","number of dimensions"),
            Option("e","epsilon","convergence threshold"),
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
        val epsilon = opts("-e", 1e-3f); // negative epsilon forces i iterations.
        val verbose = opts("-v");

        Console.OUT.println("points: "+numPoints+" clusters: "+numClusters+" dim: "+dim);

        val pg = Place.places();
        val pointsPerPlace = numPoints / pg.size();
        val initPoints = (p:Place) => {
            val rand = new x10.util.Random(p.id);
            val pts = new Array_2[Float](pointsPerPlace, dim, (Long,Long)=> rand.nextFloat());
            pts
        };

        val start = System.nanoTime();
        val clusters = computeClusters(pg, initPoints, dim, numClusters, iterations, epsilon, verbose);
        val stop = System.nanoTime();
        Console.OUT.printf("TOTAL_TIME: %.3f seconds\n", (stop-start)/1e9);

        if (verbose) {
            Console.OUT.println("\nFinal results:");
            printPoints(clusters);
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
