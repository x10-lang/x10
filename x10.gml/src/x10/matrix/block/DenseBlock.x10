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

package x10.matrix.block;

import x10.io.Console;
import x10.util.Random;
import x10.util.Timer;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

/**
 * Dense block is used to build dense block matrix (at single place) or 
 * distributed dense matrix at all places. 
 * 
 */
public class DenseBlock extends MatrixBlock {
	//
	/**
	 * Dense matrix field
	 */
	public val dense:DenseMatrix;
	//
	//--------- Profiling ---------
	public var calcTime:Long=0;
	public var commTime:Long=0;

	//public val block:DenseMatrix;
	//===================================================================
	/**
	 * Construct a dense-matrix block instance with specified row index, column index and
	 * dense matrix object.
	 *
	 * @param  rid      block's row id
	 * @param  cid      block's column id
	 * @param  m        the dense matrix
	 */
	public def this(rid:Int, cid:Int, m:DenseMatrix) {
		super(rid, cid);
		dense = m;
	}
	/**
	 * Construct a dense-matrix block instance with specified row index, column index and
	 * dense matrix object.
	 * 
	 * @param  rid      block's row id
	 * @param  cid      block's column id
	 * @param  m, n     matrix demensions
	 * @param  d        the dense matrix data storage
	 */
	public def this(rid:Int, cid:Int, m:Int, n:Int, d:Array[Double](1){rail}) {
		super(rid, cid);
		dense = new DenseMatrix(m, n, d);
	}
	//---------------------------------------------------
	/**
	 * Create a dense matrix block in a grid partition.
	 *
	 * @param rid     The block's row id
	 * @param cid     The block's column id
	 * @param m       block rows
	 * @param n       block columns
	 */
	public static def make(rid:Int, cid:Int, m:Int, n:Int):DenseBlock {
		val dmat = DenseMatrix.make(m, n);
		return new DenseBlock(rid, cid, dmat);	
	}
	
	/**
	 * Create a dense matrix block in a grid partition with user provided
	 * memory space
	 *
	 * @param gp      The grid partitioning of matrix
	 * @param rid     The block's row id
	 * @param cid     The block's column id
	 * @param da      The data array of the block
	 */
	public static def make(
			gp:Grid, 
			rid:Int, cid:Int, 
			da:Array[Double](1){rail}):DenseBlock {
		val m = gp.rowBs(rid);
		val n = gp.colBs(cid);
		val dmat = new DenseMatrix(m, n, da);
		return new DenseBlock(rid, cid, dmat);
	}
	//
	/**
	 * Create a dense matrix block using matrix partitioning information.
	 *
	 * @param gp      The grid partitioning of matrix
	 * @param rid     The block's row id
	 * @param cid     The block's column id
	 */
	public static def make(gp:Grid, rid:Int, cid:Int):DenseBlock {
		val m = gp.rowBs(rid);
		val n = gp.colBs(cid);
		val dmat = DenseMatrix.make(m, n);
		return new DenseBlock(rid, cid, dmat);	
	}

	//
	//---------------------------
	//
	/**
	 * Initialize all elements in matrix block with a constant value.
	 *
	 * @param  ival      Initial value
	 */
	public def init(ival:Double):void {
		dense.init(ival);
	}
	/**
	 * Initialize matrix block data with input function, given offset on 
	 * row and column.
	 */
	public def init(x_off:Int, y_off:Int, f:(Int, Int)=>Double):void {
		dense.init(x_off, y_off, f);
	}
	
	/**
	 * Initialize all elements in the matrix block with random values.
	 *
	 */
	public def initRandom(): void {
		dense.initRandom();
	}
	//
	
	/**
	 * Initialize matrix block data with random values between given
	 * range.
	 * 
	 * @param lo         lower bound for random value
	 * @param up         upper bound for random value
	 */
	public def initRandom(lb:Int, ub:Int) {
		dense.initRandom(lb, ub);
	}
	
	//==================================================================
	// Data access
	//==================================================================
	/**
	 * Return the matrix instance of the block
	 */
	public def getMatrix():DenseMatrix = dense;

	/**
	 * Return the data array storing the matrix block data.
	 */
	public def getData():Array[Double](1) = dense.d;

	/**
	 * Return the surface index array. Valid for sparse matrix only.
	 */
	public def getIndex():Array[Int](1) {
		Debug.flushln("No index for dense block");
		return new Array[Int](0);
	}

	//======================================
	// Some short-keys for matrix functions
	//======================================

	public def alloc(m:Int, n:Int) = new DenseBlock(myRowId, myColId, dense.alloc(m, n));	

	public def alloc()             = new DenseBlock(myRowId, myColId, dense.alloc(dense.M, dense.N));

	public def clone() {
		//Debug.flushln("Clone dense block");
		val ndb = new DenseBlock(myRowId, myColId, dense.clone());
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
	public operator this(r:Int, c:Int):Double = dense(r, c);
		
	//==================================================================
	// Copye columns to a dense matrix
	//==================================================================
	/**
	 * Copy columns from dense block to a dense matrix. 
	 * 
	 * @param srcoff     column offset in source dense block
	 * @param colcnt     number of columns to copy
	 * @param dstden     target dense matrix
	 */
	public def copyCols(srcoff:Int, colcnt:Int, dstden:DenseMatrix):Int {
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
	public def copyCols(srcoff:Int, colcnt:Int, dstmat:Matrix): Int {
		Debug.assure(dstmat instanceof DenseMatrix, 
					 "Target is not a dense matrix instance");
		return copyCols(srcoff, colcnt, dstmat as DenseMatrix);
	}

	//
	//==================================================================
	
	/**
	 * Copy number of rows from dense block to a matrix
	 *
	 * @param srcoff     row offset in source matrix
	 * @param rowcnt     number of rows to copy
	 * @param dstden     the target dense matrix
	 */
	public def copyRows(srcoff:Int, rowcnt:Int, dstden:DenseMatrix):Int {
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
	public def copyRows(srcoff:Int, rowcnt:Int, dstmat:Matrix):Int {
		Debug.assure(dstmat instanceof DenseMatrix);
		return copyRows(srcoff, rowcnt, dstmat as DenseMatrix);
	}

	//==================================================================
	// Override the MatrixBlock method
	//==================================================================
	/**
	 *  Add columns with columns from another matrix
	 *
	 * @param srcoff     column offset in the dense block
	 * @param colcnt     number of columns to add
	 * @param srcmat     source matrix to add with
	 */
	public def addCols(coloff:Int, colcnt:Int, srcmat:Matrix):void {
		Debug.assure(srcmat instanceof DenseMatrix);
		addCols(coloff, colcnt, srcmat as DenseMatrix);
	}

	/**
	 *  Add columns in dense block with columns from other dense matrix
	 *
	 * @param coloff     column offset in the dense block
	 * @param colcnt     number of columns to add
	 * @param srcden     source dense matrix to add with
	 */	
	public def addCols(coloff:Int, colcnt:Int, srcden:DenseMatrix):void {
		Debug.assure(srcden.M <= dense.M && colcnt<=srcden.N && coloff+colcnt<=dense.N,
				"off:"+coloff+" cnt:"+colcnt+" dst.N:"+dense.N);

		var src:Int=0;
		var j:Int;
		for (var dst:Int=coloff*dense.M; dst<(coloff+colcnt)*dense.M; dst+=dense.M, src+=srcden.M) {
			j=src;
			for (var i:Int=dst; i<dst+srcden.M; i++, j++) 
				dense.d(i) += srcden.d(j);
		}
	}
		
	//==================================================================
	public def toString() : String {
		val output:String = "Dense matrix block ("+myRowId+","+myColId+"): "+
							   dense.toString();
		return output;
	} 
	//
	public def print() { print(""); }
	public def print(msg:String) {
		Console.OUT.print(msg+this.toString());
		Console.OUT.flush();
	}
	//
	public def debugPrint() { debugPrint(""); }
	public def debugPrint(msg:String) {
		if (Debug.disable) return;
		Debug.println(msg+this.toString());
	}

}
