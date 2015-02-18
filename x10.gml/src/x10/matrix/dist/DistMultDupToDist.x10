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

package x10.matrix.dist;

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;


import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.sparse.SparseMultDenseToDense;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;
import x10.matrix.dist.DupDenseMatrix;

/**
 * Implementation of distributed multiply duplicated matrix and store result
 * in distributed dense block matrix over all places.
 * <p>
 * A has 1-D partitioning in nx1 blocks, and B has dense
 * matrix replicated at all n places.
 */
public class DistMultDupToDist {
	/**
	 * DistSparseMatrix matrix A is partitioned in nx1 blocks, where n is number of
	 * places. DupDenseMatrix B has dense matrix replicated in all
	 * n places.
	 */
	public static def comp(
			A:DistSparseMatrix, 
			B:DupDenseMatrix{self.M==A.N}, 
			C:DistDenseMatrix(A.M,B.N),
			plus:Boolean): DistDenseMatrix(C) {
		
		Debug.assure(A.M==C.M&&A.N==B.M&&B.N==C.N, "Matrix dimension mismatch");
		Debug.assure(A.grid.numColBlocks==1L, "Matrix partition of A is not single column block partitioning");
		Debug.assure(C.grid.numColBlocks==1L, "Output matrix partition is not single column block partitioning");
		
		finish ateach([p] in C.dist) {
			val smA = A.getMatrix(p);
			val dmB = B.getMatrix() as DenseMatrix{self.M==smA.N}; 
			// as DenseMatrix(smA.N);
			val dmC = C.getMatrix(p) as DenseMatrix(smA.M, dmB.N);
			//
			/* TIMING */ val stt = Timer.milliTime();
			SparseMultDenseToDense.comp(smA, dmB, dmC, plus);
			/* TIMING */ C.distBs(p).calcTime += Timer.milliTime() - stt;
		}
		return C;
	}

	public static def mult(
			A:DistSparseMatrix, 
			B:DupDenseMatrix{self.M==A.N}, 
			C:DistDenseMatrix(A.M,B.N))	= comp(A, B, C, false);

	public static def comp(
			A:DistSparseMatrix, 
			B:DupDenseMatrix{self.M==A.N}, 
			C:DistDenseMatrix(A.M,B.N))	= comp(A, B, C, false);

	/**
	 * DistSparseMatrix matrix A is partitioned in nx1 blocks partition, 
	 * where n is number of places. 
	 * DupDenseMatrix B has dense matrix replicated in all
	 * n places. The multiplication requires B transposed. 
	 */
	public static def compMultTrans(
			A:DistSparseMatrix, 
			B:DupDenseMatrix{self.N==A.N}, 
			C:DistDenseMatrix(A.M,B.M),
			plus:Boolean) : DistDenseMatrix(C) {
				
		Debug.assure(A.M==C.M&&A.N==B.N&&B.M==C.N, "Matrix dimension mismatch");
		Debug.assure(A.grid.numColBlocks==1L, "Matrix A partition is not single column block partitioning");
		Debug.assure(C.grid.numColBlocks==1L, "Output matrix partition is not single column block partitioning");

		finish ateach([p] in C.dist) {
			val smA = A.getMatrix(p);
			val dmB = B.getMatrix() as DenseMatrix{self.N == smA.N};
			val dmC = C.getMatrix(p) as DenseMatrix(smA.M, dmB.M);
			/* TIMING */ val stt = Timer.milliTime();
			SparseMultDenseToDense.compMultTrans(smA, dmB, dmC, plus);
			/* TIMING */ C.distBs(p).calcTime += Timer.milliTime() - stt;
		}
		return C;
	}

	public static def compMultTrans(
			A:DistSparseMatrix, 
			B:DupDenseMatrix{self.N==A.N}, 
			C:DistDenseMatrix(A.M,B.M))	= compMultTrans(A, B, C, false);

	/**
	 * DistSparseMatrix matrix A is partitioned in 1xn blocks partition, 
	 * where n is number of places. 
	 * DupDenseMatrix B has dense matrix replicated in all
	 * n places. The multiplication requires A transposed to perform
	 * matrix multiplication in all places.
	 */
	public static def compTransMult(
			A:DistSparseMatrix, 
			B:DupDenseMatrix{self.M==A.M}, 
			C:DistDenseMatrix(A.N,B.N),
			plus:Boolean) : DistDenseMatrix(C) {
				
		Debug.assure(A.N==C.M&&A.M==B.M&&B.N==C.N, "Matrix dimension mismatch");
		Debug.assure(A.grid.numRowBlocks==1L, "Matrix A partition is not single row block partitioning");
		Debug.assure(C.grid.numRowBlocks==1L, "Output matrix partition is not single row block partitioning");

		finish ateach(val [p]:Point in C.dist) {
			val smA = A.local();
			val dmB = B.local() as DenseMatrix{self.M == smA.M};
			val dmC = C.local() as DenseMatrix(smA.N, dmB.N);
			/* TIMING */ val stt = Timer.milliTime();
			SparseMultDenseToDense.compTransMult(smA, dmB, dmC, plus);
			/* TIMING */ C.distBs(p).calcTime += Timer.milliTime() - stt;
		}
		return C;
	}

	/**
	 * DistDenseMatrix matrix A is partitioned in nx1 blocks, where n is number of
	 * places. DupDenseMatrix B has dense matrix replicated in all
	 * n places.
	 */
	public static def comp(
			A:DistDenseMatrix, 
			B:DupDenseMatrix{self.M==A.N}, 
			C:DistDenseMatrix(A.M,B.N),
			plus:Boolean ) : DistDenseMatrix(C) {
	  
		Debug.assure(A.M==C.M&&A.N==B.M&&B.N==C.N, "Matrix dimension mismatch");
		Debug.assure(A.grid.numColBlocks==1L, "Matrix input A is not 1-column block partitioning");
		Debug.assure(C.grid.numColBlocks==1L, "Matrix output is not 1-column block partitioning");
		
		finish ateach([p] in C.dist) {
		    val dmA = A.getMatrix(p);
		    val dmB = B.getMatrix() as DenseMatrix(dmA.N);
		    val dmC = C.getMatrix(p) as DenseMatrix(dmA.M, dmB.N);
		    val alpha = 1.0 as ElemType;
		    val beta = (plus?1.0:0.0) as ElemType;
		    /* TIMING */ val stt = Timer.milliTime();
		    DenseMatrixBLAS.comp(alpha, dmA, dmB, beta, dmC);
		    /* TIMING */ C.distBs(p).calcTime += Timer.milliTime() - stt;
		}
		return C;
	}
						   
	public static def comp(
			A:DistDenseMatrix, 
			B:DupDenseMatrix{self.M==A.N}, 
			C:DistDenseMatrix(A.M,B.N))	= comp(A, B, C, false);

	/**
	 * DistDenseMatrix matrix A is partitioned in nx1 blocks, where n is number of
	 * places. DupDenseMatrix B has dense matrix replicated in all
	 * n places. B is transposed in performing multiplication
	 */
	public static def compMultTrans(
			A:DistDenseMatrix, 
			B:DupDenseMatrix{self.N==A.N}, 
			C:DistDenseMatrix(A.M,B.M),
			plus:Boolean) : DistDenseMatrix(C) {
										
	    Debug.assure(A.M==C.M&&A.N==B.N&&B.M==C.N, "Matrix dimension mismatch");
	    Debug.assure(A.grid.numColBlocks==1L, "Matrix input A is not 1-column block partitioning");
	    Debug.assure(C.grid.numColBlocks==1L, "Matrix output is not 1-column block partitioning");
		
	    finish ateach([p] in C.dist) {
		val dmA = A.getMatrix(p);
		val dmB = B.getMatrix() as DenseMatrix{self.N==dmA.N};
		val dmC = C.getMatrix(p) as DenseMatrix(dmA.M, dmB.M);
		
	    	val alpha = 1.0 as ElemType;
    		val beta = (plus?1.0:0.0) as ElemType;
		/* TIMING */ val stt = Timer.milliTime();
		DenseMatrixBLAS.compMultTrans(alpha, dmA, dmB, beta, dmC);
		/* TIMING */ C.distBs(p).calcTime += Timer.milliTime() - stt;
	    }
	    return C;
	}

	public static def compMultTrans(
			A:DistDenseMatrix, 
			B:DupDenseMatrix{self.N==A.N}, 
			C:DistDenseMatrix(A.M,B.M))	= compMultTrans(A, B, C, false);

	/**
	 * DistDenseMatrix matrix A is partitioned in 1xn blocks, where n is number of
	 * places. DupDenseMatrix B has dense matrix replicated in all
	 * n places. A is transposed in performing multiplication in parallel at all
	 * places
	 */
	public static def compTransMult(
			A:DistDenseMatrix, 
			B:DupDenseMatrix{self.M==A.M},
			C:DistDenseMatrix(A.N,B.N),
			plus:Boolean) : DistDenseMatrix(C) {
				
	    Debug.assure(A.N==C.M&&A.M==B.M&&B.N==C.N, "Matrix dimension mismatch");
	    Debug.assure(A.grid.numRowBlocks==1L, "Matrix input A is not 1-row block partitioning");
	    Debug.assure(C.grid.numRowBlocks==1L, "Matrix output is not 1-row block partitioning");
		
	    finish ateach(val [p]:Point in C.dist) {
		val dmA = A.local();
		val dmB = B.local() as DenseMatrix{self.M==dmA.M};
		val dmC = C.local() as DenseMatrix(dmA.N, dmB.N);
		
	    	val alpha = 1.0 as ElemType;
    		val beta = (plus?1.0:0.0) as ElemType;
		/* TIMING */ val stt = Timer.milliTime();
		DenseMatrixBLAS.compTransMult(alpha, dmA, dmB, beta, dmC);
		/* TIMING */ C.distBs(p).calcTime += Timer.milliTime() - stt;
	    }
	    return C;
	}
}
