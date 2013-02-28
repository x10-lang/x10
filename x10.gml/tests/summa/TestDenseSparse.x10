/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;
import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.block.Grid;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

import x10.matrix.dist.summa.SummaDenseMultSparse;

/**
   This class contains test cases for dense matrix multiplication.
   <p>

   <p>
 */

public class TestDenseSparse{
    public static def main(args:Rail[String]) {
		val testcase = new SummaDenseMultSparseTest(args);
		testcase.run();
	}
}

class SummaDenseMultSparseTest {

	public val M:Int;
	public val N:Int;
	public val K:Int;
	public val nzd:Double;

	public val pA:Grid;
	public val pB:Grid;
	public val pC:Grid;
	
    public def this(args:Rail[String]) {
		M   = args.size > 0 ?Int.parse(args(0)):25;
		N   = args.size > 1 ?Int.parse(args(1)):21;
		K   = args.size > 2 ?Int.parse(args(2)):27;	
		nzd = args.size > 3 ?Double.parse(args(3)):0.5; 
		
		val numP = Place.numPlaces();//Place.MAX_PLACES;
		Console.OUT.printf("\nTest SUMMA dist dense*sparse matrix over %d places and sparsity: %f\n", 
							numP, nzd);
		pA = Grid.make(M, K);
		pB = Grid.make(K, N);
		pC = Grid.make(M, N);
	}
	
	public def run(): void {
		var ret:Boolean = true;
 		// Set the matrix function

		ret &= (testDenseMultSparse());
		ret &= (testDenseMultSparseTrans());
		
		if (ret)
			Console.OUT.println("SUMMA x10 distributed dense * sparse matrix multiply test passed!");
		else
			Console.OUT.println("--------SUMMA x10 distributed dense * sparse matrix multiply test failed!--------");
	}
	//------------------------------------------------

	//-----------------------------------------------------------------
	
	public def testDenseMultSparse():Boolean {
		val numP = Place.numPlaces();//Place.MAX_PLACES;
		Console.OUT.printf("\nTest SUMMA dist dense*sparse matrix over %d places and sparsity %f\n", 
				numP, nzd);
		Debug.flushln("Start allocating memory space for dist dense matrix A");
		val da = DistDenseMatrix.make(pA);
		Debug.flushln("Start initializing sparse matrix A");
		da.initRandom();
		
		Debug.flushln("Start allocating memory space for dist sparse matrix B");
		val db = DistSparseMatrix.make(pB, nzd);
		db.initRandom();
		Debug.flushln("Start initializing sparse matrix B");

		val dc = DistDenseMatrix.make(pC); 

		Debug.flushln("Start calling SUMMA distributed dense*sparse routine");
		SummaDenseMultSparse.mult(1, 0.0, da, db, dc);
		Debug.flushln("SUMMA done");
		
		val ma = da.toDense();
		val mb = db.toDense();
		val mc = DenseMatrix.make(ma.M, mb.N);
		
		Debug.flushln("Start sequential dense matrix multiply");
		DenseMatrixBLAS.comp(ma, mb, mc, false);
		Debug.flushln("Done sequential dense matrix multiply");

		val ret = dc.equals(mc as Matrix(dc.M, dc.N));
		if (ret)
			Console.OUT.println("SUMMA x10 distributed dense*sparse test passed!");
		else
			Console.OUT.println("-----SUMMA x10 distributed dense*sparse matrix multplication test failed!-----");
		return ret;
	}
	
	public def testDenseMultSparseTrans():Boolean {
		val numP = Place.numPlaces();//Place.MAX_PLACES;
		Console.OUT.printf("\nTest SUMMA x10 dist dense*sparse^T over %d places\n", numP);

		Debug.flushln("Start allocating memory space for sparse matrix A");
		val da = DistDenseMatrix.make(M, K); 
		Debug.flushln("Start initializing dense matrix A "+
				da.grid.numRowBlocks+" "+da.grid.numColBlocks);
		da.initRandom();
		
		Debug.flushln("Start allocating memory space for dist sparse matrix B");
		val db = DistSparseMatrix.make(N, K, nzd);
		Debug.flushln("Start initializing matrix B "+
						db.grid.numRowBlocks+" "+db.grid.numColBlocks );
		db.initRandom();

		val dc = DistDenseMatrix.make(M, N);

		Debug.flushln("Start calling SUMMA dense*sparse^T X10 routine");
		SummaDenseMultSparse.multTrans(1, 0.0, da, db, dc);
		Debug.flushln("SUMMA done");
		
		val ma = da.toDense();
		val mb = db.toDense();
		val mc = DenseMatrix.make(ma.M, mb.M);
		
		Debug.flushln("Start sequential dense matrix multTrans");
		DenseMatrixBLAS.compMultTrans(ma, mb, mc, false);
		Debug.flushln("Done sequential dense matrix multTrans");

		val ret = dc.equals(mc as Matrix(dc.M, dc.N));
		if (ret)
			Console.OUT.println("SUMMA x10 distributed dense*sparse^T test passed!");
		else
			Console.OUT.println("-----SUMMA x10 distributed dense*sparse^T test failed!-----");
		return ret;
	}
}
