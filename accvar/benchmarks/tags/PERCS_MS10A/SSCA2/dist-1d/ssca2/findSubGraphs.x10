package ssca2;

import x10.util.*;
import x10.util.concurrent.AtomicBoolean;

class findSubGraphs  {
	
	public static def compute(G: defs.graph, maxIntWtList: Rail[defs.edge]!) : Double {
		
		val GLOBALS = at (defs.container) {defs.container.globals};
		val n = GLOBALS.N;
		val nthreads = util.x10_get_num_threads();
		val numPhases = GLOBALS.SubGraphPathLength + 1;
		val S: Rail[types.VERT_T]! = Rail.make[types.VERT_T](n);
		val visited: Rail[AtomicBoolean!]! = Rail.make[AtomicBoolean!](n, (Int)=>new AtomicBoolean(false));
		val start: Rail[types.LONG_T]! = Rail.make[types.LONG_T](numPhases+2);
		val pSCount: Rail[types.LONG_T]! = Rail.make[types.LONG_T](nthreads+1);
		
		val maxIntWtListSize = maxIntWtList.length;
		
		var elapsed_time: Double = util.get_seconds();
		
		val count  = Rail.make[Int](1, (Int)=>0);
		val phase_num =  Rail.make[Int](1, (Int)=>0);
		
		finish  {
			val sync: Clock = Clock.make();
		
		//x10.io.Console.OUT.println("before for");
		foreach ((tid) in 0..nthreads-1) /* clocked(sync) */  {
			
			//x10.io.Console.OUT.println("enter");
			
			
			for ((search_num) in 0..maxIntWtListSize-1) {
				
				//next;
				if(tid==0) { 
					for((i) in 0..visited.length-1) visited(i).set(false);
					S(0) = maxIntWtList(search_num).startVertex;
					S(1) = maxIntWtList(search_num).endVertex;
					visited(S(0)).set(true);
					visited(S(1)).set(true);
					count(0) = 2;
					phase_num(0) = 1;
					start(0) = 0;
					start(1) = 1;
					start(2) = 2;
				}
				
				//next;
				
				while (phase_num(0) <= GLOBALS.SubGraphPathLength) {
					val  pS = new GrowableRail[types.VERT_T]();
					var pCount: Int = 0;
					val iterSize = start(phase_num(0)+1) - start(phase_num(0));
					val extra_offset = tid >= (iterSize % nthreads)  ? iterSize % nthreads : 0;
					val extra_iter = tid < (iterSize % nthreads)  ?  1 : 0;
					val chunkSize = iterSize/nthreads + extra_iter;
					
					val lo = extra_offset + start(phase_num(0)) +  tid*chunkSize;
					val hi = Math.min(start(phase_num(0)+1)-1, lo + chunkSize-1);
					
					//x10.io.Console.OUT.println(" lo..hi " + tid + " " + lo + " "  + hi);
					
					for ((vert) in  lo..hi) {
						val v = S(vert);
						val lo_1 = (G.numEdges as Rail[types.LONG_T]!)(v);
						val hi_1 = (G.numEdges as Rail[types.LONG_T]!)(v+1);
						for((j) in lo_1..hi_1-1){
							val w = (G.endV as Rail[types.LONG_T]!)(j);
							if (v==w) continue;
							//x10.io.Console.OUT.println("phase: " + phase_num(0) + " " + v + " " + w + " " + visited(w) + " " + pCount);
							
							
							val ret = visited(w).compareAndSet(false, true);
							if (ret) {
								pS.add(w);
								pCount++;
							}
							
							//atomic {
							//	if (!visited(w)) {
							//		visited(w) = true;
							//		pS.add(w);
							//	        pCount++;
							//	}
							//}
						}
					}
					
					//next;
					
					pSCount(tid+1) = pCount;
					
					//next;
					
					if (tid==0) {
						pSCount(0) = start(phase_num(0)+1);
						for ((k) in 1..nthreads) pSCount(k) = pSCount(k-1)  + pSCount(k);
						start(phase_num(0)+2) = pSCount(nthreads);
						count(0) = pSCount(nthreads);
						phase_num(0)++;
					}
					
					//next;
					
					for ((k) in pSCount(tid)..pSCount(tid+1)-1) {
						S(k) = pS(k-pSCount(tid));
					}
					
					//next;
				} /* End of search */
				
				if (tid==0) {
					x10.io.Console.OUT.println("Search from <" + S(0) + " ," + S(1) + " > " + "number of vertices visited: " + count(0));
				}
			}
			//next;
		}
		//sync.drop();
		}
		
		elapsed_time = util.get_seconds() - elapsed_time;
		return elapsed_time;
	}
	
};
