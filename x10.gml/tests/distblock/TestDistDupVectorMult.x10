/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

import x10.io.Console;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.MathTool;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;
import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistDupVectorMult;

/**
   <p>

   <p>
 */
public class TestDistDupVectorMult {
	
    public static def main(args:Array[String](1)) {
		val testcase = new RunDDVectorMult(args);
		testcase.run();
	}
}

class RunDDVectorMult {
	public val M:Int;
	public val N:Int;
	public val bM:Int;
	public val bN:Int;
	public val nzd:Double;
	
	public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):20;
		N = args.size > 1 ?Int.parse(args(1)):M+1;
		bM = args.size > 2 ?Int.parse(args(2)):4;
		bN = args.size > 3 ?Int.parse(args(3)):5;
		nzd =  args.size > 6 ?Double.parse(args(6)):0.99;
	
	}
	public def run (): void {
		Console.OUT.println("Starting Dist-Dup block matrix vector multiply tests");
		Console.OUT.printf("Matrix (%d,%d) ", M, N);
		Console.OUT.printf(" partitioned in (%dx%d) and nzd:%f\n", bM, bN, nzd);

		var ret:Boolean = true;

		ret &= (testDistMatDistVecMult());
		ret &= (testDistVecDistMatMult());
		ret &= (testDistMatDupVecMult());
		ret &= (testDupVecDistMatMult());
		ret &= (testDistDupDupMult());
		
		if (ret)
			Console.OUT.println("Dist block matrix - vector multiply test passed!");
		else
			Console.OUT.println("----------------Dist block matrix - vector multiply test failed!----------------");
	}
	
	public def testDistMatDistVecMult():Boolean{
		Console.OUT.println("Starting DistBlockMatrix - DistVector -> DupVector multiply test");
		val mA = DistBlockMatrix.makeDense(M, N, bM, bN, 1, Place.MAX_PLACES) as DistBlockMatrix(M,N);
		val vB = DistVector.make(N, bN);
		val vC = DupVector.make(M);
		
		mA.init((r:Int,c:Int)=>1.0*(r+c));
		vB.init((r:Int)=>1.0*(r+1));
		
		DistDupVectorMult.comp(mA, vB, vC, false);
		//A.printMatrix();
		//B.printMatrix();
		//C.printMatrix();
		
		val dA = mA.toDense() as DenseMatrix(M,N);
		val vb = vB.toVector() as Vector(N);
		val vc = dA % vb;
		if ( vc.equals(vC.local() as Vector(vc.M)))
			Console.OUT.println("DistBlockMatrix - DistVector multiply test passed!");
		else {
			Console.OUT.println("--------DistBlockMatrix - DistVector multiply test failed!--------");
			return false;
		}
		return true;
	}
	
	public def testDistVecDistMatMult() {
		Console.OUT.println("Starting DistVector - DistBlockMatrix -> DupVector multiply test");
		val mB = DistBlockMatrix.makeDense(M, N, bM, bN, Place.MAX_PLACES, 1) as DistBlockMatrix(M,N);
		val vA = DistVector.make(M, bM);

		vA.initRandom();
		mB.initRandom();
		val vC = vA % mB;
		val vc = vA.toVector() % mB.toDense();

		if (vc.equals(vC.local() as Vector(vc.M)))
			Console.OUT.println("DistVector - DistBlockMatrix  multiply test passed!");
		else {
			Console.OUT.println("--------DistVector - DistBlockMatrix multiply test failed!--------");
			return false;
		}
		return true;
	}

	public def testDistMatDupVecMult():Boolean{
		Console.OUT.println("Starting DistBlockMatrix - DupVector -> DistVector multiply test");
		val mA = DistBlockMatrix.makeDense(M, N, bM, bN, Place.MAX_PLACES, 1) as DistBlockMatrix(M,N);
		val vB = DupVector.make(N);
		val vC = DistVector.make(M, bM);
		
		mA.init((r:Int,c:Int)=>1.0+r);
		vB.init((r:Int)=>1.0+r);
		
		DistDupVectorMult.comp(mA, vB, vC, false);
		
		val vc = mA.toDense() % vB.local();
		//vc.print();
		
		if ( vC.equals(vc as Vector(vC.M)))
			Console.OUT.println("DistBlockMatrix - DupVector multiply test passed!");
		else {
			Console.OUT.println("--------DistBlockMatrix - DupVector multiply test failed!--------");
			return false;
		}
		return true;
	}
	
	public def testDupVecDistMatMult() : Boolean {
		Console.OUT.println("Starting DupVector - DistBlockMatrix -> DistVector multiply test");
		val mB = DistBlockMatrix.makeDense(M, N, bM, bN, 1, Place.MAX_PLACES) as DistBlockMatrix(M,N);
		val vA = DupVector.make(M);
		
		vA.initRandom();//((r:Int)=>1.0+r);
		mB.initRandom();//((r:Int,c:Int)=>1.0+c);
		val vC = vA % mB;
		//vC.print();
		val vc = vA.local() % mB.toDense();
		//vc.print();
		if ( vC.equals(vc as Vector(vC.M)))			
			Console.OUT.println("DupVector - DistBlockMatrix multiply test passed!");
		else {
			Console.OUT.println("--------DupVector - DistBlockMatrix multiply test failed!--------");
			return false;
		}
		return true;
	}

	public def testDistDupDupMult():Boolean{
		var ret:Boolean = true;
		val pM:Int = MathTool.sqrt(Place.MAX_PLACES);
		val pN:Int = Place.MAX_PLACES / pM;
		Console.OUT.printf("Starting DistBlockMatrix * DupVector = DupVector multiply test on %d x %d places\n", pM, pN);
		val mA = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN) as DistBlockMatrix(M,N);
		val vB = DupVector.make(N);
		val vC = DupVector.make(M);
		
		mA.initRandom();
		vB.initRandom();
		
		DistDupVectorMult.comp(mA, vB, vC, false);
		//A.printMatrix();
		//B.printMatrix();
		//C.printMatrix();
		
		val dA = mA.toDense() as DenseMatrix(M,N);
		val vb = vB.local() as Vector(N);
		val vc = dA % vb;
		//dC.printMatrix();
		
		ret &= vc.equals(vC.local() as Vector(vc.M));

		if (ret)
			Console.OUT.println("DistBlockMatrix x DupVector = DupVector multiply test passed!");
		else
			Console.OUT.println("--------DistBlockMatrix x DupVector = DupVector multiply test failed!--------");
		return ret;
	}

}