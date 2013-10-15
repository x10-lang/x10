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

import x10.util.StringBuilder;

import x10.matrix.blas.BLAS;
import x10.matrix.blas.DenseMatrixBLAS;

public type Vector(m:Long)=Vector{self.M==m};
public type Vector(v:Vector)=Vector{self==v};

/**
 * Class Vector uses a Rail of length M as data storage)

 <p> This implemnetation has a dense, single-place representation.

 <p> Methods include 
 <p> 1) product of two vectors: Mx1 * 1xM,
 <p> 2) product of vector and a scalar: Mx1 * 1,
 <p> 3) product of a scalar and a vector: 1 * 1xM,
 <p> 4) addition of two vectors: Mx1 + Mx1,
 <p> 5) subtraction between two vectors: Mx1 - Mx1,
 <p> 6) addition of a vector and a scalar: Mx1 + 1,
 <p> 7) subtraction of a scalar from a vector: Mx1 - 1,
 <p> 8) inverse of a vector: Mx1
 <p> 9) norm of a vector: Mx1
 */
public class Vector(M:Long) implements (Long) => Double {
    /** Vector data */
    public val d:Rail[Double]{self.size==M};

    public def this(x:Rail[Double]):Vector(x.size) {
    	property(x.size);
    	this.d=x;
    }

    /** Copy constructor */    
    public def this(src:Vector):Vector(src.M) {
    	property(src.M);
    	this.d = new Rail[Double](src.d);
    }

    public static def make(n:Long, v:Double) {
		val d = new Rail[Double](n, v);
		return new Vector(d);
    }

    public static def make(n:Long) {
	    val d = new Rail[Double](n);
		return new Vector(d);
    }
    
    /**
     * Initialize all elements of the vector with a constant value.
     * @param  iv 	the constant value
     */	
    public def init(iv:Double): Vector(this) {
    	for (var i:Long=0; i<M; i++)	
    		this.d(i) = iv;
    	return this;
    }

    /**
     * Initialize vector with random values between 0.0 and 1.0.
     */	
    public def initRandom(): Vector(this) {
    	val rgen = RandTool.getRandGen();
    	for (var i:Long=0; i<M; i++) {
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
    public def initRandom(lb:Long, ub:Long): Vector(this) {
    	val len = Math.abs(ub-lb)+1;
    	val rgen = RandTool.getRandGen();
    	//val ll = M*M / 100;
    	for (var i:Long=0; i<M; i++) {
    		this.d(i) = rgen.nextLong(len)+lb;
    	}
    	return this;
    }
    
    /**
     * Init with function
     */
    public def init(f:(Long)=>Double): Vector(this) {
    	for (var i:Long=0; i<M; i++)
    		this.d(i) = f(i);
    	return this;
    }

    public def rail():Rail[Double] = d;

    public def clone():Vector(M) {
        val nv = new Rail[Double](this.d);
        return new Vector(nv);
    }

	// Data access and set
    public def apply(i:Long)=d(i);
    public operator this(i:Long) = d(i);

	public operator this(i:Long)=(v:Double):Double {
		this.d(i) = v;
		return v;
	}
	
    /** 
     * Subset of vector from off with length of len
     */
    public def subset(off:Long, len:Long):Vector(len) {
		val na = new Rail[Double](len);
		Rail.copy(this.d, off, na, 0L, len);
		return new Vector(na);
	}
	
	public  def reset():void {
		for (var i:Long=0; i< M; i++) this.d(i) = 0.0;
	}

	// Copy all data from vector v to local at dst_off
	public static def copyTo(src:Vector, soff:Long, dst:Vector, doff:Long, len:Long) {
		Debug.assure(soff+len<=src.M && doff+len<=dst.M, "Buffer overflow in vector copy");
		Rail.copy(src.d, soff, dst.d, doff, len);
	}
	
	// Copy part of data from vector v to local starting from index 0
	public  def copyTo(v:Vector) {
		copyTo(this, 0L, v, 0L, this.M);
	}
	
	public def copyTo(mat:DenseMatrix) {
		Rail.copy(this.d, 0L, mat.d, 0L, M);
	}


	// Cell-wise operations
	
	/**
	 * Product of a vector and a scalar: Mx1 * 1
	 */
    public  def scale(a:Double) :Vector(this) {
		for (var i:Long=0; i < M; ++i)
			this.d(i) = a * this.d(i);
		return this;
    }

    /**
     * this = V * dv + this
     */
    public def scaleAdd(V:Vector(M), dv:Double): Vector(this) {
    	for (var i:Long=0; i < M; ++i) 
    		this.d(i) += dv * V.d(i);	
    	return this;
    }

    public def scaleAdd(dv:Double, V:Vector(M)) = scaleAdd(V, dv);

	/**
	 * Cell-wise mulitply of two vectors
	 */
    public def cellMult(V:Vector(M)): Vector(this) {
        for (var i:Long=0; i < M; ++i) 
			this.d(i) *= V.d(i);
        return this;
    }
	 

	/**
	 * Addition of two vectors: Mx1 + Mx1
	 */
    public def cellAdd (V:Vector(M)):Vector(this) {
		for (var i:Long=0; i < M; ++i) 	this.d(i) += V.d(i);
		return this;
    }

    public def cellAdd (d:Double):Vector(this) {
    	for (var i:Long=0; i < M; ++i) 	this.d(i) += d;
    	return this;
    }


	/** 
	 * Subtract vector B from this vector
	 */
    public  def cellSub(B:Vector(M)):Vector(this) {
		for (var i:Long=0; i < M; ++i) 
			this.d(i) -= B.d(i);
		return this;
    }

	/**
	 * Subtract the scalar d from this vector
	 */
    public  def cellSub(d:Double):Vector(this) {
		for (var i:Long=0; i < M; ++i) this.d(i) -= d;
		return this;
    }
    
    public def cellSubFrom(d:Double):Vector(this) {
    	for (var i:Long=0; i < M; ++i) this.d(i) = d - this.d(i);
    	return this;   	
    }
    

    /**
     * cellwise division: this = dv / this;
     */
    public  def cellDiv(dv:Double):Vector(this) {
    	for (var i:Long=0; i < M; ++i) this.d(i) /= dv;
    	return this;
    }

    public def cellDiv(v:Vector(this.M)):Vector(this) {
    	for (var i:Long=0; i< M; ++i) {
    		this.d(i) /= v.d(i);
    	}
    	return this;
    }
        
    public def cellDivBy(dv:Double) : Vector(this) {
    	for (var i:Long=0; i < M; ++i) this.d(i) = dv / this.d(i);
    	return this;    	
    }

    /**
	 * Product transition of a vector: Mx1 * (Mx1)^T
     * Return this^T * x.
     */
    public def blasTransProduct(x:Vector):Double =
		BLAS.compDotProd(this.M, this.d, x.d);
	

	public def dotProd(v:Vector(M)):Double {
		var d:Double = 0.0;
		for(var i:Long=0; i<M; i++) d += this.d(i) * v.d(i);
		return d;
	}
	
	public def mult(v:Vector(M), dv:Double):Vector(this) {
		v.copyTo(this);
		return scale(dv);
	}

	// Using Blas routines: self = op(A)* b, self += op(A) * b,

	public def mult(A:Matrix(M), B:Vector(A.N), plus:Boolean): Vector(this) =
		VectorMult.comp(A, B, this, plus);
 
	public def transMult(A:Matrix{self.N==this.M}, B:Vector(A.M), plus:Boolean) =
		VectorMult.comp(B, A, this, plus);
	
	public def mult(A:Matrix(M), B:Vector(A.N)): Vector(this) = VectorMult.comp(A, B, this, false);
	public def transMult(A:Matrix{self.N==this.M}, B:Vector(A.M)) =	VectorMult.comp(B, A, this, false);


	public def mult(B:Vector, A:Matrix(B.M,this.M), plus:Boolean)      = 
		VectorMult.comp(B, A, this, plus);
	public def multTrans(B:Vector, A:Matrix(this.M,B.M), plus:Boolean) = 
		VectorMult.comp(A, B, this, plus);

	public def mult(B:Vector, A:Matrix(B.M,this.M))      = VectorMult.comp(B, A, this, false);
	public def multTrans(B:Vector, A:Matrix(this.M,B.M)) = VectorMult.comp(A, B, this, false);
	

	// Dense-vector multiply

	public def mult(A:DenseMatrix(this.M), B:Vector(A.N), plus:Boolean) = 
		VectorMult.comp(A, B, this, plus);	
	public  def transMult(A:DenseMatrix{self.N==this.M}, B:Vector(A.M), plus:Boolean) = 
		VectorMult.comp(B, A, this, plus);
	
	public def mult(A:DenseMatrix(this.M), B:Vector(A.N))               = VectorMult.comp(A, B, this, false);	
	public def transMult(A:DenseMatrix{self.N==this.M}, B:Vector(A.M)) = VectorMult.comp(B, A, this, false);


	public def mult(B:Vector, A:DenseMatrix(B.M,this.M), plus:Boolean)      = 
		VectorMult.comp(B, A, this, plus);
	public def multTrans(B:Vector, A:DenseMatrix(this.M,B.M), plus:Boolean) = 
		VectorMult.comp(A, B, this, plus);
		
	public def mult(B:Vector, A:DenseMatrix(B.M,this.M))      =VectorMult.comp(B, A, this, false);
	public def multTrans(B:Vector, A:DenseMatrix(this.M,B.M)) =VectorMult.comp(A, B, this, false);

	// Symmetric-vector multiply

	public  def mult(A:SymDense(this.M), B:Vector(A.N), plus:Boolean) =
		VectorMult.comp(A, B, this, plus);
	
	public  def transMult(A:SymDense(this.M), B:Vector(A.N), plus:Boolean) =
		VectorMult.comp(B, A, this, plus);

	public  def mult(A:SymDense(this.M), B:Vector(A.N))      = VectorMult.comp(A, B, this, false);
	public  def transMult(A:SymDense(this.M), B:Vector(A.N)) = VectorMult.comp(B, A, this, false);


	public def mult(B:Vector, A:SymDense(B.M,this.M), plus:Boolean)      = 
		VectorMult.comp(B, A, this, plus);
	public def multTrans(B:Vector, A:SymDense(this.M,B.M), plus:Boolean) = 
		VectorMult.comp(A, B, this, plus);

	public def mult(B:Vector, A:SymDense(B.M,this.M))      = VectorMult.comp(B, A, this, false);
	public def multTrans(B:Vector, A:SymDense(this.M,B.M)) = VectorMult.comp(A, B, this, false);


	// Triangular-vector multiply

	// this = A * this
	public  def mult(A:TriDense(this.M)) =
		VectorMult.comp(A, this);
	
	public  def transMult(A:TriDense(this.M)) =
		VectorMult.comp(this, A);
	


	// Operand overloading

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
		VectorMult.comp(this, that, Vector.make(that.N), false) as Vector(that.N);
	public  operator this % (that:DenseMatrix(M)) =
		VectorMult.comp(this, that, Vector.make(that.N), false) as Vector(that.N);
	public  operator this % (that:SymDense(M)) =
		VectorMult.comp(this, that, Vector.make(that.N), false) as Vector(that.N);
	public  operator this % (that:TriDense(M)) =
		VectorMult.comp(this.clone(), that) as Vector(that.N);

	//Left-side operand overload
 	public  operator (that:Matrix{self.N==this.M}) % this =
 		VectorMult.comp(that, this, Vector.make(that.M), false) as Vector(that.M);
 	public  operator (that:DenseMatrix{self.N==this.M}) % this =
 		VectorMult.comp(that, this, Vector.make(that.M), false) as Vector(that.M);
 	public  operator (that:SymDense{self.N==this.M}) % this =
 		VectorMult.comp(that, this, Vector.make(that.M), false) as Vector(that.M);
 	public  operator (that:TriDense{self.N==this.M}) % this =
 		VectorMult.comp(that, this.clone()) as Vector(that.M);

 	/**
 	 * Inverse of a vector: Mx1
 	 */
 	public def inverse():Vector(this) = this.cellDivBy(1.0);

 	/**
 	 * Norm of a vector: Mx1
 	 */
 	public def norm():Double = 
 		BLAS.compNorm(this.M, this.d);
 	
 	
 	/*
     * Euclidean distance between two vectors
     */
 	public static def compDistance(a:Vector, b:Vector(a.M)):Double {
 		var d:Double = 0.0;
 		for (var i:Long=0; i<a.M; i++)
 			d += (a.d(i)-b.d(i)) * (a.d(i)-b.d(i));
 		return Math.sqrt(d);
 	}
 	
 	/*
     * Euclidean distance between this vector and another vector V
     */
 	public def compDistance(V:Vector(M)):Double =
 		compDistance(this, V);
 	
 	public static def norm(a:Vector, b:Vector(a.M))=compDistance(a,b);
 	public def norm(V:Vector(M)) = compDistance(this, V);

    /**
     * Compute the maximum absolute value of all elements of this vector
     * (the vector norm with p==Inf)
     *
     * @return max absolute value of any element
     */
    public def maxNorm():Double {
 		var max:Double = 0.0;
 		for (var i:Long=0; i<M; i++) max = Math.max(Math.abs(d(i)), max);
        return max;
    }
 	
 	// Sum
 	public def sum():Double {
 		var s:Double = 0.0;
 		for (var i:Long=0; i<M; i++) s+= this.d(i);
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
 	public def solveTriMultSelf(A:TriDense(M,M)):Vector(this) {
 		DenseMatrixBLAS.solveTriMultVec(A, this);
 		return this;
 	}
 	

 	public def likeMe(v:Vector):Boolean {
 		return this.M==v.M;
 	}
 	

 	public def equals(dval:Double):Boolean {
 		for (var c:Long=0; c< M; c++)
 			if (MathTool.isZero(this.d(c) - dval) == false) {
 				Console.OUT.println("Diff found [" + c + "] : "+ 
 						this.d(c) + " <> "+ dval);
 				return false;
 			}
 		return true;
 	}
 	
	public def equals(v:Vector(M)):Boolean {
		for (var c:Long=0; c< M; c++)
			if (MathTool.isZero(this.d(c) - v.d(c)) == false) {
				Console.OUT.println("Diff found [" + c + "] : "+ 
									this.d(c) + " <> "+ v.d(c));
				return false;
			}
		return true;
	}
	
	public def equals(mat:Matrix) :Boolean {
		if (mat.M == 1L && mat.N == this.M) {
			for (var c:Long=0; c<M; c++) {
				if (MathTool.isZero(this.d(c) - mat(0,c)) == false) {
					Console.OUT.println("Diff found [" + c + "] : "+ 
							this.d(c) + " <> "+ mat(0,c));
					return false;
				}
			}
			return true;
		}
		
		if (mat.N == 1L && mat.M == this.M) {
			for (var c:Long=0; c<M; c++) {
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
	

	/**
	 * Replace each element x_i in this vector with the exponential e^(x_i).
     *
	 * @return 		result ("this" instance)
	 */
    public def exp():Vector(this) {
        for (var i:Long=0; i<M; i++) {
            d(i) = Math.exp(d(i));
        }
        return this;
    }

	public def toString():String {
		val output=new StringBuilder();
		output.add("Vector("+this.M+") [ ");
		for (var i:Long=0; i<M; i++)
			output.add(this.d(i).toString()+" ");
		output.add("]\n");
		return output.toString();
	}
}

