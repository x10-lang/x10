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

import x10.util.ArrayList;

import x10.matrix.Debug;
import x10.matrix.MathTool;
import x10.matrix.block.Grid;



/**
 * This class represents how matrix blocks in a partition grid are distributed
 * among all places.
 * <p>
 * In a grid partitioning, all blocks are assigned with block numbers in a column-wise
 * fashion.  DistGrid provides a mechanism (or mapping functions) between a block number
 * and a place number.
 * 
 * <p> There are several mapping functions provided through this class,
 * The default one-2-one function mapps each block to the place with the place ID.
 * 
 * Propose function
 * 1) When partition blocks > places, partitioning blocks and grouping adjacent blocks
 * to the same place in the same way that matrix is partitioned.
 * 
 * 2) Mapping list: given a block ID ==> a place ID.
 * Mapping list is a m-to-n function, where m>=n, m is number of blocks, and n is number of 
 * places.
 * 
 * In this implementation, one hash table and one list array are used, 
 * The hash table maps place IDs to block IDs, where place ID is key and
 * block ID is value. The list array maps block IDs to place IDs.
 * Therefore, it is easy for iteration over blocks as well as over places. 
 * However, these two maps must keep consistance.
 */

public class DistMap(numBlock:Int, numPlace:Int)  {
	
	public val blockmap:Array[Int](1);            //mapping block ID to its place ID
	public val placemap:Array[ArrayList[Int]](1); //mapping place ID to list of block IDs
	
	public def this(numBlk:Int, numPlc:Int) {
		property(numBlk, numPlc);

		blockmap = new Array[Int](numBlk, -1);
		placemap = new Array[ArrayList[Int]](numPlc, (i:Int)=>(new ArrayList[Int]()));
	}
	
	public def this(blkmap:Array[Int](1), plcmap:Array[ArrayList[Int]](1)) {
		property(blkmap.size, plcmap.size);
		blockmap = blkmap;
		placemap = plcmap;
	}
	//==========================================
	public static def make(numBlk:Int):DistMap {
		return new DistMap(numBlk, Place.MAX_PLACES);
	}
	
	public static def make(numBlk:Int, mapfunc:(Int)=>Int) {
		val dmap = make(numBlk);
		for (var b:Int=0; b<numBlk; b++) 
			dmap.add(b, mapfunc(b));
		return dmap;
	}
	
	public static def makeCylic(numBlk:Int, numPlc:Int) = make(numBlk, (i:Int)=>i%numPlc);
	public static def makeUnique() = make(Place.MAX_PLACES, (i:Int)=>i);
	public static def makeUnique(numBlk:Int) = make(numBlk, (i:Int)=>i);
	
	public static def makeConstant(numBlk:Int) = make(numBlk, (i:Int)=>0);
	public static def makeConstant(numBlk:Int, p:Int) = make(numBlk, (i:Int)=>p); 
	
	//==========================================
	
	//==========================================
	/**
	 * Add block ID and place ID in mapping
	 */
 	public def add(blkID:Int, plcID:Int) {

 		blockmap(blkID)=plcID;
 		val bset = placemap(plcID);
 		bset.add(blkID);
 	}

 	
 	/**
 	 * Find place ID for a given block ID
 	 */
 	public def findPlace(blkID:Int):Int {
 		return blockmap(blkID);
 	}
	
 	/**
 	 * Get block ID set mapped to the same place
 	 */
 	public def getBlockList(plcID:Int):ArrayList[Int] {
 		return placemap(plcID);
 	}
 	
 	/**
 	 * Return iterator on blocks within the specified place
 	 */
 	public def getBlockIterator(plcID:Int) : Iterator[Int] {
 		val blst = getBlockList(plcID);
 		return blst.iterator();
 	}
 	//--------------------------------------
 	//
 	//--------------------------------------
 	public def getPlaceListInRing(grid:Grid, rootbid:Int, select:(Int,Int)=>Int):Array[Int](1) {
 		val alist = new ArrayList[Int]();
 		val rowbid = grid.getRowBlockId(rootbid);
 		val colbid = grid.getColBlockId(rootbid);
 		//Check blocks in the same row, iterate all column ids
 		//Check blocks in the same column, iterate all row ids
 		val targetsize = select(grid.numColBlocks, grid.numRowBlocks);
 		
 		alist.add(here.id());
 		for (var id:Int=0; id<targetsize; id++) {
 			val pid = select(
 					findPlace(grid.getBlockId(rowbid, id)), 
 					findPlace(grid.getBlockId(id, colbid)));
 			if (! alist.contains(pid))
 				alist.add(pid);
 		}
 		return alist.toArray();
 	}
 	//--------------------------------------
 	// Modification
 	//--------------------------------------
 	
 	/**
 	 * Map block to a new place
 	 */
 	public def changeBlock(blkID:Int, plcID:Int) {
 		val oldpid = blockmap(blkID);
 		//Set the new place ID for block
 		blockmap(blkID) = plcID;
	
 		if (oldpid >= 0 ) {
 			val oldlst = getBlockList(oldpid);
 			oldlst.remove(blkID);
 		} 
 		
 		val newlst = getBlockList(plcID);
 		newlst.add(blkID);
 	}
 	//=======================================
 	
 	// public def transEquals(g:Grid, tmap:DistMap):Boolean {
 	// 	var retval:Boolean = true;
 	// 	val tg = g.newT();
 	// 	for (var rb:Int=0; rb<g.numRowBlocks&&retval; rb++) {
 	// 		for (var cb:Int=0; cb<g.numColBlocks&&retval; cb++) {
 	// 			val bid = g.getBlockId(rb, cb);
 	// 			val tbid = tg.getBlockId(cb,rb);
 	// 			retval &= (blockmap(bid)==tmap.blockmap(tbid));
 	// 		}
 	// 	}
 	// 	return retval;
 	// }
 	
 	//=======================================
 	public def equals(that:DistMap) : Boolean {
 		var retval:Boolean = true;
 		
 		if (this==that) return true;
 		if (this.numBlock!=that.numBlock) return false;

 		for (var i:Int=0; i<blockmap.size && retval; i++) {
 			retval &= this.blockmap(i)==that.blockmap(i);
 		}
 		return retval;
 	}
}