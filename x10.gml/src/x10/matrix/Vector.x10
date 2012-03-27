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

import x10.matrix.blas.BLAS;
import x10.matrix.blas.DenseMatrixBLAS;

public type Vector(m:Int)=Vector{self.M==m};
public type Vector(v:Vector)=Vector{self==v};


/**
 * Class Vector uses an M &#42 1 array as data staorage)

 <p> This implemnetation has a dense, single-place representation.

 <p> Methods include 
 <p> 1) product of two vectors: Mx1 * 1xM,
 <p> 2) product of vector and a scalar: Mx1 * 1,
 <p> 3) product of a scalar and a vector: 1 * 1xM,
 <p> 4) addition of two vectors: Mx1 + Mx1,
 <p> 5) substraction between two vectors: Mx1 - Mx1,
 <p> 6) addition of a vector and a scalar: Mx1 + 1,
 <p> 7) substraction of a scalar from a vector: Mx1 - 1,
 <p> 8) inverse of a vector: Mx1
 <p> 9) norm of a vector: Mx1

 */
public class Vector(M:Int) implements (Int) => Double {

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
    	for (var i:Int=0; i<M; i++)	
    		this.d(i) = iv;
    	return this;
    }

    /**
     * Initialize vector with random values between 0.0 and 1.0.
     */	
    public def initRandom(): Vector(this) {
    	val rgen = RandTool.getRandGen();
    	for (var i:Int=0; i<M; i++) {
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
    	//val ll = M*M / 100;
    	for (var i:Int=0; i<M; i++) {
    		this.d(i) = rgen.nextInt(len)+lb;
    	}
    	return this;
    }
    
    /**
     * Init with function
     */
    public def init(f:(int)=>Double): Vector(this) {
    	for (var i:Int=0; i<M; i++)
    		this.d(i) = f(i);
    	return this;
    }
    
    
	//======================================================
    public def rail():Array[Double](1) = d;

	public  def clone():Vector(M) {
		val nv = new Array[Double](M);
		Array.copy(this.d, 0, nv, 0, M);
		return new Vector(M, nv);
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
		for (var i:Int=0; i< M; i++) this.d(i) = 0.0;
	}
	//----------------------------------
	// Copy all data from vector v to local at dst_off
	public static def copyTo(src:Vector, soff:Int, dst:Vector, doff:Int, len:Int) {
		Debug.assure(soff+len<src.M && doff+len<dst.M, "Buffer overflow in vector copy");
		Array.copy(src.d, soff, dst.d, doff, len);
	}
	
	// Copy part of data from vector v to local starting from index 0
	public  def copyTo(v:Vector) {
		copyTo(this, 0, v, 0, this.M);
	}
	
	public def copyTo(mat:DenseMatrix) {
		Array.copy(this.d, 0, mat.d, 0, M);
	}

	//------------------------------------------------------------------
	// Cell-wise operations
	//------------------------------------------------------------------
	
	/**
	 * Product of a vector and a scalar: Mx1 * 1
	 */
    public  def scale(a:Double) :Vector(this) {
		for (var i:Int=0; i < M; ++i)
			this.d(i) = a * this.d(i);
		return this;
    }
        
	/**
	 * Cell-wise mulitply of two vectors
	 */
    public def cellMult(V:Vector(M)): Vector(this) {
        for (var i:Int=0; i < M; ++i) 
			this.d(i) *= V.d(i);
        return this;
    }
	    
    
	//======================================================
	/**
	 * Addition of two vectors: Mx1 + Mx1
	 */
    public def cellAdd (V:Vector(M)):Vector(this) {
		for (var i:Int=0; i < M; ++i) 	this.d(i) += V.d(i);
		return this;
    }

    public def cellAdd (d:Double):Vector(this) {
    	for (var i:Int=0; i < M; ++i) 	this.d(i) += d;
    	return this;
    }


	//======================================================
	/** 
	 * Substraction between two vectors
	 */
    public  def cellSub(B:Vector(M)):Vector(this) {
		for (var i:Int=0; i < M; ++i) 
			this.d(i) -= B.d(i);
		return this;
    }

	/**
	 * Substraction a scalar from a vector: Mx1 - 1
	 */
    public  def cellSub(d:Double):Vector(this) {
		for (var i:Int=0; i < M; ++i) this.d(i) -= d;
		return this;
    }
    
    public def cellSubFrom(d:Double):Vector(this) {
    	for (var i:Int=0; i < M; ++i) this.d(i) = d - this.d(i);
    	return this;   	
    }
    
	//======================================================
    /**
     * cellwise division: this = dv / this;
     */
    public  def cellDiv(dv:Double):Vector(this) {
    	for (var i:Int=0; i < M; ++i) this.d(i) /= dv;
    	return this;
    }
    //

    public def cellDiv(v:Vector(this.M)):Vector(this) {
    	for (var i:Int=0; i< M; ++i) {
    		this.d(i) /= v.d(i);
    	}
    	return this;
    }
        
    public def cellDivBy(dv:Double) : Vector(this) {
    	for (var i:Int=0; i < M; ++i) this.d(i) = dv / this.d(i);
    	return this;    	
    }
	//===============================================
    /**
	 * Pruduct transition of a vector: Mx1 * (Mx1)^T
     * Return this^T * x.
     */
    public def blasTransProduct(x:Vector):Double =
		BLAS.compDotProd(this.M, this.d, x.d);
	

	public def mult(v:Vector(M)):Double {
		var d:Double = 0.0;
		for(var i:Int=0; i<M; i++) d += this.d(i) * v.d(i);
		return d;
	}
	//======================================================
	
	
	//-------------------------------------------------------------------
	// Using Blas routines: self = op(A)* b, self += op(A) * b,
	//-------------------------------------------------------------------
	public def mult(A:Matrix(M), B:Vector(A.N), plus:Boolean): Vector(this) =
		VectorMult.mult(A, B, this, plus);
 
	public def transMult(A:Matrix{self.N==this.M}, B:Vector(A.M), plus:Boolean) =
		VectorMult.mult(B, A, this, plus);
	
	//------------------------
	public def mult(B:Vector, A:Matrix(B.M,this.M), plus:Boolean)      = 
		VectorMult.mult(B, A, this, plus);
	public def multTrans(B:Vector, A:Matrix(this.M,B.M), plus:Boolean) = 
		VectorMult.mult(A, B, this, plus);
	
	//-------------------------------------------------------------------
	// Dense-vector multiply
	//-------------------------------------------------------------------
	public def mult(A:DenseMatrix(this.M), B:Vector(A.N), plus:Boolean) = 
		VectorMult.mult(A, B, this, plus);	
	public  def transMult(A:DenseMatrix{self.N==this.M}, B:Vector(A.M), plus:Boolean) = 
		VectorMult.mult(B, A, this, plus);
	
	//-----------------------------
	public def mult(B:Vector, A:DenseMatrix(B.M,this.M), plus:Boolean)      = 
		VectorMult.mult(B, A, this, plus);
	public def multTrans(B:Vector, A:DenseMatrix(this.M,B.M), plus:Boolean) = 
		VectorMult.mult(A, B, this, plus);
		
	//-------------------------------------------------------------------
	// Symmetric-vector multiply
	//-------------------------------------------------------------------
	public  def mult(A:SymMatrix(this.M), B:Vector(A.N), plus:Boolean) =
		VectorMult.mult(A, B, this, plus);
	
	public  def transMult(A:SymMatrix(this.M), B:Vector(A.N), plus:Boolean) =
		VectorMult.mult(B, A, this, plus);
	//--------------
	public def mult(B:Vector, A:SymMatrix(B.M,this.M), plus:Boolean)      = 
		VectorMult.mult(B, A, this, plus);
	public def multTrans(B:Vector, A:SymMatrix(this.M,B.M), plus:Boolean) = 
		VectorMult.mult(A, B, this, plus);

	//-------------------------------------------------------------------
	// Triangular-vector multiply
	//-------------------------------------------------------------------
	// this = A * this
	public  def mult(A:TriMatrix(this.M)) =
		VectorMult.mult(A, this);
	
	public  def transMult(A:TriMatrix(this.M)) =
		VectorMult.mult(this, A);
	

	//======================================================
	// Operand overloading
	//======================================================
	// Operator add
	public  operator this + (that:Vector(M)) = this.clone().cellAdd(that) as Vector(M);
	public  operator this + (dv:Double)      = this.clone().cellAdd(dv)   as Vector(M);
	// Operator sub
	public  operator this - (that:Vector(M)) = this.clone().cellSub(that)   as Vector(M);
	public  operator this - (dv:Double)      = this.clone().cellSub(dv)     as Vector(M);
	public  operator (dv:Double) - this      = this.clone().cellSubFrom(dv) as Vector(M);
	
	// Operator cellwise multiply
	public  operator this * (dv:Double)      = this.clone().scale(dv)      as Vector(M);
	public  operator (dv:Double) * this      = this.clone().scale(dv)      as Vector(M);
	public  operator this * (that:Vector(M)) = this.clone().cellMult(that) as Vector(M);

	// Operator cellwise div
	public  operator this / (dv:Double)           = this.clone().cellDiv(dv)   as Vector(M);
	public  operator this / (that:Vector(this.M)) = this.clone().cellDiv(that) as Vector(M);
	public  operator (dv:Double) / this           = this.clone().cellDivBy(dv) as Vector(M);

	//Righ-side Operand overload
	public  operator this % (that:Matrix(M)) = 
		VectorMult.mult(this, that, Vector.make(that.N), false) as Vector(that.N);
	public  operator this % (that:DenseMatrix(M)) =
		VectorMult.mult(this, that, Vector.make(that.N), false) as Vector(that.N);
	public  operator this % (that:SymMatrix(M)) =
		VectorMult.mult(this, that, Vector.make(that.N), false) as Vector(that.N);
	public  operator this % (that:TriMatrix(M)) =
		VectorMult.mult(this.clone(), that) as Vector(that.N);

	//Left-side operand overload
 	public  operator (that:Matrix{self.N==this.M}) % this =
 		VectorMult.mult(that, this, Vector.make(that.M), false) as Vector(that.M);
 	public  operator (that:DenseMatrix{self.N==this.M}) % this =
 		VectorMult.mult(that, this, Vector.make(that.M), false) as Vector(that.M);
 	public  operator (that:SymMatrix{self.N==this.M}) % this =
 		VectorMult.mult(that, this, Vector.make(that.M), false) as Vector(that.M);
 	public  operator (that:TriMatrix{self.N==this.M}) % this =
 		VectorMult.mult(that, this.clone()) as Vector(that.M);
 
 	//========================================================================
 	// Matrix multiflies with part of vector and store result in part of vector
 	//========================================================================
 	
 	
 	
 	
 	//======================================================
 	/**
 	 * Inverse of a vector: Mx1
 	 */
 	public def inverse():Vector(this) = this.cellDivBy(1.0);

 	/**
 	 * Norm of a vector: Mx1
 	 */
 	public def norm():Double = 
 		BLAS.compNorm(this.M, this.d);
 	
 	
 	// Euclidean distance
 	public static def compDistance(a:Vector, b:Vector(a.M)):Double {
 		var d:Double = 0.0;
 		for (var i:Int=0; i<a.M; i++)
 			d += (a.d(i)-b.d(i)) * (a.d(i)-b.d(i));
 		return Math.sqrt(d);
 	}
 	
 	public def compDistance(V:Vector(M)):Double =
 		compDistance(this, V);
 	
 	public static def norm(a:Vector, b:Vector(a.M))=compDistance(a,b);
 	public def norm(V:Vector(M)) = compDistance(this, V);
 	
 	// Sum
 	public def sum():Double {
 		var s:Double = 0.0;
 		for (var i:Int=0; i<M; i++) s+= this.d(i);
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
 	public def solveTriMultSelf(A:TriMatrix(M,M)):Vector(this) {
 		DenseMatrixBLAS.solveTriMultVec(A, this);
 		return this;
 	}
 	
	//======================================================
 	public def likeMe(v:Vector):Boolean {
 		return this.M==v.M;
 	}
 	
	//-------------------------------------------------------------------
	public  def equals(v:Vector(M)):Boolean {
		for (var c:Int=0; c< M; c++)
			if (MathTool.isZero(this.d(c) - v.d(c)) == false) {
				Console.OUT.println("Diff found [" + c + "] : "+ 
									this.d(c) + " <> "+ v.d(c));
				return false;
			}
		return true;
	}
	
	public  def equals(mat:Matrix) :Boolean {
		if (mat.M == 1 && mat.N == this.M) {
			for (var c:Int=0; c<M; c++) {
				if (MathTool.isZero(this.d(c) - mat(0,c)) == false) {
					Console.OUT.println("Diff found [" + c + "] : "+ 
							this.d(c) + " <> "+ mat(0,c));
					return false;
				}
			}
			return true;
		}
		
		if (mat.N == 1 && mat.M == this.M) {
			for (var c:Int=0; c<M; c++) {
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
		for (var c:Int=0; c< M; c++)
			if (MathTool.isZero(this.d(c) - v) == false) {
				Console.OUT.println("Diff found [" + c + "] : "+ 
						this.d(c) + " <> "+ v);
				return false;
			}
		return true;
	}
	
	//======================================================
	public def toString():String {
		var output:String="Vector("+this.M+") [ ";
		for (var i:Int=0; i<M; i++)
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

