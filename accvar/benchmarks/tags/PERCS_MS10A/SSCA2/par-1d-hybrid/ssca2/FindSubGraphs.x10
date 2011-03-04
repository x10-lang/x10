package ssca2;

import x10.util.*;
import x10.compiler.Native;
import x10.util.concurrent.AtomicBoolean;
import x10.util.concurrent.AtomicInteger;

class FindSubGraphs {
	
	public static def compute (pg: Defs.pGraph, world: Comm!, sourceList: Rail[Defs.edge]!, SubGraphPathLength: Int, nthreads: Int) {
		
		val nplaces = Place.MAX_PLACES;
		
		val L = new GrowableRail[Types.VERT_T](0);
		
		val N_local = Rail.make[Rail[GrowableRail[Types.VERT_T]]](Place.MAX_PLACES, (i: Int)=>Rail.make[GrowableRail[Types.VERT_T]](nthreads, (i: Int)=>new GrowableRail[Types.VERT_T](0)));
		val c_local = Rail.make[Rail[Types.VERT_T]](Place.MAX_PLACES, (i:Int)=>Rail.make[Int](nthreads+1, (i:Int)=>0));
		val count = Rail.make[Rail[Types.VERT_T]](Place.MAX_PLACES, (i:Int)=>Rail.make[Int](nthreads+1, (i:Int)=>0));
		val nVerticesPerThread = Rail.make[Types.INT_T](nthreads, (i:Int)=>0);
		val N = Rail.make[GrowableRail[Types.VERT_T]](Place.MAX_PLACES, (i:Int)=>new GrowableRail[Types.VERT_T](count(i)(nthreads)));
		
		for ((elem) in 0..sourceList.length-1) {
			val srcEdge = sourceList(elem);
			
			for (var tid: Int = 0; tid < nthreads; tid++) nVerticesPerThread(tid) = 0;
			
			val pg_here = pg.restrict_here();
			val vertices = pg_here.vertices;
			val visited = new LinearArray[AtomicBoolean](vertices, (i:Int)=>new AtomicBoolean(false));
			
			val source = srcEdge.endVertex;  
			if (pg.owner(srcEdge.startVertex) == here) { if(source != srcEdge.startVertex) visited(srcEdge.startVertex).set(true); nVerticesPerThread(0)++; }
			
			L.setLength(0);
			if (pg.owner(source) == here) { L.add(source); }
			var l: Int = 0;
			while (l <= SubGraphPathLength) {
				
				for (var place: Int = 0; place < Place.MAX_PLACES; place++) {
					for (var tid: Int = 0; tid < nthreads; tid++) {
						N_local(place)(tid).setLength(0);
						c_local(place)(tid) = 0;
						count(place)(tid) = 0;
					}
				}
				
				val frontier: GrowableRail[Types.VERT_T]! = L;
				val flength = frontier.length();
				val frontierSize = world.sum(flength);
				//x10.io.Console.OUT.println("phase " + l + "  " + frontierSize + " " + flength);
				if (frontierSize == 0) break;
				
				finish for (var id: Int = 0; id < nthreads; id++) { 
					val tid = id;
					async {
						val chunkSize = flength / nthreads;
						val i_begin = tid*chunkSize;
						val i_end = tid == nthreads-1 ? flength : (tid+1)*chunkSize;
						
						//x10.io.Console.OUT.println("tid " + tid + " " + i_begin + " " + i_end);
						for (var i_var: Int = i_begin; i_var < i_end; i_var++) {
							val i = i_var; 
							val vertex = frontier(i);
							
							val ret = visited(vertex).compareAndSet(false, true);
							if (!ret) continue; 
							
							val lo = pg_here.numEdges(vertex);
							val hi = pg_here.numEdges(vertex+1)-1;
							
							nVerticesPerThread(tid)++;
							
							for (var k: Int = lo; k <=hi; k++) {
								val neighbor = pg_here.endV(k);
								val owner = pg.owner(neighbor);
								N_local(owner.id)(tid).add(neighbor);
								c_local(owner.id)(tid)++;
							}
						}}}
				
				if (l == SubGraphPathLength)  break;
				
				for (var place: Int = 0; place < nplaces; place++) {
					for (var id: Int = 1; id <= nthreads; id++) { 
						count(place)(id) = count(place)(id-1) + c_local(place)(id-1);
					}
				}
				
				for (var i: Int =0 ;i < Place.MAX_PLACES; i++) N(i).setLength(count(i)(nthreads));
				finish for (var id: Int = 0; id < nthreads; id++) { 
					val tid = id;
					async {
						for (var place: Int = 0; place < nplaces; place++) {
							var i: Int = 0;
						for (var offset: Int = count(place)(tid); offset < count(place)(tid+1); offset++) {
							N(place)(offset) = N_local(place)(tid)(i++);
							//x10.io.Console.OUT.println("vertex " + tid + "  " + offset +" " + N(place)(offset));
						}
						} 
					}
				}
				
				world.alltoallv[Types.VERT_T](N, L);
				
				l = l  + 1;
				
			}
			
			var nVerticesPerPlace: Int = 0;
			for (var tid: Int = 0; tid < nthreads; tid++) { 
				nVerticesPerPlace += nVerticesPerThread(tid);
			}
			val nVertices = world.sum(nVerticesPerPlace);
			
			if (here.id == 0)
				x10.io.Console.OUT.println("Search from <" + srcEdge.startVertex + " ," + srcEdge.endVertex + " > " + "number of vertices visited: " + nVertices);
			world.barrier();
		}
		for ((i) in 0..Place.MAX_PLACES-1) Runtime.deallocObject(N(i));
		Runtime.deallocObject(N);         
		Runtime.deallocObject(L);         
		
	}
}

