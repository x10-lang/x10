/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2012.
 */

import x10.io.Console;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.summa.SummaMult;
import x10.matrix.distblock.summa.SummaMultTrans;


/**
   <p>

   <p>
 */
public class TestSumma {
	
    public static def main(args:Array[String](1)) {
		val testcase = new TestRunSumma(args);
		testcase.run();
	}
}

class TestRunSumma {
	public val M:Int;
	public val K:Int;
	public val N:Int;
	public val bM:Int;
	public val bN:Int;
	public val nzd:Double;
	
	//-------------
	//Matrix block partitioning
	val gA:Grid;
	val gB:Grid, gTransB:Grid;
	val gC:Grid;
	//Matrix blocks distribution
	val gdA:DistGrid, dA:DistMap;
	val dB:DistMap, dTransB:DistMap;
	val dC:DistMap;
	
	public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):4;
		K = args.size > 1 ?Int.parse(args(1)):6;
		N = args.size > 2 ?Int.parse(args(2)):4;
		bM = args.size > 3 ?Int.parse(args(3)):2;
		bN = args.size > 4 ?Int.parse(args(4)):2;
		nzd =  args.size > 5 ?Double.parse(args(5)):1.0;
		
		gA = new Grid(M, K, bM, bN);
		gB = new Grid(K, N, bM, bN);
		gC = new Grid(M, N, bM, bN);

		gTransB = new Grid(N, K, bM, bN);
		
		gdA = DistGrid.make(gA);
		dA  = gdA.dmap;
		dB = (DistGrid.make(gB)).dmap;
		dC = (DistGrid.make(gC)).dmap;
		
		dTransB = (DistGrid.make(gTransB)).dmap;
	}

    public def run (): void {

		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testMult());
		ret &= (testMultTrans());
		ret &= (testSparseMult());
		ret &= (testSparseMultTrans());
		ret &= (testCylicDistMult());
		ret &= (testCylicDistMultTrans());

		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
	}
    
	public def testMult():Boolean {
		Console.OUT.println("Starting SUMMA on multiply dense block Matrix test");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				M, K, K, N, bM, bN);
		Console.OUT.printf("distributed in (%dx%d) places\n", gdA.numRowPlaces, gdA.numColPlaces);
		var ret:Boolean = true;
		val a = DistBlockMatrix.makeDense(gA, dA).init((r:Int,c:Int)=>1.0*(r+c+1));
		val b = DistBlockMatrix.makeDense(gB, dB).init((r:Int,c:Int)=>2.0*(r*c+1));
		val c = DistBlockMatrix.makeDense(gC, dC);
		SummaMult.mult(a, b, c, false);
		//a.printMatrix();
		//b.printMatrix();
		//c.printMatrix();
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(a.N, b.N);
		val dc= da % db;
		//dc.printMatrix();
		ret &= dc.equals(c as Matrix(dc.M,dc.N));

		if (ret)
			Console.OUT.println("Distributed dense block Matrix SUMMA mult passed!");
		else
			Console.OUT.println("--------Distributed dense block matrix SUMMA mult test failed!--------");
		return ret;
	}

	public def testSparseMult():Boolean {
		Console.OUT.println("Starting SUMMA on multiply sparse block Matrix test");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				M, K, K, N, bM, bN);
		Console.OUT.printf("distributed in (%dx%d) places\n", gdA.numRowPlaces, gdA.numColPlaces);
		var ret:Boolean = true;
		val a = DistBlockMatrix.makeSparse(gA, dA, nzd).init((r:Int,c:Int)=>1.0*(r+c));
		val b = DistBlockMatrix.makeSparse(gB, dB, nzd).init((r:Int,c:Int)=>1.0*(r+c));
		val c = DistBlockMatrix.makeDense(gC, dC);
		//a.printMatrix("A=");
		//b.printMatrix("B=");
		SummaMult.mult(a, b, c, false);
		Debug.flushln("Done SUMMA mult");
		//c.printMatrix("Summa result:");
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(a.N, b.N);
		val dc= da % db;
		//dc.printMatrix("Verified result:");
		ret &= dc.equals(c as Matrix(dc.M,dc.N));

		if (ret)
			Console.OUT.println("Distributed sparse block Matrix SUMMA mult passed!");
		else
			Console.OUT.println("--------Distributed sparse block matrix SUMMA mult test failed!--------");
		return ret;
	}

	
	public def testMultTrans():Boolean {
		Console.OUT.println("Starting SUMMA on multiply-Transpose of dense block Matrix test");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				gA.M, gA.N, gTransB.M, gTransB.N, bM, bN);
		Console.OUT.printf("distributed in (%dx%d) places\n", gdA.numRowPlaces, gdA.numColPlaces);

		var ret:Boolean = true;
		val a = DistBlockMatrix.makeDense(gA, dA).initRandom();
		val b = DistBlockMatrix.makeDense(gTransB, dB).initRandom() as DistBlockMatrix{self.N==a.N};
		val c = DistBlockMatrix.makeDense(gC, dC) as DistBlockMatrix(a.M,b.M);
		SummaMultTrans.multTrans(a, b, c, false);
		Debug.flushln("Done SUMMA multTrans");
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(b.M, a.N);
		val dc= DenseMatrix.make(da.M, db.M) as DenseMatrix(a.M,b.M);
		dc.multTrans(da, db, false);
		ret &= dc.equals(c as Matrix(dc.M,dc.N));

		if (ret)
			Console.OUT.println("Distributed dense block Matrix SUMMA multTrabs passed!");
		else
			Console.OUT.println("--------Distributed dense block matrix SUMMA multTrans test failed!--------");
		return ret;
	}
	
	public def testSparseMultTrans():Boolean {
		Console.OUT.println("Starting SUMMA on multiply-Transpose of sparse block Matrix test");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				gA.M, gA.N, gTransB.M, gTransB.N, bM, bN);
		Console.OUT.printf("nz density:%f, distributed in (%dx%d) places\n", 
				nzd, gdA.numRowPlaces, gdA.numColPlaces);

		var ret:Boolean = true;
		val a = DistBlockMatrix.makeSparse(gA, dA, nzd).initRandom();
		val b = DistBlockMatrix.makeSparse(gTransB, dB, nzd).initRandom() as DistBlockMatrix{self.N==a.N};
		val c = DistBlockMatrix.makeDense(gC, dC) as DistBlockMatrix(a.M,b.M);
		SummaMultTrans.multTrans(a, b, c, false);
		Debug.flushln("Done SUMMA multTrans");
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(b.M, a.N);
		val dc= DenseMatrix.make(da.M, db.M) as DenseMatrix(a.M,b.M);
		dc.multTrans(da, db, false);
		ret &= dc.equals(c as Matrix(dc.M,dc.N));

		if (ret)
			Console.OUT.println("Distributed sparse block Matrix SUMMA multTrabs passed!");
		else
			Console.OUT.println("--------Distributed sparse block matrix SUMMA multTrans test failed!--------");
		return ret;
	}
	
	public def testCylicDistMult():Boolean {
		Console.OUT.println("Starting SUMMA on multiply dense block Matrix test using cylic distribution");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				M, K, K, N, bM, bN);
		Console.OUT.printf("cylic distribution in %d places\n", Place.MAX_PLACES);
		var ret:Boolean = true;
		val dmap = DistMap.makeCylic(bM*bN, Place.MAX_PLACES);
		
		val a = DistBlockMatrix.makeDense(gA, dmap).initRandom();
		val b = DistBlockMatrix.makeDense(gB, dmap).initRandom();
		val c = DistBlockMatrix.makeDense(gC, dmap);
		SummaMult.mult(a, b, c, false);
		
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(a.N, b.N);
		val dc= da % db;
		ret &= dc.equals(c as Matrix(dc.M,dc.N));

		if (ret)
			Console.OUT.println("Cylic distribution of dense block Matrix SUMMA mult test passed!");
		else
			Console.OUT.println("--------Cylic distribution of dense block matrix SUMMA mult test failed!--------");
		return ret;
	}
	
	public def testCylicDistMultTrans():Boolean {
		Console.OUT.println("Starting SUMMA on mult-trans dense block Matrix test using cylic distribution");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				M, K, K, N, bM, bN);
		Console.OUT.printf("cylic distribution in %d places\n", Place.MAX_PLACES-1);
		var ret:Boolean = true;
		val dmap = DistMap.makeCylic(bM*bN, Place.MAX_PLACES-1);
		
		val a = DistBlockMatrix.makeDense(gA, dmap).initRandom();
		val b = DistBlockMatrix.makeDense(gTransB, dmap).initRandom();

		val c = DistBlockMatrix.makeDense(gC, dmap);
		SummaMultTrans.multTrans(a, b, c, false);
		
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(b.M, a.N);
		val dc= DenseMatrix.make(da.M, db.M) as DenseMatrix(a.M,b.M);
		dc.multTrans(da, db, false);
		
		if (ret)
			Console.OUT.println("Cylic distribution of dense block Matrix SUMMA mult-trans test passed!");
		else
			Console.OUT.println("--------Cylic distribution of dense block matrix SUMMA mult-trans test failed!--------");
		return ret;
	}
} 
