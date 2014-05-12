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

package x10.matrix.sparse;


import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.util.RandTool;

import x10.util.Pair;
import x10.util.Random;
import x10.util.StringBuilder;

/**
 * Stores <tt>n</tt> elements, <tt>A(a1)=v1, ..., A(in)=an</tt>,
 * where the indices are not necessarily adjacent, but
 * must be distinct and in increasing order.
 * 
 * <p> The indices <tt>ai</tt> are called <em>surface indices</em>. The entries
 * are stored contiguously in an array called the <em>underlying array</em>.
 * Indices into the underlying array are called <em>underlying indices</em>.
 */

public class CompressArray {
    //TODO: Use Rail[Pair[Long,Double]] as backing storage.
	//Comments: the underlying storage won't be compatible to conventional
	//sparse matrix format CSC or CSR, because the indexes and values are 
	//in two arrays
	// CHECK: Rail[Pair] uses the continuous memory space, so as to support
	// MPI inter-process communication

	public var index:Rail[Long]; // the indices i1,..., in; indices must be positive
	public var value:Rail[Double];//{self.size==index.size}; // the values v1,..., vn

	public var count:Long=0; // n
	public def count()=count;


	// Constructor

	public def this(idxlst:Rail[Long], 
					vallst:Rail[Double]{self.size==idxlst.size},
					cnt:Long) { 
		this.index = idxlst;
		this.value = vallst;
		count = cnt;
		//for (var i:Long=0; i<index.size&&index(i)>=0; i++) count++;
	}

	/**
	 * Create an empty compressed array with space for s entries.
	 */
	public def this(s:Long) { 
		Debug.assure(s>=0);
		index = new Rail[Long](s, -1) as Rail[Long];
		value = new Rail[Double](s) as Rail[Double];
		count = 0;
	}

	// Constructor with memory allocation

	/**
	 * Make a compressed array.
	 *
	 * @param sz     Number of entries or underlying indices.
	 */
	public static def make(sz:Long) = new CompressArray(sz); 


	/**
	 * Compress array data initialization. Starting from offset in the 
	 * underlying array, set data in the compress array upto the last underlying 
	 * indices with indices.  The surface indices values are between 0 and 
	 * maxIndex-1.  Each element of the matrix is tested.  An entry is added only if 
	 * a generated random number is below nzd. init is called
	 * with the index of the underlying array and a random number
	 * generator. The resulting value is used to initialize the entry.
	 *
	 * @param offset          Offset for the beginning index of the array
	 * @param maxIndex        The maximum surface index created.
	 * @param init(ci,rg)     The data initialization function, mapping ci to 
	 *                       a value using random rg.
	 * @param nzd             Percentage of nonzero entries or sparsity
	 * @return                number of non-zero added 
	 */
	protected def init(offset:Long, maxIndex:Long, init:(ci:Long,rg:Random)=>Double, nzd:Double) =
		init(offset, 0, maxIndex, init, nzd);

	protected def init(offset:Long, sttIndex:Long, maxIndex:Long, init:(ci:Long,rg:Random)=>Double, nzd:Double):Long {
		val size = storageSize();
		if (offset >= size)	return 0; 
		val rg = RandTool.getRandGen();  
		var ci:Long= offset;
		for (var i:Long=sttIndex; i<maxIndex; i++) {
			if (rg.nextDouble() < nzd) {
				this(ci)=Pair(i,init(ci,rg));
				ci++;
				if (ci == size)
					break;
			}
		}
		val c = ci - offset;
		count += c;
		return c;
	}
	
	/**
	 * Initialize compress array with a constant values.  This method is used
	 * for testing purpose.
	 * Distance between two adjacent surface indices is randomly created
	 * in normal distribution
	 * 
	 * @param offset         the starting index in the storage.
	 * @param maxIndex       the maximum surface index
	 * @param nzd            nonzero density
	 * @param return         number of nonzeros added
	 */
	public def initConstValue(offset:Long, maxIndex:Long, v:Double, nzd:Double):Long =
		init(offset, maxIndex, (ci:Long, rg:Random)=>(v), nzd);
	public def initConstValue(offset:Long, sttIndex:Long, maxIndex:Long, v:Double,	nzd:Double):Long =
		init(offset, sttIndex, (ci:Long, rg:Random)=>(v), nzd);
	
	/**
	 * Initialize compress array using nonzero indexing generating function and value
	 * generating function.
	 * 
	 * @param offset        the starting position in the storage
	 * @param maxIndex      the maximum surface index
	 * @param fidx          the indexing function, given [0..maxIndex) range. Must be ascending. 
	 * @param fval          nonzero value generating function given [0..maxIndex) range If zero, ignored.                     
	 * @return number of nonzero values
	 */
	public def init(offset:Long, maxIndex:Long, fidx:(Long)=>Long, fval:(Long)=>Double):Long =
		init(offset, 0, maxIndex, fidx, fval);

	public def init(offset:Long, sttIndex:Long, maxIndex:Long, fidx:(Long)=>Long, fval:(Long)=>Double):Long {
		var nzidx:Long=0;
		var stidx:Long=offset;
		var stval:Double=0;
		for (var i:Long=sttIndex; i<maxIndex&&stidx<index.size; i++) {
			nzidx = fidx(i);
			if (nzidx >= maxIndex) break;
			stval = fval(nzidx);
			if (! MathTool.isZero(stval)) {
				this.index(stidx)=nzidx;
				this.value(stidx)=stval;
				stidx++;
			}
		}
		count +=stidx-offset;
		return stidx-offset;
	}

    /**
	 * Initialialize compress array using given initial function.
	 * 
	 * @param offset       the starting position in the storage
	 * @param maxIndex     the maximum surface index
	 * @param f            value generating function, given [0..maxIndex) range.
	 */
	public def init(offset:Long, maxIndex:Long, f:(Long)=>Double):Long =
		init(offset, 0, maxIndex, f);
	
	public def init(offset:Long, sttIndex:Long, maxIndex:Long, f:(Long)=>Double):Long {
	
		var nzidx:Long=0;
		var stidx:Long=offset;
		var stval:Double=0;
		for (var i:Long=sttIndex; i<maxIndex&&stidx<index.size; i++) {
			stval = f(i);
			if (! MathTool.isZero(stval)) {
				this.index(stidx)=i;
				this.value(stidx)=stval;
				stidx++;
			}
		}
		count += stidx-offset;
		return stidx-offset;
	}

	/**
	 * Initialize compress array with random values.  This method is used
	 * for testing and performancing benchmark.
	 * Distance between two adjacent surface indices is randomly created
	 * in normal distribution
	 * 
	 * @param offset         the starting index in the storage.
	 * @param maxIndex       the maximum surface index
	 * @param nzd            nonzero density
	 * @param return         number of non-zeros added 
	 */
	public def initRandom(offset:Long, maxIndex:Long, nzd:Double):Long =
	    init(offset, 0, maxIndex, (ci:Long, rg:Random)=>rg.nextDouble(), nzd);

	public def initRandom(offset:Long, sttIndex:Long, maxIndex:Long, nzd:Double):Long =
		init(offset, sttIndex, maxIndex, (ci:Long, rg:Random)=>rg.nextDouble(), nzd);

	/**
	 * Fast initialize method. Distance between two adjacent surface indices is randomly created
	 * in uniform distribution.  This method is used for testing and performance
	 * benchmark.
	 *
	 * @param offset   	-- Offset of starting position
	 * @param maxIndex 	-- Maximum index for all entries
	 * @param nzd      	-- Nonzero percentage for each entry, must >= 1.0
	 * @param lb		-- lower random value bound
	 * @param ub		-- upper random value bound
	 * @param return   	-- number of nonzeros added
	 */
	public def initRandomFast(offset:Long, maxIndex:Long, nzd:Double, lb:Long, ub:Long):Long =
		initRandomFast(offset, 0, maxIndex, nzd, lb, ub);
	
	public def initRandomFast(offset:Long, sttIndex:Long, maxIndex:Long, nzd:Double, lb:Long, ub:Long):Long { 
		val sts = storageSize();
		//val cnt = maxIndex*nzd > sts ? sts: ((maxIndex*nzd) as Long); 
		if (offset >=  sts) return 0;

		//val rg = RandTool.getRandGen();
		var ci:Long= offset;   
		val avgDst:Double = 1.0/nzd;
		val dstMax:Double = 2.0*avgDst - 1; 
		
		var i:Long = sttIndex;
		var nextDst:Long;
		// Set the starting posistion, taking half of avg distance
		if (avgDst > 1.0) {
			i += RandTool.nextDouble()* avgDst/2; 
			// Generate nonzero indexes
			while (ci < sts) {
				if (i >= maxIndex) break;
				this.index(ci) = i;
				ci ++;
				nextDst = (RandTool.nextDouble() * dstMax) as Long; 
				i += nextDst + 1; 
			}
		} else {
			//Special case for full dense matrix
			while (ci < sts && i< maxIndex) {
				this.index(ci) = i;
				i++; ci++;
			}
		}

		val len:Long = ub-lb+1;
		if (len <= 1) {
			for (var vi:Long=offset; vi < ci; vi++) {
				this.value(vi) = RandTool.nextDouble();
			}
		} else {
			for (var vi:Long=offset; vi < ci; vi++) {
				this.value(vi) = RandTool.nextLong(len) + lb;
			}
		}
	    val c:Long = ci-offset;
		count += c;  //Increase the total nonzero CompressArray
		return c;
	}
	/**
	 * Fast initialize method. Distance between two adjacent surface indices is randomly created
	 * in uniform distribution.  This method is used for testing and performance
	 * benchmark.
	 * 
	 * @param offset   	-- Offset of starting position
	 * @param maxIndex 	-- Maximum index for all entries
	 * @param nzd      	-- Nonzero percentage for each entry, must >= 1.0
	 * @param return   	-- number of nonzeros added
	 */
	public def initRandomFast(offset:Long, maxIndex:Long, nzd:Double) =
		initRandomFast(offset, 0, maxIndex, nzd, 0, -1);

	/**
	 * Make a copy of myself
	 */
	public def clone():CompressArray {
		val idxlist = new Rail[Long](this.index) as Rail[Long];
		val vallist = new Rail[Double](this.value) as Rail[Double]{self.size==idxlist.size};
		val ca = new CompressArray(idxlist, vallist, this.count);
		//ca.count = this.count;
		return ca;
	}

	/**
	 * Reset all indices
	 */
	public def reset():void {
		count = 0;
	}

	/**
	 * Increase storage size by reallocating memory space
	 */
	protected def incStorage(incsz:Long): void {
		
		val nsts:Long = storageSize() + incsz;
		if (nsts > Long.MAX_VALUE) Debug.exit("Memory overflow");

		// Allocate new storage
		val newidx = new Rail[Long](nsts);// (i:Long)=>(i<sts)?i<index(i):-1);
		val newval = new Rail[Double](nsts);// (i:Long)=>(i<sts)?value(i):0.0);
		// Copy data
		Rail.copy[Long](index, 0, newidx, 0, count);
		Rail.copy[Double](value, 0, newval, 0, count);
		// Replace index and value Rails
		index = newidx;
		value = newval;
	}

	/**
	 * Test and increase storage size 
	 *
	 * @param off     starting offset for new data in storage.
	 * @param cnt     number of elements will be added to storage
	 * @return     return true if storage is re-allocated
	 */
	public def testIncStorage(off:Long, cnt:Long): Boolean {
		var retval:Boolean=false;
		val cursz = storageSize();
		val chksz = off + cnt;
		if (chksz > cursz) {
			//Debug.flushln("Test storage size fail! Compress data offset:"+off+" add cnt:"+cnt+
			//				" storage size:"+cursz+" Re-allocation increase to "+chksz);
			retval = true;
			incStorage(chksz - cursz);
		}	
		return retval;
	}

	/**
	 * Test and increase storage size 
	 *
	 * @param cnt     number of elements will be added to storage
	 * @return     return true if storage is re-allocated
	 */
	public def testIncStorage(cnt:Long) = testIncStorage(this.count(), cnt);

	/**
	 * Update the compressed array. It is the caller's responsibility
	 * to ensure that the v.first >=0, and after this update the 
	 * compressed array satisfies its invariants.
	 * @param pos: The index of the entry in the underlying array to be updated.
	 * @param v: The (surface index, value) pair to update the entry, 
	 */
	public operator this(pos:Long)=(v:Pair[Long,Double]):void {
		Debug.assure(pos < this.storageSize(), 
					 "CompressArray prealloc "+
					 this.storageSize()+" memory overflow ");
		//if (index(pos) < 0) count++;
		index(pos) = v.first;
		value(pos) = v.second;
	}	

	/**
	 * Return the surface index associated with the index i in the 
	 * underlying Rail. 
	 */
	public def getIndex(i:Long) = index(i);
	/**
	 * Return the value associated with index i in the underlying Rail.
	 */
	public def getValue(i:Long) = value(i);

	/**
	 * Return the value at surface index i.
	 */
	public operator this(i:Long) : Double = find(i);

	/**
	 * The number of entries allocated for this compressed array. 
	 * May be nonzero even if the compressed array has zero elements.
	 * storageSize() must be less than Long.MAX_VALUE.
	 */
	public def storageSize():Long = this.index.size;

	/**
	 * Search between start and end for the index i that is mapped
	 * to the surface index idx.  If such an index does not exist
	 * then return the index in the backing Rail for a value within 
	 * the range that is no smaller than idx.
	 */
	public def find(idx:Long, start:Long, end:Long):Long { 

		if (this.count==0L) return 0;

		var min:Long = start; 
		var max:Long = end; 

		if (min>=max) return min;
		var mid:Long = min;
		do {
			mid = min + (max - min) / 2;
			if (index(mid) < idx) {
				min = mid + 1;
			} else {
				max = mid - 1; 
			}
			if (index(mid) == idx) return mid;			
			
		} while ( min <= max );
		if (min > end) min = end; 
		return min;
	}

	/**
	 * Return the value associated with index idx for this array,
	 * and 0.0 if the array does not have a value for idx.
	 */
	public def find(idx:Long): Double {
		val fidx = find(idx, 0, this.count-1);
		if (index(fidx) == idx) return value(fidx);
		return 0.0D;
	}

	/** obsolete
	 * Copy length entries from the compressed array, starting from srcOffset
	 * into dest starting at destOffset. The surface index of the entry in the 
	 * destination compressed array is less than the surface index of
	 * the entry in the source array.
	 */
	public def copyRange(srcOffset:Long, length:Long, dstOffset:Long,
						 base:Long, dest:CompressArray) : void {
		if (length <= 0) return;
		var dpos:Long = dstOffset;

		//Size test should be performed before calling this method for better performance
		//This is safe-guard for those functions which do not perform size test.
		testIncStorage(dstOffset, length);
		for (var spos:Long=srcOffset; spos<srcOffset+length; spos++, dpos++){
		    dest(dpos) = Pair(getIndex(spos)-base, getValue(spos));
		}
		dest.count +=length;
	}

	// Copy data between compress array

	/**
	 * Copy specified range of compressed array from source to target.  The target compress
	 * array allows to specify the modification for its index values, allowing target compress array
s	 * has different original size (uncompress data array size) from the source.
	 * 
	 * @param src        The source compress array
	 * @param srcoff     The starting position of source compress array
	 * @param dst        The target compress array
	 * @param dstoff     The starting position of target compress array
	 * @param len        The number of elements (index-value pair) to be copied 
	 * @param idxchg     Modification on the index values in the target compress array
	 */
	public static def copy(src:CompressArray, var srcoff:Long,
						   dst:CompressArray, var dstoff:Long, len:Long, idxchg:Long) : void {
		if (len <= 0) return;
		//val srcend = srcoff + len;
		//Size test should be performed before calling this method for better performance
		dst.testIncStorage(dstoff, len);

		//for (;srcoff < srcend; srcoff++, dstoff++){
		//    dst(dstoff) = Pair(src.getIndex(srcoff)-idxchg, src.getValue(srcoff));
		//}
		Rail.copy[Long](src.index, srcoff, dst.index, dstoff, len);
		if (idxchg!=0L) {
			for (var i:Long=dstoff; i<dstoff+len; i++)	dst.index(i) -= idxchg;
		}
		Rail.copy[Double](src.value, srcoff, dst.value, dstoff, len);
		dst.count += len;
	}

	/**
	 * Copy specified range of compress array from source to target. No index modification.
	 * If destination storage limit is reached, a new storage will be
	 * allocated and previous data will be copied to the new storage.
	 *
	 * @param src        The source compress array
	 * @param srcoff     The starting position of source compress array
	 * @param dst        The target compress array
	 * @param dstoff     The starting position of target compress array
	 * @param len        The number of elements (index-value pair) to be copied 
	 */
	public static def copy(src:CompressArray, var srcoff:Long, 
						   dst:CompressArray, var dstoff:Long, len:Long) : void {
		if (len <= 0) return;
		//Size test should be performed before calling this method for better performance
		dst.testIncStorage(dstoff, len);

		Rail.copy[Long](src.index, srcoff, dst.index, dstoff, len);
		Rail.copy[Double](src.value, srcoff, dst.value, dstoff, len);
		dst.count += len;	
	}




	/**
	 *  Return the number of nonzero (nz) entries in the given array. 
	 * An entry v is nz if Math.testZero(v) succeeds.
	 */
	public static def countNonZero(d:Rail[Double]):Long {
		var nzcnt:Long = 0;
		for (var i:Long=0; i<d.size; i++) {
			if (!MathTool.isZero(d(i))) nzcnt++;
		}
		return nzcnt;
	}
	
	/**
	 * Return a new compressed array containing all the 
	 * nonzero entries in d. 
	 */
	public static def compress(d:Rail[Double]):CompressArray {
		val nzc = CompressArray.countNonZero(d);
		val out = new CompressArray(nzc);
		var pos:Long = 0;
		val size = d.size;
		for (var i:Long=0; i<size; i++) {
			if (!MathTool.isZero(d(i))) {
			    out(pos) = Pair(i,d(i));
				pos++;
			}
		}
		out.count = pos;
		return out;
	}
	/**
	 * Transfer as many nz entries from d into this 
	 * the as can fit into this. The entries are transferred
	 * into the destination starting from offset.
	 * Note: For the invariant of the dest compressed array to 
	 * be satisfied, the compressed array should be empty, since
	 * the index of the first element transferred is going to be zero.,
	 * 
	 * @param offset     the index of the first entry in dest into which to copy
	 * @return     number of nz entries transferred
	 */
	public def compressAt(destOffset:Long, s:Rail[Double]):Long { 
		var destI:Long = destOffset;
		val size = this.storageSize();
		val sSize = s.size;
		for (var i:Long=0; i < sSize && destI < size; i++) {
		    if (!MathTool.isZero(s(i))) {
		        this(destI)=Pair(i, s(i));
		        destI++;
		    }
		}
		val c = destI - destOffset;
		this.count += c;
		return c;
	}


	/**
	 * Extract data from this compressed array and transfer into dest.
	 * Start from the srcOffset index in the underlying array, and 
	 * transfer at most count entries. In the destination, copy these 
	 * entries after destOffset. All data in destination array will be overwritten.
	 *
	 * @param srcOffset     starting offset of source compress array 
	 * @param count         number of nonzero to extract from compress array
	 * @param dstOffset     starting offset for target array to store the extracted data
	 * @param dest          target array
	 */
	public def extract(srcOffset:Long, count:Long, 
					   dstOffset:Long, dest:Rail[Double]):void {
		if (count <=0L || this.count ==0L) return;
		Debug.assure(srcOffset+count-1  < this.count, 
					 "extract compress array out of range");
		val destSize = dest.size;
		var preidx:Long = dstOffset;
		var dstidx:Long = 0;

		for (var i:Long=srcOffset; i<srcOffset+count; i++) {
			if (index(i) < 0) break;

			dstidx = dstOffset+index(i);

			//Reset target array between two adjacent nonzeros 
			for (var j:Long=preidx; j<dstidx && i<destSize; j++) dest(j) = 0.0;
			
			if (dstidx < destSize) dest(dstidx) = value(i);
			preidx = dstidx+1;
		}
	}

	/**
	 * Extract all data in this into dest.
	 */
	public def extract(dest:Rail[Double]) {
		extract(0, this.count, 0, dest);
	}

	/**
	 * Allocate ls entries from current array and return a new Rail
	 * containing these netries. 
	 */
	public def extract(ls:Long):Rail[Double] {
		val dst = new Rail[Double](ls);
		extract(dst);
		return dst;
	} 



	// Util methods

	public def toString():String {
		val outstr = new StringBuilder();
		outstr.add("Compressed Array (" + this.storageSize()+ ") NZ "+count+"  [");
		for (var i:Long=0; i<this.count; i++) {
			outstr.add(" "+getIndex(i)+":"+getValue(i)+" ");
		}
		outstr.add(" ]");
		return outstr.toString();
	}

	public def equals(cl:CompressArray):Boolean {
		if (this.count != cl.count) 
			return false;
		//
		for (var i:Long=0; i<this.count; i++) {
			if ((this.getIndex(i) != cl.getIndex(i)) ||
				(this.getValue(i) != cl.getValue(i)))
				return false;
		}
		return true;
	}

	public def equals(al:Rail[Double]):Boolean {
		var pos:Long = 0;
		var idx:Long = (this.count==0L)?-1L:this.getIndex(pos);
		for (var i:Long=0; i<al.size; i++) {
			if (idx == i) {
				if (MathTool.equals(getValue(pos), al(i))) {
					if (pos < this.count-1) {
						pos ++;
						idx = index(pos);
					}
					continue;
				} else {
					return false;
				}
			} else if (MathTool.isZero(al(i))) {
				continue;
			}
			return false;
		}
		return true;
	}
}
