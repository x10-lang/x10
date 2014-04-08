/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2013.
 */

package x10.matrix.block;

import x10.util.Timer;
import x10.compiler.Inline;

import x10.matrix.Matrix;
import x10.matrix.builder.MatrixBuilder;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;

/**
 * Matrix block class serves as the base class for dense block matrix and 
 * sparse block matrix class. It can be used to build
 * dense-sparse mixed matrix blocks too.
 */
public abstract class MatrixBlock(myRowId:Long, myColId:Long) {
	/** Starting row */
	public val rowOffset:Long;
    /** Starting column */
	public val colOffset:Long;

	/**
	 * Profiling use. Record the computation time involved in computing the block
	 */
	public var calcTime:Long = 0;

	/**
	 * Profiling use. 
	 * Record the communication time involved in sending/receiving the block.
	 */
	public var commTime:Long = 0;

	/**
	 * Neighbor block places
	 */
	public var placeEast:Long=-1;
	public var placeWest:Long=-1;
	public var placeNorth:Long=-1;
	public var placeSouth:Long=-1;

	/**
	 * Construct a instance of matrix block with specified row and column index
	 * of its partitioning.
	 * 
	 * @param rid    row block index in partitioning grid
	 * @param cid    column block index in partitioning grid
	 */
	public def this(rid:Long, cid:Long, roff:Long, coff:Long) {
		property(rid, cid);
		rowOffset=roff;
		colOffset=coff;
	}

	/**
	 * For testing purpose.
	 *
	 * <p> Initial matrix block with a specified contant value. 
	 *
	 * @param  ival 	the constant value
	 */
	abstract public def init(ival:Double) : void;

	/**
	 * Initialize block matrix data using function, which maps global row and column
	 * indexes within block to a double value.
	 * 
	 * @param f         function given global row and column indexes returns a double value
	 */
	public def init(f:(Long,Long)=>Double) : void {
		// see XTENLANG-3167
		// must apply f to offset row,column for this block
		val offsetF = (a:Long,b:Long)=> f(a+rowOffset,b+colOffset);
		getBuilder().init(offsetF);
	}
	
	/**
	 * Initial block matrix with given function in nonzero density randomly
	 * @param nzDensity    Nonzero density, which is used to control row index distance between two adjacent nonzero  entries in the same column.
	 * @param f:(Long,Long)=>Double   Matrix entry value generator function, mapping row and column to a double value.
	 */
	public def initRandom(nzDensity:Double, f:(Long,Long)=>Double) : void {
		getBuilder().initRandom(nzDensity, f);
	}                                      

	/**
	 * For testing purpose. Obsolete
	 *
	 * <p> Initial matrix data in blocks with random values.
	 */
	abstract public def initRandom() : void ;
	abstract public def initRandom(nzDensity:Double) : void;

	/**
	 * Initialize matrix block data with random values between given
	 * range.
	 * 
	 * @param lo         lower bound for random value
	 * @param up         upper bound for random value
	 */
	abstract public def initRandom(lo:Long, up:Long) : void;
	
	abstract public def getBuilder() : MatrixBuilder;
	abstract public def getSymBuilder() : MatrixBuilder;
	abstract public def getTriBuilder(up:Boolean) : MatrixBuilder;
	//abstract public def initRandomSym(halfDensity:Double) : void;
	//abstract public def initRandomTri(up:Boolean, halfDensity:Double) : void;

	/**
	 * Allocate memory space for the same matrix block of this
	 */
	abstract public def alloc(m:Long, n:Long):MatrixBlock;
	public def alloc():MatrixBlock = alloc(getMatrix().M,getMatrix().N);
	abstract public def allocFull(m:Long, n:Long):MatrixBlock;

	/**
	 * Make a copy of myself
	 */
	abstract public def clone():MatrixBlock;

	/**
	 * Return the actual matrix object of the matrix block.
	 */
	abstract public def getMatrix():Matrix;

	/**
	 * Return the underlying matrix element data storage.
	 */
	abstract public def getData():Rail[Double];

	/**
	 * Return the index array, the surface indices for the sparse matrix.
	 * Not valid for dense matrix block.
	 */
	abstract public def getIndex():Rail[Long];
	public def getCompressArray() = (getMatrix() as SparseCSC).ccdata;

	/**
	 * Return the matrix data of specified row and column index.
	 */
	abstract public operator this(r:Long, c:Long):Double;

	public def copyTo(that:MatrixBlock): void {
		val smat = this.getMatrix();
		smat.copyTo(that.getMatrix() as Matrix(smat.M, smat.N));
	}
		
	/**
	 * Copy columns of block into the given matrix.
	 */
	abstract public def copyCols(coloff:Long, num:Long, mat:Matrix):Long;
	
	/**
	 * Copy rows of block into the given matrix.
	 */
	abstract public def copyRows(rowoff:Long, num:Long, mat:Matrix):Long;
	
	/**
	 * This method is used in SUMMA transposed multiplication.
	 */
	//public abstract def addCols(y:Long, num:Long, mat:Matrix):void ;

	public def reset():void {
		val st = Timer.milliTime();
		//calcTime=0; commTime=0;
		getMatrix().reset();
		calcTime += Timer.milliTime() -st;
	}

	@Inline
	public def mult(ablk:MatrixBlock, bblk:MatrixBlock, plus:Boolean):void {
		val st = Timer.milliTime();
		val cmat = getMatrix();
		val amat = ablk.getMatrix() as Matrix(cmat.M);
		val bmat = bblk.getMatrix() as Matrix(amat.N,cmat.N);
		cmat.mult(amat, bmat, plus);
		calcTime += Timer.milliTime() - st;
	}
	
	@Inline
	public def transMult(ablk:MatrixBlock, bblk:MatrixBlock, plus:Boolean ) :void {
		val st = Timer.milliTime();
		val cmat = getMatrix();
		val amat = ablk.getMatrix() as Matrix{self.N==cmat.M};
		val bmat = bblk.getMatrix() as Matrix(amat.M,cmat.N);
		cmat.transMult(amat, bmat, plus);
		calcTime += Timer.milliTime() - st;
	}
	
	@Inline
	public def multTrans(ablk:MatrixBlock, bblk:MatrixBlock, plus:Boolean):void {
		val st = Timer.milliTime();
		val cmat = getMatrix();
		val amat = ablk.getMatrix() as Matrix(cmat.M);
		val bmat = bblk.getMatrix() as Matrix(cmat.N,amat.N);
		cmat.multTrans(amat, bmat, plus);
		calcTime += Timer.milliTime() - st;
	}

	//Transpose methods
	//abstract public def transposeFrom(srcblk:MatrixBlock):void;
	//abstract public def transposeTo(dstblk:MatrixBlock):void;
	abstract public def transposeFrom(srcmat:Matrix):void ;

	abstract public def compColDataSize(colOff:Long, colCnt:Long):Long;
	
	public def getDataCount():Long = compColDataSize(0, getMatrix().N);

	public def isDense():Boolean = (getMatrix() instanceof DenseMatrix); 
	
	public def isSparse():Boolean = (getMatrix() instanceof SparseCSC);

	public def sameAs(mb:MatrixBlock):Boolean {
		if (this.myRowId == mb.myRowId &&
			this.myColId == mb.myColId) 
			return true;
		return false;
	}

	public def equals(other:MatrixBlock):Boolean {
		val srcmat = getMatrix();
		val objmat = other.getMatrix() as Matrix(srcmat.M, srcmat.N);
		return srcmat.equals(objmat);
	}

	public abstract def getStorageSize():Long;

	public def toString():String {
		val output:String = "Matrix Block ("+myRowId+","+myColId+") " +
		"Offset:"+rowOffset+","+colOffset+") "+getMatrix().toString();
		return output;
	}
}
