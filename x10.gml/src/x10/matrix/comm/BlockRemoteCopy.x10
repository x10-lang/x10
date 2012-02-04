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
	protected static val baseTagCopyTo:Int=100000;
	protected static val baseTagCopyFrom:Int=200000;
	protected static val baseTagCopyIdxTo:Int=300000;
	protected static val baseTagCopyIdxFrom:Int=400000;
	protected static val baseTagCopyValTo:Int=500000;
	protected static val baseTagCopyValFrom:Int=600000;

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
			val dstpid = distBS().findPlace(dstbid);
			copy(srcmat, srcColOff, distBS, dstpid, dstbid, dstColOff, colCnt)  
		};
		return dsz;
	}
	
	//------------------------------------------
	// Remote array copy matrix to/from 
	//------------------------------------------
		
	public static def copy(src:Matrix, dstBS:BlocksPLH, dstbid:Int):Int =
		copy(src, 0, dstBS, dstBS().findPlace(dstbid), dstbid, 0, src.N);

	public static def copy(srcBS:BlocksPLH, srcbid:Int, dst:Matrix):Int =
		copy(srcBS, srcBS().findPlace(srcbid), srcbid, 0, dst, 0, dst.N);
	//---------------------
	public static def copy(src:Matrix, dstBS:BlocksPLH, dstpid:Int, dstbid:Int):Int =
		copy(src, 0, dstBS, dstpid, dstbid, 0, src.N);

	public static def copy(srcBS:BlocksPLH, srcpid:Int, srcbid:Int, dst:Matrix):Int =
		copy(srcBS, srcpid, srcbid, 0, dst, 0, dst.N);
	
	//===================================================================
	//===================================================================
	public static def copy(src:Matrix, srcColOff:Int, 
			dstBS:BlocksPLH, dstpid:Int, dstbid:Int, dstColOff:Int, colCnt:Int) : Int {
		if (src instanceof DenseMatrix)
			return copy(src as DenseMatrix, srcColOff, dstBS, dstpid, dstbid, dstColOff, colCnt);
		else if (src instanceof SparseCSC)
			return copy(src as SparseCSC, srcColOff, dstBS, dstpid, dstbid, dstColOff, colCnt);
		else
			Debug.exit("Matrix type is not supported in remote block copy");
		return 0;
	}
	
	public static def copy(srcBS:BlocksPLH, srcpid:Int, srcbid:Int, srcColOff:Int, 
			dst:Matrix, dstColOff:Int, colCnt:Int) : Int {
		if (dst instanceof DenseMatrix)
			return copy(srcBS, srcpid, srcbid, srcColOff, dst as DenseMatrix, dstColOff, colCnt);
		else if (dst instanceof SparseCSC)
			return copy(srcBS, srcpid, srcbid, srcColOff, dst as SparseCSC, dstColOff, colCnt);
		else
			Debug.exit("Matrix type is not supported in remote block copy");
		return 0;
	}

	//=====================================================================
	protected static def copy(src:DenseMatrix, srcColOff:Int, 
				dstBS:BlocksPLH, dstpid:Int, bid:Int, dstColOff:Int, colCnt:Int): Int {		
		val srcIdxOff:Int = src.M * srcColOff;
		val datCnt:Int    = src.M * colCnt;
		val dstIdxOff:Int = dstBS().getGrid().getRowSize(bid) * dstColOff;
		
		copyOffset(src, srcIdxOff, dstBS, dstpid, bid, dstIdxOff, datCnt);
		return datCnt;
	}
	//-----------------------------------------------------------------

	protected static def copyOffset(src:DenseMatrix, srcIdxOff:Int, 
			dst:BlocksPLH, dstpid:Int, bid:Int, dstIdxOff:Int, datCnt:Int): void {
		
		Debug.assure(srcIdxOff+datCnt<=src.d.size, "Number of columns copied exceeds data buffer");
		
		if (here.id() == dstpid) {
			val blk = dst().find(bid);
			val dstden = blk.getMatrix() as DenseMatrix;
			Array.copy(src.d, srcIdxOff, dstden.d, dstIdxOff, datCnt);
		} else {
			
			@Ifdef("MPI_COMMU") {
				mpiCopyOffset(src, srcIdxOff, dst, dstpid, bid, dstIdxOff, datCnt);
			}
			
			@Ifndef("MPI_COMMU") {
				x10CopyOffset(src, srcIdxOff, dst, dstpid, bid, dstIdxOff, datCnt);
			}
		}		
	}
	/**
	 * 
	 */
	protected static def mpiCopyOffset(src:DenseMatrix, srcIdxOff:Int, 
			dstBS:BlocksPLH, dstpid:Int, bid:Int, dstIdxOff:Int, datCnt:Int): void {

		@Ifdef("MPI_COMMU") {
			val srcpid = here.id();         //Implicitly carried to dst place				
			finish {
				// At the source place, sending out the data
				async {		
					val tag = srcpid * baseTagCopyTo + dstpid + bid;
					WrapMPI.world.send(src.d, srcIdxOff, datCnt, dstpid, tag);
				}
				// At the destination place, receiving the data 
				at (Dist.makeUnique()(dstpid)) async {
					//Remote capture: bid, dstColOff, datCnt, 
					val blk = dstBS().find(bid);
					val dstden = blk.getMatrix() as DenseMatrix;
			
					Debug.assure(dstIdxOff+datCnt<=dstden.d.size, 
					"Copy receiving side data overflow");
					val tag    = srcpid * baseTagCopyTo + here.id()+bid;
					WrapMPI.world.recv(dstden.d, dstIdxOff, datCnt, srcpid, tag);
				}
			}
		}
	}

	protected static def x10CopyOffset(src:DenseMatrix, srcIdxOff:Int, 
			dstBS:BlocksPLH, dstpid:Int, bid:Int, dstIdxOff:Int, datCnt:Int): void {
		
		val buf = src.d as Array[Double]{self!=null};
		val srcbuf = new RemoteArray[Double](buf);

		at (Dist.makeUnique()(dstpid)) {
			//Remote copy: dst, srcbuf, srcIdxOff, dstIdxOff, datCnt,
			val blk = dstBS().find(bid);
			val dstden = blk.getMatrix() as DenseMatrix;
			Debug.assure(dstIdxOff+datCnt <= dstden.d.size, "Copy receiving side data overflow");
			finish Array.asyncCopy[Double](srcbuf, srcIdxOff, dstden.d, dstIdxOff, datCnt);
		}
	}


	//------------------------------------------
	// Remote array copy From 
	//------------------------------------------
	
	protected static def copy(srcBS:BlocksPLH, srcpid:Int, srcbid:Int, srcColOff:Int, 
						   dst:DenseMatrix, dstColOff:Int, colCnt:Int): Int {

		val dstIdxOff:Int = dst.M * dstColOff;
		val srcM:Int      = srcBS().getGrid().getRowSize(srcbid);
		val datCnt:Int    = srcM * colCnt;
		val srcIdxOff:Int = srcM * dstColOff;
		
		copyOffset(srcBS, srcpid, srcbid, srcIdxOff, dst, dstIdxOff, datCnt);
		return datCnt;
	}

	//===============================================================
	protected static def copyOffset(srcBS:BlocksPLH, srcpid:Int, srcbid:Int, srcIdxOff:Int, 
			dst:DenseMatrix, dstIdxOff:Int, datCnt:Int): void {

		Debug.assure(dstIdxOff+datCnt<=dst.d.size, 
		"Number of column for copying at receiving side exceeds matrix data buffer");
		
		if (here.id() == srcpid) {
			val blk = srcBS().find(srcbid);
			val srcden = blk.getMatrix() as DenseMatrix;
			Array.copy(srcden.d, srcIdxOff, dst.d, dstIdxOff, datCnt);
		} else {
			
			@Ifdef("MPI_COMMU") {
				mpiCopyOffset(srcBS, srcpid, srcbid, srcIdxOff, dst, dstIdxOff, datCnt);
			}

			@Ifndef("MPI_COMMU") {
				x10CopyOffset(srcBS, srcpid, srcbid, srcIdxOff, dst, dstIdxOff, datCnt);
			}
		}
	}
		
	/**
	 * Copy data from remote matrix to here in a vector
	 */
	protected static def mpiCopyOffset(srcBS:BlocksPLH, srcpid:Int, bid:Int, srcIdxOff:Int,
								 dst:DenseMatrix, dstIdxOff:Int, datCnt:Int): void {
		@Ifdef("MPI_COMMU") {
			val dstpid = here.id();
			finish {
				at (Dist.makeUnique()(srcpid)) async {
					//Need: src, dstpid, srcIdxOff, datCnt,
					val blk = srcBS().find(bid);
					val srcden = blk.getMatrix() as DenseMatrix;				
					val tag = here.id() * baseTagCopyFrom + dstpid + bid;
					
					Debug.assure(srcIdxOff + datCnt <= srcden.d.size, 
							"Sending side data referencing overflow");
					WrapMPI.world.send(srcden.d, srcIdxOff, datCnt, dstpid, tag);
				}
				async {
					val tag    = srcpid * baseTagCopyFrom + dstpid + bid;
					WrapMPI.world.recv(dst.d, dstIdxOff, datCnt, srcpid, tag);
				}
			}
		}
	}

	/**
	 * Copy data from remote dense matrix to here in a vector
	 */
	protected static def x10CopyOffset(srcBS:BlocksPLH, srcpid:Int, bid:Int, srcIdxOff:Int,
			dst:DenseMatrix, dstIdxOff:Int, datCnt:Int): void {
		
		val rmt:DenseRemoteSourceInfo  = at (Dist.makeUnique()(srcpid)) { 
			//Need: src, bid, srcIdxOff, datCnt
			val blk = srcBS().find(bid);
			val srcden = blk.getMatrix() as DenseMatrix;
			
			Debug.assure(srcIdxOff + datCnt <= srcden.d.size, 
					"Sending side matrix data referencing overflow");
			new DenseRemoteSourceInfo(srcden.d, srcIdxOff, datCnt)

		};
		finish Array.asyncCopy[Double](rmt.valbuf, rmt.offset, dst.d, dstIdxOff, rmt.length);
	}

	//========================================================================================
	// Sparse matrix copyTo/From
	//========================================================================================

	//------------------------
	// Copy sparse from here to remote place
	//------------------------

	protected static def copy(src:SparseCSC, srcColOff:Int, 
			dstBS:BlocksPLH, dstpid:Int, dstbid:Int, dstColOff:Int, colCnt:Int): Int {
		
		Debug.assure(srcColOff+colCnt <= src.N, "At source, number of columns exceeds matrix dimension ");
		
		if (here.id() == dstpid) {
			val blk = dstBS().find(dstbid);
			val dstspa = blk.getMatrix() as SparseCSC;
			val dz = SparseCSC.copyCols(src, srcColOff, dstspa, dstColOff, colCnt);
			return dz;
		}
		
		
		@Ifdef("MPI_COMMU") {
			return mpiCopy(src, srcColOff, dstBS, dstpid, dstbid, dstColOff, colCnt);
		}
		
		@Ifndef("MPI_COMMU") {
			return x10Copy(src, srcColOff, dstBS, dstpid, dstbid, dstColOff, colCnt);
		}
	}
	
	
	protected static def mpiCopy(src:SparseCSC, srcColOff:Int,
			dstBS:BlocksPLH, dstpid:Int, bid:Int, dstColOff:Int, colCnt:Int): Int {

		val srcpid = here.id();        
		val datasz = src.countNonZero(srcColOff, colCnt); //Implicitly carried to dst place
		if (datasz == 0) return 0;
		
		@Ifdef("MPI_COMMU") {
			finish {
				at (Dist.makeUnique()(dstpid)) async {
					// Need: srcpid, dstColOff, datasz;
					val blk = dstBS().find(bid);
					val dstspa = blk.getMatrix() as SparseCSC;
					Debug.assure(dstColOff+colCnt<=dstspa.N, "At destination, number of columns exceeds matrix dimension");
					
					val dstoff = dstspa.getNonZeroOffset(dstColOff);
					val idxtag    = baseTagCopyIdxTo + srcpid * 10000 + here.id();
					val valtag    = baseTagCopyValTo + srcpid * 10000 + here.id();
									
					//++++++++++++++++++++++++++++++++++++++++++++
					//Do NOT call getIndex()/getValue() before init at destination place
					//+++++++++++++++++++++++++++++++++++++++++++++
					dstspa.initRemoteCopyAtDest(dstColOff, colCnt, datasz);
					WrapMPI.world.recv(dstspa.getIndex(), dstoff, datasz, srcpid, idxtag);
					WrapMPI.world.recv(dstspa.getValue(), dstoff, datasz, srcpid, valtag);
					dstspa.finalizeRemoteCopyAtDest();
				}
				async {
					val idxtag    = baseTagCopyIdxTo + here.id() * 10000 + dstpid;
					val valtag    = baseTagCopyValTo + here.id() * 10000 + dstpid;
					val srcoff = src.getNonZeroOffset(srcColOff);
					
					src.initRemoteCopyAtSource(srcColOff, colCnt);
					WrapMPI.world.send(src.getIndex(), srcoff, datasz, dstpid, idxtag);
					WrapMPI.world.send(src.getValue(), srcoff, datasz, dstpid, valtag);
					src.finalizeRemoteCopyAtSource();
				}			
			}
		}
		return datasz;
	}
	
	//Sparse matrix remote copy To
	protected static def x10Copy(src:SparseCSC, srcColOff:Int,
			dstBS:BlocksPLH, dstpid:Int, bid:Int,  dstColOff:Int,	colCnt:Int) : Int {

		val datcnt = src.initRemoteCopyAtSource(srcColOff, colCnt);
		if (datcnt == 0) return 0;
		
		val idxbuf = src.getIndex() as Array[Int]{self!=null};
		val valbuf = src.getValue() as Array[Double]{self!=null};
		val datoff = src.getNonZeroOffset(srcColOff);
		val rmtidx = new RemoteArray[Int](idxbuf);
		val rmtval = new RemoteArray[Double](valbuf);

		at (Dist.makeUnique()(dstpid)) {
			//Remote capture: datcnt, rmtidx, rmtval, datoff			
			val blk = dstBS().find(bid);
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
	
	protected static def copy(srcBS:BlocksPLH, srcpid:Int, srcbid:Int, srcColOff:Int,
			dst:SparseCSC, dstColOff:Int, colCnt:Int): Int {

		Debug.assure(dstColOff+colCnt<=dst.N, 
				"The range of columns exceeds receiving side matrix dimension");
		
		if (here.id() == srcpid) {
			val blk = srcBS().find(srcbid);
			val srcspa = blk.getMatrix() as SparseCSC;
			
			return SparseCSC.copyCols(srcspa, srcColOff, dst, dstColOff, colCnt);
		}

		@Ifdef("MPI_COMMU") {
			return mpiCopy(srcBS, srcpid, srcbid, srcColOff, dst, dstColOff, colCnt);
		}

		@Ifndef("MPI_COMMU") {
			return x10Copy(srcBS, srcpid, srcbid, srcColOff, dst, dstColOff, colCnt);
		}
	}

	/**
	 * Based on mpi send/recv, copy data from remote place to here. The data size is 
	 * transfered to here implicitly before matrix data copy.
	 */
	protected static def mpiCopy(srcBS:BlocksPLH, srcpid:Int, srcbid:Int, srcColOff:Int,
			dst:SparseCSC, dstColOff:Int, colCnt:Int): Int {

		val dstpid = here.id();//Implicitly carried to dst place
		
		// Get the data count first, remote capture is used 
		val dsz = at (Dist.makeUnique()(srcpid)) {
			compBlockDataSize(srcBS, srcbid, srcColOff, colCnt)
		};
		if (dsz == 0) return 0;
		
		//Start paired send/recv
		@Ifdef("MPI_COMMU") {
			finish {
				at (Dist.makeUnique()(srcpid)) async {
					//Need: srcBS, srcbid, dstpid, srcColOff, dsz
					val srcspa = srcBS().find(srcbid).getMatrix() as SparseCSC;
					val srcoff = srcspa.getNonZeroOffset(srcColOff);;
					
					val idxtag    = baseTagCopyIdxFrom + here.id() * 10000 + dstpid;
					val valtag    = baseTagCopyValFrom + here.id() * 10000 + dstpid;
					
					srcspa.initRemoteCopyAtSource(srcColOff, colCnt);
					WrapMPI.world.send(srcspa.getIndex(), srcoff, dsz, dstpid, idxtag);
					WrapMPI.world.send(srcspa.getValue(), srcoff, dsz, dstpid, valtag);
					srcspa.finalizeRemoteCopyAtSource();
				}
				
				async {
					
					val dstoff = dst.getNonZeroOffset(dstColOff);
					val idxtag = baseTagCopyIdxFrom + srcpid * 10000 + here.id();
					val valtag = baseTagCopyValFrom + srcpid * 10000 + here.id();
					
					dst.initRemoteCopyAtDest(dstColOff, colCnt, dsz);
					WrapMPI.world.recv(dst.getIndex(), dstoff, dsz, srcpid, idxtag);
					WrapMPI.world.recv(dst.getValue(), dstoff, dsz, srcpid, valtag);
					dst.finalizeRemoteCopyAtDest();
				}
			}
		}
		
		return dsz;
	}

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
	protected static def x10Copy(src:BlocksPLH, srcpid:Int, bid:Int, srcColOff:Int,
			dst:SparseCSC, dstColOff:Int, colCnt:Int): Int {

		val rmt = at (Dist.makeUnique()(srcpid)) {
			val blk = src().find(bid);
			val srcspa = blk.getMatrix() as SparseCSC;
			val off = srcspa.getNonZeroOffset(srcColOff);;
			val cnt = srcspa.countNonZero(srcColOff, colCnt);
			
			srcspa.initRemoteCopyAtSource(srcColOff, colCnt);
			SparseRemoteSourceInfo(srcspa.getIndex(), srcspa.getValue(), off, cnt)
		};

		if (rmt.length == 0) return 0;

		val dstoff = dst.getNonZeroOffset(dstColOff);
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
