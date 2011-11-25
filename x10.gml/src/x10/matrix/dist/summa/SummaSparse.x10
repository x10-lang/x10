/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.matrix.dist.summa;

import x10.io.Console;
import x10.util.Timer;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.MathTool;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.SparseMultSparseToDense;
import x10.matrix.sparse.SparseMultDenseToDense;
// Grid partitioning
import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;
// Matrix dist
import x10.matrix.dist.DistMatrix;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

// Inter-place communication
import x10.matrix.comm.CommHandle;
import x10.matrix.comm.MatrixRemoteCopy;
import x10.matrix.comm.MatrixReduce;

/** 
 * This is SUMMA implementation in X10 for distributed sparse matrix.
 * 
 * 
 * @see SummaDense
 */
public class SummaSparse {
	
	val beta:Double;
	val panelSize :Int;
	//
	val A:DistSparseMatrix;
	val B:DistSparseMatrix;
	val C:DistDenseMatrix;
	//
	val comhd:CommHandle;
	val rowBsPsMap:DistArray[Array[Int](1)](1);
	val colBsPsMap:DistArray[Array[Int](1)](1);
	//------------------------------------------------

	//=====================================================================
	// Constructor
	//=====================================================================
	public def this(ps:Int, be:Double,
					a:DistSparseMatrix, 
					b:DistSparseMatrix, 
					c:DistDenseMatrix) {
		//Check panelsize
		panelSize = Math.min(Math.min(ps, a.grid.getMinColSize()),
							 Math.min(ps, b.grid.getMinRowSize()));
		A = a; B=b; C=c;
		//alpha = al;
		beta  = be;
		//			
		comhd = new CommHandle();
		rowBsPsMap = a.grid.getRowBsPsMap();
		colBsPsMap = b.grid.getColBsPsMap();
	}
		
	//=====================================================================
	public static def estPanelSize(dA:DistSparseMatrix, dB:DistSparseMatrix):Int {
		val estCommuDataSize = 1024 * 1024 / 8 * 4;
		val ldm_a = dA.grid.rowBs(0);
		val ldm_b = dB.grid.colBs(0);
		val a_nzd =dA.distBs(here.id()).sparse.compSparsity();
		val b_nzd =dB.distBs(here.id()).sparse.compSparsity();
		val ldm = Math.max(((1.0/a_nzd)*ldm_a) as Int, ((1.0/b_nzd)* ldm_b) as Int);
		val max_ps = Math.min(dA.grid.colBs(0), dB.grid.rowBs(0));
		val estps:Int = estCommuDataSize/ldm;
		
		if (estps < 1)      return 1;
		if (estps > max_ps)	return max_ps;
		return estps;
	}	
	//------------------------------
	public def checkDim(myRowId:Int, myColId:Int):Boolean {
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
	//=====================================================================
	/**
	 * Perform SUMMA distributed dense matrix multiply: C = A &#42 B + beta &#42 C.
	 * 
	 * @param ps       panel size
	 * @param beta     scaling factor for output matrix
	 * @param A        first input distributed sparse matrix in multiplication
	 * @param B        second input distributed sparse matrix in multiplication
	 * @param C        the input/result distributed dense matrix
	 * @see parallelMult(wk1, wk2)
	 * @see SummaDense.parallelMult(wk1, wk2)
	 */
	public static def mult(var ps:Int,  /* Panel size*/
						   beta:Double, 
						   A:DistSparseMatrix, 
						   B:DistSparseMatrix, 
						   C:DistDenseMatrix) {
		if (ps < 1) ps = estPanelSize(A, B);
		
		val s = new SummaSparse(ps, beta, A, B, C);

		val wk1:DistArray[SparseCSC](1) = DistArray.make[SparseCSC](C.dist);
		val wk2:DistArray[SparseCSC](1) = DistArray.make[SparseCSC](C.dist);
		finish for (val [p]:Point in C.dist) {
			val rn = A.grid.getRowSize(p);
			val cn = B.grid.getColSize(p);
			async at (C.dist(p)){
				val pid=here.id();
				wk1(pid) = SparseCSC.make(rn, s.panelSize, 1.0);
				wk2(pid) = SparseCSC.make(s.panelSize, cn, 1.0);
			}
		}
		
		s.parallelMult(wk1, wk2);
	}
	
	/**
	 * Perform SUMMA distributed dense matrix multiply: C = A &#42 B<sup>T</sup> + beta &#42 C
	 * 
	 * @param ps       panel size
	 * @param beta     scaling factor for output matrix
	 * @param A        first input distributed sparse matrix in multiplication
	 * @param B        second input distributed sparse matrix which is used in tranposed form 
	 * @param C        the input/result distributed dense matrix
	 */	
	public static def multTrans(var ps:Int,  /* Panel size*/
								beta:Double, 
								A:DistSparseMatrix, 
								B:DistSparseMatrix, 
								C:DistDenseMatrix) {
		if (ps < 1) ps = estPanelSize(A, B);
		
		val s = new SummaSparse(ps, beta, A, B, C);

		val wk1:DistArray[DenseMatrix](1) = DistArray.make[DenseMatrix](C.dist);
		val wk2:DistArray[SparseCSC](1) = DistArray.make[SparseCSC](C.dist);
		val tmp:DistArray[DenseMatrix](1) = DistArray.make[DenseMatrix](C.dist);

		finish for (val [p]:Point in C.dist) {
			val rn = A.grid.getRowSize(p);
			val cn = B.grid.getColSize(p);
			async at (C.dist(p)){
				wk1(here.id()) = DenseMatrix.make(rn, s.panelSize);
				wk2(here.id()) = SparseCSC.make(s.panelSize, cn, 1.0);
				tmp(here.id()) = DenseMatrix.make(rn, s.panelSize);
			}
		}
		s.parallelMultTrans(wk1, wk2, tmp);
	}
	//=====================================================================
	//=====================================================================
	//
	public def parallelMult(work1:DistArray[SparseCSC](1),
							work2:DistArray[SparseCSC](1)) {
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
		if (MathTool.isZero(beta)) C.scale(0.0); 

		for (var kk:Int=0; kk<K; kk+=iwrk) {
			iwrk = Math.min(panelSize, B.grid.rowBs(itRow)-ii);
			iwrk = Math.min(iwrk,      A.grid.colBs(itCol)-jj); 
			val klen = iwrk;

			//Debug.flushln("Root place starts iteration "+kk+" panel size:"+klen); 
			//-------------------------------------------------------------------
			//Packing columns and rows and broadcast to same row and column block
			/* TIMING */ st = Timer.milliTime();
			ringCastRowBs(jj, iwrk, itCol, A, rowBsPsMap, work1);
			//Debug.flushln("Row blocks bcast ends ");
			ringCastColBs(ii, iwrk, itRow, B, colBsPsMap, work2);
			/* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st;
			//Debug.flushln("Column blocks bcast ends");
			
			//-----------------------------------------------------------------
			//
		    /* TIMING */ st = Timer.milliTime();
			finish 	for (var p:Int=0; p<Place.MAX_PLACES; p++) {
				//finish ateach (val [p]:Point in C.dist) { 
				val pid  = p;
				async at (C.distBs.dist(pid)) {
					/* update local block */
					val mypid = here.id();
					val wk1 = work1(mypid);
					val wk2 = work2(mypid);
					val ma = new SparseCSC(wk1.M, klen, wk1.ccdata);
					val mb = new SparseCSC(klen, wk2.N, wk2.ccdata);// as DenseMatrix(klen, bc);
					val bc = C.local() as DenseMatrix(wk1.M, wk2.N);//distBs(pid);
					//ma.printMatrix("Place "+mypid+" panel A");
					//mb.printMatrix("Place "+mypid+" panel B");
					SparseMultSparseToDense.comp(ma, mb, bc, true);
					//bc.print("Place "+mypid+" Mult result");
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

	//======================================================================
	// Ring cast temporary data work1 and work2 to row blocks and column blocks
	//======================================================================
	protected static def ringCastRowBs(jj:Int, klen:Int, itCol:Int,
										dsA:DistSparseMatrix,
										mapBsPs:DistArray[Array[Int](1)](1),
										work1:DistArray[SparseCSC](1)) {
		
		finish for (var p:Int=0; p<Place.MAX_PLACES; p++) { 
			val pid = p;
			val myColId = dsA.grid.getColBlockId(pid);
			val myRowId = dsA.grid.getRowBlockId(pid);
			if (myColId == itCol) {
				async at (dsA.distBs.dist(pid)) {
					val mypid = here.id();
					val src = work1(mypid);
					val cnt = dsA.distBs(mypid).copyCols(jj, klen, src);
					//src.print("Row ring cast out "+cnt+" from root at place:"+mypid);
					dsA.comm.rcastOp.rcast(work1, cnt, mapBsPs(mypid));
				}
			}
		}
	}
	
	protected static def ringCastColBs(ii:Int, klen:Int, itRow:Int, 
									   dsB:DistSparseMatrix,
									   mapBsPs:DistArray[Array[Int](1)](1),
									   work2:DistArray[SparseCSC](1)):void {
		
		finish for (var p:Int=0; p<Place.MAX_PLACES; p++) { 
			val pid = p;
			val myColId = dsB.grid.getColBlockId(pid);
			val myRowId = dsB.grid.getRowBlockId(pid);
			// Broadcast to all column blocks
			if (myRowId == itRow) {
				async at (dsB.distBs.dist(pid)) {
					val mypid = here.id();
					val src = work2(mypid);
					val cnt = dsB.distBs(mypid).copyRows(ii, klen, src);
					//src.print("Col ring cast src at place:"+mypid);
					dsB.comm.rcastOp.rcast(work2, cnt, mapBsPs(mypid));
				}
			}
		}
	}	
	//
	//================================================================================
	//================================================================================
	//================================================================================
	// SUMMA transpose-B method
	//
	public def parallelMultTrans(work1:DistArray[DenseMatrix](1),
								 work2:DistArray[SparseCSC](1),
								 tmpwk:DistArray[DenseMatrix](1)) {
		val K = B.M;
		//------------------------
		var itRow:Int = 0;
		var itCol:Int = 0; //Current processing iteration
		//
		var iwrk:Int = 0;
		var ii:Int = 0;
		var jj:Int = 0;
		//
		var st:Long=0;
		//---------------------------------------------------
		Debug.assure(A.N==B.N&&C.M==A.M&&C.N==B.M, "Matrix dimension mismatch");
		
		//Scaling the matrixesx
		/* TIMING */ st = Timer.milliTime();
		if (MathTool.isZero(beta))  C.scale(0.0D);
		/* TIMING */ C.distBs(here.id()).calcTime += Timer.milliTime() - st;

		//
		for (var kk:Int=0; kk<K; kk+=iwrk) {
			//Debug.flushln("Iteration start at "+kk+" itRow:"+itRow+
			//			  " itCol:"+itCol+" ii:"+ii+" jj:"+jj);
			iwrk = Math.min(panelSize, C.grid.colBs(itCol)-jj);
			iwrk = Math.min(iwrk,      B.grid.rowBs(itRow)-ii); 
			val klen = iwrk;
			//Debug.flushln("Iteration start at "+kk+" panel size:"+
			//				klen+" jj:"+jj+" A block col:"+C.grid.colBs(itCol));
			//-----------------------------------------------------------------
			//Packing columns and rows for broadcast
			/* TIMING */ st = Timer.milliTime();
			ringCastColBs(ii, klen, itRow, B, colBsPsMap, work2);
			/* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st; 
			//Debug.flushln("Column blocks bcast done");
			//-----------------------------------------------------------------
			
			// Perform block matrix multiply in all places
			/* TIMING */ st = Timer.milliTime();
			finish ateach (val [pid]:Point in C.dist) { 
				//
				val mypid = here.id();
				val ma = new DenseMatrix(work1(mypid).M, klen, work1(mypid).d);
				val mb = new SparseCSC(klen, work2(mypid).N, work2(mypid).ccdata);
				val bA = A.local() as SparseCSC(ma.M,mb.N);//distBs(pid);
				//
				SparseMultSparseToDense.compMultTrans(bA, mb, ma, false);
				//ma.multTrans(bA, mb, false);
			}
			/* TIMING */ C.distBs(here.id()).calcTime += Timer.milliTime() - st; 
			//Debug.flushln("Panel matrix mult done");
			   
			//---------------------------------------------------------------------------
			// Perform a ring broadcast reduce sum operation
			// C += reduceSum on work1  
			/* TIMING */ st = Timer.milliTime();
			SummaDense.reduceSumRowBs(jj, klen, itCol, C, rowBsPsMap, work1, tmpwk);
			/* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st; 
			//---------------------------------------------------------------------------
			//Debug.flushln("Row block reduce and result updated");

			/* update icurcol, icurrow, ii, jj */
			ii += iwrk;
			jj += iwrk;
			if ( jj>=C.grid.colBs(itCol)) { itCol++; jj = 0; };
			if ( ii>=B.grid.rowBs(itRow)) { itRow++; ii = 0; };
		}
	}
}