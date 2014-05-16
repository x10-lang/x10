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

package x10.matrix.sparse;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

/**
 * Implementation of addition operations on sparse matrices.
 */
public class SparseAddToDense {
	/**
	 * Add sparse matrix with dense matrix, result store in dense. 
	 * C += A
	 */
	public static def comp(A:SparseCSC, 
						   C:DenseMatrix(A.M,A.N)) : void {
		
		var off:Long = 0;
		for (var c:Long=0; c<C.N; c++) {
			val aln = A.getCol(c);
			for (var r:Long=0; r<aln.size(); r++) C.d(off+aln.getIndex(r)) += aln.getValue(r);
			off += C.M;
		}
	}


	/**
	 * Add sparse matrix with dense matrix, result store in dense. 
	 * A += C
	 */
	public static def comp(A:DenseMatrix, C:SparseCSC(A.M,A.N)) { 
		comp(C, A);
	}
	/**
	 * Add two sparse matrix to a dense matrix, result store in dense. 
	 * C = A + B
	 */
	public static def comp(A:SparseCSC, 
						   B:SparseCSC(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(B, C);
	}

	/**
	 * Add two sparse matrix to a dense matrix, result store in dense. 
	 * C = A + B
	 */
	public static def comp(A:SparseCSC, 
						   B:SparseCSR(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		B.copyTo(C);
		comp(A, C);
	}

	/**
	 * Add sparse and dense to another dense matrix. 
	 * C = A + B
	 */
	public static def comp(A:SparseCSC, 
						   B:DenseMatrix(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		B.copyTo(C);
		comp(A, C);
	}


	// CSR format 

	/**
	 * Add sparse CSR with dense matrix. Result store in dense
	 * C += A
	 */
	public static def comp(A:SparseCSR, 
						   C:DenseMatrix(A.M,A.N)):void {
		for (var r:Long=0; r<A.M; r++) {
			val aln = A.getRow(r);
			for (var c:Long=0; c<aln.size(); c++) C.d(aln.getIndex(c)*C.M+r) += aln.getValue(c);
		}
	}

	/**
	 * Add sparse CSR with dense matrix. Result store in dense
	 * A += C
	 */
	public static def comp(A:DenseMatrix, 
						   C:SparseCSR(A.M,A.N)) {
		comp(C, A);
	}
	/**
	 * Add two sparse CSR matrices to a dense matrix
	 * C = A + B
	 */
	public static def comp(A:SparseCSR, 
						   B:SparseCSR(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(B, C);
	}

	/**
	 * Add two sparse CSR matrices to a dense matrix
	 * C = A + B
	 */
	public static def comp(A:SparseCSR, 
						   B:SparseCSC(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		A.copyTo(C);
		comp(B, C);
	}

	/**
	 * Add a sparse CSR and a dense to another dense
	 * C = A + B
	 */
	public static def comp(A:SparseCSR, 
						   B:DenseMatrix(A.M,A.N),
						   C:DenseMatrix(A.M,A.N)) : void {
		B.copyTo(C);
		comp(A, C);
	}
}
