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
 * This class defines blocks are partitioned among all places in a grid-like.
 * It does not provide data distribution as Grid does for matrix data, but it defines 
 * methods to build maps from block IDs to place IDs. 
 */
public class DistGrid{
	public val dmap:DistMap;
	
	public def this(dm:DistMap) {
		dmap = dm;
	}

	public def this(matgrid:Grid, rowCs:Int, colCs:Int) {
		val nps   = rowCs * colCs;
		dmap  = new DistMap(matgrid.size, nps);
		
		Debug.assure(rowCs <= matgrid.numRowBlocks && 
				colCs <= matgrid.numColBlocks, 
				"Cannot distribute ("+matgrid.numRowBlocks+" x "+matgrid.numColBlocks+") blocks"+
				" over ("+rowCs+" x "+colCs+") places");
		//Remove this check for duplicated block matrix
		//Debug.assure(nps == Place.MAX_PLACES, 
		//		"Partitioning blocks error! Number of clusters "+nps+" is not same as places "+Place.MAX_PLACES);
		
		val blkgrid = new Grid(matgrid.numRowBlocks, matgrid.numColBlocks, rowCs, colCs);
				
		//This is not an efficient method, but not too much hurt on performance
		for (var cb:Int=0; cb<matgrid.numColBlocks; cb++) { 
			for (var rb:Int=0; rb<matgrid.numRowBlocks; rb++) {
				val pid = blkgrid.findBlock(rb, cb); 
				val bid = matgrid.getBlockId(rb, cb);
				dmap.set(bid, pid);
			}
		}
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
	
	public static def makeHorizon(g:Grid) = makeMaxRow(g, 1, Place.MAX_PLACES);
	
	public static def makeVertical(g:Grid) = makeMaxRow(g, Place.MAX_PLACES, Place.MAX_PLACES);
	
}
