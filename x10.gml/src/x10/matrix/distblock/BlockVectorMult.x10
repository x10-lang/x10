/*
 * 
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.matrix.distblock;

import x10.util.ArrayList;
import x10.compiler.Inline;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.Vector;
import x10.matrix.VectorMult;

import x10.matrix.Debug;
import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;
import x10.matrix.block.MatrixBlock;

/**
 * This class defines list of matrix blocks which live in the same place
 */
public class BlockVectorMult  { 

	public static def comp(aSet:BlockSet, bV:Vector, cV:Vector, plus:Boolean):Vector(cV) =
		comp(aSet, bV, 0, cV, 0, plus);
	
	public static def comp(aSet:BlockSet, 
			bV:Vector, offsetB:Int, 
			cV:Vector, offsetC:Int, plus:Boolean):Vector(cV) {
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
			val rowOff = grid.startRow(ablk.myRowId)-offsetC;
			val colOff = grid.startCol(ablk.myColId)-offsetB;
			//val rowCnt = grid.rowBs(ablk.myRowId);
			//val colCnt = grid.colBs(ablk.myColId);
			val mA = ablk.getMatrix();
			//Debug.flushln("Col partition:"+grid.colBs.toString());
			//Debug.flushln("Compute blk:"+ablk.myRowId+","+ablk.myColId+" rowOff:"+rowOff+" colOff:"+colOff+" Starting colOff:"+grid.startCol(ablk.myColId)+" offsetB:"+offsetB);
			VectorMult.comp(mA, bV, colOff, cV, rowOff, true);
		}
		return cV;
	}
	
	
	public static def comp(bV:Vector, aSet:BlockSet, cV:Vector, plus:Boolean):Vector(cV) =
		comp(bV, 0, aSet, cV, 0, plus);
	
	public static def comp(bV:Vector, offsetB:Int, 
			aSet:BlockSet, cV:Vector, offsetC:Int, plus:Boolean):Vector(cV) {
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
			val rowOff = grid.startRow(ablk.myRowId) - offsetB;
			val colOff = grid.startCol(ablk.myColId) - offsetC;
			//val rowCnt = grid.rowBs(ablk.myRowId);
			//val colCnt = grid.colBs(ablk.myColId);
			val mA = ablk.getMatrix() as Matrix(bV.M);
			//Debug.flushln("Vector:"+rowOff+" * block:"+ablk.myRowId+","+ablk.myColId+"=>Vector:"+colOff);
			VectorMult.comp(bV, rowOff, mA, cV, colOff, true);
		}
		return cV;
	}
}