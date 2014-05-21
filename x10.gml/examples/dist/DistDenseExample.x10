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

import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.dist.DistDenseMatrix;

/**
 * Examples of cell-wise operations and matrix multiplication on distributed dense matrix.
 */
public class DistDenseExample {
	
	public static def main(args:Rail[String]) {
		val m = args.size > 0 ? Long.parse(args(0)):50;
		val n = args.size > 1 ? Long.parse(args(1)):m+1;
		val k = args.size > 2 ? Long.parse(args(2)):m+2;	    	
		val p = args.size > 3 ?Double.parse(args(3)):0.5;
		
		val testcase = new RunDistDense(m,n,k,p);
		testcase.run();
	}
	
	static class RunDistDense(M:Long, N:Long, K:Long, nzp:Double) {
		
		public def this(m:Long, n:Long, k:Long, p:Double) {
			property(m, n, k, p);
		}
		
		public def run (): void {
			Console.OUT.println("Starting dense matrix clone/add/sub/scaling tests");
			Console.OUT.printf("Matrix M:%d K:%d N:%d\n", M, N, K);
			
			var ret:Boolean = true;
			// Set the matrix function
			ret &= (testClone());
			ret &= (testCopyTo());
			ret &= (testScale());
			ret &= (testAdd());
			ret &= (testAddSub());
			ret &= (testAddAssociative());
			ret &= (testScaleAdd());
			ret &= (testCellMult());
			ret &= (testCellDiv());
			
			if (ret)
				Console.OUT.println("Test passed!");
			else
				Console.OUT.println("----------------Test failed!----------------");
		}
		public def testClone():Boolean{
			var ret:Boolean = true;

			//Using default data partitioning method.
			//Partition matrix in MxN into blocks same as the number of places
			val ddm = DistDenseMatrix.make(M, N);
			ddm.initRandom();
			
			val ddm1 = ddm.clone();
			ret = ddm.equals(ddm1);
			
			if (ret)
				Console.OUT.println("DistDenseMatrix Clone test passed!");
			else
				Console.OUT.println("--------Dense Matrix Clone test failed!--------");
			return ret;
		}
		
		public def testCopyTo():Boolean {
			var ret:Boolean = true;
			val ddm = DistDenseMatrix.make(M, N);
			val dbm = DenseBlockMatrix.make(ddm.grid);
			val dm  = DenseMatrix.make(M,N);
			
			ddm.initRandom();
			
			ddm.copyTo(dbm);          //Copy distributed dense matrix to dense block matrix at here
			//using array copy or MPI P2P  communication method
			ret &= ddm.equals(dbm);
			
			dbm.copyTo(dm);          //Copy the dense block matrix to dense matrix.
			ret &= ddm.equals(dbm);
			
			if (ret)
				Console.OUT.println("Dist dense Matrix copyTo test passed!");
			else
				Console.OUT.println("--------Dist dense matrix copyTo test failed!--------");	
			return ret;
		}
		
		public def testScale():Boolean{
			
			val dm = DistDenseMatrix.make(M, N);
			dm.initRandom();
			
			val dm1  = dm * 2.5;
			val m = dm.toDense();  //Copy distributed dense matrix to dense matrix element by element.
			
			val ret = dm.equals(m);
			if (ret)
				Console.OUT.println("Dist dense Matrix scaling test passed!");
			else
				Console.OUT.println("--------Dist dense matrix Scaling test failed!--------");	
			return ret;
		}
		
		public def testAdd():Boolean {
			val dm = DistDenseMatrix.make(M, N);
			dm.initRandom();
			
			val dm1 = dm * -1.0;
			val dm0 = dm + dm1;
			val ret = dm0.equals(0.0);
			
			if (ret)
				Console.OUT.println("Add: dm + dm*-1 test passed");
			else
				Console.OUT.println("--------Add: dm + dm*-1 test failed--------");
			return ret;
		}
		
		public def testAddSub():Boolean {
			
			val dm = DistDenseMatrix.make(M, N);
			val dm1= DistDenseMatrix.make(M, N);
			
			dm.initRandom();
			dm.initRandom();
			//sp.print("Input:");
			val dm2= dm  + dm1;
			//sp2.print("Add result:");
			//
			val dm_c  = dm2 - dm1;
			val ret   = dm.equals(dm_c);
			//sp_c.print("Another add result:");
			if (ret)
				Console.OUT.println("Dist dense matrix Add-sub test passed!");
			else
				Console.OUT.println("--------Dist dense matrix Add-sub test failed!--------");
			return ret;
		}
		
		
		public def testAddAssociative():Boolean {
			
			val a = DistDenseMatrix.make(M, N);
			val b = DistDenseMatrix.make(M, N);
			val c = DistDenseMatrix.make(M, N);
			a.initRandom();
			b.initRandom();
			c.initRandom();
			
			val c1 = a + b + c;
			val c2 = a + (b + c);
			val ret = c1.equals(c2);
			if (ret)
				Console.OUT.println("Add associative test passed!");
			else
				Console.OUT.println("--------Add associative test failed!--------");
			return ret;
		}
		
		public def testScaleAdd():Boolean {
			val a = DistDenseMatrix.make(M, N);
			a.initRandom();
			
			val m = a.toDense();
			val a1= a * 0.2;
			val a2= 0.8 * a;
			var ret:Boolean = a.equals(a1+a2);
			ret &= a.equals(m);
			
			if (ret)
				Console.OUT.println("Dense Matrix scaling-add test passed!");
			else
				Console.OUT.println("--------Dist dense matrix scaling-add test failed!--------");
			return ret;
		}
		
		public def testCellMult():Boolean {
			val a = DistDenseMatrix.make(M, N);
			val b = DistDenseMatrix.make(M, N);
			
			a.initRandom();
			b.initRandom();
			
			val c = (a + b) * a;
			val d = a * a + b * a;
			var ret:Boolean = c.equals(d);
			
			val da= a.toDense();
			val db= b.toDense();
			val dc= (da + db) * da;
			ret &= dc.equals(c);
			
			if (ret)
				Console.OUT.println("Dist dense Matrix cellwise mult passed!");
			else
				Console.OUT.println("--------Dist dense matrix cellwise mult test failed!--------");
			return ret;
		}
		
		public def testCellDiv():Boolean {
			val a = DistDenseMatrix.make(M, N);
			val b = DistDenseMatrix.make(M, N);
			
			a.initRandom();
			b.initRandom();
			
			val c = (a + b) * a;
			val d =  c / (a + b);
			var ret:Boolean = d.equals(a);
			
			if (ret)
				Console.OUT.println("Dist dense Matrix cellwise mult-div passed!");
			else
				Console.OUT.println("--------Dist dense matrix cellwise mult-div test failed!--------");
			return ret;
		}
	}
	
	
} 
