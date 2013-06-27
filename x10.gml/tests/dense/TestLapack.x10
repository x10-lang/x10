/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.lapack.DenseMatrixLAPACK;

/**
 * This class contains test cases for LAPACK wrapper routines.
 */
public class TestLapack {

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Int.parse(args(0)):4;
		val n = (args.size > 1) ? Int.parse(args(1)):1;
		val testcase = new LapackTest(m, n);
		testcase.run();
	}
}

class LapackTest {
	public val M:Long;
	public val N:Long;

    public def this(m:Long, n:Long) {
    	M = m; N=n;
	}

    public def run(): void {
		var ret:Boolean = true;

 		// BLAS implementation
		ret &= (testLinearEquation());
		ret &= (testEigenValue());
		ret &= (testEigenVector());

		if (ret)
			Console.OUT.println("Dense matrix LAPACK Test passed!");
		else
			Console.OUT.println("----Dense matrix LAPACK Test failed!----");
	}
	
	public def testLinearEquation():Boolean {
		Console.OUT.printf("Start dense matrix (%dx%d) * (%dx%d) = (%dx%d) LAPACK linear equation test\n",
						    M, M, M, N, M, N);
		val A = DenseMatrix.make(M,M).initRandom();
		val X = DenseMatrix.make(M,N).init((i:Long)=>(1.0+i));
		val B = A % X;
		
		val ipv = new Rail[Int](M);
		val info = DenseMatrixLAPACK.solveLinearEquation(A, B, ipv);
		Debug.assure(info==0, "Linear equation solve failed. Exit info code:"+info);
				
		val ret = B.equals(X);
		if (ret)
			Console.OUT.println("LAPACK linear equation test passed!");
		else
			Console.OUT.println("--------LAPACK linear equation test failed!--------");
		return ret;
	}
	
	public def testEigenValue():Boolean {
		Console.OUT.printf("Start LAPACK matrix (%dx%d) eigen value test\n",
				M, M);		
		
		val A = DenseMatrix.make(M,M).initRandom();
		val tmp = new Rail[Double](4*M);
		val egv = Vector.make(M);
		
		val info = DenseMatrixLAPACK.compEigenValue(A, egv, tmp);
		Debug.assure(info==0, "Compute eigen value failed. Exit info code:"+info);
		
		val ret = (info==0);
		if (ret)
			Console.OUT.println("X10 dense LAPACK compute eigen value test passed!");
		else
			Console.OUT.println("------- X10 dense LAPACK compute eigen value test failed! -------");
		return ret;		
	}
	
	public def testEigenVector():Boolean {
		Console.OUT.printf("Start LAPACK matrix (%dx%d) compute eigen vector test\n",
				M, M);		
		
		val A = DenseMatrix.make(M,M).initRandom();
		val tmp = new Rail[Double](4*M);
		val egv = Vector.make(M);
		val info = DenseMatrixLAPACK.compEigenVector(A, egv, tmp);
		Debug.assure(info==0, "Compute eigen vector failed. Exit info code:"+info);
		
		val ret = (info==0);
		if (ret)
			Console.OUT.println("LAPACK compute eigen vector test passed!");
		else
			Console.OUT.println("------- LAPACK compute eigen vector test failed! -------");
		return ret;		
	}
}
