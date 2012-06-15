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

import x10.io.Console;
import x10.util.ArrayList;
import x10.util.Random;
import x10.util.Timer;
import x10.util.StringBuilder;

import x10.matrix.Matrix;
import x10.matrix.Debug;
import x10.matrix.MathTool;
import x10.matrix.RandTool;
import x10.matrix.DenseMatrix;
import x10.matrix.MatrixBuilder;

public type TriSparseBuilder(bld:TriSparseBuilder)=TriSparseBuilder{self==bld};
public type TriSparseBuilder(m:Int)=TriSparseBuilder{self.M==m};

/**
 * 
 */
public class TriSparseBuilder(M:Int) implements MatrixBuilder {
	
	val builder:SparseCSCBuilder(M);
	val up:Boolean;
	
	public def this(uptri:Boolean, spbd:SparseCSCBuilder{self.M==self.N}) {
		property(spbd.M);
		up = uptri;
		builder = spbd;
	}

	public static def make(up:Boolean, m:Int):TriSparseBuilder(m) {
		val sbd = SparseCSCBuilder.make(m,m) as SparseCSCBuilder{self.M==self.N};
		val bdr = new TriSparseBuilder(up, sbd);
		return bdr as TriSparseBuilder(m);
	}
		
	//====================================
	//=====================================
	static def countHalfNZ(up:Boolean, spa:SparseCSC):Int {
		var nzcnt:Int = 0;
		val ca = spa.getStorage();
		for (var c:Int=0; c<spa.N; c++) {
			val cl = spa.ccdata.cLine(c);
			if (up)
				nzcnt += cl.countIndexRangeBefore(c);
			else
				nzcnt += cl.countIndexRangeAfter(c, spa.M);
		}
		return nzcnt;
	}
	
	public static def countUpperNZ(spa:SparseCSC):Int = countHalfNZ(true, spa);
	public static def countLowerNZ(spa:SparseCSC):Int = countHalfNZ(false, spa);
	
	//=======================================
	
	public static def copyHalf(up:Boolean, srcspa:SparseCSC, dstspa:SparseCSC(srcspa.M,srcspa.N)) {
		val src = srcspa.getStorage();
		val dst = dstspa.getStorage();
		var nzcnt:Int = 0;
		for (var c:Int=0; c<srcspa.N; c++) {
			val srcln = srcspa.ccdata.cLine(c);
			val dstln = dstspa.ccdata.cLine(c);
			val cpylen = up?srcln.countIndexRangeBefore(c):srcln.countIndexRangeAfter(c, srcspa.M);

			dstln.offset = nzcnt;
			if (cpylen > 0) {
				val srcoff = up?srcln.offset:srcln.offset+srcln.length-cpylen;
				Array.copy[Int   ](src.index, srcoff, dst.index, dstln.offset, cpylen);
				Array.copy[Double](src.value, srcoff, dst.value, dstln.offset, cpylen);
			}
			dstln.length = cpylen;
			nzcnt += cpylen;
		}	
		dst.count = nzcnt;
	}
	//=======================================
	//=======================================
	
	/**
	 * Initialize symmetric sparse builder using matrix data generator function.
	 */
	public def init(fval:(Int, Int)=>Double) :TriSparseBuilder(this) {
		for (var c:Int=0; c<builder.N; c++) {
			val stt = up?0:c;
			val end = up?c:M;
			for (var r:Int=stt; r<end; r++) {
				val v = fval(r, c);
				if (! MathTool.isZero(v)) builder.append(r, c, v);
			}
		}
		return this;
	}
	
	/**
	 * Initial triangulor matrix builder with randomized in row indexes, and
	 * values using the given function.
	 * 
	 * @param nzd    nonzero sparsity. Used to computing row index distance between two nonzeros.
	 * @param fval   return a double value using row and column index as inputs. 
	 */
	public def initRandom(halfNZD:Double, fval:(Int, Int)=>Double):TriSparseBuilder(this) {
		val rgen = RandTool.getRandGen();
		val maxdst:Int = ((1.0/halfNZD) as Int) * 2 - 1;
		var r:Int = rgen.nextInt(maxdst/2);
		var c:Int = 0;
		while (true) {
			if (r < M){
				if (up)
					builder.append(c, r, fval(c, r));
				else 
					builder.append(r, c, fval(r, c));
				r+= rgen.nextInt(maxdst) + 1;
			} else {
				c++;
				r -= (M - c);
				if (c>=M) break;
			}
		}
		
		return this;
	}
	
	public def initRandom(halfNZD:Double):TriSparseBuilder(this) =
		initRandom(halfNZD, (Int,Int)=>(RandTool.getRandGen().nextDouble()));

	/**
	 * Initialize builder using existing sparse matrix. 
	 * This is not best way to creat triangulor matrix.  It is more fast using
	 * copyHalf method.
	 */
	public def init(src:SparseCSC{self.M==self.N}) : TriSparseBuilder(this) {
		val srcca = src.getStorage();
		for (var c:Int=0; c<src.N; c++) {
			val srcln = src.ccdata.cLine(c);
			val cpylen = up?srcln.countIndexRangeBefore(c):srcln.countIndexRangeAfter(c, src.M);

			var off :Int = up?srcln.offset:srcln.offset+srcln.length-cpylen;
			for (var idx:Int=off; idx<off+cpylen; idx++) {
				val r = srcca.index(idx);
				val v = srcca.value(idx);
				builder.append(r, c, v);
			}
		}	
		return this;
	}
	
	//====================================
	/**
	 * Return the value at given row and column; If not found in nonzero list, 0 is returned.
	 */
	public def get(r:Int, c:Int) : Double {
		if (up) {
			if ( r <= c)
				return builder.findEntry(r,c).value;
			else
				return 0.0;
		} else {
			if ( r >= c)
				return builder.findEntry(r,c).value;
			else
				return 0.0;
		}
	}
	
	/**
	 * Set the entry of given row and column to value, if it is found. Otherwise append
	 * new nonzero entry at the end of nonzero list;
	 */
	public def set(r:Int, c:Int, v:Double) : void {
		if ((up && r <= c) || (up==false && r >= c))
			builder.set(r, c, v);
		else {
			Debug.flushln("Error is in setting triangular matrix");
		}
	}
	
	public def reset(r:Int, c:Int) : Boolean {
		if ((up && r <= c) || (up==false && r >= c))
			return builder.reset(r, c);
		else {
			return false;
		}
	}

	//====================================
	
	public def toSparseCSC():SparseCSC(M,M) = builder.toSparseCSC() as SparseCSC(M,M);
	
	public def toSparseCSC(spa:SparseCSC(M,M)){
		builder.toSparseCSC(spa);
	}

	public def toMatrix():Matrix(M,M) = toSparseCSC() as Matrix(M,M);
	
	//===============================
	public def toString() = builder.toString();
	public def print(msg:String) { builder.print(msg);}
	public def print() { builder.print();}
	
	
}
