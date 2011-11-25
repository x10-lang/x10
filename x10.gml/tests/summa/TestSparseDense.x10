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

import x10.io.Console;
import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.blas.DenseMultBLAS;
import x10.matrix.block.Grid;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

import x10.matrix.dist.summa.SummaSparse;
import x10.matrix.dist.summa.SummaSparseMultDense;

/**
   This class contains test cases for dense matrix multiplication.
   <p>

   <p>
 */

public class TestSparseDense{
    public static def main(args:Array[String](1)) {
		val testcase = new SummaSparseMultDenseTest(args);
		testcase.run();
	}
}

class SummaSparseMultDenseTest {

	public val M:Int;
	public val N:Int;
	public val K:Int;
	public val nzd:Double;

	public val pA:Grid;
	public val pB:Grid;
	public val pC:Grid;
	
    public def this(args:Array[String](1)) {
		M   = args.size > 0 ?Int.parse(args(0)):25;
		N   = args.size > 1 ?Int.parse(args(1)):26;
		K   = args.size > 2 ?Int.parse(args(2)):27;	
		nzd = args.size > 3 ?Double.parse(args(3)):0.5; 
		
		val numP = Place.numPlaces();//Place.MAX_PLACES;
		Console.OUT.printf("\nTest SUMMA dist sparse*dense matrix over %d places and sparsity: %f\n", 
							numP, nzd);
		pA = Grid.make(M, K);
		pB = Grid.make(K, N);
		pC = Grid.make(M, N);
	}
	
	public def run(): void {
		var ret:Boolean = true;
 		// Set the matrix function

		ret &= (testSparseMultDense());
		ret &= (testSparseMultDenseTrans());
		
		if (ret)
			Console.OUT.println("SUMMA x10 distributed sparse*dense matrix test passed!");
		else
			Console.OUT.println("--------SUMMA x10 distributed sparse*dense matrix test failed!--------");
	}
	//------------------------------------------------

	//-----------------------------------------------------------------
	
	public def testSparseMultDense():Boolean {
		val numP = Place.numPlaces();//Place.MAX_PLACES;
		Console.OUT.printf("\nTest SUMMA dist sparse*dense matrix over %d places and sparsity %f\n", 
				numP, nzd);
		Debug.flushln("Start allocating memory space for dist sparse matrix A");
		val da = DistSparseMatrix.make(pA, nzd);
		Debug.flushln("Start initializing sparse matrix A");
		da.initRandom();
		
		Debug.flushln("Start allocating memory space for dist dense matrix B");
		val db = DistDenseMatrix.make(pB);
		Debug.flushln("Start initializing sparse matrix B");
		db.initRandom();

		val dc = DistDenseMatrix.make(pC);

		Debug.flushln("Start calling SUMMA sparse mult dense to dense X10 routine");
		SummaSparseMultDense.mult(1, 0.0, da, db, dc);
		Debug.flushln("SUMMA done");
		
		val ma = da.toDense();
		val mb = db.toDense();
		val mc = DenseMatrix.make(ma.M, mb.N);
		
		Debug.flushln("Start sequential dense matrix multiply");
		DenseMultBLAS.comp(ma, mb, mc, false);
		Debug.flushln("Done sequential dense matrix multiply");

		val ret = dc.equals(mc as Matrix(dc.M, dc.N));
		if (ret)
			Console.OUT.println("SUMMA x10 distributed sparse*dense matrix test passed!");
		else
			Console.OUT.println("-----SUMMA x10 distributed sparse*dense matrix test failed!-----");
		return ret;
	}
	
	public def testSparseMultDenseTrans():Boolean {
		val numP = Place.numPlaces();//Place.MAX_PLACES;
		Console.OUT.printf("\nTest SUMMA x10 dist sparse*dense^T over %d places\n", numP);

		Debug.flushln("Start allocating memory space for sparse matrix A");
		val da = DistSparseMatrix.make(M, K, nzd); 
		Debug.flushln("Start initializing matrix A "+
				da.grid.numRowBlocks+" "+da.grid.numColBlocks);
		da.initRandom();
		
		Debug.flushln("Start allocating memory space for dist dense matrix B");
		val db = DistDenseMatrix.make(N, K);
		db.initRandom();
		Debug.flushln("Start initializing matrix B "+
				db.grid.numRowBlocks+" "+db.grid.numColBlocks );

		val dc = DistDenseMatrix.make(M, N);

		Debug.flushln("Start calling SUMMA sparse*dense^T X10 routine");
		SummaSparseMultDense.multTrans(1, 0.0, da, db, dc);
		Debug.flushln("SUMMA done");
		
		val ma = da.toDense();
		val mb = db.toDense();
		val mc = DenseMatrix.make(ma.M, mb.M);
		
		Debug.flushln("Start sequential dense matrix multTrans");
		DenseMultBLAS.compMultTrans(ma, mb, mc, false);
		Debug.flushln("Done sequential dense matrix multTrans");

		val ret = dc.equals(mc as Matrix(dc.M, dc.N));
		if (ret)
			Console.OUT.println("SUMMA x10 distributed sparse*dense^T test passed!");
		else
			Console.OUT.println("-----SUMMA x10 distributed sparse*dense^T test failed!-----");
		return ret;
	}
}
