/*
 * 
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.matrix.distblock;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.VectorMult;
import x10.matrix.block.Grid;

/**
 * This class performs matrix-vector multiplication for block-partitioned matrices.
 */
public class BlockVectorMult  { 

	public static def comp(aSet:BlockSet, bV:Vector, cV:Vector, plus:Boolean):Vector(cV) =
		comp(aSet, bV, 0, cV, 0, plus);
	
	public static def comp(aSet:BlockSet, 
			bV:Vector, offsetB:Long, 
			cV:Vector, offsetC:Long, plus:Boolean):Vector(cV) {
		val itr = aSet.iterator();
		val grid = aSet.getGrid();
		if (!plus) cV.reset();
		//aSet.buildBlockMap();
		//val blkmap = aSet.blockMap;
		//for (var rb:Int=blkmap.region.min(0); rb<=blkmap.region.max(0); rb++) {
		//	for (var cb:Int=blkmap.region.min(1); cb<=blkmap.region.max(1); cb++) {
		//		val ablk = blockMap(rb, cb);
		while (itr.hasNext()) {
			val ablk = itr.next();
			//No need to comput row and column offset for every block, when dist is DistGrid.
			val rowOff = ablk.rowOffset - offsetC;//grid.startRow(ablk.myRowId)-offsetC;
			val colOff = ablk.colOffset - offsetB;//grid.startCol(ablk.myColId)-offsetB;
			//val rowCnt = grid.rowBs(ablk.myRowId);
			//val colCnt = grid.colBs(ablk.myColId);
			val mA = ablk.getMatrix();
			VectorMult.comp(mA, bV, colOff, cV, rowOff, true);
		}
		return cV;
	}
	
	
	public static def comp(bV:Vector, aSet:BlockSet, cV:Vector, plus:Boolean):Vector(cV) =
		comp(bV, 0, aSet, cV, 0, plus);
	
	public static def comp(bV:Vector, offsetB:Long, 
			aSet:BlockSet, cV:Vector, offsetC:Long, plus:Boolean):Vector(cV) {
		val itr = aSet.iterator();
		val grid = aSet.getGrid();
		if (!plus) cV.reset();
		//aSet.buildBlockMap();
		//val blkmap = aSet.blockMap;
		//for (var rb:Int=blkmap.region.min(0); rb<=blkmap.region.max(0); rb++) {
		//	for (var cb:Int=blkmap.region.min(1); cb<=blkmap.region.max(1); cb++) {
		//		val ablk = blockMap(rb, cb);
		while (itr.hasNext()) {
			val ablk = itr.next();
			val rowOff = ablk.rowOffset - offsetB;//grid.startRow(ablk.myRowId) - offsetB;
			val colOff = ablk.colOffset - offsetC;//grid.startCol(ablk.myColId) - offsetC;
			//val rowCnt = grid.rowBs(ablk.myRowId);
			//val colCnt = grid.colBs(ablk.myColId);
			val mA = ablk.getMatrix();
			VectorMult.comp(bV, rowOff, mA, cV, colOff, true);
		}
		return cV;
	}
}
