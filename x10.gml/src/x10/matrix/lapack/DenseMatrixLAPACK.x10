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

import x10.util.StringBuilder;

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
	public static def solveLinearEquation(A:DenseMatrix, BX:DenseMatrix(A.N), ipiv:Rail[Int]{self.size==A.N}) {
        Debug.assure(A.N < Int.MAX_VALUE, "32-bit LAPACK only supports matrix dimension < 2^31");
        Debug.assure(BX.N < Int.MAX_VALUE, "32-bit LAPACK only supports matrix dimension < 2^31");
		val info = DriverLAPACK.solveLinearEquation(A.d, BX.d, ipiv, [A.N as Int, BX.N as Int]);

        if (info > 0) throw new LAPACKException(info, "solveLinearEquation");
	}

    public static def solveLinearEquation(A:DenseMatrix, BX:Vector(A.N), ipiv:Rail[Int]{self.size==A.N}) {
        Debug.assure(A.N < Int.MAX_VALUE, "32-bit LAPACK only supports matrix dimension < 2^31");
		val info = DriverLAPACK.solveLinearEquation(A.d, BX.d, ipiv, [A.N as Int, 1n]);

        if (info > 0) throw new LAPACKException(info, "solveLinearEquation");
 	}
/*	
	public static def solveLinearEquation(A:DenseMatrix, BX:DenseMatrix(A.N), ipvec:Vector(A.M)):Int {
		val ip = new Rail[Long](ipvec.M, (i:Long)=>(ipvec.d(i) as Long));
		return DriverLAPACK.solveLinearEquation(A.d, BX.d, ip, [A.N as Int, BX.N as Int]);
	}
*/
	/**
	 * Compute all eigenvalues of the real symmetric matrix A.
     * @param A a real symmetric matrix. Only the upper half is used.
     * @param W on return, the eigenvalues of A in ascending order
     * @param work a work array, size >= 8*A.N
     * @param iwork a work array, size == 5*A.N
	 */
	public static def compEigenvalues(A:DenseMatrix, W:Vector(A.N), work:Rail[Double], iwork:Rail[Int]) {
        Debug.assure(A.N < Int.MAX_VALUE, "32-bit LAPACK only supports matrix dimension < 2^31");
		Debug.assure(work.size >= 8*A.N, "Work space used to compute eigenvalues is too small");
		Debug.assure(iwork.size >= 5*A.N, "Integer work space used to compute eigenvalues is too small");
		val info = DriverLAPACK.compEigenvalues(A.d, W.d, work, iwork, [A.N as Int, work.size as Int]);

        if (info > 0) throw new LAPACKException(info, "compEigenvalues");
	}

	/**
	 * Compute all eigenvalues of the real symmetric matrix A.
     * @param A a real symmetric matrix. Only the upper half is used.
     * @param W on return, the eigenvalues of A in ascending order
	 */
	public static def compEigenvalues(A:DenseMatrix, W:Vector(A.N)) {
        val work = new Rail[Double](8*A.N);
        val iwork = new Rail[Int](5*A.N);
		compEigenvalues(A, W, work, iwork);
	}
	
	/**
	 * Compute all eigenvalues and eigenvectors of the real symmetric matrix A.
     * @param A a real symmetric matrix. Only the upper half is used.
     *   On return, the upper half of A is destroyed.
     * @param W on return, the eigenvalues of A in ascending order
     * @param Z on return, the eigenvectors of A corresponding to eigenvalues
     * @param work a work array, size >= 8*A.N
     * @param iwork a work array, size == 5*A.N
	 */
	public static def compEigenvectors(A:DenseMatrix, W:Vector(A.N), Z:DenseMatrix(A.M,A.N), work:Rail[Double], iwork:Rail[Int]) {
        Debug.assure(A.N < Int.MAX_VALUE, "32-bit LAPACK only supports matrix dimension < 2^31");
		Debug.assure(work.size >= 8*A.N, "Work space used to compute eigenvectors is too small");
		Debug.assure(iwork.size >= 5*A.N, "Integer work space used to compute eigenvectors is too small");
        val ifail = new Rail[Int](A.N);
		val info = DriverLAPACK.compEigenvectors(A.d, W.d, Z.d, work, iwork, ifail, [A.N as Int, work.size as Int]);

        if (info > 0) {
            val convergeMessage = new StringBuilder();
            convergeMessage.add("compEigenvectors: the following eigenvectors failed to converge:");
            for (i in 0..(A.N-1)) {
                if (ifail(i) >= 0) {
                    convergeMessage.add(" ").add(ifail(i));
                }
            }
            
            throw new LAPACKException(info, convergeMessage.toString());
        }
	}

	/**
	 * Compute all eigenvalues and eigenvectors of the real symmetric matrix A.
     * @param A a real symmetric matrix. Only the upper half is used.
     *   On return, the upper half of A is destroyed.
     * @param W on return, the eigenvalues of A in ascending order
     * @param Z on return, the eigenvectors of A corresponding to eigenvalues
	 */
	public static def compEigenvectors(A:DenseMatrix, W:Vector(A.N), Z:DenseMatrix(A.M,A.N)) {
        val work = new Rail[Double](8*A.N);
        val iwork = new Rail[Int](5*A.N);
		compEigenvectors(A, W, Z, work, iwork);
	}
}

// vim:tabstop=4:shiftwidth=4:expandtab
