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

import x10.matrix.Debug;
import x10.matrix.MathTool;

import x10.matrix.block.Grid;

/**
 * A DistGrid instance specifies how blocks are distributed among places in a grid.
 * DistGrid uses integer array to map block IDs to place IDs.
 * DistGrid is a special case of distribution map, in which matrix blocks in a partition
 * grid is distributed in places of grid. Blocks neighoring in partitioning grid are
 * either in the same place or neighering places.
 */
public class DistGrid(numRowPlaces:Int, numColPlaces:Int) {
	
	//==================================
	public val dmap:DistMap;
	public val placeGrid:Grid;
	
	//===================================
	public def this(plzGrid:Grid,dp:DistMap) {
		property(plzGrid.numRowBlocks, plzGrid.numColBlocks);
		placeGrid = plzGrid;
		dmap = dp;
	}
	
	public def this(matgrid:Grid, nRowPs:Int, nColPs:Int) {
		property(nRowPs, nColPs);
		dmap = new DistMap(matgrid.size, nRowPs*nColPs);

		Debug.assure(
				nRowPs <= matgrid.numRowBlocks && nColPs <= matgrid.numColBlocks, 
				"Cannot distribute ("+matgrid.numRowBlocks+" x "+matgrid.numColBlocks+") blocks"+
				" over ("+nRowPs+" x "+nColPs+") places");
		placeGrid = new Grid(matgrid.numRowBlocks, matgrid.numColBlocks, nRowPs, nColPs);
		
		//This is not an efficient method, 
		for (var cb:Int=0; cb<matgrid.numColBlocks; cb++) { 
			for (var rb:Int=0; rb<matgrid.numRowBlocks; rb++) {
				val pid = placeGrid.findBlock(rb, cb); 
				val bid = matgrid.getBlockId(rb, cb);
				dmap.blockmap(bid) = pid;
			}
		}
	}
	//============================================================
	
	public static def compPartition(num:Int, blks:Int, plcs:Int) : Array[Int](1){rail} {
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
	 * @param maxRowClusters   the maximum number clusters in a row
	 * @param totalClusters    the total clusters used in partitioning blocks
	 * @return                 the map of block IDs to place IDs.
	 */	
	public static def makeMaxRow(matgrid:Grid, maxRowPs:Int, totalPs:Int) {
		val nRowBs    = matgrid.numRowBlocks;
		var rowPs:Int = nRowBs < maxRowPs ? nRowBs : maxRowPs;
		while (totalPs % rowPs != 0) { rowPs--; }
		if (rowPs == 0) rowPs = 1;
		val colPs = totalPs/rowPs;
		return new DistGrid(matgrid, rowPs, colPs);
	}
	
	public static def makeMaxCol(matgrid:Grid, maxColPs:Int, totalPs:Int) {
		val nColBs    = matgrid.numColBlocks;
		var colPs:Int = nColBs < maxColPs ? nColBs : maxColPs;
		while (totalPs % colPs != 0) { colPs--; }
		if (colPs == 0) colPs = 1;
		val rowPs = totalPs/colPs;
		return new DistGrid(matgrid, rowPs, colPs);
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
	//==================================================================
	/**
	 * Aggregate all segments in the same place to one segment,
	 * when computing the number of rows or columns within a place
	 */
	private def compLocalRows(matGrid:Grid, rowPid:Int):Int {
		//val rowPid = placeGrid.getRowBlockId(pid);
		val sttRowBlk = placeGrid.startRow(rowPid);
		var rowtt:Int = 0;
		for (var rowbid:Int=0; rowbid<placeGrid.rowBs(rowPid); rowbid++) {
			rowtt += matGrid.rowBs(rowbid+sttRowBlk);
		}
		return rowtt;
	}

	private def compLocalCols(matGrid:Grid, colPid:Int):Int {
		//val colPid = placeGrid.getColBlockId(pid);
		val sttColBlk = placeGrid.startCol(colPid);
		var coltt:Int = 0;
		for (var colbid:Int=0; colbid<placeGrid.colBs(colPid); colbid++) {
			coltt += matGrid.colBs(colbid+sttColBlk);
		}
		return coltt;
	}
	//=================================
	
	//------------
	public def getAggRowBs(grid:Grid):Array[Int](1){rail}{
		val rbs = new Array[Int](numRowPlaces, (i:Int)=>compLocalRows(grid, i));
		return rbs;
	}
	
	public def getAggColBs(grid:Grid):Array[Int](1){rail}{
		val cbs = new Array[Int](numColPlaces, (i:Int)=>compLocalCols(grid, i));
		return cbs;
	}
}
