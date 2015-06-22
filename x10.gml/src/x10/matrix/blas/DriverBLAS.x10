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

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;
import x10.matrix.ElemType;   

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

 * vj: Fixed to support ElemType. See comments in src/x10/matrix/ElemType.x10

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
    @Native("java", "WrapBLAS.<#T$box> scaleET(#N, #alpha, #X);")
    @Native("c++","scale(#N, #alpha, (#X)->raw);")
    public static native def scale[T](
            N:Long, 
            alpha:T, 
            X:Rail[T]):void;

    /**
     * Copy N contiguous elements from X to Y.
     *
     * @param N        number of data to copy
     * @param X        source array
     * @param Y        destination array
     * vjTODO: Check the Java portion
     */
    @Native("java", "WrapBLAS. <#T$box> copyET(#N, #X, #Y);")
    @Native("c++", "copy(#N, (#X)->raw, (#Y)->raw);")
    public static native  def copy[T](
            N:Long, 
            X:Rail[T], 
            Y:Rail[T]):void;
    /**
     * Return dot product of vectors X and Y. 
     *
     * @param n        number of data to operate
     * @param X        right side array of dot product
     * @param Y        left side array of dot product
     * @return         dot-product result
     */
    @Native("java", "WrapBLAS.<#T$box>dotProdET(#n, #X, #Y)")
    @Native("c++","dot_prod(#n,(#X)->raw,(#Y)->raw)")
    public static native def dot_prod[T](
            n:Long, 
            X:Rail[T],
            Y:Rail[T]):T;

    /**
     * Perform Euclidean norm. Incremental step is 1.
     *
     * @param  n      number of data to operate in array
     * @param  X      data array.
     * @return        Eculidean norm
     */
    @Native("java", "WrapBLAS.<#T$box>norm2ET(#n, #X)")
    @Native("c++","norm2((#n),(#X)->raw)")
    public static native def norm[T](
            n:Long, 
            X:Rail[T]):T ;

    /**
     * Sum of absolute values of array X for n number of data.
     * @param  n      number of data to operate in array
     * @param  X      data array.
     * @return        absolute sum
     */
    @Native("java", "WrapBLAS.<#T$box>absSumET(#n, #X)")
    @Native("c++","abs_sum((#n),(#X)->raw)")
    public static native def abs_sum[T](
            n:Long, 
            X:Rail[T]):T;

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
    @Native("java","WrapBLAS.<#T$box> matmatMultOffET(#alpha, #mA, #mB, #beta, #mC, #dim.getLongArray(), (#ld).getLongArray(), (#offset).getLongArray(), (#trans).getIntArray());")
    @Native("c++","matrix_matrix_mult((#alpha),(#mA)->raw,(#mB)->raw,(#beta),(#mC)->raw,(#dim)->raw,(#ld)->raw,(#offset)->raw,(#trans)->raw);")
    public static native def matrix_matrix_mult[T](
            alpha:T, mA:Rail[T], 
	    mB:Rail[T],
            beta:T,  mC:Rail[T],
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
    @Native("java","WrapBLAS.<#T$box> matmatMultET((#alpha), (#mA), (#mB), (#beta), (#mC), (#dim).getLongArray(), (#ld).getLongArray(), #trans.getIntArray());")  
  @Native("c++","matrix_matrix_mult((#alpha),(#mA)->raw,(#mB)->raw,(#beta),(#mC)->raw,(#dim)->raw,(#ld)->raw,(#trans)->raw);")
    public static native def matrix_matrix_mult[T](
            alpha:T, mA:Rail[T], 
                          mB:Rail[T],
            beta:T,  mC:Rail[T],
            dim:Rail[Long], 
            ld:Rail[Long], 
            trans:Rail[Int]):void; 

    /**
     * Compute mC(N,N) = alpha &mC2 mA(N,K) &mC2 mA(K,N)' + beta &mC2 mC(N,N).
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
    @Native("java","WrapBLAS.<#T$box> symRankKUpdateOffET((#alpha), (#mA), (#beta), (#mC), (#dim).getLongArray(), (#ld).getLongArray(), (#offset).getLongArray(), (#upper), (#trans));")
    @Native("c++","sym_rank_k_update((#alpha),(#mA)->raw,(#beta),(#mC)->raw,(#dim)->raw,(#ld)->raw,(#offset)->raw,(#upper),(#trans))")
    public static native def sym_rank_k_update[T](
            alpha:T, mA:Rail[T], 
            beta:T,  mC:Rail[T],
            dim:Rail[Long],
            ld:Rail[Long],
            offset:Rail[Long],
            upper:Boolean,
            trans:Boolean):void; 

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
    @Native("java","WrapBLAS. <#T$box> symRankKUpdateET((#alpha), (#mA), (#beta), (#mC), (#dim).getLongArray(), (#upper), (#trans));")
    @Native("c++","sym_rank_k_update((#alpha),(#mA)->raw,(#beta),(#mC)->raw,(#dim)->raw,(#upper),(#trans))")
    public static native def sym_rank_k_update[T](
            alpha:T, mA:Rail[T], 
            beta:T,  mC:Rail[T],
            dim:Rail[Long], 
            upper:Boolean,
            trans:Boolean):void ; 

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
    @Native("java","WrapBLAS.<#T$box> symmatMultET((#alpha), (#mA), (#mB), (#beta), (#mC), (#dim).getLongArray());")
    @Native("c++","sym_matrix_mult((#alpha), (#mA)->raw,(#mB)->raw,(#beta),(#mC)->raw,(#dim)->raw)")
    public static native def sym_matrix_mult[T](
            alpha:T, mA:Rail[T], 
                          mB:Rail[T], 
            beta:T,  mC:Rail[T],
            dim:Rail[Long]):void; 

    /**
     * Compute mC = alpha*mA &#42 mB + beta*mC, 
     * where mA is lower triangle of symmetric matrix.
     */
    @Native("java","WrapBLAS. <#T$box> matsymMultET((#mB), (#alpha),(#mA), (#beta),(#mC),(#dim).getLongArray());")
    @Native("c++","matrix_sym_mult((#mB)->raw,(#alpha),(#mA)->raw,(#beta),(#mC)->raw,(#dim)->raw)")
    public static native def matrix_sym_mult[T](
                          mB:Rail[T], 
            alpha:T, mA:Rail[T], 
            beta:T,  mC:Rail[T],
			  dim:Rail[Long]):void ; 

    /**
     * Compute mB =  op( mA ) &#42 mB, mA is non-unit triangular matrix.
     *
     * @param mA     Double precision array storing triangular matrix mA.
     * @param mB     Double precision array storing matrix mB, which also is the output.
     * @param dim    dimension array [M, N], which are rows of mB and columns of mB. Lower/upper triangular flag sets the last value.
     * @param tranA  transpose option for mA
     */
    @Native("java","WrapBLAS.<#T$box> trimatMultET((#mA),(#mB),(#dim).getLongArray(),(#tranA));")
    @Native("c++","tri_matrix_mult((#mA)->raw,(#mB)->raw,(#dim)->raw,(#tranA))")
    public static native def tri_matrix_mult[T](
            mA:Rail[T], 
            mB:Rail[T], 
            dim:Rail[Long], 
            tranA:Int):void; 
    /**
     * Compute mB =  mA &#42 op( mB ), mB is non-unit triangular matrix.
     *
     * @param mA     ElemType precision array storing matrix mA and output matrix.
     * @param mB     ElemType precision array storing triangular matrix mB.
     * @param dim    dimension array [M, N], which are rows of mB and columns of mB. 
     * @param tranB  transpose option for matrix mB
     */
    @Native("java","WrapBLAS. <#T$box> mattriMultET((#mA),(#mB),(#dim).getLongArray(), (#tranB));")
    @Native("c++","matrix_tri_mult((#mA)->raw,(#mB)->raw,(#dim)->raw,(#tranB))")
    public static native def matrix_tri_mult[T](mA:Rail[T], 
                                             mB:Rail[T],
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
    @Native("java","WrapBLAS.<#T$box> matvecMultOffET((#alpha),(#mA),(#x),(#beta),(#y),(#dim).getLongArray(),(#lda),(#offset).getLongArray(),(#transA));")
    @Native("c++","matrix_vector_mult((#alpha),(#mA)->raw,(#x)->raw,(#beta),(#y)->raw,(#dim)->raw,#lda,(#offset)->raw,#transA)")
    public static native def matrix_vector_mult[T](
            alpha:T, mA:Rail[T], 
                          x:Rail[T], 
            beta:T,  y:Rail[T],
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
    @Native("java","WrapBLAS.<#T$box> matvecMultET((#alpha),(#mA),(#x), (#beta),(#y), (#dim).getLongArray(), (#transA));")
    @Native("c++","matrix_vector_mult((#alpha),(#mA)->raw,(#x)->raw,(#beta),(#y)->raw,(#dim)->raw,(#transA))")
    public static native def matrix_vector_mult[T](
            alpha:T, mA:Rail[T], 
                          x:Rail[T], 
            beta:T,  y:Rail[T],
            dim:Rail[Long], 
            transA:Int):void; 

    /**
     * Compute  y = alpha &beta2 mA &beta2 y + beta &beta2 y, 
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
    @Native("java","WrapBLAS.<#T$box> symvecMultET((#alpha),(#mA),(#x),(#beta),(#y),(#dim).getLongArray());")
    @Native("c++","sym_vector_mult((#alpha),(#mA)->raw,(#x)->raw,(#beta),(#y)->raw,(#dim)->raw);")
	public static native def sym_vector_mult[T](
            alpha:T, mA:Rail[T], 
                          x:Rail[T], 
            beta:T,  y:Rail[T],
            dim:Rail[Long]):void; 

    /**
     * Triangular-vector multply:  op(mA) &tA2 x = b, where mA is unit lower-non-diagonal matrix.
     * 
     * @param mA     the lower-non-diagonal matrix
     * @param bx     right-hand side vector as input, and output
     * @param N      leading dimension of mA 
     * @param lda
     * @param tA     transpose option for mA
     */    
    @Native("java","WrapBLAS.<#T$box> trivecMultET((#mA),(#uplo), (#bx), (#lda), (#tA));")
    @Native("c++","tri_vector_mult((#mA)->raw,(#uplo),(#bx)->raw,(#lda),(#tA));")
    public static native def tri_vector_mult[T](
            mA:Rail[T], 
            uplo:Int,
            bx:Rail[T], 
            lda:Long, tA:Int):void; 


    //A := alpha*x*y**T + A,
    /**
     * Compute A = alpha &#42 x &#42 y &#42 &#42 T + A, general rank-1 update.
     *
     * @param alpha  scalar alpha
     * @param x      vector of dimension at least M+offsetX
     * @param y      output vector
     * @param mA     the first matrix (right-side)
     * @param dim    dimension array [M, N], which are rows and columns of mA
     * @param offset row and column offsets [xr, yr, Ar, Ac] into matrix/vectors
     * @param inc    increments [incX, incY] for elements of X and Y
     * @param lda    leading dimension of A

     */
    @Native("java","WrapBLAS.<#T$box> rankOneUpdateET((#alpha),(#x),(#y),(#mA),(#dim).getLongArray(),(#offset).getLongArray(),(#inc).getLongArray(), (#lda));")
    @Native("c++","rank_one_update((#alpha),(#x)->raw,(#y)->raw,(#mA)->raw,(#dim)->raw,(#offset)->raw,(#inc)->raw,(#lda))")
    public static native def rank_one_update[T](
            alpha:T,
            x:Rail[T],
            y:Rail[T],
            mA:Rail[T],
            dim:Rail[Long],
            offset:Rail[Long],
            inc:Rail[Long],
            lda:Long):void ; 

    /**
     * Solve equation  mA &#42 x = b, where mA is unit lower-triangular matrix.
     *
     * @param mA     the unit lower-triangular matrix
     * @param bx     right-hand side vector as input, and output
     * @param dim    leading dimension and order of mA 
     * @param transA transpose option for mA 
     */    
    @Native("java","WrapBLAS. <#T$box> trivecSolveET((#mA), (#bx), (#dim).getLongArray(),(#transA));")
    @Native("c++","tri_vector_solve((#mA)->raw,(#bx)->raw,(#dim)->raw,(#transA));")
    public static native def tri_vector_solve[T](
            mA:Rail[T], 
            bx:Rail[T], 
            dim:Rail[Long], transA:Int):void; 

    /**
     * Solve matrix equation  op(mA) &#42 X = B, where mA is unit lower-triangular matrix.
     * 
     * @param mA     the unit lower-triangular matrix
     * @param BX     right-hand side matrix as input, and output
     * @param dim    leading dimension of mA and leading dimension of B 
     * @param transA transpose option for mA 
     */    
    @Native("java","WrapBLAS.<#T$box> trimatSolveET((#mA),(#BX),(#dim).getLongArray(),(#transA));")
    @Native("c++","tri_matrix_solve((#mA)->raw,(#BX)->raw,(#dim)->raw,(#transA));")
    public static native def tri_matrix_solve[T](
            mA:Rail[T], 
            BX:Rail[T], 
            dim:Rail[Long], transA:Int):void; 

    /**
     * Solve matrix equation  X &#42 op(mA) = B, where mA is unit lower-triangular matrix.
     * 
     * @param BX     left-hand side matrix as input, and output
     * @param mA     the unit lower-triangular matrix
     * @param dim    leading dimension of mA and leading dimension of B 
     * @param transA transpose option for mA 
     */    
    @Native("java","WrapBLAS.<#T$box> mattriSolveET((#BX),(#mA),(#dim).getLongArray(),(#transA));")
    @Native("c++","matrix_tri_solve((#BX)->raw,(#mA)->raw,(#dim)->raw,(#transA))")
    public static native def matrix_tri_solve[T](
            BX:Rail[T], 
            mA:Rail[T], 
            dim:Rail[Long], transA:Int):void; 
}
