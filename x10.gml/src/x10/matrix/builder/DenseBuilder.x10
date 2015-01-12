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
import x10.matrix.util.Debug;
import x10.matrix.util.RandTool;

public type DenseBuilder(blder:DenseBuilder)=DenseBuilder{self==blder};
public type DenseBuilder(m:Long,n:Long)=DenseBuilder{self.M==m,self.N==n};

/**
 * Initialize a matrix in dense format.
 */
public class DenseBuilder(M:Long, N:Long) implements MatrixBuilder {
	public val dense:DenseMatrix;
	
	/**
	 * Creat dense matrix builder and using specified dense as output.
	 * @param  den    Output dense matrix instance
	 */
	public def this(den:DenseMatrix) {
		property(den.M,den.N);
		dense = den;
	}
	
	/**
	 * Creat dense builder and dense matrix
	 * @param m   rows, leading dimension
	 * @param n   columns
	 */
	public static def make(m:Long, n:Long): DenseBuilder(m,n) {
		val bdr = new DenseBuilder(DenseMatrix.make(m, n));
		return bdr as DenseBuilder(m,n);
	}
	
	/**
	 * Initialize dense builder using given dense matrix.
	 * @param rowOff    the starting row in target matrix
	 * @param colOff    the starting column
	 * @param srcden    source matrix
	 * 
	 */
	public def init(rowOff:Long, colOff:Long, srcden:DenseMatrix) : DenseBuilder(this) {
		Debug.assure(rowOff+srcden.M<=dense.M, "Dense builder cannot using given matrix to initialize. Row overflow");
		Debug.assure(colOff+srcden.N<=dense.N, "Dense builder cannot using given matrix to initialize. Column overflow");
		var stt:Long = rowOff;
		for (var c:Long=colOff; c<colOff+srcden.N; c++, stt+= dense.M)
			Rail.copy[Double](srcden.d, 0, dense.d, stt, srcden.M);
		return this;
	}
	
	public def init(srcden:DenseMatrix) = init(0, 0, srcden);
	
	/**
	 * Initial dense matrix with initial function.
	 */
	public def init(initFunc:(Long,Long)=>Double):DenseBuilder(this) {
		var idx:Long=0;
		for (var c:Long=0; c<dense.N; c++ )
			for (var r:Long=0; r<dense.M; r++, idx++)
				dense.d(idx) = initFunc(r, c);
		return this;
	}
	
	/**
	 * Initial dense matrix with initial function and location in dense matrix is generated randomly.
	 * @param nzDensity    nonzero sparsity.
	 * @param initFunc     nonzero value generating function.
	 */
	public def initRandom(nzDensity:Double, initFunc:(Long,Long)=>Double):DenseBuilder(this) {
		val maxdst:Long = ((1.0/nzDensity) as Long) * 2 - 1;
		var idx:Long= RandTool.nextLong(maxdst/2);
		for (; idx<dense.M*dense.N; idx+=RandTool.nextLong(maxdst)+1){
			dense.d(idx) = initFunc(idx%dense.M, idx/dense.M);
		}
		return this;
	}

	
	public def initRandom(nzDensity:Double): DenseBuilder(this) =
		initRandom(nzDensity, (Long,Long)=>RandTool.getRandGen().nextDouble());
	
	public def initTransposeFrom(src:DenseMatrix(N,M)):DenseBuilder(this) {
		var src_idx:Long =0;
		var dst_idx:Long =0;
		//Need to be more efficient
		//Possible idea is to tranpose or copy small block each time.
		for (var c:Long=0; c < this.N; c++) {
			dst_idx = c;
			for (var r:Long=0; r < this.M; r++, dst_idx+=M, src_idx++) {
				this.dense.d(dst_idx) = src.d(src_idx);
			}
		}
		return this;
	}
	
	public def set(r:Long, c:Long, dv:Double) : void {
		dense(r, c) = dv;
	}
	
	public def reset(r:Long, c:Long) : Boolean {
		dense(r, c) =0.0;
		return true;
	}

	public def toMatrix():Matrix = dense as Matrix;
	public def toDense():DenseMatrix = dense;
	//public def toTriDense() : TriDense = dense as TriDense;
	//public def toSymDense() : SymDense = dense as SymDense;
}
