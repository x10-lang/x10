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
import x10.matrix.comm.mpi.WrapMPI;
import x10.matrix.sparse.CompressArray;


/**
 * This class broadcasts data in double-precision or CompressArray to all places
 * 
 * <p> Two implementations are available, using MPI routines and X10 remote array copy.
 * To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class ArrayBcast extends ArrayRemoteCopy {

	
	//=================================================
	//=================================================
	// Broadcast array data in place local handle
	//=================================================
	//=================================================

	/**
	 * Broadcast data array from here to all other places.
	 * Remote places are accessed via PlaceLocalHandle.
	 * 
	 * @param dmat    distributed storage for source and its copies in all places
	 */
	public static def bcast(duplist:DataArrayPLH) {
		val data:Array[Double](1) = duplist();
		bcast(duplist, data.size);
	}

	/**
	 * Broadcast data array from here to all other places.
	 * 
	 * @param duplist     distributed storage for source and its copies in all places
	 * @param dataCnt     count of double-precision data to broadcast
	 */
	public static def bcast(duplist:DataArrayPLH, dataCnt:Int) : void {
		Debug.assure(dataCnt <= duplist().size, "Data overflow in data buffer");
		
		@Ifdef("MPI_COMMU") {
			mpiBcast(duplist, dataCnt);
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10Bcast(duplist, dataCnt);
		}
	} 

	//=================================================================
	/**
	 * Broadcast data array from here to 
	 * to all other places.
	 * 
	 * @param dmlist     distributed storage for source and its broadcast copies in all places
	 * @param dataCnt    count of double-precision data to broadcast
	 */
	protected static def mpiBcast(dmlist:DataArrayPLH, dataCnt:Int):void {
		@Ifdef("MPI_COMMU") {
			
			if (Place.MAX_PLACES <= 1) return;
			
			val root   = here.id();
			finish ateach (val [p]:Point in WrapMPI.world.dist) {
				//Need: dmlist, dataCnt, root
				val dstbuf = dmlist();
				WrapMPI.world.bcast(dstbuf, 0, dataCnt, root);
			}
		}
	}
	
	//--------------------------------------------------------------------------
	
	/**
	 *  Broadcast data to number of places from here
	 */
	protected static def x10Bcast(dmlist:DataArrayPLH, dataCnt:Int): void {

		val pcnt   = Place.MAX_PLACES;

		if (pcnt <= 1 || dataCnt == 0) return;
		
		binaryTreeCast(dmlist, dataCnt, pcnt);
	}

	//----------------------------------------------------------------
	
	/**
	 * X10 implementation of broadcast data via Binary tree structure.
	 * 
	 */
	protected static def binaryTreeCast(dmlist:DataArrayPLH, dataCnt:Int, pcnt:Int): void {		
		val root   = here.id();
		val src = dmlist();

		val lfcnt:Int = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = root + lfcnt;

		// Specify the remote buffer
		val srcbuf = new RemoteArray[Double](src as Array[Double](1){self!=null});
		
		finish {
			at (new Place(rtroot)) {
				val dstbuf = dmlist();
				// Using copyFrom style
				finish Array.asyncCopy[Double](srcbuf, 0, dstbuf, 0, dataCnt);
				
				// Perform binary bcast on the right brank
				if (rtcnt > 1 ) async {
					binaryTreeCast(dmlist, dataCnt, rtcnt);
				}
			}
			// Perform binary bcast on the left branch
			if (lfcnt > 1) async {
				binaryTreeCast(dmlist, dataCnt, lfcnt); 
			}
		}
	}
	//=================================================
	/**
	 * Bcast a segment of data to specified list of places
	 */
	public static def bcast(duplist:DataArrayPLH, offset:Int, datCnt:Int, plcList:Array[Int](1){rail}) {
		for (var i:Int=0; i<plcList.size; i++) {
			val pid = plcList(i);
			copy(duplist(), offset, duplist, pid, offset, datCnt);
		}
	}


	//=================================================
	// Broadcast SparseCSC matrix to all
	//=================================================

	/**
	 * Broadcast compress array stored in DistArray
	 * at here to all other places. 
	 * 
	 * @param smlist     compress array data buffer in all places
	 */
	public static def bcast(smlist:CompArrayPLH) {
		val data = smlist();
		bcast(smlist, data.storageSize());
	}

	/**
	 * Broadcast compress array stored in dist array from here
	 * to all other places. 
	 * 
	 * @param smlist    compress array date buffer in all places
	 * @param dataCnt   number of data to broadcast
	 */
	public static def bcast(smlist:CompArrayPLH, dataCnt:Int): void {
		Debug.assure(dataCnt <= smlist().storageSize(), "Data overflow in bcast");
		
		@Ifdef("MPI_COMMU") {
			mpiBcast(smlist, dataCnt);
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10Bcast(smlist, dataCnt);
		}
	} 

	//===================================
	/**
	 * Broadcast compress array in the PlaceLocalHandle here 
	 * to all other places.
	 * 
	 * @param smlist     source and target compress array
	 * @param dataCnt    number of data to broadcast
	 */

	/**
	 * Using MPI routine to implement sparse matrix broadcast
	 * 
	 */
	protected static def mpiBcast(smlist:CompArrayPLH, dataCnt:Int):void {
		@Ifdef("MPI_COMMU") {
			
			if (Place.MAX_PLACES <= 1) return;
			
			val root   = here.id();
			finish ateach (val [p]:Point in WrapMPI.world.dist) {
				//Need: root, smlist, datasz, colOff, colCnt,
				val ca = smlist();	
				
				WrapMPI.world.bcast(ca.index, 0, dataCnt, root);
				WrapMPI.world.bcast(ca.value, 0, dataCnt, root);
				
			}
		}
	}
	
	//-------------------------------------------------------
	/**
	 *  Broadcast compress array among the pcnt number of places followed from here
	 */
	protected static def x10Bcast(smlist:CompArrayPLH, dataCnt:Int): void {

		val pcnt = Place.MAX_PLACES;
		if (pcnt <= 1 || dataCnt == 0) return;
		
		binaryTreeCast(smlist, dataCnt, pcnt);
	}
	

	/**
	 * Broadcast compress array using remote array copy in X10
	 */
	protected static def binaryTreeCast(smlist:CompArrayPLH, dataCnt:Int, pcnt:Int): void {		
		val myid = here.id();

		val lfcnt:Int = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = myid + lfcnt;

		// Specify the remote buffer
		val srcca = smlist();
		val idxbuf:Array[Int](1)    = srcca.index;
		val valbuf:Array[Double](1) = srcca.value;
		val srcidx = new RemoteArray[Int   ](idxbuf as Array[Int   ]{self!=null});
		val srcval = new RemoteArray[Double](valbuf as Array[Double]{self!=null});
		
		finish {		
			at (new Place(rtroot)) {
				//Need: smlist, srcidx, srcval, srcOff, colOff, colCnt and datasz
				val dstca = smlist();
				finish Array.asyncCopy[Int   ](srcidx, 0, 
						dstca.index, 0, dataCnt);
				finish Array.asyncCopy[Double](srcval, 0, 
						dstca.value, 0, dataCnt);

				// Perform binary bcast on the right brank
				if (rtcnt > 1 ) async {
					binaryTreeCast(smlist, dataCnt, rtcnt);
				}
			}
			// Perform binary bcast on the left branch
			if (lfcnt > 1) async {
				binaryTreeCast(smlist, dataCnt, lfcnt); 
			}
		}
	}
	
	//=======================================
	//util
	//=======================================
	public static def verify(srcplh:DataArrayPLH, dataCnt:Int):Boolean {
		var ret:Boolean = true;
		val buf=srcplh();
		//for (val [p]:Point in Place.places()) {
		for (val [p]:Point in Dist.makeUnique()) {
			val rmt= at (new Place(p)) srcplh();//remote capture
			for (var i:Int=0; i<dataCnt; i++) ret &= (buf(i)==rmt(i));
		}
		return ret;
	}
}