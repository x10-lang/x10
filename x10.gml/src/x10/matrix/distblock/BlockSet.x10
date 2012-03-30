/*
 * 
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

import x10.util.ArrayList;
import x10.compiler.Inline;

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
	//---------------
	//-----------------------------------------
	
	public val blocklist:ArrayList[MatrixBlock];
	
	/**
	 * This is used for fast access when distribution is DistGrid.
	 */
	protected var blockMap:Array[MatrixBlock](2);
		
	//-----------------------------------------
	public var rowCastPlaceMap:CastPlaceMap;
	public var colCastPlaceMap:CastPlaceMap; 
	
	//--------------------------	
	//==========================================

	public def this(g:Grid, map:DistMap) {
		grid=g; dmap = map;
		blocklist = new ArrayList[MatrixBlock]();	
		blockMap=null;
		rowCastPlaceMap=null;
		colCastPlaceMap=null;
	}

	public def this(g:Grid, map:DistMap, bl:ArrayList[MatrixBlock]) {
		grid=g; dmap = map; blocklist = bl;
		blockMap = null;
		rowCastPlaceMap=null;
		colCastPlaceMap=null;
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
	public static def make(m:Int, n:Int, rowBs:Int, colBs:Int, rowPs:Int, colPs:Int) {
		//val gd = new Grid(m, n, rowBs, colBs); not balanced when considering distribution among rowPs and colPs
		val gd = DistGrid.makeGrid(m, n, rowBs, colBs, rowPs, colPs);
		Debug.assure(rowPs*colPs == Place.MAX_PLACES,
				"number of distributions groups of blocks must equal to number of places");
		val dp = new DistGrid(gd, rowPs, colPs);
		return new BlockSet(gd, dp.dmap);
	}

	public def allocDenseBlocks() : BlockSet {
		val itr = dmap.buildBlockIteratorAtPlace(here.id());
		while (itr.hasNext()) {
			val bid    = itr.next();
			val rowbid = grid.getRowBlockId(bid);
			val colbid = grid.getColBlockId(bid);
			val m      = grid.rowBs(rowbid);
			val n      = grid.colBs(colbid);
			add(DenseBlock.make(rowbid, colbid, m, n));
		}
		assignNeighborPlaces();
		return this;
	}
	
	public def allocSparseBlocks(nzd:Double) : BlockSet {
		val itr = dmap.buildBlockIteratorAtPlace(here.id());
		while (itr.hasNext()) {
			val bid    = itr.next();
			val rowbid = grid.getRowBlockId(bid);
			val colbid = grid.getColBlockId(bid);
			val m      = grid.rowBs(rowbid);
			val n      = grid.colBs(colbid);
			add(SparseBlock.make(rowbid, colbid, m, n, nzd));
		}
		assignNeighborPlaces();		
		return this;
	}
	//--------------
	public static def makeDense(m:Int, n:Int, rowBs:Int, colBs:Int, rowPs:Int, colPs:Int) =
		make(m, n, rowBs, colBs, rowPs, colPs).allocDenseBlocks();
	
	public static def makeSparse(m:Int, n:Int, rowBs:Int, colBs:Int, rowPs:Int, colPs:Int, nzd:Double) =
		make(m, n, rowBs, colBs, rowPs, colPs).allocSparseBlocks(nzd);
	//--------------------
	
	public static def makeDense(g:Grid, d:DistMap) {
		return new BlockSet(g,d).allocDenseBlocks();
	}
	
	public static def makeSparse(g:Grid, d:DistMap, nzd:Double) {
		return new BlockSet(g,d).allocSparseBlocks(nzd);
	}
	//========================================================
	
	/**
	 * Front row blocks are those blocks which has smallest row ID in the block set in
	 * each column block.
	 * The front row blocks serves as temporary space to store column-wise cast of
	 * the second operand matrix in SUMMA.
	 */
	protected def makeFrontRowBlockSet(rowCnt:Int):BlockSet {
		//Fake row partitioning in all blocks
		val nbl:ArrayList[MatrixBlock] = allocFrontBlocks(rowCnt, (r:Int,c:Int)=>c);
		val nbs = new BlockSet(grid, dmap, nbl);
		nbs.colCastPlaceMap = CastPlaceMap.buildColCastMap(grid, dmap);
		nbs.rowCastPlaceMap = CastPlaceMap.buildRowCastMap(grid, dmap);
		return nbs;
	}
	
	/**
	 * Front column blocks are those blocks which has smallest column block ID in the block set in
	 * each row block. The front column blocks serves temporary space to store row-wise cast of
	 * the first operand matrix in SUMMA
	 */
	protected def makeFrontColBlockSet(colCnt:Int):BlockSet {
		//Fake the column partitioning in all blocks
		val nbl:ArrayList[MatrixBlock] = allocFrontBlocks(colCnt, (r:Int, c:Int)=>r);
		val nbs =  new BlockSet(grid, dmap, nbl);
		nbs.colCastPlaceMap = CastPlaceMap.buildColCastMap(grid, dmap);
		nbs.rowCastPlaceMap = CastPlaceMap.buildRowCastMap(grid, dmap);
		return nbs;
	}

	protected def makeFrontColDenseBlockSet(colCnt:Int):BlockSet {
		//Fake the column partitioning in all blocks
		val nbl:ArrayList[MatrixBlock] = allocFrontDenseBlocks(colCnt, (r:Int, c:Int)=>r);
		val nbs =  new BlockSet(grid, dmap, nbl);
		nbs.colCastPlaceMap = CastPlaceMap.buildColCastMap(grid, dmap);
		nbs.rowCastPlaceMap = CastPlaceMap.buildRowCastMap(grid, dmap);
		return nbs;
	}

	
	//-----------------------
	@Inline	
	private final def allocFrontBlocks(cnt:Int, select:(Int,Int)=>Int):ArrayList[MatrixBlock] {
		val blst = new ArrayList[MatrixBlock]();
		val itr = iterator();
		while (itr.hasNext()) {
			val srcblk = itr.next();
			if (containBlockIn(srcblk, blst, select)) continue;

			val srcmat = srcblk.getMatrix();
			val m = select(srcmat.M, cnt);
			val n = select(cnt, srcmat.N);
			val nblk = srcblk.allocFull(m, n);
			blst.add(nblk);
		}
		return blst;
	}

	@Inline	
	private final def allocFrontDenseBlocks(cnt:Int, select:(Int,Int)=>Int):ArrayList[MatrixBlock] {
		val blst = new ArrayList[MatrixBlock]();
		val itr = iterator();
		while (itr.hasNext()) {
			val srcblk = itr.next();
			if (containBlockIn(srcblk, blst, select)) continue;

			val srcmat = srcblk.getMatrix();
			val m = select(srcmat.M, cnt);
			val n = select(cnt, srcmat.N);
			val nblk = DenseBlock.make(srcblk.myRowId, srcblk.myColId, m, n) as MatrixBlock;//srcblk.allocFull(m, n);
			blst.add(nblk);
		}
		return blst;
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
	/**
	 * Sort all blocks in column-major
	 */
	protected def sort() {
		this.blocklist.sort((b1:MatrixBlock,b2:MatrixBlock)=>cmp(b1,b2));
	}
	
	/**
	 * Sort all blocks in column-major
	 */
	@Inline 
	private static def cmp(b1:MatrixBlock, b2:MatrixBlock):Int {
		val retval:Int=0;
		if (b1.myColId == b2.myColId) 
			return b1.myRowId-b2.myRowId;
		else 
			return b1.myColId-b2.myColId;
	}
	//===========================================
	/**
	 * Build block 2D map. 
	 */
	public def buildBlockMap() {
		if (blockMap != null) return;
		//sort list first
		sort();
		//Figure out how mange blocks in rwo and column
		var nrb:Int = 1;
		val stcb:Int = blocklist.get(0).myColId;
		while (nrb<blocklist.size()) {
			val blk = blocklist.get(nrb);
			if (blk.myColId != stcb) break;
			nrb++;
		}
		val numColBlk:Int = blocklist.size()/nrb;
		val numRowBlk:Int = nrb;
		//Debug.flushln("Build block "+numRowBlk+"x"+numColBlk);
		Debug.assure(numRowBlk*numColBlk==blocklist.size());
		// Assuming all blocks forms in rectangle 
		val minRow = blocklist.get(0).myRowId;
		val minCol = blocklist.get(0).myColId;
		val maxRow = minRow+numRowBlk-1;
		val maxCol = minCol+numColBlk-1;
		blockMap = new Array[MatrixBlock]((minRow..maxRow)*(minCol..maxCol), 
				(p:Point)=>blocklist.get(p(0)-minRow+(p(1)-minCol)*numRowBlk));
		
	}
	
	//==============================================================
	/**
	 * Build neighboring places for all blocks.
	 * If nieghbor place ID < 0, it does not have neighbor in that direction.
	 */
	public def assignNeighborPlaces() {
		val itr = iterator();
		while (itr.hasNext()) {
			val blk = itr.next();
			blk.placeNorth = findNorthPlace(blk);
			blk.placeSouth = findSouthPlace(blk);
			blk.placeEast = findEastPlace(blk);
			blk.placeWest = findWestPlace(blk);
		}
	}
	
	/**
	 * Find neighboring block's place
	 */
	public def findNorthPlace(blk:MatrixBlock) = findNeighborPlace(blk, (n:Int,s:Int, e:Int,w:Int)=>n);
	public def findSouthPlace(blk:MatrixBlock) = findNeighborPlace(blk, (n:Int,s:Int, e:Int,w:Int)=>s);
	public def findEastPlace(blk:MatrixBlock)  = findNeighborPlace(blk, (n:Int,s:Int, e:Int,w:Int)=>e);
	public def findWestPlace(blk:MatrixBlock)  = findNeighborPlace(blk, (n:Int,s:Int, e:Int,w:Int)=>w);

	@Inline 
	private final def findNeighborPlace(blk:MatrixBlock, select:(Int, Int, Int, Int)=>Int):Int {
		val nbid = select(
				grid.getNorthId(blk.myRowId, blk.myColId),
				grid.getSouthId(blk.myRowId, blk.myColId),
				grid.getEastId( blk.myRowId, blk.myColId),
				grid.getWestId( blk.myRowId, blk.myColId));
		if (nbid < 0 ) return -1;
		return findPlace(nbid);
	}

	//=======================================
	/**
	 * Build ring cast place list map
	 */
	public def buildCastPlaceMap() {
		
		if (rowCastPlaceMap != null)
			rowCastPlaceMap = CastPlaceMap.buildRowCastMap(grid, dmap);
		
		if (colCastPlaceMap != null)
			colCastPlaceMap = CastPlaceMap.buildColCastMap(grid, dmap);
	}
	
	
	//=======================================
	protected def search(rid:Int, cid:Int):Int {
		if (blocklist.size() == 0) return -1;

		var min:Int = 0; 
		var max:Int = blocklist.size() - 1; 
		var blk:MatrixBlock;
		var mid:Int = min;
		do {
			mid = min + (max - min) / 2;
			blk = blocklist.get(mid);
			
			if (blk.myColId < cid || ( blk.myColId == cid && blk.myRowId < rid)) {
				min = mid + 1;
			} else {
				max = mid - 1; 
			}
			blk = blocklist.get(mid);
			if (blk.myRowId == rid && blk.myColId== cid) return mid;			
			
		} while ( min<=max );
		return -1;
	}

	public def find(rid:Int, cid:Int):MatrixBlock {
		val idx = search(rid, cid);
		if (idx < 0 ) {
			Debug.flushln(toString());
			Debug.flushln(dmap.toString());
			Debug.exit("Cannot find block ("+rid+","+cid+") at place "+here.id());
		}
		return blocklist.get(idx);
	}
	
	// public def find(rid:Int, cid:Int): MatrixBlock {
	// 	val it = this.iterator();
	// 	while (it.hasNext()) {
	// 		val blk = it.next();
	// 		if (blk.myRowId == rid &&
	// 			blk.myColId == cid ) return blk;
	// 	}
	// 	Debug.exit("Cannot find block ("+rid+","+cid+") at place "+here.id());
	// 	return null;
	// }
	
	@Inline
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
	
	
	//-----------------------------
	/**
	 * Front row block is the first block (smallest row block ID) in the specified column block
	 * of the block set. The front row block is required in SUMMA when receiving column-wise
	 * cast of the second operand matrix.
	 */
	public def findFrontRowBlock(colId:Int) = findFrontBlock(0, colId, (r:Int, c:Int)=>c);

	/**
	 * Front column block is the first block (smallest column block ID) in the specified row blocks
	 * of the block set. The front block is required in SUMMA when receiving row-wise
	 * cast of the first operand matrix.
	 */
	public def findFrontColBlock(rowId:Int) = findFrontBlock(rowId, 0, (r:Int, c:Int)=>r);
	
	/**
	 * Find the first block having the row-block id (or column block id) in
	 * rowwise (or column wise) in the block set. This is used when performing
	 * ring-cast.
	 * 
	 */
	@Inline
	public final def findFrontBlock(bid:Int, select:(Int, Int)=>Int):MatrixBlock =
		findFrontBlock(grid.getRowBlockId(bid), grid.getColBlockId(bid), select);
	/**
	 * Block set must have all blocks sorted in the increasing order of column block id and then row 
	 * block id
	 */
	@Inline
	public final def findFrontBlock(rowId:Int, colId:Int, select:(Int, Int)=>Int):MatrixBlock {
		val id = select(rowId, colId);
		val it = blocklist.iterator();
		while (it.hasNext()) {
			val blk = it.next();
			val checkid = select(blk.myRowId, blk.myColId);
			if (checkid == id)
				return blk;
		}
		//This should be error
		Debug.exit("Error in searching front block ("+rowId+","+colId+")\n"+toString());
		return null;
	}	
	
	//--------------------
	/**
	 * Check if the given block has front row/column block stored in the given list of blocks or not.
	 */
	@Inline
	private static def containBlockIn(src:MatrixBlock, blist:ArrayList[MatrixBlock], 
			select:(Int,Int)=>Int):Boolean {
		val itr = blist.iterator();
		while (itr.hasNext()){
			val blk = itr.next();
			val chkId = select(src.myRowId, src.myColId);
			val selId = select(blk.myRowId, blk.myColId);
			if (selId == chkId) return true;
		}
		return false;		
	}
			
	protected static def containBlockInRow(blk:MatrixBlock, blist:ArrayList[MatrixBlock]):Boolean = 
		containBlockIn(blk, blist, (r:Int,c:Int)=>r);
	protected static def containBlockInCol(blk:MatrixBlock, blist:ArrayList[MatrixBlock]):Boolean = 
		containBlockIn(blk, blist, (r:Int,c:Int)=>c);

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
	
	public def reset():void {
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
	@Inline 
	public final def selectCast(rootblk:MatrixBlock, colCnt:Int, select:(Int,Int)=>Int) {
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
		 val rootblk = this.findFrontBlock(rootbid, select);
		selectReduce(rootblk, colCnt, select, opFunc);
	}
	
	//---------------------------------------
	public def gridReduce(dstblk:MatrixBlock, datCnt:Int, 
			select:(Int,Int)=>Int, opFunc:(DenseMatrix,DenseMatrix, Int)=>DenseMatrix) {
					
		val dstden = dstblk.getMatrix() as DenseMatrix;
		val it = this.blocklist.iterator();
		val target = select(dstblk.myRowId, dstblk.myColId);
		while (it.hasNext()) {
			val blk = it.next();
			if (blk != dstblk) {
				val chkid = select(blk.myRowId, blk.myColId);
				if (target == chkid) {
					//Debug.flushln("Copy root to ("+blk.myRowId+","+blk.myColId+")");
					//rootblk.copyCols(0, colCnt, blk.getMatrix());
					opFunc(blk.getMatrix() as DenseMatrix, dstden, datCnt);
				}
			}
		}
	}
	
	public def rowReduce(dstblk:MatrixBlock, datCnt:Int, 
			opFunc:(DenseMatrix,DenseMatrix, Int)=>DenseMatrix) {
		gridReduce(dstblk, datCnt, (r:Int,c:Int)=>r, opFunc);
	}
	
	public def colReduce(dstblk:MatrixBlock, datCnt:Int, 
			opFunc:(DenseMatrix,DenseMatrix, Int)=>DenseMatrix) {
		gridReduce(dstblk, datCnt, (r:Int,c:Int)=>c, opFunc);
	}
	
	//======================================
	public def iterator() = blocklist.iterator();
	
	//======================================
	
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

	public def getBlockDataCount(bid:Int):Int {
		val blk = findBlock(bid);
		Debug.assure(blk!=null, "Cannot locate block "+bid+" in block set at " +here.id());
		return blk.getDataCount();
	}
	
	public def getBlockDataCountAt(blkidx:Int)=
		get(blkidx).getDataCount();
	
	public def getAllBlocksDataCount():Int {
		var dsz:Int =0;
		val blkitr = this.iterator();
		while (blkitr.hasNext()) {
			val blk = blkitr.next();
			dsz += blk.getDataCount();
		}
		return dsz;
	}	
	//=================================================
	public def getAllCommTime() :Long {
		
		val blkitr = this.iterator();
		var tt:Long = 0;
		while (blkitr.hasNext()) {
			val blk = blkitr.next();
			tt += blk.commTime;
		}
		return tt;	
	}
	
	public def getAllCalcTime() :Long {
		var tt:Long = 0;
		val blkitr = this.iterator();
		while (blkitr.hasNext()) {
			val blk = blkitr.next();
			tt += blk.calcTime;
		}
		return tt;
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
	
	public def printBlockMap() {
		var outstr:String="";
		
		if (blockMap==null) buildBlockMap();
		for (var r:Int=blockMap.region.min(0); r<=blockMap.region.max(0); r++) {
			for (var c:Int=blockMap.region.min(1); c<=blockMap.region.max(1); c++) {
				val b = blockMap(r, c);
				outstr +=("Block("+r+","+c+"):["+b.myRowId+","+b.myColId+"] ");
			}
			outstr += "\n";
		}
		Console.OUT.println(outstr);
		Console.OUT.flush();
	}
	
}