/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 *  (C) Copyright Australian National University 2013.
 */

import harness.x10Test;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;

import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.MatrixMultXTen;
import x10.matrix.DenseMultXTen;

/**
 * This class contains test cases for dense matrix multiplication.
 */
public class TestTrans extends x10Test {
	public val M:Long;
	public val N:Long;
	public val K:Long;

    public def this(args:Rail[String]) {
		M = args.size > 0 ?Long.parse(args(0)):50;
		N = args.size > 1 ?Long.parse(args(1)):(M as Int)+1;
		K = args.size > 2 ?Long.parse(args(2)):(M as Int)+2;
	}

    public def run():Boolean {
		var ret:Boolean = true;

 		// BLAS implementation
		ret &= (testMultTransA());
        ret &= (testMatTransMultVector());
		ret &= (testMultTransB());
		ret &= (testMultTransAB());
		// X10 Implementation
		ret &= (testMMTransA());
		ret &= (testMMTransB());
		ret &= (testMMTransAB());

		return ret;
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

		val c = DenseMatrix.make(M, N);
		DenseMultXTen.compTransMult(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		if (!ret)
			Console.OUT.println("----X10 dense driver - transpose A test failed!----");
		return ret;
	}

    public def testMatTransMultVector():Boolean {
		Console.OUT.printf("\nTest X10 Dense, Matrix-Vector, Transpose A, BLAS multiply driver: (%dx%d)^T * (%dx%d)\n",
				K, M, K, N);
		val a = DenseMatrix.make(K, M).initRandom();
		val v = Vector.make(K).initRandom();
		val c1 = Vector.make(M);
		val c2 = Vector.make(M);

		DenseMatrixBLAS.compTransMult(1.0, a, v, 0.0, c1);
		DenseMultXTen.compTransMult(a, v, c2, false);
		
		var ret:Boolean = true;
		if (!c1.equals(c2)) {
			Console.OUT.println("----- c = A^Tv : BLAS != X10 Dense driver -----\n");
			ret = false;
		}

		DenseMatrixBLAS.compTransMult(1.0, a, v, 1.0, c1);
		DenseMultXTen.compTransMult(a, v, c2, true);

		if (!c1.equals(c2)) {
			Console.OUT.println("----- c += A^Tv : BLAS != X10 Dense driver -----\n");
			ret = false;
		}
		
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

		val c= DenseMatrix.make(M, N);
		DenseMultXTen.compMultTrans(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		if (!ret)
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

		DenseMatrixBLAS.compTransMultTrans(1.0, a, b, 0.0, cm);

		val c= DenseMatrix.make(M, N);
		MatrixMultXTen.compTransMultTrans(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		if (!ret)
			Console.OUT.println("----X10 dense driver - transpose AB test failed!----");
		return ret;
	}



	// X10 driver matrix base 
	public def testMMTransA():Boolean {
		Console.OUT.printf("Test X10 matrix driver multiply transpose A. A(%dx%d), B(%dx%d)\n",
						   K, M, K, N);
		val am = DenseMatrix.make(K, M);
		val bm = DenseMatrix.make(K, N);
		am.initRandom();
		bm.initRandom();
		val cm = DenseMatrix.make(M,N); 
		DenseMatrixBLAS.compTransMult(1.0, am, bm, 0.0, cm);
	
		val a  = am as Matrix(K,M);
		val b  = bm as Matrix(K, N);
		val c  = DenseMatrix.make(M,N);
		MatrixMultXTen.compTransMult(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		if (!ret)
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
		DenseMatrixBLAS.compMultTrans(1.0, am, bm, 0.0, cm);

		val a = am as Matrix(M, K);
		val b = bm as Matrix(N,K); 
		val c = DenseMatrix.make(M,N);
		MatrixMultXTen.compMultTrans(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		if (!ret)
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
		DenseMatrixBLAS.compTransMultTrans(1.0, am, bm, 0.0, cm);
		
		val a  = am as Matrix(K,M);
		val b  = bm as Matrix(N,K);
		val c  = DenseMatrix.make(M, N);
		MatrixMultXTen.compTransMultTrans(a, b, c, false);

		val ret = c.equals(cm as Matrix(c.M, c.N));
		if (!ret)
			Console.OUT.println("----X10 matrix driver - transpose AB test failed!----");
		return ret;
	}

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Long.parse(args(0)):50;
		new TestTrans(args).execute();
	}
}
