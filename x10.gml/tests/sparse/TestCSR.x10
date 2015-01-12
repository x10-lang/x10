/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.Compress2D;
import x10.matrix.sparse.SparseCSR;
import x10.matrix.sparse.SparseCSC;

public class TestCSR extends x10Test {
	public val nzp:Double;
	public val M:Long;
	public val N:Long;
	public val K:Long;

    public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):4;
		nzp = args.size > 1 ? Double.parse(args(1)):0.9;
		N = args.size > 2 ? Long.parse(args(2)):(M as Int)+1;
		K = args.size > 3 ? Long.parse(args(3)):(M as Int)+2;
	}

    public def run():Boolean {
		Console.OUT.println("CSR Test M:"+M+" N:"+N+" Sparsity: "+nzp);
		var ret:Boolean = true;
		ret &= (testClone());
		ret &= (testAdd());
		ret &= (testAddSub());
		ret &= (testAddAssociative());
		ret &= (testScaleAdd());
		ret &= (testExtraction());

        return ret;
	}

	public def testClone():Boolean{
		Console.OUT.println("CSR Test clone()");
		val sp = SparseCSC.make(M, N, nzp);
		sp.initRandom(nzp);
		val sp1 = sp.clone();
		var ret:Boolean = sp.equals(sp1);
		if (!ret)
			Console.OUT.println("--------CSR Clone test failed!--------");

        sp(1, 1) = sp1(2,2) = 10.0;

        if ((sp(1,1)==sp1(2,2)) && (sp(1,1)==10.0)) {
            ret &= true;
        } else {
            ret &= false;
            Console.OUT.println("---------- SparseCSR Matrix chain assignment test failed!-------");
        }

        return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("CSR Add test");
		val sp = SparseCSR.make(M, N, nzp);
		sp.initRandom();

		val nsp= sp * (-1.0);
		val sp0 = sp + nsp;

		val ret = sp0.equals(0.0);
		if (!ret)
			Console.OUT.println("--------CSR Add: sp+sp.neg() test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("CSR Add-sub");
		val sp = SparseCSR.makeRand(M, N, nzp);
		val sp1= SparseCSR.makeRand(M, N, nzp);
		val sp2= sp  + sp1;

		val sp_c  = sp2 - sp1;
		val ret   = sp.equals(sp_c);
		if (!ret)
			Console.OUT.println("--------CSR Add-sub test failed!--------");
		return ret;
	}
	public def testAddAssociative():Boolean {
		Console.OUT.println("CSR Add associative test");
		val a = SparseCSR.makeRand(M, N, nzp);
		val b = SparseCSR.makeRand(M, N, nzp);
		val c = SparseCSR.makeRand(M, N, nzp);
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (!ret)
			Console.OUT.println("--------CSR Add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("CSR Scaling-Add test");
		val a = SparseCSR.makeRand(M, N, 0.3);
		val a1= a * 0.2 ;
		val a2= a * 0.8;
		val aa=a1+a2;
		val ret = a.equals(aa);
		if (!ret)
			Console.OUT.println("--------CSR Scaling-Add test failed!--------");
		return ret;
	}

	public def testExtraction():Boolean {
		var ret:Boolean=true;
		Console.OUT.println("CSR submatrix and data extraction test");
		val sm = SparseCSR.makeRand(M, N, nzp);
		val ca = new CompressArray(sm.countNonZero());
		val c2d= Compress2D.make(M, ca);
		val s2 = new SparseCSR(M, N, c2d);

		//sm.copyRowsToSparse(0, M, s2);//in SparseCSR
		SparseCSR.copyRows(sm, 0, s2, 0, M);
		ret &= sm.equals(s2);

 		//sm.copyColsToSparse(0, N, s2);
		SparseCSR.copyCols(sm, 0, s2, 0, N);
 		ret &= s2.equals(s2);

		val s3 = SparseCSR.make(M-2, N);
		//sm.copyRowsToSparse(1, M-2, s3);
		SparseCSR.copyRows(sm, 1, s3, 0, M-2);
		for (var r:Long=0; r<s3.M; r++)
			for (var c:Long=0; c<s3.N; c++)
				ret &= (sm(r+1, c)==s3(r, c));

		val s4 = SparseCSR.make(M, N-2);
 		//sm.copyColsToSparse(1, N-2, s4);
		SparseCSR.copyCols(sm, 1, s4, 0, N-2);
		for (var r:Long=0; r<s4.M; r++)
			for (var c:Long=0; c<s4.N; c++)
				ret &= sm(r, c+1)==s4(r, c);

		if (!ret)
			Console.OUT.println("--------CSR submatrix and data extraction failed!--------");
		return ret;
	}

    public static def main(args:Rail[String]) {
		new TestCSR(args).execute();
	}
}
