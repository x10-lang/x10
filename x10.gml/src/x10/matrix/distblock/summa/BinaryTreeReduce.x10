/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.matrix.distblock.summa;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.block.MatrixBlock;
import x10.matrix.comm.mpi.WrapMPI;
import x10.matrix.distblock.BlockSet;

/**
 * Grid row/column wise reduce
 */
protected class BinaryTreeReduce {
    protected static def reduceToHere(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Long, dstblk:MatrixBlock, datCnt:Long, 
			select:(Long,Long)=>Long, opFunc:(DenseMatrix,DenseMatrix,Long)=>DenseMatrix, 
			plist:Rail[Long]):void {
			
		if (datCnt == 0L) return;
		if (plist.size < 1) return;
		val pcnt   = plist.size;
		val rtcnt  = (pcnt+1) / 2 - 1; 
		val lfcnt  = pcnt - rtcnt - 1;
		val rtroot = plist(lfcnt);

		val lfplist = new Rail[Long](lfcnt, (i:Long)=>plist(i));
		val rtplist = new Rail[Long](rtcnt, (i:Long)=>plist((lfcnt+i+1)));
		
		@Ifdef("MPI_COMMU") {
			//Debug.flushln("call mpiBinaryTreeReduce");
			mpiBinaryTreeReduce(distBS, tmpBS, rootbid, dstblk, datCnt, select, opFunc, lfplist, rtroot, rtplist);
		}
		@Ifndef("MPI_COMMU") {
			x10BinaryTreeReduce(distBS, tmpBS, rootbid, dstblk, datCnt, select, opFunc, lfplist, rtroot, rtplist);
		} 
	}
		
	private static def x10BinaryTreeReduce(
			distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Long, dstblk:MatrixBlock, datCnt:Long,
			select:(Long,Long)=>Long, opFunc:(DenseMatrix,DenseMatrix,Long)=>DenseMatrix,
			leftPlist:Rail[Long], rightRoot:Long, rightPlist:Rail[Long]) {
					
		val rmtbuf:GlobalRail[Double];
		
		finish {
			//Left branch reduction
			if (leftPlist.size > 0)	async {
				reduceToHere(distBS, tmpBS, rootbid, dstblk, datCnt, select, opFunc, leftPlist);
			}
			// Need to bring data from remote place
            rmtbuf = at(Place(rightRoot)) {
				//Remote capture:distBS, tmpBS, colCnt, rightPlist
				val rmtblk = distBS().findFrontBlock(rootbid, select);
				//Debug.flushln("Visiting block("+rmtblk.myRowId+","+rmtblk.myColId+")");

				if (rightPlist.size > 0)  {
					//Only if there are more places to visit than remote pid
					reduceToHere(distBS, tmpBS, rootbid, rmtblk, datCnt, select, opFunc, rightPlist);
				}
				new GlobalRail[Double](rmtblk.getData() as Rail[Double]{self!=null})
			};
		}
		
		//val dstblk = distBS().findFrontBlock(rootbid, select);
		val rcvblk = tmpBS().findFrontBlock(rootbid, select);
		val rcvden = rcvblk.getMatrix() as DenseMatrix;
		val dstden = dstblk.getMatrix() as DenseMatrix;

		finish Rail.asyncCopy[Double](rmtbuf, 0, rcvden.d, 0, datCnt);
		//Debug.flushln("Perform reduction with data from place "+leftRoot);
		opFunc(rcvden, dstden, datCnt);
	}

	private static def mpiBinaryTreeReduce(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], rootbid:Long, dstblk:MatrixBlock, datCnt:Long, select:(Long,Long)=>Long, opFunc:(DenseMatrix,DenseMatrix,Long)=>DenseMatrix, leftPlist:Rail[Long], rightRoot:Long, rightPlist:Rail[Long]):void  {
		@Ifdef("MPI_COMMU") 
		{
			val dstpid = here.id();
			val rcvblk = tmpBS().findFrontBlock(rootbid, select);
			val rcvden = rcvblk.getMatrix() as DenseMatrix;
			finish {
				//Left branch reduction
				if (leftPlist.size > 0) async {
					//Debug.flushln("Start left branch");
					reduceToHere(distBS, tmpBS, rootbid, dstblk, datCnt, select, opFunc, leftPlist);
				}

				//Debug.flushln("Goto right branch root:"+leftRoot);
				at(Place(rightRoot)) {
					//Debug.flushln("Start left branch");
					//Remote capture:distBS, tmpBS, colCnt, rightPlist
					val rmtblk = distBS().findFrontBlock(rootbid, select);
					if (rightPlist.size > 0)  {
						reduceToHere(distBS, tmpBS, rootbid, rmtblk, datCnt, select, opFunc, rightPlist);
					}
					val rmtden = rmtblk.getMatrix() as DenseMatrix;
					val tag = rootbid;//baseTagCopyTo + here.id();
				
					//Debug.flushln("Reduce - Start sending data to "+dstpid);
					WrapMPI.world.send(rmtblk.getData(), 0, datCnt, dstpid, tag);
					//Debug.flushln("Reduce - Done sending data to "+dstpid);
				}
		
                val tag = rootbid;//baseTagCopyTo + remotepid;
                //Debug.flushln("Reduce - Ready to receive data from "+rightRoot+" tag:"+tag);
                WrapMPI.world.recv(rcvden.d, 0, datCnt, rightRoot, tag);
                //Debug.flushln("Reduce - Data recieved from "+rightRoot+" tag:"+tag);

                val dstden = dstblk.getMatrix() as DenseMatrix;
                opFunc(rcvden, dstden, datCnt);
			}
		}
	}
}
