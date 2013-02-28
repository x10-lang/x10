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
import x10.matrix.block.Grid;
import x10.matrix.block.SparseBlockMatrix;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;


/**
   <p>

   <p>
 */
public class TestDistSparse {
	
    public static def main(args:Rail[String]) {
		val testcase = new RunDistSparseTest(args);
		testcase.run();
	}
 
	static class RunDistSparseTest {
		public val nzp:Double;
		public val M:Int;
		public val N:Int;

		public val g:Grid;
		public val grow:Grid;

		public def this(args:Rail[String]) {
			M = args.size > 0 ?Int.parse(args(0)):5;
			N = args.size > 1 ?Int.parse(args(1)):M+1;
			nzp = args.size > 2 ?Double.parse(args(2)):1.0;

			g   = Grid.make(M,N);
			grow= new Grid(M, N, 1, Place.MAX_PLACES);
		}

		public def run():Boolean {

			var ret:Boolean= true;
			ret &= testClone();
			ret &= testCopy();
			ret &= testGather();

			return ret;
		}
		public def testClone():Boolean {
			Console.OUT.println("Test dist sparse matrix clone");
			var ret:Boolean;
			val m1  = DistSparseMatrix.make(g, nzp);
			m1.initRandom();

			val m2 = m1.clone();
		
			ret = m1.equals(m2);
			if (ret)
				Console.OUT.println("Test dist sparse matrix clone passed");
			else
				Console.OUT.println("--------------Test dist sparse matrix clone failed!--------------");
			m1(1, 1) = m2(2,2) = 10.0;

			if ((m1(1,1)==m2(2,2)) && (m2(2,2)==10.0)) {
				ret &= true;
				Console.OUT.println("Dist sparse Matrix chain assignment test passed!");
			} else {
				ret &= false;
				Console.OUT.println("---------- Dist sparse chain assignment test failed!-------");
			}

			return ret;
		}

		public def testCopy():Boolean {
			Console.OUT.println("Test dist sparse matrix copy");
			var ret:Boolean;
			val ds  = DistSparseMatrix.make(g, nzp);
			ds.initRandom();
			val dd  = DistDenseMatrix.make(g);

			Debug.flushln("Start copying data from dist sparse to dist dense");
			ds.copyTo(dd);
			Debug.flushln("Done");

			ret = ds.equals(dd);
			  
			if (ret)
				Console.OUT.println("Test dist sparse matrix copy to passed");
			else
				Console.OUT.println("--------------Test dist sparse matrix copy to failed!--------------");
			return ret;
		}

		public def testGather():Boolean {
			Console.OUT.println("Test dist sparse matrix gather");
			var ret:Boolean;
			val ds  = DistSparseMatrix.make(g, nzp);
			ds.initRandom();

			val sbm  = SparseBlockMatrix.make(g, nzp);

			ds.copyTo(sbm);
			
			ret = ds.equals(sbm);
			  
			if (ret)
				Console.OUT.println("Test dist sparse matrix copy to passed");
			else
				Console.OUT.println("--------------Test dist sparse matrix copy to failed!--------------");
			return ret;
		}
	}
}
