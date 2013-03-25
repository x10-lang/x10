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

package x10.matrix.comm;

import x10.io.Console;
import x10.util.Timer;
import x10.util.Pair;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
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
public class MatrixScatter extends MatrixRemoteCopy {

	//==============================================
	// Constructor
	//==============================================
	public def this() {
		super();
	}

	//==============================================
	// Dense matrix block scatter
	//==============================================
	/**
	 * Scatter data from matrix blocks from here to distributed blocks in all places.
	 * A local block in the array is sent to a distributed block, which is corresponding
	 * to its distributed array
	 *
	 * @param src     source matrix block array.
	 * @param dst     target distributed matrix blocks 
	 */
	public static def scatter(
			src:Array[DenseBlock](1), 
			dst:DistArray[DenseBlock](1)) : void {
		
		val nb = dst.region.size();
		Debug.assure(nb==src.size, 
			"Number blocks in dist and local array mismatch");
		
		finish for (var bid:Int=0; bid<nb; bid++) {
			val srcden = src(bid).getMatrix();
			//Debug.flushln("Scatter: copy to block:"+bid);
			copy(srcden, 0, dst, bid, 0, srcden.N);
		}
	}


	//======================================================

	//------------------------------------------------------------
	// Scatter from single row blocks partitioning
	//------------------------------------------------------------

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

		Debug.assure(gp.numRowBlocks==1||gp.N==1, 
			"Number of row block in partition must be 1 or matrix is a vector");

		@Ifdef("MPI_COMMU") {
			if (gp.N==1) 
				mpiScatterVector(gp as Grid{gp.N==1}, src.d, dst);
			else
				mpiScatterRowBs(gp, src, dst);
		}
		@Ifndef("MPI_COMMU") {
			if (gp.N==1)
				x10ScatterVector(gp as Grid{gp.N==1}, src.d, dst);
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
			val szlist = new Array[Int](gp.numColBlocks, (i:Int)=>gp.colBs(i)*gp.rowBs(0));
			val root = here.id();
			finish 	{ 
				for(val [p] :Point in dst.dist) {
					val datcnt = szlist(p);
					if (p != root) {
						at (dst.dist(p)) async {
							val dstden = dst(here.id()).getMatrix();
							/*******************************************/
							// Not working
							//val tmpbuf:Array[Double](1)= null; //fake
							//val tmplst:Array[Int](1)=null;//   //fake
							/*******************************************/
							val tmpbuf = new Array[Double](0); //fake
							val tmplst = new Array[Int](0);   //fake
							//Debug.flushln("P"+p+" starting non root scatter :"+datcnt);
							WrapMPI.world.scatterv(tmpbuf, tmplst, dstden.d, datcnt, root);
						}
					} 
				}

				async {
					/**********************************************/
					// DO NOT move this block into for loop block
					// MPI process will hang, Cause is not clear
					/**********************************************/	
					//Debug.flushln("P"+root+" starting root scatter:"+szlist.toString());
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
			gp:Grid{self.N==1}, 
			src:Array[Double](1), 
			dst:DistArray[DenseBlock](1)): void {

		@Ifdef("MPI_COMMU") {
			//Only one row block partition
			val szlist = gp.rowBs;
			val root = here.id();
			finish 	{ 
				for(val [p] :Point in dst.dist) {
					val datcnt = szlist(p);
					if (p != root) {
						at (dst.dist(p)) async {
							val dstden = dst(here.id()).getMatrix();
							/*******************************************/
							// Not working
							//val tmpbuf:Array[Double](1)= null; //fake
							//val tmplst:Array[Int](1)=null;//   //fake
							/*******************************************/
							val tmpbuf = new Array[Double](0); //fake
							val tmplst = new Array[Int](0);   //fake
							//Debug.flushln("P"+p+" starting non root scatter :"+datcnt);
							WrapMPI.world.scatterv(tmpbuf, tmplst, dstden.d, datcnt, root);
						}
					} 
				}

				async {
					/**********************************************/
					// DO NOT move this block into for loop block
					// MPI process will hang, Cause is not clear
					/**********************************************/	
					//Debug.flushln("P"+root+" starting root scatter:"+szlist.toString());
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
		var coloff:Int=0;
		for (var cb:Int=0; cb<gp.numColBlocks; cb++) {

			val colcnt = gp.colBs(cb);
			
			if (cb != root) {
				x10Copy(srcden, coloff, dst, cb, 0, colcnt); 

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
			gp:Grid{self.N==1}, 
			src:Array[Double](1),	
			dst:DistArray[DenseBlock](1)): void {

		val root = here.id();
		var rowoff:Int=0;
		for (var rb:Int=0; rb<gp.numRowBlocks; rb++) {

			val rowcnt = gp.rowBs(rb);
			
			if (rb != root) {
				x10Copy(src, rowoff, dst, rb, 0, rowcnt); 

			} else {
				//Make local copying
				val dstden = dst(root).getMatrix();
				Array.copy(src, rowoff, dstden.d, 0, rowcnt);
			}
			rowoff += rowcnt;
		}
	}


	//============================================================
	// Sparse matrix block scatter
	//============================================================
	/**
	 * Scatter data from sparse blocks at here to distributed sparse blocks
	 *
	 * @param src     source sparse matrix blocks
	 * @param dst     target distributed sparse matrix block array 
	 */
	public static def scatter(
			src:Array[SparseBlock](1), 
			dst:DistArray[SparseBlock](1)) : void {
		
		val nb = dst.region.size();
		var szlist:Array[Int](1);

		Debug.assure(nb==src.size, 
			"Number blocks in dist and local array mismatch");

		finish for (var bid:Int=0; bid<nb; bid++) {
			
			val srcspa = src(bid).getMatrix();
			copy(srcspa, 0, dst, bid, 0, srcspa.N);
		}
	}

	//------------------------------------------------------------
	// Scatter sparse matrix block from single row blocks partitioning
	//------------------------------------------------------------

	/**
	 * Scatter single-row partitioning blocks in all places to a 
	 * dense matrix at here
	 */
	public static def scatterRowBs(
			gp:Grid, 
			src:SparseCSC, 
			dst:DistArray[SparseBlock](1)) : void {

		Debug.assure(gp.numRowBlocks==1 || gp.N==1, 
					 "Number of row block in partition must be 1, or matrix is a vector");
		// Test sparse block storage 
		@Ifdef("MPI_COMMU") {
			mpiScatterRowBs(gp, src, dst);
		}
		@Ifndef("MPI_COMMU") {
			x10ScatterRowBs(gp, src, dst);
		}
	}

	protected static def compNonZeroBs(gp:Grid, src:SparseCSC):Array[Int](1) {

		val nzl = new Array[Int](gp.size);
		var sttcol:Int = 0;
		var colcnt:Int = gp.getColSize(0);
		for (var cb:Int=0; cb<nzl.size; cb++) {
			colcnt = gp.getColSize(cb);
			nzl(cb) = src.countNonZero(sttcol, colcnt);
			sttcol+=colcnt;
		}
		return nzl;

	}
	//----------------------------------------------------------------

	protected static def mpiScatterRowBs(
			gp:Grid, 
			srcspa:SparseCSC,
			dst:DistArray[SparseBlock](1)): void {

		@Ifdef("MPI_COMMU") {

		val root = here.id();			
		var sttcol:Int = 0;
		var datoff:Int = 0;
		val nzlist = compNonZeroBs(gp, srcspa);
		
		finish {
			for(val [p] :Point in dst.dist) {
				if (p != root) {
					val datcnt = nzlist(p);
					at (dst.dist(p)) async {
						//Need: dst, root, datcnt
						val dstspa = dst(here.id()).getMatrix();
						//----------------------------------
						// Not working
						//val rcvidx:Array[Int](1) = null; 
						//val rcvval:Array[Double](1) = null;
						//val szl:Array[Int](1) = null;
						//-----------------------------------
						val tmpidx = new Array[Int](0); 
						val tmpval = new Array[Double](0);
						val tmplst = new Array[Int](0);
					
						//++++++++++++++++++++++++++++++++++++++++++++
						//Do NOT call getIndex()/getValue() before init
						//+++++++++++++++++++++++++++++++++++++++++++++
						dstspa.initRemoteCopyAtDest(datcnt);
						WrapMPI.world.scatterv(tmpidx, tmplst, 
									 dstspa.getIndex(), datcnt, root);
						WrapMPI.world.scatterv(tmpval, tmplst, 
									 dstspa.getValue(), datcnt, root);
						dstspa.finalizeRemoteCopyAtDest();
						//dstspa.print("Recv scatter block data");
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
		var coloff:Int = 0;

		for (var cb:Int=0; cb<gp.numColBlocks; cb++) {
			
			val colcnt = gp.colBs(cb);

			if (cb != root) {
				x10Copy(srcspa, coloff, dst, cb, 0, colcnt);
			} else {
				//Make local copying
				val dstspa = dst(root).getMatrix();
				SparseCSC.copyCols(srcspa, coloff, dstspa, 0, colcnt);
			}
			coloff += colcnt;
		}
	}
}
