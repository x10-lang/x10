	
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
			val blist = findColBlockList(B, cb);
			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
				val cmat = C.findBlock(rb, cb).getMatrix();
				val alist = findRowBlockList(A, rb);
				val ait = alist.iterator();
				val bit = blist.iterator();
				Debug.assure(alist.size()==blist.size(), 
						"Partition mismatch! Number of partition not same.");
				while (ait.hasNext()) {
					val ablk = ait.next();
					val amat = ablk.getMatrix() as Matrix(cmat.M);
					val bblk = bit.next();
					val bmat = bblk.getMatrix() as Matrix(amat.N,cmat.N);
					Debug.assure(ablk.myColId==bblk.myRowId, 
							"Block partition misaligned in block matrix multiply");
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
			val blist = findColBlockList(B, cb);
			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
				val cmat = C.findBlock(rb, cb).getMatrix();
				val alist = findColBlockList(A, rb);
				val ait = alist.iterator();
				val bit = blist.iterator();
				Debug.assure(alist.size()==blist.size(), 
						"Partition mismatch! Numbers of partition blocks not same");
				while (ait.hasNext()) {
					val ablk = ait.next();
					val bblk = bit.next();
					Debug.assure(ablk.myRowId==bblk.myRowId,
							"Block partition misaligned in block trans-multiply");
					val amat = ablk.getMatrix() as Matrix(cmat.N);
					val bmat = bblk.getMatrix() as Matrix(amat.M,cmat.N);
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
			val blist = findRowBlockList(B, cb);
			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
				val cmat = C.findBlock(rb, cb).getMatrix();
				val alist = findRowBlockList(A, rb);
				val ait = alist.iterator();
				val bit = blist.iterator();
				Debug.assure(alist.size()==blist.size(), 
						"Partition mismatch! Number of partitions not same");
				while (ait.hasNext()) {
					val ablk = ait.next();
					val bblk = bit.next();
					Debug.assure(ablk.myColId==bblk.myColId,
							"Block partition misaligned in block multiply-trans");
					
					val amat = ablk.getMatrix() as Matrix(cmat.M);
					val bmat = bblk.getMatrix() as Matrix(cmat.N,amat.N);
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
		//sort
		retlst.sort((b1:MatrixBlock,b2:MatrixBlock)=>select(b2.myColId,b2.myRowId)-select(b1.myColId,b1.myRowId));
		// if (here.id()==0) {
		// 	val itr1 = blklist.iterator();
		// 	while (itr1.hasNext()) {
		// 		val b=itr1.next();
		// 		val cmpid = select(b.myColId, b.myRowId);
		// 		Debug.flushln("Block ("+b.myRowId+","+b.myColId+") cmp value"+cmpid);
		// 	}
		// }
		// Debug.flushln("Done select+sort");
		return retlst;
	}
	
	public static def findRowBlockList(blklist:ArrayList[MatrixBlock], rid:Int) =
		findSelect(blklist, rid, (r:Int, c:Int)=>r);

	public static def findColBlockList(blklist:ArrayList[MatrixBlock], rid:Int) =
		findSelect(blklist, rid, (r:Int, c:Int)=>c);
	
}
	