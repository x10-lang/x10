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

package x10.matrix.comm.mpi;

import x10.regionarray.Dist;
import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;

import x10.matrix.util.Debug;

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
 */
@NativeCPPInclude("mpi_api.h")
@NativeCPPCompilationUnit("mpi_api.cc")
public class WrapMPI {
    
	@Native("c++","mpi_new_comm()")
		public static native def gml_new_commu():void;

	@Native("c++","mpi_get_proc_info((#1)->raw, (#2)->raw, (#3)->raw, (#4)->raw)")
		public static native def get_proc_info(rk:Rail[Int], np:Rail[Int], hlen:Rail[Int], hstr:Rail[Int]):void;

    @Native("c++","mpi_get_name_maxlen((#1)->raw)")
		public static native def get_name_maxlen(l:Rail[Int]):void;

    @Native("c++","mpi_get_comm_nproc((#1)->raw)")
		public static native def get_comm_nproc(np:Rail[Int]):void;

    @Native("c++","mpi_get_comm_pid((#1)->raw)")
		private static native def get_comm_pid(rk:Rail[Int]):void;

    public static val world:WrapMPI = new WrapMPI();
    
	public val dist:Dist(1);

	public def this(d:Dist(1)) {
		// Distribution is unique;
		dist = d;
		@Ifdef("MPI_COMMU") {
			finish ateach(d) {
				gml_new_commu();
			}			

			// This is for testing purpose
			if (d.numPlaces() > 1) {
				finish for ([p] in d) {
					//val pp = Point.make([p]) as Point(dist.region.rank);
					val pl = dist(p);
					async {
						val mpi_pid = at(pl) world.getCommProcID();
						if ((p as Int) != mpi_pid) {
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
		//pidmap = new Rail[Int](Place.numPlaces(), (i:Int)=>i);
		//displs = new Rail[Int](Place.numPlaces(), 0);
		@Ifdef("MPI_COMMU") {
			finish ateach(Dist.makeUnique()) {
				gml_new_commu();
			}
		}
	}

	/**
	 * Return MPI process's host name.
	 */
	public static def getProcInfo():String {
		val mlen = 128;//mpi_name_maxlen();
		val rk   = new Rail[Int](1, 0n);
		val np   = new Rail[Int](1, 0n);
		val hlen = new Rail[Int](1, 0n);
		val hstr = new Rail[Int](mlen, 0n);
		world.get_proc_info(rk, np, hlen, hstr);
		val sc = new Rail[Char](hlen(0), (i:Long)=>(hstr(i) as Char));
		return new String(sc, 0, hlen(0));
	}

	/**
	 * Return MPI process rank ID at here.
	 */
	public static def getCommProcID():Int {
		val rk = new Rail[Int](1, 0n);
		world.get_comm_pid(rk);
		return rk(0);
	}

	/**
	 * Send double-precision array.
	 */
    @Native("c++","mpi_send_double((#1)->raw, #2, #3, #4, #5)")
		static native def send_double(
			buf:Rail[Double], 
			off:Int, 
			cnt:Int, 
			dst:Int, 
			tag:Int):void;

	/**
	 * Send double-precision data array.
	 */
	public def send(buf:Rail[Double], cnt:Long, dst:Long, tag:Long): void {
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
	public def send(buf:Rail[Double], off:Long, cnt:Long, dst:Long, tag:Long) : void {
		send_double(buf, off as Int, cnt as Int, dst as Int, tag as Int);
	}

	/**
	 * Receive double-precision data array.
	 */
    @Native("c++","mpi_recv_double((#1)->raw, #2, #3, #4, #5)")
		static native def recv_double(
			buf:Rail[Double], 
			off:Int, 
			cnt:Int, 
			rsc:Int, 
			tag:Int):void;
	
	/**
	 * Receive double-precision data array.
	 */
	public def recv(buf:Rail[Double], cnt:Long, src:Long, tag:Long): void {
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
	public def recv(buf:Rail[Double], off:Long, cnt:Long, src:Long, tag:Long) : void {
		recv_double(buf, off as Int, cnt as Int, src as Int, tag as Int);
	}

	/**
	 * Non-blocking send double-precision data array.
	 */
    @Native("c++","mpi_Isend_double((#1)->raw,#2,#3,#4, #5, (#6)->raw)")
		static native def Isend_double(
			buf:Rail[Double], 
			off:Int, 
			cnt:Int, 
			dst:Int, 
			tag:Int, 
			rh:Rail[Int]):void;

	/**
	 * Non-blocking send double-precision data array.
	 */
	public def immSend(buf:Rail[Double], cnt:Long, dst:Long, tag:Long):RequestHandleMPI = 
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
			buf:Rail[Double], 
			off:Long, 
			cnt:Long, 
			dst:Long, 
			tag:Long
			):RequestHandleMPI {

		val req = new RequestHandleMPI();
		//val p_dst = pidmap(dst);
		Isend_double(buf, off as Int, cnt as Int, dst as Int, tag as Int, req.handle);
		return req;
	}

	/**
	 * Non-blocking receive double-precision data array.
	 */
    @Native("c++","mpi_Irecv_double((#1)->raw,#2,#3,#4,#5, (#6)->raw)")
		static native def Irecv_double(
			buf:Rail[Double], 
			off:Int, 
			cnt:Int, 
			rsc:Int, 
			tag:Int, 
			rh:Rail[Int]):void;

	/**
	 * Non-blocking receive double-precision data array.
	 */
	public def immRecv(buf:Rail[Double], cnt:Long, src:Long, tag:Long) :RequestHandleMPI = 
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
			buf:Rail[Double], 
			off:Long, 
			cnt:Long, 
			src:Long, 
			tag:Long):RequestHandleMPI {

		val req = new RequestHandleMPI();
		//val p_src = pidmap(src);
		Irecv_double(buf, off as Int, cnt as Int, src as Int, tag as Int, req.handle);
		return req;
	}

	/**
	 * Send integer array.
	 */
    @Native("c++","mpi_send_long((#1)->raw, #2, #3, #4, #5)")
		static native def send_long(buf:Rail[Long], off:Int, cnt:Int, dst:Int, tag:Int):void;

	public def send(buf:Rail[Long],	cnt:Long, dst:Long, tag:Long): void {
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
			buf:Rail[Long], 
			off:Long, 
			cnt:Long,
			dst:Long,
			tag:Long) : void {

		send_long(buf, off as Int, cnt as Int, dst as Int, tag as Int);
	}
	
    @Native("c++","mpi_recv_long((#1)->raw, #2, #3, #4, #5)")
		static native def recv_long(
			buf:Rail[Long], 
			off:Int, cnt:Int, rsc:Int, 
			tag:Int):void;
	
	/**
	 * Receive integer array
	 *
	 * @param buf			receive data buffer of Long array 
	 * @param off		    the starting index for the receiving data
	 * @param cnt			count of data to receive
	 * @param dst			destination process rank or place ID.
	 * @param tag 			message tag
	 */
	public def recv(buf:Rail[Long], cnt:Long, src:Long, tag:Long):void {
		recv(buf, 0L, cnt, src, tag);
	}

	public def recv(
			buf:Rail[Long], 
			off:Long, cnt:Long, src:Long, 
			tag:Long):void {
		recv_long(buf, off as Int, cnt as Int, src as Int, tag as Int);
	}
	

	/**
	 * Broadcast routine.
	 */
	@Native("c++", "mpi_bcast_double((#1)->raw,#2,#3,#4)")
		static native def mpi_bcast(buf:Rail[Double], off:Int, cnt:Int, root:Int):void;
	//void mpi_bcast_double(double* buf, int off, int cnt, int root);


	/**
	 * Broadcast double-precision data array.
	 *
	 * @param buf			data buffer to broadcast
	 * @param off		    the starting index for the broadcast data
	 * @param cnt			count of data to receive
	 * @param root			root place which has the source of the broadcast data.
	 */
	public def bcast(buf:Rail[Double], off:Long, cnt:Long, root:Long):void {
		mpi_bcast(buf, off as Int, cnt as Int, root as Int);
	}

	public def bcast(buf:Rail[Double], cnt:Long, root:Long) :void {
		mpi_bcast(buf, 0n, cnt as Int, root as Int);
	}

	/**
	 * Broadcast integer data array
	 */
	@Native("c++", "mpi_bcast_long((#1)->raw,#2,#3,#4)")
		static native def mpi_bcast(buf:Rail[Long], off:Int, cnt:Int, root:Int):void;

	/**
	 * Broadcast integer data array.
	 *
	 * @param buf			data buffer to broadcast. Significant in all places.
	 * @param off		    the starting index for the broadcast data
	 * @param cnt			count of data to receive
	 * @param root			root place
	 */	
	public def bcast(buf:Rail[Long], off:Long, cnt:Long, root:Long):void {
		mpi_bcast(buf, off as Int, cnt as Int, root as Int);
	}

	public def bcast(buf:Rail[Long], cnt:Long, root:Long) :void {
		mpi_bcast(buf, 0n, cnt as Int, root as Int);
	}

	/**
	 * MPI gatherv native routine.
	 */
	@Native("c++", "mpi_gatherv_double((#1)->raw,#2,#3,(#4)->raw,#5,(#6)->raw,(#7)->raw,#8)")
		static native def gatherv_double(
			sendbuf:Rail[Double], 
			sendoff:Int, sendcnt:Int,
			recvbuf:Rail[Double],
			recvoff:Int, recvcnts:Rail[Int],
			recvdispls:Rail[Int],
			root:Int):void;

	/**
	 * Gather double-precision data arrays from all places to here.
	 *
	 * @param sendbuf			sending buffer. Significant in all places.
	 * @param sendcnt           count of data to send to root place.
	 * @param recvbuf			receiving buffer. Significant at the root place. 0-length array for non-root place.
	 * @param recvcnts 			the data counts received from all places. Significant only at the root place
	 * @param root			    root place ID.
	 */
	public def gatherv(
			sendbuf:Rail[Double], 
			sendcnt:Long,
			recvbuf:Rail[Double],
			recvcnts:Rail[Long],
			root:Long):void {
		gatherv(sendbuf, 0, sendcnt, recvbuf, 0, recvcnts, root);
	}

	public def gatherv(
			sendbuf:Rail[Double], sendoff:Long, sendcnt:Long,
			recvbuf:Rail[Double], recvoff:Long,
			recvcnts:Rail[Long],
			root:Long):void {

		//Compute displs, since the recv data is adjacent to each other.
		var displs:Rail[Int];
        var intRecvcnts:Rail[Int];
		//Debug.assure(pidmap(0) == 0, "Inconsistance found in pid map");
		if (root == here.id()) {
			displs = new Rail[Int](Place.numPlaces());
            intRecvcnts = new Rail[Int](Place.numPlaces());
            intRecvcnts(0) = recvcnts(0) as Int;
			for (var i:Long=1; i<displs.size; i++) {
				//Debug.assure(pidmap(i) == i, "Inconsistance found in pid map");
                intRecvcnts(i) = recvcnts(i) as Int;
				displs(i) = displs(i-1) + intRecvcnts(i-1);
			}
		} else {
			//dummay arrays, non-significant at non-root
			displs = new Rail[Int](0);
            intRecvcnts = new Rail[Int](0);
		}

		gatherv_double(sendbuf, sendoff as Int, sendcnt as Int, 
			recvbuf, recvoff as Int, intRecvcnts, displs, 
			root as Int);
	}

	/**
	 * Gather integer data arrays from all places
	 */
	@Native("c++", "mpi_gatherv_long((#1)->raw,#2,#3,(#4)->raw,#5,(#6)->raw,(#7)->raw,#8)")
		static native def gatherv_long(
			sendbuf:Rail[Long], 
			sendoff:Int, sendcnt:Int,
			recvbuf:Rail[Long], recvoff:Int, recvcnts:Rail[Int], recvdispls:Rail[Int], 
			root:Int):void;

	public def gatherv(
			sendbuf:Rail[Long], sendcnt:Long,
			recvbuf:Rail[Long], recvcnts:Rail[Long],
			root:Long):void {
		gatherv(sendbuf, 0, sendcnt, recvbuf, 0, recvcnts, root);
	}

	public def gatherv(
			sendbuf:Rail[Long], sendoff:Long, sendcnt:Long,
			recvbuf:Rail[Long], recvoff:Long,
			recvcnts:Rail[Long],
			root:Long):void {
		//Compute displs, since the recv data is adjacent to each other.
		//displs(0) = 0;
		//Debug.assure(pidmap(0) == 0, "Inconsistance found in pid map");
        var intRecvcnts:Rail[Int];
		var displs:Rail[Int];
		if (root == here.id()) {
			displs = new Rail[Int](Place.numPlaces(), 0n);
            intRecvcnts = new Rail[Int](Place.numPlaces());
            intRecvcnts(0) = recvcnts(0) as Int;
			for (var i:Long=1; i<displs.size; i++) {
				//Debug.assure(pidmap(i) == i, "Inconsistance found in pid map");
                intRecvcnts(i) = recvcnts(i) as Int;
				displs(i) = displs(i-1) + intRecvcnts(i-1);
			}
		} else {
			displs = new Rail[Int](0n); //Dummy
            intRecvcnts = new Rail[Int](0n); //Dummy
		}

		gatherv_long(sendbuf, sendoff as Int, sendcnt as Int, 
					recvbuf, recvoff as Int, intRecvcnts, displs, 
					root as Int);

	}

	/**
	 * Scatter double-precision data array from here to all places
	 */
	@Native("c++", "mpi_scatterv_double((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw,#5,#6)")
		static native def scatterv_double(
			sendbuf:Rail[Double], sendcnts:Rail[Int], displs:Rail[Int],
			recvbuf:Rail[Double], recvcnt:Int,
			root:Int):void;

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
			sendbuf:Rail[Double], sendcnts:Rail[Long],
			recvbuf:Rail[Double], recvcnt:Long,
			root:Long):void {
		//Compute displs. The recv data is adjacent to each other.
		//displs(0) = 0;
		//Debug.assure(pidmap(0) == 0, "Inconsistance found in pid map");
        var intSendcnts:Rail[Int];
		var displs:Rail[Int];
		if (root == here.id()) {
            intSendcnts = new Rail[Int](Place.numPlaces());
			displs = new Rail[Int](Place.numPlaces());
            intSendcnts(0) = sendcnts(0) as Int;
			for (var i:Long=1; i<displs.size; i++) {
				//Debug.assure(pidmap(i) == i, "Inconsistance found in pid map");
                intSendcnts(i) = sendcnts(i) as Int;
				displs(i) = displs(i-1) + intSendcnts(i-1);
			}
		} else {
			displs = new Rail[Int](0);
            intSendcnts = new Rail[Int](0);
		}

		scatterv_double(sendbuf, intSendcnts, displs,
						recvbuf, recvcnt as Int, 
						root as Int);
	}

	/**
	 * Scattering integer data arrays
	 */
	@Native("c++", "mpi_scatterv_long((#1)->raw,(#2)->raw,(#3)->raw,(#4)->raw,#5,#6)")
		static native def scatterv_long(
			sendbuf:Rail[Long], sendcnts:Rail[Int], displs:Rail[Int],
			recvbuf:Rail[Long], recvcnt:Int,
			root:Int):void;

	public def scatterv(
			sendbuf:Rail[Long], sendcnts:Rail[Long],
			recvbuf:Rail[Long], recvcnt:Long,
			root:Long):void {
		//Compute displs, since the recv data is adjacent to each other.
		//displs(0) = 0;
		//Debug.assure(pidmap(0) == 0, "Inconsistance found in pid map");
		var intSendcnts:Rail[Int];
		var displs:Rail[Int];
		if (root == here.id()) {
            intSendcnts = new Rail[Int](Place.numPlaces());
			displs = new Rail[Int](Place.numPlaces());
            intSendcnts(0) = sendcnts(0) as Int;
			for (var i:Long=1; i<displs.size; i++) {
				//Debug.assure(pidmap(i) == i, "Inconsistance found in pid map");
                intSendcnts(i) = sendcnts(i) as Int;
				displs(i) = displs(i-1) + intSendcnts(i-1);
			}
		} else {
			displs = new Rail[Int](0);
            intSendcnts = new Rail[Int](0);
		}

		scatterv_long(sendbuf, intSendcnts, displs,
					 recvbuf, recvcnt as Int,
					 root as Int);
	}
	
	/**
	 * All gather, same as Gather+bcast.
	 */
	@Native("c++", "mpi_allgatherv_double((#1)->raw,#2,#3,(#4)->raw,#5,(#6)->raw,(#7)->raw)")
		static native def all_gatherv(
			sendbuf:Rail[Double], sendoff:Int, sendcnt:Int,
			recvbuf:Rail[Double], recvoff:Int, recvcnts:Rail[Int], recvdispls:Rail[Int]):void;

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
			sendbuf:Rail[Double], sendoff:Long, sendcnt:Long,
			recvbuf:Rail[Double], recvoff:Long, recvcnts:Rail[Long]):void {

		//Compute displs, since the recv data is adjacent to each other.
		//Debug.assure(pidmap(0) == 0, "Inconsistance found in pid map");
		val intRecvcnts = new Rail[Int](Place.numPlaces());
		val displs = new Rail[Int](Place.numPlaces());
		
        intRecvcnts(0) = recvcnts(0) as Int;
		for (var i:Long=1; i<Place.numPlaces(); i++) {
			//Debug.assure(pidmap(i) == i, "Inconsistance found in pid map");
			displs(i) = displs(i-1) + intRecvcnts(i-1);
		}
		all_gatherv(sendbuf, sendoff as Int, sendcnt as Int, 
					recvbuf, recvoff as Int, intRecvcnts, displs);
	}

	/**
	 * Reduce all values from all places to a single values by adding them all.
	 */
	@Native("c++", "mpi_reduce_sum_double((#1)->raw,#2,(#3)->raw,#4,#5,#6)")
		static native def reduce_sum(
			sendbuf:Rail[Double], sendoff:Int,
			recvbuf:Rail[Double], recvoff:Int, cnt:Int,
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
			sendbuf:Rail[Double], sendoff:Long, 
			recvbuf:Rail[Double], recvoff:Long,
			cnt:Long, root:Long):void {
		reduce_sum(sendbuf, sendoff as Int, recvbuf, recvoff as Int, cnt as Int, root as Int);
	}

	/**
	 * Reduce all values from all places to one by adding them up.
	 */
	public def reduceSum(sendbuf:Rail[Double], recvbuf:Rail[Double],
			cnt:Long, root:Long):void {
		reduce_sum(sendbuf, 0n, recvbuf, 0n, cnt as Int, root as Int);
	}

	/**
	 * Reduce-sum and broadcast result to all places
	 */
	@Native("c++", "mpi_allreduce_sum_double((#1)->raw,#2,(#3)->raw,#4,#5)")
		public static native def allReduceSum(
			sendbuf:Rail[Double], sendoff:Int,
			recvbuf:Rail[Double], recvoff:Int,
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
			sendbuf:Rail[Double], 
			recvbuf:Rail[Double],	
			cnt:Long): void {
		allReduceSum(sendbuf, 0n, recvbuf, 0n, cnt as Int);
	}

	// Non-blocking int P2P

    @Native("c++","mpi_Isend_long((#1)->raw,#2,#3,#4,#5,(#6)->raw)")
		static native def Isend_long(
			buf:Rail[Long], off:Int, cnt:Int, 
			dst:Int, tag:Int, rh:Rail[Int]):void;

	public def immSend(
			buf:Rail[Long], cnt:Long, dst:Long, 
			tag:Long):RequestHandleMPI = 
		immSend(buf, 0, cnt, dst, tag);

	public def immSend(
			buf:Rail[Long], off:Long, cnt:Long, 
			dst:Long, tag:Long):RequestHandleMPI {
		val req = new RequestHandleMPI();
		//val p_dst = pidmap(dst);
		Isend_long(buf, off as Int, cnt as Int, dst as Int, tag as Int, req.handle);
		return req;
	}

    @Native("c++","mpi_Irecv_long((#1)->raw,#2,#3,#4,#5,(#6)->raw)")
		static native def Irecv_long(
			buf:Rail[Long], off:Int, cnt:Int, 
			rsc:Int, tag:Int, rh:Rail[Int]):void;

	public def immRecv(	buf:Rail[Long], cnt:Long, src:Long, tag:Long):RequestHandleMPI = 
		immRecv(buf, 0, cnt as Int, src, tag);

	public def immRecv(
			buf:Rail[Long], off:Long, cnt:Long, 
			src:Long, tag:Long):RequestHandleMPI {
		val req = new RequestHandleMPI();
		//val p_src = pidmap(src);
		Isend_long(buf, off as Int, cnt as Int, src as Int, tag as Int, req.handle);
		return req;
	}

	// Request waiting
	public static def mywait(rh:RequestHandleMPI) { rh.mywait();}

	public static def test(rh:RequestHandleMPI) = rh.test(); 
}
