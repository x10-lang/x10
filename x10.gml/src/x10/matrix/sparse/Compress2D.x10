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
import x10.util.Pair;
import x10.util.StringBuilder;
import x10.matrix.Debug;

/**
 * This class provides abstraction of 2-dimension compressed array based on array
 * of 1-dimensional compress array.
 * 
 */

public class Compress2D {
	//public static type Rail[T] = Array[T](1){rail};

	public val cLine:Array[Compress1D](1);

	/**
	 * Create a Compress2D with the given data.
	 *
	 * @param  cl      Array of compress1D
	 */
	public def this(cl:Array[Compress1D](1)) {
		cLine = cl;
	}
	/**
	 * Create a Compress2D array with n lines, all lines
	 * sharing the same compressed array. The starting and ending index
	 * in the compress array are not set.
	 *
	 * @param n     Number of compressed lines.
	 * @param ca     The shared data storage of compressed array.
	 */
	public static def make(n:Int, ca:CompressArray): Compress2D {    
	    return new Compress2D(new Array[Compress1D](n,
					(Int)=>new Compress1D(0,0,ca)));
	}

	/**
	 * Create a new Compress2D with n lines with storage to hold the specified 
	 * maximum number of nonzeros.
	 *
	 * @param  n      number of compress1D 
	 * @param  maxCount     the total number of non-zeros 
	 */
	public static def make(n:Int, maxCount:Int): Compress2D {
		val ca = new CompressArray(maxCount);
		return Compress2D.make(n, ca);
	}

	//--------
	// Not define memory space to hold data
	// Can be used to store discrete data
	// TODO: Figure out what this is doing.
	static def makeDisjoint(n:Int): Compress2D {
		val cl  = new Array[Compress1D](n);
		return new Compress2D(cl);
	}

	/**
	 * Create compress2D using ia, ja, anv aval.
	 *
	 * @param ia     array of sizes in compress lines
	 * @param ja     array of index array for nonzeros
	 * @param aval     array of matrix nonzero values.
	 */
	public static def make(ia:Array[Int](1),     //Compress line sizes
						   ja:Array[Int](1),     //Index array
						   aval:Array[Double](1){self.size==ja.size} //Data array
						   ):Compress2D {
		val len = ia.size-1;
		val cpd = new Array[Compress1D](len);
		val ca  = new CompressArray(ja as Array[Int](1){rail}, 
									aval as Array[Double](1){rail}, ia(len));

		var pos:Int = 0;
		var offset:Int=0;
		for (var i:Int=0; i<len; i++) {
			offset = ia(i);
			val sln = ia(i+1) - ia(i);
			cpd(i) = new Compress1D(offset, sln, ca);
		}
		return new Compress2D(cpd);
    }

	/**
	 * Create a 2D compressed array given the list of compressed line sizes.
	 * @param ca     The storage
	 * @param start     The starting index in lineSize
	 * @param count     Number of lines
	 * @param lineSize     Array of sizes of lines.
	 */
	public static def make(ca:CompressArray, start:Int, count:Int,
						   lineSize:Array[Int](1)):Compress2D {
		Debug.assure(start+count <= lineSize.size);
		val cd = new Array[Compress1D](count);
		var sourceLine:Int = start;
		var offset:Int = 0;
		for (var i:Int=0; i<count; i++, sourceLine++) {
			cd(i) = new Compress1D(offset, lineSize(sourceLine), ca);
			offset += lineSize(sourceLine);
		}
		return new Compress2D(cd);
	}

	//----------------------------------------------------------
	// Initialization
	//----------------------------------------------------------
	/**
	 * Initial compress2D with a constant value, but surface indices
	 * are generated randomly.  This method is for testing purpose
	 *
	 * @param ldm     Leading dimension of compress lines or the original size
	 * @param v       The value for all entries
	 * @param nzp     Nonzero sparsity
	 * @return        number of nonzero count
	 */
	public def initConst(ldm:Int, v:Double, nzp:Double):Int {
		var offset:Int=0;
		val ca = getStorage();
		for (var l:Int=0; l<cLine.size; l++) {
			cLine(l).initConst(ldm, v, nzp, offset, ca);
			offset += cLine(l).length;
		}
		return offset;
	}
	/**
	 * Initialize Compress2D with random values.
	 *
	 * @param ldm     Leading dimension of compress lines or the original size
	 * @param nzp     Nonzero sparsity 
	 * @return        number of nonzero count;
	 */
	public def initRandom(ldm:Int, nzp:Double): Int {
		var offset:Int=0;
		val ca = getStorage();
		for (var l:Int=0; l<cLine.size; l++) {
			cLine(l).initRandom(ldm, nzp, offset, ca);
			offset += cLine(l).length;
		}
		return offset;
	}
	
	
	
	/**
	 * Initialize with random values 
	 *
	 * @param ldm     The leading dimension or the compress line original size
	 * @param nzp     Nonzero sparsity
	 * @param lb      lower bound of random value
	 * @param up      upper bound of random value
	 * @return        number of nonzero count
	 */
	public def initRandomFast(ldm:Int, nzp:Double, lb:Int, ub:Int):Int {
		var offset:Int=0;
		val ca = getStorage();
		val nl:Int = cLine.size;
		//val ll:Int = nl /100 >0?nl/100:1;
		for (var l:Int=0; l<nl; l++) {
			//Debug.flushln("Random initial compress line "+l);
			cLine(l).initRandomFast(ldm, nzp, offset, ca, lb, ub);
			offset += cLine(l).length;
			// if (here.id()==0) {
			// 	Console.OUT.print(".");Console.OUT.flush();
			// }
		}
		return offset;
	}
	/**
	 * Initialize with random values 
	 * 
	 * @param ldm     The leading dimension or the compress line original size
	 * @param nzp     Nonzero sparsity
	 * @return        number of nonzero count
	 */
	public def initRandomFast(ldm:Int, nzp:Double) =
		initRandomFast(ldm, nzp, 0, 0);

	
	//-------------------------------------------------------------
	/**
	 * Combination of make and random initialize 
	 *
	 * @param n            the number of compress lines 
	 * @param maxIndex     The leading dimension 
	 * @param nzp          Nonzero sparsity
	 * @param ca           The data storage for compress array.
	 */
	public static def makeRand(n:Int,
							   maxIndex:Int, 
							   nzp:Double,
							   ca:CompressArray
							   ):Compress2D {
		val c2d = Compress2D.make(n, ca);
		c2d.initRandom(maxIndex, nzp);
		return c2d;
	}	
	/**
	 * Combination of make and random initialize fast 
	 *
	 * @param n            the number of compress lines 
	 * @param maxIndex     The leading dimension 
	 * @param nzp          Nonzero percentage
	 * @param ca           The data storage for compress array.
	 */
	public static def makeRandomFast(n:Int,
									 maxIndex:Int, 
									 nzp:Double,
									 ca:CompressArray
									 ):Compress2D {
		val c2d = Compress2D.make(n, ca);
		c2d.initRandomFast(maxIndex, nzp);
		return c2d;
	}
	
	//--------------------
	/**
	 * Return number of compress lines
	 */
	public def size():Int = cLine.size;
	
	/**
	 * Return number of non zero entries in compress array
	 */
	public def getNonZeroCount():Int {
		val ll = size() - 1;
		if (ll < 0) return 0;
		return cLine(ll).offset + cLine(ll).length;
	}

	/**
	 * Count non zero entries in specified range of compress lines.
	 * The actual storage size in compress array could be larger, if compress 
	 * lines are not adjacent to each other in the compress array
	 *
	 * @param lineOff     the starting compress line
	 * @param lineCnt     the number of compress lines
	 */
	public def countNonZero(lineOff:int, lineCnt:Int):Int {
		
		var n:Int =0;
		for (var i:Int=lineOff; i< lineOff+lineCnt; i++)
			n += cLine(i).length;
		return n;
	}
	
	public def countNonZeroTo(idxval:int, lineCnt:Int):Int {
		
		var n:Int =0;
		for (var i:Int=0; i< lineCnt; i++)
			n += cLine(i).countNonZeroTo(idxval);
		return n;
	}
	
	/**
	 * Count non zero in all compress lines
	 */
	public def countNonZero() : Int {
		if (size() == 0) return 0;
		return cLine(size()-1).offset+cLine(size()-1).length;
		//countNonZero(0, size());
	}

	/**
	 * Reset all compress lines
	 */
	public def reset():void {
		reset(0);
	}
	
	/*
	 * Partial reset
	 */
	public def reset(offln:Int):void {
		for (var i:Int=offln; i< cLine.size; i++) {
			cLine(i).reset();
		}
		if (offln == 0)
			getStorage().reset();
		else 
			getStorage().count = cLine(offln-1).offset+cLine(offln-1).length;
	}
	//=====================================================================
	/**
	 * Make a copy of myself
	 */
	public def clone():Compress2D {
		val ca = this.getStorage().clone();
		val cd = new Array[Compress1D](this.size(), 
		        (i:Int) => new Compress1D(this.cLine(i).offset, 
								   this.cLine(i).length, ca));
		return new Compress2D(cd);
	}

	//=====================================================================
	// Data access
	//=====================================================================
	//
	// Make sure CompressArray object is same at all Compress1D (CompressLine)
	
	/**
	 * Get the value array of compressed array
	 */
	public def getValue() = getStorage().value;
	/** 
	 * Get the surface index array of compressed array
	 */
	public def getIndex() = getStorage().index;
	
	/**
	 * Return element value in the compressed array which is paired
	 * with the surface index idx in the lnum compess line (Compress1D) 
	 * This operation is expensive. Binary search within the compressed line
	 * is used.
	 */
	public operator this(lnum:Int, idx:Int) = cLine(lnum)(idx);
	
	/**
	 * Return the specified compress line object 
	 *
	 * @param lnum     the index of compress line in Compress2D
	 */
	public def getLine(lnum:Int) = cLine(lnum);

	/**
	 * Return the compress data storage, or CompressArray object.
	 * All compress lines share the same storage. The return value is
	 * from the first compress line.
	 */
	//public def getDataArray() = cLine(0).cArray;
	public def getStorage() = cLine(0).cArray;

	//-------------------------------------------
	// If found idx at cLine(lnum), it will replace current value-index
	// If not, it will try to append at the end of storage at the first
	// available space
	/**
	 * Set (surface index, value) pair in the i-th compressed line.
	 * The data entry of surface index must exist, otherwise the operation
	 * fails.
	 * This operator should be used with caution. 
	 * Modifying compressed data after it is created should be avoided.
	 */
	public operator this(i:Int)=(w:Pair[Int,Double]):void{
	    val idx = w.first;
	    val v = w.second;
		val pos = cLine(i).find(idx);
		if (pos >= 0)
			cLine(i)(pos)=w;
		else {
			Console.OUT.println("Not found line "+i+" index "+idx+
								", Cannot set value "+v);
			Console.OUT.flush();
		}
	}

	/**
	 * Replace the line at i with cl. This method is used for
	 * for disjoint storage.
	 */
	public def setLine(i:Int, cl:Compress1D) : void{
		cLine(i) = cl;
	}
	//-------------------------------------------------------------------
	//
	public def find(i:Int, idx:Int) = cLine(i).find(idx);
	//
    /**
     * Compress all data in array at the specified compress line and offset.
	 * @param i     The target line.
	 * @param offset     Offset in memory storage
	 * @param d     The source for the compressed data
	 * @returns     The number of elements copied.
     */
	public def compressAt(i:Int, offset:Int, d:Array[Double](1)) = 
		cLine(i).compressAt(offset, d);
	
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------

	/**
	 * Write compressed array to ia, ja and aval.
	 */
	public def toCompressSparse(ia:Array[int](1), 
								ja:Array[int](1), 
								aval:Array[Double](1)
								) : void{
		var pos:Int = 0;
		var i:Int = 0;
		for (; i<cLine.size; i++) {
			val ln = cLine(i);
			ia(i) = pos;
			for (var idx:Int=0; idx<ln.length; idx++,pos++) {
				ja(pos)   = ln.getIndex(idx);
				aval(pos) = ln.getValue(idx);
			}
 		}
		ia(i) = pos;
	}
   

    //========================================================================
    //========================================================================
	// Add a compressline to a compressline at specified index to a given array
	public def addLinesToArray(lst:Int,   // starting line
							   lcnt:Int,  // number of lines
							   ldm:Int,   // maximum index
							   dst:Array[Double](1) //destination array
							   ):void {
		var dstoff:Int=0;
		for (var i:Int=lst; i<lst+lcnt; i++, dstoff+=ldm)
			cLine(i).addToArray(dstoff, dst as Array[Double](1){rail});
	}

    //========================================================================
	// No actualy cLine copy. The cLine still in the source memory space
    //========================================================================
	//public def getLines(st:Int, 
	//					num:Int, 
	//					comp2d:Compress2D) : void {
	//	Debug.assure(num <= comp2d.size());
	//	for (var i:Int=0; i<num; i++) {
	//		comp2d.setLine(i, cLine(st+i));
	//	}
	//}

    //========================================================================
	// Copy a range of cLine across all lines and stored in Compress2D
    //========================================================================
	// The target will have indexes adjust
	public def copySectionToC2D(start:Int, //Starting line
								len:Int,   //Number of lines
								dst:Compress2D // Target Compress2D
								) : Int {
		val nl = this.size(); //Get number of compressed lines
		val ca = dst.getStorage();//
		var dstoff:Int=0;
		Debug.assure(nl==dst.size(), 
					  "Cannot perform section copy, compress2D size mismatch");
		for (var i:Int=0; i<nl; i++) {
			val nc = cLine(i).copyPart(start, len, dstoff, ca);
			dst.cLine(i).offset = dstoff;
			dst.cLine(i).length = nc;
			dstoff += nc;
		}
		ca.count = dstoff;
		return dstoff;
	}
	//
	public def copySectionToCArray(start:Int,  //Starting line
								   len:Int,    //Number of lines
								   ca:CompressArray //Target memory storage
								   ): Int {
		val nl = this.size(); //Get number of compressed lines
		var dstoff:Int=0;
		for (var i:Int=0; i<nl; i++) {
			dstoff += cLine(i).copyPart(start, len, dstoff, ca);
		}
		ca.count = dstoff;
		return dstoff; // Number of NZ count
	}
    //========================================================================
	//Copy compress line data to one memory location
    //========================================================================

	public def copyLinesToC2D(start:Int,       //Starting line
							  cnt:Int,         //Number of lines for copy
							  dst:Compress2D   //Target Compress2D
							  ): Int {         //Return number of data copied
		val ca = dst.getStorage();
		Debug.assure(start+cnt <= this.size() && cnt <= dst.size());
		var dstline:Int = 0;
		var dstoff:Int = 0;
		//this.debugPrint("Copy source Compress2D start:"+start+" cnt:"+cnt);
		for (var srcline:Int=start; srcline<start+cnt; srcline++, dstline++) {
			val nc = cLine(srcline).copyAll(dstoff, ca);
			dst.cLine(dstline).offset = dstoff;
			dst.cLine(dstline).length = nc;
			dstoff += nc; // increasing offset
		}
		ca.count = dstoff;
		//dst.debugPrint("Copy dest Compress2D is ");
		return dstoff;
	}

	/**
	 * Obsolete
	 */
	public def copyLinesToCArray(start:Int,       // Starting line
								 cnt:Int,         // Number of lines
								 ca:CompressArray // Target CompressArray
								 ):Int {          // Return number of data copied
		Debug.assure(start+cnt <= this.size());
		Debug.assure(countNonZero(start, cnt) <= ca.storageSize(), 
					  "Not enough space in CompressArray");
		var dsti:Int = 0;
		var dstoff:Int = 0;
		for (var srci:Int=start; srci<start+cnt; srci++, dsti++) {
			dstoff += cLine(srci).copyAll(dstoff, ca);
		}
		ca.count = dstoff;
		return dstoff;
	}

	//--------------------------------------------------
	// Copy data between compress 2D
	//--------------------------------------------------

	/**
	 * Copy the specified range of compress lines from source to target.
	 * All compressed lines (offset-length) shoudl not be referenced, untill they are reset.
	 * The range of compressed lines' offsets are adjusted based on the count of compressed array.
	 * 
	 * @param srcC2D            the source compress 2D array
	 * @param srcLineOffset     the starting line int 2D array
	 * @param dstC2D            the target compress 2D array
	 * @param dstLineOffset     line offset of destination compressed 2D
	 * @param lineCnt           number of lines to be copied
	 * @return					number of compressed elements copied
	 */
	public static def copy(srcC2D:Compress2D, var srcLineOffset:Int,
						   dstC2D:Compress2D, var dstLineOffset:Int, lineCnt:Int) :Int {

		val srcend:Int = srcLineOffset+lineCnt;
		val dstend:Int = dstLineOffset+lineCnt;
		//Make sure the source and destionation are bounded.
		Debug.assure(dstend <= dstC2D.size() && srcend <= srcC2D.size(), 
					 "Illegal line offset in source or target");

		// Get the starting offset by looking previous compress line
		val sttoff:Int=(dstLineOffset==0)?0:dstC2D.cLine(dstLineOffset-1).offset +
			                                dstC2D.cLine(dstLineOffset-1).length;
		var curoff:Int=sttoff;
		var dlen:Int = 0;

		for (;srcLineOffset < srcend; srcLineOffset++, dstLineOffset++) {
			// Set the offset at the target
			dstC2D.cLine(dstLineOffset).offset = curoff;
			Compress1D.copy(srcC2D.cLine(srcLineOffset), dstC2D.cLine(dstLineOffset));
			curoff += dstC2D.cLine(dstLineOffset).length;
		}
		// Reset compress array count
		dstC2D.getStorage().count = curoff;
		// Get the length by substract the starting offset from current offset
		return curoff - sttoff; 
	}
	
	/**
	 * Copy all lines from source to target. If target has more compress lines,
	 * they are reset to 0-length.
	 * 
	 * @param src   	The source compress 2D
	 * @param dst    	The target compress 2D
	 * @return 			number of compressed elements copied
	 */
	public static def copy(src:Compress2D, dst:Compress2D) : Int {

		val datacnt = Compress2D.copy(src, 0, dst, 0, src.size());
		
		for (var ln:Int = src.size(); ln < dst.size(); ln++) {
			dst.cLine(ln).offset = datacnt;
			dst.cLine(ln).length = 0;
		}
		return datacnt;
	}

	/**
	 * Copy the specified part in all compress lines to target
	 * The range of indexes is specified using its starting index and 
	 * the number of indexes in it uncompressed data array.
	 *
	 * @param srcC2D   -- The source compress 2D
	 * @param idxStart -- The starting index of the source of uncompressed data array
	 * @param dstC2D   -- The target compress 2D
	 * @param idxCount -- The number of indexes in the source of uncompressed data array
	 *
	 */
	public static def copySection(srcC2D:Compress2D, idxStart:Int,
								  dstC2D:Compress2D, idxCount:Int) : Int {

		//Debug.assure(srcC2D.size()==dstC2D.size(), 
		//			 "Cannot perform section copy, compress2D size mismatch");
		var ln:Int =0;
		var dstoff:Int=0;
		for (; ln < srcC2D.size(); ln++) {
			//Set the target compress line offset
			dstC2D.cLine(ln).offset = dstoff;
			//Make copy
			//Debug.flushln("Copy section compress line:"+ln+" dst offset:"+dstoff );
			Compress1D.copySection(srcC2D.cLine(ln), idxStart, 
								   dstC2D.cLine(ln), idxCount);
			//Reset the total offset in compress array
			dstoff += dstC2D.cLine(ln).length;
		}
		// Reset the rest of target
		for (; ln < dstC2D.size(); ln++) {
			dstC2D.cLine(ln).offset = dstoff;
			dstC2D.cLine(ln).length = 0;
		}
		//Reset the target compress array count
		dstC2D.getStorage().count = dstoff;
		//Debug.flushln("Done copy 2D");
		return dstoff;
	}

	//----------------------------------------------------------------

	//-----------
	// Set given compress 2D to specified startln compressed line
	// Make sure sparse matrix created with cd has the same compressed array
	/**
	 * @param startLine -- Starting positoin
	 * @param count -- Number of compressed lines
	 * @param source -- The source
	 */
	public def resetCols(startLine:Int, count:Int, source: Compress2D){ 
		Debug.assure(startLine+count <= source.size() && count <= this.size());
		//Debug.println("Reset columns from "+startln+" cnt:"+cnt);
		for (var di:Int=0, si:Int=startLine; si<startLine+count; si++, di++) {
			this.cLine(di).offset = source.cLine(si).offset;
			this.cLine(di).length = source.cLine(si).length;
		}
	}
	
    //========================================================================
	// Modifying the index, sequentialized 2D->1D index in column-wise
    //========================================================================

	/**
	 * The storage of compress array does not indicate the start of each compress line
	 * in Compress2D. The offset and length of a compress line are recorded in compress1D feilds.
	 * To reduce communication data, compress1D fields are not transfered with compress array. 
	 *
	 * <P> This function is used to mark the start offset of compress lines in their
	 * storage (CompressArray), so that the offset values can be rebuilt. 
	 * The mechanism is to set the index value at the start of a compress line to negative.
	 *
	 * @param ldm  -- Size of the origianl (uncompressed) line in 2D, which is also the leading dimension of matrix.
	 * @param lineOff -- offset line of the starting compressed line
	 * @param lineCnt -- Number of compressed lines
	 * @return -- storage size for the range of compress lines
	 */
	public def serializeIndex(ldm:Int, lineOff:Int, lineCnt:Int ) : Int {
		val ca = getStorage();
		var pos:Int=0; 
		val sttpos:Int = cLine(lineOff).offset;

		if (lineCnt == 0) return 0;

		for (var c:Int=lineOff; c<lineOff+lineCnt; c++) {
			pos = cLine(c).offset;
			//Debug.flushln("Reset column:"+c+" start index:"+pos);
			if (pos >= ca.index.size) break; //This condition is used to guarde when the last line is empty
			ca.index(pos) -= ldm;            //Mark the index value
			//Debug.flushln("To value:"+ ca.index(pos));
		}
		return pos-sttpos+cLine(lineOff+lineCnt-1).length;
	}

	/**
	 * Marking the start indexes of all compressed lines.
	 *
	 * @param ldm -- the original size of the uncompressed line.
	 * @return -- element storage count for all compress lines.
	 */
	public def serializeIndex(ldm:Int) =
		serializeIndex(ldm, 0, cLine.size);
	

	/**
	 * Reverse index values changed by the serialization to original ones.
	 *
	 * @param ldm  -- the original size of the uncompressed line.
	 * @param lineOff -- the starting line
	 */
	public def resetIndex(ldm:Int, lineOff:Int): void {
		val ca = getStorage();
		var pos:Int = 0;
		for (var c:Int=lineOff; c<cLine.size; c++) {
			// Add M for the first index value
			pos = cLine(c).offset;

			if (pos >= ca.index.size) break;
 
			if (ca.index(pos) < 0)
				ca.index(pos) += ldm;
			else
				break;
		}
	}

	/**
	 * Build the start and length of compress lines  based on serialization
	 *
	 * @param ldm -- The leading dimension of matrix
	 * @param lineOff -- Offset line of the starting compressed line
	 * @param lineCnt -- Number of compressed lines
	 * @param dataCnt -- Number of nonzero elements
	 * @return -- Return number of elements left unclaimed. Should be 0.
	 */
	public def buildIndex(ldm:Int, lineOff:Int, lineCnt:Int, var dataCnt:Int) : Int {
		if (lineCnt == 0) return dataCnt;

		val ca = getStorage();
		//val lineEnd = lineOff+lineCnt-1;
		//Compute the starting offset in storage array
		var pos:Int = lineOff==0?0:cLine(lineOff-1).offset+cLine(lineOff-1).length;
		var len:Int = 0;

		Debug.assure((pos+dataCnt)<=ca.index.size, 
					 "Building index fail - data count exceeds the storage size"); 
		
		if (ca.index(pos) < 0) 
			ca.index(pos) += ldm; 			// Adjust for the starting line
		
		//cLine(lineOff).length = dataCnt;
		var c:Int = lineOff;
		while (c < lineOff+lineCnt) {
			len = 0;
			cLine(c).offset = pos;			//set the starting offset for compress line
			while (pos < ca.index.size && dataCnt > 0 ) {
				if (ca.index(pos) < 0) {
					ca.index(pos) += ldm; 	//Adjust ithe index value of next line
					break;                	//Start next compress line
				} else {
					len++;                	//Increase length of line
					pos++;                	//Increase the index of current index of compress array
					dataCnt--;			  	//Decrease data counter
				}
			}
			cLine(c).length = len; 			//Set the length of compressed line
			c++; 							//Next compress line
		}
		//ca.print("Result of deseq CA ");
		ca.count = cLine(c-1).offset + cLine(c-1).length;
		
		return dataCnt;
	}
	
	/**
	 * Build the indexe of all compressed lines
	 *
	 * @param ldm -- The leading dimension. The original size of compressed line
	 * @return -- Number of elements left unclaimed. Should be 0, if serilazation is correct.
	 */
	public def buildIndex(ldm:Int) = buildIndex(ldm, 0, cLine.size, getStorage().count);

    //========================================================================
	// Utils
    //========================================================================
	public def toString():String {
		var outstr:String = "  "+cLine.size+" compress lines\n";
		for (var i:Int =0; i<cLine.size; i++) {
			outstr += i.toString() + "\t"+cLine(i).toString() + "\n";
		}
		return outstr;
	}

	public def print(msg:String):void {
		var outstr:String = msg + 
			"---------- Compressed 2D ----------\n" +
			toString() +
			"-----------------------------------\n";
		Console.OUT.print(outstr);
		Console.OUT.flush();
	}
	public def print() { print(""); }

	public def debugPrint(msg:String) {
		var outstr:String = msg + 
			"---------- Compressed 2D ----------\n" +
			toString() +
			"-----------------------------------\n";
		Debug.println(outstr);		
	}
	public def debugPrint() { debugPrint(""); }
  
	
	public def equals(cd:Compress2D):Boolean {
		if (this.size() != cd.size()) return false;
		for (var i:Int=0; i<cLine.size; i++) {
			if (cLine(i).equals(cd.getLine(i)) == false)
				return false;
		}
		return true;
	}

	//-----------------------
	// Randomness info
	//-----------------------
	public def compAvgLineSize():Double = getStorage().count/this.size();

	public def compLineSizeSumDvn(avg:Double):Double {
		var stdd:Double = 0.0;
		var df:Double = 0.0;
		for (var cl:Int=0; cl<size(); cl++) {
			df = cLine(cl).length - avg;
			stdd += df*df;
		}
		return stdd;
	}

	public def compLineSizeStdDvn():Double {
		val avg  = compAvgLineSize();
		val stdd = compLineSizeSumDvn(avg);
		return x10.lang.Math.sqrt(stdd / this.size());
	}
	//----------
	public def compAvgIndexDst() :Double {
		var avgd:Double = 0.0;
		for (var cl:Int=0; cl<this.size(); cl++) {
			avgd += cLine(cl).compAvgIndexDst();
		}
		return avgd / this.size();
	}
	public def compIndexDstSumDvn(avg:Double) :Double {
		var td:Double = 0.0;
		for (var cl:Int=0; cl<this.size(); cl++) {
			td += cLine(cl).compIndexDstSumDvn(avg);
		}
		return td;
	}
	public def getIndexDstCnt() : Int {
		var cnt:Int = 0;
		for (var cl:Int=0; cl<this.size(); cl++) {
			val c = cLine(cl).length - 1;
			if (c> 0) cnt += c;
		}
		return cnt;
	}
	public def compIndexDstStdDvn() : Double {
		val td = compIndexDstSumDvn(compAvgIndexDst());
		val cnt= getIndexDstCnt();
		if (cnt <= 0) return 0.0;
		return x10.lang.Math.sqrt(td/cnt);
	}
}

