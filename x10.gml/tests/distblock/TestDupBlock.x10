/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

import harness.x10Test;

import x10.compiler.Ifndef;

import x10.matrix.util.Debug;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DupBlockMatrix;

public class TestDupBlock extends x10Test {
	public val nzp:Double;
	public val M:Long;
	public val N:Long;
	public val K:Long;
	
	public val bM:Long;
	public val bN:Long;
	public val grid:Grid;
	
    public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):4;
		nzp = args.size > 1 ?Double.parse(args(1)):0.9;
		N = args.size > 2 ? Long.parse(args(2)):4;
		K = args.size > 3 ? Long.parse(args(3)):(M as Int)+2;
		bM= args.size > 4 ? Long.parse(args(4)):2;
		bN= args.size > 5 ? Long.parse(args(5)):2;

		grid = new Grid(M, N, bM, bN);
		//dmap = DistGrid.make(grid).dmap; 
		Console.OUT.printf("DupBlockMatrix M:%d K:%d N:%d, blocks(%d, %d) \n", M, N, K, bM, bN);
		
	}

    public def run():Boolean {
		Console.OUT.println("Starting Duplicated block matrix clone/add/sub/scaling tests");

		var ret:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!
		ret &= (testClone());
		ret &= (testSync());
		ret &= (testScale());
		ret &= (testAdd());
		ret &= (testAddSub());
		ret &= (testAddAssociative());
		ret &= (testScaleAdd());
		ret &= (testCellMult());
		ret &= (testCellDiv());
    }
        return ret;
	}
	public def testClone():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Starting dup block matrix clone test on dense blocks");
		val ddm = DupBlockMatrix.makeDense(M, N, bM, bN).init((r:Long, c:Long)=>(1.0+r+c));
		
		val ddm1 = ddm.clone();
		ret = ddm.equals(ddm1);
		ret &= ddm.checkSync();
		if (!ret)
		 	Console.OUT.println("--------DupBlockMatrix Clone test failed!--------");
		return ret;
	}

	public def testSync():Boolean {
		var ret:Boolean = true;
		Console.OUT.println("Starting dup block Matrix sync test");
		val dupblk = DupBlockMatrix.makeDense(grid);
		dupblk.init((r:Long,c:Long)=>(1.0*(r+c)%2*(r+c)));
		ret &= dupblk.checkSync();
	
		if (!ret)
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
 		if (!ret)
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
		if (!ret)
			Console.OUT.println("--------DistBlockMatrix Add: dm + dm*-1 test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Starting Duplicate Block Matrix add-sub test");
		val dm = DupBlockMatrix.makeDense(grid).initRandom();
		val dm1= DupBlockMatrix.makeDense(grid).initRandom();
		val dm2   = dm  + dm1;
		val dm_c  = dm2 - dm1;
		
		val ret = dm.equals(dm_c as Matrix(dm.M, dm.N));
		if (!ret)
			Console.OUT.println("--------DupBlockMatrix Add-sub test failed!--------");
		return ret;
	}


	public def testAddAssociative():Boolean {
		Console.OUT.println("Starting dup block matrix associative test");
		var ret:Boolean=true;
		val a = DupBlockMatrix.makeDense(grid).init((r:Long,c:Long)=>1.0*((r+c)));
		val b = DupBlockMatrix.makeDense(grid).init((r:Long,c:Long)=>1.0*((r+c)));;
		val c = DupBlockMatrix.makeSparse(grid, nzp).init((r:Long,c:Long)=>1.0*((r+c)%3));
		val c1 = a + b + c;
		ret &= c1.checkSync();
		val c2 = a + (b + c);
		ret &= c2.checkSync();
		ret &= c1.equals(c2);
		if (!ret)
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

		if (!ret)
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

		if (!ret)
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

		if (!ret)
			Console.OUT.println("--------Duplicated block matrix cellwise mult-div test failed!--------");
		return ret;
	}

    public static def main(args:Rail[String]) {
		new TestDupBlock(args).execute();
	}
}
