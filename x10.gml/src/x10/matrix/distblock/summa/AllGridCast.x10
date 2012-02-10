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

import x10.matrix.block.MatrixBlock;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;


public class AllGridCast {

	public static def startRowCast(jj:Int, klen:Int, itCol:Int, 
			distA:DistBlockMatrix, work1:PlaceLocalHandle[BlockSet]) {

		val dmap = distA.getMap();
		val grid = distA.getGrid();
		val sttplc = dmap.findPlace(grid.getBlockId(0, itCol));
		if (here.id()!= sttplc) {
			at (Dist.makeUnique()(sttplc)) {
				startRowCast(jj, klen, itCol, distA, work1);
			}
		} else {
			//Using column cast place list as the starting place of row cast
			val bs   = distA.handleBS();
			val plst = bs.colCastPlaceMap.getPlaceList(itCol);
			
			for ([p]:Point in plst) {
				val pid = plst(p);
				if (pid != here.id()) async {
					at (Dist.makeUnique()(pid)) {
						rowCastAll(jj, klen, itCol, distA, work1);
					}
				} else async {
					rowCastAll(jj, klen, itCol, distA, work1);
				}
			}
		}		
	}
	
	protected static def rowCastAll(
			jj:Int, klen:Int, itCol:Int, 
			dA:DistBlockMatrix, work1:PlaceLocalHandle[BlockSet]) {
		
		val grid = dA.getGrid();
		val bs = dA.handleBS();
		val itr = bs.iterator();
		while (itr.hasNext()) {
			val srcblk = itr.next();
			if (srcblk.myColId != itCol) continue;
			val rootbid = grid.getBlockId(srcblk.myRowId, srcblk.myColId);
			
			val dstblk  = work1().findFrontRowBlock(srcblk.myRowId);
			val rowplst:Array[Int](1) = bs.rowCastPlaceMap.getPlaceList(srcblk.myRowId);
			val datcnt = srcblk.copyCols(jj, klen, dstblk.getMatrix());
			
			BlockGridCast.rowCastToPlaces(work1, rootbid, datcnt, rowplst);
		}
	}
		
	//--------------------------------------
	
	
	public static def startColCast(ii:Int, klen:Int, itRow:Int, distB:DistBlockMatrix, work2:PlaceLocalHandle[BlockSet]){
		val dmap = distB.getMap();
		val grid = distB.getGrid();
		val sttplc = dmap.findPlace(grid.getBlockId(itRow, 0));
		if (here.id()!= sttplc) {
			at (Dist.makeUnique()(sttplc)) {
				startColCast(ii, klen, itRow, distB, work2);
			}
		} else {
			//Using column cast place list as the starting place of row cast
			val bs   = distB.handleBS();
			val plst = bs.rowCastPlaceMap.getPlaceList(itRow);
			
			for ([p]:Point in plst) {
				val pid = plst(p);
				if (pid != here.id()) async {
					at (Dist.makeUnique()(pid)) {
						colCastAllBlocks(ii, klen, itRow, distB, work2);
					}
				} else async {
					colCastAllBlocks(ii, klen, itRow, distB, work2);
				}
			}
		}		
	}
	
	protected static def colCastAllBlocks(
			ii:Int, klen:Int, itRow:Int, 
			dB:DistBlockMatrix, 
			work2:PlaceLocalHandle[BlockSet]) {
		
		val grid = dB.getGrid();
		val bs  = dB.handleBS();
		val itr = bs.iterator();
		
		while (itr.hasNext()) {
			val srcblk = itr.next();
			if (srcblk.myRowId != itRow) continue;
			val rootbid = grid.getBlockId(srcblk.myRowId, srcblk.myColId);
			val dstblk  = work2().findFrontColBlock(srcblk.myColId);
			val colplst = bs.colCastPlaceMap.getPlaceList(srcblk.myColId);
			//------------------------------------------------
			//Dense matrix must reshape to (klen x N), klen may be smaller than the
			//original matrix M when it is created. The data buffer must keep data compact 
			//before sending out. Sparse matrix is not affacted.
			var mat:Matrix = dstblk.getMatrix();
			if (mat instanceof DenseMatrix && klen != mat.M) {
				val den = new DenseMatrix(klen, mat.N, (mat as DenseMatrix).d);
				mat = den as Matrix;
			}
			//-------------------------------------------------
			val datcnt = srcblk.copyRows(ii, klen, mat);
			BlockGridCast.colCastToPlaces(work2, rootbid, datcnt, colplst);
		}
	}
	
	//========================================
	//========================================

	public static def startVerifyRowCast(jj:Int, klen:Int, itCol:Int, 
			distA:DistBlockMatrix, work1:PlaceLocalHandle[BlockSet]):Boolean {
		var retval:Boolean = true;
		val dmap = distA.getMap();
		val grid = distA.getGrid();
		val sttplc = dmap.findPlace(grid.getBlockId(0, itCol));
		if (here.id()!= sttplc) {
			retval &= at (Dist.makeUnique()(sttplc)) 
				startVerifyRowCast(jj, klen, itCol, distA, work1);
		} else {
			//Using column cast place list as the starting place of row cast
			val bs   = distA.handleBS();
			val plst = bs.colCastPlaceMap.getPlaceList(itCol);
			
			for ([p]:Point in plst) {
				val pid = plst(p);
				retval &= at (Dist.makeUnique()(pid)) 
						verifyRowCastAll(jj, klen, itCol, distA, work1);
				if (!retval) return false;
			}
		}
		return retval;
	}
		
	protected static def verifyRowCastAll(
			jj:Int, klen:Int, itCol:Int, 
			dA:DistBlockMatrix, 
			work1:PlaceLocalHandle[BlockSet]): Boolean {
		var retval:Boolean = true;
		val grid = dA.getGrid();
		val bs = dA.handleBS();
		val itr = bs.iterator();
		while (itr.hasNext()&&retval) {
			val srcblk = itr.next();
			if (srcblk.myColId != itCol) continue;
			val rootbid = grid.getBlockId(srcblk.myRowId, srcblk.myColId);
			val dstblk  = work1().findFrontRowBlock(srcblk.myRowId);
			val rowplst = bs.rowCastPlaceMap.getPlaceList(srcblk.myRowId);
			val srcmat = srcblk.getMatrix();
			val dstmat = dstblk.getMatrix();
			
			//src.copyCols(jj, klen, dstblk.getMatrix());
			val datcnt = srcblk.compColDataSize(jj, klen);
			//srcmat.printMatrix("source mat "+jj+" root col:"+itCol+" klen"+klen);
			//dstmat.printMatrix("target mat");
			
			for (var c:Int=0; c<klen&&retval; c++)
				for (var r:Int=0; r<srcmat.M&&retval; r++)
					retval &= (srcmat(r, c+jj)==dstmat(r, c));
			
			if (!retval) Debug.flushln("copy failed");
			retval &= BlockGridCast.verifyRowCast(work1, rootbid, datcnt, rowplst);
			//if (!retval) Debug.flushln("fail");
		}
		return retval;
	}
	
	//---------------
	public static def startVerifyColCast(ii:Int, klen:Int, itRow:Int, 
			distB:DistBlockMatrix, work2:PlaceLocalHandle[BlockSet]): Boolean{
		var retval:Boolean = true;
		val dmap = distB.getMap();
		val grid = distB.getGrid();
		val sttplc = dmap.findPlace(grid.getBlockId(itRow, 0));
		if (here.id()!= sttplc) {
			retval = at (Dist.makeUnique()(sttplc)) 
				startVerifyColCast(ii, klen, itRow, distB, work2);
			
		} else {
			//Using column cast place list as the starting place of row cast
			val bs   = distB.handleBS();
			val plst = bs.rowCastPlaceMap.getPlaceList(itRow);
			
			for ([p]:Point in plst) {
				val pid = plst(p);
				retval &= at (Dist.makeUnique()(pid)) verifyColCastAll(ii, klen, itRow, distB, work2);
				if (!retval) return false;
			}
		}
		return retval;
	}
	
	protected static def verifyColCastAll(
			ii:Int, klen:Int, itRow:Int, 
			dB:DistBlockMatrix, 
			work2:PlaceLocalHandle[BlockSet]) {
		var retval:Boolean=true;
		val grid = dB.getGrid();
		val bs  = dB.handleBS();
		val itr = bs.iterator();
		
		while (itr.hasNext()&&retval) {
			val srcblk = itr.next();
			if (srcblk.myRowId != itRow) continue;
			
			val rootbid = grid.getBlockId(srcblk.myRowId, srcblk.myColId);
			val dstblk  = work2().findFrontColBlock(srcblk.myColId);
			val colplst = bs.colCastPlaceMap.getPlaceList(srcblk.myColId);
			
			var mat:Matrix = dstblk.getMatrix();
			if (mat instanceof DenseMatrix && klen != mat.M) {
				val den = new DenseMatrix(klen, mat.N, (mat as DenseMatrix).d);
				mat = den as Matrix;
			}
			val src = srcblk.getMatrix();
			for (var c:Int=0; c<mat.N&&retval; c++)
				for (var r:Int=0; r<klen&&retval; r++)
					retval &= mat(r,c)==src(r+ii, c);
			
			//-------------------------------------------------
			val datcnt = srcblk.copyRows(ii, klen, mat);
			retval &= BlockGridCast.verifyColCast(work2, rootbid, datcnt, colplst);
		}
		return retval;
	}
	
	
}
