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

package x10.matrix.block;

import x10.io.Console;
import x10.util.ArrayList;

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

	public val listBs:ArrayList[MatrixBlock];
	//----------------------------------------
	public var blockMap:Array[MatrixBlock](2);
	
	//================================================================
	/**
	 * Construtblock matrix instance.
	 *
	 * @param  gp  Grid partition
	 * @param  ms  Matrix block array
	 */
	public def this(gp:Grid, ms:ArrayList[MatrixBlock]) {
		super(gp.M, gp.N);
		property(gp);
		listBs = ms;
		blockMap=null;
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
		listBs = new ArrayList[MatrixBlock](gp.size);
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

	public static def makeDense(m:Int, n:Int, rowbs:Int, colbs:Int):BlockMatrix(m,n) =
		makeDense(new Grid(m,n,rowbs,colbs));

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

	public static def makeSparse(m:Int, n:Int, rowbs:Int, colbs:Int, nzd:Double):BlockMatrix(m,n) =
		makeSparse(new Grid(m,n,rowbs,colbs), nzd);

	/**
	 * 
	 */
	public static def makeDense(that:BlockMatrix): BlockMatrix(that.M, that.N) {
		val bm:BlockMatrix(that.M, that.N) = makeDense(that.grid) as BlockMatrix(that.M, that.N);
		that.copyTo(bm);
		return bm;
	}
	
	//================================================================

	/**
	 * Allocate memory space to hold (mxn) block matrix.
	 * No supported, since no partitioning information.
	 */
	public def alloc(m:Int, n:Int):BlockMatrix(m,n) {
		Debug.assure(m==M&&n==N);
		val nm = new BlockMatrix(this.grid) as BlockMatrix(m,n);
		for(var p :Int=0; p<nm.grid.size; p++) {
			val rid = this.grid.getRowBlockId(p);
			val cid = this.grid.getColBlockId(p);
			val mat = this.listBs.get(p).getMatrix();
			if (mat instanceof DenseMatrix)
				nm.listBs(p) = new DenseBlock(rid, cid, mat as DenseMatrix);
			else if (mat instanceof SparseCSC)
				nm.listBs(p) = new SparseBlock(rid, cid, mat as SparseCSC);
			else
				Debug.exit("Matrix type is not supported in creating matrix block");
		}
		return nm;
	}


	public def allocDenseBlocks() : BlockMatrix(this) {
		for(var p :Int=0; p<grid.size; p++) {
			val rid = this.grid.getRowBlockId(p);
			val cid = this.grid.getColBlockId(p);
			this.listBs(p) = DenseBlock.make(this.grid, rid, cid);
		}
		return this;
	}

	public def allocSparseBlocks(nzd:Double): BlockMatrix(this) {
		for(var p :Int=0; p<grid.size; p++) {
			val rid = this.grid.getRowBlockId(p);
			val cid = this.grid.getColBlockId(p);
			this.listBs(p) = SparseBlock.make(this.grid, rid, cid, nzd);
		}
		return this;
	}

	/**
	 * Make a copy of myself, while sharing the same matrix partitioning instance.
	 *
	 */
	public def clone():BlockMatrix(M,N) {
		val nbm = new BlockMatrix(this.grid) as BlockMatrix(M,N);
		for(var p :Int=0; p<grid.size; p++) {
			nbm.listBs(p) = this.listBs(p).clone();//getMatrix(p).clone();
		}
		return nbm;
	}

	//================================================================

	/**
	 * Initialize dense block matrix with a constant value
	 *
	 */
	public def init(ival:Double):BlockMatrix(this) {
		for (var p :Int=0; p<grid.size; p++) {
			listBs(p).init(ival);
		}
		return this;
	}

	/**
	 * Given initial function which maps matrix (row, column) index to
	 * a double value.
	 */
	public def init(f:(Int, Int)=>Double):BlockMatrix(this) {
		var roff:Int=0;
		var coff:Int=0;
		for (var cb:Int=0; cb<grid.numColBlocks; coff+=grid.colBs(cb), roff=0, cb++)
			for (var rb:Int=0; rb<grid.numRowBlocks; roff+=grid.rowBs(rb), rb++ ) {
				listBs(grid.getBlockId(rb, cb)).init(roff, coff, f);
			}		
		return this;
	}
	

	/**
	 * Initialize dense block matrix with random values 
	 * 
	 */
	public def initRandom():BlockMatrix(this) {
		for (var p :Int=0; p<grid.size; p++) {
			listBs(p).initRandom();
		}
		return this;
	}
	
	public def initRandom(lo:Int, up:Int):BlockMatrix(this) {
		for (var p :Int=0; p<grid.size; p++) {
			listBs(p).initRandom(lo, up);
		}
		return this;
	}
	/**
	 * Reset all entry to 0.0
	 *
	 */
	public def reset():void {
		for (var p :Int=0; p<grid.size; p++) {
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
	
	public def getBlock(i:Int) = this.listBs(i);
	//-------------------------------------------
		
	public def findBlock(rid:Int, cid:Int):MatrixBlock {
		val bid = grid.getBlockId(rid, cid);
		val blk = listBs(bid);
		if (blk.myRowId==rid && blk.myColId==cid) return blk;
		Debug.flushln("Block Id is not mapped to the same index in the listBs, try to search listBs");
		return searchBlock(rid, cid);
	}
	
	public def searchBlock(rid:Int, cid:Int):MatrixBlock {
		val it = listBs.iterator();
		while (it.hasNext()) {
			val blk = it.next();
			if (blk.myRowId == rid &&
					blk.myColId == cid ) return blk;
		}
		return null;
	}
	//-------------------------------------------
	/**
	 * For fast asscess block without searching block every time.
	 * Restriction is block row id and column id must be the same as the block map
	 * corrodinate indexes. 
	 * This process is not a must, since block matrix has block id mapped to the
	 * same index ID in listBs.
	 */
	public def buildBlockMap(): void{
		if (blockMap != null) return;
		val rowmax = grid.numRowBlocks -1;
		val colmax = grid.numColBlocks -1;
		blockMap = new Array[MatrixBlock]((0..rowmax)*(0..colmax), (p:Point(2))=>findBlock(p(0),p(1)));
	}
	
	//-------------------------------------------

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
	public  operator this(x:Int,y:Int)=(v:Double):Double {
		val loc = grid.find(x, y);
		val bid = grid.getBlockId(loc(0), loc(1));
		this.getMatrix(bid)(loc(2), loc(3))=v;
		return v;
	}
	//---------------------------------------------------
	// Copy
	//---------------------------------------------------

	/**
	 * Convert to dense matrix in provided memory space
	 */
    public def copyTo(dm:DenseMatrix(M,N)):void {
   	
		var rowoff:Int=0;
		var coloff:Int=0;
		for (var cb:Int=0; cb<grid.numColBlocks; coloff+=grid.colBs(cb), rowoff=0, cb++) {
			for (var rb:Int=0; rb<grid.numRowBlocks; rowoff+=grid.rowBs(rb), rb++) {
				
				val bid = grid.getBlockId(rb, cb);
				val src = this.getMatrix(bid);

				if (src instanceof DenseMatrix) {
					val densrc = src as DenseMatrix;
					DenseMatrix.copySubset(src as DenseMatrix, 0, 0, dm, rowoff, coloff, src.M, src.N);
				} else if (src instanceof SparseCSC) {
					SparseCSC.copyTo(src as SparseCSC, dm, rowoff, coloff); 
				} else {
					Debug.exit("CopyTo: target matrix type not supported");
				}
					
			}
		}
	}
	
    public def copyTo(that:BlockMatrix(M,N)): void {
    	Debug.assure(this.grid.equals(that.grid), "Data partitioning is not compatible");
    	
    	for (var p :Int=0; p<grid.size; p++) {
    		this.listBs(p).copyTo(that.listBs(p));
    	}
    }
    
    /**
     * Copy data from blocks to a sparse CSC matrix
     * 
     * @param dst 	target sparse matrix
     */
    public def copyTo(dst:SparseCSC(M,N)): void {

    	val sz:Long = getStorageSize();
    	Debug.assure(sz <= Int.MAX_VALUE,
    	"Copy block matrix fail! Exceeding the limit of array");
    	//Check storage size
    	dst.testIncStorage(0, sz as Int);
    	
    	var dstcol:Int=0;
    	var cnt:Int=0;
    	for (var cb:Int=0; cb < grid.numColBlocks; cb++) {
    		for (var col:Int=0; col < grid.colBs(cb); col++, dstcol++) {
    			val dstln = dst.getCol(dstcol);
    			var sttidx:Int = 0;

    			dstln.offset = cnt;
    			for (var rb:Int=0; rb<grid.numRowBlocks; rb++) {
    				val bid = grid.getBlockId(rb, cb);
    				val blk = listBs(bid) as SparseBlock;
    				val src = blk.sparse;
    				val srcln = src.getCol(col);
    				
    				srcln.appendTo(dstln, sttidx);

    				sttidx += src.M;
    				cnt   += srcln.length;
    			}
    		}
    	}
    	//dst.print("copy to result:");
    }
    
    public def copyTo(that:Matrix(M,N)): void {
    	if (likeMe(that)) {
    		copyTo(that as BlockMatrix(M,N));
    	} else if (that instanceof DenseMatrix) {
    		copyTo(that as DenseMatrix(M,N));
    	} else {
    		Debug.exit("CopyTo: target matrix is not compatible");
    	}
    }
    
    //---------------------------
    public def copyFrom(src:DenseMatrix(M,N)):void {
    	
    	var rowoff:Int=0;
    	var coloff:Int=0;
    	for (var cb:Int=0; cb<grid.numColBlocks; coloff+=grid.colBs(cb), rowoff=0, cb++) {
    		for (var rb:Int=0; rb<grid.numRowBlocks; rowoff+=grid.rowBs(rb), rb++) {
    			
    			val bid = grid.getBlockId(rb, cb);
    			val dst = this.getMatrix(bid) as DenseMatrix;

    			DenseMatrix.copySubset(src, rowoff, coloff, dst, 0, 0, dst.M, dst.N); 			
    		}
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
		for (var b:Int=0; b<grid.size; b++) {
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
	protected def cellAddTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		throw new UnsupportedOperationException("Not implemented");
	}

	/**
	 * this = this + x;
	 *
	 */
	public def cellAdd(that:BlockMatrix(M,N)) {
		Debug.assure(likeMe(that), "Block matrix add fails - matrix partitioning incompatible");
		
		//Debug.flushln("Here ");
		for (var p :Int=0; p<grid.size; p++) {
			val dst = this.listBs(p).getMatrix();
			val src = that.listBs(p).getMatrix();
			dst.cellAdd(src as Matrix(dst.M, dst.N));
		}		

		return this;
	}
	
	public def cellAdd(d:Double):BlockMatrix(this) {
		//Debug.flushln("Here ");
		for (var p :Int=0; p<grid.size; p++) {
			val dst = listBs(p).getMatrix();
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
			return cellSub(x as BlockMatrix(M,N));
		// This is slow
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
		throw new UnsupportedOperationException("Not implemented");
	}

	/**
	 * this = this -v;
	 */
	public def cellSub(v:Double) = this.cellAdd(-v);


	/**
	 * this = this - that;
	 * 
	 */
	public def cellSub(that:BlockMatrix(M,N)) {
		Debug.assure(likeMe(that), 
				"Block matrix substract fails - matrix partition not compatible");
		
		for (var p :Int=0; p<grid.size; p++) {
			val dst = this.listBs(p).getMatrix();
			val obj = that.listBs(p).getMatrix();
			dst.cellSub(obj as Matrix(dst.M, dst.N));
		}
		return this;
	}
	/**
	 * this = v - this
	 */
	public def cellSubFrom(dv:Double) : BlockMatrix(this) {
		for (var p :Int=0; p<grid.size; p++) {
			val dst = this.getMatrix(p);
			dst.cellSubFrom(dv);
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
	public def cellMult(that:BlockMatrix(M,N)):BlockMatrix(this) {
		Debug.assure(likeMe(that), 
				"Block matrix cell mult fails - matrices partition not match");
		
		for (var p :Int=0; p<grid.size; p++) {
			val dst = this.listBs(p).getMatrix();
			val src = that.listBs(p).getMatrix();
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
    
    public def cellDivBy(v:Double):BlockMatrix(this) {
    	Debug.exit("No implementation");
    	return this;
    }
    
	/**
	 * this /= x;
	 *
	 */
	public def cellDiv(that:BlockMatrix(M,N)): BlockMatrix(this) {
		Debug.assure(likeMe(that), 
				"Block matrix cell divide fails - matrices partition not match");
		
		for (var p :Int=0; p<grid.size; p++) {
			val dst = this.listBs(p).getMatrix();
			val src = that.listBs(p).getMatrix();
			dst.cellDiv(src as Matrix(dst.M, dst.N));
		}		

		return this;
	}
	
	/**
	 * this = this /v
	 */
	public def cellDiv(v:Double): BlockMatrix(this) {
		
		for (var p :Int=0; p<grid.size; p++) {
			val dst = this.listBs(p).getMatrix();
			dst.scale(1.0/v);
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
	public def mult(A:Matrix(this.M), B:Matrix(A.N,this.N),	plus:Boolean):Matrix(this) {
		if (A instanceof BlockMatrix && B instanceof BlockMatrix )
			return mult(A as BlockMatrix, B as BlockMatrix, plus);
		Debug.exit("Not implemented yet");
		return this;	
	}
					

	/** 
	 * Not implemented yet. 
	 * <p> This method is designed to perform 
	 * this += A<sup>T</sup> *&#42 B, when plus is true, 
	 * else this = A<sup>T</sup> *&#42 B
	 */
	public def transMult(A:Matrix{self.N==this.M}, B:Matrix(A.M,this.N), plus:Boolean):BlockMatrix(this) {
		if (A instanceof BlockMatrix && B instanceof BlockMatrix )
			return transMult(A as BlockMatrix, B as BlockMatrix, plus);
		Debug.exit("Not implemented yet");
		return this;		
    }
	/** 
	 * Not implemented. 
	 */
	public def multTrans(A:Matrix(this.M), B:Matrix(this.N, A.N), plus:Boolean):BlockMatrix(this) {
		if (A instanceof BlockMatrix && B instanceof BlockMatrix )
			return multTrans(A as BlockMatrix, B as BlockMatrix, plus);

		Debug.exit("Not implemented yet");
		return this;		
    }
	//---------
	public def mult(A:Matrix(this.M), B:Matrix(A.N,this.N)) = mult(A, B, false);
	public def transMult(A:Matrix{self.N==this.M}, B:Matrix(A.M,this.N)) = transMult(A, B, false);
	public def multTrans(A:Matrix(this.M), B:Matrix(this.N, A.N)) = multTrans(A, B, false);
		
	//====================================================================
	public def mult(A:BlockMatrix(this.M), B:BlockMatrix(A.N,this.N), plus:Boolean):BlockMatrix(this) {
		BlockBlockMult.mult(A, B, this, plus);
		return this;	
	}
	
	public def transMult(A:BlockMatrix{self.N==this.M}, B:BlockMatrix(A.M,this.N), plus:Boolean):BlockMatrix(this) {
		BlockBlockMult.transMult(A, B, this, plus);
		return this;	
	}
	
	public def multTrans(A:BlockMatrix(this.M), B:BlockMatrix(this.N,A.N), plus:Boolean):BlockMatrix(this) {
		BlockBlockMult.multTrans(A, B, this, plus);
		return this;
	}
	//----------------------------
	public def mult(A:BlockMatrix(this.M), B:BlockMatrix(A.N,this.N)) = mult(A, B, false);
	public def transMult(A:BlockMatrix{self.N==this.M}, B:BlockMatrix(A.M,this.N)) = transMult(A, B, false);
	public def multTrans(A:BlockMatrix(this.M), B:BlockMatrix(this.N,A.N)) = multTrans(A, B, false);
	
	//====================================================================
	//Operator overload
	//====================================================================
	public operator - this = this.clone().scale(-1.0) as BlockMatrix(M,N);
	/**
	 * Operation result is stored in block matrix using dense block as storage.
	 */
	public operator (v:Double) + this = makeDense(this).cellAdd(v) as BlockMatrix(M,N);
	public operator this + (v:Double) = makeDense(this).cellAdd(v) as BlockMatrix(M,N);
	public operator this - (v:Double) = makeDense(this).cellSub(v) as BlockMatrix(M,N);
	
	public operator (v:Double) - this = makeDense(this).cellSubFrom(v) as BlockMatrix(M,N);
	public operator this / (v:Double) = makeDense(this).cellDiv(v) as BlockMatrix(M,N);
	public operator (v:Double) / this = makeDense(this).cellDivBy(v) as BlockMatrix(M,N);
	
	/**
	 * Operator overloading for cell-wise scaling operation and return result in a new dense matrix. 
	 */
	public operator this * (alpha:Double) = this.clone().scale(alpha) as BlockMatrix(M,N);
	public operator this * (alpha:Int)    = this.clone().scale(alpha as Double) as BlockMatrix(M,N);
	public operator (alpha:Double) * this = this * alpha;
	public operator (alpha:Int) * this    = this * alpha;;

	
	public operator this + (that:BlockMatrix(M,N)) = makeDense(this).cellAdd(that) as BlockMatrix(M,N);
	public operator this - (that:DenseMatrix(M,N)) = makeDense(this).cellSub(that) as BlockMatrix(M,N);
	public operator this * (that:DenseMatrix(M,N)) = makeDense(this).cellMult(that) as BlockMatrix(M,N);
	public operator this / (that:DenseMatrix(M,N)) = makeDense(this).cellDiv(that) as BlockMatrix(M,N);

	//====================================================================
	// Utils
	//====================================================================
	public def getStorageSize():Long {
		var nzcnt:Long=0;
		for (var p :Int=0; p<grid.size; p++) {
			nzcnt += listBs(p).getStorageSize();
		}
		return nzcnt;	
	}
	
	
	/**
	 * Check matrix has the same type, partition and dimension or not.
	 *
	 * @param A -- input matrix
	 */
	public def likeMe(A:Matrix):Boolean =
	    (A instanceof BlockMatrix &&
	            (A as BlockMatrix).grid.equals(this.grid));

	public def likeMe(A:BlockMatrix):Boolean =
		((A.grid==this.grid || A.grid.equals(this.grid)));


	public def toString():String {
		var output:String="---------- Block Matrix ["+M+"x"+N+"] ----------\n";;
		for (var p :Int=0; p<grid.size; p++) {
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

	public def printBlockMap() {
		var outstr:String="";
		
		if (blockMap==null) buildBlockMap();
		for (var r:Int=blockMap.region.min(0); r<=blockMap.region.max(0); r++) {
			for (var c:Int=blockMap.region.min(1); c<=blockMap.region.max(1); c++) {
				val b = blockMap(r, c);
				outstr +=("Block("+r+","+c+"):["+b.myRowId+","+b.myColId+"] ");
			}
			outstr += "\n";
		}
		Console.OUT.println(outstr);
		Console.OUT.flush();
	}
	
}
