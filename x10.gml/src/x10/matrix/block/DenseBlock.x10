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

package x10.matrix.block;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.util.RandTool;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.builder.DenseBuilder;
import x10.matrix.builder.SymDenseBuilder;
import x10.matrix.builder.TriDenseBuilder;

/**
 * Dense block is used to build dense block matrix (at single place) or 
 * distributed dense matrix at all places. 
 */
public class DenseBlock extends MatrixBlock {
	/**
	 * Dense matrix field
	 */
	public val dense:DenseMatrix;
	public val builder:DenseBuilder(dense.M,dense.N);

	/**
	 * Construct a dense-matrix block instance with specified row index, column index and
	 * dense matrix object.
	 *
	 * @param  rid      block's row id
	 * @param  cid      block's column id
	 * @param  m        the dense matrix
	 */
	public def this(rid:Long, cid:Long, roff:Long, coff:Long, m:DenseMatrix) {
		super(rid, cid, roff, coff);
		dense = m;
		builder = new DenseBuilder(dense);//
	}

	/**
	 * Create a dense matrix block in a grid partition.
	 *
	 * @param rid     The block's row id
	 * @param cid     The block's column id
	 * @param m       block rows
	 * @param n       block columns
	 */
	public static def make(rid:Long, cid:Long, roff:Long, coff:Long, m:Long, n:Long):DenseBlock {
		val dmat = DenseMatrix.make(m, n);
		return new DenseBlock(rid, cid, roff, coff, dmat);	
	}
	
	/**
	 * Create a dense matrix block in a grid partition with user provided
	 * memory space
	 *
	 * @param gp      The grid partitioning of matrix
	 * @param rid     The block's row id
	 * @param cid     The block's column id
	 * @param da      The data for the block
	 */
	public static def make(
			gp:Grid, 
			rid:Long, cid:Long, 
			da:Rail[ElemType]{self!=null}):DenseBlock {
		val m = gp.rowBs(rid);
		val n = gp.colBs(cid);
		val dmat = new DenseMatrix(m, n, da);
		return new DenseBlock(rid, cid, gp.startRow(rid), gp.startCol(cid), dmat);
	}

	/**
	 * Create a dense matrix block using matrix partitioning information.
	 *
	 * @param gp      The grid partitioning of matrix
	 * @param rid     The block's row id
	 * @param cid     The block's column id
	 */
	public static def make(gp:Grid, rid:Long, cid:Long):DenseBlock {
		val m = gp.rowBs(rid);
		val n = gp.colBs(cid);
		val dmat = DenseMatrix.make(m, n);
		return new DenseBlock(rid, cid, gp.startRow(rid), gp.startCol(cid), dmat);	
	}

	/**
	 * Initialize all elements in matrix block with a constant value.
	 *
	 * @param  ival      Initial value
	 */
	public def init(ival:ElemType):void {
		dense.init(ival);
	}
	
	/**
	 * Initialize all elements in the matrix block with random values.
	 *
	 */
	public def initRandom():void {
		dense.initRandom();
	}
	
	public def getBuilder():DenseBuilder(dense.M,dense.N) = builder;
	public def getSymBuilder():DenseBuilder{self.M==self.N} = new SymDenseBuilder(dense as DenseMatrix{self.M==self.N});
	public def getTriBuilder(up:Boolean):DenseBuilder{self.M==self.N} = new TriDenseBuilder(up, dense as DenseMatrix{self.M==self.N});

	public def initRandom(nonZeroDensity:Float):void {
	    getBuilder().initRandom(nonZeroDensity, (Long,Long)=>RandTool.nextElemType[ElemType]());
	}

	// public def initRandomSym(halfDensity:ElemType) : void {
	// 	val symbld = new SymDenseBuilder(dense as DenseMatrix{self.M==self.N});
	// 	symbld.initRandom(halfDensity);
	// }

	// public def initRandomTri(up:Boolean, halfDensity:ElemType) : void {
	// 	val tribld = new TriDenseBuilder(up, dense as DenseMatrix{self.M==self.N});
	// 	tribld.initRandom(halfDensity);
	// }
	
	/**
	 * Initialize matrix block data with random values between given
	 * range.
	 * 
	 * @param lo         lower bound for random value
	 * @param up         upper bound for random value
	 */
	public def initRandom(lb:Long, ub:Long) {
		dense.initRandom(lb, ub);
	}
	
	public def initRandomSym(lb:Long, ub:Long) {
		dense.initRandom(lb, ub);
	}

	/**
	 * Return the matrix instance of the block
	 */
	public def getMatrix():DenseMatrix = dense;

	/**
	 * Return the data for the matrix block.
	 */
	public def getData() = dense.d;

	/**
	 * Return the surface index array. Valid for sparse matrix only.
	 */
	public def getIndex():Rail[Long] {
		Debug.flushln("No index for dense block");
		return new Rail[Long](0);
	}

	// Some short-keys for matrix functions
	public def alloc(m:Long, n:Long) = new DenseBlock(myRowId, myColId, rowOffset, colOffset, dense.alloc(m, n));	

	public def alloc()             = new DenseBlock(myRowId, myColId, rowOffset, colOffset, dense.alloc(dense.M, dense.N));
	public def allocFull(m:Long, n:Long) = new DenseBlock(myRowId, myColId, rowOffset, colOffset, dense.alloc(m, n));

	public def clone() {
		val ndb = new DenseBlock(myRowId, myColId, rowOffset, colOffset, dense.clone());
		return ndb;
	}
	
	/**
	 * Reset block fields
	 */
	public def reset() : void {
		super.reset();
		dense.reset();
	}
	/**
	 * Return the matrix data give its row and column.
	 */
	public operator this(r:Long, c:Long):ElemType = dense(r, c);
		
	/**
	 * Copy columns from dense block to a dense matrix. 
	 * 
	 * @param srcoff     column offset in source dense block
	 * @param colcnt     number of columns to copy
	 * @param dstden     target dense matrix
	 */
	public def copyCols(srcoff:Long, colcnt:Long, dstden:DenseMatrix):Long {
		DenseMatrix.copyCols(dense, srcoff, dstden, 0, colcnt);		
		return colcnt*dense.M;
	}

	/**
	 * Copy columns from dense block to a matrix instance.
	 *
	 * @param srcoff     column offset in source dense block
	 * @param colcnt     number of columns to copy
	 * @param dstmat     target matrix
	 */
	public def copyCols(srcoff:Long, colcnt:Long, dstmat:Matrix):Long {
        assert (dstmat instanceof DenseMatrix) :
            "Target is not a dense matrix instance";
		return copyCols(srcoff, colcnt, dstmat as DenseMatrix);
	}

	/**
	 * Copy number of rows from dense block to a matrix
	 *
	 * @param srcoff     row offset in source matrix
	 * @param rowcnt     number of rows to copy
	 * @param dstden     the target dense matrix
	 */
	public def copyRows(srcoff:Long, rowcnt:Long, dstden:DenseMatrix):Long {
		DenseMatrix.copyRows(dense, srcoff, dstden, 0, rowcnt);
		return rowcnt* dense.N;
	}

	/**
	 * Copy number of rows from dense block to a matrix
	 *
	 * @param srcoff     row offset in source matrix
	 * @param rowcnt     number of rows to copy
	 * @param dstmat     the target matrix
	 */
	public def copyRows(srcoff:Long, rowcnt:Long, dstmat:Matrix):Long {
		assert dstmat instanceof DenseMatrix;
		return copyRows(srcoff, rowcnt, dstmat as DenseMatrix);
	}


	// Override the MatrixBlock method

	/**
	 *  Add columns with columns from another matrix
	 *
	 * @param srcoff     column offset in the dense block
	 * @param colcnt     number of columns to add
	 * @param srcmat     source matrix to add with
	 */
	public def addCols(coloff:Long, colcnt:Long, srcmat:Matrix):void {
		assert srcmat instanceof DenseMatrix;
		addCols(coloff, colcnt, srcmat as DenseMatrix);
	}

	/**
	 *  Add columns in dense block with columns from other dense matrix
	 *
	 * @param coloff     column offset in the dense block
	 * @param colcnt     number of columns to add
	 * @param srcden     source dense matrix to add with
	 */	
	public def addCols(coloff:Long, colcnt:Long, srcden:DenseMatrix):void {
        assert (srcden.M <= dense.M && colcnt<=srcden.N && coloff+colcnt<=dense.N) :
            "off:"+coloff+" cnt:"+colcnt+" dst.N:"+dense.N;

		var src:Long=0;
		var j:Long;
		for (var dst:Long=coloff*dense.M; dst<(coloff+colcnt)*dense.M; dst+=dense.M, src+=srcden.M) {
			j=src;
			for (var i:Long=dst; i<dst+srcden.M; i++, j++) 
				dense.d(i) += srcden.d(j);
		}
	}

	/**
	 *  Add columns with columns from another matrix
	 *
	 * @param rowoff     row offset in the dense block
	 * @param rowcnt     number of rows to add
	 * @param srcmat     source matrix to add with
	 */	
    public def addRows(rowoff:Long, rowcnt:Long, srcmat:Matrix):void {
        assert srcmat instanceof DenseMatrix;
        addRows(rowoff, rowcnt, srcmat as DenseMatrix);
    }

    /**
     *  Add Rows in dense block with rows from other dense matrix
     *
     * @param rowoff     row offset in the dense block
     * @param rowcnt     number of rows to add
     * @param srcden     source dense matrix from which to add
     */	
    public def addRows(rowoff:Long, rowcnt:Long, srcden:DenseMatrix):void {
        assert (srcden.N <= dense.N && rowcnt<=srcden.M && rowoff+rowcnt<=dense.M) :
            "off:"+rowoff+" cnt:"+rowcnt+" dst.M:"+dense.M;
        var src:Long=0;
        var j:Long;
        for (var dst:Long=rowoff; dst<=rowoff+(srcden.N-1)*dense.M; dst+=dense.M, src+=srcden.M) {
            j=src;
            for (var i:Long=dst; i<dst+rowcnt; i++, j++) {
                dense.d(i) += srcden.d(j);
            }
        }
    }
	
	public def compColDataSize(colOff:Long, colCnt:Long):Long = dense.M*colCnt;

	// Transpose methods
	
	public def transposeFrom(srcblk:DenseBlock) {
		val src = srcblk.dense;
		val dst = this.dense as DenseMatrix(src.N,src.M); 
		dst.T(src);
	}
	
	public def transposeTo(dstblk:DenseBlock) {
		val src = this.dense;
		val dst = dstblk.dense as DenseMatrix(src.N,src.M); 
		dst.T(src);
	}
	
	public def transposeFrom(srcmat:Matrix) {
		if (srcmat instanceof DenseMatrix) {
			val src = srcmat as DenseMatrix;
			val dst = dense as DenseMatrix(src.N,src.M);
			dst.T(src);
		} else {
			throw new UnsupportedOperationException("Matrix types are not supported in transpose method");
		}
	}		

	public def getStorageSize():Long = dense.M*dense.N;
	
	public def toString():String {
		val output:String = "Dense matrix block ("+myRowId+","+myColId+"): "+
							   dense.toString();
		return output;
	}
}
