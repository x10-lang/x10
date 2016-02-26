/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package x10.matrix.comm;

import x10.regionarray.DistArray;

import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.sparse.SparseCSC;

import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;

/**
 * This class transfers matrix data across different places.
 * Matrix data is defined by DenseBlock, SparseBlock or DenseMatrix on DistArray.
 * 
 * <p>For more information on how to build different backends and runtime, 
 * run command "make help" at the root directory of GML library.
 */
public class MatrixRemoteCopy {
	/**
	 * Copy whole dense matrix from here to the remote place via DistArray of dense matrices.
	 *
	 * @param dmlist 	DistArray of dense matrices
	 * @param dstpid 	The destination place
	 * @return -- number of elements copied
	 */
	public static def copyTo(dmlist:DistArray[DenseMatrix](1), dstpid:Long) {
		val srcden = dmlist(here.id());
		return copy(srcden, 0, dmlist, dstpid, 0, srcden.N);
	}

	/**
	 * Copy whole dense matrix from remote place to here
	 *
	 * @param dmlist 	DistArray of dense matrices
	 * @param srcpid 	The source place
	 * @return -- number of elements copied
	 */
	public static def copyFrom(dmlist:DistArray[DenseMatrix](1), srcpid:Long) {
		val dstden = dmlist(here.id());
		return copy(dmlist, srcpid, 0, dstden, 0, dstden.N);
	}

	/**
	 * Copy specified columns from source matrix at here to the remote 
	 * place via DistArray of dense matrix.
	 * 
	 * @param srcden    	the source dense matrix at here
	 * @param srcColOff 	starting column in in source matrix
	 * @param dmlist    	target dense matrix in DistArray
	 * @param dstpid    	the place id of target dense matrix in DistArray.
	 * @param dstColOff 	starting column offset in target matrix at destination place
	 * @param colCnt    	count of columns to be copied from source dense matrix
	 * @return -- number of elements copied.
	 */
	public static def copy(
			srcden:DenseMatrix, srcColOff:Long,
			dmlist:DistArray[DenseMatrix](1), 
			dstpid:Long, dstColOff:Long, colCnt:Long):Long {

		var dsz:Long = 0;
		if (here.id() == dstpid) {
			DenseMatrix.copyCols(srcden, srcColOff, dmlist(dstpid), dstColOff, colCnt);
			return srcden.M * colCnt;
		}

		dsz = x10Copy(srcden, srcColOff, dmlist, dstpid, dstColOff, colCnt);
		return dsz;
	}
	
	/**
	 * Copy source dense matrix from here to the remote dense matrix of
	 * duplicated dense matrix at a remote place using Array remote copy.
	 * The source and destination dense matrices must have the same leading dension - M.
	 * 
	 * @param srcden 		source dense matrix at here
	 * @param srcColOff		column offset in source matrix
	 * @param dmlist 		target dense matrix in DistArray
	 * @param dstpid 		place id of target dense matrix in the duplicated dense matrix.
	 * @param dstColOff 	column offset in target matrix
	 * @param colCnt 		Count of columns to be copied from source dense matrix
	 * @return -- Number of elements copied.
	 */
	protected static def x10Copy(
			srcden:DenseMatrix, srcColOff:Long,
			dmlist:DistArray[DenseMatrix](1), dstpid:Long,
			dstColOff:Long, colCnt:Long):Long {
		
		assert (srcColOff + colCnt <= srcden.N) :
		    "at source place, illegal column offset and count";
		val buf = srcden.d as Rail[ElemType]{self!=null};
		val srcbuf = new GlobalRail[ElemType](buf);
		val datcnt = srcden.M * colCnt;
		val srcoff = srcden.M * srcColOff;

		assert srcColOff+colCnt <= srcden.N;
		at(dmlist.dist(dstpid)) {
			//Implicit copy: dst, srcbuf, srcoff, datcnt
			val dstden = dmlist(here.id());
			val dstoff = dstColOff * dstden.M;

			assert dstColOff*dstden.M+datcnt <= dstden.M*dstden.N;
			finish Rail.asyncCopy[ElemType](srcbuf, srcoff, dstden.d, dstoff, datcnt);		
		}

		return datcnt;
	}

	/**
	 * Copy multiple columns of the dense matrix in the specified place to
	 * here
	 * 
	 * @param dmlist    	source matrix in the duplicated dense matrix
	 * @param srcpid   		source matrix's place id.
	 * @param srcColOff 	starting column in in source matrix
	 * @param dstden   		destination dense matrix of the copy
	 * @param dstColOff 	starting column in target matrix
	 * @param colCnt    	count of columns to be copied in source matrix
	 * @return -- Number of elements copied
	 */
	public static def copy(
			dmlist:DistArray[DenseMatrix](1), srcpid:Long, srcColOff:Long, 
			dstden:DenseMatrix, dstColOff:Long, 
			colCnt:Long):Long {

		var dsz:Long = 0;
		if (here.id() == srcpid) {
			DenseMatrix.copyCols(dmlist(srcpid), srcColOff, dstden, dstColOff, colCnt);
			return dstden.M * colCnt;
		}

		dsz = x10Copy(dmlist, srcpid, srcColOff, dstden, dstColOff, colCnt);
		return dsz;
	}
	
	/**
	 * Copy remote dense matrix from specified place to here, using array remote copy.
	 * The source and destination dense matrix must have the same leading dimension.
	 *
	 * @param dmlist  		source dense matrix in DistArray
	 * @param srcpid  		source matrix's place id.
	 * @param srcColOff  	column offset in source matrix
	 * @param dstden  		destination dense matrix of the copy
	 * @param dstColOff 	column offset in target matrix
	 * @param colCnt  		count of columns to be copied in source matrix
	 * @return 				number of elements copied
	 */
	public static def x10Copy(
			dmlist:DistArray[DenseMatrix](1), srcpid:Long, srcColOff:Long,
			dstden:DenseMatrix, dstColOff:Long, 
			colCnt:Long):Long {

		val dstoff = dstden.M * dstColOff;
		val rmt:DenseRemoteSourceInfo  = at(dmlist.dist(srcpid)) { 
			//Need: dmlist, srcColOff, colCnt
			val mat = dmlist(here.id());
			val off = srcColOff * mat.M;
			val cnt = colCnt    * mat.M;
			assert (off + cnt <= mat.M * mat.N) :
						 "Matrix remote copy fails! Illegal size at source matrix";
			DenseRemoteSourceInfo(mat.d, off, cnt)
		};

		assert (dstoff+rmt.length <= dstden.M*dstden.N) :
					 "Matrix remote copy fails! Illegal size at target matrix";
		
		finish Rail.asyncCopy[ElemType](rmt.valbuf, rmt.offset, dstden.d, dstoff, rmt.length);
		
		return rmt.length;
	}

	/**
	 * Copy multiple columns from source dense matrix at here to the specified remote 
	 * place in DistArray of dense matrix.
	 * 
	 * @param srcden   		the source dense matrix at here
	 * @param srcColOff 	starting column in in source matrix
	 * @param dmlist    	target dense blocks in DistArray
	 * @param dstpid    	the place id of target dense matrix in DistArray.
	 * @param dstColOff 	starting column offset in target matrix at destination place
	 * @param colCnt   		count of columns to be copied from source dense matrix
	 * @return 				number of elements copied.
	 */
	public static def copy(
			srcden:DenseMatrix, srcColOff:Long,
			dmlist:DistArray[DenseBlock](1), 
			dstpid:Long, dstColOff:Long, 
			colCnt:Long):Long {
		
		var dsz:Long = 0;
		if (here.id() == dstpid) {
			DenseMatrix.copyCols(srcden, srcColOff, 
								 dmlist(dstpid).getMatrix(), dstColOff, colCnt);
			return srcden.M * colCnt;
		}

		dsz = x10Copy(srcden, srcColOff, dmlist, dstpid, dstColOff, colCnt);
		return dsz;
	}

	// Dense matrix remote copy to
	protected static def x10Copy(
			srcden:DenseMatrix,  srcColOff:Long,
			dst:DistArray[DenseBlock](1), dstbid:Long, 
			dstColOff:Long, colCnt:Long):Long {

		val buf = srcden.d as Rail[ElemType]{self!=null};
		val srcbuf = new GlobalRail[ElemType](buf);
		val datcnt = srcden.M * colCnt;
		val srcoff = srcden.M * srcColOff;
	
		assert srcColOff+colCnt <= srcden.N;
		at(dst.dist(dstbid)) {
			//Implicit copy: dst, srcbuf, srcoff, datcnt
			val dstden = dst(here.id()).getMatrix();
			val dstoff = dstColOff * dstden.M;

			assert dstColOff*dstden.M+datcnt <= dstden.M*dstden.N;
			finish Rail.asyncCopy[ElemType](srcbuf, srcoff, dstden.d, dstoff, datcnt);		
		}
		return datcnt;
	}
	

	//Dense matrix block remote copy From

	/**
	 * Copy multiple columns from source dense matrix at here to the specified remote 
	 * place in DistArray of dense matrix.
	 * 
	 * @param dmlist    	target dense blocks in DistArray
	 * @param srcpid    	the place id of target dense matrix in DistArray.
	 * @param srcColOff 	starting column in in source matrix
	 * @param dstden    	the source dense matrix at here
	 * @param dstColOff 	starting column offset in target matrix at destination place
	 * @param colCnt    	count of columns to be copied from source dense matrix
	 * @return -- number of elements copied.
	 */
	public static def copy(
			dmlist:DistArray[DenseBlock](1), srcpid:Long, srcColOff:Long,
			dstden:DenseMatrix, dstColOff:Long, 
			colCnt:Long):Long {
		
		var dsz:Long = 0;
		if (here.id() == srcpid) {
			DenseMatrix.copyCols(dmlist(srcpid).getMatrix(), srcColOff, 
								 dstden, dstColOff, colCnt);
			return dstden.M * colCnt;
		}

		dsz = x10Copy(dmlist, srcpid, srcColOff, dstden, dstColOff, colCnt);
		return dsz;
	}
	
	protected static def x10Copy(
			src:DistArray[DenseBlock](1), srcbid:Long, srcColOff:Long,
			dstden:DenseMatrix, dstColOff:Long, colCnt:Long):Long {

		val rmt:DenseRemoteSourceInfo = at(src.dist(srcbid)) { 
			//Implicit copy: src, srcColOff, colCnt, 
										
		    val mat = src(here.id()).getMatrix();
			val off = srcColOff * mat.M;
			var cnt:Long = mat.M * colCnt;
			assert (cnt <= mat.M * mat.N) :
						 "Matrix remote copy fails! Illegal size at source matrix";
			DenseRemoteSourceInfo(mat.d, off, cnt)
		};

		val sttoff = dstColOff * dstden.M;
		assert (sttoff+rmt.length <= dstden.M*dstden.N) :
					 "Matrix remote copy fails! Illegal size at target matrix";
		
		finish Rail.asyncCopy[ElemType](rmt.valbuf, rmt.offset, dstden.d, sttoff, rmt.length);
		return rmt.length;
	}
	
	/**
	 * Copy whole sparse matrix from here to remote place stored in DistArray
	 *
	 * @param smlist 	DistArray of sparse matrices
	 * @param dstpid 	destination place id
	 * @return 			number of elements copied
	 */
	public static def copyTo(smlist:DistArray[SparseCSC](1), dstpid:Long) {
		val srcspa = smlist(here.id());
		return copy(srcspa, 0, smlist, dstpid, 0, srcspa.N);
	}

	/**
	 * Copy whole sparseCSC matrix from remote place to here
	 *
	 * @param smlist 	DistArray of sparse matrices
	 * @param srcpid 	The source place
	 * @return -- number of elements copied
	 */
	public static def copyFrom(smlist:DistArray[SparseCSC](1), srcpid:Long) {
		val dstspa = smlist(here.id());
		return copy(smlist, srcpid, 0, dstspa, 0, dstspa.N);
	}


	// Sparse matrix copy To

	/**
	 * Copy multiple columns from source sparse matrix at here to the specified remote 
	 * place in DistArray.
	 * 
	 * @param srcspa 		source sparseCSC matrix at here
	 * @param srcColOff		column offset in source matrix
	 * @param smlist 		target sparse matrix in DistArray
	 * @param dstpid 		place id of target sparse matrix in DistArray.
	 * @param dstColOff		column offset in target matrix
	 * @param colCnt 		count of columns to be copied from source sparse matrix
	 * @return 				number of elements copied.
	 */
	public static def copy(
			srcspa:SparseCSC, srcColOff:Long,
			smlist:DistArray[SparseCSC](1), dstpid:Long, dstColOff:Long, 
			colCnt:Long):Long {
		
		var dsz:Long = 0;

		if (here.id() == dstpid) {
			dsz = SparseCSC.copyCols(srcspa, srcColOff, smlist(dstpid), dstColOff, colCnt);
			return dsz;
		}

		dsz = x10Copy(srcspa, srcColOff, smlist, dstpid, dstColOff, colCnt);
		return dsz;
	}

	/**
	 * Copy source sparse matrix from here to the remote sparse matrix at reomote 
	 * place using Array remote copy.
	 * The source and destination dense matrices must have the same leading dension - M.
	 * 
	 * @param srcspa 		source sparse matrix at here
	 * @param srcColOff 	column offset in source matrix
	 * @param smlist 		target sparse matrix in DistArray
	 * @param dstpid 		place id of target sparse matrix in DistArray.
	 * @param dstColOff 	column offset in target matrix
	 * @param colCnt 		count of columns to be copied from source matrix
	 * @return -- Number of elements copied.
	 */
	protected static def x10Copy(
			srcspa:SparseCSC, srcColOff:Long,
			dst:DistArray[SparseCSC](1), dstpid:Long, dstColOff:Long,
			colCnt:Long):Long {

		assert (srcColOff+colCnt <= srcspa.N) :
					 "At source place illegal column offset and count";

		val idxbuf = srcspa.getIndex() as Rail[Long]{self!=null};
		val valbuf = srcspa.getValue() as Rail[ElemType]{self!=null};
		val datoff = srcspa.getNonZeroOffset(srcColOff);
		val datcnt = srcspa.initRemoteCopyAtSource(srcColOff, colCnt);
		val rmtidx = new GlobalRail[Long](idxbuf);
		val rmtval = new GlobalRail[ElemType](valbuf);

		at(dst.dist(dstpid)) {
			//Implicit copy:dst, datcnt, rmtidx, rmtval, datoff
			val dstspa = dst(here.id());

			//++++++++++++++++++++++++++++++++++++++++++++
			//Do not call getIndex()/getValue() before init at destination place
			//+++++++++++++++++++++++++++++++++++++++++++++
			dstspa.initRemoteCopyAtDest(dstColOff, colCnt, datcnt);
			val dstoff = dstspa.getNonZeroOffset(dstColOff);
			finish Rail.asyncCopy[Long  ](rmtidx, datoff, dstspa.getIndex(), dstoff, datcnt);
			finish Rail.asyncCopy[ElemType](rmtval, datoff, dstspa.getValue(), dstoff, datcnt);
			dstspa.finalizeRemoteCopyAtDest();
		}
		srcspa.finalizeRemoteCopyAtSource();

		return datcnt;
	}

	/**
	 * Copy multiple columns of the sparseCSC matrix in the specified place to
	 * here
	 *
	 * @param smlist  		source sparse matrix in DistArray
	 * @param srcpid  		source matrix's place id.
	 * @param srcColOff  	column offset in source matrix
	 * @param dstspa  		destination sparse matrix of the copy
	 * @param dstColOff 	column offset in target matrix
	 * @param colCnt  		count of columns to be copied in source matrix
	 * @return -- Number of elements copied
	 */
	public static def copy(
			smlist:DistArray[SparseCSC](1), srcpid:Long, srcColOff:Long,
			dstspa:SparseCSC, dstColOff:Long, 
			colCnt:Long):Long {
		var dsz:Long = 0;

		if (here.id() == srcpid) {
			dsz = SparseCSC.copyCols(smlist(srcpid), srcColOff,  dstspa, dstColOff, colCnt);
			return dsz;
		}

		dsz = x10Copy(smlist, srcpid, srcColOff, dstspa, dstColOff, colCnt);
		return dsz;
	}

	/**
	 * Copy remote sparse matrix from specified place to here, using array remote copy.
	 * The source and destination matrix must have the same leading dimension.
	 *
	 * @param smlist  		source sparseCSC matrix in DistArray
	 * @param srcpid  		source matrix's place id.
	 * @param srcColOff  	column offset in source matrix
	 * @param dstspa 		destination sparse matrix at here
	 * @param dstColOff  	column offset in target matrix
	 * @param colCnt 		count of columns to be copied in source matrix
	 * @return 				number of elements copied
	 */
	public static def x10Copy(
			smlist:DistArray[SparseCSC](1), srcpid:Long, srcColOff:Long,
			dstspa:SparseCSC, dstColOff:Long, 
			colCnt:Long):Long {

		assert (dstColOff+colCnt <= dstspa.N) :
					 "At destination place, illegal column offset and count";

		val dstpid = here.id();

		val rmt = at(smlist.dist(srcpid)) {
			//Need: smlist, srcColOff, colCnt
			val mat = smlist(here.id());
			val off = mat.getNonZeroOffset(srcColOff);
			val cnt = mat.countNonZero(srcColOff, colCnt);

			mat.initRemoteCopyAtSource(srcColOff, colCnt);
			SparseRemoteSourceInfo(mat.getIndex(), mat.getValue(), off, cnt)
		};

		val dstoff = dstspa.getNonZeroOffset(dstColOff);
		//++++++++++++++++++++++++++++++++++++++++++++
		//Do NOT call getIndex()/getValue() before init at destination place
		//+++++++++++++++++++++++++++++++++++++++++++++
		dstspa.initRemoteCopyAtDest(srcColOff, colCnt, rmt.length);
		finish Rail.asyncCopy[Long  ](rmt.idxbuf, rmt.offset, 
									   dstspa.getIndex(), dstoff, rmt.length);
		finish Rail.asyncCopy[ElemType](rmt.valbuf, rmt.offset, 
									   dstspa.getValue(), dstoff, rmt.length);
		
		finish {
			at(smlist.dist(srcpid)) async {
				//Need: smlist
				val srcspa = smlist(here.id());
				srcspa.finalizeRemoteCopyAtSource();
			}
			dstspa.finalizeRemoteCopyAtDest();
		}
		
		return rmt.length;
	}

	/**
	 * Copy multiple columns from source sparse matrix block at here to the specified remote 
	 * place in DistArray.
	 * 
	 * @param srcspa 		source sparseCSC matrix at here
	 * @param srcColOff 	column offset in source matrix
	 * @param smlist 		target sparse matrix block in DistArray
	 * @param dstpid 		place id of target sparse matrix in DistArray.
	 * @param dstColOff 	column offset in target matrix
	 * @param colCnt 		count of columns to be copied from source sparse matrix
	 * @return 				number of elements copied.
	 */
	public static def copy(
			srcspa:SparseCSC, srcColOff:Long,
			smlist:DistArray[SparseBlock](1), dstpid:Long, dstColOff:Long, 
			colCnt:Long):Long {
		
		var dsz:Long = 0;
		if (here.id() == dstpid) {
			dsz = SparseCSC.copyCols(srcspa, srcColOff, 
									 smlist(dstpid).getMatrix(), dstColOff, colCnt);
			return dsz;
		}

		dsz = x10Copy(srcspa, srcColOff, smlist, dstpid, dstColOff, colCnt);
		return dsz;
	}

	//Sparse matrix remote copy To
	protected static def x10Copy(
			srcspa:SparseCSC, srcColOff:Long,
			dst:DistArray[SparseBlock](1), dstbid:Long, dstColOff:Long, 
			colCnt:Long):Long {

		assert (srcColOff+colCnt <= srcspa.N) :
					 "At source place illegal column offset and count";

		val idxbuf = srcspa.getIndex() as Rail[Long]{self!=null};
		val valbuf = srcspa.getValue() as Rail[ElemType]{self!=null};
		val datoff = srcspa.getNonZeroOffset(srcColOff);
		val datcnt = srcspa.initRemoteCopyAtSource(srcColOff, colCnt);
		val rmtidx = new GlobalRail[Long](idxbuf);
		val rmtval = new GlobalRail[ElemType](valbuf);

		at(dst.dist(dstbid)) {
			//Implicit copy:dst, datcnt, rmtidx, rmtval, datoff
			val dstspa = dst(here.id()).getMatrix();

			//++++++++++++++++++++++++++++++++++++++++++++
			//Do not call getIndex()/getValue() before init at destination place
			//+++++++++++++++++++++++++++++++++++++++++++++
			dstspa.initRemoteCopyAtDest(dstColOff, colCnt, datcnt);
			val dstoff = dstspa.getNonZeroOffset(dstColOff);
			finish Rail.asyncCopy[Long  ](rmtidx, datoff, dstspa.getIndex(), dstoff, datcnt);
			finish Rail.asyncCopy[ElemType](rmtval, datoff, dstspa.getValue(), dstoff, datcnt);
			dstspa.finalizeRemoteCopyAtDest();
		}

		srcspa.finalizeRemoteCopyAtSource();
		return datcnt;
	}


	// Sparse Block copy From

	/**
	 * Copy multiple columns of the sparseCSC matrix in the specified place to
	 * here
	 *
	 * @param smlist  		source sparse matrix block in DistArray
	 * @param srcpid 		source matrix's place id.
	 * @param srcColOff  	column offset in source matrix
	 * @param dstspa 		destination sparse matrix of the copy
	 * @param dstColOff  	column offset in target matrix
	 * @param colCnt  		count of columns to be copied in source matrix
	 * @return 				number of elements copied
	 */
	public static def copy(
			smlist:DistArray[SparseBlock](1), srcpid:Long, srcColOff:Long,
			dstspa:SparseCSC, dstColOff:Long, 
			colCnt:Long):Long {
		
		var dsz:Long = 0;
		if (here.id() == srcpid) {
			dsz = SparseCSC.copyCols(smlist(srcpid).getMatrix(), srcColOff,  
									 dstspa, dstColOff, colCnt);
			return dsz;
		}

		dsz = x10Copy(smlist, srcpid, srcColOff, dstspa, dstColOff, colCnt);
		return dsz;
	}

	//Sparse matrix remote copyt from
	protected static def x10Copy(
			src:DistArray[SparseBlock](1), srcbid:Long, srcColOff:Long,
			dstspa:SparseCSC, dstColOff:Long, 
			colCnt:Long):Long {

		val rmt:SparseRemoteSourceInfo  = at(src.dist(srcbid)) { 
			//Need: src, srcColOff, colCnt
			val mat = src(here.id()).getMatrix();
			val off = mat.getNonZeroOffset(srcColOff);
			val cnt = mat.initRemoteCopyAtSource(srcColOff, colCnt);

			SparseRemoteSourceInfo(mat.getIndex(), mat.getValue(),  off, cnt)
		};

		val datoff = dstspa.getNonZeroOffset(dstColOff);

		//++++++++++++++++++++++++++++++++++++++++++++
		//Do NOT call getIndex()/getValue() before init
		//+++++++++++++++++++++++++++++++++++++++++++++
		dstspa.initRemoteCopyAtDest(dstColOff, colCnt, rmt.length);
		finish Rail.asyncCopy[Long  ](rmt.idxbuf, rmt.offset, dstspa.getIndex(), datoff, rmt.length);
		finish Rail.asyncCopy[ElemType](rmt.valbuf, rmt.offset, dstspa.getValue(), datoff, rmt.length);
	
		//Rebuild or reset indexing
		finish {
			at  (src.dist(srcbid)) async {
				val mat  = src(here.id()).getMatrix();
				mat.finalizeRemoteCopyAtSource();
			}
			dstspa.finalizeRemoteCopyAtDest();
		}
		return rmt.length;
	}

	/**
	 * Copy multiple columns from source dense matrix at here to the specified remote 
	 * place in DistArray of dense matrix.
	 * 
	 * @param src    		the source dense vector array
	 * @param srcOff 		starting offset in source matrix
	 * @param dmlist    	target dense matrix in DistArray
	 * @param dstpid    	the place id of target dense matrix in DistArray.
	 * @param dstColOff 	starting column offset in target matrix at destination place
	 * @param dataCnt   	count of data to be copied from source vector
	 */
	public static def copy(
			src:Rail[ElemType], srcOff:Long,
			dmlist:DistArray[DenseMatrix](1), 
			dstpid:Long, dstColOff:Long, 
			dataCnt:Long): void {
		
		var dsz:Long = 0;
		
		if (here.id() == dstpid) {
			val dstmat = dmlist(dstpid);
			Rail.copy(src, srcOff, dstmat.d, dstColOff*dstmat.M, dataCnt);
			return;
		}

		x10Copy(src, srcOff, dmlist, dstpid, dstColOff, dataCnt);
	}

	/**
	 * Copy vector from here to remote dense matrix
	 */
	protected static def x10Copy(
			src:Rail[ElemType], srcOff:Long,
			dmlist:DistArray[DenseMatrix](1), dstpid:Long, dstColOff:Long, 
			dataCnt:Long):void {

		assert (srcOff + dataCnt <= src.size) :
		    "at source place, illegal column offset and count";
		val buf = src as Rail[ElemType]{self!=null};
		val srcbuf = new GlobalRail[ElemType](buf);

		at(dmlist.dist(dstpid)) {
			//Implicit copy: dst, srcbuf, srcOff, dataCnt
			val dstden = dmlist(here.id());
			val dstoff = dstColOff * dstden.M;

			assert dstColOff*dstden.M+dataCnt <= dstden.M*dstden.N;
			finish Rail.asyncCopy[ElemType](srcbuf, srcOff, dstden.d, dstoff, dataCnt);		
		}
	}
	

	/**
	 * Copy multiple columns of the dense matrix in the specified place to
	 * here
	 * 
	 * @param dmlist   		source matrix in the dist dense matrix
	 * @param srcpid    	source matrix's place id.
	 * @param srcColOff 	starting column in in source matrix
	 * @param dst      		destination vector array
	 * @param dstOff   		starting offset in vector
	 * @param dataCnt   	count of data to copy
	 */
	public static def copy(
			dmlist:DistArray[DenseMatrix](1), srcpid:Long, srcColOff:Long, 
			dst:Rail[ElemType], dstOff:Long, 
			dataCnt:Long):void {

		if (here.id() == srcpid) {
			val srcden = dmlist(srcpid);
			Rail.copy(srcden.d, srcColOff*srcden.M, dst, dstOff, dataCnt);
			return;
		}

		x10Copy(dmlist, srcpid, srcColOff, dst, dstOff, dataCnt);
	}

	/**
	 * Copy data from remote dense matrix to here in a vector
	 */
	protected static def x10Copy(
			dmlist:DistArray[DenseMatrix](1), srcpid:Long, srcColOff:Long,
			dst:Rail[ElemType], dstOff:Long, 
			dataCnt:Long):void {

		val rmt:DenseRemoteSourceInfo  = at(dmlist.dist(srcpid)) { 
			//Need: dmlist, srcColOff, dataCnt
			val mat = dmlist(here.id());
			val off = srcColOff * mat.M;
			assert (off + dataCnt <= mat.M * mat.N) :
			    "Matrix remote copy fails! Illegal size at source matrix";
			DenseRemoteSourceInfo(mat.d, off, dataCnt)
		};

        assert (dstOff+rmt.length <= dst.size) :
            "Matrix remote copy fails! Illegal size at target matrix";
		
		finish Rail.asyncCopy[ElemType](rmt.valbuf, rmt.offset, dst, dstOff, dataCnt);
	}
	
	/**
	 * Copy multiple columns from source dense matrix at here to the specified remote 
	 * place in DistArray of dense matrix.
	 * 
	 * @param src   		the source vector array at here
	 * @param srcOff 		starting offset in in source vector array
	 * @param dmlist    	target dense blocks in DistArray
	 * @param dstpid   		the place id of target dense matrix in DistArray.
	 * @param dstColOff 	starting column offset in target matrix at destination place
	 * @param dataCnt    	count of data to copy from source dense matrix
	 */
	public static def copy(
			src:Rail[ElemType], srcOff:Long,
			dmlist:DistArray[DenseBlock](1), dstpid:Long, dstColOff:Long, 
			dataCnt:Long):void {
		
		if (here.id() == dstpid) {
			val dstden = dmlist(dstpid).getMatrix();
			Rail.copy(src, srcOff, dstden.d, dstColOff*dstden.M, dataCnt);
			return ;
		}

		x10Copy(src, srcOff, dmlist, dstpid, dstColOff, dataCnt);
	}

	/**
	 * Copy vector from here to remote block 
	 */
	protected static def x10Copy(
			src:Rail[ElemType], srcOff:Long,
			dst:DistArray[DenseBlock](1), dstbid:Long, dstColOff:Long, 
			dataCnt:Long):void {

		val buf = src as Rail[ElemType]{self!=null};
		val srcbuf = new GlobalRail[ElemType](buf);
		
		assert srcOff+dataCnt <= src.size;
		at(dst.dist(dstbid)) {
			//Implicit copy: dst, srcbuf, srcOff, dataCnt
			val dstden = dst(here.id()).getMatrix();
			val dstoff = dstColOff * dstden.M;

			assert dstColOff*dstden.M+dataCnt <= dstden.M*dstden.N;
			finish Rail.asyncCopy[ElemType](srcbuf, srcOff, dstden.d, dstoff, dataCnt);		
		}
	}
	
	/**
	 * Copy multiple columns from source dense matrix at here to the specified remote 
	 * place in DistArray of dense matrix.
	 * 
	 * @param dmlist    	target dense blocks in DistArray
	 * @param srcpid    	the place id of target dense matrix in DistArray.
	 * @param srcColOff 	starting column in in source matrix
	 * @param dst       	the target vector array at here
	 * @param dstOff    	starting offset in target vector array
	 * @param dataCnt   	count of data to be copied from source dense matrix
	 */
	public static def copy(
			dmlist:DistArray[DenseBlock](1), srcpid:Long, srcColOff:Long,
			dst:Rail[ElemType], dstOff:Long, 
			dataCnt:Long):void {
		
		if (here.id() == srcpid) {
			val srcden = dmlist(srcpid).getMatrix();
			Rail.copy(srcden.d, srcColOff*srcden.M, dst, dstOff, dataCnt);
			return;
		}

		x10Copy(dmlist, srcpid, srcColOff, dst, dstOff, dataCnt);
	}

	/**
	 * Copy data from remote block to here in vector 
	 */
	protected static def x10Copy(
			src:DistArray[DenseBlock](1), srcbid:Long, srcColOff:Long,
			dst:Rail[ElemType], dstOff:Long, 
			dataCnt:Long):void {

		val rmt:DenseRemoteSourceInfo = at(src.dist(srcbid)) { 
			//Implicit copy: src, srcColOff, datdCnt, 
			
			val mat = src(here.id()).getMatrix();
			val off = srcColOff * mat.M;
            assert (off+dataCnt <= mat.M * mat.N) :
                "Matrix remote copy fails! Illegal size at source matrix";
			DenseRemoteSourceInfo(mat.d, off, dataCnt)
		};

        assert (dstOff+dataCnt <= dst.size) :
            "Matrix remote copy fails! Illegal size at target matrix";
		
		finish Rail.asyncCopy[ElemType](rmt.valbuf, rmt.offset, dst, dstOff, dataCnt);
	}
}
