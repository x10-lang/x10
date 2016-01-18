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

import x10.regionarray.DistArray;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.sparse.SparseCSC;

/**
 * Ring cast sends data from here to a number of places
 *.
 * <p> It starts with sending data from here to the first place in the list, and from the first place
 * to the second place in the list, and so on until the end of list. 
 * 
 * <p> This operation is implemented for SUMMA matrix
 * multiplication, when places in the same partition row (or column) use
 * the same block of matrix data as the first operand (or the second) in matrix
 * multiply.
 *
 */
public class MatrixRingCast {
	/**
	 * Broadcast the whole dense matrix at here to all places one by one 
	 *
	 * @param dmlist     distributed storage for copies of dense matrix 
	 */
	public static def rcast(dmlist:DistArray[DenseMatrix](1)) {
		val sz = dmlist(here.id()).N * dmlist(here.id()).M;
		rcast(dmlist, sz);
	}

	/**
	 * Broadcast data of dense matrix to all places.
	 *
	 * @param dmlist     distributed storage for copies of dense matrix
	 * @param datCnt     number of data to broadcast 
	 */
	public static def rcast(dmlist:DistArray[DenseMatrix](1), datCnt:Long):void {
		val pcnt = dmlist.dist.region.size();
		val plist = new Rail[Long](pcnt, (i:Long)=>i);
		rcast(dmlist, datCnt, plist);
	}

	/**
	 * Send data of dense matrix from here to a list of places.
	 * 
	 * @param dmlist     distributed storage for copies of dense matrix
	 * @param datCnt     number of data to broadcast
	 * @param plist      the list of places in the ring cast
	 */
	public static def rcast(
			dmlist:DistArray[DenseMatrix](1), datCnt:Long, 
			plist:Rail[Long]) : void {

		x10RingCast(dmlist, datCnt, plist);
	}

	/**
	 * Broadcast columns of sparse matrix to all places.
	 *
	 * @param smlist     distributed storage for copies of SparseCSC sparse matrix
	 * @param datCnt     count of nonzero data in the sparse matrix to broadcast 
	 */
	public static def rcast(smlist:DistArray[SparseCSC](1), datCnt:Long):void {
		val pcnt = smlist.dist.region.size();
		val plist = new Rail[Long](pcnt, (i:Long)=>i);
		rcast(smlist, datCnt, plist);
	}

	/**
	 * Broadcast the whole sparse matrix at here to all places 
	 * 
	 * @param smlist     distributed storage for copies of sparse matrix 
	 */
	public static def rcast(smlist:DistArray[SparseCSC](1)) {
		val sz = smlist(here.id()).countNonZero();
		rcast(smlist, sz);
	}

	/**
	 * Send columns in sparse matrix from here to a list of places.
	 *
	 * @param smlist     distributed storage for copies of sparse matrix
	 * @param datCnt     count of nonzero data in the sparse matrix to broadcast
	 * @param plist      the list of places in the ring cast
	 */
	public static def rcast(
			smlist:DistArray[SparseCSC](1), 
			datCnt:Long, 
			plist:Rail[Long]) : void {

		x10RingCast(smlist, datCnt, plist);
	}

	/**
	 * Sending data of dense matrix from here to a list of places using X10 remote array copy.
	 *
	 * @param  dmlist     distributed storage for copies of dense matrix
	 * @param  datCnt     counts of data to be sent
	 * @param  plist      the list of place IDs, which must contain place id of here.
	 */
	protected static def x10RingCast(
			dmlist:DistArray[DenseMatrix](1), datCnt:Long, 
			plist:Rail[Long]):void {
		//Check place list 

		if (plist.size == 0L) return;
		val root   = here.id();
		val srcden = dmlist(root);	

		val rmtbuf = new GlobalRail[ElemType](srcden.d as Rail[ElemType]{self!=null});
		val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));

		val nxtpid = plist(0);
		at(dmlist.dist(nxtpid)) {
			//Need: rmtbuf, dmlist, colOff, offset, datasz, nplist, root
			copyToHere(rmtbuf, dmlist, datCnt, nplist, root);
		}
	}

	private static def copyToHere(
			srcbuf:GlobalRail[ElemType],
			dmlist:DistArray[DenseMatrix](1),
			datCnt:Long,
			plist:Rail[Long],
			root:Long): void {
		
		val mypid  = here.id();
		val rcvden = dmlist(mypid);
		//Copy data from source place
		if (mypid != root && datCnt > 0) {
			finish Rail.asyncCopy[ElemType](srcbuf, 0L, rcvden.d, 0L, datCnt);
		}
		
		//Goto next place in the list
		if (plist.size >= 1) {
			val nxtpid = plist(0); // Get next place id in the list
			val rmtbuf = new GlobalRail[ElemType](rcvden.d as Rail[ElemType]{self!=null});
			val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));
			at(dmlist.dist(nxtpid)) {
				//Need: rmtbuf, dmlist, colOff, offset, datasz, nplist, root
				copyToHere(rmtbuf, dmlist, datCnt, nplist, root);
			}
		}
	}


	/**
	 * Sending the data from here to places in the list by using X10 remote copy.
	 *
	 * @param  smlist     	distributed storage for copies of sparse matrix
	 * @param  datCnt     	count of nonzero data to send
	 * @param  plist     	the list of place IDs, which must contain place id of here.
	 */
	protected static def x10RingCast(
			smlist:DistArray[SparseCSC](1), 
			datCnt:Long, 
			plist:Rail[Long]):void {
		
		//Check place list 
		if (plist.size == 0L) return;
		val root   = here.id();
		val srcspa = smlist(root);	
		val rmtidx = new GlobalRail[Long  ](srcspa.getIndex() as Rail[Long]{self!=null});
		val rmtval = new GlobalRail[ElemType](srcspa.getValue() as Rail[ElemType]{self!=null});
		val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));

		val nxtpid = plist(0);
		
		srcspa.initRemoteCopyAtSource();
		at(smlist.dist(nxtpid)) {
			copyToHere(rmtidx, rmtval, smlist, datCnt, nplist, root);
		}
		srcspa.finalizeRemoteCopyAtSource();		
	}

	private static def copyToHere(
			rmtIndex:GlobalRail[Long], 
			rmtValue:GlobalRail[ElemType],
			smlist:DistArray[SparseCSC](1),
			datCnt:Long,
			plist:Rail[Long],
			root:Long): void {
		
		val mypid  = here.id();
		val rcvspa = smlist(mypid);
		//Copy data from source place
		if (mypid != root) {
			rcvspa.initRemoteCopyAtDest(datCnt);
			if (datCnt > 0) {
				finish Rail.asyncCopy[Long  ](rmtIndex, 0L, rcvspa.getIndex(), 0L, datCnt);
				finish Rail.asyncCopy[ElemType](rmtValue, 0L, rcvspa.getValue(), 0L, datCnt);
			}	
		}
		
		//Goto next place in the list
		if (plist.size >= 1) {
			val nxtpid = plist(0); // Get next place id in the list
			val rmtidx = new GlobalRail[Long  ](rcvspa.getIndex() as Rail[Long]{self!=null});
			val rmtval = new GlobalRail[ElemType](rcvspa.getValue() as Rail[ElemType]{self!=null});
			val nplist = new Rail[Long](plist.size-1, (i:Long)=>plist(i+1));
			at(smlist.dist(nxtpid)) {
				//Need: rmtidx, rmtval, dmlist, datCnt, nplist, root
				copyToHere(rmtidx, rmtval, smlist, datCnt, nplist, root);
			}
		}

		if (mypid != root) {
			rcvspa.finalizeRemoteCopyAtDest();
		}
	}
}
