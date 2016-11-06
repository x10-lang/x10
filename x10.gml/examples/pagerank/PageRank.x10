/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2016.
 *  (C) Copyright Sara Salem Hamouda 2014.
 */

import x10.regionarray.Dist;
import x10.util.Random;
import x10.util.Timer;

import x10.matrix.ElemType;
import x10.matrix.util.Debug;
import x10.matrix.Vector;
import x10.matrix.distblock.DistGrid;

import x10.matrix.block.SparseBlock;
import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.sparse.*;

import x10.util.Team;
import x10.util.ArrayList;
import x10.util.HashMap;
import x10.util.resilient.PlaceManager.ChangeDescription;
import x10.util.resilient.localstore.Cloneable;
import x10.util.resilient.localstore.Snapshottable;
import x10.util.resilient.iterative.*;

/**
 * Parallel PageRank algorithm based on GML distributed sparse block matrix.
 * <p> Input matrix G is partitioned into (numRowBsG &#42 numColBsG) blocks. All blocks
 * are distributed to (Place.numPlaces(), 1) places, or vertical distribution.
 * <p>[g_(0,0),           g_(0,1),           ..., g(0,numColBsG-1)]
 * <p>[g_(1,0),           g_(1,1),           ..., g(1,numColBsG-1)]
 * <p>......
 * <p>[g_(numRowBsG-1,0), g_(numRowBsG-1,1), ..., g(numRowBsG-1,numColBsG-1)]
 * <p>
 * <p> Vector P is partitioned into (numColBsG &#42 1) blocks, and replicated in all places.
 * <p>[p_(0)]
 * <p>[p_(1)]
 * <p>......
 * <p>[p_(numColBsG-1)]
 */
public class PageRank implements SPMDResilientIterativeApp {
	static val VERBOSE = System.getenv("PAGERANK_DEBUG") != null && System.getenv("PAGERANK_DEBUG").equals("1");
	
    public val tolerance:Float;
    public val iterations:Long;
    public val alpha:ElemType= 0.85 as ElemType;

    /** Google Matrix or link structure */
    public val G:DistBlockMatrix{self.M==self.N};
    /** PageRank vector */
    public val P:DupVector(G.N);
    /** Personalization vector */
    //public val U:DistVector(G.N);

    /** temp data: G * P */
    val GP:DistVector(G.N);
    
    /** Number of non-zeros in G */
    private val nnz:Float;

    private val executor:SPMDResilientIterativeExecutor;
    private var appTempDataPLH:PlaceLocalHandle[AppTempData];
    private val root:Place;
    private var places:PlaceGroup;
    private var team:Team;
    
    /**
     * Create a new PageRank instance
     * @param edges the edges matrix, where edges(i,j) == 1.0 if there is an
     *   outgoing link from j to i; 0.0 otherwise
     * @param it the number of iterations of PageRank to run
     * @param tolerance the value below which maximum change in page rank must
     *   fall for convergence to be reached (only has effect if iterations==0)
     * @param nnz number of non-zeros in edge matrix
     * @param executor the instance of the resilient iterative executor framework that is running the algorithm
     */
    public def this(
            edges:DistBlockMatrix{self.M==self.N},
            it:Long,
            tolerance:Float,
            nnz:Long,
            executor:SPMDResilientIterativeExecutor) {
        Debug.assure(DistGrid.isVertical(edges.getGrid(), edges.getMap()), 
                "Input edges matrix does not have vertical distribution.");

        G = edges;
        this.executor = executor;
        this.places = executor.activePlaces();
        this.team = executor.team();
        this.nnz = nnz;
        root = here;

        // divide the weight of each outgoing edge by the number of outgoing edges
        finish for (p in executor.activePlaces()) at (p) async {
            val colSums = Vector.make(edges.N);
            edges.colSumTo_local(colSums);

            val localBlockIter = edges.handleBS().iterator();
            while (localBlockIter.hasNext()) {
                val matrix = localBlockIter.next().getMatrix() as SparseCSC;
                for (j in 0..(matrix.N-1)) {
                    if (colSums(j) > 0) {
                        val compressedCol = matrix.getCol(j);
                        for (i in 0..(compressedCol.length-1)) {
                            compressedCol.setValue(i, compressedCol.getValue(i) / colSums(j));
                        }
                    }
                    // TODO account for vertices with zero outgoing edges
                }
            }
        }

        P = DupVector.make(G.N, places, team);
        P.init(1.0);
        //U = DistVector.make(G.N, g.getAggRowBs(), places, team);
        iterations = it;
        this.tolerance = tolerance;
        
        GP = DistVector.make(G.N, G.getAggRowBs(), places, team);//G must have vertical distribution
    }

    public static def makeRandom(gN:Long, nzd:Float, it:Long, tolerance:Float, numRowBs:Long, numColBs:Long, executor:SPMDResilientIterativeExecutor) {
        val numRowPs = executor.activePlaces().size();
        val numColPs = 1;
        val g = DistBlockMatrix.makeSparse(gN, gN, numRowBs, numColBs, numRowPs, numColPs, nzd, executor.activePlaces(), executor.team());
        val pr = new PageRank(g, it, tolerance, g.getTotalNonZeroCount(), executor);
        finish ateach(Dist.makeUnique(executor.activePlaces())) {
            g.initRandom_local();
            // TODO init personalization vector U
        }
        return pr;
    }

    /**
     * Initialize the Google Matrix G with a log-normal distribution of outward
     * links. Values for mu and sigma are taken from the Pregel paper.
     *
     * @see Malewicz et al. (2010)
     *   Pregel: a system for large-scale graph processing.
     *   http://dx.doi.org/10.1145/1807167.1807184
     */
    public static def makeLogNormal(gN:Long, it:Long, tolerance:Float, numRowBs:Long, numColBs:Long, executor:SPMDResilientIterativeExecutor) {
        val densityGuess = 0.079f;
        val numRowPs = executor.activePlaces().size();
        val numColPs = 1;
        val places = executor.activePlaces();
        val team = executor.team();
        
        val g = DistBlockMatrix.make(gN, gN, numRowBs, numColBs, numRowPs, numColPs, places, team);
        
        val mu = 4.0f as ElemType;
        val sigma = 1.3f as ElemType;

        val rand = new Random(); // TODO allow choice of random seed
        val vertexOutDegree = new Rail[Int](gN);
        var globalNnzGuess:Long = 0;
        for (i in 0..(gN-1)) {
            var X:ElemType = ElemType.MAX_VALUE;
            while (X >= ElemType.MAX_VALUE) {
              val Z = rand.nextGaussian();
              X = Math.exp(mu + sigma*Z);
            }
            vertexOutDegree(i) = Math.floor(X) as Int;
            globalNnzGuess += vertexOutDegree(i);
        }

        finish ateach(Dist.makeUnique(places)) {
            val grid = g.handleBS().getGrid();
            val itr = g.handleBS().getDistMap().buildBlockIteratorAtPlace(places.indexOf(here));
            val rand2 = new Random(); // TODO allow choice of random seed
            val invN = 1.0 / gN;
            while (itr.hasNext()) {
                val bid    = itr.next();
                val rowbid = grid.getRowBlockId(bid);
                val colbid = grid.getColBlockId(bid);
                val m      = grid.rowBs(rowbid);
                val n      = grid.colBs(colbid);
                val roff   = grid.startRow(rowbid);
                val coff   = grid.startCol(colbid);
                val nnzGuess = (m*127.1) as Long;
                val values = new ArrayList[ElemType](nnzGuess);
                val indices = new ArrayList[Long](nnzGuess);
                val offsets = new Rail[Long](n);
                val lengths = new Rail[Long](n);
                for (col in 0..(n-1)) {
                    offsets(col) = values.size();
                    val placeNonZeros = Math.min(m, (vertexOutDegree(col) * (m as Double / gN) + 0.5) as Long);
                    val meanSpacing = (m as Double) / placeNonZeros - 1;
                    var row:Long = 0;
                    for (i in 1..placeNonZeros) {
                        // random spacing to next non-zero. TODO should use Poisson distributed spacing
                        if (meanSpacing > 0.25) {
                            row += (0.5 * rand2.nextDouble() * 2 * meanSpacing) as Long;
                        }
                        if (row >= m) row -= m;
                        indices.add(row);
                        values.add(1.0);
                        row += 1;
                    }
                    lengths(col) = indices.size() - offsets(col);
                }
                val ca = new CompressArray(indices.toRail(), values.toRail(), values.size());
                val c1 = new Rail[Compress1D](n);
                for (col in 0..(n-1)) {
                    c1(col) = new Compress1D(offsets(col), lengths(col), ca);
                }
                val c2 = new Compress2D(c1);
                val block = new SparseBlock(rowbid, colbid, roff, coff, new SparseCSC(m, n, c2));
                g.handleBS().add(block);
            }
        }

        return new PageRank(g, it, tolerance, g.getTotalNonZeroCount(), executor);
    }

    public def run(startTime:Long):Vector(G.N) {
        val start = (startTime != 0)?startTime:Timer.milliTime();  
        assert (G.isDistVertical()) : "dist block matrix must have vertical distribution";
    
        appTempDataPLH = PlaceLocalHandle.make[AppTempData](places, ()=>new AppTempData());
        
        executor.run(this, start);

        return P.local();
    }

    public def printInfo() {
        val nzc =  G.getTotalNonZeroCount();
        Console.OUT.printf("Input Matrix G:(%dx%d), partition:(%dx%d) blocks, ",
                G.M, G.N, G.getGrid().numRowBlocks, G.getGrid().numColBlocks);
        Console.OUT.printf("distribution:(%dx%d), number of non-zeros:%d\n", 
                Place.numPlaces(), 1, nzc);

        Console.OUT.printf("Input duplicated vector P(%d), duplicated in all places\n", P.M);

        //Console.OUT.printf("Input vector U(%d)\n", U.M);

        Console.OUT.flush();
    }
    
    public def isFinished_local():Boolean {
        return (iterations <= 0 && appTempDataPLH().maxDelta < tolerance) || (iterations > 0 && appTempDataPLH().iter >= iterations);
    }

    public def step_local():void {
        GP.mult_local(G, P);
        GP.scale_local(alpha);
    
        //val teleport = U.dot_local(P) * (1.0f-alpha); // personalized
        val teleport = 1.0f-alpha;
        GP.cellAdd_local(teleport);

        val localP = P.local();
        var localMaxDelta:ElemType = 0.0 as ElemType;
        val offset = GP.getOffset();
        for (i in offset..(offset+GP.getSegSize()(places.indexOf(here))-1)) {
            localMaxDelta = Math.max(localMaxDelta, Math.abs(GP(i) - localP(i)));
        }
        appTempDataPLH().maxDelta = team.allreduce(localMaxDelta, Team.ADD);
        
        GP.copyTo_local(root, localP);  // only root will have copy of GP in P.local()        

        P.sync_local(root);

        appTempDataPLH().iter++;
    }

    public def getCheckpointData_local():HashMap[String,Cloneable] {
    	val map = new HashMap[String,Cloneable]();
    	if (appTempDataPLH().iter == 0) {
    		map.put("G", G.makeSnapshot_local());
    	}
    	//map.put("U", U.makeSnapshot_local());
    	map.put("P", P.makeSnapshot_local());
    	map.put("app", appTempDataPLH().makeSnapshot_local());
    	if (VERBOSE) Console.OUT.println(here + "Checkpointing at iter ["+appTempDataPLH().iter+"] maxDelta["+appTempDataPLH().maxDelta+"] ...");
    	return map;
    }
    
    public def restore_local(restoreDataMap:HashMap[String,Cloneable], lastCheckpointIter:Long) {
    	G.restoreSnapshot_local(restoreDataMap.getOrThrow("G"));
    	//U.restore_local(restoreDataMap.getOrThrow("U"));
    	P.restoreSnapshot_local(restoreDataMap.getOrThrow("P"));
    	appTempDataPLH().restoreSnapshot_local(restoreDataMap.getOrThrow("app"));
    	if (VERBOSE) Console.OUT.println(here + "Restore succeeded. Restarting from iteration["+appTempDataPLH().iter+"] maxDelta["+appTempDataPLH().maxDelta+"] ...");
    }
    
    public def remake(changes:ChangeDescription, newTeam:Team) {
        this.places = changes.newActivePlaces;
    	this.team = newTeam;
        val newRowPs = changes.newActivePlaces.size();
        val newColPs = 1;
        if (VERBOSE) Console.OUT.println(here + "Remake, newRowPs["+newRowPs+"], newColPs["+newColPs+"] ...");
        val nzd = nnz as Float / (G.M * G.N);
        // TODO remake using nnz instead of nzd
        G.remake(newRowPs, newColPs, changes.newActivePlaces, changes.addedPlaces);
        //U.remake(G.getAggRowBs(), changes.newActivePlaces, newTeam, changes.addedPlaces);
        P.remake(changes.newActivePlaces, newTeam, changes.addedPlaces);
        GP.remake(G.getAggRowBs(), changes.newActivePlaces, newTeam, changes.addedPlaces);
        
        for (sparePlace in changes.addedPlaces){
    		if (VERBOSE) Console.OUT.println("Adding place["+sparePlace+"] to appTempDataPLH ...");
    		PlaceLocalHandle.addPlace[AppTempData](appTempDataPLH, sparePlace, ()=>new AppTempData());
    	}
        if (VERBOSE) Console.OUT.println("Remake succeeded. Restarting from iteration["+appTempDataPLH().iter+"] ...");
    }
    
    class AppTempData implements Cloneable, Snapshottable {
        public var iter:Long;
        /** Maximum change in page rank from previous iteration */
        public var maxDelta:ElemType = 1.0 as ElemType;
    
        public def this() { }
        
        public def this(iter:Long, maxDelta:ElemType) { 
        	this.iter = iter;
        	this.maxDelta = maxDelta;
        }
        
        public def clone():Cloneable {
        	return new AppTempData(iter, maxDelta);
        }
        
        public def makeSnapshot_local() = this;
        
        public def restoreSnapshot_local(o:Cloneable) {        
        	val other = o as AppTempData;
        	this.iter = other.iter;
        	this.maxDelta = other.maxDelta;
        }
    }
}
