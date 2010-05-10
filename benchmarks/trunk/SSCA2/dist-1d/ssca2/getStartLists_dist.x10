package ssca2;

import x10.util.*;
class getStartLists_dist  {
	
	public static def compute(pg:defs.pGraph, place: Int, world: Comm!):  Rail[defs.edge]! {
	
	var local_max: Int = -1;
        var pCount: Int = 0;
        val tmpListSize = 1000;
        val pList = new GrowableRail[defs.edge](tmpListSize);

            val pg_here = pg.restrict_here();
            for((vertex) in  pg_here.vertices) {
		val lo = pg_here.numEdges(vertex);
		val hi = pg_here.numEdges(vertex+1)-1;
		for((j) in lo..hi) {
			if (pg_here.weight(j) >  local_max){
				local_max = pg_here.weight(j); 
				pCount = 0;
                                pList.setLength(0);
				pList.add(defs.edge(vertex, pg_here.endV(j), j, local_max));
				pCount++;
			} else if (pg_here.weight(j) == local_max){
				pList.add(defs.edge(vertex, pg_here.endV(j), j, local_max));
				pCount++;
			}
		}
	    }

	
        val max = world.max(local_max);

        if (max != local_max) pCount = 0;

        val maxList = world.allgatherv(pList, pCount as Long);
       
        return maxList;	
	
	}
	
}; 
