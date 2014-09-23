/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.block.Grid;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;
import x10.matrix.distblock.DistDupMult;

public class TestDistDupMult extends x10Test {
	public val M:Long;
	public val K:Long;
	public val N:Long;
	public val bM:Long;
	public val bK:Long;
	public val bN:Long;
	public val nzd:Double;

	val gA:Grid, gTransA:Grid;
	val gB:Grid, gTransB:Grid;
	val gC:Grid;
	val dA:DistMap, dTransA:DistMap;
	val dC:DistMap, dTransC:DistMap;
	
    public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):20;
		K = args.size > 1 ? Long.parse(args(1)):(M as Int)+1;
		N = args.size > 2 ? Long.parse(args(2)):(M as Int)+2;
		bM = args.size > 3 ? Long.parse(args(3)):8;
		bK = args.size > 4 ? Long.parse(args(4)):9;
		bN = args.size > 5 ? Long.parse(args(5)):10;
		nzd =  args.size > 6 ?Double.parse(args(6)):0.99;
		
		gA = new Grid(M, K, bM, bK);
		dA = (new DistGrid(gA, Place.numPlaces(), 1)).dmap;		

		gTransA = new Grid(K, M, bK, bM);
		dTransA = (new DistGrid(gTransA, 1, Place.numPlaces())).dmap;
		
		gB = new Grid(K, N, bK, bN);
		gTransB = new Grid(N, K, bN, bK);
		
		gC = new Grid(M, N, bM, bN);
		dC = (new DistGrid(gC, Place.numPlaces(), 1)).dmap;
		dTransC = DistGrid.makeHorizontal(gC).dmap;
	}

    public def run():Boolean {
		Console.OUT.println("Starting Dist-Dup block matrix multiply tests");
		Console.OUT.printf("Matrix (%d,%d) mult (%d,%d) ", M, K, K, N);
		Console.OUT.printf(" partitioned in (%dx%d) and (%dx%d) blocks, nzd:%f\n", 
						    bM, bK, bK, bN, nzd);

		var ret:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!
		ret &= (testMult());
 		ret &= (ret && testTransMult());
 		ret &= (ret && testMultTrans());
    }
		return ret;
	}

	public def testMult():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Starting Dist-Dup block matrix multiply test");
		//This is not best way to create dist block matrix. 
		//Partition gA and distribution dA are remotely captured to all places
		val A = DistBlockMatrix.makeDense(gA, dA) as DistBlockMatrix(M,K);
		//val A = DistBlockMatrix.make(M, K, bM, bK, 1, Place.numPlaces());

		//val B = DistBlockMatrix.makeSparse(gB, dB, nzd) as DistBlockMatrix(K,N);
		val B = DupBlockMatrix.makeDense(gB) as DupBlockMatrix(K,N);
		val C = DistBlockMatrix.makeDense(gC, dC) as DistBlockMatrix(M,N);
		
		A.init((r:Long,c:Long)=>1.0*(r+c));
		B.init((r:Long,c:Long)=>1.0*(r+c));
		
		DistDupMult.comp(A, B, C, false);
		
		val dA = A.toDense() as DenseMatrix(M,K);
		val dB = B.toDense() as DenseMatrix(K,N);
		val dC = DenseMatrix.make(M,N);
		dC.mult(dA, dB, false);
		
		ret &= dC.equals(C as Matrix(dC.M,dC.N));

		if (!ret)
			Console.OUT.println("--------Dist-Dup Block matrix multiply test failed!--------");
		return ret;
	}

	public def testTransMult():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Starting Dist-Dup block matrix trans-multiply test (transpose 1st operand)");
		
		val A = DistBlockMatrix.makeDense(K, M, bK, bM, 1, Place.numPlaces());
		val B = DupBlockMatrix.makeDense(K, N, bK, bN);
		val C = DistBlockMatrix.makeDense(M, N, bM, bN, Place.numPlaces(), 1) as DistBlockMatrix(M,N);
		
		A.init((r:Long,c:Long)=>1.0*(r+c));
		B.init((r:Long,c:Long)=>1.0*(r+c));
		DistDupMult.compTransMult(A, B, C, false);
		
		val dA = A.toDense() as DenseMatrix(K,M);
		val dB = B.toDense() as DenseMatrix(K,N);
		val dC = DenseMatrix.make(M,N);
		dC.transMult(dA, dB, false);
		
		ret &= dC.equals(C as Matrix(dC.M,dC.N));

		if (!ret)
			Console.OUT.println("--------Dist-Dup Block matrix trans-multiply test failed!--------");
		return ret;
	}
	
	public def testMultTrans():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Starting Dist-Dup block matrix multiply-transpose test (transpose on 2nd operand)");
		val A = DistBlockMatrix.makeDense(gA, dA) as DistBlockMatrix(M,K);
		val B = DupBlockMatrix.makeDense(gTransB) as DupBlockMatrix(N,K);
		val C = DistBlockMatrix.makeDense(gC, dC) as DistBlockMatrix(M,N);
		
		A.init((r:Long,c:Long)=>1.0*((r+c)));
		B.init((r:Long,c:Long)=>1.0*((r+c)));
		
		DistDupMult.compMultTrans(A, B, C, false);
		
		val dA = A.toDense() as DenseMatrix(M,K);
		val dB = B.toDense() as DenseMatrix(N,K);
		val dC = DenseMatrix.make(M,N);
		dC.multTrans(dA, dB, false);
		
		ret &= dC.equals(C as Matrix(dC.M,dC.N));

		if (!ret)
			Console.OUT.println("--------Dist-Dup block matrix multiply-transpose test failed!--------");
		return ret;
	}

    public static def main(args:Rail[String]) {
		new TestDistDupMult(args).execute();
	}
} 
