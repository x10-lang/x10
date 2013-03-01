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
import x10.matrix.dist.DistMatrix;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

import x10.matrix.dist.DistMatrix;

/**
   <p>

   <p>
 */
public class TestDist {
	
    public static def main(args:Rail[String]) {
		val testcase = new TestGridDist(args);
		testcase.run();
	}
}
class TestGridDist {
	public val nzp:Double;
	public val M:Int;
	public val N:Int;
	public val K:Int;	

    public def this(args:Rail[String]) {
		M = args.size > 0 ?Int.parse(args(0)):4;
		nzp = args.size > 1 ?Double.parse(args(1)):0.5;
		N = args.size > 2 ?Int.parse(args(2)):M+1;
		K = args.size > 3 ?Int.parse(args(3)):M+2;	
	}

	public def run():Boolean {
		var status:Boolean=true;
		val grid = Grid.make(M, N, Place.MAX_PLACES);
		val m1  = DistMatrix.makeDense(grid);
		m1.initRandom();
		//m1.printMatrix();
		//m1.printBlock();
		val m2 = m1.clone(); 
 		val m3 = m1 - m2;
// 
 		status= m3.equals(0.0);
// 		
// 		val m4 = DistMatrix.makeDense(grid);
// 		val m5 = m4.clone();
// 		val m6 = (m4 + m5) - (m5 + m4);
// 		status &= m6.equals(0.0);
		
		Console.OUT.println("Test result:"+status);
		return status;
	}
} 