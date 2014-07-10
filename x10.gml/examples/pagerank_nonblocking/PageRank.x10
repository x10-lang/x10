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

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlockMatrix;
import x10.matrix.dist.DupDenseMatrix;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

/**
 * Parallel Page Rank algorithm based on GML distributed
 * dense/sparse matrix
 */
public class PageRank {
	val rowG:Long;
	val colP:Long;

	public val iterations:Long;
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
	val UP:DenseMatrix(colP, colP);

	public def this(mg:Long, np:Long, nzd:Double, it:Long) {
		rowG = mg;
		colP = np;

		iterations = it;
		nzDensity=nzd;

		gridG = new Grid(rowG, rowG, Place.numPlaces(), 1);
		gridGP= new Grid(rowG, colP, Place.numPlaces(), 1);

		G = DistSparseMatrix.make(gridG, nzDensity) as DistSparseMatrix(rowG, rowG);
		P = DupDenseMatrix.make(rowG, colP);
		E = DenseMatrix.make(rowG, colP);
		U = DenseMatrix.make(colP, rowG);

		distGP = DistDenseMatrix.make(gridGP) as DistDenseMatrix(rowG, colP);
		blckGP = DenseBlockMatrix.make(gridGP) as DenseBlockMatrix(rowG, colP);
		GP     = DenseMatrix.make(rowG, colP);
		UP     = DenseMatrix.make(colP, colP);
	}
	
	public def init():void {
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
		var parTime:Long = 0;
		var seqTime:Long = 0;
        var bcastTime:Long = 0;
        var gatherTime:Long = 0;
        var totalTime:Long = 0;

		totalTime -= Timer.milliTime();	
		for (1..iterations) {
			parTime -= Timer.milliTime();
			distGP.mult(G, P).scale(alpha);
			parTime += Timer.milliTime();

			gatherTime -= Timer.milliTime();
            distGP.copyTo(blckGP, GP);// dist -> dup
			gatherTime += Timer.milliTime();

			seqTime -= Timer.milliTime();
			P.local()
				.mult(E, UP.mult(U, P.local()))
				.scale(1-alpha)
				.cellAdd(GP);
			seqTime += Timer.milliTime();

			bcastTime -= Timer.milliTime();
			P.sync(); // broadcast
			bcastTime += Timer.milliTime();
		}
		totalTime += Timer.milliTime();
		val mulTime = distGP.getCalcTime();
		val commTime = bcastTime + gatherTime;
		Console.OUT.printf("Gather: %d ms Bcast: %d ms Mul: %d ms\n", gatherTime, bcastTime, mulTime);
		Console.OUT.printf("G:%d PageRank total runtime for %d iter: %d ms, ", G.M, iterations, totalTime);
		Console.OUT.printf("comm: %d ms, par calc: %d ms, seq calc: %d ms\n", commTime, parTime, seqTime);
		Console.OUT.flush();
		
		return P.local();
	}

	public def printInfo() {
		Console.OUT.printf("Place: %d, G:(%dx%d) P:(%dx%d))", 
						   Place.numPlaces(), G.M, G.N, P.M, P.N);
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
