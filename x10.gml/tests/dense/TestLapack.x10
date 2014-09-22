/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

import harness.x10Test;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
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
		ret &= (testEigenValue());
		ret &= (testEigenVector());

		return ret;
	}
	
	public def testLinearEquation():Boolean {
		Console.OUT.printf("Dense matrix (%dx%d) * (%dx%d) = (%dx%d) LAPACK linear equation test\n",
						    M, M, M, N, M, N);
		val A = DenseMatrix.make(M,M).initRandom();
		val X = DenseMatrix.make(M,N).init((i:Long)=>(1.0+i));
		val B = A % X;
		
		val ipv = new Rail[Int](M);
		val info = DenseMatrixLAPACK.solveLinearEquation(A, B, ipv);
		Debug.assure(info==0n, "Linear equation solve failed. Exit info code:"+info);
				
		val ret = B.equals(X);
		if (!ret)
			Console.OUT.println("--------LAPACK linear equation test failed!--------");
		return ret;
	}
	
	public def testEigenValue():Boolean {
		Console.OUT.printf("LAPACK matrix (%dx%d) eigen value test\n", M, M);		
		
		val A = DenseMatrix.make(M,M).initRandom();
		val tmp = new Rail[Double](4*M);
		val egv = Vector.make(M);
		
		val info = DenseMatrixLAPACK.compEigenValue(A, egv, tmp);
		Debug.assure(info==0n, "Compute eigen value failed. Exit info code:"+info);
		
		val ret = (info==0n);
		if (!ret)
			Console.OUT.println("------- X10 dense LAPACK compute eigen value test failed! -------");
		return ret;		
	}
	
	public def testEigenVector():Boolean {
		Console.OUT.printf("LAPACK matrix (%dx%d) compute eigen vector test\n", M, M);		
		
		val A = DenseMatrix.make(M,M).initRandom();
		val tmp = new Rail[Double](4*M);
		val egv = Vector.make(M);
		val info = DenseMatrixLAPACK.compEigenVector(A, egv, tmp);
		Debug.assure(info==0n, "Compute eigen vector failed. Exit info code:"+info);
		
		val ret = (info==0n);
		if (!ret)
			Console.OUT.println("------- LAPACK compute eigen vector test failed! -------");
		return ret;		
	}

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Long.parse(args(0)):4;
		val n = (args.size > 1) ? Long.parse(args(1)):1;
		new TestLapack(m, n).execute();
	}
}
