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
// Grid partitioning
import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.DenseBlock;
// Matrix dist
import x10.matrix.dist.DistMatrix;
import x10.matrix.dist.DistDenseMatrix;
// Inter-place communication
import x10.matrix.comm.CommHandle;
import x10.matrix.comm.MatrixRemoteCopy;
import x10.matrix.comm.MatrixReduce;

/** 
 * This is SUMMA implementation in X10 for distributed dense matrix.
 * 
 * <p>In addition to the dimension requirement for matrix multiplication,
 * to use SUMMA, all input and output matrices must be decomposed
 * in the same way, which satisfies the matrix multiplication dimension requirement
 * for blocks in the same position of two input matrices and output matrix.
 * 
 * <p>To achieve better performance, panel size needs to be optimized, which could
 * be varied due to different matrices dimensions, network bandwidth, and 
 * BLAS library performance.
 */
public class SummaDense {
	
	//val alpha:Double;
	val beta:Double;
	val panelSize :Int;
	//
	val A:DistDenseMatrix;
	val B:DistDenseMatrix;
	val C:DistDenseMatrix;
	//
	val comhd:CommHandle;
	val rowBsPsMap:DistArray[Array[Int](1)](1);
	val colBsPsMap:DistArray[Array[Int](1)](1);
	//------------------------------------------------

	//=====================================================================
	// Constructor
	//=====================================================================
	public def this(
			ps:Int, be:Double,
			a:DistDenseMatrix, 
			b:DistDenseMatrix, 
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
	//------------------------------
	/**
	 * Check input and output distributed matrix partitioning for SUMMA
	 */
	protected def checkPartition():Boolean {
		var ret:Boolean = true;
		
		ret &= A.grid.numRowBlocks == B.grid.numRowBlocks;
		ret &= A.grid.numRowBlocks == C.grid.numRowBlocks;
		ret &= A.grid.numColBlocks == B.grid.numColBlocks;
		ret &= A.grid.numColBlocks == C.grid.numColBlocks;
		
		for (var rb:Int=0; rb<A.grid.numRowBlocks && ret; rb++) 
			ret &= A.grid.rowBs(rb)==C.grid.rowBs(rb);
			
		for (var cb:Int =0; cb<A.grid.numColBlocks && ret; cb++) 
			ret &= B.grid.colBs(cb) == C.grid.colBs(cb);
		
		return ret;
	}
	
	/**
	 * Check input and output distributed matrix partitioning for SUMMA
	 */
	protected def checkPartitionTrans():Boolean {
		var ret:Boolean = true;
		
		ret &= A.grid.numRowBlocks == B.grid.numRowBlocks;
		ret &= A.grid.numRowBlocks == C.grid.numRowBlocks;
		ret &= A.grid.numColBlocks == B.grid.numColBlocks;
		ret &= A.grid.numColBlocks == C.grid.numColBlocks;
		
		for (var rb:Int=0; rb<A.grid.numRowBlocks && ret; rb++) 
			ret &= A.grid.rowBs(rb)==C.grid.rowBs(rb);
		
		for (var cb:Int =0; cb<A.grid.numColBlocks && ret; cb++) 
			ret &= B.grid.rowBs(cb) == C.grid.colBs(cb);
		
		return ret;
	}
		
	
	
	protected def checkDim(myRowId:Int, myColId:Int):Boolean {
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
	 * Estimate the panel size.
	 */
	public static def estPanelSize(dA:DistDenseMatrix, dB:DistDenseMatrix):Int {
		//val estCommuDataSize = 1024 * 1024 / 8 * 4;
		//val ldm_a = dA.grid.rowBs(0);
		//val ldm_b = dB.grid.colBs(0);
		//val ldm = Math.max(ldm_a, ldm_b);
		
		val maxps = Math.min(dA.grid.colBs(0), dB.grid.rowBs(0));
		val estps:Int = 128;//estCommuDataSize/ldm;
		if (estps < 1)      return 1;
		if (estps > maxps)	return maxps;
		return estps;
	}
	
	//=====================================================================
	/**
	 * SUMMA distributed dense matrix multiplication: C = A &#42 B + beta &#42 C.
	 * 
	 * <p>Two additional distributed dense matrices are created.
	 * These two matrices are panel size matrix used in row and column blocks
	 * broadcast and performing matrix multiplication. 
	 * 
	 * @param ps       panel size
	 * @param beta     scaling factor for output matrix
	 * @param A        first input distributed dense matrix in multiplication
	 * @param B        second input distributed dense matrix in multiplication
	 * @param C        the input/result distributed dense matrix
	 * @see parallelMult(wk1, wk2)
	 */
	public static def mult(
			var ps:Int,  /* Panel size*/
			beta:Double, 
			A:DistDenseMatrix, 
			B:DistDenseMatrix, 
			C:DistDenseMatrix) {
		if (ps < 1) ps = estPanelSize(A, B);
		val s = new SummaDense(ps, beta, A, B, C);

		val wk1:DistArray[DenseMatrix](1) = DistArray.make[DenseMatrix](C.dist);
		val wk2:DistArray[DenseMatrix](1) = DistArray.make[DenseMatrix](C.dist);
		finish for (val [p]:Point in C.dist) {
			val rn = A.grid.getRowSize(p);
			val cn = B.grid.getColSize(p);
			async at (C.dist(p)){
				wk1(here.id()) = DenseMatrix.make(rn, s.panelSize);
				wk2(here.id()) = DenseMatrix.make(s.panelSize, cn);
			}
		}
		
		s.parallelMult(wk1, wk2);
	}
	//----------------------------------------------------------
	/**
	 * SUMMA distributed dense matrix multiplication: C = A &#42 B<sup>T</sup> + beta * C
	 * 
	 * @param ps       panel size
	 * @param beta     scaling factor for output matrix
	 * @param A        first input distributed dense matrix in multiplication
	 * @param B        second input distributed dense matrix which is used in tranposed form 
	 * @param C        the input/result distributed dense matrix
	 */
	public static def multTrans(
			var ps:Int,  /* Panel size*/
			beta:Double, 
			A:DistDenseMatrix, 
			B:DistDenseMatrix, 
			C:DistDenseMatrix) {
		if (ps < 1) ps = estPanelSize(A, B);
		
		val s = new SummaDense(ps, beta, A, B, C);
		val wk1:DistArray[DenseMatrix](1) = DistArray.make[DenseMatrix](C.dist);
		val wk2:DistArray[DenseMatrix](1) = DistArray.make[DenseMatrix](C.dist);
		val tmp:DistArray[DenseMatrix](1) = DistArray.make[DenseMatrix](C.dist);

		finish for (val [p]:Point in C.dist) {
			val rn = A.grid.getRowSize(p);
			val cn = B.grid.getColSize(p);
			async at (C.dist(p)){
				wk1(here.id()) = DenseMatrix.make(rn, s.panelSize);
				wk2(here.id()) = DenseMatrix.make(s.panelSize, cn);
				tmp(here.id()) = DenseMatrix.make(rn, s.panelSize);
			}
		}
		s.parallelMultTrans(wk1, wk2, tmp);
	}
	//=====================================================================
	//=====================================================================
	//
	/**
	 * Distributed matrix multiplication using SUMMA alogrithm
	 * 
	 * @param work1		temporary space used for ring cast each row blocks
	 * @param work2 	temporary space used for ring cast each column blocks
	 */
	public def parallelMult(
			work1:DistArray[DenseMatrix](1), 
			work2:DistArray[DenseMatrix](1)) {
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
		if (MathTool.isZero(beta)) C.scale(0.0);
		for (var kk:Int=0; kk<K; kk+=iwrk) {
			iwrk = Math.min(panelSize, B.grid.rowBs(itRow)-ii);
			iwrk = Math.min(iwrk,      A.grid.colBs(itCol)-jj); 
			val klen = iwrk;

			//Debug.flushln("Root place starts iteration "+kk+" panel size:"+klen); 
			//
			//-------------------------------------------------------------------
			//Packing columns and rows and broadcast to same row and column block
			/* TIMING */ st = Timer.milliTime();
			ringCastRowBs(jj, iwrk, itCol, A, rowBsPsMap, work1);
			ringCastColBs(ii, iwrk, itRow, B, colBsPsMap, work2);
			/* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st;
			//Debug.flushln("Row and column blocks bcast ends");
			
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
					val ma = new DenseMatrix(wk1.M, klen, wk1.d);// as DenseMatrix(br, klen);
					val mb = new DenseMatrix(klen, wk2.N, wk2.d);// as DenseMatrix(klen, bc);
					val bc = C.local() as DenseMatrix(wk1.M, wk2.N);//distBs(pid);
					//ma.print("Place "+mypid+" panel a");
					//mb.print("Place "+mypid+" panel b");
					bc.mult(ma, mb, true);
					//bc.print("Place "+mypid+" panel a*b");	
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
	/**
	 * Broadcast data within the row blocks, for all block rows in partitioning.
	 * 
	 * 
	 * @param jj 		the starting index of columns in block
	 * @param klen 		number of column count
	 * @param itCol 	the root's column block id
	 * @param dA  		the data source from distributed dense matrix
	 * @param mapBsPs 	mapping of block ids to place ids in the same row blocks
	 * @param work1 	the output data in distributed array 
	 */
	protected static def ringCastRowBs(
			jj:Int, klen:Int, itCol:Int, 
			dA:DistDenseMatrix, 
			mapBsPs:DistArray[Array[Int](1)](1),
			work1:DistArray[DenseMatrix](1)): void {
		
		finish for (var p:Int=0; p<Place.MAX_PLACES; p++) { 
			val pid = p;
			val myColId = dA.grid.getColBlockId(pid);
			val myRowId = dA.grid.getRowBlockId(pid);
			if (myColId == itCol) {
				async at (dA.distBs.dist(pid)) {
					val mypid  = here.id();
					val srcden = work1(mypid);
					val datcnt = dA.distBs(mypid).copyCols(jj, klen, srcden);
					//srcden.print("Row block cast src at place"+mypid+" to places"+mapBsPs(mypid).toString());
					dA.comm.rcastOp.rcast(work1, datcnt, mapBsPs(mypid));
				}
			}
		}
	}
	
	/**
	 * Broadcast data within the column blocks, for all block columns in partitoning.
	 * 
	 * @param ii 		the starting index of row in block
	 * @param klen 		number of row count
	 * @param itRow 	the root's row block id
	 * @param dA  		the data source from distributed dense matrix
	 * @param mapBsPs 	mapping of block ids to place ids in the same column blocks
	 * @param work2   	the output data in distributed array 
	 */	
	protected static def ringCastColBs(
			ii:Int, klen:Int, itRow:Int, 
			dB:DistDenseMatrix,
			mapBsPs:DistArray[Array[Int](1)](1),
			work2:DistArray[DenseMatrix](1)):void {
		
		finish for (var p:Int=0; p<Place.MAX_PLACES; p++) { 
			val pid = p;
			val myColId = dB.grid.getColBlockId(pid);
			val myRowId = dB.grid.getRowBlockId(pid);
			// Broadcast to all column blocks
			if (myRowId == itRow) {
				async at (dB.distBs.dist(pid)) {
					val mypid = here.id();
					val orgden = work2(mypid);
					val srcden = new DenseMatrix(klen, orgden.N, orgden.d);
					val datcnt = dB.distBs(mypid).copyRows(ii, klen, srcden);
					//srcden.print("Col bcast src:");
					dB.comm.rcastOp.rcast(work2, datcnt, mapBsPs(mypid));
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
	/**
	 * Distributed matrix multiplication with second matrix transposed
	 * 
	 * @param work1 	temporary space used in ring cast each row blocks
	 * @param work2 	temporary space used in ring cast each column blocks
	 * @param tmpwk 	temporary space used in reduce operation
	 */
	public def parallelMultTrans(
			work1:DistArray[DenseMatrix](1), 
			work2:DistArray[DenseMatrix](1),
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
		Debug.assure(A.N==B.N&&C.M==A.M&&C.N==B.M);
		
		//Scaling the matrixesx
		/* TIMING */ st = Timer.milliTime();
		if (MathTool.isZero(beta))  C.scale(0.0D);
		/* TIMING */ C.distBs(here.id()).calcTime += Timer.milliTime() - st;

		//Loops through all columns in C
		for (var kk:Int=0; kk<K; kk+=iwrk) {
			//
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
				
				val mypid = here.id();
				val ma = new DenseMatrix(work1(mypid).M, klen, work1(mypid).d);
				val mb = new DenseMatrix(klen, work2(mypid).N, work2(mypid).d);
				val bA = A.local() as DenseMatrix(ma.M,mb.N);//distBs(pid);
				
				ma.multTrans(bA, mb, false);
				//ma.debugPrint("Place"+mypid+" multTrans result");
			}
			/* TIMING */ C.distBs(here.id()).calcTime += Timer.milliTime() - st; 
			//Debug.flushln("Panel matrix mult done");
			   
			//---------------------------------------------------------------------------
			// Perform a ring broadcast reduce sum operation
			/* TIMING */ st = Timer.milliTime();
			reduceSumRowBs(jj, klen, itCol, C, rowBsPsMap, work1, tmpwk);
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
	//
	//---------------------------------------------------------
	/**
	 * Reduce on work1 in all row blocks and add the result to output matrix dC at
	 * specified offset.
	 */
	protected static def reduceSumRowBs(
			jj:Int, klen:Int, itCol:Int,
			dC:DistDenseMatrix,
			mapBsPs:DistArray[Array[Int](1)](1),
			work1:DistArray[DenseMatrix](1),
			tmpwk:DistArray[DenseMatrix](1)): void {
		
		finish for (var p:Int=0; p<Place.MAX_PLACES; p++) {
			val pid = p;
			val myRowId  = dC.grid.getRowBlockId(p);
			val root_pid = dC.grid.getBlockId(myRowId, itCol);
			if (pid == root_pid) {
				async at (dC.distBs.dist(pid)) {
					val mypid = here.id();
					val tmp   = tmpwk(mypid);
					val plst  = mapBsPs(mypid);
					//work1(mypid).print("Work1 P"+mypid+"add "+plst.toString());
					dC.comm.reduceOp.reduceSum(work1, tmp, klen, plst);
					dC.distBs(here.id()).addCols(jj, klen, work1(mypid));
				}
			}
		}
	}

}