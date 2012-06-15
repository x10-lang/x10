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

package x10.matrix.distblock;

import x10.compiler.Inline;
import x10.io.Console;
import x10.util.Timer;
import x10.util.StringBuilder;

import x10.matrix.Matrix;
import x10.matrix.MatrixBuilder;
import x10.matrix.DenseMatrix;
import x10.matrix.Debug;
import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.SymGrid;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.comm.BlockSetRemoteCopy;

public type DistMatrixBuilder(b:DistMatrixBuilder)=DistMatrixBuilder{self==b};
public type DistMatrixBuilder(m:Int,n:Int)=DistMatrixBuilder{self.M==m,self.N==n};

/*
 * 
 */
public class DistMatrixBuilder(M:Int,N:Int) implements MatrixBuilder {
 
	public val dmat:DistBlockMatrix(M,N);

	//-------------------------------------
	public def this(dm:DistBlockMatrix) {
		property(dm.M,dm.N);
		dmat = dm;
	}
	
	//=====================================
	/**
	 * Creat distributed block matrix builder with given partitioning and block distribution map. 
	 * The actual memory spaces are not allocated.
	 */
	public static def make(pg:Grid, dp:DistMap):DistMatrixBuilder(pg.M,pg.N) {
		//Remote capture: partitioning and distribution
		val dm = DistBlockMatrix.make(pg, dp);
		val bld =  new DistMatrixBuilder(dm);
		return bld as DistMatrixBuilder(pg.M,pg.N);
	}
	
	/**
	 * Creat symmetric distributed block matrix with given leading dimension and its partitioning blocks.
	 */
	public static def makeSym(m:Int, bM:Int):DistMatrixBuilder(m,m) {
		val sgrid = new SymGrid(m, bM);
		val dgrid = DistGrid.make(sgrid as Grid);
		val bdr = make(sgrid, dgrid.dmap);
		return bdr as DistMatrixBuilder(m,m);
	}
	
	//===============================
	public def allocDense(): DistMatrixBuilder(this) {
		finish ateach (d:Point in Dist.makeUnique()) {
			dmat.handleBS().allocDenseBlocks();
		}
		return this;
	}
	
	public def allocSparse(nzd:Double): DistMatrixBuilder(this) {
		finish ateach (d:Point in Dist.makeUnique()) {
			dmat.handleBS().allocSparseBlocks(nzd);
		}
		return this;
	}

	//======================
	public def init(initFun:(Int,Int)=>Double) : DistMatrixBuilder(this) {
		finish ateach (d:Point in Dist.makeUnique()) {
			val itr = dmat.handleBS().iterator();
			val pgrid = dmat.handleBS().getGrid();
			while (itr.hasNext()) {
				val blk = itr.next();
				blk.init(initFun);
			}
		}
		return this;
	}
	
	public def initRandom(nonZeroDensity:Double):DistMatrixBuilder(this) {
		finish ateach (d:Point in Dist.makeUnique()) {
			val itr = dmat.handleBS().iterator();
			while (itr.hasNext()) {
				itr.next().initRandom(nonZeroDensity);
			}
		}
		return this;
	}
	//==============================
	public def initRandom() : DistMatrixBuilder(this) {
		finish ateach (d:Point in Dist.makeUnique()) {
			val itr = dmat.handleBS().iterator();
			while (itr.hasNext()) {
				itr.next().initRandom();
			}
		}
		return this;
	}

	public def initRandomSym(halfDensity:Double) : DistMatrixBuilder(this) {
		finish ateach (d:Point in Dist.makeUnique()) {
			val itr = dmat.handleBS().iterator();
			while (itr.hasNext()) {
				val blk = itr.next();
				//Only init the lower part
				if (blk.myRowId > blk.myColId)
					blk.initRandom(halfDensity);
				else if (blk.myRowId == blk.myColId) {
					blk.initRandomSym(halfDensity);
				}
			}
		}
		mirror(true);
		return this;
	}


	public def initRandomTri(halfNZD:Double, up:Boolean) : DistMatrixBuilder(this){
		finish ateach (d:Point in Dist.makeUnique()) {
			val itr = dmat.handleBS().iterator();
			while (itr.hasNext()) {
				val blk = itr.next();
				//Only init the lower part
				if (blk.myRowId > blk.myColId)
					blk.initRandom(halfNZD);
				else if (blk.myRowId == blk.myColId) {
					blk.initRandomTri(halfNZD, up);
				}
			}
		}
		return this;
	}
	//=========================
	public def mirror(toUpper:Boolean) {
		finish ateach (d:Point in Dist.makeUnique()) {
			val blkitr = dmat.handleBS().iterator();
			while (blkitr.hasNext()) {
				val blk = blkitr.next();
				//Only init the lower part
				if (toUpper) { 
					if (blk.myRowId >= blk.myColId) continue; //source
				} else {
					if (blk.myRowId <= blk.myColId) continue;
				}
				//copy remote block to dstblk at here
				val srcbid = dmat.handleBS().getGrid().getBlockId(blk.myColId, blk.myRowId);
				val dstmat:Matrix = blk.getMatrix();
			
				if (dstmat instanceof DenseMatrix) {
					if (dstmat.M==dstmat.N) {
						val dst = dstmat as DenseMatrix;
						BlockSetRemoteCopy.copy(dmat.handleBS, srcbid, dst);
						dst.selfT();
					} else { 
						val rcvmat = DenseMatrix.make(dstmat.M,dstmat.N);
						BlockSetRemoteCopy.copy(dmat.handleBS, srcbid, rcvmat); 
						blk.transposeFrom(rcvmat);
					}
				} else if (dstmat instanceof SparseCSC) {
					val dst = dstmat as SparseCSC;
					BlockSetRemoteCopy.copy(dmat.handleBS, srcbid, dst); 
					dst.selfT();
				} else {
					throw new UnsupportedOperationException("Matrix type not supported in transpose");
				}
			}
		}
		return this;		
	}
	
	@Inline
	public def mirrorToUpper() {
		mirror(true);
	}
	@Inline	
	public def mirrorToLower() {
		mirror(false);
	}
	//=====================================
	public def set(r:Int, c:Int, value:Double): void{
		val grid = dmat.handleBS().grid;
		val loc = grid.find(r, c);
		val bid = grid.getBlockId(loc(0), loc(1));
		val bx  = loc(2);
		val by  = loc(3);
		val pid = dmat.handleBS().dmap.findPlace(bid);
		//Remote capture: bid, bx, by, 
		at (Place.place(pid)) {
			val blkset:BlockSet = dmat.handleBS();
			val blk:MatrixBlock = blkset.find(bid);
			if (blk == null) 
				Debug.exit("Error in search blocks in block set");
			
			blk.getBuilder().set(bx, by, value);
		}
	}
	
	public def reset(r:Int, c:Int):Boolean {
		val grid = dmat.handleBS().grid;
		val loc = grid.find(r, c);
		val bid = grid.getBlockId(loc(0), loc(1));
		val bx  = loc(2);
		val by  = loc(3);
		val pid = dmat.handleBS().dmap.findPlace(bid);
		//Remote capture: bid, bx, by, 
		val ret = at (Place.place(pid)) {
			val blkset:BlockSet = dmat.handleBS();
			val blk:MatrixBlock = blkset.find(bid);
			if (blk == null) 
				Debug.exit("Error in search blocks in block set");
			
			blk.getBuilder().reset(bx, by)
		};
		return ret;
	}
	
	//=====================================
	public static def checkSymmetric(mat:Matrix):Boolean {
		var ret:Boolean = true;
		for (var c:Int=0; c<mat.N&&ret; c++)
			for (var r:Int=c+1; r<mat.M&&ret; r++)
				ret &= (mat(r,c)==mat(c,r));
		return ret;
	}
	
	public def checkSymmetric():Boolean = checkSymmetric(this.dmat);
	//=====================================

	public def toDistBlockMatrix():DistBlockMatrix(M,N) = dmat;
		
	
	public def toMatrix():Matrix(M,N) = dmat as Matrix(M,N);
		

	
}