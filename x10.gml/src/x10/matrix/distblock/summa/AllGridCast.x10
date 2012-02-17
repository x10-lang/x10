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
			//Using column cast place list as the starting places of row cast
			val plst = work1().colCastPlaceMap.getPlaceList(itCol);
			finish for ([p]:Point in plst) {
				val pid = plst(p);
				if (pid != here.id()) async {
					at (Dist.makeUnique()(pid)) {
						rowCastAll(jj, klen, itCol, distA, work1);
					}
				} else async {
					rowCastAll(jj, klen, itCol, distA, work1);
				}
			}
			
			// if (!startVerifyRowCast(jj, klen, itCol, distA, work1))
			// 	Debug.exit("Verify rowCast failed");
			// Debug.flushln("Verification pass row cast, offset:"+jj+" len:"+klen+" colB:"+itCol);		
		}
	}
	
	protected static def rowCastAll(
			jj:Int, klen:Int, itCol:Int, 
			dA:DistBlockMatrix, work1:PlaceLocalHandle[BlockSet]) {
		
		val grid = dA.getGrid();
		val itr  = dA.handleBS().iterator();
		
		while (itr.hasNext()) {
			val blk = itr.next();
			if (blk.myColId != itCol) continue;
			
			val rootbid = grid.getBlockId(blk.myRowId, blk.myColId);
			val dstblk  = work1().findFrontRowBlock(blk.myRowId);
			//Debug.flushln("Copy block :"+blk.myRowId+","+blk.myColId+" to "+dstblk.myRowId+","+dstblk.myColId);
			//if (blk.myRowId==0)	Debug.flushln("Root block :\n"+blk.toString());
			
			val datcnt = blk.copyCols(jj, klen, dstblk.getMatrix());
			
			val rowplst:Array[Int](1) = work1().rowCastPlaceMap.getPlaceList(blk.myRowId);			
			BlockGridCast.rowCastToPlaces(work1, rootbid, klen, datcnt, rowplst);
			//Debug.flushln("Rowwise cast Rootbid"+rootbid+" datcnt:"+datcnt);
		}
	}
		
	//--------------------------------------
	
	
	public static def startColCast(ii:Int, klen:Int, itRow:Int, 
			distB:DistBlockMatrix, work2:PlaceLocalHandle[BlockSet]){
		
		val dmap = distB.getMap();
		val grid = distB.getGrid();
		val sttplc = dmap.findPlace(grid.getBlockId(itRow, 0));
		if (here.id()!= sttplc) {
			at (Dist.makeUnique()(sttplc)) {
				startColCast(ii, klen, itRow, distB, work2);
			}
		} else {
			//Using row cast place list as the starting places of row cast
			val plst = work2().rowCastPlaceMap.getPlaceList(itRow);
			finish for ([p]:Point in plst) {
				val pid = plst(p);
				if (pid != here.id()) async {
					at (Dist.makeUnique()(pid)) {
						colCastAll(ii, klen, itRow, distB, work2);
					}
				} else async {
					colCastAll(ii, klen, itRow, distB, work2);
				}
			}

			// if (!startVerifyColCast(ii, klen, itRow, distB, work2))
			// 	Debug.exit("Verify colCast failed");
			// Debug.flushln("Verification pass col cast, offset:"+ii+" len:"+klen+" colB:"+itRow);
		}
	}
	
	protected static def colCastAll(
			ii:Int, klen:Int, itRow:Int, 
			dB:DistBlockMatrix, 
			work2:PlaceLocalHandle[BlockSet]) {
		
		val grid = dB.getGrid();
		val itr  = dB.handleBS().iterator();
		
		while (itr.hasNext()) {
			val blk = itr.next();
			if (blk.myRowId != itRow) continue;
			
			val rootbid = grid.getBlockId(blk.myRowId, blk.myColId);
			//------------------------------------------------
			val dstblk  = work2().findFrontColBlock(blk.myColId);
			//Debug.flushln("Copy block :"+blk.myRowId+","+blk.myColId+" to "+dstblk.myRowId+","+dstblk.myColId);
			//Dense matrix must reshape to (klen x N), klen may be smaller than the
			//original matrix M when it is created. The data buffer must keep data compact 
			//before sending out. Sparse matrix is not affacted.
			var mat:Matrix = dstblk.getMatrix();
			if (mat instanceof DenseMatrix && klen != mat.M) {
				val den = new DenseMatrix(klen, mat.N, (mat as DenseMatrix).d);
				mat = den as Matrix;
			}
			val datcnt = blk.copyRows(ii, klen, mat);
			//-------------------------------------------------
			val colplst:Array[Int](1) = work2().colCastPlaceMap.getPlaceList(blk.myColId);						
			BlockGridCast.colCastToPlaces(work2, rootbid, mat.N, datcnt, colplst);	
			//Debug.flushln("Colwise cast Rootbid"+rootbid+" datcnt:"+datcnt);

		}	
	}
	
	//========================================
	//========================================
	
	public static def startVerifyRowCast(jj:Int, klen:Int, itCol:Int, 
			distA:DistBlockMatrix, work1:PlaceLocalHandle[BlockSet]):Boolean {
		var retval:Boolean = true;
		val dmap = distA.getMap();
		val grid = distA.getGrid();
		for  (var rowId:Int=0; rowId < grid.numRowBlocks&&retval; rowId++) {
			val sttbid = grid.getBlockId(rowId, 0);
			val sttplc = dmap.findPlace(sttbid);
			val rId    = rowId;
			retval &= at (Dist.makeUnique()(sttplc)) verifyRowCastAll(jj, klen, itCol, rId, distA, work1);
			if (!retval) Debug.flushln("Verify failed when checking "+rowId+
					" idx off:"+jj+" klen:"+klen+" itCol:"+itCol+" start plc:"+sttplc);
		}
		return retval;
	}
		
	protected static def verifyRowCastAll(
			jj:Int, klen:Int, itCol:Int, 
			rowId:Int, dA:DistBlockMatrix, 
			work1:PlaceLocalHandle[BlockSet]): Boolean {
		var retval:Boolean = true;
		val grid = dA.getGrid();
		val dmap = dA.getMap();

		// Check copy is correct or not
		val rootbid = grid.getBlockId(rowId, itCol);
		val rootpid = dmap.findPlace(rootbid);
		
		retval &= at (Dist.makeUnique()(rootpid)) {
			val rootblk = dA.handleBS().findBlock(rootbid);
			val dstblk  = work1().findFrontRowBlock(rowId);
		
			val srcmat = rootblk.getMatrix();
			val dstmat = dstblk.getMatrix();
			var eql:Boolean = true;
			//srcmat.printMatrix("source mat idx off:"+jj+" root col:"+itCol+" klen"+klen);
			//dstmat.printMatrix("target mat");
			
			for (var c:Int=0; c<klen&&eql; c++)
				for (var r:Int=0; r<srcmat.M&&eql; r++)
					eql &= (srcmat(r, c+jj)==dstmat(r, c));
			if (!eql) Debug.flushln("Copy columns verification failed");
		
			// check all row blocks
			eql &= BlockGridCast.verifyRowCastEast(klen, work1, dstblk);
			eql &= BlockGridCast.verifyRowCastWest(klen, work1, dstblk);
			eql
		};
		return retval;
	}
	
	//---------------
	public static def startVerifyColCast(ii:Int, klen:Int, itRow:Int, 
			distB:DistBlockMatrix, work2:PlaceLocalHandle[BlockSet]): Boolean{
		var retval:Boolean = true;
		val dmap = distB.getMap();
		val grid = distB.getGrid();
		
		for  (var colId:Int=0; colId < grid.numColBlocks&&retval; colId++) {
			val sttbid = grid.getBlockId(0, colId);
			val sttplc = dmap.findPlace(sttbid);
			val cId    = colId;
			retval &= at (Dist.makeUnique()(sttplc)) 
				verifyColCastAll(ii, klen, itRow, cId, distB, work2);
			if (!retval) Debug.flushln("Verify failed when checking column "+cId+
					" idx off:"+ii+" klen:"+klen+" itRow:"+itRow+" start plc:"+sttplc);
		}
		return retval;
	}
	
	protected static def verifyColCastAll(
			ii:Int, klen:Int, itRow:Int, colId:Int,
			dB:DistBlockMatrix, 
			work2:PlaceLocalHandle[BlockSet]) {

		var retval:Boolean=true;
		val grid = dB.getGrid();
		val dmap = dB.getMap();
				
		// Check copy is correct or not
		val rootbid = grid.getBlockId(itRow, colId);
		val rootpid = dmap.findPlace(rootbid);
		
		retval &= at (Dist.makeUnique()(rootpid)) {
			val rootblk = dB.handleBS().findBlock(rootbid);
			val srcmat = rootblk.getMatrix();
			//Debug.flushln("Searching front block:"+colId);
			val dstblk  = work2().findFrontColBlock(colId);
			//Debug.flushln("Found "+dstblk.myRowId+","+dstblk.myColId);
			
			var mat:Matrix = dstblk.getMatrix();
			if (mat instanceof DenseMatrix && klen != mat.M) {
				val den = new DenseMatrix(klen, mat.N, (mat as DenseMatrix).d);
				mat = den as Matrix;
			}

			var eql:Boolean = true;
			//srcmat.printMatrix("source mat idx off:"+ii+" root row:"+itRow+" klen"+klen);
			//mat.printMatrix("target mat");
			
			for (var c:Int=0; c<srcmat.N&&eql; c++)
				for (var r:Int=0; r<klen&&eql; r++)
					eql &= (srcmat(r+ii, c)==mat(r, c));
			
			if (!eql) Debug.flushln("Copy rows verification failed");
			
			// check all row blocks
			eql &= BlockGridCast.verifyColCastNorth(klen, work2, dstblk);
			eql &= BlockGridCast.verifyColCastSouth(klen, work2, dstblk);
			eql
		};
		return retval;
	}
	
	
}
