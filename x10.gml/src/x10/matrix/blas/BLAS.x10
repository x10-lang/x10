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

import x10.compiler.NoInline;
import x10.matrix.ElemType;
/**
 * The class provides direct calls to BLAS routines. Matrix data inputs are
 * stored as Rail[ElemType] in column-major format.  The leading dimension
 * of matrix MUST be equal to the number of rows.
 * 
 * <p> NoInline is enforced to avoid WrapBLAS methods inlined in other packages in managed backend 
 */
public class BLAS {
	public static @NoInline def compScale(n:Long, a:ElemType, x:Rail[ElemType]):void {
		DriverBLAS.scale(n, a, x);
	}
	
	public static @NoInline def doCopy(n:Long, src:Rail[ElemType], dst:Rail[ElemType]):void {
		DriverBLAS.copy(n, src, dst);
	}
	
	public static @NoInline def compDotProd(n:Long, X:Rail[ElemType], Y:Rail[ElemType]):ElemType {
		val ret = DriverBLAS.dot_prod(n, X, Y);
		return ret;
	}
	
	public static @NoInline def compNorm(n:Long, X:Rail[ElemType]):ElemType {
		return DriverBLAS.norm(n, X);
	}
	
	public static @NoInline def compAbsSum(n:Long, X:Rail[ElemType]):ElemType {
		return DriverBLAS.abs_sum(n, X);
	}
	
	public static @NoInline def compMatMultMat(alpha:ElemType, mA:Rail[ElemType], mB:Rail[ElemType], beta:ElemType, mC:Rail[ElemType], dim:Rail[Long], trans:Rail[Int]):void {
        val ld = [dim(0), dim(2), dim(0)];
		DriverBLAS.matrix_matrix_mult(alpha, mA, mB, beta, mC, dim, ld, trans); 
	}

	public static @NoInline def compSymMultMat(alpha:ElemType, mA:Rail[ElemType], mB:Rail[ElemType], beta:ElemType, mC:Rail[ElemType], dim:Rail[Long]):void {
		DriverBLAS.sym_matrix_mult(alpha, mA, mB, beta, mC, dim);
	}

	public static @NoInline def compMatMultSym(alpha:ElemType, mB:Rail[ElemType], mA:Rail[ElemType], beta:ElemType, mC:Rail[ElemType], dim:Rail[Long]):void {
		DriverBLAS.matrix_sym_mult(mB, alpha, mA, beta, mC, dim);
	}

	public static @NoInline def compTriMultMat(mA:Rail[ElemType], mB:Rail[ElemType], dim:Rail[Long], tranA:Int):void {
		DriverBLAS.tri_matrix_mult(mA, mB, dim, tranA);
	}

	public static @NoInline def compMatMultTri(mB:Rail[ElemType], mA:Rail[ElemType],
			dim:Rail[Long],tranB:Int):void {
		DriverBLAS.matrix_tri_mult(mB, mA, dim, tranB);
	}

	public static @NoInline def compMatMultVec(alpha:ElemType, mA:Rail[ElemType], x:Rail[ElemType], beta:ElemType, y:Rail[ElemType], dim:Rail[Long], transA:Int):void {
		DriverBLAS.matrix_vector_mult(alpha, mA, x, beta, y, dim, transA);
	}
	
	public static @NoInline def compSymMultVec(alpha:ElemType, mA:Rail[ElemType], x:Rail[ElemType], beta:ElemType, y:Rail[ElemType], dim:Rail[Long]):void {
		DriverBLAS.sym_vector_mult(alpha, mA, x, beta, y, dim);
	}
	
	public static @NoInline def compTriMultVec(mA:Rail[ElemType], uplo:Boolean, bx:Rail[ElemType], 
			lda:Long, tA:Int):void {
		DriverBLAS.tri_vector_mult(mA, uplo?1n:0n, bx, lda, tA);
	}

	public static @NoInline def solveTriMultVec(mA:Rail[ElemType], bx:Rail[ElemType], 
			dim:Rail[Long], transA:Int):void {
		DriverBLAS.tri_vector_solve(mA, bx, dim, transA);
	}
	
	public static @NoInline def solveTriMultMat(mA:Rail[ElemType], BX:Rail[ElemType], 
			dim:Rail[Long], transA:Int):void {
		DriverBLAS.tri_matrix_solve(mA, BX, dim, transA);
	}

	public static @NoInline def solveMatMultTri(BX:Rail[ElemType], mA:Rail[ElemType], 
			dim:Rail[Long], transA:Int):void {
		DriverBLAS.matrix_tri_solve(BX, mA, dim, transA);
	}
}
