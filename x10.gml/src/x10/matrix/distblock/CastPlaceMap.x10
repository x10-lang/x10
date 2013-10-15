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
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistMap;

public class CastPlaceMap {
	protected val castPlaceMap:HashMap[Long, Rail[Long]];

	public def this(castmap:HashMap[Long, Rail[Long]]) {
		castPlaceMap = castmap;
	}
	
	@Inline
	public static def buildPlaceList(g:Grid, dmap:DistMap, rootbid:Long, select:(Long,Long)=>Long):Rail[Long] {
		val alist = new ArrayList[Long]();
		val rowbid = g.getRowBlockId(rootbid);
		val colbid = g.getColBlockId(rootbid);
		//Check blocks in the same row, iterate all column ids
		//Check blocks in the same column, iterate all row ids
		val sz:Long = select(g.numColBlocks, g.numRowBlocks);
		//alist.add(here.id());
		for (var id:Long=0; id<sz; id++) {
			val bid = select(g.getBlockId(rowbid, id), g.getBlockId(id, colbid));
			val pid = dmap.findPlace(bid);
			if (! alist.contains(pid)&&pid!=here.id()) {
				alist.add(pid);
			}
		}
		val al = alist.toRail();
		return al;
	}
	
	@Inline	
	public static def make(g:Grid, dmap:DistMap, select:(Long,Long)=>Long) {
		val castmap = new HashMap[Long, Rail[Long]]();
		for (var bid:Long=0; bid<g.size; bid++) {
			if (dmap.blockmap(bid) != here.id()) continue;
			val id = select(g.getRowBlockId(bid), g.getColBlockId(bid));
			if (castmap.containsKey(id)) continue;
			
			val plclst = buildPlaceList(g, dmap, bid, select);
			castmap.put(id, plclst);
		}
		return new CastPlaceMap(castmap);
	}
	
    public static def buildRowCastMap(g:Grid, dmap:DistMap) = make(g, dmap, (r:Long,c:Long)=>r);
    public static def buildColCastMap(g:Grid, dmap:DistMap) = make(g, dmap, (r:Long,c:Long)=>c);
	
	public def getPlaceList(id:Long):Rail[Long] {
		val bval:Box[Rail[Long]] = castPlaceMap.get(id);
		return bval.value;
	}
}
