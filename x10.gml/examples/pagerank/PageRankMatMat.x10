/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */
package pagerank;

import x10.util.Timer;
//
import x10.matrix.util.Debug;
//
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

//
import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistMap;

import x10.matrix.distblock.DupBlockMatrix;
import x10.matrix.distblock.DistBlockMatrix;

import x10.matrix.distblock.DistDupMult;
//

/**
 * Parallel Page Rank algorithm based on GML distributed block matrix.
 * 
 * Input parameters:
 * <p> 1) Sparse matrix G
 * <p> 4) Iteration
 * <p>
 * <p> Input matrix G is partitioned into (numRowBsG &#42 numColBsG) blocks. All blocks
 * are distributed to (Place.numPlaces(), 1) places, or vertical distribution.
 * <p>[g_(0,0),           g_(0,1),           ..., g(0,numColBsG-1)]
 * <p>[g_(1,0),           g_(1,1),           ..., g(1,numColBsG-1)]
 * <p>......
 * <p>[g_(numRowBsG-1,0), g_(numRowBsG-1,1), ..., g(numRowBsG-1,numColBsG-1)]
 * <p>
 * <p> Matrix P is partitioned into (numColBsG &#42 1) blocks, and replicated in all places.
 * <p>[p_(0,0)]
 * <p>[p_(1,0)]
 * <p>......
 * <p>[p_(numColBsG-1,0)]
 * 
 * 
 * 
 */
public class PageRankMatMat {


	static val pN:Long = 1;
	public val iteration:Long;
	public val alpha:ElemType= 0.85 as ElemType;

	

	public val G:DistBlockMatrix;
	public val P:DupBlockMatrix(G.N, pN);
	public val E:BlockMatrix(G.N, P.N);
	public val U:BlockMatrix(P.N, G.N);

	// Temp data
	val GP:DistBlockMatrix(G.M, P.N); // Distributed version of G*P
	val denGP:DenseMatrix(G.N, P.N);  // Used to collect GP and store in dense format (single column)
	val blkP:BlockMatrix(G.N, P.N);
	val UP:BlockMatrix(P.N, P.N);
	
	// Time profiling
	var tt:Long = 0;
	var ct:Long = 0;
	
	public def this(g:DistBlockMatrix, p:DupBlockMatrix(g.N, pN), e:BlockMatrix(g.N, p.N), u:BlockMatrix(p.N, g.N), it:Int) {
		Debug.assure(DistGrid.isVertical(g.getGrid(), g.getMap()), 
				"Input block matrix g does not have vertical distribution.");
				
		G = g;
		P = p as DupBlockMatrix(G.N); 
		E = e as BlockMatrix(G.N,P.N); 
		U = u as BlockMatrix(P.N,G.N); 
		iteration = it;
		val gG = g.getGrid();
		val gP = p.getGrid();
		val rowPs = Place.numPlaces();
		
		val gridGP = new Grid(G.M, P.N, gG.rowBs, gP.colBs);
		val distGP = new DistGrid(gridGP, rowPs, 1);
		GP    = DistBlockMatrix.makeDense(gridGP, distGP.dmap) as DistBlockMatrix(G.M, P.N);
		
		denGP = DenseMatrix.make(G.M, P.N) as DenseMatrix(G.M, P.N); //Store gathered GP blocks in dense format
		blkP  = BlockMatrix.makeDense(gP) as BlockMatrix(G.N, P.N);  
		
		val gridUP = new Grid(P.N, P.N, gP.colBs, gP.colBs);
		UP    = BlockMatrix.makeDense(gridUP) as BlockMatrix(P.N, P.N);
	}

	public static def make(gM:Long, nzd:Float, it:Int, gRowBs:Int, gColBs:Int) {

		val gN = gM;
		val pColBs = 1;
		//---- Distribution---
		val gRowPs = Place.numPlaces();
		val gColPs = 1;
		
		val g = DistBlockMatrix.makeSparse(gM, gN, gRowBs, gColBs, gRowPs, gColPs, nzd) as DistBlockMatrix(gM,gN);
		val p = DupBlockMatrix.makeDense(gN, pN, gColBs, pColBs) as DupBlockMatrix(gN, pN);
		val e = BlockMatrix.makeDense(gN, pN, gColBs, pColBs) as BlockMatrix(gN, pN);
		val u = BlockMatrix.makeDense(pN, gN, pColBs, gColBs) as BlockMatrix(pN, gN);
		return new PageRankMatMat(g, p, e, u, it);
	}
	
	public static def make(gridG:Grid, blockMap:DistMap, gridP:Grid, gridE:Grid, gridU:Grid,  nzd:Float, it:Int) {
		//gridG, distG, gridP, gridE and gridU are remote captured in all places
		val g = DistBlockMatrix.makeSparse(gridG, blockMap, nzd) as DistBlockMatrix(gridG.M, gridG.N);
		val p = DupBlockMatrix.makeDense(gridP) as DupBlockMatrix(g.N, pN);
		val e = BlockMatrix.makeDense(gridE) as BlockMatrix(g.N, p.N);
		val u = BlockMatrix.makeDense(gridU) as BlockMatrix(p.N, g.N);
		return new PageRankMatMat(g, p, e, u, it);
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

	public def run():BlockMatrix {
		var seqtime:Long =0;
		var comtime:Long =0;
		var stt:Long =0;
		tt = P.getCommTime();
		Debug.flushln("Start parallel PageRank at "+tt);	
		val st = Timer.milliTime();		
		for (var i:Long=0; i<iteration; i++) {
			
			GP.mult(G, P).scale(alpha);
			
			stt = Timer.milliTime();
			GP.copyTo(denGP as DenseMatrix(GP.M,GP.N));     // dist -> local dense
			comtime += Timer.milliTime()-stt;
			
			blkP.copyFrom(denGP); // repackage to block matrix

			stt = Timer.milliTime();
			P.local().mult(E, UP.mult(U, P.local())).scale(1-alpha).cellAdd(blkP);
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
		
		return P.local();
	}

	public def printInfo() {
		//
		val nzc =  G.getTotalNonZeroCount();
		val nzd =  nzc / (G.M * G.N);
		Console.OUT.printf("Input G:(%dx%d), partition:(%dx%d) blocks, ",
				G.M, G.N, G.getGrid().numRowBlocks, G.getGrid().numColBlocks);
		Console.OUT.printf("distribution:(%dx%d), nonzero density:%f count:%f\n", 
				Place.numPlaces(), 1,  nzd, nzc);

		Console.OUT.printf("Input P:(%dx%d), partition:(%dx%d) blocks, duplicated in all places\n",
				P.M, P.N, P.getGrid().numRowBlocks, P.getGrid().numColBlocks);

		Console.OUT.printf("Input E:(%dx%d), partition:(%dx%d) blocks, local block matrix\n",
				E.M, E.N, E.grid.numRowBlocks, E.grid.numColBlocks);

		Console.OUT.printf("Input U:(%dx%d), partition:(%dx%d) blocks, local block matrix\n",
				U.M, U.N, U.grid.numRowBlocks, U.grid.numColBlocks);

		Console.OUT.flush();
	}

}
