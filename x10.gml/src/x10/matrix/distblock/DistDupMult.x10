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

package x10.matrix.distblock;

import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Debug;

import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.BlockBlockMult;

import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;

/**
 * Block matrix distributed in (n, 1) places multiply block matrix 
 * Result is stored in Duplicated block matrix at here.
 */
public class DistDupMult {
	
	/**
	 * 
	 */
	public static def mult(
			A:DistBlockMatrix, 
			B:DupBlockMatrix(A.N), 
			C:DistBlockMatrix(A.M,B.N), plus:Boolean) : DistBlockMatrix(C) {

		val gA = A.getGrid();
		val gB = B.getGrid();
		val gC = C.getGrid();
		
		//Global.assure(A.flagTranspose == true);
		Debug.assure(Grid.match(gA.rowBs, gC.rowBs),
				"Row partition of first and result matrix mismatch");
		Debug.assure(Grid.match(gB.colBs, gC.colBs),
				"Column partition of second and result matrix mismatch");
		
		finish ateach (Dist.makeUnique()) {
			//
			val bsA = A.handleBS();
			val bsB = B.local();
			val bsC = C.handleBS();
			bsA.buildBlockMap();
			bsB.buildBlockMap();
			bsC.buildBlockMap();
			BlockBlockMult.mult(bsA.blockMap, bsB.blockMap, bsC.blockMap, plus);
		}
		return C;
	}
	
	public static def transMult(
			A:DistBlockMatrix, 
			B:DupBlockMatrix(A.M), 
			C:DistBlockMatrix(A.N,B.N), plus:Boolean) : DistBlockMatrix(C) {

		val gA = A.getGrid();
		val gB = B.getGrid();
		val gC = C.getGrid();
		
		Debug.assure(Grid.match(gA.colBs, gC.rowBs),
		"Column partition of first and result matrix mismatch");
		Debug.assure(Grid.match(gB.colBs, gC.colBs),
		"Column partition of second and result matrix mismatch");
		
		finish ateach (Dist.makeUnique()) {
			//
			val bsA = A.handleBS();
			val bsB = B.local();
			val bsC = C.handleBS();
			BlockBlockMult.transMult(bsA.blockMap, bsB.blockMap, bsC.blockMap, plus);
		}
		
		return C;

	}
	
	public static def multTrans(
			A:DistBlockMatrix, 
			B:DupBlockMatrix{self.N==A.N},
			C:DistBlockMatrix(A.M,B.M), plus:Boolean) : DistBlockMatrix(C) {

		val gA = A.getGrid();
		val gB = B.getGrid();
		val gC = C.getGrid();

		Debug.assure(Grid.match(gA.rowBs, gC.rowBs),
		"Row partition of first and result matrix mismatch");
		Debug.assure(Grid.match(gB.rowBs, gC.colBs),
		"Row partition of second and result matrix mismatch");
		
		finish ateach (Dist.makeUnique()) {
			//
			val bsA = A.handleBS();
			val bsB = B.local();
			val bsC = C.handleBS();
			
			BlockBlockMult.multTrans(bsA.blockMap, bsB.blockMap, bsC.blockMap, plus);
		}
		return C;	

	}
	//==================================================
	
}
