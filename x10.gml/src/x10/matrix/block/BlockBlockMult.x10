	
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
	public static def mult(
			A:BlockMatrix, 
			B:BlockMatrix(A.N),
			C:BlockMatrix(A.M,B.N),
			plus:Boolean):BlockMatrix(C) {
		Debug.assure(Grid.match(C.grid.rowBs, A.grid.rowBs), "Row partiton mismatch in result matrix");
		Debug.assure(Grid.match(C.grid.colBs, B.grid.colBs), "Column partition mismatch in result matrix");
		Debug.assure(Grid.match(A.grid.colBs, B.grid.rowBs), "Row and column partition mismatch");
		
		var startcol:Int = 0;
		val aM = A.grid.numRowBlocks;
		val aN = A.grid.numColBlocks;
		val bM = B.grid.numRowBlocks;
		val bN = B.grid.numColBlocks;
		val cM = C.grid.numRowBlocks;
		val cN = C.grid.numColBlocks;
		
		for (var c:Int=0; c<cN; c++) {
			if (!plus) {
				for (var i:Int=startcol; i<startcol+cM; i++)
					C.listBs(i).getMatrix().reset();
			}

			for (var k:Int=0; k<aN; k++) {
				val v2    = B.listBs(k+bM*c).getMatrix(); //m2(k, c)
				//
				for (var r:Int=0; r<A.M; r++) {
					val v1= A.listBs(r+k*aM).getMatrix(); //m1(r, k)
					val v3= C.listBs(startcol+r).getMatrix();
					v3.mult(v1 as Matrix(v3.M), v2 as Matrix(v1.N,v3.N), true);
				}
			}
			startcol += cM;
		}
		return C;
	}
			
	public static def mult(
			A:ArrayList[MatrixBlock], 
			B:ArrayList[MatrixBlock],
			C:BlockMatrix,
			plus:Boolean):BlockMatrix(C) {
		
		// var startcol:Int = 0;
		// if (! plus) this.reset();
		// 
		// for (var cb:Int=0; cb < B.grid.numColBlocks; cb++) {
		// 	for (var kb:Int=0; kb<A.grid.numColBlocks; kb++) {
		// 		for (var rb:Int=0; rb<A.grid.numRowBlocks; rb++) {
		// 			val c = this.findBlock(rb, cb).getMatrix();
		// 			val a = A.findBlock(rb, kb).getMatrix() as Matrix(c.M);
		// 			val b = B.findBlock(kb, cb).getMatrix() as Matrix(a.N,c.N);
		// 			c.mult(a, b, true);
		// 		}
		// 	}
		// }
		return C;	
	}
}
	