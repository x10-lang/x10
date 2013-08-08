/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.block.Grid;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

import x10.matrix.dist.summa.SummaSparse;
import x10.matrix.dist.summa.SummaSparseMultDense;

/**
 * This class contains test cases for sparse matrix multiplication.
 */
public class TestSparse{
    public static def main(args:Rail[String]) {
		val testcase = new SummaSparseMultTest(args);
		testcase.run();
	}
}

class SummaSparseMultTest {
	public val M:Long;
	public val N:Long;
	public val K:Long;
	public val nzd:Double;

	public val pA:Grid;
	public val pB:Grid;
	public val pC:Grid;
	
    public def this(args:Rail[String]) {
		M   = args.size > 0 ? Long.parse(args(0)):21;
		N   = args.size > 1 ? Long.parse(args(1)):31;
		K   = args.size > 2 ? Long.parse(args(2)):17;	
		nzd = args.size > 3 ?Double.parse(args(3)):0.5; 
		
		val numP = Place.numPlaces();//Place.MAX_PLACES;
		Console.OUT.printf("\nTest SUMMA dist sparse matrix over %d places and sparsity: %f, %d %d %d\n", 
							numP, nzd, M, N, K);
		pA = Grid.make(M, K);
		pB = Grid.make(K, N);
		pC = Grid.make(M, N);
	}
	
	public def run(): void {
		var ret:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!

		ret &= (testSparse());
		ret &= (testSparseMultTrans());
		
		if (ret)
			Console.OUT.println("SUMMA x10 distributed dense matrix multiply test passed!");
		else
			Console.OUT.println("--------SUMMA x10 distributed sparse matrix multiply test failed!--------");
    }
	}

	public def testSparse():Boolean {
		val numP = Place.numPlaces();//Place.MAX_PLACES;
		Console.OUT.printf("\nTest SUMMA dist sparse matrix over %d places and sparsity %f\n", 
							numP, nzd);
		Debug.flushln("Start allocating memory space for sparse matrix A");
		val da = DistSparseMatrix.make(pA, nzd);
		da.initRandom();
		
		Debug.flushln("Start allocating memory space for sparse matrix B");
		val db = DistSparseMatrix.make(pB, nzd);
		db.initRandom();

		val dc = DistDenseMatrix.make(pC);

		Debug.flushln("Start calling SUMMA sparse mult sparse to dense X10 routine");
		SummaSparse.mult(0, 0.0, da, db, dc);
		Debug.flushln("SUMMA done");
		
		val ma = da.toDense();
		val mb = db.toDense();
		val mc = DenseMatrix.make(ma.M, mb.N);
		
		Debug.flushln("Start sequential dense matrix multiply");
		DenseMatrixBLAS.comp(ma, mb, mc, false);
		Debug.flushln("Done sequential dense matrix multiply");

		val ret = dc.equals(mc as Matrix(dc.M, dc.N));
		if (ret)
			Console.OUT.println("SUMMA x10 distributed sparse matrix multplication test passed!");
		else
			Console.OUT.println("-----SUMMA x10 distributed sparse matrix multplication test failed!-----");

		return ret;
	}
	
	public def testSparseMultTrans():Boolean {
		val numP = Place.numPlaces();//Place.MAX_PLACES;
		Console.OUT.printf("\nTest SUMMA x10 dist dense matrix multTrans over %d places\n", numP);
		Debug.flushln("Start allocating memory space for matrix A");
		val da = DistSparseMatrix.make(M, K, nzd); 
		Debug.flushln("Start initializing matrix A "+
						da.grid.numRowBlocks+" "+da.grid.numColBlocks);
		da.initRandom();
		Debug.flushln("Start allocating memory space for matrix B");
		val db = DistSparseMatrix.make(N, K, nzd);
		db.initRandom();
		Debug.flushln("Start initializing matrix B "+
						db.grid.numRowBlocks+" "+db.grid.numColBlocks );

		val dc = DistDenseMatrix.make(M, N);

		Debug.flushln("Start calling SUMMA sparse multTrans X10 routine");
		SummaSparse.multTrans(0, 0.0, da, db, dc);
		Debug.flushln("SUMMA done");
		
		val ma = da.toDense();
		val mb = db.toDense();
		val mc = DenseMatrix.make(ma.M, mb.M);
		
		Debug.flushln("Start sequential dense matrix multTrans");
		DenseMatrixBLAS.compMultTrans(ma, mb, mc, false);
		Debug.flushln("Done sequential dense matrix multTrans");

		val ret = dc.equals(mc as Matrix(dc.M, dc.N));
		if (ret)
			Console.OUT.println("SUMMA x10 distributed sparse matrix multTrans test passed!");
		else
			Console.OUT.println("-----SUMMA x10 distributed sparse matrix multTrans test failed!-----");
		return ret;
	}
}
