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
 *  Only Double based matrix data is supported in the current version.
 *  All matrix data is stored in column-major format. 
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
	// Level One 

	// for	x = a*x
	// Reciprocal Scale: x <-x/alpha
	// void cblas_dscal(blasint N, double alpha, double *X, blasint incX);

	/**
	 * x *= alpha. Raise number of data stored continuously in Rail X 
	 * by a factor of alpha. The incremental step is 1.
	 *
	 * @param N        number of data to operate on
	 * @param alpha    scalar
	 * @param x        data array
	 */
	@Native("java", "WrapBLAS.scale(#1, #2, (#3).getDoubleArray())")
    @Native("c++","scale(#1, #2, (#3)->raw)")
		public static native def scale(
				N:Long, 
				alpha:Double, 
				x:Rail[Double]):void;

	// Vector Copy: y <-x
	// void cblas_dcopy(blasint n, double *x, blasint incx, double *y, blasint incy);
	/**
	 * Copy N data from X to Y, and the incremental step is 1.
	 *
	 * @param N        number of data to copy
	 * @param X        source array
	 * @param Y        destination array
	 */
	@Native("java", "WrapBLAS.copy(#1, (#2).getDoubleArray(), (#3).getDoubleArray())")
	@Native("c++", "copy(#1, (#2)->raw, (#3)->raw)")
		public static native def copy(
				N:Long, 
				X:Rail[Double], 
				Y:Rail[Double]):void;

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
    @Native("java","WrapBLAS.dotProd(#1,(#2).getDoubleArray(),(#3).getDoubleArray())")
    @Native("c++","dot_prod(#1,(#2)->raw,(#3)->raw)")
		public static native def dot_prod(
				n:Long, 
				X:Rail[Double],
				Y:Rail[Double]):Double;	
	
	// for	Euclidean norm
	// double cblas_dnrm2 (blasint N, double *X, blasint incX);
	/**
	 * Perform Euclidean norm. Incremental step is 1.
	 *
	 * @param  n      number of data to operate in array
	 * @param  X      data array.
	 * @return        Eculidean norm
	 */
    @Native("java","WrapBLAS.norm2(#1,(#2).getDoubleArray())")
    @Native("c++","norm2(#1,(#2)->raw)")
		public static native def norm(
				n:Long, 
				X:Rail[Double]):Double;

	// SUM: for	sum of absolute values
	// double cblas_dasum (blasint n, double *x, blasint incx);
	/**
	 * Sum of absolute values of array X for n number of data.
	 * @param  n      number of data to operate in array
	 * @param  X      data array.
	 * @return        absolute sum
	 */
	@Native("java","WrapBLAS.absSum(#1,(#2).getDoubleArray())")
	@Native("c++","abs_sum(#1,(#2)->raw)")
		public static native def abs_sum(
				n:Long, 
				X:Rail[Double]):Double;

	// Level Three

	//Simplified matrix-matrix/vector function interface
	
	/**
	 * Call the matrix multiplication implemented in C.  The source code of
	 * this implementation can be found in wrap_blas.c.  This method is for
	 * debugging and performance test purpose only. 
	 */
	@Native("c++","c_mat_mat_mult((#1)->raw,(#2)->raw,(#3)->raw,#4,#5,#6)")
		public static native def c_mat_mat_mult(
				mA:Rail[Double], 
				mB:Rail[Double], 
				mC:Rail[Double],
				M:Long, N:Long, K:Long):void;

	// C = alpah * op(A) * op(B) + beta*C
	/**
	 * Compute mC(M,N) = alpha &#42 mA(M,K) &#42 mB(K,N) + beta &#42 mC(M,N).
	 *
	 * @param mA     the first matrix in multiplication
	 * @param mB     the second matrix in multiplication
	 * @param mC     the output matrix
     * @param dim    dimension array [M, N, K], which are rows of mA, columns of mB, and columns of mC.
     * @param ld    leading dimension array [LDA, LDB, LDC], which are leading dimensions of A, B, C.
	 * @param scale  scalars [alpha, beta] applying on the first matrix and output matrix
	 * @param trans  integer array for transpose flags on the first and second matrix. 0 for non-transpose.
	 *
	 */
	@Native("java","WrapBLAS.matmatMultLd((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4).getLongArray(),(#5).getLongArray(),(#6).getDoubleArray(),(#7).getIntArray())")
	@Native("c++","matrix_matrix_mult((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw,(#5)->raw,(#6)->raw,(#7)->raw)")
		public static native def matrix_matrix_mult(
				mA:Rail[Double], 
				mB:Rail[Double], 
				mC:Rail[Double],
				dim:Rail[Long], 
				ld:Rail[Long], 
				scale:Rail[Double],
				trans:Rail[Int]):void;

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
	@Native("java","WrapBLAS.matmatMult((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4).getLongArray(),(#5).getDoubleArray(),(#6).getIntArray())")
	@Native("c++","matrix_matrix_mult((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw,(#5)->raw,(#6)->raw)")
		public static native def matrix_matrix_mult(
				mA:Rail[Double], 
				mB:Rail[Double], 
				mC:Rail[Double],
				dim:Rail[Long], 
				scale:Rail[Double],
				trans:Rail[Int]):void;


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
	@Native("java","WrapBLAS.symmatMult((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4).getLongArray(),(#5).getDoubleArray())")
	@Native("c++","sym_matrix_mult((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw,(#5)->raw)")
		public static native def sym_matrix_mult(
				mA:Rail[Double], 
				mB:Rail[Double], 
				mC:Rail[Double],
				dim:Rail[Long], 
				scale:Rail[Double]):void;


	// C = alpah* op(A) * op(B) + beta*C, A is symmetrix of lower triangular matrix
	@Native("java","WrapBLAS.matsymMult((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4).getLongArray(),(#5).getDoubleArray())")
	@Native("c++","matrix_sym_mult((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw,(#5)->raw)")
		public static native def matrix_sym_mult(
				mB:Rail[Double], 
				mA:Rail[Double], 
				mC:Rail[Double],
				dim:Rail[Long], 
				scale:Rail[Double]):void;


	/**
	 * Compute mB =  op( mA ) &#42 mB, mA is non-unit triangular matrix.
	 *
	 * @param mA     Double precision array storing triangular matrix mA.
	 * @param mB     Double precision array storing matrix mB, which also is the output.
	 * @param dim    dimension array [M, N], which are rows of mB and columns of mB. Lower/upper triangular flag sets the last value.
	 * @param tranA  transpose option for mA
	 *
	 */
	@Native("java","WrapBLAS.trimatMult((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getLongArray(),#4)")
	@Native("c++","tri_matrix_mult((#1)->raw,(#2)->raw,(#3)->raw,#4)")
		public static native def tri_matrix_mult(
				mA:Rail[Double], 
				mB:Rail[Double], 
				dim:Rail[Long], 
				tranA:Int):void;

	// A =  A * op( B ), B is lower-non-unit triangular
	/**
	 * Compute mB =  mA &#42 op( mB ), mB is non-unit triangular matrix.
	 *
	 * @param mA     Double precision array storing matrix mA and output matrix.
	 * @param mB     Double precision array storing triangular matrix mB.
	 * @param dim    dimension array [M, N], which are rows of mB and columns of mB. 
	 * @param tranB  transpose option for matrix mB
	 *
	 */
	@Native("java","WrapBLAS.mattriMult((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getLongArray(),#4)")
	@Native("c++","matrix_tri_mult((#1)->raw,(#2)->raw,(#3)->raw,#4)")
		public static native def matrix_tri_mult(mA:Rail[Double], 
												 mB:Rail[Double],
												 dim:Rail[Long], 
												 tranB:Int):void;


	// Level Two

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
	@Native("java","WrapBLAS.matvecMult((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4).getLongArray(),(#5).getDoubleArray(),#6)")
	@Native("c++","matrix_vector_mult((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw,(#5)->raw,#6)")
		public static native def matrix_vector_mult(
				mA:Rail[Double], 
				x:Rail[Double], 
				y:Rail[Double],
				dim:Rail[Long], 
				scale:Rail[Double], 
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
	@Native("java","WrapBLAS.symvecMult((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4).getLongArray(),(#5).getDoubleArray())")
	@Native("c++","sym_vector_mult((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw,(#5)->raw)")
		public static native def sym_vector_mult(
				mA:Rail[Double], 
				x:Rail[Double],
				y:Rail[Double],
				dim:Rail[Long], 
				scale:Rail[Double]):void;

	/**
	 * Triangular-vector multply:  op(mA) &#42 x = b, where mA is unit lower-non-diagonal matrix.
	 * 
	 * @param mA     the lower-non-diagonal matrix
	 * @param bx     right-hand side vector as input, and output
	 * @param N      leading dimension of mA 
	 * @param tA     transpose option for mA
	 * 
	 */	
	@Native("java","WrapBLAS.trivecMult((#1).getDoubleArray(),#2,(#3).getDoubleArray(),#4,#5)")
	@Native("c++","tri_vector_mult((#1)->raw,#2,(#3)->raw,#4,#5)")
	public static native def tri_vector_mult(
			mA:Rail[Double], 
			uplo:Int,
			bx:Rail[Double], 
			lda:Long, tA:Int):void;


	//A := alpha*x*y**T + A,
	/**
	 * Compute A = alpha &#42 x &#42 y &#42 &#42 T + A, general rank-1 update.
	 *
	 * @param mA     the first matrix (right-side)
	 * @param x      left-side operand vector
	 * @param y      output vector
	 * @param dim    dimension array [M, N], which are rows and columns of mA
	 * @param offset starting offsets [offsetX, offsetY] for elements of X and Y
	 * @param inc    increments [incX, incY] for elements of X and Y
     * @param lda    leading dimension of A
	 * @param alpha  scalar alpha
	 */
	@Native("java","WrapBLAS.rankOneUpdate((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4).getLongArray(),(#5).getLongArray(),(#6).getLongArray(),#7,#8)")
	@Native("c++","rank_one_update((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw,(#5)->raw,(#6)->raw,#7,#8)")
		public static native def rank_one_update(
				mA:Rail[Double],
				x:Rail[Double],
				y:Rail[Double],
				dim:Rail[Long],
                offset:Rail[Long],
                inc:Rail[Long],
				lda:Long,
				alpha:Double):void;



	/**
	 * Solve equation  mA &#42 x = b, where mA is unit lower-triangular matrix.
	 *
	 * @param mA     the unit lower-triangular matrix
	 * @param bx     right-hand side vector as input, and output
	 * @param dim    leading dimension and order of mA 
	 * @param transA transpose option for mA 
	 *
	 */	
	@Native("java","WrapBLAS.trivecSolve((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getLongArray(),#4)")
	@Native("c++","tri_vector_solve((#1)->raw,(#2)->raw,(#3)->raw,#4)")
		public static native def tri_vector_solve(
				mA:Rail[Double], 
				bx:Rail[Double], 
				dim:Rail[Long], transA:Int):void;
	

	/**
	 * Solve matrix equation  op(mA) &#42 X = B, where mA is unit lower-triangular matrix.
	 * 
	 * @param mA     the unit lower-triangular matrix
	 * @param BX     right-hand side matrix as input, and output
	 * @param dim    leading dimension of mA and leading dimension of B 
	 * @param transA transpose option for mA 
	 * 
	 */	
	@Native("java","WrapBLAS.trimatSolve((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getLongArray(),#4)")
	@Native("c++","tri_matrix_solve((#1)->raw,(#2)->raw,(#3)->raw,#4)")
	public static native def tri_matrix_solve(
			mA:Rail[Double], 
			BX:Rail[Double], 
			dim:Rail[Long], transA:Int):void;

	/**
	 * Solve matrix equation  X &#42 op(mA) = B, where mA is unit lower-triangular matrix.
	 * 
	 * @param BX     left-hand side matrix as input, and output
	 * @param mA     the unit lower-triangular matrix
	 * @param dim    leading dimension of mA and leading dimension of B 
	 * @param transA transpose option for mA 
	 * 
	 */	
	@Native("java","WrapBLAS.mattriSolve((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getLongArray(),#4)")
	@Native("c++","matrix_tri_solve((#1)->raw,(#2)->raw,(#3)->raw,#4)")
	public static native def matrix_tri_solve(
			BX:Rail[Double], 
			mA:Rail[Double], 
			dim:Rail[Long], transA:Int):void;

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

	//y=A^t * b+y
	public static def doSymMultiply(A:DenseMatrix, b:Vector, y:Vector):void {
		val M = A.M;
		val n = b.N;
		// Perform matrix operation
		sym_vector_mult(A.d, b.d, y.d, [M, n], [1.0, 1.0]);
	}



	//Solve b=A * x
	public static def doSolve(A:DenseMatrix, x:Vector):void {
		val M = A.M;
		val n = x.N;

		// Perform matrix operation
		tri_matrix_solve(A.d, x.d, M, n);
	}
	 * */
}
