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

import x10.io.Console;

//import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.blas.DenseMultBLAS;
import x10.matrix.MatrixMultXTen;
import x10.matrix.DenseMultXTen;

/**
   This class contains test cases for dense matrix multiplication.
   <p>

   <p>
 */
public class TestTrans {

    public static def main(args:Array[String](1)) {
		val m = (args.size > 0) ? Int.parse(args(0)):50;
		val testcase = new TransMultTest(args);
		testcase.run();
	}
}

class TransMultTest {

	public val M:Int;
	public val N:Int;
	public val K:Int;

    public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):50;
		N = args.size > 1 ?Int.parse(args(1)):M+1;
		K = args.size > 2 ?Int.parse(args(2)):M+2;

	}

    public def run(): void {
		var ret:Boolean = true;

 		// BLAS implementation
		ret &= (testMultTransA());
		ret &= (testMultTransB());
		ret &= (testMultTransAB());
		// X10 Implementation
		ret &= (testMMTransA());
		ret &= (testMMTransB());
		ret &= (testMMTransAB());

		if (ret)
			Console.OUT.println("Dense matrix multiply transpose Test passed!");
		else
			Console.OUT.println("----Dense matrix multiply transpose Test failed!----");
	}
	
	public def testMultTransA():Boolean {
		Console.OUT.printf("Test X10 dense matrix driver multiply transpose A. A(%dx%d), B(%dx%d)\n",
						   K, M, K, N);
		val a = DenseMatrix.make(K, M);
		val b = DenseMatrix.make(K, N);
		a.initRandom();
		b.initRandom();

		val cm = DenseMatrix.make(M, N);
		cm.transMult(a, b, false);
		//DenseMultBLAS.compTransMult(a, b, cm, false); //c.print("Result a.T * b\n");

		val c = DenseMatrix.make(M, N);
		DenseMultXTen.compTransMult(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		Console.OUT.printf("Result matrix: %dx%d\n", c.M, c.N);
		//dc.print("Dense a*b=\n");
		if (ret)
			Console.OUT.println("X10 dense driver - transpose A test passed!");
		else
			Console.OUT.println("----X10 dense driver - transpose A test failed!----");
		return ret;
	}

	public def testMultTransB():Boolean {
		Console.OUT.printf("Test X10 driver dense matrix multiply transpose B. A(%dx%d), B(%dx%d)\n",
						   M, K, N, K);
		val a = DenseMatrix.make(M, K);
		val b = DenseMatrix.make(N, K);
		a.initRandom();
		b.initRandom();

		val cm = DenseMatrix.make(M, N);
		cm.multTrans(a, b, false);
		//DenseMultBLAS.compMultTrans(a, b, cm, false);
		//val c = a * b.T();

		val c= DenseMatrix.make(M, N);
		DenseMultXTen.compMultTrans(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		Console.OUT.printf("Result matrix: %dx%d\n", c.M, c.N);
		//dc.print("Dense a*b=\n");
		if (ret)
			Console.OUT.println("X10 dense driver - transpose B test passed!");
		else
			Console.OUT.println("----X10 dense driver - transpose B test failed!----");
		return ret;
	}

	public def testMultTransAB():Boolean {
		Console.OUT.printf("Test X10 driver dense matrix multiply transpose B. A(%dx%d), B(%dx%d)\n",
						   K, M, N, K);
		val a = DenseMatrix.make(K, M);
		val b = DenseMatrix.make(N, K);
		a.initRandom();
		b.initRandom();
		val cm = DenseMatrix.make(M, N);

		DenseMultBLAS.compTransMultTrans(a, b, cm, false); //val c = a.T() * b.T();
		//
		val c= DenseMatrix.make(M, N);
		MatrixMultXTen.compTransMultTrans(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		Console.OUT.printf("Result matrix: %dx%d\n", c.M, c.N);
		//dc.print("Dense a*b=\n");
		if (ret)
			Console.OUT.println("X10 dense driver - transpose AB test passed!");
		else
			Console.OUT.println("----X10 dense driver - transpose AB test failed!----");
		return ret;
	}


	//----------------------------------------------
	// X10 driver matrix base 
	public def testMMTransA():Boolean {
		Console.OUT.printf("Test X10 matrix driver multiply transpose A. A(%dx%d), B(%dx%d)\n",
						   K, M, K, N);
		val am = DenseMatrix.make(K, M);
		val bm = DenseMatrix.make(K, N);
		am.initRandom();
		bm.initRandom();
		val cm = DenseMatrix.make(M,N); 
		DenseMultBLAS.compTransMult(am, bm, cm, false);
		//c=a.T() * b;
		//c.print("Result a.T * b\n");
	
		val a  = am as Matrix(K,M);
		val b  = bm as Matrix(K, N);
		val c  = DenseMatrix.make(M,N);
		MatrixMultXTen.compTransMult(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		Console.OUT.printf("Result matrix: %dx%d\n", c.M, c.N);
		//dc.print("Dense a*b=\n");
		if (ret)
			Console.OUT.println("X10 matrix driver - transpose A test passed!");
		else
			Console.OUT.println("----X10 matrix driver - transpose A test failed!----");
		return ret;
	}

	public def testMMTransB():Boolean {
		Console.OUT.printf("Test X10 driver matrix multiply transpose B. A(%dx%d), B(%dx%d)\n",
						   M, K, N, K);

		val am = DenseMatrix.make(M, K);
		val bm = DenseMatrix.make(N, K);
		am.initRandom();
		bm.initRandom();

		val cm = DenseMatrix.make(M,N);
		DenseMultBLAS.compMultTrans(am, bm, cm, false);//val c = a * b.T();

		val a = am as Matrix(M, K);
		val b = bm as Matrix(N,K); 
		val c = DenseMatrix.make(M,N);
		MatrixMultXTen.compMultTrans(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		Console.OUT.printf("Result matrix: %dx%d\n", c.M, c.N);
		//dc.print("Dense a*b=\n");
		if (ret)
			Console.OUT.println("X10 matrix driver - transpose B test passed!");
		else
			Console.OUT.println("----X10 matrix driver - transpose B test failed!----");
		return ret;
	}

	public def testMMTransAB():Boolean {
		Console.OUT.printf("Test X10 driver matrix multiply transpose B. A(%dx%d), B(%dx%d)\n",
						   K, M, N, K);
		val am = DenseMatrix.make(K, M);
		val bm = DenseMatrix.make(N, K);
		am.initRandom();
		bm.initRandom();
		val cm = DenseMatrix.make(M,N);
		DenseMultBLAS.compTransMultTrans(am, bm, cm, false); //val c = a.T() * b.T();
		
		val a  = am as Matrix(K,M);
		val b  = bm as Matrix(N,K);
		val c  = DenseMatrix.make(M, N);
		MatrixMultXTen.compTransMultTrans(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		Console.OUT.printf("Result matrix: %dx%d\n", c.M, c.N);
		//dc.print("Dense a*b=\n");
		if (ret)
			Console.OUT.println("X10 matrix driver - transpose AB test passed!");
		else
			Console.OUT.println("----X10 matrix driver - transpose AB test failed!----");
		return ret;
	}

}