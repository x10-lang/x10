package x10.matrix.comm;

import x10.io.Console;
import x10.util.Timer;
import x10.util.Pair;

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.SparseCSC;

import x10.matrix.block.MatrixBlock;
import x10.matrix.distblock.BlockSet;
//import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
//import x10.matrix.distblock.DistBlockMatrix;


public type BlocksPLH  =PlaceLocalHandle[BlockSet];


/**
 */
public class BlockRemoteCopy {
	private static val baseTagCopyTo:Int=100000;
	private static val baseTagCopyFrom:Int=200000;
	private static val baseTagCopyIdxTo:Int=300000;
	private static val baseTagCopyIdxFrom:Int=400000;
	private static val baseTagCopyValTo:Int=500000;
	private static val baseTagCopyValFrom:Int=600000;

	//-----------------------------------------
	/**
	 * Copy source block to target block in distributed block matrix structure.
	 */
	public static def copy(distBS:BlocksPLH, 
			srcbid:Int, srcColOff:Int, 
			dstbid:Int, dstColOff:Int, 
			colCnt:Int):Int {
		//Favor of current place is source place, and using copyto
		if (srcbid ==dstbid) return 0;
		
		val srcpid = distBS().findPlace(srcbid);
		val dsz = at (Dist.makeUnique()(srcpid)) {
			val blk = distBS().find(srcbid);
			val srcmat = blk.getMatrix();
			copy(srcmat, srcColOff, distBS, dstbid, dstColOff, colCnt)  
		};
		return dsz;
	}
	
	
	//------------------------------------------
	// Remote array copy matrix to/from 
	//------------------------------------------
	public static def copy(src:MatrixBlock, dstBS:BlocksPLH, dstbid:Int):Int =
		copy(src.getMatrix(), dstBS, dstbid);

	public static def copy(srcBS:BlocksPLH, srcbid:Int, dst:MatrixBlock):Int =
		copy(srcBS, srcbid, dst.getMatrix());	
	
	//----------------------------------------------
	public static def copy(src:Matrix, dstBS:BlocksPLH, dstbid:Int):Int =
		copy(src, 0, dstBS, dstbid, 0, src.N);

	public static def copy(srcBS:BlocksPLH, srcbid:Int, dst:Matrix):Int =
		copy(srcBS, srcbid, 0, dst, 0, dst.N);
	
	//----------------------------------------------
	public static def copy(src:Matrix, srcColOff:Int, dstBS:BlocksPLH, dstbid:Int, dstColOff:Int, colCnt:Int) : Int {
		if (src instanceof DenseMatrix)
			return copy(src as DenseMatrix, srcColOff, dstBS, dstbid, dstColOff, colCnt);
		else if (src instanceof SparseCSC)
			return copy(src as SparseCSC, srcColOff, dstBS, dstbid, dstColOff, colCnt);
		else
			Debug.exit("Matrix type is not supported in remote block copy");
		return 0;
	}
	
	public static def copy(srcBS:BlocksPLH, srcbid:Int, srcColOff:Int, dst:Matrix, dstColOff:Int, colCnt:Int) : Int {
		if (dst instanceof DenseMatrix)
			return copy(srcBS, srcbid, srcColOff, dst as DenseMatrix, dstColOff, colCnt);
		else if (dst instanceof SparseCSC)
			return copy(srcBS, srcbid, srcColOff, dst as SparseCSC, dstColOff, colCnt);
		else
			Debug.exit("Matrix type is not supported in remote block copy");
		return 0;
	}

	//=====================================================================
	//public static def copy(src:DenseMatrix, dst:DistBlockMatrix, bid:Int)= 
	//	copy(src, 0, dst.handleBS, bid, 0, src.N);
	
	public static def copy(src:DenseMatrix, srcColOff:Int, 
						   dst:BlocksPLH, bid:Int, dstColOff:Int, colCnt:Int): Int {
		
		val dstpid = dst().findPlace(bid);
		var dsz:Int=0;

		Debug.assure(srcColOff+colCnt<=src.N, "Number of columns copied exceeds source matrix dimension");
		
		if (here.id() == dstpid) {
			val blk = dst().find(bid);
			Debug.assure(blk!=null, "Cannot find block in block set");
			val dstden = blk.getMatrix() as DenseMatrix;
			DenseMatrix.copyCols(src, srcColOff, dstden, dstColOff, colCnt);
			return src.M*colCnt;
		}
		
		@Ifdef("MPI_COMMU") {
			dsz =mpiCopy(src, srcColOff, dst, dstpid, bid, dstColOff, colCnt);
		}
		
		@Ifndef("MPI_COMMU") {
			dsz = x10Copy(src, srcColOff, dst, dstpid, bid, dstColOff, colCnt);
		}
		return dsz;
	}
	
	/**
	 *
	 */
	public static def mpiCopy(src:DenseMatrix, srcColOff:Int, 
			dst:BlocksPLH, dstpid:Int, bid:Int, dstColOff:Int, colCnt:Int): Int {

		val datCnt:int = src.M*colCnt;
	
		@Ifdef("MPI_COMMU") {
			val srcpid = here.id();         //Implicitly carried to dst place				
			/*
			 * How to differ messages to different blocks at same place,
			 * Only one process is allowed to execute the communication routine
			 * at one place. The message tag does not carry which block the message
			 * is intended to get to. Therefore, receiver part must be paired with
			 * its sender party.
			 */
			finish {
				// At the source place, sending out the data
				val srcOff = src.M*srcColOff;
				async {		
					val tag = srcpid * baseTagCopyTo + dstpid + bid;
					WrapMPI.world.send(src.d, srcOff, datCnt, dstpid, tag);
				}
				// At the destination place, receiving the data 
				at (Dist.makeUnique()(dstpid)) async {
					//Remote capture: bid, dstColOff, datCnt, 
					val blk = dst().find(bid);
					val dstden = blk.getMatrix() as DenseMatrix;
					val dstOff = dstden.M * dstColOff;
					
					Debug.assure(dstOff+datCnt<=dstden.d.size, 
								 "Copy receiving side data overflow");
					val tag    = srcpid * baseTagCopyTo + here.id()+bid;
					WrapMPI.world.recv(dstden.d, dstOff, datCnt, srcpid, tag);
				}
			}
		}
		return datCnt;
	}
	

	public static def x10Copy(src:DenseMatrix, srcColOff:Int, 
		dst:BlocksPLH, dstpid:Int, bid:Int, dstColOff:Int, colCnt:Int): Int {
		
		val datCnt:Int = src.M*colCnt;
		val buf = src.d as Array[Double]{self!=null};
		val srcbuf = new RemoteArray[Double](buf);
		val srcOff = src.M * srcColOff;

		at (Dist.makeUnique()(dstpid)) {
			//Remote copy: dst, srcbuf, srcOff, dstColOff, datCnt,
			val blk = dst().find(bid);
			val dstden = blk.getMatrix() as DenseMatrix;
			val dstOff = dstden.M*dstColOff; 
			Debug.assure(dstOff+datCnt <= dstden.d.size, "Copy receiving side data overflow");
			finish Array.asyncCopy[Double](srcbuf, srcOff, dstden.d, dstOff, datCnt);
		}
		return datCnt;
	}

	//------------------------------------------
	// Remote array copy From 
	//------------------------------------------
	
	//public static def copy(src:DistBlockMatrix, srcbid:Int, dst:DenseMatrix) =
	//	copy(src.handleBS, srcbid, 0, dst, 0, dst.N);
	
	/**
	 * Copy multiple columns of the dense matrix in the specified place to
	 * here
	 * 
	 * @param src       -- source matrix in the dist array
	 * @param srcpid    -- source array's place id.
	 * @param srcColOff -- starting column in source matrix
	 * @param dst       -- destination vector array
	 * @param dstColOff -- starting column in receiving array at here
	 * @param colCnt    -- count of column to copy
	 * @return number of data copied
	 */
	public static def copy(src:BlocksPLH, srcbid:Int, srcColOff:Int, 
						   dst:DenseMatrix, dstColOff:Int, colCnt:Int): Int {

		val srcpid = src().findPlace(srcbid);
		var dsz:Int =0;
		
		Debug.assure(dstColOff+colCnt<=dst.N, "Number of column for copying at receiving side exceeds matrix dimension");
		
		if (here.id() == srcpid) {
			val blk = src().find(srcbid);
			val srcden = blk.getMatrix() as DenseMatrix;
			DenseMatrix.copyCols(srcden, srcColOff, dst, dstColOff, colCnt);
			return dst.M*colCnt;
		}
		
		@Ifdef("MPI_COMMU") {
			dsz = mpiCopy(src, srcpid, srcbid, srcColOff, dst, dstColOff, colCnt);
		}

		@Ifndef("MPI_COMMU") {
			dsz = x10Copy(src, srcpid, srcbid, srcColOff, dst, dstColOff, colCnt);
		}
		return dsz;
	}

	/**
	 * Copy data from remote matrix to here in a vector
	 */
	protected static def mpiCopy(src:BlocksPLH, srcpid:Int, bid:Int, srcColOff:Int,
								 dst:DenseMatrix, dstColOff:Int, colCnt:Int): Int {
		val datCnt:Int = dst.M*colCnt; //assuming dst has same leading dimension as src
		@Ifdef("MPI_COMMU") {
			val dstpid = here.id();
			finish {
				at (Dist.makeUnique()(srcpid)) async {
					//Need: src, bid, srcOff, datCnt,
					val blk = src().find(bid);
					val srcden = blk.getMatrix() as DenseMatrix;				
					val tag = here.id() * baseTagCopyFrom + dstpid + bid;
					val srcOff = srcColOff * srcden.M;
					
					Debug.assure(srcOff + datCnt <= srcden.d.size, "Sending side data referencing overflow");
					WrapMPI.world.send(srcden.d, srcOff, datCnt, dstpid, tag);
				}
				async {
					val tag    = srcpid * baseTagCopyFrom + dstpid + bid;
					val dstOff = dst.M* dstColOff;
					WrapMPI.world.recv(dst.d, dstOff, datCnt, srcpid, tag);
				}
			}
		}
		return datCnt;
	}

	/**
	 * Copy data from remote dense matrix to here in a vector
	 */
	protected static def x10Copy(src:BlocksPLH, srcpid:Int, bid:Int, srcColOff:Int,
								 dst:DenseMatrix, dstColOff:Int, colCnt:Int): Int {
		
	
		val rmt:DenseRemoteSourceInfo  = at (Dist.makeUnique()(srcpid)) { 
			//Need: src, bid, srcColOff, datCnt
			val blk = src().find(bid);
			val srcden = blk.getMatrix() as DenseMatrix;
			val srcOff = srcColOff * srcden.M;
			val datCnt = colCnt * srcden.M;
			
			Debug.assure(srcOff + datCnt <= srcden.d.size, "Sending side matrix data referencing overflow");
			new DenseRemoteSourceInfo(srcden.d, srcOff, datCnt)

		};
		val dstOff = dst.M * dstColOff;
		finish Array.asyncCopy[Double](rmt.valbuf, rmt.offset, dst.d, dstOff, rmt.length);
		return rmt.length;
	}

	//========================================================================================
	// Sparse matrix copyTo/From
	//========================================================================================

	//------------------------
	// Copy sparse from here to remote place
	//------------------------
	
	//public static def copy(src:SparseCSC, dst:DistBlockMatrix, bid:Int) =
	//	copy(src, 0, dst.handleBS, bid, 0, src.N);
	
	public static def copy(src:SparseCSC, srcColOff:Int, 
			dst:BlocksPLH, bid:Int, dstColOff:Int, colCnt:Int): Int {
		
		val dstpid = dst().findPlace(bid);

		Debug.assure(srcColOff+colCnt <= src.N, "At source, number of columns exceeds matrix dimension ");
		
		if (here.id() == dstpid) {
			val blk = dst().find(bid);
			val dstspa = blk.getMatrix() as SparseCSC;
			val dz = SparseCSC.copyCols(src, srcColOff, dstspa, dstColOff, colCnt);
			return dz;
		}
		
		
		@Ifdef("MPI_COMMU") {
			return mpiCopy(src, srcColOff, dst, dstpid, bid, dstColOff, colCnt);
		}
		
		@Ifndef("MPI_COMMU") {
			return x10Copy(src, srcColOff, dst, dstpid, bid, dstColOff, colCnt);
		}
	}
	
	
	protected static def mpiCopy(
			src:SparseCSC, srcColOff:Int,
			dst:BlocksPLH, dstpid:Int, bid:Int, dstColOff:Int, 
			colCnt:Int): Int {

		val srcpid = here.id();        
		val datasz = src.countNonZero(srcColOff, colCnt); //Implicitly carried to dst place
		@Ifdef("MPI_COMMU") {
			finish {
				at (Dist.makeUnique()(dstpid)) async {
					// Need: srcpid, dstColOff, datasz;
					val blk = dst().find(bid);
					val dstspa = blk.getMatrix() as SparseCSC;
					Debug.assure(dstColOff+colCnt<=dstspa.N, "At destination, number of columns exceeds matrix dimension");
					
					val dstoff = dstspa.getNonZeroOffset(dstColOff);
					val tag    = srcpid * 10000 + here.id();
					
					//++++++++++++++++++++++++++++++++++++++++++++
					//Do NOT call getIndex()/getValue() before init at destination place
					//+++++++++++++++++++++++++++++++++++++++++++++
					dstspa.initRemoteCopyAtDest(dstColOff, colCnt, datasz);
					WrapMPI.world.recv(dstspa.getIndex(), dstoff, datasz, srcpid, tag);
					WrapMPI.world.recv(dstspa.getValue(), dstoff, datasz, srcpid, tag+100001);
					dstspa.finalizeRemoteCopyAtDest();
				}
				async {
					val tag    = srcpid * 10000 + dstpid;
					val srcoff = src.getNonZeroOffset(srcColOff);
					
					src.initRemoteCopyAtSource(srcColOff, colCnt);
					WrapMPI.world.send(src.getIndex(), srcoff, datasz, dstpid, tag);
					WrapMPI.world.send(src.getValue(), srcoff, datasz, dstpid, tag+100001);
					src.finalizeRemoteCopyAtSource();
				}			
			}
		}
		return datasz;
	}

	
	//Sparse matrix remote copy To
	protected static def x10Copy(
			src:SparseCSC, srcColOff:Int,
			dst:BlocksPLH, dstpid:Int, bid:Int,  dstColOff:Int, 
			colCnt:Int) : Int {


		val idxbuf = src.getIndex() as Array[Int]{self!=null};
		val valbuf = src.getValue() as Array[Double]{self!=null};
		val datoff = src.getNonZeroOffset(srcColOff);
		val datcnt = src.initRemoteCopyAtSource(srcColOff, colCnt);
		val rmtidx = new RemoteArray[Int](idxbuf);
		val rmtval = new RemoteArray[Double](valbuf);

		
		at (Dist.makeUnique()(dstpid)) {
			//Remote capture: datcnt, rmtidx, rmtval, datoff			
			val blk = dst().find(bid);
			val dstspa = blk.getMatrix() as SparseCSC;
			//++++++++++++++++++++++++++++++++++++++++++++
			//Do not call getIndex()/getValue() before init at destination place
			//+++++++++++++++++++++++++++++++++++++++++++++
			dstspa.initRemoteCopyAtDest(dstColOff, colCnt, datcnt);
			val dstoff = dstspa.getNonZeroOffset(dstColOff);
			finish Array.asyncCopy[Int   ](rmtidx, datoff, dstspa.getIndex(), dstoff, datcnt);
			finish Array.asyncCopy[Double](rmtval, datoff, dstspa.getValue(), dstoff, datcnt);
			dstspa.finalizeRemoteCopyAtDest();
		}

		src.finalizeRemoteCopyAtSource();
		return datcnt;
	}
	
	//---------------------------------
	// Copy sparse from remote to here
	//---------------------------------
	//public static def copy(src:DistBlockMatrix, bid:Int, dst:SparseCSC) =
	//	copy(src.handleBS, bid, 0, dst, 0, dst.N);
	
	/**
	 * Copy multiple columns of the sparseCSC matrix in the specified place to
	 * here
	 * 
	 * @param src   		source sparse matrix blocks in all places
	 * @param srcbid  		source block id.
	 * @param srcColOff  	column offset in source matrix
	 * @param dstspa  		destination sparse matrix of the copy
	 * @param dstColOff 	column offset in target matrix
	 * @param colCnt  		count of columns to be copied in source matrix
	 * @return -- Number of elements copied
	 */
	public static def copy(
			src:BlocksPLH, srcbid:Int, srcColOff:Int,
			dst:SparseCSC, dstColOff:Int, 
			colCnt:Int): Int {

		val srcpid = src().findPlace(srcbid);
		Debug.assure(dstColOff+colCnt<=dst.N, "The range of columns exceeds receiving side matrix dimension");
		
		if (here.id() == srcpid) {
			val blk = src().find(srcbid);
			val srcspa = blk.getMatrix() as SparseCSC;
			
			return SparseCSC.copyCols(srcspa, srcColOff, dst, dstColOff, colCnt);
		}

		@Ifdef("MPI_COMMU") {
			return mpiCopy(src, srcpid, srcbid, srcColOff, dst, dstColOff, colCnt);
		}

		@Ifndef("MPI_COMMU") {
			return x10Copy(src, srcpid, srcbid, srcColOff, dst, dstColOff, colCnt);
		}
	}

	/**
	 * Based on mpi send/recv, copy data from remote place to here. The data size is 
	 * transfered to here implicitly before matrix data copy.
	 */
	protected static def mpiCopy(
			src:BlocksPLH, srcpid:Int, bid:Int, srcColOff:Int,
			dst:SparseCSC, dstColOff:Int, 
			colCnt:Int): Int {

		val dstpid = here.id();//Implicitly carried to dst place
		
		// Get the data count first
		val dsz = compBlockDataSize(src, bid, srcColOff, colCnt);
		
		//Start paired send/recv
		@Ifdef("MPI_COMMU") {
			finish {
				at (Dist.makeUnique()(srcpid)) async {
					//Need: src, srcpid, srcColOff
					val srcspa = src().find(bid).getMatrix() as SparseCSC;
					val srcoff = srcspa.getNonZeroOffset(srcColOff);;
					val tag    = here.id() * 20000 + dstpid;
					val datasz = srcspa.countNonZero(dstColOff, colCnt);
					
					srcspa.initRemoteCopyAtSource(srcColOff, colCnt);
					WrapMPI.world.send(srcspa.getIndex(), srcoff, datasz, dstpid, tag);
					WrapMPI.world.send(srcspa.getValue(), srcoff, datasz, dstpid, tag+1000002);
					
					srcspa.finalizeRemoteCopyAtSource();
				}
				
				async {
					
					val tag    = srcpid * 20000 + dstpid;
					val dstoff = dst.getNonZeroOffset(dstColOff);
					
					//++++++++++++++++++++++++++++++++++++++++++++
					//Do NOT call getIndex()/getValue() before init at destination place
					//+++++++++++++++++++++++++++++++++++++++++++++
					dst.initRemoteCopyAtDest(dstColOff, colCnt, dsz);
					WrapMPI.world.recv(dst.getIndex(), dstoff, dsz, srcpid, tag);
					WrapMPI.world.recv(dst.getValue(), dstoff, dsz, srcpid, tag+1000002);
					dst.finalizeRemoteCopyAtDest();
				}
			}
		}
		
		return dsz;
	}

	//------------------------------------------

	/**
	 * Copy sparse matrix block to here via array remote copy.
	 * 
	 * @param src   		source sparseCSC matrix block in all places
	 * @param srcpid  		source matrix's place id.
	 * @param srcColOff  	column offset in source matrix
	 * @param dst   		destination sparse matrix at here
	 * @param dstColOff  	column offset in target matrix
	 * @param colCnt 		count of columns to be copied in source matrix
	 * @return 				number of elements copied
	 */
	public static def x10Copy(
			src:BlocksPLH, srcpid:Int, bid:Int, srcColOff:Int,
			dst:SparseCSC, dstColOff:Int, 
			colCnt:Int): Int {

		val rmt = at (Dist.makeUnique()(srcpid)) {
			val blk = src().find(bid);
			val srcspa = blk.getMatrix() as SparseCSC;
			val off = srcspa.getNonZeroOffset(srcColOff);;
			val cnt = srcspa.countNonZero(srcColOff, colCnt);
			
			srcspa.initRemoteCopyAtSource(srcColOff, colCnt);
			SparseRemoteSourceInfo(srcspa.getIndex(), srcspa.getValue(), off, cnt)
		};

		val dstoff = dst.getNonZeroOffset(dstColOff);
		//++++++++++++++++++++++++++++++++++++++++++++
		//Do NOT call getIndex()/getValue() before init at destination place
		//+++++++++++++++++++++++++++++++++++++++++++++
		dst.initRemoteCopyAtDest(dstColOff, colCnt, rmt.length);
		finish Array.asyncCopy[Int   ](rmt.idxbuf, rmt.offset, dst.getIndex(), dstoff, rmt.length);
		finish Array.asyncCopy[Double](rmt.valbuf, rmt.offset, dst.getValue(), dstoff, rmt.length);

		finish {
			at (Dist.makeUnique()(srcpid)) async {
				val srcspa = src().find(bid).getMatrix() as SparseCSC;
				srcspa.finalizeRemoteCopyAtSource();
			}
			dst.finalizeRemoteCopyAtDest();
		}
		
		return rmt.length;
	}
	
	//====================================================
	public static def compBlockDataSize(distBS:BlocksPLH, rootbid:Int, colOff:Int, colCnt:Int):Int {

		if (colCnt < 0) return 0;
		var dsz:Int = 0;
		val rootpid= distBS().findPlace(rootbid);

		if (here.id() == rootpid) {
			val blk = distBS().findBlock(rootbid);
			return blk.compColDataSize(colOff, colCnt);
			
		} else {
			dsz = at (Dist.makeUnique()(rootpid)) {
				val blk = distBS().findBlock(rootbid);
				blk.compColDataSize(colOff, colCnt)
			};
		}
		return dsz;
	}

	public static def compBlockDataSize(distBS:BlocksPLH, rootbid:Int):Int =
		compBlockDataSize(distBS, rootbid, 0, distBS().getGrid().getColSize(rootbid));

}