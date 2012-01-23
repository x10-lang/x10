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

package x10.matrix.sparse;

import x10.io.Console;

import x10.matrix.Debug;
import x10.matrix.MathTool;
import x10.matrix.RandTool;

import x10.util.Pair;
import x10.util.Random;

/**
 * Stores <tt>n</tt> elements, <tt>A(a1)=v1, ..., A(in)=an</tt>,
 * where the indices are not necessarily adjacent, but
 * must be distinct and in increasing order.
 * 
 * <p> The indices <tt>ai</tt> are called <em>surface indices</em>. The entries
 * are stored contiguously in an array called the <em>underlying array</em>.
 * Indices into the underlying array are called <em>underlying indices</em>.
 */

//public static type Rail[T] = Array[T](1){rail};

public class CompressArray {

    //TODO: Use Rail[Pair[Int,Double]] as backing storage.
	//Comments: the underlying storage won't be compatible to conventional
	//sparse matrix format CSC or CSR, because the indexes and values are 
	//in two arrays
	// CHECK: Rail[Pair] uses the continuous memory space, so as to support
	// MPI inter-process communication

    //static type Rail[T] = Array[T](1){rail};

	public var index:Array[Int](1); // the indices i1,..., in; indices must be positive
	public var value:Array[Double](1);//{self.size==index.size,rail}; // the values v1,..., vn

	
	protected var count:Int=0; // n
	public def count()=count;

	//===========================================================
	// Constructor
	//===========================================================
	public def this(idxlst:Array[Int](1){rail}, 
					vallst:Array[Double](1){self.size==idxlst.size,rail},
					cnt:Int) { 
		this.index = idxlst;// as Rail[Int]{rail};
		this.value = vallst;// as Array[Double](1){self.size==index.size,rail};
		count = cnt;
		//for (var i:Int=0; i<index.size&&index(i)>=0; i++) count++;
	}

	/**
	 * Create an empty compressed array with space for s entries.
	 */
	public def this(s:Int) { 
		Debug.assure(s>=0);
		index = new Array[Int](s, -1) as Array[Int](1){rail};
		value = new Array[Double](s, 0.0) as Array[Double](1){rail};
		count = 0;
	}


	//===============================================
	// Constructor with memory allocation
	//===============================================
	/**
	 * Make a compressed array.
	 *
	 * @param sz     Number of entries or underlying indices.
	 */
	public static def make(sz:Int) = new CompressArray(sz); 

	//===============================================
	// Initialization
	//===============================================
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
	protected def init(offset:Int, 
					   maxIndex:Int, 
					   init:(ci:Int,rg:Random)=>Double, 
					   nzd:Double):Int {
	    val size = storageSize();
		if (offset >= size) 
		    return 0; 
		val rg = RandTool.getRandGen();  
		var ci:Int= offset;
		for (var i:Int=0; i<maxIndex; i++) {
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
	public def initConstValue(offset:Int, 
							  maxIndex:Int, 
							  v:Double, 
							  nzd:Double) : Int =
		init(offset, maxIndex, (ci:Int, rg:Random)=>(v), nzd);

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
	public def init(offset:Int, maxIndex:Int, 
			fidx:(Int)=>Int, 
			fval:(Int)=>Double):Int {
		var nzidx:Int=0;
		var stidx:Int=offset;
		var stval:Double=0;
		for (var i:Int=0; i<maxIndex&&stidx<index.size; i++) {
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
	public def init(offset:Int, maxIndex:Int, f:(Int)=>Double):Int {
	
		var nzidx:Int=0;
		var stidx:Int=offset;
		var stval:Double=0;
		for (var i:Int=0; i<maxIndex&&stidx<index.size; i++, stidx++) {
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
	public def initRandom(offset:Int, 
						  maxIndex:Int, 
						  nzd:Double): Int =
	    init(offset, maxIndex, (ci:Int, rg:Random)=>rg.nextDouble(), nzd);

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
	public def initRandomFast(offset:Int, maxIndex:Int, nzd:Double, 
							lb:Int, ub:Int): Int { 
		val sts = storageSize();
		val cnt = maxIndex*nzd > sts ? sts: ((maxIndex*nzd) as Int);
	    
		if (offset >=  sts) return 0;

		//val rg = RandTool.getRandGen();
		var ci:Int= offset;   
		val avgDst:Double = 1.0/nzd;    
		val dstMax:Double = 2.0*avgDst - 1; 
		
		var i:Int = 0;
		var nextDst:Int=avgDst as Int;
		// Set the starting posistion, taking half of avg distance
		if (avgDst > 1.0) {
			//nextDst = RandTool.nextNormalRandDst(avgDst)/2; 
			// Generate nonzero indexes
			while (ci < sts) {
				nextDst = (RandTool.nextDouble() * dstMax) as Int; 
				i += nextDst; 
				if (i >= maxIndex) break;
				this.index(ci) = i;
				ci ++; i++;
				//nextDst = RandTool.nextUniRandDst(dstMax);
			}
		} else {
			//Special case for full dense matrix
			while (ci < sts && i< maxIndex) {
				this.index(ci) = i;
				i++; ci++;
			}
		}

		val len:Int = ub-lb+1;
		if (len <= 1) {
			for (var vi:Int=offset ; vi < ci; vi++) {
				this.value(vi) = RandTool.nextDouble();
			}
		} else {
			for (var vi:Int=offset ; vi < ci; vi++) {
				this.value(vi) = RandTool.nextInt(len) + lb;
			}
		}
		//Console.OUT.print("."); Console.OUT.flush();
	    val c:Int = ci-offset;
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
	public def initRandomFast(offset:Int, maxIndex:Int, nzd:Double) =
		initRandomFast(offset, maxIndex, nzd, 0, -1);

	/**
	 * Make a copy of myself
	 */
	public def clone():CompressArray {
		val idxlist = new Array[Int](this.index) as Array[Int](1){rail};
		val vallist = new Array[Double](this.value) as Array[Double](1){self.size==idxlist.size,rail};
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
	protected def incStorage(incsz:Int): void {
		
		val nsts:Int = storageSize() + incsz;
		if (nsts > Int.MAX_VALUE) Debug.exit("Memory overflow");

		// Allocate new storage
		val newidx = new Array[Int](nsts);// (i:Int)=>(i<sts)?i<index(i):-1);
		val newval = new Array[Double](nsts);// (i:Int)=>(i<sts)?value(i):0.0);
		// Copy data
		Array.copy[Int   ](index, 0, newidx, 0, count);
		Array.copy[Double](value, 0, newval, 0, count);
		// Replace index and value arrays
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
	public def testIncStorage(off:Int, cnt:Int): Boolean {
		var retval:Boolean=false;
		val cursz = storageSize();
		val chksz = off + cnt;
		if (chksz > cursz) {
			Debug.flushln("Test storage size fail! Compress data offset:"+off+" add cnt:"+cnt+
							" storage size:"+cursz+" Re-allocation increase to "+chksz);
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
	public def testIncStorage(cnt:Int) = testIncStorage(this.count(), cnt);

	//=======================================

	//=======================================================
	/**
	 * Update the compressed array. It is the caller's responsibility
	 * to ensure that the v.first >=0, and after this update the 
	 * compressed array satisfies its invariants.
	 * @param pos: The index of the entry in the underlying array to be updated.
	 * @param v: The (surface index, value) pair to update the entry, 
	 */
	public operator this(pos:Int)=(v:Pair[Int,Double]):void {
		Debug.assure(pos < this.storageSize(), 
					 "CompressArray prealloc "+
					 this.storageSize()+" memory overflow ");
		//if (index(pos) < 0) count++;
		index(pos) = v.first;
		value(pos) = v.second;
	}	

	/**
	 * Return the surface index associated with the index i in the 
	 * underlying array. 
	 */
	public def getIndex(i:Int) = index(i);
	/**
	 * Return the value associated with the undex i in the underlying
	 * array.
	 */
	public def getValue(i:Int) = value(i);

	/**
	 * Return the value at surface index i.
	 */
	public operator this(i:Int) : Double = find(i);

	/**
	 * The number of entries allocated for this compressed array. 
	 * May be nonzero even if the compressed array has zero elements.
	 * storageSize() must be less than Int.MAX_VALUE.
	 */
	public def storageSize() :Int = this.index.size;

	/**
	 * Search between start and end for the index i that is mapped
	 * to the surface index idx.  If such an index does not exist
	 * then return the index in the backing array for a value within 
	 * the range that is no smaller than idx.
	 */
	public def find(idx:Int, start:Int, end:Int):Int { 

		if (this.count==0) return 0;

		var min:Int = start; 
		var max:Int = end; 

		if (min>=max) return min;
		var mid:Int = min;
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
	public def find(idx:Int): Double {
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
	public def copyRange(srcOffset:Int, length:Int, dstOffset:Int,
						 base:Int, dest:CompressArray) : void {
		if (length <= 0) return;
		var dpos:Int = dstOffset;

		//Size test should be performed before calling this method for better performance
		//This is safe-guard for those functions which do not perform size test.
		testIncStorage(dstOffset, length);
		for (var spos:Int=srcOffset; spos<srcOffset+length; spos++, dpos++){
		    dest(dpos) = Pair(getIndex(spos)-base, getValue(spos));
		}
		dest.count +=length;
	}


	//---------------------------------------------------------------------------
	// Copy data between compress array
	//---------------------------------------------------------------------------
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
	public static def copy(src:CompressArray, var srcoff:Int,
						   dst:CompressArray, var dstoff:Int, len:Int, idxchg:Int) : void {
		if (len <= 0) return;
		val srcend = srcoff + len;
		//Size test should be performed before calling this method for better performance
		dst.testIncStorage(dstoff, len);

		for (;srcoff < srcend; srcoff++, dstoff++){
		    dst(dstoff) = Pair(src.getIndex(srcoff)-idxchg, src.getValue(srcoff));
		}
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
	public static def copy(src:CompressArray, var srcoff:Int, 
						   dst:CompressArray, var dstoff:Int, len:Int) : void {
		if (len <= 0) return;
		//Size test should be performed ahead of copying for better performance
		dst.testIncStorage(dstoff, len);

		val srcend = srcoff + len;
		for (;srcoff < srcend; srcoff++, dstoff++){
		    dst(dstoff) = Pair(src.getIndex(srcoff), src.getValue(srcoff));
		}
		dst.count += len;
	}

	//--------------------------------------------------------------------
	//

	/**
	 *  Return the number of nonzero (nz) entries in the given array. 
	 * An entry v is nz if Math.testZero(v) succeeds.
	 */
	public static def countNonZero(d:Array[Double](1)):Int {
		var nzcnt:Int = 0;
		for (var i:Int=0; i<d.size; i++) {
			if (!MathTool.isZero(d(i))) nzcnt++;
		}
		return nzcnt;
	}
	
	/**
	 * Return a new compressed array containing all the 
	 * nonzero entries in d. 
	 */
	public static def compress(d:Array[Double](1)):CompressArray {
		val nzc = CompressArray.countNonZero(d);
		val out = new CompressArray(nzc);
		var pos:Int = 0;
		val size = d.size;
		for (var i:Int=0; i<size; i++) {
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
	public def compressAt(destOffset:Int, s:Array[Double](1)):Int { 
		var destI:Int = destOffset;
		val size = this.storageSize();
		val sSize = s.size;
		for (var i:Int=0; i < sSize && destI < size; i++) {
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
	public def extract(srcOffset:Int, count:Int, 
					   dstOffset:Int, dest:Array[Double](1)):void {
		if (count <=0 || this.count ==0) return;
		Debug.assure(srcOffset+count-1  < this.count, 
					 "extract compress array out of range");
		val destSize = dest.size;
		var preidx:Int = dstOffset;
		var dstidx:Int = 0;

		for (var i:Int=srcOffset; i<srcOffset+count; i++) {
			if (index(i) < 0) break;

			dstidx = dstOffset+index(i);

			//Reset target array between two adjacent nonzeros 
			for (var j:Int=preidx; j<dstidx && i<destSize; j++) dest(j) = 0.0;
			
			if (dstidx < destSize) dest(dstidx) = value(i);
			preidx = dstidx+1;
		}
	}

	/**
	 * Extract all data in this into dest.
	 */
	public def extract(dest:Array[Double](1)) {
		extract(0, this.count, 0, dest);
	}

	/**
	 * Allocate ls entries from current array and return a new Rail
	 * containing these netries. 
	 */
	public def extract(ls:Int):Array[Double](1) {
		val dst = new Array[Double](ls);
		extract(dst);
		return dst;
	} 


	//=========================================================
	// Util methods
	//=========================================================
	public def toString():String {
		var outstr:String="Compressd Array (" + this.storageSize() 
							   + ") NZ "+count+"  [";
		for (var i:Int=0; i<this.count; i++) {
			outstr += " "+getIndex(i)+":"+getValue(i)+" ";
		}
		outstr += " ]";
		return outstr;
	}

	public def print(msg:String) {
		val ostr:String = msg +	"\n"+toString();
		Console.OUT.println(ostr);
		Console.OUT.flush();
	}
	public def print() { print(""); }

	public def debugPrint(msg:String) {
		if (Debug.disable) return;
		val output:String= msg+toString();
		Debug.println(output);
	}
	public def debugPrint() { debugPrint("");}

	public def equals(cl:CompressArray):Boolean {
		if (this.count != cl.count) 
			return false;
		//
		for (var i:Int=0; i<this.count; i++) {
			if ((this.getIndex(i) != cl.getIndex(i)) ||
				(this.getValue(i) != cl.getValue(i)))
				return false;
		}
		return true;
	}

	public def equals(al:Array[Double](1)):Boolean {
		var pos:Int = 0;
		var idx:Int = (this.count==0)?-1:this.getIndex(pos);
		for (var i:Int=0; i<al.size; i++) {
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
	//------------------

}