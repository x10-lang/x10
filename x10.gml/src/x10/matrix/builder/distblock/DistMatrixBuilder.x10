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

package x10.matrix.builder.distblock;

import x10.compiler.Inline;
import x10.io.Console;
import x10.util.Timer;
import x10.util.StringBuilder;

import x10.matrix.Matrix;
import x10.matrix.RandTool;
import x10.matrix.DenseMatrix;
import x10.matrix.SymDense;

import x10.matrix.Debug;
import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.comm.BlockSetRemoteCopy;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.BlockSet;

import x10.matrix.builder.MatrixBuilder;

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
	public static def make(m:Int, n:Int, bM:Int, bN:Int):DistMatrixBuilder(m,n) {
		val grid = new Grid(m, n, bM, bN);
		val dgrid = DistGrid.make(grid);
		val bdr = make(grid, dgrid.dmap);
		return bdr as DistMatrixBuilder(m,n);
	}
	
	//===============================
	public def allocAllDenseBlocks(): DistMatrixBuilder(this) {
		finish ateach (d:Point in Dist.makeUnique()) {
			dmat.handleBS().allocDenseBlocks();
		}
		return this;
	}
	
	public def allocAllSparseBlocks(nzd:Double): DistMatrixBuilder(this) {
		finish ateach (d:Point in Dist.makeUnique()) {
			dmat.handleBS().allocSparseBlocks(nzd);
		}
		return this;
	}

	//======================
	public def init(initFun:(Int,Int)=>Double) : DistMatrixBuilder(this) {
		finish ateach (d:Point in Dist.makeUnique()) {
			val itr = dmat.handleBS().iterator();
			while (itr.hasNext()) {
				itr.next().init(initFun);
			}
		}
		return this;
	}
		
	public def initRandom(nonZeroDensity:Double):DistMatrixBuilder(this) {
		finish ateach (d:Point in Dist.makeUnique()) {
			val itr = dmat.handleBS().iterator();
			while (itr.hasNext()) {
				itr.next().initRandom(nonZeroDensity, (Int,Int)=>RandTool.getRandGen().nextDouble());
			}
		}
		return this;
	}
	
	public def initRandom(nzDensity:Double, initFun:(Int,Int)=>Double) : DistMatrixBuilder(this) {
		finish ateach (d:Point in Dist.makeUnique()) {
			val itr = dmat.handleBS().iterator();
			while (itr.hasNext()) {
				itr.next().initRandom(nzDensity, initFun);
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


	//=====================================
	public def set(r:Int, c:Int, value:Double): void{
		val grid = dmat.handleBS().getGrid();
		val loc = grid.find(r, c);
		val bid = grid.getBlockId(loc(0), loc(1));
		val bx  = loc(2);
		val by  = loc(3);
		val pid = dmat.handleBS().getDistMap().findPlace(bid);
		//Remote capture: bid, bx, by, 
		at (Place.place(pid)) {
			val blkset:BlockSet = dmat.handleBS();
			val blk:MatrixBlock = blkset.find(bid);
			if (blk == null) 
				Debug.exit("Error in search block in block set");
			
			blk.getBuilder().set(bx, by, value);
		}
	}
	
	public def reset(r:Int, c:Int):Boolean {
		val grid = dmat.handleBS().getGrid();
		val loc = grid.find(r, c);
		val bid = grid.getBlockId(loc(0), loc(1));
		val bx  = loc(2);
		val by  = loc(3);
		val pid = dmat.handleBS().getDistMap().findPlace(bid);
		//Remote capture: bid, bx, by, 
		val ret = at (Place.place(pid)) {
			val blkset:BlockSet = dmat.handleBS();
			val blk:MatrixBlock = blkset.find(bid);
			if (blk == null) 
				Debug.exit("Error in searching block in block set");
			
			blk.getBuilder().reset(bx, by)
		};
		return ret;
	}
	
	//=====================================
	//=====================================

	public def toDistBlockMatrix():DistBlockMatrix(M,N) = dmat;
		
	
	public def toMatrix():Matrix(M,N) = dmat as Matrix(M,N);
		

	
}