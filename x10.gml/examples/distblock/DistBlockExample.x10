/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2012.
 */

import x10.io.Console;

import x10.matrix.Debug;
import x10.matrix.DenseMatrix;

import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistBlockMatrix;

/**
   <p>
 * Examples of distributed block matrix
   <p>
 */
public class DistBlockExample {
	
    public static def main(args:Array[String](1)) {
		val testcase = new RunExample(args);
		testcase.run();
	}
}

class RunExample {
	public val nzp:Double;
	public val M:Int;
	public val N:Int;
	public val K:Int;
	public val bM:Int;
	public val bN:Int;


    public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):30;
		nzp = args.size > 1 ?Double.parse(args(1)):0.9;
		N = args.size > 2 ?Int.parse(args(2)):M+1;
		K = args.size > 3 ?Int.parse(args(3)):M+2;
		bM= args.size > 4 ?Int.parse(args(4)):Place.MAX_PLACES+1;
		bN= args.size > 5 ?Int.parse(args(5)):Place.MAX_PLACES+15;
		
		Console.OUT.printf("Matrix M:%d K:%d N:%d, blocks(%d, %d) \n", M, N, K, bM, bN);
		
	}

    public def run ():void {
    	
		Console.OUT.println("Starting dist block matrix clone/add/sub/scaling example run");

		var ret:Boolean = true;
 		// Set the matrix function
		exampleAddSub();
		exampleAddAssociative();
		exampleCellMult();
		exampleCellDiv();

	}


	public def exampleAddSub():Boolean {
		Console.OUT.println("Starting DistBlockMatrix add-sub example");
		val dm = DistBlockMatrix.makeDense(M, N, bM, bN).initRandom();
		val dm1= DistBlockMatrix.makeDense(M, N, bM, bN).initRandom();
		//sp.print("Input:");
		val dm2   = dm  + dm1;
		//
		val dm_c  = dm2 - dm1;
		val ret   = dm.equals(dm_c as Matrix(dm.M, dm.N));
		//sp_c.print("Another add result:");
		if (ret)
			Console.OUT.println("DistBlockMatrix Add-sub example passed!");
		else
			Console.OUT.println("--------DistBlockMatrix Add-sub example failed!--------");
		return ret;
	}


	public def exampleAddAssociative():Boolean {
		Console.OUT.println("Starting dist block matrix associative example");
		val grid = new Grid(M, N, bM, bN);
		val dmap = DistGrid.make(grid).dmap; 

		val a = DistBlockMatrix.makeDense(grid, dmap).initRandom();
		val b = DistBlockMatrix.makeDense(grid, dmap).initRandom();;
		val c = DistBlockMatrix.makeSparse(grid, dmap, nzp).initRandom();

		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (ret)
			Console.OUT.println("DistBlockMatrix Add associative example passed!");
		else
			Console.OUT.println("--------DistBlockMatrix Add associative example failed!--------");
		return ret;
	}

	public def exampleCellMult():Boolean {
		Console.OUT.println("Starting dist block Matrix cellwise mult example");

		val a = DistBlockMatrix.makeDense(M, N, bM, bN, Place.MAX_PLACES, 1).initRandom();
		val b = DistBlockMatrix.makeDense(M, N, bM, bN, Place.MAX_PLACES, 1).initRandom();

		val c = (a + b) * a;
		val d = a * a + b * a;
		var ret:Boolean = c.equals(d);

		val da= a.toDense();
		val db= b.toDense();
		val dc= (da + db) * da;
		ret &= dc.equals(c);

		if (ret)
			Console.OUT.println("Dist block Matrix cellwise mult examplepassed!");
		else
			Console.OUT.println("--------Dist block matrix cellwise mult example failed!--------");
		return ret;
	}

	public def exampleCellDiv():Boolean {
		Console.OUT.println("Starting DistBlockMatrix cellwise mult-div example");
		val grid = new Grid(M, N, bM, bN);
		val dmap = DistGrid.makeHorizon(grid).dmap; 

		val a = DistBlockMatrix.makeDense(grid, dmap).initRandom();
		val b = DistBlockMatrix.makeDense(grid, dmap).initRandom();

		val c = (a + b) * a;
		val d =  c / (a + b);
		var ret:Boolean = d.equals(a);

		if (ret)
			Console.OUT.println("Dist block Matrix cellwise mult-div example passed!");
		else
			Console.OUT.println("--------Dist block matrix cellwise mult-div example failed!--------");
		return ret;
	}



} 
