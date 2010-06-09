package ssca2;

import x10.util.*;
class getStartLists  {
	
	public static def compute(G:defs.graph):  Pair[Double, Rail[defs.edge]!] {
		var elapsed_time: Double;
	
	val nthreads = util.x10_get_num_threads();
	val p_start = Rail.make[types.LONG_T](nthreads);
	val p_end = Rail.make[types.LONG_T](nthreads);
	val local_max = Rail.make[types.LONG_T](nthreads, (p: Int)=>-1);
	val maxWeight = Rail.make[types.LONG_T](1);
	val maxIntWtList: Rail[Rail[defs.edge]!]! = Rail.make[Rail[defs.edge]!](1);
	val m = G.m;
	val n = G.n;
	
	elapsed_time = util.x10_get_wtime();
	
	finish  {
		
		val c: Clock = Clock.make();
	foreach ((tid) in 0..nthreads-1)  clocked(c)  {
		var pCount: Int = 0;
	val chunkSize = n/nthreads;
	
	val tmpListSize = 1000;
	val pList = Rail.make[defs.edge](tmpListSize);
        x10.io.Console.OUT.println("point 0");
	
	for((i) in tid*chunkSize..(tid+1)*chunkSize-1) {
		val lo = (G.numEdges as Rail[types.LONG_T]!)(i);
		val hi = (G.numEdges as Rail[types.LONG_T]!)(i+1);
		for((j) in lo..hi-1) {
			if ((G.weight as Rail[types.LONG_T]!)(j) >  local_max(tid)) {
				local_max(tid) = (G.weight as Rail[types.LONG_T]!)(j); 
				pCount = 0;
				pList(pCount) = new defs.edge(i, (G.endV as Rail[types.VERT_T]!)(j), j, local_max(tid));
				pCount++;
			} else if ((G.weight as Rail[types.LONG_T]!)(j) == local_max(tid)){
				pList(pCount) = new defs.edge(i, (G.endV as Rail[types.VERT_T]!)(j), j, local_max(tid));
				pCount++;
			}
		}
	}

	
	p_end(tid) = pCount;
	p_start(tid)= 0;
	
	next;
	
	if (tid==0) {
		var tmp: types.LONG_T  = local_max(0);
	for((tid) in 1..nthreads-1) {
		if (local_max(tid) > tmp) tmp = local_max(tid);
	}
	
	maxWeight(0) = tmp;
	}
        x10.io.Console.OUT.println("point 2");
	
	next;
	
	if (maxWeight(0) != local_max(tid)) p_end(tid) = 0;
	
	next;
	
	if (tid==0) {
		//scan
		for((i) in 1..nthreads-1) {
			p_end(i) = p_end(i-1) + p_end(i);
			p_start(i) = p_end(i-1);
		}
	}
        x10.io.Console.OUT.println("point 3");
	
	next;
	
	if (tid==0) { 
		maxIntWtList(0) = Rail.make[defs.edge](p_end(nthreads-1));
	}
	
	next;

	for ((j) in p_start(tid)..p_end(tid)-1) {
		val startVertex = pList(j-p_start(tid)).startVertex;
		val endVertex = pList(j-p_start(tid)).endVertex;
		val e = pList(j-p_start(tid)).e;
		val w = pList(j-p_start(tid)).w;
		maxIntWtList(0)(j) = new defs.edge(startVertex, endVertex, e, w);
	}
        x10.io.Console.OUT.println("point 4");
	}
	
	c.drop();
	} 
	elapsed_time = util.x10_get_wtime() - elapsed_time;
	return Pair[Double, Rail[defs.edge]!](elapsed_time, maxIntWtList(0) as Rail[defs.edge]!);
	
	}
}; 
