/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */

package x10.matrix.lapack;

/**
 * JNI wrappers for corresponding functions in wrap_lapack.cc
 */
public class WrapLAPACK {
	static { 
		System.loadLibrary("jlapack");
	}

	public static native int solveLinearEquation(double[] A, double[] BX, int[] ipiv, int[] dim);

	public static native int compEigenvalues(double[] A, double[] W, double[] WORK, int[] IWORK, int[] dim);
	public static native int compEigenvectors(double[] A, double[] W, double[] Z, double[] WORK, int[] IWORK, int[] IFAIL, int[] dim);
}

// vim:tabstop=4:shiftwidth=4:expandtab
