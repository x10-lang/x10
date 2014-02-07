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

import x10.compiler.NoInline;

public class LAPACK {
	public static @NoInline def solveLinearEquation(A:Rail[Double], B:Rail[Double], ipiv:Rail[Int],
			dim:Rail[Int]):Int {
		return DriverLAPACK.solveLinearEquation(A, B, ipiv, dim);
	}
	
	public static @NoInline def compEigenValue(A:Rail[Double], W:Rail[Double], WORK:Rail[Double],
			dim:Rail[Int]):Int {
		return DriverLAPACK.compEigenValue(A, W, WORK, dim);
	}
	
	public static @NoInline def compEigenVector(A:Rail[Double], W:Rail[Double], WORK:Rail[Double],
			dim:Rail[Int]):Int {
		return DriverLAPACK.compEigenVector(A, W, WORK, dim);
	}
}
