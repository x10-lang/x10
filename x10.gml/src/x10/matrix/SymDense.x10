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

public type SymDense(m:Int, n:Int)=SymDense{m==n, self.M==m, self.N==n};
public type SymDense(m:Int)=SymDense{self.M==m,self.N==m};
public type SymDense(C:SymDense)=SymDense{self==C};
public type SymDense(C:Matrix)=SymDense{self==C};


/**
 * The symmetric dense matrix is derived from dense matrix. It inherits the memory layout of the
 * dense matrix instance.  Therefore, it complies with
 * BLAS symmetric matrix. Only the lower part of matrix data is referenced.
 * <p>
 * Result of cell-wise operations on symmetric matrix is stored in dense instances.
 * Most cellwise operations resort to the super dense class's operations without
 * optimization.
 */
public class SymDense extends DenseMatrix{self.M==self.N} {
	
	//================================================================
	// Base data structure
	//================================================================
	
	//================================================================
	// Constructor, maker, and clone method
	//================================================================	
	public def this(n:Int, x:Array[Double](1){rail}) : SymDense(n){
		super(n, n, x);
	}
	
	//----------------------------------------------------------------
	public static def make(n:Int):SymDense(n) {
		val x = new Array[Double](n*n);
		return new SymDense(n, x);
	}

	public static def make(src:SymDense):SymDense(src.M) {
		val n = src.M;
		val newd = new Array[Double](n*n);
		Array.copy(src.d, newd);
		return new SymDense(n, newd);
	}

	
	public def clone():SymDense(M){
		val nd = new Array[Double](this.d) as Array[Double](1){rail};
		val nm = new SymDense(M, nd);
		return nm as SymDense(M);
	}
	
	public  def alloc(m:Int, n:Int):SymDense(m,n) {
		Debug.assure(m==n);
		val x = new Array[Double](m*m);
		val nm = new SymDense(m, x);
		return nm as SymDense(m,n);
	}
	
	public def alloc() = alloc(this.M, this.M);
	
	//======================================================================
	// Data copy and reset 
	//======================================================================
	/**
	 * Copy the lower part to target and mirror the lower part to upper part.
	 */
	public def copyTo(dm:DenseMatrix(M,N)):void {
		var colstt:Int=0;
		for (var len:Int=N; len > 0; len--, colstt+=M+1) {
			Array.copy(this.d, colstt, dm.d, colstt, len);		
		}
		mirrorLowerToUpper(dm);
	}
	
	public def copyTo(smat:SymDense(N)): void {
		super.copyTo(smat as DenseMatrix(M,N));
	}

	public def copyTo(mat:Matrix(M,N)) : void {
		if (mat instanceof DenseMatrix) {
			copyTo(mat as DenseMatrix(M,N));
		} else if (likeMe(mat)) {
			copyTo(mat as SymDense(N));
		} else {
			Debug.exit("CopyTo: Target matrix type is not compatible");
		}
	}
	//-------------------
	/**
	 *  Copy lower triangular part to upper part.
	 */
	public static def mirrorLowerToUpper(dm:DenseMatrix) :void {
		var colstt:Int=0;
		var len:Int = (dm.M>dm.N)?dm.N:dm.M;
		for (; len > 0; len--, colstt+=dm.M+1) {
			var j:Int = colstt+dm.M;
			for (var i:Int=colstt+1; i<colstt+len; i++, j+=dm.M)
				dm.d(j) = dm.d(i);		
		}
	}

	/**
	 * Copy upper part to lower
	 */
	public static def mirrorUpperToLower(dm:DenseMatrix) :void {
		var colstt:Int=0;
		var len:Int = (dm.M>dm.N)?dm.N:dm.M;
		for (; len > 0; len--, colstt+=dm.M+1) {
			var j:Int = colstt+dm.M;
			for (var i:Int=colstt+1; i<colstt+len; i++, j+=dm.M)
				dm.d(i) = dm.d(j);		
		}
	}



	//-------------------------------------------------------------------
	// Data initialization
	//-------------------------------------------------------------------
	/**
	 * Initialize all elements of the symmetric matrix with a constant value.
	 * Only the lower part is initialized.
	 * 
	 * @param  iv 	the constant value
	 */	
	public def init(iv:Double): SymDense(this) {
		for (var i:Int=0; i<this.d.size; i++)
			this.d(i) = iv;
		return this;
	}
	
	
	/**
	 * Init with function. Only the lower triangular part and mirror the lower to
	 * upper part.
	 * 
	 * @param f    The function to use to initialize the matrix
	 * @return this object
	 */
	public def init(f:(Int,Int)=>Double): SymDense(this) {
		var i:Int=0;
		for (var c:Int=0; c<N; c++, i+=c)
			for (var r:Int=c; r<M; r++, i++)
				this.d(i) = f(r, c);
		mirrorLowerToUpper(this as DenseMatrix);
		return this;
	}
	
	/**
	 * Initialize lower part of matrix with random values, and then mirror to upper part. 
	 */	
	public def initRandom(): SymDense(this) {
		val rgen = RandTool.getRandGen();
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)
				this.d(i) = rgen.nextDouble();
		mirrorLowerToUpper(this);
		return this;
	}
	
	/**
	 * Initialize lower part with random values within the given bounds, and then
	 * mirror to upper part.
	 *  
	 * @param lb	lower bound of random values
	 * @param up	upper bound of random values
	 */	
	public def initRandom(lb:Int, ub:Int): SymDense(this) {
		val rgen = RandTool.getRandGen();
		val l = Math.abs(ub-lb)+1;
		var colstt:Int=0;
		for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)
				this.d(i) = rgen.nextInt(l)+lb;
		mirrorLowerToUpper(this);
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
	public def T(sym:SymDense(M)) {
		copyTo(sym);
	}
	
	public def T():SymDense(N) = clone();
	
	//=====================================================================
	// Cellwise operations. Only lower triangular part is modified.
	//=====================================================================
	public  def scale(a:Double):SymDense(this)  {
		for (var i:Int =0; i<M*N; i++)
			this.d(i) *= a;
		return this;
	}
	
	public def sum():Double {
		var tt:Double = 0.0;
		var colstt:Int=1;
		for (var len:Int=M-1; len>0; len--, colstt+=M+1) 
			for (var i:Int=colstt; i<colstt+len; i++)
				tt += this.d(i);
		tt *= 2;
		for (var i:Int=0; i<N*M; i+=M+1)
			tt += this.d(i);
		return tt;
	}
	
	//------------------------
	// Lower half cell-add operation
	//------------------------
	public def cellAdd(v:Double) : SymDense(this) = 
		super.cellAdd(v) as SymDense(this);
		
	public def cellAdd(x:SymDense(M,N)):SymDense(this) =
		super.cellAdd(x as DenseMatrix(M,N)) as SymDense(this);
	
	public def cellAddTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var colstt:Int=0;
		for (var len:Int=M; len>0; len--, colstt+=M+1) {
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
	
	// public def lowerCellSub(v:Double):SymDense(this) {
	// 	var colstt:Int=0;
	// 	for (var len:Int=M; len>0; len--, colstt+=M+1)
	// 		for (var i:Int=colstt; i<colstt+len; i++)		
	// 			this.d(i) -= v;
	// 	return this;
	// }
	public def cellSub(v:Double):SymDense(this) = 
		super.cellSub(v) as SymDense(this);

	public def cellSubFrom(v:Double):SymDense(this) =
		super.cellSubFrom(v) as SymDense(this);
	
	public def cellSub(x:SymDense(M,N)):SymDense(this) =
		super.cellSub(x as DenseMatrix(M,N)) as SymDense(this);
	
	/**
	 * x = x - this;
	 */
	public def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var colstt:Int=0;
		for (var len:Int=M; len>0; len--, colstt+=M+1) {
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
	// Lower part cell-wise matrix multiplication
	//----------------------------------
	// public def lowerCellMult(v:Double):SymDense(this) {
	// 	var colstt:Int=0;
	// 	for (var len:Int=M; len>0; len--, colstt+=M+1)
	// 		for (var i:Int=colstt; i<colstt+len; i++)		
	// 			this.d(i) *= v;
	// 	return this;
	// }
	public def cellMult(v:Double):SymDense(this) =
		super.cellMult(v) as SymDense(this);
	
	public def cellMult(x:SymDense(M,N)):SymDense(this) =
		super.cellMult(x as DenseMatrix(M,N)) as SymDense(this);
	
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
	public def cellDiv(v:Double):SymDense(this) =
		super.cellDiv(v) as SymDense(this);
	
	public def cellDivBy(v:Double):SymDense(this) =
		super.cellDivBy(v) as SymDense(this);
	
	public def cellDiv(x:SymDense(M,N)):SymDense(this) =
		super.cellDiv(x as DenseMatrix(M,N)) as SymDense(this);
	
	public def cellDivBy(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var colstt:Int=0;
		for (var len:Int=M; len>0; len--, colstt+=M+1) {
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
	// public def mult(A:Matrix(this.M), B:Matrix(A.N,this.N),	plus:Boolean):SymDense(this) {
	// 	throw new UnsupportedOperationException("Matrix multiply does not support using SymDense as output matrix");
	// }
	// 
	// public def transMult(A:Matrix{self.N==this.M}, B:Matrix(A.M,this.N), plus:Boolean):SymDense(this) {
	// 	throw new UnsupportedOperationException("Matrix transposed multiply does not support using SymDense as output matrix");
	// }
	// 		
	// public def multTrans(A:Matrix(this.M), B:Matrix(this.N, A.N), plus:Boolean):SymDense(this) {
	// 	throw new UnsupportedOperationException("Matrix multiply transposed does not support using SymDense as output matrix");
	// }


	//==================================================================
	// Operator
	//==================================================================
	public operator - this            = this.clone().scale(-1.0)      as SymDense(M,N);
	public operator this + (v:Double) = this.clone().cellAdd(v)       as SymDense(M,N);
	public operator (v:Double) + this = (this + v) as SymDense(M,N);

	public operator this - (v:Double) = this.clone().cellSub(v)       as SymDense(M,N);
	public operator (v:Double) - this = this.clone().cellSubFrom(v)   as SymDense(M,N);
	public operator this / (v:Double) = this.clone().cellDiv(v)       as SymDense(M,N);
	public operator (v:Double) / this = this.clone().cellDivBy(v)     as SymDense(M,N);
	public operator this * (alpha:Double) = this.clone().scale(alpha) as SymDense(M,N);
	public operator this * (alpha:Int)    = this * (alpha as Double);
	public operator (alpha:Double) * this = this * alpha;
	public operator (alpha:Int) * this    = this * alpha;
	
	
	public operator this + (that:SymDense(M)) = this.clone().cellAdd(that)  as SymDense(M,N);
	public operator this - (that:SymDense(M)) = this.clone().cellSub(that)  as SymDense(M,N);
	public operator this * (that:SymDense(M)) = this.clone().cellMult(that) as SymDense(M,N);
	public operator this / (that:SymDense(M)) = this.clone().cellDiv(that)  as SymDense(M,N);

// 	/**
// 	 * Operation with dense matrix and store result in dense format
// 	 */
// 	public operator this + (that:DenseMatrix(M,N)):DenseMatrix(M,N) = this.cellAddTo(that.clone());
// 	public operator this - (that:DenseMatrix(M,N)):DenseMatrix(M,N) = this.toDense().cellSub(that) as DenseMatrix(M,N);
// 	public operator this * (that:DenseMatrix(M,N)):DenseMatrix(M,N) = this.cellMultTo(that.clone()) as DenseMatrix(M,N);
// 	public operator this / (that:DenseMatrix(M,N)):DenseMatrix(M,N) = this.toDense().cellDiv(that) as DenseMatrix(M,N);
// 	
// 	
// 	public operator (that:DenseMatrix(M,N)) + this = this + that;
// 	public operator (that:DenseMatrix(M,N)) - this = this.cellSubFrom(that.clone());
// 	public operator (that:DenseMatrix(M,N)) * this = this * that;
// 	public operator (that:DenseMatrix(M,N)) / this = this.cellDivBy(that.clone()) as DenseMatrix(M,N);
	
	
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
		if ((m instanceof SymDense) && m.M==M && m.N==N) return true;
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
