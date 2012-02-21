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

import x10.compiler.Inline;

import x10.util.ArrayList;
import x10.util.HashMap;
import x10.util.Box;

import x10.matrix.Debug;
import x10.matrix.MathTool;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistMap;

/**
 * 
 */
public class CastPlaceMap {
	
	protected val castPlaceMap:HashMap[Int, Array[Int](1)];
	//======================================================
		
	public def this(castmap:HashMap[Int, Array[Int](1)]) {
		castPlaceMap = castmap;
	}
	
	//===========================================
	
	@Inline
	public static def buildPlaceList(g:Grid, dmap:DistMap, rootbid:Int, select:(Int,Int)=>Int):Array[Int](1) {
		val alist = new ArrayList[Int]();
		val rowbid = g.getRowBlockId(rootbid);
		val colbid = g.getColBlockId(rootbid);
		//Check blocks in the same row, iterate all column ids
		//Check blocks in the same column, iterate all row ids
		val sz:Int = select(g.numColBlocks, g.numRowBlocks);
		//alist.add(here.id());
		for (var id:Int=0; id<sz; id++) {
			val bid = select(g.getBlockId(rowbid, id), g.getBlockId(id, colbid));
			val pid = dmap.findPlace(bid);
			if (! alist.contains(pid)) {
				alist.add(pid);
			}
		}
		val al = alist.toArray();
		return al;
	}
	
	//-----------------------
	@Inline	
	public static def make(g:Grid, dmap:DistMap, select:(Int,Int)=>Int) {
		val castmap = new HashMap[Int, Array[Int](1)]();
		for (var bid:Int=0; bid<g.size; bid++) {
			if (dmap.blockmap(bid) != here.id()) continue;
			val id = select(g.getRowBlockId(bid), g.getColBlockId(bid));
			if (castmap.containsKey(id)) continue;
			
			val plclst = buildPlaceList(g, dmap, bid, select);
			castmap.put(id, plclst);
		}
		return new CastPlaceMap(castmap);
	}
	
	//----------------------------------------------
	
	public static def buildRowCastMap(g:Grid, dmap:DistMap) = make(g, dmap, (r:Int,c:Int)=>r);
	public static def buildColCastMap(g:Grid, dmap:DistMap) = make(g, dmap, (r:Int,c:Int)=>c);
	
	//-----------------------------------------------
	public def getPlaceList(id:Int): Array[Int](1) {
		val bval:Box[Array[Int](1)] = castPlaceMap.get(id);
		return bval.value;
	}
	
	//------------------------------------------------
	
}