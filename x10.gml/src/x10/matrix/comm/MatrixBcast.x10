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
import x10.matrix.ElemType;

import x10.matrix.DenseMatrix;
import x10.matrix.comm.mpi.WrapMPI;
import x10.matrix.sparse.SparseCSC;

/**
 * This class provides broadcast functions for dense and sparse matrices.
 * The result is defined in DistAarray structure which stores the broadcast data at all places.
 * 
 * <p> Two implementations are available. One uses MPI routines, 
 * and the other is based on X10 remote array copy.
 * To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class MatrixBcast {
	/**
	 * Broadcast dense matrix from here to all other places.
	 * This routine is used in sync of DupDenseMatrix
	 *
	 * @param dmat    Input-output. Distributed storage for the source and copies of dense matrix in all places
	 * @return        count of double-precision data to broadcast
	 */
	public static def bcast(dupmat:DistArray[DenseMatrix](1)) = bcast(dupmat, 0, dupmat(here.id()).N);

	/**
	 * Broadcast dense matrix from here to all other places. 
	 * This routine is used in sync of DupDenseMatrix
	 *
	 * @param dupmat      Input+output. Distributed storage for dense matrix in all places
	 * @param coloff      Input. Offset for the starting column for broadcast
	 * @param colcnt      Input. Count of columns to broadcast
	 * @return            Number of elements to broadcast
	 */
	public static def bcast(dupmat:DistArray[DenseMatrix](1), coloff:Long, colcnt:Long):Long {
		var datasz:Long = 0;
		
		@Ifdef("MPI_COMMU") {
			datasz = mpiBcast(dupmat, coloff, colcnt);
		}
		@Ifndef("MPI_COMMU") {
			datasz = x10Bcast(dupmat, coloff, colcnt);
		}
		return datasz;
	} 

	/**
	 * Broadcast dense matrix stored by using MPI bcast routine.
	 *
	 * @param dmlist      Distributed storage for dense matrices in all places
	 * @param colOff      Offset for the starting column
	 * @param colCnt      Number of columns to broadcast
	 * @return            Number of elements broadcast
	 */
	protected static def mpiBcast(dmlist:DistArray[DenseMatrix](1), colOff:Long, colCnt:Long):Long {
		if (dmlist.dist.region.size() <= 1) return 0;

		val root   = here.id();
		val datasz = dmlist(root).M * colCnt;  //Using global matrix M to compute data size
		
		@Ifdef("MPI_COMMU") {
			finish ateach([p] in dmlist.dist) {
				//Need: dmlist, datasz, root and colOff
				val denmat = dmlist(here.id());	
				val offset = denmat.M * colOff;
				WrapMPI.world.bcast(denmat.d, offset, datasz, root);
			}
		}
		return datasz;
	}

	/**
	 *  Broadcast dense matrix among the pcnt number of places followed from here
	 */
	protected static def x10Bcast(dmlist:DistArray[DenseMatrix](1), colOff:Long, colCnt:Long):Long {
		val pcnt   = dmlist.dist.region.size();
		val datasz = dmlist(here.id()).M * colCnt;

		if (pcnt <= 1 || colCnt == 0L) return 0;
		
		binaryTreeCast(dmlist, colOff, colCnt, pcnt);
		return datasz;
	}
		
	/**
	 * X10 implementation of broadcast data in binary tree routes.
	 */
	protected static def binaryTreeCast(
			dmlist:DistArray[DenseMatrix](1), 
			colOff:Long, colCnt:Long, 
			pcnt:Long): void {
		
		val root   = here.id();
		val srcden = dmlist(root);
		val lfcnt:Long = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = root + lfcnt;

		// Specify the remote buffer
		val srcbuf = new GlobalRail[ElemType](srcden.d as Rail[ElemType]{self!=null});
		val srcoff = srcden.M * colOff; //Source offset is determined by local M
			
		finish {
			at(dmlist.dist(rtroot)) async {
				val dstden = dmlist(here.id());
				val dstoff = colOff * dstden.M; //Destination offset is determined by local M
				val datasz = dstden.M * colCnt;
				// Using copyFrom style
				finish Rail.asyncCopy[ElemType](srcbuf, srcoff, dstden.d, dstoff, datasz);
							
				// right branch
				if (rtcnt > 1) {
					binaryTreeCast(dmlist, colOff, colCnt, rtcnt);
				}
			}

			// left branch
			if (lfcnt > 1) {
				binaryTreeCast(dmlist, colOff, colCnt, lfcnt); 
			}
		}
	}

	/**
	 * Broadcast sparseCSC matrix from here to all other places.
	 * This routine is used in sync DupSparseMatrix
	 *
	 * @param smlist      Input+output. Distributed storage for source and copies of sparse matrix in all places.
	 * @return      Number of elements in broadcast
	 */
	public static def bcast(smlist:DistArray[SparseCSC](1)) = bcast(smlist, 0, smlist(here.id()).N);

	/**
	 * Broadcast sparse matrix from here to all other places. 
	 * This routine is used in sync of DupDenseMatrix
	 *
	 * @param smlist      Input+output. Distributed storage for source and copies of sparse matrix in all places.x
	 * @param colOff      Offset for the starting column 
	 * @param colCnt      Number of columns to broadcast
	 * @return      Number of elements in broadcast
	 */
	public static def bcast(
			smlist:DistArray[SparseCSC](1), 
			colOff:Long, colCnt:Long):Long {
		
		var datasz:Long = 0;
		@Ifdef("MPI_COMMU") {
			datasz = mpiBcast(smlist, colOff, colCnt);
		}
		@Ifndef("MPI_COMMU") {
			datasz = x10Bcast(smlist, colOff, colCnt);
		}
		return datasz;
	}


	/**
	 * Using MPI routine to implement sparse matrix broadcast
	 */
	protected static def mpiBcast(
			smlist:DistArray[SparseCSC](1),
			colOff:Long, colCnt:Long):Long {
	
		if (smlist.dist.region.size() <= 1) return 0;

		val root   = here.id();
		val datasz = smlist(root).countNonZero(colOff,colCnt);  
		@Ifdef("MPI_COMMU") {

			finish ateach([p] in smlist.dist) {
				//Need: root, smlist, datasz, colOff, colCnt,
				val spamat = smlist(here.id());	
				val offset = spamat.getNonZeroOffset(colOff);
				
				//++++++++++++++++++++++++++++++++++++++++++++
				//Do NOT call getIndex()/getValue() before init at destination place
				//+++++++++++++++++++++++++++++++++++++++++++++
				if (p == root) 
					spamat.initRemoteCopyAtSource(colOff, colCnt);
				else
					spamat.initRemoteCopyAtDest(colOff, colCnt, datasz);
				
				WrapMPI.world.bcast(spamat.getIndex(), offset, datasz, root);
				WrapMPI.world.bcast(spamat.getValue(), offset, datasz, root);
				
				if (p == root) 
					spamat.finalizeRemoteCopyAtSource();
				else
					spamat.finalizeRemoteCopyAtDest();
			}
		}
		return datasz;		
	}

	/**
	 *  Broadcast sparse matrix among the pcnt number of places followed
	 */
	protected static def x10Bcast(
			smlist:DistArray[SparseCSC](1), 
			colOff:Long, 
			colCnt:Long):Long {

		val pcnt = smlist.dist.region.size();
		if (pcnt <= 1 || colCnt == 0L) return 0;
		
		val srcmat = smlist(here.id());
		val srcoff = srcmat.getNonZeroOffset(colOff);
		val datasz = srcmat.countNonZero(colOff, colCnt);

		srcmat.initRemoteCopyAtSource(colOff, colCnt);

		binaryTreeCast(smlist, colOff, srcoff, colCnt, datasz, pcnt);

		srcmat.finalizeRemoteCopyAtSource();

		return datasz;
	}

	/**
	 * Broadcast sparse matrix using remote array copy in X10
	 */
	protected static def binaryTreeCast(
			smlist:DistArray[SparseCSC](1), 
			colOffset:Long, srcOffset:Long, 
			colCnt:Long, dataCnt:Long, 
			pcnt:Long): void {
		
		val myid = here.id();
		val lfcnt:Long = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = myid + lfcnt;

		// Specify the remote buffer
		val srcspa = smlist(myid);
        val idxbuf = srcspa.getIndex();
        val valbuf = srcspa.getValue();
        val srcidx = new GlobalRail[Long  ](idxbuf as Rail[Long  ]{self!=null});
        val srcval = new GlobalRail[ElemType](valbuf as Rail[ElemType]{self!=null});
	
		finish {		
			at(smlist.dist(rtroot)) async {
				//Need: smlist, srcidx, srcval, srcOff, colOff, colCnt and datasz
				val dstspa = smlist(here.id());
				val dstoff = dstspa.getNonZeroOffset(colOffset); 
				// Using copyFrom style
				//++++++++++++++++++++++++++++++++++++++++++++
				//Do NOT call getIndex()/getValue() before init at destination place
				//+++++++++++++++++++++++++++++++++++++++++++++
				dstspa.initRemoteCopyAtDest(colOffset, colCnt, dataCnt);
				finish Rail.asyncCopy[Long  ](srcidx, srcOffset, 
											   dstspa.getIndex(), dstoff, dataCnt);
				finish Rail.asyncCopy[ElemType](srcval, srcOffset, 
											   dstspa.getValue(), dstoff, dataCnt);

				// right branch
				if (rtcnt > 1) {
					binaryTreeCast(smlist, colOffset, dstoff, colCnt, dataCnt, rtcnt);
					dstspa.finalizeRemoteCopyAtDest();
				} else {
					dstspa.finalizeRemoteCopyAtDest();
				}
			}

			// left branch
			if (lfcnt > 1) {
				binaryTreeCast(smlist, colOffset, srcOffset, colCnt, dataCnt, lfcnt); 
			}
		}
	}
}
