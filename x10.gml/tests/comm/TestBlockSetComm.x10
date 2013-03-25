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
import x10.matrix.distblock.DupBlockMatrix;		

import x10.matrix.comm.BlockSetBcast;
import x10.matrix.comm.BlockSetRemoteCopy;
import x10.matrix.comm.BlockSetReduce;

/**
   This class contains test cases P2P communication for matrix over different places.
   <p>

   <p>
 */

public class TestBlockSetComm{
    public static def main(args:Rail[String]) {
		val m = args.size > 0 ?Int.parse(args(0)):40;
		val n = args.size > 1 ?Int.parse(args(1)):40;
		val bm= args.size > 2 ?Int.parse(args(2)):3;
		val bn= args.size > 3 ?Int.parse(args(3)):7;
		val d = args.size > 4 ? Double.parse(args(4)):0.99;
		val testcase = new BlockSetCommTest(m, n, bm, bn, d);
		testcase.run();
	}
}


class BlockSetCommTest {

	public val M:Int;
	public val N:Int;
	public val nzdensity:Double;
	public val bM:Int;
	public val bN:Int;
	public val grid:Grid;
	
	public val numplace:Int;

	public val dupden:DupBlockMatrix;
	public val dupspa:DupBlockMatrix;
	
	public val dblks:BlockMatrix;
	public val sblks:BlockMatrix;
	
	public val rootbid:Int = 0;
	
    public def this(m:Int, n:Int, bm:Int, bn:Int, d:Double) {

		M=m; N=n;
		nzdensity = d;
		bM = bm; bN = bn;
		grid = new Grid(m, n, bm, bn);
		
		dupden = DupBlockMatrix.makeDense(m, n, bm, bn);
		dupspa = DupBlockMatrix.makeSparse(m, n, bm, bn, nzdensity);
		
		dblks = BlockMatrix.makeDense(grid);
		sblks = BlockMatrix.makeSparse(grid, nzdensity);
		
		numplace = Place.numPlaces();
	}
	
	public def run(): void {
 		// Set the matrix function
		var retval:Boolean = true;

		Console.OUT.println("******************************************************");
		Console.OUT.println("Test dense block set commu in distributed block matrix");
		Console.OUT.println("******************************************************");

 		retval &= testCopyTo(dupden);
 		retval &= testCopyFrom(dupden);
 		retval &= testBcast(dupden);
 		retval &= testReduce(dupden);

 		Console.OUT.println("*******************************************************");
 		Console.OUT.println("Test sparse block set commu in distributed block matrix");	
 		Console.OUT.println("*******************************************************");
 		retval &= testCopyTo(dupspa);
 		retval &= testCopyFrom(dupspa);
 		retval &= testBcast(dupspa);
		if (retval) 
			Console.OUT.println("BlockSet P2P and collective commu test passed!");
		else
			Console.OUT.println("------------BlockSet P2P and collective commu test failed!-----------");
	}
	//------------------------------------------------
	
	public def testCopyTo(dst:DupBlockMatrix):Boolean {
		val ret:Boolean;
		var ds:Int = 0;
		
		Console.OUT.println("\nTest P2P copyTo dup block set matrix ("+M+","+N+") "+
				"partitioned in ("+bM+","+bN+") blocks duplicated over "+ numplace+" places");
		//src.printMatrix("CopyTo source");
		dst.reset();
		dst.local().initRandom();
		var st:Long =  Timer.milliTime();
		for (var p:Int=1; p<numplace; p++) {
			ds += BlockSetRemoteCopy.copySetTo(dst.handleDB, p);
		}
		
		val avgt = 1.0*(Timer.milliTime() - st)/(numplace-1);
		//dst.printMatrix("CopyTo Destination");
		
		Console.OUT.printf("P2P copyTo %d bytes : %.3f ms, thput: %2.2f MB/s per iteration\n", 
				ds*8, avgt, 8000.0*ds/avgt/1024/1024);
		
		ret = dst.checkSync();
		if (ret)
			Console.OUT.println("P2P CopyTo dup blockset matrix passed!");
		else
			Console.OUT.println("--------P2P CopyTo dup blockset matrix test failed!--------");
		
		return ret;

	}

	public def testCopyFrom(src:DupBlockMatrix) : Boolean{
		var ret:Boolean = true;
		var ds:Int = 0;
		var st:Long = 0;
		var tt:Long = 0;//Timer.milliTime() - st;
		
		Console.OUT.println("\nTest P2P copyFrom dup blockset matrix ("+M+","+N+") "+
				"partitioned in ("+bM+","+bN+") blocks duplicated over "+ numplace+" places");
		src.reset();
		src.local().init((r:Int,c:Int)=>1.0*((r+c)%3));
		//src.printMatrix("CopyFrom Source matrix");
		for (var p:Int=1; p<numplace; p++) {
			st =  Timer.milliTime();
			val pid = p;
			ds += at (Dist.makeUnique()(pid)) {
				BlockSetRemoteCopy.copySetFrom(src.handleDB, 0)
			};
			tt += Timer.milliTime() - st;
			//dst.printMatrix("CopyFrom Received "+b );
		}
		//src.printAllCopies();		
		ret = src.checkSync();
		val avgt = 1.0*tt/(numplace-1);
		Console.OUT.printf("P2P copyFrom %d bytes: %.3f ms, thput: %2.2f MB/s per iteration\n", 
				ds*8, avgt, 8000.0*ds/avgt/1024/1024);

		if (ret) 
			Console.OUT.println("P2P CopyFrom dup blockset matrix check passed!");
		else
			Console.OUT.println("--------P2P CopyFrom dup block set matrix test failed!--------");
		
		return ret;
	}	
	
	//------------------------------------------------
	public def testBcast(bmat:DupBlockMatrix):Boolean {
		var ret:Boolean = true;
		var ds:Int = 0;
		var avgt:Double=0;
		Console.OUT.println("\nTest Bcast on dup block set matrix, each block ("+M+"x"+N+") "+
				"partitioned in ("+bM+","+bN+") blocks duplicated over "+ numplace+" places");
		
		//bmat.fetchBlock(rootbid).print("BCast root");
		for (var p:Int=0; p<numplace && ret; p++ ) {
			Console.OUT.println("Bcast from root block from place"+p); 
			Console.OUT.flush();
			bmat.reset();
			at (Dist.makeUnique()(p)) {
				bmat.local().init((r:Int, c:Int)=>(1.0+r+c)*((r+c)%3));
			}
			val st:Long =  Timer.milliTime();
			BlockSetBcast.bcast(bmat.handleDB, p);
			avgt += (Timer.milliTime() - st);
			//bmat.printAllMatrixCopies();
			ret &= bmat.checkSync();
		}
	
		Console.OUT.printf("Bcast %d bytes average time: %.3f ms\n", 
						   ds*8, avgt/numplace);
		
		//ret = dbmat.syncCheck();
		if (ret)
			Console.OUT.println("Bcast dist block matrix passed!");
		else
			Console.OUT.println("--------Bcast block matrix test failed!--------");
		
		return ret;

	} 	
	

	public def testReduce(dmat:DupBlockMatrix):Boolean {
		var ret:Boolean = true;
		var avgt:Double = 0.0;
		Console.OUT.printf("\nTest reduce of dup block matrix over %d places\n", numplace);
		dmat.allocTmp();
		
		for (var p:Int=0; p < numplace&&ret; p++) {
			Console.OUT.println("Reduce to root place "+p);
			dmat.reset();
			dmat.init(1.0);
			val st:Long =  Timer.milliTime();
			BlockSetReduce.reduceSum(dmat.handleDB, dmat.tmpDB, p);
			avgt += (Timer.milliTime() - st);
			//distmat.printMatrix();
			//bmat.printMatrix();
			ret &= at (Dist.makeUnique()(p)) {
				//dmat.local().printMatrix();
				dmat.local().equals(numplace as Double)
			};
		}
		if (ret)
			Console.OUT.println("Test reduceSum for dist block set matrix test passed!");
		else
			Console.OUT.println("-----Test reduceSum for dist block set matrix failed!-----");
		return ret;
	}

}