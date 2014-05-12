/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.matrix.block;

import x10.matrix.util.Debug;
import x10.matrix.util.VerifyTool;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.Compress1D;
import x10.matrix.sparse.SparseCSC;

public type SparseBlockMatrix(M:Long)=SparseBlockMatrix{self.M==M};
public type SparseBlockMatrix(M:Long, N:Long)=SparseBlockMatrix{self.M==M, self.N==N};
public type SparseBlockMatrix(C:SparseBlockMatrix)=SparseBlockMatrix{self==C};

/**
 * OBSOLETE. Replace by BlockMatrix
 * 
 * Sparse block matrix is constructed by a Rail of sparse blocks, and
 * partitioning grid specifies how each block is mapped to the whole matrix.
 *  
 */
public class SparseBlockMatrix(grid:Grid) extends Matrix  {

	public val listBs:Rail[SparseBlock];

	/**
	 * Construct sparse block matrix instance.
	 *
	 * @param  gp     Grid partition
	 * @param  blkMs  Matrix blocks
	 */
	public def this(gp:Grid, blkMs:Rail[SparseBlock]) {
		super(gp.M, gp.N);
		property(gp);
		listBs = blkMs;
	}

	/**
	 * Construct block matrix instance without memory allocation for blocks.
	 *
	 * @param  gp  Grid partition
	 */
	public def this(gp:Grid) {
		super(gp.M, gp.N);
		property(gp);
		listBs = new Rail[SparseBlock](gp.numRowBlocks* gp.numColBlocks);

	}

	/**
	 * Create an instance of sparse-block matrix, and allocate memory space
	 * for each block. All block have the same sparsity
     * This method must be combined with setBlock(...) to set each MatrixBlock
	 *
	 * @param  gp   	block partitioning of matrix
	 * @param nzd   	sparsity
	 */
	public static def make(gp:Grid, nzd:Double):SparseBlockMatrix(gp.M, gp.N) {
		val dbm = new SparseBlockMatrix(gp) as SparseBlockMatrix(gp.M, gp.N);
		dbm.alloc(nzd);
		return dbm;
	}

	/**
	 * Make a sparse block matrix and initialize each elment with a random
	 * value.
	 *
	 * @param  gp  	matrix partitioning of matrix
	 * @param nzd  	nonzero sparsity for all blocks
	 *
	 */
	public static def makeRand(gp:Grid, nzd:Double) : SparseBlockMatrix(gp.M, gp.N) {
		val dbm = new SparseBlockMatrix(gp);
		dbm.alloc(nzd);
		dbm.initRandom();
		return dbm;
	}

	/**
	 * Allocate memory space for sparse blocks in the grid partitioning
	 *
	 * @param nzd 		sparsity for all blocks
	 */
	public def alloc(nzd:Double):void {
		for(var c:Long=0; c<grid.numColBlocks; c++) {
			for (var r:Long=0; r<grid.numRowBlocks; r++) {
				val p = grid.getBlockId(r, c);
				listBs(p) = SparseBlock.make(grid, r, c, nzd);
			}
		}
	}

	/**
	 * Allocate memory space for mxn sparse block matrix. Not supported
	 * in sparse block matrix and an exception is thrown, because the
	 * partitioning is unknown.
	 * 
	 */
	public def alloc(m:Long, n:Long):SparseBlockMatrix(m,n) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Clone self
	 *
	 */
	public def clone():SparseBlockMatrix(M,N) {
		val nbm = new SparseBlockMatrix(this.grid);
		for (p in 0..(listBs.size-1)) {
			nbm.listBs(p) = this.listBs(p).clone();//getMatrix(p).clone();
		}
		return nbm as SparseBlockMatrix(M,N);
	}


	/**
	 * Initialize sparse block matrix with a constant value and specified sparsity
	 *
	 * @param ival 	constant value for all elements
	 * @param nzd  	sparsity for all blocks
	 */
	public def init(ival:Double, nzd:Double):void {
		for (p in 0..(listBs.size-1)) {
			listBs(p).sparse.init(ival, nzd);
		}
	}

	/**
	 * Initialize all nonzero elements in sparse block matrix to a specified value. 
	 * The index distance btween two adjacent nonzero elements in the same columns is
	 * determined by the storage size.
	 *
	 * @param ival 		constant value for all elements
	 */
	public def init(ival:Double):SparseBlockMatrix(this) {
		for (p in 0..(listBs.size-1)) {
			listBs(p).sparse.init(ival);
		}
		return this;
	}

	public def init(f:(Long, Long)=>Double):SparseBlockMatrix(this) {
		for (var cb:Long=0; cb<grid.numColBlocks; cb++)
			for (var rb:Long=0; rb<grid.numRowBlocks; rb++ ) {
				listBs(grid.getBlockId(rb, cb)).init(f);
			}		
		return this;
	}
	
	/**
	 * Initialize sparse block matrix with random values and specified sparsity
	 * which is used to determine the index distance between adjacent nonzero
	 * elements in the same column.
	 * 
	 * @param nzd  	sparsity for all blocks
	 */
	public def initRandom(nzd:Double):SparseBlockMatrix(this) {
		for (p in 0..(listBs.size-1)) {
			listBs(p).sparse.initRandom(nzd);
		}
		return this;
	}

	/**
	 * Initialize sparse block matrix with random values. The sparsity of
	 * each block is computed by the storage size over the size of matrix.
	 */
	public def initRandom():SparseBlockMatrix(this) {
		for (p in 0..(listBs.size-1)) {
			listBs(p).sparse.initRandom();
		}
		return this;
	}
	
	public def initRandom(lo:Long, up:Long):SparseBlockMatrix(this) {
		for (p in 0..(listBs.size-1)) {
			listBs(p).sparse.initRandom(lo,up);
		}
		return this;
	}


	// Data copy and reset


	/**
	 * Reset all nonzero entries, and the number of nonzeros to 0.
	 *
	 */
	public def reset():void {
		for (p in 0..(listBs.size-1)) {
			listBs(p).sparse.reset();
		}
	}

	/**
	 * Copy element values to the target matrix of the same dimension.
	 *
	 * @param dst 		the target dense matrix.		
	 */
	public def copyTo(dm:DenseMatrix(M,N)) {

		//Debug.assure(this.M==dm.M&&this.N==dm.N);
		var dstcolidx:Long=0;
		
		for (var cb:Long=0; cb < grid.numColBlocks; cb++) {
			var dstblkidx:Long = dstcolidx;

			for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
				val src = listBs(grid.getBlockId(rb, cb)).sparse;
				var srcoff:Long = 0;
				var dstoff:Long = dstblkidx;
				//Uncompress column by column
				for (var col:Long=0; col<src.N; col++, srcoff+=src.M, dstoff+=dm.M)
					src.ccdata.cLine(col).extract(dstoff, dm.d);

				dstblkidx += src.M;
			}

			dstcolidx += grid.colBs(cb) * this.M;
		}
	}

	/**
	 * Convert to a dense matrix
	 */
	public def toDense():DenseMatrix(M,N) {
		val dm = DenseMatrix.make(M,N);
		copyTo(dm);
		return dm;
	}
		
	/**
	 * Copy data from blocks to a sparse CSC matrix
	 * 
	 * @param dst 	target sparse matrix
	 */
	public def copyTo(dst:SparseCSC(M,N)): void {

		val nzcnt:Long = countNonZero();
		//Check storage size
		dst.testIncStorage(0, nzcnt);
		
		var dstcol:Long=0;
		var cnt:Long=0;
		for (var cb:Long=0; cb < grid.numColBlocks; cb++) {
			for (var col:Long=0; col < grid.colBs(cb); col++, dstcol++) {
				val dstln = dst.getCol(dstcol);
				var sttidx:Long = 0;

				dstln.offset = cnt;
				for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
					val bid = grid.getBlockId(rb, cb);
					val src = listBs(bid).sparse;
					val srcln = src.getCol(col);
 
					srcln.appendTo(dstln, sttidx);

					sttidx += src.M;
					cnt   += srcln.length;
				}
			}
		}
	}
	
	public def copyTo(that:DenseBlockMatrix(M,N)):void {
		
	}

	public def copyTo(that:Matrix(M,N)):void {
		
	}
	
	/**
	 *  Copy data from sparse matrix to myself. Used for scatter operation.
	 * 
	 * @param sm 	source sparse matrix
	 */
	public def copyFrom(sm:SparseCSC(M,N)): void {
		var srccol:Long=0;
		var cnt:Long = 0;

		for (var cb:Long=0; cb < grid.numColBlocks; cb++) {
			for (var col:Long=0; col < grid.colBs(cb); col++, srccol++) {
				val srcln = sm.getCol(srccol);
				var sttidx:Long = 0;
				
				for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
					val bid = grid.getBlockId(rb, cb);
					val dst = listBs(bid).getMatrix();
					val dstln = dst.getCol(col);
					
					dstln.offset = (col==0L)?0L:dst.getCol(col-1).offset+dst.getCol(col-1).length;
					//NOTE: storage re-allocation if source data > storage size.
					//Performance concern.
					Compress1D.copySection(srcln, sttidx, dstln, dst.M);
					
					sttidx += dst.M;
				}
			}
		}
	}

	/**
	 *  Return the matrix instance of matrix block at index of array
	 *
	 * @param  i  index of matrix block in array
	 */
	public def getMatrix(i:Long) <: Matrix = this.listBs(i).getMatrix();

	/**
	 *  Return element value in matrix. override the method in super class.
	 *
	 * @param  r  the r-th rows in the matrix
	 * @param  c  the c-th columns in the matrix
	 */
	public operator this(r:Long, c:Long):Double {
		val loc = grid.find(r, c);
		val bid = grid.getBlockId(loc(0), loc(1));
		return listBs(bid).sparse(loc(2), loc(3));
	}

	/**
	 *  Set element value at the specified position.
	 *
	 * @param  r  the index of rows in the matrix
	 * @param  c  the index of columns in the matrix
	 */
	public  operator this(x:Long,y:Long)=(v:Double):Double  {
		val loc = grid.find(x, y);
		val bid = grid.getBlockId(loc(0), loc(1));
		listBs(bid).sparse(loc(2), loc(3))=v;
		return v;
	}

	/**
	 * Raise each cell in the matrix by the factor of a:Double.
	 *
	 * @param  a  	the scaling factor
	 */
	public  def scale(a:Double) {
		for (p in 0..(listBs.size-1)) {
			listBs(p).sparse.scale(a);
		}
		return this;
	}

    /**
     * Not valid operation for sparse matrix
	 *
	 * @param   x  the source matrix to be added with
     */
    public def cellAdd(x:Matrix(M,N)):SparseBlockMatrix(this)  {
		Debug.exit("Matrix add does not support using sparse matrix to store result");
	    return this;
	}

    public def cellAdd(d:Double):SparseBlockMatrix(this)  {
    	Debug.exit("Matrix add does not support using sparse matrix to store result");
    	return this;
    }
    
	/**
	 * dst = this + dst
	 */
	public def cellAddTo(dst:DenseMatrix(M,N)) {
		Debug.exit("Not implemented");
		return dst;
	}


    /**
     * Cell-wise subtraction: this -= x, not support by sparse matrix
	 *
	 * @param  x  the subtracting matrix
     */
    public def cellSub(x:Matrix(M,N))  {
		Debug.exit("Matrix substract does not support using sparse matrix as target");
		return this;
	}
	/**
	 * x = x - this
	 */
	protected def cellSubFrom(x:DenseMatrix(M,N)) {
		Debug.exit("Not implemented");
		return x;
	}
	
	public def cellSubFrom(dv:Double) : SparseBlockMatrix(this) {
		Debug.exit("Not implemented");
		return this;
	}


    /**
     * Cell-wise multiplication, return this = this &#42 x
	 *
	 * @param  x  the multiplying matrix
     */
    public def cellMult(x:Matrix(M,N))  {
		Debug.exit("Matrix cell mult does not support using sparse as target matrix");
	    return this;
	}

	/**
	 * Compute x = this &#42 x 
	 */
	protected def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		Debug.assure(false, "Not implement");
		return x;
	}



    /**
     * Cell-wise division, return this /= x
     */
	public  def cellDiv(x:Matrix(M,N)) {
		Debug.exit("Matrix div does not support using sparse matrix as target");
	
	    return this;
	}

	/**
	 * x = this / x
	 */
    protected def cellDivBy(x:DenseMatrix(M,N)):DenseMatrix(x) {
		Debug.assure(false, "Not implement");
		return x;		
	}

    /**
     * Not supported. 
     */
	public def mult(
			A:Matrix(this.M), 
			B:Matrix(A.N,this.N), 
			plus:Boolean):SparseBlockMatrix(this) {
		Debug.exit("Matrix mult does not support using sparse matrix as target");
		return this;	
	}		

	/** 
	 * Not supported. 
	 */
	public def transMult(
			A:Matrix{self.N==this.M}, 
			B:Matrix(A.M,this.N),
			plus:Boolean):SparseBlockMatrix(this) {
		Debug.exit("Matrix mult does not support using sparse matrix as target");
		return this;		
    }

	/** 
	 * Not supported. 
	 */
	public def multTrans(
			A:Matrix(this.M), 
			B:Matrix(this.N,A.N), 
			plus:Boolean):SparseBlockMatrix(this)	{
		Debug.exit("Matrix mult does not support using sparse matrix as target");
		return this;		
    }


	public operator - this = this.clone().scale(-1.0) as SparseBlockMatrix(M,N);
	
	

	/**
	 * Transpose matrix.
	 */
	public def T(dbm:SparseBlockMatrix(N,M)): void {
		Debug.exit("Not implemented");
	}


	/**
	 * Check matrix has the same type, partition and dimension or not.
	 *
	 * @param A 	 input matrix
	 */
	public def likeMe(A:Matrix):Boolean =
	    (A instanceof SparseBlockMatrix &&
	            (A as SparseBlockMatrix).grid.equals(this.grid));

	/**
	 * Check desne block matrix matrix has the same data or not.
	 *
	 * @param m 	input dense block matrix
	 * @return 		true or false
	 */		
	public def equals(m:SparseBlockMatrix(M,N)) =
		VerifyTool.testSame(this as Matrix(M,N), m as Matrix(M,N));

	/**
	 * Convert matrix data into string
	 */
	public def toString():String {
		var output:String="---------- Sparse-block Matrix ["+M+"x"+N+"] ----------\n";;
		for (p in 0..(listBs.size-1)) {
			output+= "--- Sparse block("+p+") ---\n"+listBs(p).toString();
		}
		output += "----------------------------------------------------\n";
		return output;
	}
	
	/**
	 * Get sparsity of all blocks
	 */
	public def countNonZero() : Long {
		var nzcnt:Long=0;
		for (p in 0..(listBs.size-1)) {
			nzcnt += listBs(p).sparse.countNonZero();
		}
		return nzcnt;
	}

	/**
	 * Get sparsity of all blocks
	 */
	public def compSparsity() : Double = 1.0*countNonZero()/M/N;
}
