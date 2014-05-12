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

package x10.matrix.sparse;

import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

/**
 * Implementation of Dense multiplies with CSC. Result is put in a dense matrix.
 * If the input sparse needs to be transposed, use SparseCSR.TtoCSC() or 
 * SparseCSC.TtoCSR() to the sparse object first, there is no additional 
 * memory space is allocated.
 */
public class DenseMultSparseToDense {
	/**
	 * Return sparse matrix multiplication of m1 &#42 m2 in a new dense object
	 */
	public static def comp(m1:DenseMatrix,  m2:SparseCSC{self.M==m1.N}):DenseMatrix(m1.M,m2.N) =
		comp(m1, m2, new DenseMatrix(m1.M, m2.N), false);
	
	/**
	 * Perform dense times sparse and put result in the dense format
	 * if plus is true, m3 += m1 &#42 m2, else m3 = m1 &#42 m2
	 */
	public static def comp(m1:DenseMatrix, m2:SparseCSC{self.M==m1.N},
		m3:DenseMatrix{self.M==m1.M,self.N==m2.N}, plus:Boolean):DenseMatrix(m3) {
		//if (m1.isTransposed()) { 
		//	compTransA(m1, m2, m3, plus); 
		//	return;
		//}
		//if (m2.isTransposed()) { 
		//	mult(m1, m2.TtoCSR(), m3, plus);
		//	return;
		//}
		//Debug.flushln("Using X10 driver: Dense * CSC -> Dense");
		Debug.assure(m3.M>=m1.M&&m1.N == m2.M&&m2.N<=m3.N);
		//
		var startcol:Long = 0;
		var v1idx:Long = 0;
		for (var c:Long=0; c<m2.N; c++, startcol +=m3.M) {			
			if (! plus) {
				for (var i:Long=startcol; i<startcol+m3.M; i++) m3.d(i) = 0.0;
			}
			val m2col = m2.getCol(c);
			for (var kidx:Long=0; kidx<m2col.size(); kidx++) {
				val k     = m2col.getIndex(kidx);
				val v2    = m2col.getValue(kidx);//m2(k, c);
				v1idx = k*m1.M;
				for (var r:Long=0; r<m1.M; r++, v1idx++) {
					//val v1 = m1.apply(r, k); 
					val v1 = m1.d(v1idx); // m1(r, k)
					m3.d(startcol+r) += v1 * v2;
				}
			}
		}
		return m3;
	}

	/**
	 * Return m1<sup>T</sup> &#42 m2 in a new dense object
	 */
	public static def compTransMult(m1:DenseMatrix, m2:SparseCSC{self.M==m1.M}):DenseMatrix(m1.N,m2.N) =
		compTransMult(m1, m2,  new DenseMatrix(m1.N, m2.N), false);

	// Iterate m1 row-wise for transposed matrix
	// The stride is larger than 1, which could cause cache misses
	/**
	 * Perform dense times sparse and put result in the dense format
	 * if plus is true, m3 += m1<sup>T</sup> &#42 m2, else m3 = m1<sup>T</sup> &#42 m2
	 */
	public static def compTransMult(m1:DenseMatrix, m2:SparseCSC{self.M==m1.M}, 
		m3:DenseMatrix{self.M==m1.N,self.N==m2.N}, plus:Boolean):DenseMatrix(m3) {
		//Debug.flushln("Using X10 driver: Dense * CSC -> Dense");
		Debug.assure(m3.M>=m1.N&&m1.M == m2.M&&m2.N<=m3.N);
		//
		var startcol:Long = 0;
		var v1idx:Long = 0;
		for (var c:Long=0; c<m2.N; c++, startcol +=m3.M) {			
			if (! plus) {
				for (var i:Long=startcol; i<startcol+m3.M; i++) m3.d(i) = 0.0;
			}
			val m2col = m2.getCol(c);
			for (var kidx:Long=0; kidx<m2col.size(); kidx++) {
				val k     = m2col.getIndex(kidx);
				val v2    = m2col.getValue(kidx);//m2(k, c);
				v1idx = k;
				for (var r:Long=0; r<m1.N; r++, v1idx+=m1.M) {
					//val v1 = m1.apply(r, k); 
					val v1 = m1.d(v1idx); // m1(k, r)
					m3.d(startcol+r) += v1 * v2;
				}
			}
		}
		return m3;
	}
									
	// slow: iterate on k based on definition
	/**
	 * Perform dense times sparse and put result in the dense format.
	 * Slow version, iterate k row-wise of m1 and column-wise of m2.
	 * if plus is true, m3 += m1<sup>T</sup> &#42 m2, else m3 = m1<sup>T</sup> &#42 m2
	 */
	public static def compTransMult_ByDef(m1:DenseMatrix,  m2:SparseCSC{self.M==m1.M}, 
		m3:DenseMatrix{self.M==m1.N,self.N==m2.N}, plus:Boolean):DenseMatrix(m3) {
		
		//Debug.flushln("Using X10 driver: Dense.T * CSC -> Dense");
		//Debug.assure(m3.M>=m1.N&&m1.M == m2.M&&m2.N<=m3.N);
		//
		var m1stcol:Long;
		for (var c:Long=0; c<m2.N; c++) {
			m1stcol = 0;
			val m2col = m2.getCol(c);
			for (var r:Long=0; r<m1.N; r++, m1stcol+=m1.M) {
				var v3:Double = 0.0;
				for (var kidx:Long=0; kidx<m2col.size(); kidx++) {
					val k = m2col.getIndex(kidx);
					val v2= m2col.getValue(kidx); // m2(k, c);
					val v1= m1.d(m1stcol+k);      //m1(k, r);
					v3 += v1 * v2;
				} 
				if (plus )
					m3(r,c) += v3; //.set(m3.apply(r, c)+v3, r, c);
				else
					m3(r,c) = v3; // m3.set(v3, r, c);
			}
		}
		return m3;
	}

	// Transpose A to new Densematrix, and them perform multiplication
	// Require new memory space
	public static def compTransMult_newT(m1:DenseMatrix, m2:SparseCSC{self.M==m1.M}, 
		m3:DenseMatrix{self.M==m1.N,self.N==m2.N}, plus:Boolean) : DenseMatrix(m3) {
		val tm1 = DenseMatrix.make(m1.N,m1.M);
		m1.T(tm1);
		return comp(tm1, m2, m3, plus);
	}
	/**
	 * Perform dense times sparse and put result in the dense format
	 * using addition memory space to store transposed m1 data.
	 * if plus is true, m3 += m1<sup>T</sup> &#42 m2, else m3 = m1<sup>T</sup> &#42 m2
	 */
	public static def compTransMult(m1:DenseMatrix,m2:SparseCSC{self.M==m1.M}, 
		m3:DenseMatrix(m1.N,m2.N), m1T:DenseMatrix(m1.N,m1.M), plus:Boolean) : DenseMatrix(m3) {
		m1.T(m1T);
		return comp(m1T, m2, m3, plus);
	}
	
	/**
	 * Compute matrix multiplication m1 &#42 m2<sup>T</sup> in dense format
	 */
	public static def compMultTrans(m1:DenseMatrix, m2:SparseCSC{self.N==m1.N}):DenseMatrix(m1.M, m2.M) =
		comp(m1, m2.TtoCSR(), new DenseMatrix(m1.M, m2.M), false);

	/**
	 * Perform matrix multiplication m3 += m1 &#42 m2<sup>T</sup> if plus is true, 
	 * else m3 = m1 &#42 m2<sup>T</sup>
	 */
	public static def compMultTrans(m1:DenseMatrix, m2:SparseCSC{self.N==m1.N}, 
		m3:DenseMatrix{self.M==m1.M,self.N==m2.M}, plus:Boolean) : DenseMatrix(m3) =
		comp(m1, m2.TtoCSR(), m3, plus);


	// Dense format multiply with CSR


	/**
	 * Return m1 &#42 m2 in a new dense object
	 */
	public static def comp(m1:DenseMatrix,  m2:SparseCSR{self.M==m1.N}):DenseMatrix(m1.M,m2.N) =
		comp(m1, m2, new DenseMatrix(m1.M, m2.N), false);

	
	/**
	 * Perform dense times sparse and put result in the dense format
	 * if plus is true, m3 += m1 &#42 m2, else m3 = m1 &#42 m2
	 */
	public static def comp(m1:DenseMatrix, m2:SparseCSR{self.M==m1.N}, 
			m3:DenseMatrix{self.M==m1.M,self.N==m2.N}, plus:Boolean):DenseMatrix(m3) {


		// Design note:
		// Iterate m2 columnwise, not good for very sparse CSR


		// Similar to TransposeA case
		//Debug.flushln("Using X10 driver: Dense.T * CSR -> Dense");
		Debug.assure(m3.M>=m1.M&&m1.N == m2.M&&m2.N<=m3.N);
		//Debug.assure(false, "Need verifcation for Dense*CSR->Dense driver");

		val tmprow = m2.getTempRow(); //alloc tmp space if not allocated before
		var startcol:Long=0;
		var v1idx:Long =0;
		for (var r:Long=0; r<m1.M; r++, startcol+=m1.M) { // Outer-most iterates on row 
			// Reset the tmp memory space
			for (var i:Long=0; i<m2.N; i++) tmprow(i) = 0.0D;
			v1idx = r;
			for (var k:Long=0; k<m2.M; k++, v1idx+=m1.M) {
				val v1 = m1.d(v1idx); // m1(r, k);
				if (MathTool.isZero(v1)) continue;
				//
				val m2row = m2.getRow(k);
				for (var cidx:Long=0; cidx<m2row.size(); cidx++) {
					val c = m2row.getIndex(cidx);
					val v2= m2row.getValue(cidx); //m2(k, c)
					tmprow(c) += v1 * v2;
				}
			}
			//Copy the tmp back to dst
			var dstidx:Long=r;
			if (plus)
				for (var i:Long=0; i<m2.N; i++, dstidx+=m3.M) m3.d(dstidx)+=tmprow(i);
			else
				for (var i:Long=0; i<m2.N; i++, dstidx+=m3.M) m3.d(dstidx) =tmprow(i);
		}
		return m3;
	}


	/**
	 * Compute m1<sup>T</sup> &#42 m2 in a new dense object
	 */
	public static def compTransMult(m1:DenseMatrix, m2:SparseCSR{self.M==m1.M}) : DenseMatrix(m1.N,m2.N) =
		compTransMult(m1, m2, new DenseMatrix(m1.N, m2.N), false);

	// Using m2 tmprow to store results, before copying to dst
	/**
	 * Perform dense times sparse and put result in the dense format
	 * if plus is true, m3 += m1<sup>T</sup> &#42 m2, else m3 = m1<sup>T</sup> &#42 m2
	 */
	public static def compTransMult(m1:DenseMatrix, m2:SparseCSR{self.M==m1.M}, 
		m3:DenseMatrix{self.M==m1.N,self.N==m2.N}, plus:Boolean):DenseMatrix(m3) {
		//Debug.flushln("Using X10 driver: Dense.T * CSR -> Dense");
		//Debug.assure(m3.M>=m1.N&&m1.M == m2.M&&m2.N<=m3.N);
		//
		val tmprow = m2.getTempRow();
		var startcol:Long=0;
		for (var r:Long=0; r<m1.N; r++, startcol+=m1.M) { // Outer-most iterates on row 
			// Reset the tmp memory space
			for (var i:Long=0; i<m2.N; i++) tmprow(i) = 0.0D;

			for (var k:Long=0; k<m2.M; k++) {
				val v1 = m1.d(startcol+k); // m1(k, r);
				if (MathTool.isZero(v1)) continue;
				//
				val m2row = m2.getRow(k);
				for (var cidx:Long=0; cidx<m2row.size(); cidx++) {
					val c = m2row.getIndex(cidx);
					val v2= m2row.getValue(cidx); //m2(k, r)
					tmprow(c) += v1 * v2;
				}
			}
			//Copy the tmp back to dst
			var dstidx:Long=r;
			if (plus)
				for (var i:Long=0; i<m2.N; i++, dstidx+=m3.M) m3.d(dstidx)+=tmprow(i);
			else
				for (var i:Long=0; i<m2.N; i++, dstidx+=m3.M) m3.d(dstidx) =tmprow(i);
		}
		return m3;
	}
	/**
	 * Return matrix multiplication m1 &#42 m2<sup>T</sup> in dense format
	 */
	public static def compMultTrans(m1:DenseMatrix,m2:SparseCSR{self.N==m1.N}):DenseMatrix(m1.M, m2.M) =
		comp(m1, m2.TtoCSC(), new DenseMatrix(m1.M, m2.M), false);

	/**
	 * Perform matrix multiplication m3 += m1 &#42 m2<sup>T</sup> if plus is true, 
	 * else m3 = m1 &#42 m2<sup>T</sup>
	 */
	public static def compMultTrans(m1:DenseMatrix, m2:SparseCSR{self.N==m1.N}, 
		m3:DenseMatrix{self.M==m1.M,self.N==m2.M}, plus:Boolean):DenseMatrix(m3) =
			comp(m1, m2.TtoCSC(), m3, plus);

}
