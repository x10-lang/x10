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
import x10.util.ArrayList;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;

import x10.matrix.Debug;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;

import x10.matrix.block.MatrixBlock;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistGrid;

/**
 * Broadcast data from the first block at local to all blocks.
 * In MPI implementation, it is required to have at least one block in BlockSet list.
 * 
 */
public class BlockSetBcast extends BlockSetRemoteCopy {

	//public var mpi:UtilMPI;

	//====================================
	// Constructor
	//====================================
	public def this() {
		super();
	}

	//=================================================
	// Broadcast dense matrix to all
	//=================================================

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
	public static def bcast(distBS:BlocksPLH, rootpid:Int) : Int {
		var dsz:Int = 0;
			
		@Ifdef("MPI_COMMU") {
			val mat0 = distBS().getFirst();
			if (mat0.isDense()) {
				dsz= mpiBcastDense(distBS, rootpid);
			} else if (mat0.isSparse()) {
				dsz= mpiBcastSparse(distBS, rootpid);
			} else {
				Debug.exit("Block type is not supported");
			}
		}
		
		@Ifndef("MPI_COMMU") {
			dsz= x10Bcast(distBS, rootpid);
		}

		return dsz;
	} 

	//=================================================================
	
	/**
	 * Broadcast dense matrix stored by using MPI bcast routine.
	 *
	 * @param dmlist      Distributed storage for dense matrices in all places
	 * @param colOff      Offset for the starting column
	 * @param colCnt      Number of columns to broadcast
	 * @return            Number of elements broadcast
	 */
	protected static def mpiBcastDense(distBS:BlocksPLH, rootpid:Int):Int {
		var datcnt:Int = 0;	
		@Ifdef("MPI_COMMU") {
			
			finish ateach (Dist.makeUnique()) {
				val blkitr = distBS().iterator();
				while (blkitr.hasNext()) {
				//Get the data count from root block
				//Remote capture: colOff, colCnt, rootpid
					val blk  = blkitr.next();
					val den  = blk.getMatrix() as DenseMatrix;
					val datasz = den.M * den.N;
					WrapMPI.world.bcast(den.d, 0, datasz, rootpid);
				}
			}
			datcnt = distBS().getAllBlocksDataSize();
		}
		return datcnt;
	}

	/**
	 * Using MPI routine to implement sparse matrix broadcast
	 * 
	 */
	protected static def mpiBcastSparse(distBS:BlocksPLH, rootpid:Int):Int {
		var datcnt:Int = 0;

		@Ifdef("MPI_COMMU") {
			if (rootpid != here.id()) {
				at (Dist.makeUnique()(rootpid)) {
					mpiBcastSparse(distBS, rootpid);
				}
			}else {
				var i:Int=0;
				val blkitr = distBS().iterator();
				val szlist = new Array[Int](distBS().blocklist.size());
				while (blkitr.hasNext()) {
					val blk = blkitr.next();
					szlist(i) = blk.getDataSize();
					datcnt += szlist(i);
					i++;
				}

				finish ateach (Dist.makeUnique()) {
					//Remote capture: distBS, szlist
					val itr = distBS().iterator();
					var j:Int = 0;
					//Debug.flushln(szlist.toString());;

					while (itr.hasNext()) {
						
						val blk = itr.next();
						val spa  = blk.getMatrix() as SparseCSC;
						val datasz = szlist(j);
						if (here.id() == rootpid) 
							spa.initRemoteCopyAtSource();
						else
							spa.initRemoteCopyAtDest(datasz);
						
						WrapMPI.world.bcast(spa.getIndex(), 0, datasz, rootpid);
						WrapMPI.world.bcast(spa.getValue(), 0, datasz, rootpid);
							
						if (here.id() == rootpid) 
							spa.finalizeRemoteCopyAtSource();
						else
							spa.finalizeRemoteCopyAtDest();
						j++;
					}
				}
			}
		}
		return datcnt;
	}
	
	//=============================================================================
	// x10 remote copy implemetation of bcast
	//=============================================================================
	/**
	 *  Broadcast dense matrix among the pcnt number of places followed from here
	 */
	protected static def x10Bcast(distBS:BlocksPLH, rootpid:Int): Int {

		var datcnt:Int=0;
		
		if (here.id() != rootpid) {
			datcnt = at (Dist.makeUnique()(rootpid)) {
				x10Bcast(distBS, rootpid)
			};
		} else {
			if (Place.MAX_PLACES > 1) {
				val itr=distBS().iterator();
				while (itr.hasNext()) {
					val blk = itr.next();
					val blkdatsz = blk.getDataSize();
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
	
	//--------------------------------------------------
	private static def startBinaryTreeCast(distBS:BlocksPLH, 
			srcblk:MatrixBlock, datcnt:Int) {
		finish {
			if (here.id() != 0) async {
				val plcnt = here.id();
				val sttpl = 0;
				copyCastToBranch(distBS, srcblk, datcnt, sttpl, plcnt);
			}
			val plcnt = Place.MAX_PLACES-here.id();
			if (plcnt > 1 ) async {
				castToBranch(distBS, srcblk, datcnt, plcnt);
			}
		}
	}
	
	private static def copyCastToBranch(distBS:BlocksPLH, 
			srcblk:MatrixBlock, datCnt:Int,
			sttpl:Int, plcnt:Int): void {
		
		val blkid  = distBS().getGrid().getBlockId(srcblk.myRowId, srcblk.myColId);
		if (srcblk.isDense()) {
			val srcden = srcblk.getMatrix() as DenseMatrix;
			val srcbuf = new RemoteArray[Double](srcden.d as Array[Double]{self!=null});
			at (Dist.makeUnique()(sttpl)) {
				//Remote capture: distBS, srcbuf, blkid, datCnt, plcnt
				val dstblk = distBS().findBlock(blkid);
				val dstden = dstblk.getMatrix() as DenseMatrix;

				if (datCnt > 0)	finish {
					Array.asyncCopy[Double](srcbuf, 0, dstden.d, 0, datCnt);
				}
				if (plcnt > 1)
					castToBranch(distBS, dstblk, datCnt, plcnt);
			}			
		} else if (srcblk.isSparse()) {
			val srcspa = srcblk.getMatrix() as SparseCSC;
			val idxbuf:Array[Int](1)    = srcspa.getIndex();
			val valbuf:Array[Double](1) = srcspa.getValue();
			val srcidx = new RemoteArray[Int   ](idxbuf as Array[Int   ]{self!=null});
			val srcval = new RemoteArray[Double](valbuf as Array[Double]{self!=null});		
			at (Dist.makeUnique()(sttpl)) {
				//Remote capture: distBS, srcidx, srcval, srcoff, colOff, colCnt, datCnt
				val dstblk = distBS().findBlock(blkid);
				val dstspa = dstblk.getMatrix() as SparseCSC;
				dstspa.initRemoteCopyAtDest(datCnt);
				if (datCnt > 0) {
					finish Array.asyncCopy[Int   ](srcidx, 0, dstspa.getIndex(), 0, datCnt);
					finish Array.asyncCopy[Double](srcval, 0, dstspa.getValue(), 0, datCnt);
				}
				
				if (plcnt > 1 ) 
					castToBranch(distBS, dstblk, datCnt, plcnt);
				dstspa.finalizeRemoteCopyAtDest();				
			}
		} else {
			Debug.exit("Matrix block type is not supported");
		}
	}	
	//-------------------------------------------------------
	private static def castToBranch(distBS:BlocksPLH, 
			srcblk:MatrixBlock,	datCnt:Int,	plcnt:Int) {

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
}