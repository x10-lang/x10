/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

//package commu.placehandle;

import x10.io.Console;
import x10.util.Timer;

import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DupDenseMatrix;

//import x10.matrix.comm.CommHandle;
import x10.matrix.comm.ArrayRemoteCopy;
import x10.matrix.comm.ArrayBcast;
import x10.matrix.comm.ArrayGather;
import x10.matrix.comm.ArrayScatter;
import x10.matrix.comm.ArrayReduce;


/**
   This class contains test cases for dense matrix multiplication.
   <p>

   <p>
 */

public class TestCommu{
    public static def main(args:Rail[String]) {
		val testcase = new TestArrayCommu(args);
		testcase.run();
	}
}

class TestArrayCommu {

	public val vrfy:Boolean;
	public val iter:Int;
	public val M:Int;

	public val nplace:Int = Place.MAX_PLACES;
	public val segt:Array[Int](1);

	
	public var syncTime:Long = 0;
	public var gatherTime:Long = 0;

	public var allgatherTime:Long = 0;
	public var reduceTime:Long = 0;
	
	//---------
	public val dist:Dist= Dist.makeUnique();
	public val dstA:DataArrayPLH;
	public val dstB:DataArrayPLH;
	public val dat:Array[Double](1){rail};
	public val datAll:Array[Double](1){rail};
	
	public val gpart:Grid;
	
	public val szlist:Array[Int](1);
	
	public val checkTime:Array[Long](1) = new Array[Long](Place.MAX_PLACES);
	
	public def this(args:Rail[String]) {
		val m = args.size > 0 ?Int.parse(args(0)):1024;
		M = m;
		iter = args.size > 1 ? Int.parse(args(1)):1;
		vrfy = args.size > 2 ? true : false;

		segt =  new Array[Int](nplace, (i:Int)=>m);   
		//
		dstA = DataArrayPLH.make[Array[Double](1){rail}](dist, ()=>(new Array[Double](m)));
		dstB = DataArrayPLH.make[Array[Double](1){rail}](dist, ()=>(new Array[Double](m)));

		dat    = new Array[Double](m);
		datAll = new Array[Double](M*nplace);
		
		gpart = new Grid(M*nplace, 1, nplace, 1);
		szlist = new Array[Int](nplace, (i:Int)=>m);
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
				Console.OUT.println("Test and benchmark array handle commuication passed!");
			else
				Console.OUT.println("--------Test of array handle communication failed!--------");
		}	
	}
	//------------------------------------------------
	public def testCopy():Boolean {
		val op:ArrayRemoteCopy = new ArrayRemoteCopy();
		var ret:Boolean = true;
		val dd = DupDenseMatrix.make(M, 1, dstA);
		val src= dd.local().d;
		Console.OUT.println("\nTest P2P copy handle commu matrix over "+ nplace+" placaces");
		dd.local().initRandom();
		
		val stt:Long = Timer.milliTime();
		for (var i:Int=0; i<iter; i++) {
			for (var p:Int=0; p<nplace; p++) {
				if (p != here.id()) {
					//var st:Long =  Timer.milliTime();
					op.copy(src, 0, dstA, p, 0, M);
					//checkTime(p) =  Timer.milliTime() - st; 
				}
			}
		}
		val tt = 1.0*(Timer.milliTime()-stt)/iter/(nplace-1);
		Console.OUT.printf("Test P2P copy for %d iterations: %.4f ms, thput: %2.2f MB/s per iteration\n", 
				iter, tt, 8.0*1000*M/tt/1024/1024);

		if (vrfy) {
			ret = dd.syncCheck();
			if (ret)
				Console.OUT.println("Test P2P copy for PlaceLocalHandle commu test passed!");
			else
				Console.OUT.println("-----Test P2P copy for PlaceLocalHandle commu failed!-----");
		}
		return ret;
		
	}	
	
	
	//------------------------------------------------
	public def testBcast():Boolean {
		val op:ArrayBcast = new ArrayBcast();
		var ret:Boolean = true;
		val den = new DenseMatrix(M, 1, dstA() as Array[Double](1){rail});
		den.initRandom();
		Console.OUT.printf("\nTest PlaceLocalHandle bcast over %d places\n", nplace);
		
		//denA.initRandom();
		val stt=Timer.milliTime();
		for (var i:Int=0; i<iter; i++) {
			op.bcast(dstA, M);
		}
		val tt = 1.0 * (Timer.milliTime() - stt)/iter;
		Console.OUT.printf("Test bcast for %d iterations: %.4f ms, thput: %2.2f MB/s per iteration\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy) {
			val dd = DupDenseMatrix.make(M, 1, dstA);
			ret = dd.syncCheck();
			if (ret)
				Console.OUT.println("Test bcast for PlaceLocalHandle commu test passed!");
			else
				Console.OUT.println("-----Test bcast for PlaceLocalHandle commu failed!-----");
		}
		return ret;
	}
	
	public def testGather():Boolean {
		val op:ArrayGather = new ArrayGather();
		val dd = DistDenseMatrix.make(gpart, dstA);
		var ret:Boolean = true;
		Console.OUT.printf("\nTest PlaceLocalHandle gather over %d places\n", nplace);
		dd.initRandom();

		val st = Timer.milliTime();
		for (var i:Int=0; i<iter; i++) {
			op.gather(dstA, datAll, gpart.rowBs);
		}
		val tt = (1.0*Timer.milliTime()-st)/iter;
		Console.OUT.printf("Test gather for %d iterations: %.4f ms, thput: %.2f MB/s\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy) {
			val den = new DenseMatrix(M*nplace, 1, datAll as Array[Double](1){rail});
			ret = den.equals(dd as Matrix(den.M, den.N));
			if (ret)
				Console.OUT.println("Test gather of PlaceLocalHandle commu test passed!");
			else
				Console.OUT.println("-----Test gather of PlaceLocalHandle commu failed!-----");
		}
		return ret;
	}

	public def testScatter():Boolean {
		val op:ArrayScatter = new ArrayScatter();
		val den = new DenseMatrix(M*nplace, 1, datAll);
		var ret:Boolean = true;
		Console.OUT.printf("\nTest PlaceLocalHandle scatter over %d places\n", nplace);
		den.initRandom();
		//den.print();
		val stt = Timer.milliTime();
		for (var i:Int=0; i<iter; i++) {
			op.scatter(datAll, dstA, szlist); 
		}
		val tt = 1.0*(Timer.milliTime() - stt)/iter;
				
		Console.OUT.printf("Test scatter for %d iterations: %.4f ms, thput: %.2f MB/s\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy) {
			val dd = DistDenseMatrix.make(gpart, dstA);
			//dd.print("Scatter result");
			ret = den.equals(dd as Matrix(den.M, den.N));
			if (ret)
				Console.OUT.println("Test scatter of PlaceLocalHandle passed!");
			else
				Console.OUT.println("-----Test scatter of PlaceLocalHandle failed!-----");
		}
		return ret;
	}
	

	public def testReduce(): Boolean {
		val op:ArrayReduce = new ArrayReduce();
		var ret:Boolean = true;
		val dd = DistDenseMatrix.make(gpart, dstA);
		dd.init(1.0);
		//dd.print("Source");
		Console.OUT.printf("\nTest reduce of PlaceLocalHandle over %d places\n", nplace);
		val org:Array[Double](1){rail}=new Array[Double](dstA());

		val stt=Timer.milliTime();
		for (var i:Int=0; i<iter; i++) {
			op.reduceSum(dstA, dstB, M);
		}
		val tt = 1.0*(Timer.milliTime() - stt)/iter;
		Console.OUT.printf("Test reduce for %d iterations: %.4f ms, thput: %.2f MB/s\n", 
						   iter, tt, 8.0*1000*M/tt/1024/1024);
		if (vrfy && iter==1) {
			val dat=new DenseMatrix(M, 1, dstA() as Array[Double](1){rail});
			//dat.print("Result");
			val tgt=new DenseMatrix(M, 1, org);
			tgt.cellMult(nplace);
			ret = dat.equals(tgt as Matrix(dat.M, dat.N));
			if (ret)
				Console.OUT.println("Test reduce of PlaceLocalHandle  passed!");
			else
				Console.OUT.println("-----Test reduce of PlaceLocalHandle  failed!-----");
		}
		return ret;
	}
}
