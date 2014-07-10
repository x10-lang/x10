/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */


import x10.matrix.util.Debug;
import x10.matrix.block.Grid;
import x10.matrix.block.SparseBlockMatrix;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

/**
 * Examples of cell-wise operations and matrix multiplication on distributed sparse matrix.
 */
public class DistSparseExample {
	
	public static def main(args:Rail[String]) {
		val m = args.size > 0 ? Long.parse(args(0)):100;
		val n = args.size > 1 ? Long.parse(args(1)):m+1;
		val k = args.size > 2 ? Long.parse(args(2)):m+2;	    	
		val p = args.size > 3 ?Double.parse(args(3)):0.5;
		
		val testcase = new RunDistSparse(m,n,k,p);
		testcase.run();
	}
	
	static class RunDistSparse(M:Long, N:Long, K:Long, nzp:Double) {
		public val g:Grid;
		public val grow:Grid;
		
		public def this(m:Long, n:Long, k:Long, p:Double) {
			property(m, n, k, p);

			//Partition matrix in MxN into blocks same as the number of places
			//The partitioning grid is to be spared or close to square partitioning 
			g   = Grid.make(M,N); 
			
			//Partition matrix in MxN into 1 row blocks
			grow= new Grid(M, N, 1, Place.numPlaces());  
		}
 

		public def run():Boolean {

			var ret:Boolean= true;
			ret &= testClone();
			ret &= testCopy();
			ret &= testGather();

			return ret;
		}
		public def testClone():Boolean {
			var ret:Boolean;
			val m1  = DistSparseMatrix.make(g, nzp);
			m1.initRandom();

			val m2 = m1.clone();
		
			ret = m1.equals(m2);
			if (ret)
				Console.OUT.println("Test dist sparse matrix clone passed");
			else
				Console.OUT.println("--------------Test dist sparse matrix clone failed!--------------");
			return ret;
		}

		public def testCopy():Boolean {
			var ret:Boolean;
			val ds  = DistSparseMatrix.make(g, nzp);
			ds.initRandom();

			val dd  = DistDenseMatrix.make(g);

			ds.copyTo(dd); //Copy distributed sparse matrix into a distributed dense matrix of the dsame block distribution

			ret = ds.equals(dd);
			  
			if (ret)
				Console.OUT.println("Test dist sparse matrix copy to passed");
			else
				Console.OUT.println("--------------Test dist sparse matrix copy to failed!--------------");
			return ret;
		}

		public def testGather():Boolean {
			var ret:Boolean;
			
			val ds  = DistSparseMatrix.make(g, nzp);
			val sbm  = SparseBlockMatrix.make(g, nzp);
			ds.initRandom();

			ds.copyTo(sbm); //Copy all dist blcoks into block matrix at here using gather collective communication
			
			ret = ds.equals(sbm);
			  
			if (ret)
				Console.OUT.println("Test dist sparse matrix copy to passed");
			else
				Console.OUT.println("--------------Test dist sparse matrix copy to failed!--------------");
			return ret;
		}
	}
}
