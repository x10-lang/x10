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
import x10.matrix.VerifyTools;

import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.Compress2D;
import x10.matrix.sparse.SparseCSC;

/**
   <p>

   <p>
 */

public class TestCSC{
    public static def main(args:Array[String](1)) {
		val testcase = new AddSubCSC(args);
		testcase.run();
	}
}

class AddSubCSC {
	public val nzp:Double;
	public val M:Int;
	public val N:Int;

    public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):10;
		N = args.size > 1 ?Int.parse(args(1)):10;
		nzp = args.size > 2 ?Double.parse(args(2)):0.99;
	}

	public def run(): void {
		var ret:Boolean = true;
		Console.OUT.println("CSC Test on "+M+"x"+N+" matrix "+ nzp+" sparsity");
 		// Set the matrix function
		ret &= (testClone());
		ret &= (testAdd());
		ret &= (testAddSub());
		ret &= (testAddAssociative());
		ret &= (testScaleAdd());
		ret &= (testExtraction());

		if (ret)
			Console.OUT.println("CSC Test passed!");
		else
			Console.OUT.println("----------------CSC Test failed!----------------");
	}

	public def testClone():Boolean{
		Console.OUT.println("CSC Test clone()");
		val sp = SparseCSC.make(M, N, nzp);
		sp.initRandom(nzp);
		sp.printRandomInfo();
		//return true;
		//sp.print();
		val sp1 = sp.clone();
		//sp1.print();
		val ret = sp.equals(sp1);
		if (ret)
		 	Console.OUT.println("CSC Clone test passed!");
		else
		 	Console.OUT.println("--------CSC Clone test failed!--------");
		return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("CSC Add test");
		//val sp = SparseCSC.makeRand(M, N, nzp);
		val sp = SparseCSC.make(M, N, nzp);
		sp.initRandom(nzp);
		val nsp= sp * (-1.0);
		val sp0 = sp + nsp;

		val ret = sp0.equals(0.0);
		if (ret)
			Console.OUT.println("CSC Add: sp+sp.neg() test passed");
		else
			Console.OUT.println("--------CSC Add: sp+sp.neg() test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("CSC Add-sub");
		val sp = SparseCSC.make(M, N, nzp);
		val sp1= SparseCSC.make(M, N, nzp);
		sp.initRandom(nzp); sp1.initRandom(nzp);

		//sp.print("Input:");
		val sp2= sp  + sp1;
		//sp2.print("Add result:");
		//
		val sp_c  = sp2 - sp1;
		val ret   = sp.equals(sp_c);
		//sp_c.print("Another add result:");
		if (ret)
			Console.OUT.println("CSC Add-sub test passed!");
		else
			Console.OUT.println("--------CSC Add-sub test failed!--------");
		return ret;
	}
	public def testAddAssociative():Boolean {
		Console.OUT.println("CSC Add associative test");
		val a = SparseCSC.make(M, N, nzp);
		val b = SparseCSC.make(M, N, nzp);
		val c = SparseCSC.make(M, N, nzp);
		a.initRandom(nzp); b.initRandom(nzp); c.initRandom(nzp);
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (ret)
			Console.OUT.println("CSC Add associative test passed!");
		else
			Console.OUT.println("--------CSC Add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("CSC Scaling-Add test");
		val a = SparseCSC.make(M, N, 0.3);
		a.initRandom(0.3);
		//a.print("src matrix");
		val a1= a * 0.2 ;
		//a1.print("Raise 0.2");
		val a2= a * 0.8;
		//a2.print("Raise 0.8");
		val aa=a1+a2;
		//aa.print();
		val ret = a.equals(aa);
		if (ret)
			Console.OUT.println("CSC Scaling-Add test passed!");
		else
			Console.OUT.println("--------CSC Scaling-Add test failed!--------");
		return ret;
	}

	public def testExtraction():Boolean {
		var ret:Boolean=true;
		Console.OUT.println("CSC submatrix and data extraction test");
		val sm = SparseCSC.makeRand(M, N, nzp);
		val ca = new CompressArray(sm.countNonZero());
		val c2d= Compress2D.make(N, ca);
		val s2 = new SparseCSC(M, N, c2d);

		SparseCSC.copyRows(sm, 0, s2, 0, M);
		//sm.copyRowsToSparse(0, M, s2);//in SparseCSC
		ret &= sm.equals(s2); 
		if (ret) Console.OUT.println("Copy row by row passed");

 		//sm.copyColsToSparse(0, N, s2);
		SparseCSC.copy(sm, s2);
 		ret &= s2.equals(s2);
		if (ret) Console.OUT.println("Full copy all columns passed");
		
		val s3 = SparseCSC.make(M-2, N);
		//sm.copyRowsToSparse(1, M-2, s3);
		SparseCSC.copyRows(sm, 1, s3, 0, M-2);
		for (var c:Int=0; c<s3.N; c++)
			for (var r:Int=0; r<s3.M; r++)
				ret &= sm(r+1, c)==s3(r, c); 
		if (ret) Console.OUT.println("Partial rows copy passed");

		val s4 = SparseCSC.make(M, N-2);
 		//sm.copyColsToSparse(1, N-2, s4);
		SparseCSC.copyCols(sm, 1, s4, 0, N-2);
		for (var c:Int=0; c<s4.N; c++)
			for (var r:Int=0; r<s4.M; r++)
				ret &= sm(r, c+1)==s4(r, c); 
		if (ret) Console.OUT.println("Partial column copy passed");

		
		if (ret)
			Console.OUT.println("CSC submatrix and data extraction test passed!");
		else
			Console.OUT.println("--------CSC submatrix and data extraction failed!--------");
		return ret;		
	}
}
