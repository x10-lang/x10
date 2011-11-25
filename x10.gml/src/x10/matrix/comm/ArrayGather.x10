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
import x10.matrix.sparse.CompressArray;

/**
 * Gather operations collects data arrays distributed in all places to
 * root. The distributed storage can be accessed via PlaceLocalHandle or DistArray 
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
public class ArrayGather extends ArrayRemoteCopy {
	//==============================================
	// Constructor
	//==============================================
	public def this() {
		super();
	}

	//==============================================
	// 
	//==============================================
	/**
	 * Gather data arrays from all places to here in a list of arrays
	 * (separate memory spaces).
	 *
	 * @param src     distributed storage for data arrays in all places
	 * @param dst     storage of list arrays for gather result 
	 */
	public static def gather(
			src:DistDataArray, 
			dst:Array[Array[Double](1)](1)) : void {
		
		val nb = src.region.size();
		Debug.assure(nb==dst.size, 
					 "Number blocks in dist and local array not match");
		
		finish for (var bid:Int=0; bid<nb; bid++) {
			val dstbuf = dst(bid);
				
			if (bid == here.id()) {
				val srcbuf = src(bid);
				Array.copy(srcbuf, 0, dstbuf, 0, dstbuf.size);

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


	//------------------------------------------------------------
	// Gather from single row blocks partitioning
	//------------------------------------------------------------

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
			dst:Array[Double](1),
			szlist:Array[Int](1)):void {

		@Ifdef("MPI_COMMU") {
			mpiGather(src, dst, szlist);
		}
		@Ifndef("MPI_COMMU") {
			x10Gather(src, dst, szlist);
		}
	}

	//
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
			dst:Array[Double](1), 
			szlist:Array[Int](1)):void {
		
		@Ifdef("MPI_COMMU") {
			val root = here.id();
			finish 	{ 
				for(val [p] :Point in src.dist) {
					val datcnt = szlist(p);
					if (p != root) {
						at (src.dist(p)) async {
							val srcbuf = src(here.id());
							/*******************************************/
							// Not working
							//val tmpbuf:Array[Double](1)= null; //fake
							//val tmplst:Array[Int](1)=null;//   //fake
							/*******************************************/
							val tmpbuf = new Array[Double](0); //fake
							val tmplst = new Array[Int](0);   //fake
							//Debug.flushln("P"+p+" starting non root gather :"+datcnt);
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
					//Debug.flushln("P"+root+" starting root gather:"+szlist.toString());
				
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
			dstbuf:Array[Double](1),
			gp:Array[Int](1)): void {

		val root = here.id();
		var off:Int=0;
		for (var cb:Int=0; cb<gp.size; cb++) {
			val datcnt = gp(cb);
			
			if (cb != root) {
				x10Copy(src, cb, 0, dstbuf, off, datcnt); 

			} else {
				//Make local copying
				val srcbuf = src(cb);
				Array.copy(srcbuf, 0, dstbuf, off, datcnt);
			}
			off += datcnt;
		}
	}
	
	//==============================================
	// Remote data access via PlaceLocalHandle
	//==============================================
	/**
	 * Gather distributed arrays from all places to here
	 * at here.
	 * 
	 * @param src    distributed storage of source arrays on PlaceLocalHandle
	 * @param dst    storage of list arrays for gather result
	 */
	public static def gather(
			src:DataArrayPLH, 
			dst:Array[Array[Double](1)](1)) : void {
		
		val nb = Place.MAX_PLACES;
		Debug.assure(nb==dst.size, 
		"Number blocks in dist and local array not match");
		
		finish for (var bid:Int=0; bid<nb; bid++) {
			val dstbuf = dst(bid);
			
			if (bid == here.id()) {
				val srcbuf = src();
				Array.copy(srcbuf, 0, dstbuf, 0, dstbuf.size);

			} else {

				@Ifdef("MPI_COMMU") {
					{ 
						mpiCopy(src, bid, 0, dstbuf, 0, dstbuf.size);
					}
				}
				@Ifndef("MPI_COMMU") {
					{
						x10Copy(src, bid, 0, dstbuf, 0, dstbuf.size);
					}
				}
			}
			
		}
	}


	//------------------------------------------------------------
	// Gather from single row blocks partitioning
	//------------------------------------------------------------

	/**
	 * Gather distributed arrays from all places to here and store 
	 * in a continous memory space
	 *
	 * @param src    distributed storage of source arrays on PlaceLocalHandle
	 * @param dst    storage array for gather result
	 * @param gp     list of array sizes
	 */
	public static def gather( 
			src:DataArrayPLH, 
			dst:Array[Double](1),
			gp:Array[Int](1)) : void {

		@Ifdef("MPI_COMMU") {
			mpiGather(src, dst, gp);
		}
		@Ifndef("MPI_COMMU") {
			x10Gather(src, dst, gp);
		}
	}

	//
	/**
	 * Gather distributed arrays from all places to here
	 * by using mpi gather routine.
	 * 
	 * @param src     distributed storage of data arrays 
	 * @param dst     storage array for gather result at here
	 * @param szlist  list of array sizes.
	 */
	public static def mpiGather(
			src:DataArrayPLH, 
			dst:Array[Double](1),
			szlist:Array[Int](1)):void {
		
		@Ifdef("MPI_COMMU") {

			val root = here.id();
			finish 	{ 
				for(val [p] in WrapMPI.world.dist) {
					val datcnt = szlist(p);
					if (p != root) {
						at (WrapMPI.world.dist(p)) async {
							val srcbuf = src();
							/*******************************************/
							// Not working
							//val tmpbuf:Array[Double](1)= null; //fake
							//val tmplst:Array[Int](1)=null;//   //fake
							/*******************************************/
							val tmpbuf = new Array[Double](0); //fake
							val tmplst = new Array[Int](0);   //fake
							//Debug.flushln("P"+p+" starting non root gather :"+datcnt);
							WrapMPI.world.gatherv(srcbuf, 0, datcnt, tmpbuf, 0, tmplst, root);
						}
					} 
				}

				async {
					/**********************************************/
					// DO NOT move this block into for loop block
					// MPI process will hang, Cause is not clear
					/**********************************************/	
					val srcbuf = src();
					//Debug.flushln("P"+root+" starting root gather:"+szlist.toString());
				
					WrapMPI.world.gatherv(srcbuf, 0, szlist(root), dst, 0, szlist, root);
				}
			
			}
		}
	}
	
	/**
	 * Gather distributed arrays from all places to here by using x10 remote array copy.
	 * 
	 * @param src     distributed storage of data arrays in all places.
	 * @param dstbuf  storage array for gather result
	 * @param gp      list of array sizes
	 */
	public static def x10Gather(
			src:DataArrayPLH, 
			dstbuf:Array[Double](1),
			gp:Array[Int](1)): void {

		val root = here.id();
		var off:Int=0;
		for (var cb:Int=0; cb<gp.size; cb++) {
			val datcnt = gp(cb);
			
			if (cb != root) {
				x10Copy(src, cb, 0, dstbuf, off, datcnt);
			} else {
				//Make local copying
				val srcbuf = src();
				Array.copy(srcbuf, 0, dstbuf, off, datcnt);
			}
			off += datcnt;
		}
	}
	
	//=======================================
	//util
	//=======================================
	public static def verify(
			src:DataArrayPLH, buf:Array[Double](1), 
			szlist:Array[Int](1)):Boolean =
			ArrayScatter.verify(buf, src, szlist);	
}
