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

package x10.matrix.distblock.summa;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistBlockMatrix;

public class AllGridCast {
	public static def startRowCast(jj:Long, klen:Long, itCol:Long, 
			distA:DistBlockMatrix, work1:PlaceLocalHandle[BlockSet]) {

		val dmap = distA.getMap();
		val grid = distA.getGrid();
		val sttplc = dmap.findPlace(grid.getBlockId(0, itCol));
		if (here.id()!= sttplc) {
            at(Place(sttplc)) {
				startRowCast(jj, klen, itCol, distA, work1);
			}
		} else {
			//Using column cast place list as the starting places of row cast
			val plst = work1().colCastPlaceMap.getPlaceList(itCol);
			//Debug.flushln("Init all row cast from places:"+plst.toString());
			finish {
                for (pid in plst) {
                    at(Place(pid)) async {
						//Debug.flushln("Starting row cast to places at row block "+itCol);
						rowCastAll(jj, klen, itCol, distA, work1);
					}
				}
				rowCastAll(jj, klen, itCol, distA, work1);
			}
			
			// if (!startVerifyRowCast(jj, klen, itCol, distA, work1))
			// 	Debug.exit("Verify rowCast failed");
			// Debug.flushln("Verification pass row cast, offset:"+jj+" len:"+klen+" colB:"+itCol);		
		}
	}
	
	protected static def rowCastAll(
			jj:Long, klen:Long, itCol:Long, 
			dA:DistBlockMatrix, work1:PlaceLocalHandle[BlockSet]) {
		
		val grid = dA.getGrid();
		val itr  = dA.handleBS().iterator();
		
		while (itr.hasNext()) {
			val blk = itr.next();
			if (blk.myColId != itCol) continue;
			
			val rootbid = grid.getBlockId(blk.myRowId, blk.myColId);
			val dstblk  = work1().findFrontColBlock(blk.myRowId);
			//Debug.flushln("Copy block :"+blk.myRowId+","+blk.myColId+" to "+dstblk.myRowId+","+dstblk.myColId);
			//if (blk.myRowId==0)	Debug.flushln("Root block :\n"+blk.toString());
			
			val datcnt = blk.copyCols(jj, klen, dstblk.getMatrix());
			
			val rowplst = work1().rowCastPlaceMap.getPlaceList(blk.myRowId);	
			//Debug.flushln("Starting row cast to places:"+rowplst.toString());
			BlockGridCast.rowCastToPlaces(work1, rootbid, klen, datcnt, rowplst);
			//Debug.flushln("Done row cast to places:"+rowplst.toString());
			//Debug.flushln("Rowwise cast Rootbid"+rootbid+" datcnt:"+datcnt);
		}
	}
		
	public static def startColCast(ii:Long, klen:Long, itRow:Long, 
			distB:DistBlockMatrix, work2:PlaceLocalHandle[BlockSet]){
		
		val dmap = distB.getMap();
		val grid = distB.getGrid();
		val sttplc = dmap.findPlace(grid.getBlockId(itRow, 0));
		if (here.id()!= sttplc) {
            at(Place(sttplc)) {
				startColCast(ii, klen, itRow, distB, work2);
			}
		} else {
			//Using row cast place startlist as the starting places of row cast
			val plst = work2().rowCastPlaceMap.getPlaceList(itRow);
			finish {
				for (pid in plst) {
					at(Place(pid)) async {
						colCastAll(ii, klen, itRow, distB, work2);
					}
				} 
				colCastAll(ii, klen, itRow, distB, work2);
			}

			// if (!startVerifyColCast(ii, klen, itRow, distB, work2))
			// 	Debug.exit("Verify colCast failed");
			// Debug.flushln("Verification pass col cast, offset:"+ii+" len:"+klen+" colB:"+itRow);
		}
	}
	
	protected static def colCastAll(
			ii:Long, klen:Long, itRow:Long, 
			dB:DistBlockMatrix, 
			work2:PlaceLocalHandle[BlockSet]) {
		
		val grid = dB.getGrid();
		val itr  = dB.handleBS().iterator();
		
		while (itr.hasNext()) {
			val blk = itr.next();
			if (blk.myRowId != itRow) continue;
			
			val rootbid = grid.getBlockId(blk.myRowId, blk.myColId);

			val dstblk  = work2().findFrontRowBlock(blk.myColId);
			//Debug.flushln("Copy block :"+blk.myRowId+","+blk.myColId+" to "+dstblk.myRowId+","+dstblk.myColId);
			//Dense matrix must reshape to (klen x N), klen may be smaller than the
			//original matrix M when it is created. The data buffer must keep data compact 
			//before sending out. Sparse matrix is not affacted.
			var mat:Matrix = dstblk.getMatrix();
			if (mat instanceof DenseMatrix && klen != mat.M) {
				Debug.assure(klen <= mat.M, "Target leading dimension "+mat.M+" is too small "+klen);
				val den = new DenseMatrix(klen, mat.N, (mat as DenseMatrix).d);
				mat = den as Matrix;
			}
			//Debug.flushln(blk.toString()+"\n"+mat.toString());
			val datcnt = blk.copyRows(ii, klen, mat);

			val colplst = work2().colCastPlaceMap.getPlaceList(blk.myColId);
			//Debug.flushln("Starting col cast to places:"+colplst.toString());
			BlockGridCast.colCastToPlaces(work2, rootbid, mat.N, datcnt, colplst);	
			//Debug.flushln("Done col cast to places:"+colplst.toString());
			//Debug.flushln("Colwise cast Rootbid"+rootbid+" datcnt:"+datcnt);

		}	
	}
	
	public static def startVerifyRowCast(jj:Long, klen:Long, itCol:Long, 
			distA:DistBlockMatrix, work1:PlaceLocalHandle[BlockSet]):Boolean {
		var retval:Boolean = true;
		val dmap = distA.getMap();
		val grid = distA.getGrid();
		for  (var rowId:Long=0; rowId < grid.numRowBlocks&&retval; rowId++) {
			val sttbid = grid.getBlockId(rowId, 0);
			val sttplc = dmap.findPlace(sttbid);
			val rId    = rowId;
            val result = at(Place(sttplc)) 
				verifyRowCastAll(jj, klen, itCol, rId, distA, work1);
            retval &= result;
			if (!retval) Debug.flushln("Verify failed when checking "+rowId+
					" idx off:"+jj+" klen:"+klen+" itCol:"+itCol+" start plc:"+sttplc);
		}
		return retval;
	}
		
	protected static def verifyRowCastAll(
			jj:Long, klen:Long, itCol:Long, 
			rowId:Long, dA:DistBlockMatrix, 
			work1:PlaceLocalHandle[BlockSet]): Boolean {
		var retval:Boolean = true;
		val grid = dA.getGrid();
		val dmap = dA.getMap();

		// Check copy is correct or not
		val rootbid = grid.getBlockId(rowId, itCol);
		val rootpid = dmap.findPlace(rootbid);
		
        retval &= at(Place(rootpid)) {
			val rootblk = dA.handleBS().findBlock(rootbid);
			val dstblk  = work1().findFrontColBlock(rowId);
		
			val srcmat = rootblk.getMatrix();
			val dstmat = dstblk.getMatrix();
			var eql:Boolean = true;
			
			for (var c:Long=0; c<klen&&eql; c++)
				for (var r:Long=0; r<srcmat.M&&eql; r++)
					eql &= (srcmat(r, c+jj)==dstmat(r, c));
			if (!eql) Debug.flushln("Copy columns verification failed");
		
			// check all row blocks
			eql &= BlockGridCast.verifyRowCastEast(klen, work1, dstblk);
			eql &= BlockGridCast.verifyRowCastWest(klen, work1, dstblk);
			eql
		};
		return retval;
	}
	
	public static def startVerifyColCast(ii:Long, klen:Long, itRow:Long, 
			distB:DistBlockMatrix, work2:PlaceLocalHandle[BlockSet]): Boolean{
		var retval:Boolean = true;
		val dmap = distB.getMap();
		val grid = distB.getGrid();
		
		for (var colId:Long=0; colId < grid.numColBlocks && retval; colId++) {
			val sttbid = grid.getBlockId(0, colId);
			val sttplc = dmap.findPlace(sttbid);
			val cId    = colId;
            retval &= at(Place(sttplc)) 
				verifyColCastAll(ii, klen, itRow, cId, distB, work2);
			if (!retval) Debug.flushln("Verify failed when checking column "+cId+
					" idx off:"+ii+" klen:"+klen+" itRow:"+itRow+" start plc:"+sttplc);
		}
		return retval;
	}
	
	protected static def verifyColCastAll(
			ii:Long, klen:Long, itRow:Long, colId:Long,
			dB:DistBlockMatrix, 
			work2:PlaceLocalHandle[BlockSet]) {

		var retval:Boolean=true;
		val grid = dB.getGrid();
		val dmap = dB.getMap();
				
		// Check copy is correct or not
		val rootbid = grid.getBlockId(itRow, colId);
		val rootpid = dmap.findPlace(rootbid);
		
        retval &= at(Place(rootpid)) {
			val rootblk = dB.handleBS().findBlock(rootbid);
			val srcmat = rootblk.getMatrix();
			//Debug.flushln("Searching front block:"+colId);
			val dstblk  = work2().findFrontRowBlock(colId);
			//Debug.flushln("Found "+dstblk.myRowId+","+dstblk.myColId);
			
			var mat:Matrix = dstblk.getMatrix();
			if (mat instanceof DenseMatrix && klen != mat.M) {
				val den = new DenseMatrix(klen, mat.N, (mat as DenseMatrix).d);
				mat = den as Matrix;
			}

			var eql:Boolean = true;
			
			for (var c:Long=0; c<srcmat.N&&eql; c++)
				for (var r:Long=0; r<klen&&eql; r++)
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
