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

import x10.matrix.ElemType;

import x10.matrix.comm.mpi.WrapMPI;

/**
 * Gather operations collects data arrays distributed in all places to
 * root. The distributed storage can be accessed via DistArray 
 * data structure.
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
public class DistArrayGather extends DistArrayRemoteCopy {
	/**
	 * Gather data arrays from all places to here in a list of arrays
	 * (separate memory spaces).
	 *
	 * @param src     distributed storage for data arrays in all places
	 * @param dst     storage of list arrays for gather result 
	 */
	public static def gather(
			src:DistDataArray, 
			dst:Rail[Rail[ElemType]]) : void {
		
		val nb = src.region.size();
        assert (nb==dst.size) :
            "Number of blocks in dist and local array do not match";
		
		finish for (var bid:Long=0; bid<nb; bid++) {
			val dstbuf = dst(bid);
				
			if (bid == here.id()) {
				val srcbuf = src(bid);
				Rail.copy(srcbuf, 0, dstbuf, 0, dstbuf.size);

			} else {

				@Ifdef("MPI_COMMU") {
					mpiCopy(src, bid, 0, dstbuf, 0, dstbuf.size);
				}
				@Ifndef("MPI_COMMU") {
					x10Copy(src, bid, 0, dstbuf, 0, dstbuf.size);
				}
			}
			
		}
	}

	// Gather from single row blocks partitioning
	/**
	 * Gather distributed arrays from all places to here in a continuous memory space.
	 * Gathered arrays will be placed next to each other.
	 *
	 * @param src			distributed storage for source arrays in all places.
	 * @param dst			storage array for the gather result
	 * @param szlist		list of array sizes
	 */
	public static def gather(
			src:DistDataArray, 
			dst:Rail[ElemType],
			szlist:Rail[Long]):void {

		@Ifdef("MPI_COMMU") {
			mpiGather(src, dst, szlist);
		}
		@Ifndef("MPI_COMMU") {
			x10Gather(src, dst, szlist);
		}
	}

	/**
	 * Gather distributed arrays from all places
	 * by using mpi gather routine.
	 *
	 * @param src       distributed storage for source arrays in all places 
	 * @param dst       storage array for the gather result
	 * @param szlist    list of array sizes
	 */
	public static def mpiGather(
			src:DistDataArray, 
			dst:Rail[ElemType], 
			szlist:Rail[Int]):void {
		
		@Ifdef("MPI_COMMU") {
			val root = here.id();
			finish 	{ 
				for([p] in src.dist) {
					val datcnt = szlist(p);
					if (p != root) {
						at(src.dist(p)) async {
							val srcbuf = src(here.id());
							/*******************************************/
							// Not working
							//val tmpbuf= null; //fake
							//val tmplst=null;//   //fake
							/*******************************************/
							val tmpbuf = new Array[ElemType](0); //fake
							val tmplst = new Array[Int](0);   //fake
							WrapMPI.world.gatherv(srcbuf, 0, datcnt, tmpbuf, 0, tmplst, root);
						}
					} 
				}

				async {
					/**********************************************/
					// DO NOT move this block into for loop block
					// MPI process will hang, Cause is not clear
					/**********************************************/	
					val srcbuf = src(root);
					WrapMPI.world.gatherv(srcbuf, 0, szlist(root), dst, 0, szlist, root);
				}
			
			}
		}
	}
	
	/**
	 * Gather distributed arrays from all places to here
	 * 
	 * @param src     distributed storage for source arrays in all places.
	 * @param dstbuf  storage array for gather result
	 * @param gp      list of array sizes
	 */
	public static def x10Gather(
			src:DistDataArray, 
			dstbuf:Rail[ElemType],
			gp:Rail[Long]): void {

		val root = here.id();
		var off:Long=0;
		for (var cb:Long=0; cb<gp.size; cb++) {
			val datcnt = gp(cb);
			
			if (cb != root) {
				x10Copy(src, cb, 0, dstbuf, off, datcnt); 

			} else {
				//Make local copying
				val srcbuf = src(cb);
				Rail.copy(srcbuf, 0, dstbuf, off, datcnt);
			}
			off += datcnt;
		}
	}
}
