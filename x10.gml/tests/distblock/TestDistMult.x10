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

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;
import x10.matrix.distblock.DistDistMult;

/**
   <p>

   <p>
 */
public class TestDistMult {
	
    public static def main(args:Array[String](1)) {
		val testcase = new RunDistBlockMatrix(args);
		testcase.run();
	}
}

class RunDistBlockMatrix {
	public val M:Int;
	public val K:Int;
	public val N:Int;
	public val bM:Int;
	public val bK:Int;
	public val bN:Int;
	public val nzd:Double;
	
	//-------------
	//Matrix block partitioning
	val gA:Grid, gTransA:Grid;
	val gB:Grid, gTransB:Grid;
	val gC:Grid;
	//Matrix blocks distribution
	val dA:DistMap, dTransA:DistMap;
	val dB:DistMap, dTransB:DistMap;
	
    public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):10;
		K = args.size > 1 ?Int.parse(args(1)):M+1;
		N = args.size > 2 ?Int.parse(args(2)):M+2;
		bM = args.size > 3 ?Int.parse(args(3)):4;
		bK = args.size > 4 ?Int.parse(args(4)):5;
		bN = args.size > 5 ?Int.parse(args(5)):5;
		nzd =  args.size > 6 ?Double.parse(args(6)):0.99;
		
		gA = new Grid(M, K, bM, bK);
		gB = new Grid(K, N, bK, bN);
		gC = new Grid(M, N, bM, bN);
		gTransA = new Grid(K, M, bK, bM);
		gTransB = new Grid(N, K, bN, bK);
		
		dA = (new DistGrid(gA, 1, Place.MAX_PLACES)).dmap;
		dB = (new DistGrid(gB, Place.MAX_PLACES, 1)).dmap;
		
		dTransA = (new DistGrid(gTransA, Place.MAX_PLACES, 1)).dmap;
		dTransB = (new DistGrid(gTransB, 1, Place.MAX_PLACES)).dmap;
	}

    public def run (): void {
		Console.OUT.println("Starting Dist-Dist block matrix multiply tests");
		Console.OUT.printf("Matrix (%d,%d) mult (%d,%d) ", M, K, K, N);
		Console.OUT.printf(" partitioned in (%dx%d) and (%dx%d) blocks, nzd:%f\n", 
						    bM, bK, bK, bN, nzd);

		var ret:Boolean = true;
 		// Set the matrix function
 		ret &= (testMult());
 		ret &= (ret && testTransMult());
 		ret &= (ret && testMultTrans());

		if (ret)
			Console.OUT.println("Dist block matrix multiply test passed!");
		else
			Console.OUT.println("----------------Dist block matrix multiply test failed!----------------");
	}

	public def testMult():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Starting Dist block matrix multiply test");
		val A = DistBlockMatrix.makeDense(gA, dA) as DistBlockMatrix(M,K);
		//val B = DistBlockMatrix.makeSparse(gB, dB, nzd) as DistBlockMatrix(K,N);
		val B = DistBlockMatrix.makeDense(gB, dB) as DistBlockMatrix(K,N);
		val C = DupBlockMatrix.makeDense(gC) as DupBlockMatrix(M,N);
		
		A.init((r:Int,c:Int)=>1.0*(r+c));
		B.init((r:Int,c:Int)=>1.0*(r+c));
		
		DistDistMult.mult(A, B, C, false);
		//A.printMatrix();
		//B.printMatrix();
		//C.printMatrix();
		
		val dA = A.toDense() as DenseMatrix(M,K);
		val dB = B.toDense() as DenseMatrix(K,N);
		val dC = DenseMatrix.make(M,N);
		dC.mult(dA, dB, false);
		//dC.printMatrix();
		
		ret &= dC.equals(C as Matrix(dC.M,dC.N));

		if (ret)
			Console.OUT.println("Dist Block matrix multiply test passed!");
		else
			Console.OUT.println("--------Dist Block matrix multiply test failed!--------");
		return ret;
	}
	

	public def testTransMult():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Starting block matrix trans-multiply test (transpose 1st operand)");
		val A = DistBlockMatrix.makeDense(gTransA, dTransA) as DistBlockMatrix(K,M);
		val B = DistBlockMatrix.makeDense(gB, dB) as DistBlockMatrix(K,N);
		val C = DupBlockMatrix.makeDense(gC) as DupBlockMatrix(M,N);
		
		A.init((r:Int,c:Int)=>1.0*(r+c));
		B.init((r:Int,c:Int)=>1.0*(r+c));
		DistDistMult.transMult(A, B, C, false);
		
		//A.printMatrix();
		//B.printMatrix();
		//C.printMatrix();
		
		val dA = A.toDense() as DenseMatrix(K,M);
		val dB = B.toDense() as DenseMatrix(K,N);
		val dC = DenseMatrix.make(M,N);
		dC.transMult(dA, dB, false);
		//dC.printMatrix();
		
		ret &= dC.equals(C as Matrix(dC.M,dC.N));

		if (ret)
			Console.OUT.println("Dist Block matrix trans-multiply test passed!");
		else
			Console.OUT.println("--------Dist Block matrix trans-multiply test failed!--------");
		return ret;
	}
	
	public def testMultTrans():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Starting Dist block matrix multiply-transpose test (transpose on 2nd operand)");
		val A = DistBlockMatrix.makeDense(gA, dA) as DistBlockMatrix(M,K);
		val B = DistBlockMatrix.makeDense(gTransB, dTransB) as DistBlockMatrix(N,K);
		val C = DupBlockMatrix.makeDense(gC) as DupBlockMatrix(M,N);
		
		A.init((r:Int,c:Int)=>1.0*((r+c)));
		B.init((r:Int,c:Int)=>1.0*((r+c)));
		
		DistDistMult.multTrans(A, B, C, false);
		//A.printMatrix();
		//B.printMatrix();
		//C.printMatrix();
		
		val dA = A.toDense() as DenseMatrix(M,K);
		val dB = B.toDense() as DenseMatrix(N,K);
		val dC = DenseMatrix.make(M,N);
		dC.multTrans(dA, dB, false);
		//dC.printMatrix();
		
		ret &= dC.equals(C as Matrix(dC.M,dC.N));

		if (ret)
			Console.OUT.println("Dist block matrix multiply-transpose test passed!");
		else
			Console.OUT.println("--------Dist block matrix multiply-transpose test failed!--------");
		return ret;
	}

} 
