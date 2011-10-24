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

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;

import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;

/**
 * This class includes p2p, bcast, ring cast, gather, scatter, and reduce sum
 * operations for distributed matrices.
 *
 * <p> Two different implementations are available for all communication. 
 * One is based on MPI API which relying on host MPI installation, and the other 
 * is X10 remote array copy which is default. 
 * In most cases, MPI can deliver better performance than using X10 remote array copy. 
 * However, it depends on host MPI installation and hardware support.  
 * X10 remote copy does not have such restriction, and can be exported to heterogeneous
 * platforms, as well as for GPU computing.
 * 
 * <p>To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class CommHandle {

	//-------------------------
	public val p2pOp  :MatrixRemoteCopy;
	
	public val bcastOp:MatrixBcast;

	public val rcastOp:MatrixRingCast;

	public val gatherOp:MatrixGather;
	//public val reduce:MatrixReduce;	
	public val scatterOp:MatrixScatter;
	public val reduceOp:MatrixReduce;

	//====================================
	// Constructor
	//====================================
	public def this() {
		p2pOp   = new MatrixRemoteCopy();
		bcastOp = new MatrixBcast();
		rcastOp = new MatrixRingCast();
		gatherOp  = new MatrixGather();
		scatterOp = new MatrixScatter();
		reduceOp  = new MatrixReduce();
	}
	//====================================
	//====================================

	//----------------------
	
	public def copy(srcden:DenseMatrix, dmlist:DistArray[DenseMatrix](1), dstpid:Int): void {
		p2pOp.copy(srcden, 0, dmlist, dstpid, 0, srcden.N);
	}

	public def copy(dmlist:DistArray[DenseMatrix](1), srcpid:Int, dstden:DenseMatrix): void {
		p2pOp.copy(dmlist, srcpid, 0, dstden, 0, dstden.N);
	}
	//-----------------------
	public def bcast(dupmat:DistArray[DenseMatrix](1)):void {
		bcastOp.bcast(dupmat);
	}

	public def bcast(dupspa:DistArray[SparseCSC](1)):void {
		bcastOp.bcast(dupspa);
	}

	//------------------
	public def rcast(smlist:DistArray[DenseMatrix](1)):void {
		rcastOp.rcast(smlist);
	}

	public def rcast(smlist:DistArray[SparseCSC](1)):void {
		rcastOp.rcast(smlist);
	}

	//----------------------
	// Dense
	public def gather(src:DistArray[DenseBlock](1), dst:Array[DenseBlock](1)) {
		gatherOp.gather(src, dst);
	}

	public def gatherRowBs(gp:Grid, src:DistArray[DenseBlock](1), dst:DenseMatrix):void{
		gatherOp.gatherRowBs(gp, src, dst);
	}

	//Sparse
	public def gather(src:DistArray[SparseBlock](1), dst:Array[SparseBlock](1)):void {
		gatherOp.gather(src, dst);
	}

	public def gatherRowBs(gp:Grid, src:DistArray[SparseBlock](1), dst:SparseCSC):void{
		gatherOp.gatherRowBs(gp, src, dst);
	}
	//------------------------
	// Dense
	public def scatter(src:Array[DenseBlock](1), dst:DistArray[DenseBlock](1)): void {
		scatterOp.scatter(src, dst);
	}
	public def scatterRowBs(gp:Grid, src:DenseMatrix, dst:DistArray[DenseBlock](1)) : void {
		scatterOp.scatterRowBs(gp, src, dst);
	}
	// Sparse
	public def scatter(src:Array[SparseBlock](1), dst:DistArray[SparseBlock](1)) : void {
		scatterOp.scatter(src, dst);
	}
	public def scatterRowBs(gp:Grid, src:SparseCSC, dst:DistArray[SparseBlock](1)) : void {
		scatterOp.scatterRowBs(gp, src, dst);
		
	}

	//============================
	
	public def reduceSum(ddmat:DistArray[DenseMatrix](1), ddtmp:DistArray[DenseMatrix](1)): void {
		reduceOp.reduceSum(ddmat, ddtmp);
	}

	public def allReduceSum(ddmat:DistArray[DenseMatrix](1), ddtmp:DistArray[DenseMatrix](1)): void {
		reduceOp.allReduceSum(ddmat, ddtmp);
	}

}