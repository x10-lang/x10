/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;

import x10.matrix.Debug;
import x10.matrix.DenseMatrix;

import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;

import x10.matrix.distblock.DupBlockMatrix;


/**
   <p>

   <p>
 */
public class TestDupBlock {
	
    public static def main(args:Array[String](1)) {
		val testcase = new TestDupBlk(args);
		testcase.run();
	}
}

class TestDupBlk {
	public val nzp:Double;
	public val M:Int;
	public val N:Int;
	public val K:Int;
	
	public val bM:Int;
	public val bN:Int;
	public val grid:Grid;
	
    public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):4;
		nzp = args.size > 1 ?Double.parse(args(1)):0.9;
		N = args.size > 2 ?Int.parse(args(2)):4;
		K = args.size > 3 ?Int.parse(args(3)):M+2;
		bM= args.size > 4 ?Int.parse(args(4)):2;
		bN= args.size > 5 ?Int.parse(args(5)):2;

		grid = new Grid(M, N, bM, bN);
		//dmap = DistGrid.make(grid).dmap; 
		Console.OUT.printf("DupMatrix M:%d K:%d N:%d, blocks(%d, %d) \n", M, N, K, bM, bN);
		
	}

    public def run (): void {
		Console.OUT.println("Starting Duplicated block matrix clone/add/sub/scaling tests");

		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testClone());
		ret &= (testSync());
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
		Console.OUT.println("Starting dup block matrix clone test on dense blocks");
		val ddm = DupBlockMatrix.makeDense(M, N, bM, bN).init((r:Int, c:Int)=>(1.0+r+c));
		Debug.flushln("Initialization done");
		//ddm.print();
		//ddm.printMatrix();
		
		val ddm1 = ddm.clone();
		Debug.flushln("Clone done");
		ret = ddm.equals(ddm1);
		Debug.flushln("Equal test done");
		ret &= ddm.checkSync();
		// 
		if (ret)
			Console.OUT.println("DupBlockMatrix Clone test passed!");
		else
		 	Console.OUT.println("--------DupBlockMatrix Clone test failed!--------");
		return ret;
	}

	public def testSync():Boolean {
		var ret:Boolean = true;
		Console.OUT.println("Starting dup block Matrix sync test");
		val dupblk = DupBlockMatrix.makeDense(grid);
		dupblk.init((r:Int,c:Int)=>(1.0*(r+c)%2*(r+c)));
		ret &= dupblk.checkSync();
		if (! ret) return ret;
	
		if (ret)
			Console.OUT.println("Dup dense block matrix copyTo and Sync test passed!");
		else
			Console.OUT.println("--------Dup dense block matrix copyTo and sync test failed!--------");	
		return ret;
	}

 	public def testScale():Boolean{
 		Console.OUT.println("Starting dup block matrix scaling test");
 		val dm = DupBlockMatrix.makeDense(grid).initRandom();

 		val dm1  = dm * 2.5;
 		val m = dm.toDense();
 		val m1 = m * 2.5;
 		val ret = dm1.equals(m1);
 		dm1.checkSync();
 		if (ret)
 			Console.OUT.println("Dup block Matrix scaling test passed!");
 		else
 			Console.OUT.println("--------Dup block matrix Scaling test failed!--------");	
 		return ret;
 	}
 
	public def testAdd():Boolean {
		Console.OUT.println("Starting Dup block dense matrix add test");
		val dm = DupBlockMatrix.makeDense(grid).initRandom();

		val dm1:DupBlockMatrix = dm  * -1.0;
		val dm0 = dm + dm1;
		var ret:Boolean = dm0.equals(0.0);
		ret &= dm1.checkSync();
		ret &= dm0.checkSync();
		if (ret)
			Console.OUT.println("DistBlockMatrix Add: dm + dm*-1 test passed");
		else
			Console.OUT.println("--------DistBlockMatrix Add: dm + dm*-1 test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Starting Duplicate Block Matrix add-sub test");
		val dm = DupBlockMatrix.makeDense(grid).initRandom();
		val dm1= DupBlockMatrix.makeDense(grid).initRandom();
		//sp.print("Input:");
		val dm2   = dm  + dm1;
		val dm_c  = dm2 - dm1;
		
		val ret   = dm.equals(dm_c as Matrix(dm.M, dm.N));
		//sp_c.print("Another add result:");
		if (ret)
			Console.OUT.println("DupBlockMatrix Add-sub test passed!");
		else
			Console.OUT.println("--------DupBlockMatrix Add-sub test failed!--------");
		return ret;
	}


	public def testAddAssociative():Boolean {
		Console.OUT.println("Starting dup block matrix associative test");
		var ret:Boolean=true;
		val a = DupBlockMatrix.makeDense(grid).init((r:Int,c:Int)=>1.0*((r+c)));
		val b = DupBlockMatrix.makeDense(grid).init((r:Int,c:Int)=>1.0*((r+c)));;
		val c = DupBlockMatrix.makeSparse(grid, nzp).init((r:Int,c:Int)=>1.0*((r+c)%3));
		val c1 = a + b + c;
		ret &= c1.checkSync();
		val c2 = a + (b + c);
		ret &= c2.checkSync();
		ret &= c1.equals(c2);
		if (ret)
			Console.OUT.println("DupBlockMatrix Add associative test passed!");
		else
			Console.OUT.println("--------DupBlockMatrix Add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Starting duplicated block Matrix scaling-add test");
		var ret:Boolean = true;
		val a = DupBlockMatrix.makeDense(grid).initRandom();

		val m = a.toDense();
		val a1= a * 0.2;
		val a2= 0.8 * a;
		ret &= a.equals(a1+a2);
		ret &= a.equals(m);

		if (ret)
			Console.OUT.println("DupBlockMatrix scaling-add test passed!");
		else
			Console.OUT.println("--------DupBlockeMatrix scaling-add test failed!--------");
		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Starting duplicated block Matrix cellwise mult test");
		var ret:Boolean = true;
		val a = DupBlockMatrix.makeDense(grid).initRandom();
		val b = DupBlockMatrix.makeDense(grid).initRandom();

		val c = (a + b) * a;
		ret &= c.checkSync();
		val d = a * a + b * a;
		ret &= c.equals(d);
		ret &= c.checkSync();

		val da= a.toDense();
		val db= b.toDense();
		val dc= (da + db) * da;
		ret &= dc.equals(c);

		if (ret)
			Console.OUT.println("Duuplicated block Matrix cellwise mult passed!");
		else
			Console.OUT.println("--------Duuplicated block matrix cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Starting DistBlockMatrix cellwise mult-div test");

		val a = DupBlockMatrix.makeDense(grid).initRandom();
		val b = DupBlockMatrix.makeDense(grid).initRandom();

		val c = (a + b) * a;
		val d =  c / (a + b);
		var ret:Boolean = d.equals(a);
		ret &= d.checkSync();

		if (ret)
			Console.OUT.println("Duplicated block Matrix cellwise mult-div passed!");
		else
			Console.OUT.println("--------Duplicated block matrix cellwise mult-div test failed!--------");
		return ret;
	}



} 
