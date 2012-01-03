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

package x10.matrix.lapack;

import x10.compiler.NoInline;

/**
 */
public class LAPACK {

	public static @NoInline def solveLinearEquation(A:Array[Double](1), B:Array[Double](1), ipiv:Array[Int](1),
			dim:Array[Int](1)):Int {
		return DriverLAPACK.solveLinearEquation(A, B, ipiv, dim);
	}
	
	public static @NoInline def compEigenValue(A:Array[Double](1), W:Array[Double](1), WORK:Array[Double](1),
			dim:Array[Int](1)):Int {
		return DriverLAPACK.compEigenValue(A, W, WORK, dim);
	}
	
	public static @NoInline def compEigenVector(A:Array[Double](1), W:Array[Double](1), WORK:Array[Double](1),
			dim:Array[Int](1)):Int {
		return DriverLAPACK.compEigenVector(A, W, WORK, dim);
	}
	
	
}