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

package x10.matrix.distblock;

import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.block.Grid;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.comm.BlockRingCast;

/**
 * SUMMA implementation on distributed block matrix
 */
public class DistDistSummaMult {
	//val alpha:Double;
	val beta:Double;
    val panelSize:Long;

	val A:DistBlockMatrix;
	val B:DistBlockMatrix;
	val C:DistBlockMatrix;

	val work1:PlaceLocalHandle[BlockSet];
	val work2:PlaceLocalHandle[BlockSet];

	public var commTime:Long=0;
	public var calcTime:Long=0;

	// Constructor
	public def this(
			ps:Long, be:Double,
			a:DistBlockMatrix, 
			b:DistBlockMatrix, 
			c:DistBlockMatrix,
			w1:PlaceLocalHandle[BlockSet],
			w2:PlaceLocalHandle[BlockSet]) {
		//Check panelsize
		work1 = w1;
		work2 = w2;
		
		panelSize = ps;
		A = a; B=b; C=c;
		
		//A.buildRowCastPlaceMap(); // unimplemented!
		//B.buildColCastPlaceMap(); // unimplemented!
		
		//alpha = al;
		beta  = be;
		
		throw new Error("Unimplemented");
	}
	 
	/**
	 * Estimate the panel size.
	 */
	public static def estPanelSize(ps:Long, ga:Grid, gb:Grid):Long {
		val maxps = Math.min(ga.colBs(0), gb.rowBs(0));
		var estps:Long = 128;//estCommuDataSize/ldm;
		estps = Math.min(ps, estps);
		
		if (estps < 1)      estps = 1;
		if (estps > maxps)	estps = maxps;
		
		estps = Math.min(Math.min(estps, ga.getMinColSize()),
				Math.min(estps, gb.getMinRowSize()));	
		
		return estps;
	}	

	public static def mult(
			var ps:Long,  /* Panel size*/
			beta:Double, 
			A:DistBlockMatrix, 
			B:DistBlockMatrix, 
			C:DistBlockMatrix) {
		
		val pansz = estPanelSize(ps, A.getGrid(), B.getGrid());
		val w1 = A.makeTempFrontColBlocks(pansz);
		val w2 = B.makeTempFrontRowBlocks(pansz);
		val s = new DistDistSummaMult(pansz, beta, A, B, C, w1, w2);

		s.parallelMult();
	}

	/**
	 * Distributed matrix multiplication using SUMMA alogrithm
	 * 
	 * @param work1		temporary space used for ring cast each row blocks
	 * @param work2 	temporary space used for ring cast each column blocks
	 */
	public def parallelMult() {
		val K = A.N;

		var itRow:Long = 0;
		var itCol:Long = 0; //Current processing iteration
		var iwrk:Long = 0;
		var ii:Long = 0;
		var jj:Long = 0;
		var st:Long= 0;
		val gA = A.getGrid();
		val gB = B.getGrid();

		//Scaling the matrices
		if (MathTool.isZero(beta)) C.reset();
		
		for (var kk:Long=0; kk<K; kk+=iwrk) {
			iwrk = Math.min(panelSize, gB.rowBs(itRow)-ii);
			iwrk = Math.min(iwrk,      gA.colBs(itCol)-jj); 
			val klen = iwrk;

			//Debug.flushln("Root place starts iteration "+kk+" panel size:"+klen); 
			//

			//Packing columns and rows and broadcast to same row and column block
			/* TIMING */ st = Timer.milliTime();
			startRowCast(jj, iwrk, itCol, A, work1);
			startColCast(ii, iwrk, itRow, B, work2);
			/* TIMING */ commTime += Timer.milliTime() - st;
			//Debug.flushln("Row and column blocks bcast ends");
			

			/* TIMING */ st = Timer.milliTime();
			// finish 	ateach(Dist.makeUnique()) {
			// 	/* update local block */
			// 	val mypid = here.id();
			// 	val wk1 = work1();
			// 	val wk2 = work2();
			// 	val cbs = C.handleBS();
			// 	val itr = cbs.iterator();
			// 	val cmap = C.handleBS().blockMap;
			// 	while (itr.hasNext()) {
			// 		val cblk = itr.next();
			// 		val rb = cblk.myRowId;
			// 		val cb = cblk.myColId;
			// 		val cmat = cblk.getMatrix();
			// 		val ablk = wk1.findLocalRootRowBlock(rb);
			// 		val bblk = wk2.findLocalRootColBlock(cb);
			// 		val amat = ablk.getMatrix() as Matrix(self.M, klen);
			// 		val bmat = bblk.getMatrix() as Matrix(klen, self.N);
			// 			
			// 		cmat.mult(amat, bmat, true);
			// 	}	
			//  }
			/* TIMING */ calcTime += Timer.milliTime() - st;
			/* update icurcol, icurrow, ii, jj */
			ii += iwrk;
			jj += iwrk;
			if ( jj>=gA.colBs(itCol)) { itCol++; jj = 0; };
			if ( ii>=gB.rowBs(itRow)) { itRow++; ii = 0; };
		}
	}

	public static def startRowCast(jj:Long, klen:Long, itCol:Long, distA:DistBlockMatrix, work1:PlaceLocalHandle[BlockSet]){
		val dmap = distA.getMap();
		val grid = distA.getGrid();
		val sttplc = dmap.findPlace(grid.getBlockId(0, itCol));
		if (here.id()!= sttplc) {
			at(Place(sttplc)) {
				startRowCast(jj, klen, itCol, distA, work1);
			}
		} else {
			//Using column cast place list as the starting place of row cast
			val bs   = distA.handleBS();
			val plst = bs.colCastPlaceMap.getPlaceList(itCol);
			
			for (p in plst.range()) {
				val pid = plst(p);
				if (pid != here.id()) async {
					at(Place(pid)) {
						ringCastRowBs(jj, klen, itCol, distA, work1);
					}
				} else async {
					ringCastRowBs(jj, klen, itCol, distA, work1);
				}
			}
		}		
	}
	
	protected static def ringCastRowBs(
			jj:Long, klen:Long, itCol:Long, 
			dA:DistBlockMatrix, 
			work1:PlaceLocalHandle[BlockSet]): void {
		
		val grid = dA.getGrid();
		val bs = dA.handleBS();
		val itr = bs.iterator();
		while (itr.hasNext()) {
			val src = itr.next();
			if (src.myColId != itCol) continue;
			val rootbid = grid.getBlockId(src.myRowId, src.myColId);
			val dstblk  = work1().findFrontRowBlock(src.myRowId);
			val rowplst = bs.rowCastPlaceMap.getPlaceList(src.myRowId);
			val datcnt = src.copyCols(jj, klen, dstblk.getMatrix());
			BlockRingCast.rowCastToPlaces(work1, rootbid, datcnt, rowplst);
		}
	}
	
	public static def startColCast(ii:Long, klen:Long, itRow:Long, distB:DistBlockMatrix, work2:PlaceLocalHandle[BlockSet]){
		val dmap = distB.getMap();
		val grid = distB.getGrid();
		val sttplc = dmap.findPlace(grid.getBlockId(itRow, 0));
		if (here.id()!= sttplc) {
			at(Place(sttplc)) {
				startColCast(ii, klen, itRow, distB, work2);
			}
		} else {
			//Using column cast place list as the starting place of row cast
			val bs   = distB.handleBS();
			val plst = bs.rowCastPlaceMap.getPlaceList(itRow);
			
			for (p in plst.range()) {
				val pid = plst(p);
				if (pid != here.id()) async {
					at(Place(pid)) {
						ringCastColBs(ii, klen, itRow, distB, work2);
					}
				} else async {
					ringCastColBs(ii, klen, itRow, distB, work2);
				}
			}
		}		
	}
	
	protected static def ringCastColBs(
			ii:Long, klen:Long, itRow:Long, 
			dB:DistBlockMatrix, 
			work2:PlaceLocalHandle[BlockSet]) {
		
		val grid = dB.getGrid();
		val bs  = dB.handleBS();
		val itr = bs.iterator();
		
		while (itr.hasNext()) {
			val src = itr.next();
			if (src.myRowId != itRow) continue;
			val rootbid = grid.getBlockId(src.myRowId, src.myColId);
			val dstblk  = work2().findFrontColBlock(src.myColId);
			val colplst = bs.colCastPlaceMap.getPlaceList(src.myColId);

			//Dense matrix must reshape to (klen x N), klen may be smaller than the
			//original matrix M when it is created. The data buffer must keep data compact 
			//before sending out. Sparse matrix is not affacted.
			var mat:Matrix = dstblk.getMatrix();
			if (mat instanceof DenseMatrix && klen != mat.M) {
				val den = new DenseMatrix(klen, mat.N, (mat as DenseMatrix).d);
				mat = den as Matrix;
			}

			val datcnt = src.copyRows(ii, klen, mat);
			BlockRingCast.colCastToPlaces(work2, rootbid, datcnt, colplst);
		}
	}
}
