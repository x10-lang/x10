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

package x10.matrix.comm;

import x10.regionarray.DistArray;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.comm.mpi.WrapMPI;
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
 * <p> Two implementations are available. One uses MPI routines, and the other is based
 * on X10 remote array copy.
 * To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
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

				@Ifdef("MPI_COMMU") {
					MatrixRemoteCopy.mpiCopy(src, bid, 0, dstden, 0, dstden.N);
				}
				@Ifndef("MPI_COMMU") {
					MatrixRemoteCopy.x10Copy(src, bid, 0, dstden, 0, dstden.N);
				}
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

		@Ifdef("MPI_COMMU") {
			if (gp.N==1L)
				mpiGatherVector(gp as Grid{gp.N==1L}, src, dst.d);
			else
				mpiGatherRowBs(gp, src, dst);
		}
		@Ifndef("MPI_COMMU") {
			if (gp.N==1L)
				x10GatherVector(gp as Grid{gp.N==1L}, src, dst.d);
			else
				x10GatherRowBs(gp, src, dst);
		}
	}


	/**
	 * Gather matrix block from all places to dense matrix at haere
	 * by calling mpi gather routine.
	 *
	 * @param gp     single row block partitioning
	 * @param src     source distributed blocks dense matrix
	 * @param dstden     target dense matrix at here
	 */
	public static def mpiGatherRowBs(
			gp:Grid, 
			src:DistArray[DenseBlock](1),
			dstden:DenseMatrix): void {
		
		//Only one row block partition
		val szlist = new Rail[Long](gp.numColBlocks,
									(i:Long)=>gp.colBs(i)*gp.rowBs(0));
		val root = here.id();
		
		@Ifdef("MPI_COMMU") {
			finish 	{ 
				for([p] in src.dist) {
					val datcnt = szlist(p);
					if (p != root) {
						at(src.dist(p)) async {
							val srcden = src(here.id()).getMatrix();
							/*******************************************/
							// Not working
							//val tmpbuf= null; //fake
							//val tmplst=null;//   //fake
							/*******************************************/
							val tmpbuf = new Rail[ElemType](0); //fake
							val tmplst = new Rail[Long](0);   //fake
                            WrapMPI.world.gatherv(srcden.d, 0, datcnt, 
                                tmpbuf, 0, tmplst, root);
						}
					} 
				}

				async {
					/**********************************************/
					// DO NOT move this block into for loop block
					// MPI process will hang, Cause is not clear
					/**********************************************/	
					val srcden = src(root).getMatrix();
					WrapMPI.world.gatherv(srcden.d, 0, szlist(root), 
										  dstden.d, 0, szlist, root);
				}
			
			}
		}
	}
	
	/**
	 * Gather vector from distributed dense matrix (column=1)
	 */
	public static def mpiGatherVector(
			gp:Grid{self.N==1L}, 
			src:DistArray[DenseBlock](1),
			dst:Rail[ElemType]): void {
				
		//Only one row block partition
		val szlist = gp.rowBs;
		val root = here.id();

		@Ifdef("MPI_COMMU") {
			finish 	{
				for([p] in src.dist) {
					val datcnt = szlist(p);
					if (p != root) {
						at(src.dist(p)) async {
							val srcden = src(here.id()).getMatrix();
							/*******************************************/
							// Not working
							//val tmpbuf= null; //fake
							//val tmplst=null;//   //fake
							/*******************************************/
							val tmpbuf = new Rail[ElemType](0); //fake
							val tmplst = new Rail[Long](0);   //fake
							WrapMPI.world.gatherv(srcden.d, 0, datcnt, 
												  tmpbuf, 0, tmplst, root);
						}
					} 
				}

				async {
					/**********************************************/
					// DO NOT move this block into for loop block
					// MPI process will hang, Cause is not clear
					/**********************************************/	
					val srcden = src(root).getMatrix();
				
					WrapMPI.world.gatherv(srcden.d, 0, szlist(root), 
										  dst, 0, szlist, root);
				}
			}
		}
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

		@Ifdef("MPI_COMMU") {
			szlist = mpiGatherSize(src);
		}

		finish for (var bid:Long=0; bid<nb; bid++) {
			val dstspa = dst(bid).getMatrix();
			if (bid == here.id()) {
				val srcspa = src(bid).getMatrix();
				SparseCSC.copyCols(srcspa, 0, dstspa, 0, srcspa.N);

			} else {
				val colcnt = dstspa.N;
				@Ifdef("MPI_COMMU") {
					MatrixRemoteCopy.mpiCopy(src, bid, 0, dstspa, 0, colcnt);
						//mpiCopy(src, bid, dstspa, 0, colcnt, szlist(bid));
				}
				@Ifndef("MPI_COMMU") {
					MatrixRemoteCopy.x10Copy(src, bid, 0, dstspa, 0, colcnt);
						//x10Copy(src, bid, dstspa, 0, colcnt);
				}
			}
			
		}
	}

	// Gather block size 
	protected static def mpiGatherSize(src:DistArray[SparseBlock](1)):Rail[Long] {
		val root = here.id();
		val rcvbuf = new Rail[Long](Place.numPlaces());

		@Ifdef("MPI_COMMU") {
		//Collecting size
		finish {
			for([p] in src.dist) {
				if (p != root) {
					at(src.dist(p)) async {
						val srcspa = src(here.id()).getMatrix();
						val datasz = new Rail[Long](1, srcspa.getNonZeroCount());
						val scvtmp = new Rail[Long](0);
						val cnttmp = new Rail[Long](0);
						WrapMPI.world.gatherv(datasz, 0, 1, scvtmp, 0, cnttmp, root);
					}
				}
			}

			{ 
				// Do NOT move this block into for loops
				// MPI prrocess will hang
				val srcspa = src(root).getMatrix();
				val datasz = new Rail[Long](1, srcspa.getNonZeroCount());
				val rcvcnt = new Rail[Long](Place.numPlaces(), 1L);
				WrapMPI.world.gatherv(datasz, 0, 1, rcvbuf, 0, rcvcnt, root);
			}
		}
		}
		return rcvbuf;
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

		@Ifdef("MPI_COMMU") {
			mpiGatherRowBs(gp, src, dst);
		}
		@Ifndef("MPI_COMMU") {
			x10GatherRowBs(gp, src, dst);
		}
	}

	protected static def mpiGatherRowBs(
			gp:Grid,
			src:DistArray[SparseBlock](1),
			dst:SparseCSC): void {
		
		@Ifdef("MPI_COMMU") {
			val root = here.id();			
			var nzcnt:Long = 0;
			val szlist = mpiGatherSize(src);

			finish {
				for([p] in src.dist) {
					nzcnt += szlist(p);
					if (p != root) {
						at(src.dist(p)) async {
							val srcspa = src(here.id()).getMatrix();
							val datcnt = srcspa.getNonZeroCount();

							// Not working
							//val tmpidx = null; 
							//val tmpval = null;
							//val tmplst = null;

							val tmpidx = new Rail[Long](0); 
							val tmpval = new Rail[ElemType](0);
							val tmplst = new Rail[Long](0);
					
							srcspa.initRemoteCopyAtSource();
							WrapMPI.world.gatherv(srcspa.getIndex(), 0, datcnt, tmpidx, 0, tmplst, root);
							WrapMPI.world.gatherv(srcspa.getValue(), 0, datcnt, tmpval, 0, tmplst, root);
							srcspa.finalizeRemoteCopyAtSource();
					
						}
					} 
				}
			
				{
					// Root place block func
					// Do NOT move into for loop
					val srcspa = src(root).getMatrix();
					srcspa.initRemoteCopyAtSource();
					//++++++++++++++++++++++++++++++++++++++++++++
					//Do NOT call getIndex()/getValue() before init
					//+++++++++++++++++++++++++++++++++++++++++++++
					dst.initRemoteCopyAtDest(0, dst.N, nzcnt);
				
					WrapMPI.world.gatherv(srcspa.getIndex(), 0, szlist(root), dst.getIndex(), 0, szlist, root);
					WrapMPI.world.gatherv(srcspa.getValue(), 0, szlist(root), dst.getValue(), 0, szlist, root);

					srcspa.finalizeRemoteCopyAtSource();

					//Cannot finalize all at one time.
					//Because the empty line of previous block won't be shown
					//by its next line index value's modification.
					//dst.finalizeRemoteCopyAtDest();

				}
				// Rebuilt index offset-length for all collected blocks.
				var sttcol:Long=0;
				for(var b:Long=0; b<gp.numColBlocks; b++) {
					dst.finalizeRemoteCopyAtDest(sttcol, gp.colBs(b), szlist(b));
					sttcol += gp.colBs(b);
				}

			}
		}
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
