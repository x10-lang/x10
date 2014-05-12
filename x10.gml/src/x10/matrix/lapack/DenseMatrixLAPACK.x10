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

import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;

/**
 * This class provides static methods to perform LAPACK routines using dense
 * matrix and vector:
 * 
 * 1) Solve linear equations
 * 2) Compue eigenvalues
 * 3) Compute eigenvalues and eigenvectors
 */
public class DenseMatrixLAPACK {
	/**
	 * Solve linear equations:  A &#42  X = B
     *
     * @param matrix A(MxN)
     * @param matrix B(LDBxNRHS); on exit, contains matrix X(N*NRHS)
     * @param permutation vector; on exit, contains pivot indices defining permutation matrix P
	 */	
	public static def solveLinearEquation(A:DenseMatrix, BX:DenseMatrix(A.N), ipiv:Rail[Int]{self.size==A.N}):Int {
        Debug.assure(A.N < Int.MAX_VALUE, "32-bit LAPACK only supports matrix dimension < 2^31");
        Debug.assure(BX.N < Int.MAX_VALUE, "32-bit LAPACK only supports matrix dimension < 2^31");
		return DriverLAPACK.solveLinearEquation(A.d, BX.d, ipiv, [A.N as Int, BX.N as Int]);
	}

    public static def solveLinearEquation(A:DenseMatrix, BX:Vector(A.N), ipiv:Rail[Int]{self.size==A.N}):Int {
        Debug.assure(A.N < Int.MAX_VALUE, "32-bit LAPACK only supports matrix dimension < 2^31");
		return DriverLAPACK.solveLinearEquation(A.d, BX.d, ipiv, [A.N as Int, 1n]);
 	}
/*	
	public static def solveLinearEquation(A:DenseMatrix, BX:DenseMatrix(A.N), ipvec:Vector(A.M)):Int {
		val ip = new Rail[Long](ipvec.M, (i:Long)=>(ipvec.d(i) as Long));
		return DriverLAPACK.solveLinearEquation(A.d, BX.d, ip, [A.N as Int, BX.N as Int]);
	}
*/
	/**
	 * Solve matrix A &#42  X = B
	 */
	public static def compEigenValue(A:DenseMatrix, W:Vector(A.N), tmp:Rail[Double]):Int {
        Debug.assure(A.N < Int.MAX_VALUE, "32-bit LAPACK only supports matrix dimension < 2^31");
		Debug.assure(tmp.size >= 3*A.N-1, "Temp space used to compute eigen values is too small");
		return DriverLAPACK.compEigenValue(A.d, W.d, tmp, [A.N as Int, tmp.size as Int]);
	}
	
	public static def compEigenVector(A:DenseMatrix, W:Vector(A.N), tmp:Rail[Double]):Int {
        Debug.assure(A.N < Int.MAX_VALUE, "32-bit LAPACK only supports matrix dimension < 2^31");
		Debug.assure(tmp.size >= 3*A.N-1, "Temp space used to compute eigen values is too small");
		return DriverLAPACK.compEigenVector(A.d, W.d, tmp, [A.N as Int, tmp.size as Int]);
	}
}
