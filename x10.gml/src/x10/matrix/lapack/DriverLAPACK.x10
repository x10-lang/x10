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

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;

@NativeCPPInclude("wrap_lapack.h")
@NativeCPPCompilationUnit("wrap_lapack.c")
	
/**
 *  This class provides LAPACK interface in X10 via native calls.
 *  Only Double based matrix data array is supported in the current version.
 *  All matrix data is stored in column-major arrays. 
 *  All methods declared here have corresponding specification defined by
 *  BLAS, please refer to BLAS specification for detailed information.
 *  
 */
protected class DriverLAPACK {

	
	/**
	 * Solve linear equations A * X = B
	 *
	 *
	 */
	@Native("java","WrapLAPACK.solveLinearEquation((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getIntArray(),(#4).raw().getIntArray())")
	@Native("c++","solve_linear_equation((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw())")
		public static native def solveLinearEquation(
				A:Array[Double](1), 
				BX:Array[Double](1), 
				ip:Array[Int](1),
				dim:Array[Int](1)):Int;
	//int solve_linear_equation(double* A, double* B, int* IPIV, int* dim)

	//========================================================================

	/**
	 * Compute eigenvalues
	 *
	 */
	@Native("java","WrapLAPACK.compEigenValue((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getDoubleArray(),(#4).raw().getIntArray())")
	@Native("c++","comp_eigenvalue((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw())")
		public static native def compEigenValue(
				A:Array[Double](1), 
				W:Array[Double](1), 
				WORK:Array[Double](1),
				dim:Array[Int](1)):Int;

	/**
	 * Compute eigenvalues
	 * 
	 */
	@Native("java","WrapLAPACK.compEigenVector((#1).raw().getDoubleArray(),(#2).raw().getDoubleArray(),(#3).raw().getDoubleArray(),(#4).raw().getIntArray())")
	@Native("c++","comp_eigenvector((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw())")
	public static native def compEigenVector(
			A:Array[Double](1), 
			W:Array[Double](1), 
			WORK:Array[Double](1),
			dim:Array[Int](1)):Int;

}