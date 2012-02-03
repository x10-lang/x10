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

package x10.matrix.distblock.mult;

import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Debug;

import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;


import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.BlockSet;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupBlockMatrix;

/**
 * One-way partitioning (distribution) distributed matrix multiply
 * (1, rowblocks) * (rowblocks, 1) ==> (1, 1)
 */
public class DistDupMult {
	
	/**
	 * 
	 */
	public static def mult(A:DistBlockMatrix, 
			B:DupBlockMatrix(A.N), 
			C:DistBlockMatrix(A.M,B.N), plus:Boolean) : DistBlockMatrix(C) {

		val Agrid = A.getGrid();
		val Cgrid = C.getGrid();
		val Amap  = A.getMap();
		val Cmap  = C.getMap();
		
		//Global.assure(A.flagTranspose == true);
		Debug.assure(Agrid.numRowBlocks==1 && Bgrid.numColBlocks==1, 
				"Only single row and column partitioning is allowed");
		Debug.assure(Grid.match(Agrid.colBs, Bgrid.rowBs), 
				"Partition of two matrices mismatch");
		Debug.assure(Amap.equals(Bmap), "Matrix blocks distribution mismatch");

		finish ateach (Dist.makeUnique()) {
			//
			var plusflag:Boolean = plus;
			val matC = C.local();
			val itrA = A.handleBS().iterator();
			val itrB = B.handleBS().iterator();

			while (itrA.hasNext() && itrB.hasNext()) {
				val matA = itrA.next().getMatrix() as Matrix(matC.M);
				val matB = itrB.next().getMatrix() as Matrix(matA.N, matC.N);
				
				matC.mult(matA, matB, plusflag);
				plusflag = true;
			}
		}
		
		C.allReduceSum(); 
		return C;
	}
	
	public static def transMult(A:DistBlockMatrix, 
			B:DistBlockMatrix(A.M), 
			C:DupBlockMatrix(A.N,B.N), plus:Boolean) : DupBlockMatrix(C) {

		val Agrid = A.getGrid();
		val Bgrid = B.getGrid();
		val Amap  = A.getMap();
		val Bmap  = B.getMap();
		
		//Global.assure(A.flagTranspose == true);
		Debug.assure(Agrid.numColBlocks==1 && Bgrid.numColBlocks==1, 
				"Only single column partitioning is allowed");
		Debug.assure(Grid.match(Agrid.rowBs, Bgrid.rowBs), 
				"Partition of two matrices mismatch");
		Debug.assure(Amap.equals(Bmap), "Blocks distribution mismatch");

		finish ateach (Dist.makeUnique()) {
			var plusflag:Boolean = plus;
			val matC = C.local();
			val itrA = A.handleBS().iterator();
			val itrB = B.handleBS().iterator();

			while (itrA.hasNext() && itrB.hasNext()) {
				val matA = itrA.next().getMatrix() as Matrix{self.N==matC.M};
				val matB = itrB.next().getMatrix() as Matrix(matA.M,matC.N);
				
				matC.transMult(matA, matB, plusflag);
				plusflag = true;
			}
		}
		
		C.allReduceSum(); 
		return C;
	}
	
	public static def multTrans(A:DistBlockMatrix, 
			B:DistBlockMatrix{self.N==A.N},
			C:DupBlockMatrix(A.M,B.M), plus:Boolean) : DupBlockMatrix(C) {

		val Agrid = A.getGrid();
		val Bgrid = B.getGrid();
		val Amap  = A.getMap();
		val Bmap  = B.getMap();
		
		//Global.assure(A.flagTranspose == true);
		Debug.assure(Agrid.numRowBlocks==1 && Bgrid.numRowBlocks==1,
			"Only single row and column partitioning is allowed");
		Debug.assure(Grid.match(Agrid.colBs, Bgrid.colBs), 
			"Partition of two matrices mismatch");
		Debug.assure(Amap.equals(Bmap), "Matrix blocks distribution mismatch");

		finish ateach (Dist.makeUnique()) {
			var plusflag:Boolean = plus;
			val matC = C.local();
			val itrA = A.handleBS().iterator();
			val itrB = B.handleBS().iterator();

			while (itrA.hasNext() && itrB.hasNext()) {
				val matA = itrA.next().getMatrix() as Matrix(matC.M);
				val matB = itrB.next().getMatrix() as Matrix(matC.N, matA.N);
				
				matC.multTrans(matA, matB, plusflag);
				plusflag = true;
			}
		}
		
		C.allReduceSum(); 
		return C;
	}
	//==================================================
	
}
