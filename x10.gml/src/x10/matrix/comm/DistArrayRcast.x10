/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package x10.matrix.comm;

import x10.matrix.ElemType;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

/**
 * Ring cast, similar to broadcast, sends data to selected places, while using place-to-place
 * communication without optimization on its broadcast route.
 * It starts by sending data from here to the first place in the list  
 * Upon receiving data, the first place forwards the data to the second place, 
 * and so on until the end of list.
 * This operation is mainly used by SUMMA algorithm,
 * when broadcasting data among places in the same partition row (or column).
 *
 * <p> Two data types are supported, double-precision array and compressed
 * data array (CompressArray).
 */
public class DistArrayRcast extends DistArrayRemoteCopy { 

	// RingCast: receive form previous one and send to one next in a ring 

	/**
	 * Broadcast the whole distributed array at here to all places one by one 
	 *
	 * @param dmlist 		distributed storage for data arrays 
	 */
	public static def bcast(dmlist:DistDataArray) : void{
		val sz = dmlist(here.id()).size;
		rcast(dmlist, sz);
	}

	/**
	 * Broadcast double-precision data array from here to all places.
	 *
	 * @param dmlist 		distributed storage for data arrays
	 * @param datCnt 		number of data elements in array to broadcast 
	 */
	public static def rcast(dmlist:DistDataArray, datCnt:Long) : void{
		val pcnt = dmlist.dist.region.size();
		val plist = new Rail[Long](pcnt, (i:Long)=>(i));
		rcast(dmlist, datCnt, plist);
	}


	/**
	 * Send double-precision data array from here to the specified places.
	 * 
	 * @param dmlist 		distributed storage for data arrays
	 * @param datCnt 		number of data to broadcast from here.
	 * @param plist  		the list of places in the ring cast
	 */
	public static def rcast(
			dmlist:DistDataArray, 
			datCnt:Long, 
			plist:Rail[Long]) : void {
		
		x10Rcast(dmlist, datCnt, plist);
	}

	// Sparse matrix

	/**
	 * Broadcast data in compress array to all places.
	 *
	 * @param smlist 		dist compress array 
	 * @param catCnt 		number of nonzero data elements to broadcast 
	 */
	public static def rcast(smlist:DistCompArray, datCnt:Long) : void{
		val pcnt = smlist.dist.region.size();
		val plist = new Rail[Long](pcnt, (i:Long)=>(i));
		rcast(smlist, datCnt, plist);
	}

	/**
	 * Broadcast the whole compress array at here to all places 
	 * 
	 * @param smlist 		distributed storage for copies of compress array in all places
	 */
	public static def rcast(smlist:DistCompArray): void {
		rcast(smlist, smlist(here.id()).count);
	}

	/**
	 * Send compress array from here to a list of places.
	 *
	 * @param smlist 		distributed storage for copies of compress array
	 * @param datCnt 		number of nonzero data elements to broadcast
	 */
	public static def rcast(
			smlist:DistCompArray, 
			datCnt:Long, 
			plist:Rail[Long]) : void {
		
		x10Rcast(smlist, datCnt, plist);
	}

	/**
	 * Send the double-precision data array from here to a list of places by using
	 * x10 remote array copy.
	 *
	 * @param  dmlist 		distributed storage for copies of array 
	 * @param  datCnt 		counts of data to send
	 * @param  plist 		the list of place IDs.
	 */
	protected static def x10Rcast(
			dmlist:DistDataArray, 
			datCnt:Long, 
			plist:Rail[Long]):void {
		
		//Check place list 
		if (plist.size == 0 || datCnt<=0) return;
		val root   = here.id();
		val srcden = dmlist(root);	

		val rmtbuf = new GlobalRail[ElemType](srcden as Rail[ElemType]{self!=null});
		val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));

		val nxtpid = plist(0);
		at(dmlist.dist(nxtpid)) {
			//Implicit capture: rmtbuf, dmlist, datasz, nplist, root
			copyToHere(rmtbuf, dmlist, datCnt, nplist, root);
		}
	}

	private static def copyToHere(
			srcbuf:GlobalRail[ElemType],
			dmlist:DistDataArray,
			datCnt:Long,
			plist:Rail[Long],
			root:Long): void {
		
		val mypid  = here.id();
		val rcvden = dmlist(mypid);

		//Copy data from source place
		if (mypid != root) {
			finish Rail.asyncCopy[ElemType](srcbuf, 0, rcvden, 0, datCnt);
		}
		
		//Goto next place in the list
		if (plist.size >= 1) {
			val nxtpid = plist(0); // Get next place id in the list
			val rmtbuf = new GlobalRail[ElemType](rcvden as Rail[ElemType]{self!=null});
			val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));
			at(dmlist.dist(nxtpid)) {
				//Need: rmtbuf, dmlist, colOff, offset, datasz, nplist, root
				copyToHere(rmtbuf, dmlist, datCnt, nplist, root);
			}
		}
	}


	/**
	 * Sending the data from here to a list places by using x10 remote copy
	 *
	 * @param  smlist 		distributed storage for copies of data array
	 * @param  datCnt 		counts of data elements to send
	 * @param  plist  		the list of place IDs.
	 */
	protected static def x10Rcast(
			smlist:DistCompArray, 
			datCnt:Long, 
			plist:Rail[Long]):void {
		
		//Check place list 
		if (plist.size == 0 || datCnt<=0) return;
		val root   = here.id();
		val srcspa = smlist(root);	

		val rmtidx = new GlobalRail[Long](srcspa.index as Rail[Long]{self!=null});
		val rmtval = new GlobalRail[ElemType](srcspa.value as Rail[ElemType]{self!=null});
		val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));

		val nxtpid = plist(0);
		
		//srcspa.initRemoteCopyAtSource(colOff, colCnt);
		at(smlist.dist(nxtpid)) {
			//Need: rmtidx, rmtval, dmlist, colOff, offset, cnlCnt, datasz, nplist, root
			copyToHere(rmtidx, rmtval, smlist, datCnt, nplist, root);
		}
		//srcspa.finalizeRemoteCopyAtSource();
	}

	private static def copyToHere(
			rmtIndex:GlobalRail[Long], 
			rmtValue:GlobalRail[ElemType],
			smlist:DistCompArray,
			datCnt:Long,
			plist:Rail[Long],
			root:Long): void {
		
		val mypid  = here.id();
		val rcvspa = smlist(mypid);

		//Copy data from source place
		if (mypid != root) {
			//++++++++++++++++++++++++++++++++++++++++++++
			//If receive side does not have enough space, program will crush
			//+++++++++++++++++++++++++++++++++++++++++++++
			//rcvspa.initRemoteCopyAtDest(colOff, colCnt, datasz);
			finish Rail.asyncCopy[Long](rmtIndex, 0, rcvspa.index, 0, datCnt);
			finish Rail.asyncCopy[ElemType](rmtValue, 0, rcvspa.value, 0, datCnt);
		}

		//Goto next place in the list
		if (plist.size >= 1) {
			val nxtpid = plist(0); // Get next place id in the list
			val rmtidx = new GlobalRail[Long](rcvspa.index as Rail[Long]{self!=null});
			val rmtval = new GlobalRail[ElemType](rcvspa.value as Rail[ElemType]{self!=null});
			val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));
			at(smlist.dist(nxtpid)) {
				//Need: rmtidx, rmtval, dmlist, colOff, offset, datasz, nplist, root
				copyToHere(rmtidx, rmtval, smlist, datCnt, nplist, root);
			}
		}
		//Not matrix, no need to finalize
		//if (mypid != root)
		//	rcvspa.finalizeRemoteCopyAtDest();
	}
}
