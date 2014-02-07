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

package x10.matrix.lapack;

/**
 * Defines routines in X10 calling JNI wrapper for corresponding LAPACK routine
 */
public class WrapLAPACK {
	static { 
		System.loadLibrary("jlapack");
	}

	public static native int solveLinearEquation(double[] A, double[] BX, int[] ipiv, int[] dim);

	public static native int compEigenValue(double[] A, double[] W, double[] WORK, int[] dim);
	public static native int compEigenVector(double[] A, double[] W, double[] WORK, int[] dim);
}
