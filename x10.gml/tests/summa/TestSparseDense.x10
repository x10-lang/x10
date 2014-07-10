/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

import x10.matrix.dist.summa.SummaSparseMultDense;

/**
 * This class contains test cases for sparse-dense matrix multiplication with SUMMA.
 */
public class TestSparseDense{
    public static def main(args:Rail[String]) {
		val testcase = new SummaSparseMultDenseTest(args);
		testcase.run();
	}
}

class SummaSparseMultDenseTest {
	public val M:Long;
	public val N:Long;
	public val K:Long;
	public val nzd:Double;

	public val pA:Grid;
	public val pB:Grid;
	public val pC:Grid;
	
    public def this(args:Rail[String]) {
		M   = args.size > 0 ? Long.parse(args(0)):25;
		N   = args.size > 1 ? Long.parse(args(1)):26;
		K   = args.size > 2 ? Long.parse(args(2)):27;	
		nzd = args.size > 3 ?Double.parse(args(3)):0.5; 
		
		val numP = Place.numPlaces();//Place.numPlaces();
		Console.OUT.printf("\nTest SUMMA dist sparse*dense matrix over %d places and sparsity: %f\n", 
							numP, nzd);
		pA = Grid.make(M, K);
		pB = Grid.make(K, N);
		pC = Grid.make(M, N);
	}
	
	public def run(): void {
		var ret:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!
		ret &= (testSparseMultDense());
		ret &= (testSparseMultDenseTrans());
		
		if (!ret)
			Console.OUT.println("--------SUMMA x10 distributed sparse*dense matrix test failed!--------");
    }
	}
	
	public def testSparseMultDense():Boolean {
		val numP = Place.numPlaces();//Place.numPlaces();
		Console.OUT.printf("\nTest SUMMA dist sparse*dense matrix over %d places and sparsity %f\n", 
				numP, nzd);
		val da = DistSparseMatrix.make(pA, nzd);
		da.initRandom();
		
		val db = DistDenseMatrix.make(pB);
		db.initRandom();

		val dc = DistDenseMatrix.make(pC);

		SummaSparseMultDense.mult(0, 0.0, da, db, dc);
		
		val ma = da.toDense();
		val mb = db.toDense();
		val mc = DenseMatrix.make(ma.M, mb.N);
		
		mc.mult(ma, mb);

		val ret = dc.equals(mc as Matrix(dc.M, dc.N));
		if (!ret)
			Console.OUT.println("-----SUMMA x10 distributed sparse*dense matrix test failed!-----");
		return ret;
	}
	
	public def testSparseMultDenseTrans():Boolean {
		val numP = Place.numPlaces();//Place.numPlaces();
		Console.OUT.printf("\nTest SUMMA x10 dist sparse*dense^T over %d places\n", numP);

		val da = DistSparseMatrix.make(M, K, nzd); 
		da.initRandom();
		
		val db = DistDenseMatrix.make(N, K);
		db.initRandom();

		val dc = DistDenseMatrix.make(M, N);

		SummaSparseMultDense.multTrans(0, 0.0, da, db, dc);
		
		val ma = da.toDense();
		val mb = db.toDense();
		val mc = DenseMatrix.make(ma.M, mb.M);
		
		mc.multTrans(ma, mb);

		val ret = dc.equals(mc as Matrix(dc.M, dc.N));
		if (!ret)
			Console.OUT.println("-----SUMMA x10 distributed sparse*dense^T test failed!-----");
		return ret;
	}
}
