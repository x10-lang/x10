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

import x10.regionarray.Dist;
import x10.util.Timer;
import x10.util.StringBuilder;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.Debug;
import x10.matrix.MathTool;
import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.BlockMatrix;
import x10.matrix.comm.BlockGather;
import x10.matrix.comm.BlockScatter;
import x10.matrix.comm.BlockSetBcast;

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
public class DistBlockMatrix extends Matrix {
	public val handleBS:PlaceLocalHandle[BlockSet];

	/**
	 * Time profiling
	 */
	transient var commTime:Long =0;
	transient var calcTime:Long =0;

	//This field only defined when DistGrid is used in block distribution
	transient var gdist:DistGrid = null;

	
	public def this(bs:PlaceLocalHandle[BlockSet]) {
		super(bs().grid.M, bs().grid.N);
		handleBS  = bs;	
	}
	
	public def this(gridDist:DistGrid, bs:PlaceLocalHandle[BlockSet]) {
		super(bs().grid.M, bs().grid.N);
		handleBS  = bs;	
		gdist = gridDist;
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
    public static def make(g:Grid, dmap:DistMap):DistBlockMatrix(g.M, g.N){
		//Remote capture: g, dmap
		val bs = PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD, ()=>(new BlockSet(g, dmap)));//Remote capture
		return new DistBlockMatrix(bs) as DistBlockMatrix(g.M,g.N);
	}

    public static def make(g:Grid, gridDist:DistGrid):DistBlockMatrix(g.M, g.N){
		//Remote capture: g, dmap
		val dmap = gridDist.dmap;
		val bs = PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD, ()=>(new BlockSet(g, dmap)));//Remote capture
		return new DistBlockMatrix(gridDist, bs) as DistBlockMatrix(g.M,g.N);
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
	public static def make(m:Long, n:Long, 
			rowBs:Long, colBs:Long, 
			rowPs:Long, colPs:Long):DistBlockMatrix(m,n) {
		Debug.assure(rowPs*colPs==Place.MAX_PLACES, "Block partitioning error");
		val blks = PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD, 
				()=>(BlockSet.make(m,n,rowBs,colBs,rowPs,colPs)));
		val gdist = new DistGrid(blks().getGrid(), rowPs, colPs);
		return new DistBlockMatrix(gdist, blks) as DistBlockMatrix(m,n);
		
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
		val colPs:Long = MathTool.sqrt(Place.MAX_PLACES);//Math.sqrt(Place.MAX_PLACES) as Int;
		val rowPs = Place.MAX_PLACES / colPs;
		return make(m, n, rowBs, colBs, rowPs, colPs);
	}
	
	/**
	 * Create DistBlockMatrix instance using default partitioning and distribution map
	 * 
	 * @param m, n           number of matrix rows and columns
	 * @return DistBlockMatrix instance
	 */
	public static def make(m:Long, n:Long):DistBlockMatrix(m,n) {
		val colBs = MathTool.sqrt(Place.MAX_PLACES);
		val rowBs = Place.MAX_PLACES / colBs;
		return make(m, n, rowBs, colBs, rowBs, colBs);
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
	public static def make(
			m:Long, n:Long, 
			rowbs:Long, colbs:Long,
			rowPartFunc:(Long)=>Long, 
			colPartFunc:(Long)=>Long, 
			mapFunc:(Long)=>Long) {
		
		val ttbs = rowbs * colbs;
		val blks = PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD, 
				()=>(new BlockSet(Grid.make(rowbs, colbs, rowPartFunc, colPartFunc), DistMap.make(ttbs, mapFunc))));
		return new DistBlockMatrix(blks);
	}

	//Make a copy, but sharing partitioning grid and distribution map
	public static def makeDense(d:DistBlockMatrix):DistBlockMatrix(d.M,d.N) {
		val sblks = d.handleBS;
		val dblks = PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD, 
				()=>(BlockSet.makeDense(sblks().grid, sblks().dmap)));
		
		return  new DistBlockMatrix(d.gdist, dblks) as DistBlockMatrix(d.M,d.N);
	}

	public static def makeDense(g:Grid, gd:DistGrid):DistBlockMatrix(g.M,g.N) {
		val bs = PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD, 
				()=>(BlockSet.makeDense(g, gd.dmap)));//Remote capture
		return new DistBlockMatrix(gd, bs) as DistBlockMatrix(g.M,g.N);		
	}
	
	public static def makeSparse(g:Grid, gd:DistGrid, nzp:Double):DistBlockMatrix(g.M,g.N) {
		val bs = PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD, 
				()=>(BlockSet.makeSparse(g, gd.dmap, nzp)));//Remote capture
		return new DistBlockMatrix(gd, bs) as DistBlockMatrix(g.M,g.N);		
	}

	
	public static def makeDense(g:Grid, d:DistMap):DistBlockMatrix(g.M,g.N) {
		val bs = PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD, 
				()=>(BlockSet.makeDense(g, d)));//Remote capture
		return new DistBlockMatrix(bs) as DistBlockMatrix(g.M,g.N);		
	}
	
	public static def makeSparse(g:Grid, d:DistMap, nzp:Double):DistBlockMatrix(g.M,g.N) {
		val bs = PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD, 
				()=>(BlockSet.makeSparse(g, d, nzp)));//Remote capture
		return new DistBlockMatrix(bs) as DistBlockMatrix(g.M,g.N);		
	}

	public static def makeDense(g:Grid):DistBlockMatrix(g.M,g.N) =
		makeDense(g, DistGrid.make(g).dmap);
	
	public static def makeSparse(g:Grid, nzp:Double):DistBlockMatrix(g.M,g.N) =
		makeSparse(g, DistGrid.make(g).dmap, nzp);
	

	public static def makeDense(m:Long, n:Long, rbs:Long, cbs:Long, rps:Long, cps:Long) =
		make(m, n, rbs, cbs, rps, cps).allocDenseBlocks();

	public static def makeDense(m:Long, n:Long, rbs:Long, cbs:Long) =
		make(m, n, rbs, cbs).allocDenseBlocks();

	public static def makeSparse(m:Long, n:Long, rbs:Long, cbs:Long, rps:Long, cps:Long, npz:Double) =
		make(m, n, rbs, cbs, rps, cps).allocSparseBlocks(npz);

	public static def makeSparse(m:Long, n:Long, rbs:Long, cbs:Long, npz:Double) =
		make(m, n, rbs, cbs).allocSparseBlocks(npz);

	/**
	 * Allocate dense matrix for all blocks
	 */
	public def allocDenseBlocks():DistBlockMatrix(this) {
		finish ateach(p in Dist.makeUnique()) {
			handleBS().allocDenseBlocks();
		}
		return this;
	}

	/**
	 * Allocate sparse matrix for all blocks
	 */
	public def allocSparseBlocks(nzd:Double):DistBlockMatrix(this) {
		//Remote capture: nnz
		finish ateach(p in Dist.makeUnique()) {
			handleBS().allocSparseBlocks(nzd);
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
		PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD,
				()=>this.handleBS().makeFrontRowBlockSet(rowCnt));
	
	/**
	 * Used to create temporary space in SUMMA. This method does not create a complete 
	 * distributed block matrix. It only creates the front blocks of specified number of
	 * columns. The front column blocks are used to as temp space to store data of
	 * columns from the first operand matrix in SUMMA
	 */
	public def makeTempFrontColBlocks(colCnt:Long) =
		PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD,
				()=>this.handleBS().makeFrontColBlockSet(colCnt));

	public def makeTempFrontColDenseBlocks(colCnt:Long) =
		PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD,
				()=>this.handleBS().makeFrontColDenseBlockSet(colCnt));
	
    public def init(dval:Double):DistBlockMatrix(this){
		//Remote capture: dval 
		finish ateach(p in Dist.makeUnique()) {
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
		finish ateach(p in Dist.makeUnique()) {
			val blkitr = handleBS().iterator();
			while (blkitr.hasNext()) {
				val blk = blkitr.next();
				blk.initRandom();
			}
		}
		return this;
	}
	
	public def initRandom(lb:Long, ub:Long) : DistBlockMatrix(this){
		finish ateach(p in Dist.makeUnique()) {
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
	public def init(f:(Long,Long)=>Double): DistBlockMatrix(this) {
		finish ateach(p in Dist.makeUnique()) {
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
	public def initBlock(bid:Long, f:(Long,Long)=>Double): DistBlockMatrix(this) {
		val pid = this.getMap().findPlace(bid);
		at(Place(pid)) {
			val blk = handleBS().findBlock(bid);
			blk.init(f);
		}
		return this;
	}
	
	public def initBlock(rowId:Long,colId:Long, f:(Long,Long)=>Double) =
		initBlock(getGrid().getBlockId(rowId, colId), f);
	
	/**
	 * Allocate memory space for new dist block matrix using the same
	 * matrix partitioning and block distribution as this.
	 */
	public def alloc(m:Long, n:Long) : DistBlockMatrix(m,n) {
		Debug.assure(m==M&&n==N, "Matrix dimension is not same");
		val nm = DistBlockMatrix.make(getGrid(), getMap()) as DistBlockMatrix(m,n);
        finish ateach(p in Dist.makeUnique()) {
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
        val bs = PlaceLocalHandle.make[BlockSet](PlaceGroup.WORLD, 	
					()=>(this.handleBS().clone()));
		
		return new DistBlockMatrix(bs) as DistBlockMatrix(M,N);
	}
	
	public def reset() {
        finish ateach(p in Dist.makeUnique()) {
			handleBS().reset();
		}
	}
	
	public def copyTo(that:DistBlockMatrix(M,N)) {
		val stt = Timer.milliTime();
        finish ateach(p in Dist.makeUnique()) {
			val sit  = this.handleBS().iterator();
			val dit  = that.handleBS().iterator();
			while (sit.hasNext()&&dit.hasNext()) {
				val sblk = sit.next();
				val dblk = dit.next();
				Debug.assure(sblk.myRowId==dblk.myRowId && sblk.myColId==dblk.myColId,
						"Block mismatch in DistBlockMatrix copyTo");
				val smat = sblk.getMatrix();
				val dmat = dblk.getMatrix();
				smat.copyTo(dmat as Matrix(smat.M, smat.N));
			}
		}
		commTime += Timer.milliTime() - stt;
	}
	
	public def copyTo(dst:BlockMatrix(M,N)) {
		
		val srcgrid = this.getGrid();
		Debug.assure(srcgrid.equals(dst.grid),
            "source and destination matrix partitions are not compatible");
		val stt = Timer.milliTime();
		BlockGather.gather(this.handleBS, dst.listBs);
		commTime += Timer.milliTime() - stt;
	}
	
	public def copyTo(dst:DupBlockMatrix(M,N)) {
		val srcgrid = this.getGrid();
		Debug.assure(srcgrid.equals(dst.local().grid),
            "source and destination matrix partitions are not compatible");
		val stt = Timer.milliTime();		
		BlockGather.gather(this.handleBS, dst.local().listBs);
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
			Debug.exit("Not supported matrix type for converting DistBlockMatrix");
		}
	}
	
	public def copyTo(den:DenseMatrix(M,N)): void {
		val stt = Timer.milliTime();
		if (den.N == 1L) {
			BlockGather.gatherVector(this.handleBS, den as DenseMatrix(M,1L));
		} else if (getGrid().numRowBlocks==1L){
			BlockGather.gatherRowBs(this.handleBS, den as Matrix);
		} else {
			Debug.exit("DistBlockMatrix does not support direct copyTo densematrix,"+
					"unless it is single-column matrix (vector)."+
					"Workaround is to use inter-media BlockMatrix to save gathered blocks"+
					"and then convert to dense using BlockMatrix.copyTo(DenseMatrix)");
		}
		commTime += Timer.milliTime() - stt;
	}

	public def copyFrom(src:BlockMatrix(M,N)) {
		val dstgrid = getGrid();
		val stt = Timer.milliTime();

		Debug.assure(dstgrid.equals(src.grid),
		"source and destination matrix partitions are not compatible");
		BlockScatter.scatter(src.listBs, this.handleBS);
		commTime += Timer.milliTime() - stt;
	}
	
	public  operator this(x:Long, y:Long):Double {
		val grid = handleBS().grid;
		val loc = grid.find(x, y);
		val bid = grid.getBlockId(loc(0), loc(1));
		val bx  = loc(2);
		val by  = loc(3);
		val pid = handleBS().dmap.findPlace(bid);
		//Remote capture: bid, bx, by, 
		val dv = at(Place.place(pid)) {
			val blkset:BlockSet = this.handleBS();
			val blk:MatrixBlock = blkset.find(bid);
			if (blk == null) 
				Debug.exit("Error in search blocks in block set");
			
			blk(bx, by)
		};
		return dv;
	}
	public operator this(x:Long, y:Long)=(d:Double):Double {
		val grid = handleBS().grid;
		val loc = grid.find(x, y);
		val bid = grid.getBlockId(loc(0), loc(1));
		val bx  = loc(2);
		val by  = loc(3);
		val pid = handleBS().dmap.findPlace(bid);
		//Remote capture: bid, bx, by, 
		at(Place.place(pid)) {
			val blkset:BlockSet = handleBS();
			val blk:MatrixBlock = blkset.find(bid);
			if (blk == null) 
				Debug.exit("Error in search blocks in block set");
			
			blk.getMatrix()(bx, by) = d;
		}
		return d;
	}
	

	/**
	 * Get block to here. If block is not at local, it will be remote captured
	 * and compied to here.
	 */
	public def fetchBlock(bid:Long):MatrixBlock {
		val map = getMap();
		val pid = map.findPlace(bid);
		val blk = at(Place(pid)) handleBS().findBlock(bid);
		return blk;
	}
	public def fetchBlock(rid:Long, cid:Long):MatrixBlock =
		fetchBlock(getGrid().getBlockId(rid, cid));

	public def getGrid():Grid   = this.handleBS().grid;
	public def getMap():DistMap = this.handleBS().dmap;

	public def scale(alpha:Double): DistBlockMatrix(this) {
        finish ateach(p in Dist.makeUnique()) {
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
	
	public def cellAdd(dv:Double): DistBlockMatrix(this) {
		finish ateach(p in Dist.makeUnique())  {
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
		
		finish ateach(p in Dist.makeUnique())  {
			val bsetA= A.handleBS();
			val itr = this.handleBS().iterator();
			while (itr.hasNext()) {
			 	val b1:MatrixBlock = itr.next();
			 	val m1:Matrix      = b1.getMatrix();
			 	val b2:MatrixBlock = bsetA.find(b1.myRowId, b1.myColId);
			 	
			 	if (b2 == null) Debug.exit("Can not find corresponding block");
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
		
		finish ateach(p in Dist.makeUnique()) {
			val itr = this.handleBS().iterator();
			val blkA = A.handleBS();
			while (itr.hasNext()) {
				val b1 = itr.next();
				val m1 = b1.getMatrix();
				val b2 = blkA.find(b1.myRowId, b1.myColId);
				if (b2 == null) Debug.exit("Can not find corresponding block");
				m1.cellSub(b2.getMatrix() as Matrix(m1.M, m1.N));
			}
		}
		return this;	
	}
	
	protected def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x) {
		throw new UnsupportedOperationException("Matrix type mismatch");						
	}
	
	public def cellSubFrom(dv:Double): DistBlockMatrix(this) {
		finish ateach(p in Dist.makeUnique())  {
			//Remote capture: dv
			val bitr:Iterator[MatrixBlock] = this.handleBS().iterator();
			
			while (bitr.hasNext()) {
				val b:MatrixBlock = bitr.next();
				val mat = b.getMatrix();
				mat.cellSubFrom(dv);
			}
		}
		return this;
	}

	public def cellMult(A:Matrix(M,N)): Matrix(this) {
		if (! likeMe(A))
			throw new UnsupportedOperationException("Distributed matrix not compatible for computing");
		return cellMult(A as DistBlockMatrix(M,N));	
	}

	public def cellMult(A:DistBlockMatrix(M,N)): DistBlockMatrix(this) {
		if (! likeMe(A))
			throw new UnsupportedOperationException("Distributed matrix not compatible for computing");
		finish ateach(p in Dist.makeUnique()) {
			val itr  = this.handleBS().iterator();
			val blkA = A.handleBS();
			while (itr.hasNext()) {
				val b1 = itr.next();
				val m1 = b1.getMatrix();
				val b2 = blkA.find(b1.myRowId, b1.myColId);
				if (b2 == null) Debug.exit("Can not find corresponding block");
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
		
		finish ateach(p in Dist.makeUnique()) {
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

	
	public operator this + (dv:Double) = this.clone().cellAdd(dv) as DistBlockMatrix(M,N);
	public operator this - (dv:Double) = this.clone().cellAdd(-dv) as DistBlockMatrix(M,N);
	public operator (dv:Double) + this = this.clone().cellAdd(dv) as DistBlockMatrix(M,N);
	public operator (dv:Double) - this = this.clone().cellSubFrom(-dv) as DistBlockMatrix(M,N);
	
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
		finish ateach(p in Dist.makeUnique()) {
			handleBS().buildBlockMap();
		}
	}
	
	public def getAllDataCount():Long {
		var tt:Long = 0;
		for (var p:Long=0; p<Place.MAX_PLACES; p++) {
			val ds = at(Place(p)) handleBS().getAllBlocksDataCount();
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
		finish ateach(p in Dist.makeUnique()) {
			val bset = handleBS();
			bset.sync(bset.getFirst());
		}
	}

	public def getCalcTime() = calcTime;
	public def getCommTime() = commTime;
	
	/**
	 * Check all blocks are same or not
	 */
	public def checkAllBlocksEqual() : Boolean {
		val rtmat:Matrix = handleBS().getFirst().getMatrix();
		var retval:Boolean = true;
		for (var p:Long=0 ; p<Place.MAX_PLACES && retval; p++) {
			//Debug.flushln("Check block local sync at "+p);
			if (here.id() != p) {
				retval &= at(Place(p)) {
					//Remote capture: rtmat
					handleBS().allEqual(rtmat)				
				};
			} else {
				retval &= handleBS().allEqual(rtmat);
			}
			
			if (!retval) 
				Console.OUT.println("Integrity check failed at place "+p);
		}
		//Debug.flushln("Check block local sync done");
		return retval;
	}
	
    public def getTotalDataSize():Long {
		var dsz:Long=0;
		for (p in Place.places()) {
            val c = at(p) { handleBS().getAllBlocksDataCount()};
			dsz += c;
		}
		return dsz;
	}
	
    public def toStringBlock():String {
		val output = new StringBuilder();
		output.add("-------- Dist Matrix Block size:["+M+" x "+N+"] ---------\n");
		for (p in Place.places()) {
			output.add(at (p) { handleBS().toString()});
		}
		output.add("--------------------------------------------------\n");
		return output.toString();
	}
}
