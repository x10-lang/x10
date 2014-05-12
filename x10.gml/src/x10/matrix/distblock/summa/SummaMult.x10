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

import x10.regionarray.Dist;
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.Grid;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistBlockMatrix;

/**
 * SUMMA implementation on distributed block matrix
 */
public class SummaMult {
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
		
		//alpha = al;
		beta  = be;
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
			A:DistBlockMatrix, 
			B:DistBlockMatrix, 
			C:DistBlockMatrix, plus:Boolean) {
		mult(10, plus?1.0:0.0, A, B, C);
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
		val s = new SummaMult(pansz, beta, A, B, C, w1, w2);

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
		var itRow:Int = 0n;
		var itCol:Int = 0n; //Current processing iteration
		var iwrk:Long = 0;
		var ii:Long = 0;
		var jj:Long = 0;
		var st:Long= 0;
		val gA = A.getGrid();
		val gB = B.getGrid();

		//Scaling the matrixesx
		if (MathTool.isZero(beta)) C.reset();
		
		for (var kk:Long=0; kk<K; kk+=iwrk) {
			iwrk = Math.min(panelSize, gB.rowBs(itRow)-ii);
			iwrk = Math.min(iwrk,      gA.colBs(itCol)-jj); 
			val klen = iwrk;

			//Debug.flushln("Root place starts iteration "+kk+" panel size:"+klen); 
			//Packing columns and rows and broadcast to same row and column block
			/* TIMING */ 
			st = Timer.milliTime();
			//Debug.flushln("Start row ring-bcast. jj:"+jj+" iwrk:"+iwrk+" itcol:"+itCol);
			AllGridCast.startRowCast(jj, iwrk, itCol, A, work1);
			//Debug.flushln("Start col ring-bcast. ii:"+ii+" itRow:"+itRow);
			AllGridCast.startColCast(ii, iwrk, itRow, B, work2);
			/* TIMING */ 
			commTime += Timer.milliTime() - st;
			/* TIMING */ 
			st = Timer.milliTime();
			//Debug.flushln("Row and column blocks bcast ends");
			
			finish ateach(Dist.makeUnique()) {
				/* update local block */
				val mypid = here.id();
				val wk1 = work1();
				val wk2 = work2();
				val cbs = C.handleBS();
				val itr = cbs.iterator();
				while (itr.hasNext()) {
					val cblk = itr.next();
					val cmat = cblk.getMatrix();
					val ablk = wk1.findFrontColBlock(cblk.myRowId); 
					val bblk = wk2.findFrontRowBlock(cblk.myColId);
					
					val amat:Matrix;
					val bmat:Matrix;
					if (ablk.isDense()) {
						amat = new DenseMatrix(ablk.getMatrix().M, klen, ablk.getData()) as Matrix;
					} else {
						amat = new SparseCSC(ablk.getMatrix().M, klen, ablk.getCompressArray()) as Matrix;
					}
					if (bblk.isDense()) {
						bmat = new DenseMatrix(klen, bblk.getMatrix().N, bblk.getData()) as Matrix;
					} else {
						bmat = new SparseCSC(klen, bblk.getMatrix().N, bblk.getCompressArray()) as Matrix;
					}
					val stt:long=Timer.milliTime();
					cmat.mult(amat as Matrix(cmat.M), bmat as Matrix(amat.N, cmat.N), true);
					cblk.calcTime += Timer.milliTime()-stt;
				}
			 }
			/* TIMING */ 
			calcTime += Timer.milliTime() - st;
			//Debug.flushln("Done all local matrix computation");

			/* update icurcol, icurrow, ii, jj */
			ii += iwrk;
			jj += iwrk;
			if ( jj>=gA.colBs(itCol)) { itCol++; jj = 0; };
			if ( ii>=gB.rowBs(itRow)) { itRow++; ii = 0; };
		}
	}
}
