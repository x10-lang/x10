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
import x10.util.ArrayList;
import x10.util.HashMap;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.Random;
import x10.util.Team;
import x10.util.foreach.Block;
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
 * Points are stored in each Place in an Array_2[Float].
 *
 * For the highest throughput and most scalable implementation of
 * the KMeans algorithm in X10 for Native X10, see KMeans.x10 in the
 * X10 Benchmarks Suite (separate download from x10-lang.org).
 */
public class ResilientKMeansSPMD {

    static class LocalState {
        val points:Array_2[Float];
        val oldClusters:Array_2[Float];
        val clusters:Array_2[Float];
        val clusterCounts:Rail[Int];
        val numPoints:Long;
        val numClusters:Long;
        val epsilon:Float;
        val dim:Long;
        val team:Team;
        var kernelTime:Long = 0;
        var commTime:Long = 0;
        var converged:Boolean = false;
        val verbose:Boolean;

        def this(team:Team, initPoints:(Place)=>Array_2[Float], dim:Long, numClusters:Long,
                 epsilon:Float, verbose:Boolean) {
            points = initPoints(here);
            oldClusters = new Array_2[Float](numClusters, dim);
            val tmp = points; // hack around escaping this in constructor
            clusters  = new Array_2[Float](numClusters, dim, (i:long, j:long)=>tmp(i,j));
            clusterCounts = new Rail[Int](numClusters);
            numPoints = points.numElems_1;
            this.numClusters = numClusters;
            this.dim = dim;
            this.epsilon = epsilon;
            this.team = team;
            this.verbose = verbose;
        }

        def isFinished() = converged;

        def step():void {
            // Prepare for computation
            Array.copy(clusters, oldClusters);
            clusters.raw().clear();
            clusterCounts.clear();

            // Primary kernel: for every point, determine current closest
            //                 cluster and assign the point to that cluster.
            kernelTime -= System.nanoTime();
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
            kernelTime += System.nanoTime();

            // Reduction to accumulate new centroids and counts across all places
            commTime -= System.nanoTime();
            team.allreduce(clusters.raw(), 0L, clusters.raw(), 0L, clusters.raw().size, Team.ADD);
            team.allreduce(clusterCounts, 0L, clusterCounts, 0L, clusterCounts.size, Team.ADD);
            commTime += System.nanoTime();

            // Normalize to compute new cluster centroids
            for (k in 0..(numClusters-1)) {
                if (clusterCounts(k) > 0) {
                    for (d in 0..(dim-1)) clusters(k,d) /= clusterCounts(k);
                }
            }

            // Test for convergence
            converged = false;
            for ([i,j] in clusters.indices()) {
                if (Math.abs(oldClusters(i,j) - clusters(i,j)) > epsilon) {
                    return; // leaves converged false
                }
            }
            converged = true;

            if (here.id==0 && verbose) {
//                Console.OUT.println("Iteration: "+iter); // FIXME: should be stored in ResilientApp
                printPoints(clusters);
            }
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
            Console.OUT.println("TODO: implement checkpoint!");
            val map = new HashMap[String,Cloneable]();
            // Need to checkpoint: (a) points at each place (read only)
            //                     (b) optionally currentClusters (master place only...same everywhere).
            return map;
        }
             
        public def restore_local(restoreDataMap:HashMap[String,Cloneable], lastCheckpointIter:Long):void {
            Console.OUT.println("TODO: implement restore!");
            // Need to restore (a) points in addedPlaces and (b) currentClusters (master place).
        }

        public def remake(changes:ChangeDescription, newTeam:Team):void {
            Console.OUT.println("TODO: implement remake!");
            // Initialize plh[LocalState]
            // update team.
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

    static def computeClusters(initPoints:(Place)=>Array_2[Float], dim:Long,
                               numClusters:Long, iterations:Long, epsilon:Float, verbose:Boolean,
                               checkpointFreq:Long, sparePlaces:Long):Array_2[Float] {
        val startTime = System.currentTimeMillis();
        val executor = new SPMDResilientIterativeExecutor(checkpointFreq, sparePlaces, false, true);
        val pg = executor.activePlaces();
        val team = executor.team();

        // Initialize algorithm state in every active place.
        val plh = PlaceLocalHandle.make[LocalState](pg, ()=>{ new LocalState(team, initPoints, dim, numClusters, epsilon, verbose) });
        
        // Set initial cluster centroids to average of first k points in each place.
        finish {
            for (h in pg) at (h) async {
                val ls = plh();
                team.allreduce(ls.points.raw(), 0, ls.clusters.raw(), 0, numClusters*dim, Team.ADD);
                for ([i,j] in ls.clusters.indices()) {
                    ls.clusters(i,j) /= pg.size();
                }
            }
        }
 
        if (verbose) {
            Console.OUT.println("Initial clusters: ");
            printPoints(plh().clusters);
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
            Option("n","num","quantity of points"),
            Option("s","spare","number of spare places"),
            Option("k","checkpointFreq","number of interations between checkpoints")
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
            val pts = new Array_2[Float](pointsPerPlace, dim, (Long,Long)=> rand.nextFloat());
            pts
        };

        val start = System.nanoTime();
        val clusters = computeClusters(initPoints, dim, numClusters, iterations, epsilon, verbose, checkpointFreq, sparePlaces);
        val stop = System.nanoTime();
        Console.OUT.printf("TOTAL_TIME: %.3f seconds\n", (stop-start)/1e9);

        if (verbose) {
            Console.OUT.println("\nFinal results:");
            printPoints(clusters);
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
