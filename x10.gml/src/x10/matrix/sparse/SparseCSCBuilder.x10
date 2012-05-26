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

package x10.matrix.sparse;

import x10.compiler.Inline;
import x10.io.Console;
import x10.util.Pair;
import x10.util.StringBuilder;
import x10.util.ArrayList;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.MathTool;
import x10.matrix.DenseMatrix;
import x10.matrix.VerifyTools;

import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.Compress2D;
import x10.matrix.sparse.SparseCSC;

public class SparseCSCBuilder {
	
	static struct NonZeroEntry(row:Int, col:Int, value:Double) {

		def this(r:Int, c:Int, v:Double) { 
			property(r, c, v);
		}
		@Inline
		public def equal(that:NonZeroEntry) : Boolean = (this.row==that.row&&this.col==that.col);
		
		@Inline
		public def sameRow(r:Int): Boolean = (r == this.row);
		
		@Inline
		public def sameCol(c:Int): Boolean = (c == this.col);
		
		@Inline
		public def toString():String =
			"Row:"+row.toString() + "\t Col:"+col.toString()+"\t value:"+value.toString();
		
	}
	
	static val zeroEntry = NonZeroEntry(0, 0, 0.0);
	
	//=======================================================
	val colsizes:Array[Int](1);
	val nonzeros:ArrayList[NonZeroEntry];

	public var M:Int;
	public var N:Int;
	//=======================================================
	public def this(m:Int, n:Int) {
		M = m; N=n;
		
		nonzeros = new ArrayList[NonZeroEntry]();
		colsizes = new Array[Int](n);
	}
	
	//----------------------------------------------
	/*
	 * Creating sparse csc matrix builder using given existing sparse matrix.
	 */
	public static def make(spamat:SparseCSC): SparseCSCBuilder {
		val bld = new SparseCSCBuilder(spamat.M, spamat.N);
		val ca = spamat.getStorage();
		var offset:Int = 0;
		for (var c:Int=0; c<bld.N; c++) {
			val cnt = spamat.ccdata.cLine(c).length;
			for (var i:Int=0; i<cnt; i++, offset++) {
				bld.append(ca.index(offset), c, ca.value(offset));
			}
		}
		return bld;
	}
	
	public static def make(spamat:SparseCSR): SparseCSCBuilder {
		val bld = new SparseCSCBuilder(spamat.M, spamat.N);
		val ca = spamat.getStorage();
		var offset:Int = 0;
		for (var r:Int=0; r<bld.M; r++) {
			val cnt = spamat.crdata.cLine(r).length;
			for (var i:Int=0; i<cnt; i++, offset++) {
				bld.append(r, ca.index(offset), ca.value(offset));
			}
		}
		return bld;
	}
	
	public static def makeTranspose(spamat:SparseCSC): SparseCSCBuilder {
		val bld = new SparseCSCBuilder(spamat.N, spamat.M);
		val ca = spamat.getStorage();
		var offset:Int = 0;
		for (var c:Int=0; c<spamat.N; c++) {
			val cnt = spamat.ccdata.cLine(c).length;
			for (var i:Int=0; i<cnt; i++, offset++) {
				val r = ca.index(offset);
				bld.add(c, r, ca.value(offset));
			}
		}
		return bld;
	}
	
	//==============================================
	/*
	 * Append nonzero matrix entry (row, column and values), after the last of
	 * entry of same column in the nonzero list
	 */
	public def add(r:Int, c:Int, v:Double) {
		val nz = NonZeroEntry(r, c, v);
		val idx = findLastIndexInCol(c);
		nonzeros.addBefore(idx+1, nz);
		colsizes(c) ++;
	}
	
	/*
	 * Append nonzero at the end of nonzero list. 
	 */
	public def append(r:Int, c:Int, v:Double) {
		nonzeros.add(NonZeroEntry(r, c, v));
		colsizes(c) ++;
	}
	
	/*
	 * Return the value at given row and column; If not found in nonzero list, 0 is returned.
	 */
	public def get(r:Int, c:Int) : Double {
		val foundnz = find(r, c);
		return foundnz.value;
	}
	
	/*
	 * Set the entry of given row and column to value, if it is found. Otherwise append
	 * new nonzero entry at the end of nonzero list;
	 */
	public def set(r:Int, c:Int, v:Double) : Boolean {
		val idx = findIndex(r, c);
		if (idx < 0) {
			add(r, c, v);
			return true;
		} else {
			val nz = NonZeroEntry(r, c, v);
			nonzeros.set(nz, idx);
			return false;
		}
	}
	
	//======================================
	/*
	 * Find the index of the entry of given row and column in the nonzero list. If not found, -1 is returned.
	 */
	public def findIndex(r:Int, c:Int) : Int {
		var i:Int = findLastIndexInCol(c);
		for (; i>=0; i--) {
			val nz = nonzeros.get(i);
			if (nz.col < c) break;
			if (nz.row == r ) return i;
		}
		return -1;
	}
	
	public def find(r:Int, c:Int) : NonZeroEntry {
		val idx = findIndex(r, c);
		if (idx >=0) return nonzeros.get(idx);
		return zeroEntry;
	}

	public def findLastIndexInCol(c:Int) : Int {
		var i:Int = 0;
		for (var k:Int=0; k<=c; k++)
			i += colsizes(k);
		return i-1;
	}
	
	//=========================================
	
	public def remove(r:Int, c:Int): Boolean {
		val idx = findIndex(r, c);
		if (idx >=0) { 
			nonzeros.removeAt(idx);
			return true;
		}
		return false;
	}

	public def swap(r1:Int, c1:Int, r2:Int, c2:Int) {
		val i1 = findIndex(r1, c1);
		val i2 = findIndex(r2, c2);
		
		if (i1<0 && i2<0) return;
		if (i1>=0 && i2>=0) {
			val tmp = nonzeros.get(i1);
			nonzeros.set(nonzeros.get(i2), i1);
			nonzeros.set(tmp, i2);
			return;
		}
		if (i1>=0) {
			val tmp = nonzeros.get(i1);
			val idx = findLastIndexInCol(c2);
			nonzeros.addBefore(idx+1, tmp);
			nonzeros.set(zeroEntry, i1);
			return;
		}
		if (i2>=0) {
			val tmp = nonzeros.get(i2);
			val idx = findLastIndexInCol(c1);
			nonzeros.addBefore(idx+1, tmp);
			nonzeros.set(zeroEntry, i2);
		}
	}
	
	//====================================
	
	@Inline
	public def cmpColMajor(nz1:NonZeroEntry,nz2:NonZeroEntry):Int {
		if (nz1.col < nz2.col) return -1;
		if (nz1.col == nz2.col) {
			if (nz1.row < nz2.row) return -1;
			if (nz1.row == nz2.row) return 0;
		}
		return 1;
	}

	
	/*
	 * This method is used to convert sparse matrix data to CSC sparse matrix memory layout
	 */
	public def sortColMajor() {
		nonzeros.sort((nz1:NonZeroEntry,nz2:NonZeroEntry)=>cmpColMajor(nz1,nz2));
	}
	
	//=================================
	/*
	 * Convert nonzero list to SparseCSC data layout. The nonzero list will be
	 * sorted in column-major first, which could be time-consuming if nonzero entries
	 * are added in a different order other then column-major.
	 */
	public def toSparseCSC() : SparseCSC {
		val spa = SparseCSC.make(M,N, nonzeros.size());
		toSparseCSC(spa);
		return spa;
	}
	
	public def toSparseCSC(spa:SparseCSC) {
		sortColMajor();
		
		val nzs = nonzeros.size();
		val ca  = spa.getStorage();
		val c2d = spa.ccdata;
		
		var idx:Int = 0;  //source count
		var cnt:Int = 0;  //dest count at CompressArray
		for (var l:Int=0; l<N; l++) {
			val cline = c2d.cLine(l);
			cline.offset = cnt;
			cline.length = 0;
			
			for (var r:Int=0; r<colsizes(l); r++, idx++) {
				val nzv = nonzeros.get(idx).value;
				//Remove all zero entries
				if (MathTool.isZero(nzv)) continue;
				ca.index(cnt) = nonzeros.get(idx).row;
				ca.value(cnt) = nonzeros.get(idx).value;
				cnt++; 
				cline.length++;
			}
		}
		ca.count = cnt;
	}
	
	//=================================
	public def toString():String {
		
		val str = new StringBuilder();
		str.add("Sparse matrix in CSC ["+M+","+N+"] nonzero entry list:\n");
		for (var i:Int=0; i<nonzeros.size(); i++) {
			val ent = nonzeros.get(i);	
			str.add(i.toString()+"\t "+ent.toString()+"\n");
		}
		return str.toString();
	}
	
	public def print(msg:String) {
		Console.OUT.println(msg);
		Console.OUT.print(toString());
		Console.OUT.flush();
	}
	
	public def print():void {
		print("");
	}
}