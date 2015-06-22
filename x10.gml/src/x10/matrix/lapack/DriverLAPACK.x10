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
import x10.matrix.ElemType;	
/**
 * This class provides a wrapper around the LAPACK library via native calls.
 * All matrices/vectors use double precision and column-major format.
 * @see http://www.netlib.org/lapack/
 */
@NativeCPPInclude("wrap_lapack.h")
@NativeCPPCompilationUnit("wrap_lapack.cc")
protected class DriverLAPACK {
    @Native("java","WrapLAPACK.solveLinearEquationET(#A, #BX, (#ip).getIntArray(), (#dim).getIntArray())")
    @Native("c++","solve_linear_equation((#A)->raw,(#BX)->raw,(#ip)->raw,(#dim)->raw)")
	public static native def solveLinearEquation[T](
	     A:Rail[T], 
	     BX:Rail[T], 
	     ip:Rail[Int],
	     dim:Rail[Int]):Int;

    @Native("java","WrapLAPACK.compEigenvaluesET(#A, #W, #WORK,(#IWORK).getIntArray(), (#dim).getIntArray())")
    @Native("c++","comp_eigenvalues((#A)->raw,(#W)->raw,(#WORK)->raw,(#IWORK)->raw,(#dim)->raw)")
    public static native def compEigenvalues[T](
        A:Rail[T], 
        W:Rail[T], 
        WORK:Rail[T],
        IWORK:Rail[Int],
        dim:Rail[Int]):Int;

    @Native("java","WrapLAPACK.compEigenvectorsET(#A, #W, #Z, #WORK, (#IWORK).getIntArray(), (#IFAIL).getIntArray(), (#dim).getIntArray())")
    @Native("c++","comp_eigenvectors((#A)->raw,(#W)->raw,(#Z)->raw,(#WORK)->raw,(#IWORK)->raw,(#IFAIL)->raw,(#dim)->raw)")
    public static native def compEigenvectors[T](
        A:Rail[T],
        W:Rail[T],
        Z:Rail[T],
        WORK:Rail[T],
        IWORK:Rail[Int],
        IFAIL:Rail[Int],
        dim:Rail[Int]):Int;
}

// vim:tabstop=4:shiftwidth=4:expandtab
