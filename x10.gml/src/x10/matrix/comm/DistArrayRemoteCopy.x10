/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.matrix.comm;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.comm.mpi.WrapMPI;
import x10.matrix.sparse.CompressArray;

/**
 * This class supports inter-place communication for data arrays which are defined
 * in DistArray at all places. 
 * 
 * <p>To enable MPI communication, add "-define MPI_COMMU -cxx-prearg -DMPI_COMMU"
 * in x10c++ build command, when you include commu package in your application source
 * code, or link to the proper GML library (native_mpi version).
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class DistArrayRemoteCopy {
	private static val baseTagCopyTo:Int=100000n;
	private static val baseTagCopyFrom:Int=200000n;
	private static val baseTagCopyIdxTo:Int=300000n;
	private static val baseTagCopyIdxFrom:Int=400000n;
	private static val baseTagCopyValTo:Int=500000n;
	private static val baseTagCopyValFrom:Int=600000n;
	private static val baseTagCopyToPLH:Int=700000n;
	private static val baseTagCopyFromPLH:Int=800000n;
	
	/**
	 * Copy multiple columns from source dense matrix at here to the specified remote 
	 * place via DistArray of dense matrix.
	 * 
	 * @param src      -- the source array
	 * @param srcOff   -- starting column in in source matrix
	 * @param distdst  -- target in dist array 
	 * @param dstpid   -- the place id of target in DistArray.
	 * @param dstOff   -- starting column offset in target matrix at destination place
	 * @param dataCnt  -- count of columns to be copied from source dense matrix
	 */
	public static def copy(src:Rail[Double], srcOff:Long, dstlist:DistDataArray, 
			dstpid:Long, dstOff:Long, dataCnt:Long): void {
		
		if (here.id() == dstpid) {
			Rail.copy(src, srcOff, dstlist(dstpid), dstOff, dataCnt);
			return;
		}
		
        assert (srcOff+dataCnt <= src.size) :
            "At source place, illegal data offset:"+srcOff+
            " or data count:"+dataCnt;
		
		@Ifdef("MPI_COMMU") {
			{
				mpiCopy(src, srcOff, dstlist, dstpid, dstOff, dataCnt);
			}
		}
		@Ifndef("MPI_COMMU") {
			{
				x10Copy(src, srcOff, dstlist, dstpid, dstOff, dataCnt);
			}
		}
	}
	
	/*
	 * Copy vector from here to remote dense matrix
	 */
	protected static def mpiCopy(
			src:Rail[Double], srcOff:Int, 
			dstlist:DistDataArray, dstpid:Int,
			dstOff:Int,			 	dataCnt:Long) :void  {
		
	@Ifdef("MPI_COMMU") {
		val srcpid = here.id();         //Implicitly carried to dst place
		
		finish {
			async {
				// At the source place, sending out the data
				val tag = srcpid * baseTagCopyTo + dstpid;
				WrapMPI.world.send(src, srcOff, dataCnt, dstpid, tag);
			}
			// At the destination place, receiving the data 
			at(dstlist.dist(dstpid)) async {
				val dst = dstlist(here.id());
				//Need: dmlist, srcpid, dstColOff, datasz
				assert (dstOff+dataCnt <= dst.size) :
                    "Receiving side data overflow";
				val tag    = srcpid * baseTagCopyTo + here.id();
				WrapMPI.world.recv(dst, dstOff, dataCnt, srcpid, tag);
			}
		}
	}
	}
	/**
	 * Copy vector from here to remote dense matrix
	 */
	protected static def x10Copy(
			src:Rail[Double], srcOff:Long,
			dstlist:DistDataArray, dstpid:Long,
			dstOff:Long, dataCnt:Long): void {
		
		val buf = src as Rail[Double]{self!=null};
		val srcbuf = new GlobalRail[Double](buf);

		at(dstlist.dist(dstpid)) {
			//Implicit copy: dst, srcbuf, srcOff, dataCnt
			val dst = dstlist(here.id());
			assert dstOff+dataCnt <= dst.size;
			finish Rail.asyncCopy[Double](srcbuf, srcOff, dst, dstOff, dataCnt);		
		}
	}
	

	// Remote array copy From 

	/**
	 * Copy multiple columns of the dense matrix in the specified place to
	 * here
	 * 
	 * @param srclist   -- source matrix in the dist array
	 * @param srcpid    -- source array's place id.
	 * @param srcOff    -- starting offset in source matrix
	 * @param dst       -- destination vector array
	 * @param dstOff    -- starting offset in receiving array at here
	 * @param dataCnt   -- count of data to copy
	 */
	public static def copy(
			srclist:DistDataArray, srcpid:Long, srcOff:Long, 
			dst:Rail[Double], dstOff:Long, dataCnt:Long): void {

		if (here.id() == srcpid) {
			val src = srclist(srcpid);
			Rail.copy(src, srcOff, dst, dstOff, dataCnt);
			return;
		}
		assert (dstOff+dataCnt <= dst.size) :
            "Receiving array overflow";
		
		@Ifdef("MPI_COMMU") {
			{
				mpiCopy(srclist, srcpid, srcOff, dst, dstOff, dataCnt);
			}
		}

		@Ifndef("MPI_COMMU") {
			{
				x10Copy(srclist, srcpid, srcOff, dst, dstOff, dataCnt);
			}
		}
	}
	/**
	 * Copy data from remote matrix to here in a vector
	 */
	protected static def mpiCopy(
			srclist:DistDataArray, srcpid:Int, srcOff:Int,
			dst:Rail[Double], dstOff:Int, dataCnt:Long): void {
		
	@Ifdef("MPI_COMMU") {
		val dstpid = here.id();
		finish {
			at(srclist.dist(srcpid)) async {
				//Need: dstlist, dstpid, srcOff, dataCnt, 
				val src = srclist(here.id());
				val tag = here.id() * baseTagCopyFrom + dstpid;
				assert (srcOff + dataCnt <= src.size) :
                    "Sending array overflow";
				
				WrapMPI.world.send(src, srcOff, dataCnt, dstpid, tag);
			}
			async {
				val tag    = srcpid * baseTagCopyFrom + dstpid;
				WrapMPI.world.recv(dst, dstOff, dataCnt, srcpid, tag);
			}
		}
	}
	}	
	/**
	 * Copy data from remote dense matrix to here in a vector
	 */
	protected static def x10Copy(
			srclist:DistDataArray, srcpid:Long, srcOff:Long,
			dst:Rail[Double], dstOff:Long, dataCnt:Long): void {

		val rmt:GlobalRail[Double]  = at(srclist.dist(srcpid)) { 
			//Need: dstlist, srcOff, dataCnt
			val src = srclist(here.id());
			assert (srcOff + dataCnt <= src.size) :
                "Sending array overflow";
			new GlobalRail[Double](src as Rail[Double]{self!=null})
		};
		finish Rail.asyncCopy[Double](rmt, srcOff, dst, dstOff, dataCnt);
	}
	

	// Remote compress array Copy To



	// Sparse block copy To

	/**
	 * Copy compress array data from here to the specified remote 
	 * place in DistArray.
	 * 
	 * @param src    -- source of compress array at here
	 * @param srcOff -- offset in source 
	 * @param dstlist -- target compress array in DistArray
	 * @param dstpid -- place id of target in DistArray.
	 * @param dstOff -- column offset in target 
	 * @param dataCnt -- count of data to be copied from source 
	 */
	public static def copy(
			src:CompressArray, srcOff:Long,
			dstlist:DistCompArray, dstpid:Long, dstOff:Long, 
			dataCnt:Long): void {

		if (here.id() == dstpid) {
			val dst = dstlist(here.id());
			CompressArray.copy(src, srcOff, dst, dstOff, dataCnt);
			return;
		}

		assert (srcOff+dataCnt <= src.storageSize()) :
            "Sending side overflow";
		
		@Ifdef("MPI_COMMU") {
			{
				mpiCopy(src, srcOff, dstlist, dstpid, dstOff, dataCnt);
			}
		}
		@Ifndef("MPI_COMMU") {
			{
				x10Copy(src, srcOff, dstlist, dstpid, dstOff, dataCnt);
			}
		}
	}


	/**
	 * Based on mpi send/recv, copy data from here to remote place
	 */
	protected static def mpiCopy(
			src:CompressArray, srcOff:Int,
			dstlist:DistCompArray, dstpid:Int, dstOff:Int, 
			dataCnt:Long): void {
		
	@Ifdef("MPI_COMMU") {

		val srcpid = here.id();        
		finish {
			async {
				val tag    = srcpid * baseTagCopyIdxTo + dstpid;
				WrapMPI.world.send(src.index, srcOff, dataCnt, dstpid, tag);
				WrapMPI.world.send(src.value, srcOff, dataCnt, dstpid, tag+100001);
			}

			at(dstlist.dist(dstpid)) async {
				// Need: dstlist, srcpid, dstOff, dataCnt;
				val dst = dstlist(here.id());
				val tag = srcpid * baseTagCopyIdxTo + here.id();
				assert (dstOff+dataCnt <= dst.storageSize()) :
                    "Receiving side arrays overflow";
				WrapMPI.world.recv(dst.index, dstOff, dataCnt, srcpid, tag);
				WrapMPI.world.recv(dst.value, dstOff, dataCnt, srcpid, tag+100001);
			}
		}
	}
	}


	//Sparse matrix remote copy To
	protected static def x10Copy(
			src:CompressArray, srcOff:Long,
			dstlist:DistCompArray, dstpid:Long, dstOff:Long, 
			dataCnt:Long): void {

		val idxbuf = src.index as Rail[Long]{self!=null};
		val valbuf = src.value as Rail[Double]{self!=null};
		val rmtidx = new GlobalRail[Long](idxbuf);
		val rmtval = new GlobalRail[Double](valbuf);

		at(dstlist.dist(dstpid)) {
			//Implicit copy:dstlist, dataCnt, rmtidx, rmtval, srcOff dstOff
			val dst = dstlist(here.id());
			assert (dstOff+dataCnt <= dst.storageSize()) :
                "Receiving side arrays overflow";
			finish Rail.asyncCopy[Long](rmtidx, srcOff, dst.index, dstOff, dataCnt);
			finish Rail.asyncCopy[Double](rmtval, srcOff, dst.value, dstOff, dataCnt);
		}
	}


	// Remote compress array copy From

	/**
	 * Copy data of compress array from remote place to here
	 * 
	 * @param dstlist  -- source compress array in DistArray
	 * @param srcpid  -- source place id.
	 * @param srcOff  -- offset in source
	 * @param dstspa  -- destination place id
	 * @param dstOff  -- offset in target
	 * @param dataCnt  -- data count to be copied in source matrix
	 */
	public static def copy(
			srclist:DistCompArray, srcpid:Long, srcOff:Long,
			dst:CompressArray, dstOff:Long, 
			dataCnt:Long): void {
		
		if (here.id() == srcpid) {
			CompressArray.copy(srclist(srcpid), srcOff, dst, dstOff, dataCnt);
			return;
		}

		@Ifdef("MPI_COMMU") {
			{
				mpiCopy(srclist, srcpid, srcOff, dst, dstOff, dataCnt);
			}
		}

		@Ifndef("MPI_COMMU") {
			{
				x10Copy(srclist, srcpid, srcOff, dst, dstOff, dataCnt);
			}
		}
	}

	//Remote sparse matrix copy from
	protected static def mpiCopy(
			srclist:DistCompArray, srcpid:Int, srcOff:Int,
			dst:CompressArray, dstOff:Int, 
			dataCnt:Long): void {
		
	@Ifdef("MPI_COMMU") {
		val dstbid = here.id();
		finish {
			at(srclist.dist(srcpid)) async {
				//Need: dstpid, srclist, srcOff, dataCnt
				val src = srclist(here.id());
				val tag    = here.id() * baseTagCopyIdxFrom + dstbid;
				WrapMPI.world.send(src.index, srcOff, dataCnt, dstbid, tag);
				WrapMPI.world.send(src.value, srcOff, dataCnt, dstbid, tag+1000);
			}
			
			async {
				val tag = srcpid * baseTagCopyIdxFrom + dstbid;

				WrapMPI.world.recv(dst.index, dstOff, dataCnt, srcpid, tag);
				WrapMPI.world.recv(dst.value, dstOff, dataCnt, srcpid, tag+1000);
			}
		}
	}
	}

	//Sparse matrix remote copyt from
	protected static def x10Copy(
			srclist:DistCompArray, srcpid:Long, srcOff:Long,
			dst:CompressArray, dstOff:Long, 
			dataCnt:Long): void {

		val rmt:RemotePair = at(srclist.dist(srcpid)) { 
			//Need: srclist
			val src = srclist(here.id());
			val idxbuf = src.index as Rail[Long]{self!=null};
			val valbuf = src.value as Rail[Double]{self!=null};
			val rmtidx = new GlobalRail[Long](idxbuf);
			val rmtval = new GlobalRail[Double](valbuf);
			RemotePair(rmtidx, rmtval)
		};

		finish Rail.asyncCopy[Long](rmt.first,  srcOff, dst.index, dstOff, dataCnt);
		finish Rail.asyncCopy[Double](rmt.second, dstOff, dst.value, dstOff, dataCnt);
	}
}
