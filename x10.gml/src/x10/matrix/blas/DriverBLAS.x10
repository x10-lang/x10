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

package x10.matrix.blas;

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;
   

/**
 * This class provides a wrapper around the BLAS routines via native calls.
 * All matrices/vectors use double precision and column-major format.
 * @see http://www.netlib.org/blas/
 *  
 * <p> 
 * NOTE: This class is package protected so that calls of BLAS routines
 * must be made through BLAS or DenseMultBLAS.  This is a workaround for
 * Managed X10 when inlining Java methods from WrapBLAS.java in packages other
 * than x10.matrix.blas, which causes the compiler to complain that WrapBLAS
 * cannot be found.
 */
@NativeCPPInclude("wrap_blas.h")
@NativeCPPCompilationUnit("wrap_blas.cc")
protected class DriverBLAS {
    // Level One 

    /**
     * Compute x = alpha &#42 x.
     *
     * @param N        number of elements to operate on
     * @param alpha    scalar
     * @param x        data array
     */
    @Native("java", "WrapBLAS.scale(#1, #2, (#3).getDoubleArray())")
    @Native("c++","scale(#1, #2, (#3)->raw)")
    public static native def scale(
            N:Long, 
            alpha:Double, 
            x:Rail[Double]):void;

    /**
     * Copy N contiguous elements from X to Y.
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

    /**
     * Return dot product of vectors X and Y. 
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

    /**
     * Compute mC(M,N) = alpha &#42 mA(M,K) &#42 mB(K,N) + beta &#42 mC(M,N).
     *
     * @param alpha  a scalar by which mA is premultiplied
     * @param mA     the first matrix in multiplication
     * @param mB     the second matrix in multiplication
     * @param beta   a scalar by which to scale vector y and add to result.
     *               if beta is 0, y need not be initialized on input
     * @param mC     the output matrix
     * @param dim    dimension array [M, N, K], which are rows of mA, columns of mB, and columns of mC.
     * @param ld     leading dimension array [LDA, LDB, LDC], which are leading dimensions of A, B, C.
     * @param offset row and column offsets [Ar, Ac, Br, Bc, Cr, Cc] into matrices
     * @param trans  integer array for transpose flags on the first and second matrix. 0 for non-transpose.
     */
    @Native("java","WrapBLAS.matmatMultOff((#1),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4),(#5).getDoubleArray(),(#6).getLongArray(),(#7).getLongArray(),(#8).getLongArray(),(#9).getIntArray())")
    @Native("c++","matrix_matrix_mult((#1),(#2)->raw,(#3)->raw,(#4),(#5)->raw,(#6)->raw,(#7)->raw,(#8)->raw,(#9)->raw)")
    public static native def matrix_matrix_mult(
            alpha:Double, mA:Rail[Double], 
                          mB:Rail[Double],
            beta:Double,  mC:Rail[Double],
            dim:Rail[Long], 
            ld:Rail[Long], 
            offset:Rail[Long],
            trans:Rail[Int]):void;

    /**
     * Compute mC(M,N) = alpha &#42 mA(M,K) &#42 mB(K,N) + beta &#42 mC(M,N).
     *
     * @param alpha  a scalar by which mA is premultiplied
     * @param mA     the first matrix in multiplication
     * @param mB     the second matrix in multiplication
     * @param beta   a scalar by which to scale matrix C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param mC     the output matrix
     * @param dim    dimension array [M, N, K], which are rows of mA, columns of mB, and columns of mC.
     * @param ld     leading dimension array [LDA, LDB, LDC], which are leading dimensions of A, B, C.
     * @param trans  integer array for transpose flags on the first and second matrix. 0 for non-transpose.
     */
    @Native("java","WrapBLAS.matmatMult((#1),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4),(#5).getDoubleArray(),(#6).getLongArray(),(#7).getLongArray(),(#8).getIntArray())")
    @Native("c++","matrix_matrix_mult((#1),(#2)->raw,(#3)->raw,(#4),(#5)->raw,(#6)->raw,(#7)->raw,(#8)->raw)")
    public static native def matrix_matrix_mult(
            alpha:Double, mA:Rail[Double], 
                          mB:Rail[Double],
            beta:Double,  mC:Rail[Double],
            dim:Rail[Long], 
            ld:Rail[Long], 
            trans:Rail[Int]):void;

    /**
     * Compute mC(N,N) = alpha &#42 mA(N,K) &#42 mA(K,N)' + beta &#42 mC(N,N).
     *
     * @param alpha  a scalar by which mA is premultiplied
     * @param mA     the first matrix in multiplication
     * @param beta   a scalar by which to scale matrix C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param mC     the output matrix
     * @param dim    dimension array [N, K], which are rows of mA and columns of mA.
     * @param offset row and column offsets [Ar, Ac, Br, Bc, Cr, Cc] into matrices
     * @param upper  if true, update upper half of mC; otherwise update lower half
     * @param trans  whether to transpose mA
     */
    @Native("java","WrapBLAS.symRankKUpdateOff((#1),(#2).getDoubleArray(),(#3),(#4).getDoubleArray(),(#5).getLongArray(),(#6).getLongArray(),(#7).getLongArray(),(#8),(#9))")
    @Native("c++","sym_rank_k_update((#1),(#2)->raw,(#3),(#4)->raw,(#5)->raw,(#6)->raw,(#7)->raw,(#8),(#9))")
    public static native def sym_rank_k_update(
            alpha:Double, mA:Rail[Double], 
            beta:Double,  mC:Rail[Double],
            dim:Rail[Long],
            ld:Rail[Long],
            offset:Rail[Long],
            upper:Boolean,
            trans:Boolean
    ):void;

    /**
     * Compute mC(N,N) = alpha &#42 mA(N,K) &#42 mA(K,N)' + beta &#42 mC(N,N).
     *
     * @param alpha  a scalar by which mA is premultiplied
     * @param mA     the first matrix in multiplication
     * @param beta   a scalar by which to scale matrix C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param mC     the output matrix
     * @param dim    dimension array [N, K], which are rows of mA and columns of mA.
     * @param upper  if true, update upper half of mC; otherwise update lower half
     * @param trans  whether to transpose mA
     */
    @Native("java","WrapBLAS.symRankKUpdate((#1),(#2).getDoubleArray(),(#3),(#4).getDoubleArray(),(#5).getLongArray(),(#6),(#7))")
    @Native("c++","sym_rank_k_update((#1),(#2)->raw,(#3),(#4)->raw,(#5)->raw,(#6),(#7))")
    public static native def sym_rank_k_update(
            alpha:Double, mA:Rail[Double], 
            beta:Double,  mC:Rail[Double],
            dim:Rail[Long], 
            upper:Boolean,
            trans:Boolean
    ):void;

    /**
     * Compute mC =  alpha*mB &#42 mA + beta*mC, 
     * where mA is lower triangle of symmetric matrix.
     *
     * @param alpha  a scalar by which mB is premultiplied
     * @param mA     the first symmetric matrix in multiplication
     * @param mB     the second triangular matrix in multiplication
     * @param beta   a scalar by which to scale mC and add to result.
     *               if beta is 0, mC need not be initialized on input
     * @param mC     the output matrix
     * @param dim    dimension array [M, N], rows and columns of mC
     */
    @Native("java","WrapBLAS.symmatMult((#1),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4),(#5).getDoubleArray(),(#6).getLongArray())")
    @Native("c++","sym_matrix_mult((#1),(#2)->raw,(#3)->raw,(#4),(#5)->raw,(#6)->raw)")
    public static native def sym_matrix_mult(
            alpha:Double, mA:Rail[Double], 
                          mB:Rail[Double], 
            beta:Double,  mC:Rail[Double],
            dim:Rail[Long]):void;

    /**
     * Compute mC = alpha*mA &#42 mB + beta*mC, 
     * where mA is lower triangle of symmetric matrix.
     */
    @Native("java","WrapBLAS.matsymMult((#1).getDoubleArray(),(#2),(#3).getDoubleArray(),(#4),(#5).getDoubleArray(),(#6).getLongArray())")
    @Native("c++","matrix_sym_mult((#1)->raw,(#2),(#3)->raw,(#4),(#5)->raw,(#6)->raw)")
    public static native def matrix_sym_mult(
                          mB:Rail[Double], 
            alpha:Double, mA:Rail[Double], 
            beta:Double,  mC:Rail[Double],
            dim:Rail[Long]):void;

    /**
     * Compute mB =  op( mA ) &#42 mB, mA is non-unit triangular matrix.
     *
     * @param mA     Double precision array storing triangular matrix mA.
     * @param mB     Double precision array storing matrix mB, which also is the output.
     * @param dim    dimension array [M, N], which are rows of mB and columns of mB. Lower/upper triangular flag sets the last value.
     * @param tranA  transpose option for mA
     */
    @Native("java","WrapBLAS.trimatMult((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getLongArray(),#4)")
    @Native("c++","tri_matrix_mult((#1)->raw,(#2)->raw,(#3)->raw,#4)")
    public static native def tri_matrix_mult(
            mA:Rail[Double], 
            mB:Rail[Double], 
            dim:Rail[Long], 
            tranA:Int):void;

    /**
     * Compute mB =  mA &#42 op( mB ), mB is non-unit triangular matrix.
     *
     * @param mA     Double precision array storing matrix mA and output matrix.
     * @param mB     Double precision array storing triangular matrix mB.
     * @param dim    dimension array [M, N], which are rows of mB and columns of mB. 
     * @param tranB  transpose option for matrix mB
     */
    @Native("java","WrapBLAS.mattriMult((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getLongArray(),#4)")
    @Native("c++","matrix_tri_mult((#1)->raw,(#2)->raw,(#3)->raw,#4)")
    public static native def matrix_tri_mult(mA:Rail[Double], 
                                             mB:Rail[Double],
                                             dim:Rail[Long], 
                                             tranB:Int):void;


    // Level Two

    /**
     * Compute y = alpha &#42 mA &#42 y + beta &#42 y, matrix-vector multiplication.
     *
     * @param alpha  a scalar by which mA is premultiplied
     * @param mA     the first matrix (right-side)
     * @param x      left-side operand vector
     * @param beta   a scalar by which to scale vector y and add to result.
     *               if beta is 0, y need not be initialized on input
     * @param y      output vector
     * @param dim    dimension array [M, N], which are rows and columns of mA
     * @param lda    leading dimension of mA
     * @param offset row and column offsets into mA and offsets into vectors [Ar, Ac, xr, yr] 
     * @param transA transpose flag for matrix mA
     */
    @Native("java","WrapBLAS.matvecMultOff((#1),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4),(#5).getDoubleArray(),(#6).getLongArray(),#7,(#8).getLongArray(),#9)")
    @Native("c++","matrix_vector_mult((#1),(#2)->raw,(#3)->raw,(#4),(#5)->raw,(#6)->raw,#7,(#8)->raw,#9)")
    public static native def matrix_vector_mult(
            alpha:Double, mA:Rail[Double], 
                          x:Rail[Double], 
            beta:Double,  y:Rail[Double],
            dim:Rail[Long], 
            lda:Long, 
            offset:Rail[Long],
            transA:Int):void;

    /**
     * Compute y = alpha &#42 mA &#42 y + beta &#42 y, matrix-vector multiplication.
     *
     * @param alpha  a scalar by which mA is premultiplied
     * @param mA     the first matrix (right-side)
     * @param x      left-side operand vector
     * @param beta   a scalar by which to scale vector y and add to result.
     *               if beta is 0, y need not be initialized on input
     * @param y      output vector
     * @param dim    dimension array [M, N], which are rows and columns of mA
     * @param transA transpose flag for matrix mA
     */
    @Native("java","WrapBLAS.matvecMult((#1),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4),(#5).getDoubleArray(),(#6).getLongArray(),#7)")
    @Native("c++","matrix_vector_mult((#1),(#2)->raw,(#3)->raw,(#4),(#5)->raw,(#6)->raw,#7)")
    public static native def matrix_vector_mult(
            alpha:Double, mA:Rail[Double], 
                          x:Rail[Double], 
            beta:Double,  y:Rail[Double],
            dim:Rail[Long], 
            transA:Int):void;

    /**
     * Compute  y = alpha &#42 mA &#42 y + beta &#42 y, 
     * where A is lower triangle of symmetric matrix. Incremental step is 1.
     *
     * @param alpha  a scalar by which mA is premultiplied
     * @param mA     the symmetric matrix (right-side)
     * @param x      left-side operand vector
     * @param beta   a scalar by which to scale vector y and add to result.
     *               if beta is 0, y need not be initialized on input
     * @param y      output vector
     * @param dim    dimension array [M, N], which are rows and columns of mA
     */
    @Native("java","WrapBLAS.symvecMult(#1,(#2).getDoubleArray(),(#3).getDoubleArray(),#4,(#5).getDoubleArray(),(#6).getLongArray())")
    @Native("c++","sym_vector_mult(#1,(#2)->raw,(#3)->raw,#4,(#5)->raw,(#6)->raw)")
    public static native def sym_vector_mult(
            alpha:Double, mA:Rail[Double], 
                          x:Rail[Double], 
            beta:Double,  y:Rail[Double],
            dim:Rail[Long]):void;

    /**
     * Triangular-vector multply:  op(mA) &#42 x = b, where mA is unit lower-non-diagonal matrix.
     * 
     * @param mA     the lower-non-diagonal matrix
     * @param bx     right-hand side vector as input, and output
     * @param N      leading dimension of mA 
     * @param tA     transpose option for mA
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
     */    
    @Native("java","WrapBLAS.mattriSolve((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getLongArray(),#4)")
    @Native("c++","matrix_tri_solve((#1)->raw,(#2)->raw,(#3)->raw,#4)")
    public static native def matrix_tri_solve(
            BX:Rail[Double], 
            mA:Rail[Double], 
            dim:Rail[Long], transA:Int):void;

}
