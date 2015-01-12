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

package x10.matrix.block;

import x10.matrix.Matrix;
import x10.matrix.util.RandTool;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.Compress2D;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.builder.SparseCSCBuilder;
import x10.matrix.builder.SymSparseBuilder;
import x10.matrix.builder.TriSparseBuilder;

/**
 * Sparse block is used to build sparse block matrix (at single place) or 
 * distributed sparse matrix at all places. 
 */
public class SparseBlock extends MatrixBlock {
	public val sparse:SparseCSC;
	public var builder:SparseCSCBuilder(sparse.M,sparse.N) = null;

	/**
	 * Construct a sparse CSC matrix block.
	 *
	 * @param  rid      block's row id
	 * @param  cid      block's column id
	 * @param  m        the sparse matrix in CSC format
	 */
	public def this(rid:Long, cid:Long, roff:Long, coff:Long, m:SparseCSC) {
		super(rid, cid, roff, coff);
		sparse = m;
	}

	/**
	 * Create a sparse-matrix block based on matrix partitioning.
	 *
	 * @param  gp      The grid partitioning of matrix
	 * @param  rid      block's row id
	 * @param  cid      block's column id
	 * @param  cdat     the sparse matrix's compress2D data structure
	 */
	public static def make(gp:Grid, 
						   rid:Long, cid:Long, 
						   cdat:Compress2D
						   ) : SparseBlock {
		val m = gp.rowBs(rid);
		val n = gp.colBs(cid);
		val smat = new SparseCSC(m, n, cdat);
		return new SparseBlock(rid, cid, gp.startRow(rid), gp.startCol(cid), smat);
	}
	
	/**
	 * Create a sparse-matrix block and allocate memory space for specified
	 * number of nonzero elements.
	 *
	 * @param   gp      The grid partitioning of matrix
	 * @param  rid      block's row id
	 * @param  cid      block's column id
	 * @param  nzcnt     number of nonzero element in block
	 */
	public static def make(gp:Grid, rid:Long, cid:Long, nzcnt:Long
						   ) : SparseBlock {
		val m = gp.rowBs(rid);
		val n = gp.colBs(cid);
		val smat = SparseCSC.make(m, n, nzcnt);
		return new SparseBlock(rid, cid, gp.startRow(rid), gp.startCol(cid), smat);
	}
	
	/**
	 * Create a sparse-matrix block and allocate memory space for 
	 * specified nonzer sparsity
	 *
	 * @param   gp      The grid partitioning of matrix
	 * @param  rid      block's row id
	 * @param  cid      block's column id
	 * @param  nzd      sparsity in block
	 */
	public static def make(gp:Grid, rid:Long, cid:Long, nzd:Double
						   ) : SparseBlock {
		val m = gp.rowBs(rid);
		val n = gp.colBs(cid);
		val nzcnt = (nzd*m*n) as Long;
		val smat = SparseCSC.make(m, n, nzcnt);
		return new SparseBlock(rid, cid, gp.startRow(rid), gp.startCol(cid), smat);
	}
	
	/**
	 * Create a sparse-matrix block and allocate memory space for 
	 * specified nonzer sparsity
	 * 
	 * @param   gp      The grid partitioning of matrix
	 * @param  rid      block's row id
	 * @param  cid      block's column id
	 * @param  m        rows in block
	 * @param  n        columns in block
	 * @param  nzd      sparsity in block
	 */
	public static def make(rid:Long, cid:Long, roff:Long, coff:Long, m:Long, n:Long, nzd:Double
	) : SparseBlock {
		val nzcnt = (nzd*m*n) as Long;
		val smat = SparseCSC.make(m, n, nzcnt);
		return new SparseBlock(rid, cid, roff, coff, smat);
	}
	
	/**
	 * Create a sparse-matrix block and allocate memory space for 
	 * specified nonzer sparsity
	 * 
	 * @param  rid      block's row id
	 * @param  cid      block's column id
	 * @param  m        rows in block
	 * @param  n        columns in block
	 * @param  nzcnt    nonzero count
	 */
	public static def make(rid:Long, cid:Long, roff:Long, coff:Long, m:Long, n:Long, nzcnt:Long
	) : SparseBlock {
		val smat = SparseCSC.make(m, n, nzcnt);
		return new SparseBlock(rid, cid, roff, coff, smat);
	}	
	
	public def getBuilder():SparseCSCBuilder(sparse.M,sparse.N) {
		if (builder == null)
			builder= new SparseCSCBuilder(sparse);
		return builder;
	}
	
	public def getSymBuilder():SparseCSCBuilder{self.M==self.N} {
		val bld = getBuilder();
		builder = new SymSparseBuilder(bld as SparseCSCBuilder{self.M==self.N}) as SparseCSCBuilder(sparse.M,sparse.N);
		return builder as SparseCSCBuilder{self.M==self.N};
	}
	
	public def getTriBuilder(up:Boolean):SparseCSCBuilder{self.M==self.N} {
		val bld = getBuilder();
		builder = new TriSparseBuilder(up, bld as SparseCSCBuilder{self.M==self.N}) as SparseCSCBuilder(sparse.M,sparse.N);
		return builder as SparseCSCBuilder{self.M==self.N};
	}

	//Initialization
	public def init(dv:Double):void {
		sparse.init(dv);
	}
	
	/**
	 * Initialize matrix block data with input function, given offset on 
	 * row and column.
	 */
	public def init(f:(Long, Long)=>Double):void {
		sparse.init(rowOffset, colOffset, f);
	}
	
	/**
	 * Initialize the sparse matrix block with random values.
	 */
	public def initRandom():void {
		sparse.initRandom();
	}

	public def initRandom(nzDensity:Double):void {
		getBuilder().initRandom(nzDensity, (Long,Long)=>RandTool.getRandGen().nextDouble());
	}
	
// 	public def initRandomSym(halfNZDensity:Double):void {
// 		val bdr = getBuilder();
// 		Debug.assure(sparse.M==sparse.N, "Not square matrix block");
// 		val symbdr = new SymSparseBuilder(bdr as SparseCSCBuilder(sparse.M));
// 		symbdr.initRandom(halfNZDensity).toSparseCSC(sparse as SparseCSC(symbdr.M,symbdr.M));
// 	}
// 
// 	public def initRandomTri(up:Boolean, halfNZDensity:Double):void {
// 		val bdr = getBuilder();
// 		Debug.assure(sparse.M==sparse.N, "Not square matrix block");
// 		val tribdr = new TriSparseBuilder(up, bdr as SparseCSCBuilder(sparse.M));
// 		tribdr.initRandom(halfNZDensity).toSparseCSC(sparse as SparseCSC(tribdr.M,tribdr.M));
// 	}
	
	/**
	 * Initialize matrix block data with random values within given
	 * range.
	 * 
	 * @param lo         lower bound for random value
	 * @param up         upper bound for random value
	 */
	public def initRandom(lb:Long, ub:Long) {
		sparse.initRandom(lb, ub);
	}

	/**
	 * Return the instance of matrix in the sparse block
	 */
	public def getMatrix():SparseCSC = sparse;

	/**
	 * Return the compress array of the sparse block
	 */
	public def getStorage():CompressArray = sparse.getStorage();

	/**
	 * Return the element value array of the sparse block
	 */
	public def getData() = sparse.getValue();

	/**
	 * Return the index array of the sparse block
	 */
	public def getIndex() = sparse.getIndex();



	public operator this(r:Long, c:Long) = sparse(r, c);
	

	// Overwrite MatrixBlock methods

	public def alloc(m:Long, n:Long) = new SparseBlock(myRowId, myColId, rowOffset, colOffset, sparse.alloc(m, n));	
	public def alloc() = new SparseBlock(myRowId, myColId, rowOffset, colOffset, sparse.alloc(sparse.M, sparse.N));
	
	public def allocFull(m:Long, n:Long) = 
		make(myRowId, myColId, rowOffset, colOffset, m, n, 1.0);
	
	
	/**
	 * Make a copy of myself
	 */
	public def clone() = new SparseBlock(myRowId, myColId, rowOffset, colOffset, sparse.clone());
	
	/**	
	 * Reset block fields 
	 */
	public def reset() : void {
		super.reset();
		sparse.reset();
	}

	// Overwrite GridBlock method
	
	/**
	 * Copy columns from source sparse block to a matrix of same type.
	 *
	 * @param srcoff     the starting columns from the source block matrix to copy
	 * @param colcnt     number of columns to copy
	 * @param dstmat     target matrix, must be SparseCSC type
	 */
	public def copyCols(srcoff:Long, colcnt:Long, dstmat:Matrix):Long {
		assert dstmat instanceof SparseCSC;
		return copyCols(srcoff, colcnt, dstmat as SparseCSC);
	}
	
	/**
	 * Copy columns from source sparse block to a sparse matrix
	 *
	 * @param srcoff     the starting columns from the source block matrix to copy
	 * @param colcnt     number of columns to copy
	 * @param dstspa     target sparse matrix
	 */
	public def copyCols(srcoff:Long, colcnt:Long, dstspa:SparseCSC):Long =
		SparseCSC.copyCols(sparse, srcoff, dstspa, 0, colcnt);
	


	// Implementing GridBlock abstract method
	/**
	 * Copy rows from sparse block to a matrix
	 *
	 * @param srcoff     the starting row from the source block matrix to copy
	 * @param rowcnt     number of rows to copy
	 * @param dstmat     target matrix
	 */
	public def copyRows(srcoff:Long, rowcnt:Long, dstmat:Matrix):Long {
		assert dstmat instanceof SparseCSC;
		return copyRows(srcoff, rowcnt, dstmat as SparseCSC);
	}
	
	/**
	 * Copy rows from sparse block to a sparse matrix
	 *
	 * @param srcoff     the starting row from the source block matrix to copy
	 * @param rowcnt     number of rows to copy
	 * @param dstspa     target sparse matrix 
	 */
	public def copyRows(srcoff:Long, rowcnt:Long, dstspa:SparseCSC):Long {
		return SparseCSC.copyRows(sparse, srcoff, dstspa, 0, rowcnt);
	}


	public def extractCols(y:Long,    //Starting columns
						   num:Long,  //Number of columns
						   dmat:DenseMatrix{self.M==sparse.M,self.N==num} //Targeting dense matrix
						   ) {
		sparse.extractCols(y, num, dmat);
	}

	// Overwrite GridBlock method

	/**
	 *  Add columns with columns from another matrix
	 *
	 * @param srcoff     column offset in the dense block
	 * @param colcnt     number of columns to add
	 * @param srcmat     source matrix to add with
	 */
// 	public def addCols(y:Long, num:Long, mat:Matrix):void {
// 		Debug.assure(mat instanceof SparseCSC);
// 		addCols(y, num, mat as SparseCSC);
// 	}

// 	public def addCols(y:Long, num:Long, smat:SparseCSC):void {
// 		Debug.assure(sparse.M==smat.M);
// 		var off:Long = 0;
// 		val ln = sparse.getTempCol(); //Temporary memory space
// 		for (var c:Long=y; c<y+num; c++) {
// 			val c1 = sparse.getCol(c);
// 			for (var i:Long=0; i<ln.size; i++) ln(i) = 0.0;
// 			//
// 			for (var ridx:Long=0; ridx<c1.size(); ridx++) {
// 				ln(c1.getIndex(ridx)) += c1.getValue(ridx);
// 			}			
// 			val c2 = smat.getCol(c-y);
// 			for (var ridx:Long=0; ridx<c2.size(); ridx++) {
// 				ln(c2.getIndex(ridx)) += c2.getValue(ridx);
// 			}
// 			off += smat.compressAt(c, off, ln);
// 		}
// 	}

// 	public def mult(a:Matrix, b:Matrix, plus:Boolean): void { // not return this
// 		if ((a instanceof SparseCSC) && (b instanceof SparseCSC))
// 			SparseMultToCSCol.comp(a as SparseCSC, b as SparseCSC, sparse, plus);
// 		else 
// 			throw new UnsupportedOperationException("Error in input block matrix types");
// 	}

	// Transpose
	public def transposeFrom(srcblk:SparseBlock) {
		val src = srcblk.sparse as SparseCSC(sparse.N,sparse.M);
		val bdr = getBuilder();
		bdr.initTransposeFrom(src);
		bdr.toSparseCSC();
	}
	
	public def transposeTo(spablk:SparseBlock) {
		spablk.transposeFrom(this);
	}
	
	public def transposeFrom(srcmat:Matrix) {
		if (srcmat instanceof SparseCSC) {
			val src = srcmat as SparseCSC(sparse.N,sparse.M);
			val bdr = getBuilder();
			bdr.initTransposeFrom(src);
			bdr.toSparseCSC();
		} else {
			throw new UnsupportedOperationException("Matrix types are not supported in transpose method");
		}
	}

	// Utils

	public def compColDataSize(colOff:Long, colCnt:Long):Long = sparse.countNonZero(colOff,colCnt);

	public def countNonZero() = sparse.countNonZero();
	
	public def getStorageSize() = sparse.getStorageSize();

	public def toString() : String {
		val output:String = "Sparse Grid Block ("+myRowId+","+myColId+")\n"+
							   sparse.toString();
		return output;
	}
}
