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
import x10.compiler.NoInline;

import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.SymMatrix;
import x10.matrix.TriMatrix;

/**
 * This class provides static methods to perform matrix 
 * multiplication and triangular solver function using BLAS driver. 
 * 
 * <P> The general case of matrix multiplication requires two input
 * matrices, A and B, and one output matrix C. Scaling factors 
 * for input matrix A and output matrix C are ignored here. 
 * If plus = true, then performs C += A x B, otherwise C = A x B
 * 
 * <p> The leading dimension of a matrix must be same as its number of rows.
 * All matrices uses column-major storage.
 * 
 * <P> For functions other than multiply, look for in BLAS.x10.
 * 
 */

public class DenseMultBLAS {

	//===============================================================
	// Dense multiply vector
	//===============================================================
	public static def comp(
			A:DenseMatrix, 
            B:Vector(A.N), 
            C:Vector(A.M), 
            plus:Boolean) :void {
		val dim:Array[Int](1)=[A.M, A.N];
		val scal:Array[Double](1) = new Array[Double](2);
	    scal(0)=1.0;
		scal(1)=plus?1.0:0.0;
        val transA:Int = 0;
		DriverBLAS.matrix_vector_mult(A.d, B.d, C.d, dim, scal, transA);
	}

	public static def compTransMult(
			A:DenseMatrix, 
            B:Vector(A.M), 
            C:Vector(A.N), 
            plus:Boolean) :void {
		val dim:Array[Int](1)=[A.M, A.N];
		val scal:Array[Double](1) = new Array[Double](2);
	    scal(0)=1.0;
		scal(1)=plus?1.0:0.0;
        val transA:Int = 1;
		DriverBLAS.matrix_vector_mult(A.d, B.d, C.d, dim, scal, transA);
	}

	//===============================================================
	// Symmetric multiply vector
	//===============================================================
	public static def comp(
			A:SymMatrix,
            B:Vector(A.N), 
            C:Vector(A.N), 
            plus:Boolean) :void {
		val dim:Array[Int](1)=[A.N, A.M];
		val scal:Array[Double](1) = new Array[Double](2);
	    scal(0)=1.0;
		scal(1)=plus?1.0:0.0;
		DriverBLAS.sym_vector_mult(A.d, B.d, C.d, dim, scal);
	}

	//===============================================================
	// Symmetric multiply dense
	//===============================================================
	public static def comp(
			A:SymMatrix, 
			B:DenseMatrix{A.N==B.M}, 
			C:DenseMatrix{A.M==C.M&&B.N==C.N},
			plus:Boolean  ):void {
		val scal = new Array[Double](2);
		scal(0) = 1.0;
		scal(1) = plus?1.0:0.0;
		val dims:Array[Int](1) = [ C.M, C.N ];

		DriverBLAS.sym_matrix_mult(A.d, B.d, C.d, dims, scal);
	}

	public static def comp(
			A:DenseMatrix, 
			B:SymMatrix{A.N==B.M}, 
			C:DenseMatrix{A.M==C.M&&B.N==C.N},
			plus:Boolean  ):void {
		val scal = new Array[Double](2);
		scal(0) = 1.0;
		scal(1) = plus?1.0:0.0;
		val dims:Array[Int](1) = [ C.M, C.N ];

		DriverBLAS.matrix_sym_mult(A.d, B.d, C.d, dims, scal);
	}

	//===============================================================
	// Triangular dense matrix multiply
	//===============================================================

	public static def comp(A:TriMatrix, B:DenseMatrix{A.N==B.M}):void  {
		DriverBLAS.tri_matrix_mult(A.d, B.d, [B.M, B.N], 0);
	}

	public static def compTransMult(A:TriMatrix, B:DenseMatrix{A.N==B.M}):void  {
		DriverBLAS.tri_matrix_mult(A.d, B.d, [B.M, B.N], 1);
	}

	public static def comp(B:DenseMatrix, A:TriMatrix{B.N==A.M}):void  {
		DriverBLAS.matrix_tri_mult(B.d, A.d, [B.M, B.N], 0);
	}

	public static def compMultTrans(B:DenseMatrix, A:TriMatrix{B.N==A.M}):void  {
		DriverBLAS.matrix_tri_mult(B.d, A.d, [B.M, B.N], 1);
	}
	
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
	public static def comp(A:DenseMatrix, B:DenseMatrix{A.N==B.M}, C:DenseMatrix{A.M==C.M&&B.N==C.N}, plus:Boolean):void {
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

	//---------------------------------------------------------------------
	
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
	public static def compTransMultTrans(A:DenseMatrix,	B:DenseMatrix{B.N==A.M}):DenseMatrix(A.N,B.M) {
		val C = DenseMatrix.make(A.N, B.M);
		compTransMultTrans(A, B, C, false);
		return C;
	}
			
	//===========================================================
	//
	//===========================================================
	/**
	 * Triangular solver  A &#42  x = b
	 */	
	public static def solveTriMultVec(A:TriMatrix, bx:Vector(A.N)) : void {
		DriverBLAS.tri_vector_solve(A.d, bx.d, [A.M, A.N], 0);
	}
	//-------------------------------------------------------------
	/**
	 * Solve matrix A &#42  X = B
	 */
	public static def solveTriMultMat(A:TriMatrix, BX:DenseMatrix(A.N)) : void {
		DriverBLAS.tri_matrix_solve(A.d, BX.d, [BX.M, BX.N], 0);
	}

	public static def solveTriTransMultMat(A:TriMatrix, BX:DenseMatrix(A.M,A.N)) : void {
		DriverBLAS.tri_matrix_solve(A.d, BX.d, [BX.M, BX.N], 1);
	}
	
	//-------------------------------------------------------------
	/**
	 * Solve matrix X &#42 op(A) = B 
	 */
	public static def solveMatMultTri(BX:DenseMatrix, A:TriMatrix(BX.N)) : void {
		DriverBLAS.matrix_tri_solve(BX.d, A.d, [BX.M, BX.N], 0);
	}
	
	public static def solveMatMultTransTri(BX:DenseMatrix, A:TriMatrix(BX.M)) : void {
		DriverBLAS.matrix_tri_solve(BX.d, A.d, [BX.M, BX.N], 1);
	}	

}
