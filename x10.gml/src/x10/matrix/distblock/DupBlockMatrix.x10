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

package x10.matrix.distblock;

import x10.regionarray.Dist;
import x10.util.StringBuilder;
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.block.BlockMatrix;
import x10.matrix.comm.BlockSetBcast;
import x10.matrix.comm.BlockSetReduce;

public type DupBlockMatrix(m:Long, n:Long)=DupBlockMatrix{self.M==m, self.N==n};   
public type DupBlockMatrix(m:Long)=DupBlockMatrix{self.M==m}; 
public type DupBlockMatrix(C:DupBlockMatrix)=DupBlockMatrix{self==C}; 

public class DupBlockMatrix extends Matrix {
    public val handleDB:PlaceLocalHandle[BlockSet];
    public val local:PlaceLocalHandle[BlockMatrix(M,N)]; //Repackage blocks in BlockSet to BlockMatrix 
    public val tmpDB:PlaceLocalHandle[BlockSet];
    private transient var tmpReady:Boolean;

    /*
     * Time profiling
     */
    transient var commTime:Long = 0;
    transient var calcTime:Long = 0;
    
    public def this(bs:PlaceLocalHandle[BlockSet]) {
        //val mat:Matrix = blkhdl().getMatrix();
        super(bs().getGrid().M, bs().getGrid().N);
        handleDB  = bs;
        tmpDB = PlaceLocalHandle.make[BlockSet](Place.places(),
                ()=>new BlockSet(bs().grid, bs().dmap, Place.places()));
        tmpReady = false;
        local = PlaceLocalHandle.make[BlockMatrix(M,N)](Place.places(), 
                ()=>((new BlockMatrix(bs().getGrid(), bs().blocklist)) as BlockMatrix(M,N)));
    }

    public static def make(bset:BlockSet) {
        val grd = bset.getGrid();
        //Remote caputre is used, performance.
        val hdl = PlaceLocalHandle.make[BlockSet](Place.places(), ()=>bset);
        return new DupBlockMatrix(hdl) as DupBlockMatrix(grd.M,grd.N);
    }

    //Remote capture of g to all places
    public static def make(g:Grid) {
        val hdl = PlaceLocalHandle.make[BlockSet](Place.places(), 
                ()=>new BlockSet(g, DistMap.makeConstant(g.size, here.id()), Place.places()));
        return new DupBlockMatrix(hdl) as DupBlockMatrix(g.M, g.N);
    }
    
    public static def makeDense(g:Grid) {
        val hdl = PlaceLocalHandle.make[BlockSet](Place.places(), 
                ()=>BlockSet.makeDense(g, DistMap.makeConstant(g.size, here.id())));
        return new DupBlockMatrix(hdl) as DupBlockMatrix(g.M, g.N);
    }
    
    public static def makeSparse(g:Grid, nzd:Double) {
        val hdl = PlaceLocalHandle.make[BlockSet](Place.places(), 
                ()=>BlockSet.makeSparse(g, DistMap.makeConstant(g.size, here.id()), nzd));
        return new DupBlockMatrix(hdl) as DupBlockMatrix(g.M, g.N);
    }    
    
    public static def make(m:Long, n:Long, blkM:Long, blkN:Long) {
        val hdl = PlaceLocalHandle.make[BlockSet](Place.places(), 
                ()=>new BlockSet(new Grid(m, n, blkM, blkN), 
                        DistMap.makeConstant(blkM*blkN, here.id()), Place.places()));
        return new DupBlockMatrix(hdl) as DupBlockMatrix(m, n);
    }

    public static def makeDense(m:Long, n:Long, blkM:Long, blkN:Long) {
        val hdl = PlaceLocalHandle.make[BlockSet](Place.places(), 
                ()=>BlockSet.makeDense(new Grid(m, n, blkM, blkN), 
                        DistMap.makeConstant(blkM*blkN, here.id())));
        return new DupBlockMatrix(hdl) as DupBlockMatrix(m, n);
    }
    
    public static def makeSparse(m:Long, n:Long, blkM:Long, blkN:Long, nzd:Double) {
        val hdl = PlaceLocalHandle.make[BlockSet](Place.places(), 
                ()=>BlockSet.makeSparse(new Grid(m, n, blkM, blkN),
                        DistMap.makeConstant(blkM*blkN, here.id()), nzd));
        return new DupBlockMatrix(hdl) as DupBlockMatrix(m, n);
    }

    public static def makeDense(m:Long, n:Long) = makeDense(m, n, 1, 1);
    public static def makeSparse(m:Long, n:Long, nzd:Double) = makeSparse(m, n, 1, 1, nzd);
    
    public static def makeDense(dmat:DupBlockMatrix) {
        val hdl = PlaceLocalHandle.make[BlockSet](Place.places(), 
                ()=>BlockSet.makeDense(dmat.handleDB().getGrid(), dmat.handleDB().getDistMap()));
        val nm = new DupBlockMatrix(hdl) as DupBlockMatrix(dmat.M,dmat.N);
        dmat.copyTo(nm);
        return nm;
    }
    
    public def alloc(m:Long, n:Long):DupBlockMatrix(m,n) {
        throw new UnsupportedOperationException("Not support allocation");
    }
    
    public def alloc() = alloc(M,N);
    
    public def clone():DupBlockMatrix(M,N) {
        val bs = PlaceLocalHandle.make[BlockSet](Place.places(), 
                ()=>handleDB().clone());    
    
        return new DupBlockMatrix(bs) as DupBlockMatrix(M,N);
    }
    
    public def reset() {
        finish ateach(Dist.makeUnique()) {
            handleDB().reset();
        }
    }
    
    public def allocTmp() : void {
        if (tmpReady) return;
        tmpReady = true;
        finish ateach(Dist.makeUnique()) {
            val itr = handleDB().iterator();
            while (itr.hasNext()) {
                tmpDB().add(itr.next().alloc());
            }
        }
    }

    public def init(dv:Double) : DupBlockMatrix(this) {
        finish ateach(Dist.makeUnique()) {
            val it = handleDB().iterator();
            while (it.hasNext()) {
                it.next().init(dv);
            }
        }
        return this;
    }
    
    public def initRandom() : DupBlockMatrix(this) {
        val it = handleDB().iterator();
        while (it.hasNext()) {
            it.next().initRandom();
        }
        sync();
        return this;
    }
    
    public def initRandom(lo:Long, up:Long) : DupBlockMatrix(this) {
        val it = handleDB().iterator();
        while (it.hasNext()) {
            it.next().initRandom(lo,up);
        }
        sync();
        return this;
    }
    
    public def init(f:(Long,Long)=>Double) : DupBlockMatrix(this) {
        val it = handleDB().iterator();
        while (it.hasNext()) {
            it.next().init(f);
        }
        sync();
        return this;
    }
    
    public def copyTo(den:DenseMatrix(M,N)):void {
        local().copyTo(den);
    }

    public def copyTo(dst:DupBlockMatrix(M,N)):void {
        finish ateach(Dist.makeUnique()) {
            local().copyTo(dst.local());
        }
    }
    
    public def copyTo(mat:Matrix(M,N)):void {
        local().copyTo(mat);
    }
    
    public  operator this(x:Long, y:Long):Double = local()(x,y);

    public operator this(x:Long, y:Long)=(dv:Double):Double {
        finish ateach(Dist.makeUnique()) {
            //Remote capture: x, y, d
            local()(x,y) = dv;    
        }
        return dv;
    }
    
    public def getGrid():Grid = handleDB().getGrid();
    
    /**
     * Scaling method. All copies are updated concurrently
     */
    public def scale(a:Double) {
        finish ateach(Dist.makeUnique()) {
            local().scale(a);
        }
        return this;
    }
    
    /**
     * Cellwise multiplication. 
     */
    public def cellAdd(A:Matrix(M,N)) {
        local().cellAdd(A);
        sync();
        return this;
    }

    /**
     * Cellwise addition. All copies are updated
     */
    public def cellAdd(A:DenseMatrix(M,N)) {
        local().cellAdd(A);
        sync();
        return this;
    }

    /**
     * Concurrently perform cellwise addition on all copies.
     */
    public def cellAdd(that:DupBlockMatrix(M,N))  {
        //Debug.assure(this.M==A.M&&this.N==A.N);
        finish ateach(Dist.makeUnique()) {
            local().cellAdd(that.local());
        }
        return this;
    }

    public def cellAdd(d:Double)  {
        //Debug.assure(this.M==A.M&&this.N==A.N);
        finish ateach(Dist.makeUnique()) {
            local().cellAdd(d);
        }
        return this;
    }

    /**
     * Perform cellwise operation x = x + this 
     * 
     * @param x     input and output matrix
     * @return      the addition result
     */
    protected def cellAddTo(x:DenseMatrix(M,N)) {
        x.cellAdd(local());
        return x;
    }


    /**
     * Cellwise subtraction. 
     */
    public def cellSub(A:Matrix(M,N)) {
        local().cellSub(A);
        sync();
        return this;
    }

    /**
     * Cellwise subtraction. All copies are updated
     */
    public def cellSub(A:DenseMatrix(M,N))  {
        local().cellSub(A);
        sync();
        return this;
    }

    /**
     * Concurrently perform cellwise subtraction on all copies
     */
    public def cellSub(A:DupBlockMatrix(M,N)) {
        finish ateach(Dist.makeUnique()) {
            local().cellSub(A.local());
        }
        return this;
    }
    
    /**
     * Perform cell-wise subtraction  x = x - this.
     */
    public def cellSubFrom(x:DenseMatrix(M,N)) {
        x.cellSub(local());
        return x;
    }

    /**
     * Cellwise multiplication. 
     */
    public def cellMult(A:Matrix(M,N))  {
        local().cellMult(A);
        sync();
        return this;
    }
    
    /**
     * Cellwise multiplication.
     */
    public def cellMult(A:DenseMatrix(M,N)) {
        local().cellMult(A);
        sync();
        return this;
    }
    
    /**
     * Perform cell-wise multiply operation x = this &#42 x 
     */
    protected def cellMultTo(dst:DenseMatrix(M,N)) {
        dst.cellMult(local());
        return dst;
    }
    /**
     * Cellwise multiplication. All copies are modified with
     * the corresponding dense matrix copies.
     */
    public def cellMult(A:DupBlockMatrix(M,N))  {
        //Debug.assure(this.M==A.M&&this.N==A.N);
        finish ateach(Dist.makeUnique()) {
            local().cellMult(A.local());
        }
        return this;
     }

    /**
     * this = this /v
     */
    public def cellDiv(v:Double): DupBlockMatrix(this) {
        finish ateach(Dist.makeUnique()) {
            local().cellDiv(v);
        }
        return this;
    }    
    
    /**
     * Cellwise division. 
     */
    public def cellDiv(A:Matrix(M,N)): DupBlockMatrix(this) { 
        local().cellDiv(A);
        sync();
        return this;
    }    

    /**
     * Cellwise division. All copies are updated
     */
    public def cellDiv(A:DenseMatrix(M,N)) { 
        this.local().cellDiv(A);
        sync();
        return this;
    }

    /**
     * Cellwise division. All copies are modified with
     * the corresponding dense matrix copies.
     */    
    public def cellDiv(A:DupBlockMatrix(M,N)) {
        //Debug.assure(this.M==A.M&&this.N==A.N);
        finish ateach(Dist.makeUnique()) {
            this.local().cellDiv(A.local());
           }
        return this;
    }

    /** 
     * Perform cellwise return x = this / x 
     */ 
    protected def cellDivBy(x:DenseMatrix(M,N)) {
        x.cellDiv(local());
        return x;
    }

    // Operator overload
    public operator - this            = this.clone().scale(-1.0)    as DupBlockMatrix(M,N);
    public operator (v:Double) + this = makeDense(this).cellAdd(v)  as DupBlockMatrix(M,N);
    public operator this + (v:Double) = makeDense(this).cellAdd(v)  as DupBlockMatrix(M,N);
    public operator this - (v:Double) = makeDense(this).cellAdd(-v) as DupBlockMatrix(M,N);
    
    public operator this / (v:Double) = makeDense(this).cellDiv(v)     as DupBlockMatrix(M,N);
    
    public operator this * (alpha:Double) = this.clone().scale(alpha) as DupBlockMatrix(M,N);
    public operator (alpha:Double) * this = this * alpha;
    
    public operator this + (that:DupBlockMatrix(M,N)) = makeDense(this).cellAdd(that)  as DupBlockMatrix(M,N);
    public operator this - (that:DupBlockMatrix(M,N)) = makeDense(this).cellSub(that)  as DupBlockMatrix(M,N);
    public operator this * (that:DupBlockMatrix(M,N)) = makeDense(this).cellMult(that) as DupBlockMatrix(M,N);
    public operator this / (that:DupBlockMatrix(M,N)) = makeDense(this).cellDiv(that)  as DupBlockMatrix(M,N);

    /**
     * Multiplication method by using X10 driver. All copies are updated.
     * this = A &#42 B if plus is false, else this += A &#42 B
     */
    public def mult(A:Matrix(this.M), B:Matrix(A.N,this.N),    plus:Boolean):DupBlockMatrix(this) {

        if (A instanceof DenseMatrix(A) && B instanceof DenseMatrix(B) )
            return mult(A as DenseMatrix, B as DenseMatrix, plus);
        else if (A instanceof DupBlockMatrix && B instanceof DupBlockMatrix)
            return mult(A as DupBlockMatrix, B as DupBlockMatrix, plus);

        Debug.flushln("Not support using Matrix instances as parameters");
        throw new UnsupportedOperationException();
    }

    /**
     * this += A &#42 B if plus is true, else this = A %#42 B
     * @param A      the first operand of dense matrix
     * @param B      the second operand of dense matrix
     * @param plus     result plus flag
     */
    public def mult(A:DenseMatrix(this.M), B:DenseMatrix(A.N,this.N), plus:Boolean):DupBlockMatrix(this) {
        
        /* Timing */ val st = Timer.milliTime();
        local().mult(A, B, plus);
        /* Timing */ calcTime += Timer.milliTime() - st;
        sync();
        return this;
    }
    
    /**
     * this += A &#42 B if plus is true, results are synchronized at every place
     * 
     * @param A      the first operand of duplicated dense matrix
     * @param B      the second operand of duplicated dense matrix
     * @param plus   result plus flag     
     */    
    public def mult(A:DupBlockMatrix(this.M), B:DupBlockMatrix(A.N,this.N), plus:Boolean):DupBlockMatrix(this) {
        
        /* Timing */ val st = Timer.milliTime();
        finish ateach(Dist.makeUnique()) {
            this.local().mult(A.local(), B.local(), plus);
        }
        /* Timing */ calcTime += Timer.milliTime() - st;
        return this;
    }

    public def mult(A:Matrix(this.M),B:Matrix(A.N,this.N))                 = mult(A, B, false);
    public def mult(A:DenseMatrix(this.M), B:DenseMatrix(A.N,this.N))      = mult(A,B,false);
    public def mult(A:DupBlockMatrix(this.M), B:DupBlockMatrix(A.N,this.N))= mult(A,B,false);
    

    /**
     * this += A<sup>T</sup> &#42 B if plus is true. Result copies are 
     * synchronized in every place.
     * 
     * @param A      the first matrix operand
     * @param B      the second matrix operand
     * @param plus     result plus flag     
     */
    public def transMult(A:Matrix{self.N==this.M},B:Matrix(A.M,this.N), plus:Boolean):DupBlockMatrix(this) {
        
        if (A instanceof DenseMatrix && B instanceof DenseMatrix )    
            return transMult(A as DenseMatrix, B as DenseMatrix, plus);
        else if (A instanceof DupBlockMatrix && B instanceof DupBlockMatrix)
            return transMult(A as DupBlockMatrix, B as DupBlockMatrix, plus);
        //else if (A instanceof DistBlockMatrix && B instanceof DistDenseMatrix)
        //    return transMult(A as DistDenseMatrix, B as DistDenseMatrix, plus);
        //else if (A instanceof DistDenseMatrix && B instanceof DistSparseMatrix)
        //    return transMult(A as DistDenseMatrix, B as DistSparseMatrix, plus);
                
        Debug.flushln("Not support using Matrix instances as parameters");
                throw new UnsupportedOperationException();
    }    

    /**        
     * this += A<sup>T</sup> &#42 B if plus is true. Result copies are 
     *         synchronized in every place.
     * 
     *     @param A      first operand of dense matrix
     * @param B      second operand of dense matrix 
     * @param plus     result plus flag     
     */    
    public def transMult(A:DenseMatrix{self.N==this.M}, B:DenseMatrix(A.M,this.N), plus:Boolean):DupBlockMatrix(this) {
        /* Timing */ val st = Timer.milliTime();
        local().transMult(A, B, plus);
        /* Timing */ calcTime += Timer.milliTime() -st;
        sync();
        return this;
    }
    
    /**
     * this += A<sup>T</sup> &#42 B if plus is true, results are synchronized 
     * in every place.
     * 
     * @param A      first operand of dense matrix
     * @param B      second operand of dense matrix
     * @param plus     result plus flag     
     */    
    public def transMult(A:DupBlockMatrix{self.N==this.M}, B:DupBlockMatrix(A.M,this.N), plus:Boolean):DupBlockMatrix(this) {
        /* Timing */ val st = Timer.milliTime();
        finish ateach(Dist.makeUnique()) {
            local().transMult(A.local(), B.local(), plus);
        }
        /* Timing */ calcTime += Timer.milliTime() - st;
        return this;
    }

    public def transMult(A:Matrix{self.N==this.M},B:Matrix(A.M,this.N))                 =transMult(A,B,false);
    public def transMult(A:DenseMatrix{self.N==this.M}, B:DenseMatrix(A.M,this.N))      =transMult(A,B,false);
    public def transMult(A:DupBlockMatrix{self.N==this.M}, B:DupBlockMatrix(A.M,this.N))=transMult(A,B,false);
    

    /**
     * this = A &#42 B<sup>T</sup>
     */
    public def multTrans(A:Matrix(this.M), B:Matrix(this.N, A.N), plus:Boolean):DupBlockMatrix(this){

        if (A instanceof DenseMatrix && B instanceof DenseMatrix )
            return multTrans(A as DenseMatrix, B as DenseMatrix, plus);
        else if (A instanceof DupBlockMatrix && B instanceof DupBlockMatrix)
            return multTrans(A as DupBlockMatrix, B as DupBlockMatrix, plus);
        
        Debug.flushln("Not support using Matrix instances as parameters");
        throw new UnsupportedOperationException();
    }

    public def multTrans(A:DenseMatrix(this.M),    B:DenseMatrix(this.N,A.N), plus:Boolean ):DupBlockMatrix(this) {
        /* Timing */ val st = Timer.milliTime();
        local().multTrans(A, B, plus);
        /* Timing */ calcTime += Timer.milliTime() -st;
        sync();
        return this;
    }
    
    /**
     * this = A &#42 B, results are synchronized at every place
     */
    public def multTrans(A:DupBlockMatrix(this.M), B:DupBlockMatrix(this.N,A.N), plus:Boolean) {
        /* Timing */ val st = Timer.milliTime();
        finish ateach(Dist.makeUnique()) {
            local().multTrans(A.local(), B.local(), plus);
        }
        /* Timing */ calcTime += Timer.milliTime() - st;
        return this;
    }

    public def multTrans(A:Matrix(this.M), B:Matrix(this.N, A.N))               =multTrans(A,B,false);
    public def multTrans(A:DenseMatrix(this.M),    B:DenseMatrix(this.N,A.N))      =multTrans(A,B,false);
    public def multTrans(A:DupBlockMatrix(this.M), B:DupBlockMatrix(this.N,A.N))=multTrans(A,B,false);
    
    

    
    public def mult(A:DistBlockMatrix(this.M),B:DistBlockMatrix(A.N,this.N), plus:Boolean):DupBlockMatrix(this) =
        DistDistMult.mult(A, B, this, plus);

    public def transMult(A:DistBlockMatrix{self.N==this.M},B:DistBlockMatrix(A.M,this.N),plus:Boolean):DupBlockMatrix(this) =
        DistDistMult.compTransMult(A, B, this, plus);
    
    public def multTrans(A:DistBlockMatrix(this.M),B:DistBlockMatrix(this.N, A.N),plus:Boolean):DupBlockMatrix(this) =
        DistDistMult.compMultTrans(A, B, this, plus);

    public def mult(A:DistBlockMatrix(this.M),B:DistBlockMatrix(A.N,this.N))              = DistDistMult.mult(A, B, this, false);
    public def transMult(A:DistBlockMatrix{self.N==this.M},B:DistBlockMatrix(A.M,this.N)) = DistDistMult.compTransMult(A, B, this, false);
    public def multTrans(A:DistBlockMatrix(this.M),B:DistBlockMatrix(this.N, A.N))        = DistDistMult.compMultTrans(A, B, this, false);

    /**
     * Operator % performs duplicated block matrix multiplication
     */
    public operator this % (that:DupBlockMatrix{self.M==this.N}) =
        DupBlockMatrix.makeDense(this.M, that.N, this.local().grid.numRowBlocks, 
                that.local().grid.numColBlocks).mult(this, that, false) as DupBlockMatrix(this.M,that.N);


    public def sync():void {
        /* Timing */ val st = Timer.milliTime();
        BlockSetBcast.bcast(handleDB);
        /* Timing */ commTime += Timer.milliTime() - st;
        //Debug.flushln("Calling sync "+commTime);
    }

    
    public def reduce(opFunc:(DenseMatrix,DenseMatrix)=>DenseMatrix): void {
        val rootbid = here.id();
        allocTmp();
        /* Timing */ val st = Timer.milliTime();
        BlockSetReduce.reduce(handleDB, tmpDB, rootbid, opFunc);
        /* Timing */ commTime += Timer.milliTime() - st;
    }
    
    public def reduceSum(): void {
        val rootbid = here.id();
        allocTmp();
        /* Timing */ val st = Timer.milliTime();
        BlockSetReduce.reduceSum(handleDB, tmpDB, rootbid);
        /* Timing */ commTime += Timer.milliTime() - st;
    }
    
    public def allReduceSum(): void {
        allocTmp();
        /* Timing */ val st = Timer.milliTime();
        BlockSetReduce.allReduceSum(handleDB, tmpDB);
        /* Timing */ commTime += Timer.milliTime() - st;
    }    


    public def likeMe(that:Matrix): Boolean =
        (that instanceof DupBlockMatrix && this.local().likeMe((that as DupBlockMatrix).local()));
        


    public def checkSync():Boolean {
        val rootmat  = local();
        var retval:Boolean = true;
        for (var p:Long=0; p < Place.numPlaces() && retval; p++) {
            val blkitr = handleDB().iterator();
            while (blkitr.hasNext() && retval) {
                val blk = blkitr.next();
                retval &= at(Place(p)) {
                    val mat = blk.getMatrix();
                    val tgt = handleDB().find(blk.myRowId, blk.myColId);
                    mat.equals(tgt.getMatrix() as Matrix(mat.M, mat.N))
                };
            }
        }
        return retval;
    }

    public def getCalcTime() = calcTime;
    public def getCommTime() = commTime;
    public def getPlaces() = Place.places();

    public def toString() :String {
        val output=new StringBuilder();
        output.add( "---Duplicated block Matrix size:["+M+"x"+N+"]---\n");
        output.add(handleDB().toString());
        output.add( "--------------------------------------------------\n");
        return output.toString();
    }

    public def allToString() {
        val output=new StringBuilder();
        output.add("-------- Duplicated block matrix :["+M+" x "+N+"] ---------\n");
        for (p in Place.places()) {
            output.add("Copy at place " + p.id() +"\n");
            output.add(at (p) { handleDB().toString()});
        }
        output.add("--------------------------------------------------\n");
        return output.toString();
    }
}

