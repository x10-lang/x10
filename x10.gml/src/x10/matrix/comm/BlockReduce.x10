/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package x10.matrix.comm;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.block.MatrixBlock;
import x10.matrix.distblock.BlockSet;

/**
 * This class provides reduce-sum communication for distributed matrix.
 */
public class BlockReduce extends BlockRemoteCopy {
	/**
	 * Reduce all distributed blocks and store the result of sum of all blocks at specified root block.
	 * All input blocks will be modified, and result will be stored at root block.
	 * 
	 * @param distBS     distributed blocks
	 * @param tmpBS      temporary space to store received blocks. Only one block in each place is required
	 * @param rootbid    root block ID.
	 * 
	 */
	public static def reduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH, rootbid:Long): void {
		x10Reduce(distBS, tmpBS, rootbid, (a:DenseMatrix, b:DenseMatrix)=>b.cellAdd(a as DenseMatrix(b.M,b.N)));
	}
	
	/**
	 * Perform reduce on all blocks, and store the result at root block.
	 * Input blocks will be modified.
	 * 
	 * @param distBS        input distributed blocks
	 * @param tmpBS         temporary space to store received blocks. Only one block is required for each place
	 * @param rootbid       root block ID
	 * @param opFunc        Reduce operation function, which take two dense matrix as parameters, the 
	 */
	public static def reduce(distBS:BlocksPLH, tmpBS:BlocksPLH, 
			rootbid:Long,
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix) {
		x10Reduce(distBS, tmpBS, rootbid, opFunc);
	}
	
	/**
	 * Reduce all dense matrices stored in DistArray from all places
	 * to here
	 *
	 * @param ddmat      Input/Output. Duplicated dense matrix of source and target matrix
	 * @param ddtmp      Temp matrix space to store the receiving data.
	 * @return           Number of elements to received
	 */
	public static def x10Reduce(distBS:BlocksPLH, tmpBS:BlocksPLH, rootbid:Long,
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix) {
		
		val dmap = distBS().getDistMap();
		val rootpid:Long = dmap.findPlace(rootbid);
		var leftpcnt:Long = Place.numPlaces();
		
		if (here.id() != rootpid) {
			at(Place(rootpid)) {
				x10Reduce(distBS, tmpBS, rootbid, opFunc);
			}
		} else {
			val rtblk  = distBS().findBlock(rootbid);
			finish {
				if (rootpid == 0L) 
					reduceToHere(distBS, tmpBS, rtblk, Place.numPlaces(), opFunc);
				else {
					val lfpcnt = rootpid;
					val rtpcnt = Place.numPlaces() - lfpcnt;
					binaryTreeReduce(distBS, tmpBS, rtblk, rtpcnt, 0, lfpcnt, opFunc);
				}
			}
		}
	}

	private static def binaryTreeReduce(
			distBS:BlocksPLH, tmpBS:BlocksPLH, 
			rootblk:MatrixBlock, rootPCnt:Long,
			remotepid:Long, remotePCnt:Long,
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix) {
		var rmtbuf:GlobalRail[ElemType];
		finish {
			//Left branch reduction
			rmtbuf =  at(Place(remotepid)) {
				//Remote capture:distBS, tmpBS, lfpcnt;
				val blk    = distBS().getFirst();
				async {
					reduceToHere(distBS, tmpBS, blk, remotePCnt, opFunc);
				}
				new GlobalRail[ElemType](blk.getData() as Rail[ElemType]{self!=null})
			};
			//Right branch reduction
			async {
				reduceToHere(distBS, tmpBS, rootblk, rootPCnt, opFunc);
			}
		}
		val tmpblk = tmpBS().getFirst();
		val dstden = rootblk.getMatrix() as DenseMatrix;
		val rcvden = tmpblk.getMatrix() as DenseMatrix;
		val datcnt = dstden.M*dstden.N;
		finish Rail.asyncCopy[ElemType](rmtbuf, 0, rcvden.d, 0, datcnt);
		
		opFunc(rcvden, dstden);
		//dstmat.cellAdd(rcvmat as DenseMatrix(dstmat.M, dstmat.N));
	}

	protected static def reduceToHere(distBS:BlocksPLH, tmpBS:BlocksPLH, 
			rootblk:MatrixBlock, pcnt:Long, 
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix): void {
		
		val leftRoot = here.id();
		if (pcnt > 1) {
			val leftPCnt  = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
			val rightPCnt  = pcnt - leftPCnt;
			val rightRoot  = leftRoot + leftPCnt;
		
			binaryTreeReduce(distBS, tmpBS, rootblk, leftPCnt, rightRoot, rightPCnt, opFunc);
		} else if (pcnt == 1L) {
			distBS().reduce(rootblk, opFunc);
		}
	}

	/**
	 * Reduce all distributed blocks and store the result of sum of all blocks at specified root block.
	 * All input blocks will be modified, and result will be stored at root block.
	 * 
	 * @param distBS     distributed blocks
	 * @param tmpBS      temporary space to store received blocks. Only one block in each place is required
	 * 
	 */
	public static def allReduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH): void {
		x10AllReduce(distBS, tmpBS, (a:DenseMatrix, b:DenseMatrix)=>b.cellAdd(a as DenseMatrix(b.M,b.N)));
	}
	
	/**
	 * Perform reduce on all blocks, and store the result at root block.
	 * Input blocks will be modified.
	 * 
	 * @param distBS        input distributed blocks
	 * @param tmpBS         temporary space to store received blocks. Only one block is required for each place
	 * @param rootbid       root block ID
	 * @param opFunc        Reduce operation function, which take two dense matrix as parameters, the 
	 */
	public static def allReduce(distBS:BlocksPLH, tmpBS:BlocksPLH, 
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix) {
		x10AllReduce(distBS, tmpBS, opFunc);
	}
	
	public static def x10AllReduce(distBS:BlocksPLH, tmpBS:BlocksPLH,
			opFunc:(DenseMatrix,DenseMatrix)=>DenseMatrix): void {

		x10Reduce(distBS, tmpBS, 0, opFunc);

		BlockBcast.bcast(distBS, 0);
	}

	/**
     * Create temporary space used in reduce for storing received data
	 */
	public static def makeTempDistBlockMatrix(m:Long, n:Long):BlocksPLH =
		PlaceLocalHandle.make[BlockSet](Place.places(), 
				()=>BlockSet.makeDense(m*Place.numPlaces(), n, Place.numPlaces(), 1, Place.numPlaces(),1));
}

