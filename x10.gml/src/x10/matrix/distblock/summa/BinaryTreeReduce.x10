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

import x10.matrix.Debug;
import x10.matrix.RandTool;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;

import x10.matrix.comm.mpi.WrapMPI;

import x10.matrix.distblock.CastPlaceMap;
import x10.matrix.distblock.BlockSet;


/**
 * Grid row/column wise reduce
 *.
 */
protected class BinaryTreeReduce {

	//=========================================================
	//=========================================================
	protected  static def reduceToHere(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, dstblk:MatrixBlock, datCnt:Int, 
			select:(Int, Int)=>Int, opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix, 
			plist:Array[Int](1)):void {
			
		if (datCnt ==0) return;
		if (plist.size < 1) return;
		val pcnt   = plist.size;
		val rtcnt:Int = (pcnt+1) / 2 - 1; 
		val lfcnt  = pcnt - rtcnt - 1;
		val rtroot = plist(lfcnt);

		val lfplist = new Array[Int](lfcnt, (i:Int)=>plist(i));
		val rtplist = new Array[Int](rtcnt, (i:Int)=>plist(lfcnt+i+1));
		
		@Ifdef("MPI_COMMU") {
			//Debug.flushln("call mpiBinaryTreeReduce");
			mpiBinaryTreeReduce(distBS, tmpBS, rootbid, dstblk, datCnt, select, opFunc, lfplist, rtroot, rtplist);
		}
		@Ifndef("MPI_COMMU") {
			x10BinaryTreeReduce(distBS, tmpBS, rootbid, dstblk, datCnt, select, opFunc, lfplist, rtroot, rtplist);
		} 
	}
		
	
	//-----------------------------
	private static def x10BinaryTreeReduce(
			distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, dstblk:MatrixBlock, datCnt:Int,
			select:(Int,Int)=>Int, opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix,
			leftPlist:Array[Int](1), rightRoot:Int, rightPlist:Array[Int](1)) {
					
		var rmtbuf:RemoteArray[Double]=null;
		
		finish {
			//Left branch reduction
			if (leftPlist.size > 0)	async {
				reduceToHere(distBS, tmpBS, rootbid, dstblk, datCnt, select, opFunc, leftPlist);
			}
			async {
				// Need to bring data from remote place
				rmtbuf =  at (Dist.makeUnique()(rightRoot)) {
					//Remote capture:distBS, tmpBS, colCnt, rightPlist
					val rmtblk = distBS().findFrontBlock(rootbid, select);
					//Debug.flushln("Visiting block("+rmtblk.myRowId+","+rmtblk.myColId+")");

					if (rightPlist.size > 0)  {
						//Only if there are more places to visit than remote pid
						reduceToHere(distBS, tmpBS, rootbid, rmtblk, datCnt, select, opFunc, rightPlist);
					}
					new RemoteArray[Double](rmtblk.getData() as Array[Double]{self!=null})
				};
			}
		}
		
		//val dstblk = distBS().findFrontBlock(rootbid, select);
		val rcvblk = tmpBS().findFrontBlock(rootbid, select);
		val rcvden = rcvblk.getMatrix() as DenseMatrix;
		val dstden = dstblk.getMatrix() as DenseMatrix;

		finish Array.asyncCopy[Double](rmtbuf, 0, rcvden.d, 0, datCnt);
		//Debug.flushln("Perform reduction with data from place "+leftRoot);
		opFunc(rcvden, dstden, datCnt);
	}

	private static def mpiBinaryTreeReduce(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], rootbid:Int, dstblk:MatrixBlock, datCnt:Int, select:(Int,Int)=>Int, opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix,	leftPlist:Array[Int](1), rightRoot:Int,
			rightPlist:Array[Int](1)):void  {
					
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
				at (Dist.makeUnique()(rightRoot)) async {
					//Debug.flushln("Start left branch");
					//Remote capture:distBS, tmpBS, colCnt, rightPlist
					val rmtblk = distBS().findFrontBlock(rootbid, select);
					if (rightPlist.size > 0)  {
						reduceToHere(distBS, tmpBS, rootbid, rmtblk, datCnt, select, opFunc, rightPlist);
					}
					val rmtden = rmtblk.getMatrix() as DenseMatrix;
					val tag    = rootbid;//baseTagCopyTo + here.id();
				
					//Debug.flushln("Reduce - Start sending data to "+dstpid);
					WrapMPI.world.send(rmtblk.getData(), 0, datCnt, dstpid, tag);
					//Debug.flushln("Reduce - Done sending data to "+dstpid);
				}
		
				async {
					val tag    = rootbid;//baseTagCopyTo + remotepid;
					//Debug.flushln("Reduce - Ready to receive data from "+rightRoot+" tag:"+tag);
					WrapMPI.world.recv(rcvden.d, 0, datCnt, rightRoot, tag);
					//Debug.flushln("Reduce - Data recieved from "+rightRoot+" tag:"+tag);

					val dstden = dstblk.getMatrix() as DenseMatrix;
					opFunc(rcvden, dstden, datCnt);
				}
			}
		}
	}
}