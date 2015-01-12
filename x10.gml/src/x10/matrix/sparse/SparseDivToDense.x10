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

package x10.matrix.sparse;

import x10.matrix.util.MathTool;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

/**
 * Implementation of division operation on sparse matrices.
 */
class SparseDivToDense {
	/**
	 *  Cellwise div sparse matrix in CSC by a dense matrix 
	 *  C = A - C
	 */
	public static def comp(A:SparseCSC, 
						   C:DenseMatrix(A.M,A.N)) : void {
		var off:Long = 0;
					   
		for (var c:Long=0; c<C.N; c++) {
			val aln = A.getCol(c);
			for (var r:Long=0; r<aln.size(); r++) {
				val v = C.d(off+aln.getIndex(r));
				if (MathTool.isZero(v))
					C.d(off+aln.getIndex(r)) = aln.getValue(r)/MathTool.delta;
				else
					C.d(off+aln.getIndex(r)) = aln.getValue(r)/v;
			}
			off += C.M;
		}
	}

	/**
	 *  Cellwise div sparse CSC with another CSC to a dense matrix 
	 *  C = A / B
	 */
	public static def comp(A:SparseCSC, 
						   B:SparseCSC(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(C, B);
	}

	/**
	 *  Cellwise div sparse CSC with CSR to a dense matrix 
	 *  C = A / B
	 */
	public static def comp(A:SparseCSC, 
						   B:SparseCSR(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(C, B);
	}

	/**
	 * Cellwise div dense with sparse CSC
	 *  C = C / A
	 */
	public static def comp(C:DenseMatrix, 
						   A:SparseCSC(C.M,C.N)):void {
		var off:Long = 0;
		for (var c:Long=0; c<C.N; c++) {
			val aln = A.getCol(c);
			for (var r:Long=0; r<aln.size(); r++) {
				val v = aln.getValue(r);
				if (MathTool.isZero(v))
					C.d(off+aln.getIndex(r)) /= MathTool.delta;
				else
					C.d(off+aln.getIndex(r)) /= v;
			}
			off += C.M;	
		}
	}

	/**
	 *  Cellwise div sparse matrix from a dense matrix
	 *  C = A / B
	 */
	public static def comp(A:DenseMatrix, 
						   B:SparseCSC(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(C, B);
	}


	/**
	 * Cellwise div sparse CSR by dense, which also stores the result
	 * C = A / C 
	 */
	public static def comp(A:SparseCSR, 
						   C:DenseMatrix(A.M,A.N)):void {
		//
		for (var r:Long=0; r<A.M; r++) {
			val aln = A.getRow(r);
			for (var i:Long=r; i<A.M*A.N; i+=A.M) C.d(i) = - C.d(i);
			for (var c:Long=0; c<aln.size(); c++) {
				val idx = r+C.M*aln.getIndex(c);
				val v = C.d(idx);
				if (MathTool.isZero(v))
					C.d(idx) =  aln.getValue(c) / MathTool.delta;
				else 
					C.d(idx) =  aln.getValue(c) /v;
			}
		}
	}

	/**
	 *  Cellwise div sparse CSR with another CSR to a dense matrix 
	 *  C = A / B
	 */
	public static def comp(A:SparseCSR, 
						   B:SparseCSR(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(C, B);
	}

	/**
	 *  Cellwise div sparse CSR with another CSC to a dense matrix 
	 *  C = A / B
	 */
	public static def comp(A:SparseCSR, 
						   B:SparseCSC(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(C, B);
	}

	/**
	 *  Cellwise div sparse CSR with a dense to a dense matrix 
	 *  C = A / B
	 */
	public static def comp(A:SparseCSR, 
						   B:DenseMatrix(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		B.copyTo(C);
		comp(A, C);
	}

	/**
	 * Div sparse CSR to dense
	 * C = C / A 
	 */
	public static def comp(C:DenseMatrix,
						   A:SparseCSR(C.M,C.N)):void {
		for (var r:Long=0; r<C.M; r++) {
			val aln = A.getRow(r);
			for (var c:Long=0; c<aln.size(); c++) {
				val idx = r+C.M*aln.getIndex(c);
				val v = aln.getValue(c);
				if (MathTool.isZero(v))
					C.d(idx) /= MathTool.delta;
				else
					C.d(idx) /= v;
			}
		}
	}

	/**
	 *  Div a dense with CSR to a dense matrix 
	 *  C = A / B
	 */
	public static def comp(A:DenseMatrix, 
						   B:SparseCSR(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(C, B);
	}
}
