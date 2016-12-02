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
import x10.util.Pair;
import x10.util.Random;

/**
 * A distributed implementation of KMeans clustering.
 *
 * Intra-place concurrency is exposed via Foreach.
 *
 * A PlaceLocalHandle is used to store the local state of
 * the algorithm which consists of the points the place
 * is responsible for assigning to clusters and scratch storage
 * for computing a step of the algorithm by determining which
 * cluster each point is currently assigned to and what the
 * new centroid of that cluster is.
 *
 * For the highest throughput and most scalable implementation of
 * the KMeans algorithm in X10 for Native X10, see KMeans.x10 in the
 * X10 Benchmarks Suite (separate download from x10-lang.org).
 */
public class KMeans {

    /*
     * The local state consists of the points assigned to
     * this place and scratch storage for computing the new
     * cluster centroids based on the current assignment
     * of points to clusters.
     */
    final static class LocalState implements x10.io.Unserializable {
        var points:Rail[Float];
        var clusters:Rail[Float];
        var clusterCounts:Rail[Int];
        var numPoints:Long;
        var numClusters:Long;
        var dim:Long;

        def this(initPoints:(Place)=>Rail[Float], numPoints:Long, dim:Long, numClusters:Long) {
            points = initPoints(here);
            clusters  = new Rail[Float](numClusters * dim);
            clusterCounts = new Rail[Int](numClusters);
            this.numPoints = numPoints;
            this.numClusters = numClusters;
            this.dim = dim;
        }

        // outlined from localStep to help JIT compiler
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

        def localStep(currentClusters:Rail[Float]) {
            // clear scratch storage to prepare for this step
            clusters.clear();
            clusterCounts.clear();

            // Primary kernel: for every point, determine current closest
            //                 cluster and assign the point to that cluster.
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
        }
    }

    static class KMeansMaster implements x10.io.Unserializable {
        val lsPLH:PlaceLocalHandle[LocalState];
        var currentClusters:Rail[Float];
        val newClusters:Rail[Float];
        val newClusterCounts:Rail[Int];
        var pg:PlaceGroup;
        val numClusters:Long;
        val dim:Long;
        val epsilon:Float;
        var converged:Boolean = false;
        var maxIterations:Long;
        var currentIteration:Long;

        def this(lsPLH:PlaceLocalHandle[LocalState], pg:PlaceGroup, epsilon:Float, iterations:Long) {
           this.lsPLH = lsPLH;
           this.pg = pg;
           this.epsilon = epsilon;
           this.numClusters = lsPLH().numClusters;
           this.dim = lsPLH().dim;
           currentClusters = new Rail[Float](numClusters * dim);
           newClusters = new Rail[Float](numClusters * dim);
           newClusterCounts = new Rail[Int](numClusters);
           maxIterations = iterations;
           currentIteration = 0;
        }

        def setInitialCentroids() {
            finish {
                for (p in pg) async {
                    val plh = this.lsPLH; // don't capture this!
                    val tmp = at (p) { new Rail[Float](plh().numClusters * plh().dim, (i:Long) => { plh().points(i) }) };
                    atomic {
                        for (i in currentClusters.range()) {
                            currentClusters(i) += tmp(i);
                        }
                    }
                }
            }
            val np = pg.numPlaces();
            for (i in currentClusters.range()) {
                currentClusters(i) /= np;
            }
        }

        public def isFinished() = converged || currentIteration >= maxIterations;

        // Perform one global step of the KMeans algorithm
        // by coordinating the localSteps in each place and
        // accumulating the resulting new cluster centroids.
        public def step() {
            finish {
                val masterGR = GlobalRef(this);
                val shadowClusters = this.currentClusters; // avoid capture of KMeansMaster object in at!
                val shadowPLH = this.lsPLH;  // avoid capture of KMeansMaster object in at!
                for (p in pg) at (p) async {
                    val ls = shadowPLH();
                    ls.localStep(shadowClusters);
                    val resultClusters = ls.clusters;
                    val resultCounts = ls.clusterCounts;
                    at (masterGR.home) async {
                        val master = masterGR();
                        atomic {
                            for (i in master.newClusters.range()) {
                                master.newClusters(i) += resultClusters(i);
                            }
                            for (i in master.newClusterCounts.range()) {
                                master.newClusterCounts(i) += resultCounts(i);
                            }
                        }
                    }
                }
            }

            // Normalize to compute new cluster centroids
            for (k in 0..(numClusters-1)) {
                if (newClusterCounts(k) > 0) {
                    for (d in 0..(dim-1)) newClusters(k*dim + d) /= newClusterCounts(k);
                }
            }

            // Test for convergence
            var didConverge:Boolean = true;
            for (i in newClusters.range()) {
                if (Math.abs(currentClusters(i) - newClusters(i)) > epsilon) {
                    didConverge = false;
                    break;
                }
            }
            converged = didConverge;
            currentIteration += 1;

            // Prepare for next iteration
            Rail.copy(newClusters, currentClusters);
            newClusters.clear();
            newClusterCounts.clear();
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

    static def computeClusters(activePlaces:PlaceGroup, initPoints:(Place)=>Rail[Float], numPoints:Long,
                               dim:Long, numClusters:Long, iterations:Long, epsilon:Float,
                               verbose:Boolean):Rail[Float] {
        // Initialize KMeans LocalState in active places.
        val localPLH = PlaceLocalHandle.make[LocalState](activePlaces, ()=>{ new LocalState(initPoints, numPoints, dim, numClusters) });

        // Initialize algorithm state
        val master = new KMeansMaster(localPLH, activePlaces, epsilon, iterations);
        master.setInitialCentroids();

        if (verbose) {
            Console.OUT.println("Initial clusters: ");
            printPoints(master.currentClusters, numClusters, dim);
        }

        // Perform iterative algorithm
        for (iter in 0..(iterations-1)) {
            master.step();
            if (master.isFinished()) break;
        }

        return master.currentClusters;
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
