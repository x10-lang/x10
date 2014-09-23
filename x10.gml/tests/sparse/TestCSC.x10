/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

import harness.x10Test;

import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.Compress2D;
import x10.matrix.sparse.SparseCSC;

public class TestCSC extends x10Test {
	public val nzp:Double;
	public val M:Long;
	public val N:Long;

    public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):8;
		N = args.size > 1 ? Long.parse(args(1)):8;
		nzp = args.size > 2 ? Double.parse(args(2)):0.99;
	}

    public def run():Boolean {
		var ret:Boolean = true;
		Console.OUT.println("CSC Test on "+M+"x"+N+" matrix "+ nzp+" sparsity");
		ret &= (testClone());
		ret &= (testInit());
		ret &= (testAdd());
		ret &= (testAddSub());
		ret &= (testAddAssociative());
		ret &= (testScaleAdd());
		//ret &= (testExtraction());
		ret &= (testCopy());

        return ret;
	}

	public def testClone():Boolean{
		Console.OUT.println("CSC Test clone()");
		val sp = SparseCSC.make(M, N, nzp);
		sp.initRandom(nzp);
		sp.printStatistics();
		val sp1 = sp.clone();
		var ret:Boolean = sp.equals(sp1);
		if (!ret)
		 	Console.OUT.println("--------CSC Clone test failed!--------");

        sp1(1, 1) = sp1(2,2) = 10.0;

        if ((sp1(1,1)==sp1(2,2)) && (sp1(1,1)==10.0)) {
            ret &= true;
        } else {
            ret &= false;
            Console.OUT.println("---------- SparseCSC Matrix chain assignment test failed!-------");
        }

		return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("CSC Add test");
		val sp = SparseCSC.make(M, N, nzp);
		sp.initRandom();
		val nsp= sp * (-1.0);
		val sp0 = sp + nsp;

		val ret = sp0.equals(0.0);
		if (!ret)
			Console.OUT.println("--------CSC Add: sp+sp.neg() test failed--------");
		return ret;
	}

	public def testInit():Boolean {
		Console.OUT.println("Sparse CSC initialization func test");
		var ret:Boolean=true;
		val sp = SparseCSC.make(M, N, 0.6).init((r:Long, c:Long)=>((r+c)%2 as Double));

		for (var c:Long=0; c<N; c++)
			for (var r:Long=0; r<M; r++)
				ret &= (sp(r,c) == ((r+c)%2 as Double));

		if (!ret)
			Console.OUT.println("--------SparseCSC initialization func test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("CSC Add-sub");
		val sp = SparseCSC.make(M, N, nzp);
		val sp1= SparseCSC.make(M, N, nzp);
		sp.initRandom(nzp); sp1.initRandom(nzp);

		val sp2 = sp + sp1;
		val sp_c = sp2 - sp1;
		val ret = sp.equals(sp_c);
		if (!ret)
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
		if (!ret)
			Console.OUT.println("--------CSC Add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("CSC Scaling-Add test");
		val a = SparseCSC.make(M, N, 0.3);
		a.initRandom(0.3);
		val a1= a * 0.2 ;
		val a2= a * 0.8;
		val aa=a1+a2;
		val ret = a.equals(aa);
		if (!ret)
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

 		//sm.copyColsToSparse(0, N, s2);
		SparseCSC.copy(sm, s2);
 		ret &= s2.equals(s2);

		val s3 = SparseCSC.make(M-2, N, nzp);
		//sm.copyRowsToSparse(1, M-2, s3);
		SparseCSC.copyRows(sm, 1, s3, 0, M-2);
		for (var c:Long=0; c<s3.N; c++)
			for (var r:Long=0; r<s3.M; r++)
				ret &= sm(r+1, c)==s3(r, c);

		val s4 = SparseCSC.make(M, N-2, nzp);
 		//sm.copyColsToSparse(1, N-2, s4);
		SparseCSC.copyCols(sm, 1, s4, 0, N-2);
		for (var c:Long=0; c<s4.N; c++)
			for (var r:Long=0; r<s4.M; r++)
				ret &= sm(r, c+1)==s4(r, c);

		if (!ret)
			Console.OUT.println("--------CSC submatrix and data extraction failed!--------");
		return ret;
	}

	public def testCopy():Boolean {
		Console.OUT.println("CSC copying to another CSC");
		Console.OUT.flush();
		var ret:Boolean = true;
		val sm = SparseCSC.make(M, N, nzp).initRandom();
		val dm = SparseCSC.make(M, N, nzp);

		SparseCSC.copyCols(sm, N-1, dm, 0, 1);
		for (var r:Long=0; r<M; r++)
			ret &= (sm(r, N-1)== dm(r, 0));

		if (!ret)
			Console.OUT.println("--------CSC copy test failed!--------");
		return ret;
	}

    public static def main(args:Rail[String]) {
		new TestCSC(args).execute();
	}
}
