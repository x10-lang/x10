/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package x10.matrix.comm;

import x10.regionarray.DistArray;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.sparse.SparseCSC;

import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;

/**
 * This class provide scatter communication for distributed matrix. 
 * 
 * <p> This operation allows a) scattering array of matrix blocks to distributed
 * matrix blocks, b) scattering single column matrix (vector) to distributed matrix blocks, 
 * and c) scattering matrix to distributed blocks which has single-row block partitioning.
 *
 * <p> The target data lives on DistArray of dense blocks or sparse blocks,
 * depending on the source matrix types.
 *
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class MatrixScatter {
	/**
	 * Scatter data from matrix blocks from here to distributed blocks in all places.
	 * A local block in the array is sent to a distributed block, which is corresponding
	 * to its distributed array
	 *
	 * @param src     source matrix block array.
	 * @param dst     target distributed matrix blocks 
	 */
	public static def scatter(
			src:Rail[DenseBlock], 
			dst:DistArray[DenseBlock](1)) : void {
		
		val nb = dst.region.size();
		assert(nb==src.size) :
			"Number blocks in dist and local array mismatch";
		
		finish for (var bid:Long=0; bid<nb; bid++) {
			val srcden = src(bid).getMatrix();
			MatrixRemoteCopy.copy(srcden, 0, dst, bid, 0, srcden.N);
		}
	}

	/**
	 * Scatter data of dense matrix in a continuous memory space into distributed 
	 * dense blocks of all places.
	 * <p> This method supports two types of dense matrix: 1) single column (which also
	 * single column block partitioning), and 2) single row block partitioning.
	 * Such restriction allows matrix data stored its continuous memory space to be 
	 * transferred to its destination place.
	 *
	 * @param gp			matrix partitioning
	 * @param src			source matrix for scattering
	 * @param dst			distributed dense blocks to store the scattering results
	 */
	public static def scatterRowBs(
			gp:Grid, 
			src:DenseMatrix, 
			dst:DistArray[DenseBlock](1)) : void {

        assert (gp.numRowBlocks==1L ||gp.N==1L) :
			"Number of row block in partition must be 1 or matrix is a vector";

		if (gp.N==1L)
			x10ScatterVector(gp as Grid{gp.N==1L}, src.d, dst);
		else
			x10ScatterRowBs(gp, src, dst);
	}

	/**
	 * Scatter dense matrix at here to distributed dense blocks, partitioned 
	 * in single row blocks.
	 * 
	 * @param gp         single row block partitioning
	 * @param srcden     source dense matrix.
	 * @param dst        target distributed dense block matrix 
	 */
	public static def x10ScatterRowBs(
			gp:Grid, 
			srcden:DenseMatrix,
			dst:DistArray[DenseBlock](1)): void {

		val root = here.id();
		var coloff:Long=0;
		for (var cb:Long=0; cb<gp.numColBlocks; cb++) {

			val colcnt = gp.colBs(cb);
			
			if (cb != root) {
				MatrixRemoteCopy.x10Copy(srcden, coloff, dst, cb, 0L, colcnt); 

			} else {
				//Make local copying
				val dstden = dst(root).getMatrix();
				DenseMatrix.copyCols(srcden, coloff, dstden, 0, colcnt);
			}
			coloff += colcnt;
		}
	}

	/**
	 * Scatter 1-column dense matrix (vector) to distributed dense blocks.
	 */
	public static def x10ScatterVector(
			gp:Grid{self.N==1L}, 
			src:Rail[ElemType],	
			dst:DistArray[DenseBlock](1)): void {

		val root = here.id();
		var rowoff:Long=0;
		for (var rb:Long=0; rb<gp.numRowBlocks; rb++) {

			val rowcnt = gp.rowBs(rb);
			
			if (rb != root) {
				MatrixRemoteCopy.x10Copy(src, rowoff, dst, rb, 0L, rowcnt); 

			} else {
				//Make local copying
				val dstden = dst(root).getMatrix();
				Rail.copy(src, rowoff, dstden.d, 0L, rowcnt);
			}
			rowoff += rowcnt;
		}
	}

	/**
	 * Scatter data from sparse blocks at here to distributed sparse blocks
	 *
	 * @param src     source sparse matrix blocks
	 * @param dst     target distributed sparse matrix block array 
	 */
	public static def scatter(
			src:Rail[SparseBlock], 
			dst:DistArray[SparseBlock](1)) : void {
		
		val nb = dst.region.size();

		assert (nb==src.size) :
			"Number blocks in dist and local array mismatch";

		finish for (var bid:Long=0; bid<nb; bid++) {
			val srcspa = src(bid).getMatrix();
			MatrixRemoteCopy.copy(srcspa, 0L, dst, bid, 0L, srcspa.N);
		}
	}

	/**
	 * Scatter single-row partitioning blocks in all places to a 
	 * dense matrix at here
	 */
	public static def scatterRowBs(
			gp:Grid, 
			src:SparseCSC, 
			dst:DistArray[SparseBlock](1)) : void {

		assert (gp.numRowBlocks==1L || gp.N==1L) :
					 "Number of row block in partition must be 1, or matrix is a vector";
		// Test sparse block storage 
		x10ScatterRowBs(gp, src, dst);
	}

    protected static def compNonZeroBs(gp:Grid, src:SparseCSC):Rail[Long] {
        val nzl = new Rail[Long](gp.size);
        var sttcol:Long = 0L;
		var colcnt:Long = gp.getColSize(0);
		for (var cb:Long=0; cb<nzl.size; cb++) {
			colcnt = gp.getColSize(cb);
			nzl(cb) = src.countNonZero(sttcol, colcnt);
			sttcol+=colcnt;
		}
		return nzl;

	}

	/**
	 * Scatter blocks in row-wise.
	 */
	protected static def x10ScatterRowBs(gp:Grid, 
										 srcspa:SparseCSC,
										 dst:DistArray[SparseBlock](1)): void {

		val root = here.id();
		var coloff:Long = 0;
		for (var cb:Long=0; cb<gp.numColBlocks; cb++) {
			val colcnt = gp.colBs(cb);

			if (cb != root) {
				MatrixRemoteCopy.x10Copy(srcspa, coloff, dst, cb, 0L, colcnt);
			} else {
				//Make local copying
				val dstspa = dst(root).getMatrix();
				SparseCSC.copyCols(srcspa, coloff, dstspa, 0, colcnt);
			}
			coloff += colcnt;
		}
	}
}
