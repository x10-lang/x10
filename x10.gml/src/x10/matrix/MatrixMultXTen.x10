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

package x10.matrix;

import x10.io.Console;
import x10.util.Random;


/**
 *  This class provides matrix multiplication driver in X10, when the actual matrix type is unknown.
 *  All elements are accessed via the abstract method, which could have high latency.
 *  The result of multiply is stored in dense format.
 *  
 */
public class MatrixMultXTen {

	//================================================================
	//================================================================
	// C = A * B + plus * C

	/**
	 * X10 driver of matrix multiplication: C+= A &#42 B if plus is true, else C = A &#42 B
	 * If A or B (or both) needs to be transposed , using different methods,
	 * compMatrixMultTrans (A &#42 B<sup>T<sup>) or compTransMultMatrix (A<sup>T<sup> &#42 B)
	 *
	 *
	 * @param A     first matrix
	 * @param B     second matrix
	 * @param C     dense matrix used to store the result
	 * @param plus  add-on flag
	 *
	 */
	public static def comp(
			A:Matrix, 
			B:Matrix{self.M==A.N}, 
			C:DenseMatrix{C.M==A.M&&C.N==B.N}, 
			plus:Boolean):Matrix(C){
		var startcol:Int = 0;
		for (var c:Int=0; c<B.N; c++) {
			if (!plus) {
				for (var i:Int=startcol; i<startcol+C.M; i++)
					C.d(i) = 0.0D;
			}

			for (var k:Int=0; k<A.N; k++) {
				val v2    = B(k, c); //m2(k, c)
				//
				if (MathTool.isZero(v2)) continue;
				for (var r:Int=0; r<A.M; r++) {
					val v1= A(r, k); //m1(r, k)
					if (MathTool.isZero(v1)) continue;
					C.d(startcol+r) += v1 * v2;
				}
			}
			startcol += C.M;
		}		
		return C;
	}

	//

	/**
	 * X10 driver of matrix multiplication: C+= A<sup>T</sup> &#42 B if plus is true, 
	 * else C = A<sup>T</sup> &#42 B
	 *
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
		var startcol:Int = 0;
		for (var c:Int=0; c<B.N; c++) {
			if (!plus) {
				for (var i:Int=startcol; i<startcol+C.M; i++) C.d(i) = 0.0D;
			}

			for (var k:Int=0; k<A.M; k++) {
				val v2    = B(k, c);
				//
				if (MathTool.isZero(v2)) continue;
				for (var r:Int=0; r<A.N; r++) {
					val v1= A(k, r);
					if (MathTool.isZero(v1)) continue;
					C.d(startcol+r) += v1 * v2;
				}
			}
			startcol += C.M;
		}
		return C;
	}

	// 
	/**
	 * x10 matrix driver of matrix multiplication, C = A &#42 B<sup>T</sup> if plus is true
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
		var startcol:Int = 0;
		for (var c:Int=0; c<B.M; c++) {
			if (!plus) {
				for (var i:Int=startcol; i<startcol+C.M; i++) C.d(i) = 0.0D;
			}

			for (var k:Int=0; k<A.N; k++) {
				val v2 = B(c, k);
				//
				if (MathTool.isZero(v2)) continue;
				for (var r:Int=0; r<A.M; r++) {
					val v1= A(r, k);
					if (MathTool.isZero(v1)) continue;
					C.d(startcol+r) += v1 * v2;
				}
			}
			startcol += C.M;
		} 
		return C;
	}
	
	/**
	 * x10 driver of matrix multiplication, C += A<sup>T</sup> &#42 B<sup>T</sup> 
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
		var startcol:Int = 0;
		//SysUtil.assure(C.M==A.N&&A.M==B.N&&B.M==C.N, 
		//			   "Dimension mismatch in Matrix.T()*Matrix.T()");

		for (var c:Int=0; c<B.M; c++) {
			if (!plus) {
				for (var i:Int=startcol; i<startcol+C.M; i++) C.d(i) = 0.0D;
			}
			for (var k:Int=0; k<A.M; k++) {
				val v2    = B(c, k);
				//
				if (MathTool.isZero(v2)) continue;
				for (var r:Int=0; r<A.N; r++) {
					val v1= A(k, r);
					if (MathTool.isZero(v1)) continue;
					C.d(startcol+r) += v1 * v2;
				}
			}
			startcol += C.M;
		}
		return C;
	}

	//-------------------------------------------------------------------
	// Simplified mult interface
	//-------------------------------------------------------------------
	/**
	 * X10 driver of matrix multiplication in short: C = A &#42 B,
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
	 * X10 driver of matrix multiplication. Return A &#42 B in a new 
	 * dense matrix object
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