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

import x10.matrix.distblock.BlockSet;

/**
 * The class provides reduce-sum communication for distributed matrix,
 */
public class BlockSetReduce extends BlockSetRemoteCopy {
	/**
	 * Reduce all distributed blocks and store the result of sum of all blocks at specified root block.
	 * All input blocks will be modified, and result will be stored at root block.
	 * 
	 * @param distBS     distributed blocks
	 * @param tmpBS      temporary space to store received blocks. Only one block in each place is required
	 * @param rootbid    root block ID.
	 * 
	 */
	public static def reduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH, rootpid:Long): void {
		x10Reduce(distBS, tmpBS, rootpid, (a:DenseMatrix, b:DenseMatrix)=>b.cellAdd(a as DenseMatrix(b.M,b.N)));
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
			rootpid:Long,
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix) {
		x10Reduce(distBS, tmpBS, rootpid, opFunc);
	}
	
	/**
	 * Reduce all dense matrices stored in DistArray from all places
	 * to here
	 *
	 * @param ddmat      Input/Output. Duplicated dense matrix of source and target matrix
	 * @param ddtmp      Temp matrix space to store the receiving data.
	 * @return           Number of elements to received
	 */
	public static def x10Reduce(distBS:BlocksPLH, tmpBS:BlocksPLH, rootpid:Long,
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix) {
		
		val dmap = distBS().getDistMap();
		var leftpcnt:Long = Place.numPlaces();
		
		if (here.id() != rootpid) {
			at(Place(rootpid)) {
				x10Reduce(distBS, tmpBS, rootpid, opFunc);
			}
		} else {
			finish {
				if (rootpid == 0L) 
					reduceToHere(distBS, tmpBS, 0, Place.numPlaces(), opFunc);
				else {
					val lfpcnt = rootpid;
					val rtpcnt = Place.numPlaces() - lfpcnt;
					binaryTreeReduce(distBS, tmpBS, rootpid, rtpcnt, 0, lfpcnt, opFunc);
				}
			}
		}
	}

	private static def binaryTreeReduce(
			distBS:BlocksPLH, tmpBS:BlocksPLH, 
			nearbypid:Long, rootPCnt:Long,
			remotepid:Long, remotePCnt:Long,
			opFunc:(DenseMatrix, DenseMatrix)=>DenseMatrix) {
		var rmtbuflst:Rail[GlobalRail[ElemType]];
		finish {
			//Left branch reduction
			rmtbuflst =  at(Place(remotepid)) {
				//Remote capture:distBS, tmpBS, lfpcnt;
				async {
					reduceToHere(distBS, tmpBS, here.id(), remotePCnt, opFunc);
				}
				val bl = distBS().blocklist;
				new Rail[GlobalRail[ElemType]](bl.size(), 
						(i:Long)=>new GlobalRail[ElemType](bl(i).getData() as Rail[ElemType]{self!=null}))
			};
			//Right branch reduction
			async {
				reduceToHere(distBS, tmpBS, nearbypid, rootPCnt, opFunc);
			}
		}
		//val dstIt = distBS().iterator();
		//val tmpIt = tmpBS().iterator();
		for (var i:Long=0; i<rmtbuflst.size; i++)  {
			val dstblk = distBS().blocklist.get(i);
			val dstden = dstblk.getMatrix() as DenseMatrix;
			val rcvden = tmpBS().blocklist.get(i).getMatrix() as DenseMatrix;
			val datcnt = dstden.M*dstden.N;
			finish Rail.asyncCopy[ElemType](rmtbuflst(i), 0L, rcvden.d, 0L, datcnt);
			opFunc(rcvden, dstden);
			
		}
	}


	protected static def reduceToHere(distBS:BlocksPLH, tmpBS:BlocksPLH, 
			rootpid:Long, pcnt:Long, 
			opFunc:(DenseMatrix,DenseMatrix)=>DenseMatrix): void {
		
		val leftRoot = here.id();
		if (pcnt > 1) {
			val leftPCnt  = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
			val rightPCnt  = pcnt - leftPCnt;
			val rightRoot  = leftRoot + leftPCnt;
		
			binaryTreeReduce(distBS, tmpBS, rootpid, leftPCnt, rightRoot, rightPCnt, opFunc);
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
			opFunc:(DenseMatrix,DenseMatrix)=>DenseMatrix) {
				
		val rtpid = here.id();
		x10Reduce(distBS, tmpBS, rtpid, opFunc);
		BlockSetBcast.bcast(distBS, rtpid);
	}

	/**
	 * Create temporary space used in reduce for storing received data
	 */
	public static def makeTempDistBlockMatrix(m:Long, n:Long):BlocksPLH =
		PlaceLocalHandle.make[BlockSet](Place.places(), 
				()=>BlockSet.makeDense(m*Place.numPlaces(), n, Place.numPlaces(), 1, Place.numPlaces(),1));
}

