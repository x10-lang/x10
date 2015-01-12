/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2012-2015.
 */

import harness.x10Test;

import x10.compiler.Ifndef;

import x10.matrix.util.Debug;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.summa.AllGridCast;

public class TestGridCast extends x10Test {
	public val M:Long;
	public val N:Long;
	public val nzdensity:Double;
	public val bM:Long;
	public val bN:Long;
	
	public val numplace:Long;

	public val dbmat:DistBlockMatrix;
	public val sbmat:DistBlockMatrix;
	public val tmpmat:DistBlockMatrix;

	public val dblks:BlockMatrix;
	public val sblks:BlockMatrix;
	
	public val rootbid:Long = 0;
	
    public def this(m:Long, n:Long, bm:Long, bn:Long, d:Double) {
		M=m; N=n;
		nzdensity = d;
		bM = bm; bN = bn;
				
		dbmat = DistBlockMatrix.makeDense(m*bm, n*bn, bm, bn);
		tmpmat = DistBlockMatrix.makeDense(m*bm, n*bn, bm, bn);
		sbmat = DistBlockMatrix.makeSparse(m*bm, n*bn, bm, bn, nzdensity);
		
		dbmat.initBlock(rootbid, (x:Long, y:Long)=>(1.0+x+y));
		sbmat.initBlock(rootbid, (x:Long, y:Long)=>1.0*(x+y)*((x+y)%2));
		
		dblks = BlockMatrix.makeDense(dbmat.getGrid());
		sblks = BlockMatrix.makeSparse(sbmat.getGrid(), nzdensity);
		
		numplace = Place.numPlaces();
	}
	
    public def run():Boolean {
		var retval:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!
		Console.OUT.println("****************************************************************");
		Console.OUT.println("Test dense blocks collective commu in distributed block matrix");
		Console.OUT.println("****************************************************************");


	 	retval &= testGridRowCast(dbmat);
	 	retval &= testGridColCast(dbmat);

		Console.OUT.println("****************************************************************");
		Console.OUT.println("Test sparse blocks collective commu in distributed block matrix");	
		Console.OUT.println("****************************************************************");

		retval &= testGridRowCast(sbmat);
		retval &= testGridColCast(sbmat);		
    }
        return retval;
	}

	public def testGridRowCast(distmat:DistBlockMatrix):Boolean {
		Console.OUT.printf("Test row-wise cast of dist block matrix over %d places\n", numplace);
		var retval:Boolean = true;
		val grid = distmat.getGrid();
		val dmap = distmat.getMap();
		
		val tmp = distmat.makeTempFrontColBlocks(1);
 		distmat.init((r:Long,c:Long)=>1.0*(r+c));
 		
		for (var colId:Long=0; colId<grid.numColBlocks&&retval; colId++) {
			finish AllGridCast.startRowCast(0, 1, colId, distmat, tmp);
			retval &= AllGridCast.startVerifyRowCast(0, 1, colId, distmat, tmp);
		}

		if (!retval)
			Console.OUT.println("-----Test row-wise cast for dist block matrix failed!-----");
		return retval;
	}
	
	public def testGridColCast(distmat:DistBlockMatrix):Boolean {
		Console.OUT.printf("Test ring cast column-wise of dist block matrix over %d places\n", numplace);
		
		var retval:Boolean = true;
		val grid = distmat.getGrid();
		val dmap = distmat.getMap();
		val tmp = distmat.makeTempFrontRowBlocks(1);
		distmat.init((r:Long,c:Long)=>1.0*(r+c)%2);
		
		for (var rowId:Long=0; rowId < grid.numRowBlocks&&retval; rowId++) {
			finish AllGridCast.startColCast(0, 1, rowId, distmat, tmp);
			retval &= AllGridCast.startVerifyColCast(0, 1, rowId, distmat, tmp);
		}
				
		if (!retval)
			Console.OUT.println("-----Test column-wise cast for dist block matrix failed!-----");
		return retval;
	}

    public static def main(args:Rail[String]) {
		val m = args.size > 0 ? Long.parse(args(0)):2;
		val n = args.size > 1 ? Long.parse(args(1)):2;
		val bm= args.size > 2 ? Long.parse(args(2)):2;
		val bn= args.size > 3 ? Long.parse(args(3)):3;
		val d = args.size > 4 ? Double.parse(args(4)):0.9;
		new TestGridCast(m, n, bm, bn, d).execute();
	}
}
