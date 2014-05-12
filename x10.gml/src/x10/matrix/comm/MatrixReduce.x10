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

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.comm.mpi.WrapMPI;

/**
 * The class provides reduce-sum communication for distributed matrix,
 * 
 * <p> This operation adds matrix blocks from all places and stores result
 * in dense format at here, using provided temporary dense matrix blocks
 * space to receive data.
 *
 * <p> Two implementations are available. One uses MPI routines, and the other
 * is based on X10 remote array copy.
 * To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class MatrixReduce {
	/**
	 * Reduce all dense matrices stored in DistArray from all places
	 * to here
	 *
	 * @param ddmat      Input/Output. Duplicated dense matrix of source and target matrix
	 * @param ddtmp      Temp matrix space to store the receiving data.
	 * @return           Number of elements to received
	 */
	public static def reduceSum(
			ddmat:DistArray[DenseMatrix](1),
			ddtmp:DistArray[DenseMatrix](1)):Long {
		var datasz:Long = 0;
		
		@Ifdef("MPI_COMMU") {
			datasz = mpiReduceSum(ddmat, ddtmp);
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast");
			datasz = x10ReduceSum(ddmat, ddtmp);
		}
		return datasz;
	} 

	/**
	 * Perform reduce sum of all matrces stored in the duplicated matrix
	 * Result is stored in the matrix at root place.
	 *
	 * @param ddmat     input and output matrix. 
	 * @param ddtmp     temp matrix storing the inter-place communication data.
	 */
	public static def mpiReduceSum(
			ddmat:DistArray[DenseMatrix](1),
			ddtmp:DistArray[DenseMatrix](1)):Long {
		
		val root = here.id();
		@Ifdef("MPI_COMMU") {
			finish ateach([p] in ddmat) {
				val pid = here.id();
				val dst = ddmat(pid);
				val sz  = dst.M * dst.N;
				val src:DenseMatrix(dst.M, dst.N) = ddtmp(pid) as DenseMatrix(dst.M, dst.N);
				//Check temp space
				//if (ddtmp(pid) == null) 
				//	ddtmp(pid) = dst.alloc();
				//src = ddtmp(pid) as DenseMatrix(dst.M, dst.N);
				dst.copyTo(src);
				// Counting the all reduce-sum time in communication
				WrapMPI.world.reduceSum(src.d, dst.d, sz, root);
			}
		}
		return ddmat(root).M * ddmat(root).N;
	}

	/**
	 * Sum of all matrices in the duplicated matrix and the result overwrite the
	 * matrix at here.
	 *
	 * @param ddmat     input and output matrix. 
	 * @param ddtmp     temp matrix storing the inter-place communication data.
	 */
	public static def x10ReduceSum(
			ddmat:DistArray[DenseMatrix](1), 
			ddtmp:DistArray[DenseMatrix](1)):Long {
		val root = here.id();
		val mat  = ddmat(root);
		val pcnt = ddmat.region.size();

		reduceSumToHere(ddmat, ddtmp, pcnt);

		return mat.M*mat.N;
	}

	/**
	 * Binary recursive reduce sum.
	 * Notice dmat is input and output matrix.
	 */
	protected static def reduceSumToHere(
			ddmat:DistArray[DenseMatrix](1), 
			ddtmp:DistArray[DenseMatrix](1),
			var pcnt:Long): void {
		
		val root = here.id();
		val ttpcnt = ddmat.region.size();
		val dstden = ddmat(root);
		
		if (root + pcnt > ttpcnt) pcnt = ttpcnt-root;
		if (pcnt <= 1) return;

		val lfcnt  = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = root + lfcnt;
		if (pcnt > 2) {
			finish {
				if (lfcnt > 1) async {
					reduceSumToHere(ddmat, ddtmp, lfcnt); 
				}
				if (rtcnt > 1 ) {
					at(ddmat.dist(rtroot)) async {
						reduceSumToHere(ddmat, ddtmp, rtcnt);
					}
				}
			}
		}
		val tmpden = ddtmp(root) as DenseMatrix(dstden.M, dstden.N);
		//if (ddtmp(root)==null)	ddtmp(root) = dstden.alloc(); 
		//tmpden = ddtmp(root) as DenseMatrix(dstden.M, dstden.N);
		
		MatrixRemoteCopy.x10Copy(ddmat, rtroot, 0L, tmpden, 0L, tmpden.N);
		dstden.cellAdd(tmpden);		
	}

	/**
	 * Perform all reduce sum operation. 
	 * @see reduceSum()
	 * Result is synchronized for all copies of duplicated matrix
	 *
	 * @param ddmat     input and output matrix. 
	 * @param ddtmp     temp matrix storing the inter-place communication data.
	 */
	public static def allReduceSum(
			ddmat:DistArray[DenseMatrix](1),
			ddtmp:DistArray[DenseMatrix](1)):Long {
		var datasz:Long = 0;
		
		@Ifdef("MPI_COMMU") {
			datasz = mpiAllReduceSum(ddmat, ddtmp);
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			datasz = x10AllReduceSum(ddmat, ddtmp);
		}
		return datasz;
	} 

	/**
	 * Perform all reduce sum operation. 
	 * @see reduceSum()
	 * Result is synchronized for all copies of duplicated matrix
	 *
	 * @param ddmat     input and output matrix. 
	 * @param ddtmp     temp matrix storing the inter-place communication data.
	 */
	protected static def mpiAllReduceSum(
			ddmat:DistArray[DenseMatrix](1),
			ddtmp:DistArray[DenseMatrix](1)):Long {
		
		val root = here.id();
		//Debug.flushln("Start all reduce");
		@Ifdef("MPI_COMMU") {
			finish ateach([p] in ddmat) {
				val pid = here.id();
				val dst = ddmat(pid);
				val sz  = dst.M * dst.N;
				val src = ddtmp(pid) as DenseMatrix(dst.M, dst.N);
				//Check temp space
				//if (ddtmp(pid) == null)
				//	ddtmp(pid) = dst.alloc();
				//src = ddtmp(pid) as DenseMatrix(dst.M, dst.N);
				dst.copyTo(src);
				// Counting the all reduce-sum time in communication

				//Execution hang-up.
				//X10 MPI runtime conflicts with MPI calls 
				//WrapMPI.world.allReduceSum(src.d, dst.d, sz);

				//Work-around. Not optimized
				WrapMPI.world.reduceSum(src.d, dst.d, sz, root);
				WrapMPI.world.bcast(dst.d, 0, sz, root);
			}
			
		}
		//Debug.flushln("All reduce complete");
		
		return ddmat(root).M * ddmat(root).N;
	}

	protected static def x10AllReduceSum(ddmat:DistArray[DenseMatrix](1),
								  ddtmp:DistArray[DenseMatrix](1)):Long {
		val root = here.id();
		val den = ddmat(root);
		val sz = x10ReduceSum(ddmat, ddtmp);

		MatrixBcast.x10Bcast(ddmat, 0, den.N);
		return sz;
	}

	/**
	 * Perform reduce sum of all matrces stored in the duplicated matrix
	 * from specified list of places. This method is not optimized.
	 * Result is stored in the matrix at root place.
	 * 
	 * @param ddmat     input and output matrix. 
	 * @param tmp       temp matrix storing the inter-place communication data at root.
	 * @param colcnt    column count
	 * @param plist     list of place ids
	 */
	public static def reduceSum(
			ddmat:DistArray[DenseMatrix](1),
			tmp:DenseMatrix, colcnt:Long,
			plist:Rail[Long]):Long {

		val root = here.id();
		val rtden = ddmat(here.id());
		val dstbuf = rtden.d;
		val dstden = new DenseMatrix(rtden.M, colcnt, dstbuf);
		val srcden = new DenseMatrix(rtden.M, colcnt, tmp.d);
		for (placeId in plist) {
			if (placeId != here.id()) {
				MatrixRemoteCopy.copy(ddmat, placeId, 0L, srcden, 0L, colcnt);
				dstden.cellAdd(srcden);
			}
		}
		return dstden.M * dstden.N;
	}
}

