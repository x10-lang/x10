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

package x10.matrix.builder;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.TriDense;
import x10.matrix.Debug;
import x10.matrix.RandTool;
import x10.matrix.MathTool;

public type TriDenseBuilder(blder:TriDenseBuilder)=TriDenseBuilder{self==blder};
public type TriDenseBuilder(m:Int)=TriDenseBuilder{self.M==m,self.N==m};

/**
 * Builder/Initializer of symmetric dense matrix.
 */
public class TriDenseBuilder extends DenseBuilder{self.M==self.N} implements MatrixBuilder {
	
	val upper:Boolean;
	public def this(tri:TriDense) {
		super(tri as DenseMatrix{self.M==self.N});
		upper=tri.upper;
	}
	
	public def this(up:Boolean, den:DenseMatrix{self.M==self.N}) {
		super(den);
		upper=up;
	}
	
	/**
	 * Creat dense builder and dense matrix
	 * @param m   rows, leading dimension
	 * @param n   columns
	 */
	public static def make(up:Boolean, m:Int): TriDenseBuilder(m) {
		val bdr = new TriDenseBuilder(TriDense.make(up, m));
		return bdr as TriDenseBuilder(m);
	}
	
	//==============================================
	
	/**
	 * Initial triangular dense matrix with initial function.
	 */
	public def initUpper(initFunc:(Int,Int)=>Double):TriDenseBuilder(this) {
		var i:Int =0;
		for (var c:Int=0; c<this.N; c++) {
			var r:Int=0;
			for (; r<=c; r++, i++ )   dense.d(i) = initFunc(r, c);
			for (;r<this.M; r++, i++) dense.d(i) = 0.0;
		}
		return this;
	}
		
	public def initLower(initFunc:(Int,Int)=>Double):TriDenseBuilder(this) {
		var i:Int =0;
		for (var c:Int=0; c<this.N; c++ ) {
			var r:Int=0;
			for (; r<c; r++, i++ )    dense.d(i) = 0.0;
			for (;r<this.M; r++, i++) dense.d(i) = initFunc(r, c);
		}
		return this;
	}
		
	public def init(initFunc:(Int,Int)=>Double):TriDenseBuilder(this) =
		upper?initUpper(initFunc):initLower(initFunc);

	
	/**
	 * Initial symmetric dense matrix with initial function and location in dense matrix is generated randomly.
	 * @param nzDensity    nonzero sparsity.
	 * @param initFunc     nonzero value generating function.
	 */
	public def initUpperRandom(nzDensity:Double, initFunc:(Int,Int)=>Double):TriDenseBuilder(this) {
		val maxdst:Int = ((1.0/nzDensity) as Int) * 2 - 1;
		var i:Int= RandTool.nextInt(maxdst/2);
		var stt:Int=0;
		for (var c:Int=0; c<this.N; c++, stt+=dense.M, i+=(this.M-c)  ) {
			var r:Int = i - stt;
			for (;r<=c; i+= RandTool.nextInt(maxdst)+1, r=i-stt) 
				dense.d(i) = initFunc(r, c);
		}
		return this;
	}

	public def initLowerRandom(nzDensity:Double, initFunc:(Int,Int)=>Double):TriDenseBuilder(this) {
		val maxdst:Int = ((1.0/nzDensity) as Int) * 2 - 1;
		var i:Int= RandTool.nextInt(maxdst/2);
		var stt:Int=0;
		for (var c:Int=0; c<this.N; c++, stt+=dense.M, i+=c  ) {
			var r:Int = i - stt;
			for (;r<this.M; i+= RandTool.nextInt(maxdst)+1, r=i-stt) 
				dense.d(i) = initFunc(r, c);
		}
		return this;
	}
	
	public def initRandom(nzDensity:Double, initFunc:(Int,Int)=>Double):TriDenseBuilder(this) =
		upper?initUpperRandom(nzDensity, initFunc):initLowerRandom(nzDensity, initFunc);

	public def initRandom(nzDensity:Double): DenseBuilder(this) =
		initRandom(nzDensity, (Int,Int)=>RandTool.getRandGen().nextDouble());
	
	//===============================================
	public def set(r:Int, c:Int, dv:Double) : void {
		if ((upper && r<=c)||(upper==false && r>=c))
			dense(r, c) = dv;
		else {
			Debug.flushln("Warning seting triangular zero part");
		}
	}
	
	public def reset(r:Int, c:Int) : Boolean {
		dense(r, c) =0.0;
		return true;
	}
	//===============================================
	/**
	 * copy from upper or lower triangular and its mirror part.
	 */
	public def initFrom(src:DenseMatrix) {
		if (upper)
			copyUpper(src, this.dense as DenseMatrix(src.N));
		else
			copyLower(src, this.dense as DenseMatrix(src.M));
	}
	
	public static def copyUpper(src:DenseMatrix, dst:DenseMatrix(src.N)) {
		var srcoff:Int=0;
		var dstoff:Int=0;
		for (var len:Int=1; len<=src.N; len++, srcoff+=src.M, dstoff+=dst.M) {
			Array.copy[Double](src.d, srcoff, dst.d, dstoff, len);
		}
	}
	
	public static def copyLower(src:DenseMatrix, dst:DenseMatrix(src.M)) {
		var srcoff:Int=0;
		var dstoff:Int=0;
		for (var len:Int=src.M; len>0; len--, srcoff+=src.M+1, dstoff+=dst.M+1) {
			Array.copy[Double](src.d, srcoff, dst.d, dstoff, len);
		}		
	}
	
	
	//===============================================
	public def isUpperZero():Boolean {
		var ret:Boolean = true;
		for (var c:Int=0; c<M&&ret; c++)
			for (var r:Int=0; r<c&&ret; r++)
				ret &= MathTool.isZero(dense(r, c));
		return ret;
	}
	
	public def isLowerZero():Boolean {
		var ret:Boolean = true;
		for (var c:Int=0; c<M&&ret; c++)
			for (var r:Int=c+1; r<M&&ret; r++)
				ret &= MathTool.isZero(dense(r, c));
		return ret;
	}
	//===============================================
	/**
	 * Convert to symmetric dense builder use the same memory space.
	 * 
	 */
	public def toSymDenseBuilder():SymDenseBuilder(M) {
		val symbld = new SymDenseBuilder(dense as DenseMatrix{self.M==self.N});
		if (upper)
			symbld.mirrorToLower();
		else
			symbld.mirrorToUpper();
		return symbld as SymDenseBuilder(M);
	}
	
	public def toMatrix():Matrix = dense as Matrix;
	public def toTriDense() : TriDense(M) {
		val tri = new TriDense(upper, M, dense.d);
		return tri as TriDense(M);
	}
	
	
}