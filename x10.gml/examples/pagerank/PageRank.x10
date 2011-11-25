/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package pagerank;

import x10.io.Console;
import x10.util.Timer;
//
import x10.matrix.Debug;
//
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.blas.DenseMultBLAS;
//
import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlockMatrix;
import x10.matrix.dist.DupDenseMatrix;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

import x10.matrix.dist.DistMultDupToDist;
//

/**
 * Parallel Page Rank algorithm based on GML distributed
 * dense/sparse matrix
 */
public class PageRank {

	val rowG:Int;
	val colP:Int;

	public val iteration:Int;
	public val nzDensity:Double;
	public val alpha:Double= 0.85;

	public val G:DistSparseMatrix(rowG, rowG);
	public val P:DupDenseMatrix(rowG, colP);
	public val E:DenseMatrix(rowG, colP);
	public val U:DenseMatrix(colP, rowG);

	val gridG:Grid;
	val gridGP:Grid;

	val distGP:DistDenseMatrix(rowG, colP); // Distributed version of G*P
	val blckGP:DenseBlockMatrix(rowG, colP);// Used to collecte dist block of P
	val GP:DenseMatrix(rowG, colP);
	//val dupGP:DupDenseMatrix(rowG, colP);   // Duplicated version of G*P
	val UP:DenseMatrix(colP, colP);

	val ts:Long = Timer.milliTime();
	var tt:Long = 0;

	public def this(mg:Int, np:Int, nzd:Double, it:Int) {

		rowG = mg;
		colP = np;

		iteration = it;
		nzDensity=nzd;

		gridG = new Grid(rowG, rowG, Place.MAX_PLACES, 1);
		gridGP= new Grid(rowG, colP, Place.MAX_PLACES, 1);

		G = DistSparseMatrix.make(gridG, nzDensity) as DistSparseMatrix(rowG, rowG);
		P = DupDenseMatrix.make(rowG, colP);
		E = DenseMatrix.make(rowG, colP);
		U = DenseMatrix.make(colP, rowG);

		distGP = DistDenseMatrix.make(gridGP) as DistDenseMatrix(rowG, colP);
		blckGP = DenseBlockMatrix.make(gridGP) as DenseBlockMatrix(rowG, colP);
		GP     = DenseMatrix.make(rowG, colP);
		//dupGP  = DupDenseMatrix.make(rowG, colP); //Need to perform MPI_getherv
		UP     = DenseMatrix.make(colP, colP);
	}
	
	public def init():void {
		//-----------------------------------------------
		Debug.flushln("Start initialize input matrices");		
		G.initRandom(nzDensity);
		Debug.flushln("Dist sparse matrix initialization completes");		
		P.initRandom();
		Debug.flushln("Initialize duplicated dense matrix P completes");		
		E.initRandom();
		Debug.flushln("Initialize dense matrix E completes");		
		U.initRandom();
		Debug.flushln("Initialize dense matrix U completes");		
	}

	public def run():DenseMatrix {
		var startt:Long =0;
		var pcompt:Long =0;
		var scompt:Long =0;
		Debug.flushln("Start parallel PageRank");	
		val st = Timer.milliTime();
				
		for (var i:Int=0; i<iteration; i++) {
			startt = Timer.milliTime();
			distGP.mult(G, P)
				.scale(alpha)
				.copyTo(blckGP, GP);// dist -> dup
			//blckGP.copyTo(GP);
			pcompt += Timer.milliTime() - startt;
			startt = Timer.milliTime();

			P.local()
				.mult(E, UP.mult(U, P.local()))
				.scale(1-alpha)
				.cellAdd(GP);
			scompt += Timer.milliTime() - startt;
			P.sync(); // broadcast
		}
		tt = Timer.milliTime() - st;
		val pcomtime = distGP.getCalcTime();
		val commtime = blckGP.getCommTime() + P.getCommTime();

		Console.OUT.printf("G:%d PageRank total runtime for %d iter: %d ms, ", rowG, iteration, tt );
		Console.OUT.printf("comm: %d ms, seq calc: %d ms\n", commtime, scompt);
		Console.OUT.flush();
		
		return P.local();
	}

	public def printInfo() {
		//
		Console.OUT.printf("Place: %d, G:(%dx%d) P:(%dx%d))", 
						   Place.MAX_PLACES, G.M, G.N, P.M, P.N);
		Console.OUT.printf("distG(%dx%d)  nzDensity:%.3f\n",
						   gridG.numRowBlocks, gridG.numColBlocks, nzDensity);
		Console.OUT.flush();
		
		val nzc:Float =  G.getTotalNonZeroCount() as Float;
		val nzd:Float =  nzc / (G.M * G.N as Float);
		Console.OUT.printf("G nonzero %f, %dx%d, density is %f\n", 
						   nzc, G.M, G.N, nzd);
		Console.OUT.printf("Average column nonzero count:%.3f, std:%.3f\n",
						   G.getAvgColumnSize(), G.getColumnSizeStdDvn());
		Console.OUT.printf("Average nonzero index distance:%.3f, std:%.3f\n",
						   G.compAvgIndexDst(), G.compIndexDstStdDvn());
		//V.printBlockColumnSizeAvgStd();
		Console.OUT.flush();
	}

}
