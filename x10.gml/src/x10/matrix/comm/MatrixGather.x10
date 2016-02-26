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
 * This class provide gather communication for distributed matrix.
 * 
 * <p> This operation allows a) gathering distributed matrix blocks to
 * an array of blocks at here, b) gathering distributed single column blocks
 * (vector) in all places to single-column matrix at here,   
 * and c) gathering distributed blocks which is in single-row block partitioning 
 * to matrix at here.
 *
 * <p> The source data lives on DistArray of dense blocks or sparse blocks, 
 * and the gathering destination is array of matrix dense/sparse blocks or 
 * a dense/sparse matrix.
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class MatrixGather {
    /**
     * Gather data from distributed matrix blocks in all places to the dense
     * block matrix at here.
	 *
	 * @param src     source distributed matrix blocks
	 * @param dst     target matrix block Rail 
	 */
	public static def gather(
			src:DistArray[DenseBlock](1), 
			dst:Rail[DenseBlock]) : void {
		
		val nb = src.region.size();
		assert (nb==dst.size) :
			"Number blocks in dist and local array not match";
		
		finish for (var bid:Long=0; bid<nb; bid++) {
			val dstden = dst(bid).getMatrix();
				
			if (bid == here.id()) {
				val srcden = src(bid).getMatrix();
                assert (srcden.M==dstden.M) :
                    "source and target matrix have different leading dimension";
				DenseMatrix.copyCols(srcden, 0, dstden, 0, srcden.N);

			} else {
				MatrixRemoteCopy.x10Copy(src, bid, 0, dstden, 0, dstden.N);
			}
		}
	}

	/**
	 * Gather single-row partitioning blocks in all places to a 
	 * dense matrix at here
	 */
	public static def gatherRowBs(
			gp:Grid, 
			src:DistArray[DenseBlock](1), 
			dst:DenseMatrix) : void {

		assert (gp.numRowBlocks==1L || gp.N==1L) :
			"Number of row block in partition must be 1 or matrix is a vector";

		if (gp.N==1L)
			x10GatherVector(gp as Grid{gp.N==1L}, src, dst.d);
		else
			x10GatherRowBs(gp, src, dst);
	}

	/**
	 * Gather blocks in row-wise.
	 */
	protected static def x10GatherRowBs_copyto(
			gp:Grid, 
			src:DistArray[DenseBlock](1),
			dstden:DenseMatrix): void {

		val root = here.id();
		val dstbuf = new GlobalRail[ElemType](dstden.d as Rail[ElemType]{self!=null});

		var startoffset:Long = 0;
		for (var cb:Long=0; cb<gp.numColBlocks; cb++) {
			val pid = cb;			   
			if (pid != root) {
				//Copy remote to here copying, Go to remote and copy to here
				val startrcvoff = startoffset;
				at(src.dist(pid)) async {
					val srcden = src(here.id()).dense;
					Rail.asyncCopy[ElemType](srcden.d, 0, dstbuf, startrcvoff, 
											srcden.M*srcden.N);
				} 
			} else {
				//Make local copying
				var rcvoff:Long = startoffset;
				val srcden = src(pid).dense;
				Rail.copy[ElemType](srcden.d, 0, dstden.d, rcvoff, srcden.M*srcden.N);
			}
			startoffset += gp.rowBs(0) * gp.colBs(pid);
		}
	}

	/**
	 * Copy distributed dense matrix blocks from all places to the dense matrix
	 * at here.
	 * 
	 * @param gp     single row block partitioning
	 * @param src     source matrix, distributed in all places.
	 * @param dstden     target dense matrix at here
	 */
	public static def x10GatherRowBs(
			gp:Grid, 
			src:DistArray[DenseBlock](1),
			dstden:DenseMatrix): void {

		val root = here.id();
		var coloff:Long=0;
		for (var cb:Long=0; cb<gp.numColBlocks; cb++) {
			val colcnt = gp.colBs(cb);
			
			if (cb != root) {
				MatrixRemoteCopy.x10Copy(src, cb, 0, dstden, coloff, colcnt); 

			} else {
				//Make local copying
				val srcden = src(cb).getMatrix();
				DenseMatrix.copyCols(srcden, 0, dstden, coloff, colcnt);
			}
			coloff += colcnt;
		}
	}
	
	public static def x10GatherVector(
            gp:Grid{self.N==1L}, 
            src:DistArray[DenseBlock](1),
            dst:Rail[ElemType]):void {

		val root = here.id();
		var rowoff:Long=0;
		for (var rb:Long=0; rb<gp.numRowBlocks; rb++) {
			val rowcnt = gp.rowBs(rb);
			
			if (rb != root) {
				MatrixRemoteCopy.x10Copy(src, rb, 0, dst, rowoff, rowcnt); 

			} else {
				//Make local copying
				val srcden = src(rb).getMatrix();
				Rail.copy(srcden.d, 0L, dst, rowoff, rowcnt);
			}
			rowoff += rowcnt;
		}
	}

	/**
	 * Gather data from distributed sparse matrix blocks to the sparse blcok matrix
	 * at here.
	 *
	 * @param src     source distributed sparsematrix blocks
	 * @param dst     target sparse matrix block Rail 
	 */
	public static def gather(
			src:DistArray[SparseBlock](1), 
			dst:Rail[SparseBlock]) : void {
		
		val nb = src.region.size();
		var szlist:Rail[Long];

        assert (nb==dst.size) :
            "Number blocks in dist and local array mismatch";

		finish for (var bid:Long=0; bid<nb; bid++) {
			val dstspa = dst(bid).getMatrix();
			if (bid == here.id()) {
				val srcspa = src(bid).getMatrix();
				SparseCSC.copyCols(srcspa, 0, dstspa, 0, srcspa.N);

			} else {
				val colcnt = dstspa.N;
				MatrixRemoteCopy.x10Copy(src, bid, 0, dstspa, 0, colcnt);
			}
			
		}
	}

	/**
	 * Gather single-row partitioning blocks in all places to a 
	 * dense matrix at here
	 */
	public static def gatherRowBs(
			gp:Grid, 
			src:DistArray[SparseBlock](1), 
			dst:SparseCSC) : void {

		assert (gp.numRowBlocks==1L ||gp.N==1L) :
			"Number of row block in partition must be 1 or matrix is a vector";

		x10GatherRowBs(gp, src, dst);
	}

	/**
	 * Gather blocks in row-wise.
	 */
	protected static def x10GatherRowBs(
			gp:Grid, 
			src:DistArray[SparseBlock](1),
			dstspa:SparseCSC): void {

		val root = here.id();
		var coloff:Long = 0;

		for (var cb:Long=0; cb<gp.numColBlocks; cb++) {
			val colcnt = gp.colBs(cb);

			if (cb != root) {
				MatrixRemoteCopy.x10Copy(src, cb, 0, dstspa, coloff, colcnt);
			} else {
				//Make local copying
				val srcspa = src(cb).getMatrix();
				SparseCSC.copyCols(srcspa, 0, dstspa, coloff, colcnt);
			}
			coloff += colcnt;
		}
	}
}
