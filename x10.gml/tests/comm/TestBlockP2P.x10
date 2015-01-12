/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */

import harness.x10Test;

import x10.compiler.Ifndef;
import x10.util.Timer;
import x10.regionarray.DistArray;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistBlockMatrix;

import x10.matrix.comm.BlockRemoteCopy;

/**
   This class contains test cases P2P communication for matrix over different places.
 */
public class TestBlockP2P extends x10Test {
	public val M:Long;
	public val N:Long;
	public val nzdensity:Double;
	public val bM:Long;
	public val bN:Long;
	
	public val numplace:Long;

	public val dbmat:DistBlockMatrix;
	public val sbmat:DistBlockMatrix;
	
	public val srcden:DenseMatrix;
	public val dstden:DenseMatrix(srcden.M, srcden.N);
	
	public val srcspa:SparseCSC;
	public val dstspa:SparseCSC;
	
	public val rmtcp:BlockRemoteCopy;

    public def this(m:Long, n:Long, bm:Long, bn:Long, d:Double) {
		M=m * bm; N=n * bn;
		nzdensity = d;
		bM = bm; bN = bn;
		
		dbmat = DistBlockMatrix.makeDense(M, N, bm, bn);
		sbmat = DistBlockMatrix.makeSparse(M, N, bm, bn, nzdensity);
		
		srcden = DenseMatrix.make(m, n).init((x:Long, y:Long)=>(1.0+x+y));
		dstden = DenseMatrix.make(srcden.M, srcden.N);
		
		srcspa = SparseCSC.make(m, n, nzdensity).init((x:Long, y:Long)=>1.0*(x+y)*((x+y)%2));
		dstspa = SparseCSC.make(srcspa.M, srcspa.N, nzdensity);
		
		rmtcp     = new BlockRemoteCopy();
		numplace  = Place.numPlaces();
	}
	
    public def run():Boolean {
		var retval:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!
		Console.OUT.println("Test dense blocks in distributed block matrix");

		retval &= testCopyTo(srcden as Matrix, dbmat);
		retval &= testCopyFrom(dbmat, dstden as Matrix, srcden as Matrix);

		Console.OUT.println("Test sparse blocks in distributed block matrix");
	
		retval &= testCopyTo(srcspa as Matrix, sbmat);
		retval &= testCopyFrom(sbmat, dstspa as Matrix, srcspa as Matrix);
    }
        return retval;
	}


	public def testCopyTo(src:Matrix, dst:DistBlockMatrix):Boolean {
		val ret:Boolean;
		var ds:Long = 0L;
		
		Console.OUT.println("\nTest P2P copyTo dist block matrix ("+M+"x"+N+") "+
				"("+bM+","+bN+") blocks over "+ numplace+" places");
		var st:Long =  Timer.milliTime();
		for (var b:Long=0; b<bM*bN; b++) {
			val dstpid = dst.handleBS().findPlace(b);
			ds += rmtcp.copy(src, dst.handleBS, dstpid, b);
		}
		
		val avgt = 1.0*(Timer.milliTime() - st)/(numplace-1);
		
		Console.OUT.printf("P2P copyTo %d bytes : %.3f ms, thput: %2.2f MB/s per iteration\n", 
						   ds*8, avgt, 8000.0*ds/avgt/1024/1024);
		
		ret = dbmat.checkAllBlocksEqual();
		if (!ret)
			Console.OUT.println("--------P2P CopyTo dist block matrix test failed!--------");
		
		return ret;

	}

	public def testCopyFrom(src:DistBlockMatrix, dst:Matrix, ori:Matrix) : Boolean{
		var ret:Boolean = true;
		var ds:Long = 0;
		var st:Long = 0;
		var tt:Long = 0;//Timer.milliTime() - st;
		
		Console.OUT.println("\nTest P2P copyFrom dist block matrix ("+M+"x"+N+") "+
				"("+bM+","+bN+") blocks over "+ numplace+" places");

		for (var b:Long=0; b<bM*bN; b++) {
			val srcpid = src.handleBS().findPlace(b);
			st =  Timer.milliTime();
			ds += rmtcp.copy(src.handleBS, srcpid, b, dst);
			tt += Timer.milliTime() - st;
			ret &= ori.equals(dst);
		}
		
		val avgt = 1.0*tt/(numplace);
		Console.OUT.printf("P2P copyFrom %d bytes: %.3f ms, thput: %2.2f MB/s per iteration\n", 
						   ds*8, avgt, 8000.0*ds/avgt/1024/1024);

		if (!ret) 
			Console.OUT.println("--------P2P CopyFrom dist block matrix test failed!--------");
		
		return ret;
	}

    public static def main(args:Rail[String]) {
		val m = args.size > 0 ? Long.parse(args(0)):60;
		val n = args.size > 1 ? Long.parse(args(1)):60;
		val bm= args.size > 2 ? Long.parse(args(2)):2;
		val bn= args.size > 3 ? Long.parse(args(3)):6;
		val d = args.size > 4 ? Double.parse(args(4)):0.80;
		new TestBlockP2P(m, n, bm, bn, d).execute();
	}
}
