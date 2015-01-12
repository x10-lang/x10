/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

//package commu.p2p;

import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.dist.DupDenseMatrix;
import x10.matrix.dist.DistDenseMatrix;

import x10.matrix.comm.MatrixRemoteCopy;

/**
   This class contains benchmark test for P2P inter-place comummunication.
   <p>

   <p>
 */

public class TestMatP2P{
    public static def main(args:Rail[String]) {
		val testcase = new TestMatP2PCopy(args);
		testcase.run(); 
	}
}


class TestMatP2PCopy {

	public val vrfy:Boolean;
	public val iter:Int;
	public val M:Long;
	public val N:Long;

	public val numplace:Int;
	public val checkTime:Array[Long](1);


    public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):50;
		N = args.size > 1 ? Long.parse(args(1)):M;
		iter = args.size > 2 ? Int.parse(args(2)):1;
		vrfy = args.size > 3 ? true : false;

		numplace =  Place.numPlaces();
		checkTime = new Array[Long](numplace, 0);
	}
	
	public def run(): void {
		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testDistCopy());
		ret &= (testDupCopy());
		//ret &= (testAsyncCopy());
		
		//ret &= (testDistCopy());

		if (vrfy) {
			if (ret)
				Console.OUT.println("Test and benchmark Matrix P2P copy passed!");
			else
				Console.OUT.println("--------Test of Matrix P2P copy failed!--------");
		}
	}


	public def testAsyncCopy():Boolean {
		val cnt = M * N;
		val srcbuf = new Array[Double](cnt);
		Console.OUT.println("\nTest async copy of dup matrix over "+ numplace+" placaces");
	
		val rmtbuf = new GlobalRail[Double](srcbuf as Array[Double]{self!=null});
		val dupMs = DupDenseMatrix.make(M,N).dupMs;
		
		var ttt:Double = 0.0;
		var minUT:Double=Double.MAX_VALUE;
		var maxUT:Double=Double.MIN_VALUE;
		
		for (var i:Long=0; i<iter; i++) {
			val stt = Timer.nanoTime();
			at(Place(1)) {
				//Implicit copy: dst, srcbuf, srcOff, dataCnt
				val dst = dupMs(here.id());
				finish Array.asyncCopy[Double](rmtbuf, 0, dst.d, 0, cnt);
			}
			val tt = 1.0*(Timer.nanoTime()-stt)/1000000;
			ttt += tt;
			if (tt > maxUT) maxUT=tt;
			if (tt < minUT) minUT=tt;

			//Console.OUT.printf("Test async Copy %d-th time: %8.4f ms, thput: %5.2f MB/s\n", 
				//	i,tt,  8000.0*M*N/(tt*1024*1024));
		}
		val avgUT = ttt / iter;
		Console.OUT.printf("Test async copy for all %d iteration %8.4f ms, thput: %5.2f MB/s (%5.2f %5.2f)\n", 
						    iter, avgUT,  8000.0*M*N/(avgUT*1024*1024), minUT, maxUT);
		
		return true;
	}
	

	public def testDupCopy():Boolean {
		var ret:Boolean = true;
		Console.OUT.println("\nTest P2P copy of dup matrix over "+ numplace+" placaces");
		
		val dupDA = DupDenseMatrix.make(M,N);
		val denA  = dupDA.local();
		denA.initRandom();
		
		var ttt:Double=0.0;
		var minUT:Double=Double.MAX_VALUE;
		var maxUT:Double=Double.MIN_VALUE;
		
		val src = dupDA.local();
		for (var i:Long=0; i<iter; i++) {
			val st = Timer.nanoTime();
			MatrixRemoteCopy.copy(src, 0, dupDA.dupMs, 1, 0, src.N); 
			//dupDA.comm.copy(, dupDA.dupMs, p);
			val tt =  1.0* (Timer.nanoTime() - st)/1000000;
			ttt +=tt;
			if (tt > maxUT) maxUT=tt;
			if (tt < minUT) minUT=tt;

			//Console.OUT.printf("Test P2P copy %d-th time: %8.3f ms, thput: %5.2f MB/s\n", 
			//		i, tt, 8000.0*M*N/(tt*1024*1024));
		}
		val avgUT = ttt / iter;
		Console.OUT.printf("Test P2P copy for %d iterations: %8.3f ms, thput: %5.2f MB/s (%5.2f, %5.2f)\n", 
							iter, avgUT, 8000.0*M*N/(avgUT*1024*1024), minUT, maxUT);
		//Console.OUT.println("Individual copies: "+checkTime.toString());
		
		if (vrfy) {
			ret = dupDA.syncCheck();
			if (ret)
				Console.OUT.println("Test P2P copy for dup matrix test passed!");
			else
				Console.OUT.println("-----Test P2P copy for dup matrix failed!-----");
		}
		return ret;
		
	}


	public def testDistCopy():Boolean {
		var ret:Boolean = true;
		Console.OUT.println("\nTest P2P copy of dist block matrix over "+ numplace+" placaces");
		
		val np    = Place.numPlaces();
		val gpart = new Grid(M*np, N, np, 1);
		val dstDA = DistDenseMatrix.make(gpart);
		val blkDA = DenseBlockMatrix.make(gpart);
		blkDA.initRandom();

		var ttt:Double = 0.0;
		var minUT:Double=Double.MAX_VALUE;
		var maxUT:Double=Double.MIN_VALUE;
		
		for (var i:Long=0; i<iter; i++) {
			val st = Timer.nanoTime();
			val srcden:DenseMatrix = blkDA.getMatrix(here.id());
			MatrixRemoteCopy.copy(srcden, 0, dstDA.distBs, 1, 0, srcden.N); 
			val tt = 1.0* ( Timer.nanoTime() - st)/1000000;
			ttt += tt;
			if (tt > maxUT) maxUT=tt;
			if (tt < minUT) minUT=tt;

			//Console.OUT.printf("Test P2P copy %d-th: %8.3f ms, thput: %4.2f MB/s\n", 
			//		   			i, tt, 8000.0*M*N/(tt*1024*1024));
		}
		val avgUT = ttt / iter;
		Console.OUT.printf("Test P2P copy for %d iterations: %8.1f ms, thput: %4.2f MB/s (%5.2f %5.2f)\n", 
							iter, avgUT, 8000.0*M*N/(avgUT*1024*1024), minUT, maxUT);
		
		if (vrfy) {
			blkDA.getMatrix(here.id()).copyTo(dstDA.getMatrix(here.id()));
			ret = dstDA.equals(blkDA as Matrix(dstDA.M, dstDA.N));
			if (ret)
				Console.OUT.println("Test P2P copy for dist matrix test passed!");
			else
				Console.OUT.println("-----Test P2P copy for dist matrix failed!-----");
		}
		return ret;
	}
}
