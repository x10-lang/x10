/*
 *  This file is part of the X10 Applications project.
 * 
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.MatrixMultXTen;
import x10.matrix.DenseMultXTen;
import x10.matrix.VerifyTools;

/**
 * This class contains test cases for dense matrix multiplication.
 * <p>
 * 
 * <p>
 */

public class TestMultDense{
	public static def main(args:Rail[String]) {
		val testcase = new MultiplyTest(args);
		testcase.run();
	}
	static 
	class MultiplyTest {
		
		public val M:Int;
		public val N:Int;
		public val K:Int;
		
		public def this(args:Rail[String]) {
			M = args.size > 0 ?Int.parse(args(0)):50;
			N = args.size > 1 ?Int.parse(args(1)):M+1;
			K = args.size > 2 ?Int.parse(args(2)):M+2;
			
		}
		
		public def run(): void {
			var ret:Boolean = true;
			// Set the matrix function
			ret &= (testMultAssociative());
			ret &= (testAddMultDist());
			ret &= (testSubMultDist());
			ret &= (testMatrixMult());
			ret &= (testDenseMult());
			ret &= (testBlasMult());
			ret &= (testMultDrivers());
			//ret &= (mm.testSmallMult());
			
			if (ret)
				Console.OUT.println("Dense matrix multiply Test passed!");
			else
				Console.OUT.println("--------Dense matrix multiply Test failed!--------");
		}
		//------------------------------------------------
		//------------------------------------------------
		public def testMultAssociative():Boolean {
			Console.OUT.printf("\nTest Dense matrix multiply associative: %dx%d * %dx%d * %dx%d\n",
					M, K, K, N, N, M);
			val a = DenseMatrix.make(M, K);
			val b = DenseMatrix.make(K, N);
			val c = DenseMatrix.make(N, M);
			a.initRandom(); 
			b.initRandom(); 
			c.initRandom();

			val d = (a % b) % c;
			val d1= a % (b % c);
			
			val ret = d1.equals(d);
			Console.OUT.printf("Result matrix: %dx%d\n", d.M, d.N);
			//dc.print("Dense a*b=\n");
			if (ret)
				Console.OUT.println("Dense matrix multiply associative test passed!");
			else
				Console.OUT.println("-----Dense matrix multiply associative test failed!-----");
			return ret;
		}
		public def testAddMultDist():Boolean {
			Console.OUT.println("\nTest Dense matrix (a+b)*c = a*c+b*c ");
			val a = DenseMatrix.make(M, K);
			val b = DenseMatrix.make(M, K);
			val c = DenseMatrix.make(K, N);
			a.initRandom(); b.initRandom(); c.initRandom();

			val d = (a + b) % c;
			val d1= a % c + b % c;
			val ret = d1.equals(d);
			//dc.print("Dense a*b=\n");
			if (ret)
				Console.OUT.println("Dense matrix (a+b)*c = a*c+b*c test passed!");
			else
				Console.OUT.println("-----Dense matrix (a+b)*c = a*c+b*c test failed!-----");
			return ret;
		}
		//
		public def testSubMultDist():Boolean {
			Console.OUT.println("\nTest (a-b)*c = a*c-b*c ");
			val a = DenseMatrix.make(M, K);
			val b = DenseMatrix.make(M, K);
			val c = DenseMatrix.make(K, N);
			a.initRandom(); b.initRandom(); c.initRandom();

			val d = (a - b) % c;
			val d1= a % c - b % c;
			
			val ret = d1.equals(d);
			if (ret)
				Console.OUT.println("Dense matrix (a-b)*c = a*c-b*c test passed!");
			else
				Console.OUT.println("-----Dense matrix (a-b)*c = a*c-b*c test failed!-----");
			return ret;
		}
		
		public def testMatrixMult():Boolean {
			Console.OUT.printf("\nTest X10 matrix multiply driver: (%dx%d) * (%dx%d)\n",
					M, K, K, N);
			val a = DenseMatrix.make(M, K);
			val b = DenseMatrix.make(K, N);
			a.initRandom(); b.initRandom();
	   
			val c = MatrixMultXTen.comp(a as Matrix(M,K), b as Matrix(K,N));
			
			Console.OUT.printf("Result matrix: %dx%d\n", c.M, c.N);
			
			val ret = VerifyTools.verifyMatMult(a, b, c);
			if (ret)
				Console.OUT.println("Matrix base X10 multiply driver test passed!");
			else
				Console.OUT.println("-----Matrix base X10 multply driver test failed!-----");
			return ret;
		}
		
		
		public def testDenseMult():Boolean {
			Console.OUT.printf("\nTest X10 dense multiplication driver: (%dx%d) * (%dx%d)\n",
					M, K, K, N);
			val a:DenseMatrix(M,K) = DenseMatrix.makeRand(M, K);
			val b:DenseMatrix(K,N) = DenseMatrix.makeRand(K, N);
			val c:DenseMatrix(a.M,b.N) = DenseMultXTen.comp(a, b);
			
			Console.OUT.printf("Result matrix: %dx%d\n", c.M, c.N);
			val ret = VerifyTools.verifyMatMult(a, b, c);
			
			if (ret)
				Console.OUT.println("Dense matrix X10 multiply driver test passed!");
			else
				Console.OUT.println("-----Dense matrix X10 multiply driver test failed!-----");
			return ret;
		}
		
		public def testBlasMult():Boolean {
			Console.OUT.printf("\nTest BLAS multiplication driver: (%dx%d) * (%dx%d)\n",
					M, K, K, N);
			val a = DenseMatrix.make(M, K);
			val b = DenseMatrix.make(K, N);
			a.initRandom(); b.initRandom();

			val c = DenseMatrixBLAS.comp(a, b);
			
			Console.OUT.printf("Result matrix: %dx%d\n", c.M, c.N);
			
			val ret = VerifyTools.verifyMatMult(a, b, c);
			if (ret)
				Console.OUT.println("Dense matrix BLAS multiply driver test passed!");
			else
				Console.OUT.println("-----Dense matrix BLAS multiply driver test failed!-----");
			return ret;
		}
		
		
		public def testMultDrivers():Boolean {
			Console.OUT.printf("\nTest X10 Dense, Matrix, BLAS multiply driver: (%dx%d) * (%dx%d)\n",
					M, K, K, N);
			val a = DenseMatrix.make(M, K);
			val b = DenseMatrix.make(K, N);
			a.initRandom(); b.initRandom();

			val c1 = DenseMatrixBLAS.comp(a, b);
			val c2 = DenseMultXTen.comp(a, b);
			val c3 = MatrixMultXTen.comp(a as Matrix(M,K), b as Matrix(K,N));
			
			var ret:Boolean = true;
			if (!c1.equals(c2 as Matrix(c1.M,c1.N))) {
				Console.OUT.println("----- BLAS <> X10 Dense driver -----\n");
				ret = false;
			}
			if (!c1.equals(c3 as Matrix(c1.M,c1.N))) {
				Console.OUT.println("----- BLAS <> X10 Matrix driver -----\n");
				ret = false;
			}
			
			if (ret)
				Console.OUT.println("BLAS == X10 Matrix == X10 Dense driver\n");
			
			return ret;
		}
		
		
		// 	public def testSmallMult():Boolean {
		// 		Console.OUT.printf("\nTest X10 MM driver on small numbers: (%dx%d) * (%dx%d)\n",
		// 						   M, K, K, N);
		// 		val a = DenseMatrix.makeSmall(M, K);
		// 		val b = DenseMatrix.makeSmall(K, N);
		// 		val c = DenseMatrixBLAS.comp(a, b);
		// 		Console.OUT.printf("Result matrix: %dx%d\n", c.M, c.N);
		// 		val cd= MatrixMultXTen.comp(a, b);
		
		// 		var ret:Boolean = true;
		// 		if (! c.testSame(cd)) {
		// 			ret = false;
		// 			Console.OUT.println("-----Test X10 dense multiply driver on small number test failed!-----\n");
		// 		}
		
		// 		val cm= MatrixMultXTen.comp(a as Matrix, b as Matrix);
		// 		if( !c.testSame(cm) ) {
		// 			Console.OUT.println("-----Test X10 matrix multiply driver on small number test failed!-----\n");
		// 			ret = false;
		// 		}
		
		// 		if (ret) 
		// 			Console.OUT.println("Test X10 matrix multiply driver on small number test passed!\n");
		// 		return ret;
		// 	}
	}
}
