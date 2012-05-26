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

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;

@NativeCPPInclude("wrap_blas.h")
@NativeCPPCompilationUnit("wrap_blas.c")
	
/**
 *  This class provides BLAS interface in X10 via native calls.
 *  Only Double based matrix data array is supported in the current version.
 *  All matrix data is stored in column-major arrays. 
 *  All methods declared here have corresponding specification defined by
 *  BLAS, please refer to BLAS specification for detailed information.
 *  
 *  <p> 
 * NOTE: This class has been changed to be protected, so that calls of BLAS routines
 * are made through BLAS or DenseMultBLAS.  This is a workaround for managed backend
 * when inlining Java methods from WrapBLAS.java in packages other than blas, which causes 
 * compiler complains that WrapBLAS cannot be found.
 * 
 *  <p>To compile BLAS library, add your system blas path and library in post compile options.
 */
protected class DriverBLAS {

	// 
    //------------------------------------------------------------------------
	// Level One 
    //------------------------------------------------------------------------

	// for	x = a*x
	// Reciprocal Scale: x <-x/alpha
	// void cblas_dscal(blasint N, double alpha, double *X, blasint incX);

	/**
	 * x *= alpha. Raise number of data stored continuously in array X 
	 * by a factor of alpha. The incremental step is 1.
	 *
	 * @param N        number of data to operate on
	 * @param alpha    scalar
	 * @param x        data array
	 */
	@Native("java", "WrapBLAS.scale(#1, #2, (#3).raw().getDoubleArray())")
    @Native("c++","scale(#1, #2, (#3)->raw()->raw())")
		public static native def scale(
				N:Int, 
				alpha:Double, 
				x:Array[Double](1)):void;

	// Vector Copy: y <-x
	// void cblas_dcopy(blasint n, double *x, blasint incx, double *y, blasint incy);
	/**
	 * Copy N data from X to Y, and the incremental step is 1.
	 *
	 * @param N        number of data to copy
	 * @param X        source array
	 * @param Y        destination array
	 */
	@Native("java", "WrapBLAS.copy(#1, (#2).raw().getDoubleArray(), (#3).raw().getDoubleArray())")
	@Native("c++", "copy(#1, (#2)->raw()->raw(), (#3)->raw()->raw())")
		public static native def copy(
				N:int, 
				X:Array[Double](1), 
				Y:Array[Double](1)):void;

	// for	dot product
    // DOT Production: r <- beta * r + alpha * x^T * y
	// double cblas_ddot(blasint n, double *x, blasint incx, double *y, blasint incy);

	/**
	 * Return dot product of array X and Y with incremental step is 1 for X and Y. 
	 *
	 * @param n        number of data to operate
	 * @param X        right side array of dot product
	 * @param Y        left side array of dot product
	 * @return         dot-product result
	 */
    @Native("java","WrapBLAS.dotProd(#1,(#2).raw().getDoubleArray(),(#3).raw().getDoubleArray())")
    @Native("c++","dot_prod(#1,(#2)->raw()->raw(),(#3)->raw()->raw())")
		public static native def dot_prod(
				n:Int, 
				X:Array[Double](1),
				Y:Array[Double](1)):Double;	
	
	// for	Euclidean norm
	// double cblas_dnrm2 (blasint N, double *X, blasint incX);
	/**
	 * Perform Euclidean norm. Incremental step is 1.
	 *
	 * @param  n      number of data to operate in array
	 * @param  X      data array.
	 * @return        Eculidean norm
	 */
    @Native("java","WrapBLAS.norm2(#1,(#2).raw().getDoubleArray())")
    @Native("c++","norm2(#1,(#2)->raw()->raw())")
		public static native def norm(
				n:Int, 
				X:Array[Double]):Double;

	// SUM: for	sum of absolute values
	// double cblas_dasum (blasint n, double *x, blasint incx);
	/**
	 * Sum of absolute values of array X for n number of data.
	 * @param  n      number of data to operate in array
	 * @param  X      data array.
	 * @return        absolute sum
	 */
	@Native("java","WrapBLAS.absSum(#1,(#2).raw().getDoubleArray())")
	@Native("c++","abs_sum(#1,(#2)->raw()->raw())")
		public static native def abs_sum(
				n:Int, 
				X:Array[Double](1)):Double;

   
    //------------------------------------------------------------------------
	// Level Three
    //------------------------------------------------------------------------


    //------------------------------------------------------------------------
	//Simplified matrix-matrix/vector function interface
    //------------------------------------------------------------------------
	
	/**
	 * Call the matrix multiplication implemented in C.  The source code of
	 * this implementation can be found in wrap_blas.c.  This method is for
	 * debugging and performance test purpose only. 
	 */
	@Native("c++","c_mat_mat_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),#4,#5,#6)")
		public static native def c_mat_mat_mult(
				mA:Array[Double](1), 
				mB:Array[Double](1), 
				mC:Array[Double](1),
				M:Int, N:Int, K:Int):void;

	// C = alpah * op(A) * op(B) + beta*C
	/**
	 * Compute mC(M,N) = alpha &#42 mA(M,K) &#42 mB(K,N) + beta &#42 mC(M,N).
	 *
	 * @param mA     the first matrix in multiplication
	 * @param mB     the second matrix in multiplication
	 * @param mC     the output matrix
     * @param dim    dimension array [M, N, K], which are rows of mA, columns of mB, and columns of mC.
	 * @param scale  scalars [alpha, beta] applying on the first matrix and output matrix
	 * @param trans  integer array for transpose flags on the first and second matrix. 0 for non-transpose.
	 *
	 */
	@Native("java","WrapBLAS.matmatMult((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getDoubleArray(),(#4).raw().getIntArray(),(#5).raw().getDoubleArray(),(#6).raw().getIntArray())")
	@Native("c++","matrix_matrix_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),(#5)->raw()->raw(),(#6)->raw()->raw())")
		public static native def matrix_matrix_mult(
				mA:Array[Double](1), 
				mB:Array[Double](1), 
				mC:Array[Double](1),
				dim:Array[Int](1), 
				scale:Array[Double](1),
				trans:Array[Int](1)):void;

	//========================================================================

	/**
	 * Compute mB =  alpha &#42 op( mA ) &#42 mB + beta &#42 mB, where mA is lower symmetric matrix
	 *
	 * @param mA     the first symmetric matrix in multiplication
	 * @param mB     the second triangular matrix in multiplication
	 * @param mC     the output matrix
     * @param dim    dimension array [M, N], rows and columns of mC
	 * @param scale  scalars [alpha, beta] applying on the first matrix and output matrix
	 *
	 */
	// C = alpah* op(A) * op(B) + beta*C, A is symmetrix of lower triangular matrix
	@Native("java","WrapBLAS.symmatMult((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getDoubleArray(),(#4).raw().getIntArray(),(#5).raw().getDoubleArray())")
	@Native("c++","sym_matrix_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),(#5)->raw()->raw())")
		public static native def sym_matrix_mult(
				mA:Array[Double](1), 
				mB:Array[Double](1), 
				mC:Array[Double](1),
				dim:Array[Int](1), 
				scale:Array[Double](1)):void;


	// C = alpah* op(A) * op(B) + beta*C, A is symmetrix of lower triangular matrix
	@Native("java","WrapBLAS.matsymMult((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getDoubleArray(),(#4).raw().getIntArray(),(#5).raw().getDoubleArray())")
	@Native("c++","matrix_sym_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),(#5)->raw()->raw())")
		public static native def matrix_sym_mult(
				mB:Array[Double](1), 
				mA:Array[Double](1), 
				mC:Array[Double](1),
				dim:Array[Int](1), 
				scale:Array[Double](1)):void;

	//================================================================================
	/**
	 * Compute mB =  op( mA ) &#42 mB, mA is lower-non-unit triangular matrix.
	 *
	 * @param mA     Double precision array storing triangular matrix mA.
	 * @param mB     Double precision array storing matrix mB, which also is the output.
	 * @param dim    dimension array [M, N], which are rows of mB and columns of mB.
	 * @param tranA  transpose option for mA
	 *
	 */
	@Native("java","WrapBLAS.trimatMult((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getIntArray(),#4)")
	@Native("c++","tri_matrix_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),#4)")
		public static native def tri_matrix_mult(
				mA:Array[Double](1), 
				mB:Array[Double](1), 
				dim:Array[Int](1), 
				tranA:Int):void;

	// A =  A * op( B ), B is lower-non-unit triangular
	/**
	 * Compute mB =  mA &#42 op( mB ), mB is lower-non-unit triangular matrix.
	 *
	 * @param mA     Double precision array storing matrix mA and output matrix.
	 * @param mB     Double precision array storing triangular matrix mB.
	 * @param dim    dimension array [M, N], which are rows of mB and columns of mB. 
	 * @param tranB  transpose option for matrix mB
	 *
	 */
	@Native("java","WrapBLAS.mattriMult((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getIntArray(),#4)")
	@Native("c++","matrix_tri_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),#4)")
		public static native def matrix_tri_mult(mA:Array[Double](1), 
												 mB:Array[Double](1),
												 dim:Array[Int](1), 
												 tranB:Int):void;

	
	//------------------------------------------------------------------------
	// Level Two
	//------------------------------------------------------------------------

    //------------------------------------------------------------------------
	//y = A*x 
	//y = alpah * op(A)*x + beta * y
	/**
	 * Compute y = alpha &#42 mA &#42 y + beta &#42 y, matrix-vector multiplication.
	 *
	 * @param mA     the first matrix (right-side)
	 * @param x      left-side operand vector
	 * @param y      output vector
	 * @param dim    dimension array [M, N], which are rows and columns of mA
	 * @param scale  scalars [alpha, beta]
	 * @param transA transpose flag for matrix mA
	 */
	@Native("java","WrapBLAS.matvecMult((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getDoubleArray(),(#4).raw().getIntArray(),(#5).raw().getDoubleArray(),#6)")
	@Native("c++","matrix_vector_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),(#5)->raw()->raw(),#6)")
		public static native def matrix_vector_mult(
				mA:Array[Double](1), 
				x:Array[Double](1), 
				y:Array[Double](1),
				dim:Array[Int](1), 
				scale:Array[Double](1), 
				transA:Int):void;

	////y = alpah* A *x + beta * y, A is symmetrix matrix of lower triangular part
	/**
	 * Compute  y = alpha &#42 mA &#42 y + beta &#42 y, where mA is symmetric matrix. Incremental step is 1.
	 *
	 * @param mA     the symmetric matrix (right-side)
	 * @param x      left-side operand vector
	 * @param y      output vector
	 * @param dim    dimension array [M, N], which are rows and columns of mA
	 * @param scale  scalars [alpha, beta]
	 *
	 */
	@Native("java","WrapBLAS.symvecMult((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getDoubleArray(),(#4).raw().getIntArray(),(#5).raw().getDoubleArray())")
	@Native("c++","sym_vector_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),(#5)->raw()->raw())")
		public static native def sym_vector_mult(
				mA:Array[Double](1), 
				x:Array[Double](1),
				y:Array[Double](1),
				dim:Array[Int](1), 
				scale:Array[Double](1)):void;
	//------------------------------------------------------------------------
	/**
	 * Triangular-vector multply:  op(mA) &#42 x = b, where mA is unit lower-non-diagonal matrix.
	 * 
	 * @param mA     the lower-non-diagonal matrix
	 * @param bx     right-hand side vector as input, and output
	 * @param N      leading dimension of mA 
	 * @param tA     transpose option for mA
	 * 
	 */	
	@Native("java","WrapBLAS.trivecMult((#1).raw().getDoubleArray(),#2,(#3).raw().getDoubleArray(),#4,#5)")
	@Native("c++","tri_vector_mult((#1)->raw()->raw(),#2,(#3)->raw()->raw(),#4,#5)")
	public static native def tri_vector_mult(
			mA:Array[Double](1), 
			uplo:Int,
			bx:Array[Double](1), 
			lda:Int, tA:Int):void;


	//------------------------------------------------------------------------
	//------------------------------------------------------------------------
	/**
	 * Solve equation  mA &#42 x = b, where mA is unit lower-triangular matrix.
	 *
	 * @param mA     the unit lower-triangular matrix
	 * @param bx     right-hand side vector as input, and output
	 * @param dim    leading dimension and order of mA 
	 * @param transA transpose option for mA 
	 *
	 */	
	@Native("java","WrapBLAS.trivecSolve((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getIntArray(),#4)")
	@Native("c++","tri_vector_solve((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),#4)")
		public static native def tri_vector_solve(
				mA:Array[Double](1), 
				bx:Array[Double](1), 
				dim:Array[Int](1), transA:Int):void;
	
    //------------------------------------------------------------------------
	/**
	 * Solve matrix equation  op(mA) &#42 X = B, where mA is unit lower-triangular matrix.
	 * 
	 * @param mA     the unit lower-triangular matrix
	 * @param BX     right-hand side matrix as input, and output
	 * @param dim    leading dimension of mA and leading dimension of B 
	 * @param transA transpose option for mA 
	 * 
	 */	
	@Native("java","WrapBLAS.trimatSolve((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getIntArray(),#4)")
	@Native("c++","tri_matrix_solve((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),#4)")
	public static native def tri_matrix_solve(
			mA:Array[Double](1), 
			BX:Array[Double](1), 
			dim:Array[Int](1), transA:Int):void;

	/**
	 * Solve matrix equation  X &#42 op(mA) = B, where mA is unit lower-triangular matrix.
	 * 
	 * @param BX     left-hand side matrix as input, and output
	 * @param mA     the unit lower-triangular matrix
	 * @param dim    leading dimension of mA and leading dimension of B 
	 * @param transA transpose option for mA 
	 * 
	 */	
	@Native("java","WrapBLAS.mattriSolve((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getIntArray(),#4)")
	@Native("c++","matrix_tri_solve((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),#4)")
	public static native def matrix_tri_solve(
			BX:Array[Double](1), 
			mA:Array[Double](1), 
			dim:Array[Int](1), transA:Int):void;

   /*
	//y=A*b
	public static def doMultiply(A:DenseMatrix, b:Vector):Vector{
		val M = A.M;
		val n = b.N;
		// Perform matrix operation
		val nv = new Array[Double](M);
		matrix_vector_mult(A.d, b.d, nv, M, n);
		return new Vector(nv);
	}
	//y=alpah* A*b+ beta*y
	public static def doMultiply(A:DenseMatrix, b:Vector, y:Vector):void {
		val M = A.M;
		val n = b.N;
		// Perform matrix operation
		matrix_vector_mult(A.d, b.d, y.d, [M, n], [1.0, 1.0], 0);
	}

	//y=A^t * b
	public static def doTransMultiply(A:DenseMatrix, b:Vector):Vector {
		val M = A.M;
		val n = b.N;
		val nv = new Array[Double](M);
		// Perform matrix operation
		matrix_vector_mult(A.d, b.d, nv, [M, n], [1.0, 0.0], 1);
		return new Vector(nv);
	}
	//y=A^t * b+y
	public static def doTransMultiply(A:DenseMatrix, b:Vector, y:Vector):void {
		val M = A.M;
		val n = b.N;
		// Perform matrix operation
		matrix_vector_mult(A.d, b.d, y.d, [M, n], [1.0, 1.0], 1);
	}
    //------------------------------------------------------------------------
	//y=A^t * b+y
	public static def doSymMultiply(A:DenseMatrix, b:Vector, y:Vector):void {
		val M = A.M;
		val n = b.N;
		// Perform matrix operation
		sym_vector_mult(A.d, b.d, y.d, [M, n], [1.0, 1.0]);
	}


    //------------------------------------------------------------------------
	//Solve b=A * x
	public static def doSolve(A:DenseMatrix, x:Vector):void {
		val M = A.M;
		val n = x.N;

		// Perform matrix operation
		tri_matrix_solve(A.d, x.d, M, n);
	}
	 * */
}