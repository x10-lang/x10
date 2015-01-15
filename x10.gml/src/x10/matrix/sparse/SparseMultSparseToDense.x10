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

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

/**
 * Implementation of sparse matrices mutliply sparse matrices.
 * Results are stored in a dense matrices
 */
public class SparseMultSparseToDense {
    
    
    // CSC format multiply with CSC 
    
    // This is the fastest approach, no conversion. All iterations are based on indexes
    // of non-zero elements
    
    /**
     * Return sparse multiplication result of m1 &#42 m2 in dense format
     */
    public static def comp(m1:SparseCSC, m2:SparseCSC{self.M==m1.N}):DenseMatrix(m1.M,m2.N) =
								     comp(m1, m2, new DenseMatrix(m1.M, m2.N), false);
    
    /**
     * If plus true, perform m3 += m1 &#42 m2 else m3 = m2 &#42 m3 
     */
    public static def comp(m1:SparseCSC, m2:SparseCSC{self.M==m1.N}, 
			   m3:DenseMatrix{self.M==m1.M,self.N==m2.N}, 
			   plus:Boolean):DenseMatrix(m3) {
	//if (m1.isTransposed()) {
	//	SparseMultToDense.comp(m1.TtoCSR(), m2, m3, plus); 
	//	return;
	//}
	//if (m2.isTransposed()) {
	//	SparseMultToDense.comp(m1, m2.TtoCSR(), m3, plus); 
	//	return;
	//}
	//
	assert (m3.M>=m1.M&&m1.N == m2.M&&m2.N<=m3.N);
	//
	val dst = m3.d;//new Compress2D(m2.N);
	var col_st:Long = 0;
	for (var c:Long=0; c<m2.N; c++, col_st+=m3.M) {
	    if (!plus) { //reset the column if no plus is required
		for (var i:Long=col_st; i<col_st+m3.M; i++) dst(i) = 0.0 as ElemType;
	    }
	    // Compute the the c-column in result, using all of m1 and c-col of m2
	    val m2col = m2.getCol(c);
	    for (var kidx:Long=0; kidx<m2col.size(); kidx++) {
		val k     = m2col.getIndex(kidx);
		val v2    = m2col.getValue(kidx);//m2(k, c);
		// 
		val m1col = m1.getCol(k);
		for (var ridx:Long=0; ridx<m1col.size(); ridx++) {
		    val r = m1col.getIndex(ridx);
		    val v1= m1col.getValue(ridx); //m1(r, k);
		    dst(col_st+r) += v1 * v2;
		}
	    }
	}
	return m3;//new SparseCSC(m1.M, m2.N, ccdata);
    }
    
    
    // Transpose mult and mult transpose
    
    /**
     * If plus true, perform m3 += m1<sup>T</sup> &#42 m2 else m3 = m2<sup>T</sup> &#42 m3 
     */
    public static def compTransMult(m1:SparseCSC, 
				    m2:SparseCSC{self.M==m1.M},m3:DenseMatrix{self.M==m1.N,self.N==m2.N}, plus:Boolean):DenseMatrix(m3) =
															comp(m1.TtoCSR(), m2, m3, plus);

    public static def compTransMult(m1:SparseCSC, m2:SparseCSC{self.M==m1.M}, 
				    m3:DenseMatrix{self.M==m1.N,self.N==m2.N}):DenseMatrix(m3) =
									       comp(m1.TtoCSR(), m2, m3, false);	
    
    /**
     * Return m1<sup>T</sup> &#42 m2 
     */
    public static def compTransMult(m1:SparseCSC,m2:SparseCSC{self.M==m1.M}):DenseMatrix(m1.N,m2.N) = 
									     comp(m1.TtoCSR(), m2);
    
    /**
     * If plus true, perform m3 += m1 &#42 m2<sup>T</sup> else m3 = m2 &#42 m3<sup>T</sup>
     */
    public static def compMultTrans(m1:SparseCSC, m2:SparseCSC{self.N==m1.N}, 
				    m3:DenseMatrix{self.M==m1.M,self.N==m2.M}, 
				    plus:Boolean) : DenseMatrix(m3) =
						    comp(m1, m2.TtoCSR(), m3, plus);
    
    /**
     * Return m1 &#42 m2<sup>T</sup> 
     */
    public static def compMultTrans(m1:SparseCSC, m2:SparseCSC{self.N==m1.N}):DenseMatrix(m1.M,m2.M) 
									      = comp(m1, m2.TtoCSR());
    
    
    // CSC format multiply with CSR 
    
    // Slow. m2 needs conversion from compressed-row to compress-column
    
    /**
     * Return sparse multiplication result of m1 &#42 m2 in dense format
     */
    public static def comp(m1:SparseCSC, m2:SparseCSR{self.M==m1.N}):DenseMatrix(m1.M,m2.N) =
								     comp(m1, m2, new DenseMatrix(m1.M, m2.N), false);

/**
	 * If plus true, perform m3 += m1 &#42 m2 else m3 = m2 &#42 m3 
	 * Performance note: m2 is expanded to dense format column by column,
	 * using additional memory space is used in m2.
	 * The multiplication process is same as dense &#42 CSC. 
	 */	
	public static def comp(m1:SparseCSC, m2:SparseCSR{self.M==m1.N}, m3:DenseMatrix{self.M==m1.M, self.N==m2.N}, 
		plus:Boolean):DenseMatrix(m3) {
		//
		//if (m1.isTransposed()) {
		//	SparseMultToDense.comp(m1.TtoCSR(), m2, m3, plus);
		//	return;
		//}
		//if (m2.isTransposed()) {
		//	SparseMultToDense.comp(m1, m2.TtoCSC(), m3, plus);
		//	return;
		//}
		//
		assert (m3.M>=m1.M&&m1.N == m2.M&&m2.N<=m3.N);
		//
		//val m3     = new SparseCSC(m1.M, m2.N);
		val dst = m3.d;//new Compress2D(m2.N);
		var col_st:Long = 0;
		for (var c:Long=0; c<m2.N; c++, col_st+=m3.M) {
			if (!plus) {
				for (var i:Long=col_st; i<col_st+m3.M; i++) dst(i) = 0.0 as ElemType;
			}
			//
			val m2col= m2.getCol(c); //Expensive conversion
			for (var kidx:Long=0; kidx<m2col.size(); kidx++) {
				val k     = m2col.getIndex(kidx);
				val v2    = m2col.getValue(kidx); //m2(k, c);
				val m1col = m1.getCol(k);
				for (var ridx:Long=0; ridx<m1col.size(); ridx++) {
					val r = m1col.getIndex(ridx);
					val v1= m1col.getValue(ridx); //m1(r, k);
					dst(col_st+r) += v1 * v2;
				}
			}
		}
		return m3;
	}

	/**
	 * If plus true, perform m3 += m1<sup>T</sup> &#42 m2 else m3 = m2<sup>T</sup> &#42 m3 
	 */
	public static def compTransMult(m1:SparseCSC, m2:SparseCSR{self.M==m1.M}, 
			m3:DenseMatrix{self.M==m1.N,self.N==m2.N}, plus:Boolean) =
		comp(m1.TtoCSR(), m2, m3, plus);

	/**
	 * Return m1<sup>T</sup> &#42 m2 
	 */
	public static def compTransMult(m1:SparseCSC, m2:SparseCSR{m2.M==m1.M}):DenseMatrix(m1.N,m2.N) 
		= comp(m1.TtoCSR(), m2);

	/**
	 * If plus true, perform m3 += m1 &#42 m2<sup>T</sup> else m3 = m2 &#42 m3<sup>T</sup> 
	 */
	public static def compMultTrans(m1:SparseCSC, m2:SparseCSR{self.N==m1.N}, 
			m3:DenseMatrix{self.M==m1.M, self.N==m2.M}, plus:Boolean) : DenseMatrix(m3) =
		comp(m1, m2.TtoCSC(), m3, plus);

	/**
	 * Return m1 &#42 m2<sup>T</sup>
	 */
	public static def compMultTrans(m1:SparseCSC, m2:SparseCSR{self.N==m1.N}):DenseMatrix(m1.M,m2.M) 
		= comp(m1, m2.TtoCSC());


	// CSR format multiply with CSC 

	// Put result in CSC form
	// Fast, no conversion
	/**
	 * Return multiplication result of m1 &#42 m2 in dense format
	 */
	public static def comp(m1:SparseCSR, m2:SparseCSC{self.M==m1.N}):DenseMatrix(m1.M, m2.N) =
		comp(m1, m2, new DenseMatrix(m1.M, m2.N), false);

	/**
	 * If plus true, perform m3 += m1 &#42 m2 else m3 = m1 &#42 m2 
	 * Performance note: m1 is expanded to column one at time.
	 * Additional temporary memory space is used, which has the size of 
	 * m1's column. This temp space is used when computing the same row of m3.
	 * This process should run faster than the third one (?)
	 */
	public static def comp(m1:SparseCSR, m2:SparseCSC{self.M==m1.N}, m3:DenseMatrix{self.M==m1.M, self.N==m2.N}, 
			plus:Boolean):DenseMatrix(m3) {
		assert (m3.M>=m1.M&&m1.N == m2.M&&m2.N<=m3.N);

		for (var r:Long=0; r<m1.M; r++) {
			val m1rowEpd = m1.extractRow(r); // Additional mem is used;

			for (var c:Long=0; c<m2.N; c++) {
				val m2col = m2.getCol(c);
				var v3:ElemType = 0.0 as ElemType;
				if (plus) v3 = m3(r, c); 
				for (var kidx:Long=0; kidx<m2col.size(); kidx++) {
					val k = m2col.getIndex(kidx);
					val v2= m2col.getValue(kidx); //m2(k, c)
					val v1= m1rowEpd(k);  //m1(r, k)
					v3 += v1 * v2;
				}
				m3(r, c) = v3;
			}
		}
		return m3;
	}

	//Slow, CSR->CSC conversion for m1
	/**
	 * If plus true, perform m3 += m1 &#42 m2 else m3 = m2 &#42 m3 
	 * Performance note: the CSR is transposed to CSC. 
	 * This method is memory expensive, additional memory is used 
	 * which is same as m1. However, after CSR is converted, the process
	 * runs faster than others
	 */
	public static def comp1(m1:SparseCSR, m2:SparseCSC{self.M==m1.N}, 
			m3:DenseMatrix{self.M==m1.M, self.N==m2.N},plus:Boolean):DenseMatrix(m3) {
		//
		//if (m1.isTransposed()) {SparseMultToDense.comp(m1.TtoCSC(), m2, m3, plus);return;}
		//if (m2.isTransposed()) {SparseMultToDense.comp(m1, m2.TtoCSR(), m3, plus);return;}
		//
		assert (m3.M>=m1.M&&m1.N == m2.M&&m2.N<=m3.N);
		//
		// Convert m1 to CSC first
		return comp(m1.toCSC(), m2, m3, plus);
	}

	/**
	 * Return CSR &#42 CSC in dense format.
	 * Performance note: No addition memoy is allocated. The process write each cell
	 * in m3 one at time, while accessing m1 row-wise and m2 column-wise.
	 * This process is slow, but no addition memory space is used.
	 */
	public static def comp2(m1:SparseCSR,m2:SparseCSC{self.M==m1.N},
		m3:DenseMatrix{self.M==m1.M, self.N==m2.N}, 
		plus:Boolean ): DenseMatrix(m3) {
		assert (m3.M>=m1.M&&m1.N == m2.M&&m2.N<=m3.N);

		var m1strow:Long = 0;
		var m2stcol:Long = 0;
		for (var c:Long=0; c<m2.N; c++) {
			val m2col = m2.getCol(c);
			for (var r:Long=0; r<m1.M; r++) {
				val m1row = m1.getRow(r);
				var m1kidx:Long = 0;
				var m2kidx:Long = 0;
				var v3:ElemType = 0;
				if (m1kidx < m1row.length && m2kidx < m2col.length) {
					var m1k:Long = m1row.getIndex(0L);
					var m2k:Long = m2col.getIndex(0L);
					while (true) {
						//
						if (m1k == m2k) {
							v3 += m1row.getValue(m1kidx) * m2col.getValue(m2kidx);
							m1kidx++; m2kidx++;
							if (m1kidx>=m1row.length || m2kidx>=m2col.length) break;
							m1k = m1row.getIndex(m1kidx);
							m2k = m2col.getIndex(m2kidx);
						} else if (m1k < m2k) {
							m1kidx++;
							if (m1kidx>=m1row.length) break;
							m1k = m1row.getIndex(m1kidx);
						} else {
							m2kidx++;
							if ( m2kidx>=m2col.length ) break;
							m2k = m2col.getIndex(m2kidx);
						}
					}
				}
				if (plus) m3(r,c) += v3; else m3(r,c)  = v3;
			}
		}
		return m3;
	}




	/**
	 * If plus true, perform m3 += m1<sup>T</sup> &#42 m2 else m3 = m2<sup>T</sup> &#42 m3 
	 */
	public static def compTransMult(m1:SparseCSR, m2:SparseCSC{self.M==m1.M}, 
			m3:DenseMatrix{self.M==m1.N,self.N==m2.N}, plus:Boolean) : DenseMatrix(m3) =
		comp(m1.TtoCSC(), m2, m3, plus);

	/**
	 * Return m1<sup>T</sup> &#42 m2 
	 */
	public static def compTransMult(m1:SparseCSR, m2:SparseCSC{self.M==m1.M}):DenseMatrix(m1.N,m2.N) 
		= comp(m1.TtoCSC(), m2);

	/**
	 * If plus true, perform m3 += m1 &#42 m2<sup>T</sup> else m3 = m2 &#42 m3<sup>T</sup> 
	 */
	public static def compMultTrans(m1:SparseCSR, m2:SparseCSC{self.N==m1.N},
		m3:DenseMatrix{self.M==m1.M,self.N==m2.M}, plus:Boolean) : DenseMatrix(m3) =
			comp(m1, m2.TtoCSR(), m3, plus);
	

	/**
	 * Return m1 &#42 m2<sup>T</sup> 
	 */
	public static def compMultTrans(m1:SparseCSR, m2:SparseCSC{self.N==m1.N}):DenseMatrix(m1.M,m2.M) 
		= comp(m1, m2.TtoCSR());


	// CSR format multiply with CSR

	// Compute to CSC
	// Slow, update dst dense matrix in row-wise
	/**
	 * Return multiplication result of m1 &#42 m2 in dense format
	 */
	public static def comp(m1:SparseCSR,  m2:SparseCSR{self.M==m1.N}) : DenseMatrix(m1.M, m2.N) =
		comp(m1, m2,  new DenseMatrix(m1.M, m2.N), false);

	//---- Performance improvement
	// Using SparseCSR tmprow to store data before write back to destination

	/**
	 * If plus true, perform m3 += m1 &#42 m2 else m3 = m2 &#42 m3 
	 */
	public static def comp(m1:SparseCSR, m2:SparseCSR{self.M==m1.N}, m3:DenseMatrix{self.M==m1.M,self.N==m2.N}, 
		plus:Boolean):DenseMatrix(m3) {
		//if (m1.isTransposed()) {SparseMultToDense.comp(m1.TtoCSC(), m2, m3, plus);return;}
		//if (m2.isTransposed()) {SparseMultToDense.comp(m1, m2.TtoCSC(), m3, plus);return;}

		assert (m3.M>=m1.M&&m1.N == m2.M&&m2.N<=m3.N);
		val dst = m3.d;//new Compress2D(m2.N);
		for (var r:Long=0; r<m1.M; r++) {
			if (!plus) { //reset the column if no plus is required
				for (var i:Long=r; i<m3.N*m3.M; i+=m3.M) dst(i) = 0.0 as ElemType;
			}
			// Compute the the c-column in result, using all of m1 and c-col of m2
			val m1row = m1.getRow(r);
			for (var kidx:Long=0; kidx<m1row.size(); kidx++) {
				val k     = m1row.getIndex(kidx);
				val v1    = m1row.getValue(kidx);//m1(r, k);
				// 
				val m2row = m2.getRow(k);
				for (var cidx:Long=0; cidx<m2row.size(); cidx++) {
					val c = m2row.getIndex(cidx);
					val v2= m2row.getValue(cidx); //m2(k, c);
					dst(r+c*m3.M) += v1 * v2;
				}
			}
		}
		return m3;
	}

	/**
	 * If plus true, perform m3 += m1<sup>T</sup> &#42 m2 else m3 = m2<sup>T</sup> &#42 m3 
	 */
	public static def compTransMult(m1:SparseCSR, m2:SparseCSR{self.M==m1.M}, 
		m3:DenseMatrix{self.M==m1.N,self.N==m2.N}, plus:Boolean) : DenseMatrix(m3) =
		comp(m1.TtoCSC(), m2, m3, plus);

	/**
	 * Return m1<sup>T</sup> &#42 m2 
	 */
	public static def compTransMult(m1:SparseCSR, m2:SparseCSR{self.M==m1.M}):DenseMatrix(m1.N,m2.N) 
		=  comp(m1.TtoCSC(), m2);

	/**
	 * If plus true, perform m3 += m1 &#42 m2<sup>T</sup> else m3 = m2 &#42 m3<sup>T</sup> 
	 */
	public static def compMultTrans(m1:SparseCSR, m2:SparseCSR{self.N==m1.N}, 
		m3:DenseMatrix{self.M==m1.M,self.N==m2.M}, plus:Boolean) : DenseMatrix(m3) =
		comp(m1, m2.TtoCSC(), m3, plus);

	/**
	 * Return m1 &#42 m2<sup>T</sup> 
	 */
	public static def compMultTrans(m1:SparseCSR, m2:SparseCSR{self.N==m1.N}):DenseMatrix(m1.M,m2.M) 
		= comp(m1, m2.TtoCSC());
	
}
