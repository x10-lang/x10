/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2010.
 */

package blas;

import x10.io.Console;
import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;


@NativeCPPInclude("wrap_blas.h")
@NativeCPPCompilationUnit("wrap_blas.c")

/**
   This class provides native interface handling some of BLAS functions.
   Only Double type data is supported in our current version

   <p>
   <p>
 */
public class MatrixBlas {

	// 
    //------------------------------------------------------------------------
	// Level One 
    //------------------------------------------------------------------------

	// for	x = a*x
	// Reciprocal Scale: x <-x/alpha
	// void cblas_dscal(blasint N, double alpha, double *X, blasint incX);
    @Native("c++","scale(#1, #2, (#3)->raw()->raw())")
		public static native def scale(N:Int, alpa:Double, x:Array[Double](1)):Void;

	// Vector Copy: y <-x
	// void cblas_dcopy(blasint n, double *x, blasint incx, double *y, blasint incy);
	@Native("c++","copy(#1, (#2)->raw()->raw(), (#3)->raw()->raw())")
		public static native def copy(N:int, X:Array[Double](1), Y:Array[Double](1)):Void;

	// for	dot product
    // DOT Production: r <- beta * r + alpha * x^T * y
	// double cblas_ddot(blasint n, double *x, blasint incx, double *y, blasint incy);
    @Native("c++","dot_prod(#1,(#2)->raw()->raw(),(#3)->raw()->raw())")
		public static native def dot_prod(n:Int, X:Array[Double](1),Y:Array[Double](1)):Double;	
	
	// for	Euclidean norm
	// double cblas_dnrm2 (blasint N, double *X, blasint incX);
    @Native("c++","norm2(#1,(#2)->raw()->raw())")
		public static native def norm(n:Int, X:Array[Double]):Double;

	// SUM: for	sum of absolute values
	// double cblas_dasum (blasint n, double *x, blasint incx);
	@Native("c++","abs_sum(#1,(#2)->raw()->raw())")
		public static native def abs_sum(n:Int, X:Array[Double](1)):Double;

   
    //------------------------------------------------------------------------
	// Level Two
    //------------------------------------------------------------------------


    //------------------------------------------------------------------------
	//Simplified matrix-matrix/vector function interface
    //------------------------------------------------------------------------
	@Native("c++","c_mat_mat_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),#4,#5,#6)")
		public static native def c_mat_mat_mult(mA:Array[Double](1), mB:Array[Double](1), 
												mC:Array[Double](1),
												M:Int, N:Int, K:Int):Void;
	// C = alpah * op(A) * op(B) + beta*C
	@Native("c++","matrixT_matrixT_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),(#5)->raw()->raw(),(#6)->raw()->raw())")
		public static native def matrixT_matrixT_mult(mA:Array[Double](1), mB:Array[Double](1), 
													  mC:Array[Double](1),
													  dim:Array[Int](1), scale:Array[Double](1),
													  trans:Array[Int](1)):Void;

	// C = alpah* op(A) * op(B) + beta*C, A is symmetrix of lower triangular matrix
	@Native("c++","sym_matrix_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),(#5)->raw()->raw())")
		public static native def sym_matrix_mult(mA:Array[Double](1), mB:Array[Double](1), 
												 mC:Array[Double](1),
												 dim:Array[Int](1), scale:Array[Double](1)):Void;

	// B =  op( A ) * B, A is lower-non-unit triangular
	@Native("c++","triT_matrix_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),#4)")
		public static native def triT_matrix_mult(mA:Array[Double](1), mB:Array[Double](1), 
												  dim:Array[Int](1), tranA:Int):Void;
	// A =  A * op( B ), B is lower-non-unit triangular
	@Native("c++","matrix_triT_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),#4)")
		public static native def matrix_triT_mult(mA:Array[Double](1), mB:Array[Double](1),
												  dim:Array[Int](1), tranB:Int):Void;

    //------------------------------------------------------------------------
	//y = A*x 
	@Native("c++","matrix_vector_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),#4,#5)")
		public static native def matrix_vector_mult(mA:Array[Double](1), x:Array[Double](1), 
													y:Array[Double](1),
													M:Int, N:Int):Void;
	//y = alpah * op(A)*x + beta * y
	@Native("c++","matrixT_vector_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),(#5)->raw()->raw(),#6)")
		public static native def matrixT_vector_mult(mA:Array[Double](1), x:Array[Double](1), y:Array[Double](1),
													dim:Array[Int](1), scale:Array[Double](1), transA:Int):Void;
	////y = alpah* A *x + beta * y, A is symmetrix matrix of lower triangular part
	@Native("c++","sym_vector_mult((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),(#5)->raw()->raw())")
		public static native def sym_vector_mult(mA:Array[Double](1), x:Array[Double](1), y:Array[Double](1),
												 dim:Array[Int](1), scale:Array[Double](1)):Void;

    //------------------------------------------------------------------------
	
	@Native("c++","tri_matrix_solve((#1)->raw()->raw(),(#2)->raw()->raw(),#3,#4)")
		public static native def tri_matrix_solve(mA:Array[Double](1), bx:Array[Double](1), M:Int, N:Int):Void;

    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
}