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

import x10.regionarray.Array;
import x10.regionarray.Region;
import x10.util.ArrayList;
import x10.util.StringBuilder;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;

public type BlockMatrix(M:Long)=BlockMatrix{self.M==M};
public type BlockMatrix(M:Long, N:Long)=BlockMatrix{self.M==M, self.N==N};
public type BlockMatrix(C:BlockMatrix)=BlockMatrix{self==C};

/**
 * Block matrix provides an abstraction of partitioned matrix in blocks,
 * which could be either dense or sparse blocks.
 */
public class BlockMatrix(grid:Grid) extends Matrix  {
    public val listBs:ArrayList[MatrixBlock];
    public var blockMap:Array[MatrixBlock](2);

    /**
     * Construct block matrix instance.
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
     * Construct block matrix instance.  Matrix blocks are not allocated
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

    /**
     * Create an instance of block matrix.  Because the matrix block type is 
     * unknown, there is no memory allocation.
     * This method must be combined with setBlock(...) to set each MatrixBlock
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

    public static def makeDense(m:Long, n:Long, rowbs:Long, colbs:Long):BlockMatrix(m,n) =
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

    public static def makeSparse(m:Long, n:Long, rowbs:Long, colbs:Long, nzd:Double):BlockMatrix(m,n) =
        makeSparse(new Grid(m,n,rowbs,colbs), nzd);

    public static def makeDense(that:BlockMatrix): BlockMatrix(that.M, that.N) {
        val bm:BlockMatrix(that.M, that.N) = makeDense(that.grid) as BlockMatrix(that.M, that.N);
        that.copyTo(bm);
        return bm;
    }

    /**
     * Allocate memory space to hold (mxn) block matrix.
     * No supported, since no partitioning information.
     */
    public def alloc(m:Long, n:Long):BlockMatrix(m,n) {
        Debug.assure(m==M&&n==N);
        val nm = new BlockMatrix(this.grid) as BlockMatrix(m,n);
        for(var p:Long=0; p<nm.grid.size; p++) {
            val rid = this.grid.getRowBlockId(p);
            val cid = this.grid.getColBlockId(p);
            val roff= this.grid.startRow(rid);
            val coff= this.grid.startCol(cid);
            val mat = this.listBs.get(p).getMatrix();
            if (mat instanceof DenseMatrix)
                nm.listBs(p) = new DenseBlock(rid, cid, roff, coff, mat as DenseMatrix);
            else if (mat instanceof SparseCSC)
                nm.listBs(p) = new SparseBlock(rid, cid, roff, coff, mat as SparseCSC);
            else
                Debug.exit("Matrix type is not supported in creating matrix block");
        }
        return nm;
    }

    public def allocDenseBlocks() : BlockMatrix(this) {
        for(var p:Long=0; p<grid.size; p++) {
            val rid = this.grid.getRowBlockId(p);
            val cid = this.grid.getColBlockId(p);
            this.listBs(p) = DenseBlock.make(this.grid, rid, cid);
        }
        return this;
    }

    public def allocSparseBlocks(nzd:Double): BlockMatrix(this) {
        for(var p:Long=0; p<grid.size; p++) {
            val rid = this.grid.getRowBlockId(p);
            val cid = this.grid.getColBlockId(p);
            this.listBs(p) = SparseBlock.make(this.grid, rid, cid, nzd);
        }
        return this;
    }

    /**
     * Make a copy of myself, while sharing the same matrix partitioning instance.
     */
    public def clone():BlockMatrix(M,N) {
        val nbm = new BlockMatrix(this.grid) as BlockMatrix(M,N);
        for(var p:Long=0; p<grid.size; p++) {
            nbm.listBs(p) = this.listBs(p).clone();//getMatrix(p).clone();
        }
        return nbm;
    }

    /**
     * Initialize dense block matrix with a constant value
     */
    public def init(ival:Double):BlockMatrix(this) {
        for (var p:Long=0; p<grid.size; p++) {
            listBs(p).init(ival);
        }
        return this;
    }

    /**
     * Given initial function which maps matrix (row, column) index to
     * a double value.
     */
    public def init(f:(Long, Long)=>Double):BlockMatrix(this) {
        for (var cb:Long=0; cb<grid.numColBlocks; cb++)
            for (var rb:Long=0; rb<grid.numRowBlocks; rb++ ) {
                listBs(grid.getBlockId(rb, cb)).init(f);
            }        
        return this;
    }

    /**
     * Initialize dense block matrix with random values 
     */
    public def initRandom():BlockMatrix(this) {
        for (var p:Long=0; p<grid.size; p++) {
            listBs(p).initRandom();
        }
        return this;
    }
    
    public def initRandom(lo:Long, up:Long):BlockMatrix(this) {
        for (var p:Long=0; p<grid.size; p++) {
            listBs(p).initRandom(lo, up);
        }
        return this;
    }

    /**
     * Reset all entry to 0.0
     */
    public def reset():void {
        for (var p:Long=0; p<grid.size; p++) {
            listBs(p).getMatrix().reset();
        }
    }

    /**
     *  Return the matrix instance of matrix block at index of array
     *
     * @param  i  index of matrix block in array
     */
    public def getMatrix(i:Long) <: Matrix = this.listBs(i).getMatrix();
    
    public def getBlock(i:Long) = this.listBs(i);

    public def findBlock(rid:Long, cid:Long):MatrixBlock {
        val bid = grid.getBlockId(rid, cid);
        val blk = listBs(bid);
        if (blk.myRowId==rid && blk.myColId==cid) return blk;
        Debug.flushln("Block Id is not mapped to the same index in the listBs, try to search listBs");
        return searchBlock(rid, cid);
    }
    
    public def searchBlock(rid:Long, cid:Long):MatrixBlock {
        val it = listBs.iterator();
        while (it.hasNext()) {
            val blk = it.next();
            if (blk.myRowId == rid &&
                    blk.myColId == cid ) return blk;
        }
        return null;
    }

    /**
     * For fast access block without searching block every time.
     * Restriction is block row id and column id must be the same as the block map
     * corrodinate indexes. 
     * This process is not a must, since block matrix has block id mapped to the
     * same index ID in listBs.
     */
    public def buildBlockMap(): void{
        if (blockMap != null) return;
        val rowmax = grid.numRowBlocks -1;
        val colmax = grid.numColBlocks -1;
        blockMap = new Array[MatrixBlock](Region.makeRectangular((0L..rowmax), ((0L as Long)..colmax)), ([r,c]:Point)=>findBlock(r,c));
    }
    
    /**
     * Access an element at the specified location in matrix. 
     * Note that x should be in the range 0..M-1, and y should lie in the range 0..N-1
     * 
     * @param x   row index 
     * @param y   column index
     * @return    element value
     * 
     */
    public operator this(x:Long, y:Long):Double {
        val loc = grid.find(x, y);
        val bid = grid.getBlockId(loc(0), loc(1));
        return this.getMatrix(bid)(loc(2), loc(3));
    }

    /**
     * Set the element's value at the specified row and column in matrix.
     * 
     * @param x   row index 
     * @param y   column index
     * @param v   the new value for the element
     */
    public operator this(x:Long, y:Long)=(v:Double):Double {
        val loc = grid.find(x, y);
        val bid = grid.getBlockId(loc(0), loc(1));
        this.getMatrix(bid)(loc(2), loc(3))=v;
        return v;
    }

    /**
     * Convert to dense matrix in provided memory space
     */
    public def copyTo(dm:DenseMatrix(M,N)):void {
        var rowoff:Long=0;
        var coloff:Long=0;
        for (var cb:Long=0; cb<grid.numColBlocks; coloff+=grid.colBs(cb), rowoff=0, cb++) {
            for (var rb:Long=0; rb<grid.numRowBlocks; rowoff+=grid.rowBs(rb), rb++) {
                
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
        
        for (var p:Long=0; p<grid.size; p++) {
            this.listBs(p).copyTo(that.listBs(p));
        }
    }
    
    /**
     * Copy data from blocks to a sparse CSC matrix
     * 
     * @param dst     target sparse matrix
     */
    public def copyTo(dst:SparseCSC(M,N)): void {

        val sz:Long = getStorageSize();
        //Check storage size
        dst.testIncStorage(0, sz);
        
        var dstcol:Long=0;
        var cnt:Long=0;
        for (var cb:Long=0; cb < grid.numColBlocks; cb++) {
            for (var col:Long=0; col < grid.colBs(cb); col++, dstcol++) {
                val dstln = dst.getCol(dstcol);
                var sttidx:Long = 0;

                dstln.offset = cnt;
                for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
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

    public def copyFrom(src:DenseMatrix(M,N)):void {
        var rowoff:Long=0;
        var coloff:Long=0;
        for (var cb:Long=0; cb<grid.numColBlocks; coloff+=grid.colBs(cb), rowoff=0, cb++) {
            for (var rb:Long=0; rb<grid.numRowBlocks; rowoff+=grid.rowBs(rb), rb++) {
                
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

    /**
     * Raise each cell in the matrix by the factor of a:Double.
     *
     * @param  a  -- the scaling factor
     */
    public  def scale(a:Double) {
        for (var b:Long=0; b<grid.size; b++) {
            listBs(b).getMatrix().scale(a);
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
            return cellAdd(x as BlockMatrix(M,N));

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
    protected def cellAddTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * this = this + x;
     */
    public def cellAdd(that:BlockMatrix(M,N)) {
        Debug.assure(likeMe(that), "Block matrix add fails - matrix partitioning incompatible");
        
        for (var p:Long=0; p<grid.size; p++) {
            val dst = this.listBs(p).getMatrix();
            val src = that.listBs(p).getMatrix();
            dst.cellAdd(src as Matrix(dst.M, dst.N));
        }        

        return this;
    }
    
    public def cellAdd(d:Double):BlockMatrix(this) {
        for (var p:Long=0; p<grid.size; p++) {
            val dst = listBs(p).getMatrix();
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
            return cellSub(x as BlockMatrix(M,N));
        // This is slow
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
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * this = this -v;
     */
    public def cellSub(v:Double) = this.cellAdd(-v);


    /**
     * this = this - that;
     */
    public def cellSub(that:BlockMatrix(M,N)) {
        Debug.assure(likeMe(that), 
                "Block matrix substract fails - matrix partition not compatible");
        
        for (var p:Long=0; p<grid.size; p++) {
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
        for (var p:Long=0; p<grid.size; p++) {
            val dst = this.getMatrix(p);
            dst.cellSubFrom(dv);
        }
        return this;    
    }


    /**
     * Cell-wise multiplication, return this *= x
     *
     * @param  x  the multiplying matrix
     */
    public def cellMult(x:Matrix(M,N)):BlockMatrix(this)  {
        if (likeMe(x))
            return cellMult(x as BlockMatrix(M,N));

        for (var c:Long=0; c<N; c++) {
            for (var r:Long=0; r<M; r++) {
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
     */
    public def cellMult(that:BlockMatrix(M,N)):BlockMatrix(this) {
        Debug.assure(likeMe(that), 
                "Block matrix cell mult fails - matrices partition not match");
        
        for (var p:Long=0; p<grid.size; p++) {
            val dst = this.listBs(p).getMatrix();
            val src = that.listBs(p).getMatrix();
            dst.cellMult(src as Matrix(dst.M, dst.N));
        }        
        return this;
    }


    /**
     * Cell-wise division, return this = this / x
     */
    public  def cellDiv(x:Matrix(M,N)): BlockMatrix(this){
        if (likeMe(x))
            return cellDiv(x as BlockMatrix(M,N));

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
        Debug.exit("Not implemented");
        return x;        
    }
    
    public def cellDivBy(v:Double):BlockMatrix(this) {
        Debug.exit("No implementation");
        return this;
    }
    
    /**
     * this /= x;
     */
    public def cellDiv(that:BlockMatrix(M,N)): BlockMatrix(this) {
        Debug.assure(likeMe(that), 
                "Block matrix cell divide fails - matrices partition not match");
        for (var p:Long=0; p<grid.size; p++) {
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
        for (var p:Long=0; p<grid.size; p++) {
            val dst = this.listBs(p).getMatrix();
            dst.scale(1.0/v);
        }
        return this;
    }

    /**
     * Not implemented yet. 
     * <p> This method is designed to perform block matrix multiplication, 
     * returning this += A *&#42 B if plus is true, else
     * this = A *&#42 B
     */
    public def mult(A:Matrix(this.M), B:Matrix(A.N,this.N),    plus:Boolean):Matrix(this) {
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

    public def mult(A:Matrix(this.M), B:Matrix(A.N,this.N)) = mult(A, B, false);
    public def transMult(A:Matrix{self.N==this.M}, B:Matrix(A.M,this.N)) = transMult(A, B, false);
    public def multTrans(A:Matrix(this.M), B:Matrix(this.N, A.N)) = multTrans(A, B, false);
        

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

    public def mult(A:BlockMatrix(this.M), B:BlockMatrix(A.N,this.N)) = mult(A, B, false);
    public def transMult(A:BlockMatrix{self.N==this.M}, B:BlockMatrix(A.M,this.N)) = transMult(A, B, false);
    public def multTrans(A:BlockMatrix(this.M), B:BlockMatrix(this.N,A.N)) = multTrans(A, B, false);
    

    //Operator overload

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
    public operator (alpha:Double) * this = this * alpha;
    
    public operator this + (that:BlockMatrix(M,N)) = makeDense(this).cellAdd(that) as BlockMatrix(M,N);
    public operator this - (that:DenseMatrix(M,N)) = makeDense(this).cellSub(that) as BlockMatrix(M,N);
    public operator this * (that:DenseMatrix(M,N)) = makeDense(this).cellMult(that) as BlockMatrix(M,N);
    public operator this / (that:DenseMatrix(M,N)) = makeDense(this).cellDiv(that) as BlockMatrix(M,N);

    // Utils
    public def getStorageSize():Long {
        var nzcnt:Long=0;
        for (var p:Long=0; p<grid.size; p++) {
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
        val output = new StringBuilder();
        output.add("---------- Block Matrix ["+M+"x"+N+"] ----------\n");
        for (var p:Long=0; p<grid.size; p++) {
            output.add("--- Block("+p+") ---\n"+this.listBs(p).toString());
        }
        output.add( "----------------------------------------------------\n");
        return output.toString();
    }

    public def printBlockMap() {
        val outstr = new StringBuilder();
        
        if (blockMap==null) buildBlockMap();
        for (var r:Long=blockMap.region.min(0); r<blockMap.region.max(0); r++) {
            for (var c:Long=blockMap.region.min(1); c<blockMap.region.max(1); c++) {
                val b = blockMap(r, c);
                outstr.add("Block("+r+","+c+"):["+b.myRowId+","+b.myColId+"] ");
            }
            outstr.add("\n");
        }
        Console.OUT.println(outstr.toString());
        Console.OUT.flush();
    }
}
