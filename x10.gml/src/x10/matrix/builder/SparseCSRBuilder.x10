/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.matrix.builder;

import x10.compiler.Inline;
import x10.util.StringBuilder;
import x10.util.ArrayList;

import x10.matrix.Matrix;
import x10.matrix.util.MathTool;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.Compress2D;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.SparseCSR;

/**
 * Incomplete
 */
public class SparseCSRBuilder {
	
	static struct NonZeroEntry(col:Long, value:Double) {

		def this(c:Long, v:Double) { 
			property(c, v);
		}
		@Inline
		public def equal(that:NonZeroEntry) : Boolean = (this.col==that.col);
		
	
		@Inline
		public def toString():String = "<C:"+col+","+value+">";
		
	}
	
	static val zeroEntry = NonZeroEntry(0, 0.0);
	
	var nzcount:Long=0;
	val nzrow:Rail[ArrayList[NonZeroEntry]];

	public var M:Long;
	public var N:Long;

	public def this(m:Long, n:Long) {
		M = m; N=n; 
		nzrow    = new Rail[ArrayList[NonZeroEntry]](m, (i:Long)=>new ArrayList[NonZeroEntry]());
	}

	/*
	 * Creating sparse csc matrix builder using given existing sparse matrix.
	 */
	public static def make(spamat:SparseCSR): SparseCSRBuilder {
		val bld = new SparseCSRBuilder(spamat.M, spamat.N);
		val ca = spamat.getStorage();
		var offset:Long = 0;
		for (var r:Long=0; r<bld.M; r++) {
			val cnt = spamat.crdata.cLine(r).length;
			for (var i:Long=0; i<cnt; i++, offset++) {
				bld.add(r, ca.index(offset), ca.value(offset));
			}
		}
		return bld;
	}
	
	public static def make(spamat:SparseCSC): SparseCSRBuilder {
		val bld = new SparseCSRBuilder(spamat.M, spamat.N);
		val ca = spamat.getStorage();
		var offset:Long = 0;
		for (var c:Long=0; c<bld.N; c++) {
			val cnt = spamat.ccdata.cLine(c).length;
			for (var i:Long=0; i<cnt; i++, offset++) {
				bld.add(ca.index(offset), c, ca.value(offset));
			}
		}
		return bld;
	}
	
	public static def makeTranspose(spamat:SparseCSR): SparseCSRBuilder {
		val bld = new SparseCSRBuilder(spamat.N, spamat.M);
		val ca = spamat.getStorage();
		var offset:Long = 0;
		for (var r:Long=0; r<spamat.M; r++) {
			val cnt = spamat.crdata.cLine(r).length;
			for (var i:Long=0; i<cnt; i++, offset++) {
				val c = ca.index(offset);
				bld.add(c, r, ca.value(offset));
			}
		}
		return bld;
	}
	

	/*
	 * Add new nonzero entry at the ordered nonzero list of the specified column
	 */
	public def add(r:Long, c:Long, v:Double) {
		val nz = NonZeroEntry(c, v);
		val idx = findIndex(r, c);
		if (idx < 0)
			nzrow(r).add(nz);
		else
			nzrow(r).addBefore(idx, nz);
		nzcount ++;
	}
	
	/*
	 * Append nonzero at the end of nonzero list of the specified column. 
	 */
	public def append(r:Long, c:Long, v:Double) {
		val nz = NonZeroEntry(c, v);
		nzrow(r).add(nz);
		nzcount ++;
	}
	
	/*
	 * Return the value at given row and column; If not found in nonzero list, 0 is returned.
	 */
	public def get(r:Long, c:Long) : Double {
		val foundnz = find(r, c);
		return foundnz.value;
	}
	
	/*
	 * Set the entry of given row and column to value, if it is found. Otherwise append
	 * new nonzero entry at the end of nonzero list;
	 */
	public def set(r:Long, c:Long, v:Double) : Boolean {
		val idx = findIndex(r, c);
		if (idx < 0) {
			add(r, c, v);
			return true;
		} else {
			val nz = NonZeroEntry(c,v);
			nzrow(r).set(nz, idx);
			return false;
		}
	}
	

	/*
	 * Find the index of the entry of given row and column in the nonzero list. If not found, -1 is returned.
	 */
	public def findIndex(r:Long, c:Long) : Long {
		var i:Long = nzrow(r).size()-1;
		for (; i>=0; i--) {
			val nz = nzrow(r).get(i);
			if (nz.col == c ) return i;
		}
		return -1;
	}
	
	public def find(r:Long, c:Long) : NonZeroEntry {
		val idx = findIndex(r, c);
		if (idx >=0) return nzrow(r).get(idx);
		return zeroEntry;
	}


	
	public def remove(r:Long, c:Long): Boolean {
		val idx = findIndex(r, c);
		if (idx >=0) { 
			nzrow(r).removeAt(idx);
			nzcount --;
			return true;
		}
		return false;
	}

	/**
	 * Swap two matrix values. The two modified nonzero columns 
	 * are not ordered.
	 */
	public def swap(r1:Int, c1:Int, r2:Int, c2:Int) {
		val i1 = findIndex(r1, c1);
		val i2 = findIndex(r2, c2);
		
		if (i1<0 && i2<0) return;
		if (i1>=0 && i2>=0) {
			val tmp = nzrow(r1).get(i1);
			nzrow(r1).set(nzrow(r2).get(i2), i1);
			nzrow(c2).set(tmp, i2);
			return;
		}
		if (i1>=0) {
			nzrow(r2).add(nzrow(r1).get(i1));
			nzrow(r1).set(zeroEntry, i1);
			nzcount ++;
			return;
		}
		if (i2>=0) {
			nzrow(r1).add(nzrow(r2).get(i2));
			nzrow(r2).set(zeroEntry, i2);
			nzcount++;
		}
	}
	

	
	@Inline
	public def cmpRowMajor(nz1:NonZeroEntry,nz2:NonZeroEntry):Int {
		if (nz1.col <  nz2.col) return -1n;
		if (nz1.col == nz2.col) return 0n;
		return 1n;
	}

	
	/*
	 * This method is used to convert sparse matrix data to CSC sparse matrix memory layout
	 */
	public def sortRowMajor() {
		for (var i:Long=0; i<M; i++) {
			if (nzrow(i).size() > 0) {
				nzrow(i).sort((nz1:NonZeroEntry,nz2:NonZeroEntry)=>cmpRowMajor(nz1,nz2));
			}
		}
	}

	/*
	 * Convert nonzero list to SparseCSR data layout. The nonzero list will be
	 * sorted in column-major first, which could be time-consuming if nonzero entries
	 * are added in a different order other then column-major.
	 */
	public def toSparseCSR() : SparseCSR {
		val spa = SparseCSR.make(M,N, nzcount);
		toSparseCSR(spa);
		return spa;
	}
	
	public def toSparseCSR(spa:SparseCSR) {
		sortRowMajor();
		
		val ca  = spa.getStorage();
		val c2d = spa.crdata;
		
		var cnt:Long = 0;  //dest count at CompressArray
		for (var l:Long=0; l<M; l++) {
			val cline = c2d.cLine(l);
			cline.offset = cnt;
			cline.length = 0;
			val nzr = nzrow(l);
			for (var r:Long=0; r<nzr.size(); r++) {
				val nzv = nzr.get(r).value;
				//Remove all zero entries
				if (MathTool.isZero(nzv)) continue;
				ca.index(cnt) = nzr.get(r).col;
				ca.value(cnt) = nzr.get(r).value;
				cnt++;
			}
			cline.length = cnt - cline.offset;
		}
		ca.count = cnt;
	}

	public def toString():String {
		val str = new StringBuilder();
		str.add("Sparse matrix builder in CSR ["+M+","+N+"] nonzero entry list:\n");
		for (var r:Long=0; r<M; r++) {
			str.add("Row:"+r+" ");
			for (var i:Long=0; i<nzrow(r).size(); i++) {
				val ent = nzrow(r).get(i);	
				str.add(ent.toString());
			}
			str.add("\n");
		}
		return str.toString();
	}
}
