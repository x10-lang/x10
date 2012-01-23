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

/**
 * The class provides reduce-sum communication for distributed matrix,
 * 
 */
public class BlockReduce extends BlockRemoteCopy {

	//public var mpi:UtilMPI;

	//====================================
	// Constructor
	//====================================
	public def this() {
		super();

	}
	//=================================================
	// Broadcast dense matrix to all
	//=================================================

	/**
	 * Reduce all dense matrices stored in DistArray from all places
	 * to here
	 *
	 * @param ddmat      Input/Output. Duplicated dense matrix of source and target matrix
	 * @param ddtmp      Temp matrix space to store the receiving data.
	 * @return           Number of elements to received
	 */
	public static def reduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH, rootbid:Int) {
		
		val dmap = distBS().getDistMap();
		val rootpid:Int = dmap.findPlace(rootbid);
		var leftpcnt:Int = Place.MAX_PLACES;
		
		Debug.assure(here.id() == 0);
		finish {
			if (rootpid != here.id()) {
				leftpcnt = rootpid + 1;
				//Go to rootpid, starting reduce
				at (Dist.makeUnique()(rootpid)) async {
					val rightpcnt = Place.MAX_PLACES - rootpid;
					reduceSumToHere(distBS, tmpBS, rightpcnt); //Reduce to the first block
				}
			}
			
			async {
			//Start left side reduce
				reduceSumToHere(distBS, tmpBS, leftpcnt);
			}
		}
	
		if (rootpid != here.id()) {
			val bid = distBS().getLocalBlockIdAt(0);
			at (Dist.makeUnique()(rootpid)) {
				copy(distBS, bid, tmpBS().getFirst());
				if (rootbid != distBS().getLocalBlockIdAt(0))
					copy(distBS().getFirst(), distBS, rootbid);
				val rootblk = distBS().find(rootbid);
				val rootden = rootblk.getMatrix() as DenseMatrix;
				rootden.cellAdd(tmpBS().getFirst().getMatrix() as DenseMatrix(rootden.M, rootden.N));
			}
		} else {
			if (rootbid != distBS().getLocalBlockIdAt(0)) {	
				val src = distBS().getFirst().getMatrix() as DenseMatrix;
				val dst = distBS().find(rootbid).getMatrix() as DenseMatrix(src.M, src.N);
				src.copyTo(dst);
			}
		}
	} 


	//=========================================================
	
	/**
	 * Binary recursive reduce sum.
	 * Notice dmat is input and output matrix.
	 */
	protected static def reduceSumToHere(distBS:BlocksPLH, tmp:BlocksPLH, var pcnt:Int): void {
		
		val ttpcnt = Place.MAX_PLACES;
		val root = here.id();
		
		if (root + pcnt > ttpcnt) pcnt = ttpcnt-root;
		if (pcnt <= 1) return;

		val lfcnt  = (pcnt+1) / 2; // make sure left part is larger, if cnt is odd 
		val rtcnt  = pcnt - lfcnt;
		val rtroot = root + lfcnt;
		if (pcnt > 2) {
			finish {
				if (lfcnt > 1) async {
					reduceSumToHere(distBS, tmp, lfcnt); 
				}
				if (rtcnt > 1 ) {
					at (Dist.makeUnique()(rtroot)) async {
						reduceSumToHere(distBS, tmp, rtcnt);
					}
				}
			}
		}
		val tmpden = tmp().getFirst().getMatrix() as DenseMatrix;
		val dstblk = distBS().getFirst();
		val dstden = dstblk.getMatrix() as DenseMatrix;
		val blkset = distBS();
		
		blkset.reduceSum(dstblk);
		copy(distBS, rtroot, 0, tmpden, 0, tmpden.N);
		dstden.cellAdd(tmpden as DenseMatrix(dstden.M, dstden.N));
	}
	//=============================================================
	/**
	 * Perform reduce sum of all matrces stored in the duplicated matrix
	 * Result is stored in the matrix at root place.
	 * 
	 * @param ddmat     input and output matrix. 
	 * @param ddtmp     temp matrix storing the inter-place communication data.
	 */
	public static def mpiReduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH, rootbid:Int):void {
			
		val dmap = distBS().getDistMap();
		val rootpid:Int = dmap.findPlace(rootbid);

		@Ifdef("MPI_COMMU") {
			finish ateach (val [p]:Point in Dist.makeUnique()) {
				
				val srcblk = (rootpid==here.id())? tmpBS().find(rootpid) : tmpBS().getFirst();
				val dstblk = (rootpid==here.id())?distBS().find(rootpid):distBS().getFirst();

				//Copy all blocks to tmp, tmp is source in MPI reduce
				distBS().copyTo(tmpBS());

				//Local reduce Sum
				tmpBS().reduceSum(srcblk);
				
				val src = srcblk.getMatrix() as DenseMatrix;
				val dst = dstblk.getMatrix() as DenseMatrix;
				val dsz = src.M*src.N;
				WrapMPI.world.reduceSum(dst.d, dst.d, dsz, rootpid);
			}
		}
	}
	
	//=============================================================

	public static def allReduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH): void {

		reduceSum(distBS, tmpBS, 0);

		BlockBcast.bcast(distBS, 0);
	}

	/**
	 * Perform all reduce sum operation. 
	 * @see reduceSum()
	 * Result is synchronized for all copies of duplicated matrix
	 * 
	 * @param ddmat     input and output matrix. 
	 * @param ddtmp     temp matrix storing the inter-place communication data.
	 */
	protected static def mpiAllReduceSum(distBS:BlocksPLH, tmpBS:BlocksPLH): void {
		
		//Debug.flushln("Start all reduce");
		@Ifdef("MPI_COMMU") {
			finish ateach (val [p]:Point in Dist.makeUnique()) {
			
				val srcblk = tmpBS().getFirst();
				val dstblk = distBS().getFirst();

				//Copy all blocks to tmp, tmp is source in MPI reduce
				distBS().copyTo(tmpBS());

				//Local reduce Sum
				tmpBS().reduceSum(srcblk);
				
				val src = srcblk.getMatrix() as DenseMatrix;
				val dst = dstblk.getMatrix() as DenseMatrix;
				val dsz = src.M*src.N;
				
				// all Reduce may cause process to hange
				WrapMPI.world.allReduceSum(dst.d, dst.d, dsz);
				distBS().sync();
			}
			
		}
	}
	
}

