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
import x10.util.StringBuilder;

import x10.matrix.blas.DenseMatrixBLAS;

public type SymMatrix(m:Int, n:Int)=SymMatrix{m==n, self.M==m, self.N==n};
public type SymMatrix(m:Int)=SymMatrix{self.M==m,self.N==m};
public type SymMatrix(C:SymMatrix)=SymMatrix{self==C};
public type SymMatrix(C:Matrix)=SymMatrix{self==C};


/**
 * The symmetric matrix in X10 stores the lower part of matrix data in column based.
 * However, the full matrix storage needs to be allocated, which compiles with
 * BLAS symmetric matrix.
 * <p>
 * For distributed symmetric matrix, please check DistSymBuilder and DistBlockMatrix.
 */
public class SymMatrix extends Matrix{self.M==self.N} {
	
	//================================================================
	// Base data structure
	//================================================================
	public val d:Array[Double](1){rail};
	
	//================================================================
	// Constructor, maker, and clone method
	//================================================================	
	public def this(n:Int, x:Array[Double](1){rail}) : SymMatrix(n){
		super(n,n);
		this.d = x;
		Debug.assure( n*n <= x.size, "Storage of array cannot hold all matrix data"); 
	}
	
	//----------------------------------------------------------------
	public static def make(n:Int):SymMatrix(n) {
		val x = new Array[Double](n*n);
		return new SymMatrix(n, x);
	}

	public static def make(src:SymMatrix) {
		val n = src.N;
		val newd = new Array[Double](n*n);
		Array.copy(src.d, newd);
		return new SymMatrix(n, newd);
	}
	
	/**
	 * Create symmetrix object from dense matrix's lower triangular  part
	 */
	public static def make(src:DenseMatrix):SymMatrix(src.N) {
		val nd = new Array[Double](src.N*src.N);
		var colstt:Int=0;
		for (var len:Int=src.N; len>0; len--, colstt+=src.M+1)
			Array.copy(src.d, colstt, nd, colstt, len);
		return new SymMatrix(src.N, nd);
	}
	
	public def clone():SymMatrix(M,N){
		val nd = new Array[Double](this.d) as Array[Double](1){rail};
		val nm = new SymMatrix(M, nd);
		return nm as SymMatrix(M,N);
	}
	
	public  def alloc(m:Int, n:Int):SymMatrix(m,n) {
		Debug.assure(m==n);
		val x = new Array[Double](m*m);
		val nm = new SymMatrix(m, x);
		return nm as SymMatrix(m,n);
	}
	
	public def alloc() = alloc(this.M, this.M);
	
	//======================================================================
	// Data copy and reset 
	//======================================================================
	
	public def copyTo(dm:DenseMatrix(M,N)):void {
		var colstt:Int=0;
		for (var len:Int=N; len > 0; len--, colstt+=M+1) {
			Array.copy(this.d, colstt, dm.d, colstt, len);		
			var j:Int = colstt+M;
			for (var i:Int=colstt+1; i<colstt+len; i++, j+=M)
				dm.d(j) = this.d(i);		
		}
	}
	
	public def copyTo(smat:SymMatrix(N)): void {
		var colstt:Int=0;
		for (var len:Int=N; len > 0; len--, colstt+=M+1) {
			Array.copy(this.d, colstt, smat.d, colstt, len);	
		}
	}

	public def copyTo(mat:Matrix(M,N)) : void {
		if (mat instanceof DenseMatrix) {
			copyTo(mat as DenseMatrix(M,N));
		} else if (likeMe(mat)) {
			copyTo(mat as SymMatrix(N));
		} else {
			Debug.exit("CopyTo: Target matrix type is not compatible");
		}
	}
	
	//--------------------------------------------------
	public def toDense():DenseMatrix(M,N) {
		val dm = DenseMatrix.make(M,N);
		copyTo(dm);
		return dm;
	}
	
	public def castToDense():DenseMatrix(M,N) {
		var colstt:Int=0;
		for (var len:Int=N; len > 0; len--, colstt+=M+1) {
			var j:Int = colstt+M;
			for (var i:Int=colstt+1; i<colstt+len; i++, j+=M)
				this.d(j) = this.d(i);		
		}
		return new DenseMatrix(M, N, this.d);
	}

	/**
	 * Reset lower part of triangular matrix. No touch on upper part.
	 */
	public  def reset():void {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)
				this.d(i)=0.0;
	}	
	
	//-------------------------------------------------------------------
	// Data initialization
	//-------------------------------------------------------------------
	/**
	 * Initialize all elements of the symmetric matrix with a constant value.
	 * @param  iv 	the constant value
	 */	
	public def init(iv:Double): SymMatrix(this) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)
				this.d(i) = iv;
		return this;
	}
	
	/**
	 * Initialize using function. Only lower triangular part is valid.
	 * The upper triangular part is written using initial function.
	 * 
	 * @param f    The function to use to initialize the matrix
	 * @return this object
	 */
	public def init(f:(Int)=>Double): SymMatrix(this) {
		for (var i:Int=0; i<M*N; i++)
			this.d(i) = f(i);
		return this;
	}
	
	/**
	 * Init with function. Only the lower triangular part is initialized.
	 * 
	 * @param f    The function to use to initialize the matrix
	 * @return this object
	 */
	public def init(f:(Int,Int)=>Double): SymMatrix(this) {
		var i:Int=0;
		for (var c:Int=0; c<N; c++, i+=c+M-N)
			for (var r:Int=c; r<M; r++, i++)
				this.d(i) = f(r, c);
		return this;
	}
	
	/**
	 * Initialize all elements of the dense matrix with random 
	 * values between 0.0 and 1.0.
	 */	
	public def initRandom(): SymMatrix(this) {
		val rgen = RandTool.getRandGen();
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)
				this.d(i) = rgen.nextDouble();
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
		val rgen = RandTool.getRandGen();
		val l = Math.abs(ub-lb)+1;
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)
				this.d(i) = rgen.nextInt(l)+lb;
		return this;
	}
	
	//======================================================================
	// Data access and set
	//======================================================================
	
	public  operator this(x:Int, y:Int):Double {
		if (y < x)
			return this.d(y*M+x);
		else
			return this.d(x*M+y);
	}
	
	public  operator this(x:Int,y:Int) = (v:Double):Double {
		if (y < x) 
			this.d(y*M +x) = v;
		else
			this.d(x*M +y) = v;
		return v;
	}	
	//=====================================================================
	// Transpose
	//=====================================================================
	public def T(sym:SymMatrix(M)) {
		copyTo(sym);
	}
	
	public def T():SymMatrix(N) = clone();
	
	//=====================================================================
	// Cellwise operations. Only lower triangular part is modified.
	//=====================================================================
	public  def scale(a:Double):SymMatrix(this)  {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)		
				this.d(i) = this.d(i) * a;
		return this;
	}
	
	public def sum():Double {
		var tt:Double = 0.0;
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1) 
			for (var i:Int=colstt+1; i<colstt+len; i++)
				tt += this.d(i);
		tt *= 2;
		for (var i:Int=0; i<N*M; i+=M+1)
			tt += this.d(i);
		return tt;
	}
	
	//------------------------
	// Add operation
	//------------------------
	public def cellAdd(v:Double):SymMatrix(this) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)		
				this.d(i) += v;
		return this;
	}
	
	public def cellAdd(x:Matrix(M,N)):SymMatrix(this)   {
		throw new UnsupportedOperationException("Cell-wise addition does not support using SymMatrix as output matrix");
	}
	
	public def cellAdd(x:SymMatrix(M)):SymMatrix(this) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)		
				this.d(i) += x.d(i);
		return this;
	}	
	
	public def cellAddTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1) {
			x.d(colstt) += this.d(colstt);
			var j:Int = colstt+M;
			for (var i:Int=colstt+1; i<colstt+len; i++, j+=M) {		
				x.d(i) += this.d(i);
				x.d(j) += this.d(i);
			}
		}
		return x;
	}
	
	//----------------------------------
	// Cell-wise matrix multiplication
	//----------------------------------
	
	public def cellSub(v:Double):SymMatrix(this) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)		
				this.d(i) -= v;
		return this;
	}

	public def cellSubFrom(v:Double):SymMatrix(this) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)		
				this.d(i) = v-this.d(i);
		return this;
	}
	
	public def cellSub(x:Matrix(M,N)):SymMatrix(this)   {
		throw new UnsupportedOperationException("Cell-wise subtract does not support using SymMatrix as output matrix");
	}
	
	public def cellSub(x:SymMatrix(M)):SymMatrix(this) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)		
				this.d(i) -= x.d(i);
		return this;
	}	
	
	/**
	 * x = x - this;
	 */
	public def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1) {
			x.d(colstt) -= this.d(colstt);
			var j:Int = colstt+M;
			for (var i:Int=colstt+1; i<colstt+len; i++, j+=M) {		
				x.d(i) -= this.d(i);
				x.d(j) -= this.d(i);
			}
		}
		return x;
	}
	
	//----------------------------------
	// Cell-wise matrix multiplication
	//----------------------------------
	public def cellMult(v:Double):SymMatrix(this) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)		
				this.d(i) *= v;
		return this;
	}
	
	public def cellMult(x:Matrix(M,N)):SymMatrix(this)   {
		throw new UnsupportedOperationException("Cell-wise multiply does not support using SymMatrix as output matrix");
	}
	
	public def cellMult(x:SymMatrix(M)):SymMatrix(this) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)		
				this.d(i) *= x.d(i);
		return this;
	}	
	
	public def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1) {
			x.d(colstt) *= this.d(colstt);
			var j:Int = colstt+M;
			for (var i:Int=colstt+1; i<colstt+len; i++, j+=M) {	
				x.d(i) *= this.d(i);
				x.d(j) *= this.d(i);
			}
		}
		return x;
	}
	//-------------------------
	// Cellwise division
	//-------------------------
	public def cellDiv(v:Double):SymMatrix(this) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)	
				this.d(i) /= v;
		return this;
	}
	public def cellDivBy(v:Double):SymMatrix(this) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)	
				this.d(i) = v/this.d(i);
		return this;
	}	
	
	public def cellDiv(x:Matrix(M,N)):SymMatrix(this)   {
		throw new UnsupportedOperationException("Cell-wise division does not support using SymMatrix as output matrix");
	}
	
	public def cellDiv(x:SymMatrix(M)):SymMatrix(this) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)	
				this.d(i) /= x.d(i);
		return this;
	}	
	
	public def cellDivBy(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1) {
			x.d(colstt) /= this.d(colstt);
			var j:Int = colstt+M;
			for (var i:Int=colstt+1; i<colstt+len; i++, j+=M) {	
				x.d(i) /= this.d(i);
				x.d(j) /= this.d(i);
			}
		}
		return x;
	}
	
	//================================================================
	// Matrix multiply operations: self this<- op(A)*op(B) + (plus?1:0) * C
	// Default is using BLAS driver
	// Use DenseMatrixBLAS method calls
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
	public operator this + (v:Double) = this.clone().cellAdd(v) as SymMatrix(M,N);
	public operator (v:Double) + this = this + v;

	public operator this - (v:Double) = this.clone().cellSub(v) as SymMatrix(M,N);
	public operator (v:Double) - this = this.clone().cellSubFrom(v) as SymMatrix(M,N);
	public operator this / (v:Double) = this.clone().cellDiv(v) as SymMatrix(M,N);
	public operator (v:Double) / this = this.clone().cellDivBy(v) as SymMatrix(M,N);
	public operator this * (alpha:Double) = this.clone().scale(alpha) as SymMatrix(M,N);
	public operator this * (alpha:Int)    = this * (alpha as Double);
	public operator (alpha:Double) * this = this * alpha;
	public operator (alpha:Int) * this    = this * alpha;
	
	
	public operator this + (that:SymMatrix(M)) = this.clone().cellAdd(that);
	public operator this - (that:SymMatrix(M)) = this.clone().cellSub(that);
	public operator this * (that:SymMatrix(M)) = this.clone().cellMult(that);
	public operator this / (that:SymMatrix(M)) = this.clone().cellDiv(that);

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
	
	
	/**
	 * Operation multiply, result stores in dense 
	 */
	public operator this % (that:DenseMatrix(N)):DenseMatrix(M,that.N) {
		val ret = DenseMatrix.make(this.M, that.N);
		DenseMatrixBLAS.comp(this, that, ret, false);
		return ret;
	}
	
	public operator (that:DenseMatrix{self.N==this.M}) % this :DenseMatrix(that.M,N) {
		val ret = DenseMatrix.make(that.M, this.N);
		DenseMatrixBLAS.comp(that, this, ret, false);
		return ret;
	}
		
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
		val outstr = new StringBuilder();
		outstr.add("--------- Symmetric Matrix "+M+" x "+N+" lower part data ---------\n");
		for (var r:Int=0; r<M; r++) {
			outstr.add(r+"\t[");
			for (var c:Int=0; c<=r; c++)
				outstr.add(this(r,c).toString()+" ");
			outstr.add("]\n");
		}
		outstr.add("---------------------------------------\n");
		return outstr.toString();	
	}
	
	public def print(msg:String): void {
		Console.OUT.println(msg+"\n"+toString());
	}
	
	public def print():void {
		Console.OUT.println(toString());
	}
	
}
