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

package x10.matrix.comm;

import x10.regionarray.Dist;
import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.comm.mpi.WrapMPI;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;

/**
 * Broadcast data from the first block at local to all blocks.
 * In MPI implementation, it is required to have at least one block in BlockSet list.
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
		var dsz:Long = 0;
			
		@Ifdef("MPI_COMMU") {
			val mat0 = distBS().getFirst();
			if (mat0.isDense()) {
				dsz= mpiBcastDense(distBS, rootbid, coloff, colcnt);
			} else if (mat0.isSparse()) {
				dsz= mpiBcastSparse(distBS, rootbid, coloff, colcnt);
			} else {
				Debug.exit("Block type is not supported");
			}
		}
		
		@Ifndef("MPI_COMMU") {
			dsz= x10Bcast(distBS, rootbid, coloff, colcnt);
		}

		return dsz;
	} 

	/**
	 * Broadcast dense matrix stored by using MPI bcast routine.
	 *
	 * @param dmlist      Distributed storage for dense matrices in all places
	 * @param colOff      Offset for the starting column
	 * @param colCnt      Number of columns to broadcast
	 * @return            Number of elements broadcast
	 */
	protected static def mpiBcastDense(distBS:BlocksPLH, rootbid:Long, colOff:Long, colCnt:Long):Long {
		if (colCnt < 0) return 0;
		
		@Ifdef("MPI_COMMU") {
			val rootpid    = distBS().findPlace(rootbid);
			//Get the data count from root block
			finish ateach(Dist.makeUnique()) {
				//Remote capture: colOff, colCnt, rootpid
				val bset = distBS();
				val blk  = (here.id()==rootpid)?bset.findBlock(rootbid):bset.getFirst();
				val den  = blk.getMatrix() as DenseMatrix;
				val offset = den.M * colOff;
				val datasz = den.M * colCnt;
				
				WrapMPI.world.bcast(den.d, offset, datasz, rootpid);
				bset.sync(blk, colOff, colCnt);
			}
		}
		return distBS().getFirstMatrix().M * colCnt;
	}

	/**
	 * Using MPI routine to implement sparse matrix broadcast
	 * 
	 */
	protected static def mpiBcastSparse(distBS:BlocksPLH, rootbid:Long, colOff:Long, colCnt:Long):Long {
		val datasz = compBlockDataSize(distBS, rootbid, colOff, colCnt);  

		@Ifdef("MPI_COMMU") {
			finish ateach([p] in Dist.makeUnique()) {
				//Need: rootbid, distBS, datasz, colOff, colCnt,
				val rootpid    = distBS().findPlace(rootbid);
				val bset = distBS();
				val blk  = (here.id()==rootpid)?bset.findBlock(rootbid):bset.getFirst();
				val spa  = blk.getMatrix() as SparseCSC;
				val offset = spa.getNonZeroOffset(colOff);
				
				//++++++++++++++++++++++++++++++++++++++++++++
				//Do NOT call getIndex()/getValue() before init at destination place
				//+++++++++++++++++++++++++++++++++++++++++++++
				if (p == rootpid) 
					spa.initRemoteCopyAtSource(colOff, colCnt);
				else
					spa.initRemoteCopyAtDest(colOff, colCnt, datasz);
				
				WrapMPI.world.bcast(spa.getIndex(), offset, datasz, rootpid);
				WrapMPI.world.bcast(spa.getValue(), offset, datasz, rootpid);
				
				if (p == rootpid) 
					spa.finalizeRemoteCopyAtSource();
				else
					spa.finalizeRemoteCopyAtDest();
				
				bset.sync(blk, colOff, colCnt);
			}
		}
		return datasz;
	}
	
	// x10 remote copy implemetation of bcast

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
			val srcbuf = new GlobalRail[Double](srcden.d as Rail[Double]{self!=null});
			val srcoff = srcden.M * colOff;
			at(Place(sttpl)) {
				//Remote capture: distBS, srcbuf, colOff, colCnt, datCnt, plcnt
				val dstblk = distBS().getFirst();
				val dstden = dstblk.getMatrix() as DenseMatrix;
				val dstoff = dstden.M * colOff;
				finish Rail.asyncCopy[Double](srcbuf, srcoff, dstden.d, dstoff, datCnt);

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
			val srcval = new GlobalRail[Double](valbuf as Rail[Double]{self!=null});		

			at(Place(sttpl)) {
				//Remote capture: distBS, srcidx, srcval, srcoff, colOff, colCnt, datCnt
				val dstblk = distBS().getFirst();
				val dstspa = dstblk.getMatrix() as SparseCSC;
				val dstoff = dstspa.getNonZeroOffset(colOff); 

				dstspa.initRemoteCopyAtDest(colOff, colCnt, datCnt);
				finish Rail.asyncCopy[Long  ](srcidx, srcoff, dstspa.getIndex(), dstoff, datCnt);
				finish Rail.asyncCopy[Double](srcval, srcoff, dstspa.getValue(), dstoff, datCnt);
				
				if (plcnt > 1)
					castToBranch(distBS, dstblk, colOff, colCnt, datCnt, plcnt);

				dstspa.finalizeRemoteCopyAtDest();
				distBS().sync(dstblk, colOff, colCnt);
			}
		} else {
			Debug.exit("Matrix block type is not supported");
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

	// private static def finalizeBcast(distBS:BlocksPLH, rootbid:Long, colOff:Long, colCnt:Long){
	// 	val rootpid = here.id();
	// 	finish ateach([p] in Dist.makeUnique()) {
	// 		//Remote block set update
	// 		val bset = distBS();
	// 		val blk  = (here.id()==rootpid)?bset.findBlock(rootbid):bset.getFirst();
	// 		if (blk.isSparse() && Place.numPlaces() > 1) {
	// 			val spa = blk.getMatrix() as SparseCSC;
	// 			if (here.id() != rootpid)
	// 				spa.finalizeRemoteCopyAtDest();
	// 			else
	// 				spa.finalizeRemoteCopyAtSource();
	// 		}
	// 		bset.sync(blk, colOff, colCnt);
	// 	}	
	// }
}
