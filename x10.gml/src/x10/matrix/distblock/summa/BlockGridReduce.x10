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

import x10.matrix.comm.WrapMPI;

import x10.matrix.distblock.CastPlaceMap;
import x10.matrix.distblock.BlockSet;


/**
 * Ring cast sends data from here to a set of blocks, or partial broadcast
 *.
 */
public class BlockGridReduce {

	//==================================================
	// RingCast: receive form previous one and send to one next in a ring 
	//==================================================
	public static def rowWise(rid:Int, cid:Int):Int = rid;
	public static def colWise(rid:Int, cid:Int):Int = cid;
	
	
	/**
	 * 
	 */
	public static def rowReduceSum(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], rootbid:Int, colCnt:Int):void {
		ringReduce(distBS, tmpBS, rootbid, colCnt, 
				(rid:Int,cid:Int)=>rid, 
				(src:DenseMatrix,dst:DenseMatrix, cc:Int)=>sum(src, dst, cc));
	}
	
	public static def colReduceSum(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], rootbid:Int, colCnt:Int):void {
		ringReduce(distBS, tmpBS, rootbid, colCnt, 
				(rid:Int,cid:Int)=>cid, 
				(src:DenseMatrix,dst:DenseMatrix, cc:Int)=>sum(src, dst, cc));
	}
	
	public static def sum(src:DenseMatrix, root:DenseMatrix, colCnt:Int):DenseMatrix {
		for (var i:Int=0; i<root.M*colCnt; i++) {
			root.d(i) += src.d(i);
		}
		return root;
	}
	//===================================================================

	public static def ringReduce(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], rootbid:Int, colCnt:Int, 
			select:(Int,Int)=>Int, 
			opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix) {
		
		val rootpid = distBS().findPlace(rootbid);
		
		if (rootpid != here.id()) {
			//Goto rootpid to start ringcast
			at (Dist.makeUnique()(rootpid)) {
				ringReduce(distBS, tmpBS, rootbid, colCnt, select, opFunc);
			}
		} else {
			//Romte capture: distBS(), rootbid, colCnt
			val dmap = distBS().getDistMap();
			val grid = distBS().getGrid();
			val plst =  CastPlaceMap.buildPlaceList(grid, dmap, rootbid, select); 
			//Root is the first in the list
			Debug.assure(plst(0)==rootpid, "RingCast place list must starts with root place id");
			//Debug.flushln("Ring cast to "+plst.toString());
			if (plst.size >= 1) {
				val rootblk = distBS().findBlock(rootbid);
				reduceToHere(distBS, tmpBS, rootblk, colCnt, select, opFunc, plst);
			}
		}		
	}
	
	//=========================================================
	protected static def reduceToHere(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], 
			rootblk:MatrixBlock, colCnt:Int, 
			select:(Int, Int)=>Int,
			opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix,
			plst:Array[Int](1)): void {
				
				//val leftRoot = here.id();
				val pcnt = plst.size;
				if (pcnt > 1) {
					Debug.assure(here.id()==plst(0));
					val leftPCnt  = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
					val rightPCnt  = pcnt - leftPCnt;
					val rightplst = new Array[Int](rightPCnt, (i:Int)=>plst(leftPCnt+i));
					val leftplst  = new Array[Int](leftPCnt, (i:Int)=>plst(i));

					@Ifdef("MPI_COMMU") {
						mpiBinaryTreeReduce(distBS, tmpBS, rootblk, colCnt, select, opFunc, 
								leftplst, rightplst);
					}
					@Ifndef("MPI_COMMU") {
						x10BinaryTreeReduce(distBS, tmpBS, rootblk, colCnt, select, opFunc, 
								leftplst, rightplst);
					}
				} else if (pcnt == 1) {
					distBS().selectReduce(rootblk, colCnt, select, opFunc);
				}
			}
				
	
	//-----------------------------
	private static def x10BinaryTreeReduce(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], 
			rootblk:MatrixBlock, colCnt:Int,
			select:(Int,Int)=>Int,	
			opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix, 
			nearbyPlcList:Array[Int](1), 
			remotePlcList:Array[Int](1)) {
		
		var rmtbuf:RemoteArray[Double];
		finish {
			//Left branch reduction
			val remotepid = remotePlcList(0);
			rmtbuf =  at (Dist.makeUnique()(remotepid)) {
				//Remote capture:distBS, tmpBS, colCnt, remotePlcList
				val rootbid = distBS().getGrid().getBlockId(rootblk.myRowId, rootblk.myColId);
				val rmtblk = distBS().findFrontBlock(rootbid, select);
				async {
					reduceToHere(distBS, tmpBS, rmtblk, colCnt, select, opFunc, remotePlcList);
				}
				
				new RemoteArray[Double](rmtblk.getData() as Array[Double]{self!=null})
			};
			//Right branch reduction
			async {
				reduceToHere(distBS, tmpBS, rootblk, colCnt, select, opFunc, nearbyPlcList);
			}
		}
		val rootbid = distBS().getGrid().getBlockId(rootblk.myRowId, rootblk.myColId);
		val rcvblk = tmpBS().findFrontBlock(rootbid, select);
		val rcvden = rcvblk.getMatrix() as DenseMatrix;
		val dstden = rootblk.getMatrix() as DenseMatrix;

		val datcnt = dstden.M*colCnt;
		finish Array.asyncCopy[Double](rmtbuf, 0, rcvden.d, 0, datcnt);
		
		opFunc(rcvden, dstden, colCnt);
		//dstmat.cellAdd(rcvmat as DenseMatrix(dstmat.M, dstmat.N));
	}

	private static def mpiBinaryTreeReduce(distBS:PlaceLocalHandle[BlockSet], tmpBS:PlaceLocalHandle[BlockSet], 
			rootblk:MatrixBlock, colCnt:Int,
			select:(Int,Int)=>Int,
			opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix, 
			nearbyPlcList:Array[Int](1), 
			remotePlcList:Array[Int](1))  {
				
		@Ifdef("MPI_COMMU") {
			val dstpid = here.id();
			val rcvbid = distBS().getGrid().getBlockId(rootblk.myRowId, rootblk.myColId);
			val rcvblk = tmpBS().findFrontBlock(rcvbid, select);
			val rcvden = rcvblk.getMatrix() as DenseMatrix;
			
			finish {
				//Left branch reduction
				val remotepid = remotePlcList(0);
				at (Dist.makeUnique()(remotepid)) async {
					//Remote capture:distBS, tmpBS, colCnt, remotePlcList
					val rootbid = distBS().getGrid().getBlockId(rootblk.myRowId, rootblk.myColId);
					val rmtblk = distBS().findFrontBlock(rootbid, select);
					
					reduceToHere(distBS, tmpBS, rmtblk, colCnt, select, opFunc, remotePlcList);
					
					val rmtden = rmtblk.getMatrix() as DenseMatrix;
					val datcnt = rmtden.M * colCnt;
					val tag = rootbid;//baseTagCopyTo + here.id();
					
					WrapMPI.world.send(rmtblk.getData(), 0, datcnt, dstpid, tag);
				}
				//left branch reduction
				async {
					reduceToHere(distBS, tmpBS, rootblk, colCnt, select, opFunc, nearbyPlcList);
					
					val datcnt = rcvden.M*colCnt;
					val tag    = rcvbid;//baseTagCopyTo + remotepid;
					WrapMPI.world.recv(rcvden.d, 0, datcnt, remotepid, tag);
				}
			}
			
			val dstden = rootblk.getMatrix() as DenseMatrix;
			opFunc(rcvden, dstden, colCnt);
		}
	}
}