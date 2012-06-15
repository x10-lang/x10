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
import x10.util.StringBuilder;

public type Matrix(M:Int)=Matrix{self.M==M};
public type Matrix(M:Int, N:Int)=Matrix{self.M==M, self.N==N};
public type Matrix(C:Matrix)=Matrix{self==C};

/**
 * The base class, has many specialized representations, e.g. for a
 * dense matrix, sparse matrix, dense/sparse block matrix,
 * distributed dense/sparse matrix, and duplicated dense/sparse matrix, etc.
 * 
 * <p> Operations supported by this library includes scaling, add, sub,
 * cellwise multiplication, and matrix multiplication.  In order to reus
 * memory space, matrix operation methods requires all inputs and output
 * matrix with memory allocated. Output matrix also serves as the method 
 * invocation instance, therefore, allowing chain operations. 
 * Only operators allocate memory within their implementations.
 * 
 * <p> In most cases, outputs of matrix operations are stored in dense matrix 
 * or matrix built with dense matrices. 
 * Only few operations allow using the sparse matrix as output, such as scaling 
 * operation.
 * Sparse matrix has confined memory space, and handling more nonzero elements 
 * than its storage space is complex. 
 * Therefore, this library generally does not support to use sparse matrix to store
 * results.
 * 
 * <p> One may first develop a sequential program, and then introduce
 * parallelization in place by merely choosing to distribute the
 * arrays involved and use finish, async and etc. That is, the
 * parallel, distributed program looks very similar to the sequential
 * program. Indeed, the parallelization scheme is much more easily
 * described in async-finish style rather than in SPMD style.
 * 
 * <p> X10's object-oriented features are adequate to describe the nature of the 
 * specialization scheme. Constrained types can be used to get more static assurances about
 * the correctness of the code (the compiler is doing more reasoning
 * on the programmer's behalf).  
 * 
 * <p> The program can be easily interfaced with native code,
 * e.g. for Cholesky factorization, in those cases in which efficient
 * native libraries are already available.
 * 
 * <p> Author vj and Jeff
 * 
 */
public abstract class Matrix(M:Int, N:Int) {
	
	//=====================================================================
	/**
	 * Constructor of matrix instance. 
	 * 
	 * @param m   the number of rows in matrix
	 * @param n   the number of columns in matrix
	 * @return    instance of matrix.
	 */	
	public def this(m:Int, n:Int) {
		property(m,n);
	}
	
	//=====================================================================
	// Memory allocation
	//=====================================================================
	
	/**
	 * Allocate memory space for a new matrix object.
	 * This non-static abstract method. For static method use make()
	 * 
	 * @param m   rows of matrix
	 * @param n   columns of matrix
	 * @return    instance of matrix
	 */
	abstract public def alloc(m:Int, n:Int): Matrix(m,n);
	
	
	/**
	 * Return a copy of this matrix instance. It should share no mutable data with this.
	 */
	abstract public def clone():Matrix(M,N);

	/**
	 * Initial matrix data with function, mapping (row index, column index) to double. 
	 */
	abstract public def init(f:(Int, Int)=>Double):Matrix(this);
	abstract public def init(dv:Double):Matrix(this);
	abstract public def initRandom():Matrix(this);
	abstract public def initRandom(lowBound:Int, upperBound:Int):Matrix(this);
	
	//======================================================================
	// Data copy and reset 
	//======================================================================
	
	/**
	 * Reset all element values in matrix to 0.
	 */
	abstract public def reset():void;
	
	/**
	 * Copy all elements to the target dense matrix which has the same matrix dimensions.
	 * 
	 * @param dst    the target dense matrix.		
	 */
	abstract public def copyTo(dst:DenseMatrix(M,N)):void;
	
	/**
	 * Copy all elements to a dense matrix instance. 
	 * 
	 * <p>This method could be slow due to accessing all matrix elements. 
	 * This method can be used for debugging or testing purpose.
	 * 
	 * @return		a new dense matrix instance
	 */
	public def toDense():DenseMatrix(M,N) {
		val dm = DenseMatrix.make(M,N);
		for (var c:Int =0; c<this.N; c++)
			for (var r:Int=0; r<this.M; r++)
				dm(r,c)=this(r,c);
		
		return dm;
	}
	
	/**
	 * Copy data to another matrix
	 */
	abstract public def copyTo(that:Matrix(M,N)): void;
	
	//======================================================================
	// Data access and set
	//======================================================================
	
	/**
	 * Access an element at the specified location in matrix. 
	 * Note that x should be in the range 0..M-1, and y should lie in the range 0..N-1
	 * 
	 * @param x   row index 
	 * @param y   column index
	 * @return    element value
	 * 
	 */
	abstract public operator this(x:Int, y:Int):Double;
	
	/**
	 * Set the element's value at the specified row and column in matrix.
	 * 
	 * @param x   row index 
	 * @param y   column index
	 * @param d   the new value for the element
	 */
	abstract public operator this(x:Int, y:Int)=(d:Double):Double;
	
	
	/**
	 * Access an element of the matrix using columnwise positioning method.
	 * Note that "a" should be in the range 0..(M*N-1).
	 * 
	 * @param a   index of the column major position in the storage
	 * @return    element value
	 */
	public operator this(a:Int):Double = this(a%M, a/M);
	
	/**
	 * Set an element's value at specified location using column major positioning method.
	 * 
	 * @param a   index of column major position in the storage
	 * @param d   new element value
	 */
	public operator this(a:Int)=(d:Double):Double {
		this(a%M, a/N) = d;
		return d;
	}
	
	//=====================================================================
	// Cellwise operations
	//=====================================================================
	
	/**
	 * Scale all elements in the matrix by a specified double floating point number. 
	 * 
	 * @param 	alpha	scaling factor
	 * @return         	this instance which holds the result
	 */
	abstract public def scale(alpha:Double): Matrix(this);
	
	/**
	 * Scale all elements in the matrix by a specified integer number. 
	 * 
	 * @param alpha    scaling factor
	 * @return         this instance
	 */
	public def scale(alpha:Int) = scale(alpha as Double);
	
	//---------------------------------    
	// Addition operation
	//---------------------------------
	/**
	 * Cellwise addition this = this + x.
	 * 
	 * @param x   input matrix
	 * @return    this instance which stores the result
	 */
	abstract public def cellAdd(x:Matrix(M,N)): Matrix(this); 

	/**
	 * Cellwise add
	 */
	abstract public def cellAdd(d:Double): Matrix(this); 
	
	/**
	 * Cellwise operation x = x + this 
	 * 
	 * @param x   input and output matrix
	 * @return    this instance
	 */
	abstract protected def cellAddTo(x:DenseMatrix(M,N)):DenseMatrix(x);
	
	//---------------------------
	// Subtract operation
	//---------------------------
	
	/**
	 * Cell-wise subtraction this = this - x.
	 * 
	 * @param x   the second input matrix in subtraction
	 * @return    this instance
	 */
	abstract public def cellSub(x:Matrix(M,N)): Matrix(this); 
	
	/**
	 * Cell-wise subtraction  x = x - this.
	 * 
	 * @param x   first input and output of subtraction
	 * @return    result matrix instance
	 */
	abstract protected def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x);
	
	/**
	 * Cell-wise subtraction this = x - this
	 */
	abstract protected def cellSubFrom(dv:Double):Matrix(this);
	
	//----------------------------------
	// Cell-wise matrix multiplication
	//----------------------------------
	/**
	 * Compute cell-wise multiplication on "this" and given matrix x: this= this &#42 x
	 * 
	 * @param x   matrix of multiplication object 
	 * @return    result of calling instance matrix
	 */
	abstract public def cellMult(x:Matrix(M,N)): Matrix(this);
	
	/**
	 * Cell-wise multiply operation x = this &#42 x 
	 * 
	 * @param x   matrix of multiplication object which is also the result 
	 * @return    result matrix instance 	 
	 */
	abstract protected def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x);     
	
	
	//----------------------------------
	// Cellwise division operation
	//----------------------------------
	/**
	 * Cell-wise division this = this / x;
	 * 
	 * @param x		object matrix of division
	 * @return		result of calling instance matrix
	 */
	abstract public def cellDiv(x:Matrix(M,N)):Matrix(this); 
	
	/**
	 * Cell-wise division, returning x = this / x 
	 * 
	 * @param x		object of division.
	 * @return		result matrix
	 */
	abstract protected def cellDivBy(x:DenseMatrix(M,N)):DenseMatrix(x); 
	
	//-------------------------------------------------------------------
	// Matrix multiplication
	//-------------------------------------------------------------------
	
	/**
	 * Matrix multiplication. Return this +=A &#42 B if plus is true, otherwise
	 * this  = A &#42 B. 
	 * 
	 * @param A		the first operand matrix, or the left side operand of multiplication sign
	 * @param B		the second operand matrix, or the right side operand of multiplication sign
	 * @param plus	result add-on flag. If true, this += A &#42 B.
	 * @return		multiplication result
	 */
	abstract public def mult(A:Matrix(this.M),B:Matrix(A.N,this.N),	plus:Boolean):Matrix(this);		
		   
	/** 
	 * Compute this += A<sup>T</sup> &#42 B if plus is true, otherwise 
	 * this = A<sup>T</sup> &#42 B 
	 * 
	 * @param A		the first operand of the multiply operation which is used as transposed.
	 * @param B		the second operand of the multiply operation
	 * @param plus	result add-on flag. If true, add the multiplication result to output matrix.
	 * @return		multiplication result
	 */
	abstract public def transMult(A:Matrix{self.N==this.M}, B:Matrix(A.M,this.N), plus:Boolean):Matrix(this); 
			
	/** 
	 * Compute this += A &#42 B<sup>T</sup> if plus is true, otherwise 
	 * this = A &#42 B<sup>T</sup>.
	 * 
	 * @param A		the first operand matrix of the multiply operation
	 * @param B		the second operand matrix which is transposed in multiplication 
	 * @param plus	result add-on flag. If true, this += A &#42 B<sup>T</sup>.
	 * @return		multiplication result
	 */
	abstract public def multTrans(A:Matrix(this.M),	B:Matrix(this.N, A.N), plus:Boolean):Matrix(this);
	
	//=====================================================================
	// Operator overloading
	//=====================================================================
	
	//---------------------------
	//Cellwise operators
	//---------------------------
	public operator - this = clone().scale(-1.0) as Matrix(M,N);
	
	/**
	 * Scaling operator overloading: this = this &#42 dblv
	 * 
	 * @param dblv		scaling factor in double
	 * @return			result of operation 
	 */
	public operator this * (dv:Double) = this.clone().scale(dv) as Matrix(M,N);

	/**
	 * Scaling operator overloading: this = this &#42 intv
	 * 
	 * @param intv		scaling factor in double
	 * @return			result of operation 
	 */
	public operator this * (iv:Int) = this.clone().scale(iv) as Matrix(M,N); 
	
	/**
	 * Scaling operator overloading: this = dblv &#42 this
	 * 
	 * @param dblv		scaling factor in double
	 * @return			result of operation 
	 */
	public operator (dv:Double) * this = this.clone().scale(dv) as Matrix(M,N);
	
	/**
	 * Scaling operator overloading: this = intv &#42 this
	 * 
	 * @param intv		scaling factor in double
	 * @return			result of operation 
	 */
	public operator (iv:Int) * this = this.clone().scale(iv) as Matrix(M,N);
	
	//=====================================================================
	
	/**
	 * Cell-wise add operator overloading. Return cell-wise matrix add of this+that 
	 * in a new matrix instance which has the same type as the right-side operand matrix,
	 * or the invoking object.
	 * 
	 * @param that		object matrix of add operation
	 * @return			result of cell-wise add
	 */
	public operator this + (that:Matrix(M,N)) = this.clone().cellAdd(that) as Matrix(M,N);
	
	public operator this + (dv:Double)        = this.clone().cellAdd(dv) as Matrix(M,N);
	
	/**
	 * Cell-wise subtraction operator overloading. Return matrix cell-wise subtraction of
	 * this - that in a new matrix instance which has the same type as the left-side 
	 * operand matrix or the invoking object.
	 * 
	 * @param that		object matrix of subtraction operation
	 * @return			result of cell-wise subtract 
	 */
	public operator this - (that:Matrix(M,N))= this.clone().cellSub(that) as Matrix(M,N);
	
	/**
	 * Cell-wise multiply operator overload. Return cell-wise matrix multiply this &#42 that
	 * in a new matrix object which has the same type as the first operand matrix or
	 * the invoking object.
	 * 
	 * @param that		object matrix of multiply
	 * @return			result of cell-wise multiply
	 */
	public operator this * (that:Matrix(M,N)) = this.clone().cellMult(that) as Matrix(M,N);

	public operator this - (dv:Double)        = this.clone().cellAdd(-dv) as Matrix(M,N);
	
	/**
	 * Cell-wise division operator overload. Return cell-wise matrix division of this / that 
	 * in a new matrix object which has the same class type as the second operand matrix or
	 * the invoking object
	 * 
	 * @param that		object matrix of division operation
	 * @return			result of cell-wise division
	 */
	public operator this / (that:Matrix(M,N)) = this.clone().cellDiv(that) as Matrix(M,N);
	
	/**
	 * Matrix multiply operator overloading. Return matrix multiplication of this &#42 that 
	 * in a new matrix object which has the same class type as the first matrix object
	 * or the invoking object. Note: the overloading operator is "%", not "&#42" which is
	 * assigned to cell-wise multiply operation. There is not operator overloading for 
	 * transMult() and multTrans() methods.
	 * 
	 * @param that		object matrix of multiply operation
	 * @return			result of cell-wise division
	 */
	
	public operator this % (that:Matrix{self.M==this.N}):Matrix(this.M,that.N) {
		val m:Matrix(this.M, that.N) = this.alloc(this.M, that.N);
		return m.mult(this, that, false);
	}
	
	
	//===================================================================
	// Testing tools
	//===================================================================
	
	/**
	 * Return true if this matrix equals to another matrix
	 * 
	 * @param m   the comparison matrix
	 * @return true if elements in m are same as this
	 */
	public def equals(m:Matrix(M,N)) = VerifyTools.testSame(this, m);
	//public def equal(m:Matrix(M,N)) = equals(m);
	
	/**
	 * Return true if very elements in this matrix equals to v.
	 */	
	//public def equal(v:Double):Boolean = Matrix.testSame(this, v);
	public def equals(v:Double):Boolean = VerifyTools.testSame(this, v);
	public def equals(v:Int):Boolean    = VerifyTools.testSame(this, v as Double);
	public def equals(v:Long):Boolean   = VerifyTools.testSame(this, v as Double);
	
	
	/** 
	 * Instance type check 
	 */
	abstract public def likeMe(m:Matrix):Boolean;
	
	//===================================================================
	// Printing tools
	//===================================================================
	
	/** 
	 * Overwrite the super class method of converting object to a string object
	 */
	public def toString() = dataToString();
	
	/** 
	 * Convert all elements in the matrix into a string
	 */
	public def dataToString():String {
		val dstr = new StringBuilder();
		
		dstr.add("--------- Matrix "+M+" x "+N+" ---------\n");
		for (var r:Int=0; r<M; r++) {
			dstr.add(r.toString()+"\t[ ");
			for (var c:Int=0; c<N; c++)
				dstr.add(String.format("%03.3f ", [this(r,c) as Any]));
			dstr.add("]\n");
		}
		dstr.add("---------------------------------------");
		return dstr.toString(); 
	}
	
	/**
	 * Print out all element data in the matrix
	 */	
	public def printMatrix():void { printMatrix(""); }
	
	/**
	 * Print out all element data in the matrix with message
	 */			
	public def printMatrix(msg:String) {
		val output:String = 
			msg + dataToString();
		Console.OUT.println(output);
		Console.OUT.flush();
	}
	
	//==============================================================
	// Used for debugging
	//==============================================================
	//
	public def debugPrint() { debugPrint("");}
	
	public def debugPrint(msg:String) {
		val output:String= msg + dataToString();
		Debug.println(output);
		Debug.flush();
	}
}

