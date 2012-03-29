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


/**
 * Support distributed block matrix multiplies with duplicate block matrix, and
 * duplicated block matrix multiplies with distributed block matrix. Matrix partitions
 * are check but not distribution. User must make sure the block distribution is correct,
 * otherwise, result is not correct or runtime exception occurs.
 * <p>
 * Current implementation relies on DistGrid to create corresponding block map for 
 * easy and fast access blocks. Therefore, distributed block matrix must have blocks
 * distributed in grid-like map.
 * 
 */
public class DistDupMult {
	
	/**
	 * 
	 */
	public static def comp(
			A:DistBlockMatrix, 
			B:DupBlockMatrix(A.N), 
			C:DistBlockMatrix(A.M,B.N), plus:Boolean) : DistBlockMatrix(C) {

		val gA = A.getGrid();
		val gB = B.getGrid();
		val gC = C.getGrid();
		
		Debug.assure(DistGrid.isVertical(gA, A.getMap()), 
				"First dist block matrix must has vertical distribution");
		Debug.assure(DistGrid.isVertical(gC, C.getMap()), 
				"Output dist block matrix must has vertical distribution");

		Debug.assure(Grid.match(gA.rowBs, gC.rowBs),
				"Row partition of first and result matrix mismatch");
		Debug.assure(Grid.match(gB.colBs, gC.colBs),
				"Column partition of second and result matrix mismatch");

		/* Timing */ val st = Timer.milliTime();
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
		/* Timing */ C.calcTime += Timer.milliTime() - st;
		return C;
	}
	
	public static def compTransMult(
			A:DistBlockMatrix, 
			B:DupBlockMatrix(A.M), 
			C:DistBlockMatrix(A.N,B.N), plus:Boolean) : DistBlockMatrix(C) {

		val gA = A.getGrid();
		val gB = B.getGrid();
		val gC = C.getGrid();
		
		Debug.assure(DistGrid.isHorizontal(gA, A.getMap()), 
				"First dist block matrix must has horizontal distribution");
		Debug.assure(DistGrid.isHorizontal(gC, C.getMap()), 
				"Output dist block matrix must has horizontal distribution");

		Debug.assure(Grid.match(gA.colBs, gC.rowBs),
				"Column partition of first and result matrix mismatch");
		Debug.assure(Grid.match(gB.colBs, gC.colBs),
				"Column partition of second and result matrix mismatch");
		
		/* Timing */ val st = Timer.milliTime();
		finish ateach (Dist.makeUnique()) {
			//
			val bsA = A.handleBS();
			val bsB = B.local();
			val bsC = C.handleBS();
			bsA.buildBlockMap();
			bsB.buildBlockMap();
			bsC.buildBlockMap();
			BlockBlockMult.transMult(bsA.blockMap, bsB.blockMap, bsC.blockMap, plus);
		}
		/* Timing */ C.calcTime += Timer.milliTime() - st;
		return C;

	}
	
	public static def compMultTrans(A:DistBlockMatrix, B:DupBlockMatrix{self.N==A.N},C:DistBlockMatrix(A.M,B.M), 
			plus:Boolean) : DistBlockMatrix(C) {

		val gA = A.getGrid();
		val gB = B.getGrid();
		val gC = C.getGrid();

		Debug.assure(DistGrid.isVertical(gA, A.getMap()), 
				"First dist block matrix must has vertical distribution");
		Debug.assure(DistGrid.isVertical(gC, C.getMap()), 
				"Output dist block matrix must has vertical distribution");

		Debug.assure(Grid.match(gA.rowBs, gC.rowBs),
				"Row partition of first and result matrix mismatch");
		Debug.assure(Grid.match(gB.rowBs, gC.colBs),
				"Row partition of second and result matrix mismatch");
		
		/* Timing */ val st = Timer.milliTime();
		finish ateach (Dist.makeUnique()) {
			//
			val bsA = A.handleBS();
			val bsB = B.local();
			val bsC = C.handleBS();
			bsA.buildBlockMap();
			bsB.buildBlockMap();
			bsC.buildBlockMap();
	
			BlockBlockMult.multTrans(bsA.blockMap, bsB.blockMap, bsC.blockMap, plus);
		}
		/* Timing */ C.calcTime += Timer.milliTime() - st;

		return C;
		
	}
			
	//================================================
		
	public static def comp(
			A:DupBlockMatrix, 
			B:DistBlockMatrix(A.N), 
			C:DistBlockMatrix(A.M,B.N), plus:Boolean) : DistBlockMatrix(C) {

		val gA = A.getGrid();
		val gB = B.getGrid();
		val gC = C.getGrid();
		
		Debug.assure(DistGrid.isHorizontal(gB, B.getMap()), 
				"Second dist block matrix must has horizontal distribution");
		Debug.assure(DistGrid.isHorizontal(gC, C.getMap()), 
				"Output dist block matrix must has horizontal distribution");
		
		Debug.assure(Grid.match(gA.rowBs, gC.rowBs),
				"Row partition of first and result matrix mismatch");
		Debug.assure(Grid.match(gB.colBs, gC.colBs),
				"Column partition of second and result matrix mismatch");

		/* Timing */ val st = Timer.milliTime();
		finish ateach (Dist.makeUnique()) {
			//
			val bsA = A.local();
			val bsB = B.handleBS();
			val bsC = C.handleBS();
			bsA.buildBlockMap();
			bsB.buildBlockMap();
			bsC.buildBlockMap();
			BlockBlockMult.mult(bsA.blockMap, bsB.blockMap, bsC.blockMap, plus);
		}
		/* Timing */ C.calcTime += Timer.milliTime() - st;

		return C;
	}
	public static def compTransMult(
			A:DupBlockMatrix, 
			B:DistBlockMatrix(A.M), 
			C:DistBlockMatrix(A.N,B.N), plus:Boolean) : DistBlockMatrix(C) {

		val gA = A.getGrid();
		val gB = B.getGrid();
		val gC = C.getGrid();

		Debug.assure(DistGrid.isHorizontal(gB, B.getMap()), 
				"Second dist block matrix must has horizontal distribution");
		Debug.assure(DistGrid.isHorizontal(gC, C.getMap()), 
				"Output dist block matrix must has horizontal distribution");
		
		Debug.assure(Grid.match(gA.colBs, gC.rowBs),
				"Column partition of first and result matrix mismatch");
		Debug.assure(Grid.match(gB.colBs, gC.colBs),
				"Column partition of second and result matrix mismatch");

		/* Timing */ val st = Timer.milliTime();
		finish ateach (Dist.makeUnique()) {
			//
			val bsA = A.local();
			val bsB = B.handleBS();
			val bsC = C.handleBS();
			bsA.buildBlockMap();
			bsB.buildBlockMap();
			bsC.buildBlockMap();
			BlockBlockMult.transMult(bsA.blockMap, bsB.blockMap, bsC.blockMap, plus);
		}
		/* Timing */ C.calcTime += Timer.milliTime() - st;
		
		return C;

	}
	
	public static def compMultTrans(
			A:DupBlockMatrix, B:DistBlockMatrix{self.N==A.N},C:DistBlockMatrix(A.M,B.M), 
			plus:Boolean) : DistBlockMatrix(C) {

		val gA = A.getGrid();
		val gB = B.getGrid();
		val gC = C.getGrid();

		Debug.assure(DistGrid.isVertical(gB, B.getMap()), 
				"Second dist block matrix must has vertical distribution");
		Debug.assure(DistGrid.isVertical(gC, C.getMap()), 
				"Output dist block matrix must has vertical distribution");

		Debug.assure(Grid.match(gA.rowBs, gC.rowBs),
				"Row partition of first and result matrix mismatch");
		Debug.assure(Grid.match(gB.rowBs, gC.colBs),
				"Row partition of second and result matrix mismatch");

		/* Timing */ val st = Timer.milliTime();
		finish ateach (Dist.makeUnique()) {
			//
			val bsA = A.local();
			val bsB = B.handleBS();
			val bsC = C.handleBS();
			bsA.buildBlockMap();
			bsB.buildBlockMap();
			bsC.buildBlockMap();
			
			BlockBlockMult.multTrans(bsA.blockMap, bsB.blockMap, bsC.blockMap, plus);
		}
		/* Timing */ C.calcTime += Timer.milliTime() - st;
		return C;
		
	}
	

}
