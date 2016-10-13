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
import x10.util.Random;
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

    static class LocalState {
        val points:Array_2[Float];
        val oldClusters:Array_2[Float];
        val clusters:Array_2[Float];
        val clusterCounts:Rail[Int];
        val numPoints:Long;
        val numClusters:Long;
        val dim:Long;
        var computeTime:Long = 0;
        var commTime:Long = 0;

        def this(initPoints:(Place)=>Array_2[Float], dim:Long, numClusters:Long) {
            points = initPoints(here);
            oldClusters = new Array_2[Float](numClusters, dim);
            val tmp = points; // hack around escaping this in constructor
            clusters  = new Array_2[Float](numClusters, dim, (i:long, j:long)=>tmp(i,j));
            clusterCounts = new Rail[Int](numClusters);
            numPoints = points.numElems_1;
            this.numClusters = numClusters;
            this.dim = dim;
        }
    }

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

    static def initialize(pg:PlaceGroup, team:Team, initPoints:(Place)=>Array_2[Float],
                          dim:Long, numClusters:Long):LocalState {
        val ls = new LocalState(initPoints, dim, numClusters);

        // Set initial cluster centroids to average of first k points in each place.
        team.allreduce(ls.points.raw(), 0, ls.clusters.raw(), 0, numClusters*dim, Team.ADD);
        for ([i,j] in ls.clusters.indices()) {
            ls.clusters(i,j) /= pg.size();
        }

        return ls;
    }

    static def oneStep(ls:LocalState, team:Team, dim:Long, numClusters:Long, epsilon:Float):Boolean {
        Array.copy(ls.clusters, ls.oldClusters);

        ls.clusters.raw().clear();
        ls.clusterCounts.clear();

        // Primary kernel: for every point, determine current closest
        //                 cluster and assign the point to that cluster.
        ls.computeTime -= System.nanoTime();
        Block.for(mine:LongRange in 0..(ls.numPoints-1)) {
            val scratchClusters = new Array_2[Float](numClusters, dim);
            val scratchClusterCounts = new Rail[Int](numClusters);
            for (p in mine) {
                var closest:Long = -1;
                var closestDist:Float = Float.MAX_VALUE;
                for (k in 0..(numClusters-1)) {
                    var dist : Float = 0;
                    for (d in 0..(dim-1)) {
                        val tmp = ls.points(p,d) - ls.oldClusters(k,d);
                        dist += tmp * tmp;
                    }
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = k;
                    }
                }
                for (d in 0..(dim-1)) {
                    scratchClusters(closest,d) += ls.points(p,d);
                }
                scratchClusterCounts(closest)++;
            }
            atomic {
                for ([i,j] in scratchClusters.indices()) {
                    ls.clusters(i,j) += scratchClusters(i,j);
                }
                for (i in scratchClusterCounts.range()) {
                    ls.clusterCounts(i) += scratchClusterCounts(i);
                }
            }
        }
        ls.computeTime += System.nanoTime();

        // Aggregate computed clusters across all places
        ls.commTime -= System.nanoTime();
        team.allreduce(ls.clusters.raw(), 0L, ls.clusters.raw(), 0L, ls.clusters.raw().size, Team.ADD);
        team.allreduce(ls.clusterCounts, 0L, ls.clusterCounts, 0L, ls.clusterCounts.size, Team.ADD);
        ls.commTime += System.nanoTime();

        // Normalize to compute new cluster centroids
        for (k in 0..(numClusters-1)) {
            if (ls.clusterCounts(k) > 0) {
                for (d in 0..(dim-1)) ls.clusters(k,d) /= ls.clusterCounts(k);
            }
        }

        // Test for convergence
        for ([i,j] in ls.clusters.indices()) {
            if (Math.abs(ls.oldClusters(i,j) - ls.clusters(i,j)) > epsilon) {
                return false;
            }
        }
        return true;
    }

    static def computeClusters(pg:PlaceGroup, initPoints:(Place)=>Array_2[Float], dim:Long,
                               numClusters:Long, iterations:Long, epsilon:Float, verbose:Boolean):Array_2[Float] {
        val ans = new Array_2[Float](numClusters, dim);
        val ansRef = GlobalRail(ans.raw());

        val team = pg.equals(Place.places()) ? Team.WORLD : Team(pg);
        finish {
            for (h in pg) at (h) async {
                val ls = initialize(pg, team, initPoints, dim, numClusters);
                if (here.id==0 && verbose) {
                    Console.OUT.println("Initial clusters: ");
                    printPoints(ls.clusters);
                }

                for (iter in 0..(iterations-1)) {
                    val converged = oneStep(ls, team, dim, numClusters, epsilon);
                    if (converged) break;
                    if (here.id==0 && verbose) {
                        Console.OUT.println("Iteration: "+iter);
                        printPoints(ls.clusters);
                    }
                }

                Console.OUT.printf("%d: computation %.3f s communication %.3f s\n",
                                   here.id, ls.computeTime/1E9, ls.commTime/1E9);

                team.barrier();

                if (here.id == 0) {
                    finish Rail.asyncCopy(ls.clusters.raw(), 0, ansRef, 0, ls.clusters.raw().size);
                }

            }
        }

        return ans;
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

        val clusters = computeClusters(pg, initPoints, dim, numClusters, iterations, epsilon, verbose);

        Console.OUT.println("\nFinal results:");
        printPoints(clusters);
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
