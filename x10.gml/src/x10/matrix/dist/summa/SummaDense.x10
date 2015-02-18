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

import x10.matrix.util.MathTool;
import x10.matrix.util.Debug;

import x10.matrix.comm.MatrixReduce;
import x10.matrix.comm.MatrixRingCast;

import x10.matrix.dist.DistDenseMatrix;

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
    //val alpha:ElemType;
    val beta:ElemType;
    val panelSize:Long;
    val A:DistDenseMatrix;
    val B:DistDenseMatrix;
    val C:DistDenseMatrix;
    val rowBsPsMap:DistArray[Rail[Long]](1);
    val colBsPsMap:DistArray[Rail[Long]](1);

    public def this(
            ps:Long, be:ElemType,
            a:DistDenseMatrix, 
            b:DistDenseMatrix, 
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

    /**
     * Estimate the panel size.
     */
    public static def estPanelSize(dA:DistDenseMatrix, dB:DistDenseMatrix):Long {
        val maxps = Math.min(dA.grid.colBs(0), dB.grid.rowBs(0));
        val estps:Long = 128;//estCommuDataSize/ldm;
        if (estps < 1L) return 1;
        if (estps > maxps) return maxps;
        return Math.max(estps,1L);
    }
    
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
            var ps:Long,
            beta:ElemType, 
            A:DistDenseMatrix, 
            B:DistDenseMatrix, 
            C:DistDenseMatrix) {

        var ret:Boolean = true;        
        ret &= A.grid.numRowBlocks == B.grid.numRowBlocks;
        ret &= A.grid.numRowBlocks == C.grid.numRowBlocks;
        ret &= A.grid.numColBlocks == B.grid.numColBlocks;
        ret &= A.grid.numColBlocks == C.grid.numColBlocks;
                Debug.assure(ret,"Grids mismatched! (#Blocks)");
        
        for (var rb:Long=0; rb<C.grid.numRowBlocks && ret; rb++) 
            ret &= A.grid.rowBs(rb)==C.grid.rowBs(rb);
                Debug.assure(ret,"Grids mismatched! (A row C row)");    
            
        for (var cb:Long=0; cb<C.grid.numColBlocks && ret; cb++) 
            ret &= B.grid.colBs(cb) == C.grid.colBs(cb);
                Debug.assure(ret,"Grids mismatched! (B col C col)");

        if (ps < 1) ps = estPanelSize(A, B);
        val s = new SummaDense(ps, beta, A, B, C);

        val wk1 = DistArray.make[DenseMatrix](C.dist);
        val wk2 = DistArray.make[DenseMatrix](C.dist);
        finish for ([p] in C.dist) {
            val rn = A.grid.getRowSize(p);
            val cn = B.grid.getColSize(p);
            at(C.dist(p)) async {
                wk1(here.id()) = DenseMatrix.make(rn, s.panelSize);
                wk2(here.id()) = DenseMatrix.make(s.panelSize, cn);
            }
        }
        
        s.parallelMult(wk1, wk2);
    }

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
            var ps:Long,
            beta:ElemType, 
            A:DistDenseMatrix, 
            B:DistDenseMatrix, 
            C:DistDenseMatrix) {

        var ret:Boolean = true;        
        ret &= A.grid.numRowBlocks == B.grid.numRowBlocks;
        ret &= A.grid.numRowBlocks == C.grid.numRowBlocks;
        ret &= A.grid.numColBlocks == B.grid.numColBlocks;
        ret &= A.grid.numColBlocks == C.grid.numColBlocks;
                Debug.assure(ret,"Grids mismatched! (#Blocks)");
        
        for (var rb:Long=0; rb<C.grid.numRowBlocks && ret; rb++) 
            ret &= A.grid.rowBs(rb)==C.grid.rowBs(rb);    
                Debug.assure(ret,"Grids mismatched! (A row C row)");    

        for (var cb:Long=0; cb<A.grid.numColBlocks && ret; cb++) 
            ret &= A.grid.colBs(cb) == B.grid.colBs(cb);
                Debug.assure(ret,"Grids mismatched! (A col B col)");

        if (ps < 1) ps = estPanelSize(A, B);
        
        val s = new SummaDense(ps, beta, A, B, C);
        val wk1 = DistArray.make[DenseMatrix](C.dist);
        val wk2 = DistArray.make[DenseMatrix](C.dist);
        val tmp = DistArray.make[DenseMatrix](C.dist);

        finish for ([p] in C.dist) {
            val rn = A.grid.getRowSize(p);
            val cn = B.grid.getColSize(p);
            at(C.dist(p)) async {
                wk1(here.id()) = DenseMatrix.make(rn, s.panelSize);
                wk2(here.id()) = DenseMatrix.make(s.panelSize, cn);
                tmp(here.id()) = DenseMatrix.make(rn, s.panelSize);
            }
        }
        s.parallelMultTrans(wk1, wk2, tmp);
    }

    /**
     * SUMMA distributed dense matrix multiplication: C = A<sup>T</sup> &#42 B + beta * C
     * 
     * @param ps       panel size
     * @param beta     scaling factor for output matrix
     * @param A        first input distributed dense matrix which is used in tranposed form
     * @param B        second input distributed dense matrix
     * @param C        the input/result distributed dense matrix
     */
    public static def transMult(
            var ps:Long,
            beta:ElemType,
            A:DistDenseMatrix,
            B:DistDenseMatrix,
            C:DistDenseMatrix) {

        var ret:Boolean = true;        
        ret &= A.grid.numRowBlocks == B.grid.numRowBlocks;
        ret &= A.grid.numRowBlocks == C.grid.numRowBlocks;
        ret &= A.grid.numColBlocks == B.grid.numColBlocks;
        ret &= A.grid.numColBlocks == C.grid.numColBlocks;
                Debug.assure(ret,"Grids mismatched! (#Blocks)");
        
        for (var rb:Long=0; rb<C.grid.numColBlocks && ret; rb++) 
            ret &= B.grid.colBs(rb)==C.grid.colBs(rb);    
                Debug.assure(ret,"Grids mismatched! (B col C col)");
    
        for (var cb:Long=0; cb<A.grid.numRowBlocks && ret; cb++) 
            ret &= A.grid.rowBs(cb) == B.grid.rowBs(cb);
                Debug.assure(ret,"Grids mismatched! (B col A col)");

                Debug.assure(ret,"Grids mismatched!");
                       
        if (ps < 1) ps = estPanelSize(A, B);
        
        val s = new SummaDense(ps, beta, A, B, C);
        val wk1 = DistArray.make[DenseMatrix](C.dist);
        val wk2 = DistArray.make[DenseMatrix](C.dist);
        val tmp = DistArray.make[DenseMatrix](C.dist);

        finish for ([p] in C.dist) {
            val rn = A.grid.getRowSize(p);
            val cn = B.grid.getColSize(p);
            at (C.dist(p)) async {
                wk1(here.id()) = DenseMatrix.make(rn, s.panelSize);
                wk2(here.id()) = DenseMatrix.make(s.panelSize, cn);
                tmp(here.id()) = DenseMatrix.make(s.panelSize, cn); // see reduceSumColBS
            }
        }
        s.parallelTransMult(wk1, wk2, tmp);
    }

    /**
     * Distributed matrix multiplication using SUMMA alogrithm
     * 
     * @param work1        temporary space used for ring cast each row blocks
     * @param work2     temporary space used for ring cast each column blocks
     */
    public def parallelMult(
            work1:DistArray[DenseMatrix](1), 
            work2:DistArray[DenseMatrix](1)) {
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
            ringCastRowBs(jj, iwrk, itCol, A, rowBsPsMap, work1);
            ringCastColBs(ii, iwrk, itRow, B, colBsPsMap, work2);
            /* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st;
            //Debug.flushln("Row and column blocks bcast ends");
            
            /* TIMING */ st = Timer.milliTime();
            finish ateach([p] in C.distBs.dist) { 
                /* update local block */
                val mypid = here.id();
                val wk1 = work1(mypid);
                val wk2 = work2(mypid);
                val ma = new DenseMatrix(wk1.M, klen, wk1.d);// as DenseMatrix(br, klen);
                val mb = new DenseMatrix(klen, wk2.N, wk2.d);// as DenseMatrix(klen, bc);
                val bc = C.local() as DenseMatrix(wk1.M, wk2.N);//distBs(pid);
                if (bc.M!=0L && bc.N!=0L && klen!=0L) bc.mult(ma, mb, true);
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

    /**
     * Broadcast data within the row blocks, for all block rows in partitioning.
     * 
     * 
     * @param jj         the starting index of columns in block
     * @param klen         number of column count
     * @param itCol     the root's column block id
     * @param dA          the data source from distributed dense matrix
     * @param mapBsPs     mapping of block ids to place ids in the same row blocks
     * @param work1     the output data in distributed array 
     */
    protected static def ringCastRowBs(
            jj:Long, klen:Long, itCol:Long, 
            dA:DistDenseMatrix, 
            mapBsPs:DistArray[Rail[Long]](1),
            work1:DistArray[DenseMatrix](1)): void {
        
        finish for (var p:Long=0; p<Place.numPlaces(); p++) { 
            val myColId = dA.grid.getColBlockId(p);
            if (myColId == itCol) {
                at(dA.distBs.dist(p)) async {
                    val mypid  = here.id();
                    val srcden = work1(mypid);
                    val datcnt = dA.distBs(mypid).copyCols(jj, klen, srcden);
                    MatrixRingCast.rcast(work1, datcnt, mapBsPs(mypid));
                }
            }
        }
    }
    
    /**
     * Broadcast data within the column blocks, for all block columns in partitoning.
     * 
     * @param ii         the starting index of row in block
     * @param klen         number of row count
     * @param itRow     the root's row block id
     * @param dA          the data source from distributed dense matrix
     * @param mapBsPs     mapping of block ids to place ids in the same column blocks
     * @param work2       the output data in distributed array 
     */    
    protected static def ringCastColBs(
            ii:Long, klen:Long, itRow:Long, 
            dB:DistDenseMatrix,
            mapBsPs:DistArray[Rail[Long]](1),
            work2:DistArray[DenseMatrix](1)):void {
        
        finish for (var p:Long=0; p<Place.numPlaces(); p++) { 
            val myRowId = dB.grid.getRowBlockId(p);
            // Broadcast to all column blocks
            if (myRowId == itRow) {
                at(dB.distBs.dist(p)) async {
                    val mypid = here.id();
                    val orgden = work2(mypid);
                    val srcden = new DenseMatrix(klen, orgden.N, orgden.d);
                    val datcnt = dB.distBs(mypid).copyRows(ii, klen, srcden);
                    MatrixRingCast.rcast(work2, datcnt, mapBsPs(mypid));
                }
            }
        }
    }

    /**
     * Distributed matrix multiplication with second matrix transposed
     * SUMMA transpose-B method
     * 
     * @param work1     temporary space used in ring cast each row blocks
     * @param work2     temporary space used in ring cast each column blocks
     * @param tmpwk     temporary space used in reduce operation
     */
    public def parallelMultTrans(
            work1:DistArray[DenseMatrix](1), 
            work2:DistArray[DenseMatrix](1),
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

        //Loops through all columns in C
        for (var kk:Long=0; kk<K; kk+=iwrk) {
            //Debug.flushln("Iteration start at "+kk+" itRow:"+itRow+
            //              " itCol:"+itCol+" ii:"+ii+" jj:"+jj);
            iwrk = Math.min(panelSize, C.grid.colBs(itCol)-jj);
            iwrk = Math.min(iwrk,      B.grid.rowBs(itRow)-ii); 
            val klen = iwrk;
            //Debug.flushln("Iteration start at "+kk+" panel size:"+
            //                klen+" jj:"+jj+" A block col:"+C.grid.colBs(itCol));
            //Packing columns and rows for broadcast
            /* TIMING */ st = Timer.milliTime();
            ringCastColBs(ii, klen, itRow, B, colBsPsMap, work2);
            /* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st; 
            //Debug.flushln("Column blocks bcast done");
            
            // Perform block matrix multiply in all places
            /* TIMING */ st = Timer.milliTime();
            finish ateach([p] in C.dist) { 
                val mypid = here.id();
                val ma = new DenseMatrix(work1(mypid).M, klen, work1(mypid).d);
                val mb = new DenseMatrix(klen, work2(mypid).N, work2(mypid).d);
                val bA = A.local() as DenseMatrix(ma.M,mb.N);//distBs(pid);
                
                ma.multTrans(bA, mb, false);
            }
            /* TIMING */ C.distBs(here.id()).calcTime += Timer.milliTime() - st; 
            //Debug.flushln("Panel matrix mult done");
               
            // Perform a ring broadcast reduce sum operation
            /* TIMING */ st = Timer.milliTime();
            if (klen>0) reduceSumRowBs(jj, klen, itCol, C, rowBsPsMap, work1, tmpwk);
            /* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st; 

            //Debug.flushln("Row block reduce and result updated");
            /* update icurcol, icurrow, ii, jj */
            ii += iwrk;
            jj += iwrk;
            if ( jj>=C.grid.colBs(itCol)) { itCol++; jj = 0; };
            if ( ii>=B.grid.rowBs(itRow)) { itRow++; ii = 0; };
        }
    }

    /**
     * Reduce on work1 in all row blocks and add the result to output matrix dC at
     * specified offset.
     */
    protected static def reduceSumRowBs(
            jj:Long, klen:Long, itCol:Long,
            dC:DistDenseMatrix,
            mapBsPs:DistArray[Rail[Long]](1),
            work1:DistArray[DenseMatrix](1),
            tmpwk:DistArray[DenseMatrix](1)): void {
        
        finish for (var p:Long=0; p<Place.numPlaces(); p++) {
            val myRowId  = dC.grid.getRowBlockId(p);
            val root_pid = dC.grid.getBlockId(myRowId, itCol);
            if (p == root_pid) {
                at(dC.distBs.dist(p)) async {
                    val mypid = here.id();
                    val tmp   = tmpwk(mypid);
                    val plst  = mapBsPs(mypid);
                    MatrixReduce.reduceSum(work1, tmp, klen, plst);
                    dC.distBs(here.id()).addCols(jj, klen, work1(mypid));
                }
            }
        }
    }


    // Additional SUMMA routine for C = A^T x B
    public def parallelTransMult(
            work1:DistArray[DenseMatrix](1), 
            work2:DistArray[DenseMatrix](1),
            tmpwk:DistArray[DenseMatrix](1)) {
        
		val K = A.N; // Loop through all rows in C
		var itRow:Long = 0;//Current processing iteration
		var itCol:Long = 0; 
		var iwrk:Long = 0;
		var ii:Long = 0;
		var jj:Long = 0;
		var st:Long = 0;

		Debug.assure(A.M==B.M && C.M==A.N && C.N==B.N);
		
		/* TIMING */ st = Timer.milliTime();
        C.scale(beta);
		/* TIMING */ C.distBs(here.id()).calcTime += Timer.milliTime() - st;

		//Loops through all rows in C
		for (var kk:Long=0; kk<K; kk+=iwrk) {
			iwrk = Math.min(panelSize, C.grid.rowBs(itRow)-ii); // see if statements	
			iwrk = Math.min(iwrk,      A.grid.colBs(itCol)-jj); // at the end of this procedure	
			val klen = iwrk;
			//Packing columns and rows for broadcast
			/* TIMING */ st = Timer.milliTime();
            ringCastRowBs(jj, klen, itCol, A, rowBsPsMap, work1); // check			
			/* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st; 
			//Debug.flushln("Row blocks bcast done");
			
			// Perform block matrix multiply in all places
			/* TIMING */ st = Timer.milliTime();
			finish ateach ([p] in C.dist) { 
				val mypid = here.id();
				val mc = new DenseMatrix(klen, work2(mypid).N, work2(mypid).d); //check
				val ma = new DenseMatrix(work1(mypid).M, klen, work1(mypid).d); //check
				val bB = B.local() as DenseMatrix(ma.M, mc.N); //check
				
				if (mc.M!=0L && mc.N!=0L && ma.M!=0L) mc.transMult(ma, bB, false);
			}
			/* TIMING */ C.distBs(here.id()).calcTime += Timer.milliTime() - st; 
			   
			// Perform a ring broadcast reduce sum operation
			/* TIMING */ st = Timer.milliTime();
			if (klen>0) reduceSumColBs(ii, klen, itRow, C, colBsPsMap, work2, tmpwk); // check
			/* TIMING */ C.distBs(here.id()).commTime += Timer.milliTime() - st; 

			/* update icurcol, icurrow, ii, jj */
			ii += iwrk;
			jj += iwrk;
			if ( jj>=A.grid.colBs(itCol)) { itCol++; jj = 0; };
			if ( ii>=C.grid.rowBs(itRow)) { itRow++; ii = 0; };
		}
    }

    protected static def reduceSumColBs(
			ii:Long, klen:Long, itRow:Long,
			dC:DistDenseMatrix,
			mapBsPs:DistArray[Rail[Long]](1),
			work2:DistArray[DenseMatrix](1),
			tmpwk:DistArray[DenseMatrix](1)): void {
		
		finish for (var p:Long=0; p<Place.numPlaces(); p++) {
			val pid = p;
			val myColId  = dC.grid.getColBlockId(p);
			val root_pid = dC.grid.getBlockId(itRow, myColId);
			if (pid == root_pid) {
				at (dC.distBs.dist(pid)) async {
					val mypid = here.id();
					val tmp   = tmpwk(mypid);
					val plst  = mapBsPs(mypid);
					MatrixReduce.reduceSum(work2, tmp, work2(mypid).N, plst);
                    // This is number of columns see ArrayReduce.x10 and ArrayRemoteCopy.x10
					val orgden = work2(mypid);
					val srcden = new DenseMatrix(klen, orgden.N, orgden.d);
					dC.distBs(here.id()).addRows(ii, klen, srcden);
				}
			}
		}
    }
}
