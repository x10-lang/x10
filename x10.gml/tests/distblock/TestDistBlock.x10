/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;

import x10.matrix.Debug;
import x10.matrix.DenseMatrix;

import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;

import x10.matrix.distblock.DistBlockMatrix;


/**
   <p>

   <p>
 */
public class TestDistBlock {
	
    public static def main(args:Array[String](1)) {
		val testcase = new TestDB(args);
		testcase.run();
	}
}

class TestDB {
	public val nzp:Double;
	public val M:Int;
	public val N:Int;
	public val K:Int;
	public val bM:Int;
	public val bN:Int;

	public val grid:Grid;
	public val dmap:DistMap;
	
    public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):30;
		nzp = args.size > 1 ?Double.parse(args(1)):0.9;
		N = args.size > 2 ?Int.parse(args(2)):M+1;
		K = args.size > 3 ?Int.parse(args(3)):M+2;
		bM= args.size > 4 ?Int.parse(args(4)):4;
		bN= args.size > 5 ?Int.parse(args(5)):5;
		
		grid = new Grid(M, N, bM, bN);
		dmap = DistGrid.make(grid).dmap; 
		Console.OUT.printf("Matrix M:%d K:%d N:%d, blocks(%d, %d) \n", M, N, K, bM, bN);
		
	}

    public def run (): void {
		Console.OUT.println("Starting dist block matrix clone/add/sub/scaling tests");

		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testClone());
		//ret &= (testCopyTo());
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
		Console.OUT.println("Starting dist block matrix clone test on dense blocks");
		val ddm = DistBlockMatrix.make(grid, dmap).allocDenseBlocks().init((r:Int, c:Int)=>(1.0+r+c));
		Debug.flushln("Initialization done");
		//ddm.print();
		//ddm.printMatrix();
		
		val ddm1 = ddm.clone();
		Debug.flushln("Clone done");
		ret = ddm.equals(ddm1);
		Debug.flushln("Equal test done");
		
		val den = DenseMatrix.make(grid.M, grid.N).init((r:Int,c:Int)=>(1.0+r+c));
		ret &= den.equals(ddm);
		Debug.flushln("Test initial func");
		
		// 
		if (ret)
			Console.OUT.println("DistBlockMatrix Clone test passed!");
		else
		 	Console.OUT.println("--------DistBlockMatrix Clone test failed!--------");
		return ret;
	}

// 	public def testCopyTo():Boolean {
// 		var ret:Boolean = true;
// 		Console.OUT.println("Starting dist dense Matrix copyTo test");
// 		val ddm = DistDenseMatrix.make(M, N);
// 		val dbm = DenseBlockMatrix.make(ddm.grid);
// 		val dm  = DenseMatrix.make(M,N);
// 
// 		Debug.flushln("Starting initialization");
// 		ddm.initRandom();
// 		Debug.flushln("Initialization done");
// 
// 		ddm.copyTo(dbm);
// 		Debug.flushln("Convert to dense block matrix done");
// 		ret &= ddm.equals(dbm);
// 		Debug.flushln("Verify copyTo dense block matrix  done");
// 		
// 		dbm.copyTo(dm);
// 		Debug.flushln("Convert to dense matrix done");
// 		ret &= ddm.equals(dbm);
// 		Debug.flushln("Verify copyTo dense matrix done");
// 
// 		if (ret)
// 			Console.OUT.println("Dist dense Matrix copyTo test passed!");
// 		else
// 			Console.OUT.println("--------Dist dense matrix copyTo test failed!--------");	
// 		return ret;
// 	}
// 
 	public def testScale():Boolean{
 		Console.OUT.println("Starting dist block matrix scaling test");
 		val dm = DistBlockMatrix.make(M, N, bM, bN).allocDenseBlocks().initRandom();

 		val dm1  = dm * 2.5;
 		val m = dm.toDense();
 		val m1 = m * 2.5;
 		val ret = dm1.equals(m1);
 		if (ret)
 			Console.OUT.println("Dist block Matrix scaling test passed!");
 		else
 			Console.OUT.println("--------Dist block matrix Scaling test failed!--------");	
 		return ret;
 	}
 
	public def testAdd():Boolean {
		Console.OUT.println("Starting dist block dense matrix add test");
		val dm = DistBlockMatrix.make(M, N, bM, bN).allocDenseBlocks().initRandom();

		val dm1 = dm  * -1.0;
		val dm0 = dm + dm1;
		val ret = dm0.equals(0.0);
		if (ret)
			Console.OUT.println("DistBlockMatrix Add: dm + dm*-1 test passed");
		else
			Console.OUT.println("--------DistBlockMatrix Add: dm + dm*-1 test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Starting DistBlockMatrix add-sub test");
		val dm = DistBlockMatrix.makeDenseBlocks(grid, dmap).initRandom();
		val dm1= DistBlockMatrix.makeDenseBlocks(grid, dmap).initRandom();
		//sp.print("Input:");
		val dm2   = dm  + dm1;
		//
		val dm_c  = dm2 - dm1;
		val ret   = dm.equals(dm_c as Matrix(dm.M, dm.N));
		//sp_c.print("Another add result:");
		if (ret)
			Console.OUT.println("DistBlockMatrix Add-sub test passed!");
		else
			Console.OUT.println("--------DistBlockMatrix Add-sub test failed!--------");
		return ret;
	}


	public def testAddAssociative():Boolean {
		Console.OUT.println("Starting dist block matrix associative test");

		val a = DistBlockMatrix.makeDenseBlocks(grid, dmap).initRandom();
		val b = DistBlockMatrix.makeDenseBlocks(grid, dmap).initRandom();;
		val c = DistBlockMatrix.makeSparseBlocks(grid, dmap, nzp).initRandom();

		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (ret)
			Console.OUT.println("DistBlockMatrix Add associative test passed!");
		else
			Console.OUT.println("--------DistBlockMatrix Add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Starting dist block Matrix scaling-add test");

		val a = DistBlockMatrix.makeDenseBlocks(grid, dmap).initRandom();

		val m = a.toDense();
		val a1= a * 0.2;
		val a2= 0.8 * a;
		var ret:Boolean = a.equals(a1+a2);
		ret &= a.equals(m);

		if (ret)
			Console.OUT.println("DistBlockMatrix scaling-add test passed!");
		else
			Console.OUT.println("--------DistBlockeMatrix scaling-add test failed!--------");
		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Starting dist block Matrix cellwise mult test");

		val a = DistBlockMatrix.makeDenseBlocks(grid, dmap).initRandom();
		val b = DistBlockMatrix.makeDenseBlocks(grid, dmap).initRandom();

		val c = (a + b) * a;
		val d = a * a + b * a;
		var ret:Boolean = c.equals(d);

		val da= a.toDense();
		val db= b.toDense();
		val dc= (da + db) * da;
		ret &= dc.equals(c);

		if (ret)
			Console.OUT.println("Dist block Matrix cellwise mult passed!");
		else
			Console.OUT.println("--------Dist block matrix cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Starting DistBlockMatrix cellwise mult-div test");

		val a = DistBlockMatrix.makeDenseBlocks(grid, dmap).initRandom();
		val b = DistBlockMatrix.makeDenseBlocks(grid, dmap).initRandom();

		val c = (a + b) * a;
		val d =  c / (a + b);
		var ret:Boolean = d.equals(a);

		if (ret)
			Console.OUT.println("Dist block Matrix cellwise mult-div passed!");
		else
			Console.OUT.println("--------Dist block matrix cellwise mult-div test failed!--------");
		return ret;
	}



} 
