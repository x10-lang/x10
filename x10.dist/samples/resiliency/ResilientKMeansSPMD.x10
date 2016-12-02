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

import x10.util.HashMap;
import x10.util.foreach.Block;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.Random;
import x10.util.Team;
import x10.util.resilient.PlaceManager.ChangeDescription;
import x10.util.resilient.iterative.*;
import x10.util.resilient.localstore.Cloneable;

/**
 * A resilient distributed implementation of KMeans clustering
 * created by augmenting KMeansSPMD.x10 to use the
 * SPMDResilientIterativeExecutor framework.
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
public class ResilientKMeansSPMD {

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
        var currentIteration:Long = 0;
        var kernelTime:Long = 0;
        var commTime:Long = 0;
        var converged:Boolean = false;

        def this(team:Team, initPoints:(Place)=>Rail[Float], numPoints:Long,
                 dim:Long, numClusters:Long, epsilon:Float) {
            points = initPoints(here);
            clusters  = new Rail[Float](numClusters * dim);
            this.numPoints = numPoints;
            this.numClusters = numClusters;
            this.dim = dim;
            this.epsilon = epsilon;
            this.team = team;
            initializeScratchStorage();
        }

        private final def initializeScratchStorage() {
            if (currentClusters ==null) currentClusters = new Rail[Float](numClusters * dim);
            if (clusterCounts == null) clusterCounts = new Rail[Int](numClusters);
        }

        def this() { } // used to initialize an elastic/spare place before restore

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

            currentIteration += 1;

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

    static class ImmutableState(points:Rail[Float], numPoints:Long,
                                numClusters:Long, epsilon:Float, dim:Long) implements Cloneable {
        public def clone() {
            return new ImmutableState(points, numPoints, numClusters, epsilon, dim);
        }
    }

    static class MutableState(clusters:Rail[Float]) implements Cloneable {
        public def clone() {
            return new MutableState(clusters);
        }
    }

    static class KMeansApp implements SPMDResilientIterativeApp {
        val plh:PlaceLocalHandle[LocalState];

        def this(plh:PlaceLocalHandle[LocalState]) {
            this.plh = plh;
        }

        public def isFinished_local() = plh().isFinished();

        public def step_local() { plh().step(); }

        public def getCheckpointData_local():HashMap[String,Cloneable] {
            val map = new HashMap[String,Cloneable]();
            val ls = plh();
            if (ls.currentIteration == 0) {
                map.put("immutable", new ImmutableState(ls.points, ls.numPoints, ls.numClusters,
                                                        ls.epsilon, ls.dim));
            }
            map.put("mutable", new MutableState(ls.clusters));
            return map;
        }
             
        public def restore_local(restoreDataMap:HashMap[String,Cloneable], lastCheckpointIter:Long):void {
            val ls = plh();
            val immutable = restoreDataMap.getOrThrow("immutable") as ImmutableState;
            ls.points = immutable.points;
            ls.numPoints = immutable.numPoints;
            ls.numClusters = immutable.numClusters;
            ls.epsilon = immutable.epsilon;
            ls.dim = immutable.dim;
            val mutable = restoreDataMap.getOrThrow("mutable") as MutableState;
            ls.clusters = mutable.clusters;
            ls.currentIteration = lastCheckpointIter;
            ls.initializeScratchStorage();
        }

        public def remake(changes:ChangeDescription, newTeam:Team):void {
            for (np in changes.addedPlaces) {
                PlaceLocalHandle.addPlace[LocalState](plh, np, ()=>new LocalState());
            }
            changes.newActivePlaces.broadcastFlat(()=> {
                plh().team = newTeam;
            });
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

    static def computeClusters(initPoints:(Place)=>Rail[Float], numPoints:Long, dim:Long,
                               numClusters:Long, iterations:Long, epsilon:Float, verbose:Boolean,
                               checkpointFreq:Long, sparePlaces:Long):Rail[Float] {
        val startTime = System.currentTimeMillis();
        val executor = new SPMDResilientIterativeExecutor(checkpointFreq, sparePlaces, false, false);
        val pg = executor.activePlaces();
        val team = executor.team();

        // Initialize algorithm state in every active place.
        val plh = PlaceLocalHandle.make[LocalState](pg, ()=>{ new LocalState(team, initPoints, numPoints, dim, numClusters, epsilon) });
        
        // Set initial cluster centroids to average of first k points in each place.
        finish {
            val numPlaces = pg.size() as Float;
            for (h in pg) at (h) async {
                val ls = plh();
                team.allreduce(ls.points, 0, ls.clusters, 0, numClusters*dim, Team.ADD);
                for (i in ls.clusters.range()) {
                    ls.clusters(i) /= numPlaces;
                }
            }
        }
 
        if (verbose) {
            Console.OUT.println("Initial clusters: ");
            printPoints(plh().clusters, numPoints, dim);
        }

        if (hammer() != null) {
            executor.setHammer(hammer());
        }
        executor.run(new KMeansApp(plh), startTime);

        if (verbose) {
            for (p in executor.activePlaces()) {
                at (p) {
                    val ls = plh();
                    Console.OUT.printf("%d: computation %.3f s communication %.3f s\n",
                                       here.id, ls.kernelTime/1E9, ls.commTime/1E9);
                }
            }
        }

        return plh().clusters;
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
            Option("s","spare","number of spare places"),
            Option("k","checkpointFreq","number of interations between checkpoints"),
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
        val checkpointFreq = opts("-k",5);
        val sparePlaces = opts("-s",0);

        Console.OUT.println("points: "+numPoints+" clusters: "+numClusters+" dim: "+dim);

        val pointsPerPlace = numPoints / (Place.numPlaces() - sparePlaces);
        val initPoints = (p:Place) => {
            val rand = new x10.util.Random(p.id);
            val pts = new Rail[Float](pointsPerPlace * dim, (Long)=> rand.nextFloat());
            pts
        };

        val start = System.nanoTime();
        val clusters = computeClusters(initPoints, pointsPerPlace, dim, numClusters, iterations, epsilon, verbose, checkpointFreq, sparePlaces);
        val stop = System.nanoTime();
        Console.OUT.printf("TOTAL_TIME: %.3f seconds\n", (stop-start)/1e9);

        if (verbose) {
            Console.OUT.println("\nFinal results:");
            printPoints(clusters, numClusters, dim);
        }
    }

    // Saffolding for killing places during automated testing.
    static val hammer = new Cell[SimplePlaceHammer](null);
    public static def setHammerConfig(steps:String, places:String) {
        hammer() = new SimplePlaceHammer(steps, null, places);
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
