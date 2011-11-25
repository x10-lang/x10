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

package x10.matrix.blas;

import x10.io.Console;
import x10.util.Random;

import x10.matrix.Debug;
import x10.matrix.DenseMatrix;

//import x10.matrix.blas.DriverBLAS;

/**
 * This class provides static methods to perform dense matrix 
 * multiplication using BLAS driver. 
 * 
 * The general case of matrix multiplication requires two input
 * dense matrices, A and B, and one output matrix C.
 * 
 * If plus = true, then performs C += A x B, otherwise C = A x B
 */

public class DenseMultBLAS {

	//================================================================
	// Dense multiply with Dense, calling blas
	//================================================================

	/**
	 * Using BLAS driver of dense matrix multiplication to
	 * compute C += A &#42 B if plus is true, otherwise C = A &#42 B.
	 *
	 * @param A      the first operand dense matrix in multiplication
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
	 * @param plus   the add-on flag
	 */
	public static def comp(
			A:DenseMatrix, 
			B:DenseMatrix{A.N==B.M}, 
			C:DenseMatrix{A.M==C.M&&B.N==C.N},
			plus:Boolean  ):void {
		val scaling = new Array[Double](2);
		scaling(0) = 1.0;
		scaling(1) = plus?1.0:0.0;
		//
		//val tranA= A.isTransposed();
		//val tranB= B.isTransposed();
		//
		val dims = new Array[Int](3);
		dims(0) = A.M; //tranA?A.N:A.M;
		dims(1) = B.N; //tranB?B.M:B.N;
		dims(2) = A.N; //tranA?A.M:A.N;
		//val k1 = B.M;  //tranB?B.N:B.M;
		//
		val trans = new Array[Int](2);
		trans(0) = 0;//tranA?1:0;
		trans(1) = 0;//tranB?1:0;
		// Check size
		//Debug.assure(dims(2)==k1, "Two matrixs common dimension mismatch");
		//Debug.assure(C.M==dims(0)&&C.N==dims(1), "Matrix size of result mismatch");
		//
		//Debug.flushln("Calling BLAS mult driver");
		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dims, scaling, trans);
	}

	//-------------------------------------------------------------------
	// Simplified mult interface
	//-------------------------------------------------------------------
	
	/**
	 * Compute C = A &#42 B
	 *
	 * @param A      the first operand dense matrix in multiplication
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
	 */
	public static def comp(
			A:DenseMatrix, 
			B:DenseMatrix{A.N==B.M}, 
			C:DenseMatrix{A.M==C.M,B.N==C.N}  ): void {
		DenseMultBLAS.comp(A, B, C, false);
	}
	/**
	 * Compute A &#42 B and store result in a new dense matrix object. 
	 *
	 * @param A      the first operand dense matrix in multiplication
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
	 */
	public static def comp(
			A:DenseMatrix, 
			B:DenseMatrix{A.N==B.M}):DenseMatrix(A.M,B.N) {
				
		val C = DenseMatrix.make(A.M, B.N);
		DenseMultBLAS.comp(A, B, C, false);
		return C;
	}

    //------------------------------------------------------------------------
	// Methods supporting transpose 
	//------------------------------------------------------------------------

	/**
	 * Compute C += A<sup>T</sup> &#42 B if plus is true, otherwise 
	 * C = A<sup>T</sup> &#42 B by calling BLAS driver
	 *
	 * @param A      the first operand dense matrix in multiplication which is used as it is transposed
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
	 * @param plus   add-on flag
	 */
	public static def compTransMult(
			A:DenseMatrix, 
			B:DenseMatrix{B.M==A.M}, 
			C:DenseMatrix{C.M==A.N,C.N==B.N}, 
			plus:Boolean):void {
		val dim:Array[Int](1)      = [A.N, B.N, A.M];
		val scale:Array[Double](1) = plus?[1.0 as Double, 1.0]:[1.0 as Double, 0.0];
	    val trans:Array[Int](1)    = [  1,   0 ];
		// Perform matrix operation
		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dim, scale, trans);
	}

	/**
	 * Compute C = A<sup>T</sup> &#42 B 
	 *
	 * @param A      the first operand dense matrix in multiplication which is used as it is transposed
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
	 */
	public static def compTransMult(
			A:DenseMatrix, 
			B:DenseMatrix{B.M==A.M}, 
			C:DenseMatrix{C.M==A.N,C.N==B.N}):void {
		compTransMult(A, B, C, false);
	}
	/**
	 * Compute A<sup>T</sup> &#42 B and return the result in a new dense matrix object.
	 *
	 * @param A      the first operand dense matrix in multiplication which is used as it is transposed
	 * @param B      the second operand dense matrix
	 * @return       a new dense matrix which is used to store the result
	 */
	public static def compTransMult(
			A:DenseMatrix,  
			B:DenseMatrix{B.M==A.M}):DenseMatrix(A.N,B.N) {
		val C = DenseMatrix.make(A.N, B.N);
		compTransMult(A, B, C, false);
		return C;
	}
	//--------------
	
	/**
	 * Compute C += A &#42 B<sup>T</sup> if plus is true,  otherwise
	 * C = A &#42 B<sup>T</sup>
	 *
	 * @param A      the first operand dense matrix in multiplication 
	 * @param B      the second operand dense matrix which is used in transposed form
	 * @param C      dense matrix which is used to store the result
	 * @param plus   add-on flag
	 */
	public static def compMultTrans(
			A:DenseMatrix, 
			B:DenseMatrix{B.N==A.N}, 
			C:DenseMatrix{C.M==A.M,C.N==B.M}, 
			plus:Boolean):void {
		val dim:Array[Int](1)      = [A.M, B.M, A.N];
		val scale:Array[Double](1) = plus?[1.0 as Double, 1.0]:[1.0 as Double, 0.0];
	    val trans:Array[Int](1)    = [  0,   1 ];
		// Perform matrix operation
		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dim, scale, trans);
	}

	/**
	 * Compute C += A &#42 B<sup>T</sup>.
	 *
	 * @param A      the first operand dense matrix in multiplication 
	 * @param B      the second operand dense matrix which is used in transposed form
	 * @param C      dense matrix which is used to store the result
	 */
	public static def compMultTrans(
			A:DenseMatrix, 
			B:DenseMatrix{B.N==A.N}, 
			C:DenseMatrix{C.M==A.M,C.N==B.M}): void {
		compMultTrans(A, B, C, false);
	}

	/**
	 * Compute A &#42 B<sup>T</sup> and
	 * return the result in a new dense matrix object
	 *
	 * @param A      the first operand dense matrix in multiplication 
	 * @param B      the second operand dense matrix which is used in transposed form
	 * @return       a new dense matrix which is used to store the result
	 */
	public static def compMultTrans(
			A:DenseMatrix, 
			B:DenseMatrix{B.N==A.N}):DenseMatrix(A.M,B.M) {
		val C = DenseMatrix.make(A.M, B.M);
		compMultTrans(A, B, C, false);
		return C;
	}

	//---------------
	
	/**
	 * Compute C += A<sup>T</sup> &#42 B<sup>T</sup> if plus is true, 
	 * else C = A<sup>T</sup> &#42 B<sup>T</sup>
	 *
	 * @param A      the first operand dense matrix in multiplication which is used in transposed form
	 * @param B      the second operand dense matrix which is used in transposed form
	 * @param C      dense matrix which is used to store the result
	 * @param plus   add-on flag
	 */
	public static def compTransMultTrans(
			A:DenseMatrix, 
			B:DenseMatrix{B.N==A.M}, 
			C:DenseMatrix{C.M==A.N,C.N==B.M}, 
			plus:Boolean):void {
		val dim:Array[Int](1)      = [A.N, B.M, A.M];
		val scale:Array[Double](1) = plus?[1.0 as Double, 1.0]:[1.0 as Double, 0.0];
	    val trans:Array[Int](1)    = [  1 as Int,   1 ];
		// Perform matrix operation
		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dim, scale, trans);
	}

	/**
	 * BLAS driver of dense matrix multiplication. Return A<sup>T</sup> &#42 B<sup>T</sup> in a new 
	 * dense matrix object.
	 *
	 * @param A      the first operand dense matrix in multiplication which is used in transposed form
	 * @param B      the second operand dense matrix which is used in transposed form
	 * @return       a new dense matrix which is used to store the result
	 */
	public static def compTransMultTrans(
			A:DenseMatrix, 
			B:DenseMatrix{B.N==A.M}):DenseMatrix(A.N,B.M) {
		val C = DenseMatrix.make(A.N, B.M);
		compTransMultTrans(A, B, C, false);
		return C;
	}

}
