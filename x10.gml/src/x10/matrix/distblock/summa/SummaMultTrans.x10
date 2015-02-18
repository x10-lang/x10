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
import x10.matrix.ElemType;

import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;

import x10.matrix.sparse.SparseCSC;

import x10.matrix.block.Grid;

import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistBlockMatrix;

/**
 * SUMMA implementation on distributed block matrix
 */
public class SummaMultTrans {
    //val alpha:ElemType;
    val beta:ElemType;
    val panelSize:Long;
    val A:DistBlockMatrix;
    val B:DistBlockMatrix;
    val C:DistBlockMatrix;
    val work1:PlaceLocalHandle[BlockSet];
    val work2:PlaceLocalHandle[BlockSet];
    val temp:PlaceLocalHandle[BlockSet];
    
    public var commTime:Long=0;
    public var calcTime:Long=0;
    
    public def this(
		    ps:Long, be:ElemType,
		    a:DistBlockMatrix, 
		    b:DistBlockMatrix{self.N==a.N}, 
		    c:DistBlockMatrix(a.M,b.M),
		    w1:PlaceLocalHandle[BlockSet],
		    w2:PlaceLocalHandle[BlockSet],
		    w3:PlaceLocalHandle[BlockSet]) {
	//Check panelsize
	work1 = w1;
	work2 = w2;
	temp  = w3;
	
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
	if (estps > maxps)      estps = maxps;
	
	estps = Math.min(Math.min(estps, ga.getMinColSize()),
			 Math.min(estps, gb.getMinRowSize()));   
	
	return estps;
    }       
    
    public static def multTrans(    
				A:DistBlockMatrix, 
				B:DistBlockMatrix{self.N==A.N}, 
				C:DistBlockMatrix(A.M,B.M), plus:Boolean) {
	multTrans(10, (plus?1.0:0.0) as ElemType, A, B, C);
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
				A:DistBlockMatrix, 
				B:DistBlockMatrix{self.N==A.N}, 
				C:DistBlockMatrix(A.M,B.M)) {
	
	val pansz = estPanelSize(ps, A.getGrid(), B.getGrid());
	val w1 = A.makeTempFrontColDenseBlocks(pansz); //Must be dense block
	//or w1= C.makeTempFrontColBlocks(pansz); //Must be dense block
	val w2 = B.makeTempFrontRowBlocks(pansz); 
	val w3 = A.makeTempFrontColDenseBlocks(pansz); //Must be dense block
	//or w3= C.makeTempFrontColBlocks(pansz); //Must be dense block
	
	val s = new SummaMultTrans(pansz, beta, A, B, C, w1, w2, w3);
	
	s.parallelMultTrans();
    }       
    
    /**
     * Distributed matrix multiplication using SUMMA alogrithm
     * 
     * @param work1         temporary space used for ring cast each row blocks
     * @param work2         temporary space used for ring cast each column blocks
     */
    public def parallelMultTrans() {
	val K = B.M;
	var itRow:Int = 0n;
	var itCol:Int = 0n; //Current processing iteration
	var iwrk:Long = 0;
	var ii:Long = 0;
	var jj:Long = 0;
	var st:Long = 0;
	val gA = A.getGrid();
	val gB = B.getGrid();
	val gC = C.getGrid();
	
	//Scaling the matrixesx
	if (MathTool.isZero(beta)) C.reset();
	
	for (var kk:Long=0; kk<K; kk+=iwrk) {
	    //Debug.flushln("K="+kk+" itCol:"+itCol+" block N:"+gC.colBs.toString()+" idxjj:"+jj);
	    iwrk = Math.min(panelSize, gC.colBs(itCol)-jj);
	    iwrk = Math.min(iwrk,      gB.rowBs(itRow)-ii); 
	    val klen = iwrk;
	    
	    //Debug.flushln("Root place starts iteration "+kk+" panel size:"+klen); 
	    //Packing columns and rows and broadcast to same row and column block
	    /* TIMING */ 
	    st = Timer.milliTime();
	    AllGridCast.startColCast(ii, iwrk, itRow, B, work2);
	    /* TIMING */ 
	    commTime += Timer.milliTime() - st;
	    st = Timer.milliTime();
	    //Debug.flushln("Row and column blocks bcast ends");
	    
	    finish ateach(Dist.makeUnique()) {
		/* update local block */
		val mypid = here.id();
		val wk1 = work1();
		val wk2 = work2();
		val cbs = C.handleBS();
		val itr = cbs.iterator();
		wk1.reset();
		while (itr.hasNext()) {
		    val cblk = itr.next();
		    //val cmat = cblk.getMatrix();
		    val ablk = A.handleBS().find(cblk.myRowId, cblk.myColId);
		    val wblk = wk1.findFrontColBlock(cblk.myRowId); 
		    val bblk = wk2.findFrontRowBlock(cblk.myColId);
                    
		    val amat = ablk.getMatrix() as Matrix;
		    Debug.assure(bblk.getMatrix().N==amat.N, "Dimension mismatch in matrix multiply");
		    val bmat:Matrix;
		    if (bblk.isDense()) {
			bmat = new DenseMatrix(klen, amat.N, bblk.getData()) as Matrix;
		    } else {
			bmat = new SparseCSC(klen, amat.N, bblk.getCompressArray()) as Matrix;
		    }
		    //Debug.flushln("A block:"+amat);
		    //Debug.flushln("W2 block:"+bmat);
		    val wmat = new DenseMatrix(amat.M, klen, wblk.getData()) as Matrix(amat.M,klen);
		    /* TIMING */ 
		    val stt:Long = Timer.milliTime();
		    wmat.multTrans(amat, bmat as Matrix(klen, amat.N), true);
		    cblk.calcTime += Timer.milliTime() - stt;
		}
		
	    }
	    /* TIMING */ 
	    calcTime += Timer.milliTime() - st;
	    st = Timer.milliTime();
	    AllGridReduce.startRowReduceSum(jj, klen, itCol, C, work1, temp);
	    commTime += Timer.milliTime() - st;
	    
	    /* update icurcol, icurrow, ii, jj */
	    ii += iwrk;
	    jj += iwrk;
	    if ( jj>=gC.colBs(itCol)) { itCol++; jj = 0; };
	    if ( ii>=gB.rowBs(itRow)) { itRow++; ii = 0; };
	}
    }
}
