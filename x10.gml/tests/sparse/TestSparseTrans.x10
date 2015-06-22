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
import x10.matrix.ElemType;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.SparseCSR;
import x10.matrix.sparse.SparseMultSparseToDense;
import x10.matrix.sparse.SparseMultDenseToDense;
import x10.matrix.sparse.DenseMultSparseToDense;

public class TestSparseTrans extends x10Test {
    static def ET(a:Double)= a as ElemType;
    static def ET(a:Float)= a as ElemType;
    public val density:Float;
    public val M:Long;
    public val N:Long;
    public val K:Long;
    
    public def this(args:Rail[String]) {
	M = args.size > 0 ? Long.parse(args(0)):50;
	density = args.size > 1 ? Float.parse(args(1)):0.5f;
	N = args.size > 2 ? Long.parse(args(2)):(M as Int)+1;
	K = args.size > 3 ? Long.parse(args(3)):(M as Int)+2;
    }
    
    public def run():Boolean {
	var ret:Boolean = true;
	
	ret &= (testMultCtC());
	ret &= (testMultCtR());
	ret &= (testMultCtD());
	ret &= (testMultCDt());
	
	ret &= (testMultRtC());
	ret &= (testMultRtR());
	ret &= (testMultRtD());
	ret &= (testMultRDt());
	
	ret &= (testToDenseCDt());
	ret &= (testToDenseDtC());
	
        return ret;
    }
    
    public def testMultCtC():Boolean {
	Console.OUT.println("Test CSC^T * CSC -> Dense");
	val a = SparseCSC.make(K, M, density);
	val b = SparseCSC.make(K, N, density);
	a.initRandom(density); b.initRandom(density);
	
	val c = SparseMultSparseToDense.compTransMult(a, b); //a.T() * b;
	val da = a.toDense();
	val db = b.toDense();
	val dc = DenseMatrix.make(M,N).transMult(da, db);
	val ret = dc.equals(c);
	if (!ret)
	    Console.OUT.println("--------CSC^T * CSC test failed!--------\n");
	return ret;
    }
    
    public def testMultCtR():Boolean {
	Console.OUT.println("Test CSC^T * CSR -> Dense");
	val a = SparseCSC.makeRand(K, M, density);
	val b = SparseCSR.makeRand(K, N, density);
	val c = DenseMatrix.make(M,N);
	SparseMultSparseToDense.compTransMult(a, b, c, false);//a.T() * b;
	val da = a.toDense();
	val db = b.toDense();
	val dc = DenseMatrix.make(M,N).transMult(da, db);
	val ret = dc.equals(c);
	
	if (!ret)
	    Console.OUT.println("--------CSC^T * CSR test failed!--------\n");
	return ret;
    }
    
    public def testMultCtD():Boolean {
	Console.OUT.println("Test CSC.T() * Dense -> Dense");
	val a = SparseCSC.makeRand(K, M, density);
	val b = DenseMatrix.makeRand(K, N);
	val c = SparseMultDenseToDense.compTransMult(a, b);//a.T() * b;
	val da = a.toDense();
	val db = b;
	val dc = DenseMatrix.make(M,N).transMult(da, db);
	val ret = dc.equals(c);
	if (!ret)
	    Console.OUT.println("--------CSC^T * Dense test failed!--------\n");
	return ret;
    }
    
    public def testMultCDt():Boolean {
	Console.OUT.println("Test CSC * Dense^T -> Dense");
	val a = SparseCSC.makeRand(M, K, density);
	val b = DenseMatrix.makeRand(N, K);
	val c = SparseMultDenseToDense.compMultTrans(a, b);
	val da = a.toDense();
	val db = b;
	val dc = DenseMatrix.make(M,N).multTrans(da, db);
	//val dc= da * db.T();
	val ret = dc.equals(c);
	if (!ret)
	    Console.OUT.println("--------CSC * Dense^T test failed!--------\n");
	return ret;
    }
    
    public def testMultRtC():Boolean {
	Console.OUT.println("Test CSR^T * CSC -> Dense");
	val a = SparseCSR.makeRand(K, M, density);
	val b = SparseCSC.makeRand(K, N, density);
	val c = SparseMultSparseToDense.compTransMult(a, b);
	val da = a.toDense();
	val db = b.toDense();
	val dc = DenseMatrix.make(M,N).transMult(da, db);
	val ret = dc.equals(c);
	if (!ret)
	    Console.OUT.println("--------CSR.T() * CSC test failed!--------\n");
	return ret;
    }
    
    public def testMultRtR():Boolean {
	Console.OUT.println("Test CSR^T * CSR -> Dense");
	val a = SparseCSR.makeRand(K, M, density);
	val b = SparseCSR.makeRand(K, N, density);
	val c = SparseMultSparseToDense.compTransMult(a, b);//a.T() * b;
	val da = a.toDense();
	val db = b.toDense();
	val dc = DenseMatrix.make(M,N).transMult(da,db);
	val ret = dc.equals(c);
	if (!ret)
	    Console.OUT.println("--------CSR^T * CSR test failed!--------\n");
	return ret;
    }
    
    public def testMultRtD():Boolean {
	Console.OUT.println("Test CSR^T * Dense -> Dense");
	val a = SparseCSR.makeRand(K, M, density);
	val b = DenseMatrix.makeRand(K, N);
	val c = DenseMatrix.make(M, N);
	SparseMultDenseToDense.compTransMult(a, b, c, false);//a.T() * b;
	val da = a.toDense();
	val db = b;
	val dc = DenseMatrix.make(M,N).transMult(da, db);
	val ret = dc.equals(c);
	if (!ret)
	    Console.OUT.println("--------CSR^T * Dense test failed!--------\n");
	return ret;
    }
    
    public def testMultRDt():Boolean {
	Console.OUT.println("Test CSR * Dense^T -> Dense");
	val a = SparseCSR.makeRand(M, K, density);
	val b = DenseMatrix.makeRand(N, K);
	val c = SparseMultDenseToDense.compMultTrans(a, b);
	val da = a.toDense();
	val db = b;
	val dc = DenseMatrix.make(M,N).multTrans(da,db);
	val ret = dc.equals(c);
	if (!ret)
	    Console.OUT.println("--------CSC * Dense.T() test failed!--------\n");
	return ret;
    }
    
    
    public def testToDenseCDt():Boolean {
	Console.OUT.println("Test CSC * Dense^T -> Dense");
	val a = SparseCSC.makeRand(M, K, density);
	val b = DenseMatrix.makeRand(N, K);
	val c = SparseMultDenseToDense.compMultTrans(a, b);
	val da = a.toDense();
	val dc = DenseMatrix.make(M,N).multTrans(da, b);
	val ret = dc.equals(c);
	if (!ret)
	    Console.OUT.println("--------CSC * Dense^T -> Dense test failed!--------\n");
	return ret;
    }
    
    
    public def testToDenseDtC():Boolean {
	Console.OUT.println("Test Dense^T * CSC -> Dense");
	val a = DenseMatrix.makeRand(K, M);
	val b = SparseCSC.makeRand(K, N, density);
	val c = DenseMultSparseToDense.compTransMult(a, b);
	
	val db = b.toDense();
	val dc = DenseMatrix.make(M,N).transMult(a, db);
	
	val ret = dc.equals(c);
	if (!ret)
	    Console.OUT.println("--------Dense^T * CSC -> Dense test failed!--------\n");
	return ret;
    }
    
    public static def main(args:Rail[String]) {
	new TestSparseTrans(args).execute();
    }
}
