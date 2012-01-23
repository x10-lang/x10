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

import x10.compiler.NoInline;
import x10.matrix.Debug;

/**
 * The class provides direct calls to BLAS routines. Matrix data inputs are stored in Array[Double](1), whose
 * memory layout complies with BLAS specification of column-mayor storage.  The leading dimension
 * of matrix (size of memory in double to store one column data) MUST be same as the matrix rows.
 * 
 * <p> NoInline is enforced to avoid WrapBLAS methods inlined in other packages in managed backend 
 */
public class BLAS {

	public static @NoInline def compScale(n:Int, a:Double, x:Array[Double](1)):void {
		DriverBLAS.scale(n, a, x);
	}
	
	public static @NoInline def doCopy(n:int, src:Array[Double](1), dst:Array[Double](1)):void {
		DriverBLAS.copy(n, src, dst);
	}
	
	public static @NoInline def compDotProd(n:Int, X:Array[Double](1), Y:Array[Double](1)):Double {
		val ret = DriverBLAS.dot_prod(n, X, Y);
		return ret;
	}
	
	public static @NoInline def compNorm(n:Int, X:Array[Double]):Double {
		return DriverBLAS.norm(n, X);
	}
	
	public static @NoInline def compAbsSum(n:Int, X:Array[Double](1)):Double {
		return DriverBLAS.abs_sum(n, X);
	}
	//-------------------------------------------------------------
	
	public static @NoInline def compMatMultMat(mA:Array[Double](1), mB:Array[Double](1),mC:Array[Double](1), 
			dim:Array[Int](1), scale:Array[Double](1), trans:Array[Int](1)):void {
		DriverBLAS.matrix_matrix_mult(mA,mB, mC, dim, scale, trans); 
	}
	//-------------------------------------------------------------
	public static @NoInline def compSymMultMat(mA:Array[Double](1), mB:Array[Double](1), mC:Array[Double](1),
			dim:Array[Int](1), scale:Array[Double](1)):void {
		DriverBLAS.sym_matrix_mult(mA, mB, mC, dim, scale);
	}

	public static @NoInline def compMatMultSym(mB:Array[Double](1),mA:Array[Double](1),mC:Array[Double](1),
			dim:Array[Int](1), scale:Array[Double](1)):void {
		DriverBLAS.matrix_sym_mult(mB, mA, mC, dim, scale);
	}
	//-------------------------------------------------------------
 
	public static @NoInline def compTriMultMat(mA:Array[Double](1), mB:Array[Double](1), 
			dim:Array[Int](1), tranA:Int):void {
		DriverBLAS.tri_matrix_mult(mA, mB, dim, tranA);
	}

	public static @NoInline def compMatMultTri(mB:Array[Double](1),mA:Array[Double](1),
			dim:Array[Int](1),tranB:Int):void {
		DriverBLAS.matrix_tri_mult(mB, mA, dim, tranB);
	}
	//-------------------------------------------------------------

	public static @NoInline def compMatMultVec(mA:Array[Double](1), x:Array[Double](1),y:Array[Double](1),
			dim:Array[Int](1), scale:Array[Double](1), transA:Int):void {
		DriverBLAS.matrix_vector_mult(mA, x, y, dim, scale, transA);
	}
	
	public static @NoInline def compSymMultVec(mA:Array[Double](1), x:Array[Double](1), y:Array[Double](1),
			dim:Array[Int](1), scale:Array[Double](1)):void {
		//Debug.flushln("Call symmetrix multply vector");
		DriverBLAS.sym_vector_mult(mA, x, y, dim, scale);
	}
	
	public static @NoInline def compTriMultVec(mA:Array[Double](1), bx:Array[Double](1), 
			lda:Int, tA:Int):void {
		DriverBLAS.tri_vector_mult(mA, bx, lda, tA);
	}
	//-------------------------------------------------------------
	//-------------------------------------------------------------

	public static @NoInline def solveTriMultVec(mA:Array[Double](1), bx:Array[Double](1), 
			dim:Array[Int](1), transA:Int):void {
		DriverBLAS.tri_vector_solve(mA, bx, dim, transA);
	}
	
	public static @NoInline def solveTriMultMat(mA:Array[Double](1), BX:Array[Double](1), 
			dim:Array[Int](1), transA:Int):void {
		DriverBLAS.tri_matrix_solve(mA, BX, dim, transA);
	}

	public static @NoInline def solveMatMultTri(BX:Array[Double](1), mA:Array[Double](1), 
			dim:Array[Int](1), transA:Int):void {
		DriverBLAS.matrix_tri_solve(BX, mA, dim, transA);
	}
}
