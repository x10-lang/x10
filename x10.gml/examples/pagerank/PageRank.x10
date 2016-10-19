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

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.util.RandTool;
import x10.matrix.Vector;
import x10.matrix.distblock.DistGrid;
import x10.regionarray.Dist;

import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistBlockMatrix;
import x10.util.Team;
import x10.util.ArrayList;
import x10.util.HashMap;
import x10.util.resilient.localstore.*;
import x10.util.resilient.iterative.*;

/**
 * Parallel Page Rank algorithm based on GML distributed block matrix.
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
	static val DISABLE_ULFM_AGREEMENT = System.getenv("DISABLE_ULFM_AGREEMENT") != null && System.getenv("DISABLE_ULFM_AGREEMENT").equals("1");
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
    
    private val checkpointFreq:Long;
    private val nzd:Float;
    
    private var appTempDataPLH:PlaceLocalHandle[AppTempData];
    private val root:Place;
    private val places:PlaceGroup;
    private var team:Team;
    private val resilientStore:ResilientStore;
    
    public def this(
            edges:DistBlockMatrix{self.M==self.N},
            it:Long,
            tolerance:Float,
            sparseDensity:Float,
            chkpntIter:Long,
            places:PlaceGroup,
            team:Team,
            resilientStore:ResilientStore) {
        Debug.assure(DistGrid.isVertical(edges.getGrid(), edges.getMap()), 
                "Input edges matrix does not have vertical distribution.");

        G = edges;

        // divide the weight of each outgoing edge by the number of outgoing edges
        finish ateach(p in Dist.makeUnique(places)) {
            val colSums = Vector.make(edges.N);
            edges.colSumTo_local(colSums);

            val localBlockIter = edges.handleBS().iterator();
            while (localBlockIter.hasNext()) {
                val matrix = localBlockIter.next().getMatrix();
                for (j in 0..(matrix.N-1)) {
                    if (colSums(j) > 0) {
                        for (i in 0..(matrix.M-1)) {
                            matrix(i,j) /= colSums(j);
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

        checkpointFreq = chkpntIter;
        nzd = sparseDensity;
        root = here;
        this.places = places;
        this.team = team;
        this.resilientStore = resilientStore;
    }

    public static def make(gN:Long, nzd:Float, it:Long, tolerance:Float, numRowBs:Long, numColBs:Long, chkpntIter:Long, places:PlaceGroup, team:Team, resilientStore:ResilientStore) {
        val numRowPs = places.size();
        val numColPs = 1;
        val g = DistBlockMatrix.makeSparse(gN, gN, numRowBs, numColBs, numRowPs, numColPs, nzd, places, team);
        val pr = new PageRank(g, it, tolerance, nzd, chkpntIter, places, team, resilientStore);
        pr.initRandom(nzd);
        return pr;
    }
    
    private def initRandom(nzd:Float):void {
        finish ateach(Dist.makeUnique(places)) {
            G.initRandom_local();
            // TODO init personalization vector U
        }
    }

    public def run(startTime:Long):Vector(G.N) {
        val start = (startTime != 0)?startTime:Timer.milliTime();  
        assert (G.isDistVertical()) : "dist block matrix must have vertical distribution";
    
        appTempDataPLH = PlaceLocalHandle.make[AppTempData](places, ()=>new AppTempData());
    
        if (x10.xrx.Runtime.x10rtAgreementSupport() && !DISABLE_ULFM_AGREEMENT){
            new SPMDResilientIterativeExecutorULFM(checkpointFreq, resilientStore, true).run(this, start);
        }
        else {
            new SPMDResilientIterativeExecutor(checkpointFreq, resilientStore, true).run(this, start);
        }

        return P.local();
    }

    public def printInfo() {
        val nzc =  G.getTotalNonZeroCount() ;
        Console.OUT.printf("Input Matrix G:(%dx%d), partition:(%dx%d) blocks, ",
                G.M, G.N, G.getGrid().numRowBlocks, G.getGrid().numColBlocks);
        Console.OUT.printf("distribution:(%dx%d), nonzero density:%f count:%d\n", 
                Place.numPlaces(), 1,  nzd, nzc);

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
            localMaxDelta = Math.max(localMaxDelta, GP(i) - localP(i));
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
    
    public def remake(newPlaces:PlaceGroup, newTeam:Team, newAddedPlaces:ArrayList[Place]) {
    	this.team = newTeam;
        val newRowPs = newPlaces.size();
        val newColPs = 1;
        if (VERBOSE) Console.OUT.println(here + "Remake, newRowPs["+newRowPs+"], newColPs["+newColPs+"] ...");
        G.remakeSparse(newRowPs, newColPs, nzd, newPlaces, newAddedPlaces);	
        //U.remake(G.getAggRowBs(), newGroup, newTeam, newAddedPlaces);
        P.remake(newPlaces, newTeam, newAddedPlaces);
        GP.remake(G.getAggRowBs(), newPlaces, newTeam, newAddedPlaces);
        
        for (sparePlace in newAddedPlaces){
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
