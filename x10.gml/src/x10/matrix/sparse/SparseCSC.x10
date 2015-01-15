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

import x10.util.Pair;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.util.MathTool;
import x10.matrix.DenseMatrix;
import x10.matrix.util.VerifyTool;
import x10.matrix.builder.SparseCSCBuilder;
import x10.matrix.ElemType;

public type SparseCSC(M:Long)=SparseCSC{self.M==M};
public type SparseCSC(M:Long,N:Long)=SparseCSC{self.M==M, self.N==N};
public type SparseCSC(C:Matrix)=SparseCSC{self==C};
public type SparseCSC(C:SparseCSC)=SparseCSC{self==C};

/**
 * This sparse matrix class uses CSC-LT format to store matrix nonzero elements.
 * The underlying storage is defined in CompressArray.
 * 
 * <p> For testing purposes, the initialization of nonzero elements in the same
 * column are generated one after another.
 * The distance between two adjacent nonzero elements in the same column
 * is generated from the uniform distribution over [1..2/sparsity].
 */
public class SparseCSC extends Matrix {
	/**
	 * Compress 2-dimension array
	 */
	public val ccdata:Compress2D;
		
	// Temporary memory space used for type conversion and
	// data compression
	private var tmprow:Rail[ElemType];
	private var tmpcol:Rail[ElemType];

	// Used for serialization index value, reset or build 
	private var copyColOff:Long;
	private var copyColCnt:Long;
	private var copyDataCnt:Long;

	/**
	 * Construct a sparse matrix instance compressed in column major 
	 * order (CSC)
	 *
	 * @param m     Number of rows in the CSC sparse matrix
	 * @param n     Number of columns in the CSC sparse matrix
	 * @param cd     Compress 2D data structure, which contains the
	 *              the compressed array data storage
	 */
	public def this(m:Long, n:Long, cd:Compress2D):SparseCSC(m,n) {
		super(m, n);
		assert n <= cd.size();

		ccdata = cd;
		//No memory allocation for temp space
		tmprow = new Rail[ElemType](0);
		tmpcol = new Rail[ElemType](0);
	}

	/**
	 * Construct a CSC instance using specified compressed array data storage.
	 *
	 * @param m     Number of rows in the CSC sparse matrix
	 * @param n     Number of columns in the CSC sparse matrix
	 * @param ca     The data storage of compressed array.
	 */
	public def this(m:Long, n:Long, ca:CompressArray):SparseCSC(m,n) {
		super(m, n);
		ccdata = Compress2D.make(n, ca);

		tmprow = new Rail[ElemType](0);
		tmpcol = new Rail[ElemType](0);
	}

	/**
	 * Create a sparse matrix instance with memory storage for specified 
	 * number of nonzero elements
	 *
	 * @param m     Number of rows in the CSC sparse matrix
	 * @param n     Number of columns in the CSC sparse matrix
	 * @param nzcnt     Number of nonzero elements of the sparse matrix
	 */
	public static def make(m:Long, n:Long, nzcnt:Long //Number of non-zero entries
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
	public static def make(m:Long, n:Long, nzd:Float) : SparseCSC(m,n) {
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
	//public static def make(m:Long, n:Long) = SparseCSC.make(m, n, m*n);
									   

	/**
	 * Create a m x n SparseCSC matrix based on CSC data format input
	 * ia, ja, and av.
	 *
	 * @param ia     integer array, the count of nonzero of each column.
	 * @param ja     integer array, the surface indices of nonzeros in column
	 * @param av     double array, the actual matrix element data corresponding
	 *              to its surface index at the same position in ja.
	 */
	public static def make(m:Long,n:Long,	
						   ia:Rail[Long]{self!=null},
						   ja:Rail[Long]{self!=null},
						   av:Rail[ElemType]{self!=null,self.size==ja.size}
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
    public static def make(m:Long,n:Long, ca:CompressArray):SparseCSC{
    	val ccd = Compress2D.make(n, ca);
    	ccd.buildIndex(m);
	   	return new SparseCSC(m, n, ccd);
    }
		   

    // Initialization

    /**
     * For testing purposes,
     * <p> Initialize sparse matrix with a specified value, selecting
     * indices for nonzero elements using the random-fast method.
     * The distance between two adjacent nonzero elements in the same column
     * is generated from the uniform distribution over [1..2/sparsity].
     *
     * @param v      Initial value for all elements
     * @param sp     Nonzero sparsity
     * @see Compress2D.initConst()
     */
	public def init(v:ElemType, sp:Float):SparseCSC(this) {
		val cnt = ccdata.initConst(M, v, sp);
		return this;
	}
	

	/**
	 * Initialize sparse matrix elements to specified value.
	 *
	 * @param v     initial value for all nonzero elements.
	 */
	public def init(v:ElemType):SparseCSC(this) {
	    val nzd = 1.0f*getStorageSize()/M/N;
	    init(v, nzd);
	    return this;
	}
	
	/**
	 * For testing purposes,
	 * <p> Initialize sparse matrix elements with random values, selecting
	 * indices for nonzero elements using the random-fast method.
	 * The distance between two adjacent nonzero elements in the same column
	 * is generated from the uniform distribution over [1..2/sparsity].
	 * 
	 * @param lb     lower bound of random value
	 * @param up     upper bound of random value
	 * @param sp     Nonzero sparsity
	 * @see init(v:ElemType, sp:ElemType)
	 */
	public def initRandom(lb:Long, ub:Long, sp:Float) : SparseCSC(this) {
		val cnt = ccdata.initRandomFast(M, sp, lb, ub);
		return this;
	}
	
	public def initRandom(sp:Float) : SparseCSC(this) {
		val cnt = ccdata.initRandomFast(M, sp);
		return this;
	}

    /**
     * For testing purposes,
     * <p> Use the size of available storage space to compute sparsity, and
     * then initialize sparse matrix elements with random values.
     * 
     * @param lb     lower bound of random value
     * @param up     upper bound of random value
     * @see initRandom(lb:Long, ub:Long, sp:ElemType)
     */
	public def initRandom(lb:Long, ub:Long): SparseCSC(this) { 
	    val nzd = 1.0f * getStorageSize() /M/N;
	    initRandom(lb, ub, nzd);
	    return this;
	}
	
	public def initRandom(): SparseCSC(this) { 
	    val nzd = 1.0f * getStorageSize() /M/N;
	    initRandom(nzd);
	    return this;
	}

	/**
	 * Initialize with given function with range [0..M, 0..N]
	 */
	public def init(f:(Long, Long)=>ElemType): SparseCSC(this) {
		var offset:Long=0;
		val ca = getStorage();
		for (var c:Long=0; c<N; c++) {
			val ccol = ccdata.cLine(c);
			ccol.offset = offset;
			for (var r:Long=0; r<M&&offset<ca.index.size; r++) {
				val nzval  = f(r, c);
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
	public def init(fidx:(Long, Long)=>Long, fval:(Long, Long)=>ElemType): SparseCSC(this) {
		var offset:Long=0;
		val ca = getStorage();
		for (var c:Long=0; c<N; c++) {
			val ccol = ccdata.cLine(c);
			ccol.offset = offset;
			for (var r:Long=0; r<M&&offset<ca.index.size; r++) {
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
	public def init(rowoff:Long, coloff:Long, f:(Long, Long)=>ElemType): SparseCSC(this) {
		
		var offset:Long=0;
		val ca = getStorage();
		for (var c:Long=0; c<N; c++) {
			val ccol = ccdata.cLine(c);
			ccol.offset = offset;
			for (var r:Long=0; r<M&&offset<ca.index.size; r++) {
				val nzval = f(r+rowoff, c+coloff);
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
	 * For testing purposes,
	 * <p> Create a sparse matrix instance and initialize with random values
	 * and positions of nonzero positions computed randomly.
	 * 
	 * @param m     Number of rows in the matrix
	 * @param n     Number of columns in the matrix
	 * @param nzd     Nonzero sparsity
	 * @see make() and initRandom() 
	 */
	public static def makeRand(m:Long, n:Long, nzd:Float) : SparseCSC(m,n) {
		val csc = SparseCSC.make(m, n, nzd);
		csc.initRandom(nzd);

		return csc;
	}


	// Compress 2D stored in column-wise way in column-compressed 2D

	/**
	 * Allocate sparse matrix storage with the same memory storage. 
	 *
	 * @param  m      number of rows
	 * @param  n      number of columns
	 */
	public def alloc(m:Long, n:Long):SparseCSC(m,n) {
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
	public def reset(coloff:Long) :void {
		ccdata.reset(coloff);
	}

	// Data access

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
	public operator this(r:Long, c:Long):ElemType = ccdata(c, r);
	public operator this(a:Long):ElemType = ccdata(a/M,a%M);
	



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
	public operator this(r:Long, c:Long) = (v:ElemType):ElemType {
	    ccdata(c)=Pair[Long,ElemType](r,v);
	    return v;
	}
	

	// Can be used for disjoint storage
	public def setCol(c:Long, ln:Compress1D) { 
		ccdata.setLine(c, ln);
	}
	public def compressAt(c:Long, off:Long, d:Rail[ElemType]) =
		ccdata.cLine(c).compressAt(off, d);

	// No setRow method available


	// Copy the memory locations of columns into a new SparseCSC, no memory allocation
	// for matrix data

	// Get one compressed column line
	public def getCol(col:Long) = ccdata.getLine(col);
	

	// Copy the rows into a new SparseCSC

	// Get one compressed row line , no need to reset tmprow
	// This needs to be removed
	public def getRow(row:Long) : Compress1D {
		val tr = getTempRow();
		extractRow(row, tr);
		return Compress1D.compress(tr);
	}
	// When using resetCols, must not use serilaizing index
	public def resetCols(stln:Long, 
						 cnt:Long, 
						 src:SparseCSC
						 ) : void {
		ccdata.resetCols(stln, cnt, src.ccdata);
	}


	// Sequentialize and desequential index for communication

	
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
	public def initRemoteCopyAtSource(coloff:Long, colcnt:Long):Long {
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
	public def initRemoteCopyAtDest(coloff:Long, colcnt:Long, datcnt:Long) : void {
		copyColOff = coloff;
		copyColCnt = colcnt;
		copyDataCnt= datcnt;
		//Perform storage size check
		val datoff = getNonZeroOffset(coloff);
		testIncStorage(datoff, datcnt); // If storage short, re-allocate storage
	}

	public def initRemoteCopyAtDest(datcnt:Long): void {
		initRemoteCopyAtDest(0, N, datcnt);
		reset();
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
	public def finalizeRemoteCopyAtDest(coloff:Long, colcnt:Long, datcnt:Long): void {
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
	public def serializeIndex(coloff:Long, colcnt:Long) :void {
		copyDataCnt = ccdata.serializeIndex(this.M, coloff, colcnt);
	}


	/**
	 * Reverse serialization.
	 */
	public def resetIndex(coloff:Long) : void {
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
	public def buildIndex(coloff:Long, colcnt:Long, datcnt:Long):Long =
		ccdata.buildIndex(this.M, coloff, colcnt, datcnt); 
	


	// Access temporary space

	public def getTempCol() : Rail[ElemType] {
		if (tmpcol.size == 0L)
			tmpcol = new Rail[ElemType](this.M);
		else {
			for (var i:Long=0; i<this.M; i++) tmpcol(i)=0.0 as ElemType;
		}
		return tmpcol;
	}

	public def getTempRow() : Rail[ElemType] {
		if (tmprow.size == 0L) 
			tmprow = new Rail[ElemType](this.N);
		else {
			// reset the temp Rail
			for (var i:Long=0; i<this.N; i++) tmprow(i) = 0.0 as ElemType;
		}
		return tmprow;
	}

	public def getTemp(n:Long) : Rail[ElemType] {
		if (n > tmpcol.size) tmpcol = new Rail[ElemType](n);
		return tmpcol;
	}


	// Copy sparse matrix compress data


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
	public static def copyCols(src:SparseCSC, srcColOffset:Long,
							   dst:SparseCSC, dstColOffset:Long, colcnt:Long) :Long =
		Compress2D.copy(src.ccdata, srcColOffset, dst.ccdata, dstColOffset, colcnt);

	/**
	 * Copy all columns from source to target. If target has more columns,
	 * they are reset to 0 length of nonzero elements.
	 *
	 * @param src           The source sparse matrix
	 * @param dst           The target sparse matrix
	 */
	public static def copy(src:SparseCSC, dst:SparseCSC) : Long =
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
	public static def copyRows(src:SparseCSC, srcRowOffset:Long, 
							   dst:SparseCSC, dstRowOffset:Long{self==0L}, rowcnt:Long) : Long =
		Compress2D.copySection(src.ccdata, srcRowOffset, dst.ccdata, rowcnt);



	// Target is matrix, obsolete
	private def copyColsToSparse(col:Long,        //Starting column
								cnt:Long,        //Number of columns to copy
								csc:SparseCSC   //Target sparse matrix
								):Long {         //Return number of data copied
		assert (cnt == csc.N && this.M==csc.M);
		csc.reset();
		return ccdata.copyLinesToC2D(col, cnt, csc.ccdata);
	}
	// Copy the rows into a new SparseCSC
	// Get multiple rows and put them in compressed-column format (SparseCSC)
	private def copyRowsToSparse(row:Long,          //Starting row
								cnt:Long,            //Number of rows
								csc:SparseCSC       //Target sparse matrix CSC
								):Long{              //Return number of data copied
		assert (this.N==csc.N && cnt == csc.M);
		csc.reset();
	    return ccdata.copySectionToC2D(row, cnt, csc.ccdata);
	}


	// Target is CompressArray
	// Copy cnt columns starting from col to CompressArray
	// There is no sizes info for each column 
	// Return the number of data (Index, ElemType) copied
	private def copyColsToCArray(start:Long,              // Starting column
								cnt:Long,                // Number of columns
								ca:CompressArray // Target memory storage
								): Long  // Return number of data copied
		= ccdata.copyLinesToCArray(start, cnt, ca);
   
	private def copyRowsToCArray(start:Long,             // Starting rows
								cnt:Long,               // Number of rows
								ca:CompressArray //Target memory storage
								):Long  // Return number of data copied
		= ccdata.copySectionToCArray(start, cnt, ca);
		

	
	/**
	 * Test and increase storage 
	 *
	 * @param datoff     starting position in storage for new data
	 * @param datcnt     number of new elements will be added
	 * @return true if storage is re-allocated.
	 */
	public def testIncStorage(datoff:Long, datcnt:Long) = 
		ccdata.getStorage().testIncStorage(datoff, datcnt);
	
	/**
	 * Test and increase storage 
	 *
	 * @param datcnt     number of new elements will be added
	 * @return true if storage is re-allocated.
	 */
	public def testIncStorage(datcnt:Long) = 
		ccdata.getStorage().testIncStorage(datcnt);
	


	// Extract data from columns and put the result in array


	// Get decompressed column in an array
	// The target ln array needs to be reset first
	public def extractCol(col:Long, ln:Rail[ElemType]) {
		ccdata.getLine(col).extract(ln);
	}
	
	public def extractRow(row:Long, cl:Rail[ElemType]):void {
		for (var i:Long=0; i<this.N; i++) cl(i) = this(row, i);
	}


	// Using tmp storage place
	public def extractCol(col:Long):Rail[ElemType] {
		val tc = getTempCol();
		extractCol(col, tc);
		return tc;
	}

	public def extractRow(row:Long):Rail[ElemType] {
		val tr = getTempRow();
		extractRow(row, tr);
		return tr;
	}


	// Extract data to dense matrix


	/**
	 * Expand multiple compressed columns into the dense matrix
	 * For very sparse matrix, this operation is expensive, 
	 * 
	 * @param start_col     the starting column
	 * @param num_col       number of columns to extract
	 * @param dm            the target dense matrix to hold the expanded data
	 */
	public def extractCols(start_col:Long, 
						   num_col:Long, 
						   dm:DenseMatrix{self.M==this.M,self.N==num_col}
						   ) : void {
		assert (this.M <= dm.M && num_col <= dm.N);

		var dstoff:Long = 0;//offset
		for (var y:Long=start_col; y<start_col+num_col; y++) {
			ccdata.cLine(y).extract(dstoff, dm.d);
			dstoff+= dm.M;
		}
	}

	//public def extractCols(start_col:Long, num_col:Long):DenseMatrix {
	//	val dm = new DenseMatrix(num_col, this.N);
	//	extractCols(start_col, num_col, dm);
	//	return dm;
	//}


	// Get data from multiple rows and store them in dense matrix 

	/**
	 * Expand multiple compressed rows into the dense matrix.
	 * The target dense matrix is reset.
	 * @param start_row     the starting row
	 * @param num_row       number of rows to extract
	 * @param dm            the target dense matrix to hold the expanded data
	 */
	public def extractRows(start_row:Long, 
						   num_row:Long, 
						   dm:DenseMatrix{self.M==num_row,self.N==this.N}
						   ):void {
		assert (num_row <= dm.M && this.N <= dm.N);
		var colst:Long = 0;//offset
		for (var y:Long=0; y<this.M; y++, colst+=dm.M) {
			val cl = ccdata.getLine(y);
			cl.extract(start_row, num_row, colst, dm.d);
		}
	}

	public def extractRows(start_row:Long, 
						   num_row:Long
						   ): DenseMatrix(num_row,this.N) {
		val dm = new DenseMatrix(num_row, this.N);
		extractRows(start_row, num_row, dm);
		return dm;
	}

	// Return the non-zero density 

	/**
	 * Compute nonzero sparsity in storage
	 */
	public def compSparsity():Float {
		val nz:Float = ccdata.countNonZero();
		return nz/(this.M*this.N);
	}

	/**
	 * Get number of nonzero elements in specified column
	 */
	public def getColNonZeroCount(col:Long) = ccdata.cLine(col).length;

	/**
	 * Get the offset in CompressArray for the col-th column starting position
	 */
	public def getNonZeroOffset(col:Long) = 
		(col==0L?0L:ccdata.cLine(col-1).offset+ccdata.cLine(col-1).length);

	//public def getColOffset(col:Long) = ccdata.cLine(col).offset;

	/**
	 * Count the number of nonzero in the specified range of columns
	 *
	 * @param coloff     Offset column
	 * @param colcnt     Number of columns
	 * @return     the number of elements in compressed array
	 */
	public def countNonZero(coloff:Long, colcnt:Long):Long =
		ccdata.countNonZero(coloff, colcnt);

	/**
	 * Count nonzeros in compress array.
	 * This method will check nonzeros in all columns
	 */
	public def countNonZero():Long = countNonZero(0, N);

	/**
	 * Get the nonzero count from storage compress array.
	 */
	public def getNonZeroCount():Long = getStorage().count; 

	/**
	 * Get storage size.
	 */
	public def getStorageSize() = getStorage().storageSize();
	

	// Format conversion: to SCR and dense matrix

	/**
	 * Convert to a new SparseCSR. This operation is expensive
	 */
	public def toCSR():SparseCSR(M,N) {
		val sm = SparseCSR.make(this.M, this.N, countNonZero() as Long);
		toCSR(sm);
		return sm;
	}

	/**
	 * Convert to SparseCSR using provided memory space
	 * This operation is expensive
	 */	
	public def toCSR(sm:SparseCSR(M,N)):void {
		assert (this.M == sm.M && this.N == sm.N);
		var off:Long=0;
		val tr = getTempRow();
		for (var r:Long=0; r<this.M; r++) {
			extractRow(r, tr);
			off += sm.compressAt(r, off, tr);
			//crd.setLine(r, Compress1D.compress(tmprow));
		}
	}

	/**
	 * Copy data to dense matrix
	 */
	public def copyTo(dm:DenseMatrix(M,N)): void {
		extractCols(0, this.N, dm);
	}

	public static def copyTo(sp:SparseCSC, dm:DenseMatrix, roff:Long, coff:Long): void {

		var dstoff:Long = roff + coff*dm.M;
		for (var col:Long=0; col<sp.N; col++, dstoff+=dm.M)
			sp.ccdata.cLine(col).extract(dstoff, dm.d);
	}
	
	public def copyTo(that:SparseCSC(M,N)) = copy(this, that);
	
	public def copyTo(that:Matrix(M,N)):void {
		if (that instanceof DenseMatrix)
			copyTo(that as DenseMatrix);
		else if (that instanceof SparseCSC)
			copyTo(that as SparseCSC);
		else
			throw new UnsupportedOperationException("copyTo: target matrix type not supported");	
	}
	
	/**
	 * Convert to a new dense matrix object
	 */
	public def toDense():DenseMatrix(M,N) {
		val dm= DenseMatrix.make(this.M, this.N);
		
		extractCols(0, this.N, dm);
		return dm;
	}


	// Transpose methods


	/**
	 * Convert to a new SparseCSR object in transposed 
	 */
	public def TtoCSR():SparseCSR{self.M==this.N, self.N==this.M} {
		return new SparseCSR(this.N, this.M, this.ccdata);
	}
	
	// /**
	//  * Transpose matrix. Expensive.
	//  * This sparse matrix is converted to CSR using
	//  * the provided storage of CSC. 
	//  */	
	// public def T(tm:SparseCSC(N,M)):void {
	// 	val csr = new SparseCSR(M, N, tm.ccdata);
	// 	toCSR(csr);		
	// }
	
	/*
	 * Tranpose sparse matrix and put result back to the origninal compress array storage space.
	 * The return instance is a new SparseCSC using the original storage, and original (this) instance
	 * is no longer valid sparse matrix instance.
	 * "this instance" is no longer a valid sparse matrix instance. This method uses less memory space
	 * tha T().
	 * Using SparseCSCBuilder is more efficient than converting to CSR format
	 */
	public def selfT() : SparseCSC(N,M) {
		val nspa = new SparseCSC(N,M, this.getStorage()) as SparseCSC(N,M);
		val sbdr = new SparseCSCBuilder(nspa);
		sbdr.initTransposeFrom(this).toSparseCSC();

		return nspa;
	}
	
	/*
	 * Transpose matrix and put the result in a new sparse matrix instance.
	 * The original (this) instance is not changed. 
	 */
	public def T():SparseCSC(N,M) {
		val nspa = SparseCSC.make(N,M, this.getStorage().count);
		T(nspa);
		return nspa;
	}

	/*
	 * Tranpose the sparse matrix data and store it in provided SparseCSC
	 * instance.  The original matrix instance (this) is not changed 
	 * (if "this" is not used as the input)
	 */
	public def T(spa:SparseCSC(N,M)) : void {
		val sbdr = new SparseCSCBuilder(spa);
		sbdr.initTransposeFrom(this as SparseCSC(sbdr.N,sbdr.M)).toSparseCSC();
	}
	

	// Cell-wise operation methods


    /**
	 * Raise each nonzero element in the sparse matrix by a factor of double value.
	 *
	 * @param alpha     scaling factor
	 * @return          return matrix of this instance
     */
	public def scale(alpha:ElemType):SparseCSC(this) {
		val ca = getStorage();
		for (var c:Long=0; c<N; c++) {
			val cl = getCol(c);
			for (var e:Long=cl.offset; e<cl.offset+cl.length; e++)
				ca.value(e) *= alpha;
		}
		return this;
	}

	public def sum():ElemType {
		var tt:ElemType=0.0 as ElemType;
		val ca = getStorage();
		for (var c:Long=0; c<N; c++) {
			val cl = getCol(c);
			for (var e:Long=cl.offset; e<cl.offset+cl.length; e++)
				tt += ca.value(e);
		}	
		
		return tt;
	}
	

	// Cellwise addition

    /**
     * Return this += x; not supported
     */
    public def cellAdd(x:Matrix(M,N)):SparseCSC(this) {
    	throw new UnsupportedOperationException("Cell-wise addition does not support using SparseCSC as output matrix");
    }
    
    public def cellAdd(d:ElemType):SparseCSC(this) {
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

    /**
     * Return this = this - x, not supported
     */
    public def cellSub(x:Matrix(M,N)):Matrix(this) {
		throw new UnsupportedOperationException("Cell-wise subtraction does not support using SparseCSC as output matrix");
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

    /**
     * Return this = this &#42 x, not supported
     */
    public def cellMult(x:Matrix(M,N)):Matrix(this) {
		throw new UnsupportedOperationException("Cell-wise multiplication does not support using SparseCSC to store result");
    }

	/**
	 * x = x &#42 this
	 */
	protected def cellMultTo(x:DenseMatrix(M,N)) {
		var off:Long = 0;
					   
		for (var c:Long=0; c<N; c++) {
			val aln = getCol(c);
			for (var r:Long=0; r<aln.size(); r++) {
				x.d(off+aln.getIndex(r)) *= aln.getValue(r);
			}
			off += M;
		}
		return x;
	}

    /**
     * Return this = this / x, not supported
     */
    public def cellDiv(x:Matrix(M,N)):Matrix(this) {
		throw new UnsupportedOperationException("Cell-wise division does not support using SparseCSC to store result");
    }

	/**
	 * x = this / x
	 */
	protected def cellDivBy(x:DenseMatrix(M,N)) {
		SparseDivToDense.comp(this, x);
		return x;
	}


	// Matrix multiplication method

    /**
     * Not support. Sparse matrix cannot be used to store multiplication result.
	 */
	public def mult(
			A:Matrix(this.M), 
			B:Matrix(A.N,this.N), 
			plus:Boolean):SparseCSC(this) {
		
		throw new UnsupportedOperationException("Not supported. Use SparseMultSparseToDense,"+
				   "or SparseMultDenseToDense or DenseMultSparseToDense " +
				   "corresponding multiplication method");
	}
	
	public def transMult(
			A:Matrix{self.N==this.M}, 
			B:Matrix(A.M,this.N), 
			plus:Boolean):SparseCSC(this) {
	
	   throw new UnsupportedOperationException("SparseCSC.transMult not supported");			 
	}
    
	public def multTrans(
			A:Matrix(this.M), 
			B:Matrix(this.N, A.N), 
			plus:Boolean):SparseCSC(this) {
		
       throw new UnsupportedOperationException("SparseCSC.multTrans not supported");			 
    }

	// Operator overloaded

    /**
     * Scaling operation return this &#42 double
     */
    public operator this * (dblv:ElemType):SparseCSC(M,N) {
        val x = clone();
        x.scale(dblv);
        return x;
    }
 
	public operator (dblv:ElemType) * this = this * dblv;

	// Add method
	
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

	// Add operator overloading

	/**
		Add this with another matrix. 
	*/
	public operator this + (that:SparseCSC(M,N))  :DenseMatrix(M,N) = this.add(that);
	public operator this + (that:SparseCSR(M,N))  :DenseMatrix(M,N) = this.add(that);
	public operator this + (that:DenseMatrix(M,N)):DenseMatrix(M,N) = this.add(that);
	public operator (that:DenseMatrix(M,N)) + this:DenseMatrix(M,N) = this.add(that);
	public operator this + (dv:ElemType)            :DenseMatrix(M,N) = this.toDense().cellAdd(dv);
	public operator (dv:ElemType) + this            :DenseMatrix(M,N) = this.toDense().cellAdd(dv);
	

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

	// Sub operator overloading

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
	
	public operator this - (dv:ElemType)  :DenseMatrix(M,N) = this.toDense().cellSub(dv);
	

	// Cellwise mult method

	public operator this * (that:SparseCSC(M,N))   = this.cellMultTo(that.toDense()) as DenseMatrix(M,N);
	public operator this * (that:SparseCSR(M,N))   = this.cellMultTo(that.toDense()) as DenseMatrix(M,N);
	public operator this * (that:DenseMatrix(M,N)) = this.cellMultTo(that) as DenseMatrix(M,N);	
	public operator (that:DenseMatrix(M,N)) * this = this.cellMultTo(that) as DenseMatrix(M,N);	
	

	// Cellwise div method
	
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


	// Div operator overloading

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
	
	public operator this / (dv:ElemType) :DenseMatrix(M,N) = this.toDense().cellDiv(dv);
	
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


	// Util

	public def likeMe(m:Matrix):Boolean {
		return m instanceof SparseCSC && m.M==M && m.N==N;
	}

	public def equals(m:SparseCSC(M,N)) = 
		VerifyTool.testSame(this as Matrix(M,N), m as Matrix(M,N));

	public def equals(m:SparseCSR(M,N)) = 
		VerifyTool.testSame(this as Matrix(M,N), m as Matrix(M,N));

	public def toString():String {
		val outstr:String =  
			"------- Sparse Matrix in CSC "+M+"x"+N+"-------\n"+
			this.ccdata.toString() +
			"-----------------------------------------------\n";
		return outstr;
	}

	// X10 Long MAX_VALUE, change M*N to ElemType, in case
	// exceeding MAX_VALUE
	public static def compAllocSize(m:Long, n:Long, nz:Float):Long {
		var nzd:Float = nz;
		if (nzd > 1.0) {
			Debug.flushln("Nonzero density "+nzd+" > 1.0, reset to 1.0");
			nzd = 1.0f;
		}
		
		var tc:Double = nzd * n * m;
		if (tc > Long.MAX_VALUE) {
			Console.OUT.printf("Warning: size %f exceeds maximum value %ld\n", 
							   tc, Long.MAX_VALUE);
			Console.OUT.flush();
			return Long.MAX_VALUE;
		}
		//Console.OUT.println("Computed the memory size: "+retval+" for CSC density:"+nzd);
		return Math.ceil(tc) as Long;
	}

	public def printStatistics():void {
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
