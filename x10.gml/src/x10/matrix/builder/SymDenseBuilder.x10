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

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.SymDense;
import x10.matrix.util.Debug;
import x10.matrix.util.RandTool;

public type SymDenseBuilder(blder:SymDenseBuilder)=SymDenseBuilder{self==blder};
public type SymDenseBuilder(m:Long)=SymDenseBuilder{self.M==m,self.N==m};

/**
 * Builder/Initializer of symmetric dense matrix.
 */
public class SymDenseBuilder extends DenseBuilder{self.M==self.N} implements MatrixBuilder {
	
	public def this(sym:SymDense) {
		super(sym as DenseMatrix{self.M==self.N});
	}
	
	public def this(den:DenseMatrix{self.M==self.N}) {
		super(den);
	}
	
	/**
	 * Creat dense builder and dense matrix
	 * @param m   rows, leading dimension
	 * @param n   columns
	 */
	public static def make(m:Long): SymDenseBuilder(m) {
		val bdr = new SymDenseBuilder(SymDense.make(m));
		return bdr as SymDenseBuilder(m);
	}
	

	/**
	 * Initial dense matrix with initial function.
	 */
	public def init(initFunc:(Long,Long)=>Double):SymDenseBuilder(this) {
		var stt:Long=0;
		for (var c:Long=0; c<this.N; c++, stt+=1+dense.M ) {
			var i:Long = stt;
			var j:Long = stt+dense.M;
			dense.d(i++) = initFunc(c, c);
			for (var r:Long=c+1; r<this.M; r++, i++, j += dense.M) 
				dense.d(i) = dense.d(j) = initFunc(r, c);
		}
		return this;
	}

    // replicated from superclass to workaround xlC bug with using & itables
	public def init(srcden:DenseMatrix) = init(0, 0, srcden);
	public def init(rowOff:Long, colOff:Long, srcden:DenseMatrix) : DenseBuilder(this) {
		Debug.assure(rowOff+srcden.M<=dense.M, "Dense builder cannot using given matrix to initialize. Row overflow");
		Debug.assure(colOff+srcden.N<=dense.N, "Dense builder cannot using given matrix to initialize. Column overflow");
		var stt:Long = rowOff;
		for (var c:Long=colOff; c<colOff+srcden.N; c++, stt+= dense.M)
			Rail.copy[Double](srcden.d, 0, dense.d, stt, srcden.M);
		return this;
	}
	
	/**
	 * Initial symmetric dense matrix with initial function and location in dense matrix is generated randomly.
	 * @param nzDensity    nonzero sparsity.
	 * @param initFunc     nonzero value generating function.
	 */
	public def initRandom(nzDensity:Double, initFunc:(Long,Long)=>Double):SymDenseBuilder(this) {
		val maxdst:Long = ((1.0/nzDensity) as Int) * 2 - 1;
		var i:Long= RandTool.nextLong(maxdst/2);
		var stt:Long=0;
		for (var c:Long=0; c<this.N; c++, stt+=dense.M, i+=c) {
			var r:Long = i - stt;
			var j:Long = r*dense.M+c;

			//if (r==c) dense.d(i) = initFunc(c, c);
			for (;r<this.M; i+= RandTool.nextLong(maxdst)+1, r=i-stt, j=r*dense.M+c) {
				dense.d(i) = dense.d(j) = initFunc(r, c);
			}
		}
		return this;
	}

	
	public def initRandom(nzDensity:Double): SymDenseBuilder(this) =
		initRandom(nzDensity, (Long,Long)=>RandTool.getRandGen().nextDouble());
	

	public def set(r:Long, c:Long, dv:Double) : void {
		dense(r, c) = dv;
		dense(c, r) = dv;
	}
	
	public def reset(r:Long, c:Long) : Boolean {
		dense(r, c) =0.0;
		dense(c, r) =0.0;
		return true;
	}

	/**
	 * copy from upper or lower triangular and its mirror part.
	 */
	public def init(upper:Boolean, src:DenseMatrix):SymDenseBuilder(this) {
		if (upper) {
			TriDenseBuilder.copyUpper(src, this.dense as DenseMatrix(src.N));
			mirrorToLower();
		} else {
			TriDenseBuilder.copyLower(src, this.dense as DenseMatrix(src.M));
			mirrorToUpper();
		}
		return this;
	}
	/**
	 * Copy lower triangular part to upper
	 */
	public def mirrorToUpper() {
		var i:Long=1;
		var j:Long=M;
		for (var c:Long=0; c<M; c++, i+=c+1, j=i+M-1) {
			for (var r:Long=c+1; r<M; r++, i++, j+=M) 
				dense.d(j)= dense.d(i);
		}
	}
	
	/**
	 * Copy upper triangular part to lower
	 */
	public def mirrorToLower() {
		var i:Long=M;
		var j:Long=1;
		for (var c:Long=1; c<M; c++, i=c*M, j=c) {
			for (var r:Long=0; r<c; r++, i++, j+=M) 
				dense.d(j)= dense.d(i);
		}
	}
	


	public def checkSymmetric():Boolean =
		SymDense.test(dense);
	
	public def toSymDense() : SymDense(M) {
		val sym = new SymDense(M, dense.d);
		return sym as SymDense(M);
	}
	
	public def toMatrix():Matrix = dense as Matrix;

	
}
