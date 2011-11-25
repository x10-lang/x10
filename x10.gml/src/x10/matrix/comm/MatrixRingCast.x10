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


/**
 * Ring cast sends data from here to a number of places
 *.
 * <p> It starts with sending data from here to the first place in the list, and from the first place
 * to the second place in the list, and so on until the end of list. 
 * 
 * <p> This operation is implemented for SUMMA matrix
 * multiplication, when places in the same partition row (or column) use
 * the same block of matrix data as the first operand (or the second) in matrix
 * multiply.
 *
 */
public class MatrixRingCast extends MatrixRemoteCopy {

	//===========================
	// Constructor
	//===========================
	public def this() {
		super();
	}
	//==================================================
	// RingCast: receive form previous one and send to one next in a ring 
	//==================================================

	/**
	 * Broadcast the whole dense matrix at here to all places one by one 
	 *
	 * @param dmlist     distributed storage for copies of dense matrix 
	 */
	public static def rcast(dmlist:DistArray[DenseMatrix](1)) {
		val sz = dmlist(here.id()).N * dmlist(here.id()).M;
		rcast(dmlist, sz);
	}


	//--------------------------------------------
	/**
	 * Broadcast data of dense matrix to all places.
	 *
	 * @param dmlist     distributed storage for copies of dense matrix
	 * @param datCnt     number of data to broadcast 
	 */
	public static def rcast(dmlist:DistArray[DenseMatrix](1), datCnt:Int):void {
		val pcnt = dmlist.dist.region.size();
		val plist:Array[Int](1) = new Array[Int](pcnt, (i:Int)=>(i));
		rcast(dmlist, datCnt, plist);
	}

	/**
	 * Send data of dense matrix from here to a list of places.
	 * 
	 * @param dmlist     distributed storage for copies of dense matrix
	 * @param datCnt     number of data to broadcast
	 * @param plist      the list of places in the ring cast
	 */
	public static def rcast(
			dmlist:DistArray[DenseMatrix](1), datCnt:Int, 
			plist:Array[Int](1)) : void {

		@Ifdef("MPI_COMMU") {
			mpiRingCast(dmlist, datCnt, plist);
		}

		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10RingCast(dmlist, datCnt, plist);
		}
	}
	//----------------------------------------------------------------
	
	/**
	 * Send data of dense matrix from here to a list of places using
	 * MPI send/recv routines. Note, the root place (place here) must be
	 * in the list. The root place ID may appear multiple times in the
	 * the list to speed up the performance. However, it also may increase
	 * network contentions.
	 * 
	 * @param  dmlist     duplicated storage for copies of dense matrix in all places
	 * @param  datCnt     count of data to send
	 * @param  plist      the list of place IDs. Root place must be in the list
	 */
	protected static def mpiRingCast(
			dmlist:DistArray[DenseMatrix](1), 
			datCnt:Int,
			plist:Array[Int](1)):void {
		
		@Ifdef("MPI_COMMU") {
			// Check place list 
			if (plist.size <= 0) return ;

			val root   = here.id();  //Implicitly copied to all places
			finish {

				//Debug.flushln("Start MPI ring cast from "+root+" to "+plist.toString());
				val pltail = plist.size-1;
				for (var p:Int=0; p < plist.size; p++) {
					val nxtpid = (p==pltail)?plist(0):plist(p+1); //Implicitly carry to next place
					val prepid = (p==0)?plist(pltail):plist(p-1); //Implicitly carry to next place
					val curpid = plist(p);

					async at (dmlist.dist(curpid)) {
						//Need: dmlist, root, nxtpid, prepid, datCnt
						val mypid  = here.id();
						val matden = dmlist(mypid);

						if (mypid != root) {
							//Debug.flushln("P "+mypid+" recv from "+prepid);
							val dtag = prepid * 5000 + mypid;
							WrapMPI.world.recv(matden.d, 0, datCnt, prepid, dtag);
						}
						if (nxtpid != root) {
							//Debug.flushln("P "+mypid+" send to"+nxtpid);
							val dtag = mypid * 5000 + nxtpid;
							WrapMPI.world.send(matden.d, 0, datCnt, nxtpid, dtag);
						}
					}
				}
			}
		}
	}
	

	//======================================================================
	// Sparse matrix
	//======================================================================
	/**
	 * Broadcast columns of sparse matrix to all places.
	 *
	 * @param smlist     distributed storage for copies of SparseCSC sparse matrix
	 * @param datCnt     count of nonzero data in the sparse matrix to broadcast 
	 */
	public static def rcast(smlist:DistArray[SparseCSC](1), datCnt:Int):void {
		val pcnt = smlist.dist.region.size();
		val plist:Array[Int](1) = new Array[Int](pcnt, (i:Int)=>(i));
		rcast(smlist, datCnt, plist);
	}

	//-------------------------------------------------------------
	/**
	 * Broadcast the whole sparse matrix at here to all places 
	 * 
	 * @param smlist     distributed storage for copies of sparse matrix 
	 */
	public static def rcast(smlist:DistArray[SparseCSC](1)) {
		val sz = smlist(here.id()).countNonZero();
		rcast(smlist, sz);
	}

	/**
	 * Send columns in sparse matrix from here to a list of places.
	 *
	 * @param smlist     distributed storage for copies of sparse matrix
	 * @param datCnt     count of nonzero data in the sparse matrix to broadcast
	 * @param plist      the list of places in the ring cast
	 */
	public static def rcast(
			smlist:DistArray[SparseCSC](1), 
			datCnt:Int, 
			plist:Array[Int](1)) : void {

		@Ifdef("MPI_COMMU") {
			mpiRingCast(smlist, datCnt, plist);
		}

		@Ifndef("MPI_COMMU") {
			//Debug.flushln("start bcast to "+numPlaces);
			x10RingCast(smlist, datCnt, plist);
		}
	}

	/**
	 * Send columns in sparse matrix from here to a list of places using
	 * X10 remote array copy.
	 *
	 * @param  smlist     distributed storage for copies of sparse matrix
	 * @param  colCnt     counts of nonzero data to send
	 * @param  plist      the list of place IDs
	 */
	protected static def mpiRingCast(
			smlist:DistArray[SparseCSC](1), 
			datCnt:Int,
			plist:Array[Int](1)):void {

		@Ifdef("MPI_COMMU") {
			// Check place list 
			if (plist.size <= 0) return;

			val root   = here.id();                     //Implicitly copied to all places
			val srcspa = smlist(root);

			srcspa.initRemoteCopyAtSource();
			finish {
	
				val pltail = plist.size-1;
				for (var p:Int=0; p < plist.size; p++) {
					val nxtpid = (p==plist.size-1)?plist(0):plist(p+1); //Implicitly carry to next place
					val prepid = (p==0)?plist(plist.size-1):plist(p-1); //Implicitly carry to next place
					//val curpid = p;
					val curpid = plist(p);

					async at (smlist.dist(curpid)) {
						//Need: dmlist, root, nxtpid, prepid, colOff, datCnt, datasz
						val mypid  = here.id();
						val matspa = smlist(mypid);
					
						if (mypid != root) {
							val dtag = prepid * 10000 + mypid;

							//++++++++++++++++++++++++++++++++++++++++++++
							//Do NOT call getIndex()/getValue() before init at destination place
							//+++++++++++++++++++++++++++++++++++++++++++++
							matspa.initRemoteCopyAtDest(datCnt);
							WrapMPI.world.recv(matspa.getIndex(), 0, datCnt, prepid, dtag);
							WrapMPI.world.recv(matspa.getValue(), 0, datCnt, prepid, dtag);
						}
						if (nxtpid != root) {
							val dtag = mypid * 10000 + nxtpid;
							WrapMPI.world.send(matspa.getIndex(), 0, datCnt, nxtpid, dtag);
							WrapMPI.world.send(matspa.getValue(), 0, datCnt, nxtpid, dtag);
						}
					
						if (mypid != root)
							matspa.finalizeRemoteCopyAtDest();
					}
				}
			}
			srcspa.finalizeRemoteCopyAtSource();
		}
	}

	//-----------------------------------------------------

	/**
	 * Sending data of dense matrix from here to a list of places using X10 remote array copy.
	 *
	 * @param  dmlist     distributed storage for copies of dense matrix
	 * @param  datCnt     counts of data to be sent
	 * @param  plist      the list of place IDs, which must contain place id of here.
	 */
	protected static def x10RingCast(
			dmlist:DistArray[DenseMatrix](1), datCnt:Int, 
			plist:Array[Int](1)):void {
		//Check place list 

		if (plist.size == 0 ) return;
		val root   = here.id();
		val srcden = dmlist(root);	

		val rmtbuf = new RemoteArray[Double](srcden.d as Array[Double]{self!=null});
		val nplist = new Array[Int](plist.size-1, (i:Int)=>plist(i+1));

		val nxtpid = plist(0);
		at (dmlist.dist(nxtpid)) {
			//Need: rmtbuf, dmlist, colOff, offset, datasz, nplist, root
			copyToHere(rmtbuf, dmlist, datCnt, nplist, root);
		}
	}

	/**
	 *
	 */
	private static def copyToHere(
			srcbuf:RemoteArray[Double],
			dmlist:DistArray[DenseMatrix](1),
			datCnt:Int,
			plist:Array[Int](1),
			root:Int): void {
		
		val mypid  = here.id();
		val rcvden = dmlist(mypid);
		//Copy data from source place
		if (mypid != root && datCnt > 0) {
			//Debug.flushln("Copy data to here at Place "+mypid);
			finish Array.asyncCopy[Double](srcbuf, 0, rcvden.d, 0, datCnt);
		}
		//rcvden.print("Matrix data at "+mypid+" plist:"+plist.toString());
		
		//Goto next place in the list
		if (plist.size >= 1) {
			val nxtpid = plist(0); // Get next place id in the list
			val rmtbuf = new RemoteArray[Double](rcvden.d as Array[Double]{self!=null});
			val nplist = new Array[Int](plist.size-1, (i:Int)=>plist(i+1));
			at (dmlist.dist(nxtpid)) {
				//Need: rmtbuf, dmlist, colOff, offset, datasz, nplist, root
				copyToHere(rmtbuf, dmlist, datCnt, nplist, root);
			}
		}
	}


	/**
	 * Sending the data from here to places in the list by using X10 remote copy.
	 *
	 * @param  smlist     	distributed storage for copies of sparse matrix
	 * @param  datCnt     	count of nonzero data to send
	 * @param  plist     	the list of place IDs, which must contain place id of here.
	 */
	protected static def x10RingCast(
			smlist:DistArray[SparseCSC](1), 
			datCnt:Int, 
			plist:Array[Int](1)):void {
		
		//Check place list 
		if (plist.size == 0) return;
		val root   = here.id();
		val srcspa = smlist(root);	
		val rmtidx = new RemoteArray[Int   ](srcspa.getIndex() as Array[Int]{self!=null});
		val rmtval = new RemoteArray[Double](srcspa.getValue() as Array[Double]{self!=null});
		val nplist = new Array[Int](plist.size-1, (i:Int)=>plist(i+1));

		val nxtpid = plist(0);
		
		srcspa.initRemoteCopyAtSource();
		at (smlist.dist(nxtpid)) {
			copyToHere(rmtidx, rmtval, smlist, datCnt, nplist, root);
		}
		srcspa.finalizeRemoteCopyAtSource();		
	}

	/**
	 *
	 */
	private static def copyToHere(
			rmtIndex:RemoteArray[Int], 
			rmtValue:RemoteArray[Double],
			smlist:DistArray[SparseCSC](1),
			datCnt:Int,
			plist:Array[Int](1),
			root:Int): void {
		
		val mypid  = here.id();
		val rcvspa = smlist(mypid);
		//Copy data from source place
		if (mypid != root) {
			rcvspa.initRemoteCopyAtDest(datCnt);
			if (datCnt > 0) {
				finish Array.asyncCopy[Int   ](rmtIndex, 0, rcvspa.getIndex(), 0, datCnt);
				finish Array.asyncCopy[Double](rmtValue, 0, rcvspa.getValue(), 0, datCnt);
			}	
		}
		
		//Goto next place in the list
		if (plist.size >= 1) {
			val nxtpid = plist(0); // Get next place id in the list
			val rmtidx = new RemoteArray[Int   ](rcvspa.getIndex() as Array[Int]{self!=null});
			val rmtval = new RemoteArray[Double](rcvspa.getValue() as Array[Double]{self!=null});
			val nplist = new Array[Int](plist.size-1, (i:Int)=>plist(i+1));
			at (smlist.dist(nxtpid)) {
				//Need: rmtidx, rmtval, dmlist, datCnt, nplist, root
				copyToHere(rmtidx, rmtval, smlist, datCnt, nplist, root);
			}
		}

		if (mypid != root) {
			rcvspa.finalizeRemoteCopyAtDest();
			//rcvspa.print("Ring cast receive "+datCnt+" data at Place:"+mypid);
		}
	}
}