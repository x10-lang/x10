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

import x10.matrix.blas.DriverBLAS;
import x10.matrix.blas.DenseMultBLAS;

public type DenseMatrix(m:Int, n:Int)=DenseMatrix{self.M==m, self.N==n};
public type DenseMatrix(m:Int)=DenseMatrix{self.M==m};
public type DenseMatrix(C:DenseMatrix)=DenseMatrix{self==C};
public type DenseMatrix(C:Matrix)=DenseMatrix{self==C};


/**
 * The dense matrix has all elements stored in column major order in a 
 * continuous memory space. Current implementation defines element using
 * double type.
 */
public class DenseMatrix extends Matrix {
	//================================================================
	// Base data structure
	//================================================================

	/**
	 * The storage for dense matrix is 1-dimension array. 
	 * Rational: 
	 * 1) Compatible to column-major matrix storage specification, so that 
	 * the memory trunk can be passed to/from other program directly without conversion.
	 * 2) Fast access to data adjacent in the same column
	 */
	public val d:Array[Double](1){rail};

	//================================================================
	// Constructor, maker, and clone method
	//================================================================
	/**
	 * Construct a dense matrix in specified rows and columns and using provided
	 * data storage
	 *
	 * @param  m   number of rows in the dense matrix
	 * @param  n   number of columns in the dense matrix
	 * @param  x   the data array 
	 */
    public def this(m:Int, n:Int, x:Array[Double](1){rail}):DenseMatrix(m,n){
		super(m, n);
		this.d=x;
		Debug.assure(m*n <= x.size, "Dense matrix has insufficient storage space");
    }

	/**
	 * Construct a dense matrix with m rows and n columns 
	 *
	 * @param  m   number of rows in the dense matrix
	 * @param  n   number of columns in the dense matrix
	 */	
	public def this(m:Int, n:Int):DenseMatrix(m,n) {
		super(m, n);
		this.d = new Array[Double](m*n, 0.0);
	}

	//================================================================
	// Memory allocation, make, clone, and data initial methods
	//================================================================
	//
	
	/**
	 * Construct a dense matrix with m rows and n columns
	 *
	 * @param  m   	number of rows in the dense matrix
	 * @param  n    number of columns in the dense matrix
	 * @return		the constructed new instance
	 */
    public static def make(m:Int, n:Int):DenseMatrix(m,n) {
		val d = new Array[Double](m*n);
		return new DenseMatrix(m, n, d);
    }

	/**
	 * Construct a (m x n) dense matrix, with each cell is assigned by value of v.
	 *
	 * @param  m   	number of rows in the dense matrix
	 * @param  n   	number of columns in the dense matrix
	 * @param  v   	value assigned to all elements in the matrix
     * @return		the instance
	 */
    public static def make(m:Int, n:Int, v:Double):DenseMatrix(m,n) {
		val d = new Array[Double](m*n, (i:Int)=> v);
		return new DenseMatrix(m, n, d);
    }


    /**
     * Construct a copy of the provided dense matrix .
     * 
     * @param  src	the source matrix
     * @return		the copy of the source matrix
     */    
    public static def make(src:DenseMatrix):DenseMatrix(src.M, src.N) {
    	val newd = new Array[Double](src.M*src.N);
    	Array.copy(src.d, newd);
    	return new DenseMatrix(src.M, src.N, newd);
    }
    
	//-------------------------------------------------------------------

	/**
	 * Initialize all elements of the dense matrix with a constant value.
	 * @param  iv 	the constant value
	 */	
	public def init(iv:Double): DenseMatrix(this) {
		for (var i:Int=0; i<M*N; i++)	
			this.d(i) = iv;
		return this;
	}

	/**
	 * Initialize all elements of the dense matrix with random 
	 * values between 0.0 and 1.0.
	 */	
	public def initRandom(): DenseMatrix(this) {
		val rgen = RandTool.getRandGen();
		//val ll = M*N / 100;
		for (var i:Int=0; i<M*N; i++) {
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
	public def initRandom(lb:Int, ub:Int): DenseMatrix(this) {
		val len = Math.abs(ub-lb)+1;
		val rgen = RandTool.getRandGen();
		//val ll = M*N / 100;
		for (var i:Int=0; i<M*N; i++) {
			this.d(i) = rgen.nextInt(len)+lb;
		}
		return this;
	}
	/**
	 * Create a dense matrix instance and initialize it with random values
	 *
	 * @param m		number of rows
	 * @param n		number of columns
	 * @return		the new dense matrix with all elements are assigned with random values
	 * @See make()
	 * @See initRandom()
	 */
	public static def makeRand(m:Int, n:Int):DenseMatrix(m,n) {
		return DenseMatrix.make(m, n).initRandom();
	}

	/**
	 * Construct a dense matrix instance with memory allocation 
	 * for a m x n elements. This method is similar to make(m,n),
	 * but it is not a static method.
	 *
	 * @param  m   	number of rows in the dense matrix
	 * @param  n   	number of columns in the dense matrix
	 * @return		new dense matrix instance
	 */
    public  def alloc(m:Int, n:Int):DenseMatrix(m,n) {
		val d = new Array[Double](m*n);
		return new DenseMatrix(m, n, d);
    }
    
    public def alloc() = alloc(this.M, this.N);

	//======================================================================
	// Data copy, clone and reset 
	//======================================================================
	 
	/**
	 * Make a copy the matrix instance. 
     * <p>Note: this clone method use array copy constructor. 
     * The size of clone result will be the same as its source.
 	 */
	public  def clone():DenseMatrix(M,N){
		//val na = new Array[Double](M*N, (i:Int)=> this.d(i));
		val nd = new Array[Double](this.d) as Array[Double](1){rail};
		val nm = new DenseMatrix(M, N, nd);
		return nm;
	}

	/**
	 * Copy all data in this matrix to another dense matrix.
	 *
	 * @param   dm  the target dense matrix
	 */
	public def copyTo(dm:DenseMatrix(M,N)):void {
		copyCols(this, 0, dm, 0, N);
	}
	
	/**
	 * Reset all element to 0.0
	 */
	public  def reset():void {
		for (var i:Int=0; i<M*N; i++) d(i)=0.0;
	}

    /**
	 * Reset data in column c to 0.0
	 *
	 * @param  c  the column needs to be reset
	 */	
	public def resetCol(c:Int):void {
		Debug.assure(c<this.N, "Illegal specified column");
		for (var i:Int = c*this.M; i<(c+1)*this.M; i++) d(i) = 0.0;
	}

	/**
	 * Copy specified range of columns of dense matrix from source to target.
	 * The target dense matrix can be bigger than source. The data copy is
	 * performed column by column.
	 *
	 * @param src      	 	the source dense matrix
	 * @param srcColOffset	the starting column in source matrix for copy
	 * @param dst      	 	the target dense matrix
	 * @param dstColOffset	the starting column in dense matrix to store the copy data
	 * @param colCnt  	 	number of columns to copy
	 */
	public static def copyCols(src:DenseMatrix, srcColOffset:Int,  
							   dst:DenseMatrix, dstColOffset:Int, colCnt:Int): Int {

		//Make sure the source and destination are bounded.
		Debug.assure(src.M <= dst.M && 
					 srcColOffset+colCnt <= src.N && 
					 dstColOffset+colCnt <= dst.N, 
					 "Illegal column offset or count in column copy");
		var srcoff:Int = src.M * srcColOffset;
		var dstoff:Int = dst.M * dstColOffset;
		val srcend:Int = src.M * (srcColOffset+colCnt);

		if (src.M != dst.M) {
			//Copy column by column
			for (;srcoff < srcend; srcoff += src.M, dstoff += dst.M) {
				Array.copy(src.d, srcoff, dst.d, dstoff, src.M);
			}
		} else {
			//Copy all in one time
			Array.copy(src.d, srcoff, dst.d, dstoff, src.M*colCnt);
		}
		return  src.M*colCnt;
	}

	/**
	 * Copy whole dense matrix from source to target. The target matrix must
	 * have larger dimension than the source.
	 * 
	 * @param src 		source matrix
	 * @param dst		target matrix
	 */
	public static def copy(src:DenseMatrix, dst:DenseMatrix): void {

		//Make sure the source and destination are bounded.
		Debug.assure(src.N <= dst.N && src.M <= dst.M, 
					 "source or target dense matrix sizes mismatch");
		copyCols(src, 0, dst, 0, src.N);
	}

	/**
	 * Copy specified rows from source to target 
	 * 
	 * @param src           the source dense matrix
	 * @param srcRowOffset  the starting row in source matrix for copy
	 * @param dst           the target dense matrix
	 * @param dstRowOffset  the starting row in dense matrix to store the copy data
	 * @param rowCnt        number of rows to copy
	 */
	public static def copyRows(src:DenseMatrix, var srcRowOffset:Int,
							   dst:DenseMatrix, var dstRowOffset:Int, rowCnt:Int) :Int {
		
		//Make sure the source and destination are bounded.
		Debug.assure(src.N <= dst.N && 
					 srcRowOffset+rowCnt <= src.M && 
					 dstRowOffset+rowCnt <= dst.M, 
					 "illegal row offset or row count in copy rows");

		val srcSize:Int = src.M*src.N;
		for (; srcRowOffset < srcSize; 
			   srcRowOffset+=src.M, dstRowOffset+=dst.M) {
			Array.copy(src.d, srcRowOffset, dst.d, dstRowOffset, rowCnt);
		}
		return rowCnt * src.N;
	}

	/**
	 * Copy specified subset of matrix in source to target.
	 * 
	 * @param src            the source dense matrix
	 * @param srcRowOffset   the starting row in source matrix for copy
	 * @param srcColOffset   the starting column in source matrix for copy
	 * @param dst            the target dense matrix
	 * @param dstRowOffset   the starting row in dense matrix to store the copy data
	 * @param dstColOffset   the starting column in dense matrix to store the copy data
	 * @param rowCnt         number of rows to copy
	 * @param colCnt         number of column to copy
	 */
	public static def copySubset(src:DenseMatrix, var srcRowOffset:Int, var srcColOffset:Int,
			 dst:DenseMatrix, var dstRowOffset:Int, var dstColOffset:Int,
			 rowCnt:Int, colCnt:Int): Int {
		
		Debug.assure(src.M <= dst.M && 
					 srcColOffset+colCnt <= src.N && 
					 dstColOffset+colCnt <= dst.N, 
					 "illegal collumn offset or counts in subset copy");
		Debug.assure(src.N <= dst.N && 
					 srcRowOffset+rowCnt <= src.M && 
					 dstRowOffset+rowCnt <= dst.M, 
					 "illegal row offset or row count in subset copy");

		val srcEnd:Int = src.M * (srcColOffset + colCnt);
		srcRowOffset += srcColOffset * src.M;
		dstRowOffset += dstColOffset * dst.M;
		for (; srcRowOffset < srcEnd; srcRowOffset+=src.M, dstRowOffset+=dst.M) {
			Array.copy(src.d, srcRowOffset, dst.d, dstRowOffset, rowCnt);
		}		
		return rowCnt * colCnt;
	}


	//======================================================================
	// Data access and set
	//======================================================================
	
	/**
	 * Access data at (x, y) position in the dense matrix
	 *
	 * @param  x  the x-th row 
	 * @param  y  the y-th column
	 */
    public  operator this(x:Int, y:Int):Double=d(y*this.M+x);

	/**
	 * Set the value v at (x, y) in the dense matrix
	 *
	 * @param  x  the x-th row 
	 * @param  y  the y-th column
	 */
	public  operator this(x:Int,y:Int) = (v:Double):Double {
		d(y*this.M+x) = v;
		return v;
	}

	/**
	 * Get a subset matrix at (row_offset, col_offset) of size (row, col), 
	 * put data in dm.
	 *
	 * @param  row_offset  the starting row of submatrix
	 * @param  col_offset  the start column of submatrix
	 * @param  row         the number of rows in submatrix
	 * @param  col         the number of columns in submatrix
	 * @param  dm          the targeting dense matrix to store the submatrix
	 */
	public  def subset(row_offset:Int, 
			   col_offset:Int, 
			   row:Int, col:Int, 
			   dm:DenseMatrix) : void {
		copySubset(this, row_offset, col_offset, dm, 0, 0, row, col);
	}

	/**
	 * Return a dense matrix, which is the subset matrix at (row_offset, col_offset) 
	 * of size (row, col). 
	 *
	 * @param  row_offset  the starting row of submatrix
	 * @param  col_offset  the starting column of submatrix
	 * @param  row         the number of rows in submatrix
	 * @param  col         the number of columns in submatrix
	 * @return the submatrix in dense format
	 */
	public def subset(row_offset:Int, 
			  col_offset:Int, 
			  row:Int, col:Int):DenseMatrix {
		val nm = DenseMatrix.make(row, col);
		subset(row_offset, col_offset, row, col, nm);
		return nm;
	}


    //------------------------------------------------------------------
	// Transpose marking operation
	//------------------------------------------------------------------

	/**
	 * Transpose this dense matrix and store in the input dense matrix.
	 * 
	 * @param m		object to store the transposed matrix
	 */
	public def T(m:DenseMatrix(N,M)) : void {
		var src_idx:Int =0;
		var dst_idx:Int =0;
		//Debug.assure(this.M==m.N&&this.N==m.M);
		for (var c:Int=0; c < this.N; c++) {
			dst_idx = c;
			for (var r:Int=0; r < this.M; r++, dst_idx+=m.M, src_idx++) {
				m.d(dst_idx) = this.d(src_idx);
			}
		}
	}

 	//------------------------------------------------------------------
	// Cell-wise operations
	//------------------------------------------------------------------

	/**
	 * Raise each element in the matrix by a factor.
	 *
	 * @param  a	the scaling factor
	 * @return 		result ("this")
	 */
	public  def scale(a:Double):DenseMatrix(this)  {
		for (var i:Int=0; i<M*N; i++) 
			this.d(i) = this.d(i) * a;
		return this;
	}

	/**
	 * Raise each cell in the matrix by the factor.
	 *
	 * @param  a	the scaling factor
	 * @return		result
	 */
	public  def scale(a:Int) = scale(a as Double);

    /**
     * Compute the Euclidean distance between "this" and the given matrix
     * 
     * @param x matrix object
     * @return Euclidean distance
     */
    public def norm(x:Matrix(M,N)):Double {
        return Math.sqrt(frobeniusProduct(x));
    }

    /**
     * Compute the Frobenius inner product between "this" and the given matrix,
     * that is, the sum of the products of corresponding elements.
     * 
     * @param x matrix object
     * @return Frobenius inner product
     */
    public def frobeniusProduct(x:Matrix(M,N)):Double {
        var nv:Double = 0.0;
        for (var c:Int=0; c<N; c++) {
            for (var r:Int=0; r<M; r++) {
                nv += this(c,r)*x(c,r);
            }
        }
        return nv;
    }

    /**
     * Compute the Frobenius or Euclidean norm of this matrix
     * 
     * @return Euclidean norm
     */
    public def norm():Double {
        return Math.sqrt(frobeniusProduct(this));
    }

    /**
     * Compute the Euclidean distance between "this" and the given dense matrix
     * 
     * @param x dense matrix object
     * @return Euclidean distance
     */
    public def norm(x:DenseMatrix(M,N)):Double {
        return Math.sqrt(frobeniusProduct(x));
    }

    /**
     * Compute the Frobenius inner product between "this" and the given dense matrix
     * 
     * @param x dense matrix object
     * @return Frobenius inner product
     */
    public def frobeniusProduct(x:DenseMatrix(M,N)):Double {
        var nv:Double = 0.0;
        for (var i:Int=0; i<N*M; i++) {
            nv += this.d(i)*x.d(i);
        }
        return nv;
    }

    /**
     * Compute the maximum absolute value of all elements of this matrix
     * (the matrix norm with p==Inf)
     *
     * @return max absolute value of any element
     */
    public def maxNorm():Double {
        val maxAbsReducer = ((res : Double, a : Double) => Math.max(Math.abs(a), res));
        return d.reduce(maxAbsReducer, -1.0);
    }

    /**
     * Compute the trace of this matrix (sum of diagonal elements)
     *
     * @return the sum of diagonal elements
     */
    public def trace():Double {
        var tr:Double = 0.0;
        for (var i:Int=0; i<M*N; i+=N)
            tr += d(i);
        return tr;
    }
	
	/**
	 * Compute the sum of all matrix elements
	 * 
	 * @return		the sum of all matrix elements
	 */
	public def sum():Double {
		var tt:Double = 0.0;
		for (var i:Int=0; i<M*N; i++)
			tt += d(i);
		return tt;
	}
	
	/**
	 * Compute exponential of each all elements in matrix.
	 * 
	 * @return 		result ("this" instance)
	 */
	public def exp(): DenseMatrix(this) {
		for (var i:Int=0; i<M*N; i++)
			Math.exp(d(i));
		return this;
	}
	
	//------------------------
	// Add operation
	//------------------------
	/**
	 * Cell-wise add: this += [v]
	 * 
	 * @param   v	value to add to all elements
	 * @return    	result ("this" instance)
	 */
	public def cellAdd(v:Double):DenseMatrix(this) {
		for (var i:Int=0; i<M*N; i++) 
			this.d(i) += v;
		return this;
	}
	
    /**
     * Cell-wise add: this += x.
	 *
	 * @param   x 	input matrix to be added to "this"
	 * @return		result ("this")
     */
    public def cellAdd(x:Matrix(M,N)):DenseMatrix(this)   {
		x.cellAddTo(this); //Double-dispatch
	    return this;
	}

	/**
     * Cell-wise add: this += x.
	 *
	 * @param   x 	input dense matrix to be added
     * @return 		result
	 */
    public def cellAdd(x:DenseMatrix(M,N)):DenseMatrix(this)  {
	 	for (var i:Int=0; i < this.N*this.M; i++) this.d(i) += x.d(i);
	 	return this;
	}
	
	/**
	 * Perform cell-wise add: x = this + x
     * 
     * @param x		input matrix to be added with this
     * @return		result (the input x).
	 */
	protected def cellAddTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		x.cellAdd(this);
		return x;
	}

	//------------------------------
	// Subtract operation method
	//------------------------------
	/**
	 * Cell-wise subtract: this = this - [v]
	 * 
	 * @param   v 	value subtracting all elements
	 * @return    	result ("this")
	 */
	public def cellSub(v:Double):DenseMatrix(this) {
		for (var i:Int=0; i<M*N; i++) 
			this.d(i) -= v;
		return this;
	}
	
	/**
	 * Cell-wise subtract: this = [v] - this
	 * 
	 * @param   v  	value to subtract from
	 * @return     	result matrix
	 */
	public def cellSubFrom(v:Double):DenseMatrix(this) {
		for (var i:Int=0; i<M*N; i++) 
			this.d(i) = v - this.d(i);
		return this;
	}
	
    /**
     * Cell-wise subtract: this = this - x
	 *
	 * @param  x 	subtracting matrix
	 * @return		result
     */
    public def cellSub(x:Matrix(M,N)):DenseMatrix(this)   {
		x.cellSubFrom(this);
	    return this;
	}
    
    /**
     * Cell-wise subtract: this = this - x
	 *
	 * @param  x	subtracting dense matrix
     * @return		result
     */
    public def cellSub(x:DenseMatrix(M,N)):DenseMatrix(this)   {
	 	for (var i:Int=0; i < this.N*this.M; i++) this.d(i) -= x.d(i);
	 	return this;
	}
    
	/**
	 * Cell-wise subtract: x = x - this
     * 
     * @param  x	dense matrix to be subtracted by "this"
     * @return		result (input x)
	 */
	protected def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x) {
		x.cellSub(this);
		return x;
	}

	//------------------------------
	// Cellwise multiplication
	//------------------------------
	/**
	 * Cell-wise matrix multiply: this = this &#42 [v]
	 * 
	 * @param   v	value to multiply to all elements
	 * @return    	result matrix ("this")
	 */
	public def cellMult(v:Double):DenseMatrix(this) {
		for (var i:Int=0; i<M*N; i++) 
			this.d(i) *= v;
		return this;
	}
    /**
     * Cell-wise matrix multiply: this = this &#42 x
	 *
	 * @param  x	the multiplying matrix
	 * @return		result matrix
     */
    public def cellMult(x:Matrix(M,N)):DenseMatrix(this)   {
		x.cellMultTo(this);//Double-dispatch
	    return this;
	}
    
    /**
     * Cell-wise matrix multiply of this = this &#42 x
     * *
     * @param  x	the multiplying matrix
     * @return		result matrix
     */
    public def cellMult(x:DenseMatrix(M,N)):DenseMatrix(this)   {
	 	for (var i:Int=0; i < this.M*this.N; i++) this.d(i) *= x.d(i);
	 	return this;
	}
	
	/**
	 * Cell-wise matrix multiply:  x = x &#42 this
     * *
     * @param  x	the first matrix and returning result
     * @return		result in matrix x
	 */
	protected def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x)  {
		x.cellMult(this);
		return x;
	}

	//-------------------------
	// Cellwise division
	//-------------------------
	/**
	 * Cell-wise division on every element, this = this / v
	 * 
	 * @param   v  	value to be divided to all elements
	 * @return     	result in "this" matrix 
	 */
	public def cellDiv(v:Double):DenseMatrix(this) {
		for (var i:Int=0; i<M*N; i++) 
			this.d(i) /= v;
		return this;
	}
	/**
	 * Cell-wise division by on every element, this = [v] / this
	 * 
	 * @param   v  	value to be divided by
	 * @return     	result in "this" object
	 */
	public def cellDivBy(v:Double):DenseMatrix(this) {
		for (var i:Int=0; i<M*N; i++) 
			this.d(i) = v / this.d(i);
		return this;
	}
	
	/**
     * Cell-wise matrix division. Return this = this / x
	 * 
	 * @param x		input matrix to divide
	 * @return		result
     */
	public  def cellDiv(x:Matrix(M,N)):DenseMatrix(this)  {
		x.cellDivBy(this);
	    return this;
	}

	/**
     * Cell-wise division, returning this = this / x;
	 * 
	 * @param x		input dense matrix to divide
	 * @return		result
     */
	public def cellDiv(x:DenseMatrix(M,N)):DenseMatrix(this)  {
	 	for (var i:Int=0; i < M*N; i++) {
			val v = x.d(i);
			if (MathTool.isZero(v))
				this.d(i) /= MathTool.delta;
			else
				this.d(i) /= v;
		}
	 	return this;
	}

	/**
	 * Cell-wise division, returning x = x / this
	 * 
	 * @param x		input matrix to be divided
	 * @return 		result (in input x)
	 */
	protected def cellDivBy(x:DenseMatrix(M,N)):DenseMatrix(x)  {
		x.cellDiv(this);
		return x;
	}

	//================================================================
	// Matrix multiply operations: self this<- op(A)*op(B) + (plus?1:0) * C
	// Default is using BLAS driver
	//================================================================

	/**
	 * Multiply two matrices and return this += A *&#42 B if plus is true, otherwise 
	 * this = A &#42 B using X10 matrix multiplication driver.
	 * Result is stored in "this" object, which is dense matrix.
	 * Input parameters are two matrices in the base matrix type. 
	 * <p>
	 * Performance notice: This method could take more time becasue the input
	 * matrix types are not specified, and matrix multiplication is not optimized.
	 * 
	 * @param  A  	the first matrix in multiply
	 * @param  B  	the second matrix
	 * @param plus 	add-on flag
	 * @return     	result ("this" the method invoking object)
	 */
	public def mult(
			A:Matrix(this.M), 
			B:Matrix(A.N,this.N), 
			plus:Boolean):DenseMatrix(this) {

		if (likeMe(A) && likeMe(B)) 
			return mult(A as DenseMatrix(A), B as DenseMatrix(B), plus);
		//Debug.flushln("Resort to basic X10 matrix multiplication driver");
		MatrixMultXTen.comp(A, B, this, plus);
		return this;
	}
	
	/**
	 * Multiply two matrices and return this += A<sup>T</sup> &#42 B if plus is true, otherwise
	 * this = A<sup>T</sup> &#42 B,
	 * where the first matrix A is in transposed form in 
	 * the matrix multiply operation.
	 *
	 * @param  A  	the first matrix in multiplication
	 * @param  B  	the second matrix
	 * @param plus 	add-on flag
	 * @return     	result ("this" the method invoking object)
	*/
	public def transMult(
			A:Matrix{self.N==this.M}, 
			B:Matrix(A.M,this.N),
			plus:Boolean): DenseMatrix(this) {
		
		if (likeMe(A) && likeMe(B)) 
			return transMult(A as DenseMatrix(A), 
							 B as DenseMatrix(B), plus);
		//Debug.flushln("Resort to X10 matrix multiplication driver");
		MatrixMultXTen.compTransMult(A, B, this, plus);
		return this;
	}

	/**
	 * Multiply two matrices and return this += A &#42 B<sup>T</sup> if plus is true, otherwise
	 * this = A &#42 B<sup>T</sup> where the second matrix B is accessed in its transposed form in
	 * the matrix multiply operation. 
	 * 
	 * @param  A  	the first matrix in multiplication
	 * @param  B  	the second matrix in multiplication
	 * @param plus 	add-on flag
	 * @return     	result ("this", the method invoking method)
	 */
	public def multTrans(
			A:Matrix(this.M), 
			B:Matrix(this.N, A.N), 
			plus:Boolean):DenseMatrix(this) {
		
		if (likeMe(A) && likeMe(B)) 
			return multTrans(A as DenseMatrix{self.M==this.M},
							 B as DenseMatrix{self.N==A.N,self.M==this.N}, 
							 plus);
		Debug.flushln("Resort to basic X10 matrix multiplication driver");
		MatrixMultXTen.compMultTrans(A, B, this, plus);
		return this;
	}
	
	//----------------------------------------------------------
	/**
	 * Multiply two dense matrices and return this = A &#42 B using BLAS driver.
	 *
	 * @param  A 	first dense matrix in multiply
	 * @param  B 	second dense matrix in multiply
	 * @return		result of multiply ("this", the method invoking object)
	 */
	public def mult(
			A:DenseMatrix{self.M==this.M}, 
			B:DenseMatrix{self.N==this.N&&A.N==B.M})=
		mult(A, B, false);

	/**
	 * Multiply two dense matrix and return this += A &#42 B if plus is true,
	 * otherwise this = A &#42 B using BLAS drivers.
	 * 
	 * @param  A 	first dense matrix in multiply
	 * @param  B 	second dense matrix in multiply
	 * @param  plus	result add-on flag
	 * @return		result
	 */
	public def mult(A:DenseMatrix{self.M==this.M}, 
					B:DenseMatrix{self.N==this.N,A.N==B.M},//DenseMatrix(A.N,N), 
					plus:Boolean) {
		DenseMultBLAS.comp(A, B, this, plus); //BLAS driver
		return this;
	}

	/**
	 * Multiply two dense matrix and return this = A<sup>T<sup> &#42 B 
	 * where the first matrix in transposed for multiplication.
	 * 
	 * @param  A 	first dense matrix
	 * @param  B 	second dense matrix used in transposed
	 * @return		result
	 */
	public def transMult(
			A:DenseMatrix{self.N==this.M}, 
			B:DenseMatrix{self.N==this.N,A.M==B.M}//DenseMatrix(A.N,N), 
	) = transMult(A, B, false);
	
	/**
	 * Multiply two dense matrices and return this += A<sup>T<sup> &#42 B if plus is true,
	 * otherwise this = A<sup>T<sup> &#42 B. 
	 * It uses BLAS drivers, where the first matrix is in transposed format for multiplication.
	 * 
	 * @param  A 	first matrix
	 * @param  B 	second matrix
	 * @param  plus	add-on flag
	 * @return		result
	 */
	public def transMult(
			A:DenseMatrix{self.N==this.M}, 
			B:DenseMatrix{self.N==this.N,A.M==B.M},//DenseMatrix(A.N,N), 
			plus:Boolean) {
		DenseMultBLAS.compTransMult(A, B, this, plus); //BLAS driver
		return this;
	}

	/**
	 * Multiply two dense matrices and return this = A * T(B) or this += A * T(B) if plus is true
	 * using BLAS drivers, where the second matrix is in transposed format.
	 * 
	 * @param  A 	first matrix
	 * @param  B 	second matrix
	 * @param  plus	add-on flag
	 * @return		result
	 */
	public def multTrans(
			A:DenseMatrix{self.M==this.M}, 
			B:DenseMatrix{self.M==this.N,A.N==B.N},//DenseMatrix(A.N,N), 
			plus:Boolean) {
		DenseMultBLAS.compMultTrans(A, B, this, plus); //BLAS driver
		return this;
	}

	/**
	 * Multiply two dense matrices and return this = A &#42 B<sup>T<sup>  using BLAS drivers, where
	 * the second matrix is in transposed format.
	 * 
	 * @param  A 	first matrix
	 * @param  B 	second matrix
	 * @return		result
	 */
	public def multTrans(A:DenseMatrix{self.M==this.M}, 
						 B:DenseMatrix{self.M==this.N,A.N==B.N} //DenseMatrix(A.N,N), 
						 )  = multTrans(A, B, false);


	//==================================================================
	// Operator
	//==================================================================
	/**
	 * Operator overloading for cell-wise matrix subtraction, and return this - that in a new dense format
	 */
	public operator - this = clone().scale(-1.0);
	
	public operator (v:Double) + this = this.clone().cellAdd(v);
	public operator this + (v:Double):DenseMatrix(M,N) {
		val ret = this.clone();
		return ret.cellAdd(v);
		//return dst;
	}
	
	public operator this - (v:Double):DenseMatrix(M,N) {
		val ret = this.clone();
		return ret.cellSub(v);
	}
	
	public operator (v:Double) - this = this.clone().cellSubFrom(v);
	public operator this / (v:Double) = this.clone().cellDiv(v);
	public operator (v:Double) / this = this.clone().cellDivBy(v);
	
	/**
	 * Operator overloading for cell-wise scaling operation and return result in a new dense matrix. 
	 */
	public operator this * (alpha:Double) : DenseMatrix(M,N) {
		val x=clone();
		x.scale(alpha);
		return x;
	}

	public operator this * (alpha:Int) : DenseMatrix(M,N) {
		val x= clone();
		x.scale(alpha);
		return x;
	}

	public operator (alpha:Double) * this = this * alpha;
	public operator (alpha:Int) * this    = this * alpha;;

	/**
	 * Operator overloading for cell-wise add, and return result in a new dense matrix. 
	 */
	public operator this + (that:DenseMatrix(M,N)) = clone().cellAdd(that);

	/**
	 * Operator overloading for cell-wise subtraction, and return this - that in a new dense format
	 */
	public operator this - (that:DenseMatrix(M,N)) = clone().cellSub(that);
	
    /**
     * Operator overloading for cell-wise multiplication, and return cell-wise multiply result of
	 * this &#42 that in dense format
     */
	public operator this * (that:DenseMatrix(M,N)) = clone().cellMult(that);

	/**
	 * Operator overloading for cell-wise division, and return this / that in a new dense matrix
	 */
	public operator this / (that:DenseMatrix(M,N)) = clone().cellDiv(that);

	/**
	 * Operator overload for matrix multiplication. Return dense matrix multiplication this &#42 that 
	 * using BLAS driver.
	 */
	public  operator this % (that:DenseMatrix{self.M==this.N}
							 ):DenseMatrix(this.M,that.N) {
		val dm = DenseMatrix.make(this.M, that.N);
		DenseMultBLAS.comp(this, that, dm, false);
		return dm;
	}
	
	//=======================================================
	// Utils
	//=======================================================
// 	public def copyFrom(src:DenseMatrix(M,N)) {
// 		Array.copy(src.d, 0, this.d, 0, this.M*this.N);
// 	}
	/**
	 * Check matrix type and dimensions
	 * 
	 * @param	m	checking matrix
	 * @return 		true if m is dense matrix and has the same dimensions
	 */
	public def likeMe(m:Matrix):Boolean {
		if ((m instanceof DenseMatrix) && m.M==M && m.N==N) return true;
		return false;
	}

	/**
	 * Convert the whole dense matrix into a string
	 */
	public def toString() : String {
		var outstr:String ="--------- Dense Matrix "+M+" x "+N+" ---------\n";
		for (var r:Int=0; r<M; r++) {
			var rowstr:String=r.toString()+"\t[ ";
			for (var c:Int=0; c<N; c++)
				rowstr += this(r,c).toString()+" ";
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


