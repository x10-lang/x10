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

package x10.matrix.block;

import x10.io.Console;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;

public type BlockMatrix(M:Int)=BlockMatrix{self.M==M};
public type BlockMatrix(M:Int, N:Int)=BlockMatrix{self.M==M, self.N==N};
public type BlockMatrix(C:BlockMatrix)=BlockMatrix{self==C};


/**
 * Block matrix provides an abstraction of partitioned matrix in blocks,
 * which could be either dense or sparse blocks.
 */
public class BlockMatrix(grid:Grid) extends Matrix  {


	public val listBs:Array[MatrixBlock](1);


	//================================================================
	/**
	 * Construtblock matrix instance.
	 *
	 * @param  gp  Grid partition
	 * @param  ms  Matrix block array
	 */
	public def this(gp:Grid, ms:Array[MatrixBlock](1)) {
		super(gp.M, gp.N);
		property(gp);
		listBs = ms;
	}

	/**
	 * Construt block matrix instance.  Matrix blocks are not allocated
	 *
	 * @param  gp  Grid partition
	 * @param  ds  distribution of block with constraint of constant is true,
	 *             meaning mapping all points in array to one place.
	 */
	public def this(gp:Grid) {
		super(gp.M, gp.N);
		property(gp);
		listBs = new Array[MatrixBlock](gp.size);
	}


	//================================================================
	// This method must be combined with setBlock(...) to set each MatrixBlock
	/**
	 * Create an instance of block matrix.  Because the matrix block type is 
	 * unknow, there is no memory allocation.
	 *
	 * @param  gp  Grid partitioning of matrix
	 */
	public static def make(gp:Grid):BlockMatrix(gp.M, gp.N) = 
		new BlockMatrix(gp) as BlockMatrix(gp.M,gp.N);

	/**
	 * Create a block matrix with dense blocks using specified parititing.
	 *
	 * @param  gp  partitioning of matrix
	 */	
	public static def makeDense(gp:Grid): BlockMatrix(gp.M,gp.N) {
		val bm = new BlockMatrix(gp) as BlockMatrix(gp.M,gp.N);
		bm.allocDenseBlocks();
		return bm;
	}

	/**
	 * Create block matrix with sparse blocks using specified partitioning
	 * and nonxer density
	 *
	 * @param  gp  partitioning of matrix
	 * @param  nzd  nonzero density or sparsity for all sparse blocks
	 */
	public static def makeSparse(gp:Grid, nzd:Double): BlockMatrix(gp.M, gp.N) {
		val bm = new BlockMatrix(gp) as BlockMatrix(gp.M,gp.N);
		bm.allocSparseBlocks(nzd);
		return bm;
	}
	//================================================================

	/**
	 * Allocate memory space to hold (mxn) block matrix.
	 * No supported, since no partitioning information.
	 */
	public def alloc(m:Int, n:Int):BlockMatrix(m,n) {
		throw new UnsupportedOperationException();
	}


	public def allocDenseBlocks() : void {
		for([p] :Point in listBs) {
			val rid = this.grid.getRowBlockId(p);
			val cid = this.grid.getColBlockId(p);
			this.listBs(p) = DenseBlock.make(this.grid, rid, cid);
		}
	}

	public def allocSparseBlocks(nzd:Double):void {
		for([p] :Point in listBs) {
			val rid = this.grid.getRowBlockId(p);
			val cid = this.grid.getColBlockId(p);
			this.listBs(p) = SparseBlock.make(this.grid, rid, cid, nzd);
		}
	}

	/**
	 * Make a copy of myself
	 *
	 */
	public def clone():BlockMatrix(M,N) {
		val nbm = new BlockMatrix(this.grid) as BlockMatrix(M,N);
		for([p]  in listBs) {
			nbm.listBs(p) = this.listBs(p).clone();//getMatrix(p).clone();
		}
		return nbm;
	}

	//================================================================

	/**
	 * Initialize dense block matrix with a constant value
	 *
	 */
	public def init(ival:Double):void {
		for (val [p] :Point in listBs) {
			listBs(p).init(ival);
		}
	}

	/**
	 * Initialize dense block matrix with random values 
	 * 
	 */
	public def initRandom():void {
		for (val [p] :Point in listBs) {
			listBs(p).initRandom();
		}
	}

	/**
	 * Reset all entry to 0.0
	 *
	 */
	public def reset():void {
		for (val [p] :Point in listBs) {
			listBs(p).getMatrix().reset();
		}
	}

	//================================================================
	// Data access methods 
	//================================================================	

	/**
	 *  Return the matrix instance of matrix block at index of array
	 *
	 * @param  i  index of matrix block in array
	 */
	public def getMatrix(i:Int) <: Matrix = this.listBs(i).getMatrix();

	//---------
 
	//	public  def apply(var r:Int, var c:Int):Double {
	/**
	 *  Return element value in matrix. override the method in super class.
	 *
	 * @param  r  the r-th rows in the matrix
	 * @param  c  the c-th columns in the matrix
	 */
	public operator this(r:Int, c:Int):Double {
		val loc = grid.find(r, c);
		val bid = grid.getBlockId(loc(0), loc(1));
		return this.getMatrix(bid)(loc(2), loc(3));
	}

	/**
	 *  Return element value in matrix. override the method in super class.
	 *
	 * @param  r  the r-th rows in the matrix
	 * @param  c  the c-th columns in the matrix
	 */
	public  operator this(x:Int,y:Int)=(v:Double) {
		val loc = grid.find(x, y);
		val bid = grid.getBlockId(loc(0), loc(1));
		this.getMatrix(bid)(loc(2), loc(3))=v;
	}
	//---------------------------------------------------

	/**
	 * Convert to dense matrix in provided memory space
	 */
    public def copyTo(dm:DenseMatrix(M,N)):void {
		Debug.assure(this.M==dm.M&&this.N==dm.N);
		//This 
		var stt_blk:Int=0;
		for (var cb:Int=0; cb<grid.numColBlocks; cb++) {
			
			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
				val bid = grid.getBlockId(rb, cb);
				val src = this.getMatrix(bid);
				var stt_col:Int = stt_blk;

				for (var c:Int=0; c<src.N; c++) {
					var dstidx:Int = stt_col;
					for (var r:Int=0; r<src.M; r++, dstidx++)
						dm.d(dstidx)=src(r, c);
					stt_col += dm.M;
				}
				stt_blk += src.M;
			}
			stt_blk += grid.colBs(cb) * dm.M - dm.M;
		}
	}
	
	/**
	 *  Convert to dense matrix
	 */
	public def toDense():DenseMatrix(M,N) {
		val dm = DenseMatrix.make(M,N);
		copyTo(dm);
		return dm;
	}

 	//====================================================================
	// Cellwise operation
	//====================================================================

	/**
	 * Raise each cell in the matrix by the factor of a:Double.
	 *
	 * @param  a  -- the scaling factor
	 */
	public  def scale(a:Double) {
		for (val [b] : Point in listBs) {
			listBs(b).getMatrix().scale(a);
		}
		return this;
	}

	//-------------------------
	// Cellwise addition
	//-------------------------

    /**
     * Perform cell-wise addition: this += x. Current implementation is
	 * not optimized.
	 *
	 * @param   x  the source matrix to be added with
     */
    public def cellAdd(x:Matrix(M,N))  {
		if (likeMe(x))
			return cellAdd(x as BlockMatrix(M,N));

		for (var c:Int=0; c<N; c++) {
			for (var r:Int=0; r<M; r++) {
				this(r, c) = this(r, c) + x(r, c);
			}
		}
	    return this;
	}

	/**
	 * x = this * x
	 */
	protected def cellAddTo(x:DenseMatrix(M,N)) {
		Debug.exit("Not implemented");
		return x;
	}

	/**
	 * this = this + x;
	 *
	 */
	public def cellAdd(x:BlockMatrix(M,N)) {
		if (! likeMe(x)) 
			Debug.exit("Block matrix add fails - matrix type not match");
		
		//Debug.flushln("Here ");
		for (val [p] :Point in listBs) {
			val dst = listBs(p).getMatrix();
			val src = x.listBs(p).getMatrix();
			dst.cellAdd(src as Matrix(dst.M, dst.N));
		}		

		return this;
	}
	
	
	//-----------------------
    /**
     * Cell-wise subtraction: this -= x
	 *
	 * @param  x  the subtracting matrix
     */
    public def cellSub(x:Matrix(M,N))  {
		if (likeMe(x))
			return cellSub(x as BlockMatrix(M,N));

		for (var c:Int=0; c<N; c++) {
			for (var r:Int=0; r<M; r++) {
				this(r, c) = this(r, c) - x(r, c);
			}
		}
	    return this;
	}

	/**
	 * x = x - this 
	 */
	public def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x) {
		Debug.exit("Not implemented");
		return x;
	}

	/**
	 * this -= x;
	 *
	 */
	public def cellSub(x:BlockMatrix(M,N)) {
		if (! likeMe(x)) 
			Debug.exit("Block matrix substract fails - matrices not match");
		
		for (val [p] :Point in listBs) {
			val dst = listBs(p).getMatrix();
			val src = x.listBs(p).getMatrix();
			dst.cellSub(src as Matrix(dst.M, dst.N));
		}
		return this;
	}

	//--------------------------------------

    /**
     * Cell-wise multiplication, return this *= x
	 *
	 * @param  x  the multiplying matrix
     */
    public def cellMult(x:Matrix(M,N)):BlockMatrix(this)  {
		if (likeMe(x))
			return cellMult(x as BlockMatrix(M,N));

		for (var c:Int=0; c<N; c++) {
			for (var r:Int=0; r<M; r++) {
				this(r, c) = this(r, c) * x(r, c);
			}
		}
	    return this;
	}

	/**
	 * x = x * this 
	 */
	public def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		Debug.exit("Not implemented");
		return x;
	}

	/**
	 * this *= x;
	 *
	 */
	public def cellMult(x:DenseBlockMatrix(M,N)):BlockMatrix(this) {
		if (! likeMe(x)) 
			Debug.exit("Block matrix cell mult fails - matrices not match");
		
		for (val [p] :Point in listBs) {
			val dst = listBs(p).getMatrix();
			val src = x.listBs(p).getMatrix();
			dst.cellMult(src as Matrix(dst.M, dst.N));
		}		
		return this;
	}

	//---------------------------------------
    /**
     * Cell-wise division, return this = this / x
     */
	public  def cellDiv(x:Matrix(M,N)): BlockMatrix(this){
		if (likeMe(x))
			return cellDiv(x as BlockMatrix(M,N));

		for (var c:Int=0; c<N; c++) {
			for (var r:Int=0; r<M; r++) {
				this(r, c) = this(r, c) / x(r, c);
			}
		}	
	    return this;
	}

	/**
	 * x = this / x 
	 */
    public def cellDivBy(x:DenseMatrix(M,N)):DenseMatrix(x) {
		Debug.exit("Not implemented");
		return x;		
	}

	/**
	 * this /= x;
	 *
	 */
	public def cellDiv(x:BlockMatrix(M,N)): BlockMatrix(this) {
		if (! likeMe(x)) 
			Debug.exit("Block matrix cell divide fails - matrices not match");
		
		for (val [p] :Point in listBs) {
			val dst = listBs(p).getMatrix();
			val src = x.listBs(p).getMatrix();
			dst.cellDiv(src as Matrix(dst.M, dst.N));
		}		

		return this;
	}
	//====================================================================
	// Matrix multiplication 
	//====================================================================

    /**
     * Not implemented yet. 
	 * <p> This method is designed to perform block matrix multiplication, 
	 * returning this += A *&#42 B if plus is true, else
	 * this = A *&#42 B
     */
	public def mult(
			A:Matrix(this.M), 
			B:Matrix(A.N,this.N), 
			plus:Boolean):Matrix(this) {
		
		Debug.exit("Not implemented yet");
		return this;	
	}
					

	/** 
	 * Not implemented yet. 
	 * <p> This method is designed to perform 
	 * this += A<sup>T</sup> *&#42 B, when plus is true, 
	 * else this = A<sup>T</sup> *&#42 B
	 */
	public def transMult(
			A:Matrix{self.N==this.M}, 
			B:Matrix(A.M,this.N), 
			plus:Boolean):BlockMatrix(this) {
		Debug.exit("Not implemented yet");
		return this;		
    }
	/** 
	 * Not implemented. 
	 */
	public def multTrans(
			A:Matrix(this.M), 
			B:Matrix(this.N, A.N), 
			plus:Boolean):BlockMatrix(this)	{
		
		Debug.exit("Not implemented yet");
		return this;		
    }

	//====================================================================
	// Utils
	//====================================================================

	/**
	 * Check matrix has the same type, partition and dimension or not.
	 *
	 * @param A -- input matrix
	 */
	public def likeMe(A:Matrix):Boolean =
	    (A instanceof BlockMatrix &&
	            (A as BlockMatrix).grid.equals(this.grid));


	public def toString():String {
		var output:String="---------- Block Matrix ["+M+"x"+N+"] ----------\n";;
		for (val [p] :Point in this.listBs) {
			output+= "--- Block("+p+") ---\n"+this.listBs(p).toString();
		}
		output += "----------------------------------------------------\n";
		return output;
	}
	//
	public def printBlock() { printBlock("");}
	public def printBlock(msg:String) {
		Console.OUT.print(msg);
		Console.OUT.print(this.toString());
		Console.OUT.flush();
	}
	//
	public def debugPrintBlock() { debugPrintBlock(""); }
	public def debugPrintBlock(msg:String) {
		if (Debug.disable) return;
		val dbstr:String = msg + this.toString();
		Debug.println(dbstr);
	}

}
