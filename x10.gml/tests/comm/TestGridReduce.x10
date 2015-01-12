/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */

import harness.x10Test;

import x10.compiler.Ifndef;
import x10.regionarray.Dist;

import x10.matrix.util.Debug;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.summa.AllGridReduce;

public class TestGridReduce extends x10Test {
	public val M:Long;
	public val N:Long;
	public val nzdensity:Double;
	public val bM:Long;
	public val bN:Long;
	public val pM:Long;
	public val pN:Long;
	
	public val numplace:Long;

	public val dbmat:DistBlockMatrix;
	public val sbmat:DistBlockMatrix;

	public partgrid:Grid;
	public distmap:DistMap;
	public distgrid:DistGrid;
	
    public def this(m:Long, n:Long, bm:Long, bn:Long, d:Double) {
		M=m; N=n;
		nzdensity = d;
		bM = bm; bN = bn;
		
		partgrid = new Grid(m, n, bM, bN);
		distgrid = DistGrid.make(partgrid);
		distmap  = distgrid.dmap;
		pM = distgrid.numRowPlaces;
		pN = distgrid.numColPlaces;
		
		dbmat = DistBlockMatrix.makeDense(partgrid, distmap);
		sbmat = DistBlockMatrix.makeSparse(partgrid, distmap, nzdensity);
		
		numplace = Place.numPlaces();
	}
	
    public def run():Boolean {
		var retval:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!
		retval &= testRowReduceSum(dbmat);
 		retval &= testColReduceSum(dbmat);
    }
        return retval;
	}
	
	public def testRowReduceSum(distmat:DistBlockMatrix):Boolean {
		Console.OUT.printf("\nTest row-wise reduce of front column blocks on %d places\n", numplace);
		var retval:Boolean = true;
		val tmp   = distmat.makeTempFrontColBlocks(1);		
		val work1 = distmat.makeTempFrontColBlocks(1);
		distmat.reset();
		
		for (var colId:Long=0; colId<partgrid.numColBlocks; colId++) {
			initFrontBlocks(1.0, work1);
			finish AllGridReduce.startRowReduceSum(0, 1, colId, distmat, work1, tmp);			
			//Debug.flushln("Done row-wise cast from column block "+colId+" over "+pN+" places row-wise");
		}
		
		retval &= distmat.equals(pN as Double);//verifyRowReduceSum(pN as Double, 1, colId, work1);
		if (!retval) Console.OUT.println(distmat);
		if (!retval)
			Console.OUT.println("-----Test ring reduce row-wise for dist block matrix failed!-----");
		return retval;
	}
	
	public def testColReduceSum(distmat:DistBlockMatrix):Boolean {
		Console.OUT.printf("\nTest col-wise reduce of front blocks on %d places\n", numplace);
		var retval:Boolean = true;
		val grid = distmat.getGrid();
		val dmap = distmat.getMap();
		val tmp   = distmat.makeTempFrontRowBlocks(1);
		val work2 = distmat.makeTempFrontRowBlocks(1);
		
		for (var rowId:Long=0; rowId<grid.numRowBlocks; rowId++) {
			initFrontBlocks(1.0, work2);
			finish AllGridReduce.startColReduceSum(0, 1, rowId, distmat, work2, tmp);
			//Debug.flushln("Done col-wise cast from row block "+rowId+" over "+pM+" places column-wise");

		}
		retval &= distmat.equals(pN as Double);

		if (!retval)
			Console.OUT.println("-----Test ring reduce col-wise for dist block matrix failed!-----");
		return retval;
	}

	public static def initFrontBlocks(dv:Double, work:PlaceLocalHandle[BlockSet]) {
		finish ateach(Dist.makeUnique()) {
			val itr = work().iterator();
			while (itr.hasNext()) {
				val blk = itr.next();
				blk.init(dv);
			}
		}		
	}

    public static def main(args:Rail[String]) {
		val m = args.size > 0 ? Long.parse(args(0)):4;
		val n = args.size > 1 ? Long.parse(args(1)):5;
		val bm= args.size > 2 ? Long.parse(args(2)):4;
		val bn= args.size > 3 ? Long.parse(args(3)):5;
		val d = args.size > 4 ? Double.parse(args(4)):0.9;
		new TestGridReduce(m, n, bm, bn, d).execute();
	}
}
