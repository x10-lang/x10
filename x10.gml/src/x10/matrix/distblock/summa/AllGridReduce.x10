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
import x10.compiler.Inline;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.DenseBlock;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;


public class AllGridReduce {

	public static def startRowReduceSum(jj:Int, klen:Int, itCol:Int, 
			distC:DistBlockMatrix,
			work1:PlaceLocalHandle[BlockSet],  tmp:PlaceLocalHandle[BlockSet]) {

		val dmap = work1().getDistMap();
		val grid = work1().getGrid();
		val sttplc = dmap.findPlace(grid.getBlockId(0, itCol));
		if (here.id()!= sttplc) {
			at (Dist.makeUnique()(sttplc)) {
				startRowReduceSum(jj, klen, itCol, distC, work1, tmp);
			}
		} else {
			//Using column cast place list as the starting places of row cast
			val plst = work1().colCastPlaceMap.getPlaceList(itCol);
			finish {
				for ([p]:Point in plst) {
					val pid = plst(p);
					at (Dist.makeUnique()(pid)) async {
						rowReduceSumAll(jj, klen, itCol, distC, work1, tmp);
					} 
				}
				rowReduceSumAll(jj, klen, itCol, distC, work1, tmp);
			}
		}
	}
	
	protected static def rowReduceSumAll(
			jj:Int, klen:Int, itCol:Int, 
			distC:DistBlockMatrix,
			work1:PlaceLocalHandle[BlockSet], tmp:PlaceLocalHandle[BlockSet]) {
		
		val grid = work1().getGrid();
		val itr  = distC.handleBS().iterator();
		
		while (itr.hasNext()) {
			val dstblk = itr.next() as DenseBlock;
			if (dstblk.myColId != itCol) continue;
			
			val rootbid = grid.getBlockId(dstblk.myRowId, dstblk.myColId);
			val rcvblk  = work1().findFrontColBlock(dstblk.myRowId);
			//if (blk.myRowId==0)	Debug.flushln("Root block :\n"+blk.toString());
			
			val datcnt = rcvblk.compColDataSize(jj, klen);	//copyCols(jj, klen, datblk.getMatrix());
			val rowplst:Array[Int](1) = work1().rowCastPlaceMap.getPlaceList(rcvblk.myRowId);
			//Debug.flushln("Start row reduce from places:"+rowplst.toString()); 
			BlockGridReduce.rowReduceSum(work1, tmp, rootbid, rcvblk, datcnt, rowplst);
			//Debug.flushln("Done row reduce from places:"+rowplst.toString()); 

			//Debug.flushln(srcblk.toString());
			val srcden = rcvblk.getMatrix() as DenseMatrix;
			dstblk.addCols(jj, klen, srcden);
		}
	}
		
	//==============================================================================
	
	/**
	 *  Not used in SUMMA. Reserve for future mult-trans SUMMA
	 */
	public static def startColReduceSum(ii:Int, klen:Int, itRow:Int, 
			distC:DistBlockMatrix,
			work2:PlaceLocalHandle[BlockSet],  tmp:PlaceLocalHandle[BlockSet]){
		
		val dmap = work2().getDistMap();
		val grid = work2().getGrid();
		val sttplc = dmap.findPlace(grid.getBlockId(itRow, 0));
		if (here.id()!= sttplc) {
			at (Dist.makeUnique()(sttplc)) {
				startColReduceSum(ii, klen, itRow, distC, work2, tmp);
			}
		} else {
			//Using row cast place list as the starting places of row cast
			val plst = work2().rowCastPlaceMap.getPlaceList(itRow);
			finish {
				for ([p]:Point in plst) {
					val pid = plst(p);
					at (Dist.makeUnique()(pid)) async {
						colReduceSumAll(ii, klen, itRow, distC, work2, tmp);
					}
				}
				colReduceSumAll(ii, klen, itRow, distC, work2, tmp);
			}
		}
	}
	
	protected static def colReduceSumAll(
			ii:Int, klen:Int, itRow:Int, 
			distC:DistBlockMatrix,
			work2:PlaceLocalHandle[BlockSet],  
			tmp:PlaceLocalHandle[BlockSet]) {
		
		val grid = work2().getGrid();
		val itr  = distC.handleBS().iterator();
		
		while (itr.hasNext()) {
			val dstblk = itr.next() as DenseBlock;
			if (dstblk.myRowId != itRow) continue;
			
			val rootbid = grid.getBlockId(dstblk.myRowId, dstblk.myColId);
			//------------------------------------------------
			val rcvblk  = work2().findFrontRowBlock(dstblk.myColId);
			var mat:Matrix = rcvblk.getMatrix();
			if (klen != mat.M) {
				val den = new DenseMatrix(klen, mat.N, (mat as DenseMatrix).d);
				mat = den as Matrix;
			}
			val datcnt = dstblk.copyRows(ii, klen, mat);
			//-------------------------------------------------
			val colplst:Array[Int](1) = work2().colCastPlaceMap.getPlaceList(dstblk.myColId);						
			BlockGridReduce.colReduceSum(work2, tmp, rootbid, rcvblk, datcnt, colplst);
			//
			//dstblk.addRows(ii, klen, mat as DenseMatrix);

		}	
	}
	
	//========================================
	//========================================

	
}
