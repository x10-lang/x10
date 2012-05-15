/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.matrix.distblock.summa;

import x10.io.Console;
import x10.util.Timer;
import x10.util.ArrayList;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;
import x10.compiler.Inline;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;

import x10.matrix.comm.WrapMPI;

import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.CastPlaceMap;

/**
 * Grid row/column-wise broadcast
 *.
 */
protected class RingCast  {


	//=======================================================================================================
	protected static def castToPlaces(distBS:PlaceLocalHandle[BlockSet], rootbid:Int, datCnt:Int, select:(Int,Int)=>Int,
			plist:Array[Int](1)):void {
		if (plist.size < 1) return;
		val srcblk = distBS().findFrontBlock(rootbid, select);
		if (srcblk.isDense()) {
			@Ifdef("MPI_COMMU") {
				mpiRingCastDense(distBS, rootbid, datCnt, select, plist);
			}
			@Ifndef("MPI_COMMU") {
				x10RingCastDense(distBS, rootbid, datCnt, select, plist);
			}
		} else if (srcblk.isSparse()) {
			@Ifdef("MPI_COMMU") {
				mpiRingCastSparse(distBS, rootbid, datCnt, select, plist);
			}
			@Ifndef("MPI_COMMU") {
				x10RingCastSparse(distBS, rootbid, datCnt, select, plist);
			}			
		} else {
			Debug.exit("Error in block type");
		}		
	}
	

	//===================================================================	
	//===================================================================
	private static def mpiRingCastDense(distBS:PlaceLocalHandle[BlockSet], rootbid:Int, datCnt:Int, select:(Int,Int)=>Int,
			plist:Array[Int](1)):void {
		
		@Ifdef("MPI_COMMU") 
		{
			val plsz = plist.size;
			val root   = here.id();                     //Implicitly copied to all places
			finish {
				for (var p:Int=0; p < plsz; p++) {
					val nxtpid = (p==plsz-1)?root:plist(p+1); //Implicitly carry to next place
					val prepid = (p==0)?root:plist(p-1); //Implicitly carry to next place
					val curpid = plist(p);
					async at (Dist.makeUnique()(curpid)) {
						//Need: nxtpid, prepid, distBS, rootbid, datCnt
						val srcblk = distBS().findFrontBlock(rootbid, select);
						val matbuf = srcblk.getData();
						val dtag = rootbid;
						//receive data from pre-place
						//Debug.flushln("Start recv data from "+prepid);
						WrapMPI.world.recv(matbuf, 0, datCnt, prepid, dtag);
						//Debug.flushln("Done recv data from "+prepid);
						if (nxtpid != root) {//send data to next-place
							//Debug.flushln("Start sending data to "+nxtpid);
							WrapMPI.world.send(matbuf, 0, datCnt, nxtpid, dtag);
							//Debug.flushln("Done sending data to "+nxtpid);
						}
					}
				}
				//At root
				async {
					val srcblk = distBS().findFrontBlock(rootbid, select);
					val den    = srcblk.getMatrix() as DenseMatrix;
					val dtag   = rootbid;
					val nxtpid = plist(0);
					//Debug.flushln("Root start sending data to "+nxtpid);
					WrapMPI.world.send(den.d, 0, datCnt, nxtpid, dtag);
					//Debug.flushln("Root send done");
				}
			}
		}
	}
	
	private static def mpiRingCastSparse(distBS:PlaceLocalHandle[BlockSet], rootbid:Int, datCnt:Int, select:(Int,Int)=>Int,
			plist:Array[Int](1)):void {
		
		@Ifdef("MPI_COMMU") 
		{
			val plsz = plist.size;
			val root   = here.id();                     //Implicitly copied to all places
			finish {
				
				for (var p:Int=0; p < plsz; p++) {
					val nxtpid = (p==plsz-1)?root:plist(p+1); //Implicitly carry to next place
					val prepid = (p==0)?root:plist(p-1); //Implicitly carry to next place
					val curpid = plist(p);

					async at (Dist.makeUnique()(curpid)) {
						//Need: nxtpid, prepid, distBS, rootbid, datCnt
						val srcblk = distBS().findFrontBlock(rootbid, select);
						val spa = srcblk.getMatrix() as SparseCSC;
						val dtag = rootbid;
						//Receive data
						spa.initRemoteCopyAtDest(datCnt);
						WrapMPI.world.recv(spa.getIndex(), 0, datCnt, prepid, dtag);
						WrapMPI.world.recv(spa.getValue(), 0, datCnt, prepid, dtag+1000000);						
						if (nxtpid != root) {
							WrapMPI.world.send(spa.getIndex(), 0, datCnt, nxtpid, dtag);
							WrapMPI.world.send(spa.getValue(), 0, datCnt, nxtpid, dtag+1000000);
						}
						spa.finalizeRemoteCopyAtDest();
					}
				}
				//At root
				async {
					val srcblk = distBS().findFrontBlock(rootbid, select);
					val spa    = srcblk.getMatrix() as SparseCSC;
					val dtag   = rootbid;
					val nxtpid = plist(0);
					WrapMPI.world.send(spa.getIndex(), 0, datCnt, nxtpid, dtag);
					WrapMPI.world.send(spa.getValue(), 0, datCnt, nxtpid, dtag+1000000);
				}
			}
		}
	}
	
	//===================================================================
	// X10 DistArray copy implementation of ring-cast
	//===================================================================
	private static def x10RingCastDense(distBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, datCnt:Int, select:(Int,Int)=>Int,
			plist:Array[Int](1)):void {

		//Check place list 
		val srcblk = distBS().findFrontBlock(rootbid, select);
		val srcden = srcblk.getMatrix() as DenseMatrix;	
		val srcbuf = new RemoteArray[Double](srcden.d as Array[Double]{self!=null});

		val nxtpid = plist(0);
		val nplist = new Array[Int](plist.size-1, (i:Int)=>plist(i+1));
		at (Dist.makeUnique()(nxtpid)) {
			//Implicit capture: rmtbuf, dmlist, datasz, nplist, root
			copyDenseToHere(srcbuf, distBS, rootbid, datCnt, select, nplist);
		}
		
	}
	
	private static def copyDenseToHere(rmtbuf:RemoteArray[Double],	distBS:PlaceLocalHandle[BlockSet], 
			rootbid:Int, datCnt:Int, select:(Int,Int)=>Int,	plist:Array[Int](1)) {
		
		val rcvblk = distBS().findFrontBlock(rootbid, select);
		val rcvden = rcvblk.getMatrix() as DenseMatrix;
		//Debug.flushln("Copy data to here at Place "+mypid);
		finish Array.asyncCopy[Double](rmtbuf, 0, rcvden.d, 0, datCnt);

		if (plist.size > 0) {
			val nxtpid = plist(0); // Get next place id in the list
			val nplist = new Array[Int](plist.size-1, (i:Int)=>plist(i+1));
			val srcbuf = new RemoteArray[Double](rcvden.d as Array[Double]{self!=null});

			at (Dist.makeUnique()(nxtpid)) {
				//Need: srcbuf, distBS, ootbid, datCnt, nplist
				copyDenseToHere(srcbuf, distBS, rootbid, datCnt, select, nplist);
			}
		}
	}
	//----------------

	private static def x10RingCastSparse(distBS:PlaceLocalHandle[BlockSet],	rootbid:Int, datCnt:Int, select:(Int,Int)=>Int,
			plist:Array[Int](1)) {
		
		//Check place list 
		val srcblk = distBS().findFrontBlock(rootbid, select);
		val srcmat = srcblk.getMatrix() as SparseCSC;
		val srcidx = new RemoteArray[Int](srcblk.getIndex() as Array[Int]{self!=null});
		val srcdat = new RemoteArray[Double](srcblk.getData() as Array[Double]{self!=null});
		//Remove root from place list
		
		val nxtpid = plist(0);
		val nplist = new Array[Int](plist.size-1, (i:Int)=>plist(i+1));
		at (Dist.makeUnique()(nxtpid)) {
			//Implicit capture: srcidx, srcdat, distBS, rootbid, datCnt, nplist
			copySparseToHere(srcidx, srcdat, distBS, rootbid, datCnt, select, nplist);
		}
	}
	
	private static def copySparseToHere(rmtidx:RemoteArray[Int], rmtdat:RemoteArray[Double],
			distBS:PlaceLocalHandle[BlockSet], rootbid:Int, datCnt:Int, select:(Int,Int)=>Int,
			plist:Array[Int](1)) {
		
		val rcvblk = distBS().findFrontBlock(rootbid, select);
		//Debug.flushln("Copy data to here at Place "+mypid);
		val spa = rcvblk.getMatrix() as SparseCSC;
		
		spa.initRemoteCopyAtDest(datCnt);
		if (datCnt > 0) {
			finish Array.asyncCopy[Int](rmtidx, 0,    spa.getIndex(), 0, datCnt);
			finish Array.asyncCopy[Double](rmtdat, 0, spa.getValue(), 0, datCnt);
		}
		
		//rcvden.print("Matrix data at "+mypid+" plist:"+plist.toString());		
		if (plist.size > 0) {
			val nxtpid = plist(0); // Get next place id in the list
			val nplist = new Array[Int](plist.size-1, (i:Int)=>plist(i+1));
			val srcidx = new RemoteArray[Int](rcvblk.getIndex() as Array[Int]{self!=null});
			val srcdat = new RemoteArray[Double](rcvblk.getData() as Array[Double]{self!=null});

			at (Dist.makeUnique()(nxtpid)) {
				//Need: srcidx, srcdat, distBS, rootbid, datCnt, nplist
				copySparseToHere(srcidx, srcdat, distBS, rootbid, datCnt, select, nplist);
			}
		}
		spa.finalizeRemoteCopyAtDest();
	}
}