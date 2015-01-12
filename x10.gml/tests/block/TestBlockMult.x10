/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.BlockBlockMult;

public class TestBlockMult extends x10Test {
	public val M:Long;
	public val K:Long;
	public val N:Long;
	public val bM:Long;
	public val bK:Long;
	public val bN:Long;
	public val nzd:Double;

	val gA:Grid, gB:Grid, gC:Grid, gTransA:Grid, gTransB:Grid;
	
    public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):10;
		K = args.size > 1 ? Long.parse(args(1)):(M as Int)+2;
		N = args.size > 2 ? Long.parse(args(2)):(M as Int)+4;
		bM = args.size > 3 ? Long.parse(args(3)):2;
		bK = args.size > 4 ? Long.parse(args(4)):3;
		bN = args.size > 5 ? Long.parse(args(5)):4;
		nzd =  args.size > 6 ? Double.parse(args(6)):0.99;
		
		gA = new Grid(M, K, bM, bK);
		gB = new Grid(K, N, bK, bN);
		gC = new Grid(M, N, bM, bN);
		gTransA = new Grid(K, M, bK, bM);
		gTransB = new Grid(N, K, bN, bK);
	}

    public def run():Boolean {
		Console.OUT.println("Block-block matrix multiply tests");
		Console.OUT.printf("Matrix (%d,%d) mult (%d,%d) ", M, K, K, N);
		Console.OUT.printf(" partitioned in (%dx%d) and (%dx%d) blocks, nzd:%f\n", 
						    bM, bK, bK, bN, nzd);

		var ret:Boolean = true;
 		ret &= (testMult());
 		ret &= (ret && testTransMult());
 		ret &= (ret && testMultTrans());

		return ret;
	}

	public def testMult():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Block matrix multiply test");
		val A = BlockMatrix.makeDense(gA) as BlockMatrix(M,K);
		//val B = BlockMatrix.makeSparse(gB, nzd) as BlockMatrix(K,N);
		val B = BlockMatrix.makeDense(gB) as BlockMatrix(K,N);
		val C = BlockMatrix.makeDense(gC) as BlockMatrix(M,N);
		
		A.init((r:Long,c:Long)=>1.0*((r+c)));
		B.init((r:Long,c:Long)=>1.0*((r+c)));
		
		BlockBlockMult.mult(A, B, C, false);
		
		val dA = A.toDense() as DenseMatrix(M,K);
		val dB = B.toDense() as DenseMatrix(K,N);
		val dC = DenseMatrix.make(M,N);
		dC.mult(dA, dB, false);
		
		ret &= dC.equals(C as Matrix(dC.M,dC.N));

		if (!ret)
			Console.OUT.println("--------Block matrix multiply test failed!--------");
		return ret;
	}
	

	public def testTransMult():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Block matrix trans-multiply test (transpose 1st operand)");
		val A = BlockMatrix.makeDense(gTransA) as BlockMatrix(K,M);
		val B = BlockMatrix.makeDense(gB) as BlockMatrix(K,N);
		val C = BlockMatrix.makeDense(gC) as BlockMatrix(M,N);
		
		A.init((r:Long,c:Long)=>1.0*((r+c)));
		B.init((r:Long,c:Long)=>1.0*((r+c)));
		BlockBlockMult.transMult(A, B, C, false);
		
		val dA = A.toDense() as DenseMatrix(K,M);
		val dB = B.toDense() as DenseMatrix(K,N);
		val dC = DenseMatrix.make(M,N);
		dC.transMult(dA, dB, false);
		
		ret &= dC.equals(C as Matrix(dC.M,dC.N));

		if (!ret)
			Console.OUT.println("--------Block matrix trans-multiply test failed!--------");
		return ret;
	}
	
	public def testMultTrans():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Block matrix multiply-transpose test (transpose on 2nd operand)");
		val A = BlockMatrix.makeDense(gA) as BlockMatrix(M,K);
		val B = BlockMatrix.makeDense(gTransB) as BlockMatrix(N,K);
		val C = BlockMatrix.makeDense(gC) as BlockMatrix(M,N);
		
		A.init((r:Long,c:Long)=>1.0*((r+c)));
		B.init((r:Long,c:Long)=>1.0*((r+c)));
		
		BlockBlockMult.multTrans(A, B, C, false);
		
		val dA = A.toDense() as DenseMatrix(M,K);
		val dB = B.toDense() as DenseMatrix(N,K);
		val dC = DenseMatrix.make(M,N);
		dC.multTrans(dA, dB, false);
		
		ret &= dC.equals(C as Matrix(dC.M,dC.N));

		if (!ret)
			Console.OUT.println("--------Block matrix multiply-transpose test failed!--------");
		return ret;
	}

    public static def main(args:Rail[String]) {
		new TestBlockMult(args).execute();
	}
} 
