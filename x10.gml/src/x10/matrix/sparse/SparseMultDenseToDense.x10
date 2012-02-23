/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.matrix.sparse;

//
import x10.matrix.MathTool;
import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

/**
 * Implementation of sparse multiplies dense operations.
 * Results are stored in dense matrices. 
 * If sparse matrix needs to be transposed, use TtoCSC() or TtoCSR() on
 * the sparse matrix first.
 */
public class SparseMultDenseToDense {
 
    //------------------------------------------------------------------------
	// CSC format multiply with Dense
    //------------------------------------------------------------------------

	/**
	 * Return matrix multiplication m1 &#42 m2 in dense format
	 */
	public static def comp(m1:SparseCSC, m2:DenseMatrix{self.M==m1.N}):DenseMatrix(m1.M,m2.N) =
		comp(m1, m2, new DenseMatrix(m1.M, m2.N), false);
	
	/**
	 * Perform matrix multiplication m3 += m1 &#42 m2 if plus is true, 
	 * else m3 = m1 &#42 m2
	 */
	public static def comp(m1:SparseCSC, m2:DenseMatrix{self.M==m1.N}, 
		m3:DenseMatrix{self.M==m1.M,self.N==m2.N}, plus:Boolean):DenseMatrix(m3) {
		Debug.assure(m3.M>=m1.M&&m1.N == m2.M&&m2.N<=m3.N);
		//
		var startcol:Int = 0;
		var v1idx:Int = 0;
		var v2idx:Int = 0;
		for (var c:Int=0; c<m2.N; c++, startcol +=m3.M) {			
			if (! plus) {
				for (var i:Int=startcol; i<startcol+m3.M; i++) m3.d(i) = 0.0;
			}
			v1idx = 0;
			for (var k:Int=0; k<m1.N; k++) {
				val v2 = m2.d(v2idx++); //m2(k, c);
				//
				if (MathTool.isZero(v2)) {
					v1idx += m1.M;
				} else {
					val m1col = m1.getCol(k);
					for (var ridx:Int=0; ridx<m1col.size(); ridx++) {
						val r = m1col.getIndex(ridx);
						val v1 = m1col.getValue(ridx); // m1(r, k)
						m3.d(startcol+r) += v1 * v2;
					}
				}
			}
		}
		return m3;
	}
	//------------------
	/**
	 * Return matrix multiplication m1 &#42 m2<sup>T</sup> in dense format
	 */
	public static def compMultTrans(m1:SparseCSC, m2:DenseMatrix{self.N==m1.N}):DenseMatrix(m1.M, m2.M) =
		compMultTrans(m1, m2, new DenseMatrix(m1.M, m2.M), false);

	//------------------
	/**
	 * Perform matrix multiplication m3 += m1 &#42 m2<sup>T</sup> if plus is true, 
	 * else m3 = m1 &#42 m2<sup>T</sup>
	 */
	public static def compMultTrans(m1:SparseCSC, m2:DenseMatrix{self.N==m1.N}, 
		m3:DenseMatrix{self.M==m1.M, self.N==m2.M}, plus:Boolean): DenseMatrix(m3) {
		//Debug.flushln("Using X10 driver: CSC * Dense.T -> Dense");
		Debug.assure(m3.M>=m1.M&&m1.N == m2.N&&m2.M<=m3.N);
		//
		var startcol:Int = 0;
		var v1idx:Int = 0;
		var v2idx:Int = 0;
		for (var c:Int=0; c<m2.M; c++, startcol +=m3.M) {			
			if (! plus) {
				for (var i:Int=startcol; i<startcol+m3.M; i++) m3.d(i) = 0.0;
			}
			v2idx = c;
			for (var k:Int=0; k<m1.N; k++, v2idx+=m2.M) {
				//val v2 = m2.apply(c, k);
				val v2 = m2.d(v2idx); //m2(c, k);
				//val v2 = m2.d(c+k*m2.M); //m2(k, c);
				//
				if (MathTool.isZero(v2)) continue;
				val m1col = m1.getCol(k);
				for (var ridx:Int=0; ridx<m1col.size(); ridx++) {
					val r  = m1col.getIndex(ridx);
					val v1 = m1col.getValue(ridx); // m1(r, k)
					//if ( MathTool.isZero(v1)) continue;
					m3.d(startcol+r) += v1 * v2;
				}
			}
		}
		return m3;
	}
	/**
	 * Return matrix multiplication m1<sup>T</sup> &#42 m2 in dense format
	 */
	public static def compTransMult(m1:SparseCSC, m2:DenseMatrix{self.M==m1.M}):DenseMatrix(m1.N, m2.N) =
		comp(m1.TtoCSR(), m2, new DenseMatrix(m1.N, m2.N), false);

	/**
	 * Perform matrix multiplication m3 += m1<sup>T</sup> &#42 m2 if plus is true, 
	 * else m3 = m1<sup>T</sup> &#42 m2
	 */
	public static def compTransMult(m1:SparseCSC, m2:DenseMatrix{self.M==m1.M}, 
		m3:DenseMatrix{self.M==m1.N,self.N==m2.N}, plus:Boolean): DenseMatrix(m3) =
		comp(m1.TtoCSR(), m2, m3, plus);
	
    //------------------------------------------------------------------------
	// CSR format multiply with Dense
    //------------------------------------------------------------------------
	/**
	 * Return matrix multiplication m1 &#42 m2 in dense format
	 */
	public static def comp(m1:SparseCSR, m2:DenseMatrix{self.M==m1.N}):DenseMatrix(m1.M, m2.N) =
		comp(m1, m2, new DenseMatrix(m1.M, m2.N), false);
	//
	/**
	 * Perform matrix multiplication m3 += m1 &#42 m2 if plus is true, 
	 * else m3 = m1 &#42 m2
	 */
	public static def comp(m1:SparseCSR, m2:DenseMatrix{self.M==m1.N}, 
		m3:DenseMatrix{self.M==m1.M,self.N==m2.N},plus:Boolean ):DenseMatrix(m3) =
		//if (m1.isTransposed()) {
		//	comp(m1.TtoCSC(), m2, m3, plus); 
		//	return;
		//}
		//if (m2.isTransposed()) {
		//	compTransB(m1, m2, m3, plus);
		//	return;
		//}
		comp_byDef(m1, m2, m3, plus);

	// By definition,
	// iterate on r and c, for all k
	public static def comp_byDef(m1:SparseCSR, m2:DenseMatrix{self.M==m1.N}, 
		m3:DenseMatrix{self.M==m1.M, self.N==m2.N}, plus:Boolean ): DenseMatrix(m3) {
		//Debug.flushln("Using X10 driver: CSR * Dense -> Dense");
		Debug.assure(m3.M>=m1.M&&m1.N == m2.M&&m2.N<=m3.N);
		//
		var m2stcol:Int = 0;
		for (var r:Int=0; r<m1.M; r++) {
			val m1row = m1.getRow(r);
			m2stcol = 0;
			for (var c:Int=0; c<m2.N; c++, m2stcol+=m2.M) {			
				var v3:Double = 0.0;
				for (var kidx:Int=0; kidx<m1row.size(); kidx++) {
					val k = m1row.getIndex(kidx);
					val v1= m1row.getValue(kidx); //m1(r, k);
					val v2= m2.d(m2stcol+k);      //m2(k, c);
					//if (MathTool.isZero(v1)) continue;
					v3 += v1 * v2;
				} 
				if (plus )
				    m3(r,c) += v3;
				else
				    m3(r,c)=v3;
			}
		}
		return m3;
	}
	//-------
	public static def compMultTrans(m1:SparseCSR, m2:DenseMatrix{m2.N==m1.N}):DenseMatrix(m1.M,m2.M) =
		compMultTrans(m1, m2, new DenseMatrix(m1.M, m2.M), false);

	/**
	 * Perform matrix multiplication m3 += m1 %#42 m2<sup>T</sup> if plus is true, 
	 * else m3 = m1 &#42 m2<sup>T</sup>
	 */
	public static def compMultTrans(m1:SparseCSR, m2:DenseMatrix{self.N==m1.N}, 
		m3:DenseMatrix{self.M==m1.M,self.N==m2.M}, plus:Boolean):DenseMatrix(m3) {

		Debug.assure(m3.M>=m1.M&&m1.N == m2.N&&m2.M<=m3.N);

		var v2idx:Int = 0;
		var dstidx:Int=0;
		for (var r:Int=0; r<m1.M; r++) {			
			if (! plus) {
				for (var i:Int=r; i<m3.M*m3.N; i+=m3.M) m3.d(i) = 0.0;
			}
			val m1row = m1.getRow(r);
			for (var kidx:Int=0; kidx<m1row.size(); kidx++) {
				val k  = m1row.getIndex(kidx);
				val v1 = m1row.getValue(kidx);//m1(r, k);
				v2idx  = k*m2.M;
				dstidx = r;
				for (var c:Int=0; c<m2.M; c++, v2idx++, dstidx+=m3.M) {
					//val v1 = m1.apply(r, k); 
					val v2 = m2.d(v2idx); // m2(c, k)
					//if ( MathTool.isZero(v2)) continue;
					// The strike for accessing m3.d is not 1,
					// This could lead to more cache misses.
					// Additional memory allocation could be used 
					// to hold the destination space
					m3.d(dstidx) += v1 * v2;
				}
			}
		}
		return m3;
	}

	/**
	 * Return matrix multiplication m1<sup>T</sup> &#42 m2 in dense format
	 */
	public static def compTransMult(m1:SparseCSR, m2:DenseMatrix{self.M==m1.M}):DenseMatrix(m1.N, m2.N) =
		comp(m1.TtoCSC(), m2, new DenseMatrix(m1.N, m2.N), false);

	/**
	 * Perform matrix multiplication m3 += m1<sup>T</sup> &#42 m2 if plus is true, 
	 * else m3 = m1<sup>T</sup> &#42 m2
	 */
	public static def compTransMult(m1:SparseCSR, m2:DenseMatrix{self.M==m1.M}, 
		m3:DenseMatrix{self.M==m1.N, self.N==m2.N}, plus:Boolean): DenseMatrix(m3) =
		comp(m1.TtoCSC(), m2, m3, plus);

}