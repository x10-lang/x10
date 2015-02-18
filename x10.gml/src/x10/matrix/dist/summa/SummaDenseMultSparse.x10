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

package x10.matrix.dist.summa;

import x10.regionarray.DistArray;
import x10.util.Timer;


import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.DenseMultSparseToDense;

import x10.matrix.block.Grid;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

/** 
 * This class implements matrix multiplication between distributed dense and
 * distributed sparse matrix using SUMMA algorithm.
 * 
 * @see SummaDense
 */
public class SummaDenseMultSparse {
	
	val beta:ElemType;
	val panelSize:Long;

	val A:DistDenseMatrix;
	val B:DistSparseMatrix;
	val C:DistDenseMatrix;

	val rowBsPsMap:DistArray[Rail[Long]](1);
	val colBsPsMap:DistArray[Rail[Long]](1);

	public def this(ps:Long, be:ElemType,
					a:DistDenseMatrix, 
					b:DistSparseMatrix, 
					c:DistDenseMatrix) {
		//Check panelsize
		panelSize = Math.min(Math.min(ps, a.grid.getMinColSize()),
							 Math.min(ps, b.grid.getMinRowSize()));
		A = a; B=b; C=c;
		//alpha = al;
        if (MathTool.isZero(be)) beta = 0.0 as ElemType;
        else beta  = be;
		//			
		rowBsPsMap = a.grid.getRowBsPsMap();
		colBsPsMap = b.grid.getColBsPsMap();
	}
		

	public def checkDim(myRowId:Long, myColId:Long):Boolean {
		var st:Boolean = true;
		val myId = C.grid.getBlockId(myRowId, myColId);
		// 
		st &= (C.M==A.M);
		st &= (A.N==B.M);
		st &= (B.N==C.N);
		//
		if (st == false)
			Console.OUT.println("Dimension and location check fails!");
		return st;
	}

	public static def estPanelSize(dA:DistDenseMatrix, dB:DistSparseMatrix):Long {
		val estCommuDataSize = 1024 * 1024 / 8 * 4;
		val ldm_a = dA.grid.rowBs(0);
		val ldm_b = dB.grid.colBs(0);
		val l_nzd =dB.distBs(here.id()).sparse.compSparsity();
		val ldm = Math.max(ldm_a, ((1.0/l_nzd)* ldm_b) as Long);
		val max_ps = Math.min(dA.grid.colBs(0), dB.grid.rowBs(0));
		val estps:Long = estCommuDataSize/ldm;
		
		if (estps < 1)      return 1;
		if (estps > max_ps)	return max_ps;
		return estps;
	}	

	/**
	 * Perform SUMMA distributed dense matrix multiply: C = A &#42 B + beta &#42 C.
	 * 
	 * @param ps       panel size
	 * @param beta     scaling factor for output matrix
	 * @param A        first input distributed dense matrix in multiplication
	 * @param B        second input distributed sparse matrix in multiplication
	 * @param C        the input/result distributed dense matrix
	 * @see parallelMult(wk1, wk2)
	 * @see SummaDense.parallelMult(wk1, wk2)
	 */
	public static def mult(var ps:Long,
			       beta:ElemType, 
			       A:DistDenseMatrix, 
			       B:DistSparseMatrix, 
			       C:DistDenseMatrix) {
	    if (ps < 1) ps = estPanelSize(A, B);
		
	    val s = new SummaDenseMultSparse(ps, beta, A, B, C);

	    val wk1 <: DistArray[DenseMatrix](1) = DistArray.make[DenseMatrix](C.dist);
	    val wk2 <: DistArray[SparseCSC](1) = DistArray.make[SparseCSC](C.dist);
	    finish for ([placeId] in C.dist) {
		val p = placeId as Long;
		val rn = A.grid.getRowSize(p);
		val cn = B.grid.getColSize(p);
		at(C.dist(p)) async {
		    wk1(here.id()) = DenseMatrix.make(rn, s.panelSize);
		    wk2(here.id()) = SparseCSC.make(s.panelSize, cn, 1.0f);
		}
	    }
	    s.parallelMult(wk1, wk2);
	}
	
	/**
	 * Perform SUMMA distributed dense matrix multiply: C = A &#42 B<sup>T</sup> + beta &#42 C
	 * 
	 * @param ps       panel size
	 * @param beta     scaling factor for output matrix
	 * @param A        first input distributed dense matrix in multiplication
	 * @param B        second input distributed sparse matrix which is used in tranposed form 
	 * @param C        the input/result distributed dense matrix
	 */	
	public static def multTrans(var ps:Long,
								beta:ElemType, 
								A:DistDenseMatrix, 
								B:DistSparseMatrix, 
								C:DistDenseMatrix) {
	    if (ps < 1) ps = estPanelSize(A, B);
	    val s = new SummaDenseMultSparse(ps, beta, A, B, C);

	    val wk1 <: DistArray[DenseMatrix](1) = DistArray.make[DenseMatrix](C.dist);
	    val wk2 <: DistArray[SparseCSC](1) = DistArray.make[SparseCSC](C.dist);
	    val tmp <: DistArray[DenseMatrix](1) = DistArray.make[DenseMatrix](C.dist);
	    
	    finish for ([placeId] in C.dist) {
		val p = placeId as Long;
		val rn = A.grid.getRowSize(p);
		val cn = B.grid.getColSize(p);
		at(C.dist(p)) async {
		    wk1(here.id()) = DenseMatrix.make(rn, s.panelSize);
		    wk2(here.id()) = SparseCSC.make(s.panelSize, cn, 1.0f);
		    tmp(here.id()) = DenseMatrix.make(rn, s.panelSize);
		}
	    }
	    s.parallelMultTrans(wk1, wk2, tmp);
	}

	public def parallelMult(work1:DistArray[DenseMatrix](1),
							work2:DistArray[SparseCSC](1)) {
		val K = A.N;
		var itRow:Long = 0;
		var itCol:Long = 0; //Current processing iteration
		var iwrk:Long = 0;
		var ii:Long = 0;
		var jj:Long = 0;
		var st:Long= 0;

		C.scale(beta); 

		for (var kk:Long=0; kk<K; kk+=iwrk) {
			iwrk = Math.min(panelSize, B.grid.rowBs(itRow)-ii);
			iwrk = Math.min(iwrk,      A.grid.colBs(itCol)-jj); 
			val klen = iwrk;

			//Debug.flushln("Root place starts iteration "+kk+" panel size:"+klen); 

			//Packing columns and rows and broadcast to same row and column block
			/* TIMING */ st = Timer.milliTime();
			SummaDense.ringCastRowBs(jj, iwrk, itCol, A, rowBsPsMap, work1);
			SummaSparse.ringCastColBs(ii, iwrk, itRow, B, colBsPsMap, work2);
			/* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st;
			//Debug.flushln("Row and column blocks bcast ends");
			

			//
		    /* TIMING */ st = Timer.milliTime();
            finish for (var p:Long=0; p<Place.numPlaces(); p++) {
                //finish ateach(val [p]:Point in C.dist) { 
                val pid  = p;
                at(C.distBs.dist(pid)) async {
					/* update local block */
					val mypid = here.id();
					val wk1 = work1(mypid);
					val wk2 = work2(mypid);
					val ma = new DenseMatrix(wk1.M, klen, wk1.d);
					val mb = new SparseCSC(klen, wk2.N, wk2.ccdata);// as DenseMatrix(klen, bc);
					val bc = C.local() as DenseMatrix(wk1.M, wk2.N);//distBs(pid);
					DenseMultSparseToDense.comp(ma, mb, bc, true);
					//bc.mult(ma, mb, true);
				}
			}
			/* TIMING */ C.distBs(here.id()).calcTime += Timer.milliTime() - st;
			//Debug.flushln("Panel matrix mult done");
			
			/* update icurcol, icurrow, ii, jj */
			ii += iwrk;
			jj += iwrk;
			if ( jj>=A.grid.colBs(itCol)) { itCol++; jj = 0; };
			if ( ii>=B.grid.rowBs(itRow)) { itRow++; ii = 0; };
		}
		//Debug.flushall();
	}
	

	// SUMMA transpose-B method

	public def parallelMultTrans(work1:DistArray[DenseMatrix](1),
								 work2:DistArray[SparseCSC](1),
								 tmpwk:DistArray[DenseMatrix](1)) {
		val K = B.M;
		var itRow:Long = 0;
		var itCol:Long = 0; //Current processing iteration
		var iwrk:Long = 0;
		var ii:Long = 0;
		var jj:Long = 0;
		var st:Long=0;

		Debug.assure(A.N==B.N&&C.M==A.M&&C.N==B.M);
		
		/* TIMING */ st = Timer.milliTime();
		C.scale(beta);
		/* TIMING */ C.distBs(here.id()).calcTime += Timer.milliTime() - st;

		//
		for (var kk:Long=0; kk<K; kk+=iwrk) {
			//Debug.flushln("Iteration start at "+kk+" itRow:"+itRow+
			//			  " itCol:"+itCol+" ii:"+ii+" jj:"+jj);
			iwrk = Math.min(panelSize, C.grid.colBs(itCol)-jj);
			iwrk = Math.min(iwrk,      B.grid.rowBs(itRow)-ii); 
			val klen = iwrk;
			//Debug.flushln("Iteration start at "+kk+" panel size:"+
			//				klen+" jj:"+jj+" A block col:"+C.grid.colBs(itCol));

			//Packing columns and rows for broadcast
			/* TIMING */ st = Timer.milliTime();
			SummaSparse.ringCastColBs(ii, klen, itRow, B, colBsPsMap, work2);
			/* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st; 
			//Debug.flushln("Column blocks bcast done");

			
			// Perform block matrix multiply in all places
			/* TIMING */ st = Timer.milliTime();
			finish ateach(val [pid]:Point in C.dist) { 
				//
				val mypid = here.id();
				val ma = new DenseMatrix(work1(mypid).M, klen, work1(mypid).d);
				val mb = new SparseCSC(klen, work2(mypid).N, work2(mypid).ccdata);
				val bA = A.local() as DenseMatrix(ma.M, mb.N);//distBs(pid);
				//
				DenseMultSparseToDense.compMultTrans(bA, mb, ma, false);
				//ma.multTrans(bA, mb, false);
			}
			/* TIMING */ C.distBs(here.id()).calcTime += Timer.milliTime() - st; 
			//Debug.flushln("Panel matrix mult done");
			   

			// Perform a ring broadcast reduce sum operation
			// C += reduceSum on work1  
			/* TIMING */ st = Timer.milliTime();
			SummaDense.reduceSumRowBs(jj, klen, itCol, C, rowBsPsMap, work1, tmpwk);
			/* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st; 

			//Debug.flushln("Row block reduce and result updated");

			/* update icurcol, icurrow, ii, jj */
			ii += iwrk;
			jj += iwrk;
			if ( jj>=C.grid.colBs(itCol)) { itCol++; jj = 0; };
			if ( ii>=B.grid.rowBs(itRow)) { itRow++; ii = 0; };
		}
	}
}
