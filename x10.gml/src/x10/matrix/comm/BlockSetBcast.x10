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

import x10.regionarray.Dist;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;

/**
 * Broadcast data from the first block at local to all blocks.
 */
public class BlockSetBcast extends BlockSetRemoteCopy {
	/**
	 * Broadcast dense matrix from here to all other places.
	 * This routine is used in sync of DupDenseMatrix
	 *
	 * @param blks    Input-output. Distributed storage for the source and copies of dense matrix in all places
	 * @return        count of double-precision data to broadcast
	 */
	public static def bcast(distBS:BlocksPLH) = bcast(distBS, here.id());

	/**
	 * Broadcast dense matrix from here to all other places. 
	 * This routine is used in sync of DupDenseMatrix
	 *
	 * @param dupmat      Input+output. Distributed storage for dense matrix in all places
	 * @param coloff      Input. Offset for the starting column for broadcast
	 * @param colcnt      Input. Count of columns to broadcast
	 * @return            Number of elements to broadcast
	 */
	public static def bcast(distBS:BlocksPLH, rootpid:Long):Long {
		return x10Bcast(distBS, rootpid);
	} 
	
	/**
	 *  Broadcast dense matrix among the pcnt number of places followed from here
	 */
	protected static def x10Bcast(distBS:BlocksPLH, rootpid:Long):Long {
		var datcnt:Long=0;
		
		if (here.id() != rootpid) {
			datcnt = at(Place(rootpid)) {
				x10Bcast(distBS, rootpid)
			};
		} else {
			if (Place.numPlaces() > 1) {
				val itr=distBS().iterator();
				while (itr.hasNext()) {
					val blk = itr.next();
					val blkdatsz = blk.getDataCount();
					datcnt += blkdatsz;
					if (blk.isSparse() && blkdatsz > 0) {
						val spa = blk.getMatrix() as SparseCSC;
						spa.initRemoteCopyAtSource();
						startBinaryTreeCast(distBS, blk, blkdatsz);
						spa.finalizeRemoteCopyAtSource();
					} else {						
						startBinaryTreeCast(distBS, blk, blkdatsz);	
					}
				}
			}
		}
		return datcnt;
	}

	private static def startBinaryTreeCast(distBS:BlocksPLH, 
			srcblk:MatrixBlock, datcnt:Long) {
		finish {
			if (here.id() != 0L) async {
				val plcnt = here.id();
				val sttpl = 0;
				copyCastToBranch(distBS, srcblk, datcnt, sttpl, plcnt);
			}
			val plcnt = Place.numPlaces()-here.id();
			if (plcnt > 1 ) async {
				castToBranch(distBS, srcblk, datcnt, plcnt);
			}
		}
	}


	private static def castToBranch(distBS:BlocksPLH, 
			srcblk:MatrixBlock,	datCnt:Long, plcnt:Long) {
		val lfroot = here.id();
		val lfcnt  = (plcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = plcnt - lfcnt;
		val rtroot = lfroot + lfcnt;
		
		finish {
			if (rtcnt > 0) async {
				copyCastToBranch(distBS, srcblk, datCnt, rtroot, rtcnt);
			}
			if (lfcnt > 1) async {//Here is counted in
				castToBranch(distBS, srcblk, datCnt, lfcnt);
			}
		}
	}

	private static def copyCastToBranch(distBS:BlocksPLH, 
			srcblk:MatrixBlock, datCnt:Long,
			sttpl:Long, plcnt:Long): void {
		
		val blkid  = distBS().getGrid().getBlockId(srcblk.myRowId, srcblk.myColId);
		if (srcblk.isDense()) {
			val srcden = srcblk.getMatrix() as DenseMatrix;
			val srcbuf = new GlobalRail[ElemType](srcden.d as Rail[ElemType]{self!=null});
			at(Place(sttpl)) {
				//Remote capture: distBS, srcbuf, blkid, datCnt, plcnt
				val dstblk = distBS().findBlock(blkid);
				val dstden = dstblk.getMatrix() as DenseMatrix;

				if (datCnt > 0)	finish {
					Rail.asyncCopy[ElemType](srcbuf, 0, dstden.d, 0, datCnt);
				}
				if (plcnt > 1)
					castToBranch(distBS, dstblk, datCnt, plcnt);
			}
		} else if (srcblk.isSparse()) {
			val srcspa = srcblk.getMatrix() as SparseCSC;
			val idxbuf   = srcspa.getIndex();
			val valbuf = srcspa.getValue();
			val srcidx = new GlobalRail[Long  ](idxbuf as Rail[Long  ]{self!=null});
			val srcval = new GlobalRail[ElemType](valbuf as Rail[ElemType]{self!=null});		
			at(Place(sttpl)) {
				//Remote capture: distBS, srcidx, srcval, srcoff, colOff, colCnt, datCnt
				val dstblk = distBS().findBlock(blkid);
				val dstspa = dstblk.getMatrix() as SparseCSC;
				dstspa.initRemoteCopyAtDest(datCnt);
				if (datCnt > 0) {
					finish Rail.asyncCopy[Long  ](srcidx, 0L, dstspa.getIndex(), 0L, datCnt);
					finish Rail.asyncCopy[ElemType](srcval, 0L, dstspa.getValue(), 0L, datCnt);
				}
				
				if (plcnt > 1 ) 
					castToBranch(distBS, dstblk, datCnt, plcnt);
				dstspa.finalizeRemoteCopyAtDest();				
			}
		} else {
			throw new UnsupportedOperationException("Matrix block type is not supported");
		}
	}	
}
