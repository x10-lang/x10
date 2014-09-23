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

import harness.x10Test;

import x10.matrix.DenseMatrix;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.SparseCSR;

public class TestSparseMult extends x10Test {
	public val density:Double;
	public val M:Long;
	public val N:Long;
	public val K:Long;

    public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):50;
		density = args.size > 1 ? Double.parse(args(1)):0.5;
		N = args.size > 2 ? Long.parse(args(2)):(M as Int)+1;
		K = args.size > 3 ? Long.parse(args(3)):(M as Int)+2;
	}

    public def run():Boolean {
		var ret:Boolean = true;
		ret &= (testMultCC());
 		ret &= (testMultCR());
 		ret &= (testMultCD());

 		ret &= (testMultRR());
 		ret &= (testMultRC());
 		ret &= (testMultRD());

 		ret &= (testMultDC());
 		ret &= (testMultDR());

        return ret;
	}

	public def testMultCC():Boolean {
		Console.OUT.println("Test CSC * CSC -> CSC");
		val a = SparseCSC.make(M, K, density);
		val b = SparseCSC.make(K, N, density);
		a.initRandom(density); b.initRandom(density);
		val c = a % b;
		val da= a.toDense();
		val db= b.toDense();
		val dc= da % db;
		val ret = dc.equals(c);
		if (!ret)
			Console.OUT.println("---------CSC * CSC test failed!---------");
		return ret;
	}

	public def testMultCR():Boolean {
		Console.OUT.println("Test CSC * CSR -> Dense");
		val a = SparseCSC.make(M, K, density);
		val b = SparseCSR.make(K, N, density);
		a.initRandom(density); b.initRandom(density);
		val c = a % b;
		val da= a.toDense();
		val db= b.toDense();
		val dc= da % db;
		val ret = dc.equals(c);
		if (!ret)
			Console.OUT.println("---------CSC * CSR test failed!---------");
		return ret;
	}

	public def testMultCD():Boolean {
		Console.OUT.println("Test CSC * Dense -> Dense");
		val a = SparseCSC.make(M, K, density);
		val b = DenseMatrix.make(K, N);
		a.initRandom(density); b.initRandom();

		val c = a % b;
		val da= a.toDense();
		val db= b;
		val dc= da % db;
		val ret = dc.equals(c);
		if (!ret)
			Console.OUT.println("---------CSC * Dense test failed!---------");
		return ret;
	}

	public def testMultRC():Boolean {
		Console.OUT.println("Test CSR * CSC -> Dense");
		val a = SparseCSR.make(M, K, density);
		val b = SparseCSC.make(K, N, density);
		a.initRandom(density); b.initRandom(density);

		val c = a % b;//SparseMultSparseToDense.comp(a, b);
		val da= a.toDense();
		val db= b.toDense();
		val dc= da % db;
		val ret = dc.equals(c);

		if (!ret)
			Console.OUT.println("---------CSR * CSC test failed!---------");
		return ret;
	}

	public def testMultRR():Boolean {
		Console.OUT.println("Test CSR * CSR -> Dense");
		val a = SparseCSR.make(M, K, density);
		val b = SparseCSR.make(K, N, density);
		a.initRandom(density); b.initRandom(density);

		val c = a % b;//SparseMultSparseToDense.comp(a, b);
		val da= a.toDense();
		val db= b.toDense();
		val dc= da % db;
		val ret = dc.equals(c);

		if (!ret)
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
		val da= a.toDense();
		val dc= da % b;
		ret = dc.equals(c);

		if (!ret)
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

		if (!ret)
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

		if (!ret)
			Console.OUT.println("---------Dense * CSR test failed!---------");
		return ret;
	}

    public static def main(args:Rail[String]) {
		new TestSparseMult(args).execute();
	}
}
