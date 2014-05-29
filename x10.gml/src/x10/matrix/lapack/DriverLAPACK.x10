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

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;
	
/**
 *  This class provides LAPACK interface in X10 via native calls.
 *  Only Double based matrix data array is supported in the current version.
 *  All matrix data is stored in column-major arrays. 
 *  All methods declared here have corresponding specification defined by
 *  BLAS, please refer to BLAS specification for detailed information.
 */
@NativeCPPInclude("wrap_lapack.h")
@NativeCPPCompilationUnit("wrap_lapack.cc")
protected class DriverLAPACK {
	/**
	 * Solve linear equations A * X = B
	 */
	@Native("java","WrapLAPACK.solveLinearEquation((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getIntArray(),(#4).getIntArray())")
	@Native("c++","solve_linear_equation((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw)")
		public static native def solveLinearEquation(
				A:Rail[Double], 
				BX:Rail[Double], 
				ip:Rail[Int],
				dim:Rail[Int]):Int;
	//int solve_linear_equation(double* A, double* B, int* IPIV, int* dim)

	/**
	 * Compute eigenvalues
	 */
	@Native("java","WrapLAPACK.compEigenValue((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4).getIntArray())")
	@Native("c++","comp_eigenvalue((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw)")
		public static native def compEigenValue(
				A:Rail[Double], 
				W:Rail[Double], 
				WORK:Rail[Double],
				dim:Rail[Int]):Int;

	/**
	 * Compute eigenvalues
	 */
	@Native("java","WrapLAPACK.compEigenVector((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4).getIntArray())")
	@Native("c++","comp_eigenvector((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw)")
	public static native def compEigenVector(
			A:Rail[Double], 
			W:Rail[Double], 
			WORK:Rail[Double],
			dim:Rail[Int]):Int;
}
