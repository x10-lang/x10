/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

//package commu.distarray;

import x10.util.Timer;

import x10.regionarray.Dist;
import x10.regionarray.DistArray;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DupDenseMatrix;

import x10.matrix.comm.ArrayRemoteCopy;
import x10.matrix.comm.ArrayBcast;
import x10.matrix.comm.ArrayGather;
import x10.matrix.comm.ArrayScatter;
import x10.matrix.comm.ArrayReduce;
import x10.matrix.comm.DataArrayPLH;
import x10.matrix.comm.DistDataArray;


/**
 * This class contains benchmarks for array communication operations.
 */
public class TestCommu{
    public static def main(args:Rail[String]) {
		val testcase = new TestDistArrayCommu(args);
		testcase.run();
	}
}

class TestDistArrayCommu {
	public val vrfy:Boolean;
	public val iter:Long;
	public val M:Long;

	public val nplace:Long = Place.numPlaces();
	public val segt:Rail[Long];
	
	public var syncTime:Long = 0;
	public var gatherTime:Long = 0;

	public var allgatherTime:Long = 0;
	public var reduceTime:Long = 0;

	public val localA:DataArrayPLH;
	public val localB:DataArrayPLH;
	public val dstA:DistArray[Rail[Double]];
	public val dstB:DistArray[Rail[Double]];
	public val dat:Rail[Double];
	public val datAll:Rail[Double];
	
	public val gpart:Grid;
	
	public val szlist:Rail[Long];
	
	public val checkTime = new Rail[Long](Place.numPlaces());
	
	public def this(args:Rail[String]) {
		val m = args.size > 0 ? Long.parse(args(0)):1024;
		M = m;
		iter = args.size > 1 ? Long.parse(args(1)):1;
		vrfy = args.size > 2 ? true : false;

		segt = new Rail[Long](nplace, (i:Long)=>m);   

        val localA = PlaceLocalHandle.make[Rail[Double]](Place.places(), ()=>(new Rail[Double](m)));
        dstA = DistArray.make[Rail[Double]](Dist.makeUnique(), (Point)=>localA());
        this.localA = localA;

        val localB = PlaceLocalHandle.make[Rail[Double]](Place.places(), ()=>(new Rail[Double](m)));
        dstB = DistArray.make[Rail[Double]](Dist.makeUnique(), (Point)=>localB());
        this.localB = localB;

		dat    = new Rail[Double](m);
		datAll = new Rail[Double](M*nplace);
		
		gpart = new Grid(M*nplace, 1, nplace, 1);
		szlist = new Rail[Long](nplace, (i:Long)=>m);
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
		val op:ArrayRemoteCopy = new ArrayRemoteCopy();
		var ret:Boolean = true;
		val dd = DupDenseMatrix.make(M, 1, dstA);
		val src = dd.local().d;
		Console.OUT.println("\nTest P2P copy distarray commu matrix over "+ nplace+" placaces");
		dd.local().initRandom();
		
		val stt:Long = Timer.milliTime();
		for (var i:Long=0; i<iter; i++) {
			for (var p:Long=0; p<nplace; p++) {
				if (p != here.id()) {
					//var st:Long =  Timer.milliTime();
					op.copy(src, 0, localA, p, 0, M);
					//checkTime(p) =  Timer.milliTime() - st; 
				}
			}
		}
		val tt = 1.0*(Timer.milliTime()-stt)/iter/(nplace-1);
		Console.OUT.printf("Test P2P copy for %d iterations: %.4f ms, thput: %2.2f MB/s per iteration\n", 
				iter, tt, 8.0*1000*M/tt/1024/1024);
		//Console.OUT.println("Individual copies: "+checkTime.toString());
		if (vrfy) {
			ret = dd.syncCheck();
			if (ret)
				Console.OUT.println("Test P2P copy for DistArray commu test passed!");
			else
				Console.OUT.println("-----Test P2P copy for DistArray commu failed!-----");
		}
		return ret;
		
	}	
	
	

	public def testBcast():Boolean {
		val op:ArrayBcast = new ArrayBcast();
		var ret:Boolean = true;
		val den = new DenseMatrix(M, 1, dstA(here.id()));
		den.initRandom();
		Console.OUT.printf("\nTest DistArray bcast over %d places\n", nplace);
		
		//denA.initRandom();
		val stt=Timer.milliTime();
		for (var i:Long=0; i<iter; i++) {
			op.bcast(localA, M);
		}
		val tt = 1.0 * (Timer.milliTime() - stt)/iter;
		Console.OUT.printf("Test bcast for %d iterations: %.4f ms, thput: %2.2f MB/s per iteration\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy) {
			val dd = DupDenseMatrix.make(M, 1, dstA);
			ret = dd.syncCheck();
			if (ret)
				Console.OUT.println("Test bcast for DistArray commu test passed!");
			else
				Console.OUT.println("-----Test bcast for DistArray commu failed!-----");
		}
		return ret;
	}
	
	public def testGather():Boolean {
		val op:ArrayGather = new ArrayGather();
		val dd = DistDenseMatrix.make(gpart, dstA);
		var ret:Boolean = true;
		Console.OUT.printf("\nTest DistArray gather over %d places\n", nplace);
		dd.initRandom();

		val st = Timer.milliTime();
		for (var i:Long=0; i<iter; i++) {
			op.gather(localA, datAll, gpart.rowBs);
		}
		val tt = (1.0*Timer.milliTime()-st)/iter;
		Console.OUT.printf("Test gather for %d iterations: %.4f ms, thput: %.2f MB/s\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy) {
			val den = new DenseMatrix(M*nplace, 1, datAll as Rail[Double]);
			ret = den.equals(dd as Matrix(den.M, den.N));
			if (ret)
				Console.OUT.println("Test gather of DistArray commu test passed!");
			else
				Console.OUT.println("-----Test gather of DistArray commu failed!-----");
		}
		return ret;
	}

	public def testScatter():Boolean {
		val op:ArrayScatter = new ArrayScatter();
		val den = new DenseMatrix(M*nplace, 1, datAll);
		var ret:Boolean = true;
		Console.OUT.printf("\nTest DistArray scatter over %d places\n", nplace);
		den.initRandom();
		//den.print();
		val stt = Timer.milliTime();
		for (var i:Long=0; i<iter; i++) {
			op.scatter(datAll, localA, szlist); 
		}
		val tt = 1.0*(Timer.milliTime() - stt)/iter;
				
		Console.OUT.printf("Test scatter for %d iterations: %.4f ms, thput: %.2f MB/s\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy) {
			val dd = DistDenseMatrix.make(gpart, dstA);
			//dd.print("Scatter result");
			ret = den.equals(dd as Matrix(den.M, den.N));
			if (ret)
				Console.OUT.println("Test scatter of DistArray passed!");
			else
				Console.OUT.println("-----Test scatter of DistArray failed!-----");
		}
		return ret;
	}
	

	public def testReduce(): Boolean {
		val op:ArrayReduce = new ArrayReduce();
		var ret:Boolean = true;
		val dd = DistDenseMatrix.make(gpart, dstA);
		dd.init(1.0);
		//dd.print("Source");
		Console.OUT.printf("\nTest reduce of DistArray over %d places\n", nplace);
		val org = new Rail[Double](dstA(here.id()));

		val stt=Timer.milliTime();
		for (var i:Long=0; i<iter; i++) {
			op.reduceSum(localA, localB, M);
		}
		val tt = 1.0*(Timer.milliTime() - stt)/iter;
		Console.OUT.printf("Test reduce for %d iterations: %.4f ms, thput: %.2f MB/s\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy && iter==1) {
			val dat=new DenseMatrix(M, 1, dstA(here.id()) as Rail[Double]);
			//dat.print("Result");
			val tgt=new DenseMatrix(M, 1, org);
			tgt.scale(nplace);
			ret = dat.equals(tgt as Matrix(dat.M, dat.N));
			if (ret)
				Console.OUT.println("Test reduce of DistArray passed!");
			else
				Console.OUT.println("-----Test reduce of DistArray failed!-----");
		}
		return ret;
	}
}

