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

package x10.matrix.builder;

import x10.compiler.Inline;

import x10.matrix.util.MathTool;
import x10.matrix.util.RandTool;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.SymSparseCSC;

public type SymSparseBuilder(bld:SymSparseBuilder)=SymSparseBuilder{self==bld};
public type SymSparseBuilder(m:Long)=SymSparseBuilder{self.M==m};

public class SymSparseBuilder extends SparseCSCBuilder{self.M==self.N} implements MatrixBuilder {
	/**
	 * Cast Sparse matrix builder to symmetric sparse matrix, while using the 
	 * same memory allocation space.
	 */
	public def this(sbld:SparseCSCBuilder{self.M==self.N}) {
		super(sbld);
	}
	
	public def this(spa:SparseCSC{self.M==self.N}):SymSparseBuilder(spa.M) {
		super(spa);
	}
	
	public static def make(m:Long) : SymSparseBuilder(m) {
		val bdr =  new SymSparseBuilder(SparseCSCBuilder.make(m,m));
		return bdr as SymSparseBuilder(m);
	}

	/**
	 * Initialize symmetric sparse builder using matrix data generator function.
	 */
	public def init(fval:(Long,Long)=>Double) :SymSparseBuilder(this) {
		for (var c:Long=0; c<N; c++) {
			for (var r:Long=c; r<M; r++) {
				val v = fval(r, c);
				if (! MathTool.isZero(v)) {
					this.append(r, c, v);
				}
			}
		}
		return this;
	}

    // replicated from superclass to workaround xlC bug with using & itables
	public def init(spamat:SparseCSC(M,N)): SparseCSCBuilder(this) {
		val ca = spamat.getStorage();
		var offset:Long = 0;
		for (var c:Long=0; c<N; c++) {
			val cnt = spamat.ccdata.cLine(c).length;
			for (var i:Long=0; i<cnt; i++, offset++) {
				append(ca.index(offset), c, ca.value(offset));
			}
		}
		return this;
	}

	/**
	 * Initial builder with randomized in row indexes, and
	 * values using the given function.
	 * 
	 * @param nzd    nonzero sparsity. Used to computing row index distance between two nonzeros.
	 * @param fval   return a double value using row and column index as inputs. 
	 */
	public def initRandom(nzd:Double, fval:(Long,Long)=>Double):SymSparseBuilder(this) {
		val rgen = RandTool.getRandGen();
		val maxdst:Long = ((1.0/nzd) as Int) * 2 - 1;
		var r:Long = rgen.nextLong(maxdst/2);
		var c:Long = 0;
		while (true) {
            if (r < M) {
				append(r, c, fval(r, c));
				r+= rgen.nextLong(maxdst) + 1;
			} else {
				c++;
				r -= (M-c);
				if (c>=M) break;
			}
		}
		
		return this;
	}
	
	/**
     * Initialize with random values in specified sparsity.
	 */
	public def initRandom(nzd:Double) : SymSparseBuilder(this) =
		initRandom(nzd, (r:Long,c:Long)=>RandTool.getRandGen().nextDouble());
	
	/**
     * Initialize symmetric sparse builder using an existing matrix.
     * @param up       upper triangular flag
	 * @param src      source matrix.
	 */
	public def init(up:Boolean, src:SparseCSC(M,M)): SymSparseBuilder(this) {
		val ca = src.getStorage();

		for (var c:Long=0; c<M; c++) {
			val srcln = src.ccdata.cLine(c);
			val cpylen = up?srcln.countIndexRangeBefore(c):srcln.countIndexRangeAfter(c, src.M);
			var off:Long = up?srcln.offset:srcln.offset+srcln.length-cpylen;
			for (var i:Long=off; i<off+cpylen; i++) {
				val r = ca.index(i);
				val v = ca.value(i);
				append(r, c, v);
			}
		}
		return this;
	}
	
	/**
	 * Add new nonzero entry. Keep the new nonzero entry in order
	 */
	@Inline
	public def add(r:Long, c:Long, v:Double) {
		super.insert(r, c, v);
		if (r!=c)
			super.insert(c, r, v);
	}
	
	/**
	 * Append nonzero at the end of nonzero list of the specified column. 
	 */
	@Inline
	public def append(r:Long, c:Long, v:Double) {
		super.append(r, c, v);
		if (r!= c)
			super.append(c, r, v);
	}
	
	/*
	 * Return the value at given row and column; If not found in nonzero list, 0 is returned.
	 */
	public def get(r:Long, c:Long) : Double = findEntry(r,c).value;
	
	/*
	 * Set the entry of given row and column to value, if it is found. Otherwise append
	 * new nonzero entry at the end of nonzero list;
	 */
	@Inline
	public def set(r:Long, c:Long, v:Double) : void {
		super.set(r, c, v);
		if (r != c)
			super.set(c, r, v);
	}
	
	public def reset(r:Long, c:Long) : Boolean = super.remove(r,c);

	/**
	 * Copy upper triangular part to lower. The lower part must contain none nonzero entries.
	 * The original builder must be a upper triangular 
	 */
	public def mirrorToLower() {
		var r:Long =0;
		for (var c:Long=1; c<N; c++) {
			val nzc = nzcol(c);
			val itr = nzc.iterator();
			while (itr.hasNext()){
				val nz = itr.next();
				r = nz.row;
				super.append(c, r, nz.value);
				// This is efficient, but do not replace the old nonzero value, 
			}
		}
	}
	
	/**
	 * Copy lower triangular part to upper. Upper part must contains none nonzero entries.
	 */
	public def mirrorToUpper() {
		var r:Long =0;
		for (var c:Long=1; c<N; c++) {
			val nzc = nzcol(c);
			val itr = nzc.iterator();
			while (itr.hasNext()){
				val nz = itr.next();
				r = nz.row;
				super.insert(c, r, nz.value); //Not efficient.
			}
		}
	}

	public def checkSymmetric():Boolean {
		var ret:Boolean = true;
		for (var c:Long=0; c<N&&ret; c++)
			for (var r:Long=0; r<M&&ret; r++) {
				ret &= get(r,c) == get(c,r);
			}
				
		return ret;
	}

	public def toSymSparseCSC():SymSparseCSC(M) {
		val spa = toSparseCSC();
		val bdr = new SymSparseCSC(M, spa.ccdata);
		return bdr as SymSparseCSC(M);
	}
	
	public def toString() :String = "Symmetric sparse builder\n"+toString();
}
