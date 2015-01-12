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
import x10.matrix.DenseMatrix;
import x10.matrix.block.MatrixBlock;
import x10.matrix.distblock.BlockSet;

/**
 * Grid row/column wise reduce
 */
public class BlockGridReduce {
	// RingCast: receive form previous one and send to one next in a ring 
	public static def rowWise(rid:Long, cid:Long):Long = rid;
	public static def colWise(rid:Long, cid:Long):Long = cid;
	
	/**
	 * Row-wise reduction-sum of front blocks
	 */
	public static def rowReduceSum(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Long, rootblk:MatrixBlock, datCnt:Long,
			plst:Rail[Long]):void {
		reduceToRoot(distBS, tmpBS, rootbid, rootblk, datCnt, 
				(rid:Long,cid:Long)=>rid, 
				(src:DenseMatrix,dst:DenseMatrix, dcnt:Long)=>sum(src, dst, dcnt), plst);
	}
	
	/**
     * Column-wise reduction-sum of front blocks
	 */
	public static def colReduceSum(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Long, rootblk:MatrixBlock, datCnt:Long,
			plst:Rail[Long]):void {
		reduceToRoot(distBS, tmpBS, rootbid, rootblk, datCnt, 
				(rid:Long,cid:Long)=>cid, 
				(src:DenseMatrix,dst:DenseMatrix, dcnt:Long)=>sum(src, dst, dcnt), plst);
	}
	
	public static def sum(src:DenseMatrix, dst:DenseMatrix, datCnt:Long):DenseMatrix(dst) {
		Debug.assure(src.M==dst.M, 
			"Leading dimensions of destination and source are not same"); 
		for (var idx:Long=0; idx < datCnt; idx++)
			dst.d(idx) += src.d(idx);
		return dst;		
	}

	protected static def reduceToRoot(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Long, rootblk:MatrixBlock, datCnt:Long, 
			select:(Long,Long)=>Long,
			opFunc:(DenseMatrix,DenseMatrix,Long)=>DenseMatrix,
			plst:Rail[Long] ) {
		
        if (plst.size < 1) return;
		//val dstblk = distBS().findFrontBlock(rootbid, select);
		//find rootpid in the place list, if not, assume root is first one
		@Ifdef("ENABLE_RING_COMM") {
			RingReduce.reduceToHere(distBS, tmpBS, rootbid, rootblk, datCnt, select, opFunc, plst);
		}
		
		@Ifndef("ENABLE_RING_COMM") {
			BinaryTreeReduce.reduceToHere(distBS, tmpBS, rootbid, rootblk, datCnt, select, opFunc, plst);
		}
	}
}
