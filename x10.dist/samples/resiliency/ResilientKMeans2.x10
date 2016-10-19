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
import x10.util.ArrayList;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.Pair;
import x10.util.Random;
import x10.util.resilient.iterative.*;
import x10.util.resilient.localstore.ResilientStore;
import x10.util.resilient.localstore.Cloneable;
import x10.util.resilient.localstore.Snapshottable;
import x10.util.Timer;

/**
 * A resilient distributed implementation of KMeans clustering
 * created by augmenting KMeans.x10 to use the ResilientExecutor
 * framework.
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
//Test Commands
//KILL_PLACES=4 KILL_STEPS=12 X10_RESILIENT_MODE=1 mpirun -np 6 -am ft-enable-mpi ./RunKMeans.mpi -s 1 -k 5 -v
//KILL_PLACES=4 KILL_STEPS=12 X10_RESILIENT_MODE=1 X10_NPLACES=6 ./RunKMeans.sock -s 1 -k 5 -v
public class ResilientKMeans2 {

    /*
     * The local state consists of the points assigned to
     * this place and scratch storage for computing the new
     * cluster centroids based on the current assignment
     * of points to clusters.
     */
    static class LocalState implements x10.io.Unserializable {
        var points:Array_2[Float];
        var clusters:Array_2[Float];
        var clusterCounts:Rail[Int];
        var numPoints:Long;
        var numClusters:Long;
        var dim:Long;

        def this(initPoints:(Place)=>Array_2[Float], dim:Long, numClusters:Long) {
            points = initPoints(here);
            clusters  = new Array_2[Float](numClusters, dim);
            clusterCounts = new Rail[Int](numClusters);
            numPoints = points.numElems_1;
            this.numClusters = numClusters;
            this.dim = dim;
        }
        
        //should be used only for recovery
        //the data will be initialized from a checkpoint
        def this() {        	
        }
        
        def localStep(currentClusters:Array_2[Float]) {
            // clear scratch storage to prepare for this step
            clusters.raw().clear();
            clusterCounts.clear();

            // Primary kernel: for every point, determine current closest
            //                 cluster and assign the point to that cluster.
            Block.for(mine:LongRange in 0..(numPoints-1)) {
                val scratchClusters = new Array_2[Float](numClusters, dim);
                val scratchClusterCounts = new Rail[Int](numClusters);
                for (p in mine) {
                    var closest:Long = -1;
                    var closestDist:Float = Float.MAX_VALUE;
                    for (k in 0..(numClusters-1)) {
                        var dist : Float = 0;
                        for (d in 0..(dim-1)) {
                            val tmp = points(p,d) - currentClusters(k,d);
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
        }
        
        
    }
    
    //LocalState is Unserializable, we can not use it for checkpointing
    static class LocalStateSnapshot(points:Array_2[Float], numClusters:Long, dim:Long) implements Cloneable {        
    	public def clone():Cloneable {
        	return new LocalStateSnapshot(points, numClusters, dim);        	
        }
    }

    static class DistState implements Snapshottable {
    	val lsPLH:PlaceLocalHandle[LocalState];
        var places:PlaceGroup;
    
        public def this(lsPLH:PlaceLocalHandle[LocalState], places:PlaceGroup) {
            this.lsPLH = lsPLH;
            this.places = places;
        }
    
        public def makeSnapshot_local() {
        	return new LocalStateSnapshot(lsPLH().points, lsPLH().numClusters, lsPLH().dim);
        }
    
        public def restoreSnapshot_local(o:Cloneable) {
    	    val other = o as LocalStateSnapshot;
    	    lsPLH().points = other.points;    	    
    	    lsPLH().numPoints = other.points.numElems_1;
    	    lsPLH().numClusters = other.numClusters;
    	    lsPLH().dim = other.dim;
    	    
    	    lsPLH().clusters  = new Array_2[Float](other.numClusters, other.dim);
    	    lsPLH().clusterCounts = new Rail[Int](other.numClusters);
        }
        
        public def remake(newPlaces:PlaceGroup, newAddedPlaces:ArrayList[Place]) {
        	this.places = newPlaces;
        	for (p in newAddedPlaces) {
        		PlaceLocalHandle.addPlace[LocalState](lsPLH, p, ()=>new LocalState());
        	}
        }
    }
    
    static class KMeansMaster implements x10.io.Unserializable, GlobalResilientIterativeApp {
        val distState:DistState;
        var currentClusters:Array_2[Float];
        val newClusters:Array_2[Float];
        val newClusterCounts:Rail[Int];
        var pg:PlaceGroup;
        val numClusters:Long;
        val dim:Long;
        val epsilon:Float;
        var converged:Boolean = false;
    
        //master only checkpointing data
        var ckptCurrentClusters:Array_2[Float];

        def this(lsPLH:PlaceLocalHandle[LocalState], pg:PlaceGroup, epsilon:Float) {
           this.distState = new DistState(lsPLH, pg);
           this.pg = pg;
           this.epsilon = epsilon;
           this.numClusters = lsPLH().numClusters;
           this.dim = lsPLH().dim;
           currentClusters = new Array_2[Float](numClusters, dim);
           newClusters = new Array_2[Float](numClusters, dim);
           newClusterCounts = new Rail[Int](numClusters);
        }

        def setInitialCentroids() {
            finish {
                for (p in pg) async {
                    val plh = distState.lsPLH; // don't capture this!
                    val tmp = at (p) { new Array_2[Float](plh().numClusters, plh().dim, (i:Long, j:Long) => { plh().points(i,j) }) };
                    atomic {
                        for ([i,j] in currentClusters.indices()) {
                            currentClusters(i,j) += tmp(i,j);
                        }
                    }
                }
            }
            val np = pg.numPlaces();
            for ([i,j] in currentClusters.indices()) {
                currentClusters(i,j) /= np;
            }
        }
        
        public def isFinished() = converged;

        // Perform one global step of the KMeans algorithm
        // by coordinating the localSteps in each place and
        // accumulating the resulting new cluster centroids.
        public def step() {
            finish {
                for (p in pg) async {
                    val shadowClusters = this.currentClusters; // avoid capture of KMeansMaster object in at!
                    val shadowPLH = this.distState.lsPLH;  // avoid capture of KMeansMaster object in at!
                    val partialResults = at (p) {
                        val ls = shadowPLH();
                        ls.localStep(shadowClusters);
                        Pair[Array_2[Float],Rail[Int]](ls.clusters, ls.clusterCounts)
                    };
                    atomic {
                        for ([i,j] in newClusters.indices()) {
                            newClusters(i,j) += partialResults.first(i,j);
                        }
                        for (i in newClusterCounts.range()) {
                            newClusterCounts(i) += partialResults.second(i);
                        }
                    }
                }
            }

            // Normalize to compute new cluster centroids
            for (k in 0..(numClusters-1)) {
                if (newClusterCounts(k) > 0) {
                    for (d in 0..(dim-1)) newClusters(k,d) /= newClusterCounts(k);
                }
            }

            // Test for convergence
            var didConverge:Boolean = true;
            for ([i,j] in newClusters.indices()) {
                if (Math.abs(currentClusters(i,j) - newClusters(i,j)) > epsilon) {
                    didConverge = false;
                    break;
                }
            }
            converged = didConverge;

            // Prepare for next iteration
            Array.copy(newClusters, currentClusters);
            newClusters.raw().clear();
            newClusterCounts.clear();
        }
        
        //Sara: FIXME: the executor starts a fan-out to every place, although we only checkpoint data at the master place        
        public def checkpoint(store:ApplicationSnapshotStore) {        	
        	store.saveReadOnly("distState", distState);        	
        	ckptCurrentClusters = new Array_2(currentClusters);
        	//Console.OUT.println("TODO: implement checkpoint!");
            // Need to checkpoint: (a) points at each place (read only)
            //                     (b) currentClusters (master place only...same everywhere).
        }
        
        public def remake(newPlaces:PlaceGroup, newAddedPlaces:ArrayList[Place]) {
        	this.pg = newPlaces;        	
        	distState.remake(newPlaces, newAddedPlaces);
        	currentClusters = new Array_2(ckptCurrentClusters);
        }

        /* Sara: Restore is done automatically in the Global view executor
        public def restore(newPlaces:PlaceGroup, store:ApplicationSnapshotStore,
                    lastCheckpointIter:Long, newAddedPlaces:ArrayList[Place]):void {
            Console.OUT.println("TODO: implement restore!");
            // Need to restore (a) points in addedPlaces and (b) currentClusters (master place).
        }
        */
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

    static def computeClusters(pg:PlaceGroup, initPoints:(Place)=>Array_2[Float], dim:Long,
                               numClusters:Long, iterations:Long, epsilon:Float, verbose:Boolean,
                               checkpointFreq:Long, resilientStore:ResilientStore):Array_2[Float] {

    	val startTime = Timer.milliTime(); //the executor takes milli time.  
    	
        // Initialize LocalState in every Place
        val localPLH = PlaceLocalHandle.make[LocalState](pg, ()=>{ new LocalState(initPoints, dim, numClusters) });

        // Initialize algorithm state
        val master = new KMeansMaster(localPLH, pg, epsilon);
        master.setInitialCentroids();

        if (verbose) {
            Console.OUT.println("Initial clusters: ");
            printPoints(master.currentClusters);
        }

        new GlobalResilientIterativeExecutor(checkpointFreq, resilientStore).run(master, startTime);     
        
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

        val checkpointFreq = opts("-k",-1);
        val sparePlaces = opts("-s",0);
        
        Console.OUT.println("points: "+numPoints+" clusters: "+numClusters+" dim: "+dim);

        var pg:PlaceGroup = Place.places();
        var resilientStore:ResilientStore = null;
        if (x10.xrx.Runtime.RESILIENT_MODE > 0 && sparePlaces > 0) {
        	resilientStore = ResilientStore.make(sparePlaces);
        	pg = resilientStore.getActivePlaces();
        }
        else {
        	Console.OUT.println("\n--\n--\nWARNING: Running in non-resilient mode because "
        			+ "no spare places are available or X10_RESILIENT_MODE=0\n--\n--\n");
        }
        
        val pointsPerPlace = numPoints / pg.size();
        val initPoints = (p:Place) => {
            val rand = new x10.util.Random(p.id);
            val pts = new Array_2[Float](pointsPerPlace, dim, (Long,Long)=> rand.nextFloat());
            pts
        };

        val start = System.nanoTime();
        val clusters = computeClusters(pg, initPoints, dim, numClusters, iterations, epsilon, verbose, checkpointFreq, resilientStore);
        val stop = System.nanoTime();
        Console.OUT.printf("TOTAL_TIME: %.3f seconds\n", (stop-start)/1e9);

        if (verbose) {
            Console.OUT.println("\nFinal results:");
            printPoints(clusters);
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
