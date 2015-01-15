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


import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.util.VerifyTool;

import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.Compress2D;
import x10.matrix.sparse.SparseCSC;

/**
 * Examples of sparse matrix multiplication and cell-wise operations using GML.
 */
public class SparseExample{
    
    public static def main(args:Rail[String]) {
        val m = args.size > 0 ? Long.parse(args(0)):100;
        val n = args.size > 1 ? Long.parse(args(1)):101;
        val d = (args.size > 2 ?Float.parse(args(2)):0.5f);
        
	val testcase = new RunSparseExample(m, n, d);
	testcase.run();
    }
    
    
    public static class RunSparseExample(M:Long, N:Long, nzp:Float) {
	
        public def run(): void {
	    var ret:Boolean = true;
            
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
	    sp.printStatistics();
	    
	    val sp1 = sp.clone();
	    val ret = sp.equals(sp1);
	    if (ret)
		Console.OUT.println("CSC Clone test passed!");
	    else
		Console.OUT.println("--------CSC Clone test failed!--------");
	    return ret;
        }
        
        public def testAdd():Boolean {
	    val sp = SparseCSC.make(M, N, nzp);   //Create sparse matrix instance using CSC-LT format
	    //Memory space is store the specified nonzero density 
	    sp.initRandom(nzp);                   //Initialize elements in sparse matrix using specified density
	    
	    
	    val nsp= sp * (-1.0 as ElemType);
	    val sp0 = sp + nsp;
	    
	    val ret = sp0.equals(0.0 as ElemType);
	    if (ret)
		Console.OUT.println("CSC Add: sp+sp.neg() test passed");
	    else
		Console.OUT.println("--------CSC Add: sp+sp.neg() test failed--------");
	    return ret;
        }
        
        public def testAddSub():Boolean {
	    val sp = SparseCSC.make(M, N, nzp);
	    val sp1= SparseCSC.make(M, N, nzp);
	    sp.initRandom(nzp); sp1.initRandom(nzp);
	    
	    val sp2= sp  + sp1;
	    val sp_c  = sp2 - sp1;
	    val ret   = sp.equals(sp_c);
	    
	    if (ret)
		Console.OUT.println("CSC Add-sub test passed!");
	    else
		Console.OUT.println("--------CSC Add-sub test failed!--------");
	    return ret;
        }
        public def testAddAssociative():Boolean {
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
            val dot3 = 0.3 as ElemType;
            val a = SparseCSC.make(M, N, dot3);
            a.initRandom(dot3);
            val a1= a * (0.2 as ElemType);
            val a2= a * (0.8 as ElemType);
            val aa=a1+a2;
	    
	    val ret = a.equals(aa);
	    if (ret)
		Console.OUT.println("CSC Scaling-Add test passed!");
	    else
		Console.OUT.println("--------CSC Scaling-Add test failed!--------");
	    return ret;
        }
        
        public def testExtraction():Boolean {
	    var ret:Boolean=true;
	    val sm = SparseCSC.makeRand(M, N, nzp);
	    val ca = new CompressArray(sm.countNonZero());
	    val c2d= Compress2D.make(N, ca);
	    val s2 = new SparseCSC(M, N, c2d);
	    
	    SparseCSC.copyRows(sm, 0, s2, 0, M);
	    ret &= sm.equals(s2); 
	    if (ret) Console.OUT.println("Copy row by row passed");
	    
	    SparseCSC.copy(sm, s2);
	    ret &= s2.equals(s2);
	    if (ret) Console.OUT.println("Full copy all columns passed");
	    
	    val s3 = SparseCSC.make(M-2, N, (M-2)*N);
	    SparseCSC.copyRows(sm, 1, s3, 0, M-2);
	    for (var c:Long=0; c<s3.N; c++)
		for (var r:Long=0; r<s3.M; r++)
		    ret &= sm(r+1, c)==s3(r, c); 
	    if (ret) Console.OUT.println("Partial rows copy passed");
	    
	    val s4 = SparseCSC.make(M, N-2, M*(N-2));
	    SparseCSC.copyCols(sm, 1, s4, 0, N-2);
	    for (var c:Long=0; c<s4.N; c++)
		for (var r:Long=0; r<s4.M; r++)
		    ret &= sm(r, c+1)==s4(r, c); 
	    if (ret) Console.OUT.println("Partial column copy passed");
	    
	    
	    if (ret)
		Console.OUT.println("CSC submatrix and data extraction test passed!");
	    else
		Console.OUT.println("--------CSC submatrix and data extraction failed!--------");
	    return ret;             
        }
    }
}
