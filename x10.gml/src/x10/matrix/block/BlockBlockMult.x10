	
/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.matrix.block;

import x10.io.Console;
import x10.util.ArrayList;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;

public class BlockBlockMult  {

	//====================================================================
	/**
	 * Block matrix multiply. Input block matrix must be partitioned in grid, which
	 * allows array list index matches to its corresponding block id. 
	 * Therefore, iterations of blocks do not need to search for block list.
	 * 
	 */
	public static def mult(
			A:BlockMatrix, 
			B:BlockMatrix(A.N),
			C:BlockMatrix(A.M,B.N),
			plus:Boolean):BlockMatrix(C) {
		Debug.assure(Grid.match(A.grid.rowBs, C.grid.rowBs), 
			"Row partiton of first operand and result matrix mismatch in matrix multiply");
		Debug.assure(Grid.match(B.grid.colBs, C.grid.colBs), 
			"Column partition of second operand and result matrix mismatch in matrix multiply");
		Debug.assure(Grid.match(A.grid.colBs, B.grid.rowBs),
			"Column partition of first and row partition of second operand mismatch in matrix multiply");
		
		return mult(A.listBs, B.listBs, C, plus);
	}
	//-----
	public static def transMult(
			A:BlockMatrix, 
			B:BlockMatrix(A.M),
			C:BlockMatrix(A.N,B.N),
			plus:Boolean):BlockMatrix(C) {
		Debug.assure(Grid.match(A.grid.colBs, C.grid.rowBs), 
			"Column partiton of first operand and row partition of result matrix mismatch in trans-multply");
		Debug.assure(Grid.match(B.grid.colBs, C.grid.colBs), 
			"Column partition of second operand and result matrix mismatch in trans-multiply");
		Debug.assure(Grid.match(A.grid.rowBs, B.grid.rowBs), 
			"Row partition of first and second operand mismatch in trans-multiply");
		
		return transMult(A.listBs, B.listBs, C, plus);
	}		
	//-------------
	public static def multTrans(
			A:BlockMatrix, 
			B:BlockMatrix{self.N==A.N},
			C:BlockMatrix(A.M,B.M),
			plus:Boolean):BlockMatrix(C) {
		Debug.assure(Grid.match(A.grid.rowBs, C.grid.rowBs), 
			"Row partiton of first operand and result matrix mismatch in multiply-trans");
		Debug.assure(Grid.match(B.grid.rowBs, C.grid.colBs), 
			"Row partition of second operand and result matrix mismatch in multiply-trans");
		Debug.assure(Grid.match(A.grid.colBs, B.grid.colBs),
			"Column partition of first and second operand mismatch in multiply-trans");
		
		return multTrans(A.listBs, B.listBs, C, plus);
	}

	
	//===================================================================
	public static def mult(
			A:ArrayList[MatrixBlock], 
			B:ArrayList[MatrixBlock],
			C:BlockMatrix,
			plus:Boolean):BlockMatrix(C) {
		
		var startcol:Int = 0;
		if (! plus) C.reset();
		val grid = C.grid;
		
		for (var cb:Int=0; cb < grid.numColBlocks; cb++) {
			val cblist = findColBlockList(B, cb);
			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
				val cmat = C.findBlock(rb, cb).getMatrix();
				val rblist = findRowBlockList(A, rb);
				val rit = rblist.iterator();
				val cit = cblist.iterator();
				Debug.assure(rblist.size()==cblist.size(), "Partition mismatch!");
				while (rit.hasNext()) {
					val amat = rit.next().getMatrix() as Matrix(cmat.M);
					val bmat = cit.next().getMatrix() as Matrix(amat.N,cmat.N);
					cmat.mult(amat, bmat, true);
				}
			}
		}
		return C;	
	}
	
	
	public static def transMult(
			A:ArrayList[MatrixBlock], 
			B:ArrayList[MatrixBlock],
			C:BlockMatrix,
			plus:Boolean):BlockMatrix(C) {
		
		var startcol:Int = 0;
		if (! plus) C.reset();
		val grid = C.grid;
		
		for (var cb:Int=0; cb < grid.numColBlocks; cb++) {
			val cblist = findColBlockList(B, cb);
			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
				val cmat = C.findBlock(rb, cb).getMatrix();
				val rblist = findColBlockList(A, rb);
				val rit = rblist.iterator();
				val cit = cblist.iterator();
				Debug.assure(rblist.size()==cblist.size(), "Partition mismatch!");
				while (rit.hasNext()) {
					val amat = rit.next().getMatrix() as Matrix(cmat.N);
					val bmat = cit.next().getMatrix() as Matrix(amat.M,cmat.N);
					cmat.transMult(amat, bmat, true);
				}
			}
		}
		return C;	
	}
	
	public static def multTrans(
			A:ArrayList[MatrixBlock], 
			B:ArrayList[MatrixBlock],
			C:BlockMatrix,
			plus:Boolean):BlockMatrix(C) {
		
		var startcol:Int = 0;
		if (! plus) C.reset();
		val grid = C.grid;
		
		for (var cb:Int=0; cb < grid.numColBlocks; cb++) {
			val cblist = findRowBlockList(B, cb);
			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
				val cmat = C.findBlock(rb, cb).getMatrix();
				val rblist = findRowBlockList(A, rb);
				val rit = rblist.iterator();
				val cit = cblist.iterator();
				Debug.assure(rblist.size()==cblist.size(), "Partition mismatch!");
				while (rit.hasNext()) {
					val amat = rit.next().getMatrix() as Matrix(cmat.M);
					val bmat = cit.next().getMatrix() as Matrix(cmat.N,amat.N);
					cmat.multTrans(amat, bmat, true);
				}
			}
		}
		return C;	
	}	
	
	//======================
	//
	//======================
	public static def findBlock(blklist:ArrayList[MatrixBlock], rowId:Int, colId:Int):MatrixBlock {
		val itr = blklist.iterator();
		while (itr.hasNext()) {
			val blk = itr.next();
			if (blk.myRowId == rowId && blk.myColId==colId) {
				return blk;
			}
		}
		return null;
				
	}
	
	public static def findSelect(blklist:ArrayList[MatrixBlock], commonId:Int, select:(Int,Int)=>Int):ArrayList[MatrixBlock] {
		val retlst=new ArrayList[MatrixBlock]();
		val itr = blklist.iterator();
		while (itr.hasNext()) {
			val blk = itr.next();
			val tgt = select(blk.myRowId, blk.myColId);
			if (commonId == tgt) 
				retlst.add(blk);
		}
		return retlst;
	}
	
	public static def findRowBlockList(blklist:ArrayList[MatrixBlock], rid:Int) =
		findSelect(blklist, rid, (r:Int, c:Int)=>r);

	public static def findColBlockList(blklist:ArrayList[MatrixBlock], rid:Int) =
		findSelect(blklist, rid, (r:Int, c:Int)=>c);
	
}
	