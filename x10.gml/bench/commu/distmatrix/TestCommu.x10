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

//package commu.distmatrix;

import x10.util.Timer;

import x10.regionarray.Dist;
import x10.regionarray.DistArray;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DupDenseMatrix;

import x10.matrix.comm.MatrixRemoteCopy;
import x10.matrix.comm.MatrixBcast;
import x10.matrix.comm.MatrixScatter;
import x10.matrix.comm.MatrixReduce;

/**
 * This class contains benchmarks for array communication operations.
 */
public class TestCommu{
    public static def main(args:Rail[String]) {
		val testcase = new TestDistMatrixCommu(args);
		testcase.run();
	}
}

class TestDistMatrixCommu {
	public val vrfy:Boolean;
	public val iter:Long;
	public val M:Long;

	public val nplace:Long = Place.numPlaces();
	public val segt:Rail[Long];
	
	public var syncTime:Long = 0;
	public var gatherTime:Long = 0;

	public var allgatherTime:Long = 0;
	public var reduceTime:Long = 0;
	
	public val dist = Dist.makeUnique();
	
	public val dA:DistDenseMatrix;
	public val dB:DistDenseMatrix;
	public val ddA:DupDenseMatrix;
	public val bA:DenseBlockMatrix;
	
	public val dstA:DistArray[DenseBlock](1);
	public val dstB:DistArray[DenseBlock];
	public val den:DenseMatrix;
		
	public val gpart:Grid;
	
	public val szlist:Rail[Long];
	
	public def this(args:Rail[String]) {
		val m = args.size > 0 ? Long.parse(args(0)):1024;
		M = m;
		iter = args.size > 1 ? Long.parse(args(1)):1;
		vrfy = args.size > 2 ? true : false;
		
		segt =  new Rail[Long](nplace, (i:Long)=>m);   
		gpart = new Grid(m*nplace, 1, nplace, 1);
		
		dA   = DistDenseMatrix.make(gpart);
		dstA = dA.distBs;
		
		bA   = DenseBlockMatrix.make(gpart);
		
		dB   = DistDenseMatrix.make(gpart);
		dstB = dB.distBs;
		
		ddA  = DupDenseMatrix.make(m, 1);
		den  = DenseMatrix.make(m, 1);
		
		szlist = gpart.rowBs;
	}
		
	public def run(): void {
		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testCopy());
		ret &= (testBcast());
		ret &= (testGather());
		ret &= (testScatter());
		ret &= (testReduce());

		if (vrfy) {
			if (ret)	
				Console.OUT.println("Test and benchmark DistArray commuication passed!");
			else
				Console.OUT.println("--------Test of dist array communication failed!--------");
		}	
	}

	public def testCopy():Boolean {
		val op:MatrixRemoteCopy = new MatrixRemoteCopy();
		var ret:Boolean = true;
		Console.OUT.println("\nTest P2P copy duplicated matrix over "+ nplace+" places");
		ddA.local().initRandom();
		
		val stt:Long = Timer.milliTime();
		for (var i:Long=0; i<iter; i++) {
			for (var p:Long=0; p<nplace; p++) {
				if (p != here.id()) {
					val srcden=ddA.local();
					op.copy(srcden, 0, ddA.dupMs, p, 0, srcden.N);
				}
			}
		}
		val tt = 1.0*(Timer.milliTime()-stt)/iter/(nplace-1);
		Console.OUT.printf("Test P2P copy for %d iterations: %.4f ms, thput: %2.2f MB/s per iteration\n", 
							iter, tt, 8.0*1000*M/tt/1024/1024);

		if (vrfy) {
			ret = ddA.syncCheck();
			if (ret)
				Console.OUT.println("Test P2P copy for DupDenseMatrix commu test passed!");
			else
				Console.OUT.println("-----Test P2P copy for DupDenseMatrix commu failed!-----");
		}
		return ret;
	}	

	public def testBcast():Boolean {
		val op:MatrixBcast = new MatrixBcast();
		var ret:Boolean = true;
		ddA.local().initRandom();
		Console.OUT.printf("\nTest DupDenseMatrix bcast over %d places\n", nplace);
		
		//denA.initRandom();
		val stt=Timer.milliTime();
		for (var i:Long=0; i<iter; i++) {
			ddA.sync();
		}
		val tt = 1.0 * (Timer.milliTime() - stt)/iter;
		Console.OUT.printf("Test bcast for %d iterations: %.4f ms, thput: %2.2f MB/s per iteration\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy) {
			ret = ddA.syncCheck();
			if (ret)
				Console.OUT.println("Test bcast for DupDenseMatrix commu test passed!");
			else
				Console.OUT.println("-----Test bcast for DupDenseMatrix commu failed!-----");
		}
		return ret;
	}
	
	public def testGather():Boolean {
		var ret:Boolean = true;
		dA.initRandom();
		Console.OUT.printf("\nTest DistMatrix gather over %d places\n", nplace);

		val st = Timer.milliTime();
		for (var i:Long=0; i<iter; i++) {
			dA.copyTo(bA as DenseBlockMatrix(dA.M, dA.N));
		}
		val tt = (1.0*Timer.milliTime()-st)/iter;
		Console.OUT.printf("Test gather for %d iterations: %.4f ms, thput: %.2f MB/s\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy) {
			ret = dA.equals(bA as Matrix(dA.M, dA.N));
			if (ret)
				Console.OUT.println("Test gather of DistArray commu test passed!");
			else
				Console.OUT.println("-----Test gather of DistArray commu failed!-----");
		}
		return ret;
	}

	public def testScatter():Boolean {
		var ret:Boolean = true;
		bA.initRandom();
		Console.OUT.printf("\nTest DistMatrix scatter over %d places\n", nplace);
		val stt = Timer.milliTime();
		for (var i:Long=0; i<iter; i++) {
			//dA.comm.p2pOp.copy(dA.local(), 0, dA.distBs, 1, 0, dA.local().N); 
			dA.copyFrom(bA as DenseBlockMatrix(dA.M, dA.N));
		}
		val tt = 1.0*(Timer.milliTime() - stt)/iter;
				
		Console.OUT.printf("Test scatter for %d iterations: %.4f ms, thput: %.2f MB/s\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy) {
			ret = dA.equals(bA as Matrix(dA.M, dA.N));
			if (ret)
				Console.OUT.println("Test scatter of DistMatrix passed!");
			else
				Console.OUT.println("-----Test scatter of DistMatrix failed!-----");
		}
		return ret;
	}
	

	public def testReduce(): Boolean {
		val op:MatrixReduce = new MatrixReduce();
		var ret:Boolean = true;
		ddA.init(1.0);
		//dd.print("Source");
		Console.OUT.printf("\nTest reduce of DupDenseMatrix over %d places\n", nplace);

		val stt=Timer.milliTime();
		for (var i:Long=0; i<iter; i++) {
			ddA.reduceSum();
		}
		val tt = 1.0*(Timer.milliTime() - stt)/iter;
		Console.OUT.printf("Test reduce for %d iterations: %.4f ms, thput: %.2f MB/s\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy && iter==1) {
			//dat.print("Result");
			ret = ddA.equals(1.0*nplace);
			if (ret)
				Console.OUT.println("Test reduce of DupDenseMatrix passed!");
			else
				Console.OUT.println("-----Test reduce of DupDenseMatrix failed!-----");
		}
		return ret;
	}
}
