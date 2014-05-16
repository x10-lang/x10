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

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Inline;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.comm.mpi.WrapMPI;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;

/**
 * Ring cast sends data from here to a set of blocks, or partial broadcast.
 */
public class BlockRingCast extends BlockRemoteCopy {
	// RingCast: receive form previous one and send to one next in a ring 
	public static def rowWise(rid:Long, cid:Long):Long = rid;
	public static def colWise(rid:Long, cid:Long):Long = cid;
	
	/**
	 * Ring-cast sends data from root block to all blocks in the same row in partition
	 *
	 * @param distBS     distributed block sets in all places
	 * @param rootbid    root block id
	 * @param datCnt     number of data to send out
	 * @param plst       list of places to receive data
	 */
	public static def rowCastToPlaces(distBS:BlocksPLH, rootbid:Long, datCnt:Long, plst:Rail[Long]) {
		castToPlaces(distBS, rootbid, datCnt, (r:Long,c:Long)=>r, plst);
	}
	
	/**
	 * Ring-cast sends data from root block to all blocks in the same column in partition
	 * 
	 * @param distBS     distributed block sets in all places
	 * @param rootbid    root block id
	 * @param colCnt     number of data to send out
	 * @param plst       list of places
	 */	
	public static def colCastToPlaces(distBS:BlocksPLH, rootbid:Long, datCnt:Long, plst:Rail[Long]) {
		castToPlaces(distBS, rootbid, datCnt, (r:Long,c:Long)=>c, plst);
	}
	
	@Inline
	public static def castToPlaces(distBS:BlocksPLH, rootbid:Long, datCnt:Long, 
			select:(Long,Long)=>Long, plst:Rail[Long]) {

		val rootpid = distBS().findPlace(rootbid);
		if (rootpid != here.id()) {
			//Goto rootpid to start ringcast
			at(Place(rootpid)) {
				castToPlaces(distBS, rootbid, datCnt, select, plst);
			}
			return;
		}
		
		if (plst.size > 1) {
			val rtblk = distBS().findBlock(rootbid);
			if (rtblk.isSparse()) {
				val spa = rtblk.getMatrix() as SparseCSC;
				spa.initRemoteCopyAtSource();
			}
			binaryTreeCastTo(distBS, rootbid, datCnt, select, plst);
			//Local ring cast
			finalizeRingCast(distBS, rootbid, datCnt, select, plst);
		}
	}
	

	/**
	 * Broadcast data from local root block at here to a list of places. 
	 */
	protected static def binaryTreeCastTo(
			distBS:BlocksPLH, rootbid:Long, datCnt:Long, 
			select:(Long,Long)=>Long, 
			plist:Rail[Long]){

		val pcnt   = plist.size;
		val lfcnt:Long = (pcnt+1) / 2; 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = plist(lfcnt);

		val lfplist = new Rail[Long](lfcnt, (i:Long)=>plist(i));
		val rtplist = new Rail[Long](rtcnt, (i:Long)=>plist(lfcnt+i));

		//Debug.flushln("left branch "+lfplist.toString());
		//Debug.flushln("Right branch "+rtplist.toString());		
		finish {
			if (rtcnt > 0) async {
				copyBlockToRightBranch(distBS, rootbid, rtroot, datCnt, select, rtplist);
			}
			// Perform binary bcast on the left branch
			if (lfcnt > 1) async {
				binaryTreeCastTo(distBS, rootbid, datCnt, select, lfplist); 
			}
		}
	}
	
	private static def copyBlockToRightBranch(
			distBS:BlocksPLH, rootbid:Long, remotepid:Long, datCnt:Long,
			select:(Long,Long)=>Long, plist:Rail[Long]) {

		if (remotepid == here.id()) {
			if (plist.size > 1 ) {
				binaryTreeCastTo(distBS, rootbid, datCnt, select, plist);
			}
			return;
		}
		
		val srcblk = distBS().findFrontBlock(rootbid, select);
		if (srcblk.isDense()) {
			@Ifdef("MPI_COMMU") {
				mpiCopyDenseBlock(distBS, rootbid, srcblk, remotepid, datCnt, select, plist);
			}
			@Ifndef("MPI_COMMU") {
				x10CopyDenseBlock(distBS, rootbid, srcblk, remotepid, datCnt, select, plist);
			}
		} else if (srcblk.isSparse()) {
			@Ifdef("MPI_COMMU") {
				mpiCopySparseBlock(distBS, rootbid, srcblk, remotepid, datCnt, select, plist);
			}
			@Ifndef("MPI_COMMU") {
				x10CopySparseBlock(distBS, rootbid, srcblk, remotepid, datCnt, select, plist);
			}			
		} else {
			Debug.exit("Error in block type");
		}
	}

	private static def x10CopyDenseBlock(distBS:BlocksPLH, rootbid:Long, srcblk:MatrixBlock, rmtpid:Long, datCnt:Long,	select:(Long,Long)=>Long, plist:Rail[Long]):void {
		
		val srcden = srcblk.getMatrix() as DenseMatrix;
		val srcbuf = new GlobalRail[Double](srcden.d as Rail[Double]{self!=null});
		at(Place(rmtpid)) {
			//Remote capture:distBS, rootbid, datCnt, rtplist
			val blk  = distBS().findFrontBlock(rootbid, select);
			val dstden = blk.getMatrix() as DenseMatrix;
			// Using copyFrom style
			finish Rail.asyncCopy[Double](srcbuf, 0, dstden.d, 0, datCnt);
			
			// Perform binary bcast on the right branch
			if (plist.size > 1 ) {
				binaryTreeCastTo(distBS, rootbid, datCnt, select, plist);
			}
		}
		
	}
	
	private static def x10CopySparseBlock(
			distBS:BlocksPLH, 
			rootbid:Long, srcblk:MatrixBlock, rmtpid:Long, datCnt:Long,
			select:(Long,Long)=>Long, plist:Rail[Long]) {
		
		val srcspa = srcblk.getMatrix() as SparseCSC;
		val srcidx = new GlobalRail[Long](srcspa.getIndex() as Rail[Long]{self!=null});
		val srcval = new GlobalRail[Double](srcspa.getValue() as Rail[Double]{self!=null});
		
		at(Place(rmtpid)) {
			//Remote capture:distBS, rootbid, datCnt, rtplist
			val blk    = distBS().findFrontBlock(rootbid, select);
			val dstspa = blk.getMatrix() as SparseCSC;
			// Using copyFrom style
			dstspa.initRemoteCopyAtDest(datCnt);
			finish Rail.asyncCopy[Long](srcidx, 0, dstspa.getIndex(), 0, datCnt);
			finish Rail.asyncCopy[Double](srcval, 0, dstspa.getValue(), 0, datCnt);
			// Perform binary bcast on the right branch
			if (plist.size > 1 ) {
				binaryTreeCastTo(distBS, rootbid, datCnt, select, plist);
			}
		}	
	}	

	private static def mpiCopyDenseBlock(
			distBS:BlocksPLH, 
			rootbid:Long, srcblk:MatrixBlock, rmtpid:Long, datCnt:Long,
			select:(Long,Long)=>Long, 
			plist:Rail[Long]) {

		val srcpid = here.id();
		val srcden = srcblk.getMatrix() as DenseMatrix;
		val tag    = rootbid;//RandTool.nextLong(Int.MAX_VALUE);
		//Tag is used to differ different ring cast.
		//Row and column-wise ringcast must NOT be carried out at the same
		//time. This tag only allows ringcast be differed by root block id.
	
		
		@Ifdef("MPI_COMMU") 
		{
			async {
				WrapMPI.world.send(srcden.d, 0, datCnt, rmtpid, tag);
			}
			at(Place(rmtpid)) {
				//Remote capture:distBS, rootbid, datCnt, rtplist, tag
				val blk    = distBS().findFrontBlock(rootbid, select);
				val dstden = blk.getMatrix() as DenseMatrix;
				// Using copyFrom style
				WrapMPI.world.recv(dstden.d, 0, datCnt, srcpid, tag);
				
				// Perform binary bcast on the right branch
				if (plist.size > 1 ) {
					binaryTreeCastTo(distBS, rootbid, datCnt, select, plist);
				}
			}
		}
	}
	
	private static def mpiCopySparseBlock(
			distBS:BlocksPLH, 
			rootbid:Long, srcblk:MatrixBlock, rmtpid:Long, datCnt:Long,
			select:(Long,Long)=>Long, 
			plist:Rail[Long]) {

		val srcpid = here.id();
		val srcspa = srcblk.getMatrix() as SparseCSC;
		val tag = rootbid;//RandTool.nextLong(Int.MAX_VALUE);
		//Tag must allow to differ multiply ringcast.
		
		@Ifdef("MPI_COMMU") 
		{
			async {
				WrapMPI.world.send(srcspa.getIndex(), 0, datCnt, rmtpid, tag);
				WrapMPI.world.send(srcspa.getValue(), 0, datCnt, rmtpid, tag+1000000);
			}
			
			at(Place(rmtpid)) {
				//Remote capture:distBS, rootbid, datCnt, rtplist, tag
				val blk    = distBS().findFrontBlock(rootbid, select);
				val dstspa = blk.getMatrix() as SparseCSC;
				dstspa.initRemoteCopyAtDest(datCnt);
				// Using copyFrom style
				WrapMPI.world.recv(dstspa.getIndex(), 0, datCnt, srcpid, tag);
				WrapMPI.world.recv(dstspa.getValue(), 0, datCnt, srcpid, tag+1000000);
				// Perform binary bcast on the right branch
				//Debug.flushln("Recv "+here.id()+" get from "+srcpid);
				if (plist.size > 1 ) {
					binaryTreeCastTo(distBS, rootbid, datCnt, select, plist);
				}
			}
		}
	}	
	
	private static def finalizeRingCastRowwise(distBS:BlocksPLH, rootbid:Long, datCnt:Long, plist:Rail[Long]){
		finalizeRingCast(distBS, rootbid, datCnt, (rid:Long,cid:Long)=>rid, plist);
	}

	private static def finalRingCastColwise(distBS:BlocksPLH, rootbid:Long, datCnt:Long, plist:Rail[Long]){
		finalizeRingCast(distBS, rootbid, datCnt, (rid:Long,cid:Long)=>cid, plist);
	}

	private static def finalizeRingCast(distBS:BlocksPLH, 
			rootbid:Long, datCnt:Long, 
			select:(Long,Long)=>Long, plist:Rail[Long]){
		
		val rootpid = here.id();
		finish {
			//Remote block set update
            for (pid in plist) {
                at(Place(pid)) async {
					val bset = distBS();
					val blk  = bset.findFrontBlock(rootbid, select); 
					if (blk.isSparse() && plist.size > 1) { 
						//If plist has only rootpid, sparse is not initialized for copy
						val spa = blk.getMatrix() as SparseCSC;
						if (here.id() != rootpid)
							spa.finalizeRemoteCopyAtDest();
						else
							spa.finalizeRemoteCopyAtSource();
					}
					//bset.selectCast(blk, colCnt, select);
				}
			}
		}
	}
	
	public static def verifyCast(distBS:BlocksPLH, rootbid:Long, datCnt:Long, select:(Long,Long)=>Long, 
			plst:Rail[Long]):Boolean {
		var retval:Boolean = true;
		val mat = at(x10.regionarray.Dist.makeUnique()(plst(0))) {
			distBS().findFrontBlock(rootbid, select).getMatrix()
		};
				
		for (var p:Long=1; p<plst.size&&retval; p++) {
			val pid = plst(p);
			val sbj = at(Place(pid)) {
				distBS().findFrontBlock(rootbid, select).getMatrix()
			};
			for (var i:Long=0; i<datCnt&&retval; i++)
				retval &= (mat(i)==sbj(i));
		}
		return retval;
	}
	
	public static def verifyRowCast(distBS:BlocksPLH, rootbid:Long, datCnt:Long, plst:Rail[Long]) =
		verifyCast(distBS, rootbid, datCnt, (r:Long,c:Long)=>r, plst);
			
	public static def verifyColCast(distBS:BlocksPLH, rootbid:Long, datCnt:Long, plst:Rail[Long]) =
		verifyCast(distBS, rootbid, datCnt, (r:Long,c:Long)=>c, plst);
	
}
