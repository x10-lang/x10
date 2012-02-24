/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;
import x10.util.Timer;
import x10.array.DistArray;


import x10.matrix.Matrix;
import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.CastPlaceMap;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;

import x10.matrix.distblock.summa.AllGridReduce;

/**
   This class contains test cases P2P communication for matrix over different places.
   <p>

   <p>
 */

public class TestGridReduce{
    public static def main(args:Array[String](1)) {
		val m = args.size > 0 ?Int.parse(args(0)):4;
		val n = args.size > 1 ?Int.parse(args(1)):5;
		val bm= args.size > 2 ?Int.parse(args(2)):4;
		val bn= args.size > 3 ?Int.parse(args(3)):5;
		val d = args.size > 4 ? Double.parse(args(4)):0.9;
		val testcase = new GridReduceTest(m, n, bm, bn, d);
		testcase.run();
	}
}


class  GridReduceTest {

	public val M:Int;
	public val N:Int;
	public val nzdensity:Double;
	public val bM:Int;
	public val bN:Int;
	public val pM:Int;
	public val pN:Int;
	
	public val numplace:Int;

	public val dbmat:DistBlockMatrix;
	public val sbmat:DistBlockMatrix;

	public partgrid:Grid;
	public distmap:DistMap;
	public distgrid:DistGrid;
	
    public def this(m:Int, n:Int, bm:Int, bn:Int, d:Double) {

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
	
	public def run(): void {
 		// Set the matrix function
		var retval:Boolean = true;
		Console.OUT.println("Matrix dims:"+M+","+N);
		Console.OUT.println("Partitioning grid:"+bM+"x"+bN);
		Console.OUT.println("Distribution grid:"+pM+"x"+pN);
		retval &= testRowReduceSum(dbmat);
 		retval &= testColReduceSum(dbmat);
		if (retval)
			Console.OUT.println("Block communication test grid-reduce commu passed!");
		else
			Console.OUT.println("------------Block communication test collective grid-reduce failed!-----------");
	}
	//------------------------------------------------

	
	public def testRowReduceSum(distmat:DistBlockMatrix):Boolean {
		Console.OUT.printf("\nTest row-wise reduce of front blocks on %d places\n", numplace);
		var retval:Boolean = true;
		val tmp   = distmat.makeTempFrontRowBlocks(1);		
		val work1 = distmat.makeTempFrontRowBlocks(1);
		distmat.reset();
		
		for (var colId:Int=0; colId<partgrid.numColBlocks; colId++) {
			initFrontBlocks(1.0, work1);
			finish AllGridReduce.startRowReduceSum(0, 1, colId, distmat, work1, tmp);
						
			Debug.flushln("Done row-wise cast from column block "+colId+" over "+pN+" places row-wise");
		}
		
		retval &= distmat.equals(pN as Double);//verifyRowReduceSum(pN as Double, 1, colId, work1);
		if (!retval) distmat.printMatrix();
		if (retval)
			Console.OUT.println("Test ring reduce row-wise for dist block matrix test passed!");
		else
			Console.OUT.println("-----Test ring reduce row-wise for dist block matrix failed!-----");
		return retval;
	}
	
	public def testColReduceSum(distmat:DistBlockMatrix):Boolean {
		Console.OUT.printf("\nTest col-wise reduce of front blocks on %d places\n", numplace);
		var retval:Boolean = true;
		val grid = distmat.getGrid();
		val dmap = distmat.getMap();
		val tmp   = distmat.makeTempFrontColBlocks(1);
		
		val work2 = distmat.makeTempFrontColBlocks(1);
		
		for (var rowId:Int=0; rowId<grid.numRowBlocks&&retval; rowId++) {
			
			initFrontBlocks(1.0, work2);
			
			finish AllGridReduce.startColReduceSum(0, 1, rowId, work2, tmp);
			
			Debug.flushln("Done col-wise cast from row block "+rowId+" over "+pM+" places column-wise");
			retval &= verifyColReduceSum(pM as Double, 1, rowId, work2);
			Debug.flushln("Done verification from root block with row block Id:"+rowId);
		}
		
		if (retval)
			Console.OUT.println("Test ring reduce col-wise for dist block matrix test passed!");
		else
			Console.OUT.println("-----Test ring reduce col-wise for dist block matrix failed!-----");
		return retval;
	}

	//===============================================
	public static def initFrontBlocks(dv:Double, work:PlaceLocalHandle[BlockSet]) {
		finish ateach (Dist.makeUnique()) {
			val itr = work().iterator();
			while (itr.hasNext()) {
				val blk = itr.next();
				blk.init(dv);
			}
		}		
	}
	
	public static def verifyRowReduceSum(dv:Double, ccnt:Int, rowId:Int, work1:PlaceLocalHandle[BlockSet]) =
		verifyReduceSum(dv, ccnt, rowId, work1, (r:Int,c:Int)=>r);
	public static def verifyColReduceSum(dv:Double, ccnt:Int, colId:Int, work2:PlaceLocalHandle[BlockSet]) =
		verifyReduceSum(dv, ccnt, colId, work2, (r:Int,c:Int)=>c);

	
	public static def verifyReduceSum(dv:Double, colCnt:Int, id:Int, work:PlaceLocalHandle[BlockSet], sel:(r:Int,c:Int)=>Int):Boolean {
		var retval:Boolean = true;
		for (var p:Int=0; p<Place.MAX_PLACES&&retval; p++) {
			val pid = p;
			retval &= at (Dist.makeUnique()(pid)) {
				var ret:Boolean = true;
				val itr = work().iterator();
				while (itr.hasNext()&&ret) {
					val blk = itr.next();
					val tgt = sel(blk.myColId, blk.myRowId);
					if (tgt == id){
						val datbuf = blk.getData();
						val mat = blk.getMatrix();
						for (var c:Int=0; c<colCnt*mat.M&&ret; c++){ 
							ret &= datbuf(c)== dv;
							if (!ret) 
								Debug.flushln("Inconsistancy found\n"+mat.dataToString());
						}
					}
				}
				ret
			};
		}
		return retval;
	}
}