/*ls
 * 
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.matrix.distblock;

import x10.util.ArrayList;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;

import x10.matrix.Debug;
import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;
import x10.matrix.block.MatrixBlock;

/**
 * This class defines list of matrix blocks which live in the same place
 */
public class BlockSet  { 

	//----------------------------------------
	// This is temporary used for accessing globle
	// partition and distribution information
	// This initial cost is needed to save time used for 
	// remote capture.
	protected val grid:Grid;
	protected val dmap:DistMap;
	//-----------------------------------------
		
	public val blocklist:ArrayList[MatrixBlock];
	//==========================================

	public def this(g:Grid, map:DistMap) {
		grid=g; dmap = map;
		blocklist = new ArrayList[MatrixBlock]();		
	}

	public def this(g:Grid, map:DistMap, bl:ArrayList[MatrixBlock]) {
		grid=g; dmap = map; blocklist = bl;
	}
	//========================================
	/**
	 * Creating block set for given matrix, partition and distribution.
	 * No memory allocation is performed.
	 * 
	 * @param  m      number of rows in matrix
	 * @param  n      number of columns in matrix
	 * @param  rowBs  number of partition blocks in row
	 * @param  colBs  number of partition blocks in column
	 * @param  rowCs  number of group of blocks in row of grid distribution
	 * @param  colCs  number of group of blocks in column of grid distribution
	 */
	public static def make(m:Int, n:Int, rowBs:Int, colBs:Int, rowCs:Int, colCs:Int) {
		val gd = new Grid(m, n, rowBs, colBs);
		Debug.assure(rowCs*colCs == Place.MAX_PLACES, 
				"number of distributions groups of blocks must equal to number of places");
		val dp = new DistGrid(gd, rowCs, colCs);
		return new BlockSet(gd, dp.dmap);
	}

	public def allocDenseBlocks() : BlockSet {
		val itr = dmap.getBlockIterator(here.id());
		while (itr.hasNext()) {
			val bid    = itr.next();
			val rowbid = grid.getRowBlockId(bid);
			val colbid = grid.getColBlockId(bid);
			val m      = grid.rowBs(rowbid);
			val n      = grid.colBs(colbid);
			add(DenseBlock.make(rowbid, colbid, m, n));
		}
		return this;
	}
	
	public def allocSparseBlocks(nzd:Double) : BlockSet {
		val itr = dmap.getBlockIterator(here.id());
		while (itr.hasNext()) {
			val bid    = itr.next();
			val rowbid = grid.getRowBlockId(bid);
			val colbid = grid.getColBlockId(bid);
			val m      = grid.rowBs(rowbid);
			val n      = grid.colBs(colbid);
			add(SparseBlock.make(rowbid, colbid, m, n, nzd));
		}
		return this;
	}
	//--------------
	public static def makeDense(m:Int, n:Int, rowBs:Int, colBs:Int, rowCs:Int, colCs:Int) =
		make(m, n, rowBs, colBs, rowCs, colCs).allocDenseBlocks();
	
	public static def makeSparse(m:Int, n:Int, rowBs:Int, colBs:Int, rowCs:Int, colCs:Int, nzd:Double) =
		make(m, n, rowBs, colBs, rowCs, colCs).allocSparseBlocks(nzd);
	//--------------------
	
	public static def makeDense(g:Grid, d:DistMap) {
		return new BlockSet(g,d).allocDenseBlocks();
	}
	
	public static def makeSparse(g:Grid, d:DistMap, nzd:Double) {
		return new BlockSet(g,d).allocSparseBlocks(nzd);
	}
	
	//========================================
	public def getGrid()   = grid;
	public def getDistMap()= dmap;
	
	//========================================
	public def add(mb:MatrixBlock) {
		for (var i:Int=0; i<this.blocklist.size(); i++)
			if (mb.sameAs(this.blocklist.get(i)))
				return false; 
		return this.blocklist.add(mb);
	}
	
	public def addAll(bs:BlockSet) {
		var retval:Boolean = true;
		val it = bs.iterator();
		while (it.hasNext()) {
			retval &= this.blocklist.add(it.next());
		}
		return retval;
	}

	//=======================================
	
	public def remove(mb:MatrixBlock) {
		return this.blocklist.remove(mb);
	}
	
	public def retainAll(bs:BlockSet) {
		var retval:Boolean=true;
		val it = bs.iterator();
		while (it.hasNext()) {
			retval &= this.blocklist.remove(it.next());
		}
		return retval;
	}
	//=======================================

	public def find(rid:Int, cid:Int): MatrixBlock {
		val it = this.iterator();
		while (it.hasNext()) {
			val blk = it.next();
			if (blk.myRowId == rid &&
				blk.myColId == cid ) return blk;
		}
		Debug.exit("Cannot find block ("+rid+","+cid+") at place "+here.id());
		return null;
	}
	
	public def findBlock(bid:Int) = find(bid);
	
	public def find(bid:Int): MatrixBlock {
		val rid = grid.getRowBlockId(bid);
		val cid = grid.getColBlockId(bid);
		return find(rid, cid);
	}
	
	public def findPlace(bid:Int) = dmap.findPlace(bid);
	
	//---------------------------------------------------
	protected def get(i:Int) = blocklist.get(i);
	
	public def getFirst() = blocklist.getFirst();
	
	public def getFirstMatrix() = blocklist.getFirst().getMatrix();
	
	public def getLocalBlockIdAt(index:Int):Int {
		val grid = getGrid();
		val blk  = get(index);
		return grid.getBlockId(blk.myRowId, blk.myColId);
	}
	
	public def getFirstLocalBlockId() = getLocalBlockIdAt(0);
	
	public def getBlockId(rowId:Int, colId:Int):Int = getGrid().getBlockId(rowId, colId);
	
	//-------------------------------------------------
	/**
	 * Local root block is either the rootbid block, if rootbid is local, 
	 * or the first block has the same row block Id/column block Id.
	 * Select function is used to pick row-wise or column-wise search.
	 */
	public def findLocalRootBlock(rootbid:Int, selectfunc:(Int, Int)=>Int):MatrixBlock {
		val it = blocklist.iterator();
		val grid = getGrid();
		//Check if rootbid is local
		if (findPlace(rootbid) == here.id())
			return findBlock(rootbid);
		
		val rowblkid = grid.getRowBlockId(rootbid);
		val colblkid = grid.getColBlockId(rootbid);
		val targetid = selectfunc(rowblkid, colblkid);		
		while (it.hasNext()) {
			val blk = it.next();
			val checkid = selectfunc(blk.myRowId, blk.myColId);
			if (checkid == targetid)
				return blk;
		}
		//This should be error
		Debug.exit("Error in searching first root block");
		return null;
	}
	
	public def findLocalRootRowBlock(rootbid:Int):MatrixBlock =
		findLocalRootBlock(rootbid, (rid:Int, cid:Int)=>rid);
	
	public def findLocalRootColBlock(rootbid:Int):MatrixBlock =
		findLocalRootBlock(rootbid, (rid:Int, cid:Int)=>cid);
	//---------------------------------------------------
	/**
	 * Deep clone all blocks in the set
	 */
	public def clone() {
		val bl = new ArrayList[MatrixBlock](this.blocklist.size());
		val it = this.blocklist.iterator();
		while (it.hasNext()) {
			val nmb = it.next().clone();
			bl.add(nmb);
		}
		return new BlockSet(this.grid, this.dmap, bl);
	}

	// The target block set must be dense blocks
	public def copyTo(bs:BlockSet) {
		val srcit = this.blocklist.iterator();
		val dstit = bs.blocklist.iterator();
		while (srcit.hasNext()&&dstit.hasNext()) {
			val src = srcit.next().getMatrix();
			val den = dstit.next().getMatrix() as DenseMatrix(src.M, src.N);
			src.copyTo(den);
		}
	}
	
	/**
	 * 
	 */
	protected def clear():void {
		this.blocklist.clear();
	}
	
	protected def reset():void {
		val it = this.blocklist.iterator();
		while (it.hasNext()) {
			val b = it.next();
			b.reset();
		}
	}
	//=============================================
	
	public static def localCopy(srcblk:MatrixBlock, bs:BlockSet, dstidx:Int): void {
		val dstblk = bs.blocklist.get(dstidx);
		if (dstblk != srcblk) {
			srcblk.copyTo(dstblk);
		}
	}
	
	/**
	 * 
	 */
	public def sync() : void {
		sync(this.blocklist.getFirst());
	}
	
	public def sync(rtbid:Int) : void {
		val it = this.blocklist.iterator();
		val rtblk = find(rtbid);
		Debug.assure(rtblk!=null, "Cannot find root block in local block list");
		sync(rtblk);
	}
	
	public def sync(rootblk:MatrixBlock) : void {
		val it = this.blocklist.iterator();
		while (it.hasNext()) {
			val blk = it.next();
			if (blk != rootblk)	
				rootblk.copyTo(blk);
		}
	}
	
	public def sync(rootblk:MatrixBlock, colOff:Int, colCnt:Int) : void {
		val it = this.blocklist.iterator();
		while (it.hasNext()) {
			val blk = it.next();
			if (blk != rootblk)	
				rootblk.copyCols(colOff, colCnt, blk.getMatrix());
		}
	}
		
	//======================================
	public def selectCast(rootblk:MatrixBlock, colCnt:Int, select:(Int,Int)=>Int) {
		val it = this.blocklist.iterator();
		val target = select(rootblk.myRowId, rootblk.myColId);
		while (it.hasNext()) {
			val blk = it.next();
			if (blk != rootblk) {
				val chkid = select(blk.myRowId, blk.myColId);
				if (target == chkid) {
					//Debug.flushln("Copy root to ("+blk.myRowId+","+blk.myColId+")");
					rootblk.copyCols(0, colCnt, blk.getMatrix());
				}
			}
		}
	}
	
	//======================================
	
	public static def cellSum(a:DenseMatrix, b:DenseMatrix) : void {
		b.cellAdd(a as DenseMatrix(b.M, b.N));
	}
	
	/**
	 * 
	 */
	public def reduceSum(rtblk:MatrixBlock): void {
		reduce(rtblk, (a:DenseMatrix, b:DenseMatrix)=>b.cellAdd(a as DenseMatrix(b.M,b.N)));
	}
	
	public def reduceSum(rootbid:Int) : void {
		val rtblk = find(rootbid);
		reduceSum(rtblk);
	}
	
	public def reduceSumToFirst() : void {
		val rtblk = getFirst();
		reduceSum(rtblk);
	}
	
	//-------------
	/**
	 * Operate all blocks in the set and store the reducution result in specified root
	 * block. The root block is input and output, overwritten with the result
	 * 
	 * @param rtblk       root block which stores the reduce result
	 * @param opFunc      reduce function which takes two operands. First is input and second is input/output dense matrix
	 */
	public def reduce(rtblk:MatrixBlock, opFunc:(DenseMatrix,DenseMatrix)=>DenseMatrix) :void {
		val it = this.blocklist.iterator();
		while (it.hasNext()) {
			val blk = it.next();
			if (blk != rtblk) {
				val rtmat = rtblk.getMatrix() as DenseMatrix;
				val opmat = blk.getMatrix() as DenseMatrix(rtmat.M, rtmat.N);
				opFunc(opmat, rtmat);
				//rtmat.cellAdd(blk.getMatrix() as Matrix(rtmat.M, rtmat.N));
			}
		}		
		
	}
	
	/**
	 * Perform reduce operation on all blocks and store result to the first
	 * block in the set
	 */
	public def reduce(opFunc:(DenseMatrix,DenseMatrix)=>DenseMatrix) :void {
		val rootblk = this.getFirst();
		reduce(rootblk, opFunc);
	}

	/**
	 * Perform reduce all blocks and store result to the specified block.
	 */
	public def reduce(rootBlockId:Int, opFunc:(DenseMatrix,DenseMatrix)=>DenseMatrix): void {
		val rootblk = this.findBlock(rootBlockId);
		reduce(rootblk, opFunc);
	}
	
	//--------------------
	public def selectReduce(rootblk:MatrixBlock, colCnt:Int, select:(Int,Int)=>Int, 
			opFunc:(DenseMatrix,DenseMatrix, Int)=>DenseMatrix) : void{
		
		val rootden = rootblk.getMatrix() as DenseMatrix;
		val it = this.blocklist.iterator();
		val target = select(rootblk.myRowId, rootblk.myColId);
		while (it.hasNext()) {
			val blk = it.next();
			if (blk != rootblk) {
				val chkid = select(blk.myRowId, blk.myColId);
				if (target == chkid) {
					//Debug.flushln("Copy root to ("+blk.myRowId+","+blk.myColId+")");
					//rootblk.copyCols(0, colCnt, blk.getMatrix());
					opFunc(blk.getMatrix() as DenseMatrix, rootden, colCnt);
				}
			}
		}
	}
	
	public def selectReduce(rootbid:Int, colCnt:Int, select:(Int,Int)=>Int, 
			opFunc:(DenseMatrix,DenseMatrix,Int)=>DenseMatrix) {
		 val rootblk = this.findLocalRootBlock(rootbid, select);
		selectReduce(rootblk, colCnt, select, opFunc);
	}
	
	//======================================
	public def iterator() = blocklist.iterator();
	
	//======================================
	public def check():Boolean {
		val pid    = here.id();
		val mapitr = dmap.getBlockIterator(pid);
		val blkitr = this.iterator();
		while (blkitr.hasNext() && mapitr.hasNext()) {
			val blk = blkitr.next();
			val bid = mapitr.next();
			
			val dstid = grid.getBlockId(blk.myRowId, blk.myColId);
			if (dstid != bid) {
				Debug.exit("Dist blocks and their mapping are not consistent");
			}
		}
		if (blkitr.hasNext() || mapitr.hasNext()) {
			Debug.exit("Dist blocks and their mapping are not consistent");		
		}
		
		return true;
	}
	
	public def allEqual(tgtmat:Matrix):Boolean {
		var retval:Boolean = true;
		val blkitr = this.iterator();

		while (blkitr.hasNext() && retval) {
			val chkmat = blkitr.next().getMatrix();
			if (chkmat != tgtmat )
				retval &= tgtmat.equals(chkmat as Matrix(tgtmat.M,tgtmat.N));
		}
		return retval;
	}
	
	//=================================================
	//=================================================

	public def getBlockDataSize(bid:Int):Int {
		val blk = findBlock(bid);
		Debug.assure(blk!=null, "Cannot locate block "+bid+" in block set at " +here.id());
		return blk.getDataSize();
	}
	
	public def getBlockDataSizeAt(blkidx:Int)=
		get(blkidx).getDataSize();
	
	public def getAllBlocksDataSize():Int {
		var dsz:Int =0;
		val blkitr = this.iterator();
		while (blkitr.hasNext()) {
			val blk = blkitr.next();
			dsz += blk.getDataSize();
		}
		return dsz;
	}	
	
	//=================================================
	public def toString() :String {
		
		val blkitr = this.iterator();
		var outstr:String = "At place "+here.id()+" blocks:\n";
		while (blkitr.hasNext()) {
			val blk = blkitr.next();
			outstr += blk.toString();
		}
		return outstr;	
	}
}