/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.matrix;

import x10.array.BlockingUtils;

import x10.matrix.blas.BLAS;
import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.sparse.SparseCSC;

/**
 * Performs matrix-vector multiplication.
 */
public class VectorMult {
	/**
	 * Matrix-vector multiply, C = A * B, where A is matrix, B and C are vectors.
	 */
	public static def comp(A:Matrix, B:Vector(A.N), C:Vector(A.M), plus:Boolean): Vector(C) {
		if (A instanceof DenseMatrix) 
			comp(A as DenseMatrix, B, C, plus);
		else if (A instanceof SparseCSC) 
			comp(A as SparseCSC, B, C, plus);
		else if (A instanceof SymDense) 
			comp(A as SymDense, B, C, plus);
		else if (A instanceof TriDense)
			comp(A as TriDense, B, C, plus);
		else
			throw new UnsupportedOperationException("Operation not supported in vector multiply: " +
					A.typeName() + " * " + B.typeName()+" = "+C.typeName() );
		return C;
	}
	
	/**
	 * Matrix-vector multiply, C = B * A, where A is matrix, B and C are vectors.
	 */
	public static def comp(B:Vector, A:Matrix(B.M), C:Vector(A.N), plus:Boolean): Vector(C) {
		if (A instanceof DenseMatrix) 
			comp(B, A as DenseMatrix, C, plus);
		else if (A instanceof SparseCSC) 
			comp(B, A as SparseCSC, C, plus);
		else if (A instanceof SymDense) 
			comp(B, A as SymDense, C, plus);
		else if (A instanceof TriDense)
			comp(B, A as TriDense, C, plus);
		else
			throw new UnsupportedOperationException("Operation not supported in vector multiply: " +
					B.typeName() + " * " + A.typeName()+" = "+C.typeName() );
		return C;
	}
	

	public static def comp(A:Matrix, B:Vector, offB:Long, C:Vector, offC:Long, plus:Boolean): Vector(C) {
		if (A instanceof DenseMatrix) 
			comp(A as DenseMatrix, B, offB, C, offC, plus);
		else if (A instanceof SparseCSC) 
			comp(A as SparseCSC, B, offB, C, offC, plus);
		else
			throw new UnsupportedOperationException("Operation not supported in vector multiply: " +
					A.typeName() + " * " + B.typeName()+" = "+C.typeName() );
		return C;
	}
	
	public static def comp(B:Vector, offB:Long, A:Matrix, C:Vector, offC:Long, plus:Boolean): Vector(C) {
		if (A instanceof DenseMatrix) 
			comp(B, offB, A as DenseMatrix, C, offC, plus);
		else if (A instanceof SparseCSC) 
			comp(B, offB, A as SparseCSC, C, offC, plus);
		else 
			throw new UnsupportedOperationException("Operation not supported in vector multiply: " +
					B.typeName() + " * " + A.typeName()+" = "+C.typeName() );
		return C;
	}


	// X10 driver for Dense multiplies vector

	public static def x10Mult(A:DenseMatrix, B:Vector(A.N), C:Vector(A.M), plus:Boolean) =
		comp(A, B, 0, C, 0, plus);
	
	/**
	 * Multiply matrix with a segment of vector and store result in a segment of output vector
	 */
	public static def comp(A:DenseMatrix, B:Vector, var offsetB:Long, C:Vector, offsetC:Long, plus:Boolean):Vector(C) {
		assert (offsetB+A.N <= B.M) :
            "Second input vector overflow, offset:"+offsetB+" A.N:"+A.N+" > B.M:"+B.M;
		assert (offsetC+A.M <= C.M) :
            "Output vector overflow, offset:"+offsetC+" A.M:"+A.M+" C.M:"+C.M;

        if (!plus) C.d.clear(offsetC, A.M);
		var idxA:Long=0;
		for (var c:Long=0; c<A.N; c++, offsetB++) {
			val  v2  = B.d(offsetB);
			for (var r:Long=0; r < A.M; r++, idxA++) {
				val v1 = A.d(idxA);
				C.d(r+offsetC) += v1 * v2;
			}
		}
		return C;
	}

	public static def comp(A:SparseCSC, B:Vector(A.N), C:Vector(A.M), plus:Boolean)=
		comp(A, B, 0, C, 0, plus);
	
	/**
	 * Multiply matrix with a segment of vector and store result in a segment of output vector
	 */
	public static def comp(A:SparseCSC, B:Vector, offsetB:Long, C:Vector, offsetC:Long, plus:Boolean):Vector(C) {
		assert (offsetB+A.N <= B.M) :
            "Input vector overflow, offsetB:"+offsetB+" len:"+A.N+" B size:"+B.M;
		assert (offsetC+A.M <= C.M) :
            "Output vector overflow, offset:"+offsetC+" len:"+A.M+" output size:"+C.M;

		if (!plus) C.d.clear(offsetC, A.M);
		for (col in 0..(A.N-1)) {
			val colA = A.getCol(col);
			val v2 = B.d(offsetB+col);
			for (ridx in 0..(colA.size()-1)) {
				val r = colA.getIndex(ridx);
				val v1 = colA.getValue(ridx);
				C.d(r+offsetC) += v1 * v2;
			}
		}
		
		return C;
	}

    private static struct RecursiveBisection1D(start:Long, end:Long, grainSize:Long) {
        public def this(start:Long, end:Long) {
            val grainSize = (end-start) / (x10.xrx.Runtime.NTHREADS*8);
            property(start, end, grainSize);
        }

        public def this(start:Long, end:Long, grainSize:Long) {
            property(start, end, grainSize);
        }

        public def execute(body:(min_i:Long, max_i:Long)=> void) {
            if ((end-start) > grainSize) {
                val secondHalf=RecursiveBisection1D((start+end)/2L, end, grainSize);
                async secondHalf.execute(body);
                val firstHalf=RecursiveBisection1D(start, (start+end)/2L, grainSize);
                firstHalf.execute(body);
            } else {
                body(start, end-1);
            }
        }
    }
	
	public static def x10Mult(B:Vector, A:DenseMatrix(B.M), C:Vector(A.N), plus:Boolean) =
		comp(B, 0, A, C, 0, plus);
	
	public static def comp(B:Vector, A:SparseCSC(B.M), C:Vector(A.N), plus:Boolean)=
		comp(B, 0, A, C, 0, plus);

	public static def comp(B:Vector, var offsetB:Long, A:DenseMatrix, C:Vector, var offsetC:Long, plus:Boolean):Vector(C) {
		assert (offsetB+A.M <= B.M) :
            "Input vector overflow, offset:"+offsetB+" len:"+A.M+" length:"+B.M;
		assert (offsetC+A.N <= C.M) :
            "Output vector overflow, output offset:"+offsetC+" A.N:"+A.N+" C.M:"+C.M;

        if (!plus) C.d.clear(offsetC, A.N);
		var idxA:Long = 0;
		for (var c:Long=0; c<A.N; c++, offsetC++) {
			var v:ElemType = 0;
			var idxB:Long = offsetB;
			for (var r:Long=0; r<A.M; r++, idxB++, idxA++) {
				v += B.d(idxB) * A.d(idxA);
			}
			C.d(offsetC) += v;
		}
		return C;
	}

	public static def comp(B:Vector, var offsetB:Long, A:SparseCSC, C:Vector, var offsetC:Long, plus:Boolean):Vector(C) {
		assert (offsetB+A.M <= B.M) :
            "Input vector overflow, offset:"+offsetB+" len:"+A.M+" length:"+B.M;
		assert (offsetC+A.N <= C.M) :
            "Output vector overflow, output offset:"+offsetC+" A.N:"+A.N+" C.M:"+C.M;

        if (!plus) C.d.clear(offsetC, A.N);
		for (var c:Long=0; c<A.N; c++, offsetC++) {
			val colA = A.getCol(c);
			var v:ElemType = 0;
			for (var idxA:Long=0; idxA<colA.size(); idxA++) {
				val r = colA.getIndex(idxA);
				val v2= colA.getValue(idxA);
				v += B.d(offsetB+r) * v2;
			}
			C.d(offsetC) += v;
		}
		return C;
	}

	/**
	 * Using BLAS routine: C = A * B or C = A * B + C
	 */
	public static def comp(A:DenseMatrix, B:Vector(A.N), C:Vector(A.M), plus:Boolean):Vector(C) {
		val alpha = 1 as ElemType;
		val beta = (plus?1:0) as ElemType;
		DenseMatrixBLAS.comp(alpha, A, B, beta, C);
		return C;
	}

	/**
	 * Using BLAS routine: C = B * A or C = B * A + C
	 */
	public static def comp(B:Vector, A:DenseMatrix(B.M), C:Vector(A.N), plus:Boolean):Vector(C) {
		val alpha = 1 as ElemType;
		val beta = (plus?1:0) as ElemType;
		DenseMatrixBLAS.compTransMult(alpha, A, B, beta, C);
		return C;
	}

	public static def comp(A:SymDense, B:Vector(A.N), C:Vector(A.M), plus:Boolean):Vector(C) {
		val beta = (plus?1:0) as ElemType;
		BLAS.compSymMultVec(1 as ElemType, A.d, B.d, beta, C.d, [A.M, A.N]);
		return C;
	}
	
	public static def comp(B:Vector, A:SymDense(B.M), C:Vector(A.N), plus:Boolean):Vector(C) =
		comp(A, B as Vector(A.N), C, plus);

	public static def comp(A:TriDense, C:Vector(A.M)):Vector(C) {
		BLAS.compTriMultVec(A.d, A.upper, C.d, C.M, 0n);
		return C;
	}
	
	public static def comp(C:Vector, A:TriDense(C.M)):Vector(C) {
		BLAS.compTriMultVec(A.d, A.upper, C.d, C.M, 1n); 
		return C;
	}
}
