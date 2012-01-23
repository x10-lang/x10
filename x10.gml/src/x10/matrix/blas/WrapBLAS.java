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

	public static native void test();
	public static native void scale(int n,  double alpha, double[] x);
	public static native void copy(int n, double[] x, double[] y);
	public static native double dotProd(int n, double[] x, double[] y);
	public static native double norm2(int n, double[] x);

	public static native double absSum(int n, double[] x);

	public static native void matmatMult(double[] A, double[] B, double[] C, int[] dim, double[] scale, int[] trans);

	public static native void symmatMult(double[] A, double[] B, double[] C, int[] dim, double[] scale);
	public static native void matsymMult(double[] B, double[] A, double[] C, int[] dim, double[] scale);

	public static native void matvecMult(double[] A, double[] x, double[] y, int[] dim, double[] scale, int transA);
	public static native void symvecMult(double[] A, double[] x, double[] y, int[] dim, double[] scale);
	public static native void trivecMult(double[] A, double[] bx, int lda, int tranA);

	public static native void trimatMult(double[] A, double[] B, int[] dim, int tranA);
	public static native void mattriMult(double[] B, double[] A, int[] dim, int tranA);
	
	public static native void trivecSolve(double[] A, double[] bx, int[] dim, int tranA);
	public static native void trimatSolve(double[] A, double[] BX, int[] dim, int tranA);
	public static native void mattriSolve(double[] BX, double[] A, int[] dim, int tranA);

}