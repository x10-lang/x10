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
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;

import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;

/**
 * This class transfers matrix data across different places.
 * Matrix data is defined by DenseBlock, SparseBlock or DenseMatrix on DistArray.
 * 
 * <p> Two implementations are available, based on MPI routines and X10 remote array copy.
 * To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class MatrixRemoteCopy {

	// Constructor
	public def this() {
	}	

	//=========================================================================
	// Dense matrix copy To
	//=========================================================================
	/**
	 * Copy whole dense matrix from here to the remote place via DistArray of dense matrices.
	 *
	 * @param dmlist 	DistArray of dense matrices
	 * @param dstpid 	The destination place
	 * @return -- number of elements copied
	 */
	public static def copyTo(dmlist:DistArray[DenseMatrix](1), dstpid:Int) {
		val srcden = dmlist(here.id());
		return copy(srcden, 0, dmlist, dstpid, 0, srcden.N);
	}

	/**
	 * Copy whole dense matrix from remote place to here
	 *
	 * @param dmlist 	DistArray of dense matrices
	 * @param srcpid 	The source place
	 * @return -- number of elements copied
	 */
	public static def copyFrom(dmlist:DistArray[DenseMatrix](1), srcpid:Int) {
		val dstden = dmlist(here.id());
		return copy(dmlist, srcpid, 0, dstden, 0, dstden.N);
	}

	//-----------------------------------------------
	// Dense matrix copy To
	//-----------------------------------------------

	/**
	 * Copy specified columns from source matrix at here to the remote 
	 * place via DistArray of dense matrix.
	 * 
	 * @param srcden    	the source dense matrix at here
	 * @param srcColOff 	starting column in in source matrix
	 * @param dmlist    	target dense matrix in DistArray
	 * @param dstpid    	the place id of target dense matrix in DistArray.
	 * @param dstColOff 	starting column offset in target matrix at destination place
	 * @param colCnt    	count of columns to be copied from source dense matrix
	 * @return -- number of elements copied.
	 */
	public static def copy(
			srcden:DenseMatrix, srcColOff:Int,
			dmlist:DistArray[DenseMatrix](1), 
			dstpid:Int, dstColOff:Int, colCnt:Int): Int {

		var dsz:Int = 0;
		if (here.id() == dstpid) {
			DenseMatrix.copyCols(srcden, srcColOff, dmlist(dstpid), dstColOff, colCnt);
			return srcden.M * colCnt;
		}

		@Ifdef("MPI_COMMU") {
			dsz = mpiCopy(srcden, srcColOff, dmlist, dstpid, dstColOff, colCnt);
		}
		@Ifndef("MPI_COMMU") {
			dsz = x10Copy(srcden, srcColOff, dmlist, dstpid, dstColOff, colCnt);
		}
		return dsz;
	}

	//------------------------------------------------------------
	// Copy To implementation
	//------------------------------------------------------------

	/**
	 * Using MPI routine to copy multiple columns from source dense matrix at here 
	 * to the remote dense matrix of duplicated dense matrix at the specified place. 
	 * MPI send and recv are performed at here and the remote place respectively.
	 * 
	 * @param srcden 	Input. The source dense matrix at here
	 * @param dmlist 	Output. The duplicated dense matrix DistArray
	 * @param dstpid 	Input. The place id of target dense matrix in the duplicated dense matrix.
	 * @param coloff 	Input. Column offset in source matrix
	 * @param colcnt 	Input. Count of columns to be copied from source dense matrix
	 * @return -- Number of elements copied.
	 */	
	protected static def mpiCopy(
			srcden:DenseMatrix, srcColOff:Int, 
			dmlist:DistArray[DenseMatrix](1), dstpid:Int,
			dstColOff:Int, colCnt:Int) :Int  {
		
		Debug.assure(srcColOff+colCnt <= srcden.N,
					 "At source place, illegal column offset:"+srcColOff+
					 " or column count:"+colCnt);

		val srcpid = here.id();         //Implicitly carried to dst place
		val datasz = srcden.M * colCnt; //Implicitly carried to dst place
		@Ifdef("MPI_COMMU") { 
			//Debug.flushln("Starting mpi send+recv dense matrix");
			finish {
				// At the destination place, receiving the data 
				at (dmlist.dist(dstpid)) async {
					//Remote capture: dmlist, srcpid, dstColOff, datasz
					val dstden = dmlist(here.id());
					val dstoff = dstden.M * dstColOff;
					val tag    = srcpid * 10000 + here.id();
					//Debug.flushln("Starting mpi recv dense matrix"+datasz);
					WrapMPI.world.recv(dstden.d, dstoff, datasz, srcpid, tag);
					//Debug.flushln("Done mpi recv dense matrix");				
				}
				//Debug.flushln("Recv process started at remote place");
				async {
					// At the source place, sending out the data
					val srcoff = srcden.M * srcColOff;
					val tag = srcpid * 10000 + dstpid;
					//Debug.flushln("Starting mpi sending dense matrix"+datasz);
					WrapMPI.world.send(srcden.d, srcoff, datasz, dstpid, tag);
					//Debug.flushln("Done mpi sending dense matrix");
				}				
			} 
			//Debug.flushln("Done mpi send+recv dense matrix");
		}
		return datasz;
	}
	
	/**
	 * Copy source dense matrix from here to the remote dense matrix of
	 * duplicated dense matrix at a remote place using Array remote copy.
	 * The source and destination dense matrices must have the same leading dension - M.
	 * 
	 * @param srcden 		source dense matrix at here
	 * @param srcColOff		column offset in source matrix
	 * @param dmlist 		target dense matrix in DistArray
	 * @param dstpid 		place id of target dense matrix in the duplicated dense matrix.
	 * @param dstColOff 	column offset in target matrix
	 * @param colCnt 		Count of columns to be copied from source dense matrix
	 * @return -- Number of elements copied.
	 */
	protected static def x10Copy(
			srcden:DenseMatrix, srcColOff:Int,
			dmlist:DistArray[DenseMatrix](1), dstpid:Int,
			dstColOff:Int, colCnt:Int): Int {
		
		Debug.assure(srcColOff + colCnt <= srcden.N,
		"at source place, illegal column offset and count");
		val buf = srcden.d as Array[Double]{self!=null};
		val srcbuf = new RemoteArray[Double](buf);
		val datcnt = srcden.M * colCnt;
		val srcoff = srcden.M * srcColOff;

		Debug.assure(srcColOff+colCnt <= srcden.N);
		at (dmlist.dist(dstpid)) {
			//Implicit copy: dst, srcbuf, srcoff, datcnt
			val dstden = dmlist(here.id());
			val dstoff = dstColOff * dstden.M;

			Debug.assure(dstColOff*dstden.M+datcnt <= dstden.M*dstden.N);
			finish Array.asyncCopy[Double](srcbuf, srcoff, dstden.d, dstoff, datcnt);		
		}

		return datcnt;
	}

	//------------------------------------------
	// Dense matrix copy From 
	//------------------------------------------
	/**
	 * Copy multiple columns of the dense matrix in the specified place to
	 * here
	 * 
	 * @param dmlist    	source matrix in the duplicated dense matrix
	 * @param srcpid   		source matrix's place id.
	 * @param srcColOff 	starting column in in source matrix
	 * @param dstden   		destination dense matrix of the copy
	 * @param dstColOff 	starting column in target matrix
	 * @param colCnt    	count of columns to be copied in source matrix
	 * @return -- Number of elements copied
	 */
	public static def copy(
			dmlist:DistArray[DenseMatrix](1), srcpid:Int, srcColOff:Int, 
			dstden:DenseMatrix, dstColOff:Int, 
			colCnt:Int): Int {

		var dsz:Int = 0;
		if (here.id() == srcpid) {
			DenseMatrix.copyCols(dmlist(srcpid), srcColOff, dstden, dstColOff, colCnt);
			return dstden.M * colCnt;
		}

		@Ifdef("MPI_COMMU") {
			dsz = mpiCopy(dmlist, srcpid, srcColOff, dstden, dstColOff, colCnt);
		}

		@Ifndef("MPI_COMMU") {
			dsz = x10Copy(dmlist, srcpid, srcColOff, dstden, dstColOff, colCnt);
		}
		return dsz;
	}
	
	/**
	 * Copy remote dense matrix from specified place to here, using array remote copy.
	 * The source and destination dense matrix must have the same leading dimension.
	 *
	 * @param dmlist  		source dense matrix in DistArray
	 * @param srcpid  		source matrix's place id.
	 * @param srcColOff  	column offset in source matrix
	 * @param dstden  		destination dense matrix of the copy
	 * @param dstColOff 	column offset in target matrix
	 * @param colCnt  		count of columns to be copied in source matrix
	 * @return 				number of elements copied
	 */
	public static def x10Copy(
			dmlist:DistArray[DenseMatrix](1), srcpid:Int, srcColOff:Int,
			dstden:DenseMatrix, dstColOff:Int, 
			colCnt:Int): Int {

		val dstoff = dstden.M * dstColOff;
		val rmt:DenseRemoteSourceInfo  = at (dmlist.dist(srcpid)) { 
			//Need: dmlist, srcColOff, colCnt
			val mat = dmlist(here.id());
			val off = srcColOff * mat.M;
			val cnt = colCnt    * mat.M;
			Debug.assure(off + cnt <= mat.M * mat.N, 
						 "Matrix remote copy fails! Illegal size at source matrix");
			DenseRemoteSourceInfo(mat.d, off, cnt)
		};

		Debug.assure(dstoff+rmt.length <= dstden.M*dstden.N,
					 "Matrix remote copy fails! Illegal size at target matrix");
		
		finish Array.asyncCopy[Double](rmt.valbuf, rmt.offset, dstden.d, dstoff, rmt.length);
		
		return rmt.length;
	}
	

	/**
	 * Using MPI routine to copy multiple columns of dense matrix 
	 * from the specified place to here
	 *
	 * @param dmlist  		source dense matrix in the DistArray
	 * @param srcpid  		source matrix's place id.
	 * @param srcColOff  	column offset in source matrix
	 * @param dstden  		destination dense matrix of the copy
	 * @param dstColOff 	column offset in source matrix
	 * @param colCnt 		count of columns to be copied in source matrix
	 * @return 				number of elements copied
	 */
	protected static def mpiCopy(
			dmlist:DistArray[DenseMatrix](1), srcpid:Int, srcColOff:Int,
			dstden:DenseMatrix, dstColOff:Int, 
			colCnt:Int): Int {

		Debug.assure(dstColOff+colCnt <= dstden.N,
					 "At destination place, illegal column offset or count");

		val dstpid = here.id();
		val datasz = dstden.M * colCnt;

		@Ifdef("MPI_COMMU") {
		finish {
			at (dmlist.dist(srcpid)) async {
				//Need: dmlist, dstpid, srcColOff, datasz, 
				val srcden = dmlist(here.id());
				val srcoff = srcden.M * srcColOff;
				val tag    = here.id() * 20000 + dstpid;
				WrapMPI.world.send(srcden.d, srcoff, datasz, dstpid, tag);
			}
			async {
				val tag    = srcpid * 20000 + dstpid;
				val dstoff = dstden.M * dstColOff;
				WrapMPI.world.recv(dstden.d, dstoff, datasz, srcpid, tag);
			}
		}
		}
		return datasz;
	}

	//---------------------------------------------
	// Dense matrix block copy From
	//---------------------------------------------
	/**
	 * Copy multiple columns from source dense matrix at here to the specified remote 
	 * place in DistArray of dense matrix.
	 * 
	 * @param srcden   		the source dense matrix at here
	 * @param srcColOff 	starting column in in source matrix
	 * @param dmlist    	target dense blocks in DistArray
	 * @param dstpid    	the place id of target dense matrix in DistArray.
	 * @param dstColOff 	starting column offset in target matrix at destination place
	 * @param colCnt   		count of columns to be copied from source dense matrix
	 * @return 				number of elements copied.
	 */
	public static def copy(
			srcden:DenseMatrix, srcColOff:Int,
			dmlist:DistArray[DenseBlock](1), 
			dstpid:Int, dstColOff:Int, 
			colCnt:Int): Int {
		
		var dsz:Int = 0;
		if (here.id() == dstpid) {
			DenseMatrix.copyCols(srcden, srcColOff, 
								 dmlist(dstpid).getMatrix(), dstColOff, colCnt);
			return srcden.M * colCnt;
		}

		@Ifdef("MPI_COMMU") {
			dsz = mpiCopy(srcden, srcColOff, dmlist, dstpid, dstColOff, colCnt);
		}
		@Ifndef("MPI_COMMU") {
			dsz = x10Copy(srcden, srcColOff, dmlist, dstpid, dstColOff, colCnt);
		}
		return dsz;
	}

	//Dense matrix remote copy to
	protected static def mpiCopy(
			srcden:DenseMatrix, srcColOff:Int,
			dmlist:DistArray[DenseBlock](1), dstbid:Int, dstColOff:Int, 
			colCnt:Int):Int {
		
		val srcbid = here.id();
		val datasz = srcden.M * colCnt; //Implicitly carried to dst place
		@Ifdef("MPI_COMMU") {
			finish {
				//Debug.flushln("Starting mpi send+recv dense block");

				at (dmlist.dist(dstbid)) async {
					//Need: srcbid, dst, dstColOff, datasz
					val dstden = dmlist(here.id()).dense;
					val tag    = srcbid * 60000 + here.id();
					//!!!!!!!!!!!!!!!!WARNING!!!!!!!!!!!!!!!!!
					//val off    = srcden.M * dstColOff;
					// Do NOT capture srcden.M, the srcden object are copied to here
					//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					val off = dstden.M * dstColOff;
					//Debug.flushln("Start recv dense block");
					WrapMPI.world.recv(dstden.d, off, datasz, srcbid, tag);
					//Debug.flushln("Done faking recv dense block "+datasz);
				}
				//Debug.flushln("Recv process started at remote place");
				async {
					val sttoff = srcden.M*srcColOff;
					val tag = here.id() * 60000 + dstbid;
					// src and dst must have the same leading dimension
					//Debug.flushln("Start sending dense block data "+datasz);
					WrapMPI.world.send(srcden.d, sttoff, datasz, dstbid, tag);
					//Debug.flushln("Done faking sending dense block data");
				}
			}
			//Debug.flushln("Done mpi send+recv dense block");
		}
		return datasz;
	}
	
	// Dense matrix remote copy to
	protected static def x10Copy(
			srcden:DenseMatrix,  srcColOff:Int,
			dst:DistArray[DenseBlock](1), dstbid:Int, 
			dstColOff:Int, colCnt:Int): Int {

		val buf = srcden.d as Array[Double]{self!=null};
		val srcbuf = new RemoteArray[Double](buf);
		val datcnt = srcden.M * colCnt;
		val srcoff = srcden.M * srcColOff;
	
		Debug.assure(srcColOff+colCnt <= srcden.N);
		at (dst.dist(dstbid)) {
			//Implicit copy: dst, srcbuf, srcoff, datcnt
			val dstden = dst(here.id()).getMatrix();
			val dstoff = dstColOff * dstden.M;

			Debug.assure(dstColOff*dstden.M+datcnt <= dstden.M*dstden.N);
			finish Array.asyncCopy[Double](srcbuf, srcoff, dstden.d, dstoff, datcnt);		
		}
		return datcnt;
	}
	
	//------------------------------------
	//Dense matrix block remote copy From
	//------------------------------------
	/**
	 * Copy multiple columns from source dense matrix at here to the specified remote 
	 * place in DistArray of dense matrix.
	 * 
	 * @param dmlist    	target dense blocks in DistArray
	 * @param srcpid    	the place id of target dense matrix in DistArray.
	 * @param srcColOff 	starting column in in source matrix
	 * @param dstden    	the source dense matrix at here
	 * @param dstColOff 	starting column offset in target matrix at destination place
	 * @param colCnt    	count of columns to be copied from source dense matrix
	 * @return -- number of elements copied.
	 */
	public static def copy(
			dmlist:DistArray[DenseBlock](1), srcpid:Int, srcColOff:Int,
			dstden:DenseMatrix, dstColOff:Int, 
			colCnt:Int): Int {
		
		var dsz:Int = 0;
		if (here.id() == srcpid) {
			DenseMatrix.copyCols(dmlist(srcpid).getMatrix(), srcColOff, 
								 dstden, dstColOff, colCnt);
			return dstden.M * colCnt;
		}

		@Ifdef("MPI_COMMU") {
			dsz = mpiCopy(dmlist, srcpid, srcColOff, dstden, dstColOff, colCnt);
		}
		@Ifndef("MPI_COMMU") {
			dsz = x10Copy(dmlist, srcpid, srcColOff, dstden, dstColOff, colCnt);
		}
		return dsz;
	}

	//MPI matrix remote copy from
	protected static def mpiCopy(
			src:DistArray[DenseBlock](1), srcbid:Int, srcColOff:Int,
			dstden:DenseMatrix, dstColOff:Int, 
			colCnt:Int) : Int {
		
		val dstbid = here.id();
		val datcnt = dstden.M*colCnt; 
		@Ifdef("MPI_COMMU") {
			finish {
				at (src.dist(srcbid)) async {
					//Need:src, srcColOff, colCnt
					val srcden = src(here.id()).getMatrix();
					val tag = here.id() * 30000 + dstbid;
					val off = srcden.M*srcColOff;
					val cnt = srcden.M*colCnt;
					WrapMPI.world.send(srcden.d, off, cnt, dstbid, tag);
				}
				async {
					val tag = srcbid * 30000 + dstbid;
					val sttoff = dstden.M*dstColOff;
					// src and dst must have the same leading dimension
					WrapMPI.world.recv(dstden.d, sttoff, datcnt, srcbid, tag);
				}
			}
		}
		return datcnt;
	}

	
	protected static def x10Copy(
			src:DistArray[DenseBlock](1), srcbid:Int, srcColOff:Int,
			dstden:DenseMatrix, dstColOff:Int, colCnt:Int): Int {

		val rmt:DenseRemoteSourceInfo = at (src.dist(srcbid)) { 
			//Implicit copy: src, srcColOff, colCnt, 
										
		    val mat = src(here.id()).getMatrix();
			val off = srcColOff * mat.M;
			var cnt:Int = mat.M * colCnt;
			Debug.assure(cnt <= mat.M * mat.N,
						 "Matrix remote copy fails! Illegal size at source matrix");
			DenseRemoteSourceInfo(mat.d, off, cnt)
		};

		val sttoff = dstColOff * dstden.M;
		Debug.assure(sttoff+rmt.length <= dstden.M*dstden.N, 
					 "Matrix remote copy fails! Illegal size at target matrix");
		
		finish Array.asyncCopy[Double](rmt.valbuf, rmt.offset, dstden.d, sttoff, rmt.length);
		return rmt.length;
	}
	
	//=========================================================================
	// SparseCSC matrix copy To/From
	//=========================================================================
	/**
	 * Copy whole sparse matrix from here to remote place stored in DistArray
	 *
	 * @param smlist 	DistArray of sparse matrices
	 * @param dstpid 	destination place id
	 * @return 			number of elements copied
	 */
	public static def copyTo(smlist:DistArray[SparseCSC](1), dstpid:Int) {
		val srcspa = smlist(here.id());
		return copy(srcspa, 0, smlist, dstpid, 0, srcspa.N);
	}

	/**
	 * Copy whole sparseCSC matrix from remote place to here
	 *
	 * @param smlist 	DistArray of sparse matrices
	 * @param srcpid 	The source place
	 * @return -- number of elements copied
	 */
	public static def copyFrom(smlist:DistArray[SparseCSC](1), srcpid:Int) {
		val dstspa = smlist(here.id());
		return copy(smlist, srcpid, 0, dstspa, 0, dstspa.N);
	}

	//-----------------------------
	// Sparse matrix copy To
	//-----------------------------
	/**
	 * Copy multiple columns from source sparse matrix at here to the specified remote 
	 * place in DistArray.
	 * 
	 * @param srcspa 		source sparseCSC matrix at here
	 * @param srcColOff		column offset in source matrix
	 * @param smlist 		target sparse matrix in DistArray
	 * @param dstpid 		place id of target sparse matrix in DistArray.
	 * @param dstColOff		column offset in target matrix
	 * @param colCnt 		count of columns to be copied from source sparse matrix
	 * @return 				number of elements copied.
	 */
	public static def copy(
			srcspa:SparseCSC, srcColOff:Int,
			smlist:DistArray[SparseCSC](1), dstpid:Int, dstColOff:Int, 
			colCnt:Int): Int {
		
		var dsz:Int = 0;

		if (here.id() == dstpid) {
			dsz = SparseCSC.copyCols(srcspa, srcColOff, smlist(dstpid), dstColOff, colCnt);
			return dsz;
		}

		@Ifdef("MPI_COMMU") {
			dsz = mpiCopy(srcspa, srcColOff, smlist, dstpid, dstColOff, colCnt);
		}
		@Ifndef("MPI_COMMU") {
			dsz = x10Copy(srcspa, srcColOff, smlist, dstpid, dstColOff, colCnt);
		}
		return dsz;
	}


	// Sparse matrix copy To based on MPI routine
	/**
	 * Based on mpi send/recv, copy data from here to remote place
	 */
	protected static def mpiCopy(
			srcspa:SparseCSC, srcColOff:Int,
			smlist:DistArray[SparseCSC](1), dstpid:Int, dstColOff:Int, 
			colCnt:Int): Int {

		Debug.assure(srcColOff+colCnt <= srcspa.N,
					 "At source place, illegal column offset and count");

		val srcpid = here.id();        
		val datasz = srcspa.countNonZero(srcColOff, colCnt); //Implicitly carried to dst place

		@Ifdef("MPI_COMMU") {
			finish {
				at (smlist.dist(dstpid)) async {
					// Need: smlist, srcpid, dstColOff, datasz;
					val dstspa = smlist(here.id());
					val dstoff = dstspa.getNonZeroOffset(dstColOff);
					val tag    = srcpid * 10000 + here.id();
					
					//++++++++++++++++++++++++++++++++++++++++++++
					//Do NOT call getIndex()/getValue() before init at destination place
					//+++++++++++++++++++++++++++++++++++++++++++++
					dstspa.initRemoteCopyAtDest(dstColOff, colCnt, datasz);
					WrapMPI.world.recv(dstspa.getIndex(), dstoff, datasz, srcpid, tag);
					WrapMPI.world.recv(dstspa.getValue(), dstoff, datasz, srcpid, tag+100001);
					dstspa.finalizeRemoteCopyAtDest();
					//dstspa.print("Recv sparse");
				}
				async {
					val tag    = srcpid * 10000 + dstpid;
					val srcoff = srcspa.getNonZeroOffset(srcColOff);
					
					srcspa.initRemoteCopyAtSource(srcColOff, colCnt);
					WrapMPI.world.send(srcspa.getIndex(), srcoff, datasz, dstpid, tag);
					WrapMPI.world.send(srcspa.getValue(), srcoff, datasz, dstpid, tag+100001);
					srcspa.finalizeRemoteCopyAtSource();
				}
			}
			
		}
		return datasz;
	}


	/**
	 * Copy source sparse matrix from here to the remote sparse matrix at reomote 
	 * place using Array remote copy.
	 * The source and destination dense matrices must have the same leading dension - M.
	 * 
	 * @param srcspa 		source sparse matrix at here
	 * @param srcColOff 	column offset in source matrix
	 * @param smlist 		target sparse matrix in DistArray
	 * @param dstpid 		place id of target sparse matrix in DistArray.
	 * @param dstColOff 	column offset in target matrix
	 * @param colCnt 		count of columns to be copied from source matrix
	 * @return -- Number of elements copied.
	 */
	protected static def x10Copy(
			srcspa:SparseCSC, srcColOff:Int,
			dst:DistArray[SparseCSC](1), dstbid:Int, dstColOff:Int,
			colCnt:Int): Int {

		Debug.assure(srcColOff+colCnt <= srcspa.N,
					 "At source place illegal column offset and count:");

		val idxbuf = srcspa.getIndex() as Array[Int]{self!=null};
		val valbuf = srcspa.getValue() as Array[Double]{self!=null};
		val datoff = srcspa.getNonZeroOffset(srcColOff);
		val datcnt = srcspa.initRemoteCopyAtSource(srcColOff, colCnt);
		val rmtidx = new RemoteArray[Int](idxbuf);
		val rmtval = new RemoteArray[Double](valbuf);

		at (dst.dist(dstbid)) {
			//Implicit copy:dst, datcnt, rmtidx, rmtval, datoff
			val dstspa = dst(here.id());

			//Debug.flushln("Recv data dst:"+dstbid+" off:"+datoff+
			//			  " cnt:"+datcnt+" storage size"+dstspa.getStorage().storageSize());
			//++++++++++++++++++++++++++++++++++++++++++++
			//Do not call getIndex()/getValue() before init at destination place
			//+++++++++++++++++++++++++++++++++++++++++++++
			dstspa.initRemoteCopyAtDest(dstColOff, colCnt, datcnt);
			val dstoff = dstspa.getNonZeroOffset(dstColOff);
			finish Array.asyncCopy[Int   ](rmtidx, datoff, dstspa.getIndex(), dstoff, datcnt);
			finish Array.asyncCopy[Double](rmtval, datoff, dstspa.getValue(), dstoff, datcnt);
			dstspa.finalizeRemoteCopyAtDest();
		}
		srcspa.finalizeRemoteCopyAtSource();

		return datcnt;
	}

	//-----------------------------------------------------------
	// Sparse matrix copy From
	//-----------------------------------------------------------

	/**
	 * Copy multiple columns of the sparseCSC matrix in the specified place to
	 * here
	 *
	 * @param smlist  		source sparse matrix in DistArray
	 * @param srcpid  		source matrix's place id.
	 * @param srcColOff  	column offset in source matrix
	 * @param dstspa  		destination sparse matrix of the copy
	 * @param dstColOff 	column offset in target matrix
	 * @param colCnt  		count of columns to be copied in source matrix
	 * @return -- Number of elements copied
	 */
	public static def copy(
			smlist:DistArray[SparseCSC](1), srcpid:Int, srcColOff:Int,
			dstspa:SparseCSC, dstColOff:Int, 
			colCnt:Int): Int {
		var dsz:Int = 0;

		if (here.id() == srcpid) {
			dsz = SparseCSC.copyCols(smlist(srcpid), srcColOff,  dstspa, dstColOff, colCnt);
			return dsz;
		}

		@Ifdef("MPI_COMMU") {
			dsz = mpiCopy(smlist, srcpid, srcColOff, dstspa, dstColOff, colCnt);
		}

		@Ifndef("MPI_COMMU") {
			dsz = x10Copy(smlist, srcpid, srcColOff, dstspa, dstColOff, colCnt);
		}
		return dsz;
	}

	/**
	 * Based on mpi send/recv, copy data from remote place to here. The data size is 
	 * transfered to here implicitly before matrix data copy.
	 */
	protected static def mpiCopy(
			smlist:DistArray[SparseCSC](1), srcpid:Int, srcColOff:Int,
			dstspa:SparseCSC, dstColOff:Int, 
			colCnt:Int): Int {

		Debug.assure(dstColOff+colCnt <= dstspa.N,
					 "At destination place, illegal column offset and count");

		val dstpid = here.id();//Implicitly carried to dst place
		val dsz = at (smlist.dist(srcpid)) 
			smlist(here.id()).countNonZero(dstColOff, colCnt); //Implicitly carried to dst place

		@Ifdef("MPI_COMMU") {
			finish {
				at (smlist.dist(srcpid)) async {
					//Need: smlist, dstpid, srcColOff
					val srcspa = smlist(here.id());
					val srcoff = srcspa.getNonZeroOffset(srcColOff);;
					val tag    = here.id() * 20000 + dstpid;
					val datasz = dstspa.countNonZero(dstColOff, colCnt);
					
					srcspa.initRemoteCopyAtSource(srcColOff, colCnt);
					WrapMPI.world.send(srcspa.getIndex(), srcoff, datasz, dstpid, tag);
					WrapMPI.world.send(srcspa.getValue(), srcoff, datasz, dstpid, tag+1000002);
					
					srcspa.finalizeRemoteCopyAtSource();
				}
				
				async {
					
					val tag    = srcpid * 20000 + dstpid;
					val dstoff = dstspa.getNonZeroOffset(dstColOff);
					
					//++++++++++++++++++++++++++++++++++++++++++++
					//Do NOT call getIndex()/getValue() before init at destination place
					//+++++++++++++++++++++++++++++++++++++++++++++
					dstspa.initRemoteCopyAtDest(dstColOff, colCnt, dsz);
					WrapMPI.world.recv(dstspa.getIndex(), dstoff, dsz, srcpid, tag);
					WrapMPI.world.recv(dstspa.getValue(), dstoff, dsz, srcpid, tag+1000002);
					dstspa.finalizeRemoteCopyAtDest();
				}
			}
		}
		
		return dsz;
	}

	//------------------------------------------

	/**
	 * Copy remote sparse matrix from specified place to here, using array remote copy.
	 * The source and destination matrix must have the same leading dimension.
	 *
	 * @param smlist  		source sparseCSC matrix in DistArray
	 * @param srcpid  		source matrix's place id.
	 * @param srcColOff  	column offset in source matrix
	 * @param dstspa 		destination sparse matrix at here
	 * @param dstColOff  	column offset in target matrix
	 * @param colCnt 		count of columns to be copied in source matrix
	 * @return 				number of elements copied
	 */
	public static def x10Copy(
			smlist:DistArray[SparseCSC](1), srcpid:Int, srcColOff:Int,
			dstspa:SparseCSC, dstColOff:Int, 
			colCnt:Int): Int {

		Debug.assure(dstColOff+colCnt <= dstspa.N,
					 "At destination place, illegal column offset and count");

		val dstpid = here.id();

		val rmt = at (smlist.dist(srcpid)) {
			//Need: smlist, srcColOff, colCnt
			val mat = smlist(here.id());
			val off = mat.getNonZeroOffset(srcColOff);
			val cnt = mat.countNonZero(srcColOff, colCnt);

			mat.initRemoteCopyAtSource(srcColOff, colCnt);
			SparseRemoteSourceInfo(mat.getIndex(), mat.getValue(), off, cnt)
		};

		val dstoff = dstspa.getNonZeroOffset(dstColOff);
		//++++++++++++++++++++++++++++++++++++++++++++
		//Do NOT call getIndex()/getValue() before init at destination place
		//+++++++++++++++++++++++++++++++++++++++++++++
		dstspa.initRemoteCopyAtDest(srcColOff, colCnt, rmt.length);
		finish Array.asyncCopy[Int   ](rmt.idxbuf, rmt.offset, 
									   dstspa.getIndex(), dstoff, rmt.length);
		finish Array.asyncCopy[Double](rmt.valbuf, rmt.offset, 
									   dstspa.getValue(), dstoff, rmt.length);
		
		finish {
			at (smlist.dist(srcpid)) async {
				//Need: smlist
				val srcspa = smlist(here.id());
				srcspa.finalizeRemoteCopyAtSource();
			}
			dstspa.finalizeRemoteCopyAtDest();
		}
		
		return rmt.length;
	}

	//=============================================================
	// Sparase block Copy To/From
	//=============================================================

	//--------------------------
	// Sparse block copy To
	//--------------------------
	/**
	 * Copy multiple columns from source sparse matrix block at here to the specified remote 
	 * place in DistArray.
	 * 
	 * @param srcspa 		source sparseCSC matrix at here
	 * @param srcColOff 	column offset in source matrix
	 * @param smlist 		target sparse matrix block in DistArray
	 * @param dstpid 		place id of target sparse matrix in DistArray.
	 * @param dstColOff 	column offset in target matrix
	 * @param colCnt 		count of columns to be copied from source sparse matrix
	 * @return 				number of elements copied.
	 */
	public static def copy(
			srcspa:SparseCSC, srcColOff:Int,
			smlist:DistArray[SparseBlock](1), dstpid:Int, dstColOff:Int, 
			colCnt:Int): Int {
		
		var dsz:Int = 0;
		if (here.id() == dstpid) {
			dsz = SparseCSC.copyCols(srcspa, srcColOff, 
									 smlist(dstpid).getMatrix(), dstColOff, colCnt);
			return dsz;
		}

		@Ifdef("MPI_COMMU") {
			dsz = mpiCopy(srcspa, srcColOff, smlist, dstpid, dstColOff, colCnt);
		}
		@Ifndef("MPI_COMMU") {
			dsz = x10Copy(srcspa, srcColOff, smlist, dstpid, dstColOff, colCnt);
		}
		return dsz;
	}


	/**
	 * Based on mpi send/recv, copy data from here to remote place
	 */
	protected static def mpiCopy(
			srcspa:SparseCSC, srcColOff:Int,
			smlist:DistArray[SparseBlock](1), dstpid:Int, dstColOff:Int, 
			colCnt:Int): Int {

		Debug.assure(srcColOff+colCnt <= srcspa.N,
					 "At source place, illegal column offset and count");

		val srcpid = here.id();        
		val datasz = srcspa.countNonZero(srcColOff, colCnt); //Implicitly carried to dst place
		@Ifdef("MPI_COMMU") {
			finish {
				at (smlist.dist(dstpid)) async {
					// Need: smlist, srcpid, dstColOff, datasz;
					val dstspa = smlist(here.id()).getMatrix();
					val dstoff = dstspa.getNonZeroOffset(dstColOff);
					val tag    = srcpid * 10000 + here.id();
					
					//++++++++++++++++++++++++++++++++++++++++++++
					//Do NOT call getIndex()/getValue() before init at destination place
					//+++++++++++++++++++++++++++++++++++++++++++++
					dstspa.initRemoteCopyAtDest(dstColOff, colCnt, datasz);
					WrapMPI.world.recv(dstspa.getIndex(), dstoff, datasz, srcpid, tag);
					WrapMPI.world.recv(dstspa.getValue(), dstoff, datasz, srcpid, tag+100001);
					dstspa.finalizeRemoteCopyAtDest();
				}
				async {
					val tag    = srcpid * 10000 + dstpid;
					val srcoff = srcspa.getNonZeroOffset(srcColOff);
					
					srcspa.initRemoteCopyAtSource(srcColOff, colCnt);
					WrapMPI.world.send(srcspa.getIndex(), srcoff, datasz, dstpid, tag);
					WrapMPI.world.send(srcspa.getValue(), srcoff, datasz, dstpid, tag+100001);
					srcspa.finalizeRemoteCopyAtSource();
				}			
			}
		}
		return datasz;
	}


	//Sparse matrix remote copy To
	protected static def x10Copy(
			srcspa:SparseCSC, srcColOff:Int,
			dst:DistArray[SparseBlock](1), dstbid:Int,  dstColOff:Int, 
			colCnt:Int) : Int {

		Debug.assure(srcColOff+colCnt <= srcspa.N,
					 "At source place illegal column offset and count:");

		val idxbuf = srcspa.getIndex() as Array[Int]{self!=null};
		val valbuf = srcspa.getValue() as Array[Double]{self!=null};
		val datoff = srcspa.getNonZeroOffset(srcColOff);
		val datcnt = srcspa.initRemoteCopyAtSource(srcColOff, colCnt);
		val rmtidx = new RemoteArray[Int](idxbuf);
		val rmtval = new RemoteArray[Double](valbuf);

		at (dst.dist(dstbid)) {
			//Implicit copy:dst, datcnt, rmtidx, rmtval, datoff
			val dstspa = dst(here.id()).getMatrix();

			//Debug.flushln("Recv data dst:"+dstbid+" off:"+datoff+
			//			  " cnt:"+datcnt+" storage size"+dstspa.getStorage().storageSize());
			//++++++++++++++++++++++++++++++++++++++++++++
			//Do not call getIndex()/getValue() before init at destination place
			//+++++++++++++++++++++++++++++++++++++++++++++
			dstspa.initRemoteCopyAtDest(dstColOff, colCnt, datcnt);
			val dstoff = dstspa.getNonZeroOffset(dstColOff);
			finish Array.asyncCopy[Int   ](rmtidx, datoff, dstspa.getIndex(), dstoff, datcnt);
			finish Array.asyncCopy[Double](rmtval, datoff, dstspa.getValue(), dstoff, datcnt);
			dstspa.finalizeRemoteCopyAtDest();
		}

		srcspa.finalizeRemoteCopyAtSource();
		return datcnt;
	}



	//--------------------------------------------------
	// Sparse Block copy From
	//--------------------------------------------------
	/**
	 * Copy multiple columns of the sparseCSC matrix in the specified place to
	 * here
	 *
	 * @param smlist  		source sparse matrix block in DistArray
	 * @param srcpid 		source matrix's place id.
	 * @param srcColOff  	column offset in source matrix
	 * @param dstspa 		destination sparse matrix of the copy
	 * @param dstColOff  	column offset in target matrix
	 * @param colCnt  		count of columns to be copied in source matrix
	 * @return 				number of elements copied
	 */
	public static def copy(
			smlist:DistArray[SparseBlock](1), srcpid:Int, srcColOff:Int,
			dstspa:SparseCSC, dstColOff:Int, 
			colCnt:Int): Int {
		
		var dsz:Int = 0;
		if (here.id() == srcpid) {
			dsz = SparseCSC.copyCols(smlist(srcpid).getMatrix(), srcColOff,  
									 dstspa, dstColOff, colCnt);
			return dsz;
		}

		@Ifdef("MPI_COMMU") {
			dsz = mpiCopy(smlist, srcpid, srcColOff, dstspa, dstColOff, colCnt);
		}

		@Ifndef("MPI_COMMU") {
			dsz = x10Copy(smlist, srcpid, srcColOff, dstspa, dstColOff, colCnt);
		}
		return dsz;
	}

	//Remote sparse matrix copy from
	protected static def mpiCopy(
			src:DistArray[SparseBlock](1), srcbid:Int, srcColOff:Int,
			dstspa:SparseCSC, dstColOff:Int, 
			colCnt:Int) : Int {

		val dstbid = here.id();
		val dsz = at (src.dist(srcbid)) 
			src(here.id()).getMatrix().countNonZero(srcColOff, colCnt); //Implicitly carried to dst place
		@Ifdef("MPI_COMMU") {
			finish {
				at (src.dist(srcbid)) async {
					//Need: dstbid, src, srcColOff, datCnt
					val srcspa = src(here.id()).getMatrix();
					val tag    = here.id() * 40000 + dstbid;
					val off    = srcspa.getNonZeroOffset(srcColOff);
					val datcnt = srcspa.countNonZero(srcColOff, colCnt);
					
					srcspa.initRemoteCopyAtSource();
					WrapMPI.world.send(srcspa.getIndex(), off, datcnt, dstbid, tag);
					WrapMPI.world.send(srcspa.getValue(), off, datcnt, dstbid, tag+1000);
					srcspa.finalizeRemoteCopyAtSource();
				}
				async {
					val tag = srcbid * 40000 + dstbid;
					val off = dstspa.getNonZeroOffset(dstColOff);
					
					dstspa.initRemoteCopyAtDest(dstColOff, colCnt, dsz);
					//++++++++++++++++++++++++++++++++++++++++++++
					//Do NOT call getIndex()/getValue() before init
					//+++++++++++++++++++++++++++++++++++++++++++++
					WrapMPI.world.recv(dstspa.getIndex(), off, dsz, srcbid, tag);
					WrapMPI.world.recv(dstspa.getValue(), off, dsz, srcbid, tag+1000);
					dstspa.finalizeRemoteCopyAtDest();
				}
			}
		}
		return dsz;
	}

	//Sparse matrix remote copyt from
	protected static def x10Copy(
			src:DistArray[SparseBlock](1), srcbid:Int, srcColOff:Int,
			dstspa:SparseCSC, dstColOff:Int, 
			colCnt:Int) : Int {

		val rmt:SparseRemoteSourceInfo  = at (src.dist(srcbid)) { 
			//Need: src, srcColOff, colCnt
			val mat = src(here.id()).getMatrix();
			val off = mat.getNonZeroOffset(srcColOff);
			val cnt = mat.initRemoteCopyAtSource(srcColOff, colCnt);

			SparseRemoteSourceInfo(mat.getIndex(), mat.getValue(),  off, cnt)
		};

		val datoff = dstspa.getNonZeroOffset(dstColOff);

		//Debug.flushln("Recv data src:"+srcbid+" off:"+datoff+" cnt:"+rmt.length);
		//++++++++++++++++++++++++++++++++++++++++++++
		//Do NOT call getIndex()/getValue() before init
		//+++++++++++++++++++++++++++++++++++++++++++++
		dstspa.initRemoteCopyAtDest(dstColOff, colCnt, rmt.length);
		finish Array.asyncCopy[Int   ](rmt.idxbuf, rmt.offset, dstspa.getIndex(), datoff, rmt.length);
		finish Array.asyncCopy[Double](rmt.valbuf, rmt.offset, dstspa.getValue(), datoff, rmt.length);
	
		//Rebuild or reset indexing
		finish {
			at  (src.dist(srcbid)) async {
				val mat  = src(here.id()).getMatrix();
				mat.finalizeRemoteCopyAtSource();
			}
			async {
				dstspa.finalizeRemoteCopyAtDest();
			}
		}
		return rmt.length;
	}
	
	//==================================================================
	// Vectory copy to/from
	//==================================================================

	/**
	 * Copy multiple columns from source dense matrix at here to the specified remote 
	 * place in DistArray of dense matrix.
	 * 
	 * @param src    		the source dense vector array
	 * @param srcOff 		starting offset in source matrix
	 * @param dmlist    	target dense matrix in DistArray
	 * @param dstpid    	the place id of target dense matrix in DistArray.
	 * @param dstColOff 	starting column offset in target matrix at destination place
	 * @param dataCnt   	count of data to be copied from source vector
	 */
	public static def copy(
			src:Array[Double](1), srcOff:Int,
			dmlist:DistArray[DenseMatrix](1), 
			dstpid:Int, dstColOff:Int, 
			dataCnt:Int): void {
		
		var dsz:Int = 0;
		
		if (here.id() == dstpid) {
			val dstmat = dmlist(dstpid);
			Array.copy(src, srcOff, dstmat.d, dstColOff*dstmat.M, dataCnt);
			return;
		}

		@Ifdef("MPI_COMMU") {
			{
				mpiCopy(src, srcOff, dmlist, dstpid, dstColOff, dataCnt);
			}
		}
		@Ifndef("MPI_COMMU") {
			{
				x10Copy(src, srcOff, dmlist, dstpid, dstColOff, dataCnt);
			}
		}
	}

	
	/*
	 * Copy vector from here to remote dense matrix
	 */
	protected static def mpiCopy(
			src:Array[Double](1), srcOff:Int, 
			dmlist:DistArray[DenseMatrix](1), dstpid:Int, dstColOff:Int, 
			dataCnt:Int) :void  {
		
		Debug.assure(srcOff+dataCnt <= src.size,
				"At source place, illegal data offset:"+srcOff+
				                                       " or data count:"+dataCnt);
		val srcpid = here.id();         //Implicitly carried to dst place
		@Ifdef("MPI_COMMU") {
			finish {
				// At the destination place, receiving the data 
				at (dmlist.dist(dstpid)) async {
					//Need: dmlist, srcpid, dstColOff, datasz
					val dstden = dmlist(here.id());
					val dstoff = dstden.M * dstColOff;
					val tag    = srcpid * 10000 + here.id();
					WrapMPI.world.recv(dstden.d, dstoff, dataCnt, srcpid, tag);
				}
				async {
					// At the source place, sending out the data
					val tag = srcpid * 10000 + dstpid;
					WrapMPI.world.send(src, srcOff, dataCnt, dstpid, tag);
				}
			}
		}
	}
	/**
	 * Copy vector from here to remote dense matrix
	 */
	protected static def x10Copy(
			src:Array[Double](1), srcOff:Int,
			dmlist:DistArray[DenseMatrix](1), dstpid:Int, dstColOff:Int, 
			dataCnt:Int): void {

		Debug.assure(srcOff + dataCnt <= src.size,
		"at source place, illegal column offset and count");
		val buf = src as Array[Double]{self!=null};
		val srcbuf = new RemoteArray[Double](buf);

		at (dmlist.dist(dstpid)) {
			//Implicit copy: dst, srcbuf, srcOff, dataCnt
			val dstden = dmlist(here.id());
			val dstoff = dstColOff * dstden.M;

			Debug.assure(dstColOff*dstden.M+dataCnt <= dstden.M*dstden.N);
			finish Array.asyncCopy[Double](srcbuf, srcOff, dstden.d, dstoff, dataCnt);		
		}
	}
	
	//------------------------------------------
	// Dense matrix copy From 
	//------------------------------------------
	/**
	 * Copy multiple columns of the dense matrix in the specified place to
	 * here
	 * 
	 * @param dmlist   		source matrix in the dist dense matrix
	 * @param srcpid    	source matrix's place id.
	 * @param srcColOff 	starting column in in source matrix
	 * @param dst      		destination vector array
	 * @param dstOff   		starting offset in vector
	 * @param dataCnt   	count of data to copy
	 */
	public static def copy(
			dmlist:DistArray[DenseMatrix](1), srcpid:Int, srcColOff:Int, 
			dst:Array[Double](1), dstOff:Int, 
			dataCnt:Int): void {

		if (here.id() == srcpid) {
			val srcden = dmlist(srcpid);
			Array.copy(srcden.d, srcColOff*srcden.M, dst, dstOff, dataCnt);
			return;
		}

		@Ifdef("MPI_COMMU") {
			mpiCopy(dmlist, srcpid, srcColOff, dst, dstOff, dataCnt);
		}

		@Ifndef("MPI_COMMU") {
			x10Copy(dmlist, srcpid, srcColOff, dst, dstOff, dataCnt);
		}
	}

	/**
	 * Copy data from remote matrix to here in a vector
	 */
	protected static def mpiCopy(
			dmlist:DistArray[DenseMatrix](1), srcpid:Int, srcColOff:Int,
			dst:Array[Double](1), dstOff:Int, 
			dataCnt:Int): void {
		
		Debug.assure(dstOff+dataCnt <= dst.size,
		"At destination place, illegal column offset or count");

		val dstpid = here.id();
		@Ifdef("MPI_COMMU") {
			finish {
				at (dmlist.dist(srcpid)) async {
					//Need: dmlist, dstpid, srcColOff, dataCnt, 
					val srcden = dmlist(here.id());
					val srcoff = srcden.M * srcColOff;
					val tag    = here.id() * 20000 + dstpid;
					WrapMPI.world.send(srcden.d, srcoff, dataCnt, dstpid, tag);
				}
				async {
					val tag    = srcpid * 20000 + dstpid;
					WrapMPI.world.recv(dst, dstOff, dataCnt, srcpid, tag);
				}
			}
		}
	}	
	/**
	 * Copy data from remote dense matrix to here in a vector
	 */
	protected static def x10Copy(
			dmlist:DistArray[DenseMatrix](1), srcpid:Int, srcColOff:Int,
			dst:Array[Double](1), dstOff:Int, 
			dataCnt:Int): void {

		val rmt:DenseRemoteSourceInfo  = at (dmlist.dist(srcpid)) { 
			//Need: dmlist, srcColOff, dataCnt
			val mat = dmlist(here.id());
			val off = srcColOff * mat.M;
			Debug.assure(off + dataCnt <= mat.M * mat.N, 
			"Matrix remote copy fails! Illegal size at source matrix");
			DenseRemoteSourceInfo(mat.d, off, dataCnt)
		};

		Debug.assure(dstOff+rmt.length <= dst.size,
		"Matrix remote copy fails! Illegal size at target matrix");
		
		finish Array.asyncCopy[Double](rmt.valbuf, rmt.offset, dst, dstOff, dataCnt);
	}
	
	/**
	 * Copy multiple columns from source dense matrix at here to the specified remote 
	 * place in DistArray of dense matrix.
	 * 
	 * @param src   		the source vector array at here
	 * @param srcOff 		starting offset in in source vector array
	 * @param dmlist    	target dense blocks in DistArray
	 * @param dstpid   		the place id of target dense matrix in DistArray.
	 * @param dstColOff 	starting column offset in target matrix at destination place
	 * @param dataCnt    	count of data to copy from source dense matrix
	 */
	public static def copy(
			src:Array[Double](1), srcOff:Int,
			dmlist:DistArray[DenseBlock](1), dstpid:Int, dstColOff:Int, 
			dataCnt:Int): void {
		
		if (here.id() == dstpid) {
			val dstden = dmlist(dstpid).getMatrix();
			Array.copy(src, srcOff, dstden.d, dstColOff*dstden.M, dataCnt);
			return ;
		}

		@Ifdef("MPI_COMMU") {
			mpiCopy(src, srcOff, dmlist, dstpid, dstColOff, dataCnt);
		}
		@Ifndef("MPI_COMMU") {
			x10Copy(src, srcOff, dmlist, dstpid, dstColOff, dataCnt);
		}
	}
	
	/**
	 * Copy vector from here to remote dense matrix
	 */
	protected static def mpiCopy(
			src:Array[Double](1), srcOff:Int,
			dst:DistArray[DenseBlock](1), dstbid:Int, dstColOff:Int, 
			dataCnt:Int):void {
		
		val srcbid = here.id();
		@Ifdef("MPI_COMMU") {

			finish {
				at (dst.dist(dstbid)) async {
					//Need: srcbid, dst, dstColOff, dataCnt
					val dstden = dst(here.id()).getMatrix();
					val tag    = srcbid * 60000 + here.id();
					val off    = dstden.M * dstColOff;
					WrapMPI.world.recv(dstden.d, off, dataCnt, srcbid, tag);
				}
				async {
					val tag = here.id() * 60000 + dstbid;
					// src and dst must have the same leading dimension
					WrapMPI.world.send(src, srcOff, dataCnt, dstbid, tag);
				}
			}
		}
	}
	
	/**
	 * Copy vector from here to remote block 
	 */
	protected static def x10Copy(
			src:Array[Double](1),  srcOff:Int,
			dst:DistArray[DenseBlock](1), dstbid:Int, dstColOff:Int, 
			dataCnt:Int): void {

		val buf = src as Array[Double]{self!=null};
		val srcbuf = new RemoteArray[Double](buf);
		
		Debug.assure(srcOff+dataCnt <= src.size);
		at (dst.dist(dstbid)) {
			//Implicit copy: dst, srcbuf, srcOff, dataCnt
			val dstden = dst(here.id()).getMatrix();
			val dstoff = dstColOff * dstden.M;

			Debug.assure(dstColOff*dstden.M+dataCnt <= dstden.M*dstden.N);
			finish Array.asyncCopy[Double](srcbuf, srcOff, dstden.d, dstoff, dataCnt);		
		}
	}
	
	/**
	 * Copy multiple columns from source dense matrix at here to the specified remote 
	 * place in DistArray of dense matrix.
	 * 
	 * @param dmlist    	target dense blocks in DistArray
	 * @param srcpid    	the place id of target dense matrix in DistArray.
	 * @param srcColOff 	starting column in in source matrix
	 * @param dst       	the target vector array at here
	 * @param dstOff    	starting offset in target vector array
	 * @param dataCnt   	count of data to be copied from source dense matrix
	 */
	public static def copy(
			dmlist:DistArray[DenseBlock](1), srcpid:Int, srcColOff:Int,
			dst:Array[Double](1), dstOff:Int, 
			dataCnt:Int): void {
		
		if (here.id() == srcpid) {
			val srcden = dmlist(srcpid).getMatrix();
			Array.copy(srcden.d, srcColOff*srcden.M, dst, dstOff, dataCnt);
			return;
		}

		@Ifdef("MPI_COMMU") {
			mpiCopy(dmlist, srcpid, srcColOff, dst, dstOff, dataCnt);
		}
		@Ifndef("MPI_COMMU") {
			x10Copy(dmlist, srcpid, srcColOff, dst, dstOff, dataCnt);
		}
	}
	
	/**
	 * Copy data from remote block to here in vector
	 */
	protected static def mpiCopy(
			src:DistArray[DenseBlock](1), srcbid:Int, srcColOff:Int,
			dst:Array[Double](1), dstOff:Int, 
			dataCnt:Int) : void {

		val dstbid = here.id();
		@Ifdef("MPI_COMMU") {

			finish {
				at (src.dist(srcbid)) async {
					//Need:src, srcColOff, dataCnt
					val srcden = src(here.id()).getMatrix();
					val tag = here.id() * 30000 + dstbid;
					val off = srcden.M*srcColOff;
					WrapMPI.world.send(srcden.d, off, dataCnt, dstbid, tag);
				}
				async {
					val tag = srcbid * 30000 + dstbid;
					// src and dst must have the same leading dimension
					WrapMPI.world.recv(dst, dstOff, dataCnt, srcbid, tag);
				}
			}
		}
	}
	
	/**
	 * Copy data from remote block to here in vector 
	 */
	protected static def x10Copy(
			src:DistArray[DenseBlock](1), srcbid:Int, srcColOff:Int,
			dst:Array[Double](1), dstOff:Int, 
			dataCnt:Int): void {

		val rmt:DenseRemoteSourceInfo = at (src.dist(srcbid)) { 
			//Implicit copy: src, srcColOff, datdCnt, 
			
			val mat = src(here.id()).getMatrix();
			val off = srcColOff * mat.M;
			Debug.assure(off+dataCnt <= mat.M * mat.N,
			"Matrix remote copy fails! Illegal size at source matrix");
			DenseRemoteSourceInfo(mat.d, off, dataCnt)
		};

		Debug.assure(dstOff+dataCnt <= dst.size, 
		"Matrix remote copy fails! Illegal size at target matrix");
		
		finish Array.asyncCopy[Double](rmt.valbuf, rmt.offset, dst, dstOff, dataCnt);
	}
	
}
