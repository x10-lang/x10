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
//
import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
// Sparse matrix
import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.Compress1D;
import x10.matrix.sparse.Compress2D;
//
import x10.matrix.sparse.SparseCSC;

// Sparse matrix multiplication
//import x10.matrix.sparse.SparseMultToCSCol;

/**
 * Sparse block is used to build sparse block matrix (at single place) or 
 * distributed sparse matrix at all places. 
 *  
 */
public class SparseBlock extends MatrixBlock {
	//
	public val sparse:SparseCSC;
	//
	//--------- Profiling ---------
	public var calcTime:Long=0;
	public var commTime:Long=0;

	//===================================================================
	/**
	 * Construct a sparse CSC matrix block.
	 *
	 * @param  rid      block's row id
	 * @param  cid      block's column id
	 * @param  m        the sparse matrix in CSC format
	 */
	public def this(rid:Int, cid:Int, m:SparseCSC) {
		super(rid, cid);
		sparse = m;
	}

	//---------------------------------------------------
	/**
	 * Create a sparse-matrix block based on matrix partitioning.
	 *
	 * @param  gp      The grid partitioning of matrix
	 * @param  rid      block's row id
	 * @param  cid      block's column id
	 * @param  cdat     the sparse matrix's compress2D data structure
	 */
	public static def make(gp:Grid, 
						   rid:Int, cid:Int, 
						   cdat:Compress2D
						   ) : SparseBlock {
		val m = gp.rowBs(rid);
		val n = gp.colBs(cid);
		val smat = new SparseCSC(m, n, cdat);
		return new SparseBlock(rid, cid, smat);
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
	public static def make(gp:Grid, rid:Int, cid:Int, nzcnt:Int
						   ) : SparseBlock {
		val m = gp.rowBs(rid);
		val n = gp.colBs(cid);
		val smat = SparseCSC.make(m, n, nzcnt);
		return new SparseBlock(rid, cid, smat);
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
	public static def make(gp:Grid, rid:Int, cid:Int, nzd:Double
						   ) : SparseBlock {
		val m = gp.rowBs(rid);
		val n = gp.colBs(cid);
		val nzcnt:Int = (nzd*m*n) as Int;
		val smat = SparseCSC.make(m, n, nzcnt);
		return new SparseBlock(rid, cid, smat);
	}
	//----------------------------------------------------
	//Initialization
	//----------------------------------------------------
	/**
	 * Initialize the sparse-matrix block with a constant value.
	 * The sparsity used in initialization is the specified by the field of
	 " "nonZeroDensity" in SparseCSC.
	 *
	 * @param  ival      the initial used constant value
	 */
	public def init(ival:Double): void {
		sparse.init(ival);
	}

	/**
	 * Initialize the sparse matrix block with random values.
	 *
	 */
	public def initRandom():void {
		sparse.initRandom();
	}

	/**
	 * Initialize the sparse matrix block with random values.
	 *
	 * @param  nzd      sparsity of the block
	 */
	public def initRandom(nzd:Double):void {
		sparse.initRandom(nzd);
	}

	//-------------------------------------------------------------------
	/**
	 * Return the instance of matrix in the sparse block
	 */
	public def getMatrix():SparseCSC = sparse;
	//
	/**
	 * Return the compress array of the sparse block
	 */
	public def getStorage():CompressArray = sparse.getStorage();

	/**
	 * Return the element value array of the sparse block
	 */
	public def getData():Array[Double](1)   = sparse.getValue();

	/**
	 * Return the index array of the sparse block
	 */
	public def getIndex():Array[Int](1)     = sparse.getIndex();

	
	//-------------------------------------------------------------------
	//
	public operator this(r:Int, c:Int) = sparse(r, c);
	
	//-------------------------------------------------------------------
	// Overwrite MatrixBlock methods

	public def alloc(m:Int, n:Int) 
		= new SparseBlock(myRowId, myColId, sparse.alloc(m, n));	
	public def alloc() 
		= new SparseBlock(myRowId, myColId, sparse.alloc(sparse.M, sparse.N));
	//
	/**
	 * Make a copy of myself
	 */
	public def clone() = new SparseBlock(myRowId, myColId, sparse.clone());
	
	/**	
	 * Reset block fields 
	 */
	public def reset() : void {
		super.reset();
		sparse.reset();
	}
	//==================================================================
	// Overwrite GridBlock method
	
	/**
	 * Copy columns from source sparse block to a matrix of same type.
	 *
	 * @param srcoff     the starting columns from the source block matrix to copy
	 * @param colcnt     number of columns to copy
	 * @param dstmat     target matrix, must be SparseCSC type
	 */
	public def copyCols(srcoff:Int, colcnt:Int, dstmat:Matrix):Int {
		Debug.assure(dstmat instanceof SparseCSC);
		return copyCols(srcoff, colcnt, dstmat as SparseCSC);
	}
	
	/**
	 * Copy columns from source sparse block to a sparse matrix
	 *
	 * @param srcoff     the starting columns from the source block matrix to copy
	 * @param colcnt     number of columns to copy
	 * @param dstspa     target sparse matrix
	 */
	public def copyCols(srcoff:Int, colcnt:Int, dstspa:SparseCSC): Int =
		SparseCSC.copyCols(sparse, srcoff, dstspa, 0, colcnt);
	

	//==================================================================
	// Implementing GridBlock abstract method
	/**
	 * Copy rows from sparse block to a matrix
	 *
	 * @param srcoff     the starting row from the source block matrix to copy
	 * @param rowcnt     number of rows to copy
	 * @param dstmat     target matrix
	 */
	public def copyRows(srcoff:Int, rowcnt:Int, dstmat:Matrix):Int {
		Debug.assure(dstmat instanceof SparseCSC);
		return copyRows(srcoff, rowcnt, dstmat as SparseCSC);
	}
	
	/**
	 * Copy rows from sparse block to a sparse matrix
	 *
	 * @param srcoff     the starting row from the source block matrix to copy
	 * @param rowcnt     number of rows to copy
	 * @param dstspa     target sparse matrix 
	 */
	public def copyRows(srcoff:Int, rowcnt:Int, dstspa:SparseCSC) : Int {
		//Debug.flushln("Copy rows:"+srcoff+" cnt:"+rowcnt+
		//			" to dst size:"+dstspa.getStorage().storageSize());
		return SparseCSC.copyRows(sparse, srcoff, dstspa, 0, rowcnt);
		//Debug.flushln("Copy nzd count:"+cnt);
	}

	//==================================================================
	public def extractCols(y:Int,    //Starting columns
						   num:Int,  //Number of columns
						   dmat:DenseMatrix{self.M==sparse.M,self.N==num} //Targeting dense matrix
						   ) {
		sparse.extractCols(y, num, dmat);
	}


	//==================================================================

	// Overwrite GridBlock method

	/**
	 *  Add columns with columns from another matrix
	 *
	 * @param srcoff     column offset in the dense block
	 * @param colcnt     number of columns to add
	 * @param srcmat     source matrix to add with
	 */
// 	public def addCols(y:Int, num:Int, mat:Matrix):void {
// 		Debug.assure(mat instanceof SparseCSC);
// 		addCols(y, num, mat as SparseCSC);
// 	}

// 	public def addCols(y:Int, num:Int, smat:SparseCSC):void {
// 		Debug.assure(sparse.M==smat.M);
// 		var off:Int = 0;
// 		val ln = sparse.getTempCol(); //Temporary memory space
// 		for (var c:Int=y; c<y+num; c++) {
// 			val c1 = sparse.getCol(c);
// 			for (var i:Int=0; i<ln.size; i++) ln(i) = 0.0;
// 			//
// 			for (var ridx:Int=0; ridx<c1.size(); ridx++) {
// 				ln(c1.getIndex(ridx)) += c1.getValue(ridx);
// 			}			
// 			val c2 = smat.getCol(c-y);
// 			for (var ridx:Int=0; ridx<c2.size(); ridx++) {
// 				ln(c2.getIndex(ridx)) += c2.getValue(ridx);
// 			}
// 			off += smat.compressAt(c, off, ln);
// 		}
// 	}
	//==================================================================
// 	public def mult(a:Matrix, b:Matrix, plus:Boolean): void { // not return this
// 		if ((a instanceof SparseCSC) && (b instanceof SparseCSC))
// 			SparseMultToCSCol.comp(a as SparseCSC, b as SparseCSC, sparse, plus);
// 		else 
// 			Debug.exit("Error in input block matrix types");
// 	}
	//==================================================================
	// Utils
	//==================================================================
	public def countNonZero() = sparse.countNonZero();

	//-------------------------------
	public def toString() : String {
		val output:String = "Sparse Grid Block ("+myRowId+","+myColId+")\n"+
							   sparse.toString();
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
		Debug.flush();
	}

}
