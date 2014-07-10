/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

package pagerank;

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.Vector;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistMap;

import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistBlockMatrix;

/**
 * Parallel Page Rank algorithm based on GML distributed block matrix.
 * 
 * Input parameters:
 * <p> 1) Sparse matrix G
 * <p> 4) Iterations
 * <p>
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
public class PageRank {
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
	
	public def this(
			g:DistBlockMatrix{self.M==self.N}, 
			p:DupVector(g.N), 
			e:Vector(g.N), 
			u:Vector(g.N), 
			it:Long) {
		Debug.assure(DistGrid.isVertical(g.getGrid(), g.getMap()), 
				"Input block matrix g does not have vertical distribution.");
		G = g;
		P = p as DupVector(G.N);
		E = e as Vector(G.N); 
		U = u as Vector(G.N);
		iterations = it;
		
		GP    = DistVector.make(G.N, G.getAggRowBs());//G must have vertical distribution
		vGP	  = Vector.make(G.N);
	}

	public static def make(gN:Long, nzd:Double, it:Long, numRowBs:Long, numColBs:Long) {
		//---- Distribution---
		val numRowPs = Place.numPlaces();
		val numColPs = 1;
		
		val g = DistBlockMatrix.makeSparse(gN, gN, numRowBs, numColBs, numRowPs, numColPs, nzd);
		val p = DupVector.make(gN);
		val e = Vector.make(gN);
		val u = Vector.make(gN);
		return new PageRank(g, p, e, u, it);
	}
	
	public static def make(gridG:Grid, blockMap:DistMap, nzd:Double, it:Int) {
		//gridG, distG, gridP, gridE and gridU are remote captured in all places
		val g = DistBlockMatrix.makeSparse(gridG, blockMap, nzd) as DistBlockMatrix(gridG.M, gridG.M);
		val p = DupVector.make(gridG.N) as DupVector(g.N);
		val e = Vector.make(gridG.N) as Vector(g.N);
		val u = Vector.make(gridG.N) as Vector(g.N);
		return new PageRank(g, p, e, u, it);
	}
	
	
	public def init():void {
		Debug.flushln("Start initialize input matrices");		
		G.initRandom();
		Debug.flushln("Dist sparse matrix initialization completes");		
		P.initRandom();
		Debug.flushln("Initialize duplicated dense matrix P completes");		
		E.initRandom();
		Debug.flushln("Initialize dense matrix E completes");		
		U.initRandom();
		Debug.flushln("Initialize dense matrix U completes");		
	}

	public def run():Vector(G.N) {
		var parTime:Long = 0;
		var seqTime:Long = 0;
        var bcastTime:Long = 0;
        var gatherTime:Long = 0;
        var totalTime:Long = 0;

		val vP = P.local() as Vector(G.N);
		totalTime -= Timer.milliTime();		
		for (1..iterations) {
			parTime -= Timer.milliTime();
			GP.mult(G, P).scale(alpha);
			parTime += Timer.milliTime();
			
			gatherTime -= Timer.milliTime();
			GP.copyTo(vGP);     // dist -> local dense
			gatherTime += Timer.milliTime();
			
			seqTime -= Timer.milliTime();
			vP.mult(E, U.dotProd(vP)).scale(1-alpha).cellAdd(vGP);
			seqTime += Timer.milliTime();
			
			bcastTime -= Timer.milliTime();
			P.sync(); // broadcast
			bcastTime += Timer.milliTime();
		}
		totalTime += Timer.milliTime();
		val mulTime = GP.getCalcTime();
		val commTime = bcastTime + gatherTime;
		Console.OUT.printf("Gather: %d ms Bcast: %d ms Mul: %d ms\n", gatherTime, bcastTime, mulTime);
		Console.OUT.printf("G:%d PageRank total runtime for %d iter: %d ms, ", G.M, iterations, totalTime);
		Console.OUT.printf("comm: %d ms, par calc: %d ms, seq calc: %d ms\n", commTime, parTime, seqTime);
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
}
