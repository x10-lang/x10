/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2012-2013.
 */

package x10.matrix;

import x10.compiler.Inline;
import x10.compiler.CompilerFlags;
import x10.util.StringBuilder;

import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.SparseMultSparseToDense;
import x10.matrix.sparse.SparseMultDenseToDense;
import x10.matrix.sparse.DenseMultSparseToDense;
import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.util.RandTool;

public type DenseMatrix(m:Long, n:Long)=DenseMatrix{self.M==m, self.N==n};
public type DenseMatrix(m:Long)=DenseMatrix{self.M==m};
public type DenseMatrix(C:DenseMatrix)=DenseMatrix{self==C};
public type DenseMatrix(C:Matrix)=DenseMatrix{self==C};

/**
 * The dense matrix has all elements stored in column major order in a 
 * continuous memory space. Current implementation defines element using
 * double type.
 */
public class DenseMatrix extends Matrix {
    /**
     * The storage for dense matrix is Rail. 
     * Rational: 
     * 1) Compatible to column-major matrix storage specification, so that 
     * the memory trunk can be passed to/from other program directly without conversion.
     * 2) Fast access to data adjacent in the same column
     */
    public val d:Rail[Double];

    /**
     * Construct a dense matrix in specified rows and columns and using provided
     * data storage
     *
     * @param  m   number of rows in the dense matrix
     * @param  n   number of columns in the dense matrix
     * @param  x   the data 
     */
    public def this(m:Long, n:Long, x:Rail[Double]):DenseMatrix(m,n){
        super(m, n);
        this.d=x;
        if (CompilerFlags.checkBounds()) {
            Debug.assure(m*n <= x.size, "Dense matrix has insufficient storage space");
        }
    }

    /**
     * Construct a dense matrix with m rows and n columns 
     *
     * @param  m   number of rows in the dense matrix
     * @param  n   number of columns in the dense matrix
     */    
    public def this(m:Long, n:Long):DenseMatrix(m,n) {
        super(m, n);
        this.d = new Rail[Double](m*n);
    }

    /**
     * Construct a dense matrix with m rows and n columns
     *
     * @param  m       number of rows in the dense matrix
     * @param  n    number of columns in the dense matrix
     * @return        the constructed new instance
     */
    public static def make(m:Long, n:Long):DenseMatrix(m,n) {
        return new DenseMatrix(m, n);
    }

    /**
     * Construct a (m x n) dense matrix, with each cell is assigned by value of v.
     *
     * @param  m       number of rows in the dense matrix
     * @param  n       number of columns in the dense matrix
     * @param  v       value assigned to all elements in the matrix
     * @return        the instance
     */
    public static def make(m:Long, n:Long, v:Double):DenseMatrix(m,n) {
        val d = new Rail[Double](m*n, (i:Long)=> v);
        return new DenseMatrix(m, n, d);
    }

    /**
     * Construct a copy of the provided dense matrix .
     * 
     * @param  src    the source matrix
     * @return        the copy of the source matrix
     */    
    public static def make(src:DenseMatrix):DenseMatrix(src.M, src.N) {
        val newd = new Rail[Double](src.d);
        return new DenseMatrix(src.M, src.N, newd);
    }
    
    /**
     * Initialize all elements of the dense matrix with a constant value.
     * @param  iv     the constant value
     */    
    public def init(iv:Double): DenseMatrix(this) {
        for (var i:Long=0; i<M*N; i++)    
            this.d(i) = iv;
        return this;
    }

    /**
     * Initialize using function
     * 
     * @param f    The function to use to initialize the matrix
     * @return this object
     */
    public def init(f:(Long)=>Double): DenseMatrix(this) {
        for (var i:Long=0; i<M*N; i++)
            this.d(i) = f(i);
        return this;
    }
    
    /**
     * Init with function
     * 
     * @param f    The function to use to initialize the matrix, mapping (row, column) => double
     * @return this object
     */
    public def init(f:(Long,Long)=>Double): DenseMatrix(this) {
        var i:Long=0;
        for (var c:Long=0; c<N; c++)
            for (var r:Long=0; r<M; r++, i++)
                this.d(i) = f(r, c);
        return this;
    }
    
    /**
     * Init with function and row and column index offsets
     * 
     * @param rowoff  row index offset
     * @param coloff  column index offset
     * @param f    The function to use to initialize the matrix
     * @return this object
     */
    public def init(rowoff:Long, coloff:Long, f:(Long,Long)=>Double): DenseMatrix(this) {
        var i:Long=0;
        for (var c:Long=0; c<N; c++)
            for (var r:Long=0; r<M; r++, i++)
                this.d(i) = f(rowoff+r, coloff+c);
        return this;
    }    

    /**
     * Initialize all elements of the dense matrix with random 
     * values between 0.0 and 1.0.
     */    
    public def initRandom(): DenseMatrix(this) {
        val rgen = RandTool.getRandGen();
        //val ll = M*N / 100;
        for (var i:Long=0; i<M*N; i++) {
            this.d(i) = rgen.nextDouble();
        }
        return this;
    }
    
    /**
     * Initialize all elements of the dense matrix with random 
     * values between the specified range.
     * 
     * @param lb    lower bound of random values
     * @param up    upper bound of random values
     */    
    public def initRandom(lb:Long, ub:Long): DenseMatrix(this) {
        val len = Math.abs(ub-lb)+1;
        val rgen = RandTool.getRandGen();
        //val ll = M*N / 100;
        for (var i:Long=0; i<M*N; i++) {
            this.d(i) = rgen.nextLong(len)+lb;
        }
        return this;
    }
    /**
     * Create a dense matrix instance and initialize it with random values
     *
     * @param m        number of rows
     * @param n        number of columns
     * @return        the new dense matrix with all elements are assigned with random values
     * @See make()
     * @See initRandom()
     */
    public static def makeRand(m:Long, n:Long):DenseMatrix(m,n) {
        return DenseMatrix.make(m, n).initRandom();
    }

    /**
     * Construct a dense matrix instance with memory allocation 
     * for a m x n elements. This method is similar to make(m,n),
     * but it is not a static method.
     *
     * @param  m       number of rows in the dense matrix
     * @param  n       number of columns in the dense matrix
     * @return        new dense matrix instance
     */
    public  def alloc(m:Long, n:Long):DenseMatrix(m,n) {
        val d = new Rail[Double](m*n);
        return new DenseMatrix(m, n, d);
    }
    
    public def alloc() = alloc(this.M, this.N);


    /**
     * Make a copy the matrix instance. 
     * <p>Note: this clone method use Rail copy constructor. 
     * The size of clone result will be the same as its source.
      */
    public  def clone():DenseMatrix(M,N){
        //val na = new Rail[Double](M*N, (i:Long)=> this.d(i));
        val nd = new Rail[Double](this.d) as Rail[Double];
        return new DenseMatrix(M, N, nd);
    }

    /**
     * Copy all data in this matrix to another dense matrix.
     *
     * @param   dm  the target dense matrix
     */
    public def copyTo(dm:DenseMatrix(M,N)):void {
        copyCols(this, 0, dm, 0, N);
    }
    
    public def copyTo(mat:Matrix(M,N)):void {
        Debug.assure(likeMe(mat), "Copy destination matrix type mismatch");
        copyTo(mat as DenseMatrix(M,N));
    }

    public def toDense() : DenseMatrix(M,N) {
        val dm = DenseMatrix.make(M,N);
        //Do NOT use clone().  clone() could be override by derive class
        copyTo(dm);
        return dm;
    }
    
    
    /**
     * Reset all elements to 0.0
     */
    public  def reset():void {
        d.clear(0, M*N);
    }

    /**
     * Reset data in column c to 0.0
     *
     * @param  c  the column needs to be reset
     */    
    public def resetCol(c:Long):void {
        if (CompilerFlags.checkBounds()) {
            Debug.assure(c<this.N, "Illegal specified column");
        }
        d.clear(c*this.M, this.M);
    }

    /**
     * Copy specified range of columns of dense matrix from source to target.
     * The target dense matrix can be bigger than source. The data copy is
     * performed column by column.
     *
     * @param src               the source dense matrix
     * @param srcColOffset    the starting column in source matrix for copy
     * @param dst               the target dense matrix
     * @param dstColOffset    the starting column in dense matrix to store the copy data
     * @param colCnt           number of columns to copy
     */
    public static def copyCols(src:DenseMatrix, srcColOffset:Long,  
                               dst:DenseMatrix, dstColOffset:Long, colCnt:Long): Long {

        if (CompilerFlags.checkBounds()) {
        //Make sure the source and destination are bounded.
            Debug.assure(src.M <= dst.M, "Destination leading dimension "+dst.M+" less than source "+src.M);
            Debug.assure(srcColOffset+colCnt <= src.N, "Source overflow :"+srcColOffset+"+"+colCnt+" > "+src.N); 
            Debug.assure(dstColOffset+colCnt <= dst.N, "Destination overflow :"+dstColOffset+"+"+colCnt+" > "+dst.N); 
        }

        var srcoff:Long = src.M * srcColOffset;
        var dstoff:Long = dst.M * dstColOffset;
        val srcend:Long = src.M * (srcColOffset+colCnt);

        if (src.M != dst.M) {
            //Copy column by column
            for (;srcoff < srcend; srcoff += src.M, dstoff += dst.M) {
                Rail.copy(src.d, srcoff, dst.d, dstoff, src.M);
            }
        } else {
            //Copy all in one time
            Rail.copy(src.d, srcoff, dst.d, dstoff, src.M*colCnt);
        }
        return  src.M*colCnt;
    }

    /**
     * Copy whole dense matrix from source to target. The target matrix must
     * have larger dimension than the source.
     * 
     * @param src         source matrix
     * @param dst        target matrix
     */
    public static def copy(src:DenseMatrix, dst:DenseMatrix): void {
        if (CompilerFlags.checkBounds()) {
            //Make sure the source and destination are bounded.
            Debug.assure(src.N <= dst.N && src.M <= dst.M, 
                         "source or target dense matrix sizes mismatch");
        }
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
    public static def copyRows(src:DenseMatrix, var srcRowOffset:Long,
                               dst:DenseMatrix, var dstRowOffset:Long, rowCnt:Long) :Long {
        if (CompilerFlags.checkBounds()) {
        //Make sure the source and destination are bounded.
            Debug.assure(src.N <= dst.N, "Number of columns in source "+src.N+" is larger than the destination "+dst.N);
            Debug.assure(srcRowOffset+rowCnt <= src.M, "Source offset "+srcRowOffset+" + row count "+rowCnt+" exceeds source dimension "+src.M);
            Debug.assure(dstRowOffset+rowCnt <= dst.M, "Destination offset "+dstRowOffset+" + row count "+rowCnt+" exceeds destionation "+dst.M);
        }

        val srcSize:Long = src.M*src.N;
        for (; srcRowOffset < srcSize; 
               srcRowOffset+=src.M, dstRowOffset+=dst.M) {
            Rail.copy(src.d, srcRowOffset, dst.d, dstRowOffset, rowCnt);
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
    public static def copySubset(src:DenseMatrix, var srcRowOffset:Long, var srcColOffset:Long,
             dst:DenseMatrix, var dstRowOffset:Long, var dstColOffset:Long,
             rowCnt:Long, colCnt:Long): Long {
        
        if (CompilerFlags.checkBounds()) {
            Debug.assure(srcColOffset+colCnt <= src.N && 
                         dstColOffset+colCnt <= dst.N, 
                         "illegal column offset or counts in subset copy");
            Debug.assure(srcRowOffset+rowCnt <= src.M && 
                         dstRowOffset+rowCnt <= dst.M, 
                         "illegal row offset or row count in subset copy");
        }

        val srcEnd:Long = src.M * (srcColOffset + colCnt);
        srcRowOffset += srcColOffset * src.M;
        dstRowOffset += dstColOffset * dst.M;
        for (; srcRowOffset < srcEnd; srcRowOffset+=src.M, dstRowOffset+=dst.M) {
            Rail.copy(src.d, srcRowOffset, dst.d, dstRowOffset, rowCnt);
        }        
        return rowCnt * colCnt;
    }

    /**
     * Copy specified subset of matrix in source rail to target.
     * 
     * @param src            the source rail
     * @param srcOffset      the offset in the source rail from which to take the data
     * @param dst            the target dense matrix
     * @param dstRowOffset   the starting row in dense matrix to store the copy data
     * @param dstColOffset   the starting column in dense matrix to store the copy data
     * @param rowCnt         number of rows to copy
     * @param colCnt         number of column to copy
     */
    public static def copySubset(src:Rail[Double], var srcOffset:Long,
             dst:DenseMatrix,
             var dstRowOffset:Long, var dstColOffset:Long,
             rowCnt:Long, colCnt:Long): Long {
        
        if (CompilerFlags.checkBounds()) {
            Debug.assure(dstColOffset+colCnt <= dst.N, 
                         "illegal column offset or counts in subset copy");
            Debug.assure(dstRowOffset+rowCnt <= dst.M, 
                         "illegal row offset or row count in subset copy");
            Debug.assure(rowCnt*colCnt <= src.size, 
                         "subset is larger than source rail");
        }

        dstRowOffset += dstColOffset * dst.M;
        for (var col:Long = 0; col < colCnt; col++) {
            Rail.copy(src, srcOffset, dst.d, dstRowOffset, rowCnt);
            srcOffset += rowCnt;
            dstRowOffset += dst.M;
        }
        return rowCnt * colCnt;
    }
    
    /**
     * Access data at(x, y) position in the dense matrix
     *
     * @param  x  the x-th row 
     * @param  y  the y-th column
     */
    public final @Inline operator this(x:Long, y:Long):Double=d(y*M+x);

    /**
     * Set the value v at(x, y) in the dense matrix
     *
     * @param  x  the x-th row 
     * @param  y  the y-th column
     */
    public final @Inline operator this(x:Long,y:Long) = (v:Double):Double {
        d(y*M+x) = v;
        return v;
    }

    /**
     * Get a subset matrix at(row_offset, col_offset) of size (row, col), 
     * put data in dm.
     *
     * @param  row_offset  the starting row of submatrix
     * @param  col_offset  the start column of submatrix
     * @param  row         the number of rows in submatrix
     * @param  col         the number of columns in submatrix
     * @param  dm          the targeting dense matrix to store the submatrix
     */
    public  def subset(row_offset:Long, 
               col_offset:Long, 
               row:Long, col:Long, 
               dm:DenseMatrix) : void {
        copySubset(this, row_offset, col_offset, dm, 0, 0, row, col);
    }

    /**
     * Return a dense matrix, which is the subset matrix at(row_offset, col_offset) 
     * of size (row, col). 
     *
     * @param  row_offset  the starting row of submatrix
     * @param  col_offset  the starting column of submatrix
     * @param  row         the number of rows in submatrix
     * @param  col         the number of columns in submatrix
     * @return the submatrix in dense format
     */
    public def subset(row_offset:Long, 
              col_offset:Long, 
              row:Long, col:Long):DenseMatrix {
        val nm = DenseMatrix.make(row, col);
        subset(row_offset, col_offset, row, col, nm);
        return nm;
    }



    // Transpose


    /**
     * Transpose this dense matrix and store in the input dense matrix.
     * 
     * @param m        object to store the transposed matrix
     */
    public def T(m:DenseMatrix(N,M)) : void {
        var src_idx:Long =0;
        var dst_idx:Long =0;
        //Need to be more efficient
        //Possible idea is to tranpose or copy small block each time.
        //Debug.assure(this.M==m.N&&this.N==m.M);
        for (var c:Long=0; c < this.N; c++) {
            dst_idx = c;
            for (var r:Long=0; r < this.M; r++, dst_idx+=m.M, src_idx++) {
                m.d(dst_idx) = this.d(src_idx);
            }
        }
    }

    /*
     * Transpose matrix data into a new dense matrix. 
     * For dense matrix, it is quite complex to use source data storage to hold
     * tranposed result. We only provide tranpose method of using
     * additional space to store the result
     */
    public def T(): DenseMatrix(N,M) {
        val nm = DenseMatrix.make(N,M);
        T(nm);
        return nm;
    }

//    Transpose using the same memory space is very complex.
//     public def T():DenseMatrix(N,M) {
//         if (M==1||N==1) return new DenseMatrix(N, M, this.d);
//         if (M==N) return squareSelfT() as DenseMatrix(N,M);
//         
//         for (var len:Long=1; len<M; len++) {
//             val sttidx:Long=len;
//             val sttval:Double = this.d(sttidx);
//             val endidx:Long = (sttidx%M)*N+ sttidx/M;
//             var srcidx:Long=sttidx;
//             var preidx:Long=0;
// 
//             while (endidx != srcidx) {
//                 preidx = (srcidx % N) * M + srcidx/N;
//                 this.d(srcidx) = this.d(preidx);
//                 srcidx = preidx;
//             }
//             this.d(endidx) = sttval;
//         }
//         return new DenseMatrix(N,M,this.d);
//     }
    
    /*
     * Only apply to square matrix, using the same memory space to transpose dense matrix.
     * This method is destructive for source matrix.
     */
    public def selfT(): DenseMatrix(this) {
        var src_idx:Long =0;
        var dst_idx:Long =0;
        var swaptmp:Double = 0;
        Debug.assure(this.M==this.N, "Cannot perform transpose for non-square matrix");
        for (var c:Long=0; c < this.M; c++) {
            dst_idx = (c+1)*this.M+c;
            src_idx = c * this.M + c + 1;
            for (var r:Long=c+1; r < this.M; r++, dst_idx+=M, src_idx++) {
                swaptmp = this.d(dst_idx);
                this.d(dst_idx) = this.d(src_idx);
                this.d(src_idx) = swaptmp;
            }
        }
        return this;
    }
    

    // Cell-wise operations


    /**
     * Raise each element in the matrix by a factor.
     *
     * @param  a    the scaling factor
     * @return         result ("this")
     */
    public  def scale(a:Double):DenseMatrix(this)  {
        for (var i:Long=0; i<M*N; i++) 
            this.d(i) = this.d(i) * a;
        return this;
    }

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
        for (var c:Long=0; c<N; c++) {
            for (var r:Long=0; r<M; r++) {
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
        for (var i:Long=0; i<N*M; i++) {
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
        var max:Double = 0.0;
        for (var i:Long=0; i<N*M; i++) {
            max = Math.max(Math.abs(d(i)), max);
        }
        return max;
    }

    /**
     * Compute the trace of this matrix (sum of diagonal elements)
     *
     * @return the sum of diagonal elements
     */
    public def trace():Double {
        var tr:Double = 0.0;
        for (var i:Long=0; i<M*N; i+=M+1)
            tr += d(i);
        return tr;
    }
    
    /**
     * Compute the sum of all matrix elements
     * 
     * @return        the sum of all matrix elements
     */
    public def sum():Double {
        var tt:Double = 0.0;
        for (var i:Long=0; i<M*N; i++)
            tt += d(i);
        return tt;
    }
    
    /**
	 * Replace each element x_ij in this matrix with the exponential e^(x_ij).
     *
     * @return         result ("this" instance)
     */
    public def exp():DenseMatrix(this) {
        for (var i:Long=0; i<M*N; i++) {
            d(i) = Math.exp(d(i));
        }
        return this;
    }

    /**
     * Cell-wise add: this += [v]
     * 
     * @param   v    value to add to all elements
     * @return        result ("this" instance)
     */
    public def cellAdd(v:Double):DenseMatrix(this) {
        for (var i:Long=0; i<M*N; i++) 
            this.d(i) += v;
        return this;
    }
    
    /**
     * Cell-wise add: this += x.
     *
     * @param   x     input matrix to be added to "this"
     * @return        result ("this")
     */
    public def cellAdd(x:Matrix(M,N)):DenseMatrix(this)   {
        x.cellAddTo(this); //Double-dispatch
        return this;
    }

    /**
     * Cell-wise add: this += x.
     *
     * @param   x     input dense matrix to be added
     * @return         result
     */
    public def cellAdd(x:DenseMatrix(M,N)):DenseMatrix(this)  {
         for (var i:Long=0; i < this.N*this.M; i++) this.d(i) += x.d(i);
         return this;
    }

    public def cellAdd(x:TriDense(M,N)):DenseMatrix(this)  = 
        x.cellAddTo(this);

    public def cellAdd(x:SymDense(M,N)):DenseMatrix(this)  = 
        x.cellAddTo(this);
    
    /**
     * Perform cell-wise add: x = this + x
     * 
     * @param x        input matrix to be added with this
     * @return        result (the input x).
     */
    protected def cellAddTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
        x.cellAdd(this);
        return x;
    }


    // Subtract operation method

    /**
     * Cell-wise subtract: this = this - [v]
     * 
     * @param   v     value subtracting all elements
     * @return        result ("this")
     */
    public def cellSub(v:Double):DenseMatrix(this) {
        for (var i:Long=0; i<M*N; i++) 
            this.d(i) -= v;
        return this;
    }
    
    /**
     * Cell-wise subtract: this = [v] - this
     * 
     * @param   v      value to subtract from
     * @return         result matrix
     */
    public def cellSubFrom(v:Double):DenseMatrix(this) {
        for (var i:Long=0; i<M*N; i++) 
            this.d(i) = v - this.d(i);
        return this;
    }
    
    /**
     * Cell-wise subtract: this = this - x
     *
     * @param  x     subtracting matrix
     * @return        result
     */
    public def cellSub(x:Matrix(M,N)):DenseMatrix(this)   {
        x.cellSubFrom(this);
        return this;
    }
    
    /**
     * Cell-wise subtract: this = this - x
     *
     * @param  x    subtracting dense matrix
     * @return        result
     */
    public def cellSub(x:DenseMatrix(M,N)):DenseMatrix(this)   {
         for (var i:Long=0; i < this.N*this.M; i++) this.d(i) -= x.d(i);
         return this;
    }
    
    /**
     * Cell-wise subtract: x = x - this
     * 
     * @param  x    dense matrix to be subtracted by "this"
     * @return        result (input x)
     */
    protected def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x) {
        x.cellSub(this);
        return x;
    }

    /**
     * this = this - x, where x is triangular matrix
     */
    public def cellSub(x:TriDense(M,N)):DenseMatrix(this)   =
        x.cellSubFrom(this);

    public def cellSub(x:SymDense(M,N)):DenseMatrix(this)   =
        x.cellSubFrom(this);

    

    // Cellwise multiplication

    /**
     * Cell-wise matrix multiply: this = this &#42 x
     *
     * @param  x    the multiplying matrix
     * @return        result matrix
     */
    public def cellMult(x:Matrix(M,N)):DenseMatrix(this)   {
        x.cellMultTo(this);//Double-dispatch
        return this;
    }
    
    /**
     * Cell-wise matrix multiply of this = this &#42 x
     * *
     * @param  x    the multiplying matrix
     * @return        result matrix
     */
    public def cellMult(x:DenseMatrix(M,N)):DenseMatrix(this)   {
         for (var i:Long=0; i < this.M*this.N; i++) this.d(i) *= x.d(i);
         return this;
    }
    
    /**
     * Cell-wise matrix multiply:  x = x &#42 this
     * *
     * @param  x    the first matrix and returning result
     * @return        result in matrix x
     */
    protected def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x)  {
        x.cellMult(this);
        return x;
    }

    public def cellMult(x:TriDense(M,N)):DenseMatrix(this)   =
        x.cellMultTo(this);//Double-dispatch

    public def cellMult(x:SymDense(M,N)):DenseMatrix(this)   =
        x.cellMultTo(this);//Double-dispatch

    

    // Cellwise division

    /**
     * Cell-wise division on every element, this = this / v
     * 
     * @param   v      value to be divided to all elements
     * @return         result in "this" matrix 
     */
    public def cellDiv(v:Double):DenseMatrix(this) {
        for (var i:Long=0; i<M*N; i++) 
            this.d(i) /= v;
        return this;
    }
    /**
     * Cell-wise division by on every element, this = [v] / this
     * 
     * @param   v      value to be divided by
     * @return         result in "this" object
     */
    public def cellDivBy(v:Double):DenseMatrix(this) {
        for (var i:Long=0; i<M*N; i++) 
            this.d(i) = v / this.d(i);
        return this;
    }
    
    /**
     * Cell-wise matrix division. Return this = this / x
     * 
     * @param x        input matrix to divide
     * @return        result
     */
    public  def cellDiv(x:Matrix(M,N)):DenseMatrix(this)  {
        x.cellDivBy(this);
        return this;
    }

    /**
     * Cell-wise division, returning this = this / x;
     * 
     * @param x        input dense matrix to divide
     * @return        result
     */
    public def cellDiv(x:DenseMatrix(M,N)):DenseMatrix(this)  {
         for (var i:Long=0; i < M*N; i++) {
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
     * @param x        input matrix to be divided
     * @return         result (in input x)
     */
    protected def cellDivBy(x:DenseMatrix(M,N)):DenseMatrix(x)  {
        x.cellDiv(this);
        return x;
    }

    public  def cellDiv(x:SymDense(M,N)):DenseMatrix(this) =
        x.cellDivBy(this);
    

    // Matrix multiply operations: self this<- op(A)*op(B) + (plus?1:0) * C
    // Default is using BLAS driver


    /**
     * Multiply two matrices and return this += A *&#42 B if plus is true, otherwise 
     * this = A &#42 B using X10 matrix multiplication driver.
     * Result is stored in "this" object, which is dense matrix.
     * Input parameters are two matrices in the base matrix type. 
     * <p>
     * Performance notice: This method could take more time becasue the input
     * matrix types are not specified, and matrix multiplication is not optimized.
     * 
     * @param  A      the first matrix in multiply
     * @param  B      the second matrix
     * @param plus     add-on flag
     * @return         result ("this" the method invoking object)
     */
    public def mult(A:Matrix(this.M), B:Matrix(A.N,this.N), plus:Boolean):DenseMatrix(this) {
        if (A instanceof DenseMatrix){
            if (B instanceof SparseCSC)
                return mult(A as DenseMatrix(A), B as SparseCSC(B), plus);
            else if (B instanceof DenseMatrix)
                return mult(A as DenseMatrix(A), B as DenseMatrix(B), plus);                
        } else if (A instanceof SparseCSC) {
            if (B instanceof SparseCSC)
                return mult(A as SparseCSC(A), B as SparseCSC(B),  plus);
            else if (B instanceof DenseMatrix)
                return mult(A as SparseCSC(A), B as DenseMatrix(B), plus);
        }
        Debug.flushln("Resort to basic X10 matrix multiplication driver");
        MatrixMultXTen.comp(A, B, this, plus);
        return this;
    }
    
    /**
     * Multiply two matrices and return this += A<sup>T</sup> &#42 B if plus is true, otherwise
     * this = A<sup>T</sup> &#42 B,
     * where the first matrix A is in transposed form in 
     * the matrix multiply operation.
     *
     * @param  A      the first matrix in multiplication
     * @param  B      the second matrix
     * @param plus     add-on flag
     * @return         result ("this" the method invoking object)
    */
    public def transMult(A:Matrix{self.N==this.M}, B:Matrix(A.M,this.N), plus:Boolean): DenseMatrix(this) {
        
        if (A instanceof DenseMatrix){
            if (B instanceof SparseCSC)
                return transMult(A as DenseMatrix(A), B as SparseCSC(B), plus);
            else if (B instanceof DenseMatrix)
                return transMult(A as DenseMatrix(A), B as DenseMatrix(B), plus);                
        } else if (A instanceof SparseCSC) {
            if (B instanceof SparseCSC)
                return transMult(A as SparseCSC(A), B as SparseCSC(B),  plus);
            else if (B instanceof DenseMatrix)
                return transMult(A as SparseCSC(A), B as DenseMatrix(B), plus);
        }
        
        Debug.flushln("Resort to X10 matrix multiplication driver");
        MatrixMultXTen.compTransMult(A, B, this, plus);
        return this;
    }

    /**
     * Multiply two matrices and return this += A &#42 B<sup>T</sup> if plus is true, otherwise
     * this = A &#42 B<sup>T</sup> where the second matrix B is accessed in its transposed form in
     * the matrix multiply operation. 
     * 
     * @param  A      the first matrix in multiplication
     * @param  B      the second matrix in multiplication
     * @param plus     add-on flag
     * @return         result ("this", the method invoking method)
     */
    public def multTrans(A:Matrix(this.M), B:Matrix(this.N, A.N), plus:Boolean):DenseMatrix(this) {
        
        if (A instanceof DenseMatrix){
            if (B instanceof SparseCSC)
                return multTrans(A as DenseMatrix(A), B as SparseCSC(B), plus);
            else if (B instanceof DenseMatrix)
                return multTrans(A as DenseMatrix(A), B as DenseMatrix(B), plus);                
        } else if (A instanceof SparseCSC) {
            if (B instanceof SparseCSC)
                return multTrans(A as SparseCSC(A), B as SparseCSC(B),  plus);
            else if (B instanceof DenseMatrix)
                return multTrans(A as SparseCSC(A), B as DenseMatrix(B), plus);
        }
        Debug.flushln("Resort to basic X10 matrix multiplication driver");
        MatrixMultXTen.compMultTrans(A, B, this, plus);
        return this;
    }
    

    /**
     * Multiply two dense matrices and return this = A &#42 B using BLAS driver.
     *
     * @param  A     first dense matrix in multiply
     * @param  B     second dense matrix in multiply
     * @return        result of multiply ("this", the method invoking object)
     */
    public def mult(A:DenseMatrix(this.M), B:DenseMatrix(A.N,this.N))=
        mult(A, B, false);

    /**
     * Multiply two dense matrix and return this += A &#42 B if plus is true,
     * otherwise this = A &#42 B using BLAS drivers.
     * 
     * @param  A     first dense matrix in multiply
     * @param  B     second dense matrix in multiply
     * @param  plus    result add-on flag
     * @return        result
     */
    public def mult(A:DenseMatrix(this.M), B:DenseMatrix(A.N, this.N), plus:Boolean) {
        val alpha = 1.0;
        val beta = plus?1.0:0.0;
        DenseMatrixBLAS.comp(alpha, A, B, beta, this);
        return this;
    }
    
    /**
     * Multiply two dense matrix and return this = A<sup>T<sup> &#42 B 
     * where the first matrix in transposed for multiplication.
     * 
     * @param  A     first dense matrix
     * @param  B     second dense matrix used in transposed
     * @return        result
     */
    public def transMult(A:DenseMatrix{self.N==this.M}, B:DenseMatrix(A.M, this.N)) = 
        transMult(A, B, false);
    
    /**
     * Multiply two dense matrices and return this += A<sup>T<sup> &#42 B if plus is true,
     * otherwise this = A<sup>T<sup> &#42 B. 
     * It uses BLAS drivers, where the first matrix is in transposed format for multiplication.
     * 
     * @param  A     first matrix
     * @param  B     second matrix
     * @param  plus    add-on flag
     * @return        result
     */
    public def transMult(A:DenseMatrix{self.N==this.M}, B:DenseMatrix(A.M,this.N), plus:Boolean) {
		val alpha = 1.0;
		val beta = plus?1.0:0.0;
        DenseMatrixBLAS.compTransMult(alpha, A, B, beta, this);
        return this;
    }

    /**
     * Multiply two dense matrices and return this = A  &#42 T(B) or this += A  &#42 T(B) if plus is true
     * using BLAS drivers, where the second matrix is in transposed format.
     * 
     * @param  A     first matrix
     * @param  B     second matrix
     * @param  plus    add-on flag
     * @return        result
     */
    public def multTrans(A:DenseMatrix(this.M), B:DenseMatrix(this.N,A.N), plus:Boolean) {
		val alpha = 1.0;
		val beta = plus?1.0:0.0;
        DenseMatrixBLAS.compMultTrans(alpha, A, B, beta, this);
        return this;
    }

    /**
     * Multiply two dense matrices and return this = A &#42 B<sup>T<sup>  using BLAS drivers, where
     * the second matrix is in transposed format.
     * 
     * @param  A     first matrix
     * @param  B     second matrix
     * @return        result
     */
    public def multTrans(A:DenseMatrix(this.M), B:DenseMatrix(this.N,A.N)) = 
        multTrans(A, B, false);
    

    /**
     * this =this &#42 A
     */
    public def mult(A:TriDense(this.N)):DenseMatrix(this) {
        DenseMatrixBLAS.comp(this, A);
        return this;
    }
    
    /**
     * this = A &#42 this
     */
    public def multBy(A:TriDense(this.M)):DenseMatrix(this) {
        DenseMatrixBLAS.comp(A, this);
        return this;
    }

    /**
     * this =this<sup>T<sup> &#42 A
     */
    public def transMult(A:TriDense(this.N)):DenseMatrix(this) {
        DenseMatrixBLAS.compTransMult(this, A);
        return this;
    }
    
    /**
     * this = A &#42 this<sup>T<sup>
     */
    public def multTransBy(A:TriDense(this.M)):DenseMatrix(this) {
        DenseMatrixBLAS.compMultTrans(A, this);
        return this;
    }
    

    // Sparse multiply to Dense

    public def mult(A:SparseCSC(this.M), B:SparseCSC(A.N, this.N), plus:Boolean):DenseMatrix(this)=
        SparseMultSparseToDense.comp(A, B, this, plus);
    
    public def mult(A:DenseMatrix(this.M), B:SparseCSC(A.N, this.N), plus:Boolean):DenseMatrix(this) =
        DenseMultSparseToDense.comp(A, B, this, plus);
    
    public def mult(A:SparseCSC(this.M), B:DenseMatrix(A.N, this.N),plus:Boolean):DenseMatrix(this) =
        SparseMultDenseToDense.comp(A, B, this, plus);
    

    public def transMult(A:SparseCSC{self.N==this.M},B:SparseCSC(A.M,this.N), plus:Boolean): DenseMatrix(this) = 
        SparseMultSparseToDense.compTransMult(A, B, this, plus);
    
    public def transMult(A:DenseMatrix{self.N==this.M},B:SparseCSC(A.M,this.N), plus:Boolean): DenseMatrix(this) = 
        DenseMultSparseToDense.compTransMult(A, B, this, plus);

    public def transMult(A:SparseCSC{self.N==this.M},B:DenseMatrix(A.M,this.N), plus:Boolean): DenseMatrix(this) = 
        SparseMultDenseToDense.compTransMult(A, B, this, plus);


    public def multTrans(A:SparseCSC(this.M), B:SparseCSC(this.N,A.N),plus:Boolean):DenseMatrix(this) =
        SparseMultSparseToDense.compMultTrans(A, B, this, plus);
    
    public def multTrans(A:DenseMatrix(this.M), B:SparseCSC(this.N,A.N),plus:Boolean):DenseMatrix(this) =
        DenseMultSparseToDense.compMultTrans(A, B, this, plus);

    public def multTrans(A:SparseCSC(this.M), B:DenseMatrix(this.N,A.N),plus:Boolean):DenseMatrix(this) =
        SparseMultDenseToDense.compMultTrans(A, B, this, plus);


    /**
     * Operator overloading for cell-wise matrix subtraction, and return this - that in a new dense format
     */
    public operator - this = clone().scale(-1.0)                as DenseMatrix(M,N);
    public operator (v:Double) + this = this.clone().cellAdd(v) as DenseMatrix(M,N);
    public operator this + (v:Double) = this.clone().cellAdd(v) as DenseMatrix(M,N);

    public operator this - (v:Double) = this.clone().cellAdd(-v)    as DenseMatrix(M,N);
    public operator (v:Double) - this = this.clone().cellSubFrom(v) as DenseMatrix(M,N);
    
    public operator this / (v:Double) = this.clone().scale(1.0/v) as DenseMatrix(M,N);
    //public operator (v:Double) / this = this.clone().cellDivBy(v) as DenseMatrix(M,N);
    
    public operator this * (alpha:Double) = this.clone().scale(alpha) as DenseMatrix(M,N);
    public operator (alpha:Double) * this : DenseMatrix(M,N) = this * alpha;

    /**
     * Operator overloading for cell-wise add, and return result in a new dense matrix. 
     */
    public operator this + (that:DenseMatrix(M,N)) = this.clone().cellAdd(that) as DenseMatrix(M,N);

    /**
     * Operator overloading for cell-wise subtraction, and return this - that in a new dense format
     */
    public operator this - (that:DenseMatrix(M,N)) = this.clone().cellSub(that) as DenseMatrix(M,N);
    
    /**
     * Operator overloading for cell-wise multiplication, and return cell-wise multiply result of
     * this &#42 that in dense format
     */
    public operator this * (that:DenseMatrix(M,N)) = this.clone().cellMult(that) as DenseMatrix(M,N);

    /**
     * Operator overloading for cell-wise division, and return this / that in a new dense matrix
     */
    public operator this / (that:DenseMatrix(M,N)) = this.clone().cellDiv(that) as DenseMatrix(M,N);

    /**
     * Operator overload for matrix multiplication. Return dense matrix multiplication this &#42 that 
     * using BLAS driver.
     */
    public  operator this % (that:DenseMatrix{self.M==this.N}):DenseMatrix(this.M,that.N) {
        val dm = DenseMatrix.make(this.M, that.N);
		val alpha = 1.0;
		val beta = 0.0;
        DenseMatrixBLAS.comp(alpha, this, that, beta, dm);
        return dm;
    }
    

    // Utils

    /**
     * Check matrix type and dimensions
     * 
     * @param    m    checking matrix
     * @return         true if m is dense matrix and has the same dimensions
     */
    public def likeMe(m:Matrix):Boolean {
        if ((m instanceof DenseMatrix) && m.M==M && m.N==N) return true;
        return false;
    }

    /**
     * Convert the whole dense matrix into a string
     */
    public def toString() : String {
        val outstr = new StringBuilder();
        outstr.add("--------- Dense Matrix "+M+" x "+N+" ---------\n");
        for (var r:Long=0; r<M; r++) {
            outstr.add(r.toString()+"\t[ ");
            for (var c:Long=0; c<N; c++)
                outstr.add(this(r,c).toString()+" ");
            outstr.add("]\n");
        }
        outstr.add( "---------------------------------------\n");
        return outstr.toString();         
    }
}
