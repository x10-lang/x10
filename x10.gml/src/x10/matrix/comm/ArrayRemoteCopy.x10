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

import x10.matrix.ElemType;

import x10.matrix.sparse.CompressArray;

/**
 * This class supports inter-place communication for data arrays which are defined
 * in PlaceLocalHandle. 
 */
public class ArrayRemoteCopy {
	private static val baseTagCopyTo:Int=100000n;
	private static val baseTagCopyFrom:Int=200000n;
	private static val baseTagCopyIdxTo:Int=300000n;
	private static val baseTagCopyIdxFrom:Int=400000n;
	private static val baseTagCopyValTo:Int=500000n;
	private static val baseTagCopyValFrom:Int=600000n;
	private static val baseTagCopyToPLH:Int=700000n;
	private static val baseTagCopyFromPLH:Int=800000n;
	
	/**
	 * Copy multiple columns from source array data at here to the specified remote 
	 * place in DistArray of dense matrix.
	 * 
	 * @param src      -- the source array
	 * @param srcOff   -- starting column in in source matrix
	 * @param dstplh   -- target in PlaceLocalHandle 
	 * @param dstpid   -- the place id of target in DistArray.
	 * @param dstOff   -- starting column offset in target matrix at destination place
	 * @param dataCnt  -- count of columns to be copied from source dense matrix
	 */
	public static def copy(
			src:Rail[ElemType]{self!=null}, srcOff:Long, 
			dstplh:DataArrayPLH, dstpid:Long, dstOff:Long, 
			dataCnt:Long) :void  {
		
		if (here.id() == dstpid) {
			Rail.copy(src, srcOff, dstplh(), dstOff, dataCnt);
			return;
		}

        assert (srcOff+dataCnt <= src.size) :
            "At source place, illegal data offset:"+srcOff+
            " or data count:"+dataCnt;
		
		x10Copy(src, srcOff, dstplh, dstpid, dstOff, dataCnt);
	}

	protected static def x10Copy(
			src:Rail[ElemType]{self!=null}, srcOff:Long, 
			dstplh:DataArrayPLH, dstpid:Long, dstOff:Long, 
			dataCnt:Long) :void  {

        assert (srcOff+dataCnt) <= src.size;
        val gr = GlobalRail[ElemType](src);
        at(Place(dstpid)) {
            val dst = dstplh() as Rail[ElemType]{self!=null};
            assert (dstOff+dataCnt) <= dst.size;
            finish Rail.asyncCopy[ElemType](gr, srcOff, dst, dstOff, dataCnt);
        }
	}
	
	//TODO: Check this code. Introduced to support a method in DistArrayScatter.
	// unsure of how to get an Array[ElemType] at remote place from a 
	// DistDataArray.
	protected static def x10Copy(
			src:Rail[ElemType]{self!=null}, srcOff:Long, 
			dst:DistDataArray, dstpid:Long, dstOff:Long, 
			dataCnt:Long) :void  {
        assert (srcOff+dataCnt) <= src.size;
        val gr = GlobalRail[ElemType](src);
        at(Place(dstpid)) {
            val dstLocal = dst.getLocalPortion()(0);
            assert (dstOff+dataCnt) <= dstLocal.size;
            finish Rail.asyncCopy[ElemType](gr, srcOff, dstLocal, dstOff, dataCnt);
        }
	}

	/**
	 * Copy data array from in the specified place at 
	 * here to target
	 * 
	 * @param srcplh   -- source data array in PlaceLocalHandle
	 * @param srcpid    -- source array's place id.
	 * @param srcOff    -- starting offset in source matrix
	 * @param dst       -- destination vector array
	 * @param dstOff    -- starting offset in receiving array at here
	 * @param dataCnt   -- count of data to copy
	 */
	public static def copy(
			srcplh:DataArrayPLH, srcpid:Long, srcOff:Long,
			dst:Rail[ElemType]{self!=null}, dstOff:Long, 
			dataCnt:Long): void {
		
		if (here.id() == srcpid) {
			val src = srcplh();
			Rail.copy(src, srcOff, dst, dstOff, dataCnt);
			return;
		}

		assert (dstOff+dataCnt <= dst.size):
            "Receiving array overflow";
		
		x10Copy(srcplh, srcpid, srcOff, dst, dstOff, dataCnt);
	}	

	protected static def x10Copy(
			srcplh:DataArrayPLH, srcpid:Long, srcOff:Long,
			dst:Rail[ElemType]{self!=null}, dstOff:Long, 
			dataCnt:Long): void {
		assert (dstOff+dataCnt) <= dst.size;
        val gr = GlobalRail[ElemType](dst);
        at(Place(srcpid)) {
            val src = srcplh();
            assert (srcOff+dataCnt) <= src.size;
            finish Rail.asyncCopy[ElemType](src, srcOff, gr, dstOff, dataCnt);
        }
	}

	/**
	 * Copy compress array data from here to the specified remote 
	 * place in DistArray.
	 * 
	 * @param src    -- source of compress array at here
	 * @param srcOff -- offset in source 
	 * @param dstplh -- target compress array in DistArray
	 * @param dstpid -- place id of target in DistArray.
	 * @param dstOff -- column offset in target 
	 * @param dataCnt -- count of data to copy from source 
	 */
	public static def copy(
			src:CompressArray, srcOff:Long,
			dstplh:CompArrayPLH, dstpid:Long, dstOff:Long, 
			dataCnt:Long): void {

		if (here.id() == dstpid) {
			val dst = dstplh();
			CompressArray.copy(src, srcOff, dst, dstOff, dataCnt);
			return;
		}

		assert (srcOff+dataCnt <= src.storageSize()) :
            "Sending side storage overflow";
		
		x10Copy(src, srcOff, dstplh, dstpid, dstOff, dataCnt);
	}

	//Sparse matrix remote copy To
	protected static def x10Copy(
			src:CompressArray, srcOff:Long,
			dstplh:CompArrayPLH, dstpid:Long, dstOff:Long, 
			dataCnt:Long): void {

        assert (srcOff+dataCnt) <= src.index.size : "dataCnt overruns src.index";
        assert (srcOff+dataCnt) <= src.value.size : "dataCnt overruns src.value";
		val rmtidx = new GlobalRail[Long    ](src.index);
		val rmtval = new GlobalRail[ElemType](src.value);

		at(Place(dstpid)) {
			//Implicit copy:dstlist, dataCnt, rmtidx, rmtval, srcOff dstOff
			val dst = dstplh();
            assert (dstOff+dataCnt) <= dst.index.size : "dataCnt overruns dst.index";
            assert (dstOff+dataCnt) <= dst.value.size : "dataCnt overruns dst.value";
            finish {
                Rail.asyncCopy[Long    ](rmtidx, srcOff, dst.index, dstOff, dataCnt);
                Rail.asyncCopy[ElemType](rmtval, srcOff, dst.value, dstOff, dataCnt);
            }
		}
	}

	// Remote compress array copy From

	/**
	 * Copy data of compress array from remote place to here
	 * 
	 * @param dstplh  -- source compress array in DistArray
	 * @param srcpid  -- source place id.
	 * @param srcOff  -- offset in source
	 * @param dstspa  -- destination place id
	 * @param dstOff  -- offset in target
	 * @param dataCnt  -- data count to be copied in source matrix
	 */
	public static def copy(
			srcplh:CompArrayPLH, srcpid:Long, srcOff:Long,
			dst:CompressArray, dstOff:Long, 
			dataCnt:Long): void {
		
		if (here.id() == srcpid) {
			CompressArray.copy(srcplh(), srcOff, dst, dstOff, dataCnt);
			return;
		}

		x10Copy(srcplh, srcpid, srcOff, dst, dstOff, dataCnt);
	}
	
    /** Sparse matrix remote copy from */
	protected static def x10Copy(
			srcplh:CompArrayPLH, srcpid:Long, srcOff:Long,
			dst:CompressArray, dstOff:Long, 
			dataCnt:Long): void {

        assert (dstOff+dataCnt) <= dst.index.size : "dataCnt overruns dst.index";
        assert (dstOff+dataCnt) <= dst.value.size : "dataCnt overruns dst.value";
        val rmt = RemotePair(new GlobalRail[Long    ](dst.index),
                             new GlobalRail[ElemType](dst.value));
        at(Place(srcpid)) {
            val src = srcplh();
            finish {
                assert (srcOff+dataCnt) <= src.index.size : "dataCnt overruns src.index";
                assert (srcOff+dataCnt) <= src.value.size : "dataCnt overruns src.value";
                Rail.asyncCopy[Long   ](src.index,  srcOff, rmt.first, dstOff, dataCnt);
                Rail.asyncCopy[ElemType](src.value, srcOff, rmt.second, dstOff, dataCnt);
            }
        }
	}
}
