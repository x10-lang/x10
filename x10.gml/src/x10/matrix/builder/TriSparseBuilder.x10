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

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.util.RandTool;
import x10.matrix.sparse.SparseCSC;

public type TriSparseBuilder(bld:TriSparseBuilder)=TriSparseBuilder{self==bld};
public type TriSparseBuilder(m:Long)=TriSparseBuilder{self.M==m};

public class TriSparseBuilder extends SparseCSCBuilder{self.M==self.N} implements MatrixBuilder {
	
	val up:Boolean;
	
	public def this(uptri:Boolean, sbuilder:SparseCSCBuilder{self.M==self.N}) {
		super(sbuilder);
		up = uptri;
	}

	public def this(uptri:Boolean, spa:SparseCSC{self.M==self.N}) {
		super(spa);
		up = uptri;
	}
	
	public static def make(up:Boolean, m:Long):TriSparseBuilder(m) {
		val sbd = SparseCSCBuilder.make(m,m) as SparseCSCBuilder{self.M==self.N};
		val bdr = new TriSparseBuilder(up, sbd);
		return bdr as TriSparseBuilder(m);
	}

    static def countHalfNZ(up:Boolean, spa:SparseCSC):Long {
		var nzcnt:Long = 0;
		val ca = spa.getStorage();
		for (var c:Long=0; c<spa.N; c++) {
			val cl = spa.ccdata.cLine(c);
			if (up)
				nzcnt += cl.countIndexRangeBefore(c);
			else
				nzcnt += cl.countIndexRangeAfter(c, spa.M);
		}
		return nzcnt;
	}

    public static def countUpperNZ(spa:SparseCSC):Long = countHalfNZ(true, spa);
    public static def countLowerNZ(spa:SparseCSC):Long = countHalfNZ(false, spa);
		
	public static def copyHalf(up:Boolean, srcspa:SparseCSC, dstspa:SparseCSC(srcspa.M,srcspa.N)) {
		val src = srcspa.getStorage();
		val dst = dstspa.getStorage();
		var nzcnt:Long = 0;
		for (var c:Long=0; c<srcspa.N; c++) {
			val srcln = srcspa.ccdata.cLine(c);
			val dstln = dstspa.ccdata.cLine(c);
			val cpylen = up?srcln.countIndexRangeBefore(c):srcln.countIndexRangeAfter(c, srcspa.M);

			dstln.offset = nzcnt;
			if (cpylen > 0) {
				val srcoff = up?srcln.offset:srcln.offset+srcln.length-cpylen;
				Rail.copy[Long  ](src.index, srcoff, dst.index, dstln.offset, cpylen);
				Rail.copy[Double](src.value, srcoff, dst.value, dstln.offset, cpylen);
			}
			dstln.length = cpylen;
			nzcnt += cpylen;
		}	
		dst.count = nzcnt;
	}

	/**
	 * Initialize symmetric sparse builder using matrix data generator function.
	 */
    public def init(fval:(Long,Long)=>Double):TriSparseBuilder(this) {
		for (var c:Long=0; c<N; c++) {
			val stt = up?0L:c;
			val end = up?c:M;
			for (var r:Long=stt; r<end; r++) {
				val v = fval(r, c);
				if (! MathTool.isZero(v)) append(r, c, v);
			}
		}
		return this;
	}
	
	/**
	 * Initial triangular matrix builder with randomized in row indexes, and
	 * values using the given function.
	 * 
	 * @param nzd    nonzero sparsity. Used to computing row index distance between two nonzeros.
	 * @param fval   return a double value using row and column index as inputs. 
	 */
    public def initRandom(halfNZD:Double, fval:(Long,Long)=>Double):TriSparseBuilder(this) {
		val rgen = RandTool.getRandGen();
        val maxdst:Long = ((1.0/halfNZD) as Int) * 2 - 1;
        var r:Long = rgen.nextLong(maxdst/2);
        var c:Long = 0;
		while (true) {
			if (r < M){
				if (up)
					append(c, r, fval(c, r));
				else 
					append(r, c, fval(r, c));
				r+= rgen.nextLong(maxdst) + 1;
			} else {
				c++;
				r -= (M - c);
				if (c>=M) break;
			}
		}
		
		return this;
	}
	
	public def initRandom(halfNZD:Double):TriSparseBuilder(this) =
		initRandom(halfNZD, (Long,Long)=>(RandTool.getRandGen().nextDouble()));

	/**
	 * Initialize builder using existing sparse matrix. 
	 * This is not best way to create triangular matrix.  It is more fast using
	 * copyHalf method.
	 */
	public def init(src:SparseCSC(M,N)):TriSparseBuilder(this) {
		val srcca = src.getStorage();
		for (var c:Long=0; c<src.N; c++) {
			val srcln = src.ccdata.cLine(c);
			val cpylen = up?srcln.countIndexRangeBefore(c):srcln.countIndexRangeAfter(c, src.M);

			var off:Long = up?srcln.offset:srcln.offset+srcln.length-cpylen;
			for (var idx:Long=off; idx<off+cpylen; idx++) {
				val r = srcca.index(idx);
				val v = srcca.value(idx);
				append(r, c, v);
			}
		}
		return this;
	}
	

	/**
	 * Return the value at given row and column; If not found in nonzero list, 0 is returned.
	 */
	@Inline
	public def get(r:Long, c:Long) : Double {
		if (up) {
			if ( r <= c)
				return findEntry(r,c).value;
			else
				return 0.0;
		} else {
			if ( r >= c)
				return findEntry(r,c).value;
			else
				return 0.0;
		}
	}
	
	/**
	 * Set the entry of given row and column to value, if it is found. Otherwise append
	 * new nonzero entry at the end of nonzero list;
	 */
	@Inline
	public def set(r:Long, c:Long, v:Double) : void {
		if ((up && r <= c) || (up==false && r >= c))
			set(r, c, v);
		else {
			Debug.flushln("Error is in setting triangular matrix");
		}
	}
	@Inline
	public def reset(r:Long, c:Long) : Boolean {
		if ((up && r <= c) || (up==false && r >= c))
			return reset(r, c);
		else {
			return false;
		}
	}


	/**
	 * Convert to symmetrix sparse builder using the same memory space.
	 */
	public def toSymSparseBuilder():SymSparseBuilder(M) {
		val symbld = new SymSparseBuilder(this as SparseCSCBuilder{self.M==self.N});
		if (up) 
			symbld.mirrorToLower();
		else
			symbld.mirrorToUpper();
		return symbld as SymSparseBuilder(M);
	}
	
	// No TriSparse defined;
	


	public def toMatrix():Matrix(M,M) = super.toSparseCSC() as Matrix(M,M);
	

	public def toString() = "Triangular sparse builder\n"+super.toString();
	
}
