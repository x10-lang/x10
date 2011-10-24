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
 * can be accessed via PlaceLocalHandle or DistArray structure in all places.
 * 
 * <p>To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class ArrayReduce extends ArrayRemoteCopy {

	//====================================
	// Constructor
	//====================================
	public def this() {
		super();
	}

	//=================================================
	// Reduce data from all places to here in DistArray
	//=================================================

	/**
	 * Reduce all data in DistArray from all places
	 * to here
	 *
	 * @param ddmat   Input/Output. Distributed storage of data array in all places
	 * @param ddtmp   Temp distributed storage of data array used for receiving data.
	 * @param datcnt  count of double-precision data 
	 */
	public static def reduceSum(
			ddmat:DistDataArray,
			ddtmp:DistDataArray, 
			datCnt:Int):void {
		
		@Ifdef("MPI_COMMU") {
			mpiReduceSum(ddmat, ddtmp, datCnt);
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10ReduceSum(ddmat, ddtmp, datCnt);
		}
	} 

	// reduce sum
	/**
	 * Perform sum of arrays from all places.  The addition result will replace input array.
	 * <p>Note: Currently broken with runtime abort with message:
	 * <p>[0] Abort: s->bytes_sent != 0 Rendezvous Push, 0 at line 484 in file ibv_rndv.c
	 * <p>Still under investigation
	 * 
	 * @param ddmat    distributed storage for arrays in all places
	 * @param ddtmp    temp distributed storage for arrays to receive data
	 */
	public static def mpiReduceSum(
			ddmat:DistDataArray,
			ddtmp:DistDataArray, 
			datCnt:Int):void{
		
		@Ifdef("MPI_COMMU") {
			val root = here.id();
			//finish ateach (val [p]:Point in ddmat) {
			finish ateach (val [p]:Point in Dist.makeUnique()) {
				//Remote capture: root ddmat, ddtmp, datCnt;
				val pid = here.id();
				val src = ddtmp(pid);
				val dst = ddmat(pid);
				Array.copy(dst, 0, src, 0, datCnt);
				//Debug.flushln("Start reducing");
				// Counting the all reduce-sum time in communication
				WrapMPI.world.reduceSum(src, 0, dst, 0, datCnt, root);
				//Debug.flushln("Done reducing");
			}
		}
	}
	//=========================================================
	// reduce sum
	/**
	 * Sum of all data of arrays from all places. The input data will be replaced by
	 * the result.
	 *
	 * @param ddmat    distributed storage for data arrays in all places
	 * @param ddtmp    temp distributed storage used to receive data
	 */
	public static def x10ReduceSum(
			ddmat:DistDataArray, 
			ddtmp:DistDataArray, 
			datCnt:Int): void{
		
		val root = here.id();
		val mat  = ddmat(root);
		val pcnt = ddmat.region.size();

		reduceSumToHere(ddmat, ddtmp, datCnt, pcnt);
	}

	/**
	 * Binary recursive reduce sum.
	 * Notice dmat is input and output data array.
	 */
	protected static def reduceSumToHere(
			ddmat:DistDataArray, 
			ddtmp:DistDataArray,
			datCnt:Int,
			var pcnt:Int): void {
		
		val root = here.id();
		val ttpcnt = ddmat.region.size();
		val dstbuf = ddmat(root);
		
		if (root + pcnt > ttpcnt) pcnt = ttpcnt-root;

		if (pcnt <= 1) return;

		val lfcnt  = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = root + lfcnt;
		if (pcnt > 2) {
			finish {
				if (lfcnt > 1) async {
					reduceSumToHere(ddmat, ddtmp, datCnt, lfcnt); 
				}
				if (rtcnt > 1 ) {
					at (ddmat.dist(rtroot)) async {
						reduceSumToHere(ddmat, ddtmp, datCnt, rtcnt);
					}
				}
			}
		}
		
		val rcvbuf = ddtmp(root);
		x10Copy(ddmat, rtroot, 0, rcvbuf, 0, datCnt);
		for (var i:Int=0; i<datCnt; i++) dstbuf(i) += rcvbuf(i);
	}

	//=============================================================

	/**
	 * Perform all reduce sum operation. 
	 * @see reduceSum()
	 * Result is synchronized for all copies 
	 *
	 * @param ddmat    input and output. Distributed storage for data arrays in all places. 
	 * @param ddtmp -- temp distributed storage used to receive data.
	 */
	public static def allReduceSum(
			ddmat:DistDataArray,
			ddtmp:DistDataArray, 
			datCnt:Int):void {
		
		@Ifdef("MPI_COMMU") {
			mpiAllReduceSum(ddmat, ddtmp, datCnt);
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10AllReduceSum(ddmat, ddtmp, datCnt); 
		}
	} 

	/**
	 * Perform all reduce sum operation. 
	 * @see reduceSum()
	 * Result is synchronized for all copies
	 *
	 * @param ddmat    Distributed storage for input and output data arrays in all places. 
	 * @param ddtmp    Temp distributed storage of data arrays.
	 */
	protected static def mpiAllReduceSum(
			ddmat:DistDataArray,
			ddtmp:DistDataArray, 
			datCnt:Int): void {
		
	@Ifdef("MPI_COMMU") {

		val root = here.id();
		finish ateach (val [p]:Point in ddmat) {
			val pid = here.id();
			val src = ddtmp(pid);
			val dst = ddmat(pid);
			Array.copy(dst, 0, src, 0, datCnt);
			// Counting the all reduce-sum time in communication
			WrapMPI.world.allReduceSum(src, dst, datCnt);
		}
	}
	}
	
	protected static def x10AllReduceSum(
			ddmat:DistDataArray,
			ddtmp:DistDataArray, 
			datCnt:Int): void {
		
		val root = here.id();
		x10ReduceSum(ddmat, ddtmp, datCnt);
		ArrayBcast.x10Bcast(ddmat, datCnt);
	}

	//=================================================================
	// Reduce data from specified places
	//=================================================================

	
	/**
	 * Perform reduce sum of all array data accessed via PlaceLocalHandle
	 * from specified list of places. This method is not optimized.
	 * Result is stored in the array at root place.
	 * 
	 * @param ddmat    distributed storage for input and output data arrays. 
	 * @param tmp      temp data array storing the inter-place communication data at root.
	 * @param datCnt   column count
	 * @param plist    list of place IDs
	 */
	public static def reduceSum(
			ddmat:DistDataArray,
			tmp:Array[Double](1), 
			datCnt:Int,
			plist:Array[Int](1)):void{
		
		Debug.assure(tmp.size >= datCnt, "Temp data buffer overflow");
		val root = here.id();
		val dstbuf = ddmat(here.id());
		val srcbuf = tmp;
		for (val [p]:Point in plist) {
			if (plist(p) != here.id()) {
				copy(ddmat, plist(p), 0, srcbuf, 0, datCnt);
				//srcden.print("Reduce sum copy matrix data from Place:"+plist(p));
				for (var i:Int=0; i<datCnt; i++) dstbuf(i) += srcbuf(i);
			}
		}
	}

	//=========================================================
	//=========================================================
	// Reduce data from all places to here via PlaceLocalHandle
	//=========================================================
	//=========================================================

	/**
	 * Reduce data array by adding them together.
	 * 
	 * @param ddmat     Distributed storage for input and output data arrays
	 * @param ddtmp     Temp distributed storage.
	 * @param datcnt    count of double-precision data elements
	 */
	public static def reduceSum(
			ddmat:DataArrayPLH,
			ddtmp:DataArrayPLH, 
			datCnt:Int):void {
		
		@Ifdef("MPI_COMMU") {
			mpiReduceSum(ddmat, ddtmp, datCnt);
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10ReduceSum(ddmat, ddtmp, datCnt);
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
	public static def mpiReduceSum(
			ddmat:DataArrayPLH,
			ddtmp:DataArrayPLH, 
			datCnt:Int):void{
		
	@Ifdef("MPI_COMMU") {
		val root = here.id();
		finish ateach (val [p]:Point in WrapMPI.world.dist) {
			val pid = here.id();
			val src = ddtmp();
			val dst = ddmat();
			Array.copy(dst, 0, src, 0, datCnt);
			// Counting the all reduce-sum time in communication
			WrapMPI.world.reduceSum(src, dst, datCnt, root);
		}
	}
	}
	
	//=========================================================
	// reduce sum
	/**
	 * Sum of all data of arrays at all places 
	 * 
	 * @param ddmat    Distributed storage for input and output data arrays. 
	 * @param ddtmp    Temp distributed storage used in inter-place communication data.
	 * @param datCnt   count of data in array
	 */
	public static def x10ReduceSum(
			ddmat:DataArrayPLH,
			ddtmp:DataArrayPLH, 
			datCnt:Int): void{
		
		val root = here.id();
		val mat  = ddmat();
		val pcnt = Place.MAX_PLACES;

		reduceSumToHere(ddmat, ddtmp, datCnt, pcnt);
	}

	/**
	 * Binary recursive reduce sum.
	 * Notice dmat is input and output data array.
	 */
	protected static def reduceSumToHere(
			ddmat:DataArrayPLH, 
			ddtmp:DataArrayPLH,
			datCnt:Int, var pcnt:Int): void {
				
		val root = here.id();
		val ttpcnt = Place.MAX_PLACES;
		val dstbuf = ddmat();
				
		if (root + pcnt > ttpcnt) pcnt = ttpcnt-root;

		if (pcnt <= 1) return;
		val lfcnt  = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = root + lfcnt;
		if (pcnt > 2) {
			finish {
				if (lfcnt > 1) async {
					reduceSumToHere(ddmat, ddtmp, datCnt, lfcnt); 
				}
				if (rtcnt > 1 ) {
					at (new Place(rtroot)) async {
						reduceSumToHere(ddmat, ddtmp, datCnt, rtcnt);
					}
				}
			}
		}
				
		val rcvbuf = ddtmp();
		x10Copy(ddmat, rtroot, 0, rcvbuf, 0, datCnt);
		for (var i:Int=0; i<datCnt; i++) { 
			dstbuf(i) += rcvbuf(i);
		}
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
	public static def allReduceSum(
			ddmat:DataArrayPLH,
			ddtmp:DataArrayPLH, 
			datCnt:Int):void {
		
		@Ifdef("MPI_COMMU") {
			{
				mpiAllReduceSum(ddmat, ddtmp, datCnt);
			}
		}
		@Ifndef("MPI_COMMU") {
			{
				//Debug.flushln("start bcast to "+numPlaces);
				x10AllReduceSum(ddmat, ddtmp, datCnt); 
			}
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
			ddmat:DataArrayPLH,
			ddtmp:DataArrayPLH, 
			datCnt:Int): void {
		
	@Ifdef("MPI_COMMU") {

		val root = here.id();
		finish ateach (val [p]:Point in WrapMPI.world.dist) {
			val pid = here.id();
			val src = ddtmp();
			val dst = ddmat();
			Array.copy(dst, 0, src, 0, datCnt);
			// Counting the all reduce-sum time in communication
			WrapMPI.world.allReduceSum(src, dst, datCnt);
		}
	}
	}
	
	protected static def x10AllReduceSum(
			ddmat:DataArrayPLH,
			ddtmp:DataArrayPLH, 
			datCnt:Int): void {
		
			//val root = here.id();
		x10ReduceSum(ddmat, ddtmp, datCnt);
		ArrayBcast.x10Bcast(ddmat, datCnt);
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
			ddmat:DataArrayPLH,
			tmp:Array[Double](1), datCnt:Int,
			plist:Array[Int](1)):void{

		val root = here.id();
		val dstbuf = ddmat();
		val srcbuf = tmp;
		for (val [p]:Point in plist) {
			if (plist(p) != here.id()) {
				copy(ddmat, plist(p), 0, srcbuf, 0, datCnt);
						//srcden.print("Reduce sum copy matrix data from Place:"+plist(p));
				for (var i:Int=0; i<datCnt; i++) dstbuf(i) += srcbuf(i);
			}
		}
	}
}

