/*
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

import x10.matrix.Debug;
import x10.matrix.MathTool;

import x10.matrix.block.Grid;

/**
 * A DistGrid instance specifies how blocks are distributed among places in a grid-like square.
 * DistGrid uses integer array to map block IDs to place IDs.
 */
public class DistGrid(numRowPlaces:Int, numColPlaces:Int) {
	public val dmap:DistMap;
	
	public def this(nrp:Int, ncp:Int, dm:DistMap) {
		property(nrp, ncp);
		dmap = dm;
	}

	public def this(matgrid:Grid, rowPs:Int, colPs:Int) {
		property(rowPs, colPs);
		val nps   = rowPs * colPs;
		dmap  = new DistMap(matgrid.size, nps);
		
		Debug.assure(rowPs <= matgrid.numRowBlocks && 
				colPs <= matgrid.numColBlocks, 
				"Cannot distribute ("+matgrid.numRowBlocks+" x "+matgrid.numColBlocks+") blocks"+
				" over ("+rowPs+" x "+colPs+") places");
		//Remove this check for duplicated block matrix
		//Debug.assure(nps == Place.MAX_PLACES, 
		//		"Partitioning blocks error! Number of clusters "+nps+" is not same as places "+Place.MAX_PLACES);
		
		val blkgrid = new Grid(matgrid.numRowBlocks, matgrid.numColBlocks, rowPs, colPs);
				
		//This is not an efficient method, 
		for (var cb:Int=0; cb<matgrid.numColBlocks; cb++) { 
			for (var rb:Int=0; rb<matgrid.numRowBlocks; rb++) {
				val pid = blkgrid.findBlock(rb, cb); 
				val bid = matgrid.getBlockId(rb, cb);
				dmap.set(bid, pid);
			}
		}
	}
	//============================================================
	
	public static def compPartition(num:Int,blks:Int, plcs:Int) : Array[Int](1){rail} {
		Debug.assure(num>=blks&&blks>=plcs, 
				"Unsatisfied partitioning - Total:"+num+" >= Blocks:"+blks+" >= Places:"+plcs);
		val szlist:Array[Int](1){rail} = new Array[Int](blks);
		var cnt:Int =0;
		for (var p:Int=0; p<plcs; p++) {
			val tt = Grid.compBlockSize(num, plcs, p);
			val nb = Grid.compBlockSize(blks, plcs, p);
			for (var b:Int=0; b<nb; b++, cnt++){
				szlist(cnt) = Grid.compBlockSize(tt, nb, b);
			}
		}
		//Debug.flushln("Partition "+num+" to "+blks+" segs : " + szlist.toString());
		return szlist;
	}
	
	/**
	 * Creat grid partitioning with balanced number of rows and columns among places.
	 */
	public static def makeGrid(M:Int, N:Int, blkM:Int, blkN:Int, plcM:Int, plcN:Int): Grid {
		val rowBs = compPartition(M, blkM, plcM);
		val colBs = compPartition(N, blkN, plcN);
		
		return new Grid(M, N, rowBs, colBs);
	}
	
	//=================================================
	//
	//=================================================
	/**
	 * Partitioning all blocks among all places. All matrix blocks are specified 
	 * by matrix partitioning of g.  The blocks are partitioned among all places
	 * in the same way as matrix is partitioned.
	 * 
	 * @param  g     the partitioning blocks
	 * @return       the map of block IDs to place IDs.
	 */
	public static def make(g:Grid) = makeMaxRow(g, Math.sqrt(Place.MAX_PLACES) as Int, Place.MAX_PLACES);
		
	
	/**
	 * Partitioning all blocks to clusters, while maximizing number of groups (cluster) in row.
	 * 
	 * @param matgrid          the partitioning matrix in blocks specified by Grid
	 * @param maxRowClusters   the max number clusters in a row
	 * @param totalClusters    the total clusters used in partitioning blocks
	 * @return                 the map of block IDs to place IDs.
	 */	
	public static def makeMaxRow(matgrid:Grid, maxRowCs:Int, totalCs:Int) {
		val nRowBs    = matgrid.numRowBlocks;
		var rowCs:Int = nRowBs < maxRowCs ? nRowBs : maxRowCs;
		while (totalCs % rowCs != 0) { rowCs--; }
		if (rowCs == 0) rowCs = 1;
		val colCs = totalCs/rowCs;
		return new DistGrid(matgrid, rowCs, colCs);
	}
	
	public static def makeMaxCol(matgrid:Grid, maxColCs:Int, totalCs:Int) {
		val nColBs    = matgrid.numColBlocks;
		var colCs:Int = nColBs < maxColCs ? nColBs : maxColCs;
		while (totalCs % colCs != 0) { colCs--; }
		if (colCs == 0) colCs = 1;
		val rowCs = totalCs/colCs;
		return new DistGrid(matgrid, rowCs, colCs);
	}
	
	public static def makeHorizontal(g:Grid) = makeMaxRow(g, 1, Place.MAX_PLACES);
	
	public static def makeVertical(g:Grid) = makeMaxRow(g, Place.MAX_PLACES, Place.MAX_PLACES);
	//-------------------------------------------------------------
	public static def isHorizontal(g:Grid, dmap:DistMap):Boolean {
		for (var c:Int=0; c<g.numColBlocks; c++) {
			val bid0 = g.getBlockId(0, c);
			val pid0 = dmap.findPlace(bid0);
			for (var r:Int=1; r<g.numRowBlocks; r++) {
				val bid = g.getBlockId(r,c);
				val pid = dmap.findPlace(bid);
				if (pid != pid0) return false;
			}
		}
		return true;
	}

	/**
	 * Check vertical distribution of blocks. 
	 */
	public static def isVertical(g:Grid, dmap:DistMap):Boolean {
		for (var r:Int=0; r<g.numRowBlocks; r++) {
			val bid0 = g.getBlockId(r, 0);
			val pid0 = dmap.findPlace(bid0);
			for (var c:Int=1; c<g.numColBlocks; c++) {
				val bid = g.getBlockId(r,c);
				val pid = dmap.findPlace(bid);
				if (pid != pid0) return false;
			}
		}
		return true;
	}
	
	/**
	 * Aggregate all segments in the same place to one segment,
	 * when computing the number of rows or columns within a place
	 */
	public static def compLocalRows(grid:Grid, blkmap:DistMap, pid:Int):Int {
		var rowtt:Int = 0;
		
		for (var rid:Int=0; rid<grid.numRowBlocks; rid++) {
			val bid = grid.getBlockId(rid, 0);
			if (blkmap.findPlace(bid) == pid) rowtt += grid.rowBs(rid);
		}
		return rowtt;
	}

	public static def compLocalCols(grid:Grid, blkmap:DistMap, pid:Int):Int {
		var coltt:Int = 0;
		for (var cid:Int=0; cid<grid.numColBlocks; cid++) {
			val bid = grid.getBlockId(0, cid);
			if (blkmap.findPlace(bid) == pid) coltt += grid.colBs(cid);
		}
		return coltt;
	}
	//------------
	public static def getAggRowBs(grid:Grid, distgrid:DistGrid):Array[Int](1){rail}{
		val rbs = new Array[Int](distgrid.numRowPlaces, (i:Int)=>compLocalRows(grid, distgrid.dmap, i));
		return rbs;
	}
	
	public static def getAggColBs(grid:Grid, distgrid:DistGrid):Array[Int](1){rail}{
		val cbs = new Array[Int](distgrid.numColPlaces, (i:Int)=>compLocalCols(grid, distgrid.dmap, i));
		return cbs;
	}
	//-------------
	public static def getAggRowBs(numRowPlaces:Int, grid:Grid, dmap:DistMap):Array[Int](1){rail}{
		val rbs = new Array[Int](numRowPlaces, (i:Int)=>compLocalRows(grid, dmap, i));
		return rbs;
	}
	
	public static def getAggColBs(numColPlaces:Int, grid:Grid, dmap:DistMap):Array[Int](1){rail}{
		val cbs = new Array[Int](numColPlaces, (i:Int)=>compLocalCols(grid, dmap, i));
		return cbs;
	}
	
	
}
