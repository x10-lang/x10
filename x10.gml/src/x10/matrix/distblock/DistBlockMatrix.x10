/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Sara Salem Hamouda 2014-2015.
 */

package x10.matrix.distblock;

import x10.regionarray.Dist;
import x10.util.Timer;
import x10.util.StringBuilder;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;


import x10.matrix.util.MathTool;
import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.BlockMatrix;
import x10.matrix.comm.BlockGather;
import x10.matrix.comm.BlockScatter;
import x10.matrix.comm.BlockSetBcast;
import x10.matrix.block.SparseBlock;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.builder.SparseCSCBuilder;
import x10.util.resilient.DistObjectSnapshot;
import x10.util.resilient.Snapshottable;
import x10.util.resilient.BlockSetSnapshotInfo;
import x10.util.ArrayList;
import x10.util.HashMap;

public type DistBlockMatrix(M:Long, N:Long)=DistBlockMatrix{self.M==M, self.N==N};   
public type DistBlockMatrix(M:Long)=DistBlockMatrix{self.M==M}; 
public type DistBlockMatrix(C:DistBlockMatrix)=DistBlockMatrix{self==C}; 

/**
 * Distributed block matrix provides data structure for dense or sparse
 * matrix partitioned in  blocks and distributed among multiple places.  
 * Each place is allowed to assigned more than one matrix blocks. 
 * Distribution of blocks defines the place ID for each block of the matrix.
 * DistBlockMatrix is designed to replace exiting DistDenseMatrix and DistSparseMatrix,
 * which only support unique distribution, i.e. one block for designated places.
 * 
 * <p>In a DistBlockMatrix instance, matrix data partitioning is defined separately from
 * distribution of partitioned blocks.
 * Matrix data partition is specified by Grid and distribution is specified by DistMap.
 * 
 * <p>PlaceLocalHandle is used to hold blocks assigned to places.  In each place,
 * BlockSet stores all blocks in an ArrayList, with a copy of partitioning Grid and
 * distribution map DistMap.
 */
public class DistBlockMatrix extends Matrix implements Snapshottable {
    public var handleBS:PlaceLocalHandle[BlockSet];

    /**
     * Time profiling
     */
    transient var commTime:Long =0;
    transient var calcTime:Long =0;

    //This field only defined when DistGrid is used in block distribution
    transient var gdist:DistGrid = null;

    //The place group on which the matrix is distributed
    private var places:PlaceGroup;
    public def places() = places;
    
    //snapshotGrid and snapshotMap used only for restore
    private var snapshotGrid:Grid;
    private var snapshotMap:DistMap;
    private static val rebalanceMode = System.getenv("X10_GML_REBALANCE");
    
    public def this(bs:PlaceLocalHandle[BlockSet], plcs:PlaceGroup) {
        super(bs().grid.M, bs().grid.N);
        handleBS  = bs;    
        places = plcs;
    }
    
    public def this(gridDist:DistGrid, bs:PlaceLocalHandle[BlockSet], plcs:PlaceGroup) {
        super(bs().grid.M, bs().grid.N);
        gdist = gridDist;        
        handleBS  = bs;        
        places = plcs;
    }
    
    /**
     * Create dist block matrix using specified matrix data partitioning grid and
     * block distribution map. No actual memory space is allocated for matrix
     * since the matrix block type (dense/sparse) is not specified 
     * 
     * @param g     matrix data partitioning
     * @param dmpa  partitioning blocks distribution map
     * @return      DistBlockMatrix object (no memory allocation for matrix data)
     */
     public static def make(g:Grid, dmap:DistMap):DistBlockMatrix(g.M, g.N) = make(g, dmap, Place.places());
     
     public static def make(g:Grid, dmap:DistMap, places:PlaceGroup):DistBlockMatrix(g.M, g.N){
        //Remote capture: g, dmap
        val bs = PlaceLocalHandle.make[BlockSet](places, ()=>(new BlockSet(g, dmap, places)));//Remote capture
        return new DistBlockMatrix(bs, places) as DistBlockMatrix(g.M,g.N);
    }
     
    public static def make(g:Grid, gridDist:DistGrid):DistBlockMatrix(g.M, g.N) = make(g, gridDist, Place.places());
    
    public static def make(g:Grid, gridDist:DistGrid, places:PlaceGroup):DistBlockMatrix(g.M, g.N){
        //Remote capture: g, dmap
        val dmap = gridDist.dmap;
        val bs = PlaceLocalHandle.make[BlockSet](places, ()=>(new BlockSet(g, dmap, places)));//Remote capture
        return new DistBlockMatrix(gridDist, bs, places) as DistBlockMatrix(g.M,g.N);
    }
    
    /**
     * Create distributed block matrix given rows and columns of matrix, and 
     * how matrix is partitioned into blocks, which are distributed among places 
     * in a specified row and column place grid
     * 
     * @param m         number of matrix rows
     * @param n         number of matrix columns
     * @param rowBs     number of matrix partitioning row blocks
     * @param colBs     number of matrix partitioning column blocks
     * @param rowPs     number of rows of place grid
     * @param colPs     number of columns of place grid
     */
    public static def make(m:Long, n:Long, rowBs:Long, colBs:Long, rowPs:Long, colPs:Long):DistBlockMatrix(m,n) {
        return make(m, n, rowBs, colBs, rowPs, colPs, Place.places());
    }
    
    public static def make(m:Long, n:Long, 
            rowBs:Long, colBs:Long, 
            rowPs:Long, colPs:Long, places:PlaceGroup):DistBlockMatrix(m,n) {
        assert (rowPs*colPs == places.size()) :
            "Block partitioning error";        
        val blks = PlaceLocalHandle.make[BlockSet](places, 
                ()=>(BlockSet.make(m,n,rowBs,colBs,rowPs,colPs, places)));
        val mygrid = blks().getGrid();
        val gdist = new DistGrid(mygrid, rowPs, colPs);
        return new DistBlockMatrix(gdist, blks, places) as DistBlockMatrix(m,n);
        
        //val grid = new Grid(m, n, rowBs, colBs);
        //val dstgrid = new DistGrid(grid, rowPs, colPs);
        //return DistBlockMatrix.make(grid, dstgrid.dmap);
    }
    
    /**
     * Create DistBlockMatrix instance using specified number blocks in row and column
     * and default block distribution map
     *
     * @param m, n           number of matrix rows and columns
     * @param rowBs, colBs   number of row and column blocks in partitioning
     * @return DistBlockMatrix instance without memory allocation for matrix data
     */
    public static def make(m:Long, n:Long, rowBs:Long, colBs:Long):DistBlockMatrix(m,n) {
       return make(m, n, rowBs, colBs, Place.places());
    }
    
    public static def make(m:Long, n:Long, rowBs:Long, colBs:Long, places:PlaceGroup):DistBlockMatrix(m,n) {
        val colPs:Long = MathTool.sqrt(places.size());
        val rowPs = places.size() / colPs;
        return make(m, n, rowBs, colBs, rowPs, colPs, places);
    }
    
    /**
     * Create DistBlockMatrix instance using default partitioning and distribution map
     * 
     * @param m, n           number of matrix rows and columns
     * @return DistBlockMatrix instance
     */
    public static def make(m:Long, n:Long):DistBlockMatrix(m,n) {
        return make(m, n, Place.places());
    }
    
    public static def make(m:Long, n:Long, places:PlaceGroup):DistBlockMatrix(m,n) {
        val colBs = MathTool.sqrt(places.size());
        val rowBs = places.size() / colBs;
        return make(m, n, rowBs, colBs, rowBs, colBs, places);
    }
    
    /**
     * Create DistBlockMatrix instance by using row and column matrix data partitioning
     * function and block distribution map function.
     * 
     * @param m,n           number of rows and columns
     * @param rowbs,colbs   number of partitioning row blocks and column blocks
     * @param rowPartFunc   row block partitioning function, given row block index, returning number of rows in blocks
     * @param colPartFunc   column blocks partitioning function, given column block index, returning number of column in blocks
     * @param mapFunc       block distribution map function, given a block ID, returning a place ID.
     */
     public static def make(m:Long, n:Long,rowbs:Long, colbs:Long,
             rowPartFunc:(Long)=>Long,colPartFunc:(Long)=>Long,mapFunc:(Long)=>Long){
     
         return make(m, n,rowbs, colbs,rowPartFunc,colPartFunc,mapFunc, Place.places());
     }
     public static def make(m:Long, n:Long, rowbs:Long, colbs:Long,
            rowPartFunc:(Long)=>Long, colPartFunc:(Long)=>Long, mapFunc:(Long)=>Long, places:PlaceGroup) {
        
        val ttbs = rowbs * colbs;
        val blks = PlaceLocalHandle.make[BlockSet](places, 
                ()=>(new BlockSet(Grid.make(rowbs, colbs, rowPartFunc, colPartFunc), DistMap.make(ttbs, mapFunc, places.size()), places)));
        return new DistBlockMatrix(blks, places);
    }

    //Make a copy, but sharing partitioning grid and distribution map
    public static def makeDense(d:DistBlockMatrix):DistBlockMatrix(d.M,d.N) {
        val sblks = d.handleBS;
        val dblks = PlaceLocalHandle.make[BlockSet](d.places, 
                ()=>(BlockSet.makeDense(sblks().grid, sblks().dmap, d.places)));
        
        return  new DistBlockMatrix(d.gdist, dblks, d.places) as DistBlockMatrix(d.M,d.N);
    }
    public static def makeDense(g:Grid, gd:DistGrid):DistBlockMatrix(g.M,g.N) =
        makeDense(g, gd, Place.places());
    
    public static def makeDense(g:Grid, gd:DistGrid, places:PlaceGroup):DistBlockMatrix(g.M,g.N) {
        val bs = PlaceLocalHandle.make[BlockSet](places, 
                ()=>(BlockSet.makeDense(g, gd.dmap, places)));//Remote capture
        return new DistBlockMatrix(gd, bs, places) as DistBlockMatrix(g.M,g.N);        
    }
    public static def makeSparse(g:Grid, gd:DistGrid, nzp:Float):DistBlockMatrix(g.M,g.N) =
        makeSparse(g, gd, nzp, Place.places());
    
    public static def makeSparse(g:Grid, gd:DistGrid, nzp:Float, places:PlaceGroup):DistBlockMatrix(g.M,g.N) {
        val bs = PlaceLocalHandle.make[BlockSet](places, 
                ()=>(BlockSet.makeSparse(g, gd.dmap, nzp, places)));//Remote capture
        return new DistBlockMatrix(gd, bs, places) as DistBlockMatrix(g.M,g.N);        
    }

    public static def makeDense(g:Grid, d:DistMap):DistBlockMatrix(g.M,g.N) =
        makeDense(g, d, Place.places());
    
    public static def makeDense(g:Grid, d:DistMap, places:PlaceGroup):DistBlockMatrix(g.M,g.N) {
        val bs = PlaceLocalHandle.make[BlockSet](places, 
                ()=>(BlockSet.makeDense(g, d, places)));//Remote capture
        return new DistBlockMatrix(bs, places) as DistBlockMatrix(g.M,g.N);        
    }
    public static def makeSparse(g:Grid, d:DistMap, nzp:Float):DistBlockMatrix(g.M,g.N) =
        makeSparse(g, d, nzp, Place.places());
    
    public static def makeSparse(g:Grid, d:DistMap, nzp:Float, places:PlaceGroup):DistBlockMatrix(g.M,g.N) {
        val bs = PlaceLocalHandle.make[BlockSet](places, 
                ()=>(BlockSet.makeSparse(g, d, nzp, places)));//Remote capture
        return new DistBlockMatrix(bs, places) as DistBlockMatrix(g.M,g.N);        
    }
    
    public static def makeDense(g:Grid):DistBlockMatrix(g.M,g.N) =
        makeDense(g, Place.places());
    
    public static def makeDense(g:Grid, places:PlaceGroup):DistBlockMatrix(g.M,g.N) =
        makeDense(g, DistGrid.make(g, places.size()).dmap, places);
    
    public static def makeSparse(g:Grid, nzp:Float):DistBlockMatrix(g.M,g.N) =
        makeSparse(g, nzp, Place.places());
    
    public static def makeSparse(g:Grid, nzp:Float, places:PlaceGroup):DistBlockMatrix(g.M,g.N) =
        makeSparse(g, DistGrid.make(g, places.size()).dmap, nzp, places);



    public static def makeDense(m:Long, n:Long, rbs:Long, cbs:Long, rps:Long, cps:Long) =
        makeDense(m, n, rbs, cbs, rps, cps, Place.places());
        
    public static def makeDense(m:Long, n:Long, rbs:Long, cbs:Long, rps:Long, cps:Long, places:PlaceGroup) =
        make(m, n, rbs, cbs, rps, cps, places).allocDenseBlocks();

    public static def makeDense(m:Long, n:Long, rbs:Long, cbs:Long) =
        makeDense(m, n, rbs, cbs, Place.places());
    
    public static def makeDense(m:Long, n:Long, rbs:Long, cbs:Long, places:PlaceGroup) =
        make(m, n, rbs, cbs, places).allocDenseBlocks();

    public static def makeSparse(m:Long, n:Long, rbs:Long, cbs:Long, rps:Long, cps:Long, npz:Float) =
        makeSparse(m, n, rbs, cbs, rps, cps, npz, Place.places());
    
    public static def makeSparse(m:Long, n:Long, rbs:Long, cbs:Long, rps:Long, cps:Long, npz:Float, places:PlaceGroup) =
        make(m, n, rbs, cbs, rps, cps, places).allocSparseBlocks(npz);

    public static def makeSparse(m:Long, n:Long, rbs:Long, cbs:Long, npz:Float) =
        makeSparse(m, n, rbs, cbs, npz, Place.places());
    
    public static def makeSparse(m:Long, n:Long, rbs:Long, cbs:Long, npz:Float, places:PlaceGroup) =
        make(m, n, rbs, cbs, places).allocSparseBlocks(npz);

    /**
     * Allocate dense matrix for all blocks
     */    
    public def allocDenseBlocks():DistBlockMatrix(this) {
        finish ateach(p in Dist.makeUnique(places)) {
            handleBS().allocDenseBlocks();
        }
        return this;
    }

    /**
     * Allocate sparse matrix for all blocks
     */
    public def allocSparseBlocks(nzd:Float):DistBlockMatrix(this) {
        //Remote capture: nnz
        finish ateach(p in Dist.makeUnique(places)) {
            handleBS().allocSparseBlocks(nzd);
        }
        return this;
    }
    
    /**
     * Allocate sparse matrix for blocks with arbitrary sizes.
     * If the block set has previosly allocated blocks, they will be deleted first.
     */
    private def allocAndInitNonUniformSparseBlocks(f:(Long,Long)=>ElemType):DistBlockMatrix(this) {        
        finish ateach(p in Dist.makeUnique(places)) {
            handleBS().clear(); // delete old blocks
            handleBS().allocAndInitNonUniformSparseBlocks(f);
        }
        return this;
    }
    
    /**
     * Used to create temporary space in SUMMA. This method does not create a complete 
     * distributed block matrix. It only creates the front blocks of specified number of rows.
     * The front row blocks are used to as temp space to store data of rows of matrix from
     * of the second operand in SUMMA
     */
    public def makeTempFrontRowBlocks(rowCnt:Long) =
        PlaceLocalHandle.make[BlockSet](places,
                ()=>this.handleBS().makeFrontRowBlockSet(rowCnt));
    
    /**
     * Used to create temporary space in SUMMA. This method does not create a complete 
     * distributed block matrix. It only creates the front blocks of specified number of
     * columns. The front column blocks are used to as temp space to store data of
     * columns from the first operand matrix in SUMMA
     */
    public def makeTempFrontColBlocks(colCnt:Long) =
        PlaceLocalHandle.make[BlockSet](places,
                ()=>this.handleBS().makeFrontColBlockSet(colCnt));

    public def makeTempFrontColDenseBlocks(colCnt:Long) =
        PlaceLocalHandle.make[BlockSet](places,
                ()=>this.handleBS().makeFrontColDenseBlockSet(colCnt));
    
    public def init(dval:ElemType):DistBlockMatrix(this){
        //Remote capture: dval 
        finish ateach(p in Dist.makeUnique(places)) {
            val blks = handleBS();
            val blkitr = blks.iterator();
            while (blkitr.hasNext()) {
                val blk = blkitr.next();
                blk.init(dval);
            }
        }
        return this;
    }
    
    public def initRandom() : DistBlockMatrix(this){
        finish ateach(p in Dist.makeUnique(places)) {
            val blkitr = handleBS().iterator();
            while (blkitr.hasNext()) {
                val blk = blkitr.next();
                blk.initRandom();
            }
        }
        return this;
    }
    
    public def initRandom(lb:Long, ub:Long) : DistBlockMatrix(this){
        finish ateach(p in Dist.makeUnique(places)) {
            val blkitr = handleBS().iterator();
            while (blkitr.hasNext()) {
                val blk = blkitr.next();
                blk.initRandom(lb, ub);
            }
        }
        return this;
    }

    /**
     * Initial DistBlockMatrix with a given function
     */
    public def init(f:(Long,Long)=>ElemType): DistBlockMatrix(this) {
        finish ateach(p in Dist.makeUnique(places)) {
            val blks   = handleBS();
            val grid   = blks.grid;
            val blkitr = blks.iterator();
            while (blkitr.hasNext()) {
                val blk = blkitr.next();              
                blk.init(f);
            }
        }
        return this;
    }
    
    /**
     * Initialize specified block
     */
    public def initBlock(bid:Long, f:(Long,Long)=>ElemType): DistBlockMatrix(this) {
        val pIndex = this.getMap().findPlaceIndex(bid);
        at(places(pIndex)) {
            val blk = handleBS().findBlock(bid);
            blk.init(f);
        }
        return this;
    }
    
    public def initBlock(rowId:Long,colId:Long, f:(Long,Long)=>ElemType) =
        initBlock(getGrid().getBlockId(rowId, colId), f);
    
    /**
     * Allocate memory space for new dist block matrix using the same
     * matrix partitioning and block distribution as this.
     */
    public def alloc(m:Long, n:Long) : DistBlockMatrix(m,n) {
        assert (m==M && n==N) :
            "Matrix dimension is not same";
        val nm = DistBlockMatrix.make(getGrid(), getMap(), places) as DistBlockMatrix(m,n);
        finish ateach(p in Dist.makeUnique(places)) {
            val blkitr = handleBS().iterator();
            val nblk   = nm.handleBS();
            while (blkitr.hasNext()) {
                val mb = blkitr.next();
                nblk.add(mb.alloc());
            }
        }
        return nm;
    }
    
    public def clone():DistBlockMatrix(M,N) {
        val bs = PlaceLocalHandle.make[BlockSet](places,     
                    ()=>(this.handleBS().clone()));
        
        return new DistBlockMatrix(bs, places) as DistBlockMatrix(M,N);
    }
    
    public def reset() {
        finish ateach(p in Dist.makeUnique(places)) {
            handleBS().reset();
        }
    }
    
    public def copyTo(that:DistBlockMatrix(M,N)) {
        val stt = Timer.milliTime();
        finish ateach(p in Dist.makeUnique(places)) {
            val sit  = this.handleBS().iterator();
            val dit  = that.handleBS().iterator();
            while (sit.hasNext()&&dit.hasNext()) {
                val sblk = sit.next();
                val dblk = dit.next();
                assert (sblk.myRowId==dblk.myRowId && sblk.myColId==dblk.myColId) :
                    "Block mismatch in DistBlockMatrix copyTo";
                val smat = sblk.getMatrix();
                val dmat = dblk.getMatrix();
                smat.copyTo(dmat as Matrix(smat.M, smat.N));
            }
        }
        commTime += Timer.milliTime() - stt;
    }

    public def copyTo(dst:BlockMatrix(M,N)) {
        val srcgrid = this.getGrid();
        assert (srcgrid.equals(dst.grid)) :
            "source and destination matrix partitions are not compatible";
        val stt = Timer.milliTime();
        BlockGather.gather(this.handleBS, dst.listBs);
        commTime += Timer.milliTime() - stt;
    }
    
    public def copyTo(dst:DupBlockMatrix(M,N)) {
        val srcgrid = this.getGrid();
        assert (srcgrid.equals(dst.local().grid)) :
            "source and destination matrix partitions are not compatible";
        val stt = Timer.milliTime();        
        BlockGather.gather(this.handleBS, dst.local().listBs);
        //TODO: bcast should allow arbitrary place group
        BlockSetBcast.bcast(dst.handleDB);
        commTime += Timer.milliTime() - stt;
    }
    
    public def copyTo(dst:Matrix(M,N)):void {
        val grid = getGrid();
        val stt = Timer.milliTime();

        if (dst instanceof DistBlockMatrix) {
            copyTo(dst as DistBlockMatrix(M,N));
        } else if (dst instanceof BlockMatrix) {
            copyTo(dst as BlockMatrix(M,N));
        } else if (dst instanceof DupBlockMatrix) {
            copyTo(dst as DupBlockMatrix(M,N));
        } else if ((dst.N == 1L)&&( dst instanceof DenseMatrix)) {
            BlockGather.gatherVector(this.handleBS, dst as DenseMatrix(M,1L));
            commTime += Timer.milliTime() - stt;
        } else if (grid.numRowBlocks==1L) {            
            BlockGather.gatherRowBs(this.handleBS, dst);
            commTime += Timer.milliTime() - stt;
        } else {
            throw new UnsupportedOperationException("Not supported matrix type for converting DistBlockMatrix");
        }
    }
    
    public def copyTo(den:DenseMatrix(M,N)): void {
        val stt = Timer.milliTime();
        if (den.N == 1L) {
            BlockGather.gatherVector(this.handleBS, den as DenseMatrix(M,1L));
        } else if (getGrid().numRowBlocks==1L){
            BlockGather.gatherRowBs(this.handleBS, den as Matrix);
        } else {
            throw new UnsupportedOperationException("DistBlockMatrix does not support direct copyTo densematrix,"+
                    "unless it is single-column matrix (vector)."+
                    "Workaround is to use inter-media BlockMatrix to save gathered blocks"+
                    "and then convert to dense using BlockMatrix.copyTo(DenseMatrix)");
        }
        commTime += Timer.milliTime() - stt;
    }

    public def copyFrom(src:BlockMatrix(M,N)) {
        val dstgrid = getGrid();
        val stt = Timer.milliTime();

        assert (dstgrid.equals(src.grid)) :
            "source and destination matrix partitions are not compatible";
        BlockScatter.scatter(src.listBs, this.handleBS);
        commTime += Timer.milliTime() - stt;
    }
    
    public  operator this(x:Long, y:Long):ElemType {
        val grid = handleBS().grid;
        val loc = grid.find(x, y);
        val bid = grid.getBlockId(loc(0), loc(1));
        val bx  = loc(2);
        val by  = loc(3);
        val pIndex = handleBS().dmap.findPlaceIndex(bid);
        //Remote capture: bid, bx, by, 
        val dv = at(places(pIndex)) {
            val blkset:BlockSet = this.handleBS();
            val blk:MatrixBlock = blkset.find(bid);
            if (blk == null) 
                throw new UnsupportedOperationException("Error in search blocks in block set");

            blk(bx, by)
        };
        return dv;
    }
    public operator this(x:Long, y:Long)=(d:ElemType):ElemType {
        val grid = handleBS().grid;
        val loc = grid.find(x, y);
        val bid = grid.getBlockId(loc(0), loc(1));
        val bx  = loc(2);
        val by  = loc(3);
        val pIndex = handleBS().dmap.findPlaceIndex(bid);
        //Remote capture: bid, bx, by, 
        at(places(pIndex)) {
            val blkset:BlockSet = handleBS();
            val blk:MatrixBlock = blkset.find(bid);
            if (blk == null) 
                throw new UnsupportedOperationException("Error in search blocks in block set");
            
            blk.getMatrix()(bx, by) = d;
        }
        return d;
    }
    
    private def getDataValue(x:Long, y:Long, blockSet:BlockSet, grid:Grid):ElemType{
        val loc = grid.find(x, y);
        val bx  = loc(2);
        val by  = loc(3);                
        val blk:MatrixBlock = blockSet.find(loc(0),loc(1) );
        if (blk == null) 
            throw new UnsupportedOperationException("Error in search blocks in block set");
        return blk(bx, by);        
    }

    /**
     * Get block to here. If block is not at local, it will be remote captured
     * and compied to here.
     */
    public def fetchBlock(bid:Long):MatrixBlock {
        val map = getMap();
        val pIndex = map.findPlaceIndex(bid);
        val blk = at(places(pIndex)) handleBS().findBlock(bid);
        return blk;
    }
    public def fetchBlock(rid:Long, cid:Long):MatrixBlock =
        fetchBlock(getGrid().getBlockId(rid, cid));

    public def getGrid():Grid   = this.handleBS().grid;
    public def getMap():DistMap = this.handleBS().dmap;

    public def scale(alpha:ElemType): DistBlockMatrix(this) {
        finish ateach(p in Dist.makeUnique(places)) {
            val blkitr = this.handleBS().iterator();
            while (blkitr.hasNext()) {
                val blk = blkitr.next();
                blk.getMatrix().scale(alpha);
            }
        }
        return this;
    }
    
    public def cellAdd(that:Matrix(M,N)): Matrix(this)  {
        if (! likeMe(that))
            throw new UnsupportedOperationException("Distributed matrix not compatible");
        return cellAdd(that as DistBlockMatrix(M,N));
    }
    
    public def cellAdd(dv:ElemType): DistBlockMatrix(this) {
        finish ateach(p in Dist.makeUnique(places))  {
            //Remote capture: dv
            val bitr:Iterator[MatrixBlock] = this.handleBS().iterator();
            
            while (bitr.hasNext()) {
                val b:MatrixBlock = bitr.next();
                val mat = b.getMatrix();
                mat.cellAdd(dv);
            }
        }
        return this;
    }
        
    public def cellAdd(A:DistBlockMatrix(M,N)): DistBlockMatrix(this)  {
        if (! likeMe(A))
            throw new UnsupportedOperationException("Distributed matrix not compatible");
        
        finish ateach(p in Dist.makeUnique(places))  {
            val bsetA= A.handleBS();
            val itr = this.handleBS().iterator();
            while (itr.hasNext()) {
                 val b1:MatrixBlock = itr.next();
                 val m1:Matrix      = b1.getMatrix();
                 val b2:MatrixBlock = bsetA.find(b1.myRowId, b1.myColId);
                 
                 if (b2 == null) throw new UnsupportedOperationException("Can not find corresponding block");
                 m1.cellAdd(b2.getMatrix() as Matrix(m1.M, m1.N));
            }
        }
        return this;
    }
    
    protected def cellAddTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
        throw new UnsupportedOperationException("Matrix type missmatch for the operation");                
    }
    
    public def cellSub(A:Matrix(M,N)): Matrix(this) {
        if (! likeMe(A))
            throw new UnsupportedOperationException("Distributed matrix not compatible for computing");
        return cellSub(A as DistBlockMatrix(M,N));
    }
    
    public def cellSub(A:DistBlockMatrix(M,N)): DistBlockMatrix(this) {
        if (! likeMe(A))
            throw new UnsupportedOperationException("Distributed matrix not compatible for computing");
        
        finish ateach(p in Dist.makeUnique(places)) {
            val itr = this.handleBS().iterator();
            val blkA = A.handleBS();
            while (itr.hasNext()) {
                val b1 = itr.next();
                val m1 = b1.getMatrix();
                val b2 = blkA.find(b1.myRowId, b1.myColId);
                if (b2 == null) throw new UnsupportedOperationException("Can not find corresponding block");
                m1.cellSub(b2.getMatrix() as Matrix(m1.M, m1.N));
            }
        }
        return this;    
    }
    
    protected def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x) {
        throw new UnsupportedOperationException("Matrix type mismatch");                        
    }

    public def cellMult(A:Matrix(M,N)): Matrix(this) {
        if (! likeMe(A))
            throw new UnsupportedOperationException("Distributed matrix not compatible for computing");
        return cellMult(A as DistBlockMatrix(M,N));    
    }

    public def cellMult(A:DistBlockMatrix(M,N)): DistBlockMatrix(this) {
        if (! likeMe(A))
            throw new UnsupportedOperationException("Distributed matrix not compatible for computing");
        finish ateach(p in Dist.makeUnique(places)) {
            val itr  = this.handleBS().iterator();
            val blkA = A.handleBS();
            while (itr.hasNext()) {
                val b1 = itr.next();
                val m1 = b1.getMatrix();
                val b2 = blkA.find(b1.myRowId, b1.myColId);
                if (b2 == null) throw new UnsupportedOperationException("Can not find corresponding block");
                m1.cellMult(b2.getMatrix() as Matrix(m1.M, m1.N));
            }
        }
        return this;
    }
        
    protected def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
        throw new UnsupportedOperationException("Matrix type mismatch");                            
    }

    public def cellDiv(A:Matrix(M,N)):Matrix(this) {
        if (! likeMe(A))
            throw new UnsupportedOperationException("Distributed matrix not compatible for computing");
        return cellDiv(A as DistBlockMatrix(M,N));
    }
    
    public def cellDiv(A:DistBlockMatrix(M,N)):DistBlockMatrix(this) {
        if (! likeMe(A))
            throw new UnsupportedOperationException("Distributed matrix not compatible for computing");
        
        finish ateach(p in Dist.makeUnique(places)) {
            val blkit1 = this.handleBS().iterator();
            val blkit2 = A.handleBS().iterator();
            while (blkit1.hasNext()&&blkit2.hasNext()) {
                val m1 = blkit1.next().getMatrix();
                val m2 = blkit2.next().getMatrix();
                m1.cellDiv(m2 as Matrix(m1.M, m1.N));
            }
        }
        return this;
    }
    
    protected def cellDivBy(x:DenseMatrix(M,N)):DenseMatrix(x) {
        throw new UnsupportedOperationException();
    }
    
    //TODO: DistDupMult should allow arbitrary place groups
    public def mult(A:DistBlockMatrix(this.M),B:DupBlockMatrix(A.N,this.N), plus:Boolean):DistBlockMatrix(this) =
        DistDupMult.comp(A, B, this, plus);

    public def transMult(A:DistBlockMatrix{self.N==this.M},B:DupBlockMatrix(A.M,this.N),plus:Boolean):DistBlockMatrix(this) =
        DistDupMult.compTransMult(A, B, this, plus);
    
    public def multTrans(A:DistBlockMatrix(this.M),B:DupBlockMatrix(this.N, A.N),plus:Boolean):DistBlockMatrix(this) =
        DistDupMult.compMultTrans(A, B, this, plus);

    //---- simplified version, no self plus
    public def mult(A:DistBlockMatrix(this.M),B:DupBlockMatrix(A.N,this.N)):DistBlockMatrix(this) =
        DistDupMult.comp(A, B, this, false);

    public def transMult(A:DistBlockMatrix{self.N==this.M},B:DupBlockMatrix(A.M,this.N)):DistBlockMatrix(this) =
        DistDupMult.compTransMult(A, B, this, false);
    
    public def multTrans(A:DistBlockMatrix(this.M),B:DupBlockMatrix(this.N, A.N)):DistBlockMatrix(this) =
        DistDupMult.compMultTrans(A, B, this, false);

    

    public def mult(A:DupBlockMatrix(this.M),B:DistBlockMatrix(A.N,this.N), plus:Boolean):DistBlockMatrix(this) =
        DistDupMult.comp(A, B, this, plus);

    public def transMult(A:DupBlockMatrix{self.N==this.M},B:DistBlockMatrix(A.M,this.N),plus:Boolean):DistBlockMatrix(this) =
        DistDupMult.compTransMult(A, B, this, plus);
    
    public def multTrans(A:DupBlockMatrix(this.M),B:DistBlockMatrix(this.N, A.N),plus:Boolean):DistBlockMatrix(this) =
        DistDupMult.compMultTrans(A, B, this, plus);
    
    //---
    public def mult(A:DupBlockMatrix(this.M),B:DistBlockMatrix(A.N,this.N)):DistBlockMatrix(this) =
        DistDupMult.comp(A, B, this, false);

    public def transMult(A:DupBlockMatrix{self.N==this.M},B:DistBlockMatrix(A.M,this.N)):DistBlockMatrix(this) =
        DistDupMult.compTransMult(A, B, this, false);
    
    public def multTrans(A:DupBlockMatrix(this.M),B:DistBlockMatrix(this.N, A.N)):DistBlockMatrix(this) =
        DistDupMult.compMultTrans(A, B, this, false);
    

    public def mult(A:Matrix(this.M),B:Matrix(A.N,this.N), plus:Boolean):Matrix(this) {
        throw new UnsupportedOperationException();    
    }
    
    public def transMult(A:Matrix{self.N==this.M},B:Matrix(A.M,this.N),plus:Boolean):Matrix(this){
        throw new UnsupportedOperationException();            
    }
    
    public def multTrans(A:Matrix(this.M),B:Matrix(this.N, A.N),plus:Boolean):Matrix(this) {
        throw new UnsupportedOperationException();                    
    }
    

    // Operator overload

    
    public operator this + (dv:ElemType) = this.clone().cellAdd(dv) as DistBlockMatrix(M,N);
    public operator this - (dv:ElemType) = this.clone().cellAdd(-dv) as DistBlockMatrix(M,N);
    public operator (dv:ElemType) + this = this.clone().cellAdd(dv) as DistBlockMatrix(M,N);
    
    public operator this + (that:DistBlockMatrix(M,N)) = this.clone().cellAdd(that) as DistBlockMatrix(M,N);
    public operator this - (that:DistBlockMatrix(M,N)) = this.clone().cellSub(that) as DistBlockMatrix(M,N);
    public operator this * (that:DistBlockMatrix(M,N)) = this.clone().cellMult(that) as DistBlockMatrix(M,N);    
    public operator this / (that:DistBlockMatrix(M,N)) = this.clone().cellDiv(that) as DistBlockMatrix(M,N);
    

    // Util

    
    /**
     * Build block map in all places to allow fast access local block given block row and column id.
     * Do not call this method, if distribution of blocks is not grid-like.
     */
    public def buildBlockMap() {
        finish ateach(p in Dist.makeUnique(places)) {
            handleBS().buildBlockMap();
        }
    }
    
    public def getAllDataCount():Long {
        var tt:Long = 0;
        for (var p:Long=0; p<places.size(); p++) {
            val ds = at(places(p)) handleBS().getAllBlocksDataCount();
            tt += ds;
        }
        return tt;
    }
    
    public def getTotalNonZeroCount() = getAllDataCount();

    /**
     * Works correctly only when DistGrid is used to distributed blocks.
     * It returs array of integers. Each value is the total number of rows in the place of its indexing value.
     * This method is used to create a DistVector corresponding to the rows of this DistBlockMatrix instance, 
     * while DistVector dose not use blocking, meaning each place is assigned with only one vector segment which
     * is same as the total rows of the block set of DistBlockMatrix in that place.
     */
    public def getAggRowBs():Rail[Long] = gdist.getAggRowBs(getGrid());
    
    /**
     * Returns array of integers. Each value is the total number of columns in the place of its indexing value.
     * This method is used to create a DistVector corresponding to the columns of this DistBlockMatrix instance.
     */
    public def getAggColBs():Rail[Long] = gdist.getAggColBs(getGrid());

    public def isDistHorizontal():Boolean {
        if (gdist != null) {
            return (gdist.numRowPlaces==1L);
        } else {
            return DistGrid.isHorizontal(getGrid(), getMap());
        }
    }

    public def isDistVertical():Boolean {
        if (gdist != null) {
            return (gdist.numColPlaces == 1L);
        } else {
            return DistGrid.isVertical(getGrid(), getMap());
        }
    }

    public def likeMe(A:Matrix):Boolean {
        if (A instanceof DistBlockMatrix) {
            val srcBs = this.handleBS();
            val dstBs = (A as DistBlockMatrix).handleBS();
            
            return ((dstBs.grid.equals(srcBs.grid)) &&
                    (dstBs.dmap.equals(srcBs.dmap)));
        }
        return false;
    }

    public def localSync() {
        finish ateach(p in Dist.makeUnique(places)) {
            val bset = handleBS();
            bset.sync(bset.getFirst());
        }
    }

    public def getCalcTime() = calcTime;
    public def getCommTime() = commTime;
    public def getPlaces() = places;

    /**
     * Check all blocks are same or not
     */
    public def checkAllBlocksEqual() : Boolean {
        val rtmat:Matrix = handleBS().getFirst().getMatrix();
        var retval:Boolean = true;
        for (var p:Long=0 ; p<places.size() && retval; p++) {
            if (here.id() != places(p).id) {
                retval &= at(places(p)) {
                    //Remote capture: rtmat
                    handleBS().allEqual(rtmat)                
                };
            } else {
                retval &= handleBS().allEqual(rtmat);
            }
            
            if (!retval) 
                Console.OUT.println("Integrity check failed at place "+p);
        }
        return retval;
    }
    
    public def getTotalDataSize():Long {
        var dsz:Long=0;
        for (p in places) {
            val c = at(p) { handleBS().getAllBlocksDataCount()};
            dsz += c;
        }
        return dsz;
    }
    
    public def toString():String {
        val output = new StringBuilder();
        output.add("-------- Dist Matrix Block size:["+M+" x "+N+"] ---------\n");
        for (p in places) {
            output.add(at(p) { handleBS().toString() });
        }
        output.add("--------------------------------------------------\n");
        return output.toString();
    }
    
    public def printLoadStatistics(){
        for(p in places) {            
            at (p){                
                val blks   = handleBS();
                val count = blks.getStorageSize();                
                Console.OUT.println("Place["+here.id+"] DataCount["+count+"] ...");
            }
        }
    }
    
    /*
     * Snapshot mechanism
     */
    /**
     * Remake the DistBlockMatrix over a new PlaceGroup.
     * Remake does not allocate the new blocks' storage, so allocSparseBlocks(ElemType) or 
     * allocDenseBlocks() should be called after calling this remake method.
     *  
     * @param rowPs, colPs      the number of rows and columns for the place grid
     * @param newPg             the new place group to distribute the matrix over
     */
    public def remake(rowPs:Long, colPs:Long, newPg:PlaceGroup) {
        assert (rowPs*colPs==newPg.size()) :
            "Block partitioning error: rowsPs["+rowPs+"]*colPs["+colPs+"] != newPg.size["+places.size()+"]";        
        val oldGrid = getGrid();
        val blks:PlaceLocalHandle[BlockSet];
        PlaceLocalHandle.destroy(places, handleBS, (Place)=>true);

        var useOldGrid:Boolean = false;
        if (places.size() == newPg.size() || rebalanceMode == null || rebalanceMode.equals("0"))
            useOldGrid = true; // no matrix grid rebalancing

        if (!useOldGrid){                        
            val rowBs:Long = oldGrid.numRowBlocks; 
            val colBs:Long = oldGrid.numColBlocks;            
            blks = PlaceLocalHandle.make[BlockSet](newPg, ()=>(BlockSet.make(M,N,rowBs,colBs,rowPs,colPs, newPg)));
        } else {
            blks = PlaceLocalHandle.make[BlockSet](newPg, ()=>(BlockSet.make(oldGrid,rowPs,colPs, newPg)));
        }
        
        gdist = new DistGrid(blks().getGrid(), rowPs, colPs);
        handleBS  = blks;
        places = newPg;
    }
    
     /**
     * Remake the DistBlockMatrix over a new PlaceGroup using Sparse Blocks.
     * rowPlaces and colPlaces will be calculated based on the provided PlaceGroup size
     * @parm  nzd              the non-zero density of the sparse blocks. Used only if X10_GML_REBALANCE=0.
     * @param newPg            the new place group to distribute the matrix over
     */
    public def remakeSparse(nzd:Float, newPg:PlaceGroup) {
        var colPs:Long = MathTool.sqrt(newPg.size());
        val rowPs = newPg.size() / colPs;
        remakeSparse(rowPs, colPs, nzd, newPg);
    }
    
    /**
     * Remake the DistBlockMatrix over a new PlaceGroup using Sparse Blocks. 
     * The environment variable X10_GML_REBALANCE will be checked to decide whether to use the same data 
     * grid for remake or to calculate a new grid based on the new place group (newPg) size.
     * After calling the remakeSparseCheckRebalanceMode(), the restoreSnapshot() should be called 
     * before using the matrix in any computation.
     * If X10_GML_REBALANCE=0, the storage of the sparse blocks will be allocated based on the provided 
     *                         non-zero density parameter (nzd)
     * If X10_GML_REBALANCE=1, allocating the storage will be deferred to the restore time because the actual 
     *                         number of elements that will be placed in each block can not be determined by remake.
     *                         That is why restoreSnapshot() should be called after remakeSparseCheckRebalanceMode().
     * 
     * @param rowPs, colPs     the number of rows and columns for the place grid
     * @parm  nzd              the non-zero density of the sparse blocks. Used only if X10_GML_REBALANCE=0.
     * @param newPg            the new place group to distribute the matrix over
     */
    public def remakeSparse(rowPs:Long, colPs:Long, nzd:Float, newPg:PlaceGroup) {
        remake(rowPs, colPs, newPg); 
        if (places.size() == newPg.size() || rebalanceMode == null || rebalanceMode.equals("0")) {
            // no matrix grid rebalancing
            allocSparseBlocks(nzd);
        }
        //else: allocation is deferred to the restore phase
    }

    /**
     * Remake the DistBlockMatrix over a new PlaceGroup, and allocate the sparse blocks storage.
     * The storage of the sparse blocks are allocated using the provided non-zero density parameter (nzd).
     * 
     * If restoreSnapshot is called after calling this method, and if the provided data grid (g) is different from the snapshotGrid, 
     * the restoreSnapshot method will deallocate the blocks and reallocate them using the allocAndInitNonUniformSparseBlocks method.
     * 
     * @param g             the data grid
     * @parm  d             the mapping between blocks of the data grid and the places 
     * @param nzd           the non-zero density of the sparse blocks.
     * @param newPg         the new place group to distribute the matrix over
     */
    public def remakeSparse(g:Grid, d:DistMap, nzd:Float, newPg:PlaceGroup) {        
        PlaceLocalHandle.destroy(places, handleBS, (Place)=>true);
        handleBS = PlaceLocalHandle.make[BlockSet](newPg, ()=>(BlockSet.makeSparse(g, d, nzd, newPg)));
        places = newPg;
    }

    /**
     * Remake the DistBlockMatrix over a new PlaceGroup, and allocate the dense blocks storage.
     * rowPlaces and colPlaces will be calculated based on the provided PlaceGroup size
     * @param newPg            the new place group to distribute the matrix over
     */
    public def remakeDense(newPg:PlaceGroup) {
        val colPs:Long = MathTool.sqrt(newPg.size());
        val rowPs = newPg.size() / colPs;
        remakeDense(rowPs, colPs, newPg);
    }
    
    /**
     * Remake the DistBlockMatrix over a new PlaceGroup, and allocate the dense blocks storage. 
     * @param rowPs, colPs     the number of rows and columns for the place grid
     * @param newPg            the new place group to distribute the matrix over
     */
    public def remakeDense(rowPs:Long, colPs:Long, newPg:PlaceGroup){
        remake(rowPs, colPs, newPg);
        allocDenseBlocks();
    }

    /**
     * Remake the DistBlockMatrix over a new PlaceGroup, and allocate the dense blocks storage. 
     * @param g             the data grid
     * @parm  d             the mapping between blocks of the data grid and the places 
     * @param newPg         the new place group to distribute the matrix over
     */
    public def remakeDense(g:Grid, d:DistMap, newPg:PlaceGroup) {
        PlaceLocalHandle.destroy(places, handleBS, (Place)=>true);   
        handleBS = PlaceLocalHandle.make[BlockSet](newPg, ()=>(BlockSet.makeDense(g, d, newPg)));
        places = newPg;
    }
    
    /**
     * Create a snapshot for the matrix data 
     * @return a snapshot for the matrix data stored in a resilient store
     */
    public def makeSnapshot():DistObjectSnapshot {
        val snapshot = DistObjectSnapshot.make();
        val blockList = handleBS().blocklist;
        val isSparse = blockList.get(0).isSparse();
        
        if (isSparse) {
            finish ateach(pl in Dist.makeUnique(places)) {
                val i = places.indexOf(here);
                val data = handleBS();
                val blocksCount = data.blocklist.size();
                val metadata = data.getBlocksMetaData();
                val totalSize = data.getStorageSize();
                val allIndex = new Rail[Long](totalSize);
                val allValue = new Rail[ElemType](totalSize);
                
                data.initSparseBlocksRemoteCopyAtSource();
                data.flattenIndex(allIndex);
                data.flattenValue(allValue);
                data.finalizeSparseBlocksRemoteCopyAtSource();
                
                val blockSetInfo = new BlockSetSnapshotInfo(i, isSparse);
                blockSetInfo.initSparse(blocksCount, metadata, allIndex, allValue);
                snapshot.save(i, blockSetInfo);
            }
        } else {
            /* Disable flattening the dense blocks
            finish ateach(pl in Dist.makeUnique(places)) {
                val i = places.indexOf(here);
                val data = handleBS();
                val blocksCount = data.blocklist.size();
                val metadata = data.getBlocksMetaData();
                val totalSize = data.getStorageSize();
                val allValue = new Rail[ElemType](totalSize);
                data.flattenValue(allValue);
                
                val blockSetInfo = new BlockSetSnapshotInfo(i, isSparse);
                blockSetInfo.initDense(blocksCount, metadata, allValue);
                snapshot.save(i, blockSetInfo);
            }
            */
            finish ateach(pl in Dist.makeUnique(places)) {
                val i = places.indexOf(here);
                val data = handleBS();
                val blockSetInfo = new BlockSetSnapshotInfo(i, isSparse);
                blockSetInfo.setBlockSet(data);
                snapshot.save(i, blockSetInfo);
            }
        }
        snapshotGrid = getGrid();
        snapshotMap = getMap();
        return snapshot;
    }
    
    /**
     * Restore the matrix data using the provided snapshot object 
     * @param snapshot a snapshot from which to restore the data
     */
    public def restoreSnapshot(snapshot:DistObjectSnapshot) {
        val oldGrid = snapshotGrid;
        val newGrid = getGrid();
        if (!oldGrid.equals(newGrid)) {
            if (isDistVertical())
                restoreSnapshotSubBlocksVerticalDist(snapshot);
            else
                restoreSnapshotElementByElement(snapshot);
        }
        else
            restoreSnapshotBlockByBlock(snapshot);
    }
    
    /**
     * Restore the matrix element-by-element using the provided snapshot object 
     * @param snapshot a snapshot from which to restore the data
     */
    private def restoreSnapshotElementByElement(snapshot:DistObjectSnapshot) {
        val oldGrid = snapshotGrid;
        val oldDistMap = snapshotMap;        
        val cached = PlaceLocalHandle.make[HashMap[Long,BlockSetSnapshotInfo]](places, ()=>new HashMap[Long, BlockSetSnapshotInfo]());        
        val initFunc = (i:Long,j:Long)=> {
            /*
             * calculate saved place for a point (i,j) from oldPg
             */
            val blockId = oldGrid.findBlock(i,j);
            val loadPlaceIndex = oldDistMap.findPlaceIndex(blockId);
            /*
             * get (and cache) saved info for the place
             */
            val map = cached();
            var cachedBlockSetInfo:BlockSetSnapshotInfo = map.get(loadPlaceIndex);
            if (cachedBlockSetInfo == null){                
                cachedBlockSetInfo = snapshot.load(loadPlaceIndex) as BlockSetSnapshotInfo;
                map.put(loadPlaceIndex, cachedBlockSetInfo);
            }            
            val blockSet = cachedBlockSetInfo.getBlockSet();
            val foundValue = getDataValue(i,j,blockSet, oldGrid);
            return foundValue;
        };
        
        //non uniform sparse blocks are not allocated by remake
        val blockList = handleBS().blocklist;
        val isSparse = blockList.size() == 0 || blockList.get(0).isSparse();
        if (isSparse)
            allocAndInitNonUniformSparseBlocks(initFunc);
        else
            init(initFunc);
        PlaceLocalHandle.destroy(places, cached, (Place)=>true);
    }

    /**
     * Restore the matrix block-by-block using the provided snapshot object 
     * @param snapshot a snapshot from which to restore the data
     */
    private def restoreSnapshotBlockByBlock(snapshot:DistObjectSnapshot) {
        val startTime = Timer.milliTime();
        val oldGrid = snapshotGrid;
        val oldDistMap = snapshotMap;
        val newGrid = getGrid();
        finish ateach(p in Dist.makeUnique(places)) {
            var copyToTime:Long = 0;
            var loadingTime:Long=0;
            /*
             * calculate the required blocks from each old place
             */
            val newBlockSet = handleBS();
            val placeBlockMap = new HashMap[Long, ArrayList[MatrixBlock]]();
            val blkitr = newBlockSet.iterator();
            while (blkitr.hasNext()) {
                val newBlock = blkitr.next();
                val blockRowId = newBlock.myRowId;
                val blockColId = newBlock.myColId;
                //assuming that the block row id and col id will remain the same after a failure
                val blockId = oldGrid.getBlockId(blockRowId, blockColId);
                val oldPlaceIndex = oldDistMap.findPlaceIndex(blockId);
        
                var list:ArrayList[MatrixBlock] = placeBlockMap.getOrElse(oldPlaceIndex,null);
                if (list == null){
                    list = new ArrayList[MatrixBlock]();
                    placeBlockMap.put(oldPlaceIndex,list);
                }
                list.add(newBlock);
            }
            /*
             * Block by block restore
             */
            val placesIter = placeBlockMap.keySet().iterator();
            while (placesIter.hasNext()) {
                val oldPlaceIndex = placesIter.next();
                val oldBlockSet = (snapshot.load(oldPlaceIndex) as BlockSetSnapshotInfo).getBlockSet();
                val blocksList = placeBlockMap.get(oldPlaceIndex);
                for (newBlock in blocksList){
                    val blockRowId = newBlock.myRowId;
                    val blockColId = newBlock.myColId;
                    val oldBlock = oldBlockSet.find(blockRowId, blockColId);
                    oldBlock.copyTo(newBlock);
                }
            }
        }
        //Console.OUT.println("DistBlockMatrix.RestoreTime["+(Timer.milliTime() - startTime)+"]");
    }

    private def restoreSnapshotSubBlocksVerticalDist(snapshot:DistObjectSnapshot) {
        val oldGrid = snapshotGrid;
        val oldMap = snapshotMap;
        val newGrid = getGrid();

        val snapshotSegSize = oldGrid.rowBs;
        val newSegSize = newGrid.rowBs;

        val newSegmentsOffsets = new Rail[Long](newSegSize.size);
        newSegmentsOffsets(0) = 0;
        for (var i:Long = 1; i < newSegSize.size; i++) {
            for (var j:Long = 0; j < i; j++) {
                newSegmentsOffsets(i) += newSegSize(j);
            }
        }

        val oldSegmentsOffsets = new Rail[Long](snapshotSegSize.size);
        oldSegmentsOffsets(0) = 0;
        for (var i:Long = 1; i < snapshotSegSize.size; i++) {
            for (var j:Long = 0; j < i; j++) {
                oldSegmentsOffsets(i) += snapshotSegSize(j);
            }
        }
        val blockList = handleBS().blocklist;
        val isSparse = blockList.size() == 0 || blockList.get(0).isSparse();

        val cached = PlaceLocalHandle.make[HashMap[Long,BlockSetSnapshotInfo]](places, ()=>new HashMap[Long, BlockSetSnapshotInfo]());
        finish ateach(p in Dist.makeUnique(places)) {
            val newPlaceIndex:Long = places.indexOf(here);

            val blks = handleBS();
            val blkitr = blks.iterator();
            var currentBlockIndex:Long = -1;

            val tempBlocklist:ArrayList[MatrixBlock] = new ArrayList[MatrixBlock]();
            while (blkitr.hasNext()) {
                val newBlk = blkitr.next();
                if (currentBlockIndex == -1) {
                    currentBlockIndex = newGrid.getBlockId(newBlk.myRowId, newBlk.myColId);
                } else {
                    currentBlockIndex++;
                }

                val low = newBlk.rowOffset;
                val high = low + newSegSize(currentBlockIndex);

                var dstRowOffset:Long = 0;
                for (var oldBlockId:Long = 0; oldBlockId < snapshotSegSize.size; oldBlockId++) {
                    val low_old = oldSegmentsOffsets(oldBlockId);
                    val high_old = low_old + snapshotSegSize(oldBlockId);

                    var overlapFound:Boolean = false;
                    if (high_old > low && low_old <high) {
                        //calculate the overlapping interval
                        var startRow:Long = low;  //absolute row number
                        var endRow:Long = high;   //absolute row number
                        if (low_old > low)
                            startRow = low_old;
                        if (high_old < high)
                            endRow = high_old;

                        val rowsCount = endRow - startRow;
                        //load the old segment from resilient store
                        val oldPlaceIndex = oldMap.findPlaceIndex(oldBlockId);
                        val map = cached();
                        var cachedBlockSetInfo:BlockSetSnapshotInfo = map.get(oldPlaceIndex);
                        if (cachedBlockSetInfo == null) {
                            cachedBlockSetInfo = snapshot.load(oldPlaceIndex) as BlockSetSnapshotInfo;
                            map.put(oldPlaceIndex, cachedBlockSetInfo);
                        }
                        val blockSet = cachedBlockSetInfo.getBlockSet();
                        val rid = oldGrid.getRowBlockId(oldBlockId);
                        val cid = oldGrid.getColBlockId(oldBlockId);
                        val oldBlk = blockSet.find(rid, cid);

                        val oldBlockMatrix = oldBlk.getMatrix();
                        val newBlockMatrix = newBlk.getMatrix();
                        val srcColOffset = 0 ;
                        val dstColOffset = 0 ;
                        val colCnt = N;
                        var srcRowOffset:Long = 0;
                        if (low_old < low)
                            srcRowOffset = low - low_old;
                        if (!isSparse) {
                            DenseMatrix.copySubset((oldBlockMatrix as DenseMatrix), srcRowOffset, srcColOffset, (newBlockMatrix as DenseMatrix), dstRowOffset, dstColOffset, rowsCount, colCnt);
                        } else {
                            val sparseBuilder = newBlk.getBuilder() as SparseCSCBuilder;
                            val oldMatColumns = (oldBlockMatrix as SparseCSC).ccdata.cLine;
                            val cArray = (oldBlockMatrix as SparseCSC).ccdata.cLine(0).cArray;
                            for (var m:Long = 0; m <colCnt ; m++) {
                                val curCol = oldMatColumns(m);
                                val cArrayOffset =  curCol.offset;
                                val cArrayLength = curCol.length;
                                
                                if (cArrayLength == 0)  continue;
                                
                                for (var l:Long = 0; l < cArrayLength ; l++) {
                                    val rowIndex = cArray.index(cArrayOffset+l);
                                    if (rowIndex >= srcRowOffset && rowIndex < srcRowOffset+rowsCount) {
                                        val newRowId = rowIndex - srcRowOffset + dstRowOffset;
                                        val newColId = m;
                                        val rowValue = cArray.value(cArrayOffset+l) as ElemType;
                                        sparseBuilder.append(newRowId, newColId, rowValue);
                                    }
                                }
                            }
                        }
                        dstRowOffset+= rowsCount;
                        overlapFound = true;
                    } else if (overlapFound) {
                        break; // no more overlapping segments exist
                    }
                }
                if (isSparse)
                    tempBlocklist.add(new SparseBlock(newBlk.myRowId, newBlk.myColId, newBlk.rowOffset, newBlk.colOffset, (newBlk.getBuilder() as SparseCSCBuilder).toSparseCSC()));
            }
            if (isSparse) {
                handleBS().clear();
                handleBS().blocklist.addAll(tempBlocklist);
            }
        }
        PlaceLocalHandle.destroy(places, cached, (Place)=>true);
    }

}

