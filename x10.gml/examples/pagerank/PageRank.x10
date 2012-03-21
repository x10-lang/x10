/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */
package pagerank;

import x10.io.Console;
import x10.util.Timer;
//
import x10.matrix.Debug;
//
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.blas.DenseMatrixBLAS;
//
import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DupBlockMatrix;
import x10.matrix.distblock.DistBlockMatrix;

import x10.matrix.distblock.DistDupMult;
//

/**
 * Parallel Page Rank algorithm based on GML distributed block matrix.
 * 
 * Input parameters:
 * <p> 1) Rows of G (also columns of G)
 * <p> 2) Columns of P
 * <p> 3) Nozero density of G
 * <p> 4) Iteration
 * <p> 5) Number of row blocks partitioning in G
 * <p> 6) Number of column blocks partitioning in G
 * <p>
 * <p> Input matrix G is partitioned into (numRowBsG &#42 numColBsG) blocks. All blocks
 * are distributed to (Place.MAX_PLACES, 1) places, or vertical distribution.
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
public class PageRank {

	//-------Matrix dimension-----------
	val rowG:Int;
	val colP:Int;

	public val iteration:Int;
	public val nzDensity:Double;
	public val alpha:Double= 0.85;
	//-----------------------------------
	//-------- Partition and distribution parameters ------
	public val numRowBsG:Int; //row-wise partitioning blocks
	public val numColBsG:Int; //column-wise partitioning blocks
	
	//-----------------------------------
	
	//----------- Input matrix ----------
	public val G:DistBlockMatrix(rowG, rowG);
	public val P:DupBlockMatrix(rowG, colP);
	public val E:BlockMatrix(rowG, colP);
	public val U:BlockMatrix(colP, rowG);

	val GP:DistBlockMatrix(rowG, colP); // Distributed version of G*P
	val blckGP:BlockMatrix(rowG, colP);     // Used to collect dist block of P
	//val GP:DenseMatrix(rowG, colP);
	//val dupGP:DupDenseMatrix(rowG, colP); // Duplicated version of G*P
	val UP:BlockMatrix(colP, colP);

	val ts:Long = Timer.milliTime();
	var tt:Long = 0;

	public def this(mg:Int, np:Int, nzd:Double, it:Int, nrG:Int, ncG:Int) {

		rowG = mg;
		colP = np;
		iteration = it;
		nzDensity=nzd;
		
		numRowBsG = nrG; numColBsG=ncG; 
		
		val gridG = new Grid(rowG, rowG, numRowBsG, numColBsG);
		val gridP = new Grid(rowG, colP, numColBsG, 1);
		val gridU = new Grid(colP, rowG, 1, numColBsG);
		val gridGP= new Grid(rowG, colP, numRowBsG, 1);
		val gridUP= new Grid(colP, colP, 1, 1);
		
		val distG = new DistGrid(gridG, Place.MAX_PLACES, 1);  //Vertical distribution
		val distGP= new DistGrid(gridGP, Place.MAX_PLACES, 1); //Vertical distribution
		
		G = DistBlockMatrix.makeSparse(gridG, distG.dmap, nzDensity) as DistBlockMatrix(rowG, rowG);
		P = DupBlockMatrix.makeDense(gridP) as DupBlockMatrix(rowG, colP);
		E = BlockMatrix.makeDense(gridP) as BlockMatrix(rowG, colP);
		U = BlockMatrix.makeDense(gridU) as BlockMatrix(colP, rowG);
		
		GP     = DistBlockMatrix.makeDense(gridGP, distGP.dmap) as DistBlockMatrix(rowG, colP);
		blckGP = BlockMatrix.makeDense(gridGP) as BlockMatrix(rowG, colP);
		UP     = BlockMatrix.makeDense(gridUP) as BlockMatrix(colP, colP);
	}
	
	public def init():void {
		//-----------------------------------------------
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
		var startt:Long =0;
		var pcompt:Long =0;
		var scompt:Long =0;
		Debug.flushln("Start parallel PageRank");	
		val st = Timer.milliTime();
				
		for (var i:Int=0; i<iteration; i++) {
			startt = Timer.milliTime();
			GP.mult(G, P, false)
				.scale(alpha)
				.copyTo(blckGP);// dist -> dup
			//blckGP.copyTo(GP);
			pcompt += Timer.milliTime() - startt;
			startt = Timer.milliTime();

			P.local()
				.mult(E, UP.mult(U, P.local(), false), false)
				.scale(1-alpha)
				.cellAdd(GP);
			scompt += Timer.milliTime() - startt;
			P.sync(); // broadcast
		}
		tt = Timer.milliTime() - st;
		val pcomtime = GP.getCalcTime();
		val commtime = GP.getCommTime() + P.getCommTime();

		Console.OUT.printf("G:%d PageRank total runtime for %d iter: %d ms, ", rowG, iteration, tt );
		Console.OUT.printf("comm: %d ms, seq calc: %d ms\n", commtime, scompt);
		Console.OUT.flush();
		
		return P.local();
	}

	public def printInfo() {
		//
		val nzc:Float =  G.getTotalNonZeroCount() as Float;
		val nzd:Float =  nzc / (G.M * G.N as Float);
		Console.OUT.printf("Input G:(%dx%d), partition:(%dx%d) blocks, ",
				G.M, G.N, G.getGrid().numRowBlocks, G.getGrid().numColBlocks);
		Console.OUT.printf("distribution:(%dx%d), nonzero density:%f count:%f\n", 
				Place.MAX_PLACES, 1,  nzDensity, nzc);

		Console.OUT.printf("Input P:(%dx%d), partition:(%dx%d) blocks, duplicated in all places\n",
				P.M, P.N, P.getGrid().numRowBlocks, P.getGrid().numColBlocks);

		Console.OUT.printf("Input E:(%dx%d), partition:(%dx%d) blocks, local block matrix\n",
				E.M, E.N, E.grid.numRowBlocks, E.grid.numColBlocks);

		Console.OUT.printf("Input U:(%dx%d), partition:(%dx%d) blocks, local block matrix\n",
				U.M, U.N, U.grid.numRowBlocks, U.grid.numColBlocks);

		Console.OUT.flush();
	}

}
