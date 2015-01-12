/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.matrix.dist;

import x10.regionarray.Dist;
import x10.regionarray.DistArray;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;

public type DistMatrix(M:Long, N:Long)=DistMatrix{self.M==M, self.N==N};   
public type DistMatrix(M:Long)=DistMatrix{self.M==M}; 
public type DistMatrix(C:DistMatrix)=DistMatrix{self==C}; 
public type DistMatrix(g:Grid)=DistMatrix{self.grid==g}; 

/**
 * This class provides an abstraction for distributed matrix blocks, which
 * can be either dense or sparse matrix blocks.
 */
public class DistMatrix(grid:Grid){grid.M==M,grid.N==N} extends Matrix{
    public val dist:Dist(1);
    public val distBs:DistArray[MatrixBlock](1);

    /**
     * Construct a distributed matrix based on specified partitioning and distributed
     * array of blocks in all places
     * 
     * @param  grid      Grid partitioning instance.
     * @param  dbs       distributed array of matrix block
     */
    public def this(g:Grid, dbs:DistArray[MatrixBlock](1)):DistMatrix(g.M,g.N) {
        super(g.M, g.N);
        property(g);
        dist   = dbs.dist;
        distBs = dbs;
        //Debug.assure(dbs.dist.region.size() == g.size, 
        //             "Partition block number and distribution region's size not match");
    }

    /**
     * Construct distributed matrix without data memory allocation.
     *
     * @param  g      matrix partitioning
     * @param  d      partition block distribution. 
     */
    public def this(g:Grid, d:Dist(1)):DistMatrix(g.M,g.N) {
        super(g.M, g.N);
        property(g);
        dist   = d;
        distBs = DistArray.make[MatrixBlock](d);
        //Debug.assure(d.region.size() == g.size, 
        //             "Partition block number and distribution region's size not match");        
    }

    /**
     * Construct distributed block matrix using specified partitioning.
     * Partitioned blocks are uniquely distributed among
     * all places.  Because there is no block type specified,
     * no memory space is allocated for matrix data.
     *
     * @param  g      matrix partitioning. How matrix is partitioned in a grid
     */
    public def this(g:Grid){
        super(g.M, g.N);
        property(g);
        dist   = Dist.makeUnique();
        distBs = DistArray.make[MatrixBlock](dist);
        //Debug.assure(g.size==Place.numPlaces(), 
        //             "Partition blocks cannot have unique distribution among all places");
    }

    /** 
     * Make a distributed block matrix based on specified partitioning.
     * All blocks are instances of dense matrix block.
     *
     * @param  gp      Matrix partitioning
     */    
    public static def makeDense(gp:Grid) : DistMatrix(gp.M, gp.N) {
        val ddm = new DistMatrix(gp);
        ddm.allocDenseBlocks();
        return ddm;
    }

    /** 
     * Make a distributed matrix based on given dimension.
     * All blocks are instances of dense matrix block
     *
     * @param  m      number of rows
     * @param  n      number of columns
     */    
    public static def makeDense(m:Long, n:Long) : DistMatrix(m, n) {
        val g =  Grid.make(m, n, Place.numPlaces());
        return makeDense(g);
    }

    public static def makeDense(dm:DistMatrix): DistMatrix(dm.M, dm.N) {
        val nm = makeDense(dm.grid);
        dm.copyTo(nm);
        return nm;
    }
    
    /** 
     * Make a distributed block matrix based on specified partitioning and sparsity.
     * All blocks are instances of sparse matrix block and each block is
     * created with the same sparsity.
     *
     * @param  gp      Matrix partitioning
     * @param  nzd     nonzero sparsity of each block
     */    
    public static def makeSparse(gp:Grid, nzd:Double) : DistMatrix(gp.M, gp.N) {
        val ddm = new DistMatrix(gp);
        ddm.allocSparseBlocks(nzd);
        return ddm;
    }

    /** 
     * Make a distributed block matrix based on specified dimension and sparsity.
     * All blocks are instances of sparse matrix block, and each block is
     * created with the same sparsity.
     *
     * @param  m      number of rows
     * @param  n      number of columns
     * @param  nzd      nonzero sparsity    of each block
     * @return
     */    
    public static def makeSparse(m:Long, n:Long, nzd:Double) : DistMatrix(m, n) {
        val g =  Grid.make(m, n, Place.numPlaces());
        return makeSparse(g, nzd);
    }

    /**
     * Allocate dense blocks in the distributed block array
     */
    public def allocDenseBlocks():void {
        finish for([p] in this.dist) {
            val rid = this.grid.getRowBlockId(p);
            val cid = this.grid.getColBlockId(p);
            at(this.dist(p)) async {
                this.distBs(p) = DenseBlock.make(this.grid, rid, cid);
            }
        }
    }

    /**
     * Allocate sparse blocks in the distributed block array
     *
     * @param  nzd      Nonzero sparsity of each block
     */
    public def allocSparseBlocks(nzd:Double):void {
        finish for([p] in this.dist) {
            val rid = this.grid.getRowBlockId(p);
            val cid = this.grid.getColBlockId(p);
            at(this.dist(p)) async {
                this.distBs(p) = SparseBlock.make(this.grid, rid, cid, nzd);
            }
        }
    }

    /**
     * Initial matrix data with a constant value. The matrix blocks must be created
     * as either dense blocks or sparse blocks.
     *
     */    
    public def init(ival:Double) : DistMatrix(this) {
        finish ateach([p] in this.dist) {
            distBs(p).init(ival);
        }
        return this;
    }
    
    /**
     * Initialize using function. 
     * 
     * @param f    The function to use to initialize the matrix, given global row and column index
     * @return this object
     */
    public def init(f:(Long,Long)=>Double): DistMatrix(this) {
        finish for (var cb:Long=0; cb<grid.numColBlocks; cb++) {
            for (var rb:Long=0; rb<grid.numRowBlocks; rb++) {
                val pid = grid.getBlockId(rb, cb);
                at(distBs.dist(pid)) async {
                    distBs(pid).init(f);
                }
            }
        }
        return this;
    }

    /**
     * Initial matrix data with random values. The blocks must be created. 
     */    
    public def initRandom() : DistMatrix(this) {
        finish ateach([p] in this.dist) {
            distBs(p).initRandom();
        }
        return this;
    }
    
    public def initRandom(lo:Long, up:Long) : DistMatrix(this) {
        finish ateach([p] in this.dist) {
            distBs(p).initRandom(lo, up);
        }
        return this;
    }

    /**
     * Allocate memory space for a distributed matrix.
     */    
    public def alloc(m:Long, n:Long):DistMatrix(m,n) {
        throw new UnsupportedOperationException();
    }

    /**
     * Make a copy of myself, including all distributed blocks.
     */    
    public def clone():DistMatrix(M,N) {

        // The following declaration will cause runtime null exception
        // when accessing nm.distBs(p) at place p. 
        //
        // nm = new DistMatrix(this.grid, this.dist);
        // 
        // Cause is unknown. No Jira report is filed, because the is problem
        // cannot be reproduced out of DistMatrix.

        // Workaround: create DistArray explicilty.
        val dBs = DistArray.make[MatrixBlock](this.dist);
        
        finish ateach([p] in this.distBs.dist) {
            val matblk:MatrixBlock = this.distBs(here.id());
            dBs(p)=matblk.clone();
        }
        
        val nm = new DistMatrix(this.grid, dBs);
        return nm;
    }

    // Copy
    public def copyTo(that:DistMatrix(M,N)):void {
        assert this.grid.equals(that.grid);        
        finish ateach([p] in this.dist) {
            val mypid = here.id();
            val smat  = this.getMatrix(mypid);
            smat.copyTo(that.getMatrix(mypid) as Matrix(smat.M, smat.N));
        }
    }
    
    /**
     * Convert distributed block matrix to dist dense matrix 
     * in all places
     *
     * @param   ddden   the target dense block matrix.
     */
    public def copyTo(dden:DistDenseMatrix{self.grid==this.grid}):void {
        finish ateach([p] in this.dist) {
            val src = local();
            val dst = dden.local();
            src.copyTo(dst as DenseMatrix(src.M, src.N));
        }
    }

    /**
     * Copy data from distributed block matrix of all places to dense matrix 
     * at here. Additional memory space is used to store dense blocks at here.
     *
     * @param  denmat   the target dense matrix.
     */
    public def copyTo(denmat:DenseMatrix(M,N)):void {
        throw new UnsupportedOperationException("Not implemented. Gather communication is not available for MatrixBlock");
    }

    public def copyTo(that:Matrix(M,N)): void {
        if (that instanceof DistMatrix)
            copyTo(that as DistMatrix);
        else if (that instanceof DenseMatrix)
            copyTo(that as DenseMatrix);
        else
            throw new UnsupportedOperationException("CopyTo: target matrix is not supported");
    }

    /**
     * Return matrix at coordinate (r, c);
     *
     * @param  r  the r coordinate in grid partition
     * @param  c  the c coordinate in grid partition
     * @return the instance dense matrix at its place.
     */
     public def getMatrix(r:Long, c:Long) = 
         distBs(grid.getBlockId(r,c)).getMatrix();

    /**
     * Return matrix at here
     *
     * @return the instance of block matrix at here
     */
    public def local():Matrix = distBs(here.id()).getMatrix();

    protected def getMatrix(i:Long):Matrix = distBs(i).getMatrix();

    /**
     * return value at(x, y) of the matrix block
     *
     * @param  x  the r-th row of the matrix
     * @param  y  the c-th column of the matrix
     * @return value of matrix at(x, y)
    */
    public operator this(x:Long, y:Long):Double {
        val loc = grid.find(x, y);
        val bid = grid.getBlockId(loc(0), loc(1));
        val dv = at(this.distBs.dist(bid)) this.distBs(bid)(loc(2), loc(3));
        return dv;
    }


    /**
     * Set value at(x, y) of the matrix
     *
     * @param  x  the r-th row of the matrix
     * @param  y  the c-th column of the matrix
     * @param  v  the value
     */
    public  operator this(x:Long,y:Long)=(v:Double):Double {
        val loc = grid.find(x, y);
        val bid = grid.getBlockId(loc(0), loc(1));
        at(distBs.dist(bid)) getMatrix(bid)(loc(2), loc(3))=v;
        return v;
    }

    /**
     * Reset the whole matrix
     */
    public  def reset():void {
        finish ateach([p] in this.distBs) {
           getMatrix(p).reset();
        }
    } 

    /**
     * Check matrix is DistDenseMatrix object or not
     */
    public def likeMe(A:Matrix):Boolean =
         ((A instanceof DistMatrix) &&
         ((A as DistMatrix).grid.equals(this.grid)) &&
         ((A as DistMatrix).dist.equals(dist)));


    /**
     * Multiply each element in matrix by a
     */    
    public def scale(a:Double) {
        finish ateach([p] in this.dist) {
            local().scale(a);
        }
        return this;
    }
     
    /**
     * Cellwise addition. Input A must be a DistMatrix
     */
    public def cellAdd(A:Matrix(M,N)) {
    if (! likeMe(A))
        throw new UnsupportedOperationException("Not implemented");
        return cellAdd(A as DistMatrix(M,N));
    }

    /**
     * this += A 
     */    
    public def cellAdd(A:DistMatrix(M,N)) {
        assert (this.grid.equals(A.grid)) :
            "Partitioning grid not compatible";

        finish ateach([p] in this.dist) {
            val m = local();
            m.cellAdd(A.local() as Matrix(m.M,m.N));
        }
        return this;
    }

    public def cellAdd(d:Double) {
        finish ateach([p] in this.dist) {
            val m = local();
            m.cellAdd(d);
        }
        return this;
    }     

    protected def cellAddTo(dst:DenseMatrix(M,N)):DenseMatrix(dst) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * dst = this + dst
     */
    public def cellAddTo(dst:DistMatrix(M,N)) {
        finish ateach([p] in this.dist) {
            val m = local();
            m.cellAddTo(dst.local() as DenseMatrix(m.M,m.N));
        }
        return dst;
    }

    /**
     * Cellwise subtraction. Inpute A must be a DistMatrix instance
     */
    public def cellSub(A:Matrix(M,N)):DistMatrix(this)  {
        if (! likeMe(A))
            throw new UnsupportedOperationException("Not implemented");
        return cellSub(A as DistMatrix(M,N));
    }

    /**
     * this -= A
     */    
    public def cellSub(A:DistMatrix(M,N)):DistMatrix(this) {
        assert (this.grid.equals(A.grid)) :
            "Partitioning grid not compatible";
            finish ateach([p] in this.dist) {
            val m = this.local();
            m.cellSub(A.getMatrix(p) as Matrix(m.M,m.N));
        }
        return this;
    }

    /**
     * dst = dst - this
     */
    protected def cellSubFrom(dst:DenseMatrix(M,N)):DenseMatrix(dst) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * dst = dst - this
     */
    public def cellSubFrom(dst:DistMatrix(M,N)):DistMatrix(dst) {
        finish ateach([p] in this.dist) {
            val m = local();
            m.cellSubFrom(dst.local() as DenseMatrix(m.M,m.N));
        }
        return dst;
    }

    /**
     * Cellwise multiplication. A must be a DistMatrix instance
     */
    public def cellMult(A:Matrix(M,N))  {
        if (! likeMe(A))
            throw new UnsupportedOperationException("Not implemented");
        return cellMult(A as DistMatrix(M,N));
    }

    /**
     * Cellwise multiply
     */    
    public def cellMult(A:DistMatrix(M,N)) {
        assert (this.grid.equals(A.grid)) :
            "Partitioning grid not compatible";

        finish ateach([p] in this.dist) {
            val m = local();
            m.cellMult(A.local() as Matrix(m.M, m.N));
        }
        return this;
    }

    protected def cellMultTo(dst:DenseMatrix(M,N)):DenseMatrix(dst) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Compute dst = dst &#42 this
     */
    public def cellMultTo(dst:DistMatrix(M,N)) {
        finish ateach([p] in this.dist) {
            val m = local();
            m.cellMultTo(dst.local() as DenseMatrix(m.M,m.N));
        }
        return dst;
    }

    /**
     * Cellwise division. A must be a DistMatrix instance
     */
    public def cellDiv(A:Matrix(M,N)) {
        if (! likeMe(A))
            throw new UnsupportedOperationException("Not implemented");
        return cellDiv(A as DistMatrix(M,N));
    }

    /**
     * Cellwise division this /= A.
     */    
    public def cellDiv(A:DistMatrix(M,N)) {
        assert (this.grid.equals(A.grid)) :
            "Partitioning grid not compatible";
            finish ateach([p] in this.dist) {
            val m = getMatrix(p);
            m.cellDiv(A.getMatrix(p) as Matrix(m.M,m.N));
        }
        return this;
    }
     
    protected def cellDivBy(dst:DenseMatrix(M,N)):DenseMatrix(dst) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * dst = this / dst
     */
    public def cellDivBy(dst:DistMatrix(M,N)) {
        finish ateach([p] in this.dist) {
            val m = local();
            m.cellDivBy(dst.local() as DenseMatrix(m.M,m.N));
        }
        return dst;
    }

    /**
     * Not implemented. 
     * This method is designed to perform matrix multiplication, 
     * returning this +=A &#42 B if plus is true, else
     * this  = A &#42 B
     */
    public def mult(A:Matrix(M), B:Matrix(A.N,N), plus:Boolean):Matrix(this){
        assert (likeMe(A) && likeMe(B)) :
            "Not available. Implementation depends on SUMMA";
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * Not implemented.
     */
    public def transMult(
            A:Matrix{self.N==this.M},
            B:Matrix(A.M,this.N),
            plus:Boolean): DistMatrix(this) {
        
        assert (likeMe(A) && likeMe(B)) :
            "Not available. Implementation depends on SUMMA";
        throw new UnsupportedOperationException("Not supported");
    }
    
    /**
     * Not implemented.
     */
    public def multTrans(A:Matrix(M), 
            B:Matrix{A.N==self.N,self.M==this.N}, 
            plus:Boolean):DistMatrix(this){ 
        assert (likeMe(A) && likeMe(B)) :
            "Not available. Implementation depends on SUMMA";
        throw new UnsupportedOperationException("Not supported");
    }     


    // Operator overloading

    public operator - this = this.clone().scale(-1.0) as DistMatrix(M,N);
    public operator (v:Double) + this = makeDense(this).cellAdd(v) as DistMatrix(M,N);
    public operator this + (v:Double) = v + this;

    public operator this - (v:Double) = makeDense(this).cellAdd(-v) as DistMatrix(M,N);
    public operator this / (v:Double) = makeDense(this).scale(1.0/v) as DistMatrix(M,N);
    //public operator (v:Double) / this = makeDense(this).cellDivBy(v) as DistMatrix(M,N);
            
    public operator this * (alpha:Double) = this.clone().scale(alpha) as DistMatrix(M,N);
    public operator (alpha:Double) * this = this * alpha;

    public operator this + (that:DistMatrix(M,N)) = makeDense(this).cellAdd(that) as DistMatrix(M,N);
    public operator this - (that:DistMatrix(M,N)) = makeDense(this).cellSub(that) as DistMatrix(M,N);
    public operator this * (that:DistMatrix(M,N)) = makeDense(this).cellMult(that) as DistMatrix(M,N);
    public operator this / (that:DistMatrix(M,N)) = makeDense(this).cellDiv(that) as DistMatrix(M,N);

    // Utils

    /**
     * Copy elements to dense matrix at here one by one. 
     *
     * @param   
     * @return
     */    
    public def toDense():DenseMatrix(M,N) {
        val dm = DenseMatrix.make(this.M, this.N);
        for (var c:Long =0; c<this.N; c++)
            for (var r:Long=0; r<this.M; r++)
                dm(r,c)=this(r,c);
        return dm;
    }

    public def toString() :String {
        var output:String = "-------- Dist Matrix size:["+M+"x"+N+"] ---------\n";
        
        for ([p] in this.distBs.dist) {
            output += "At place " + p +": ";
            val strbuf:String = at(distBs.dist(p)) { 
                val blk:MatrixBlock=this.distBs(p);
                val denblk = blk as DenseBlock;
                val mat:DenseMatrix=denblk.getMatrix(); 
                mat.toString()
            };
            output += strbuf;
            //output += this.getMatrix(p).toString();
        }
        output += "--------------------------------------------------\n";
        return output;
    }
}
