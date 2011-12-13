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
import x10.matrix.VerifyTools;

public type DenseBlockMatrix(M:Int)=DenseBlockMatrix{self.M==M};
public type DenseBlockMatrix(M:Int, N:Int)=DenseBlockMatrix{self.M==M, self.N==N};
public type DenseBlockMatrix(C:DenseBlockMatrix)=DenseBlockMatrix{self==C};


/**
 * Dense block matrix is constructed by using an array or dense blocks, and
 * grid partition specifies how each block is mapped to the overall dense matrix.
 * 
 */
public class DenseBlockMatrix(grid:Grid) extends Matrix  {

	public val listBs:Array[DenseBlock](1);
   
	//================================================================
	/**
	 * Construt dense-block matrix instance.
	 *
	 * @param  gp     Grid partition
	 * @param  blkMs  Matrix block array
	 */
	public def this(gp:Grid, blkMs:Array[DenseBlock](1)) {
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
		listBs = new Array[DenseBlock](gp.numRowBlocks* gp.numColBlocks);
	}

	//================================================================

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

	//================================================================

	/**
	 * Allocate all dense blocks in the grid partitioning
	 *
	 */
	public def alloc():void {
		//finish ateach([p] :Point in this.dist) {
		for(var c:Int=0; c<grid.numColBlocks; c++) {
			for (var r:Int=0; r<grid.numRowBlocks; r++) {
				val p:Int = grid.getBlockId(r, c);
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
	public def alloc(m:Int, n:Int):DenseBlockMatrix(m,n) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Make a copy of myself in all places
	 *
	 */
	public def clone():DenseBlockMatrix(M,N) {
		val nbm = new DenseBlockMatrix(this.grid);
		for(val [p] :Point in listBs) {
			nbm.listBs(p) = this.listBs(p).clone();//getMatrix(p).clone();
		}
		return nbm as DenseBlockMatrix(M,N);
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
	// Data copy 
	//================================================================	
	/**
	 * Copy element values to the target matrix of the same dimension.
	 *
	 * @param dst  -- the target dense matrix.		
	 */
	public def copyTo(dst:DenseMatrix(M,N)):void {
		var dstcolidx:Int=0;
		// Iterate all blocks columnwise
		for (var cb:Int=0; cb < grid.numColBlocks; cb++) {
			var dstblkidx:Int = dstcolidx;

			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {

				val src = listBs(grid.getBlockId(rb, cb)).getMatrix();
				var srcoff:Int = 0;
				var dstoff:Int = dstblkidx;
				for (var col:Int=0; col<src.N; col++, srcoff+=src.M, dstoff+=dst.M)
					Array.copy[Double](src.d, srcoff, dst.d, dstoff, src.M);
				dstblkidx += src.M;
			}

			dstcolidx += grid.colBs(cb) * this.M;
		}
	}
	
	/**
	 * Copy data from dense matrix to myself.
	 *
	 * @param dm 	 the source dense matrix 
	 */
    public def copyFrom(dm:DenseMatrix(M,N)):void {
		var srcoff:Int=0;
		var srccol:Int = 0;
						   
		// Iterate all blocks columnwise
		for (var cb:Int=0; cb < grid.numColBlocks; cb++) {
			for (var col:Int=0; col<grid.colBs(cb); col++, srccol++)
				
				for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
					val dst = listBs(grid.getBlockId(rb, cb)).dense;
					var dstoff:Int = col*dst.M;

					Array.copy[Double](dm.d, srcoff, dst.d, dstoff, dst.M);
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
 
	/**
	 *  Return element value in matrix. override the method in super class.
	 *
	 * @param  r  the r-th rows in the matrix
	 * @param  c  the c-th columns in the matrix
	 */
	public operator this(r:Int, c:Int):Double {
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
	public  operator this(x:Int,y:Int)=(v:Double):Double {
		val loc = grid.find(x, y);
		val bid = grid.getBlockId(loc(0), loc(1));
		listBs(bid).dense(loc(2), loc(3))=v;
		return v;
	}

 	//====================================================================
	// Cellwise operation
	//====================================================================

	/**
	 * Raise each cell in the matrix by the factor of a:Double.
	 *
	 * @param  a  	 the scaling factor
	 */
	public  def scale(a:Double) {
		for (val [b] : Point in listBs) {
			listBs(b).dense.scale(a);
		}
		return this;
	}

	public def scale(a:Int) = scale(a as Double);

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
			return cellAdd(x as DenseBlockMatrix(M,N));

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
		var sttcol:Int=0;
		for (var cb:Int=0; cb<grid.numColBlocks; cb++) {
			// Iterate all blocks
			var dst_sttidx:Int = sttcol;
			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
				//Iterate all blocks in one column
				val blkden = listBs(grid.getBlockId(rb, cb)).getMatrix();
				var src_sttidx:Int = 0;
				
				for (var c:Int=0; c<blkden.N; c++) {
					var srcidx:Int = src_sttidx;
					var dstidx:Int = dst_sttidx;
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
		for (val [p] :Point in listBs) {
			val dst = listBs(p).dense;
			val src = x.listBs(p).getMatrix() as DenseMatrix(dst.M, dst.N);
			dst.cellAdd(src);
		}		

		return this;
	}

	public def cellAdd(d:Double):DenseBlockMatrix(this) {
		for (val [p] :Point in listBs) {
			val dst = listBs(p).dense;
			dst.cellAdd(d);
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
			return cellSub(x as DenseBlockMatrix(M,N));

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
		var sttcol:Int=0;
		for (var cb:Int=0; cb<grid.numColBlocks; cb++) {
			// Iterate all blocks
			var dst_sttidx:Int = sttcol;
			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
				//Iterate all blocks in one column
				val blkden = listBs(grid.getBlockId(rb, cb)).getMatrix();
				var src_sttidx:Int = 0;
				
				for (var c:Int=0; c<blkden.N; c++) {
					var srcidx:Int = src_sttidx;
					var dstidx:Int = dst_sttidx;
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
	 * this -= x;
	 *
	 */
	public def cellSub(x:DenseBlockMatrix(M,N)) {
		if (! likeMe(x)) 
			Debug.exit("Dense block matrix substract fails - matrices not match");
		
		for (val [p] :Point in listBs) {
			val dst = listBs(p).dense;
			val src = x.listBs(p).dense as DenseMatrix(dst.M, dst.N);
			dst.cellSub(src);
		}		
		return this;
	}

	//---------------

    /**
     * Cell-wise multiplication, return this = this &#42 x
	 *
	 * @param  x  the multiplying matrix
     */
    public def cellMult(x:Matrix(M,N))  {
		if (likeMe(x))
			return cellMult(x as DenseBlockMatrix(M,N));

		for (var c:Int=0; c<N; c++) {
			for (var r:Int=0; r<M; r++) {
				this(r, c) = this(r, c) * x(r, c);
			}
		}
	    return this;
	}

	/**
	 * x = x &#42 this 
	 */
	public def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		var sttcol:Int=0;
		for (var cb:Int=0; cb<grid.numColBlocks; cb++) {
			// Iterate all blocks
			var dst_sttidx:Int = sttcol;
			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
				//Iterate all blocks in one column
				val blkden = listBs(grid.getBlockId(rb, cb)).getMatrix();
				var src_sttidx:Int = 0;
				
				for (var c:Int=0; c<blkden.N; c++) {
					var srcidx:Int = src_sttidx;
					var dstidx:Int = dst_sttidx;
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
		
		for (val [p] :Point in listBs) {
			val dst = listBs(p).dense;
			val src = x.listBs(p).dense as DenseMatrix(dst.M, dst.N);
			dst.cellMult(src);
		}		
		return this;
	}

	//---------------------------------------
	// Cellwise division
	//---------------------------------------
    /**
     * Cell-wise division, return this = this / x
     */
	public  def cellDiv(x:Matrix(M,N)) {
		if (likeMe(x))
			return cellDiv(x as DenseBlockMatrix(M,N));

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
		var sttcol:Int=0;
		for (var cb:Int=0; cb<grid.numColBlocks; cb++) {
			// Iterate all blocks
			var dst_sttidx:Int = sttcol;
			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
				//Iterate all blocks in one column
				val blkden = listBs(grid.getBlockId(rb, cb)).getMatrix();
				var src_sttidx:Int = 0;
				
				for (var c:Int=0; c<blkden.N; c++) {
					var srcidx:Int = src_sttidx;
					var dstidx:Int = dst_sttidx;
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
		
		for (val [p] :Point in listBs) {
			val dst = listBs(p).dense;
			val src = x.listBs(p).dense as DenseMatrix(dst.M, dst.N);
			dst.cellDiv(src);
		}		

		return this;
	}

	//====================================================================
	// Matrix multiplication 
	//====================================================================

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

	//====================================================================
	// Utils
	//====================================================================

	/**
	 * Transpose matrix
	 */
	public def T(dbm:DenseBlockMatrix(N,M)): void {

		Debug.assure(grid.numRowBlocks==dbm.grid.numColBlocks &&
					 grid.numColBlocks==dbm.grid.numRowBlocks);
		
		for (var c:Int=0; c<grid.numColBlocks; c++) {
			for (var r:Int=0; r<grid.numRowBlocks; r++) {
				val src = listBs(grid.getBlockId(r, c)).dense;
				val dst = dbm.listBs(dbm.grid.getBlockId(c, r)).dense 
					as DenseMatrix(src.N, src.M);

				src.T(dst);
			}
		}
	}

	//---------------------------------------------------

	//================================================================
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
		VerifyTools.testSame(this as Matrix(M,N), m as Matrix(M,N));

	/**
	 * Convert matrix data into string
	 */
	public def toString():String {
		var output:String="---------- Dense-block Matrix ["+M+"x"+N+"] ----------\n";;
		for (val [p] :Point in listBs) {
			output+= "--- Dense block("+p+") ---\n"+listBs(p).toString();
		}
		output += "----------------------------------------------------\n";
		return output;
	}
	
	//-------------------------
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
