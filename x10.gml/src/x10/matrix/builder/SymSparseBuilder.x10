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

package x10.matrix.builder;

import x10.compiler.Inline;
import x10.io.Console;
import x10.util.Random;
import x10.util.Timer;
import x10.util.ArrayList;
import x10.util.StringBuilder;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Debug;
import x10.matrix.MathTool;
import x10.matrix.RandTool;

import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.SymSparseCSC;
import x10.matrix.sparse.CompressArray;
import x10.matrix.sparse.Compress1D;

public type SymSparseBuilder(bld:SymSparseBuilder)=SymSparseBuilder{self==bld};
public type SymSparseBuilder(m:Int)=SymSparseBuilder{self.M==m};

/**
 * 
 * <p>
 * 
 */
//public class SymSparseBuilder(M:Int) implements MatrixBuilder {
//public val builder:SparseCSCBuilder(M,M);
public class SymSparseBuilder extends SparseCSCBuilder{self.M==self.N} implements MatrixBuilder {
	//===================================

	public def this(sbld:SparseCSCBuilder{self.M==self.N}) {
		super(sbld);
	}
	
	//=====================================
	public static def make(m:Int) : SymSparseBuilder(m) {
		val bdr =  new SymSparseBuilder(SparseCSCBuilder.make(m,m));
		return bdr as SymSparseBuilder(m);
	}

	//=====================================
	//=====================================
	/**
	 * Initialize symmetric sparse builder using matrix data generator function.
	 */
	public def init(fval:(Int, Int)=>Double) :SymSparseBuilder(this) {
		for (var c:Int=0; c<N; c++) {
			for (var r:Int=c; r<M; r++) {
				val v = fval(r, c);
				if (! MathTool.isZero(v)) {
					this.append(r, c, v);
				}
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
	public def initRandom(nzd:Double, fval:(Int, Int)=>Double):SymSparseBuilder(this) {
		val rgen = RandTool.getRandGen();
		val maxdst:Int = ((1.0/nzd) as Int) * 2 - 1;
		var r:Int = rgen.nextInt(maxdst/2);
		var c:Int = 0;
		while (true) {
			if (r < M){
				append(r, c, fval(r, c));
				r+= rgen.nextInt(maxdst) + 1;
			} else {
				c++;
				r -= (M-c);
				if (c>=M) break;
			}
		}
		
		return this;
	}
	
	/**
	 * Initial with random value in specified sparsity.
	 */
	public def initRandom(nzd:Double) : SymSparseBuilder(this) =
		initRandom(nzd, (r:Int,c:Int)=>RandTool.getRandGen().nextDouble());
	
	//=====================================
	/**
	 * Initial symmetric sparse builder using an existing matrix.
	 * @param up       upper triangulor flag
	 * @param src      source matrix.
	 */
	public def init(up:Boolean, src:SparseCSC(M,M)): SymSparseBuilder(this) {
		val ca = src.getStorage();

		for (var c:Int=0; c<M; c++) {
			val srcln = src.ccdata.cLine(c);
			val cpylen = up?srcln.countIndexRangeBefore(c):srcln.countIndexRangeAfter(c, src.M);
			var off :Int = up?srcln.offset:srcln.offset+srcln.length-cpylen;
			for (var i:Int=off; i<off+cpylen; i++) {
				val r = ca.index(i);
				val v = ca.value(i);
				append(r, c, v);
			}
		}
		return this;
	}
	
	//=====================================
	/**
	 * Add new nonzero entry. Keep the new nonzero entry in order
	 */
	@Inline
	public def add(r:Int, c:Int, v:Double) {
		super.insert(r, c, v);
		if (r!=c)
			super.insert(c, r, v);
	}
	
	/**
	 * Append nonzero at the end of nonzero list of the specified column. 
	 */
	@Inline
	public def append(r:Int, c:Int, v:Double) {
		super.append(r, c, v);
		if (r!= c)
			super.append(c, r, v);
	}
	
	/*
	 * Return the value at given row and column; If not found in nonzero list, 0 is returned.
	 */
	public def get(r:Int, c:Int) : Double = findEntry(r,c).value;
	
	/*
	 * Set the entry of given row and column to value, if it is found. Otherwise append
	 * new nonzero entry at the end of nonzero list;
	 */
	@Inline
	public def set(r:Int, c:Int, v:Double) : void {
		super.set(r, c, v);
		if (r != c)
			super.set(c, r, v);
	}
	
	public def reset(r:Int, c:Int) : Boolean = super.remove(r,c);
	//================================
	/**
	 * Copy upper triangular part to lower. The lower part must contain none nonzero entries.
	 * The original builder must be a upper triangular 
	 */
	public def mirrorToLower() {
		var r:Int =0;
		for (var c:Int=1; c<N; c++) {
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
		var r:Int =0;
		for (var c:Int=1; c<N; c++) {
			val nzc = nzcol(c);
			val itr = nzc.iterator();
			while (itr.hasNext()){
				val nz = itr.next();
				r = nz.row;
				super.insert(c, r, nz.value); //Not efficient.
			}
		}
	}
	//================================
	public def checkSymmetric():Boolean {
		var ret:Boolean = true;
		for (var c:Int=0; c<N&&ret; c++)
			for (var r:Int=0; r<M&&ret; r++) {
				ret &= get(r,c) == get(c,r);
			}
				
		return ret;
	}
	//=================================
	public def toSymSparseCSC() : SymSparseCSC(M) {
		val spa = toSparseCSC();
		val bdr = new SymSparseCSC(M, spa.ccdata);
		return bdr as SymSparseCSC(M);
	}
	
	//public def toSparseCSC() : SparseCSC(M,M) = toSparseCSC() as SparseCSC(M,M);
	

	//public def toMatrix():Matrix(M,M) = toSparseCSC() as Matrix(M,M);

	
	//===============================
	public def toString() :String = "Symmetric sparse builder\n"+toString();
}