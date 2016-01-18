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

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;

/**
 * Broadcast data from the first block at local to all blocks.
 */
public class BlockBcast extends BlockRemoteCopy {
	/**
	 * Broadcast dense matrix from here to all other places.
	 * This routine is used in sync of DupDenseMatrix
	 *
	 * @param blks    Input-output. Distributed storage for the source and copies of dense matrix in all places
	 * @return        count of double-precision data to broadcast
	 */
	public static def bcast(distBS:BlocksPLH) = bcast(distBS, 0, 0, distBS().getGrid().getColSize(0));
	public static def bcast(distBS:BlocksPLH, rootbid:Long) = bcast(distBS, rootbid, 0, distBS().getGrid().getColSize(rootbid));

	/**
	 * Broadcast dense matrix from here to all other places. 
	 * This routine is used in sync of DupDenseMatrix
	 *
	 * @param dupmat      Input+output. Distributed storage for dense matrix in all places
	 * @param coloff      Input. Offset for the starting column for broadcast
	 * @param colcnt      Input. Count of columns to broadcast
	 * @return            Number of elements to broadcast
	 */
	public static def bcast(distBS:BlocksPLH, rootbid:Long, coloff:Long, colcnt:Long):Long {
		return x10Bcast(distBS, rootbid, coloff, colcnt);
	} 

	/**
	 *  Broadcast dense matrix among the pcnt number of places followed from here
	 */
	protected static def x10Bcast(distBS:BlocksPLH, rootbid:Long, colOff:Long, colCnt:Long):Long {
		var datcnt:Long=0;
		if (colCnt == 0L) return 0L;
		val rootpid = distBS().findPlace(rootbid);
		
		if (here.id() != rootpid) {
			datcnt = at(Place(rootpid)) {
				x10Bcast(distBS, rootbid, colOff, colCnt)
			};
		} else {
			val rtblk = distBS().findBlock(rootbid);
			if (Place.numPlaces() > 1) {
				datcnt = compBlockDataSize(distBS, rootbid, colOff, colCnt);
				if (datcnt == 0L) return 0L;
				if (rtblk.isSparse()) {
					val spa = rtblk.getMatrix() as SparseCSC;
					spa.initRemoteCopyAtSource(colOff, colCnt);
					startBinaryTreeCast(distBS, rtblk, colOff, colCnt, datcnt);	
					spa.finalizeRemoteCopyAtSource();

				} else {
					startBinaryTreeCast(distBS, rtblk, colOff, colCnt, datcnt);
				}
			}
			distBS().sync(rtblk, colOff, colCnt);

			//Finalize broadcast: finialize remote copy and local block set sync
			//finalizeBcast(distBS, rootbid, colOff, colCnt);
		}
		return datcnt;
	}

	private static def startBinaryTreeCast(distBS:BlocksPLH, 
			srcblk:MatrixBlock, colOff:Long, colCnt:Long, datcnt:Long) {
		finish {
			if (here.id() != 0L) async {
				val plcnt = here.id();
				val sttpl = 0;
				copyCastToBranch(distBS, srcblk, colOff, colCnt, datcnt, sttpl, plcnt);
			}
			val plcnt = Place.numPlaces()-here.id();
			if (plcnt > 1 ) async {
				castToBranch(distBS, srcblk, colOff, colCnt, datcnt, plcnt);
			}
		}
	}
	
	private static def copyCastToBranch(distBS:BlocksPLH, 
			srcblk:MatrixBlock, colOff:Long, colCnt:Long, datCnt:Long,
			sttpl:Long, plcnt:Long): void {
		
		if (srcblk.isDense()) {
			val srcden = srcblk.getMatrix() as DenseMatrix;
			val srcbuf = new GlobalRail[ElemType](srcden.d as Rail[ElemType]{self!=null});
			val srcoff = srcden.M * colOff;
			at(Place(sttpl)) {
				//Remote capture: distBS, srcbuf, colOff, colCnt, datCnt, plcnt
				val dstblk = distBS().getFirst();
				val dstden = dstblk.getMatrix() as DenseMatrix;
				val dstoff = dstden.M * colOff;
				finish Rail.asyncCopy[ElemType](srcbuf, srcoff, dstden.d, dstoff, datCnt);

				if (plcnt > 1)
					castToBranch(distBS, dstblk, colOff, colCnt, datCnt, plcnt);
				distBS().sync(dstblk, colOff, colCnt);
			}
			
		} else if (srcblk.isSparse()) {
			val srcspa = srcblk.getMatrix() as SparseCSC;
			val srcoff = srcspa.getNonZeroOffset(colOff);
			val idxbuf   = srcspa.getIndex();
			val valbuf = srcspa.getValue();
			val srcidx = new GlobalRail[Long  ](idxbuf as Rail[Long  ]{self!=null});
			val srcval = new GlobalRail[ElemType](valbuf as Rail[ElemType]{self!=null});		

			at(Place(sttpl)) {
				//Remote capture: distBS, srcidx, srcval, srcoff, colOff, colCnt, datCnt
				val dstblk = distBS().getFirst();
				val dstspa = dstblk.getMatrix() as SparseCSC;
				val dstoff = dstspa.getNonZeroOffset(colOff); 

				dstspa.initRemoteCopyAtDest(colOff, colCnt, datCnt);
				finish Rail.asyncCopy[Long  ](srcidx, srcoff, dstspa.getIndex(), dstoff, datCnt);
				finish Rail.asyncCopy[ElemType](srcval, srcoff, dstspa.getValue(), dstoff, datCnt);
				
				if (plcnt > 1)
					castToBranch(distBS, dstblk, colOff, colCnt, datCnt, plcnt);

				dstspa.finalizeRemoteCopyAtDest();
				distBS().sync(dstblk, colOff, colCnt);
			}
		} else {
			throw new UnsupportedOperationException("Matrix block type is not supported");
		}
	}	

	private static def castToBranch(distBS:BlocksPLH, 
			srcblk:MatrixBlock, 
			colOff:Long, colCnt:Long, datCnt:Long,
			plcnt:Long) {

		val lfroot = here.id();
		val lfcnt  = (plcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = plcnt - lfcnt;
		val rtroot = lfroot + lfcnt;
	
		finish {
			if (rtcnt > 0) async {
				copyCastToBranch(distBS, srcblk, colOff, colCnt, datCnt, rtroot, rtcnt);
			}
			if (lfcnt > 1) async {//Here is counted in
				castToBranch(distBS, srcblk, colOff, colCnt, datCnt, lfcnt);
			}
		}
	}
}
