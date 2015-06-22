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
 * This class provide scatter communication for distributed matrix. 
 * 
 * <p> This operation allows a) scattering array of matrix blocks to distributed
 * matrix blocks, b) scattering single column matrix (vector) to distributed matrix blocks, 
 * and c) scattering matrix to distributed blocks which has single-row block partitioning.
 *
 * <p> The target data lives on DistArray of dense blocks or sparse blocks,
 * depending on the source matrix types.
 *
 * <p> Two implementations are available. One uses MPI routines, and the other is based on 
 * X10 remote array copy.
 * To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
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

		@Ifdef("MPI_COMMU") {
			if (gp.N==1L) 
				mpiScatterVector(gp as Grid{gp.N==1L}, src.d, dst);
			else
				mpiScatterRowBs(gp, src, dst);
		}
		@Ifndef("MPI_COMMU") {
			if (gp.N==1L)
				x10ScatterVector(gp as Grid{gp.N==1L}, src.d, dst);
			else
				x10ScatterRowBs(gp, src, dst);
		}
	}
	
	/**
	 * Scatter dense matrix at here to distributed dense blocks, partitioned 
	 * in single row blocks via MPI scatter routine.
	 *
	 * @param gp         the partitioning having 1-row partitioning
	 * @param srcden     source dense matrix blocks 
	 * @param dst        target distributed dense block matrix 
	 */
	public static def mpiScatterRowBs(
			gp:Grid, 
			srcden:DenseMatrix,
			dst:DistArray[DenseBlock](1)): void {

		@Ifdef("MPI_COMMU") {

		//Only one row block partition
			val szlist = new Rail[Long](gp.numColBlocks, (i:Long)=>gp.colBs(i)*gp.rowBs(0));
			val root = here.id();
			finish 	{ 
				for([p] in dst.dist) {
					val datcnt = szlist(p);
					if (p != root) {
						at(dst.dist(p)) async {
							val dstden = dst(here.id()).getMatrix();
							/*******************************************/
							// Not working
							//val tmpbuf= null; //fake
							//val tmplst=null;//   //fake
							/*******************************************/
							val tmpbuf = new Rail[ElemType](0); //fake
							val tmplst = new Rail[Long](0);   //fake
							WrapMPI.world.scatterv(tmpbuf, tmplst, dstden.d, datcnt, root);
						}
					} 
				}

				async {
					/**********************************************/
					// DO NOT move this block into for loop block
					// MPI process will hang, Cause is not clear
					/**********************************************/	
					val dstden = dst(root).getMatrix();
					WrapMPI.world.scatterv(srcden.d, szlist, dstden.d, szlist(root), root);
				}
			
			}
		}
	}

	/**
	 * Scatter single column matrix (vector) to distributed dense blocks
	 *
	 * @param gp			matrix partitioning with single column blocks
	 * @param src			source matrix data buffer
	 * @param dst			destination dense blocks distributed among all blocks.
	 */
	public static def mpiScatterVector(
			gp:Grid{self.N==1L}, 
			src:Rail[ElemType], 
			dst:DistArray[DenseBlock](1)): void {

		@Ifdef("MPI_COMMU") {
			//Only one row block partition
			val szlist = gp.rowBs;
			val root = here.id();
			finish 	{ 
				for([p] in dst.dist) {
					val datcnt = szlist(p);
					if (p != root) {
						at(dst.dist(p)) async {
							val dstden = dst(here.id()).getMatrix();
							/*******************************************/
							// Not working
							//val tmpbuf= null; //fake
							//val tmplst=null;//   //fake
							/*******************************************/
							val tmpbuf = new Rail[ElemType](0); //fake
							val tmplst = new Rail[Long](0);   //fake
							WrapMPI.world.scatterv(tmpbuf, tmplst, dstden.d, datcnt, root);
						}
					} 
				}

				async {
					/**********************************************/
					// DO NOT move this block into for loop block
					// MPI process will hang, Cause is not clear
					/**********************************************/	
					val dstden = dst(root).getMatrix();
					WrapMPI.world.scatterv(src, szlist, dstden.d, szlist(root), root);
				}
			}
		}
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
		@Ifdef("MPI_COMMU") {
			mpiScatterRowBs(gp, src, dst);
		}
		@Ifndef("MPI_COMMU") {
			x10ScatterRowBs(gp, src, dst);
		}
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

	protected static def mpiScatterRowBs(
			gp:Grid, 
			srcspa:SparseCSC,
			dst:DistArray[SparseBlock](1)): void {

		@Ifdef("MPI_COMMU") {

		val root = here.id();			
		var sttcol:Long = 0;
		var datoff:Long = 0;
		val nzlist = compNonZeroBs(gp, srcspa);
		
		finish {
			for([p] in dst.dist) {
				if (p != root) {
					val datcnt = nzlist(p);
					at(dst.dist(p)) async {
						//Need: dst, root, datcnt
						val dstspa = dst(here.id()).getMatrix();

						// Not working
						//val rcvidx = null; 
						//val rcvval = null;
						//val szl = null;

						val tmpidx = new Rail[Long](0); 
						val tmpval = new Rail[ElemType](0);
						val tmplst = new Rail[Long](0);
					
						//++++++++++++++++++++++++++++++++++++++++++++
						//Do NOT call getIndex()/getValue() before init
						//+++++++++++++++++++++++++++++++++++++++++++++
						dstspa.initRemoteCopyAtDest(datcnt);
						WrapMPI.world.scatterv(tmpidx, tmplst, 
									 dstspa.getIndex(), datcnt, root);
						WrapMPI.world.scatterv(tmpval, tmplst, 
									 dstspa.getValue(), datcnt, root);
						dstspa.finalizeRemoteCopyAtDest();
					}
				} 
			}
			
			{
				// Root place block func
				// Do NOT move into for loop
				val dstspa = dst(root).getMatrix();
				val srcidx = srcspa.getIndex();
				val srcval = srcspa.getValue();
				val datcnt = nzlist(root);

				//++++++++++++++++++++++++++++++++++
				// The init remote first method must be called before 
				// getting the index and value arrays.
				// When recieve side storage is smaller than
				// incoming data, the storage will be re-allocated in the init method,
				// therefore, making the earlier storage array object obsolete
				//+++++++++++++++++++++++++++++++++++
		   
				dstspa.initRemoteCopyAtDest(datcnt);
				srcspa.initRemoteCopyAtSource();

				WrapMPI.world.scatterv(srcidx, nzlist, 
							 dstspa.getIndex(), datcnt, root);
				WrapMPI.world.scatterv(srcval, nzlist, 
							 dstspa.getValue(), datcnt, root);

				srcspa.finalizeRemoteCopyAtSource();
				dstspa.finalizeRemoteCopyAtDest();

			}
		}
		}
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
