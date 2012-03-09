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
 * Grid row/column wise reduce
 *.
 */
public class BlockGridReduce {

	//==================================================
	// RingCast: receive form previous one and send to one next in a ring 
	//==================================================
	public static def rowWise(rid:Int, cid:Int):Int = rid;
	public static def colWise(rid:Int, cid:Int):Int = cid;
	
	
	/**
	 * Row-wise reduction-sum of front blocks
	 */
	public static def rowReduceSum(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, rootblk:MatrixBlock, datCnt:Int,
			plst:Array[Int](1)):void {
		reduceToRoot(distBS, tmpBS, rootbid, rootblk, datCnt, 
				(rid:Int,cid:Int)=>rid, 
				(src:DenseMatrix,dst:DenseMatrix, dcnt:Int)=>sum(src, dst, dcnt), plst);
	}
	
	/**
	 * Column-wise reduction-sum of font blocks
	 */
	public static def colReduceSum(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, rootblk:MatrixBlock, datCnt:Int,
			plst:Array[Int](1)):void {
		reduceToRoot(distBS, tmpBS, rootbid, rootblk, datCnt, 
				(rid:Int,cid:Int)=>cid, 
				(src:DenseMatrix,dst:DenseMatrix, dcnt:Int)=>sum(src, dst, dcnt), plst);
	}
	
	//------------------------------------------------------------------------
	public static def sum(src:DenseMatrix, dst:DenseMatrix, datCnt:Int):DenseMatrix(dst) {
		Debug.assure(src.M==dst.M, 
			"Leading dimensions of destination and source are not same"); 
		for (var idx:Int=0; idx < datCnt; idx++)
			dst.d(idx) += src.d(idx);
		return dst;		
	}

	//=========================================================
	//=========================================================
	/**
	 * 
	 */
	protected static def reduceToRoot(
			distBS:PlaceLocalHandle[BlockSet], 
			tmpBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, rootblk:MatrixBlock, datCnt:Int, 
			select:(Int, Int)=>Int,
			opFunc:(DenseMatrix, DenseMatrix, Int)=>DenseMatrix,
			plst:Array[Int](1) ) {
		
		if (plst.size < 1 ) return;
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