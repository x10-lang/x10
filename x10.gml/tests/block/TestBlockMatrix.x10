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

/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.Grid;

import x10.matrix.block.BlockMatrix;

/**
   <p>

   <p>
 */
public class TestBlockMatrix {
	
    public static def main(args:Array[String](1)) {
		val testcase = new RunBlockMatrix(args);
		testcase.run();
	}
}

class RunBlockMatrix {
	public val M:Int;
	public val N:Int;
	public val R:Int;
	public val C:Int;
	public val grid:Grid;
	public val nzd:Double;

    public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):40;
		N = args.size > 1 ?Int.parse(args(1)):M+2;
		R = args.size > 2 ?Int.parse(args(2)):2;
		C = args.size > 3 ?Int.parse(args(3)):3;
		nzd =  args.size > 4 ?Double.parse(args(4)):0.2;
		grid = new Grid(M, N, R, C);
	}

    public def run (): void {
		Console.OUT.println("Starting block matrix tests");
		Console.OUT.printf("Matrix M:%d N:%d Row block:%d Column block:%d, nzd:%f\n", 
						   M, N, R, C, nzd);

		var ret:Boolean = true;
 		// Set the matrix function
 		ret &= (testClone());
		ret &= (testScale());

		if (ret)
			Console.OUT.println("Block matrix test passed!");
		else
			Console.OUT.println("----------------Block matrix test failed!----------------");
	}

	public def testClone():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Starting block matrix clone test");
		val sbm = BlockMatrix.makeDense(grid);
		sbm.initRandom();

		//dm.printBlock("Dist dense");
		val sbm1 = sbm.clone();
		ret = sbm.equals(sbm1 as Matrix(M,N));

		val dm = DenseMatrix.make(grid.M, grid.N);
		sbm.copyTo(dm);
		//dm2.print("Dense");
		ret &= sbm.equals(dm as Matrix(M,N));

		if (ret)
			Console.OUT.println("Block matrix Clone and dense conversion test passed!");
		else
			Console.OUT.println("--------Block matrix Clone test failed!--------");
		return ret;
	}

	public def testScale():Boolean{
		Console.OUT.println("Starting block matrix scaling test, nzd:"+nzd);
		val dm = BlockMatrix.makeSparse(grid, nzd);
		dm.initRandom();
		val dm1  = dm * 2.5;
		val m = dm.toDense();
		
		m.scale(2.5);
		val ret = m.equals(dm1 as Matrix(M,N));
		if (ret)
			Console.OUT.println("Block matrix scaling test passed!");
		else
			Console.OUT.println("--------Block matrix Scaling test failed!--------");	
		return ret;
	}



} 