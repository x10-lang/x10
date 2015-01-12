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
import x10.compiler.Inline;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.block.SymGrid;
import x10.matrix.builder.MatrixBuilder;
import x10.matrix.builder.SymDenseBuilder;
import x10.matrix.comm.BlockSetRemoteCopy;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.util.RandTool;

public type DistSymMatrixBuilder(b:DistSymMatrixBuilder)=DistSymMatrixBuilder{self==b};
public type DistSymMatrixBuilder(m:Long)=DistSymMatrixBuilder{self.M==m,self.N==m};

public class DistSymMatrixBuilder extends DistMatrixBuilder{self.M==self.N} implements MatrixBuilder {
    public def this(dm:DistBlockMatrix{self.M==self.N}) {
        super(dm);
    }
    
    public def this(bld:DistMatrixBuilder{self.M==self.N}) {
        super(bld.dmat as DistBlockMatrix{self.M==self.N});
    }
    
    /**
     * Create distributed block matrix builder with given partitioning and block distribution map. 
     * The actual memory spaces are not allocated.
    */
    public static def make(pg:SymGrid, dp:DistMap):DistSymMatrixBuilder(pg.M) =
        make(pg, dp, Place.places());
    
    public static def make(pg:SymGrid, dp:DistMap, places:PlaceGroup):DistSymMatrixBuilder(pg.M) {
        //Remote capture: partitioning and distribution
        val bld = DistMatrixBuilder.make(pg as Grid, dp, places);
        val sbd = new DistSymMatrixBuilder(bld as DistMatrixBuilder{self.M==self.N});
        return sbd as DistSymMatrixBuilder(pg.M);
    }
    
    /**
     * Create symmetric distributed block matrix with given leading dimension and its partitioning blocks.
     */
    public static def make(m:Long, bM:Long):DistSymMatrixBuilder(m) = make(m, bM, Place.places());
    
    public static def make(m:Long, bM:Long, places:PlaceGroup):DistSymMatrixBuilder(m) {
        val sgrid = new SymGrid(m, bM);
        val dgrid = DistGrid.make(sgrid as Grid, places.size());
        val bdr = make(sgrid, dgrid.dmap, places);
        return bdr as DistSymMatrixBuilder(m);
    }

    /**
     * Initialize distributed symmetric matrix using symmetric initial function.
     * @param initFun   Matrix entry value generator function, mapping row-column to double. 
     */
    public def init(initFun:(Long,Long)=>Double) : DistSymMatrixBuilder(this) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            val itr = dmat.handleBS().iterator();
            val pgrid = dmat.handleBS().getGrid();
            while (itr.hasNext()) {
                val blk = itr.next();
                if (blk.myRowId != blk.myColId) {
                    val bdr = blk.getBuilder();
                    bdr.init(initFun);
                } else  {
                    val bdr = blk.getSymBuilder();
                    bdr.init(initFun);
                }
            }
        }
        return this;
    }

    /**
     * Used for testing purpose. Initialize distributed symmetric matrix in random locations with value generator function and
     * sparsity 
     * @param halfDensity       nonzero sparsity
     * @param f                 nonzero value generator function.
     */
    public def initRandom(nzDensity:Double, f:(Long,Long)=>Double) : DistSymMatrixBuilder(this) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            val itr = dmat.handleBS().iterator();
            while (itr.hasNext()) {
                val blk = itr.next();
                //Only init the lower triangular blocks
                if (blk.myRowId > blk.myColId) {
                    val bdr = blk.getBuilder();
                    bdr.initRandom(nzDensity, f);
                } else if (blk.myRowId == blk.myColId) {
                    val bdr = blk.getSymBuilder();
                    bdr.initRandom(nzDensity, f);
                    if (bdr instanceof SymDenseBuilder) {
                        (bdr as SymDenseBuilder).mirrorToUpper();
                    }
                }
            }
        }
        mirror(true);
        return this;
    }

    // replicated from superclass to workaround xlC bug with using & itables
    public def initRandom(nonZeroDensity:Double):DistMatrixBuilder(this) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            val itr = dmat.handleBS().iterator();
            while (itr.hasNext()) {
                itr.next().initRandom(nonZeroDensity, (Long,Long)=>RandTool.getRandGen().nextDouble());
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

    public def mirror(toUpper:Boolean) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            val blkitr = dmat.handleBS().iterator();
            while (blkitr.hasNext()) {
                val blk = blkitr.next();
                if (toUpper) { 
                    if (blk.myRowId >= blk.myColId) continue; //source
                } else {
                    if (blk.myRowId <= blk.myColId) continue;
                }
                //copy remote block to dstblk at here
                val srcbid = dmat.handleBS().getGrid().getBlockId(blk.myColId, blk.myRowId);
                val dstmat:Matrix = blk.getMatrix();
                if (dstmat instanceof DenseMatrix) {
                    val dst = dstmat as DenseMatrix;
                    if (dstmat.M==dstmat.N) {
                        BlockSetRemoteCopy.copy(dmat.handleBS, srcbid, dst);
                        dst.selfT();
                    } else { 
                        val rcvmat = DenseMatrix.make(dstmat.M,dstmat.N);
                        BlockSetRemoteCopy.copy(dmat.handleBS, srcbid, rcvmat); 
                        blk.transposeFrom(rcvmat);
                    }
                } else if (dstmat instanceof SparseCSC) {
                    val dst = dstmat as SparseCSC;
                    val rcvmat = SparseCSC.make(dst.N, dst.M, 1.0*dst.getStorageSize()/(dst.M*dst.N));
                    BlockSetRemoteCopy.copy(dmat.handleBS, srcbid, rcvmat);
                    blk.transposeFrom(rcvmat);
                    
                } else {
                    throw new UnsupportedOperationException("Matrix type not supported in transpose");
                }
            }
        }
        return this;        
    }
    
    public @Inline def mirrorToUpper() {
        mirror(true);
    }
    
    public @Inline def mirrorToLower() {
        mirror(false);
    }

    public def set(r:Long, c:Long, value:Double): void{
        super.set(r, c, value);
        if (r != c)
            super.set(c, r, value);
    }
    
    public def reset(r:Long, c:Long):Boolean {
        var ret:Boolean= super.reset(r, c);
        if (r != c)
            ret &= super.reset(c, r);
        return ret;
    }
    

    public static def checkSymmetric(mat:Matrix):Boolean {
        var ret:Boolean = true;
        for (var c:Long=0; c<mat.N&&ret; c++)
            for (var r:Long=c+1; r<mat.M&&ret; r++)
                ret &= (mat(r,c)==mat(c,r));
        return ret;
    }
    
    public def checkSymmetric():Boolean = checkSymmetric(this.dmat);

    //public def toDistBlockMatrix():DistBlockMatrix(M,N) = dmat;
    public def toDistBlockMatrix():DistBlockMatrix(M,N) {
        finish ateach(d in Dist.makeUnique(dmat.getPlaces())) {
            val itr = dmat.handleBS().iterator();
            while (itr.hasNext()) {
                val blk = itr.next();
                val bdr =blk.getBuilder();
                bdr.toMatrix();
            }
        }
        return dmat;
    }
    
    public def toMatrix():Matrix(M,N) = toDistBlockMatrix() as Matrix(M,N);
}
