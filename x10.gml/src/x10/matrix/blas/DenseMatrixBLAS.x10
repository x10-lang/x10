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

package x10.matrix.blas;

import x10.compiler.CompilerFlags;
import x10.matrix.util.Debug;

import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.SymDense;
import x10.matrix.TriDense;

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
 */
public class DenseMatrixBLAS {

	/**
	 * Use BLAS driver for dense matrix-vector multiplication to
	 * compute y += A &#42 x if plus is true, otherwise y = A &#42 x.
	 *
	 * @param A      the first operand dense matrix in multiplication
	 * @param x      the second operand vector
	 * @param y      vector which is used to store the result
     * @param dim    the dimensions M, N used in BLAS multiply where
     *               M is the number of rows in A and y
                     N is the number of columns in A and rows in x
     * @param offset row and column offsets [Ar, Ac, xr, yr] into matrix/vectors
	 * @param plus   the add-on flag
	 */
	public static def comp(
			A:DenseMatrix, 
            x:Vector, 
            y:Vector, 
            dim:Rail[Long],
            offset:Rail[Long], 
            plus:Boolean) :void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(0) <= A.M && offset(1)+dim(1) <= A.N,
                offset(0)+"+"+dim(0) + " <= " + A.M + " && " + offset(1)+"+"+dim(1) + " <= " + A.N);
            Debug.assure(offset(2)+dim(1) <= x.M && offset(3)+dim(0) <= y.M,
                offset(2)+"+"+dim(1) + " <= " + x.M + " && " + offset(3)+"+"+dim(0) + " <= " + y.M);
        }
		val scal = new Rail[Double](2);
	    scal(0)=1.0;
		scal(1)=plus?1.0:0.0;
        val transA = 0n;
		DriverBLAS.matrix_vector_mult(A.d, x.d, y.d, dim, A.M, offset, scal, transA);
	}

	public static def comp(
			A:DenseMatrix, 
            B:Vector(A.N), 
            C:Vector(A.M), 
            plus:Boolean) :void {
		val dim=[A.M, A.N];
		val scal = new Rail[Double](2);
	    scal(0)=1.0;
		scal(1)=plus?1.0:0.0;
        val transA = 0n;
		DriverBLAS.matrix_vector_mult(A.d, B.d, C.d, dim, scal, transA);
	}

	/**
	 * Use BLAS driver for dense matrix-vector multiplication to
	 * compute y += A<sup>T<sup> &#42 x if plus is true, 
     * otherwise y = A<sup>T<sup> &#42 x.
	 *
	 * @param A      the first operand dense matrix in multiplication
	 * @param x      the second operand vector
	 * @param y      vector which is used to store the result
     * @param dim    the dimensions M, N used in BLAS multiply where
     *               M is the number of rows in A and x
                     N is the number of columns in A and rows in y
     * @param offset row and column offsets [Ar, Ac, xr, yr] into matrix/vectors
	 * @param plus   the add-on flag
	 */
	public static def compTransMult(
			A:DenseMatrix, 
            x:Vector, 
            y:Vector, 
            dim:Rail[Long],
            offset:Rail[Long], 
            plus:Boolean) :void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(0) <= A.M && offset(1)+dim(1) <= A.N,
                offset(0)+"+"+dim(0) + " <= " + A.M + " && " + offset(1)+"+"+dim(1) + " <= " + A.N);
            Debug.assure(offset(2)+dim(0) <= x.M && offset(3)+dim(1) <= y.M,
                offset(2)+"+"+dim(0) + " <= " + x.M + " && " + offset(3)+"+"+dim(1) + " <= " + y.M);
        }
		val scal = new Rail[Double](2);
	    scal(0)=1.0;
		scal(1)=plus?1.0:0.0;
        val transA = 1n;
		DriverBLAS.matrix_vector_mult(A.d, x.d, y.d, dim, A.M, offset, scal, transA);
	}

	public static def compTransMult(
			A:DenseMatrix, 
            B:Vector(A.M), 
            C:Vector(A.N), 
            plus:Boolean) :void {
		val dim=[A.M, A.N];
		val scal = new Rail[Double](2);
	    scal(0)=1.0;
		scal(1)=plus?1.0:0.0;
        val transA = 1n;
		DriverBLAS.matrix_vector_mult(A.d, B.d, C.d, dim, scal, transA);
	}


	// Symmetric multiply vector

	public static def comp(
			A:SymDense,
            B:Vector(A.N), 
            C:Vector(A.N), 
            plus:Boolean) :void {
		val dim=[A.N, A.M];
		val scal = new Rail[Double](2);
	    scal(0)=1.0;
		scal(1)=plus?1.0:0.0;
		DriverBLAS.sym_vector_mult(A.d, B.d, C.d, dim, scal);
	}


	// Symmetric multiply dense

	public static def comp(
			A:SymDense, 
			B:DenseMatrix{A.N==B.M}, 
			C:DenseMatrix{A.M==C.M&&B.N==C.N},
			plus:Boolean  ):void {
		val scal = new Rail[Double](2);
		scal(0) = 1.0;
		scal(1) = plus?1.0:0.0;
		val dims = [ C.M, C.N ];

		DriverBLAS.sym_matrix_mult(A.d, B.d, C.d, dims, scal);
	}

	public static def comp(
			A:DenseMatrix, 
			B:SymDense{A.N==B.M}, 
			C:DenseMatrix{A.M==C.M&&B.N==C.N},
			plus:Boolean  ):void {
		val scal = new Rail[Double](2);
		scal(0) = 1.0;
		scal(1) = plus?1.0:0.0;
		val dims = [ C.M, C.N ];

		DriverBLAS.matrix_sym_mult(A.d, B.d, C.d, dims, scal);
	}


	// Triangular dense matrix multiply


	/**
	 * B = A &#42 B;
 	 */
	public static def comp(A:TriDense, B:DenseMatrix{A.N==B.M}):void  {
		DriverBLAS.tri_matrix_mult(A.d, B.d, [B.M, B.N, A.upper?1L:0L], 0n);
	}
	
	/**
	 * B = A &#42 B<sup>T<sup> ;
	 */
	public static def compMultTrans(A:TriDense, B:DenseMatrix{A.N==B.M}):void  {
		DriverBLAS.tri_matrix_mult(A.d, B.d, [B.M, B.N, A.upper?1L:0L], 1n);
	}

	/**
	 * B = B &#42 A;
	 */
	public static def comp(B:DenseMatrix, A:TriDense{B.N==A.M}):void  {
		DriverBLAS.matrix_tri_mult(B.d, A.d, [B.M, B.N, A.upper?1L:0L], 0n);
	}

	/**
	 * B = B<sup>T<sup> &#42 A;
	 */
	public static def compTransMult(B:DenseMatrix, A:TriDense{B.N==A.M}):void  {
		DriverBLAS.matrix_tri_mult(B.d, A.d, [B.M, B.N, A.upper?1L:0L], 1n);
	}
	

	// Dense multiply with Dense, calling blas


	/**
	 * Using BLAS driver of dense matrix multiplication to
	 * compute C += A &#42 B if plus is true, otherwise C = A &#42 B.
	 *
	 * @param A      the first operand dense matrix in multiplication
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N and K used in BLAS multiply where
     *               M is the number of rows in A and C,
                     N is the number of columns in B and C, and
                     K is the number of columns in A and rows in B
	 * @param plus   the add-on flag
	 */
	public static def comp(A:DenseMatrix, B:DenseMatrix, C:DenseMatrix, dim:Rail[Long], plus:Boolean):void {
		val scaling = new Rail[Double](2);
		scaling(0) = 1.0;
		scaling(1) = plus?1.0:0.0;
		//
		//val tranA= A.isTransposed();
		//val tranB= B.isTransposed();
		//
		val trans = new Rail[Int](2);
		trans(0) = 0n;//tranA?1:0;
		trans(1) = 0n;//tranB?1:0;
        //
        val ld = [A.M, B.M, C.M];
		// Check size
		//Debug.assure(dims(2)==k1, "Two matrixs common dimension mismatch");
		//Debug.assure(C.M==dims(0)&&C.N==dims(1), "Matrix size of result mismatch");
		//
		//Debug.flushln("Calling BLAS mult driver");
		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dim, ld, scaling, trans);
	}

	/**
	 * Using BLAS driver of dense matrix multiplication to
	 * compute C += A &#42 B if plus is true, otherwise C = A &#42 B.
	 *
	 * @param A      the first operand dense matrix in multiplication
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N and K used in BLAS multiply where
     *               M is the number of rows in A and C,
                     N is the number of columns in B and C, and
                     K is the number of columns in A and rows in B
     * @param offset row and column offsets [Ar, Ac, Br, Bc, Cr, Cc] into matrices
	 * @param plus   the add-on flag
	 */
	public static def comp(A:DenseMatrix, B:DenseMatrix, C:DenseMatrix, dim:Rail[Long], offset:Rail[Long], plus:Boolean):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(0) <= A.M && offset(1)+dim(2) <= A.N,
                offset(0)+"+"+dim(0) + " <= " + A.M + " && " + offset(1)+"+"+dim(2) + " <= " + A.N);
            Debug.assure(offset(2)+dim(2) <= B.M && offset(3)+dim(1) <= B.N,
                offset(2)+"+"+dim(2) + " <= " + B.M + " && " + offset(3)+"+"+dim(1) + " <= " + B.N);
            Debug.assure(offset(4)+dim(0) <= C.M && offset(5)+dim(1) <= C.N,
                offset(4)+"+"+dim(0) + " <= " + C.M + " && " + offset(5)+"+"+dim(1) + " <= " + C.N);
        }

		val scaling = new Rail[Double](2);
		scaling(0) = 1.0;
		scaling(1) = plus?1.0:0.0;

		val trans = [ 0n, 0n as Int ];

        val ld = [A.M, B.M, C.M];
		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dim, ld, offset, scaling, trans);
	}

	/**
	 * Compute C = A &#42 B
	 *
	 * @param A      the first operand dense matrix in multiplication
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
	 * @param plus   the add-on flag
	 */
	public static def comp(
			A:DenseMatrix, 
			B:DenseMatrix{A.N==B.M}, 
			C:DenseMatrix{A.M==C.M,B.N==C.N},
            plus:Boolean): void {
		val dim = [A.M, B.N, A.N];
		DenseMatrixBLAS.comp(A, B, C, dim, plus);
	}


	// Simplified mult interface

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
			C:DenseMatrix{A.M==C.M,B.N==C.N}): void {
		DenseMatrixBLAS.comp(A, B, C, false);
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
		DenseMatrixBLAS.comp(A, B, C, false);
		return C;
	}


	// Methods supporting transpose 


	/**
	 * Compute C += A<sup>T</sup> &#42 B if plus is true, otherwise 
	 * C = A<sup>T</sup> &#42 B by calling BLAS driver
	 *
	 * @param A      the first operand dense matrix in multiplication which is used as it is transposed
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N and K used in BLAS multiply where
     *               M is the number of rows in A<sup>T</sup> and C,
                     N is the number of columns in B and C, and
                     K is the number of columns in A<sup>T</sup> and rows in B
	 * @param plus   add-on flag
	 */
	public static def compTransMult(
			A:DenseMatrix, 
			B:DenseMatrix, 
			C:DenseMatrix, 
            dim:Rail[Long],
			plus:Boolean):void {
        val scale = plus?[1.0 as Double, 1.0]:[1.0 as Double, 0.0];
	    val trans = [  1n,   0n ];
        val ld = [A.M, B.M, C.M];
		// Perform matrix operation
		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dim, ld, scale, trans);
	}

	/**
	 * Using BLAS driver of dense matrix multiplication to
	 * compute C += A<sup>T</sup> &#42 B if plus is true, otherwise C = A<sup>T</sup> &#42 B.
	 *
	 * @param A      the first operand dense matrix in multiplication
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N and K used in BLAS multiply where
     *               M is the number of rows in A<sup>T</sup> and C,
                     N is the number of columns in B and C, and
                     K is the number of columns in A<sup>T</sup> and rows in B
     * @param offset row and column offsets [Ar, Ac, Br, Bc, Cr, Cc] into matrices
	 * @param plus   the add-on flag
	 */
	public static def compTransMult(A:DenseMatrix, B:DenseMatrix, C:DenseMatrix, dim:Rail[Long], offset:Rail[Long], plus:Boolean):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(2) <= A.M && offset(1)+dim(0) <= A.N,
                offset(0)+"+"+dim(2) + " <= " + A.M + " && " + offset(1)+"+"+dim(0) + " <= " + A.N);
            Debug.assure(offset(2)+dim(2) <= B.M && offset(3)+dim(1) <= B.N,
                offset(2)+"+"+dim(2) + " <= " + B.M + " && " + offset(3)+"+"+dim(1) + " <= " + B.N);
            Debug.assure(offset(4)+dim(0) <= C.M && offset(5)+dim(1) <= C.N,
                offset(4)+"+"+dim(0) + " <= " + C.M + " && " + offset(5)+"+"+dim(1) + " <= " + C.N);
        }

		val scaling = new Rail[Double](2);
		scaling(0) = 1.0;
		scaling(1) = plus?1.0:0.0;

	    val trans = [ 1n, 0n ];
        val ld = [A.M, B.M, C.M];

		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dim, ld, offset, scaling, trans);
	}

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
		val dim = [A.N, B.N, A.M];
		compTransMult(A, B, C, dim, plus);
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


	/**
	 * Compute C += A &#42 B<sup>T</sup> if plus is true,  otherwise
	 * C = A &#42 B<sup>T</sup>
	 *
	 * @param A      the first operand dense matrix in multiplication 
	 * @param B      the second operand dense matrix which is used in transposed form
	 * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N and K used in BLAS multiply where
     *               M is the number of rows in A and C,
                     N is the number of columns in B<sup>T</sup> and C, and
                     K is the number of columns in A and rows in B<sup>T</sup>
	 * @param plus   add-on flag
	 */
	public static def compMultTrans(
			A:DenseMatrix, 
			B:DenseMatrix, 
			C:DenseMatrix,
            dim:Rail[Long],
			plus:Boolean):void {
		val scale = plus?[1.0 as Double, 1.0]:[1.0 as Double, 0.0];
	    val trans = [  0n,   1n ];
        val ld = [A.M, B.M, C.M];
		// Perform matrix operation
		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dim, ld, scale, trans);
	}

	/**
	 * Using BLAS driver of dense matrix multiplication to
	 * compute C += A &#42 B<sup>T</sup> if plus is true, otherwise C = A &#42 B<sup>T</sup>.
	 *
	 * @param A      the first operand dense matrix in multiplication
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N and K used in BLAS multiply where
     *               M is the number of rows in A and C,
                     N is the number of columns in B<sup>T</sup> and C, and
                     K is the number of columns in A and rows in B<sup>T</sup>
     * @param offset row and column offsets [Ar, Ac, Br, Bc, Cr, Cc] into matrices
	 * @param plus   the add-on flag
	 */
	public static def compMultTrans(A:DenseMatrix, B:DenseMatrix, C:DenseMatrix, dim:Rail[Long], offset:Rail[Long], plus:Boolean):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(0) <= A.M && offset(1)+dim(2) <= A.N,
                offset(0)+"+"+dim(0) + " <= " + A.M + " && " + offset(1)+"+"+dim(2) + " <= " + A.N);
            Debug.assure(offset(2)+dim(1) <= B.M && offset(3)+dim(2) <= B.N,
                offset(2)+"+"+dim(1) + " <= " + B.M + " && " + offset(3)+"+"+dim(2) + " <= " + B.N);
            Debug.assure(offset(4)+dim(0) <= C.M && offset(5)+dim(1) <= C.N,
                offset(4)+"+"+dim(0) + " <= " + C.M + " && " + offset(5)+"+"+dim(1) + " <= " + C.N);
        }

		val scaling = new Rail[Double](2);
		scaling(0) = 1.0;
		scaling(1) = plus?1.0:0.0;

	    val trans = [ 0n, 1n ];
        val ld = [A.M, B.M, C.M];

		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dim, ld, offset, scaling, trans);
	}
	
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
		val dim = [A.M, B.M, A.N];
        compMultTrans(A, B, C, dim, plus);
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
		val dim      = [A.N, B.M, A.M];
		val scale = plus?[1.0 as Double, 1.0]:[1.0 as Double, 0.0];
	    val trans = [ 1n, 1n as Int ];
		// Perform matrix operation
		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dim, scale, trans);
	}

	/**
	 * Using BLAS driver of dense matrix multiplication to
	 * compute C += A<sup>T</sup> &#42 B<sup>T</sup> if plus is true, otherwise C = A<sup>T</sup> &#42 B<sup>T</sup>.
	 *
	 * @param A      the first operand dense matrix in multiplication
	 * @param B      the second operand dense matrix
	 * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N and K used in BLAS multiply where
     *               M is the number of rows in A<sup>T</sup> and C,
                     N is the number of columns in B<sup>T</sup> and C, and
                     K is the number of columns in A<sup>T</sup> and rows in B<sup>T</sup>
     * @param offset row and column offsets [Ar, Ac, Br, Bc, Cr, Cc] into matrices
	 * @param plus   the add-on flag
	 */
	public static def compTransMultTrans(A:DenseMatrix, B:DenseMatrix, C:DenseMatrix, dim:Rail[Long], offset:Rail[Long], plus:Boolean):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(2) <= A.M && offset(1)+dim(0) <= A.N,
                offset(0)+"+"+dim(2) + " <= " + A.M + " && " + offset(1)+"+"+dim(0) + " <= " + A.N);
            Debug.assure(offset(2)+dim(1) <= B.M && offset(3)+dim(2) <= B.N,
                offset(2)+"+"+dim(1) + " <= " + B.M + " && " + offset(3)+"+"+dim(2) + " <= " + B.N);
            Debug.assure(offset(4)+dim(0) <= C.M && offset(5)+dim(1) <= C.N,
                offset(4)+"+"+dim(0) + " <= " + C.M + " && " + offset(5)+"+"+dim(1) + " <= " + C.N);
        }

		val scaling = new Rail[Double](2);
		scaling(0) = 1.0;
		scaling(1) = plus?1.0:0.0;

	    val trans = [ 1n, 1n as Int ];
		val ld = [A.M, B.M, C.M];

		DriverBLAS.matrix_matrix_mult(A.d, B.d, C.d, dim, ld, offset, scaling, trans);
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

    /**
     * Compute symmetric rank K update C += A &#42 A<sup>T</sup>
     *
     * @param A      a symmetric dense matrix
     * @param C      dense matrix which is used to store the result
     * @param upper  if true, update upper half of C; otherwise update lower half
     * @param plus   the add-on flag
     */
    public static def symRankKUpdate(A:DenseMatrix, C:DenseMatrix{C.M==C.N,C.N==A.M}, upper:Boolean, plus:Boolean):void {
        val dim = [A.M, A.N];
        val scaling = new Rail[Double](2);
        scaling(0) = 1.0;
        scaling(1) = plus?1.0:0.0;
        DriverBLAS.sym_rank_k_update(A.d, C.d, dim, scaling, upper, false);
    }

    /**
     * Compute symmetric rank K update C += A &#42 A<sup>T</sup>
     * for offset patches within A, C
     *
     * @param A      a symmetric dense matrix
     * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N used in BLAS rank-K update where
     *               M is the number of rows in the patch of A and the 
                     number of rows and columns in the patch of C, and
                     N is the number of columns in the patch of A
     * @param offset row and column offsets [Ar, Ac, Cr, Cc] into matrices
     * @param upper  if true, update upper half of C; otherwise update lower half
     * @param plus   the add-on flag
     */
    public static def symRankKUpdate(A:DenseMatrix, C:DenseMatrix, dim:Rail[Long], offset:Rail[Long], upper:Boolean, plus:Boolean):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(0) <= A.M && offset(1)+dim(1) <= A.N,
                offset(0)+"+"+dim(0) + " <= " + A.M + " && " + offset(1)+"+"+dim(1) + " <= " + A.N);
            Debug.assure(offset(2)+dim(0) <= C.M && offset(3)+dim(0) <= C.N,
                offset(2)+"+"+dim(0) + " <= " + C.M + " && " + offset(3)+"+"+dim(1) + " <= " + C.N);
        }
        val scaling = new Rail[Double](2);
        scaling(0) = 1.0;
        scaling(1) = plus?1.0:0.0;
        val ld = [A.M, C.M];
        DriverBLAS.sym_rank_k_update(A.d, C.d, dim, ld, offset, scaling, upper, false);
    }

    /**
     * Compute symmetric rank K update C += A<sup>T</sup> &#42 A
     *
     * @param A      a symmetric dense matrix which is used in transposed form
     * @param C      dense matrix which is used to store the result
     * @param upper  if true, update upper half of C; otherwise update lower half
     * @param plus   the add-on flag
     */
    public static def symRankKUpdateTrans(A:DenseMatrix, C:DenseMatrix{C.M==C.N,C.N==A.N}, upper:Boolean, plus:Boolean):void {
        val dim = [A.N, A.M];
        val scaling = new Rail[Double](2);
        scaling(0) = 1.0;
        scaling(1) = plus?1.0:0.0;
        DriverBLAS.sym_rank_k_update(A.d, C.d, dim, scaling, upper, true);
    }

    /**
     * Compute symmetric rank K update C += A<sup>T</sup> &#42 A
     * for offset patches within A, C
     *
     * @param A      a symmetric dense matrix which is used in transposed form
     * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N used in BLAS rank-K update where
     *               M is the number of columns in the patch of A and the 
                     number of rows and columns in the patch of C, and
                     N is the number of rows in the patch of A
     * @param offset row and column offsets [Ar, Ac, Cr, Cc] into matrices
     * @param upper  if true, update upper half of C; otherwise update lower half
     * @param plus   the add-on flag
     */
    public static def symRankKUpdateTrans(A:DenseMatrix, C:DenseMatrix, dim:Rail[Long], offset:Rail[Long], upper:Boolean, plus:Boolean):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(1) <= A.M && offset(1)+dim(0) <= A.N,
                offset(0)+"+"+dim(1) + " <= " + A.M + " && " + offset(1)+"+"+dim(0) + " <= " + A.N);
            Debug.assure(offset(2)+dim(0) <= C.M && offset(3)+dim(0) <= C.N,
                offset(2)+"+"+dim(0) + " <= " + C.M + " && " + offset(3)+"+"+dim(1) + " <= " + C.N);
        }
        val scaling = new Rail[Double](2);
        scaling(0) = 1.0;
        scaling(1) = plus?1.0:0.0;
        val ld = [A.M, C.M];
        DriverBLAS.sym_rank_k_update(A.d, C.d, dim, ld, offset, scaling, upper, true);
    }

	/**
	 * Triangular solver  A &#42  x = b
	 */	
	public static def solveTriMultVec(A:TriDense, bx:Vector(A.N)) : void {
		DriverBLAS.tri_vector_solve(A.d, bx.d, [A.M, A.N, A.upper?1L:0L], 0n);
	}

	/**
	 * Solve matrix A &#42  X = B
	 */
	public static def solveTriMultMat(A:TriDense, BX:DenseMatrix(A.N)) : void {
		DriverBLAS.tri_matrix_solve(A.d, BX.d, [BX.M, BX.N, A.upper?1L:0L], 0n);
	}

	public static def solveTriTransMultMat(A:TriDense, BX:DenseMatrix(A.M,A.N)) : void {
		DriverBLAS.tri_matrix_solve(A.d, BX.d, [BX.M, BX.N, A.upper?1L:0L], 1n);
	}

	/**
	 * Solve matrix X &#42 op(A) = B 
	 */
	public static def solveMatMultTri(BX:DenseMatrix, A:TriDense(BX.N)) : void {
		DriverBLAS.matrix_tri_solve(BX.d, A.d, [BX.M, BX.N, A.upper?1L:0L], 0n);
	}
	
	public static def solveMatMultTransTri(BX:DenseMatrix, A:TriDense(BX.M)) : void {
		DriverBLAS.matrix_tri_solve(BX.d, A.d, [BX.M, BX.N, A.upper?1L:0L], 1n);
	}	

}
