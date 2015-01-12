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

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.util.MathTool;
import x10.matrix.util.RandTool;
import x10.matrix.sparse.SparseCSC;

public type SparseCSCBuilder(bld:SparseCSCBuilder)=SparseCSCBuilder{self==bld};
public type SparseCSCBuilder(m:Long,n:Long)=SparseCSCBuilder{self.M==m,self.N==n};
public type SparseCSCBuilder(m:Long)=SparseCSCBuilder{self.M==self.N,self.M==m};

/**
 * Sparse matrix builder uses different memory space from SparseCSC instance. 
 */
public class SparseCSCBuilder(M:Long, N:Long) implements MatrixBuilder {
	
	public static struct NonZeroEntry(row:Long, value:Double) {
		def this(r:Long, v:Double) { 
			property(r, v);
		}
		@Inline
		public def equal(that:NonZeroEntry) : Boolean = (this.row==that.row);
		
		@Inline
		public def sameRow(r:Long): Boolean = (r == this.row);
		
		@Inline
		public def toString():String = "<R:"+row+","+value+">";
		
	}
	
	static val zeroEntry = NonZeroEntry(0, 0.0);
	
	public var nzcount:Long=0;
	public val nzcol:Rail[ArrayList[NonZeroEntry]];
	public val output:SparseCSC(M,N);

	public def this(leadDim:Long, cols:Rail[ArrayList[NonZeroEntry]], spa:SparseCSC(leadDim, cols.size)) {
		property(leadDim, cols.size);
		nzcol = cols;
		output = spa;//SparseCSC.make(M, N, 0); 
	}
	
	/**
	 * Creating sparse builder and using the input sparse to store the output.
	 * @param spa    SparseCSC instance to store the output
	 */
	public def this(spa:SparseCSC) {
		property(spa.M, spa.N);
		nzcol =  new Rail[ArrayList[NonZeroEntry]](spa.N, (i:Long)=>new ArrayList[NonZeroEntry]());
		output = spa;
	}
	
	/**
	 * Construct a sparse builder from the given sparse builder instance.
	 * This constructor is used in constructing super class of a symmetric or triangular sparse matrix builder
	 * which are derived from Sparse builder.
	 */
	public def this(bdr:SparseCSCBuilder) {
		property(bdr.M, bdr.N);
		nzcol = bdr.nzcol;
		output = bdr.output;
	}
	
	public static def make(m:Long, n:Long):SparseCSCBuilder(m,n) {
        val cols = new Rail[ArrayList[NonZeroEntry]](n, (i:Long)=>new ArrayList[NonZeroEntry]());
        return new SparseCSCBuilder(m, cols, SparseCSC.make(m, n, 0L));
	}

	/*
	 * Creating sparse csc matrix builder using given existing sparse matrix.
	 * @param spamat    sparse matrix to initialize the sparse builder
	 */
	public def init(spamat:SparseCSC(M,N)): SparseCSCBuilder(this) {
		val ca = spamat.getStorage();
		var offset:Long = 0;
		for (var c:Long=0; c<N; c++) {
			val cnt = spamat.ccdata.cLine(c).length;
			for (var i:Long=0; i<cnt; i++, offset++) {
				append(ca.index(offset), c, ca.value(offset));
			}
		}
        this.toSparseCSC();
		return this;
	}

	/**
	 * Create sparse matrix builder by using the transposed sparse matrix's data
	 */
	public def initTransposeFrom(spamat:SparseCSC(N,M)): SparseCSCBuilder(this) {
		val ca = spamat.getStorage();
		var offset:Long = 0;
		for (var c:Long=0; c<spamat.N; c++) {
			val cnt = spamat.ccdata.cLine(c).length;
			for (var i:Long=0; i<cnt; i++, offset++) {
				val r = ca.index(offset);
				append(c, r, ca.value(offset));
			}
		}
        this.toSparseCSC();
		return this;
	}

	/**
	 * Initialize data of sparse matrix builder with matrix generating function.
	 * @param initFunc       data value generating function.
	 */
	public def init(initFunc:(Long,Long)=>Double) : SparseCSCBuilder(this) {
		for (var c:Long=0; c<N; c++)
			for (var r:Long=0; r<M; r++) {
				val v = initFunc(r,c);
				if (MathTool.isZero(v)) continue;
				append(r, c, v);
			}

        this.toSparseCSC();
		return this;
	}
	
	/**
	 * Initialize data of sparse matrix builder with matrix generating function and specified sparsity
	 * @param nzd            nonzero sparsity
	 * @param initFunc       data value generating function.
	 */
	public def initRandom(nzd:Double, initFunc:(Long,Long)=>Double) : SparseCSCBuilder(this) {
		val rgen = RandTool.getRandGen();
		val maxdst:Long = ((1.0/nzd) as Int) * 2 - 1;
		var r:Long = rgen.nextLong(maxdst/2);
		var c:Long = 0;
		while (true) {
			if (r < M){
				append(r, c, initFunc(r, c));
				r+= rgen.nextLong(maxdst) + 1;
			} else {
				r -= M;
				c++;
				if (c>=N) break;
			}
		}
        this.toSparseCSC();
		return this;
	}

	/**
	 * Initial with random value in specified sparsity.
	 */
	public def initRandom(nzd:Double) : SparseCSCBuilder(this) =
		initRandom(nzd, (r:Long,c:Long)=>RandTool.getRandGen().nextDouble());

	/**
	 * Generate half triangular matrix.
	 */
	// public def initRandomTri(halfNZD:Double, up:Boolean) : SparseCSCBuilder(this) {
	// 	val rgen = RandTool.getRandGen();
	// 	val maxdst:Int = ((1.0/halfNZD) as Int) * 2 - 1;
	// 	var r:Long = rgen.nextLong(maxdst/2);
	// 	var c:Long = 0;
	// 	
	// 	if (up) {
	// 		while (true) {
	// 			val dia = (c < M)?c:M;
	// 			if (r <= dia ){
	// 				append(r, c, rgen.nextDouble());
	// 				r+= rgen.nextLong(maxdst) + 1;
	// 			} else {
	// 				c++;
	// 				r -= c;
	// 				if (c>=N) break;
	// 			}
	// 		}
	// 	} else {
	// 		while (true) {
	// 			if (r < M ){
	// 				append(r, c, rgen.nextDouble());
	// 				r+= rgen.nextLong(maxdst) + 1;
	// 			} else {
	// 				c++;
	// 				r -= (M - ((c < M)?c:0));
	// 				if (c>=N) break;
	// 			}
	// 		}
	// 	}
	// 	return this;
	// }
	
	// public def initRandomSym(halfNZD:Double) : SparseCSCBuilder(this) {
	// 	Debug.assure(M==N, "Not symmetric matrix");
	// 	val rgen = RandTool.getRandGen();
	// 	val maxdst:Int = ((1.0/halfNZD) as Int) * 2 - 1;
	// 	var r:Long = rgen.nextLong(maxdst/2);
	// 	var c:Long = 0;
	// 	while (true) {
	// 		if (r < M ){
	// 			val v = rgen.nextDouble();
	// 			append(r, c, v);
	// 			if (r != c) 
	// 				append(c, r, v);
	// 			r+= rgen.nextLong(maxdst) + 1;
	// 		} else {
	// 			c++;
	// 			r -= (M - c) ;
	// 			if (c>=N) break;
	// 		}
	// 	}
	// 	return this;
	// }

	


	/*
	 * Add new nonzero entry at the ordered nonzero list of the specified column
	 */
	@Inline
	public def insert(r:Long, c:Long, v:Double):Boolean {
		val nz = NonZeroEntry(r, v);
		
		val idx = find(r, c);
		if (idx >= nzcol(c).size())
			nzcol(c).add(nz); //add at the end
		else if (nzcol(c).get(idx).row != r)
			nzcol(c).addBefore(idx, nz);
		else {
			nzcol(c).set(nz, idx); //Replace the earlier one
			return false;
		}
		nzcount ++;
		return true;
	}
	
	/*
	 * Append nonzero at the end of nonzero list of the specified column. 
	 */
	@Inline
	public def append(r:Long, c:Long, v:Double) {
		if (MathTool.isZero(v)) return;
		val nz = NonZeroEntry(r, v);
		nzcol(c).add(nz);
		nzcount ++;
	}
	
	/*
	 * Return the value at given row and column; If not found in nonzero list, 0 is returned.
	 */
	@Inline
	public def get(r:Long, c:Long) : Double {
		val foundnz = findEntry(r, c);
		return foundnz.value;
	}
	
	/*
	 * Set the entry of given row and column to value, if entry is found. Otherwise append
	 * new nonzero entry at the end of nonzero list;
	 */
	@Inline
	public def set(r:Long, c:Long, v:Double):void {
		insert(r, c, v);
	}
	
	@Inline
	public def reset(r:Long, c:Long) : Boolean = remove(r,c);


	/*
	 * Find the index of the entry of given row and column in the nonzero list. 
	 * Binary search is used. If found the nonzero entry's row in column's arraylist,
	 * the index is returned. If not found, the array index for the new entry is returned.
	 */
    public def find(r:Long, c:Long):Long {
		val al = nzcol(c);
        if (al.size() == 0L) return 0L;

		var min:Long = 0; 
		var max:Long = al.size()-1; 
		
		var mid:Long = min;
		do {
			mid = min + (max - min) / 2;
			val ridx = al.get(mid).row;
			if (ridx < r) {
				min = mid + 1;
				if (min > max) return min;
			} else if (r < ridx) {
				max = mid - 1; 
				if (min > max) return max<0L?0L:max;
			} else {
				return mid;
			}

		} while ( true );
	}
	
	def findIndex(r:Long, c:Long):Long {
		val idx = find(r, c);
		if (idx >= nzcol(c).size())     return -1; //Larger than any row number in list
		if (nzcol(c).get(idx).row != r)	return -2; //Not in the row list
		return idx;
	}
	
	def findIndexFromBehind(r:Long, c:Long):Long {
		var i:Long = nzcol(c).size()-1;
		for (; i>=0; i--) {
			val nz = nzcol(c).get(i);
			if (nz.row <= r ) break;
		}
		return i;
	}
	
    public def findEntry(r:Long, c:Long):NonZeroEntry {
		val ridx = findIndex(r, c);
		if (ridx >=0) 
			return nzcol(c).get(ridx);
		return zeroEntry;
	}


	@Inline
	public def remove(r:Long, c:Long):Boolean {
		val idx = findIndex(r, c);
		if (idx < 0) return false;		//Not found
		nzcol(c).removeAt(idx);
		nzcount --;
		return true;
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
			val tmp = nzcol(c1).get(i1);
			nzcol(c1).set(nzcol(c2).get(i2), i1);
			nzcol(c2).set(tmp, i2);
			return;
		}
		if (i1>=0) {
			nzcol(c2).add(nzcol(c1).get(i1));
			nzcol(c1).set(zeroEntry, i1);
			nzcount ++;
			return;
		}
		if (i2>=0) {
			nzcol(c1).add(nzcol(c2).get(i2));
			nzcol(c2).set(zeroEntry, i2);
			nzcount++;
		}
	}

	@Inline
	public def cmpColMajor(nz1:NonZeroEntry,nz2:NonZeroEntry):Int {
		if (nz1.row < nz2.row)  return -1n;
		if (nz1.row == nz2.row) return 0n;
		return 1n;
	}

	/*
	 * This method is used to convert sparse matrix data to CSC memory layout
	 */
	public def sortColMajor() {
		for (var i:Long=0; i<N; i++) {
			if (nzcol(i).size() > 0) {
				nzcol(i).sort((nz1:NonZeroEntry,nz2:NonZeroEntry)=>cmpColMajor(nz1,nz2));
			}
		}
	}

	/*
	 * Convert nonzero list to SparseCSC data layout. The nonzero list will be
	 * sorted in column-major first, which could be time-consuming if nonzero entries
	 * are added in a different order other then column-major.
	 */
	public def toSparseCSC() : SparseCSC(output) {
		//val spa = SparseCSC.make(M, N, nzcount);
		//sortColMajor();
		
		val ca  = output.getStorage();
		val c2d = output.ccdata;
		// How to deal about when sparse compressed array is not big enough
		if (ca.storageSize() < nzcount) {
			output.testIncStorage(nzcount);
		}
		
		var cnt:Long = 0;  //dest count at CompressArray
		for (var l:Long=0; l<N; l++) {
			val cline = c2d.cLine(l);
			cline.offset = cnt;
			cline.length = 0;
			val nzc = nzcol(l);
			for (var r:Long=0; r<nzc.size(); r++) {
				val nzv = nzc.get(r).value;
				//Remove all zero entries
				if (MathTool.isZero(nzv)) continue;
				ca.index(cnt) = nzc.get(r).row;
				ca.value(cnt) = nzc.get(r).value;
				cnt++; 
			}
			cline.length = cnt - cline.offset;
		}
		ca.count = cnt;
		return output;
	}
	
	public def toMatrix():Matrix(M,N) = toSparseCSC() as Matrix(M,N);

	public def toString():String {
		val str = new StringBuilder();
		str.add("Sparse matrix builder in CSC ["+M+","+N+"] nonzero entry list:\n");
		for (var c:Long=0; c<N; c++) {
			str.add("Col:"+c+" ");
			for (var i:Long=0; i<nzcol(c).size(); i++) {
				val ent = nzcol(c).get(i);	
				str.add(ent.toString());
			}
			str.add("\n");
		}
		return str.toString();
	}
}
