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

package x10.matrix;

import x10.io.Console;
import x10.util.Random;
import x10.util.Timer;

import x10.matrix.blas.BLAS;
import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.sparse.SparseCSC;

/**
 * 
 */
public class VectorMult {
	
	/**
	 * Matrix-vector multiply, C = A * B, where A is matrix, B and C are vectors.
	 */
	public static def mult(A:Matrix, B:Vector(A.N), C:Vector(A.M), plus:Boolean): Vector(C) {
		if (A instanceof DenseMatrix) 
			mult(A as DenseMatrix, B, C, plus);
		else if (A instanceof SparseCSC) 
			mult(A as SparseCSC, B, C, plus);
		else if (A instanceof SymMatrix) 
			mult(A as SymMatrix, B, C, plus);
		else if (A instanceof TriMatrix)
			mult(A as TriMatrix, B, C, plus);
		//else if (A instanceof Diagonal)
		//	this.mult(A as Diagonal, B, plus);
		else
			throw new UnsupportedOperationException("Operation not supported in vector multiply: " +
					A.typeName() + " * " + B.typeName()+" = "+C.typeName() );
		return C;
	}
	
	/**
	 * Matrix-vector multiply, C = B * A, where A is matrix, B and C are vectors.
	 */
	public static def mult(B:Vector, A:Matrix(B.M), C:Vector(A.N), plus:Boolean): Vector(C) {
		if (A instanceof DenseMatrix) 
			mult(B, A as DenseMatrix, C, plus);
		else if (A instanceof SparseCSC) 
			mult(B, A as SparseCSC, C, plus);
		else if (A instanceof SymMatrix) 
			mult(B, A as SymMatrix, C, plus);
		else if (A instanceof TriMatrix)
			mult(B, A as TriMatrix, C, plus);
		else
			throw new UnsupportedOperationException("Operation not supported in vector multiply: " +
					B.typeName() + " * " + A.typeName()+" = "+C.typeName() );
		return C;
	}
	
	//=================================================================================
	// X10 driver for Dense multiplies vector
	//=================================================================================
	public static def x10Mult(A:DenseMatrix, B:Vector(A.N), C:Vector(A.M), plus:Boolean) =
		mult(A, B, 0, C, 0, plus);
	
	public static def mult(A:SparseCSC, B:Vector(A.N), C:Vector(A.M), plus:Boolean)=
		mult(A, B, 0, C, 0, plus);
	
	/**
	 * Multiply matrix with a segment of vector and store result in a segment of output vector
	 */
	public static def mult(A:SparseCSC, B:Vector, var offsetB:Int, C:Vector(A.M), offsetC:Int, plus:Boolean):Vector(C) {
		
		Debug.assure(offsetB+A.N<=B.M, "Input vector overflow");
		Debug.assure(offsetC+A.M<=C.M, "Output vector overflow");
		if (!plus) {
			for (var i:Int=offsetC; i< offsetC+A.M; i++) C.d(i) =0;		
		}
		for (var c:Int=0; c<A.N; c++, offsetB++) {
			val colA = A.getCol(c);
			val  v2  = B.d(offsetB);
			for (var ridx:Int=0; ridx < colA.size(); ridx++) {
				val r = colA.getIndex(ridx);
				val v1 = colA.getValue(ridx);
				C.d(r+offsetC) += v1 * v2;
			}
		}
		
		return C;
	}
	
	/**
	 * Multiply matrix with a segment of vector and store result in a segment of output vector
	 */
	public static def mult(A:DenseMatrix, B:Vector, var offsetB:Int, C:Vector(A.M), offsetC:Int, plus:Boolean):Vector(C) {
		
		Debug.assure(offsetB+A.N<=B.M, "Input vector overflow");
		Debug.assure(offsetC+A.M<=C.M, "Output vector overflow");
		if (!plus) {
			for (var i:Int=offsetC; i< offsetC+A.M; i++) C.d(i) =0;
		};
		var idxA:Int=0;
		for (var c:Int=0; c<A.N; c++, offsetB++) {
			val  v2  = B.d(offsetB);
			for (var r:Int=0; r < A.M; r++, idxA++) {
				val v1 = A.d(idxA);
				C.d(r+offsetC) += v1 * v2;
			}
		}
		return C;
	}

	//==========================================================
	public static def x10Mult(B:Vector, A:DenseMatrix(B.M), C:Vector(A.N), plus:Boolean) =
		mult(B, 0, A, C, 0, plus);
	
	public static def mult(B:Vector, A:SparseCSC(B.M), C:Vector(A.N), plus:Boolean)=
		mult(B, 0, A, C, 0, plus);

	public static def mult(B:Vector, var offsetB:Int, A:DenseMatrix, C:Vector, var offsetC:Int, plus:Boolean):Vector(C) {
		Debug.assure(offsetB+A.M<=B.M, "Input vector overflow");
		Debug.assure(offsetC+A.N<=C.M, "Output vector overflow");
		if (!plus) {
			for (var i:Int=offsetC; i<offsetC+A.N; i++) C.d(i) =0;
		}
		var idxA:Int = 0;
		for (var c:Int=0; c<A.N; c++, offsetC++) {
			var v:Double = 0;
			var idxB:Int = offsetB;
			for (var r:Int=0; r<A.M; r++, idxB++, idxA++) {
				v += B.d(idxB) * A.d(idxA);
			}
			C.d(offsetC) = v;
		}
		return C;
	}

	public static def mult(B:Vector, var offsetB:Int, A:SparseCSC, C:Vector, var offsetC:Int, plus:Boolean):Vector(C) {
		Debug.assure(offsetB+A.M<=B.M, "Input vector overflow");
		Debug.assure(offsetC+A.N<=C.M, "Output vector overflow");
		if (!plus) {
			for (var i:Int=offsetC; i<offsetC+A.N; i++) C.d(i) =0;
		}
		for (var c:Int=0; c<A.N; c++, offsetC++) {
			val colA = A.getCol(c);
			var v:Double = 0;
			for (var idxA:Int=0; idxA<colA.size(); idxA++) {
				val r = colA.getIndex(idxA);
				val v2= colA.getValue(idxA);
				v += B.d(offsetB+r) * v2;
			}
			C.d(offsetC) = v;
		}
		return C;
	}

	//============================

	//-------------------------------------------------------------------
	// Using Blas routines: C = A * b, or self += A * b,
	//-------------------------------------------------------------------
	/**
	 * Using BLAS routine: C = A * B or C = A * B + C
	 */
	public static def mult(A:DenseMatrix, B:Vector(A.N), C:Vector(A.M), plus:Boolean):Vector(C) {
		DenseMatrixBLAS.comp(A, B, C, plus);
		return C;
	}

	/**
	 * Using BLAS routine: C = B * A or C = B * A + C
	 */
	public static def mult(B:Vector, A:DenseMatrix(B.M), C:Vector(A.N), plus:Boolean):Vector(C) {
		DenseMatrixBLAS.compTransMult(A, B, C, plus);
		return C;
	}

	//-------------
	public static def mult(A:SymMatrix, B:Vector(A.N), C:Vector(A.M), plus:Boolean):Vector(C) {
		val beta = plus?1.0:0.0;
		BLAS.compSymMultVec(A.d, B.d, C.d, 
				[A.M, A.N],
				[1.0, beta]);
		return C;
	}
	
	public static def mult(B:Vector, A:SymMatrix(B.M), C:Vector(A.N), plus:Boolean):Vector(C) =
		mult(A, B as Vector(A.N), C as Vector(A.M), plus);

	//-------------
	public static def mult(A:TriMatrix, C:Vector(A.M)):Vector(C) {
		BLAS.compTriMultVec(A.d, C.d, C.M, 0); 
		return C;
	}
	
	public static def mult(C:Vector, A:TriMatrix(C.M)):Vector(C) {
		BLAS.compTriMultVec(A.d, C.d, C.M, 1); 
		return C;
	}
	//===========================================================
	
}