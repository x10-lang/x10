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


/**
 * This Java class provide wrap JNI for BLAS library
 */
public class WrapBLAS {

	static { 
		System.loadLibrary("jblas");
	}

	public static native void scale(long n, double alpha, double[] x);
	public static native void copy(long n, double[] x, double[] y);
	public static native double dotProd(long n, double[] x, double[] y);
	public static native double norm2(long n, double[] x);
	public static native double absSum(long n, double[] x);

    public static native void matmatMultOff(double alpha, double[] A, double[] B, double beta, double[] C, long[] dim, long[] ld, long[] off, int[] trans);
	public static native void matmatMult(double alpha, double[] A, double[] B, double beta, double[] C, long[] dim, long[] ld, int[] trans);

	public static native void symRankKUpdateOff(double alpha, double[] A, double beta, double[] C, long[] dim, long[] ld, long[] off, boolean upper, boolean trans);
	public static native void symRankKUpdate(double alpha, double[] A, double beta, double[] C, long[] dim, boolean upper, boolean trans);

	public static native void symmatMult(double alpha, double[] A, double[] B, double beta, double[] C, long[] dim);
	public static native void matsymMult(double[] B, double alpha, double[] A, double beta, double[] C, long[] dim);

	public static native void matvecMultOff(double alpha, double[] A, double[] x, double beta, double[] y, long[] dim, long lda, long[] off, int transA);
	public static native void matvecMult(double alpha, double[] A, double[] x, double beta, double[] y, long[] dim, int transA);
	public static native void symvecMult(double alpha, double[] A, double[] x, double beta, double[] y, long[] dim);
	public static native void trivecMult(double[] A, int uplo, double[] bx, long lda, int tranA);
	public static native void rankOneUpdate(double[] A, double[] x, double[] y, long[] dim, long[] offset, long[] inc, long lda, double alpha);

	public static native void trimatMult(double[] A, double[] B, long[] dim, int tranA);
	public static native void mattriMult(double[] B, double[] A, long[] dim, int tranA);
	
	public static native void trivecSolve(double[] A, double[] bx, long[] dim, int tranA);
	public static native void trimatSolve(double[] A, double[] BX, long[] dim, int tranA);
	public static native void mattriSolve(double[] BX, double[] A, long[] dim, int tranA);
}
