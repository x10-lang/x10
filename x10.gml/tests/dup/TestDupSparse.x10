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
import x10.matrix.dist.DupDenseMatrix;
import x10.matrix.dist.DupSparseMatrix;

/**
   <p>

   <p>
 */
public class TestDupSparse {
	
    public static def main(args:Array[String](1)) {
		val testcase = new RunDupSparseTest(args);
		testcase.run();
	}

	static class RunDupSparseTest {
		public val M:Int;
		public val N:Int;
		public val K:Int;	
		public val S:Double;

		public def this(args:Array[String](1)) {
			M = args.size > 0 ?Int.parse(args(0)):50;
			N = args.size > 1 ?Int.parse(args(1)):M+1;
			K = args.size > 2 ?Int.parse(args(2)):M+2;
			S = args.size > 3 ?Double.parse(args(3)):0.99;
			
		}

		public def run():Boolean {
			Console.OUT.println("Starting dup sparse matrix tests in " + Place.numPlaces()+" places");
			Console.OUT.print("Info of matrix sizes: M:"+M+" K:"+K+" N:"+N+" Sparsity:"+S);

			var ret:Boolean =testClone();
			ret &=testClone();
			ret &=testAddTo();
			ret &=testSubTo();

			if (ret)
				Console.OUT.println("Test passed!");
			else
				Console.OUT.println("----------------Test failed!----------------");
			return ret;
		}

		public def testClone():Boolean {

			Console.OUT.println("Starting dup sparse Matrix clone test");
			val s1:DupSparseMatrix = DupSparseMatrix.makeRand(N, M, S);
			val s2:DupSparseMatrix = s1.clone();
			var ret:Boolean = s1.equals(s2 as Matrix(s1.M, s1.N));
			ret &= s1.syncCheck();
			ret &= s2.syncCheck();
		
			if (ret)
				Console.OUT.println("Dup sparse matrix clone test passed!");
			else
				Console.OUT.println("--------Dup sparse Matrix clone test failed!--------");
                	s1(1, 1) = s2(2,2) = 10.0;

                	if ((s1(1,1)==s2(2,2)) && (s1(1,1)==10.0)) {
                        	ret &= true;
                        	Console.OUT.println("DupSparse Matrix chain assignment test passed!");
                	} else {
                        	ret &= false;
                        	Console.OUT.println("---------- DupSparse Matrix chain assignment test failed!-------");
                	}

			return ret;
		}

		public def testAddTo():Boolean{

			Console.OUT.println("Starting dup sparse Matrix addto test");
			val s1 = DupSparseMatrix.makeRand(M,N, S);
			val d1 = DupDenseMatrix.make(M,N);
			val d2 = DupDenseMatrix.makeRand(M,N);
			val d3 = d2.clone();
			
			s1.cellAddTo(d2);
			s1.copyTo(d1);
			
			d3.cellAdd(d1);

			var ret:Boolean = d2.equals(d3);
			ret &= d2.syncCheck();

			if (ret)
				Console.OUT.println("Dup sparse Matrix addto test passed!");
			else
				Console.OUT.println("--------Dup sparse matrix addto test failed!--------");
			return ret;
		}


		public def testSubTo():Boolean{

			Console.OUT.println("Starting dup sparse Matrix subto test");
			val s1 = DupSparseMatrix.makeRand(M,N, S);
			val d1 = DupDenseMatrix.make(M,N);
			val d2 = DupDenseMatrix.makeRand(M,N);
			val d3 = d2.clone();
			
			s1.cellSubFrom(d2);
			s1.copyTo(d1);
			
			d1.cellSubFrom(d3);

			var ret:Boolean = d2.equals(d3);
			ret &= d2.syncCheck();

			if (ret)
				Console.OUT.println("Dup sparse Matrix sub test passed!");
			else
				Console.OUT.println("--------Dup sparse matrix sub test failed!--------");
			return ret;
		}
	}
}
