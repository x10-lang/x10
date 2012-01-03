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

import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.SymMatrix;
import x10.matrix.TriMatrix;

/**
 * This class provides static methods to perform LAPACK routines throug dense
 * matrix and vector:
 * 
 * 1) Solve linear equations
 * 2) Compue eigenvalues
 * 3) Compute eigenvalues and eigenvectors
 * 
 */

public class DenseMatrixLAPACK {

	//===========================================================
	//
	//===========================================================
	/**
	 * Solve linear equations:  A &#42  X = B
	 */	
	public static def solveLinearEquation(A:DenseMatrix, BX:DenseMatrix(A.N), ipiv:Array[Int](1)) : Int {
		return DriverLAPACK.solveLinearEquation(A.d, BX.d, ipiv, [A.N, BX.N]);
	}
	
	public static def solveLinearEquation(A:DenseMatrix, BX:DenseMatrix(A.N), ipvec:Vector(A.M)) : Int {
		val ip:Array[Int](1) = new Array[Int](ipvec.N, (i:Int)=>(ipvec.d(i) as Int));
		return DriverLAPACK.solveLinearEquation(A.d, BX.d, ip, [A.N, BX.N]);
	}
	
	//-------------------------------------------------------------
	/**
	 * Solve matrix A &#42  X = B
	 */
	public static def compEigenValue(A:DenseMatrix, W:Vector(A.N), tmp:Array[Double](1)) : Int {
		Debug.assure(tmp.size >= 3*A.N-1, "Temp space used to compute eigen values is too small");
		return DriverLAPACK.compEigenValue(A.d, W.d, tmp, [A.N, tmp.size]);
	}
	
	public static def compEigenVector(A:DenseMatrix, W:Vector(A.N), tmp:Array[Double](1)) : Int {
		Debug.assure(tmp.size >= 3*A.N-1, "Temp space used to compute eigen values is too small");
		return DriverLAPACK.compEigenVector(A.d, W.d, tmp, [A.N, tmp.size]);
	}

}
