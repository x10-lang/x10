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

import x10.util.StringBuilder;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.util.VerifyTool;

public type DenseBlockMatrix(M:Long)=DenseBlockMatrix{self.M==M};
public type DenseBlockMatrix(M:Long, N:Long)=DenseBlockMatrix{self.M==M, self.N==N};
public type DenseBlockMatrix(C:DenseBlockMatrix)=DenseBlockMatrix{self==C};


/**
 * OBSOLETE. Use BlockMatrix.
 * 
 * Dense block matrix is constructed by using a Rail of dense blocks, and
 * grid partition specifies how each block is mapped to the overall dense matrix.
 * 
 */
public class DenseBlockMatrix(grid:Grid) extends Matrix  {
	public val listBs:Rail[DenseBlock];

	/**
	 * Construt dense-block matrix instance.
	 *
	 * @param  gp     Grid partition
	 * @param  blkMs  Matrix blocks
	 */
	public def this(gp:Grid, blkMs:Rail[DenseBlock]) {
		super(gp.M, gp.N);
		property(gp);
		listBs = blkMs;
	}

	/**
	 * Construt block matrix instance without memory allocation for blocks.
	 *
	 * @param  gp  Grid partition
	 */
	public def this(gp:Grid) {
		super(gp.M, gp.N);
		property(gp);
		listBs = new Rail[DenseBlock](gp.numRowBlocks* gp.numColBlocks);
	}



	/**
	 * Create an instance of dense-block matrix, and allocate memory space
	 * for all blocks
	 *
	 * @param  gp  	 partitioning of matrix
	 */
	public static def make(gp:Grid):DenseBlockMatrix(gp.M, gp.N) {
		val dbm = new DenseBlockMatrix(gp) as DenseBlockMatrix(gp.M, gp.N);
		dbm.alloc();
		return dbm;
	}
		
	/**
	 * Make a dense block matrix and initialize elments with random values.
	 *
	 * @param  gp  	 partitioning of matrix
	 */
	public static def makeRand(gp:Grid) : DenseBlockMatrix(gp.M, gp.N) {
		
		val dbm = new DenseBlockMatrix(gp);
		dbm.alloc();
		dbm.initRandom();
		return dbm;
	}

	/**
	 * Allocate all dense blocks in the grid partitioning
	 *
	 */
	public def alloc():void {
		//finish ateach([p] :Point in this.dist) {
		for(var c:Long=0; c<grid.numColBlocks; c++) {
			for (var r:Long=0; r<grid.numRowBlocks; r++) {
				val p = grid.getBlockId(r, c);
				listBs(p) = DenseBlock.make(this.grid, r, c);
			}
		}
	}

	/**
	 * Allocate memory space for mxn dense block matrix. Not supported
	 * in dense block matrix, because no matrix partitioning information available.
	 * An exception is thrown.
	 * 
	 */
	public def alloc(m:Long, n:Long):DenseBlockMatrix(m,n) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Make a copy of myself in all places
	 *
	 */
	public def clone():DenseBlockMatrix(M,N) {
		val nbm = new DenseBlockMatrix(this.grid);
		for (p in 0..(listBs.size-1)) {
			nbm.listBs(p) = this.listBs(p).clone();//getMatrix(p).clone();
		}
		return nbm as DenseBlockMatrix(M,N);
	}


	/**
	 * Initialize dense block matrix with a constant value
	 *
	 */
	public def init(ival:Double):DenseBlockMatrix(this) {
		for (p in 0..(listBs.size-1)) {
			listBs(p).init(ival);
		}
		return this;
	}
	
	public def init(f:(Long, Long)=>Double):DenseBlockMatrix(this) {
		for (var cb:Long=0; cb<grid.numColBlocks; cb++)
			for (var rb:Long=0; rb<grid.numRowBlocks; rb++ ) {
				listBs(grid.getBlockId(rb, cb)).init(f);
			}		
		return this;
	}
	
	/**
	 * Initialize dense block matrix with random values 
	 * 
	 */
	public def initRandom():DenseBlockMatrix(this) {
		for (p in 0..(listBs.size-1)) {
			listBs(p).initRandom();
		}
		return this;
	}
	
	public def initRandom(lo:Long, up:Long):DenseBlockMatrix(this) {
		for (p in 0..(listBs.size-1)) {
			listBs(p).initRandom(lo, up);
		}
		return this;
	}

	/**
	 * Reset all entry to 0.0
	 *
	 */
	public def reset():void {
		for (p in 0..(listBs.size-1)) {
			listBs(p).getMatrix().reset();
		}
	}


	// Data copy 

	/**
	 * Copy element values to the target matrix of the same dimension.
	 *
	 * @param dst  -- the target dense matrix.		
	 */
	public def copyTo(dst:DenseMatrix(M,N)):void {
		var dstcolidx:Long=0;
		// Iterate all blocks columnwise
		for (var cb:Long=0; cb < grid.numColBlocks; cb++) {
			var dstblkidx:Long = dstcolidx;

			for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
				val src = listBs(grid.getBlockId(rb, cb)).getMatrix();
				var srcoff:Long = 0;
				var dstoff:Long = dstblkidx;
				for (var col:Long=0; col<src.N; col++, srcoff+=src.M, dstoff+=dst.M)
					Rail.copy[Double](src.d, srcoff, dst.d, dstoff, src.M);
				dstblkidx += src.M;
			}

			dstcolidx += grid.colBs(cb) * this.M;
		}
	}

	public def copyTo(that:DenseBlockMatrix(M,N)):void {
		Debug.assure(this.grid.equals(that.grid), "Data partitioning is not compatible");
		
		for (p in 0..(listBs.size-1)) {
			this.listBs(p).copyTo(that.listBs(p));
		}		
	}

	public def copyTo(that:Matrix(M,N)):void {
		if (that instanceof DenseBlockMatrix) {
			copyTo(that as DenseBlockMatrix);
		} else if (that instanceof DenseMatrix) {
			copyTo(that as DenseMatrix(M,N));
		} else {
			Debug.exit("CopyTo: target matrix is not compatible");
		}		
	}
	
	
	/**
	 * Copy data from dense matrix to myself.
	 *
	 * @param dm 	 the source dense matrix 
	 */
    public def copyFrom(dm:DenseMatrix(M,N)):void {
		var srcoff:Long=0;
		var srccol:Long = 0;
						   
		// Iterate all blocks columnwise
		for (var cb:Long=0; cb < grid.numColBlocks; cb++) {
			for (var col:Long=0; col<grid.colBs(cb); col++, srccol++)
				
				for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
					val dst = listBs(grid.getBlockId(rb, cb)).dense;
					val dstoff = col*dst.M;

					Rail.copy[Double](dm.d, srcoff, dst.d, dstoff, dst.M);
					srcoff += dst.M;
			}
		}
	}


	/**
	 * Convert to dense matrix.
	 *
	 * @return  dense matrix 
	 */
	public def toDense():DenseMatrix(M,N) {
		val dm = DenseMatrix.make(M,N);
		copyTo(dm);
		return dm;
	}


	// Data access methods 

	/**
	 *  Return the matrix block for the given index
	 *
	 * @param  i  index of matrix block
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
		return listBs(bid).dense(loc(2), loc(3));
	}

	/**
	 *  Set element value at the specified position.
	 *
	 * @param  r  the index of rows in the matrix
	 * @param  c  the index of columns in the matrix
	 */
	public  operator this(x:Long,y:Long)=(v:Double):Double {
		val loc = grid.find(x, y);
		val bid = grid.getBlockId(loc(0), loc(1));
		listBs(bid).dense(loc(2), loc(3))=v;
		return v;
	}

	/**
	 * Raise each cell in the matrix by the factor of a:Double.
	 *
	 * @param  a  	 the scaling factor
	 */
	public def scale(a:Double) {
		for (p in 0..(listBs.size-1)) {
			listBs(p).dense.scale(a);
		}
		return this;
	}

    /**
     * Perform cell-wise addition: this += x. Current implementation is
	 * not optimized.
	 *
	 * @param   x  the source matrix to be added with
     */
    public def cellAdd(x:Matrix(M,N))  {
		if (likeMe(x))
			return cellAdd(x as DenseBlockMatrix(M,N));

		for (var c:Long=0; c<N; c++) {
			for (var r:Long=0; r<M; r++) {
				this(r, c) = this(r, c) + x(r, c);
			}
		}
	    return this;
	}

	/**
	 * x = this * x
	 */
	protected def cellAddTo(x:DenseMatrix(M,N)) {
		var sttcol:Long=0;
		for (var cb:Long=0; cb<grid.numColBlocks; cb++) {
			// Iterate all blocks
			var dst_sttidx:Long = sttcol;
			for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
				//Iterate all blocks in one column
				val blkden = listBs(grid.getBlockId(rb, cb)).getMatrix();
				var src_sttidx:Long = 0;
				
				for (var c:Long=0; c<blkden.N; c++) {
					var srcidx:Long = src_sttidx;
					var dstidx:Long = dst_sttidx;
					for (; srcidx<src_sttidx+blkden.M; srcidx++, dstidx++) 
						x.d(dstidx) += blkden(srcidx);
					src_sttidx += blkden.M;
					dst_sttidx += x.M;
				}
				dst_sttidx += blkden.M;
			}
			sttcol += x.M * grid.colBs(cb);
		}
		return x;
	}

	/**
	 * this = this + x;
	 *
	 */
	public def cellAdd(x:DenseBlockMatrix(M,N)) {
		if (! likeMe(x)) 
			Debug.exit("Dense block matrix add fails - matrix type not match");
		
		//Debug.flushln("Here ");
		for (p in 0..(listBs.size-1)) {
			val dst = listBs(p).dense;
			val src = x.listBs(p).getMatrix() as DenseMatrix(dst.M, dst.N);
			dst.cellAdd(src);
		}		

		return this;
	}

	/**
	 * Cell-wise add this = this + double
	 */
	public def cellAdd(d:Double):DenseBlockMatrix(this) {
		for (p in 0..(listBs.size-1)) {
			val dst = listBs(p).dense;
			dst.cellAdd(d);
		}		
		return this;
	}
	

    /**
     * Cell-wise subtraction: this -= x
	 *
	 * @param  x  the subtracting matrix
     */
    public def cellSub(x:Matrix(M,N))  {
		if (likeMe(x))
			return cellSub(x as DenseBlockMatrix(M,N));

		for (var c:Long=0; c<N; c++) {
			for (var r:Long=0; r<M; r++) {
				this(r, c) = this(r, c) - x(r, c);
			}
		}
	    return this;
	}

	/**
	 * x = x - this 
	 */
	public def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var sttcol:Long=0;
		for (var cb:Long=0; cb<grid.numColBlocks; cb++) {
			// Iterate all blocks
			var dst_sttidx:Long = sttcol;
			for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
				//Iterate all blocks in one column
				val blkden = listBs(grid.getBlockId(rb, cb)).getMatrix();
				var src_sttidx:Long = 0;
				
				for (var c:Long=0; c<blkden.N; c++) {
					var srcidx:Long = src_sttidx;
					var dstidx:Long = dst_sttidx;
					for (; srcidx<src_sttidx+blkden.M; srcidx++, dstidx++) 
						x.d(dstidx) -= blkden(srcidx);
					src_sttidx += blkden.M;
					dst_sttidx += x.M;
				}
				dst_sttidx += blkden.M;
			}
			sttcol += x.M * grid.colBs(cb);
		}
		return x;
	}

	/**
	 * this = this - that;
	 *
	 */
	public def cellSub(that:DenseBlockMatrix(M,N)) {
		if (! likeMe(that)) 
			Debug.exit("Dense block matrix substract fails - matrices not match");
		
		for (p in 0..(listBs.size-1)) {
			val dst = listBs(p).dense;
			val src = that.listBs(p).dense as DenseMatrix(dst.M, dst.N);
			dst.cellSub(src);
		}		
		return this;
	}

	/**
	 * Cell-wise add this = double - this
	 */
	public def cellSubFrom(d:Double):DenseBlockMatrix(this) {
		for (p in 0..(listBs.size-1)) {
			val dst = listBs(p).dense;
			dst.cellSubFrom(d);
		}		
		return this;
	}



    /**
     * Cell-wise multiplication, return this = this &#42 x
	 *
	 * @param  x  the multiplying matrix
     */
    public def cellMult(x:Matrix(M,N))  {
		if (likeMe(x))
			return cellMult(x as DenseBlockMatrix(M,N));

		for (var c:Long=0; c<N; c++) {
			for (var r:Long=0; r<M; r++) {
				this(r, c) = this(r, c) * x(r, c);
			}
		}
	    return this;
	}

	/**
	 * x = x &#42 this 
	 */
	public def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var sttcol:Long=0;
		for (var cb:Long=0; cb<grid.numColBlocks; cb++) {
			// Iterate all blocks
			var dst_sttidx:Long = sttcol;
			for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
				//Iterate all blocks in one column
				val blkden = listBs(grid.getBlockId(rb, cb)).getMatrix();
				var src_sttidx:Long = 0;
				
				for (var c:Long=0; c<blkden.N; c++) {
					var srcidx:Long = src_sttidx;
					var dstidx:Long = dst_sttidx;
					for (; srcidx<src_sttidx+blkden.M; srcidx++, dstidx++) 
						x.d(dstidx) *= blkden(srcidx);
					src_sttidx += blkden.M;
					dst_sttidx += x.M;
				}
				dst_sttidx += blkden.M;
			}
			sttcol += x.M * grid.colBs(cb);
		}
		return x;
	}

	/**
	 * this = this &#42 x;
	 *
	 */
	public def cellMult(x:DenseBlockMatrix(M,N)) {
		if (! likeMe(x)) 
			Debug.exit("Dense block matrix cell mult fails - matrices not match");
		
		for (p in 0..(listBs.size-1)) {
			val dst = listBs(p).dense;
			val src = x.listBs(p).dense as DenseMatrix(dst.M, dst.N);
			dst.cellMult(src);
		}		
		return this;
	}


	// Cellwise division

    /**
     * Cell-wise division, return this = this / x
     */
	public  def cellDiv(x:Matrix(M,N)) {
		if (likeMe(x))
			return cellDiv(x as DenseBlockMatrix(M,N));

		for (var c:Long=0; c<N; c++) {
			for (var r:Long=0; r<M; r++) {
				this(r, c) = this(r, c) / x(r, c);
			}
		}	
	    return this;
	}

	/**
	 * x = this / x 
	 */
    public def cellDivBy(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var sttcol:Long=0L;
		for (var cb:Long=0; cb<grid.numColBlocks; cb++) {
			// Iterate all blocks
			var dst_sttidx:Long = sttcol;
			for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
				//Iterate all blocks in one column
				val blkden = listBs(grid.getBlockId(rb, cb)).getMatrix();
				var src_sttidx:Long = 0L;
				
				for (var c:Long=0; c<blkden.N; c++) {
					var srcidx:Long = src_sttidx;
					var dstidx:Long = dst_sttidx;
					for (; srcidx<src_sttidx+blkden.M; srcidx++, dstidx++) 
						x.d(dstidx) = blkden(srcidx)/x.d(dstidx);
					src_sttidx += blkden.M;
					dst_sttidx += x.M;
				}
				dst_sttidx += blkden.M;
			}
			sttcol += x.M * grid.colBs(cb);
		}
		return x;		
	}

	/**
	 * this /= x;
	 *
	 */
	public def cellDiv(x:DenseBlockMatrix(M,N)) {
		if (! likeMe(x)) 
			Debug.exit("Dense block matrix cell divide fails - matrices not match");
		
		for (p in 0..(listBs.size-1)) {
			val dst = listBs(p).dense;
			val src = x.listBs(p).dense as DenseMatrix(dst.M, dst.N);
			dst.cellDiv(src);
		}		

		return this;
	}


	// Matrix multiplication 


    /**
     * Block matrix multiplication has not been implementated yet. 
     */
	public def mult(
			A:Matrix(this.M), 
			B:Matrix(A.N,this.N), 
			plus:Boolean):DenseBlockMatrix(this){
		
		Debug.exit("Not implemented yet");
		return this;	
	}

	/** 
	 * Not implementated yet.
	 */
	public def transMult(
			A:Matrix{self.N==this.M}, 
			B:Matrix(A.M,this.N),
			plus:Boolean):DenseBlockMatrix(this) {
		
		Debug.exit("Not implemented yet");
		return this;		
    }
	
	/** 
	 * Not implementated yet. 
	 */
	public def multTrans(
			A:Matrix(this.M), 
			B:Matrix(this.N, A.N), 
			plus:Boolean):DenseBlockMatrix(this)	{
		
		Debug.exit("Not implemented yet");
		return this;		
    }

	// Operator overload

	public operator - this = clone().scale(-1.0) as DenseBlockMatrix(M,N);
	public operator (v:Double) + this = this.clone().cellAdd(v) as DenseBlockMatrix(M,N);
	public operator this + (v:Double) = this.clone().cellAdd(v) as DenseBlockMatrix(M,N);

	public operator this - (v:Double) = this.clone().cellAdd(-v) as DenseBlockMatrix(M,N);
	public operator (v:Double) - this = this.clone().cellSubFrom(v) as DenseBlockMatrix(M,N);
	
	public operator this / (v:Double) = this.clone().scale(1.0/v) as DenseBlockMatrix(M,N);
	//public operator (v:Double) / this = this.clone().cellDivBy(v) as DenseBlockMatrix(M,N);
	
	public operator this * (alpha:Double) = this.clone().scale(alpha) as DenseBlockMatrix(M,N);
	public operator (alpha:Double) * this = this * alpha;

	public operator this + (that:DenseBlockMatrix(M,N)) = this.clone().cellAdd(that) as DenseBlockMatrix(M,N);
	public operator this - (that:DenseBlockMatrix(M,N)) = this.clone().cellSub(that) as DenseBlockMatrix(M,N);
	public operator this * (that:DenseBlockMatrix(M,N)) = this.clone().cellMult(that) as DenseBlockMatrix(M,N);
	public operator this / (that:DenseBlockMatrix(M,N)) = this.clone().cellDiv(that) as DenseBlockMatrix(M,N);


	// Utils

	/**
	 * Transpose matrix
	 */
	public def T(dbm:DenseBlockMatrix(N,M)): void {

		Debug.assure(grid.numRowBlocks==dbm.grid.numColBlocks &&
					 grid.numColBlocks==dbm.grid.numRowBlocks);
		
		for (var c:Long=0; c<grid.numColBlocks; c++) {
			for (var r:Long=0; r<grid.numRowBlocks; r++) {
				val src = listBs(grid.getBlockId(r, c)).dense;
				val dst = dbm.listBs(dbm.grid.getBlockId(c, r)).dense 
					as DenseMatrix(src.N, src.M);

				src.T(dst);
			}
		}
	}

	public def getCalcTime():Long = this.listBs(here.id()).calcTime;
	public def getCommTime():Long = this.listBs(here.id()).commTime;
	
	/**
	 * Check matrix has the same type, partition and dimension or not.
	 *
	 * @param A 	 input matrix
	 */
	public def likeMe(A:Matrix):Boolean =
	    (A instanceof DenseBlockMatrix &&
	            (A as DenseBlockMatrix).grid.equals(this.grid));

	/**
	 * Check desne block matrix matrix has the same data or not.
	 *
	 * @param m 	input dense block matrix
	 * @return 		true or false
	 */		
	public def equals(m:DenseBlockMatrix(M,N)) =
		VerifyTool.testSame(this as Matrix(M,N), m as Matrix(M,N));

	/**
	 * Convert matrix data into string
	 */
	public def toString():String {
		val output = new StringBuilder();
		output.add("---------- Dense-block Matrix ["+M+"x"+N+"] ----------\n");
		for (p in 0..(listBs.size-1)) {
			output.add( "--- Dense block("+p+") ---\n"+listBs(p).toString());
		}
		output.add("----------------------------------------------------\n");
		return output.toString();
	}
}
