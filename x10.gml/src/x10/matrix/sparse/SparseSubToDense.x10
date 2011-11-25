/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.matrix.sparse;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

/**
 * Implementation of subtraction operations on sparse matrices and dense matrices.
 * Result is stored in the dense matrix, no matter the dense matrix is the 
 * first or the second input matrix in the operation.
 * 
 */
class SparseSubToDense {

	/**
	 *  Substract sparse matrix in CSC by a dense matrix 
	 *  C = A - C
	 */
	public static def comp(A:SparseCSC, 
						   C:DenseMatrix(A.M,A.N)) : void {

		var off:Int = 0;
					   
		for (var c:Int=0; c<C.N; c++) {
			val aln = A.getCol(c);
			
			for (var r:Int=off; r<off+C.M;  r++) C.d(r) = -C.d(r);
			for (var r:Int=0; r<aln.size(); r++) C.d(off+aln.getIndex(r)) += aln.getValue(r);
			off += C.M;
		}
	}

	/**
	 * Subtract dense with sparse CSC
	 *  C = C - A
	 */
	public static def comp(C:DenseMatrix, 
						   A:SparseCSC(C.M,C.N)):void {
		var off:Int = 0;
		for (var c:Int=0; c<C.N; c++) {
			val aln = A.getCol(c);
			for (var r:Int=0; r<aln.size(); r++) C.d(off+aln.getIndex(r)) -= aln.getValue(r);
			off += C.M;	
		}
	}

	/**
	 *  Substract sparse CSC with another CSC to a dense matrix 
	 *  C = A - B
	 */
	public static def comp(A:SparseCSC, 
						   B:SparseCSC(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(C, B);
	}

	/**
	 *  Substract sparse CSC with CSR to a dense matrix 
	 *  C = A - B
	 */
	public static def comp(A:SparseCSC, 
						   B:SparseCSR(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		B.copyTo(C);
		comp(A, C);
	}

	/**
	 *  Substract sparse matrix from a dense matrix
	 *  C = A - B
	 */
	public static def comp(A:DenseMatrix, 
						   B:SparseCSC(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		// This is not very efficient
		A.copyTo(C);
		comp(C, B);
	}



    //------------------------------------------------------------------------
	/**
	 * Sub sparse CSR by dense, which also stores the result
	 * C = A - C 
	 */
	public static def comp(A:SparseCSR, 
						   C:DenseMatrix(A.M,A.N)):void {
		//
		for (var r:Int=0; r<A.M; r++) {
			val aln = A.getRow(r);
			for (var i:Int=r; i<A.M*A.N; i+=A.M) C.d(i) = - C.d(i);
			for (var c:Int=0; c<aln.size(); c++) {
				val idx = r+C.M*aln.getIndex(c);
				C.d(idx) +=  aln.getValue(c);
			}
		}
	}

	/**
	 * Sub sparse CSR to dense
	 * C = C - A 
	 */
	public static def comp(C:DenseMatrix,
						   A:SparseCSR(C.M,C.N)):void {

		for (var r:Int=0; r<C.M; r++) {
			val aln = A.getRow(r);
			for (var c:Int=0; c<aln.size(); c++) {
				val idx = r+C.M*aln.getIndex(c);
				C.d(idx) -= aln.getValue(c);
			}
		}
	}

	/**
	 *  Substract sparse CSR with another CSR to a dense matrix 
	 *  C = A - B
	 */
	public static def comp(A:SparseCSR, 
						   B:SparseCSR(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(C, B);
	}

	/**
	 *  Substract sparse CSR with another CSC to a dense matrix 
	 *  C = A - B
	 */
	public static def comp(A:SparseCSR, 
						   B:SparseCSC(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(C, B);
	}


	/**
	 *  Substract sparse CSR with a dense to a dense matrix 
	 *  C = A - B
	 */
	public static def comp(A:SparseCSR, 
						   B:DenseMatrix(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		B.copyTo(C);
		comp(A, C);
	}


	/**
	 *  Substract a dense with CSR to a dense matrix 
	 *  C = A - B
	 */
	public static def comp(A:DenseMatrix, 
						   B:SparseCSR(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(C, B);
	}

}
