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
 * <p> The general case of matrix multiplication requires two input
 * matrices, A and B, and one output matrix C.
 * <p> All matrices uses column-major storage.
 * <p> Two versions are provided of each multiplication method:
 * a simple interface which operates on entire matrices/vectors,
 * and a full-featured interface which operates on an offset patch within
 * each matrix/vector.
 * <p> For functions other than multiply, look for in BLAS.x10.
 */
public class DenseMatrixBLAS {

    /**
     * Compute y = alpha*A &#42 x + beta*y,
     * for an offset patch within each matrix or vector.
     *
     * @param alpha  a scalar by which A is premultiplied
     * @param A      a matrix of dimension at least (M+offset(0), N+offset(1))
     * @param x      a vector of dimension at least (N+offset(2))
     * @param beta   a scalar by which to scale vector y and add to result.
     *               if beta is 0, y need not be initialized on input
     * @param y      result vector of dimension at least (M+offset(3))
     * @param dim    the dimensions [M, N] used in BLAS multiply where
     *               M is the number of rows in A and y
                     N is the number of columns in A and rows in x
     * @param offset row and column offsets [Ar, Ac, xr, yr] into matrix/vectors
     */
    public static def comp(
            alpha:Double, A:DenseMatrix, 
                          x:Vector, 
            beta:Double,  y:Vector, 
            dim:Rail[Long],
            offset:Rail[Long]) :void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(0) <= A.M && offset(1)+dim(1) <= A.N,
                offset(0)+"+"+dim(0) + " <= " + A.M + " && " + offset(1)+"+"+dim(1) + " <= " + A.N);
            Debug.assure(offset(2)+dim(1) <= x.M && offset(3)+dim(0) <= y.M,
                offset(2)+"+"+dim(1) + " <= " + x.M + " && " + offset(3)+"+"+dim(0) + " <= " + y.M);
        }
        val transA = 0n;
        DriverBLAS.matrix_vector_mult(alpha, A.d, x.d, beta, y.d, dim, A.M, offset, transA);
    }

    public static def comp(
            alpha:Double, A:DenseMatrix, 
                          B:Vector(A.N), 
            beta:Double,  C:Vector(A.M)) :void {
        val dim=[A.M, A.N];
        val transA = 0n;
        DriverBLAS.matrix_vector_mult(alpha, A.d, B.d, beta, C.d, dim, transA);
    }

    /**
     * Compute y += alpha*A<sup>T<sup> &#42 x + beta*y,
     * for an offset patch within each matrix or vector.
     *
     * @param A      matrix of dimension at least (M+offset(0), N+offset(1))
     * @param x      vector of dimension at least (M+offset(2))
     * @param beta   scalar by which to scale vector y and add to result.
     *               if beta is 0, y need not be initialized on input
     * @param y      result vector of dimension at least (N+offset(3))
     * @param dim    the dimensions M, N used in BLAS multiply where
     *               M is the number of rows in A and x
     *               N is the number of columns in A and rows in y
     * @param offset row and column offsets [Ar, Ac, xr, yr] into matrix/vectors
     */
    public static def compTransMult(
            alpha:Double, A:DenseMatrix, 
                          x:Vector, 
            beta:Double,  y:Vector, 
            dim:Rail[Long],
            offset:Rail[Long]) :void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(0) <= A.M && offset(1)+dim(1) <= A.N,
                offset(0)+"+"+dim(0) + " <= " + A.M + " && " + offset(1)+"+"+dim(1) + " <= " + A.N);
            Debug.assure(offset(2)+dim(0) <= x.M && offset(3)+dim(1) <= y.M,
                offset(2)+"+"+dim(0) + " <= " + x.M + " && " + offset(3)+"+"+dim(1) + " <= " + y.M);
        }
        val transA = 1n;
        DriverBLAS.matrix_vector_mult(alpha, A.d, x.d, beta, y.d, dim, A.M, offset, transA);
    }

    public static def compTransMult(
            alpha:Double, A:DenseMatrix, 
                          B:Vector(A.M), 
            beta:Double,  C:Vector(A.N)) :void {
        val dim=[A.M, A.N];
        val transA = 1n;
        DriverBLAS.matrix_vector_mult(alpha, A.d, B.d, beta, C.d, dim, transA);
    }

    /** Symmetric multiply vector */
    public static def comp(
            alpha:Double, A:SymDense,
                          B:Vector(A.N), 
            beta:Double,  C:Vector(A.N)) :void {
        val dim=[A.N, A.M];
        DriverBLAS.sym_vector_mult(alpha, A.d, B.d, beta, C.d, dim);
    }


    /** Symmetric multiply dense */
    public static def comp(
            alpha:Double, A:SymDense, 
                          B:DenseMatrix{A.N==B.M}, 
            beta:Double,  C:DenseMatrix{A.M==C.M&&B.N==C.N}):void {
        val dims = [ C.M, C.N ];
        DriverBLAS.sym_matrix_mult(alpha, A.d, B.d, beta, C.d, dims);
    }

    public static def comp(
            alpha:Double, A:DenseMatrix, 
                          B:SymDense{A.N==B.M}, 
            beta:Double,  C:DenseMatrix{A.M==C.M&&B.N==C.N}):void {
        val dims = [ C.M, C.N ];
        DriverBLAS.matrix_sym_mult(A.d, alpha, B.d, beta, C.d, dims);
    }

    /**
     * Compute B = A &#42 B, where A is a triangular dense matrix.
     */
    public static def comp(A:TriDense, B:DenseMatrix{A.N==B.M}):void  {
        DriverBLAS.tri_matrix_mult(A.d, B.d, [B.M, B.N, A.upper?1L:0L], 0n);
    }
    
    /**
     * Compute B = A &#42 B<sup>T<sup>, where A is a triangular dense matrix.
     */
    public static def compMultTrans(A:TriDense, B:DenseMatrix{A.N==B.M}):void  {
        DriverBLAS.tri_matrix_mult(A.d, B.d, [B.M, B.N, A.upper?1L:0L], 1n);
    }

    /**
     * Compute B = B &#42 A, where A is a triangular dense matrix.
     */
    public static def comp(B:DenseMatrix, A:TriDense{B.N==A.M}):void  {
        DriverBLAS.matrix_tri_mult(B.d, A.d, [B.M, B.N, A.upper?1L:0L], 0n);
    }

    /**
     * Compute B = B<sup>T<sup> &#42 A, where A is a triangular dense matrix.
     */
    public static def compTransMult(B:DenseMatrix, A:TriDense{B.N==A.M}):void  {
        DriverBLAS.matrix_tri_mult(B.d, A.d, [B.M, B.N, A.upper?1L:0L], 1n);
    }

    /**
     * Compute C = alpha*A &#42 B + beta*C,
     * for an offset patch within each matrix.
     *
     * @param alpha  scalar by which A is premultiplied
     * @param A      dense matrix of dimension at least (M+Ar,K+Ac)
     * @param B      dense matrix of dimension at least (K+Br,N+Bc)
     * @param beta   scalar by which to scale matrix C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      result matrix of dimension at least (M+Cr,N+Cc)
     * @param dim    the dimensions M, N and K used in BLAS multiply where
     *               M is the number of rows in A and C,
                     N is the number of columns in B and C, and
                     K is the number of columns in A and rows in B
     * @param offset row and column offsets [Ar, Ac, Br, Bc, Cr, Cc] into matrices
     */
    public static def comp(
            alpha:Double, A:DenseMatrix, 
                          B:DenseMatrix,
            beta:Double,  C:DenseMatrix,
            dim:Rail[Long], offset:Rail[Long]):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(0) <= A.M && offset(1)+dim(2) <= A.N,
                offset(0)+"+"+dim(0) + " <= " + A.M + " && " + offset(1)+"+"+dim(2) + " <= " + A.N);
            Debug.assure(offset(2)+dim(2) <= B.M && offset(3)+dim(1) <= B.N,
                offset(2)+"+"+dim(2) + " <= " + B.M + " && " + offset(3)+"+"+dim(1) + " <= " + B.N);
            Debug.assure(offset(4)+dim(0) <= C.M && offset(5)+dim(1) <= C.N,
                offset(4)+"+"+dim(0) + " <= " + C.M + " && " + offset(5)+"+"+dim(1) + " <= " + C.N);
        }

        val trans = [ 0n, 0n as Int ];
        val ld = [A.M, B.M, C.M];
        DriverBLAS.matrix_matrix_mult(alpha, A.d, B.d, beta, C.d, dim, ld, offset, trans);
    }

    /**
     * Compute C = alpha*A &#42 B + beta*C
     *
     * @param alpha  scalar by which A is premultiplied
     * @param A      dense matrix of dimension at least (M+Ar,K+Ac)
     * @param B      dense matrix of dimension at least (K+Br,N+Bc)
     * @param beta   scalar by which to scale matrix C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      result matrix of dimension at least (M+Cr,N+Cc)
     */
    public static def comp(
            alpha:Double, A:DenseMatrix, 
                          B:DenseMatrix{A.N==B.M}, 
            beta:Double,  C:DenseMatrix{A.M==C.M,B.N==C.N}): void {
        val dim = [A.M, B.N, A.N];
        val trans = [ 0n, 0n as Int ];
        val ld = [A.M, B.M, C.M];
        DriverBLAS.matrix_matrix_mult(alpha, A.d, B.d, beta, C.d, dim, ld, trans);
    }

    /**
     * Compute C += alpha*A<sup>T</sup> &#42 B + beta*C
     * for an offset patch within each matrix.
     *
     * @param alpha  a scalar by which A is premultiplied
     * @param A      the first operand dense matrix in multiplication
     * @param B      the second operand dense matrix
     * @param beta   a scalar by which to scale input C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N and K used in BLAS multiply where
     *               M is the number of rows in A<sup>T</sup> and C,
     *               N is the number of columns in B and C, and
     *               K is the number of columns in A<sup>T</sup> and rows in B
     * @param offset row and column offsets [Ar, Ac, Br, Bc, Cr, Cc] into matrices
     */
    public static def compTransMult(
            alpha:Double, A:DenseMatrix, 
                          B:DenseMatrix,
            beta:Double,  C:DenseMatrix, 
            dim:Rail[Long], offset:Rail[Long]):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(2) <= A.M && offset(1)+dim(0) <= A.N,
                offset(0)+"+"+dim(2) + " <= " + A.M + " && " + offset(1)+"+"+dim(0) + " <= " + A.N);
            Debug.assure(offset(2)+dim(2) <= B.M && offset(3)+dim(1) <= B.N,
                offset(2)+"+"+dim(2) + " <= " + B.M + " && " + offset(3)+"+"+dim(1) + " <= " + B.N);
            Debug.assure(offset(4)+dim(0) <= C.M && offset(5)+dim(1) <= C.N,
                offset(4)+"+"+dim(0) + " <= " + C.M + " && " + offset(5)+"+"+dim(1) + " <= " + C.N);
        }

        val trans = [ 1n, 0n ];
        val ld = [A.M, B.M, C.M];
        DriverBLAS.matrix_matrix_mult(alpha, A.d, B.d, beta, C.d, dim, ld, offset, trans);
    }

    /**
     * Compute C = alpha*A<sup>T</sup> &#42 B + beta*C by calling BLAS driver
     *
     * @param alpha  a scalar by which A is premultiplied
     * @param A      the first operand dense matrix in multiplication which is used as it is transposed
     * @param B      the second operand dense matrix
     * @param beta   a scalar by which to scale input C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      dense matrix which is used to store the result
     */
    public static def compTransMult(
            alpha:Double, A:DenseMatrix,
                          B:DenseMatrix{B.M==A.M}, 
            beta:Double,  C:DenseMatrix{C.M==A.N,C.N==B.N}):void {
        val dim = [A.N, B.N, A.M];
        val trans = [ 1n, 0n ];
        val ld = [A.M, B.M, C.M];
        DriverBLAS.matrix_matrix_mult(alpha, A.d, B.d, beta, C.d, dim, ld, trans);
    }

    /**
     * Compute C = alpha*A &#42 B<sup>T</sup> + beta*C,
     * for an offset patch within each matrix.
     *
     * @param alpha  a scalar by which A is premultiplied
     * @param A      the first operand dense matrix in multiplication
     * @param B      the second operand dense matrix
     * @param beta   a scalar by which to scale input C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N and K used in BLAS multiply where
     *               M is the number of rows in A and C,
     *               N is the number of columns in B<sup>T</sup> and C, and
     *               K is the number of columns in A and rows in B<sup>T</sup>
     * @param offset row and column offsets [Ar, Ac, Br, Bc, Cr, Cc] into matrices
     */
    public static def compMultTrans(
            alpha:Double, A:DenseMatrix,
                          B:DenseMatrix,
            beta:Double,  C:DenseMatrix,
            dim:Rail[Long], offset:Rail[Long]):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(0) <= A.M && offset(1)+dim(2) <= A.N,
                offset(0)+"+"+dim(0) + " <= " + A.M + " && " + offset(1)+"+"+dim(2) + " <= " + A.N);
            Debug.assure(offset(2)+dim(1) <= B.M && offset(3)+dim(2) <= B.N,
                offset(2)+"+"+dim(1) + " <= " + B.M + " && " + offset(3)+"+"+dim(2) + " <= " + B.N);
            Debug.assure(offset(4)+dim(0) <= C.M && offset(5)+dim(1) <= C.N,
                offset(4)+"+"+dim(0) + " <= " + C.M + " && " + offset(5)+"+"+dim(1) + " <= " + C.N);
        }
        val trans = [ 0n, 1n ];
        val ld = [A.M, B.M, C.M];
        DriverBLAS.matrix_matrix_mult(alpha, A.d, B.d, beta, C.d, dim, ld, offset, trans);
    }
    
    /**
     * Compute C = alpha*A &#42 B<sup>T</sup> + beta*C 
     *
     * @param alpha  a scalar by which A is premultiplied
     * @param A      the first operand dense matrix in multiplication 
     * @param B      the second operand dense matrix which is used in transposed form
     * @param beta   a scalar by which to scale input C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      dense matrix which is used to store the result
     */
    public static def compMultTrans(
            alpha:Double, A:DenseMatrix, 
                          B:DenseMatrix{B.N==A.N}, 
            beta:Double,  C:DenseMatrix{C.M==A.M,C.N==B.M}):void {
        val dim = [A.M, B.M, A.N];
        val trans = [ 0n, 1n ];
        val ld = [A.M, B.M, C.M];
        DriverBLAS.matrix_matrix_mult(alpha, A.d, B.d, beta, C.d, dim, ld, trans);
    }

    /**
     * Compute C = alpha*A<sup>T</sup> &#42 B<sup>T</sup> + beta*C,
     * for an offset patch within each matrix.
     *
     * @param alpha  a scalar by which A is premultiplied
     * @param A      the first operand dense matrix in multiplication
     * @param B      the second operand dense matrix
     * @param beta   a scalar by which to scale input C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N and K used in BLAS multiply where
     *               M is the number of rows in A<sup>T</sup> and C,
     *               N is the number of columns in B<sup>T</sup> and C, and
     *               K is the number of columns in A<sup>T</sup> and rows in B<sup>T</sup>
     * @param offset row and column offsets [Ar, Ac, Br, Bc, Cr, Cc] into matrices
     */
    public static def compTransMultTrans(
            alpha:Double, A:DenseMatrix,
                          B:DenseMatrix, 
            beta:Double,  C:DenseMatrix,
            dim:Rail[Long], offset:Rail[Long]):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(2) <= A.M && offset(1)+dim(0) <= A.N,
                offset(0)+"+"+dim(2) + " <= " + A.M + " && " + offset(1)+"+"+dim(0) + " <= " + A.N);
            Debug.assure(offset(2)+dim(1) <= B.M && offset(3)+dim(2) <= B.N,
                offset(2)+"+"+dim(1) + " <= " + B.M + " && " + offset(3)+"+"+dim(2) + " <= " + B.N);
            Debug.assure(offset(4)+dim(0) <= C.M && offset(5)+dim(1) <= C.N,
                offset(4)+"+"+dim(0) + " <= " + C.M + " && " + offset(5)+"+"+dim(1) + " <= " + C.N);
        }
        val trans = [ 1n, 1n as Int ];
        val ld = [A.M, B.M, C.M];
        DriverBLAS.matrix_matrix_mult(alpha, A.d, B.d, beta, C.d, dim, ld, offset, trans);
    }

    /**
     * Compute C = alpha*A<sup>T</sup> &#42 B<sup>T</sup> + beta*C
     *
     * @param alpha  a scalar by which A is premultiplied
     * @param A      the first operand dense matrix in multiplication which is used in transposed form
     * @param B      the second operand dense matrix which is used in transposed form
     * @param beta   a scalar by which to scale input C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      dense matrix which is used to store the result
     */
    public static def compTransMultTrans(
            alpha:Double, A:DenseMatrix, 
                          B:DenseMatrix{B.N==A.M}, 
            beta:Double,  C:DenseMatrix{C.M==A.N,C.N==B.M}):void {
        val dim      = [A.N, B.M, A.M];
        val trans = [ 1n, 1n as Int ];
        val ld = [A.M, B.M, C.M];
        DriverBLAS.matrix_matrix_mult(alpha, A.d, B.d, beta, C.d, dim, ld, trans);
    }

    /**
     * Compute symmetric rank K update C += A &#42 A<sup>T</sup>
     *
     * @param alpha  a scalar by which A is premultiplied
     * @param A      a symmetric dense matrix
     * @param beta   a scalar by which to scale input C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      dense matrix which is used to store the result
     * @param upper  if true, update upper half of C; otherwise update lower half
     */
    public static def symRankKUpdate(
            alpha:Double, A:DenseMatrix,
            beta:Double,  C:DenseMatrix{C.M==C.N,C.N==A.M},
            upper:Boolean):void {
        val dim = [A.M, A.N];
        DriverBLAS.sym_rank_k_update(alpha, A.d, beta, C.d, dim, upper, false);
    }

    /**
     * Compute symmetric rank K update C += A &#42 A<sup>T</sup>
     * for offset patches within A, C
     *
     * @param alpha  a scalar by which A is premultiplied
     * @param A      a symmetric dense matrix
     * @param beta   a scalar by which to scale input C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N used in BLAS rank-K update where
     *               M is the number of rows in the patch of A and the 
     *               number of rows and columns in the patch of C, and
     *               N is the number of columns in the patch of A
     * @param offset row and column offsets [Ar, Ac, Cr, Cc] into matrices
     * @param upper  if true, update upper half of C; otherwise update lower half
     */
    public static def symRankKUpdate(
            alpha:Double, A:DenseMatrix,
            beta:Double,  C:DenseMatrix,
            dim:Rail[Long], offset:Rail[Long], upper:Boolean):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(0) <= A.M && offset(1)+dim(1) <= A.N,
                offset(0)+"+"+dim(0) + " <= " + A.M + " && " + offset(1)+"+"+dim(1) + " <= " + A.N);
            Debug.assure(offset(2)+dim(0) <= C.M && offset(3)+dim(0) <= C.N,
                offset(2)+"+"+dim(0) + " <= " + C.M + " && " + offset(3)+"+"+dim(1) + " <= " + C.N);
        }
        val ld = [A.M, C.M];
        DriverBLAS.sym_rank_k_update(alpha, A.d, beta, C.d, dim, ld, offset, upper, false);
    }

    /**
     * Compute symmetric rank K update C = alpha*A<sup>T</sup> &#42 A + beta*C
     *
     * @param alpha  a scalar by which A is premultiplied
     * @param A      a symmetric dense matrix which is used in transposed form
     * @param beta   a scalar by which to scale input C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      dense matrix which is used to store the result
     * @param upper  if true, update upper half of C; otherwise update lower half
     */
    public static def symRankKUpdateTrans(
            alpha:Double, A:DenseMatrix,
            beta:Double,  C:DenseMatrix{C.M==C.N,C.N==A.N},
            upper:Boolean):void {
        val dim = [A.N, A.M];
        DriverBLAS.sym_rank_k_update(alpha, A.d, beta, C.d, dim, upper, true);
    }

    /**
     * Compute symmetric rank K update C = alpha*A<sup>T</sup> &#42 A + beta*C
     * for offset patches within A, C
     *
     * @param alpha  a scalar by which A is premultiplied
     * @param A      a symmetric dense matrix which is used in transposed form
     * @param beta   a scalar by which to scale input C and add to result.
     *               if beta is 0, C need not be initialized on input
     * @param C      dense matrix which is used to store the result
     * @param dim    the dimensions M, N used in BLAS rank-K update where
     *               M is the number of columns in the patch of A and the 
                     number of rows and columns in the patch of C, and
                     N is the number of rows in the patch of A
     * @param offset row and column offsets [Ar, Ac, Cr, Cc] into matrices
     * @param upper  if true, update upper half of C; otherwise update lower half
     */
    public static def symRankKUpdateTrans(
            alpha:Double, A:DenseMatrix,
            beta:Double,  C:DenseMatrix,
            dim:Rail[Long], offset:Rail[Long], upper:Boolean):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(offset(0)+dim(1) <= A.M && offset(1)+dim(0) <= A.N,
                offset(0)+"+"+dim(1) + " <= " + A.M + " && " + offset(1)+"+"+dim(0) + " <= " + A.N);
            Debug.assure(offset(2)+dim(0) <= C.M && offset(3)+dim(0) <= C.N,
                offset(2)+"+"+dim(0) + " <= " + C.M + " && " + offset(3)+"+"+dim(1) + " <= " + C.N);
        }
        val ld = [A.M, C.M];
        DriverBLAS.sym_rank_k_update(alpha, A.d, beta, C.d, dim, ld, offset, upper, true);
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
