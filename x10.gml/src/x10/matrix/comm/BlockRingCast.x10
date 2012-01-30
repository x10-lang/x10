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
import x10.util.ArrayList;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;

import x10.matrix.Debug;
import x10.matrix.RandTool;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;


/**
 * Ring cast sends data from here to a set of blocks, or partial broadcast
 *.
 */
public class BlockRingCast extends BlockRemoteCopy {

	//===========================
	// Constructor
	//===========================
	public def this() {
		super();
	}
	//==================================================
	// RingCast: receive form previous one and send to one next in a ring 
	//==================================================
	public static def rowWise(rid:Int, cid:Int):Int = rid;
	public static def colWise(rid:Int, cid:Int):Int = cid;
	
	/**
	 * Ring-cast sends data from root block to all blocks in the same row in partition
	 *
	 * @param distBS     distributed block sets in all places
	 * @param rootbid    root block id
	 * @param colCnt     number of columns to send out
	 */
	public static def rowCast(distBS:BlocksPLH, rootbid:Int, colCnt:Int) {
		ringCast(distBS, rootbid, colCnt, (rid:Int, cid:Int)=>rid);
	}
	
	/**
	 * Ring-cast sends data from root block to all blocks in the same column in partition
	 * 
	 * @param distBS     distributed block sets in all places
	 * @param rootbid    root block id
	 * @param colCnt     number of columns to send out
	 */	
	public static def colCast(distBS:BlocksPLH, rootbid:Int, colCnt:Int) {
		
		ringCast(distBS, rootbid, colCnt, (rid:Int, cid:Int)=>cid);	
	}

	//--------------------------------------------
	/**
	 * Ring cast data from root block to blocks in the same row or column blocks
	 */
	protected static def ringCast(distBS:BlocksPLH, rootbid:Int, colCnt:Int, select:(Int,Int)=>Int):void {
		var datcnt:Int = 0;
		val rootpid = distBS().findPlace(rootbid);
		
		if (rootpid != here.id()) {
			//Goto rootpid to start ringcast
			at (Dist.makeUnique()(rootpid)) {
				ringCast(distBS, rootbid, colCnt, select);
			}
		} else {
			//Romte capture: distBS(), rootbid, colCnt
			val dmap = distBS().getDistMap();
			val grid = distBS().getGrid();
			val dsz  = compBlockDataSize(distBS, rootbid, 0, colCnt);
			val plst = dmap.getPlaceListInRing(grid, rootbid, select); 
			//Root is the first in the list
			Debug.assure(plst(0)==rootpid, "RingCast place list must starts with root place id");
			//Debug.flushln("Ring cast to "+plst.toString());
			if (plst.size > 1) {
				val rtblk = distBS().findBlock(rootbid);
				if (rtblk.isSparse()) {
					val spa = rtblk.getMatrix() as SparseCSC;
					spa.initRemoteCopyAtSource(0, colCnt);
				}
				binaryTreeCastTo(distBS, rootbid, dsz, select, plst);
			}
			//Local ring cast
			finalizeRingCast(distBS, rootbid, colCnt, select, plst);
		}
	}

	//----------------------------------------------------------------
	/**
	 * Broadcast data from local root block at here to a list of places. 
	 */
	protected static def binaryTreeCastTo(
			distBS:BlocksPLH, rootbid:Int, datCnt:Int, 
			select:(Int,Int)=>Int, 
			plist:Array[Int](1)){

		val pcnt   = plist.size;
		val lfcnt:Int = (pcnt+1) / 2; 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = plist(lfcnt);

		val lfplist = new Array[Int](lfcnt, (i:Int)=>plist(i));
		val rtplist = new Array[Int](rtcnt, (i:Int)=>plist(lfcnt+i));

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
	//--------------------------------------------------------------
	
	private static def copyBlockToRightBranch(
			distBS:BlocksPLH, rootbid:Int, remotepid:Int, datCnt:Int,
			select:(Int,Int)=>Int, plist:Array[Int](1)) {
		
		val srcblk = distBS().findLocalRootBlock(rootbid, select);
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

	//--------------------------------------------------------------
	//--------------------------------------------------------------
	private static def x10CopyDenseBlock(distBS:BlocksPLH, 
			rootbid:Int, srcblk:MatrixBlock, rmtpid:Int, datCnt:Int,
			select:(Int,Int)=>Int, plist:Array[Int](1)):void {

		val srcden = srcblk.getMatrix() as DenseMatrix;
		val srcbuf = new RemoteArray[Double](srcden.d as Array[Double](1){self!=null});

		at (Dist.makeUnique()(rmtpid)) {
			//Remote capture:distBS, rootbid, datCnt, rtplist
			val blk  = distBS().findLocalRootBlock(rootbid, select);
			val dstden = blk.getMatrix() as DenseMatrix;
			// Using copyFrom style
			finish Array.asyncCopy[Double](srcbuf, 0, dstden.d, 0, datCnt);
			
			// Perform binary bcast on the right branch
			if (plist.size > 1 ) {
				binaryTreeCastTo(distBS, rootbid, datCnt, select, plist);
			}
		}
	}
	
	private static def x10CopySparseBlock(
			distBS:BlocksPLH, 
			rootbid:Int, srcblk:MatrixBlock, rmtpid:Int, datCnt:Int,
			select:(Int,Int)=>Int, plist:Array[Int](1)) {
		
		val srcspa = srcblk.getMatrix() as SparseCSC;
		val srcidx = new RemoteArray[Int   ](srcspa.getIndex() as Array[Int   ]{self!=null});
		val srcval = new RemoteArray[Double](srcspa.getValue() as Array[Double]{self!=null});
		
		at (Dist.makeUnique()(rmtpid)) {
			//Remote capture:distBS, rootbid, datCnt, rtplist
			val blk  = distBS().findLocalRootBlock(rootbid, select);
			val dstspa = blk.getMatrix() as SparseCSC;
			// Using copyFrom style
			dstspa.initRemoteCopyAtDest(datCnt);
			finish Array.asyncCopy[Int   ](srcidx, 0, dstspa.getIndex(), 0, datCnt);
			finish Array.asyncCopy[Double](srcval, 0, dstspa.getValue(), 0, datCnt);
			// Perform binary bcast on the right branch
			if (plist.size > 1 ) {
				binaryTreeCastTo(distBS, rootbid, datCnt, select, plist);
			}
		}	
	}	
	//=======================================================================
	private static def mpiCopyDenseBlock(
			distBS:BlocksPLH, 
			rootbid:Int, srcblk:MatrixBlock, rmtpid:Int, datCnt:Int,
			select:(Int,Int)=>Int, 
			plist:Array[Int](1)) {
		
		val srcpid = here.id();
		val srcden = srcblk.getMatrix() as DenseMatrix;
		val tag = RandTool.nextInt(Int.MAX_VALUE);
				
		async {
			WrapMPI.world.send(srcden.d, 0, datCnt, rmtpid, tag);
		}
		at (Dist.makeUnique()(rmtpid)) {
			//Remote capture:distBS, rootbid, datCnt, rtplist
			val blk    = distBS().findLocalRootBlock(rootbid, select);
			val dstden = blk.getMatrix() as DenseMatrix;
			// Using copyFrom style
			WrapMPI.world.recv(dstden.d, 0, datCnt, srcpid, tag);
			
			// Perform binary bcast on the right branch
			if (plist.size > 1 ) {
				binaryTreeCastTo(distBS, rootbid, datCnt, select, plist);
			}
		}
	}
	
	private static def mpiCopySparseBlock(
			distBS:BlocksPLH, 
			rootbid:Int, srcblk:MatrixBlock, rmtpid:Int, datCnt:Int,
			select:(Int,Int)=>Int, 
			plist:Array[Int](1)) {
		
		val srcpid = here.id();
		val srcspa = srcblk.getMatrix() as SparseCSC;
		val tag = RandTool.nextInt(Int.MAX_VALUE);
		
		async {
			WrapMPI.world.send(srcspa.getIndex(), 0, datCnt, rmtpid, tag);
			WrapMPI.world.send(srcspa.getValue(), 0, datCnt, rmtpid, tag+1);
		}
		
		at (Dist.makeUnique()(rmtpid)) {
			//Remote capture:distBS, rootbid, datCnt, rtplist
			val blk    = distBS().findLocalRootBlock(rootbid, select);
			val dstspa = blk.getMatrix() as SparseCSC;
			dstspa.initRemoteCopyAtDest(datCnt);
			// Using copyFrom style
			WrapMPI.world.recv(dstspa.getIndex(), 0, datCnt, srcpid, tag);
			WrapMPI.world.recv(dstspa.getValue(), 0, datCnt, srcpid, tag+1);
			// Perform binary bcast on the right branch
			//Debug.flushln("Recv "+here.id()+" get from "+srcpid);
			if (plist.size > 1 ) {
				binaryTreeCastTo(distBS, rootbid, datCnt, select, plist);
			}
		}
	}	
	
	//=======================================================================
	private static def finalizeRingCastRowwise(distBS:BlocksPLH, rootbid:Int, colCnt:Int, plist:Array[Int](1)){
		finalizeRingCast(distBS, rootbid, colCnt, (rid:Int,cid:Int)=>rid, plist);
	}

	private static def finalRingCastColwise(distBS:BlocksPLH, rootbid:Int, colCnt:Int, plist:Array[Int](1)){
		finalizeRingCast(distBS, rootbid, colCnt, (rid:Int,cid:Int)=>cid, plist);
	}

	private static def finalizeRingCast(distBS:BlocksPLH, 
			rootbid:Int, colCnt:Int, 
			select:(Int,Int)=>Int, plist:Array[Int](1)){
		
		val rootpid = here.id();
		finish {
			//Remote block set update
			for (val [p]:Point in plist) {
				val pid = plist(p);
				async at (Dist.makeUnique()(pid)) {
					val bset = distBS();
					val blk  = bset.findLocalRootBlock(rootbid, select);
					if (blk.isSparse() && plist.size > 1) { //If plist has only rootpid, sparse is not initialized for coy
						val spa = blk.getMatrix() as SparseCSC;
						if (here.id() != rootpid)
							spa.finalizeRemoteCopyAtDest();
						else
							spa.finalizeRemoteCopyAtSource();
					}
					bset.selectCast(blk, colCnt, select);
				}
			}
		}
	}
	
	//===================================================================

}