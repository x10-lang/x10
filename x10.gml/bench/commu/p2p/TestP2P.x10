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

//package commu.p2p;

import x10.io.Console;
import x10.util.Timer;

public type DataPLH  =PlaceLocalHandle[Array[Double](1){rail}];
public type DataDA   =DistArray[Array[Double](1){rail}](1);

/**
   This class contains benchmark test for P2P inter-place comummunication.
   <p>

   <p>
 */

public class TestP2P{
    public static def main(args:Rail[String]) {
		val testcase = new TestArrayP2PCopy(args);
		testcase.run();
	}
}


class TestArrayP2PCopy {

	public val vrfy:Boolean;
	public val iter:Int;
	public val dlen:Int;
	public val M:Int;
	public val N:Int;

	public val np:Int = Place.MAX_PLACES;
	public val checkTime:Array[Long](1);

	//==============
	val ddata:DataPLH;
	val darray:DataDA;
	
	//=================
    public def this(args:Rail[String]) {
		M = args.size > 0 ?Int.parse(args(0)):500;
		N = args.size > 1 ?Int.parse(args(1)):M;
		dlen = M * N;
		iter = args.size > 2 ? Int.parse(args(2)):1;
		vrfy = args.size > 3 ? true : false;

		checkTime = new Array[Long](np, 0);

		val m = dlen;
		val d = Dist.makeUnique();
		ddata  = DataPLH.make[Array[Double](1){rail}](d, ()=>(new Array[Double](m)));
		darray = DistArray.make[Array[Double](1){rail}](d);
		
	}
	
	public def run(): void {
		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testRemoteCapture());
		ret &= (testRemoteCopy());
		
		//ret &= (testDistCopy());
	}
	//------------------------------------------------
	public def testRemoteCapture():Boolean {
		Console.OUT.println("\nTest remote capture of array over "+ np+" placaces");

		var ttt:Double = 0.0;
		var tt:Double;
		var minUT:Double=Double.MAX_VALUE;
		var maxUT:Double=Double.MIN_VALUE;
		val d  = Dist.makeUnique();
		val src = ddata();
		
		for (var i:Int=0; i<iter; i++) {
			val stt = Timer.nanoTime();
			at (new Place(1)) {
				//Implicit copy: dst, srcbuf, srcOff, dataCnt
				darray(here.id()) = src;
			}
			tt = 1.0*(Timer.nanoTime()-stt)/1000000;
			ttt += tt;
			if (tt > maxUT) maxUT=tt;
			if (tt < minUT) minUT=tt;
			//Console.OUT.printf("Test remote capture %d-th time: %8.4f ms, thput: %5.2f MB/s\n", 
			//		i,tt,  8000.0*dlen/(tt*1024*1024));
		}
		val avgUT = ttt / iter;
		Console.OUT.printf("Test remote capture %d iteration %8.4f ms, thput: %5.2f MB/s (%5.2f %5.2f, %5.2f)\n", 
						    iter, avgUT, 8000.0*M*N/(avgUT*1024*1024), minUT, avgUT, maxUT);
		return true;
	}
	
	
	//------------------------------------------------
	public def testRemoteCopy():Boolean {
		Console.OUT.println("\nTest async copy of array over "+ np+" placaces");
		//val srcint = new Array[Int](dlen);
		//val rmtbuf = new RemoteArray[Int](srcint as Array[Int]{self!=null});
		val srcbuf:Array[Double](1){rail} = ddata();
		val rmtbuf = new RemoteArray[Double](srcbuf as Array[Double]{self!=null});

		var ttt:Double = 0.0;
		var tt:Double;
		val datalen = dlen;
		var minUT:Double=Double.MAX_VALUE;
		var maxUT:Double=Double.MIN_VALUE;

		for (var i:Int=0; i<iter; i++) {
			val stt = Timer.nanoTime();
			at (new Place(1)) {
				//Implicit copy: rmtbut, dst, datalen
				val dst = ddata();
				//val dst = new Array[Int](datalen);
				finish Array.asyncCopy[Double](rmtbuf, 0, dst, 0, datalen);
			}
			tt = 1.0*(Timer.nanoTime()-stt)/1000000;
			ttt += tt;
			if (tt > maxUT) maxUT=tt;
			if (tt < minUT) minUT=tt;
			//Console.OUT.printf("Test async Copy %d-th time: %8.4f ms, thput: %5.2f MB/s\n", 
			//		i,tt,  8000.0*dlen/(tt*1024*1024));
		}
		val avgUT = ttt / iter;
		Console.OUT.printf("Test remote copy %d iteration %8.4f ms, thput: %5.2f MB/s (%5.2f %5.2f, %5.2f)\n", 
							iter, avgUT, 8000.0*M*N/(avgUT*1024*1024), minUT, avgUT, maxUT);
		
		return true;
	}
	
}
