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

package x10.matrix;

import x10.io.Console;
import x10.util.Random;
import x10.util.Timer;
import x10.util.StringBuilder;

import x10.matrix.blas.DenseMatrixBLAS;

public type TriDense(m:Int, n:Int)=TriDense{m==n, self.M==m, m==n};
public type TriDense(m:Int)=TriDense{self.M==m,self.N==m};
public type TriDense(C:TriDense)=TriDense{self==C};
public type TriDense(C:Matrix)=TriDense{self==C};


/**
 * Triangular dense matrix is derived from dense matrix. In default,  it uses the lower 
 * part to store matrix data. Therefore, it uses the same amount of memory 
 * space as dense matrix of the same dimension.  This memory layout complies with
 * symmetric matrix specification of BLAS, so the memory space can be passed to
 * BLAS routinges.
 * <p>
 * By default, the lower part is accessed. 
 * Results of cell-wise operations on triangulor matrix is stored in dense instance.
 * 
 */
public class TriDense extends DenseMatrix{self.M==self.N} {
	
	//================================================================
	// Base data structure
	//================================================================
	/*
	 * Upper or lower triangular matrix flag. If true, upper triangular.
	 * Default is false, lower triangular.
	 */
	public var uplo:Boolean= false; 
	
	//================================================================
	// Constructor, maker, and clone method
	//================================================================	
	public def this(n:Int, x:Array[Double](1){rail}) : TriDense(n){
		super(n, n, x);
	}
	
	public def this(up:Boolean, n:Int, x:Array[Double](1){rail}) : TriDense(n){
		super(n, n, x);
		uplo = up;
	}	
	//----------------------------------------------------------------
	public static def make(up:Boolean, n:Int):TriDense(n) {
		val x = new Array[Double](n*n);
		return new TriDense(up, n, x);
	}
	
	public static def make(n:Int) = make(false, n);
 
	public static def make(src:TriDense):TriDense(src.M) {
		val n = src.N;
		val newd = new Array[Double](n*n);
		Array.copy(src.d, newd);
		return new TriDense(src.uplo, n, newd);
	}
	
	public static def make(up:Boolean, src:DenseMatrix) : TriDense(src.M){
		val newd = new Array[Double](src.d);
		val nt = new TriDense(up, src.M, newd);

		if (up)
			nt.resetLower();
		else
			nt.resetUpper();
		return nt;
	}

	public def clone():TriDense(M,N){
		val nd = new Array[Double](this.d) as Array[Double](1){rail};
		val nm = new TriDense(uplo, M, nd);
		return nm as TriDense(M,N);
	}
	
	public  def alloc(m:Int, n:Int):TriDense(m,n) {
		Debug.assure(m==n);
		val x = new Array[Double](m*n);
		val nm = new TriDense(m, x);
		return nm as TriDense(m,n);
	}
	
	public def alloc() = alloc(this.M, this.M);
	
// 	//======================================================================
// 	// Data copy and reset 
// 	//======================================================================

	public def copyTo(tmat:TriDense(N)): void {
		var colstt:Int=0;
		if (uplo) {
			for (var len:Int=1; len <= M; len++, colstt+=M) {
				Array.copy(this.d, colstt, tmat.d, colstt, len);		
			}			
		} else {
			for (var len:Int=N; len > 0; len--, colstt+=M+1) {
				Array.copy(this.d, colstt, tmat.d, colstt, len);		
			}
		}
		tmat.uplo = this.uplo;
	}
	
	public def copyTo(mat:Matrix(M,N)): void {
		if (mat instanceof DenseMatrix)
			copyTo(mat as DenseMatrix(M,N));
		else if (mat instanceof TriDense)
			copyTo(mat as TriDense(N));
		else
			Debug.exit("CopyTo: matrix type does not compatible");
	}
	
	/**
	 * Reset upper triangulor part, excluding the diagnose
	 */
	public def resetUpper():void {
		var colstt:Int = 0;
		for (var c:Int=0; c < N; c++, colstt+=M) 
			for (var i:Int=colstt; i<colstt+c; i++)
				this.d(i) = 0.0;
	}
	
	public def resetLower():void {
		var colstt:Int = 0;
		for (var c:Int=0; c < N; c++, colstt+=M+1) 
			for (var i:Int=colstt+1; i<colstt+M-c; i++)
				this.d(i) = 0.0;
	}
	
	//-------------------------------------------------------------------
	// Data initialization
	//-------------------------------------------------------------------
	/**
	 * Initialize all elements of the triangular matrix with a constant value.
	 * @param  iv 	the constant value
	 */	
	public def init(iv:Double): TriDense(this) {
		if (uplo)
			super.init((r:Int,c:Int)=>(r>c)?0.0:iv);
		else
			super.init((r:Int,c:Int)=>(r<c)?0.0:iv);
		return this;
	}

	/**
	 * Initialize using function. Only lower triangular part is valid.
	 * The upper triangular part is written using initial function.
	 * 
	 * @param f    The function to use to initialize the matrix
	 * @return this object
	 */
	public def init(f:(Int)=>Double): TriDense(this) {
		if (uplo)
			super.init((r:Int,c:Int)=>(r>c)?0.0:f(c*M+r));
		else
			super.init((r:Int,c:Int)=>(r<c)?0.0:f(c*M+r));
		return this;
	}
	
	/**
	 * Init with function. Only the lower triangular part is initialized.
	 * 
	 * @param f    The function to use to initialize the matrix
	 * @return this object
	 */
	public def init(f:(Int,Int)=>Double): TriDense(this) {
		if (uplo)
			super.init((r:Int,c:Int)=>(r>c)?0.0:f(r,c));
		else
			super.init((r:Int,c:Int)=>(r<c)?0.0:f(r,c));
		return this;		
	}
	
	/**
	 * Initialize all elements of the dense matrix with random 
	 * values between 0.0 and 1.0.
	 */	
	public def initRandom(): TriDense(this) {
		val rgen = RandTool.getRandGen();

		if (uplo)
			super.init((r:Int,c:Int)=>(r>c)?0.0:rgen.nextDouble());
		else
			super.init((r:Int,c:Int)=>(r<c)?0.0:rgen.nextDouble());
		
		return this;
	}
	
	/**
	 * Initialize all elements of the dense matrix with random 
	 * values between the specified range.
	 * 
	 * @param lb	lower bound of random values
	 * @param up	upper bound of random values
	 */	
	public def initRandom(lb:Int, ub:Int): TriDense(this) {
	
		val rgen = RandTool.getRandGen();
		val l = Math.abs(ub-lb)+1;

		if (uplo)
			super.init((r:Int,c:Int)=>(r>c)?0.0:(rgen.nextInt(l)+lb as Double));
		else
			super.init((r:Int,c:Int)=>(r<c)?0.0:(rgen.nextInt(l)+lb as Double));
		
		return this;
		
	}
	
	//======================================================================
	// Data access and set
	//======================================================================
	
	public  operator this(x:Int, y:Int):Double {
		if (uplo && x<=y)
			return this.d(y*M+x);
		if (uplo==false && x >= y)
			return this.d(y*M+x);
		return 0;
	}
	
	public  operator this(x:Int,y:Int) = (v:Double):Double {
		if (uplo && x<=y)
			this.d(y*M +x) = v;
		if (uplo==false && x>=y)
			this.d(y*M +x) = v;
		return v;
	}	
	
	//=====================================================================
	// Transpose
	//=====================================================================
	public def selfT():TriDense(this) {
		var src_idx:Int =0;
		var dst_idx:Int =0;
		var swaptmp:Double = 0;
		for (var c:Int=0; c < this.M; c++) {
			dst_idx = (c+1)*this.M+c;
			src_idx = c * this.M + c + 1;
			for (var r:Int=c+1; r < this.M; r++, dst_idx+=M, src_idx++) {
				swaptmp = this.d(dst_idx);
				this.d(dst_idx) = this.d(src_idx);
				this.d(src_idx) = swaptmp;
			}
		}
		return this;
	}
	

	//=====================================================================
	// Cellwise operations. Only lower triangular part is modified.
	//=====================================================================
	public  def scale(a:Double):TriDense(this)  {
		var colstt:Int=0;
		if (uplo==false) {
			// Lower part
			for (var len:Int=M; len>0; len--, colstt+=M+1)
				for (var i:Int=colstt; i<colstt+len; i++)		
					this.d(i) = this.d(i) * a;
		} else {
			// Upper part
			for (var len:Int=1; len <=len; len++, colstt+=M)
				for (var i:Int=colstt; i<len; i++)
					this.d(i) = this.d(i) * a;
		}
		return this;
	}
	
	public def sum():Double {
		var tt:Double = 0.0;
		var colstt:Int=0;
		if (uplo==false) {
			// lower part
			for (var len:Int=M; len>0; len--, colstt+=M+1) 
				for (var i:Int=colstt; i<colstt+len; i++)
					tt += this.d(i);
		} else {
			for (var len:Int=1; len <=len; len++, colstt+=M)
				for (var i:Int=colstt; i<len; i++)
					tt += this.d(i);			
		}
		return tt;
	}
	
	//------------------------
	// Add operation
	//------------------------
	//public def cellAdd(x:TriDense(M)):DenseMatrix(this) =
	//	cellAdd(x as DenseMatrix(M,N));
	
	
	public def cellAddTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var colstt:Int=0;
		if (uplo==false) {
			for (var len:Int=M; len>0; len--, colstt+=M+1) 
				for (var i:Int=colstt; i<colstt+len; i++) 		
					x.d(i) += this.d(i);
		} else {
			for (var len:Int=1; len <=len; len++, colstt+=M)
				for (var i:Int=colstt; i<len; i++)
					x.d(i) += this.d(i);
		}
		return x;
	}
	
	//----------------------------------
	// Cell-wise matrix multiplication
	//----------------------------------
	
	public def cellSubFrom(v:Double):TriDense(this) {
		
		var colstt:Int=0;
		if (uplo==false) {
			for (var len:Int=M; len>0; len--, colstt+=M+1)
				for (var i:Int=colstt; i<colstt+len; i++)		
					this.d(i) = v-this.d(i);
		} else {
			for (var len:Int=1; len <=len; len++, colstt+=M)
				for (var i:Int=colstt; i<len; i++)
					this.d(i) = v-this.d(i);
		}
		return this;
	}
	
	//public def cellSub(x:TriDense(M)): DenseMatrix(this) = cellSub(x as DenseMatrix(M,N));
	
	/**
	 * x = x - this;
	 */
	public def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var colstt:Int=0;
		if (uplo==false) {
			for (var len:Int=M; len>0; len--, colstt+=M+1) 
				for (var i:Int=colstt; i<colstt+len; i++) 		
					x.d(i) -= this.d(i);
		} else {
			for (var len:Int=1; len <=len; len++, colstt+=M)
				for (var i:Int=colstt; i<len; i++)
					x.d(i) -= this.d(i);
		}
		return x;
	}
	
	//----------------------------------
	// Cell-wise matrix multiplication
	//----------------------------------
	public def cellMult(v:Double):TriDense(this) {
		var colstt:Int=0;
		if (uplo==false) {
			for (var len:Int=M; len>0; len--, colstt+=M+1)
				for (var i:Int=colstt; i<colstt+len; i++)		
					this.d(i) *= v;
		} else {
			for (var len:Int=1; len <=len; len++, colstt+=M)
				for (var i:Int=colstt; i<len; i++)
					this.d(i) *= v;
		}
		return this;
	}

	public def cellMult(x:TriDense(M,N)):TriDense(this) {
		if (x.uplo != this.uplo) {
			reset();
			return this;
		}
		var colstt:Int=0;
		if (uplo==false) {
			for (var len:Int=M; len>0; len--, colstt+=M+1)
				for (var i:Int=colstt; i<colstt+len; i++)		
					this.d(i) *= x.d(i);
		} else {
			for (var len:Int=1; len <=len; len++, colstt+=M)
				for (var i:Int=colstt; i<len; i++)
					this.d(i) *= x.d(i);			
		}
		return this;
	}	

	public def cellMult(x:DenseMatrix(M,N)):TriDense(this) {

		var colstt:Int=0;
		if (uplo==false) {
			for (var len:Int=M; len>0; len--, colstt+=M+1)
				for (var i:Int=colstt; i<colstt+len; i++)		
					this.d(i) *= x.d(i);
		} else {
			for (var len:Int=1; len <=len; len++, colstt+=M)
				for (var i:Int=colstt; i<len; i++)
					this.d(i) *= x.d(i);			
		}
		return this;
	}
	
	public def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x) {

		var colstt:Int=0;
		if (uplo==false) {
			for (var c:Int=0; c<N; c++, colstt+=M){
				var i:Int = colstt;
				for (; i<colstt+c; i++)	x.d(i) = 0.0;
				for (; i<colstt+M; i++) x.d(i) *= this.d(i);
			} 
		}else {
			for (var c:Int=0; c<N; c++, colstt+=M){
				var i:Int = colstt;
				for (; i<colstt+c+1; i++) x.d(i) *= this.d(i);
				for (; i<colstt+M;   i++) x.d(i) = 0.0;					
			}
		}
		return x;
	}
	
	//-------------------------
	// Cellwise division
	//-------------------------
	public def cellDiv(v:Double):TriDense(this) {
		var colstt:Int=0;
		if (uplo==false) {
			for (var len:Int=N; len>0; len--, colstt+=M+1)
			for (var i:Int=colstt; i<colstt+len; i++)	
				this.d(i) /= v;
		} else {
			for (var len:Int=1; len <=len; len++, colstt+=M)
				for (var i:Int=colstt; i<len; i++)
					this.d(i) /= v;			
		}
		return this;
	}
	
	//public def cellDivBy(v:Double):TriDense(this) {
	//	Debug.exit("Divide by 0 error");
	//	return this;
	//}	
	
	//public def cellDiv(x:TriDense(M)):TriDense(this) {
	//	Debug.exit("Divide by 0 error");
	//	return this;
	//}	
	
	//public def cellDivBy(x:DenseMatrix(M,N)):DenseMatrix(x) {
	//	Debug.exit("Divide by 0 error");
	//	return x;
	//}
	
	//================================================================
	// Matrix multiply operations: self this<- op(A)*op(B) + (plus?1:0) * C
	// Default is using BLAS driver
	// Use DenseMatrixBLAS method calls
	//================================================================
	// public def mult(A:Matrix(this.M), B:Matrix(A.N,this.N),	plus:Boolean):TriDense(this) {
	// 	throw new UnsupportedOperationException("Matrix multiply does not support using TriDense as output matrix");
	// }
	// 
	// public def transMult(A:Matrix{self.N==this.M}, B:Matrix(A.M,this.N), plus:Boolean):TriDense(this) {
	// 	throw new UnsupportedOperationException("Matrix transposed multiply does not support using TriDense as output matrix");
	// }
	// 		
	// public def multTrans(A:Matrix(this.M), B:Matrix(this.N, A.N), plus:Boolean):TriDense(this) {
	// 	throw new UnsupportedOperationException("Matrix multiply transposed does not support using TriDense as output matrix");
	// }

	//==================================================================
	// Triangular % Matrix solvers
	//==================================================================
	public def solveMatMultSelf(A:DenseMatrix{self.N==this.N}):DenseMatrix(A) {
		DenseMatrixBLAS.solveMatMultTri(A, this);
		return A;
	}
	
	public def solveSelfMultMat(A:DenseMatrix(N)):DenseMatrix(A) {
		DenseMatrixBLAS.solveTriMultMat(this, A);
		return A;
	}	
	//==================================================================
	// Operator
	//==================================================================
	public operator - this            = this.clone().scale(-1.0) as TriDense(M,N);
	public operator this + (v:Double) = this.clone().cellAdd(v)  as TriDense(M,N);
	public operator (v:Double) + this = this + v;

	public operator this - (v:Double) = this.clone().cellSub(v)     as TriDense(M,N);
	public operator (v:Double) - this = this.clone().cellSubFrom(v) as TriDense(M,N);
	
	public operator this / (v:Double) = this.clone().cellDiv(v)   as TriDense(M,N);
	public operator (v:Double) / this = this.clone().cellDivBy(v) as TriDense(M,N);
	
	public operator this * (alpha:Double) = this.clone().scale(alpha) as TriDense(M,N);
	public operator this * (alpha:Int)    = this * (alpha as Double);
	public operator (alpha:Double) * this = this * alpha;
	public operator (alpha:Int) * this    = this * alpha;
	
	
	public operator this + (that:TriDense(M)) = this.toDense().cellAdd(that)  as DenseMatrix(M,N);
	public operator this - (that:TriDense(M)) = this.toDense().cellSub(that)  as DenseMatrix(M,N);
	public operator this * (that:TriDense(M)) = this.toDense().cellMult(that) as DenseMatrix(M,N);
	public operator this / (that:TriDense(M)) {
		Debug.exit("Divide 0 error");
	}

	/**
	 * Operation with dense matrix and store result in dense format
	 */
	// public operator this + (that:DenseMatrix(M,N)) = this.cellAddTo(that.clone());
	// public operator this - (that:DenseMatrix(M,N)) = this.cellAddTo(that.clone().scale(-1));
	// public operator this * (that:DenseMatrix(M,N)) = this.cellMultTo(that.clone());
	// public operator this / (that:DenseMatrix(M,N)) = this.toDense().cellDiv(that) as DenseMatrix(M,N);
	// 
	// public operator (that:DenseMatrix(M,N)) + this = this + that;
	// public operator (that:DenseMatrix(M,N)) - this = this.cellSubFrom(that.clone());
	// public operator (that:DenseMatrix(M,N)) * this = this * that;
	public operator (that:DenseMatrix(M,N)) / this { 
		Debug.exit("Divide by 0 error");
	}
	
	
	/**
	 * Operation multiply, result stores in dense 
	 */
	public operator this % (that:DenseMatrix(N)):DenseMatrix(M,that.N)               = that.clone().multBy(this);
	public operator (that:DenseMatrix{self.N==this.M}) % this :DenseMatrix(that.M,N) = that.clone().mult(this);
		
	//=======================================================
	// Utils
	//=======================================================
	public def likeMe(m:Matrix):Boolean {
		if ((m instanceof TriDense) && m.M==M && m.N==N) return true;
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
		val outstr=new StringBuilder();
		outstr.add("--------- Triangular Matrix "+M+" x "+N);
		if (uplo)
			outstr.add(" upper part data ---------\n");
		else
			outstr.add(" lower part data ---------\n");
		for (var r:Int=0; r<M; r++) {
			outstr.add(r+"\t[ ");
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
