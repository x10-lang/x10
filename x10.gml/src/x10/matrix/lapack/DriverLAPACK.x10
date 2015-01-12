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

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;
	
/**
 * This class provides a wrapper around the LAPACK library via native calls.
 * All matrices/vectors use double precision and column-major format.
 * @see http://www.netlib.org/lapack/
 */
@NativeCPPInclude("wrap_lapack.h")
@NativeCPPCompilationUnit("wrap_lapack.cc")
protected class DriverLAPACK {
	@Native("java","WrapLAPACK.solveLinearEquation((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getIntArray(),(#4).getIntArray())")
	@Native("c++","solve_linear_equation((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw)")
		public static native def solveLinearEquation(
				A:Rail[Double], 
				BX:Rail[Double], 
				ip:Rail[Int],
				dim:Rail[Int]):Int;

    @Native("java","WrapLAPACK.compEigenvalues((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4).getIntArray(),(#5).getIntArray())")
    @Native("c++","comp_eigenvalues((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw,(#5)->raw)")
    public static native def compEigenvalues(
        A:Rail[Double], 
        W:Rail[Double], 
        WORK:Rail[Double],
        IWORK:Rail[Int],
        dim:Rail[Int]):Int;

    @Native("java","WrapLAPACK.compEigenvectors((#1).getDoubleArray(),(#2).getDoubleArray(),(#3).getDoubleArray(),(#4).getDoubleArray(),(#5).getIntArray(),(#6).getIntArray(),(#7).getIntArray())")
    @Native("c++","comp_eigenvectors((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw,(#5)->raw,(#6)->raw,(#7)->raw)")
    public static native def compEigenvectors(
        A:Rail[Double],
        W:Rail[Double],
        Z:Rail[Double],
        WORK:Rail[Double],
        IWORK:Rail[Int],
        IFAIL:Rail[Int],
        dim:Rail[Int]):Int;
}

// vim:tabstop=4:shiftwidth=4:expandtab
