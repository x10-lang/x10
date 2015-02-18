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

import x10.util.Pair;

import x10.matrix.Matrix;
import x10.matrix.util.MathTool;
import x10.matrix.builder.SymSparseBuilder;
import x10.matrix.ElemType;

public type SymSparseCSC(m:Long)         =SymSparseCSC{self.M==m,self.N==m};
public type SymSparseCSC(C:SymSparseCSC)=SymSparseCSC{self==C};
public type SymSparseCSC(C:Matrix)      =SymSparseCSC{self==C};

/**
 * Symmetric sparse matrix is derived from SparseCSC matrix. All sparse data is
 * stored in CSC format, (same as symmetric dense).
 * <p>
 * Results of some cell-wise operations on symmetric matrix is stored in dense instances.
 * 
 */
public class SymSparseCSC extends SparseCSC{self.M==self.N} {
	public def this(m:Long, cd:Compress2D):SymSparseCSC(m) {
		super(m, m, cd);
	}
	
	public def this(m:Long,ca:CompressArray):SymSparseCSC(m) {
		super(m, m, ca);
	}

	public static def make(m:Long, nzcnt:Long):SymSparseCSC(m) {
		val ca = new CompressArray(nzcnt); 
		return new SymSparseCSC(m, ca); 
	}
	
	public static def make(m:Long, nzd:Float):SymSparseCSC(m) {
		val nzc = ((nzd*m*m +m) /2) as Long;
		val ca = new CompressArray(nzc); 
		return new SymSparseCSC(m, ca); 
	}

	public def init(uplo:Boolean, src:SparseCSC(M,M)): SymSparseCSC(this) {
		val bdr = new SymSparseBuilder(this);
		bdr.init(uplo, src);
		bdr.toSparseCSC();
		return this;
	}
	
	public def init(v:ElemType, sp:Float):SparseCSC(this) {
		val cnt = ccdata.initConst(false, M, v, sp);
		return this;
	}
		
	public def init(v:ElemType):SparseCSC(this) {
	    val nzp = 1.0f*getStorageSize()/M/N;
	    val cnt = ccdata.initConst(false, M, v, nzp);
	    return this;
	}
	
	public def initRandom(lb:Long, ub:Long, sp:Float) : SparseCSC(this) {
		val cnt = ccdata.initRandomFast(false, M, sp, lb, ub);
		//sparsity = 1.0 * cnt/M/N;
		return this;
	}
	
	public def initRandom(sp:Float) : SparseCSC(this) {
		val cnt = ccdata.initRandomFast(false, M, sp);
		//sparsity = 1.0 * cnt/M/N;
		return this;
	}
	
	public def initRandom(lb:Long, ub:Long): SparseCSC(this) { 
	    val nzd = 1.0f * getStorageSize() /M/N;
	    initRandom(lb, ub, nzd);
	    return this;
	}
	
	public def initRandom(): SparseCSC(this) { 
	    val nzd = 1.0f * getStorageSize() /M/N;
	    initRandom(nzd);
	    return this;
	}
	
	public def init(f:(Long,Long)=>ElemType): SparseCSC(this) {
		var offset:Long=0;
		val ca = getStorage();
		for (var c:Long=0; c<N; c++) {
			val ccol = ccdata.cLine(c);
			ccol.offset = offset;
			for (var r:Long=c; r<M&&offset<ca.index.size; r++) {
				val nzval = f(r, c);
				if (! MathTool.isZero(nzval)) {
					ca.index(offset)=r;
					ca.value(offset)=nzval;
					offset++;
				}
			}
			ccol.length = offset - ccol.offset;
		}
		ca.count = offset;
		return this;
	}
	
	public def init(fidx:(Long,Long)=>Long, fval:(Long,Long)=>ElemType): SparseCSC(this) {
		var offset:Long=0;
		val ca = getStorage();
		for (var c:Long=0; c<N; c++) {
			val ccol = ccdata.cLine(c);
			ccol.offset = offset;
			for (var r:Long=c; r<M&&offset<ca.index.size; r++) {
				val nzidx = fidx(r, c);
				if (nzidx >= M) break;
				val nzval = fval(nzidx, c);
				if (! MathTool.isZero(nzval)) {
					ca.index(offset)=nzidx;
					ca.value(offset)=nzval;
					offset++;
				}
			}
			ccol.length = offset - ccol.offset;
		}
		ca.count = offset;
		return this;
	}

	public def clone():SymSparseCSC(M) {
		val cd = ccdata.clone();
		val ss = new SymSparseCSC(this.M, cd);
		return ss as SymSparseCSC(M);
	}

	public operator this(r:Long, c:Long):ElemType {
		if (r>=c) 
			return ccdata(c, r);
		else
			return ccdata(r, c);
	}
	
	public operator this(a:Long):ElemType {
		if (a%M>=a/M)
			return ccdata(a/M,a%M);
		return ccdata(a%M, a/M);
	}
	
	public operator this(r:Long, c:Long) = (v:ElemType):ElemType {
		if (r >=c)
			ccdata(c)=Pair[Long,ElemType](r,v);
		else
			ccdata(r)=Pair[Long,ElemType](c,v);
		return v;
	}
	
}
