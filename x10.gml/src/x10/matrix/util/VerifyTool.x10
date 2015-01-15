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

package x10.matrix.util;

import x10.matrix.Matrix;
import x10.matrix.Vector;
import x10.matrix.ElemType;	
/**
 * This class is used to verify matrix and vector properties and operations. 
 */
public class VerifyTool {
    /**
     * Verify matrix multiplication for a number of random positions in the result.
	 *
	 * @param A		multiplication first operand matrix
	 * @param B		multiplication second operand matrix
	 * @param C		multiplication result for verification
	 * @param n		number of random samplings
	 * @return 		the verification result.
     */
	public static def verifyMatMult(A:Matrix, B:Matrix, C:Matrix, n:Long):Boolean {
		val rv = RandTool.getRandGen();
 
		for (var i:Long=0; i<n; i++) {
			val x = rv.nextLong(C.M);
			val y = rv.nextLong(C.N);
			if (!verifyMatMult(x, y, A, B, C)) return false;   
			Console.OUT.flush();
		}
		Console.OUT.println();
		return true;
	}

	/**
	 * Verify complete result of two matrices multiplication.
	 *
	 * @param A		multiplication first operand matrix
	 * @param B		multiplication second operand matrix
	 * @param C		multiplication result for verification
	 * @return 		the verification result
	 */
	public static def verifyMatMult(A:Matrix, B:Matrix, C:Matrix):Boolean {
		val rv = RandTool.getRandGen();
 
		for (var x:Long=0; x<C.M; x++) {
			for (var y:Long=0; y<C.N; y++){
				val cv   = C(x,y);
				var v:ElemType = 0.0 as ElemType;
				for (var k:Long=0; k<A.N; k++) v += A(x, k) * B(k, y);
				if (! MathTool.equals(v, cv)) {
					Console.OUT.printf("Check fail (%5d, %5d) Computed:%.8f Verified:%.8f\n", 
									   x, y, cv, v);
					Console.OUT.flush();
					return false;
				}
			}
		}
		Console.OUT.println();
		return true;
	}

	/**
	 * Verify matrix multiplication at a specified location in the result.
	 *
	 * @param x		row index
	 * @param y		column index
	 * @param A 	first operand in multiplication
	 * @param B 	second operand in multiplication
	 * @param C 	multiplication result
	 * @return verification result
	 *
	 */
	public static def verifyMatMult(x:Long, y:Long, A:Matrix, B:Matrix, C:Matrix):Boolean {

		val cv       = C(x,y);
		var v:ElemType = 0.0 as ElemType;
		for (var k:Long=0; k<A.N; k++) v += A(x, k) * B(k, y);
		Console.OUT.printf("Check (%5d, %5d) Computed:%.8f Verified:%.8f\n", 
						   x, y, cv, v);

		val pass = MathTool.equals(v, cv);
		if (!pass) 
			Console.OUT.println("Not equal! Verification (A * B)failed");
		Console.OUT.flush();
		return pass;
	}
	
	/**
	 * Verify matrix multiplication for a given position in the result matrix,
	 * when multiplication requires transpose of the first matrix.
	 * 
	 */
	 public static def verifyTransMultMatrix(x:Long, y:Long, A:Matrix, B:Matrix, C:Matrix):Boolean {
		val cv   = C(x,y);
		var v:ElemType = 0.0 as ElemType;
		
		for (var k:Long=0; k<A.M; k++) v += A(k, x) * B(k, y);
		Console.OUT.printf("Check (%5d, %5d) Computed:%.8f Verified:%.8f\n", 
						   x, y, cv, v);
		val pass = MathTool.equals(v, cv);
		if (!pass)
			Console.OUT.println("Not equal. Verification (A^T * B) failed");
		Console.OUT.flush();
		return pass;
	}
	
	/**
	 * Verify matrix multiplication for a given position in the result matrix,
	 * when multiplication requires transpose of the second matrix.
	 * 
	 */
	public static def verifyMatrixMultTrans(x:Long, y:Long, A:Matrix, B:Matrix, C:Matrix):Boolean {
		val cv   = C(x,y);
		var v:ElemType = 0.0 as ElemType;

		for (var k:Long=0; k<A.N; k++) v += A(x, k) * B(y, k);
		Console.OUT.printf("Check (%5d, %5d) Computed:%.8f Verified:%.8f\n", 
						   x, y, cv, v);

		val pass = MathTool.equals(v, cv);
		if (!pass) 	
			Console.OUT.println("Not equal. Verification (A * B^T) failed");
		Console.OUT.flush();
		return pass;
	}


	// Test same tools

	/**
	 * Test two matrices equal or not.
	 * 
	 * @param m1 -- first operand
	 * @param m2 -- second operand
	 * @return if elements in two matrices are all same
	 */
	public static def testSame(m1:Matrix, m2:Matrix(m1.M, m1.N)):Boolean {
		for (var c:Long=0; c< m1.N; c++)
			for (var r:Long=0; r< m1.M; r++) {
				val v1=	m1(r,c);
				val v2= m2(r,c);
				if (MathTool.equals(v1, v2) == false) {
					Console.OUT.println("Difference found at " + 
										"(" + r + "," + c + "): " +
										v1 + " <> " + v2);
					Console.OUT.flush();
					return false;
				}
			}
		return true;
	}

	/**
	 * Compare two matrices at a number of random positions
	 *
	 * @param m1 -- first operand
	 * @param m2 -- second operand
	 * @param n  -- number of random samplings
	 * @return if all sampling elements in two matrices are same
	 */	
	public static def testSame(A:Matrix, B:Matrix, n:Long):Boolean {
		if (A.M != B.M || A.N != B.N) {
			Console.OUT.printf("Matrix demsion check fail!\n");
			return false;
		}
		val rv = RandTool.getRandGen();
		for (var i:Long=0; i<n; i++) {
			val x = rv.nextLong(A.M);
			val y = rv.nextLong(A.N);
			val av= A(x, y);
			val bv= B(x, y); 
			Console.OUT.printf("Check (%5d, %5d) src:%.8f target:%.8f\n", 
							   x, y, av, bv);
			if (!MathTool.equal(av, bv)) {
				Console.OUT.printf("Discrepancy found! Check fail!");
				Console.OUT.flush();
				return false;
			}
		}
		Console.OUT.println("Random sampling check pass!");
		Console.OUT.flush();
		return true;
	}

    /**
	 * Check if all elements equal to a value
	 *
	 * @param m -- input matrix
	 * @param v -- testing value
	 * @return true if all elements in matrix equal to v.
	 */
	public static def testSame(m:Matrix, v:ElemType):Boolean {
		for (var c:Long=0; c< m.N; c++)
			for (var r:Long=0; r< m.M; r++) {
				val v1=	m(r,c);
				if (MathTool.equal(v1, v) == false) {
					Console.OUT.println("Difference found at " + 
										"(" + r + "," + c + "): " +
										v1 + " <> " + v);
					Console.OUT.flush();
					return false;
				}
			}
		return true;
	}

	/**
	 * Test for (near) equality of two vectors.
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return true if each element in v1 is equal to the corresponding element in v2, within tolerance
	 */
	public static def testSame(v1:Vector, v2:Vector(v1.M)):Boolean {
		for (m in 0..(v1.M-1))
			if (MathTool.equals(v1(m), v2(m)) == false) {
                Console.OUT.println("element " + m + " " + v1(m) + " != " + v2(m));
				Console.OUT.flush();
				return false;
			}
		return true;
	}
}
