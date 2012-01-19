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
public class BlockBcast extends BlockRemoteCopy {

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
	public static def bcast(distBS:BlocksPLH) = bcast(distBS, 0, 0, distBS().getGrid().getColSize(0));
	public static def bcast(distBS:BlocksPLH, rootbid:Int) = bcast(distBS, rootbid, 0, distBS().getGrid().getColSize(rootbid));

	/**
	 * Broadcast dense matrix from here to all other places. 
	 * This routine is used in sync of DupDenseMatrix
	 *
	 * @param dupmat      Input+output. Distributed storage for dense matrix in all places
	 * @param coloff      Input. Offset for the starting column for broadcast
	 * @param colcnt      Input. Count of columns to broadcast
	 * @return            Number of elements to broadcast
	 */
	public static def bcast(distBS:BlocksPLH, rootbid:Int, coloff:Int, colcnt:Int) : Int {
		var dsz:Int = 0;
		val mat0 = distBS().getFirst();
			
		@Ifdef("MPI_COMMU") {
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
		//Local sync 
		finish ateach ([p]:Point in Dist.makeUnique()) {
			distBS().sync();
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
	protected static def mpiBcastDense(distBS:BlocksPLH, rootbid:Int, colOff:Int, colCnt:Int):Int {
			
		if (colCnt < 0) return 0;
		val datasz:Int = compBlockDataSize(distBS, rootbid, colOff, colCnt);
		
		@Ifdef("MPI_COMMU") {
			val rootpid    = distBS().findPlace(rootbid);
			//Get the data count from root block
			finish ateach (d:Point in Dist.makeUnique()) {
				//Remote capture: colOff, datasz, rootpid, rootpid
				val bset = distBS();
				val blk  = (here.id()==rootpid)?bset.findBlock(rootbid):bset.getFirst();
				val den  = blk.getMatrix() as DenseMatrix;
				val offset = den.M * colOff;
				
				WrapMPI.world.bcast(den.d, offset, datasz, rootpid);
				//blks.sync(blk); 
			}
		}
		return datasz;
	}

	//--------------------------------------------------------------------------
	//---------------------------------------------------------------
	/**
	 *  Broadcast dense matrix among the pcnt number of places followed from here
	 */
	protected static def x10Bcast(distBS:BlocksPLH, rootbid:Int, colOff:Int, colCnt:Int): Int {

		var datcnt:Int=0;
		if (colCnt == 0) return 0;
		val rootpid = distBS().findPlace(rootbid);
		
		finish {
			//Start two part
			//1) if rootpid!=0, goto place 0, start binaryTreeCast within the pid rang of (0 ~ rootpid-1)
			//2) goto rootpid, start binaryTreeCast within the pid rang of (rootpid, Places.MAX_PLACES-1)

			if (rootpid != 0) { 
				at (Dist.makeUnique()(0)) {
					//Remote capture: distBS, rootbid, colOff, colCnt
					val mat = distBS().getFirst().getMatrix();
					val dsz = BlockRemoteCopy.copy(distBS, rootbid, colOff, mat, colOff, colCnt);
					val pcnt = rootpid - 1;
					if (pcnt > 1) async {
						binaryTreeCast(distBS, colOff, colCnt, dsz, pcnt);
					}
				}
			}
			
			datcnt = at (Dist.makeUnique()(rootpid)) {
				//Remote capture: distBS, rootbid, colOff, colCnt
				val bset = distBS();
				val rtblk = bset.findBlock(rootbid);
				val llblk = bset.getFirst();
				if (rtblk.myRowId != llblk.myRowId || rtblk.myColId != llblk.myColId)
					rtblk.copyCols(colOff, colCnt, llblk.getMatrix());

				val pcnt = Place.MAX_PLACES - here.id();
				val dsz  = compBlockDataSize(distBS, rootbid, colOff, colCnt);
				
				if (pcnt > 1) async {
					binaryTreeCast(distBS, colOff, colCnt, dsz, pcnt);
				}
				dsz
			};
		}
		return datcnt;
	}
	
	//--------------------------------------------------
	protected static def binaryTreeCast(distBS:BlocksPLH, colOff:Int, colCnt:Int, datasz:Int, pcnt:Int): void {
		
		if (pcnt <= 1) return;
		
		val mat0 = distBS().getFirst();
		if (mat0.isDense()) {
			binaryTreeCastDense(distBS, colOff, datasz, pcnt);
		} else if (mat0.isSparse()) {
			
			val srcmat = distBS().getFirst().getMatrix() as SparseCSC;
			val srcoff = srcmat.getNonZeroOffset(colOff);

			srcmat.initRemoteCopyAtSource(colOff, colCnt);			
			binaryTreeCastSparse(distBS, colOff, srcoff, colCnt, datasz, pcnt);
			srcmat.finalizeRemoteCopyAtSource();

			// Start local block sync with the first block in local block set.
			//distBS().sync();
		} else {
			Debug.exit("Matrix block type is not supported");
		}
	}	
	//----------------------------------------------------------------
		
	/**
	 * X10 implementation of broadcast data in binary tree routes, among
	 * the first block in all places
	 * 
	 *
	 */
	protected static def binaryTreeCastDense(distBS:BlocksPLH, colOff:Int, datasz:Int, pcnt:Int): void {
		
		val root   = here.id();
		//pcnt must > 1
		val lfcnt:Int = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = root + lfcnt;

		// Specify remote buffer
		val srcden = distBS().getFirst().getMatrix() as DenseMatrix;
		val srcbuf = new RemoteArray[Double](srcden.d as Array[Double](1){self!=null});
		val srcoff = srcden.M * colOff; //Source offset is determined by local M
		
		finish {
			at (Dist.makeUnique()(rtroot)) {
				//Remote capture:srcbuf, srcoff, datasz, and rootbid
				val blk    = distBS().getFirst();
				Debug.assure(blk!=null, "No block in local block set");
				val dstden = blk.getMatrix() as DenseMatrix;
				val dstoff = colOff * dstden.M; //Destination offset is determined by local M
				// Using copyFrom style
				finish Array.asyncCopy[Double](srcbuf, srcoff, dstden.d, dstoff, datasz);
				
				// Perform binary bcast on the right branch
				if (rtcnt > 1 ) async {
					binaryTreeCastDense(distBS, colOff, datasz, rtcnt);
				}
			}

			// Perform binary bcast on the left branch
			if (lfcnt > 1) async {
				binaryTreeCastDense(distBS, colOff, datasz, lfcnt); 
			}
			// Start local block sync with the first block in local block set.
			//distBS().sync();
		}
	}


	//=================================================
	// Broadcast SparseCSC matrix to all
	//=================================================


	/**
	 * Using MPI routine to implement sparse matrix broadcast
	 *
	 */
	protected static def mpiBcastSparse(distBS:BlocksPLH, rootbid:Int, colOff:Int, colCnt:Int):Int {
	
		val root   = here.id();
		val datasz = compBlockDataSize(distBS, rootbid, colOff, colCnt);  

		@Ifdef("MPI_COMMU") {
			val rootpid    = distBS().findPlace(rootbid);
			finish ateach (val [p]:Point in Dist.makeUnique()) {
				//Need: rootpid, rootbid, distBS, datasz, colOff, colCnt,
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
				
				WrapMPI.world.bcast(spa.getIndex(), offset, datasz, root);
				WrapMPI.world.bcast(spa.getValue(), offset, datasz, root);
				
				if (p == rootpid) 
					spa.finalizeRemoteCopyAtSource();
				else
					spa.finalizeRemoteCopyAtDest();
				
				//distBS().sync(blk);
			}
		}
		return datasz;
	}

	//-------------------------------------------------------

	/**
	 * Broadcast sparse matrix using remote array copy in X10
	 */
	protected static def binaryTreeCastSparse(
			distBS:BlocksPLH, 
			colOffset:Int, srcOffset:Int, 
			colCnt:Int,  dataCnt:Int, 
			pcnt:Int): void {
		
		val myid = here.id();
		val lfcnt:Int = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = myid + lfcnt;

		// Specify the remote buffer
		val srcspa = distBS().getFirst().getMatrix() as SparseCSC;
		val idxbuf:Array[Int](1)    = srcspa.getIndex();
		val valbuf:Array[Double](1) = srcspa.getValue();
		val srcidx = new RemoteArray[Int   ](idxbuf as Array[Int   ]{self!=null});
		val srcval = new RemoteArray[Double](valbuf as Array[Double]{self!=null});
	
		finish {
			at (Dist.makeUnique()(rtroot)) {
				//Need: distBS, srcidx, srcval, srcOff, colOff, colCnt and datasz
				val dstspa = distBS().getFirst().getMatrix() as SparseCSC;
				val dstoff = dstspa.getNonZeroOffset(colOffset); 
				// Using copyFrom style
				//++++++++++++++++++++++++++++++++++++++++++++
				//Do NOT call getIndex()/getValue() before init at destination place
				//+++++++++++++++++++++++++++++++++++++++++++++
				dstspa.initRemoteCopyAtDest(colOffset, colCnt, dataCnt);
				finish Array.asyncCopy[Int   ](srcidx, srcOffset, 
											   dstspa.getIndex(), dstoff, dataCnt);
				finish Array.asyncCopy[Double](srcval, srcOffset, 
											   dstspa.getValue(), dstoff, dataCnt);

				// Perform binary bcast on the right brank
				if (rtcnt > 1 ) async {
					binaryTreeCastSparse(distBS, colOffset, dstoff, colCnt, dataCnt, rtcnt);
					dstspa.finalizeRemoteCopyAtDest();
				} else {
					dstspa.finalizeRemoteCopyAtDest();
				}

				// Start local block sync with the first block in local block set.
				// distBS().sync();
			}

			// Perform binary bcast on the left branch
			if (lfcnt > 1) async {
				binaryTreeCastSparse(distBS, colOffset, srcOffset, colCnt, dataCnt, lfcnt); 
			}
		}
	}
}