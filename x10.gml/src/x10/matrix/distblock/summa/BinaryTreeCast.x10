/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.matrix.distblock.summa;

import x10.io.Console;
import x10.util.Timer;
import x10.util.ArrayList;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;
import x10.compiler.Inline;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;

import x10.matrix.comm.WrapMPI;

import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.CastPlaceMap;

/**
 * Grid row/column-wise broadcast
 *.
 */
protected class BinaryTreeCast  {

	//=================================================================
	//==================================================================
	/**
	 * Broadcast data from local root block at here to a list of places. 
	 * plist does not contains here.id()
	 */
	protected static def castToPlaces(
			distBS:PlaceLocalHandle[BlockSet], rootbid:Int, datCnt:Int, 
			select:(Int,Int)=>Int, 
			plist:Array[Int](1)){

		if (plist.size < 1 ) return;
		val pcnt   = plist.size;
		val rtcnt:Int = (pcnt+1) / 2 - 1; 
		val lfcnt  = pcnt - rtcnt - 1;
		val rtroot = plist(lfcnt);

		val lfplist = new Array[Int](lfcnt, (i:Int)=>plist(i));
		val rtplist = new Array[Int](rtcnt, (i:Int)=>plist(lfcnt+i+1));

		//Debug.flushln("left branch list:"+lfplist.toString());
		//Debug.flushln("Right branch root:"+rtroot+" list:"+rtplist.toString());		
		finish {
			async {
				copyBlockToRightBranch(distBS, rootbid, rtroot, datCnt, select, rtplist);
			}
			// Perform binary bcast on the left branch
			if (lfcnt > 0) async {
				castToPlaces(distBS, rootbid, datCnt, select, lfplist); 
			}
		}
	}
	//--------------------------------------------------------------
	
	private static def copyBlockToRightBranch(
			distBS:PlaceLocalHandle[BlockSet], rootbid:Int, remotepid:Int, datCnt:Int,
			select:(Int,Int)=>Int, plist:Array[Int](1)):void {

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

	//--------------------------------------------------------------
	//--------------------------------------------------------------
	private static def x10CopyDenseBlock(distBS:PlaceLocalHandle[BlockSet], rootbid:Int, 
			srcblk:MatrixBlock, rmtpid:Int, datCnt:Int,	select:(Int,Int)=>Int, 
			plist:Array[Int](1)) {
			
		val srcden = srcblk.getMatrix() as DenseMatrix;
		val srcbuf = new RemoteArray[Double](srcden.d as Array[Double](1){self!=null});
		at (Dist.makeUnique()(rmtpid)) {
			//Remote capture:distBS, rootbid, datCnt, rtplist
			val blk  = distBS().findFrontBlock(rootbid, select);
			val dstden = blk.getMatrix() as DenseMatrix;
			// Using copyFrom style
			if (datCnt > 0) {
				finish Array.asyncCopy[Double](srcbuf, 0, dstden.d, 0, datCnt);
			}
			// Perform binary bcast on the right branch
			if (plist.size > 0 ) {
				castToPlaces(distBS, rootbid, datCnt, select, plist);
			}
		}
		
	}
	
	private static def x10CopySparseBlock(
			distBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, srcblk:MatrixBlock, rmtpid:Int, datCnt:Int,
			select:(Int,Int)=>Int, plist:Array[Int](1)) {
		
		val srcspa = srcblk.getMatrix() as SparseCSC;
		val srcidx = new RemoteArray[Int   ](srcspa.getIndex() as Array[Int   ]{self!=null});
		val srcval = new RemoteArray[Double](srcspa.getValue() as Array[Double]{self!=null});
		
		at (Dist.makeUnique()(rmtpid)) {
			//Remote capture:distBS, rootbid, datCnt, rtplist
			val blk    = distBS().findFrontBlock(rootbid, select);
			val dstspa = blk.getMatrix() as SparseCSC;
			// Using copyFrom style
			dstspa.initRemoteCopyAtDest(datCnt);
			if (datCnt > 0) {
				finish Array.asyncCopy[Int   ](srcidx, 0, dstspa.getIndex(), 0, datCnt);
				finish Array.asyncCopy[Double](srcval, 0, dstspa.getValue(), 0, datCnt);
			}
			// Perform binary bcast on the right branch
			if (plist.size > 0 ) {
				castToPlaces(distBS, rootbid, datCnt, select, plist);
			}
			// if (rootbid==0 && here.id()==2) {
			// 	Debug.flushln("Value:"+blk.getData().toString());
			// 	Debug.flushln("Index:"+blk.getIndex().toString());
			// }		
			dstspa.finalizeRemoteCopyAtDest();

		}	
	}	
	//=======================================================================
	private static def mpiCopyDenseBlock(distBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, srcblk:MatrixBlock, rmtpid:Int, datCnt:Int, select:(Int,Int)=>Int, 
			plist:Array[Int](1)):void {
		//Must use ":void", otherwise, @Ifdef("xxx") won't work
		//Need further investiagtion.
		val srcpid = here.id();
		val srcden = srcblk.getMatrix() as DenseMatrix;
		val tag    = rootbid;//RandTool.nextInt(Int.MAX_VALUE);
	
		@Ifdef("MPI_COMMU") 
		{
			//Tag is used to differ different ring cast.
			//Row and column-wise ringcast must NOT be carried out at the same
			//time. This tag only allows ringcast be differed by root block id.
			finish {
				 at (Dist.makeUnique()(rmtpid)) async {
					//Remote capture:distBS, rootbid, datCnt, rtplist, tag
					val blk    = distBS().findFrontBlock(rootbid, select);
					val dstden = blk.getMatrix() as DenseMatrix;
					//Debug.flushln("BinaryTree cast:Start recv data from "+srcpid);
					WrapMPI.world.recv(dstden.d, 0, datCnt, srcpid, tag);
					//Debug.flushln("BinaryTree cast:Done recv data from "+srcpid);
					if (plist.size > 0 )  {
						castToPlaces(distBS, rootbid, datCnt, select, plist);
					}
				}
				async {
					//Debug.flushln("BinaryTree cast: start sending data to "+rmtpid);
					WrapMPI.world.send(srcden.d, 0, datCnt, rmtpid, tag);
					//Debug.flushln("BinaryTree cast: Done sending data to "+rmtpid+ " done");
				}
			}
		}
	}
	
	private static def mpiCopySparseBlock(distBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, srcblk:MatrixBlock, rmtpid:Int, datCnt:Int, select:(Int,Int)=>Int, 
			plist:Array[Int](1)):void {
		
		val srcpid = here.id();
		val srcspa = srcblk.getMatrix() as SparseCSC;
		val tag = rootbid;//RandTool.nextInt(Int.MAX_VALUE);
		//Tag must allow to differ multiply ringcast.

		@Ifdef("MPI_COMMU") 
		{
			finish {
				
				at (Dist.makeUnique()(rmtpid)) async {
					//Remote capture:distBS, rootbid, datCnt, rtplist, tag
					val blk    = distBS().findFrontBlock(rootbid, select);
					val dstspa = blk.getMatrix() as SparseCSC;
					dstspa.initRemoteCopyAtDest(datCnt);
					WrapMPI.world.recv(dstspa.getIndex(), 0, datCnt, srcpid, tag);
					WrapMPI.world.recv(dstspa.getValue(), 0, datCnt, srcpid, tag+1000000);

					// Perform binary bcast on the right branch
					//Debug.flushln("Recv "+here.id()+" get from "+srcpid);
					if (plist.size > 0 ) {
						castToPlaces(distBS, rootbid, datCnt, select, plist);
					}
					dstspa.finalizeRemoteCopyAtDest();
				}
				async {
					WrapMPI.world.send(srcspa.getIndex(), 0, datCnt, rmtpid, tag);
					WrapMPI.world.send(srcspa.getValue(), 0, datCnt, rmtpid, tag+1000000);
				}

			}
		}
	}
}