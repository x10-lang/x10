/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 *  (C) Copyright Sara Salem Hamouda 2014.
 */

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.Vector;
import x10.matrix.distblock.DistGrid;

import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistBlockMatrix;
import x10.util.resilient.ResilientIterativeApp;
import x10.util.resilient.ResilientExecutor;
import x10.util.resilient.ResilientStoreForApp;

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
public class PageRank implements ResilientIterativeApp {
    public val iterations:Long;
    public val alpha:ElemType= 0.85 as ElemType;

    /** Google Matrix or link structure */
    public val G:DistBlockMatrix{self.M==self.N};
    /** PageRank vector */
    public val P:DupVector(G.N);
    /** Personalization vector */
    public val U:DistVector(G.N);

    /** temp data: G * P */
    val GP:DistVector(G.N);
    
    public var paraRunTime:Long = 0;
    public var commTime:Long;
    var seqTime:Long = 0;

    var iter:Long;
    
    private val chkpntIterations:Long;
    private val nzd:Float;
    private var places:PlaceGroup;

    public def this(
            g:DistBlockMatrix{self.M==self.N}, 
            p:DupVector(g.N), 
            u:DistVector(g.N), 
            it:Long,
            sparseDensity:Float,
            chkpntIter:Long,
            places:PlaceGroup) {
        Debug.assure(DistGrid.isVertical(g.getGrid(), g.getMap()), 
                "Input block matrix g does not have vertical distribution.");
        G = g;
        P = p as DupVector(G.N);
        U = u as DistVector(G.N);
        iterations = it;
        
        GP = DistVector.make(G.N, G.getAggRowBs(), places);//G must have vertical distribution

        chkpntIterations = chkpntIter;
        nzd = sparseDensity;
        this.places = places;
    }

    public static def make(gN:Long, nzd:Float, it:Long, numRowBs:Long, numColBs:Long, chkpntIter:Long, places:PlaceGroup) {
        //---- Distribution---
        val numRowPs = places.size();
        val numColPs = 1;
        
        val g = DistBlockMatrix.makeSparse(gN, gN, numRowBs, numColBs, numRowPs, numColPs, nzd, places);
        val p = DupVector.make(gN, places);
        val u = DistVector.make(gN, g.getAggRowBs(), places);
        return new PageRank(g, p, u, it, nzd, chkpntIter, places);
    } 
    
    public def init(nzd:Float):void {
        G.initRandom();
        val normalization = 1.0 / (0.5 * nzd * G.N);
        G.scale(normalization);
        
        // initialize pagerank to personalization vector
        P.local().initRandom();
        val sum = P.local().sum();
        P.local().cellDiv(sum);
        P.sync();
        U.copyFrom(P.local());
    }

    public def run():Vector(G.N) {
        new ResilientExecutor(chkpntIterations, places).run(this);

        paraRunTime = P.getCalcTime() + GP.getCalcTime();
        commTime = P.getCommTime() + GP.getCommTime();
        
        return P.local();
    }

    public def printInfo() {
        val nzc =  G.getTotalNonZeroCount() ;
        val nzd =  nzc / (G.M * G.N);
        Console.OUT.printf("Input Matrix G:(%dx%d), partition:(%dx%d) blocks, ",
                G.M, G.N, G.getGrid().numRowBlocks, G.getGrid().numColBlocks);
        Console.OUT.printf("distribution:(%dx%d), nonzero density:%f count:%f\n", 
                Place.numPlaces(), 1,  nzd, nzc);

        Console.OUT.printf("Input duplicated vector P(%d), duplicated in all places\n", P.M);

        Console.OUT.printf("Input vector U(%d)\n", U.M);

        Console.OUT.flush();
    }
    
    public def isFinished():Boolean {
        return iter >= iterations;
    }

    public def step():void{
        GP.mult(G, P).scale(alpha);
    
        val teleport = U.dot(P) * (1-alpha);
        GP.copyTo(P.local());
        val startSeq = Timer.milliTime();
        P.local().cellAdd(teleport);
        seqTime += (Timer.milliTime() - startSeq);
        P.sync();
    
        iter++;
    }

    public def checkpoint(store:ResilientStoreForApp):void {
        store.startNewSnapshot();
        store.saveReadOnly(G);
        store.saveReadOnly(U);
        store.save(P);
        store.commit();
    }

    public def restore(newPlaces:PlaceGroup, store:ResilientStoreForApp, lastCheckpointIter:Long):void {
        val newRowPs = newPlaces.size();
        val newColPs = 1;
        Console.OUT.println("Going to restore PageRank app, newRowPs["+newRowPs+"], newColPs["+newColPs+"] ...");
        G.remakeSparse(newRowPs, newColPs, nzd, newPlaces);
        U.remake(G.getAggRowBs(), newPlaces);
        P.remake(newPlaces);
        
        GP.remake(G.getAggRowBs(), newPlaces);
    
        store.restore();
        
        iter = lastCheckpointIter;
        Console.OUT.println("Restore succeeded. Restarting from iteration["+iter+"] ...");
    }
}
