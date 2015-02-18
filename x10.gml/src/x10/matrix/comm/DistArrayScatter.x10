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
 * This class supports scatter operation for data arrays which are defined
 * in DistArray at all places. 
 * 
 * <p> Two implementations are available. One uses MPI routines and 
 * the other is based on X10 remote array copy.
 * To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class DistArrayScatter extends ArrayRemoteCopy {
	// Distributed array scatter
	/**
	 * Scatter data from arrays at here to distributed arras in all places.
	 *
	 * @param src      source arrays.
	 * @param dst      target distributed arrays
	 */
	public static def scatter(
			src:Rail[Rail[ElemType]], 
			dst:DistDataArray) : void {
		
		val nb = dst.region.size();
        assert (nb==src.size) :
            "Number blocks in dist and local array mismatch";
		
		finish for (var bid:Long=0; bid<nb; bid++) {
			val srcbuf = src(bid);
				
			if (bid == here.id()) {
				val dstbuf = dst(bid);
				Rail.copy(srcbuf, 0, dstbuf, 0, srcbuf.size);

			} else {

				@Ifdef("MPI_COMMU") {
					{ 
						mpiCopy(srcbuf, 0, dst, bid, 0, srcbuf.size);
					}
				}
				@Ifndef("MPI_COMMU") {
					{
						x10Copy(srcbuf, 0, dst, bid, 0, srcbuf.size);
					}
				}
			}
		}
	}

	/**
	 * Scatter single-row partitioning blocks from here to all places
	 */
	public static def scatter(
			src:Rail[ElemType], 
			dst:DistDataArray, 
			gp:Rail[Long]): void {

		assert gp.size == dst.region.size();

		@Ifdef("MPI_COMMU") {
			{ 
				mpiScatter(src, dst, gp);
			}
		}
		@Ifndef("MPI_COMMU") {
			{
				x10Scatter(src, dst, gp);
			}
		}
	}

	/**
	 * Scatter data from array at here to all places 
	 * by calling mpi scatter routine.
	 *
	 * @param src      	source data array 
	 * @param dst     	target distributed array
  	 * @param gp      	size list, or partitioning of array for scattering
	 */
	public static def mpiScatter(
			src:Rail[ElemType],
			dst:DistDataArray, 
			szlist:Rail[Int]): void {
		
	@Ifdef("MPI_COMMU") {
		//Only one row block partition
		val root = here.id();
		finish { 
			for(val [p] :Point in dst.dist) {
				val datcnt = szlist(p);
				if (p != root) {
					at(dst.dist(p)) async {
						val dstbuf = dst(here.id());
						/*******************************************/
						// Not working
						//val tmpbuf= null; //fake
						//val tmplst=null;//   //fake
						/*******************************************/
						val tmpbuf = new Array[ElemType](0); //fake
						val tmplst = new Array[Int](0);   //fake
						WrapMPI.world.scatterv(tmpbuf, tmplst, 
									 dstbuf, datcnt, root);
					}
				} 
			}

			async {
				/**********************************************/
				// DO NOT move this block into for loop block
				// MPI process will hang, Cause is not clear
				/**********************************************/	
				val dstbuf = dst(root);
				WrapMPI.world.scatterv(src, szlist, dstbuf, szlist(root), root);
			}
			
		}
	}
	}
	
	/**
	 * Copy array data from here to distributed array in all places
	 * 
	 * @param src      	source array
	 * @param dst      	target distributed array 
	 * @param szlist 	size list
	 */
	public static def x10Scatter(
			src:Rail[ElemType], 
			dst:DistDataArray, 
			szlist:Rail[Long]):void {

		val root = here.id();
		var off:Long=0;
		for (var cb:Long=0; cb<szlist.size; cb++) {

			val datcnt = szlist(cb);
			
			if (cb != root) {
				x10Copy(src, off, dst, cb, 0, datcnt); 

			} else {
				//Make local copying
				val dstbuf = dst(root);
				Rail.copy(src, off, dstbuf, 0, datcnt);
			}
			off += datcnt;
		}
	}

	// Access remote array via PlaceLocalHandle

	/**
	 * Scatter data from arrays at here to arrays in all places.
	 * 
	 * @param src      source arrays.
	 * @param dst      target distributed arrays
	 */
	public static def scatter(
			src:Rail[Rail[ElemType]], 
			dst:DataArrayPLH) : void {
		
		val nb = Place.numPlaces();
        assert (nb==src.size) :
            "Number blocks in dist and local array mismatch";
		
		finish for (var bid:Long=0; bid<nb; bid++) {
			val srcbuf = src(bid);
			
			if (bid == here.id()) {
				val dstbuf = dst();
				Rail.copy(srcbuf, 0, dstbuf, 0, srcbuf.size);

			} else {

				@Ifdef("MPI_COMMU") {
					mpiCopy(srcbuf, 0, dst, bid, 0, srcbuf.size);
				}
				@Ifndef("MPI_COMMU") {
					x10Copy(srcbuf, 0, dst, bid, 0, srcbuf.size);
				}
			}
			
		}
	}

	/**
	 * Scatter single-row partitioning blocks from here to all places
	 */
	public static def scatter(
			src:Rail[ElemType], 
			dst:DataArrayPLH, 
			gp:Rail[Long]): void {

		assert gp.size == Place.numPlaces();

		@Ifdef("MPI_COMMU") {
			mpiScatter(src, dst, gp);
		}
		@Ifndef("MPI_COMMU") {
			x10Scatter(src, dst, gp);
		}
	}

	/**
	 * Scatter data from array at here to all places 
	 * by calling mpi scatter routine.
	 * 
	 * @param src      	source data array 
	 * @param dst      	target distributed array
	 * @param gp      	size list, or partitioning of array for scattering
	 */
	public static def mpiScatter(
			src:Rail[ElemType],
			dst:DataArrayPLH, 
			szlist:Rail[Int]): void {
		
	@Ifdef("MPI_COMMU") {
		//Only one row block partition
		val root = here.id();
		finish 	{ 
			for(val [p] :Point in WrapMPI.world.dist) {
				val datcnt = szlist(p);
				if (p != root) {
					at(WrapMPI.world.dist(p)) async {
						val dstbuf = dst();
						/*******************************************/
						// Not working
						//val tmpbuf= null; //fake
						//val tmplst=null;//   //fake
						/*******************************************/
						val tmpbuf = new Array[ElemType](0); //fake
						val tmplst = new Array[Int](0);   //fake
						WrapMPI.world.scatterv(tmpbuf, tmplst, dstbuf, datcnt, root);
					}
				} 
			}
			async {
				/**********************************************/
				// DO NOT move this block into for loop block
				// MPI process will hang, Cause is not clear
				/**********************************************/	
				val dstbuf = dst();
				WrapMPI.world.scatterv(src, szlist, dstbuf, szlist(root), root);
			}			
		}
	}
	}
	
	/**
	 * Copy array data from here to distributed array in all places
	 * 
	 * @param src      	source array
	 * @param dst      	target distributed array 
	 * @param szlist  	size list
	 */
	public static def x10Scatter(
			src:Rail[ElemType], 
			dst:DataArrayPLH, 
			szlist:Rail[Long]):void {

		val root = here.id();
		var off:Long=0;
		for (var cb:Long=0; cb<szlist.size; cb++) {

			val datcnt = szlist(cb);
			if (cb != root) {
				x10Copy(src, off, dst, cb, 0, datcnt);
			} else {
				//Make local copying
				val dstbuf = dst();
				Rail.copy(src, off, dstbuf, 0, datcnt);
			}
			off += datcnt;
		}
	}

	public static def verify(
			src:Rail[ElemType], 
			dstplh:DataArrayPLH, 
			szlist:Rail[Long]):Boolean {
		
		var ret:Boolean = true;
		var j:Long=0;
		for (val [p] in x10.regionarray.Dist.makeUnique()) {
			val rmt= at(Place(p)) dstplh();
			for (var i:Long=0; i<szlist(p); i++, j++) ret &= (src(j)==rmt(i));
		}
		return ret;
	}
}
