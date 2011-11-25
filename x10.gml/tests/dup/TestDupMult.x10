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
import x10.matrix.blas.DenseMultBLAS;
import x10.matrix.dist.DupDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

/**
   <p>

   <p>
 */
public class TestDupMult {
	
    public static def main(args:Array[String](1)) {
		val testcase = new RunTest(args);
		testcase.run();
	}

static class RunTest {
	public val M:Int;
	public val N:Int;
	public val K:Int;	

    public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):50;
		N = args.size > 1 ?Int.parse(args(1)):M+1;
		K = args.size > 2 ?Int.parse(args(2)):M+2;
		
	}

	public def run():Boolean {
		Console.OUT.println("Starting dup dense mult matrix tests in "+Place.numPlaces()+" places");
		Console.OUT.println("Info of matrix sizes: M:"+M+" K:"+K+" N:"+N);

		var ret:Boolean =true;
		ret &=testDupDup();
		ret &=testDupDense();

		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
		return ret;
	}

	public def testDupDup():Boolean {

		Console.OUT.println("Starting dup-dup mult test");
		val a = DupDenseMatrix.makeRand(M,K);
		val b = DupDenseMatrix.makeRand(K,N);
		val c = a % b;

		val da= a.getMatrix();
		val db= b.getMatrix();
		val dc= DenseMatrix.make(M,N);
		DenseMultBLAS.comp(da, db, dc);

		var ret:Boolean = c.equals(dc);
		ret &= c.syncCheck();
		
		if (ret)
			Console.OUT.println("Dup dense dup-dup test passed!");
		else
			Console.OUT.println("--------Dup Dense Matrix dup-dup mult test failed!--------");
		return ret;
	}

	public def testDupDense():Boolean{

		Console.OUT.println("Starting dup dense mult add test");
		val a = DupDenseMatrix.makeRand(M,K);
		val b = DenseMatrix.makeRand(K,N);
		val c = a % b;

		val da= a.getMatrix();
		val dc= DenseMatrix.make(M,N);
		DenseMultBLAS.comp(da, b, dc, false);

		var ret:Boolean = c.equals(dc);
		ret &= c.syncCheck();
 	   
		if (ret)
			Console.OUT.println("Dup dense Matrix addsub test passed!");
		else
			Console.OUT.println("--------Dup Dense Matrix addsub test failed!--------");
		return ret;
	}
}
}
