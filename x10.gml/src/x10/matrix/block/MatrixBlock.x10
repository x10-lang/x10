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

/**
 * Matrix block provides an abstraction of matrix block classes, which serves as
 * the base classes for dense block matrix and sparse block matrix class. It can be used to build
 * dense-sparse mixed matrix blocks too.
 */
public abstract class MatrixBlock {

	//-------------------------------
	// Block indexing fields:
	/**
	 * Row block index id.
	 */
	public val myRowId:Int;
	/**
	 * Column block index id.
	 */
	public val myColId:Int;

	//----------------------------
	// Profiling fields
	/**
	 * Profiling use. Record the computation time involved in computing the block
	 */
	public var calcTime:Long = 0;
	/**
	 * Profiling use. 
	 * Record the communication time involved in sending/receiving the block.
	 */
	public var commTime:Long = 0;
	
	//===================================
	// Constructor
	//===================================
	/**
	 * Construct a instance of matrix block with specified row and column index
	 * of its partitioning.
	 * 
	 * @param rid    row block index in partitioning grid
	 * @param cid    column block index in partitioning grid
	 */
	public def this(rid:Int, cid:Int) {
		myRowId = rid;
		myColId = cid;
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
	 * For testing purpose.
	 *
	 * <p> Initial matrix data in blocks with random values.
	 */
	abstract public def initRandom() : void;
	
	/**
	 * Initialize matrix block data with random values between given
	 * range.
	 * 
	 * @param lo         lower bound for random value
	 * @param up         upper bound for random value
	 */
	abstract public def initRandom(lo:Int, up:Int) : void;

	//===================================================
	/**
	 * Allocate memory space for the same matrix block of this
	 */
	abstract public def alloc():MatrixBlock;
	
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
	abstract public def getData():Array[Double](1);

	/**
	 * Return the index array, the surface indices for the sparse matrix.
	 * Not valid for dense matrix block.
	 */
	abstract public def getIndex():Array[Int](1);

	/**
	 * Return the matrix data of specified row and column index.
	 */
	abstract public operator this(r:Int, c:Int):Double;
	
	/**
	 * Copy columns of block into the given matrix.
	 */
	abstract public def copyCols(coloff:Int, num:Int, mat:Matrix):Int ;
	
	/**
	 * Copy rows of block into the given matrix.
	 */
	abstract public def copyRows(rowoff:Int, num:Int, mat:Matrix):Int ;
	
	/**
	 * This method is used in SUMMA transposed multiplication.
	 */
	//public abstract def addCols(y:Int, num:Int, mat:Matrix):void ;

	public def reset():void {
		calcTime=0; commTime=0;
	}

	//--------------------------------
	public def sameAs(mb:MatrixBlock): Boolean {
		if (this.myRowId == mb.myRowId &&
			this.myColId == mb.myColId) 
			return true;
		return false;
	}
	
	//--------------------------------
	public def toString() : String {
		val output:String = "Matrix Block ("+myRowId+","+myColId+") : " +
							   getMatrix().toString();
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
