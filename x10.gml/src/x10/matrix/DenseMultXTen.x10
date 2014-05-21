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

import x10.matrix.util.MathTool;

/**
 * This class implements dense matrix multiplication purely in X10.
 */
public class DenseMultXTen {
	// X10 dense * vector driver
	// NOTE: do not merge with MatrixMultXTen, unless the compiler bug is fixed

	/**
	 * X10 matrix * vector driver: C = A &#42 B or  C += A &#42 B 
	 * if plus is true.
	 */
	public static def comp(
			A:DenseMatrix, 
            B:Vector(A.N), 
            C:Vector(A.M), plus:Boolean) :void {
		var aidx:Long=0;
		if (!plus) for (var i:Long=0; i<A.M; i++) C.d(i) = 0.0;

		for (var c:Long=0; c<A.N; c++) {
			for (var r:Long=0; r<A.M; r++, aidx++) {
				C.d(r) += A.d(aidx) * B.d(c);
			}
		}
	}

	/**
	 * X10 matrix * vector driver: C = A <sup>T</sup> &#42 B or  C += A <sup>T</sup> &#42 B 
	 * if plus is true.
	 */
	public static def compTransMult(
			A:DenseMatrix, 
            B:Vector(A.M), 
            C:Vector(A.N), plus:Boolean) :void {
		var aidx:Long=0;
		if (!plus) for (var i:Long=0; i<A.N; i++) C.d(i) = 0.0;

		for (var c:Long=0; c<A.N; c++) {
			for (var r:Long=0; r<A.M; r++, aidx++) {
				C.d(c) += A.d(aidx) * B.d(r);
			}
		}
	}

	// X10 dense * dense driver
	// NOTE: do not merge with MatrixMultXTen, unless the compiler bug is fixed

	/**
	 * X10 driver for dense matrix multiplication. Compute C += A &#42 B if plus is true
	 * else C = A &#42 B.
	 * 
	 * @param A		first operand matrix in multiply
	 * @param B		second operand matrix 
	 * @param C		result used to store the result
	 * @param plus	add-on flag
	 */
	public static def comp(
			A:DenseMatrix, 
			B:DenseMatrix{B.M==A.N}, 
			C:DenseMatrix{C.M==A.M, C.N==B.N}, 
			plus:Boolean):void {
				
		var startcol:Long = 0;
		var v1idx:Long;
		var v2idx:Long;
		
		v2idx = 0;
		for (var c:Long=0; c<B.N; c++) {
			
			if (!plus) {
				for (var i:Long=startcol; i<startcol+C.M; i++) C.d(i) = 0.0;
			}
			v1idx = 0;
			for (var k:Long=0; k<A.N; k++) {
				//val v2    = B(k, c);
				val v2 = B.d(v2idx++);
				//
				if (MathTool.isZero(v2)) {
					v1idx += A.M;
				} else {
					for (var r:Long=0; r<A.M; r++) {
						//val v1= A(r, k);
						val v1 = A.d(v1idx++);
						C.d(startcol+r) += v1 * v2;
						//C.calcCount +=2;
					}
				}
			}
			startcol += C.M;
		}
	}
			
	//C = A^T * B + plus*C
	/**
	 * Compute dense matrix multiplication in X10.
	 * C+= A<sup>T</sup> &#42 B if plus is true, else C = A<sup>T</sup> &#42 B
	 * 
	 * @param A		first operand matrix in multiply
	 * @param B		second operand matrix
	 * @param C		result used to store the result
	 * @param plus	add-on flag
	 */
	public static def compTransMult(
			A:DenseMatrix, 
			B:DenseMatrix{B.M==A.M}, 
			C:DenseMatrix{C.M==A.N, C.N==B.N}, 
			plus:Boolean):void {
				
		var startcol:Long = 0;
		var v1idx:Long;
		var v2idx:Long;
						
		v2idx = 0;
		for (var c:Long=0; c<B.N; c++) {
			if (!plus) {
				for (var i:Long=startcol; i<startcol+C.M; i++) C.d(i) = 0.0D;
			}
			
			for (var k:Long=0; k<A.M; k++) {
				//val v2    = B(k, c);
				val v2 = B.d(v2idx++);
				//
				if (MathTool.isZero(v2)) continue;
				v1idx = k;
				for (var r:Long=0; r<A.N; r++) {
					//val v1= A(k, r);
					val v1 = A.d(v1idx); 
					v1idx += A.M;
					C.d(startcol+r) += v1 * v2;
				}
			}
			startcol += C.M;
		}		
	}

	// C += A * B^T + plus*C
	/**
	 * X10 driver of dense matrix multiplication. 
	 * Compute C += A &#42 B<sup>T</sup>, else C = A &#42 B<sup>T</sup>
	 * 
	 * @param A		first operand matrix in multiply which will be used in transposed form
	 * @param B		second operand matrix
	 * @param C		result used to store the result
	 * @param plus	add-on flag
	 */
	public static def compMultTrans(
			A:DenseMatrix, 
			B:DenseMatrix{B.N==A.N}, 
			C:DenseMatrix{C.M==A.M, C.N==B.M}, 
			plus:Boolean
		):void {
		
		var startcol:Long = 0;
		var v1idx:Long;
		var v2idx:Long;
						
		for (var c:Long=0; c<B.M; c++) {
			v1idx = 0; 
			v2idx = c;
			
			if (!plus) {
				for (var i:Long=startcol; i<startcol+C.M; i++) C.d(i) = 0.0;
			}
			
			for (var k:Long=0; k<A.N; k++) {
				//val v2 = B(c, k);
				val v2 = B.d(v2idx); v2idx += B.M;
				//
				if (MathTool.isZero(v2)) {
					v1idx += A.M;
				} else {
					for (var r:Long=0; r<A.M; r++) {
						//val v1= A(r, k);
						val v1 = A.d(v1idx++);
						C.d(startcol+r) += v1 * v2;
					}
				}
			}
			startcol += C.M;
		} 
	}
					
	// C= A.T() * B.T() + plus*C
	/**
	 * X10 driver of dense matrix multiplication. 
	 * Compute C+= A<sup>T</sup> &#42 B<sup>T</sup>, 
     * else  C = A<sup>T</sup> &#42 B<sup>T</sup>
	 * 
	 * @param A		first operand matrix in multiply which will be used in transposed form
	 * @param B		second operand matrix which will be used in transposed form
	 * @param C		result used to store the result
	 * @param plus	add-on flag
	 */
	public static def compTransMultTrans(
			A:DenseMatrix, 
			B:DenseMatrix{B.N==A.M}, 
			C:DenseMatrix{C.M==A.N, C.N==B.M}, 
			plus:Boolean
		):void {

		var startrow:Long = 0L;
		var v1idx:Long;
		var v2idx:Long;
		//Debug.assure(C.M>=A.N&&A.M==B.N&&B.M<=C.N, 
		//		   "Dimension mismatch in Dense.T()*Dense.T()");
		
		if (!plus) {
			for (var i:Long=0; i<C.M*C.N; i++) C.d(i) = 0.0D;
		}
		v1idx = 0;
		for (var r:Long=0; r<A.N; r++) {
			v2idx = 0;
			for (var k:Long=0; k<A.M; k++) {
				//val v1    = A(k, r);
				val v1 = A.d(v1idx++);
				//
				if (MathTool.isZero(v1)) {
					v2idx += B.M;
				} else {
					startrow = r;
					for (var c:Long=0; c<B.M; c++, startrow+=C.M) {
						//val v2= B(c, k);
						val v2 = B.d(v2idx++);
						C.d(startrow) += v1 * v2;
					}
				}
			}
		}
	}
			
	// Simplified mult interface
							
	/**
	 * X10 driver of dense matrix multiplication. Compute C = A &#42 B,
	 * 
     * @param A      first operand matrix in multiply
	 * @param B      second operand matrix in multiply
	 * @param C      dense matrix used to store the result
	 */
	public static def comp(
			A:DenseMatrix, 
			B:DenseMatrix{A.N==B.M}, 
			C:DenseMatrix{C.M==A.M, C.N==B.N}
	) : void {
		DenseMultXTen.comp(A, B, C, false);
	}
				
	/**
	 * X10 driver of dense matrix multiplication. 
	 * 
	 * @param A      first operand matrix in multiply
	 * @param B      second operand matrix in multiply
	 * @return       a new dense matrix which is used to store the result
	 */
	public static def comp(A:DenseMatrix, B:DenseMatrix{A.N==B.M}):DenseMatrix(A.M,B.N) {
		val C = DenseMatrix.make(A.M, B.N);
		DenseMultXTen.comp(A, B, C, false);
		return C;
	}
}
