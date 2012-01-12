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

import x10.util.ArrayList;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

import x10.matrix.Debug;
import x10.matrix.block.Grid;
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

	// public def this() {
	// 	blocklist = new ArrayList[MatrixBlock]();
	// }
	// 
	// public def this(bl:ArrayList[MatrixBlock]) {
	// 	blocklist = bl;
	// }
	
	public def this(g:Grid, map:DistMap) {
		grid=g; dmap = map;
		blocklist = new ArrayList[MatrixBlock]();		
	}

	public def this(g:Grid, map:DistMap, bl:ArrayList[MatrixBlock]) {
		grid=g; dmap = map; blocklist = bl;
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
		return null;
	}
	
	public def find(bid:Int): MatrixBlock {
		val rid = grid.getRowBlockId(bid);
		val cid = grid.getColBlockId(bid);
		return find(rid, cid);
	}
	
	public def findPlace(bid:Int) = dmap.findPlace(bid);
	
	//---------------------------------------------------
	public def get(i:Int) = blocklist.get(i);
	
	public def getFirst() = blocklist.getFirst();
	
	public def getLocalBlockId(idx:Int) = dmap.placemap(here.id()).get(idx);
	
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
	public def clear():void {
		this.blocklist.clear();
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