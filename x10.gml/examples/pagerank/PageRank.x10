/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 *  (C) Copyright Sara Salem Hamouda 2014.
 */

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.Vector;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistMap;

import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistBlockMatrix;

import x10.util.resilient.DistObjectSnapshot;
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
    static val pN:Long = 1;
    public val iterations:Long;
    public val alpha:Double= 0.85;

    public val G:DistBlockMatrix{self.M==self.N};
    public val P:DupVector(G.N);
    public val E:Vector(G.N);
    public val U:Vector(G.N);

    // Temp data
    val GP:DistVector(G.N); // Distributed version of G*P
    val vGP:Vector(G.N);  // Used to collect GP and store in dense format (single column)
    
    var vP:Vector(G.N);   //result vector

    var paraRunTime:Long = 0;
    var seqTime:Long = 0;
    var bcastTime:Long = 0;
    var gatherTime:Long = 0;

    var iter:Long;
    
    private val chkpntIterations:Long;
    private val nzd:Double;    
    
    private var G_snapshot:DistObjectSnapshot;
    
    public def this(
            g:DistBlockMatrix{self.M==self.N}, 
            p:DupVector(g.N), 
            e:Vector(g.N), 
            u:Vector(g.N), 
            it:Long,
            sparseDensity:Double,
            chkpntIter:Long,
            places:PlaceGroup) {
        Debug.assure(DistGrid.isVertical(g.getGrid(), g.getMap()), 
                "Input block matrix g does not have vertical distribution.");
        G = g;
        P = p as DupVector(G.N);
        E = e as Vector(G.N); 
        U = u as Vector(G.N);
        iterations = it;
        
        GP = DistVector.make(G.N, G.getAggRowBs(), places);//G must have vertical distribution
        vGP = Vector.make(G.N);

        vP = P.local();
        
        chkpntIterations = chkpntIter;
        nzd = sparseDensity;
    }

    public static def make(gN:Long, nzd:Double, it:Long, numRowBs:Long, numColBs:Long, chkpntIter:Long, places:PlaceGroup) {
        //---- Distribution---
        val numRowPs = places.size();
        val numColPs = 1;
        
        val g = DistBlockMatrix.makeSparse(gN, gN, numRowBs, numColBs, numRowPs, numColPs, nzd, places);
        val p = DupVector.make(gN, places);
        val e = Vector.make(gN);
        val u = Vector.make(gN);
        return new PageRank(g, p, e, u, it, nzd, chkpntIter, places);
    }
    
    public static def make(gridG:Grid, blockMap:DistMap, nzd:Double, it:Int, chkpntIter:Long, places:PlaceGroup) {
        //gridG, distG, gridP, gridE and gridU are remote captured in all places
        val g = DistBlockMatrix.makeSparse(gridG, blockMap, nzd, places) as DistBlockMatrix(gridG.M, gridG.M);
        val p = DupVector.make(gridG.N, places) as DupVector(g.N);
        val e = Vector.make(gridG.N) as Vector(g.N);
        val u = Vector.make(gridG.N) as Vector(g.N);
        return new PageRank(g, p, e, u, it, nzd, chkpntIter, places);
    }    
    
    public def init():void {
        G.initRandom();
        P.initRandom();
        E.initRandom();
        U.initRandom();
    }

    public def run():Vector(G.N) {
        new ResilientExecutor(chkpntIterations).run(this);

        val mulTime = GP.getCalcTime();

        Console.OUT.printf("Gather: %d ms Bcast: %d ms Mul: %d ms\n", gatherTime, bcastTime, mulTime);
        Console.OUT.flush();
        
        return vP;
    }

    public def printInfo() {
        val nzc:Float =  G.getTotalNonZeroCount() as Float;
        val nzd:Float =  nzc / (G.M * G.N as Float);
        Console.OUT.printf("Input Matrix G:(%dx%d), partition:(%dx%d) blocks, ",
                G.M, G.N, G.getGrid().numRowBlocks, G.getGrid().numColBlocks);
        Console.OUT.printf("distribution:(%dx%d), nonzero density:%f count:%f\n", 
                Place.numPlaces(), 1,  nzd, nzc);

        Console.OUT.printf("Input duplicated vector P(%d), duplicated in all places\n", P.M);

        Console.OUT.printf("Input vector E(%d)\n", E.M);

        Console.OUT.printf("Input vector U(%d)\n", U.M);

        Console.OUT.flush();
    }
    
    public def isFinished():Boolean {
        return iter >= iterations;
    }


    public def step():void{
        val startPar = Timer.milliTime();
        GP.mult(G, P).scale(alpha);
        paraRunTime += (Timer.milliTime() - startPar);
    
        val startGather = Timer.milliTime();
        GP.copyTo(vGP);     // dist -> local dense
        gatherTime += (Timer.milliTime() - startGather);
    
        val startSeq = Timer.milliTime();
        vP.scale(U.dotProd(vP), E).scale(1-alpha).cellAdd(vGP);
        seqTime += (Timer.milliTime() - startSeq);
    
        val startBcast = Timer.milliTime();
        P.sync(); // broadcast
        bcastTime += (Timer.milliTime() - startBcast);
        
        iter++;
    }

    public def checkpoint(store:ResilientStoreForApp):void {
        store.startNewSnapshot();
        finish {
            async {
                if (G_snapshot == null)
                    G_snapshot = G.makeSnapshot();
                store.save(G, G_snapshot, true);
            }
            async store.save(P);
            async store.save(E);
            async store.save(U);
        }
        store.commit();
    }

    public def restore(newPlaces:PlaceGroup, store:ResilientStoreForApp, lastCheckpointIter:Long):void {
        val newRowPs = newPlaces.size();
        val newColPs = 1;
        Console.OUT.println("Going to restore PageRank app, newRowPs["+newRowPs+"], newColPs["+newColPs+"] ...");
        G.remakeSparse(newRowPs, newColPs, nzd, newPlaces);
        P.remake(newPlaces);
        vP = P.local();
        
        GP.remake(G.getAggRowBs(), newPlaces);
    
        store.restore();
        
        iter = lastCheckpointIter;
        Console.OUT.println("Restore succeeded. Restarting from iteration["+iter+"] ...");
    }
    
    public def getMaxIterations():Long {
        return iterations;
    }
}
