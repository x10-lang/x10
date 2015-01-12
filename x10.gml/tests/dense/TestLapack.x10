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

import harness.x10Test;

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.lapack.DenseMatrixLAPACK;

/**
 * This class contains test cases for LAPACK wrapper routines.
 */
public class TestLapack extends x10Test {
	public val M:Long;
	public val N:Long;

    public def this(m:Long, n:Long) {
    	M = m; N=n;
	}

    public def run():Boolean {
		var ret:Boolean = true;

		ret &= (testLinearEquation());
		ret &= (testEigenvalues());
		ret &= (testEigenvectors());

		return ret;
	}
	
	public def testLinearEquation():Boolean {
		Console.OUT.printf("LAPACK linear equation test (%dx%d) * (%dx%d) = (%dx%d)\n",
						    M, M, M, N, M, N);
		val A = DenseMatrix.make(M,M).initRandom();
		val X = DenseMatrix.make(M,N).init((i:Long)=>(1.0+i));
		val B = A % X;
		
		val ipv = new Rail[Int](M);
		DenseMatrixLAPACK.solveLinearEquation(A, B, ipv);
        // solveLinearEquation will throw exception in case of failure
				
		val ret = B.equals(X);
		if (!ret)
			Console.OUT.println("--------LAPACK linear equation test failed!--------");
		return ret;
	}
	
	public def testEigenvalues():Boolean {
		Console.OUT.printf("LAPACK eigenvalue test (%dx%d)\n", M, M);		
		
		val A = DenseMatrix.make(M,M).initRandom();
		val W = Vector.make(M);
		
		DenseMatrixLAPACK.compEigenvalues(A, W);
        // compEigenvalues will throw exception in case of failure

		return true;		
	}
	
	public def testEigenvectors():Boolean {
		Console.OUT.printf("LAPACK eigenvector test (%dx%d)\n", M, M);		
		
		val A = DenseMatrix.make(M,M).initRandom();
		val W = Vector.make(M);
        val Z = DenseMatrix.make(M,M);

		DenseMatrixLAPACK.compEigenvectors(A, W, Z);
        // compEigenvectors will throw exception in case of failure

		return true;		
	}

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Long.parse(args(0)):10;
		val n = (args.size > 1) ? Long.parse(args(1)):2;
		new TestLapack(m, n).execute();
	}
}
