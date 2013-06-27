/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */
package pagerank;

import x10.util.Timer;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
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
 * <p> 4) Iteration
 * <p>
 * <p> Input matrix G is partitioned into (numRowBsG &#42 numColBsG) blocks. All blocks
 * are distributed to (Place.MAX_PLACES, 1) places, or vertical distribution.
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
	public val iteration:Long;
	public val alpha:Double= 0.85;

	public val G:DistBlockMatrix{self.M==self.N};
	public val P:DupVector(G.N);
	public val E:Vector(G.N);
	public val U:Vector(G.N);

	// Temp data
	val GP:DistVector(G.N); // Distributed version of G*P
	val vGP:Vector(G.N);  // Used to collect GP and store in dense format (single column)
	
	// Time profiling
	var tt:Long = 0;
	var ct:Long = 0;
	
	public def this(
			g:DistBlockMatrix{self.M==self.N}, 
			p:DupVector(g.N), 
			e:Vector(g.N), 
			u:Vector(g.N), 
			it:Int) {
		Debug.assure(DistGrid.isVertical(g.getGrid(), g.getMap()), 
				"Input block matrix g does not have vertical distribution.");
		G = g;
		P = p as DupVector(G.N);
		E = e as Vector(G.N); 
		U = u as Vector(G.N);
		iteration = it;
		
		GP    = DistVector.make(G.N, G.getAggRowBs());//G must have vertical distribution
		vGP	  = Vector.make(G.N);
	}

	public static def make(gN:Long, nzd:Double, it:Int, numRowBs:Int, numColBs:Int) {

		//---- Distribution---
		val numRowPs = Place.MAX_PLACES;
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
		var seqtime:Long =0;
		var comtime:Long =0;
		var stt:Long =0;
		var UP:Double=0;
		val vP = P.local() as Vector(G.N);
		tt = P.getCommTime();
		Debug.flushln("Start parallel PageRank at "+tt);	
		val st = Timer.milliTime();		
		for (var i:Long=0; i<iteration; i++) {
			
			GP.mult(G, P, false).scale(alpha);
			
			stt = Timer.milliTime();
			GP.copyTo(vGP);     // dist -> local dense
			comtime += Timer.milliTime()-stt;
			
			stt = Timer.milliTime();
			vP.mult(E, U.dotProd(vP)).scale(1-alpha).cellAdd(vGP);
			seqtime += Timer.milliTime() - stt;
			
			stt = Timer.milliTime();
			P.sync(); // broadcast
			comtime += Timer.milliTime() - stt;
		}
		tt += Timer.milliTime() - st;
		val pmultime = GP.getCalcTime();
		val commtime = GP.getCommTime() + P.getCommTime();
		Console.OUT.printf("Total comm Time:%d ms, Gather:%d ms Bcast:%d ms\n", comtime, GP.getCommTime(), P.getCommTime());
		Console.OUT.printf("G:%d PageRank total runtime for %d iter: %d ms, ", G.M, iteration, tt );
		Console.OUT.printf("comm: %d ms, mult time: %d seq calc: %d ms\n", commtime, pmultime, seqtime);
		Console.OUT.flush();
		
		return vP;
	}

	public def printInfo() {
		val nzc:Float =  G.getTotalNonZeroCount() as Float;
		val nzd:Float =  nzc / (G.M * G.N as Float);
		Console.OUT.printf("Input Matrix G:(%dx%d), partition:(%dx%d) blocks, ",
				G.M, G.N, G.getGrid().numRowBlocks, G.getGrid().numColBlocks);
		Console.OUT.printf("distribution:(%dx%d), nonzero density:%f count:%f\n", 
				Place.MAX_PLACES, 1,  nzd, nzc);

		Console.OUT.printf("Input duplicated vector P(%d), duplicated in all places\n", P.M);

		Console.OUT.printf("Input vector E(%d)\n", E.M);

		Console.OUT.printf("Input vector U(%d)\n", U.M);

		Console.OUT.flush();
	}
}
