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

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.util.MathTool;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;
import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistDupVectorMult;

public class MatVecMult {
    public static def main(args:Rail[String]) {
		val testcase = new MatVecMult(args);
		testcase.run();
	}

    public val M:Long;
	public val N:Long;
	public val bM:Long;
	public val bN:Long;
	public val nzd:Double;
	public val verify:Boolean;
	
	public def this(args:Rail[String]) {
		M = args.size > 0      ? Long.parse(args(0)):10;
		N = args.size > 1      ? Long.parse(args(1)):M+1;
		bM = args.size > 2     ? Long.parse(args(2)):4;
		bN = args.size > 3     ? Long.parse(args(3)):5;
		nzd =  args.size > 4   ? Double.parse(args(4)):0.99;
		verify = args.size > 5 ? true:false;
	
	}
	public def run (): void {
		Console.OUT.println("Starting Dist Matrix-Vector multiply demo");
		Console.OUT.printf("Matrix (%d,%d) ", M, N);
		Console.OUT.printf(" partitioned in (%dx%d) and nzd:%f\n", bM, bN, nzd);

		var ret:Boolean = true;
		Console.OUT.printf("----------------------------------------------------\n");

		ret &= (demoDistMatDistVecMult());
		ret &= (demoDistMatDupVecMult());
		ret &= (demoDistDupDupMult());
		
		Console.OUT.printf("----------------------------------------------------\n");
		if (ret)
			Console.OUT.println("Dist Matrix-Vector multiply demo done");
		else
			Console.OUT.println("----------------Error in Dist Matrix-Vector multiply!----------------");
	}
	
	public def demoDistMatDistVecMult():Boolean{
		Console.OUT.printf("----------------------------------------------------\n");
		Console.OUT.printf("Demo DistBlockMatrix * DistVector = DupVector. Matrix must have horizontal distribution\n");
		
		val pM = 1, pN=Place.numPlaces(); //Horizontal distribution
		
		Console.OUT.printf("Creating dense matrix(%d,%d) partitioned in (%dx%d) blocks ",	M, N, bM, bN);
		Console.OUT.printf("distributed in (%dx%d) places (horizontal dist)\n", pM, pN);
		val mA = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN);
		
		Console.OUT.printf("Creating distributed vector(%d) partitioned and distributed in %d places\n", N, pN);
		val vB = DistVector.make(N, pN);
		
		Console.OUT.printf("Creating output vector(%d) which is duplicated in %d places\n", M, pN);
		val vC = DupVector.make(M);
				
		Console.OUT.printf("Start random initialization for dist matrix and dist vector\n");
		mA.initRandom();
		vB.initRandom();
		
		Console.OUT.printf("Start performing matrix-vector multiplication\n");
		Console.OUT.printf("NOTE: Result is not synchronized in all places\n");
		DistDupVectorMult.comp(mA, vB, vC, false);
		
		if (verify) {
			Console.OUT.printf("Start verficiation\n");
			val dA = mA.toDense();
			val vb = vB.toVector();
			val vc = dA % vb;
			if ( vc.equals(vC.local() as Vector(vc.M)))
				Console.OUT.println("DistMatrix * DistVector verification done");
			else {
				Console.OUT.println("--------Error in DistBlockMatrix * DistVector multiply demo!--------");
				return false;
			}
		}
		return true;
	}
	
	public def demoDistMatDupVecMult():Boolean{
		Console.OUT.printf("----------------------------------------------------\n");
		Console.OUT.printf("Demo DistBlockMatrix * DupVector = DistVector. Matrix must have vertical distribution\n");
		val pM = Place.numPlaces(), pN= 1;//Vertical distribution
		
		Console.OUT.printf("Creating partitioning of matrix(%d,%d) in (%dx%d) blocks\n", M, N, bM, bN);
		val gPart = DistGrid.makeGrid(M, N, bM, bN, pM, pN);
		Console.OUT.printf("Creating distribution of the partition in (%dx%d) places (vertical dist)\n", pM, pN);
		val distg = new DistGrid(gPart, pM, pN);
		
		Console.OUT.printf("Creating Matrix in sparse format for the given partitioning and distribution\n");
		val mA = DistBlockMatrix.makeSparse(gPart, distg, nzd) as DistBlockMatrix(M,N);
		
		Console.OUT.printf("Creating duplicated vector(%d), synchronized among %d places\n", N, pN*pM);
		val vB = DupVector.make(N);
		
		Console.OUT.printf("Creating output DistVector(%d) using row-wise partitioning in %d places\n", M, pM*pN);
		val vC = DistVector.make(M, mA.getAggRowBs()); //Be careful, keep partition same
		//Same: val vC = DistVector.make(M, DistGrid.getAggRowBs(mA.getGrid(), mA.gdist));
		//Same: val vC = DistVector.make(M, pM);
		//Wrong val vC = DistVector.make(M, bM);

		Console.OUT.printf("Start random initialization for sparse matrix and vector\n");
		mA.initRandom();
		vB.initRandom();
		
		Console.OUT.printf("Perform distributed-matrix time duplicated-vector\n");
		DistDupVectorMult.comp(mA, vB, vC, false);
		
		if (verify) {
			Console.OUT.printf("Starting verification\n");
			val vc = mA.toDense() % vB.local();
			if ( vC.equals(vc as Vector(vC.M)))
				Console.OUT.printf("DistBlockMatrix * DupVector result verified\n");
			else {
				Console.OUT.printf("--------Error in DistBlockMatrix * DupVector multiplication!--------");
				return false;
			}
		}
		return true;
	}
	
	public def demoDistDupDupMult():Boolean{
	
		Console.OUT.printf("----------------------------------------------------\n");
		Console.OUT.printf("Deome DistBlockMatrix * DupVector -> DupVector for any matrix DistGrid distribution\n");
		val pM:Long = MathTool.sqrt(Place.numPlaces());
		val pN:Long = Place.numPlaces() / pM;
		
		Console.OUT.printf("Creating dense matrix (%d,%d) partitoned in (%dx%d) blocks ", M, N, bM, bN);
		Console.OUT.printf("distributed over (%dx%d) places\n", pM, pN);
		val mA = DistBlockMatrix.makeDense(M, N, bM, bN, pM, pN);

		Console.OUT.printf("Creating duplicated vector(%d) in all %d places\n", N, pM*pN);
		val vB = DupVector.make(N);

		Console.OUT.printf("Creating duplicated vector(%d) to store the result in all %d places\n", M, pM*pN);
		val vC = DupVector.make(M);
		
		Console.OUT.printf("Start random initialization of matrix and vector\n");
		mA.initRandom();
		vB.initRandom();
		
		Console.OUT.printf("Performing DistMatrix * DupVector = DupVector\n");
		DistDupVectorMult.comp(mA, vB, vC, false);
		
		if (verify) {
			Console.OUT.printf("Start verification\n");
			val dA = mA.toDense() as DenseMatrix(M,N);
			val vb = vB.local() as Vector(N);
			val vc = dA % vb;
		
			if (vc.equals(vC.local() as Vector(vc.M)))
				Console.OUT.println("DistBlockMatrix * DupVector = DupVector Done");
			else {
				Console.OUT.println("--------Error in DistBlockMatrix * DupVector = DupVector!--------");
				return false;
			}
		}
		return true;
	}
	
}
