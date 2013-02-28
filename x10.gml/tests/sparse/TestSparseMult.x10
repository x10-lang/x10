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

import x10.matrix.Debug;
import x10.matrix.DenseMatrix;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.SparseCSR;
import x10.matrix.sparse.SparseMultSparseToDense;
import x10.matrix.sparse.SparseMultDenseToDense;
import x10.matrix.sparse.DenseMultSparseToDense;

/**
   <p>

   <p>
 */
public class TestSparseMult{

    public static def main(args:Rail[String]) {
		val testcase = new SparseMult(args);
		testcase.run();
	}
}

class SparseMult {

	public val density:Double;
	public val M:Int;
	public val N:Int;
	public val K:Int;

    public def this(args:Rail[String]) {
		M = args.size > 0 ?Int.parse(args(0)):50;
		density = args.size > 1 ?Double.parse(args(1)):0.5;
		N = args.size > 2 ?Int.parse(args(2)):M+1;
		K = args.size > 3 ?Int.parse(args(3)):M+2;	
	}

	public def run(): void {
		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testMultCC());
 		ret &= (testMultCR());
 		ret &= (testMultCD());

 		ret &= (testMultRR());
 		ret &= (testMultRC());
 		ret &= (testMultRD());

 		ret &= (testMultDC());
 		ret &= (testMultDR());

		if (ret)
			Console.OUT.println("CSC Multiply Test passed!");
		else
			Console.OUT.println("------------------CSC Multiply Test failed!------------------");
	}
	
	public def testMultCC():Boolean {
		Console.OUT.println("Test CSC * CSC -> CSC");
		val a = SparseCSC.make(M, K, density);
		val b = SparseCSC.make(K, N, density);
		a.initRandom(density); b.initRandom(density);
		//a.print();
		//b.print();
		val c = a % b;
		//c.print();
		//c.printSparse("Sparse a*b=\n");
		val da= a.toDense();
		//da.print();
		val db= b.toDense();
		//db.print();
		val dc= da % db;
		//dc.print();
		val ret = dc.equals(c);
		//dc.print("Dense a*b=\n");
		if (ret)
			Console.OUT.println("CSC * CSC test passed!");
		else
			Console.OUT.println("---------CSC * CSC test failed!---------");
		return ret;
	}

	public def testMultCR():Boolean {
		Console.OUT.println("Test CSC * CSR -> Dense");
		val a = SparseCSC.make(M, K, density);
		val b = SparseCSR.make(K, N, density);
		a.initRandom(density); b.initRandom(density);
		val c = a % b;
		//c.printSparse("Sparse a*b=\n");
		val da= a.toDense();
		//da.print("Dense a=\n");
		val db= b.toDense();
		//db.print("Dense b=\n");
		val dc= da % db;
		val ret = dc.equals(c);
		//dc.print("Dense a*b=\n");
		if (ret)
			Console.OUT.println("CSC * CSR test passed!");
		else
			Console.OUT.println("---------CSC * CSR test failed!---------");
		return ret;
	}

	public def testMultCD():Boolean {
		Console.OUT.println("Test CSC * Dense -> Dense");
		val a = SparseCSC.make(M, K, density);
		val b = DenseMatrix.make(K, N);
		a.initRandom(density); b.initRandom();

		val c = a % b;
		//c.printSparse("Sparse a*b=\n");
		val da= a.toDense();
		//da.print("Dense a=\n");
		val db= b;
		//db.print("Dense b=\n");
		val dc= da % db;
		val ret = dc.equals(c);
		//dc.print("Dense a*b=\n");
		if (ret)
			Console.OUT.println("CSC * Dense test passed!");
		else
			Console.OUT.println("---------CSC * Dense test failed!---------");
		return ret;
	}
	//---------------------------------------------------------
	// SCR * 
	public def testMultRC():Boolean {
		Console.OUT.println("Test CSR * CSC -> Dense");
		val a = SparseCSR.make(M, K, density);
		val b = SparseCSC.make(K, N, density);
		a.initRandom(density); b.initRandom(density);

		val c = a % b;//SparseMultSparseToDense.comp(a, b);
		//c.print("Sparse a*b=\n");
		val da= a.toDense();
		val db= b.toDense();
		val dc= da % db;
		val ret = dc.equals(c);
		//dc.print("Dense a*b=\n");

		if (ret)
			Console.OUT.println("CSR * CSC test passed!");
		else
			Console.OUT.println("---------CSR * CSC test failed!---------");
		return ret;
	}
	
	public def testMultRR():Boolean {
		Console.OUT.println("Test CSR * CSR -> Dense");
		val a = SparseCSR.make(M, K, density);
		val b = SparseCSR.make(K, N, density);
		a.initRandom(density); b.initRandom(density);

		val c = a % b;//SparseMultSparseToDense.comp(a, b);
		//c.printSparse("Sparse a*b=\n");
		val da= a.toDense();
		val db= b.toDense();
		val dc= da % db;
		val ret = dc.equals(c);
		//dc.print("Dense a*b=\n");

		if (ret)
			Console.OUT.println("CSR * CSR test passed!");
		else
			Console.OUT.println("---------CSR * CSR test failed!---------");
		return ret;
	}

	public def testMultRD():Boolean {
		var ret:Boolean = true;
		Console.OUT.println("Test CSR * Dense -> Dense");
		val a = SparseCSR.make(M, K, density);
		val b = DenseMatrix.make(K, N);
		a.initRandom(density); b.initRandom();

		val c = a % b;//SparseMultDenseToDense.comp(a, b);
		//c.printSparse("Sparse a*b=\n");
		val da= a.toDense();
		val dc= da % b;
		ret = dc.equals(c);
		//dc.print("Dense a*b=\n");

		if (ret)
			Console.OUT.println("CSR * Dense test passed!");
		else
			Console.OUT.println("---------CSR * Dense test failed!---------");
		return ret;
	}

	public def testMultDC():Boolean {
		Console.OUT.println("Test Dense * CSC -> Dense");
		val a = DenseMatrix.make(M, K);
		val b = SparseCSC.make(K, N, density);
		a.initRandom(); b.initRandom(density);

		val c = a % b;//DenseMultSparseToDense.comp(a, b);
		val db = b.toDense();

		val dc= a % db;
		val ret = dc.equals(c);
		//dc.print("Dense a*b=\n");

		if (ret)
			Console.OUT.println("Dense * CSC test passed!");
		else
			Console.OUT.println("---------Dense * CSC test failed!---------");
		return ret;
	}

	public def testMultDR():Boolean {
		Console.OUT.println("Test Dense * CSC -> Dense");
		val a = DenseMatrix.make(M, K);
		val b = SparseCSR.make(K, N, density);
		a.initRandom(); b.initRandom(density);

		val c = a % b;//DenseMultSparseToDense.comp(a, b);
		val db = b.toDense();

		val dc= a % db;
		val ret = dc.equals(c);
		//dc.print("Dense a*b=\n");

		if (ret)
			Console.OUT.println("Dense * CSR test passed!");
		else
			Console.OUT.println("---------Dense * CSR test failed!---------");
		return ret;
	}
}
