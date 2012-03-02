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
import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;

import x10.matrix.Debug;

@NativeCPPInclude("mpi/mpi_api.h")

@NativeCPPCompilationUnit("mpi/mpi_api.c")

/**
 * This class provides methods in X10 to invoke MPI routines.  
 * Currently, the native backend running on MPI transport is supported.
 * 
 * <p> To use MPI communication routines via X10 interface, please note following 
 * limitations. 
 * First, there are not topology management functions.  All places in X10 runtime
 * must have place IDs mapped to the same process rank IDs in MPI_COMM_WORLD communicator.
 * 
 * <p> Second, not all MPI communication APIs are available. 
 * Buffered and sync P2P communication mode are not supported. 
 * Non-blocking communication is available, however, X10 "async-finish" can achieve the same 
 * effect.  
 * 
 * <p> Third, collective communication methods including bcast, scatter, gather
 * and reduce sum are available.  X10.team collective communication can be used. 
 * 
 *
 */
public class WrapMPI {
    
	@Native("c++","mpi_new_comm()")
		public static native def gml_new_commu():void;

	@Native("c++","mpi_get_proc_info((#1)->raw()->raw(), (#2)->raw()->raw(), (#3)->raw()->raw(), (#4)->raw()->raw())")
		public static native def get_proc_info(rk:Rail[Int], np:Rail[Int], hlen:Rail[Int], hstr:Rail[Int]):void;

    @Native("c++","mpi_get_name_maxlen((#1)->raw()->raw())")
		public static native def get_name_maxlen(l:Rail[Int]):void;
	//
    @Native("c++","mpi_get_comm_nproc((#1)->raw()->raw())")
		public static native def get_comm_nproc(np:Rail[Int]):void;
	//
    @Native("c++","mpi_get_comm_pid((#1)->raw()->raw())")
		private static native def get_comm_pid(rk:Rail[Int]):void;

	//
	//================================================================
	//public val id:Int;
	//
    public static val world:WrapMPI = new WrapMPI();
    
	public val dist:Dist(1);
	//================================================================
	
	public def this(d:Dist(1)) {
		
		// Distribution is unique;
		dist = d;
		@Ifdef("MPI_COMMU") {
				
			ateach(d) {
				gml_new_commu();
			}			
			//---------------------------
			// This is for testing purpose
			if (d.numPlaces() > 1) {
				finish for ([p]:Point in d) {
					//val pp = Point.make([p]) as Point(dist.region.rank);
					val pl = dist(p);
					async {
						val mpi_pid = at (pl) world.getCommProcID();
						if (p != mpi_pid) {
							Console.OUT.println("Place "+p+" is not proc id "+mpi_pid);
							Console.OUT.println("Some collective operations will not work correctly");
							Console.OUT.flush();
						}
					}
				}
			}
		}
	}

	/**
	 * No check constructor
	 */
	public def this() {
		dist = Dist.makeUnique();
		//pidmap = new Array[Int](Place.MAX_PLACES, (i:Int)=>i);
		//displs = new Array[Int](Place.MAX_PLACES, 0);
		@Ifdef("MPI_COMMU") {
			ateach (Dist.makeUnique()) {
				gml_new_commu();
			}
		}
	}
	//================================================================

	/**
	 * Return MPI process's host name.
	 */
	public static def getProcInfo():String {

		val mlen = 128;//mpi_name_maxlen();
		val rk   = new Rail[Int](1, 0);
		val np   = new Rail[Int](1, 0);
		val hlen = new Rail[Int](1, 0);
		val hstr = new Rail[Int](mlen, 0);
		world.get_proc_info(rk, np, hlen, hstr);
		val sc = new Rail[Char](hlen(0), (i:Int)=>(hstr(i) as Char));
		return new String(sc, 0, hlen(0));
	}

	public def getNumProc():Int = Place.MAX_PLACES;//pidmap.size;

	/**
	 * Return MPI process rank ID at here.
	 */
	public static def getCommProcID():Int {
		val rk = new Rail[Int](1, 0);
		world.get_comm_pid(rk);
		return rk(0);
	}

	//================================================================
	// Double based
	//================================================================

	//-------------------------------------------
	// Blocking P2P communication
	//
	/**
	 * Send double-precision array.
	 *
	 */
    @Native("c++","mpi_send_double((#1)->raw()->raw(), #2, #3, #4, #5)")
		static native def send_double(
			buf:Array[Double](1), 
			off:Int, 
			cnt:Int, 
			dst:Int, 
			tag:Int):void;


	/**
	 * Send double-precision data array.
	 */
	public def send(buf:Array[Double](1), cnt:Int, dst:Int, tag:Int): void {
		send(buf, 0, cnt, dst, tag);
	}

	/**
	 * Send double-precision data array.
	 *
	 * @param buf			data buffer in Double-precision array
	 * @param off			offset for the starting index
	 * @param cnt			count of data to sent
	 * @param dst			destination process rank or place ID.
	 * @param tag 			message tag
	 */	
	public def send(buf:Array[Double](1), off:Int, cnt:Int, dst:Int, tag:Int) : void {
		send_double(buf, off, cnt, dst, tag);
	}
	//-------------
	/**
	 * Receive double-precision data array.
	 */
    @Native("c++","mpi_recv_double((#1)->raw()->raw(), #2, #3, #4, #5)")
		static native def recv_double(
			buf:Array[Double](1), 
			off:Int, 
			cnt:Int, 
			rsc:Int, 
			tag:Int):void;
	
	/**
	 * Receive double-precision data array.
	 */
	public def recv(buf:Array[Double](1), cnt:Int, src:Int, tag:Int): void {
		recv(buf, 0, cnt, src, tag);
	}

	/**
	 * Receive double-precision data array.
	 *
	 * @param buf			receiving data buffer in double-precision array 
	 * @param off			offset for the starting index for the receiving data
	 * @param cnt			count of data to receive
	 * @param src			source process rank or place ID.
	 * @param tag 			message tag
	 */
	public def recv(buf:Array[Double](1), off:Int, cnt:Int, src:Int, tag:Int) : void {
		recv_double(buf, off, cnt, src, tag);
	}

	//--------------------------------------------
	// Non-blocking double P2P
	//

	/**
	 * Non-blocking send double-precision data array.
	 *
	 */
    @Native("c++","mpi_Isend_double((#1)->raw()->raw(),#2,#3,#4, #5, (#6)->raw()->raw())")
		static native def Isend_double(
			buf:Array[Double](1), 
			off:Int, 
			cnt:Int, 
			dst:Int, 
			tag:Int, 
			rh:Rail[Int]):void;
	//
	/**
	 * Non-blocking send double-precision data array.
	 */
	public def immSend(buf:Array[Double](1), cnt:Int, dst:Int, tag:Int ):RequestHandleMPI = 
		immSend(buf, 0, cnt, dst, tag);

	/**
	 * Non-block send double-precision data array.
	 *
	 * @param buf			receiving data buffer in double-precision array 
	 * @param off			offset for the starting index for the receiving data
	 * @param cnt			count of data to receive
	 * @param dst			destination process rank or place ID.
	 * @param tag 			message tag
	 * @param rh			sending message handle
	 */
	public def immSend(
			buf:Array[Double](1), 
			off:Int, 
			cnt:Int, 
			dst:Int, 
			tag:Int
			):RequestHandleMPI {

		val req = new RequestHandleMPI();
		//val p_dst = pidmap(dst);
		Isend_double(buf, off, cnt, dst, tag, req.handle);
		return req;
	}

	//===================================================

	/**
	 * Non-blocking receive double-precision data array.
	 */
    @Native("c++","mpi_Irecv_double((#1)->raw()->raw(),#2,#3,#4,#5, (#6)->raw()->raw())")
		static native def Irecv_double(
			buf:Array[Double](1), 
			off:Int, 
			cnt:Int, 
			rsc:Int, 
			tag:Int, 
			rh:Rail[Int]):void;
	//

	/**
	 * Non-blocking receive double-precision data array.
	 */
	public def immRecv(buf:Array[Double](1), cnt:Int, src:Int, tag:Int) :RequestHandleMPI = 
		immRecv(buf, 0, cnt, src, tag);


	/**
	 * Non-blocking receive double-precision data array.
	 *
	 * @param buf			receiving data buffer in double-precision array 
	 * @param off			offset for the starting index for the receiving data
	 * @param cnt			count of data to receive
	 * @param src			source process rank or place ID.
	 * @param tag 			message tag
	 * @param rh			receive message handle
	 */
	public def immRecv(
			buf:Array[Double](1), 
			off:Int, 
			cnt:Int, 
			src:Int, 
			tag:Int):RequestHandleMPI {

		val req = new RequestHandleMPI();
		//val p_src = pidmap(src);
		Irecv_double(buf, off, cnt, src, tag, req.handle);
		return req;
	}
	//


	//====================================================================
	// Integer based
	//====================================================================

	//--------------------------------------------
	// Blocking P2P communication
	//
	/**
	 * Send integer array.
	 */
    @Native("c++","mpi_send_int((#1)->raw()->raw(), #2, #3, #4, #5)")
		static native def send_int(buf:Array[Int](1), off:Int, cnt:Int, dst:Int, tag:Int):void;
	

	public def send(buf:Array[Int](1),	cnt:Int, dst:Int, tag:Int): void {
		send(buf, 0, cnt, dst, tag);
	}

	/**
	 * Send integer array
	 *
	 * @param buf			sending data buffer of integer array 
	 * @param off			starting index for the sending data
	 * @param cnt			count of data to receive
	 * @param dst			source process rank or place ID.
	 * @param tag 			message tag
	 */
	public def send(
			buf:Array[Int](1), 
			off:Int, 
			cnt:Int,
			dst:Int,
			tag:Int) : void {

		send_int(buf, off, cnt, dst, tag);
	}
	
    @Native("c++","mpi_recv_int((#1)->raw()->raw(), #2, #3, #4, #5)")
		static native def recv_int(
			buf:Array[Int](1), 
			off:Int, cnt:Int, rsc:Int, 
			tag:Int):void;
	
	/**
	 * Receive integer array
	 *
	 * @param buf			receive data buffer of integer array 
	 * @param off		    the starting index for the receiving data
	 * @param cnt			count of data to receive
	 * @param dst			destination process rank or place ID.
	 * @param tag 			message tag
	 */
	public def recv(buf:Array[Int](1), cnt:Int, src:Int, tag:Int) : void {
		recv(buf, 0, cnt, src, tag);
	}

	public def recv(
			buf:Array[Int](1), 
			off:Int, cnt:Int, src:Int, 
			tag:Int) : void {
		recv_int(buf, off, cnt, src, tag);
	}
	
	//--------------------------------
	// bcast
	/**
	 * Broadcast routine.
	 */
	@Native("c++", "mpi_bcast_double((#1)->raw()->raw(),#2,#3,#4)")
		static native def mpi_bcast(buf:Array[Double](1), off:Int, cnt:Int, root:Int):void;
	//void mpi_bcast_double(double* buf, int off, int cnt, int root);


	/**
	 * Broadcast double-precision data array.
	 *
	 * @param buf			data buffer to broadcast
	 * @param off		    the starting index for the broadcast data
	 * @param cnt			count of data to receive
	 * @param root			root place which has the source of the broadcast data.
	 */
	public def bcast(buf:Array[Double](1), off:int, cnt:Int, root:Int):void {
		mpi_bcast(buf, off, cnt, root);
	}

	public def bcast(buf:Array[Double](1), cnt:Int, root:Int) :void {
		mpi_bcast(buf, 0, cnt, root);
	}

	/**
	 * Broadcast integer data array
	 */
	@Native("c++", "mpi_bcast_int((#1)->raw()->raw(),#2,#3,#4)")
		static native def mpi_bcast(buf:Array[Int](1), off:Int, cnt:Int, root:Int):void;

	/**
	 * Broadcast integer data array.
	 *
	 * @param buf			data buffer to broadcast. Significant in all places.
	 * @param off		    the starting index for the broadcast data
	 * @param cnt			count of data to receive
	 * @param root			root place
	 */	
	public def bcast(buf:Array[Int](1), off:int, cnt:Int, root:Int):void {
		mpi_bcast(buf, off, cnt, root);
	}

	public def bcast(buf:Array[Int](1), cnt:Int, root:Int) :void {
		mpi_bcast(buf, 0, cnt, root);
	}


	//--------------------------------
	// Gatherv
	/**
	 * MPI gatherv native routine.
	 */
	@Native("c++", "mpi_gatherv_double((#1)->raw()->raw(),#2,#3,(#4)->raw()->raw(),#5,(#6)->raw()->raw(),(#7)->raw()->raw(),#8)")
		static native def gatherv_double(
			sendbuf:Array[Double](1), 
			sendoff:Int, sendcnt:Int,
			recvbuf:Array[Double](1),
			recvoff:Int, recvcnts:Array[Int](1),
			recvdispls:Array[Int](1),
			root:Int):void;

	/**
	 * Gather double-precision data arrays from all places to here.
	 *
	 * @param sendbuf			sending buffer. Significant in all places.
	 * @param sendcnt           count of data to send to root place.
	 * @param recvbuf			receiving buffer. Significant at the root place. 0-length array for non-root place.
	 * @param recvcnts 			the data counts received from all places. Significant only at the root place
	 * @param root			    root place ID.
	 *
	 */
	public def gatherv(
			sendbuf:Array[Double](1), 
			sendcnt:Int,
			recvbuf:Array[Double](1),
			recvcnts:Array[Int](1),
			root:Int):void {
		gatherv(sendbuf, 0, sendcnt, recvbuf, 0, recvcnts, root);
	}

	//----
	public def gatherv(
			sendbuf:Array[Double](1), sendoff:Int, sendcnt:Int,
			recvbuf:Array[Double](1), recvoff:Int,
			recvcnts:Array[Int](1),
			root:Int):void {

		//Compute displs, since the recv data is adjacent to each other.
		var displs:Array[Int](1);
		//Debug.assure(pidmap(0) == 0, "Inconsistance found in pid map");
		if (root == here.id() ) {
			displs = new Array[Int](Place.MAX_PLACES, 0);
			for (var i:Int=1; i<displs.size; i++) {
				//Debug.assure(pidmap(i) == i, "Inconsistance found in pid map");
				displs(i) = displs(i-1) + recvcnts(i-1);
			}
		} else {
			//Fake displs space, non-significant at non-root
			displs = new Array[Int](0);
		}

		gatherv_double(sendbuf, sendoff, sendcnt, 
			recvbuf, recvoff, recvcnts, displs, 
			root);
	}
	
	//--------------------

	/**
	 * Gather integer data arrays from all places
	 */
	@Native("c++", "mpi_gatherv_int((#1)->raw()->raw(),#2,#3,(#4)->raw()->raw(),#5,(#6)->raw()->raw(),(#7)->raw()->raw(),#8)")
		static native def gatherv_int(
			sendbuf:Array[Int](1), 
			sendoff:Int, sendcnt:Int,
			recvbuf:Array[Int](1), recvoff:Int, recvcnts:Array[Int](1), recvdispls:Array[Int](1), 
			root:Int):void;

	public def gatherv(
			sendbuf:Array[Int](1), sendcnt:Int,
			recvbuf:Array[Int](1), recvcnts:Array[Int](1),
			root:Int):void {
		gatherv(sendbuf, 0, sendcnt, recvbuf, 0, recvcnts, root);
	}

	public def gatherv(
			sendbuf:Array[Int](1), sendoff:Int, sendcnt:Int,
			recvbuf:Array[Int](1), recvoff:Int,
			recvcnts:Array[Int](1),
			root:Int):void {
		//Compute displs, since the recv data is adjacent to each other.
		//displs(0) = 0;
		//Debug.assure(pidmap(0) == 0, "Inconsistance found in pid map");
		var displs:Array[Int](1);
		if (root == here.id() ) {
			displs = new Array[Int](Place.MAX_PLACES, 0);
			for (var i:Int=1; i<displs.size; i++) {
				//Debug.assure(pidmap(i) == i, "Inconsistance found in pid map");
				displs(i) = displs(i-1) + recvcnts(i-1);
			}
		} else {
			displs = new Array[Int](1); //Fake
		}

		gatherv_int(sendbuf, sendoff, sendcnt, 
					recvbuf, recvoff, recvcnts, displs, 
					root);

	}
	//--------------------------------
	// Scatter
	/**
	 * Scatter double-precision data array from here to all places
	 */
	@Native("c++", "mpi_scatterv_double((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),#5,#6)")
		static native def scatterv_double(
			sendbuf:Array[Double](1), sendcnts:Array[Int](1), displs:Array[Int](1),
			recvbuf:Array[Double](1), recvcnt:Int,
			root:Int):void;
	//----

	/**
	 * Scatter double-precision data array. 
	 *
	 * @param sendbuf			the data source buffers containing segments to all places.
	 * @param sendcnts			the data counts for all places. Significant at root only. 0-length array for non-root.
	 * @param recvbuf			data receiving buffer in each place.
	 * @param recvcnt			data receiving counts at the receiving place
	 * @param root			    root place ID.
	 */
	public def scatterv(
			sendbuf:Array[Double](1), sendcnts:Array[Int](1),
			recvbuf:Array[Double](1), recvcnt:Int,
			root:Int):void {
		//Compute displs. The recv data is adjacent to each other.
		//displs(0) = 0;
		//Debug.assure(pidmap(0) == 0, "Inconsistance found in pid map");
		var displs:Array[Int](1);
		if (root == here.id() ) {
			displs = new Array[Int](Place.MAX_PLACES, 0);
			for (var i:Int=1; i<displs.size; i++) {
				//Debug.assure(pidmap(i) == i, "Inconsistance found in pid map");
				displs(i) = displs(i-1) + sendcnts(i-1);
			}
		} else {
			displs = new Array[Int](1);
		}

		scatterv_double(sendbuf, sendcnts, displs,
						recvbuf, recvcnt, 
						root);

	}
	
	//--------------------
	/**
	 * Scattering integer data arrays
	 */
	@Native("c++", "mpi_scatterv_int((#1)->raw()->raw(),(#2)->raw()->raw(),(#3)->raw()->raw(),(#4)->raw()->raw(),#5,#6)")
		static native def scatterv_int(
			sendbuf:Array[Int](1), sendcnts:Array[Int](1), displs:Array[Int](1),
			recvbuf:Array[Int](1), recvcnt:Int,
			root:Int):void;
	//----
	public def scatterv(
			sendbuf:Array[Int](1), sendcnts:Array[Int](1),
			recvbuf:Array[Int](1), recvcnt:Int,
			root:Int):void {
		//Compute displs, since the recv data is adjacent to each other.
		//displs(0) = 0;
		//Debug.assure(pidmap(0) == 0, "Inconsistance found in pid map");
		var displs:Array[Int](1);
		if (root == here.id() ) {
			displs = new Array[Int](Place.MAX_PLACES, 0);			
			for (var i:Int=1; i<displs.size; i++) {
				//Debug.assure(pidmap(i) == i, "Inconsistance found in pid map");
				displs(i) = displs(i-1) + sendcnts(i-1);
			}
		} else {
			displs = new Array[Int](1); //Fake
		}

		scatterv_int(sendbuf, sendcnts, displs,
					 recvbuf, recvcnt,
					 root);
	}
	

	//--------------------------------
	// allgatherv
	/**
	 * All gather, same as Gather+bcast.
	 */
	@Native("c++", "mpi_allgatherv_double((#1)->raw()->raw(),#2,#3,(#4)->raw()->raw(),#5,(#6)->raw()->raw(),(#7)->raw()->raw())")
		static native def all_gatherv(
			sendbuf:Array[Double](1), sendoff:Int, sendcnt:Int,
			recvbuf:Array[Double](1), recvoff:Int, recvcnts:Array[Int](1), recvdispls:Array[Int](1)):void;
	//----

	/**
	 * All gather double-precision arrays from all places
	 *
	 * @param sendbuf			data array to send to all places
	 * @param sendoff			offset of the starting index in sending buffer
	 * @param sendcnt           the data count to send
	 * @param recvbuf			receiving data buffer
	 * @param recvoff			offset of the starting index in receiving buffer
	 * @param recvcnts			data counts received from all places
	 */
	public def allGatherv(
			sendbuf:Array[Double](1), sendoff:Int, sendcnt:Int,
			recvbuf:Array[Double](1), recvoff:Int, recvcnts:Array[Int](1) ):void {

		//Compute displs, since the recv data is adjacent to each other.
		//displs(0) = 0;
		//Debug.assure(pidmap(0) == 0, "Inconsistance found in pid map");
		var displs:Array[Int](1)=new Array[Int](Place.MAX_PLACES);
		
		for (var i:Int=1; i<Place.MAX_PLACES; i++) {
			//Debug.assure(pidmap(i) == i, "Inconsistance found in pid map");
			displs(i) = displs(i-1) + recvcnts(i-1);
		}
		all_gatherv(sendbuf, sendoff, sendcnt, 
					recvbuf, recvoff, recvcnts, displs);
	}

	//------
	// reduce sum
	/**
	 * Reduce all values from all places to a single values by adding them all.
	 */
	@Native("c++", "mpi_reduce_sum_double((#1)->raw()->raw(),#2,(#3)->raw()->raw(),#4,#5,#6)")
		static native def reduce_sum(
			sendbuf:Array[Double](1), sendoff:Int,
			recvbuf:Array[Double](1), recvoff:Int, cnt:Int,
			root:Int):void;
	//void mpi_reduce_sum_double(double* sendbuf, int soff, double* recvbuf, int roff,  
	//int cnt, int root);
	/**
	 * Reduce all values from all places to one by adding them up. 
	 *
	 * @param sendbuf			the source data buffer
	 * @param sendoff			the starting offset in source buffer
	 * @param recvbuf			the receiving buffer for the result of addition
	 * @param recvoff 			the starting offset in receiving buffer.
	 * @param cnt			    data count in the array
	 * @param root   			root place ID
	 */
	public def reduceSum(
			sendbuf:Array[Double](1), sendoff:Int, 
			recvbuf:Array[Double](1), recvoff:Int,
			cnt:Int, root:Int):void {
		reduce_sum(sendbuf, sendoff, recvbuf, recvoff, cnt, root);
	}

	/**
	 * Reduce all values from all places to one by adding them up.
	 */
	public def reduceSum(sendbuf:Array[Double](1), recvbuf:Array[Double](1),
			cnt:Int, root:Int):void {
		reduce_sum(sendbuf, 0, recvbuf, 0, cnt, root);
	}
	//---------
	// all reduce sum

	/**
	 * Reduce-sum and broadcast result to all places
	 */
	@Native("c++", "mpi_allreduce_sum_double((#1)->raw()->raw(),#2,(#3)->raw()->raw(),#4,#5)")
		public static native def allReduceSum(
			sendbuf:Array[Double](1), sendoff:Int,
			recvbuf:Array[Double](1), recvoff:Int,
			cnt:Int):void;

	//void mpi_allreduce_sum_double(double*sendbuf, int soff, double* recvbuf, int soff, int cnt);

	/**
	 * Reduce-sum and broadcast result to all places
	 *
	 * @param sendbuf			data source
	 * @param recvbuf			result buffer
	 * @param cnt      			data count 
	 */
	public def allReduceSum(
			sendbuf:Array[Double](1), 
			recvbuf:Array[Double](1),	
			cnt:Int): void {
		allReduceSum(sendbuf, 0, recvbuf, 0, cnt);
	}
	//--------------------------------------------
	// Non-blocking int P2P
	//
    @Native("c++","mpi_Isend_int((#1)->raw()->raw(),#2,#3,#4,#5,(#6)->raw()->raw())")
		static native def Isend_int(
			buf:Array[Int](1), off:Int, cnt:Int, 
			dst:Int, tag:Int, rh:Rail[Int]):void;
	//
	public def immSend(
			buf:Array[Int](1), cnt:Int, dst:Int, 
			tag:Int) : RequestHandleMPI = 
		immSend(buf, 0, cnt, dst, tag);

	public def immSend(
			buf:Array[Int](1), off:Int, cnt:Int, 
			dst:Int, tag:Int ):RequestHandleMPI {
		val req = new RequestHandleMPI();
		//val p_dst = pidmap(dst);
		Isend_int(buf, off, cnt, dst, tag, req.handle);
		return req;
	}
	//
    @Native("c++","mpi_Irecv_int((#1)->raw()->raw(),#2,#3,#4,#5,(#6)->raw()->raw())")
		static native def Irecv_int(
			buf:Array[Int](1), off:Int, cnt:Int, 
			rsc:Int, tag:Int, rh:Rail[Int]):void;
	//
	public def immRecv(	buf:Array[Int](1), cnt:Int, src:Int, tag:Int):RequestHandleMPI = 
		immRecv(buf, 0, cnt, src, tag);

	public def immRecv(
			buf:Array[Int](1), off:Int, cnt:Int, 
			src:Int, tag:Int ):RequestHandleMPI {
		val req = new RequestHandleMPI();
		//val p_src = pidmap(src);
		Isend_int(buf, off, cnt, src, tag, req.handle);
		return req;
	}

	//---------------------------------------------
	// Request waiting
	public static def mywait(rh:RequestHandleMPI) { rh.mywait();}
	//
	public static def test(rh:RequestHandleMPI) = rh.test(); 
	
}
