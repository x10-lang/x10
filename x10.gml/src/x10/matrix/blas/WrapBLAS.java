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

	public static native void matmatMultLd(double[] A, double[] B, double[] C, long[] dim, long[] ld, double[] scale, int[] trans);
	public static native void matmatMult(double[] A, double[] B, double[] C, long[] dim, double[] scale, int[] trans);

	public static native void symmatMult(double[] A, double[] B, double[] C, long[] dim, double[] scale);
	public static native void matsymMult(double[] B, double[] A, double[] C, long[] dim, double[] scale);

	public static native void matvecMult(double[] A, double[] x, double[] y, long[] dim, double[] scale, int transA);
	public static native void symvecMult(double[] A, double[] x, double[] y, long[] dim, double[] scale);
	public static native void trivecMult(double[] A, int uplo, double[] bx, long lda, int tranA);
	public static native void rankOneUpdate(double[] A, double[] x, double[] y, long[] dim, long[] offset, long[] inc, long lda, double alpha);

	public static native void trimatMult(double[] A, double[] B, long[] dim, int tranA);
	public static native void mattriMult(double[] B, double[] A, long[] dim, int tranA);
	
	public static native void trivecSolve(double[] A, double[] bx, long[] dim, int tranA);
	public static native void trimatSolve(double[] A, double[] BX, long[] dim, int tranA);
	public static native void mattriSolve(double[] BX, double[] A, long[] dim, int tranA);

}
