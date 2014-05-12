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

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.util.Debug;
import x10.matrix.comm.mpi.WrapMPI;

/**
 * This class broadcasts data in double-precision or CompressArray to all places
 * 
 * <p> Two implementations are available, using MPI routines and X10 remote array copy.
 * To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class DistArrayBcast extends DistArrayRemoteCopy {
	/**
	 * Broadcast double-precision data array from here to all other places.
	 *
	 * @param dmat   distributed storage for source and copies of data array.
	 */
	public static def bcast(duplist:DistDataArray) {
		val data = duplist(here.id());
		bcast(duplist, data.size);
	}

	/**
	 * Broadcast double-precision data array from here to all other places
	 *
	 * @param duplist    distributed storage for source and copies of data array.
	 * @param dataCnt    count of double-precision data to broadcast
	 */
	public static def bcast(duplist:DistDataArray, dataCnt:Long) : void {
		Debug.assure(dataCnt <= duplist(here.id()).size, "Data overflow in bcast");
		
		@Ifdef("MPI_COMMU") {
			mpiBcast(duplist, dataCnt);
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10Bcast(duplist, dataCnt);
		}
	} 

	/**
	 * Broadcast double-precision data array via using MPI routine.
	 *
	 * @param dmlist     distributed storage for source and its copies.
	 * @param dataCnt    count of double-precision data to broadcast
	 */
	protected static def mpiBcast(dmlist:DistDataArray, dataCnt:Long):void {
			
	@Ifdef("MPI_COMMU") {
		if (dmlist.dist.region.size() <= 1) return;

		val root   = here.id();
		finish ateach(val [p]:Point in dmlist.dist) {
			//Need: dmlist, dataCnt, root
			val dstbuf = dmlist(here.id());	
			WrapMPI.world.bcast(dstbuf, 0, dataCnt, root);
		}
	}
	}

	/**
	 *  Broadcast data among the pcnt number of places followed from here
	 */
	protected static def x10Bcast(dmlist:DistDataArray, dataCnt:Long): void {

		val pcnt   = dmlist.dist.region.size();

		if (pcnt <= 1 || dataCnt == 0) return;
		
		binaryTreeCast(dmlist, dataCnt, pcnt);
	}
		
	/**
	 * X10 implementation of broadcast data via binary tree structure.
	 */
	protected static def binaryTreeCast(dmlist:DistDataArray, dataCnt:Long, pcnt:Long): void {		
		val root   = here.id();
		val src = dmlist(root);

		val lfcnt:Long = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = root + lfcnt;

		// Specify the remote buffer
		val srcbuf = new GlobalRail[Double](src as Rail[Double]{self!=null});
			
		finish {
			at(dmlist.dist(rtroot)) {
				val dstbuf = dmlist(here.id());
				// Using copyFrom style
				finish Rail.asyncCopy[Double](srcbuf, 0, dstbuf, 0, dataCnt);
							
				// Perform binary bcast on the right brank
				if (rtcnt > 1 ) async {
					binaryTreeCast(dmlist, dataCnt, rtcnt);
				}
			}

			// Perform binary bcast on the left branch
			if (lfcnt > 1) async {
				binaryTreeCast(dmlist, dataCnt, lfcnt); 
			}
		}
	}

	/**
	 * Broadcast data in compress array from here to all other places
	 *
	 * @param smlist     distributed storage for source and its copies in all places
	 */
	public static def bcast(smlist:DistCompArray) {
		val data = smlist(here.id());
		bcast(smlist, data.storageSize());
	}

	/**
	 * Broadcast data in compress array from here
	 * to all other places. 
	 *
	 * @param smlist     distributed storage of compress array for source and its copies
	 * @param dataCnt    count of nonzero data to broadcast
	 */
	public static def bcast(smlist:DistCompArray, dataCnt:Long): void {
		Debug.assure(dataCnt <= smlist(here.id()).storageSize(), "Data overflow in bcast");
		
		@Ifdef("MPI_COMMU") {
			mpiBcast(smlist, dataCnt);
		}
		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10Bcast(smlist, dataCnt);
		}
	} 


	/**
	 * Broadcast data in compress array from here 
	 * to all other places.
	 *
	 * @param smlist    distributed storage of compress array for source and its copies.
	 * @param dataCnt   count of nonzero data to broadcast
	 */
	protected static def mpiBcast(smlist:DistCompArray, dataCnt:Long):void {
		@Ifdef("MPI_COMMU") {
			if (smlist.dist.region.size() <= 1) return;
			
			val root   = here.id();
			finish ateach(val [p]:Point in smlist.dist) {
				//Need: root, smlist, datasz, colOff, colCnt,
				val ca = smlist(here.id());	
				
				WrapMPI.world.bcast(ca.index, 0, dataCnt, root);
				WrapMPI.world.bcast(ca.value, 0, dataCnt, root);
			}
		}
	}

	/**
	 *  Broadcast compress array among the pcnt number of places followed from here
	 */
	protected static def x10Bcast(smlist:DistCompArray, dataCnt:Long): void {

		val pcnt = smlist.dist.region.size();
		if (pcnt <= 1 || dataCnt == 0) return;
		
		binaryTreeCast(smlist, dataCnt, pcnt);
	}

	/**
	 * Broadcast compress array using remote array copy in X10
	 */
	protected static def binaryTreeCast(smlist:DistCompArray, dataCnt:Long, pcnt:Long): void {		
		val myid = here.id();

		val lfcnt:Long = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = myid + lfcnt;

		// Specify the remote buffer
		val srcca = smlist(myid);
		val idxbuf    = srcca.index;
		val valbuf = srcca.value;
		val srcidx = new GlobalRail[Long](idxbuf as Rail[Long]{self!=null});
		val srcval = new GlobalRail[Double](valbuf as Rail[Double]{self!=null});
	
		finish {		
			at(smlist.dist(rtroot)) {
				//Need: smlist, srcidx, srcval, srcOff, colOff, colCnt and datasz
				val dstca = smlist(here.id());
				finish Rail.asyncCopy[Long](srcidx, 0, 
											   dstca.index, 0, dataCnt);
				finish Rail.asyncCopy[Double](srcval, 0, 
											   dstca.value, 0, dataCnt);

				// Perform binary bcast on the right brank
				if (rtcnt > 1 ) async {
					binaryTreeCast(smlist, dataCnt, rtcnt);
				}
				
			}

			// Perform binary bcast on the left branch
			if (lfcnt > 1) async {
				binaryTreeCast(smlist, dataCnt, lfcnt); 
			}
		}
	}
}
