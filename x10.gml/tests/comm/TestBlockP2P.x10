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
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistBlockMatrix;

import x10.matrix.comm.BlockRemoteCopy;


/**
   This class contains test cases P2P communication for matrix over different places.
   <p>

   <p>
 */

public class TestBlockP2P{
    public static def main(args:Array[String](1)) {
		val m = args.size > 0 ?Int.parse(args(0)):60;
		val n = args.size > 1 ?Int.parse(args(1)):60;
		val bm= args.size > 2 ?Int.parse(args(2)):2;
		val bn= args.size > 3 ?Int.parse(args(3)):6;
		val d = args.size > 4 ? Double.parse(args(4)):0.80;
		val testcase = new BlockP2PTest(m, n, bm, bn, d);
		testcase.run();
	}
}


class BlockP2PTest {

	public val M:Int;
	public val N:Int;
	public val nzdensity:Double;
	public val bM:Int;
	public val bN:Int;
	
	public val numplace:Int;

	public val dbmat:DistBlockMatrix;
	public val sbmat:DistBlockMatrix;
	
	public val srcden:DenseMatrix;
	public val dstden:DenseMatrix(srcden.M, srcden.N);
	
	public val srcspa:SparseCSC;
	public val dstspa:SparseCSC;
	
	public val rmtcp:BlockRemoteCopy;


    public def this(m:Int, n:Int, bm:Int, bn:Int, d:Double) {

		M=m * bm; N=n * bn;
		nzdensity = d;
		bM = bm; bN = bn;
		
		dbmat = DistBlockMatrix.makeDense(M, N, bm, bn);
		sbmat = DistBlockMatrix.makeSparse(M, N, bm, bn, nzdensity);
		
		srcden = DenseMatrix.make(m, n).init((x:Int, y:Int)=>(1.0+x+y));
		dstden = DenseMatrix.make(srcden.M, srcden.N);
		
		srcspa = SparseCSC.make(m, n, nzdensity).init((x:Int, y:Int)=>1.0*(x+y)*((x+y)%2));
		dstspa = SparseCSC.make(srcspa.M, srcspa.N, nzdensity);
		
		rmtcp     = new BlockRemoteCopy();
		numplace  = Place.numPlaces();
	}
	
	public def run(): void {
 		// Set the matrix function
		var retval:Boolean = true;
		Console.OUT.println("Test dense blocks in distributed block matrix");

		retval &= testCopyTo(srcden as Matrix, dbmat);
		retval &= testCopyFrom(dbmat, dstden as Matrix, srcden as Matrix);

		Console.OUT.println("");
		Console.OUT.println("Test sparse blocks in distributed block matrix");
	
		retval &= testCopyTo(srcspa as Matrix, sbmat);
		retval &= testCopyFrom(sbmat, dstspa as Matrix, srcspa as Matrix);
		

		if (retval) 
			Console.OUT.println("Block communication test P2P passed!");
		else
			Console.OUT.println("------------Block communication test P2P failed!-----------");

	}
	//------------------------------------------------
	//------------------------------------------------
	public def testCopyTo(src:Matrix, dst:DistBlockMatrix):Boolean {
		val ret:Boolean;
		var ds:Int = 0;
		
		Console.OUT.println("\nTest P2P copyTo dist block matrix ("+M+"x"+N+") "+
				"("+bM+","+bN+") blocks over "+ numplace+" placaces");
		//src.printMatrix("CopyTo source");
		var st:Long =  Timer.milliTime();
		for (var b:Int=0; b<bM*bN; b++) {
			val dstpid = dst.handleBS().findPlace(b);
			ds += rmtcp.copy(src, dst.handleBS, dstpid, b);
		}
		
		val avgt = 1.0*(Timer.milliTime() - st)/(numplace-1);
		//dst.printMatrix("CopyTo Destination");
		
		Console.OUT.printf("P2P copyTo %d bytes : %.3f ms, thput: %2.2f MB/s per iteration\n", 
						   ds*8, avgt, 8000.0*ds/avgt/1024/1024);
		
		ret = dbmat.checkAllBlocksEqual();
		if (ret)
			Console.OUT.println("P2P CopyTo dist block matrix passed!");
		else
			Console.OUT.println("--------P2P CopyTo dist block matrix test failed!--------");
		
		return ret;

	}

	public def testCopyFrom(src:DistBlockMatrix, dst:Matrix, ori:Matrix) : Boolean{
		var ret:Boolean = true;
		var ds:Int = 0;
		var st:Long = 0;
		var tt:Long = 0;//Timer.milliTime() - st;
		
		Console.OUT.println("\nTest P2P copyFrom dist block matrix ("+M+"x"+N+") "+
				"("+bM+","+bN+") blocks over "+ numplace+" placaces");

		//src.printMatrix("CopyFrom Source matrix");
		for (var b:Int=0; b<bM*bN; b++) {
			val srcpid = src.handleBS().findPlace(b);
			st =  Timer.milliTime();
			ds += rmtcp.copy(src.handleBS, srcpid, b, dst);
			tt += Timer.milliTime() - st;
			//dst.printMatrix("CopyFrom Received "+b );
			ret &= ori.equals(dst);
		}
		
		val avgt = 1.0*tt/(numplace);
		Console.OUT.printf("P2P copyFrom %d bytes: %.3f ms, thput: %2.2f MB/s per iteration\n", 
						   ds*8, avgt, 8000.0*ds/avgt/1024/1024);

		if (ret) 
			Console.OUT.println("P2P CopyFrom dist block matrix check passed!");
		else
			Console.OUT.println("--------P2P CopyFrom dist block matrix test failed!--------");
		
		return ret;
	}


}