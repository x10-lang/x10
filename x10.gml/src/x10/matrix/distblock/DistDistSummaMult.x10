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

package x10.matrix.distblock;

import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Debug;
import x10.matrix.MathTool;

import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.BlockBlockMult;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;

import x10.matrix.comm.BlockRingCast;
import x10.matrix.comm.BlockReduce;

/**
 * SUMMA implementation on distributed block matrix
 */
public class DistDistSummaMult {
	//val alpha:Double;
	val beta:Double;
	val panelSize :Int;
	//
	val A:DistBlockMatrix;
	val B:DistBlockMatrix;
	val C:DistBlockMatrix;
	//
	val work1:PlaceLocalHandle[BlockSet];
	val work2:PlaceLocalHandle[BlockSet];
	//
	val gA:Grid, gB:Grid, gC:Grid;
	//------------------------------------------------

	public var commTime:Long=0;
	public var calcTime:Long=0;
	
	//=====================================================================
	// Constructor
	//=====================================================================
	public def this(
			ps:Int, be:Double,
			a:DistBlockMatrix, 
			b:DistBlockMatrix, 
			c:DistBlockMatrix,
			w1:PlaceLocalHandle[BlockSet],
			w2:PlaceLocalHandle[BlockSet]) {
		//Check panelsize
		gA = a.getGrid();
		gB = b.getGrid();
		gC = c.getGrid();
		work1 = w1;
		work2 = w2;
		
		panelSize = ps;
		A = a; B=b; C=c;
		//alpha = al;
		beta  = be;
	}
	/**
	 * Estimate the panel size.
	 */
	public static def estPanelSize(ps:Int, ga:Grid, gb:Grid):Int {
		
		val maxps = Math.min(ga.colBs(0), gb.rowBs(0));
		var estps:Int = 128;//estCommuDataSize/ldm;
		estps = Math.min(ps, estps);
		
		if (estps < 1)      estps = 1;
		if (estps > maxps)	estps = maxps;
		
		estps = Math.min(Math.min(estps, ga.getMinColSize()),
				Math.min(estps, gb.getMinRowSize()));	
		
		return estps;
	}	
	
	private static def makeColTemp(distA:DistBlockMatrix, ps:Int):BlockSet {
		val lbs = distA.handleBS();
		val g = lbs.getGrid();
		val d = lbs.getDistMap();
		val cbs:Array[Int](1) = new Array[Int](g.numColBlocks, (i:Int)=>ps);
		val ng = new Grid(g.M, g.numColBlocks*ps, g.rowBs, cbs);
		val nbl = lbs.allocFirstColBlocks();
		val nbs = new BlockSet(ng, d, nbl);
		nbs.buildBlockMap();
		return nbs;
	}
	
	private static def makeRowTemp(distB:DistBlockMatrix, ps:Int):BlockSet {
		val lbs = distB.handleBS();
		val g = lbs.getGrid();
		val d = lbs.getDistMap();
		val rbs:Array[Int](1) = new Array[Int](g.numRowBlocks, (i:Int)=>ps);
		val ng = new Grid(g.numColBlocks*ps, g.N, rbs, g.colBs);
		val nbl = lbs.allocFirstRowBlocks();
		val nbs = new BlockSet(ng, d, nbl);
		nbs.buildBlockMap();
		return nbs;
	}
	
	
	public static def mult(
			var ps:Int,  /* Panel size*/
			beta:Double, 
			A:DistBlockMatrix, 
			B:DistBlockMatrix, 
			C:DistBlockMatrix) {
		
		val pansz = estPanelSize(ps, A.getGrid(), B.getGrid());
		val w1 = PlaceLocalHandle.make[BlockSet](Dist.makeUnique(), ()=>makeColTemp(A, pansz));
		val w2 = PlaceLocalHandle.make[BlockSet](Dist.makeUnique(), ()=>makeRowTemp(B, pansz));
		val s = new DistDistSummaMult(pansz, beta, A, B, C, w1, w2);

		s.parallelMult();
		
	}	
	
	//=====================================================================
	//
	/**
	 * Distributed matrix multiplication using SUMMA alogrithm
	 * 
	 * @param work1		temporary space used for ring cast each row blocks
	 * @param work2 	temporary space used for ring cast each column blocks
	 */
	public def parallelMult() {
		//
		val K = A.N;
		//------------------------
		var itRow:Int = 0;
		var itCol:Int = 0; //Current processing iteration
		//
		var iwrk:Int = 0;
		var ii:Int = 0;
		var jj:Int = 0;
		var st:Long= 0;
		//
		//---------------------------------------------------

		//Scaling the matrixesx
		if (MathTool.isZero(beta)) C.reset();
		
		for (var kk:Int=0; kk<K; kk+=iwrk) {
			iwrk = Math.min(panelSize, gB.rowBs(itRow)-ii);
			iwrk = Math.min(iwrk,      gA.colBs(itCol)-jj); 
			val klen = iwrk;

			//Debug.flushln("Root place starts iteration "+kk+" panel size:"+klen); 
			//
			//-------------------------------------------------------------------
			//Packing columns and rows and broadcast to same row and column block
			/* TIMING */ st = Timer.milliTime();
			ringCastRowBs(jj, iwrk, itCol, A, work1);
			ringCastColBs(ii, iwrk, itRow, B, work2);
			/* TIMING */ commTime += Timer.milliTime() - st;
			//Debug.flushln("Row and column blocks bcast ends");
			
			//-----------------------------------------------------------------
			/* TIMING */ st = Timer.milliTime();
			finish 	ateach (Dist.makeUnique()) {
				/* update local block */
				val mypid = here.id();
				val wk1 = work1();
				val wk2 = work2();
				val cmap = C.handleBS().blockMap;
				for (pnt:Point(2) in cmap) {
					val rb = pnt(0);
					val cb = pnt(1);
					val cmat = cmap(pnt).getMatrix();
					val ablk = wk1.blockMap(rb, 0);
					val bblk = wk2.blockMap(0, cb);
					val amat = ablk.getMatrix() as Matrix(self.M, klen);
					val bmat = bblk.getMatrix() as Matrix(klen, self.N);
						
					cmat.mult(amat, bmat, true);
				}	
			 }
			/* TIMING */ calcTime += Timer.milliTime() - st;
			/* update icurcol, icurrow, ii, jj */
			ii += iwrk;
			jj += iwrk;
			if ( jj>=gA.colBs(itCol)) { itCol++; jj = 0; };
			if ( ii>=gB.rowBs(itRow)) { itRow++; ii = 0; };
		}
	}
	
	protected static def ringCastRowBs(
			jj:Int, klen:Int, itCol:Int, 
			dA:DistBlockMatrix, 
			work1:PlaceLocalHandle[BlockSet]): void {
		
		val grid = dA.getGrid();
		finish for (var bid:Int=0; bid<dA.getGrid().size; bid++) { 
			val myColId = grid.getColBlockId(bid);
			val myRowId = grid.getRowBlockId(bid);
			if (myColId == itCol) {
				val pid = dA.getMap().findPlace(bid);
				val rootbid = bid;
				async at (Dist.makeUnique()(pid)) {
					val dstblk = work1().blockMap(myRowId, 0);
					val srcblk = dA.handleBS().blockMap(myRowId, myColId);
					srcblk.copyCols(jj, klen, dstblk.getMatrix());
					BlockRingCast.rowCast(work1, rootbid, klen);
				}
			}
		}
	}
		
	protected static def ringCastColBs(
			ii:Int, klen:Int, itRow:Int, 
			dB:DistBlockMatrix,
			work2:PlaceLocalHandle[BlockSet]):void {
		
		val grid = dB.getGrid();
		finish for (var bid:Int=0; bid<dB.getGrid().size; bid++) { 
	
			val myColId = grid.getColBlockId(bid);
			val myRowId = grid.getRowBlockId(bid);
			// Broadcast to all column blocks
			if (myRowId == itRow) {
				val pid = dB.getMap().findPlace(bid);
				val rootbid = bid;
				async at (Dist.makeUnique()(pid)) {
					val dstblk = work2().blockMap(0, myColId);
					val srcblk = dB.handleBS().blockMap(myRowId, myColId);
					srcblk.copyRows(ii, klen, dstblk.getMatrix());
					BlockRingCast.colCast(work2, rootbid, klen);
				}
			}
		}
	}
	
	
}
