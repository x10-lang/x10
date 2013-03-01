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

import x10.matrix.dist.DupDenseMatrix;
import x10.matrix.dist.DupSparseMatrix;
import x10.matrix.comm.MatrixRemoteCopy;


/**
   This class contains test cases P2P communication for matrix over different places.
   <p>

   <p>
 */

public class TestP2P{
    public static def main(args:Rail[String]) {
		val m = args.size > 0 ?Int.parse(args(0)):50;
		val n = args.size > 1 ?Int.parse(args(1)):50;
		val d = args.size > 2 ? Double.parse(args(2)):0.5;
		val i = args.size > 3 ? Int.parse(args(3)):1;
		val testcase = new TestMatrixCopy(m, n, d, i);
		testcase.run();
	}
}


class TestMatrixCopy {

	public val M:Int;
	public val N:Int;
	public val iter:Int;
	public val nzdensity:Double;

	public val numplace:Int;

	public val dmat:DupDenseMatrix;
	public val smat:DupSparseMatrix;
	public val rmtcp:MatrixRemoteCopy;


    public def this(m:Int, n:Int, d:Double, i:Int) {

		M=m; N=n; iter=i;
		nzdensity = d;
		
		dmat = DupDenseMatrix.make(m, n);
		smat = DupSparseMatrix.make(m, n, nzdensity);

		rmtcp     = new MatrixRemoteCopy();
		numplace  = Place.numPlaces();
	}
	
	public def run(): void {
 		// Set the matrix function
		var retval:Boolean = true;
		retval &= testCopyTo();
		retval &= testCopyFrom();
		retval &= testSparseCopyTo();
		retval &= testSparseCopyFrom();

		if (retval) 
			Console.OUT.println("Matrix communication test P2P passed!");
		else
			Console.OUT.println("------------Matrix communication test P2P failed!-----------");

	}
	//------------------------------------------------
	//------------------------------------------------
	public def testCopyTo():Boolean {
		val ret:Boolean;
		var ds:Int = 0;
		dmat.local().initRandom();
		
		Console.OUT.println("\nTest P2P copyTo dense matrix("+M+"x"+N+") in double over "
							+ numplace+" placaces");

		var st:Long =  Timer.milliTime();
		val ddm : DistArray[DenseMatrix](1) = dmat.dupMs;
		for (var i:Int=0; i<iter; i++) {
			for (var p:Int=0; p<numplace; p++) {
				if (p != here.id()) {
					ds=rmtcp.copyTo(ddm, p);
				}
			}
		}
		val tt =  Timer.milliTime() - st;
		
		val avgt = 1.0*tt/iter/(numplace-1);
		Console.OUT.printf("P2P copyTo %d bytes : %.3f ms, thput: %2.2f MB/s per iteration\n", 
						   ds*8, avgt, 8000.0*ds/avgt/1024/1024);

		ret = dmat.syncCheck();
		if (ret)
			Console.OUT.println("P2P CopyTo sync check passed!");
		else
			Console.OUT.println("--------P2P CopyTo failed, sync check failed!--------");
		
		return ret;

	}

	public def testCopyFrom() : Boolean{
		val ret:Boolean;
		var ds:Int = 0;
		var st:Long =  Timer.milliTime();
		
		dmat.local().initRandom();
		
		Console.OUT.println("\nTest P2P copyFrom dense matrix("+M+"x"+N+") in double over "
							+ numplace+" placaces");
		val root = here.id();
		for (var i:Int=0; i<iter; i++) {
			for (var p:Int=0; p<numplace; p++) {
				if (p != root) {
					ds = at (dmat.dupMs.dist(p)) {
						val ddm : DistArray[DenseMatrix](1) = dmat.dupMs;
						rmtcp.copyFrom(ddm, root)
					};
				}
			}
		}
		val tt =  Timer.milliTime() - st;
		
		val avgt = 1.0*tt/iter/(numplace-1);
		Console.OUT.printf("P2P copyFrom %d bytes: %.3f ms, thput: %2.2f MB/s per iteration\n", 
						   ds*8, avgt, 8000.0*ds/avgt/1024/1024);
				
		ret=dmat.syncCheck();
		if (ret) 
			Console.OUT.println("P2P CopyFrom sync check passed!");
		else
			Console.OUT.println("--------P2P CopyFrom failed, sync check not pass!--------");
		
		return ret;
	}
	//------------------------------------------------
	public def testSparseCopyTo() :Boolean{
		val ret:Boolean;
		var ds:Int = 0;
		var st:Long =  Timer.milliTime();
		 
		smat.local().initRandom(nzdensity);
		//smat.local().printSparse();

		val dsm : DistArray[SparseCSC](1) = smat.dupMs;
		Console.OUT.println("\nTest P2P copyTo sparse matrix("+M+"x"+N+") in double over "
							+ numplace+" placaces");
		//dsm.print("Source matrix");
		for (var i:Int=0; i<iter; i++) {
			for (var p:Int=0; p<numplace; p++) {
				if (p != here.id()) {
					ds=rmtcp.copyTo(dsm, p);
				}
			}
		}
		//smat.printAll("Copy result");
		val tt =  Timer.milliTime() - st;
		
		val avgt = 1.0*tt/iter/(numplace-1);
		Console.OUT.printf("P2P sparse copyTo %d bytes : %.3f ms, thput: %2.2f MB/s per iteration\n", 
						   ds*8, avgt, 8000.0*ds/avgt/1024/1024);

		ret = smat.syncCheck();
		if (ret) 
			Console.OUT.println("P2P CopyTo sparse matrix sync check passed!");
		else
			Console.OUT.println("--------P2P CopyTo sparse matrix failed, sync check not pass!--------");
		return ret;
	}
	//------------------------------------------------}
	public def testSparseCopyFrom() :Boolean{
		val ret:Boolean;
		var ds:Int = 0;
		var st:Long =  Timer.milliTime();
		 
		smat.local().initRandom(nzdensity);
		smat.sync();
		//smat.local().printSparse();

		Console.OUT.println("\nTest P2P copyFrom sparse matrix("+M+"x"+N+") in double over "
							+ numplace+" placaces");
		
		val root = here.id();
		//val dsm : DistArray[SparseCSC](1) = smat.dupMs;
		val dsm = smat.dupMs;
		for (var i:Int=0; i<iter; i++) {
			for (var p:Int=0; p<numplace; p++) {
				if (p != here.id()) {
					ds = at (smat.dupMs.dist(p)) {
						rmtcp.copyFrom(dsm, root)
					};
				}
			}
		}
		val tt =  Timer.milliTime() - st;
		
		val avgt = 1.0*tt/iter/(numplace-1);
		Console.OUT.printf("P2P copyFrom %d bytes : %.3f ms, thput: %2.2f MB/s per iteration\n", 
						   ds*8, avgt, 8000.0*ds/avgt/1024/1024);

		ret = smat.syncCheck();
		if (ret) 
			Console.OUT.println("P2P CopyFrom sparse matrix sync check passed!");
		else
			Console.OUT.println("--------P2P CopyFrom sparse matrix failed, sync check not pass!--------");
		
		return ret;
	}

}