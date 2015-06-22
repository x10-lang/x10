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

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.comm.mpi.WrapMPI;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;
import x10.matrix.distblock.CastPlaceMap;

/**
 * Ring cast sends data from here to a set of blocks, or partial broadcast.
 */
public class BlockRingReduce extends BlockRemoteCopy {
	// RingCast: receive form previous one and send to one next in a ring 
	public static def rowWise(rid:Long, cid:Long):Long = rid;
	public static def colWise(rid:Long, cid:Long):Long = cid;
	
	public static def rowReduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH, rootbid:Long, colCnt:Long):void {
		ringReduce(distBS, tmpBS, rootbid, colCnt, 
				(rid:Long,Long)=>rid, 
				(src:DenseMatrix,dst:DenseMatrix, cc:Long)=>sum(src, dst, cc));
	}
	
	public static def colReduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH, rootbid:Long, colCnt:Long):void {
		ringReduce(distBS, tmpBS, rootbid, colCnt, 
				(rid:Long,cid:Long)=>cid, 
				(src:DenseMatrix,dst:DenseMatrix, cc:Long)=>sum(src, dst, cc));
	}
	
	public static def sum(src:DenseMatrix, root:DenseMatrix, colCnt:Long):DenseMatrix {
		for (var i:Long=0; i<root.M*colCnt; i++) {
			root.d(i) += src.d(i);
		}
		return root;
	}

	public static def ringReduce(distBS:BlocksPLH, tmpBS:BlocksPLH, rootbid:Long, colCnt:Long, 
			select:(Long,Long)=>Long, 
			opFunc:(DenseMatrix, DenseMatrix, Long)=>DenseMatrix) {
		
		val rootpid = distBS().findPlace(rootbid);
		
		if (rootpid != here.id()) {
			//Goto rootpid to start ringcast
			at(Place(rootpid)) {
				ringReduce(distBS, tmpBS, rootbid, colCnt, select, opFunc);
			}
		} else {
			//Romte capture: distBS(), rootbid, colCnt
			val dmap = distBS().getDistMap();
			val grid = distBS().getGrid();
			// TODO: is Place.places() the proper parameter below?
			val plst =  CastPlaceMap.buildPlaceList(grid, dmap, rootbid, select, Place.places()); 
			//Root is the first in the list
			assert (plst(0)==rootpid) :
                "RingCast place list must starts with root place id";
			if (plst.size >= 1) {
				val rootblk = distBS().findBlock(rootbid);
				reduceToHere(distBS, tmpBS, rootblk, colCnt, select, opFunc, plst);
			}
		}		
	}

	protected static def reduceToHere(distBS:BlocksPLH, tmpBS:BlocksPLH, 
        rootblk:MatrixBlock, colCnt:Long, 
        select:(Long, Long)=>Long,
        opFunc:(DenseMatrix, DenseMatrix, Long)=>DenseMatrix,
        plst:Rail[Long]): void {
	
        //val leftRoot = here.id();
        val pcnt = plst.size;
        if (pcnt > 1) {
            assert here.id() == plst(0);
            val leftPCnt  = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
            val rightPCnt  = pcnt - leftPCnt;
            val rightplst = new Rail[Long](rightPCnt, (i:Long)=>plst(leftPCnt+i));
            val leftplst  = new Rail[Long](leftPCnt, (i:Long)=>plst(i));

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

	private static def x10BinaryTreeReduce(distBS:BlocksPLH, tmpBS:BlocksPLH, 
			rootblk:MatrixBlock, colCnt:Long,
			select:(Long,Long)=>Long,	
			opFunc:(DenseMatrix, DenseMatrix, Long)=>DenseMatrix, 
			nearbyPlcList:Rail[Long], 
			remotePlcList:Rail[Long]) {
		
		var rmtbuf:GlobalRail[ElemType];
		finish {
			//Left branch reduction
			val remotepid = remotePlcList(0);
			rmtbuf =  at(Place(remotepid)) {
				//Remote capture:distBS, tmpBS, colCnt, remotePlcList
				val rootbid = distBS().getGrid().getBlockId(rootblk.myRowId, rootblk.myColId);
				val rmtblk = distBS().findFrontBlock(rootbid, select);
				async {
					reduceToHere(distBS, tmpBS, rmtblk, colCnt, select, opFunc, remotePlcList);
				}
				
				new GlobalRail[ElemType](rmtblk.getData() as Rail[ElemType]{self!=null})
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
		finish Rail.asyncCopy[ElemType](rmtbuf, 0, rcvden.d, 0, datcnt);
		
		opFunc(rcvden, dstden, colCnt);
		//dstmat.cellAdd(rcvmat as DenseMatrix(dstmat.M, dstmat.N));
	}

	private static def mpiBinaryTreeReduce(distBS:BlocksPLH, tmpBS:BlocksPLH, 
			rootblk:MatrixBlock, colCnt:Long,
			select:(Long,Long)=>Long,
			opFunc:(DenseMatrix, DenseMatrix, Long)=>DenseMatrix, 
			nearbyPlcList:Rail[Long], 
			remotePlcList:Rail[Long])  {
				
		@Ifdef("MPI_COMMU") {
			val dstpid = here.id();
			val rcvbid = distBS().getGrid().getBlockId(rootblk.myRowId, rootblk.myColId);
			val rcvblk = tmpBS().findFrontBlock(rcvbid, select);
			val rcvden = rcvblk.getMatrix() as DenseMatrix;
			
			finish {
				//Left branch reduction
				val remotepid = remotePlcList(0);
				at(Place(remotepid)) async {
					//Remote capture:distBS, tmpBS, colCnt, remotePlcList
					val rootbid = distBS().getGrid().getBlockId(rootblk.myRowId, rootblk.myColId);
					val rmtblk = distBS().findFrontBlock(rootbid, select);
					
					reduceToHere(distBS, tmpBS, rmtblk, colCnt, select, opFunc, remotePlcList);
					
					val rmtden = rmtblk.getMatrix() as DenseMatrix;
					val datcnt = rmtden.M * colCnt;
					val tag = baseTagCopyTo + here.id();
					
					WrapMPI.world.send(rmtblk.getData(), 0, datcnt, dstpid, tag);
				}
				//left branch reduction
				async {
					reduceToHere(distBS, tmpBS, rootblk, colCnt, select, opFunc, nearbyPlcList);
					
					val datcnt = rcvden.M*colCnt;
					val tag    = baseTagCopyTo + remotepid;
					WrapMPI.world.recv(rcvden.d, 0, datcnt, remotepid, tag);
				}
			}
			
			val dstden = rootblk.getMatrix() as DenseMatrix;
			opFunc(rcvden, dstden, colCnt);
		}
	}
}
