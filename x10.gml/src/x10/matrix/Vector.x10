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

import x10.matrix.blas.BLAS;
import x10.matrix.blas.DenseMatrixBLAS;

public type Vector(m:Int)=Vector{self.N==m};
public type Vector(v:Vector)=Vector{self==v};


/**
 * Class Vector represents an N x 1 matrix, (or 1xN matrix ?)

 <p> This implemnetation has a dense, single-place representation.

 <p> Methods include 
     1) product of two vectors: Nx1 * 1xN,52
     2) product of vector and a scalar: Nx1 * 1,
	 3) product of a scalar and a vector: 1 * 1xN,
	 4) addition of two vectors: Nx1 + Nx1,
	 5) substraction between two vectors: Nx1 - Nx1,
	 6) addition of a vector and a scalar: Nx1 + 1,
	 7) substraction of a scalar from a vector: Nx1 - 1,
	 8) inverse of a vector: Nx1
	 9) norm of a vector: Nx1

 */
public class Vector(N:Int) implements (Int) => Double {

	//================================================================
	// Vector data
	//================================================================
    public val d:Array[Double](1){rail};

	//================================================================
	// Constructor, and maker
	//================================================================
	/**
	 * Constructor based ib a Rail in Double
	 */
    public def this(n:Int, x:Array[Double](1){rail}):Vector(n) {
		property(n);
		this.d=x;
    }

    public def this(x:Array[Double](1){rail}):Vector(x.size) {
    	property(x.size);
    	this.d=x;
    }

	//----------------------------------

    public static  def make(n:Int, v:Double) {
		val d = new Array[Double](n, v);
		return new Vector(n, d);
    }

    public static  def make(n:Int) {
	    val d = new Array[Double](n);
		return new Vector(n, d);
    }
    
    //======================================================
    /**
     * Initialize all elements of the vector with a constant value.
     * @param  iv 	the constant value
     */	
    public def init(iv:Double): Vector(this) {
    	for (var i:Int=0; i<N; i++)	
    		this.d(i) = iv;
    	return this;
    }

    /**
     * Initialize vector with random values between 0.0 and 1.0.
     */	
    public def initRandom(): Vector(this) {
    	val rgen = RandTool.getRandGen();
    	for (var i:Int=0; i<N; i++) {
    		this.d(i) = rgen.nextDouble();
    	}
    	return this;
    }
    
    /**
     * Initialize vector x with random 
     * values between the specified range.
     * 
     * @param lb	lower bound of random values
     * @param up	upper bound of random values
     */	
    public def initRandom(lb:Int, ub:Int): Vector(this) {
    	val len = Math.abs(ub-lb)+1;
    	val rgen = RandTool.getRandGen();
    	//val ll = M*N / 100;
    	for (var i:Int=0; i<N; i++) {
    		this.d(i) = rgen.nextInt(len)+lb;
    	}
    	return this;
    }
    
    /**
     * Init with function
     */
    public def init(f:(int)=>Double): Vector(this) {
    	for (var i:Int=0; i<N; i++)
    		this.d(i) = f(i);
    	return this;
    }
    
    
	//======================================================
    public def rail():Array[Double](1) = d;

	public  def clone():Vector(N) {
		val nv = new Array[Double](N);
		Array.copy(this.d, 0, nv, 0, N);
		return new Vector(N, nv);
	}
	
	//======================================================================
	// Data access and set
	//======================================================================
	
    //public global  def apply(i:Int)=d(i);
    public  def apply(i:Int)=d(i);
	public  operator this(i:Int) = d(i);

	public  operator this(i:Int)=(v:Double):Double {
		this.d(i) = v;
		return v;
	}
	
	/** 
	 subset of vector from off with length of len
	*/
	public def subset(off:Int, len:Int):Vector(len) {
		val na = new Array[Double](len, len);
		Array.copy(this.d, off, na, 0, len);
		return new Vector(na);
	}
	
	public  def reset():void {
		for (var i:Int=0; i< N; i++) this.d(i) = 0.0;
	}
	//----------------------------------
	// Copy all data from vector v to local at dst_off
	public static def copyTo(src:Vector, soff:Int, dst:Vector, doff:Int, len:Int) {
		Debug.assure(soff+len<src.N && doff+len<dst.N, "Buffer overflow in vector copy");
		Array.copy(src.d, soff, dst.d, doff, len);
	}
	
	// Copy part of data from vector v to local starting from index 0
	public  def copyTo(v:Vector) {
		copyTo(this, 0, v, 0, this.N);
	}

	//------------------------------------------------------------------
	// Cell-wise operations
	//------------------------------------------------------------------
	
	/**
	 * Product of a vector and a scalar: Nx1 * 1
	 */
    public  def scale(a:Double) :Vector(this) {
		for (var i:Int=0; i < N; ++i)
			this.d(i) = a * this.d(i);
		return this;
    }
        
	/**
	 * Cell-wise mulitply of two vectors
	 */
    public def cellMult(V:Vector(N)): Vector(this) {
        for (var i:Int=0; i < N; ++i) 
			this.d(i) *= V.d(i);
        return this;
    }
	    
    
	//======================================================
	/**
	 * Addition of two vectors: Nx1 + Nx1
	 */
    public def cellAdd (V:Vector(N)):Vector(this) {
		for (var i:Int=0; i < N; ++i) 	this.d(i) += V.d(i);
		return this;
    }

    public def cellAdd (d:Double):Vector(this) {
    	for (var i:Int=0; i < N; ++i) 	this.d(i) += d;
    	return this;
    }


	//======================================================
	/** 
	 * Substraction between two vectors
	 */
    public  def cellSub(B:Vector(N)):Vector(this) {
		for (var i:Int=0; i < N; ++i) 
			this.d(i) -= B.d(i);
		return this;
    }

	/**
	 * Substraction a scalar from a vector: Nx1 - 1
	 */
    public  def cellSub(d:Double):Vector(this) {
		for (var i:Int=0; i < N; ++i) this.d(i) -= d;
		return this;
    }
    protected def cellSubFrom(d:Double):Vector(this) {
    	for (var i:Int=0; i < N; ++i) this.d(i) = d - this.d(i);
    	return this;   	
    }
    
	//======================================================
    /**
     * cellwise division: this = dv / this;
     */
    public  def cellDiv(dv:Double):Vector(this) {
    	for (var i:Int=0; i < N; ++i) this.d(i) /= dv;
    	return this;
    }
    //

    public def cellDiv(v:Vector(this.N)):Vector(this) {
    	for (var i:Int=0; i< N; ++i) {
    		this.d(i) /= v.d(i);
    	}
    	return this;
    }
        
    protected def cellDivBy(dv:Double) : Vector(this) {
    	for (var i:Int=0; i < N; ++i) this.d(i) = dv / this.d(i);
    	return this;    	
    }
	//===============================================
    /**
	 * Pruduct transition of a vector: Nx1 * (Nx1)^T
     * Return this^T * x.
     */
    public def blasTransProduct(x:Vector):Double =
		BLAS.compDotProd(this.N, this.d, x.d);
	

	public def mult(v:Vector(N)):Double {
		var d:Double = 0.0;
		for(var i:Int=0; i<N; i++) d += this.d(i) * v.d(i);
		return d;
	}
	//======================================================
	
	
	//-------------------------------------------------------------------
	// Using Blas routines: self = op(A)* b, self += op(A) * b,
	//-------------------------------------------------------------------
	public def mult(A:Matrix(N), B:Vector(A.N), plus:Boolean): Vector(this) {
		Debug.assure(A.N == B.N && this.N==A.M);
		if (A instanceof DenseMatrix) 
			 this.mult(A as DenseMatrix, B, plus);
		else if (A instanceof SymMatrix) 
			this.mult(A as SymMatrix, B, plus);
		else if (A instanceof TriMatrix)
			this.mult(A as TriMatrix, B, plus);
		//else if (A instanceof Diagonal)
		//	this.mult(A as Diagonal, B, plus);
		else
			throw new UnsupportedOperationException("Operation not supported in vector multiply: " +
													this.typeName() + ".mult(" + 
													A.typeName() + ", " + B.typeName()+")");
		return this;
	}
 
	public def transMult(A:Matrix{self.N==this.N}, B:Vector(A.M), plus:Boolean): Vector(this) {
		Debug.assure(A.N == B.N && this.N==A.M);
		if (A instanceof DenseMatrix) 
			this.transMult(A as DenseMatrix, B, plus);
		else if (A instanceof SymMatrix) 
			this.mult(A as SymMatrix, B, plus);
		//else if (A instanceof TriMatrix)
		//	this.transMult(A as TriMatrix, B, plus);
		//else if (A instanceof Diagonal)
		//	this.mult(A as Diagonal, B, plus);
		else
			throw new UnsupportedOperationException("Operation not supported in vector multiply: " +
					this.typeName() + ".mult(" + 
					A.typeName() + ", " + B.typeName()+")");
		return this;
	}
	
	//-------------------------------------------------------------------
	// Dense-vector multiply
	//-------------------------------------------------------------------
	public  def mult(A:DenseMatrix(this.N), B:Vector(A.N), plus:Boolean):Vector(this) {

		DenseMatrixBLAS.comp(A, B, this, plus);
		return this;
	}
	
	public  def transMult(A:DenseMatrix{self.N==this.N}, B:Vector(A.M), plus:Boolean):Vector(this) {

		DenseMatrixBLAS.compTransMult(A, B, this, plus);
		return this;
	}
	
	//-------------------------------------------------------------------
	// Symmetric-vector multiply
	//-------------------------------------------------------------------
	public  def mult(A:SymMatrix(this.N), B:Vector(A.N), plus:Boolean):Vector(this) {
		val beta = plus?1.0:0.0;

		BLAS.compSymMultVec(A.d, B.d, this.d, 
				[A.M, A.N],
				[1.0, beta]);
		return this;
	}
	
	public  def transMult(A:SymMatrix(this.N), B:Vector(A.N), plus:Boolean):Vector(this)
		= mult(A, B, plus);

	//-------------------------------------------------------------------
	// Triangular-vector multiply
	//-------------------------------------------------------------------
	// this = A * this
	public  def mult(A:TriMatrix(this.N)):Vector(this) {
		BLAS.compTriMultVec(A.d, this.d, this.N, 0); 
		return this;
	}
	
	public  def transMult(A:TriMatrix(this.N)):Vector(this) {
		BLAS.compTriMultVec(A.d, this.d, this.N, 1); 
		return this;
	}

	//======================================================
	// Operand overloading
	//======================================================
	// Operator add
	public  operator this + (that:Vector(N)):Vector(N) = this.clone().cellAdd(that);
	public  operator this + (dv:Double) = this.clone().cellAdd(dv);
	// Operator sub
	public  operator this - (that:Vector(N)):Vector(N) = this.clone().cellSub(that);
	public  operator this - (dv:Double) = this.clone().cellSub(dv);
	public  operator (dv:Double) - this = this.clone().cellSubFrom(dv);
	
	// Operator cellwise multiply
	public  operator this * (dv:Double)                = this.clone().scale(dv);
	public  operator (dv:Double) * this                = this.clone().scale(dv);
	public  operator this * (that:Vector(N)):Vector(N) = this.clone().cellMult(that);

	// Operator cellwise div
	public  operator this / (dv:Double)           = this.clone().cellDiv(dv);
	public  operator this / (that:Vector(this.N)) = this.clone().cellDiv(that);
	public  operator (dv:Double) / this           = this.clone().cellDivBy(dv);

	//Righ-side Operand overload
	public  operator this % (that:Matrix(N)):Vector(that.N) = 
		Vector.make(that.N).transMult(that, this as Vector(that.M), false);
	public  operator this % (that:DenseMatrix(N)):Vector(that.N) =
		Vector.make(that.N).transMult(that, this as Vector(that.M), false);
	public  operator this % (that:SymMatrix(N)):Vector(that.N) =
		Vector.make(that.N).mult(that, this as Vector(that.M), false);
	public  operator this % (that:TriMatrix(N)):Vector(that.N) =
		this.clone().transMult(that);

	//Left-side operand overload
 	public  operator (that:Matrix{self.N==this.N}) % this :Vector(that.M) =
 		Vector.make(that.M).mult(that, this as Vector(that.N), false);
 	
 	public  operator (that:DenseMatrix{self.N==this.N}) % this :Vector(that.M) =
 		Vector.make(that.M).mult(that, this as Vector(that.N), false);
 	public  operator (that:SymMatrix{self.N==this.N}) % this :Vector(that.M) =
 		Vector.make(that.M).mult(that, this, false);
 	public  operator (that:TriMatrix{self.N==this.N}) % this :Vector(that.M) =
 		this.clone().mult(that);
 
 	//======================================================
 	/**
 	 * Inverse of a vector: Nx1
 	 */
 	public def inverse():Vector(this) = this.cellDivBy(1.0);

 	/**
 	 * Norm of a vector: Nx1
 	 */
 	public def norm():Double = 
 		BLAS.compNorm(this.N, this.d);
 	
 	
 	// Euclidean distance
 	public static def compDistance(a:Vector, b:Vector(a.N)):Double {
 		var d:Double = 0.0;
 		for (var i:Int=0; i<a.N; i++)
 			d += (a.d(i)-b.d(i)) * (a.d(i)-b.d(i));
 		return Math.sqrt(d);
 	}
 	
 	public def compDistance(V:Vector(N)):Double =
 		compDistance(this, V);
 	
 	public static def norm(a:Vector, b:Vector(a.N))=compDistance(a,b);
 	public def norm(V:Vector(N)) = compDistance(this, V);
 	
 	// Sum
 	public def sum():Double {
 		var s:Double = 0.0;
 		for (var i:Int=0; i<N; i++) s+= this.d(i);
 		return s;
 	}
 	
 	// Solver
 	/**
 	 * Solve equation A &#42 x = this, wehre A is triangular matrix.
 	 * The solution x is overwritten on this object
 	 * 
 	 * @param A   Triangular matrix
 	 * @return    this object, overwritten by solution vector.
 	 */
 	public def solveTriMultSelf(A:TriMatrix(N,N)):Vector(this) {
 		DenseMatrixBLAS.solveTriMultVec(A, this);
 		return this;
 	}
 	
	//======================================================
 	public def likeMe(v:Vector):Boolean {
 		return this.N==v.N;
 	}
 	
	//-------------------------------------------------------------------
	public  def equals(v:Vector(N)):Boolean {
		for (var c:Int=0; c< N; c++)
			if (MathTool.isZero(this.d(c) - v.d(c)) == false) {
				Console.OUT.println("Diff found [" + c + "] : "+ 
									this.d(c) + " <> "+ v.d(c));
				return false;
			}
		return true;
	}
	
	public  def equals(mat:Matrix) :Boolean {
		if (mat.M == 1 && mat.N == this.N) {
			for (var c:Int=0; c<N; c++) {
				if (MathTool.isZero(this.d(c) - mat(0,c)) == false) {
					Console.OUT.println("Diff found [" + c + "] : "+ 
							this.d(c) + " <> "+ mat(0,c));
					return false;
				}
			}
			return true;
		}
		
		if (mat.N == 1 && mat.M == this.N) {
			for (var c:Int=0; c<N; c++) {
				if (MathTool.isZero(this.d(c) - mat(c, 0)) == false) {
					Console.OUT.println("Diff found [" + c + "] : "+ 
							this.d(c) + " <> "+ mat(c, 0));
					return false;
				}
			}
			return true;
		}
		return false;		
	}
	
	public  def equals(v:Double):Boolean {
		for (var c:Int=0; c< N; c++)
			if (MathTool.isZero(this.d(c) - v) == false) {
				Console.OUT.println("Diff found [" + c + "] : "+ 
						this.d(c) + " <> "+ v);
				return false;
			}
		return true;
	}
	
	//======================================================
	public def toString():String {
		var output:String="Vector("+this.N+") [ ";
		for (var i:Int=0; i<N; i++)
			output += this.d(i).toString()+" ";
		output += "]\n";
		return output;
	}
	/**
	   Print out all elements in vector
	 */
	public def print(str:String):void {
		Console.OUT.println(str);
		this.print();
	}

    public def print():void {
		Console.OUT.println(this.toString());
		Console.OUT.flush();
    }
}

