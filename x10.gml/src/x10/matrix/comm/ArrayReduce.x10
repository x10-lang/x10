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

package x10.matrix.comm;

import x10.io.Console;
import x10.util.Timer;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;

import x10.matrix.Debug;

/**
 * This class provides implementation for reduce-sum  operation for data arrays which
 * can be accessed via PlaceLocalHandle structure in all places.
 * 
 * <p>To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class ArrayReduce extends ArrayRemoteCopy {

	//=========================================================
	//=========================================================
	// Reduce data from all places to here via PlaceLocalHandle
	//=========================================================
	//=========================================================
	public static def arraySum(src:Array[Double](1){rail},dst:Array[Double](1){rail}, cnt:Int):Int {
		for (var i:Int=0; i<cnt; i++) dst(i) += src(i);
		return 1;
	}
	
	/**
	 * Reduce data array by adding them together.
	 * 
	 * @param ddmat     Distributed storage for input and output data arrays
	 * @param ddtmp     Temp distributed storage.
	 * @param datcnt    count of double-precision data elements
	 */
	public static def reduce(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Int, 
			opFunc:(Array[Double](1){rail},Array[Double](1){rail},Int)=>Int):void {
		
		@Ifdef("MPI_COMMU") {
			Debug.exit("No MPI implementation");
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10ReduceToHere(dat, tmp, datCnt, Place.MAX_PLACES, opFunc);
		}
	} 
		
	/**
	 * Reduce data array by adding them together.
	 * 
	 * @param ddmat     Distributed storage for input and output data arrays
	 * @param ddtmp     Temp distributed storage.
	 * @param datcnt    count of double-precision data elements
	 */
	public static def reduceSum(dat:DataArrayPLH, tmp:DataArrayPLH,	datCnt:Int):void {
		
		@Ifdef("MPI_COMMU") 
		{
			mpiReduceSum(dat, tmp, datCnt);
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10ReduceToHere(dat, tmp, datCnt, Place.MAX_PLACES, 
					(src:Array[Double](1){rail}, dst:Array[Double](1){rail}, c:Int)=>arraySum(src,dst,c));
		}
	}

	// reduce sum
	/**
	 * Reduce arrays from all places to here by adding them together.
	 * 
	 * @param ddmat    Distributed storage for input and output data arrays. 
	 * @param ddtmp    Temp distributed storage
	 * @param datCnt   count of data in array
	 */
	public static def mpiReduceSum(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Int):void{
		
		@Ifdef("MPI_COMMU") 
		{
			val root = here.id();
			finish ateach (val [p]:Point in WrapMPI.world.dist) {
				val src = tmp();
				val dst = dat();
				Array.copy(dst, 0, src, 0, datCnt);
				// Counting the all reduce-sum time in communication
				WrapMPI.world.reduceSum(src, dst, datCnt, root);
			}
		}
	}
	
	//=========================================================
	// reduce sum

	/**
	 * Binary recursive reduce sum.
	 * Notice dat is input and output data array.
	 */
	protected static def x10ReduceToHere(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Int, pcnt:Int, 
			opFunc:(Array[Double](1){rail},Array[Double](1){rail},Int)=>Int) {

		val root = here.id();
		
		if (pcnt <= 1) return;
		val rtcnt  = (pcnt+1) / 2; // make sure right part is larger, if cnt is odd 
		val lfcnt  = pcnt - rtcnt;
		val rtroot = root + lfcnt;
		finish {
			if (lfcnt > 0) async {
				x10ReduceToHere(dat, tmp, datCnt, lfcnt, opFunc);
			}
			if (rtcnt > 1 ) {
				at (new Place(rtroot)) async {
					x10ReduceToHere(dat, tmp, datCnt, rtcnt, opFunc);
				}
			}
		}
		val dstbuf = dat();
		val rcvbuf = tmp();
		x10Copy(dat, rtroot, 0, rcvbuf, 0, datCnt);
		opFunc(rcvbuf, dstbuf, datCnt);
	}

	//=============================================================

	/**
	 * Perform all reduce sum operation. 
	 * @see reduceSum()
	 * Result is synchronized for all copies
	 * 
     * @param ddmat    distributed storage for input and output data arrays in all places. 
	 * @param ddtmp    temp distributed storage used in inter-place communication data.
	 # @param datCnt   count of data in array
	 */
	public static def allReduce(
			dat:DataArrayPLH,
			tmp:DataArrayPLH, 
			datCnt:Int, 
			opFunc:(Array[Double](1){rail},Array[Double](1){rail},Int)=>Int) {
		
		@Ifdef("MPI_COMMU")	{
			Debug.exit("No implementation yet");
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10AllReduce(dat, tmp, datCnt, opFunc); 
		}
	} 

	/**
	 * Perform all reduce sum operation. 
	 * @see reduceSum()
	 * Result is synchronized for all copies
	 * 
	 * @param ddmat    distributed storage for input and output data arrays in all places. 
	 * @param ddtmp    temp distributed storage used in inter-place communication data.
	 * # @param datCnt   count of data in array
	 */
	public static def allReduceSum(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Int) {
		
		@Ifdef("MPI_COMMU")	{
			mpiAllReduceSum(dat, tmp, datCnt);
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10AllReduce(dat, tmp, datCnt, 
					(src:Array[Double](1){rail}, dst:Array[Double](1){rail}, c:Int)=>arraySum(src,dst,c));
		}
	} 

	
	/**
	 * Perform all reduce sum operation. 
	 * @see reduceSum()
	 * Result is synchronized for all copies
	 * 
	 * @param ddmat    distributed storage for input and output data arrays. 
	 * @param ddtmp    temp distributed storage used in inter-place communication.
	 * @param datCnt   count of data in array
	 */
	protected static def mpiAllReduceSum(
			dat:DataArrayPLH,
			tmp:DataArrayPLH, 
			datCnt:Int): void {
		
		@Ifdef("MPI_COMMU") 
		{
			val root = here.id();
			finish ateach (val [p]:Point in WrapMPI.world.dist) {
				val pid = here.id();
				val src = tmp();
				val dst = dat();
				Array.copy(dst, 0, src, 0, datCnt);
				// Counting the all reduce-sum time in communication
				WrapMPI.world.allReduceSum(src, dst, datCnt);
			}
		}
	}
	
	protected static def x10AllReduce(dat:DataArrayPLH, tmp:DataArrayPLH, datCnt:Int,
			opFunc:(Array[Double](1){rail},Array[Double](1){rail},Int)=>Int){
	
		//val root = here.id();
		x10ReduceToHere(dat, tmp, datCnt, Place.MAX_PLACES, opFunc);
		ArrayBcast.x10Bcast(dat, datCnt);
	}

	//=================================================================
	// Reduce data from specified places
	//=================================================================
		
	/**
	 * Perform reduce sum of all array data accessed via PlaceLocalHandle
	 * from specified list of places. This method is not optimized.
	 * 	Result is stored in the data array at root place.
	 * 
	 * @param ddmat    distributed storage for input and output data arrays. 
	 * @param tmp      temp data array storing the inter-place communication data at root.
	 * @param datCnt   column count
	 * @param plist    list of place IDs
	 */
	public static def reduceSum(
			dat:DataArrayPLH,
			tmp:Array[Double](1), datCnt:Int,
			plist:Array[Int](1)):void{

		val root = here.id();
		val dstbuf = dat();
		val srcbuf = tmp;
		for (val [p]:Point in plist) {
			if (plist(p) != here.id()) {
				copy(dat, plist(p), 0, srcbuf, 0, datCnt);
						//srcden.print("Reduce sum copy matrix data from Place:"+plist(p));
				for (var i:Int=0; i<datCnt; i++) dstbuf(i) += srcbuf(i);
			}
		}
	}
}

