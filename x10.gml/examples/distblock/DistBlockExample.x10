/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2012.
 */

import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistBlockMatrix;

/**
 * Examples of distributed block matrix
 */
public class DistBlockExample {
    public static def main(args:Rail[String]) {
	val testcase = new RunExample(args);
	testcase.run();
    }
}

class RunExample {
    public val nzp:Float;
    public val M:Long;
    public val N:Long;
    public val K:Long;
    public val bM:Long;
    public val bN:Long;
    
    public def this(args:Rail[String]) {
	M = args.size > 0 ? Long.parse(args(0)):30;
	nzp = args.size > 1 ?Float.parse(args(1)):0.9f;
	N = args.size > 2 ? Long.parse(args(2)):(M as Int)+1;
	K = args.size > 3 ? Long.parse(args(3)):(M as Int)+2;
	bM= args.size > 4 ? Long.parse(args(4)):(Place.numPlaces() as Int)+1;
	bN= args.size > 5 ? Long.parse(args(5)):(Place.numPlaces() as Int)+15;
        
	Console.OUT.printf("Matrix M:%d K:%d N:%d, blocks(%d, %d) \n", M, N, K, bM, bN);
    }
    
    public def run ():void {
	Console.OUT.println("Starting dist block matrix clone/add/sub/scaling example run");
	
	var ret:Boolean = true;
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
	
	val a = DistBlockMatrix.makeDense(M, N, bM, bN, Place.numPlaces(), 1).initRandom();
	val b = DistBlockMatrix.makeDense(M, N, bM, bN, Place.numPlaces(), 1).initRandom();
	
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
	val dmap = DistGrid.makeHorizontal(grid).dmap; 
	
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
