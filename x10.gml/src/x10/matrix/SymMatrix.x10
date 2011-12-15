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

package x10.matrix;

import x10.io.Console;
import x10.util.Random;
import x10.util.Timer;

//import x10.matrix.blas.DriverBLAS;

public type SymMatrix(m:Int, n:Int)=SymMatrix{m==n, self.M==m, m==n};
public type SymMatrix(m:Int)=SymMatrix{self.M==m,self.N==m};
public type SymMatrix(C:SymMatrix)=SymMatrix{self==C};
public type SymMatrix(C:Matrix)=SymMatrix{self==C};


/**
 * The symmetric matrix in X10 stores the lower part of matrix data in column based.
 * Currently, there is no distributed structure for symmetric matrix. 
 */
public class SymMatrix extends Matrix{self.M==self.N} {
	
	//================================================================
	// Base data structure
	//================================================================
	public val d:Array[Double](1){rail};
	
	//================================================================
	// Constructor, maker, and clone method
	//================================================================	
	public def this(m:Int, x:Array[Double](1){rail}) : SymMatrix(m){
		super(m,m);
		this.d = x;
		Debug.assure(m*(m+1)/2 < x.size, "Storage of array cannot hold all matrix data"); 
	}
	
	//----------------------------------------------------------------
	public static def make(m:Int):SymMatrix(m) {
		val x = new Array[Double](m*(m+1));
		return new SymMatrix(m, x);
	}

	public static def make(src:SymMatrix) {
		val m = src.M;
		val newd = new Array[Double](m*(m+1)/2);
		Array.copy(src.d, newd);
		return new SymMatrix(src.M, newd);
	}
		
	public def clone():SymMatrix(M,N){
		val nd = new Array[Double](this.d) as Array[Double](1){rail};
		val nm = new SymMatrix(M, nd);
		return nm as SymMatrix(M,N);
	}
	
	public  def alloc(m:Int, n:Int):SymMatrix(m,n) {
		Debug.assure(m==n);
		val x = new Array[Double](m*(m+1)/2);
		val nm = new SymMatrix(m, x);
		return nm as SymMatrix(m,n);
	}
	
	public def alloc() = alloc(this.M, this.M);
	
	//======================================================================
	// Data copy and reset 
	//======================================================================
	
	public def copyTo(dm:DenseMatrix(M,N)):void {
		var srcidx:Int=0;
		var dstidx:Int=0;
		var gap:Int = 0;
		
		for (var len:Int=M; len > 0; dstidx+=M+1, srcidx+=len, len--, gap++) {
			Array.copy(this.d, srcidx, dm.d, dstidx, len);
			var j:Int = dstidx+M;
			for (var i:Int=1; i<len; i++, j+=M)
				dm.d(j) = this.d(srcidx+i);		
		}
	}
	
	public def toDense():DenseMatrix(M,N) {
		val dm = DenseMatrix.make(M,N);
		copyTo(dm);
		return dm;
	}
	
	public  def reset():void {
		for (var i:Int=0; i<d.size; i++) d(i)=0.0;
	}	
	
	//-------------------------------------------------------------------
	// Data initialization
	//-------------------------------------------------------------------
	/**
	 * Initialize all elements of the symmetric matrix with a constant value.
	 * @param  iv 	the constant value
	 */	
	public def init(iv:Double): SymMatrix(this) {
		for (var i:Int=0; i<M*(M+1)/2; i++)	this.d(i) = iv;
		return this;
	}

	/**
	 * Initialize all elements of the dense matrix with random 
	 * values between 0.0 and 1.0.
	 */	
	public def initRandom(): SymMatrix(this) {
		val rgen = RandTool.getRandGen();
		//val ll = M*N / 100;
		for (var i:Int=0; i<M*(M+1)/2; i++) {
			this.d(i) = rgen.nextDouble();
		}
		return this;
	}
	
	/**
	 * Initialize all elements of the dense matrix with random 
	 * values between the specified range.
	 * 
	 * @param lb	lower bound of random values
	 * @param up	upper bound of random values
	 */	
	public def initRandom(lb:Int, ub:Int): SymMatrix(this) {
		val len = Math.abs(ub-lb)+1;
		val rgen = RandTool.getRandGen();
		//val ll = M*N / 100;
		for (var i:Int=0; i<M*(M+1)/2; i++) {
			this.d(i) = rgen.nextInt(len)+lb;
		}
		return this;
	}
	
	//======================================================================
	// Data access and set
	//======================================================================
	
	public  operator this(x:Int, y:Int):Double {
		if (y < x ) {
			return this.d(x+y*(2*M-y-1)/2);
		} else {
			return this.d(y+x*(2*M-x-1)/2); 
		}
	}
	
	public  operator this(x:Int,y:Int) = (v:Double):Double {
		if (y < x) {
			this.d(x+y*(2*M-y-1)/2) = v;
		} else {
			this.d(y+x*(2*M-x-1)/2) = v; 
		}
		return v;
	}	

	//=====================================================================
	// Cellwise operations
	//=====================================================================
	public  def scale(a:Double):SymMatrix(this)  {
		for (var i:Int=0; i<M*(M+1)/2; i++) 
			this.d(i) = this.d(i) * a;
		return this;
	}
	
	public def norm(x:Matrix(M,M)):Double {
		var nv:Double = 0.0;
		var idx:Int = 0;
		for (var c:Int=0; c<N; c++) {
			nv += this.d(idx)*x(c,c); 
			idx++;
			for (var r:Int=c+1; r<M; r++, idx++) {
				nv += this.d(idx)*(x(c,r)+x(r,c));
			}
		}
		return nv;
	}
	
	public def sum():Double {
		var tt:Double = 0.0;
		var idx:Int = 0;
		for (var c:Int=0; c<M; c++) {
			tt += this.d(idx);
			idx++;
			for (var r:Int=c+1; r<M; r++, idx++)
				tt += this.d(idx) * 2;
		}
		return tt;
	}
	//------------------------
	// Add operation
	//------------------------
	public def cellAdd(v:Double):SymMatrix(this) {
		for (var i:Int=0; i<M*(M+1)/2; i++) 
			this.d(i) += v;
		return this;
	}
	
	public def cellAdd(x:Matrix(M,N)):SymMatrix(this)   {
		throw new UnsupportedOperationException("Cell-wise addition does not support using SymMatrix as output matrix");
	}
	
	public def cellAdd(x:SymMatrix(M)):SymMatrix(this) {
		for (var i:Int=0; i<M*(M+1)/2; i++) 
			this.d(i) += x.d(i);
		return this;
	}	
	
	public def cellAddTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var src_idx:Int=0;
		var dst_idx:Int=0;
		for (var c:Int=0; c<M; c++) {
			dst_idx += c;
			var col_idx:Int = dst_idx+M;

			x.d(dst_idx) += this.d(src_idx);
			dst_idx++; src_idx++;
			for (var r:Int=c+1; r<M; r++, col_idx+=M, dst_idx++, src_idx++) {
				x.d(col_idx) += this.d(src_idx);
				x.d(dst_idx) += this.d(src_idx);
			}
		}
		return x;
	}
	
	//----------------------------------
	// Cell-wise matrix multiplication
	//----------------------------------
	
	public def cellSub(v:Double):SymMatrix(this) {
		for (var i:Int=0; i<M*(M+1)/2; i++) 
			this.d(i) -= v;
		return this;
	}

	public def cellSubFrom(v:Double):SymMatrix(this) {
		for (var i:Int=0; i<M*(M+1)/2; i++) 
			this.d(i) = v-this.d(i);
		return this;
	}
	
	public def cellSub(x:Matrix(M,N)):SymMatrix(this)   {
		throw new UnsupportedOperationException("Cell-wise subtract does not support using SymMatrix as output matrix");
	}
	
	public def cellSub(x:SymMatrix(M)):SymMatrix(this) {
		for (var i:Int=0; i<M*(M+1)/2; i++) 
			this.d(i) -= x.d(i);
		return this;
	}	
	
	/**
	 * x = x - this;
	 */
	public def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var src_idx:Int=0;
		var dst_idx:Int=0;
		for (var c:Int=0; c<M; c++) {
			dst_idx += c;
			var col_idx:Int = dst_idx+M;

			x.d(dst_idx) -= this.d(src_idx);
			dst_idx++; src_idx++;
			for (var r:Int=c+1; r<M; r++, col_idx+=M, dst_idx++, src_idx++) {
				x.d(col_idx) -= this.d(src_idx);
				x.d(dst_idx) -= this.d(src_idx);
			}
		}
		return x;
	}
	
	//----------------------------------
	// Cell-wise matrix multiplication
	//----------------------------------
	public def cellMult(v:Double):SymMatrix(this) {
		for (var i:Int=0; i<M*(M+1)/2; i++) 
			this.d(i) *= v;
		return this;
	}
	
	public def cellMult(x:Matrix(M,N)):SymMatrix(this)   {
		throw new UnsupportedOperationException("Cell-wise multiply does not support using SymMatrix as output matrix");
	}
	
	public def cellMult(x:SymMatrix(M)):SymMatrix(this) {
		for (var i:Int=0; i<M*(M+1)/2; i++) 
			this.d(i) *= x.d(i);
		return this;
	}	
	
	public def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var src_idx:Int=0;
		var dst_idx:Int=0;
		for (var c:Int=0; c<M; c++) {
			dst_idx += c;
			var col_idx:Int = dst_idx+M;

			x.d(dst_idx) *= this.d(src_idx);
			dst_idx++; src_idx++;
			for (var r:Int=c+1; r<M; r++, col_idx+=M, dst_idx++, src_idx++) {
				x.d(col_idx) *= this.d(src_idx);
				x.d(dst_idx) *= this.d(src_idx);
			}
		}
		return x;
	}
	//-------------------------
	// Cellwise division
	//-------------------------
	public def cellDiv(v:Double):SymMatrix(this) {
		for (var i:Int=0; i<M*(M+1)/2; i++) 
			this.d(i) /= v;
		return this;
	}
	public def cellDivBy(v:Double):SymMatrix(this) {
		for (var i:Int=0; i<M*(M+1)/2; i++) 
			this.d(i) = v/this.d(i);
		return this;
	}	
	
	public def cellDiv(x:Matrix(M,N)):SymMatrix(this)   {
		throw new UnsupportedOperationException("Cell-wise division does not support using SymMatrix as output matrix");
	}
	
	public def cellDiv(x:SymMatrix(M)):SymMatrix(this) {
		for (var i:Int=0; i<M*(M+1)/2; i++) 
			this.d(i) /= x.d(i);
		return this;
	}	
	
	public def cellDivBy(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var src_idx:Int=0;
		var dst_idx:Int=0;
		for (var c:Int=0; c<M; c++) {
			dst_idx += c;
			var col_idx:Int = dst_idx+M;

			x.d(dst_idx) /= this.d(src_idx);
			dst_idx++; src_idx++;
			for (var r:Int=c+1; r<M; r++, col_idx+=M, dst_idx++, src_idx++) {
				x.d(col_idx) /= this.d(src_idx);
				x.d(dst_idx) /= this.d(src_idx);
			}
		}
		return x;
	}
	
	//================================================================
	// Matrix multiply operations: self this<- op(A)*op(B) + (plus?1:0) * C
	// Default is using BLAS driver
	//================================================================
	public def mult(A:Matrix(this.M), B:Matrix(A.N,this.N),	plus:Boolean):SymMatrix(this) {
		throw new UnsupportedOperationException("Matrix multiply does not support using SymMatrix as output matrix");
	}
	
	public def transMult(A:Matrix{self.N==this.M}, B:Matrix(A.M,this.N), plus:Boolean):SymMatrix(this) {
		throw new UnsupportedOperationException("Matrix transposed multiply does not support using SymMatrix as output matrix");
	}
			
	public def multTrans(A:Matrix(this.M), B:Matrix(this.N, A.N), plus:Boolean):SymMatrix(this) {
		throw new UnsupportedOperationException("Matrix multiply transposed does not support using SymMatrix as output matrix");
	}

	//==================================================================
	// Operator
	//==================================================================
	public operator - this            = this.clone().scale(-1.0) as SymMatrix(M,N);
	public operator this + (v:Double):SymMatrix(M,N) = this.clone().cellAdd(v) as SymMatrix(M,N);
	public operator (v:Double) + this:SymMatrix(M,N) = this + v;

	public operator this - (v:Double):SymMatrix(M,N) = this.clone().cellSub(v) as SymMatrix(M,N);
	public operator (v:Double) - this:SymMatrix(M,N) = this.clone().cellSubFrom(v) as SymMatrix(M,N);
	public operator this / (v:Double):SymMatrix(M,N) = this.clone().cellDiv(v) as SymMatrix(M,N);
	public operator (v:Double) / this:SymMatrix(M,N) = this.clone().cellDivBy(v) as SymMatrix(M,N);
	public operator this * (alpha:Double):SymMatrix(M,N) = this.clone().scale(alpha) as SymMatrix(M,N);
	public operator this * (alpha:Int):SymMatrix(M,N)    = this * (alpha as Double);
	public operator (alpha:Double) * this = this * alpha;
	public operator (alpha:Int) * this    = this * alpha;
	
	
	public operator this + (that:SymMatrix(M)):SymMatrix(M,N) = this.clone().cellAdd(that);
	public operator this - (that:SymMatrix(M)):SymMatrix(M,N) = this.clone().cellSub(that);
	public operator this * (that:SymMatrix(M)):SymMatrix(M,N) = this.clone().cellMult(that);
	public operator this / (that:SymMatrix(M)):SymMatrix(M,N) = this.clone().cellDiv(that);

	/**
	 * Operation with dense matrix and store result in dense format
	 */
	public operator this + (that:DenseMatrix(M,N)):DenseMatrix(M,N) = this.cellAddTo(that.clone() as DenseMatrix(M,N));
	public operator this - (that:DenseMatrix(M,N)):DenseMatrix(M,N) = this.toDense().cellSub(that) as DenseMatrix(M,N);
	public operator this * (that:DenseMatrix(M,N)):DenseMatrix(M,N) = this.cellMultTo(that.clone()) as DenseMatrix(M,N);
	public operator this / (that:DenseMatrix(M,N)):DenseMatrix(M,N) = this.toDense().cellDiv(that) as DenseMatrix(M,N);
	
	
	public operator (that:DenseMatrix(M,N)) + this = this + that;
	public operator (that:DenseMatrix(M,N)) - this = this.cellSubFrom(that.clone());
	public operator (that:DenseMatrix(M,N)) * this = this * that;
	public operator (that:DenseMatrix(M,N)) / this = this.cellDivBy(that.clone()) as DenseMatrix(M,N);
	
	
	//=======================================================
	// Utils
	//=======================================================
	public def likeMe(m:Matrix):Boolean {
		if ((m instanceof SymMatrix) && m.M==M && m.N==N) return true;
		return false;
	}
	
	public static def test(dm:DenseMatrix):Boolean {
		if (dm.M != dm.N) return false;
		
		for (var c:Int=0; c<dm.M; c++)
			for (var r:Int=c+1; r<dm.M; r++)
				if (dm(r,c) != dm(c,r)) return false;
		return true;
	}
	
	public def toString():String {
		var idx:Int=0;
		var outstr:String ="--------- Symmetric Matrix "+M+" x "+N+" lower part data ---------\n";
		for (var r:Int=0; r<M; r++) {
			var rowstr:String=r.toString()+"\t[ ";
			for (var c:Int=0; c<=r; c++)
				rowstr += this.d(idx++).toString()+" ";
			rowstr +="]\n";
			outstr += rowstr;
		}
		outstr += "---------------------------------------\n";
		return outstr; 	
	}
	
	public def print(msg:String): void {
		Console.OUT.println(msg+"\n"+toString());
	}
	
	public def print():void {
		Console.OUT.println(toString());
	}
	

}
