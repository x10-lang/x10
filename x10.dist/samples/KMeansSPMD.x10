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
 * Points are stored in each Place in a Rail[Float].
 *
 * For the highest throughput and most scalable implementation of
 * the KMeans algorithm in X10 for Native X10, see KMeans.x10 in the
 * X10 Benchmarks Suite (separate download from x10-lang.org).
 */
public class KMeansSPMD {

    static class LocalState {
        var points:Rail[Float];
        var currentClusters:Rail[Float];
        var clusters:Rail[Float];
        var clusterCounts:Rail[Int];
        var numPoints:Long;
        var numClusters:Long;
        var epsilon:Float;
        var dim:Long;
        var team:Team;
        var kernelTime:Long = 0;
        var commTime:Long = 0;
        var converged:Boolean = false;

        def this(team:Team, initPoints:(Place)=>Rail[Float], numPoints:Long,
                 dim:Long, numClusters:Long, epsilon:Float) {
            points = initPoints(here);
            currentClusters = new Rail[Float](numClusters * dim);
            val tmp = points; // hack around escaping this in constructor
            clusters  = new Rail[Float](numClusters * dim, (i:long)=>tmp(i));
            clusterCounts = new Rail[Int](numClusters);
            this.numPoints = numPoints;
            this.numClusters = numClusters;
            this.dim = dim;
            this.epsilon = epsilon;
            this.team = team;
        }

        def isFinished() = converged;

        // outlined from step to help JIT compiler
        def kernel(mine:LongRange, currentClusters:Rail[Float],
                   scratchClusters:Rail[Float], scratchClusterCounts:Rail[Int]) {
            val dim = this.dim;
            val numClusters = this.numClusters;
            val points = this.points;
            for (p in mine) {
                var closest:Long = -1;
                var closestDist:Float = Float.MAX_VALUE;
                for (k in 0..(numClusters-1)) {
                    var dist : Float = 0;
                    for (d in 0..(dim-1)) {
                        val tmp = points(p * dim + d) - currentClusters(k * dim + d);
                        dist += tmp * tmp;
                    }
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = k;
                    }
                }
                for (d in 0..(dim-1)) {
                    scratchClusters(closest * dim + d) += points(p * dim + d);
                }
                scratchClusterCounts(closest)++;
            }
        }

        def step():void {
            // Prepare for computation
            Rail.copy(clusters, currentClusters);
            clusters.clear();
            clusterCounts.clear();

            // Primary kernel: for every point, determine current closest
            //                 cluster and assign the point to that cluster.
            kernelTime -= System.nanoTime();
            Block.for(mine:LongRange in 0..(numPoints-1)) {
                val scratchClusters = new Rail[Float](numClusters * dim);
                val scratchClusterCounts = new Rail[Int](numClusters);
                kernel(mine, currentClusters, scratchClusters, scratchClusterCounts);
                atomic {
                    for (i in scratchClusters.range()) {
                        clusters(i) += scratchClusters(i);
                    }
                    for (i in scratchClusterCounts.range()) {
                        clusterCounts(i) += scratchClusterCounts(i);
                    }
                }
            }
            kernelTime += System.nanoTime();

            // Reduction to accumulate new centroids and counts across all places
            commTime -= System.nanoTime();
            team.allreduce(clusters, 0L, clusters, 0L, clusters.size, Team.ADD);
            team.allreduce(clusterCounts, 0L, clusterCounts, 0L, clusterCounts.size, Team.ADD);
            commTime += System.nanoTime();

            // Normalize to compute new cluster centroids
            for (k in 0..(numClusters-1)) {
                if (clusterCounts(k) > 0) {
                    for (d in 0..(dim-1)) clusters(k*dim + d) /= clusterCounts(k);
                }
            }

            // Test for convergence
            var didConverge:Boolean = true;
            for (i in clusters.range()) {
                if (Math.abs(currentClusters(i) - clusters(i)) > epsilon) {
                    didConverge = false;
                    break;
                }
            }
            converged = didConverge;
        }
    }

    static def printPoints (clusters:Rail[Float], numPoints:Long, dim:Long) {
        for (d in 0..(dim-1)) {
            for (k in 0..(numPoints-1)) {
                if (k>0)
                    Console.OUT.print(" ");
                Console.OUT.print(clusters(k*dim + d).toString());
            }
            Console.OUT.println();
        }
    }

    static def initialize(pg:PlaceGroup, team:Team, initPoints:(Place)=>Rail[Float], numPoints:Long,
                          dim:Long, numClusters:Long, epsilon:Float):LocalState {
        val ls = new LocalState(team, initPoints, numPoints, dim, numClusters, epsilon);

        // Set initial cluster centroids to average of first k points in each place.
        team.allreduce(ls.points, 0, ls.clusters, 0, numClusters*dim, Team.ADD);
        val np = pg.numPlaces();
        for (i in ls.clusters.range()) {
            ls.clusters(i) /= np;
        }

        return ls;
    }

    static def computeClusters(pg:PlaceGroup, initPoints:(Place)=>Rail[Float], numPoints:Long, dim:Long,
                               numClusters:Long, iterations:Long, epsilon:Float, verbose:Boolean):Rail[Float] {
        val ans = new Rail[Float](numClusters * dim);
        val ansRef = GlobalRail(ans);

        val team = pg.equals(Place.places()) ? Team.WORLD : Team(pg);
        finish {
            for (h in pg) at (h) async {
                val ls = initialize(pg, team, initPoints, numPoints, dim, numClusters, epsilon);
                if (here.id==0 && verbose) {
                    Console.OUT.println("Initial clusters: ");
                    printPoints(ls.clusters, numClusters, dim);
                }

                for (iter in 0..(iterations-1)) {
                    ls.step();
                    if (ls.isFinished()) break;
                }

                if (verbose) {
                    Console.OUT.printf("%d: computation %.3f s communication %.3f s\n",
                                       here.id, ls.kernelTime/1E9, ls.commTime/1E9);
                    team.barrier();
                }

                if (here.id == 0) {
                    finish Rail.asyncCopy(ls.clusters, 0, ansRef, 0, ls.clusters.size);
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
            val pts = new Rail[Float](pointsPerPlace * dim, (Long)=> rand.nextFloat());
            pts
        };

        val start = System.nanoTime();
        val clusters = computeClusters(pg, initPoints, pointsPerPlace, dim, numClusters, iterations, epsilon, verbose);
        val stop = System.nanoTime();
        Console.OUT.printf("TOTAL_TIME: %.3f seconds\n", (stop-start)/1e9);

        if (verbose) {
            Console.OUT.println("\nFinal results:");
            printPoints(clusters, numClusters, dim);
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
