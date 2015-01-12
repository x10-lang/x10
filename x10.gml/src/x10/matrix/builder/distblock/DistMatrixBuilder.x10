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

package x10.matrix.builder.distblock;

import x10.regionarray.Dist;

import x10.matrix.Matrix;
import x10.matrix.util.RandTool;

import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.BlockSet;
import x10.matrix.builder.MatrixBuilder;

public type DistMatrixBuilder(b:DistMatrixBuilder)=DistMatrixBuilder{self==b};
public type DistMatrixBuilder(m:Long,n:Long)=DistMatrixBuilder{self.M==m,self.N==n};

public class DistMatrixBuilder(M:Long,N:Long) implements MatrixBuilder {
    public val dmat:DistBlockMatrix(M,N);

    /**
     * Create distributed block matrix and using specified to store output
     * @param  dm    Distributed block matrix to store the output
     */
    public def this(dm:DistBlockMatrix) {
        property(dm.M,dm.N);
        dmat = dm;
    }
    
    /**
     * Create distributed block matrix builder with given partitioning and block distribution map. 
     * The actual memory spaces are not allocated.
     */
    public static def make(pg:Grid, dp:DistMap):DistMatrixBuilder(pg.M,pg.N) =
        make(pg, dp, Place.places());
    
    public static def make(pg:Grid, dp:DistMap, places:PlaceGroup):DistMatrixBuilder(pg.M,pg.N) {
        //Remote capture: partitioning and distribution
        val dm = DistBlockMatrix.make(pg, dp, places);
        val bld =  new DistMatrixBuilder(dm);
        return bld as DistMatrixBuilder(pg.M,pg.N);
    }
    
    /**
     * Create symmetric distributed block matrix with given leading dimension and its partitioning blocks.
     */
    public static def make(m:Long, n:Long, bM:Long, bN:Long):DistMatrixBuilder(m,n) =
        make(m, n, bM, bN, Place.places());
    
    public static def make(m:Long, n:Long, bM:Long, bN:Long, places:PlaceGroup):DistMatrixBuilder(m,n) {
        val grid = new Grid(m, n, bM, bN);
        val dgrid = DistGrid.make(grid, places.size());
        val bdr = make(grid, dgrid.dmap, places);
        return bdr as DistMatrixBuilder(m,n);
    }
    
    public def allocAllDenseBlocks(): DistMatrixBuilder(this) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            dmat.handleBS().allocDenseBlocks();
        }
        return this;
    }
    
    public def allocAllSparseBlocks(nzd:Double): DistMatrixBuilder(this) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            dmat.handleBS().allocSparseBlocks(nzd);
        }
        return this;
    }

    public def init(initFun:(Long,Long)=>Double) : DistMatrixBuilder(this) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            val itr = dmat.handleBS().iterator();
            while (itr.hasNext()) {
                itr.next().init(initFun);
            }
        }
        return this;
    }
        
    public def initRandom(nonZeroDensity:Double):DistMatrixBuilder(this) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            val itr = dmat.handleBS().iterator();
            while (itr.hasNext()) {
                itr.next().initRandom(nonZeroDensity, (Long,Long)=>RandTool.getRandGen().nextDouble());
            }
        }
        return this;
    }
    
    public def initRandom(nzDensity:Double, initFun:(Long,Long)=>Double) : DistMatrixBuilder(this) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            val itr = dmat.handleBS().iterator();
            while (itr.hasNext()) {
                itr.next().initRandom(nzDensity, initFun);
            }
        }
        return this;
    }

    public def initRandom() : DistMatrixBuilder(this) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            val itr = dmat.handleBS().iterator();
            while (itr.hasNext()) {
                itr.next().initRandom();
            }
        }
        return this;
    }

    public def set(r:Long, c:Long, value:Double): void{
        val grid = dmat.handleBS().getGrid();
        val loc = grid.find(r, c);
        val bid = grid.getBlockId(loc(0), loc(1));
        val bx  = loc(2);
        val by  = loc(3);
        val pIndex = dmat.handleBS().getDistMap().findPlaceIndex(bid);
        //Remote capture: bid, bx, by, 
        at(dmat.getPlaces()(pIndex)) {
            val blkset:BlockSet = dmat.handleBS();
            val blk:MatrixBlock = blkset.find(bid);
            if (blk == null) 
                throw new UnsupportedOperationException("Error in search block in block set");
            
            blk.getBuilder().set(bx, by, value);
        }
    }
    
    public def reset(r:Long, c:Long):Boolean {
        val grid = dmat.handleBS().getGrid();
        val loc = grid.find(r, c);
        val bid = grid.getBlockId(loc(0), loc(1));
        val bx  = loc(2);
        val by  = loc(3);
        val pIndex = dmat.handleBS().getDistMap().findPlaceIndex(bid);
        //Remote capture: bid, bx, by, 
        val ret = at(dmat.getPlaces()(pIndex)) {
            val blkset:BlockSet = dmat.handleBS();
            val blk:MatrixBlock = blkset.find(bid);
            if (blk == null) 
                throw new UnsupportedOperationException("Error in searching block in block set");
            
            blk.getBuilder().reset(bx, by)
        };
        return ret;
    }
    
    public def toDistBlockMatrix():DistBlockMatrix(M,N) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            val itr = dmat.handleBS().iterator();
            while (itr.hasNext()) {
                val blk = itr.next();
                val bdr = blk.getBuilder();
                //if (blk instanceof SparseBlock)
                bdr.toMatrix();    //Output is written back dense/sparse
            }
        }
        return dmat;
    }
    
    public def toMatrix():Matrix(M,N) = 
        toDistBlockMatrix() as Matrix(M,N);
}
