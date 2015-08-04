/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */

import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;
import x10.matrix.dist.DupDenseMatrix;

import x10.matrix.dist.DistMultDupToDist;

/**
 * This class tests matrix-vector multiplication:
 * for (1..iteration) {
 *    A * V = P;
 *    V = P;
 * }
 * where A is squared sparse matrix, V and P are vectors (1-column dense matrix).
   <p>
 * The matrix A is partitioned in row-wise fashion, which is to say that the matrix is partitioned into (num_place, 1)
 * block matrix, where each place is assigned a block.
   <p>
 * Vector V is duplicated in all places, and P vector is distributed in the same way
 * as matrix A.
 * <p>
 * Before the end of each iteration, P is gathered at the root place and copied
 * to vector V, and then all copies of V are synchronized in all places.
 */

public class MatVecMult{
    public static def main(args:Rail[String]) {
    	val M   = args.size > 0 ? Long.parse(args(0)):100;
    	val nnz = args.size > 1 ? Float.parse(args(1)):0.5f;
    	val it  = args.size > 2 ? Long.parse(args(2)):3;
    	val vrf = args.size > 3 ? Long.parse(args(3)):0;
   	
		val testcase = new DVMultRowwise(M, nnz, it, vrf);
		testcase.run();
	}
}

class DVMultRowwise {
	val it:Long;
	val vrf:Long;

	val M:Long;
	
	val dstA:DistSparseMatrix(M,M);
	val dupV:DupDenseMatrix(M,1);
	val V:DenseMatrix(M,1);
	val dstP:DistDenseMatrix(M,1);
	val P:DenseMatrix(M,1);

    public def this(m:Long, nnz:Float, i:Long, v:Long) {
    	M=m;
    	it = i; vrf=v;
    	
    	val numP = Place.numPlaces();//Place.numPlaces();
    	Console.OUT.printf("\nTest Dist sparse mult Dup dense in %d places\n", numP);
 
    	val partA = new Grid(M, M, numP, 1);
    	dstA  = DistSparseMatrix.make(partA, nnz) as DistSparseMatrix(M,M);
    	//dstA.initRandom(nnz);
    	//dstA.printRandomInfo();
    	Console.OUT.flush();
    	
    	V    = DenseMatrix.make(M,1);
    	dupV = DupDenseMatrix.makeRand(M, 1);
    	dupV.initRandom();

    	val partP = new Grid(M, 1, numP, 1);
    	dstP  = DistDenseMatrix.make(partP) as DistDenseMatrix(M,1);
    	P	  = DenseMatrix.make(dstP.M, dstP.N);
	}
	
	public def run(): void {
		if (vrf > 0)
			dupV.local().copyTo(V);
		
 		// Set the matrix function
		runMultParallel();
		if (vrf > 0) {
			runVerify();
		}
	}


	public def runMultParallel():void {
		var ct:Long=0;
        var cmpt:Double = 0.0;
        var comt:Double = 0.0;
		val st = Timer.milliTime();		
		for (1..it) {
			/* Timer */ ct = Timer.milliTime();
			DistMultDupToDist.comp(dstA, dupV, dstP, false);
			/* Timer */ cmpt += Timer.milliTime()-ct;
			
			/* Timer */ ct = Timer.milliTime();
			dstP.copyTo(dupV);
			/* Timer */ comt += Timer.milliTime() -ct;
		}
		val ed = Timer.milliTime();
		Console.OUT.printf("\nDone Dist*Dup->Dist MatVecMult for %d iteration\n", it);

		val tt = (ed-st) as Float / it;
		comt = comt/it;
		cmpt = comt/it;
		Console.OUT.printf("MatVecMult Time:%9.3f ms, communication:%8.3f computation:%8.3f\n", 
				tt, comt, cmpt);		
	}

	public def runVerify():Boolean {
		Console.OUT.printf("Starting converting sparse matrix to dense\n");
		val ma = dstA.toDense() as DenseMatrix(M,M);
		val mb = V;//dupV.getMatrix() as DenseMatrix(M,1);
		val mc = DenseMatrix.make(ma.M, mb.N) as DenseMatrix(M,1);
		Console.OUT.printf("Starting verification on dense matrix\n");
		
		for (1..it) {
			mc.mult(ma, mb);
			mc.copyTo(mb);
		}
		
		val ret = mc.equals(dupV.local() as Matrix(mc.M, mc.N));
		if (!ret)
			Console.OUT.println("-----Dist*Dup->Dist MatVecMult test failed!-----");
		return ret;
	}
}
