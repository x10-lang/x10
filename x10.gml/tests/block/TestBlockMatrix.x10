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
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.Grid;

import x10.matrix.block.BlockMatrix;
import x10.matrix.block.MatrixBlock;

/**
   <p>

   <p>
 */
public class TestBlockMatrix {
	
    public static def main(args:Rail[String]) {
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

    public def this(args:Rail[String]) {
		M = args.size > 0 ?Int.parse(args(0)):50;
		N = args.size > 1 ?Int.parse(args(1)):M+2;
		R = args.size > 2 ?Int.parse(args(2)):2;
		C = args.size > 3 ?Int.parse(args(3)):3;
		nzd =  args.size > 4 ?Double.parse(args(4)):0.99;
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
		ret &= (testAddSub());
		ret &= (buildBlockMap());
		
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
		if (!ret) Console.OUT.println("--------------Block matrix clone test failed------------");
		
		val dm = DenseMatrix.make(grid.M, grid.N);
		sbm.copyTo(dm);
		//dm2.print("Dense");
		ret &= sbm.equals(dm as Matrix(M,N));
		if (!ret) Console.OUT.println("--------------Block matrix dense conversion test failed------------");

		if (ret)
			Console.OUT.println("Block matrix Clone and dense conversion test passed!");
		else
			Console.OUT.println("--------Block matrix clone and dense conversion test failed!--------");
	
		//must be nonzero positions
		sbm(0, 1) = sbm(M/2,N/3) = 10.0;

		if ((sbm(0,1)==sbm(M/2,N/3)) && (sbm(0,1)==10.0)) {
			ret &= true;
			Console.OUT.println("Block Matrix chained assignment test passed!");
		} else {
			ret &= false;
			Console.OUT.println("---------- Block Matrix chained assignment test failed!-------");
		}

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

	public def testAddSub():Boolean {
		Console.OUT.println("Starting block matrix add test");
		val dm = BlockMatrix.makeDense(grid).initRandom();
		val sm = BlockMatrix.makeSparse(grid, nzd).initRandom();

		val dm1 = dm * -1.0;
		val dm0 = dm + dm1;
		val ret = dm0.equals(0.0);
		Debug.assure(ret);
		
		sm.copyTo(dm);
		val dm00 = dm - sm;
		
		if (ret)
			Console.OUT.println("Block matrix dense block Add: dm + dm*-1 test passed");
		else
			Console.OUT.println("--------Block matrix dense block Add: dm + dm*-1 test failed--------");
		
		return ret;
	}

	public def buildBlockMap():Boolean {
		Console.OUT.println("Start building block 2D map");
		val dbm = BlockMatrix.makeDense(grid);
		dbm.init((r:Int,c:Int)=>1.0*(r+c));
		dbm.buildBlockMap();
		
		var retval:Boolean= true;
		for (var r:Int=0; r<grid.numRowBlocks &&retval; r++)
			for (var c:Int=0; c<grid.numColBlocks && retval; c++) {
				//Console.OUT.println("Block map at ("+r+","+c+")");
				//dbm.blockMap(r,c).getMatrix().printMatrix();
				//dbm.findBlock(r,c).getMatrix().printMatrix();
				
				retval &= (dbm.findBlock(r,c) == dbm.blockMap(r,c)); 
			}
		return retval;
	}
} 
