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

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.MathTool;
import x10.matrix.DenseMatrix;
import x10.matrix.VerifyTools;


public type SparseCSC(M:Int)=SparseCSC{self.M==M};
public type SparseCSC(M:Int,N:Int)=SparseCSC{self.M==M, self.N==N};
public type SparseCSC(C:Matrix)=SparseCSC{self==C};
public type SparseCSC(C:SparseCSC)=SparseCSC{self==C};

/**
 * This sparse matrix class uses CSC-LT format to store matrix nonzero elements.
 * The underlying storage is defined in ComressArray.
 * 
 * <p> For testing purpose, the initialization of nonzero elements in the same
 * column are generated one after another.  The distance of the row index
 * of two adjacent nonzero elements is determined randomly, while using the sparsity
 * (nonzero density) to determine its average/mean. 
 */
public class SparseCSC extends Matrix {

	//===============================================================
	/**
	 * Compress 2-dimension array
	 */
	public val ccdata:Compress2D;
		
	//---
	//public var sparsity:Double= 0.0;

	//----------------------------------------------------
	// Temporary memory space used for type conversion and
	// data compression
	private var tmprow:Array[Double](1);
	private var tmpcol:Array[Double](1);

	//---------------------------
	// Used for serialization index value, reset or build 
	private var copyColOff:Int;
	private var copyColCnt:Int;
	private var copyDataCnt:Int;
	//---------------------------

	//===============================================================
	// Constructor
	//===============================================================


	/**
	 * Construct a sparse matrix instance compressed in column major 
	 * order (CSC)
	 *
	 * @param m     Number of rows in the CSC sparse matrix
	 * @param n     Number of columns in the CSC sparse matrix
	 * @param cd     Compress 2D data structure, which contains the
	 *              the compressed array data storage
	 */
	public def this(m:Int, n:Int, cd:Compress2D):SparseCSC(m,n) {

		super(m, n);
		Debug.assure(n<=cd.size());

		ccdata = cd;
		//sparsity = 1.0*countNonZero()/m/n;
		//No memory allocation for temp space
		tmprow = new Array[Double](0);
		tmpcol = new Array[Double](0);
	}

	/**
	 * Construct a CSC instance using specified compressed array data storage.
	 *
	 * @param m     Number of rows in the CSC sparse matrix
	 * @param n     Number of columns in the CSC sparse matrix
	 * @param ca     The data storage of compressed array.
	 */
	public def this(m:Int, n:Int, ca:CompressArray):SparseCSC(m,n) {
		super(m, n);
		ccdata = Compress2D.make(n, ca);
		//sparsity = 1.0*countNonZero() /m/n;

		tmprow = new Array[Double](0);
		tmpcol = new Array[Double](0);
	}
	

	//----------------------------------------
	// Constructor with memory allocation
	//----------------------------------------

	/**
	 * Create a sparse matrix instance with memory storage for specified 
	 * number of nonzero elements
	 *
	 * @param m     Number of rows in the CSC sparse matrix
	 * @param n     Number of columns in the CSC sparse matrix
	 * @param nzcnt     Number of nonzero elements of the sparse matrix
	 */
	public static def make(m:Int, n:Int, nzcnt:Int //Number of non-zero entries
						   ):SparseCSC(m,n) {
		val ca = new CompressArray(nzcnt); 
		val sp = new SparseCSC(m, n, ca); 

		return sp;
	}

	/**
	 * Create a sparse matrix instance with memory storage for specified nonzero
	 * sparsity.
	 *
	 * @param m     Number of rows in the CSC sparse matrix
	 * @param n     Number of columns in the CSC sparse matrix
	 * @param nzd     The nonzero density or sparsity.
	 */
	public static def make(m:Int, n:Int, nzd:Double) : SparseCSC(m,n) {
		val cnt = compAllocSize(m, n, nzd);
		val spa = SparseCSC.make(m, n, cnt);

		return spa;
	}

	/**
	 * Create a sparse matrix with memory storage for all elements
	 *
	 * @param m     Number of rows in the CSC sparse matrix
	 * @param n     Number of columns in the CSC sparse matrix
	 */
	public static def make(m:Int, n:Int) = SparseCSC.make(m, n, m*n);
									   
	//--------------------------------------------------------------
	/**
	 * Create a m x n SparseCSC matrix based on CSC data format input
	 * ia, ja, and av.
	 *
	 * @param ia     integer array, the count of nonzero of each column.
	 * @param ja     integer array, the surface indices of nonzeros in column
	 * @param av     double array, the actual matrix element data corresponding
	 *              to its surface index at the same position in ja.
	 */
	public static def make(m:Int,n:Int,	
						   ia:Array[Int](1),
						   ja:Array[Int](1){rail},
						   av:Array[Double](1){self.size==ja.size,rail}
						   ):SparseCSC{
		val ccd = Compress2D.make(ia, ja, av);
		return new SparseCSC(m, n, ccd);
	}
	
    /**
     * Create a m x n SparseCSC matrix based on CSC data format input
     * ia, ja, and av.
     * 
     * @param m, n     matrix dimension
     * @param ca     Compress Array distributed by DistAry
     */
    public static def make(m:Int,n:Int, ca:CompressArray):SparseCSC{
    	val ccd = Compress2D.make(n, ca);
    	ccd.buildIndex(m);
	   	return new SparseCSC(m, n, ccd);
    }
		   
	//================================================================
    // Initialization
    //================================================================    
	/**
	 * For testing purpose,
	 * <p> Initialize sparse matrix with a specified value and nonzero elements' 
	 * positions are generated for the specified sparsity. The index distance
	 * between two adjacent nonzero elements in the same column is computed by
	 * a random method, and its average is determined by the sparsity. 
	 *
	 * @param v      Initial value for all elements
	 * @param sp     Nonzero sparsity
	 * @see Compress2D.initConst()
	 */
	public def init(v:Double, sp:Double):SparseCSC(this) {
		val cnt = ccdata.initConst(M, v, sp);
		return this;
		//sparsity = 1.0 * cnt/M/N;
	} 
	

	/**
	 * Initialize sparse matrix elements to specified value.
	 *
	 * @param v     initial value for all nonzero elements.
	 */
	public def init(v:Double):SparseCSC(this) {
		val nzd = 1.0*getStorageSize()/M/N;
		init(v, nzd);
		return this;
	}
	
	/**
	 * For testing purpose,
	 * 
	 * <p> Initial sparse matrix elements with random values, and
	 * and positions of nonzero elements are computed by the random-fast method.
	 * The distance between two adjacent nonzero elements in the same column
	 * is randomly generated. This random method is controled by the sparity.
	 * 
	 * @param lb     lower bound of random value
	 * @param up     upper bound of random value
	 * @param sp     Nonzero sparsity
	 * @see init(v:Double, sp:Double)
	 */
	public def initRandom(lb:Int, ub:Int, sp:Double) : SparseCSC(this) {
		val cnt = ccdata.initRandomFast(M, sp, lb, ub);
		//sparsity = 1.0 * cnt/M/N;
		return this;
	}
	
	public def initRandom(sp:Double) : SparseCSC(this) {
		val cnt = ccdata.initRandomFast(M, sp);
		//sparsity = 1.0 * cnt/M/N;
		return this;
	}
	/**
	 * For testing purpose,
	 * 
	 * <p> Use the size of available storage space to compute sparsity, and
	 * then use the sparity to initial sparse matrix elements with random values.
	 * 
	 * @param lb     lower bound of random value
	 * @param up     upper bound of random value
	 */
	public def initRandom(lb:Int, ub:Int): SparseCSC(this) { 
		val nzd = 1.0 * getStorageSize() /M/N;
		initRandom(lb, ub, nzd);
		return this;
	}
	
	public def initRandom(): SparseCSC(this) { 
		val nzd = 1.0 * getStorageSize() /M/N;
		initRandom(nzd);
		return this;
	}
	//---------------------------------------------------------
	/**
	 * Initialize with given function with range [0..M, 0..N]
	 */
	public def init(f:(Int, Int)=>Double): SparseCSC(this) {
		
		var offset:Int=0;
		val ca = getStorage();
		for (var c:Int=0; c<N; c++) {
			val ccol = ccdata.cLine(c);
			ccol.offset = offset;
			for (var r:Int=0; r<M; r++) {
				val nzval:Double = f(r, c);
				if (! MathTool.isZero(nzval)) {
					ca.index(offset)=r;
					ca.value(offset)=nzval;
					offset++;
				}
			}
			ccol.length = offset - ccol.offset;
		}
		ca.count = offset;
		return this;
	}
	
	/**
	 * Initialize wiht nonzero indexing function and value generating function.
	 * 
	 * @param fidx     Nonzero row indexing, must be ascending function. Given values (r, c), compute the r-th nonzero row index in column c.
	 * @param fval     value generating function, given row and column index.
	 */
	public def init(fidx:(Int, Int)=>Int, fval:(Int, Int)=>Double): SparseCSC(this) {
		var offset:Int=0;
		val ca = getStorage();
		for (var c:Int=0; c<N; c++) {
			val ccol = ccdata.cLine(c);
			ccol.offset = offset;
			for (var r:Int=0; r<M&&offset<ca.index.size; r++) {
				val nzidx = fidx(r, c);
				if (nzidx >= M) break;
				val nzval = fval(nzidx, c);
				if (! MathTool.isZero(nzval)) {
					ca.index(offset)=nzidx;
					ca.value(offset)=nzval;
					offset++;
				}
			}
			ccol.length = offset - ccol.offset;
		}
		ca.count = offset;
		return this;
	}
	
	/**
	 * Initial sparse matrix using function and row and column offsets.
	 */
	public def init(rowoff:Int, coloff:Int, f:(Int, Int)=>Double): SparseCSC(this) {
		
		var offset:Int=0;
		val ca = getStorage();
		for (var c:Int=0; c<N; c++) {
			val ccol = ccdata.cLine(c);
			ccol.offset = offset;
			for (var r:Int=0; r<M&&offset<ca.index.size; r++) {
				val nzval:Double = f(r+rowoff, c+coloff);
				if (! MathTool.isZero(nzval)) {
					ca.index(offset)=r;
					ca.value(offset)=nzval;
					offset++;
				}
			}
			ccol.length = offset - ccol.offset;
		}
		ca.count = offset;
		return this;
	}	
	
	
	/**
	 * For testing purpose.
	 *
	 * <p> Create a sparse matrix instance and initialize with random values
	 * and positions of nonzero positions computed randomly.
	 * 
	 * @param m     Number of rows in the matrix
	 * @param n     Number of columns in the matrix
	 * @param nzd     Nonzero sparsity
	 * @see make() and initRandom() 
	 */
	public static def makeRand(m:Int, n:Int, nzd:Double) : SparseCSC(m,n) {
		val csc = SparseCSC.make(m, n, nzd);
		csc.initRandom(nzd);

		return csc;
	}

	//--------------------------------------------------------------
	// Compress 2D stored in column-wise way in column-compressed 2D
	//--------------------------------------------------------------
	/**
	 * Allocate sparse matrix storage with the same memory storage. 
	 *
	 * @param  m      number of rows
	 * @param  n      number of columns
	 */
	public def alloc(m:Int, n:Int):SparseCSC(m,n) {
		// Maximum memory allocation
		val nz = (m==this.M) ? countNonZero(0,n) : ccdata.countNonZeroTo(m, n);
		val ca:CompressArray = new CompressArray(nz);
		return new SparseCSC(m, n, ca);
	}
	
	/**
	 * Make a copy of myself
	 */
	public def clone():SparseCSC(M,N) {
		val cd = ccdata.clone();
		return new SparseCSC(this.M, this.N, cd);
	}

	/**
	 * Reset all data to 0 and nonzero count to 0
	 */
	public def reset():void  { 
		ccdata.reset(); 
	}
	public def reset(coloff:Int) :void {
		ccdata.reset(coloff);
	}
    //========================================================================
	// Data access
    //========================================================================
	// Make sure the CompressArray is from the same location
	// for all Compress1D in Compress2D object	
	/**
	 * Return the compressed value array
	 */
	public def getValue() = ccdata.getValue();

	/**
	 * Return the compressed surface index array
	 */
	public def getIndex() = ccdata.getIndex();

	/**
	 * Return the storage of compressed data array
	 */
	public def getStorage() = ccdata.getStorage(); // return compress array

	/**
	 * Return the matrix element value at the r-th row and c-th column.
	 */
	public operator this(r:Int, c:Int):Double = ccdata(c, r);
	public operator this(a:Int):Double = ccdata(a%M,a/M);
	
	//
	//========================================================
	// 
	// If found idx at cLine(lnum), it will replace current value-index
	// If not, it will try to append at the end of storage at the first
	// available space
	/**
	 * Set v at r-th row and c-th column. 
	 * The data entry (r,c) must exist in the compressed array, otherwise
	 * the operation fails. 
	 *
	 * Modifying sparse matrix after creation should be avoided
	 */
	public operator this(r:Int, c:Int) = (v:Double):Double {
	    ccdata(c)=Pair[Int,Double](r,v);
	    return v;
	}
	
	//========================================================================
	// Can be used for disjoint storage
	public def setCol(c:Int, ln:Compress1D) { 
		ccdata.setLine(c, ln);
	}
	public def compressAt(c:Int, off:Int, d:Array[Double](1)) =
		ccdata.cLine(c).compressAt(off, d);
	//
	// No setRow method available

	//----------------------------------------------------------------------
	// Copy the memory locations of columns into a new SparseCSC, no memory allocation
	// for matrix data
	//----------------------------------------------------------------------
	// Get one compressed column line
	public def getCol(col:Int) = ccdata.getLine(col);
	
	//----------------------------------------------------------------------
	// Copy the rows into a new SparseCSC
	//----------------------------------------------------------------------
	// Get one compressed row line , no need to reset tmprow
	// This needs to be removed
	public def getRow(row:Int) : Compress1D {
		val tr = getTempRow();
		extractRow(row, tr);
		return Compress1D.compress(tr);
	}
	// When using resetCols, must not use serilaizing index
	public def resetCols(stln:Int, 
						 cnt:Int, 
						 src:SparseCSC
						 ) : void {
		ccdata.resetCols(stln, cnt, src.ccdata);
	}

	//=====================================================================
	// Sequentialize and desequential index for communication
	//=====================================================================
	
	/**
	 * Setup the remote copy of columns at the source sparse matrix.
	 *
	 * <p> Before sparse matrix data can be transfered to a remote place,
	 * the compressed storage must be modified to mark the starting element
	 * of each column in the matrix. 
	 *
	 * <p> The start of each column is recorded by the fields "offset" in 
	 * Compress1D. To reduce the amount of data in 
	 * inter-place communication, only compressed data (CompressArray) is sent out.
	 * Other fields in Compress2D and Compress1D are not transferred.
	 *
	 * <p> The setup function will modify the index array to mark the start position
	 * of the column, so that at the destination place, the target sparse matrix
	 * can rebuild the start and length for compress columns. 
	 * 
	 * <p> After the inter-place data transfer completes, finishRemoteCopyAtSource() must
	 * be called to change index values back to original, otherwise computation
	 * of index for nonzero elements will be ended in error.
	 *
	 * @param coloff     the starting column in the source sparse matrix
	 * @param colcnt     number of columns to copy in the source sparse matrix
	 *
	 */
	public def initRemoteCopyAtSource(coloff:Int, colcnt:Int):Int {
		copyColOff = coloff;
		copyColCnt = colcnt;
		copyDataCnt = ccdata.serializeIndex(this.M, coloff, colcnt);
		
		return copyDataCnt;
	}

	public def initRemoteCopyAtSource() = initRemoteCopyAtSource(0, N);
	

	/**
	 * Setup the remote copy of columns at the destination place. 
	 * When column offset, column count and data count are available at 
	 * initial time, using this function to make record at destination place
	 * and these is no need to transfer from the remote source again
	 * at finalizing time.
	 *
	 * @param coloff      the starting column in the target matrix
	 * @param colcnt      number of columns to receive
	 * @param datcnt      number of elements (index-value pairs) 
	 */
	public def initRemoteCopyAtDest(coloff:Int, colcnt:Int, datcnt:Int) : void {
		copyColOff = coloff;
		copyColCnt = colcnt;
		copyDataCnt= datcnt;
		//Perform storage size check
		val datoff = getNonZeroOffset(coloff);
		reset(coloff);
		testIncStorage(coloff, datcnt); // If storage short, re-allocate storage
	}

	public def initRemoteCopyAtDest(datcnt:Int): void {
		initRemoteCopyAtDest(0, N, datcnt);
	}

	/**
	 * Finish the remote copy of sparse matrix at source place.
	 * This function must be called before the compressed data can be used.
	 */
	public def finalizeRemoteCopyAtSource(): void {
		ccdata.resetIndex(this.M, copyColOff);
	}

	/**
	 * Finish the remote copy of sparse matrix 
	 * This function must be called before the compressed data can be used.
	 */
	public def finalizeRemoteCopyAtDest(): void {
		ccdata.buildIndex(this.M, copyColOff, copyColCnt, copyDataCnt); 
	}

	/**
	 * Rebuilt index and length for each compress line.
	 */
	public def finalizeRemoteCopyAtDest(coloff:Int, colcnt:Int, datcnt:Int): void {
		ccdata.buildIndex(this.M, coloff, colcnt, datcnt); 
	}

	/**
	 * Serilaize index values in its storage by marking the column start index, so that
	 * after index array is copied to destination place, the destination place
	 * can rebuild the compressed array's offset and length.
	 *
	 * @param coloff     Offset of columns
	 * @param colcnt     Number of columns
	 * @return number of element indexes changed
	 *
	 */
	public def serializeIndex(coloff:Int, colcnt:Int) :void {
		copyDataCnt = ccdata.serializeIndex(this.M, coloff, colcnt);
	}


	/**
	 * Reverse serialization.
	 */
	public def resetIndex(coloff:Int) : void {
		ccdata.resetIndex(this.M, coloff);
	}

	/**
	 * Set the offset and length in index array for each compress line, after the compress data
	 * in storage is copied from remote place.
	 *
	 *
	 * @param coloff     Offset of columns
	 * @param colcnt     Number of columns
	 * @param datcnt     Number of elements in storage copied from remote place
	 * @return     Number of elements unclaimed
	 */
	public def buildIndex(coloff:Int, colcnt:Int, datcnt:Int):Int =
		ccdata.buildIndex(this.M, coloff, colcnt, datcnt); 
	

	//=====================================================================
	// Access temporary space
	//=====================================================================
	public def getTempCol() : Array[Double](1) {
		if (tmpcol.size == 0)
			tmpcol = new Array[Double](this.M, 0.0);
		else {
			for (var i:Int=0; i<this.M; i++) tmpcol(i)=0.0;
		}
		return tmpcol;
	}
	//
	public def getTempRow() : Array[Double](1) {
		if (tmprow.size == 0) 
			tmprow = new Array[Double](this.N, 0.0);
		else {
			// reset the temp array
			for (var i:Int=0; i<this.N; i++) tmprow(i) = 0.0;
		}
		return tmprow;
	}
	//
	public def getTemp(n:Int) : Array[Double](1) {
		if (n > tmpcol.size) tmpcol = new Array[Double](n, 0.0);
		return tmpcol;
	}

	//=====================================================================
	// Copy sparse matrix compress data
	//=====================================================================

	/**
	 * Copy specified range of columns from source to target sparseCSC
	 *
	 * @param src           The source sparse matrix
	 * @param srcColOff     The starting columns for copy at source
	 * @param dst           The target sparse matrix
	 * @param dstColOff     The starting columns in the target matrix
	 * @param colcnt        The number of columns for copy
	 * @return     Number of nonzero elements copied
	 */
	public static def copyCols(src:SparseCSC, srcColOffset:Int,
							   dst:SparseCSC, dstColOffset:Int, colcnt:Int) :Int =
		Compress2D.copy(src.ccdata, srcColOffset, dst.ccdata, dstColOffset, colcnt);

	/**
	 * Copy all columns from source to target. If target has more columns,
	 * they are reset to 0 length of nonzero elements.
	 *
	 * @param src           The source sparse matrix
	 * @param dst           The target sparse matrix
	 */
	public static def copy(src:SparseCSC, dst:SparseCSC) : Int =
		Compress2D.copy(src.ccdata, dst.ccdata);
						

	/**
	 * Copy rows from source to target sparse CSC matrix. The target row offset
	 * is always 0. 
	 *
	 * @param src              The source sparse matrix in CSC
	 * @param srcRowOffset     The starting row in source matrix
	 * @param dst              The target matrix in sparse CSC
	 * @param dstRowOffset     The starting row in target matrix, which must be 0
	 * @param srccnt           The number of rows to copy
	 * @return     Number of nonzero elements copied.
	 */
	public static def copyRows(src:SparseCSC, srcRowOffset:Int, 
							   dst:SparseCSC, dstRowOffset:Int{self==0}, rowcnt:Int) : Int =
		Compress2D.copySection(src.ccdata, srcRowOffset, dst.ccdata, rowcnt);


	//-------------------
	// Target is matrix, obsolete
	private def copyColsToSparse(col:Int,        //Starting column
								cnt:Int,        //Number of columns to copy
								csc:SparseCSC   //Target sparse matrix
								):Int {         //Return number of data copied
		Debug.assure(cnt == csc.N&& this.M==csc.M);
		csc.reset();
		return ccdata.copyLinesToC2D(col, cnt, csc.ccdata);
	}
	// Copy the rows into a new SparseCSC
	// Get multiple rows and put them in compressed-column format (SparseCSC)
	private def copyRowsToSparse(row:Int,          //Starting row
								cnt:Int,            //Number of rows
								csc:SparseCSC       //Target sparse matrix CSC
								):Int{              //Return number of data copied
		Debug.assure(this.N==csc.N && cnt == csc.M);
		csc.reset();
	    return ccdata.copySectionToC2D(row, cnt, csc.ccdata);
	}

	//---------------------------- 
	// Target is CompressArray
	// Copy cnt columns starting from col to CompressArray
	// There is no sizes info for each column 
	// Return the number of data (Index, Double) copied
	private def copyColsToCArray(start:Int,              // Starting column
								cnt:Int,                // Number of columns
								ca:CompressArray // Target memory storage
								): Int  // Return number of data copied
		= ccdata.copyLinesToCArray(start, cnt, ca);
   
	private def copyRowsToCArray(start:Int,             // Starting rows
								cnt:Int,               // Number of rows
								ca:CompressArray //Target memory storage
								):Int  // Return number of data copied
		= ccdata.copySectionToCArray(start, cnt, ca);
		
	//----------------------------
	
	/**
	 * Test and increase storage 
	 *
	 * @param datoff     starting position in storage for new data
	 * @param datcnt     number of new elements will be added
	 * @return true if storage is re-allocated.
	 */
	public def testIncStorage(datoff:Int, datcnt:Int) = 
		ccdata.getStorage().testIncStorage(datoff, datcnt);
	
	/**
	 * Test and increase storage 
	 *
	 * @param datcnt     number of new elements will be added
	 * @return true if storage is re-allocated.
	 */
	public def testIncStorage(datcnt:Int) = 
		ccdata.getStorage().testIncStorage(datcnt);
	

	//=====================================================================
	// Extract data from columns and put the result in array
	//=====================================================================

	// Get decompressed column in an array
	// The target ln array needs to be reset first
	public def extractCol(col:Int, ln:Array[Double](1)) {
		ccdata.getLine(col).extract(ln);
	}
	
	public def extractRow(row:Int, cl:Array[Double](1)):void {
		for (var i:Int=0; i<this.N; i++) cl(i) = this(row, i);
	}

	//----------------------------------------------------------------------
	// Using tmp storage place
	public def extractCol(col:Int):Array[Double](1) {
		val tc = getTempCol();
		extractCol(col, tc);
		return tc;
	}

	public def extractRow(row:Int):Array[Double](1) {
		val tr = getTempRow();
		extractRow(row, tr);
		return tr;
	}

    //========================================================================
	// Extract data to dense matrix
    //========================================================================

	/**
	 * Expand multiple compressed columns into the dense matrix
	 * For very sparse matrix, this operation is expensive, 
	 * 
	 * @param start_col     the starting column
	 * @param num_col       number of columns to extract
	 * @param dm            the target dense matrix to hold the expanded data
	 */
	public def extractCols(start_col:Int, 
						   num_col:Int, 
						   dm:DenseMatrix{self.M==this.M,self.N==num_col}
						   ) : void {
		Debug.assure(this.M<=dm.M&&num_col<=dm.N);
		//
		var dstoff:Int = 0;//offset
		for (var y:Int=start_col; y<start_col+num_col; y++) {
			ccdata.cLine(y).extract(dstoff, dm.d);
			dstoff+= dm.M;
		}
	}
	//
	//public def extractCols(start_col:Int, num_col:Int):DenseMatrix {
	//	val dm = new DenseMatrix(num_col, this.N);
	//	extractCols(start_col, num_col, dm);
	//	return dm;
	//}

	//------------------------
	// Get data from multiple rows and store them in dense matrix 

	/**
	 * Expand multiple compressed rows into the dense matrix.
	 * The target dense matrix is reset.
	 * @param start_row     the starting row
	 * @param num_row       number of rows to extract
	 * @param dm            the target dense matrix to hold the expanded data
	 */
	public def extractRows(start_row:Int, 
						   num_row:Int, 
						   dm:DenseMatrix{self.M==num_row,self.N==this.N}
						   ):void {
		Debug.assure(num_row<=dm.M&&this.N<=dm.N);
		var colst:Int = 0;//offset
		for (var y:Int=0; y<this.M; y++, colst+=dm.M) {
			val cl = ccdata.getLine(y);
			cl.extract(start_row, num_row, colst, dm.d);
		}
	}

	public def extractRows(start_row:Int, 
						   num_row:Int
						   ): DenseMatrix(num_row,this.N) {
		val dm = new DenseMatrix(num_row, this.N);
		extractRows(start_row, num_row, dm);
		return dm;
	}
    //========================================================================
	// Return the non-zero density 

	/**
	 * Compute nonzero sparsity in storage
	 */
	public def compSparsity():Double {
		/* !!!!!!!!!!!!!!! */
		/* M * N could be larger than INT_MAX (2147483648, or 2*10^10 */
		/* in currnt X10c++, the maximum size for array */
		val nz:Double = ccdata.countNonZero() as Double;
		return nz/(this.M*this.N as Double);
	}

	/**
	 * Get number of nonzero elements in specified column
	 */
	public def getColNonZeroCount(col:Int) = ccdata.cLine(col).length;

	//---
	/**
	 * Get the offset in CompressArray for the col-th column starting position
	 */
	public def getNonZeroOffset(col:Int) = 
		(col==0?0:ccdata.cLine(col-1).offset+ccdata.cLine(col-1).length);

	//public def getColOffset(col:Int) = ccdata.cLine(col).offset;

	/**
	 * Count the number of nonzero in the specified range of columns
	 *
	 * @param coloff     Offset column
	 * @param colcnt     Number of columns
	 * @return     the number of elements in compressed array
	 */
	public def countNonZero(coloff:Int, colcnt:Int):Int =
		ccdata.countNonZero(coloff, colcnt);

	/**
	 * Count nonzeros in compress array.
	 * This method will check nonzeros in all columns
	 */
	public def countNonZero():Int = countNonZero(0, N);

	/**
	 * Get the nonzero count from storage compress array.
	 */
	public def getNonZeroCount():Int = getStorage().count(); 

	/**
	 * Get storage size.
	 */
	public def getStorageSize() = getStorage().storageSize();
	
    //========================================================================
	// Format conversion: to SCR and dense matrix
	//========================================================================
	/**
	 * Convert to a new SparseCSR. This operation is expensive
	 */
	public def toCSR():SparseCSR(M,N) {
		val sm = SparseCSR.make(this.M, this.N, countNonZero() as Int);
		toCSR(sm);
		return sm;
	}

	/**
	 * Convert to SparseCSR using provided memory space
	 * This operation is expensive
	 */	
	public def toCSR(sm:SparseCSR(M,N)):void {
		Debug.assure(this.M==sm.M&&this.N==sm.N);
		var off:Int=0;
		val tr = getTempRow();
		for (var r:Int=0; r<this.M; r++) {
			extractRow(r, tr);
			off += sm.compressAt(r, off, tr);
			//crd.setLine(r, Compress1D.compress(tmprow));
		}
	}
	//------------------------------
	/**
	 * Copy data to dense matrix
	 */
	public def copyTo(dm:DenseMatrix(M,N)): void {
		extractCols(0, this.N, dm);
	}

	public static def copyTo(sp:SparseCSC, dm:DenseMatrix, roff:Int, coff:Int): void {

		var dstoff:Int = roff + coff*dm.M;
		for (var col:Int=0; col<sp.N; col++, dstoff+=dm.M)
			sp.ccdata.cLine(col).extract(dstoff, dm.d);
	}
	
	public def copyTo(that:SparseCSC(M,N)) = copy(this, that);
	
	public def copyTo(that:Matrix(M,N)):void {
		if (that instanceof DenseMatrix)
			copyTo(that as DenseMatrix);
		else if (that instanceof SparseCSC)
			copyTo(that as SparseCSC);
		else
			Debug.exit("CopyTo: target matrix type not supported");	
	}
	
	/**
	 * Convert to a new dense matrix object
	 */
	public def toDense():DenseMatrix(M,N) {
		val dm= DenseMatrix.make(this.M, this.N);
		
		extractCols(0, this.N, dm);
		return dm;
	}

	//===================================================================
	// Transpose methods
	//===================================================================

	/**
	 * Convert to a new SparseCSR object in transposed 
	 */
	public def TtoCSR():SparseCSR{self.M==this.N, self.N==this.M} {
		return new SparseCSR(this.N, this.M, this.ccdata);
	}
	
	/**
	 * Transpose matrix. Expensive.
	 * This sparse matrix is converted to CSR using
	 * the provided storage of CSC. 
	 */	
	public def T(tm:SparseCSC(N,M)):void {
		val csr = new SparseCSR(M, N, tm.ccdata);
		toCSR(csr);		
	}

	//=====================================================================
	// Cell-wise operation methods
	//=====================================================================

    /**
	 * Raise each nonzero element in the sparse matrix by a factor of double value.
	 *
	 * @param alpha     scaling factor
	 * @return          return matrix of this instance
     */
	public def scale(alpha:Double):SparseCSC(this) {
		val ca = getStorage();
		for (var c:Int=0; c<N; c++) {
			val cl = getCol(c);
			for (var e:Int=0; e<cl.length; e++)
				ca.value(cl.offset+e) *= alpha;
		}
		return this;
	}

    /**
	 * Raise each nonzero element in the sparse matrix by a factor of specified integer.
	 *
	 * @param alpha     scaling factor
	 * @return          return matrix of this instance
     */
	public def scale(alpha:Int) = scale(alpha as Double);

	//--------------------------
	// Cellwise addition
	//--------------------------
    /**
     * Return this += x; not supported
     */
    public def cellAdd(x:Matrix(M,N)):SparseCSC(this) {
    	throw new UnsupportedOperationException("Cell-wise addition does not support using SparseCSC as output matrix");
    }
    
    public def cellAdd(d:Double):SparseCSC(this) {
    	throw new UnsupportedOperationException("Cell-wise addition does not support using SparseCSC as output matrix");
    }
    
	/**
	 * x = this + x
	 *
	 * @param x     the first input and output matrix
	 * @return      output result matrix.
	 */
    protected def cellAddTo(x:DenseMatrix(M,N)) {
		SparseAddToDense.comp(this, x);
		return x;
	}

	//-----------------------------
	// Subtract operation
	//-----------------------------
    /**
     * Return this = this - x, not supported
     */
    public def cellSub(x:Matrix(M,N)) {
		Debug.exit("Cell-wise subtraction does not support using SparseCSC as output matrix");
		return this;
    }
    
	/**
	 * x = x - this
	 *
	 * @param x     first input matrix in subtraction
	 * @return      result x - this
	 */
	protected def cellSubFrom(x:DenseMatrix(M,N)) {
		SparseSubToDense.comp(x, this);
		return x;
	}
	
	public def cellSubFrom(dv:Double): SparseCSC(this) {
		throw new UnsupportedOperationException("Cell-wise addition does not support using SparseCSC as output matrix");		
	}
	
	//-------------------------
	// Cellwise multiplication
	//-------------------------

    /**
     * Return this = this &#42 x, not supported
     */
    public def cellMult(x:Matrix(M,N)) {
		Debug.exit("Cell-wise multiplication does not support using SparseCSC to store result");
		return this;
    }

	/**
	 * x = x &#42 this
	 */
	protected def cellMultTo(x:DenseMatrix(M,N)) {
		var off:Int = 0;
					   
		for (var c:Int=0; c<N; c++) {
			val aln = getCol(c);
			for (var r:Int=0; r<aln.size(); r++) {
				x.d(off+aln.getIndex(r)) *= aln.getValue(r);
			}
			off += M;
		}
		return x;
	}


	//------------------------
	// Cellwise divison
	//------------------------
    /**
     * Return this = this / x, not supported
     */
    public def cellDiv(x:Matrix(M,N)) {
		Debug.exit("Cell-wise division does not support using SparseCSC to store result");
		return this;
    }

	/**
	 * x = this / x
	 */
	protected def cellDivBy(x:DenseMatrix(M,N)) {
		SparseDivToDense.comp(this, x);
		return x;
	}

    //========================================================================
	// Matrix multiplication method
    //========================================================================
    /**
     * Not support. Sparse matrix cannot be used to store multiplication result.
	 */
	public def mult(
			A:Matrix(this.M), 
			B:Matrix(A.N,this.N), 
			plus:Boolean):SparseCSC(this) {
		
		Debug.exit("Not supported. Use SparseMultSparseToDense,"+
				   "or SparseMultDenseToDense or DenseMultSparseToDense " +
				   "corresponding multiplication method");
		return this;
	}
	
	/** 
	 * Not support. 
	 */
	public def transMult(
			A:Matrix{self.N==this.M}, 
			B:Matrix(A.M,this.N), 
			plus:Boolean):SparseCSC(this) {
	
	   Debug.exit("Not support");			 
	   return this;
	}
    
	/** 
	 * Not support.
	 */
	public def multTrans(
			A:Matrix(this.M), 
			B:Matrix(this.N, A.N), 
			plus:Boolean):SparseCSC(this) {
		
       Debug.exit("Not support");			 
	   return this;
    }
    //------------------------------------------------------

	//-------------------------------
	// Operator overloaded
	//-------------------------------

    /**
     * Scaling operation return this &#42 double
     */
    public operator this * (dblv:Double):SparseCSC(M,N) {
        val x = clone();
        x.scale(dblv);
        return x;
    }
    /**
     * Scaling operation return this &#42 integer
     */
    public operator this * (intv:Int):SparseCSC(M,N) = this * (intv as Double);
 
	public operator (dblv:Double) * this = this * dblv;
	public operator (intv:Int)    * this = this * intv;
	
    //========================================================================
	// Add method
    //========================================================================
	
	//--------
	/**
	 *  Return this + that in a new dense 
	 */
	def add(that:SparseCSC(M,N)) : DenseMatrix(M,N) {
		val dm:DenseMatrix(M,N) = that.toDense();
		SparseAddToDense.comp(this, dm);
		return dm;
	}

	/**
	 *  Return this + that in a new dense 
	 */
	def add(that:SparseCSR(M,N)) : DenseMatrix(M,N) {
		val dm:DenseMatrix(M,N) = that.toDense();
		SparseAddToDense.comp(this, dm);
		return dm;
	}

	/**
	 *  Return this + that in a new dense 
	 */
	def add(that:DenseMatrix(M,N)): DenseMatrix(M,N) {
		val dm:DenseMatrix(M,N) = that.clone();
		SparseAddToDense.comp(this, dm);
		return dm;
	}


	//------------------------------
	// Add operator overloading
	//------------------------------
	/**
		Add this with another matrix. 
	*/
	public operator this + (that:SparseCSC(M,N))  :DenseMatrix(M,N) = this.add(that);
	public operator this + (that:SparseCSR(M,N))  :DenseMatrix(M,N) = this.add(that);
	public operator this + (that:DenseMatrix(M,N)):DenseMatrix(M,N) = this.add(that);
	public operator (that:DenseMatrix(M,N)) + this:DenseMatrix(M,N) = this.add(that);
	public operator this + (dv:Double)            :DenseMatrix(M,N) = this.toDense().cellAdd(dv);
	public operator (dv:Double) + this            :DenseMatrix(M,N) = this.toDense().cellAdd(dv);
	
	//----------------------------
	/**
	 *  Return this - that in a new dense 
	 */
	def sub(that:SparseCSC(M,N)) : DenseMatrix {
		val dm:DenseMatrix(M,N) = this.toDense();
		SparseSubToDense.comp(dm, that);
		return dm;
	}

	/**
	 *  Return this - that in a new dense 
	 */
	def sub(that:SparseCSR(M,N))  : DenseMatrix {
		val dm:DenseMatrix(M,N) = this.toDense();
		SparseSubToDense.comp(dm, that);
		return dm;
	}

	/**
	 *  Return this - that in a new dense 
	 */
	def sub(that:DenseMatrix(M,N))  : DenseMatrix {
		val dm:DenseMatrix(M,N) = that.clone();
		SparseSubToDense.comp(this, dm);
		return dm;
	}
	//------------------------------
	// Sub operator overloading
	//------------------------------
	/**
		Sub this with another matrix. 
	*/
	public operator this - (that:SparseCSC(M,N))   = this.sub(that);
	public operator this - (that:SparseCSR(M,N))   = this.sub(that);
	public operator this - (that:DenseMatrix(M,N)) = this.sub(that);
	
	public operator (that:DenseMatrix(M,N)) - this :DenseMatrix(M,N) {
		val dm:DenseMatrix(M,N) = that.clone();
		SparseSubToDense.comp(dm, this);
		return dm;
	}
	
	public operator this - (dv:Double)  :DenseMatrix(M,N) = this.toDense().cellSub(dv);
	public operator (dv:Double) - this  :DenseMatrix(M,N) = this.toDense().cellSubFrom(dv);
	
	//========================================================================
	// Cellwise mult method
	//========================================================================
	public operator this * (that:SparseCSC(M,N))   = this.cellMultTo(that.toDense()) as DenseMatrix(M,N);
	public operator this * (that:SparseCSR(M,N))   = this.cellMultTo(that.toDense()) as DenseMatrix(M,N);
	public operator this * (that:DenseMatrix(M,N)) = this.cellMultTo(that) as DenseMatrix(M,N);	
	public operator (that:DenseMatrix(M,N)) * this = this.cellMultTo(that) as DenseMatrix(M,N);	
	
	//========================================================================
	// Cellwise div method
    //========================================================================
	
	/**
	 *  Return this / that in a new dense 
	 */
	def div(that:SparseCSC(M,N)) : DenseMatrix(M,N) {
		val dm:DenseMatrix(M,N) = that.toDense();
		SparseDivToDense.comp(this, dm);
		return dm;
	}

	/**
	 *  Return this / that in a new dense 
	 */
	def div(that:SparseCSR(M,N)) : DenseMatrix(M,N) {
		val dm:DenseMatrix(M,N) = that.toDense();
		SparseDivToDense.comp(this, dm);
		return dm;
	}

	/**
	 *  Return this / that in a new dense 
	 */
	def div(that:DenseMatrix(M,N)): DenseMatrix(M,N) {
		val dm:DenseMatrix(M,N) = that.clone();
		SparseDivToDense.comp(this, dm);
		return dm;
	}

	//------------------------------
	// Div operator overloading
	//------------------------------
	/**
	 * This is divided by that and return result in dense matrix. 
	 */
	public operator this / (that:SparseCSC(M,N))   = this.div(that);
	public operator this / (that:SparseCSR(M,N))   = this.div(that);
	public operator this / (that:DenseMatrix(M,N)) = this.div(that);

	public operator (that:DenseMatrix(M,N)) / this :DenseMatrix(M,N) {
		val dm:DenseMatrix(M,N) = that.clone();
		SparseDivToDense.comp(dm, this);
		return dm;
	}
	
	public operator this / (dv:Double) :DenseMatrix(M,N) = this.toDense().cellDiv(dv);
	
	//========================================================================
	// matrix multiply
	//========================================================================
	
	/**
	 * Perform matrix multiply between two sparse csc matrices
	 */
	public operator this % (that:SparseCSC{self.M==this.N}) 
		= SparseMultSparseToDense.comp(this, that);
	public operator this % (that:SparseCSR{self.M==this.N}) 
		= SparseMultSparseToDense.comp(this, that);
	public operator this % (that:DenseMatrix{self.M==this.N}) 
		= SparseMultDenseToDense.comp(this, that);
	public operator (that:DenseMatrix{self.N==this.M}) % this 
		= DenseMultSparseToDense.comp(that, this);

    //========================================================================
	// Util
    //========================================================================

	//---------
	public def likeMe(m:Matrix):Boolean {
		return m instanceof SparseCSC && m.M==M && m.N==N;
	}

	//---------
	//public def equal(m:SparseCSC(M,N)) = equals(m);
	public def equals(m:SparseCSC(M,N)) = 
		VerifyTools.testSame(this as Matrix(M,N), m as Matrix(M,N));

	public def equals(m:SparseCSR(M,N)) = 
		VerifyTools.testSame(this as Matrix(M,N), m as Matrix(M,N));

    //========================================================================
	
	public def toString():String {
		val outstr:String =  
			"------- Sparse Matrix in CSC "+M+"x"+N+"-------\n"+
			this.ccdata.toString() +
			"-----------------------------------------------\n";
		return outstr;
	}

	/**
	   Print the sparse matrix in CSC format
	*/
	public def print(msg:String) {
		val outstr:String = msg + toString(); 
		Console.OUT.print(outstr);
		Console.OUT.flush();
	}
	//
	public def print() { print("");}

	//-----------
	public def debugPrint(msg:String) {
		if (Debug.disable) return;
		
		Debug.println(msg+toString());	
		Debug.flush();
	}
	//
	public def debugPrint() { debugPrint("");}
	//-----------

	//
	//---------------------------
	// X10 Int MAX_VALUE is 2*10^10, change M*N to Double, in case
	// exceeding MAX_VALUE
	public static def compAllocSize(m:Int, n:Int, nz:Double):Int {
		var nzd:Double = nz;
		if (nzd > 1.0) {
			Debug.flushln("Nonzero density "+nzd+" > 1.0, reset to 1.0");
			nzd = 1.0;
		}
		
		var tc:Double = nzd * n * m;
		if (tc > Int.MAX_VALUE) {
			Console.OUT.printf("Warning: size %f exceeds maximum value %d\n", 
							   tc, Int.MAX_VALUE);
			Console.OUT.flush();
			return Int.MAX_VALUE;
		}
		//Console.OUT.println("Computed the memory size: "+retval+" for CSC density:"+nzd);
		return Math.ceil(tc) as Int;
	}
	//-------------------------
	public def printRandomInfo():void {
		val nzc = countNonZero();
		val nzd = compSparsity();
		val avgdst = ccdata.compAvgIndexDst();
		val stdlnz = ccdata.compLineSizeStdDvn();
		Console.OUT.println("Sparse matrix ["+M+","+N+"] nz count:"+nzc+" sparsity:"+nzd);
		Console.OUT.println("Mean adjacent nonzero index distance: " + avgdst);
		Console.OUT.println("Compressed column nonzero size std deviation:"+stdlnz);
		Console.OUT.flush();
	}
}
