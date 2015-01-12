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

package x10.matrix.distblock;

import x10.matrix.util.Debug;
import x10.matrix.block.Grid;

/**
 * This class defines how blocks are distributed among all places.
 * It does not provide data distribution function, but define 
 * distribution map from block IDs to place IDs. 
 * 
 * <p> When number of blocks > number of places, blocks which are assigned to
 * the same place form a cluster. This class partitions blocks among places
 * in a grid map, same as the matrix is partitioned into blocks.
 */
public class BlockGrid{
	public val dmap:DistMap;
	
	public def this(dm:DistMap) {
		dmap = dm;		
	}
	
	/**
	 * Partitioning all blocks among all places. All matrix blocks are specified 
	 * by matrix partitioning of g.  The blocks are partitioned among all places
	 * in the same way as matrix is partitioned.
	 * 
	 * @param  g     the partitioning blocks
	 * @return       the map of block IDs to place IDs.
	 */
	public static def make(g:Grid) = make(g, Math.sqrt(Place.numPlaces()) as Long, Place.numPlaces());
	
	/**
	 * Partition all blocks into clusters, in the same way as matrix is partitioned to blocks.
	 * 
	 * @param matgrid          the partitioning matrix in blocks specified by Grid
	 * @param maxRowClusters   the max number clusters in a row
	 * @param totalClusters    the total clusters used in partitioning blocks
	 * @return                 the map of block IDs to place IDs.
	 */	
	public static def make(matgrid:Grid, maxRowClusters:Long, totalClusters:Long):BlockGrid {
		val nbs   = matgrid.size;
		val sqmap = DistMap.make(nbs);		
		val nps   = Place.numPlaces();
		Debug.assure(totalClusters <= nps, 
				"Partitioning blocks error - too many clusters :"+totalClusters+" cannot be bigger than total places "+nps);
		
		val blcgrid = Grid.makeMaxRow(matgrid.numRowBlocks, matgrid.numColBlocks, 
				maxRowClusters, totalClusters);
		
		Debug.assure(blcgrid.numRowBlocks <= matgrid.numRowBlocks && blcgrid.numColBlocks<= matgrid.numColBlocks,
				"Partitioning blocks error: "+
				 "("+blcgrid.numRowBlocks+" x "+blcgrid.numColBlocks+") places cannot partition "+
				 "("+matgrid.numRowBlocks+" x "+matgrid.numColBlocks+") blocks");
		
		//FIX: dont know what the add call is supposed to accomplish? 
		// the method is not defined on DistMap.
		//This is not an efficient method, but not much hurt on performance
		for (var cb:Long=0; cb<matgrid.numColBlocks; cb++) { 
			for (var rb:Long=0; rb<matgrid.numRowBlocks; rb++) {
				val pid = blcgrid.findBlock(rb, cb); 
				val bid = matgrid.getBlockId(rb, cb);
				//sqmap.add(bid, pid);
			}
		}
		val result = new BlockGrid(sqmap);
		throw new Error("FIXME");
		//return result;
	}

	/**
	 * Partition blocks into (1, num_places) clusters among all places for the given matrix partitioning.
	 */
	public static def makeHorizon(g:Grid) = make(g, 1, Place.numPlaces());
	

	/**
	 * Partition blocks into (num_places, 1) clusters among all places for the given matrix partitioning.
	 */
	public static def makeVertical(g:Grid) = make(g, Place.numPlaces(), Place.numPlaces());
}
