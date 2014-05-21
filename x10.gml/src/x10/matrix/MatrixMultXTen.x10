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

package x10.matrix;

import x10.matrix.util.MathTool;

/**
 * This class implements matrix multiplication purely in X10 for matrices
 * for which the concrete matrix type is unknown at compile time.
 * Element access uses a virtual method call, which could be expensive.
 * The result of multiply is stored in dense format.
 */
public class MatrixMultXTen {
	// C = A * B + plus * C

	/**
	 * Performs the matrix multiplication: C+= A &#42 B if plus is true, else C = A &#42 B
	 * If A or B (or both) needs to be transposed , using different methods,
	 * compMatrixMultTrans (A &#42 B<sup>T<sup>) or compTransMultMatrix (A<sup>T<sup> &#42 B)
	 *
	 *
	 * @param A     first matrix
	 * @param B     second matrix
	 * @param C     dense matrix used to store the result
	 * @param plus  add-on flag
	 */
	public static def comp(
			A:Matrix, 
			B:Matrix{self.M==A.N}, 
			C:DenseMatrix{C.M==A.M&&C.N==B.N}, 
			plus:Boolean):Matrix(C){
		var startcol:Long = 0;
		for (var c:Long=0; c<B.N; c++) {
			if (!plus) {
				for (var i:Long=startcol; i<startcol+C.M; i++)
					C.d(i) = 0.0D;
			}

			for (var k:Long=0; k<A.N; k++) {
				val v2    = B(k, c); //m2(k, c)
				if (MathTool.isZero(v2)) continue;
				for (var r:Long=0; r<A.M; r++) {
					val v1= A(r, k); //m1(r, k)
					C.d(startcol+r) += v1 * v2;
				}
			}
			startcol += C.M;
		}		
		return C;
	}

	/**
	 * Performs the matrix multiplication: C+= A<sup>T</sup> &#42 B if plus is true, 
	 * else C = A<sup>T</sup> &#42 B
	 *
	 * @param A     first matrix which will be used in transposed form
	 * @param B     second matrix
	 * @param C     dense matrix used to store the result
	 * @param plus  add-on flag
	 */
	public static def compTransMult(
			A:Matrix, 
			B:Matrix{B.M==A.M}, 
			C:DenseMatrix(A.N,B.N), 
			plus:Boolean):DenseMatrix(C) {
		var startcol:Long = 0;
		for (var c:Long=0; c<B.N; c++) {
			if (!plus) {
				for (var i:Long=startcol; i<startcol+C.M; i++) C.d(i) = 0.0D;
			}

			for (var k:Long=0; k<A.M; k++) {
				val v2    = B(k, c);
				if (MathTool.isZero(v2)) continue;
				for (var r:Long=0; r<A.N; r++) {
					val v1= A(k, r);
					C.d(startcol+r) += v1 * v2;
				}
			}
			startcol += C.M;
		}
		return C;
	}

	/**
	 * Performs the matrix multiplication: C = A &#42 B<sup>T</sup> if plus is true
	 * else C = A &#42 B<sup>T</sup> 
	 *
	 * @param A     first matrix
	 * @param B     second matrix which will be used as in transposed form
	 * @param C     dense matrix used to store the result
	 * @param plus  add-on flag
	 */
	public static def compMultTrans(
			A:Matrix, 
			B:Matrix{B.N==A.N}, 
			C:DenseMatrix(A.M, B.M), 
			plus:Boolean):DenseMatrix(C) {
		var startcol:Long = 0;
		for (var c:Long=0; c<B.M; c++) {
			if (!plus) {
				for (var i:Long=startcol; i<startcol+C.M; i++) C.d(i) = 0.0D;
			}

			for (var k:Long=0; k<A.N; k++) {
				val v2 = B(c, k);
				if (MathTool.isZero(v2)) continue;
				for (var r:Long=0; r<A.M; r++) {
					val v1= A(r, k);
					C.d(startcol+r) += v1 * v2;
				}
			}
			startcol += C.M;
		} 
		return C;
	}
	
	/**
	 * Performs the matrix multiplication: C += A<sup>T</sup> &#42 B<sup>T</sup> 
	 * if plus is true
	 *
	 * @param A     first matrix which will be used in transposed
	 * @param B     second matrix which will be used as in transposed
	 * @param C     dense matrix used to store the result
	 * @param plus  add-on flag
	 */
	public static def compTransMultTrans(
			A:Matrix, 
			B:Matrix{B.N==A.M}, 
			C:DenseMatrix(A.N, B.M),
			plus:Boolean):DenseMatrix(C) {
		var startcol:Long = 0;

		for (var c:Long=0; c<B.M; c++) {
			if (!plus) {
				for (var i:Long=startcol; i<startcol+C.M; i++) C.d(i) = 0.0D;
			}
			for (var k:Long=0; k<A.M; k++) {
				val v2    = B(c, k);
				if (MathTool.isZero(v2)) continue;
				for (var r:Long=0; r<A.N; r++) {
					val v1= A(k, r);
					C.d(startcol+r) += v1 * v2;
				}
			}
			startcol += C.M;
		}
		return C;
	}

	// Simplified mult interface

	/**
	 * Performs the matrix multiplication: C = A &#42 B,
	 * where C is user-provided dense matrix
	 *
	 * @param A     first matrix
	 * @param B     second matrix
	 * @param C     dense matrix used to store the result
	 */
	public static def comp(
			A:Matrix, 
			B:Matrix{B.M==A.N}, 
			C:DenseMatrix{C.M==A.M&&C.N==B.N}) : void {
		MatrixMultXTen.comp(A, B, C, false);
	}

	/**
	 * Performs the matrix multiplication: A &#42 B returning the result 
	 * in a new DenseMatrix instance.
	 *
	 * @param A   first matrix
	 * @param B   second matrix
	 * @return    new dense matrix storing the result
	 */
	public static def comp(
			A:Matrix, 
			B:Matrix{B.M==A.N}):DenseMatrix(A.M,B.N) {
				
		val C = DenseMatrix.make(A.M, B.N);
		MatrixMultXTen.comp(A, B, C, false);
		return C;
	}
}
