/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2012-2015.
 */

import harness.x10Test;

import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.block.Grid;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.summa.SummaMult;
import x10.matrix.distblock.summa.SummaMultTrans;

public class TestSumma extends x10Test {
	public val M:Long;
	public val K:Long;
	public val N:Long;
	public val bM:Long;
	public val bN:Long;
	public val nzd:Double;
	public val panel:Long;

	//Matrix block partitioning
	val gA:Grid;
	val gB:Grid, gTransB:Grid;
	val gC:Grid;
	//Matrix blocks distribution
	val gdA:DistGrid, dA:DistMap;
	val dB:DistMap, dTransB:DistMap;
	val dC:DistMap;
	
	public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):4;
		K = args.size > 1 ? Long.parse(args(1)):6;
		N = args.size > 2 ? Long.parse(args(2)):4;
		bM = args.size > 3 ? Long.parse(args(3)):2;
		bN = args.size > 4 ? Long.parse(args(4)):2;
		panel = args.size > 5 ? Long.parse(args(5)):2;
		nzd =  args.size > 6 ?Double.parse(args(6)):1.0;
		
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

    public def run():Boolean {
		var ret:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!
		ret &= (testMult());
		ret &= (testMultTrans());
		ret &= (testSparseMult());
		ret &= (testSparseMultTrans());
		
		//ret &= (testCylicDistMult());
		//ret &= (testCylicDistMultTrans());
		//ret &= (testRandomDistMult());
		//ret &= (testRandomDistMultTrans());
    }
        return ret;
	}
    
	public def testMult():Boolean {
		Console.OUT.println("Starting SUMMA on multiply dense block Matrix test");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				M, K, K, N, bM, bN);
		Console.OUT.printf("distributed in (%dx%d) places\n", gdA.numRowPlaces, gdA.numColPlaces);
		var ret:Boolean = true;
		val a = DistBlockMatrix.makeDense(gA, dA).init((r:Long,c:Long)=>1.0*(r+c+10));
		val b = DistBlockMatrix.makeDense(gB, dB).init((r:Long,c:Long)=>2.0*(r*c+1));
		val c = DistBlockMatrix.makeDense(gC, dC);
		SummaMult.mult(panel, 0.0, a, b, c);
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(a.N, b.N);
		val dc= da % db;
		ret &= dc.equals(c as Matrix(dc.M,dc.N));

		if (!ret)
			Console.OUT.println("--------Distributed dense block matrix SUMMA mult test failed!--------");
		return ret;
	}

	public def testSparseMult():Boolean {
		Console.OUT.println("Starting SUMMA on multiply sparse block Matrix test");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				M, K, K, N, bM, bN);
		Console.OUT.printf("distributed in (%dx%d) places\n", gdA.numRowPlaces, gdA.numColPlaces);
		var ret:Boolean = true;
		val a = DistBlockMatrix.makeSparse(gA, dA, nzd).init((r:Long,c:Long)=>1.0*(r+c));
		val b = DistBlockMatrix.makeSparse(gB, dB, nzd).init((r:Long,c:Long)=>1.0*(r+c));
		val c = DistBlockMatrix.makeDense(gC, dC);
		SummaMult.mult(panel, 0.0, a, b, c);
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(a.N, b.N);
		val dc= da % db;
		ret &= dc.equals(c as Matrix(dc.M,dc.N));

		if (!ret)
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
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(b.M, a.N);
		val dc= DenseMatrix.make(da.M, db.M) as DenseMatrix(a.M,b.M);
		dc.multTrans(da, db, false);
		ret &= dc.equals(c as Matrix(dc.M,dc.N));

		if (!ret)
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
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(b.M, a.N);
		val dc= DenseMatrix.make(da.M, db.M) as DenseMatrix(a.M,b.M);
		dc.multTrans(da, db, false);
		ret &= dc.equals(c as Matrix(dc.M,dc.N));

		if (!ret)
			Console.OUT.println("--------Distributed sparse block matrix SUMMA multTrans test failed!--------");
		return ret;
	}

	public def testCylicDistMult():Boolean {
		Console.OUT.println("Starting SUMMA on multiply dense block Matrix test using cylic distribution");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				M, K, K, N, bM, bN);
		var ret:Boolean = true;
		val dmap = DistMap.makeCylic(bM*bN, Place.numPlaces());
		
		val a = DistBlockMatrix.makeDense(gA, dmap).initRandom();
		val b = DistBlockMatrix.makeDense(gB, dmap).initRandom();
		val c = DistBlockMatrix.makeDense(gC, dmap);
		SummaMult.mult(a, b, c, false);
		
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(a.N, b.N);
		val dc= da % db;
		ret &= dc.equals(c as Matrix(dc.M,dc.N));

		if (!ret)
			Console.OUT.println("--------Cyclic distribution of dense block matrix SUMMA mult test failed!--------");
		return ret;
	}
	
	public def testCylicDistMultTrans():Boolean {
		Console.OUT.println("Starting SUMMA on mult-trans dense block Matrix test using cylic distribution");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				M, K, K, N, bM, bN);
		Console.OUT.printf("cylic distribution in %d places\n", Place.numPlaces());
		var ret:Boolean = true;
		val dmap = DistMap.makeCylic(bM*bN, Place.numPlaces());
		
		val a = DistBlockMatrix.makeDense(gA, dmap).initRandom();
		val b = DistBlockMatrix.makeDense(gTransB, dmap).initRandom() as DistBlockMatrix{self.N==a.N};

		val c = DistBlockMatrix.makeDense(gC, dmap) as DistBlockMatrix(a.M,b.M);
		SummaMultTrans.multTrans(a, b, c, false);
		
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(b.M, a.N);
		val dc= DenseMatrix.make(da.M, db.M) as DenseMatrix(a.M,b.M);
		dc.multTrans(da, db, false);
		
		if (!ret)
			Console.OUT.println("--------Cyclic distribution of dense block matrix SUMMA mult-trans test failed!--------");
		return ret;
	}

	public def testRandomDistMult():Boolean {
		Console.OUT.println("Starting SUMMA on mult dense block Matrix test using random distribution");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				M, K, K, N, bM, bN);
		Console.OUT.printf("randomly distributed in %d places\n", Place.numPlaces());
		var ret:Boolean = true;
		val dmap = DistMap.makeRandom(bM*bN, Place.numPlaces());
		
		val a = DistBlockMatrix.makeDense(gA, dmap).initRandom();
		val b = DistBlockMatrix.makeDense(gB, dmap).initRandom() as DistBlockMatrix(a.N);

		val c = DistBlockMatrix.makeDense(gC, dmap) as DistBlockMatrix(a.M,b.N);
		SummaMult.mult(a, b, c, false);
		
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(b.M, b.N);
		val dc= DenseMatrix.make(da.M, db.N) as DenseMatrix(a.M,b.N);
		dc.mult(da, db, false);
		
		if (!ret)
			Console.OUT.println("--------Random distribution of dense block matrix SUMMA mult test failed!--------");
		return ret;
	}
	
	public def testRandomDistMultTrans():Boolean {
		Console.OUT.println("Starting SUMMA on mult-trans dense block Matrix test using random distribution");
		Console.OUT.printf("matrix (%dx%d) x (%dx%d) partitioned in (%dx%d) blocks ",
				M, K, K, N, bM, bN);
		Console.OUT.printf("randomly distributed in %d places\n", Place.numPlaces());
		var ret:Boolean = true;
		val dmap = DistMap.makeRandom(bM*bN, Place.numPlaces());
		
		val a = DistBlockMatrix.makeDense(gA, dmap).initRandom();
		val b = DistBlockMatrix.makeDense(gTransB, dmap).initRandom() as DistBlockMatrix{self.N==a.N};

		val c = DistBlockMatrix.makeDense(gC, dmap) as DistBlockMatrix(a.M,b.M);
		SummaMultTrans.multTrans(a, b, c, false);
		
		val da= a.toDense() as DenseMatrix(a.M, a.N);
		val db= b.toDense() as DenseMatrix(b.M, a.N);
		val dc= DenseMatrix.make(da.M, db.M) as DenseMatrix(a.M,b.M);
		dc.multTrans(da, db, false);
		
		if (!ret)
			Console.OUT.println("--------Random distribution of dense block matrix SUMMA mult-trans test failed!--------");
		return ret;
	}

    public static def main(args:Rail[String]) {
		new TestSumma(args).execute();
	}
} 
