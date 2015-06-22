/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

import harness.x10Test;

import x10.compiler.Ifndef;
import x10.util.Timer;
import x10.regionarray.DistArray;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.block.BlockMatrix;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistBlockMatrix;		
import x10.matrix.distblock.DupBlockMatrix;		

import x10.matrix.comm.BlockSetBcast;
import x10.matrix.comm.BlockSetRemoteCopy;
import x10.matrix.comm.BlockSetReduce;

/**
   This class contains test cases P2P communication for matrix over different places.
 */
public class TestBlockSetComm extends x10Test {
    static def ET(a:Double)= a as ElemType;
    static def ET(a:Float)= a as ElemType;

	public val M:Long;
	public val N:Long;
	public val nzdensity:Float;
	public val bM:Long;
	public val bN:Long;
	public val grid:Grid;
	
	public val numplace:Long;

	public val dupden:DupBlockMatrix;
	public val dupspa:DupBlockMatrix;
	
	public val dblks:BlockMatrix;
	public val sblks:BlockMatrix;
	
	public val rootbid:Long = 0;
	
    public def this(m:Long, n:Long, bm:Long, bn:Long, d:Float) {
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
	
    public def run():Boolean {
		var retval:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!

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
    }
        return retval;
	}

	
	public def testCopyTo(dst:DupBlockMatrix):Boolean {
		val ret:Boolean;
		var ds:Long = 0L;
		
		Console.OUT.println("\nTest P2P copyTo dup block set matrix ("+M+","+N+") "+
				"partitioned in ("+bM+","+bN+") blocks duplicated over "+ numplace+" places");
		dst.reset();
		dst.local().initRandom();
		var st:Long =  Timer.milliTime();
		for (var p:Long=1; p<numplace; p++) {
			ds += BlockSetRemoteCopy.copySetTo(dst.handleDB, p);
		}
		
		val avgt = 1.0*(Timer.milliTime() - st)/(numplace-1);
		
		Console.OUT.printf("P2P copyTo %d bytes : %.3f ms, thput: %2.2f MB/s per iteration\n", 
				ds*8, avgt, 8000.0*ds/avgt/1024/1024);
		
		ret = dst.checkSync();
		if (!ret)
			Console.OUT.println("--------P2P CopyTo dup blockset matrix test failed!--------");
		
		return ret;

	}

	public def testCopyFrom(src:DupBlockMatrix) : Boolean{
		var ret:Boolean = true;
		var ds:Long = 0L;
		var st:Long = 0L;
		var tt:Long = 0L;//Timer.milliTime() - st;
		
		Console.OUT.println("\nTest P2P copyFrom dup blockset matrix ("+M+","+N+") "+
				"partitioned in ("+bM+","+bN+") blocks duplicated over "+ numplace+" places");
		src.reset();
		src.local().init((r:Long,c:Long)=>ET(1.0*((r+c)%3)));
		for (var p:Long=1; p<numplace; p++) {
			st =  Timer.milliTime();
			val pid = p;
			ds += at(Place(pid)) {
				BlockSetRemoteCopy.copySetFrom(src.handleDB, 0)
			};
			tt += Timer.milliTime() - st;
		}
		ret = src.checkSync();
		val avgt = 1.0*tt/(numplace-1);
		Console.OUT.printf("P2P copyFrom %d bytes: %.3f ms, thput: %2.2f MB/s per iteration\n", 
				ds*8, avgt, 8000.0*ds/avgt/1024/1024);

		if (!ret) 
			Console.OUT.println("--------P2P CopyFrom dup block set matrix test failed!--------");
		
		return ret;
	}	

	public def testBcast(bmat:DupBlockMatrix):Boolean {
		var ret:Boolean = true;
		var ds:Long = 0L;
		var avgt:Double=0;
		Console.OUT.println("\nTest Bcast on dup block set matrix, each block ("+M+"x"+N+") "+
				"partitioned in ("+bM+","+bN+") blocks duplicated over "+ numplace+" places");
		
		for (var p:Long=0; p<numplace && ret; p++ ) {
			Console.OUT.println("Bcast from root block from place"+p); 
			Console.OUT.flush();
			bmat.reset();
			at(Place(p)) {
			    bmat.local().init((r:Long, c:Long)=>ET((1.0+r+c)*((r+c)%3)));
			}
			val st:Long =  Timer.milliTime();
			BlockSetBcast.bcast(bmat.handleDB, p);
			avgt += (Timer.milliTime() - st);
			ret &= bmat.checkSync();
		}
	
		Console.OUT.printf("Bcast %d bytes average time: %.3f ms\n", 
						   ds*8, avgt/numplace);
		
		//ret = dbmat.syncCheck();
		if (!ret)
			Console.OUT.println("--------Bcast block matrix test failed!--------");
		
		return ret;
	} 	

	public def testReduce(dmat:DupBlockMatrix):Boolean {
		var ret:Boolean = true;
		var avgt:Double = 0.0;
		Console.OUT.printf("\nTest reduce of dup block matrix over %d places\n", numplace);
		dmat.allocTmp();
		
		for (var p:Long=0; p < numplace&&ret; p++) {
			Console.OUT.println("Reduce to root place "+p);
			dmat.reset();
			dmat.init(ET(1.0));
			val st:Long =  Timer.milliTime();
			BlockSetReduce.reduceSum(dmat.handleDB, dmat.tmpDB, p);
			avgt += (Timer.milliTime() - st);
			ret &= at(Place(p)) {
				dmat.local().equals(numplace as Double)
			};
		}
		if (!ret)
			Console.OUT.println("-----Test reduceSum for dist block set matrix failed!-----");
		return ret;
	}

    public static def main(args:Rail[String]) {
		val m = args.size > 0 ? Long.parse(args(0)):40;
		val n = args.size > 1 ? Long.parse(args(1)):40;
		val bm= args.size > 2 ? Long.parse(args(2)):3;
		val bn= args.size > 3 ? Long.parse(args(3)):7;
		val d = args.size > 4 ? Float.parse(args(4)):0.99f;
		new TestBlockSetComm(m, n, bm, bn, d).execute();
	}
}
