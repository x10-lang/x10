package ssca2;

import x10.util.*;
public class computeGraph  {
	
	public static def compute (SDGdata:defs.graphSDG): Pair[Double,defs.graph] {
		val n = defs.container.globals.N;
		val m = defs.container.globals.M;
		val nthreads = util.x10_get_num_threads();
		val endV = Rail.make[types.VERT_T](m);
		val degree = Rail.make[types.LONG_T](n);
		val numEdges = Rail.make[types.LONG_T](n+1);
		val pos = Rail.make[types.LONG_T](m);
		val w = Rail.make[types.WEIGHT_T](m);
		val pSums = Rail.make[types.LONG_T](nthreads);
		
		
		var time: Double = util.x10_get_wtime();
		
		//x10.io.Console.OUT.println("computeGraph " + "loop 1");
		finish {
			
			c: Clock = Clock.make();
		
		foreach((tid) in 0..nthreads-1)  clocked(c)  {
			
			val chunkSize = m/nthreads;
			
			for ((i) in tid*chunkSize..(tid+1)*chunkSize-1) {
				val u = (SDGdata.startVertex as Rail[types.VERT_T]!)(i);
				atomic {pos(i) = degree(u); degree(u)++;}
			}
			
			next;
			
			util.prefix_sums(degree, numEdges, pSums,n, tid);
			//x10.io.Console.OUT.println("computeGraph " + "loop 2 " + " " + numEdges);
			
			next;
			
			//x10.io.Console.OUT.println("computeGraph " + "loop 3");
			for ((i) in tid*chunkSize..(tid+1)*chunkSize-1) {
				val u = (SDGdata.startVertex as Rail[types.VERT_T])(i);
				val j = numEdges(u) + pos(i);
				endV(j) = (SDGdata.endVertex as Rail[types.VERT_T])(i);
				w(j) = (SDGdata.weight as Rail[types.LONG_T])(i);
			}
			
			next;
		}
		c.drop();
		}
		
		
		time = util.x10_get_wtime() - time; 
		
		val G  = defs.graph(n, m, endV, numEdges, w);
		
		/* for ((i) in 0..SDGdata.m-1) {
			 * x10.io.Console.OUT.println("[ " + (SDGdata.startVertex as Rail[types.VERT_T]!)(i) + " " + (SDGdata.endVertex as Rail[types.VERT_T]!)(i) + " " + (SDGdata.weight as Rail[types.LONG_T]!)(i) + "]"); 
		* }
		* 
		* for((i) in 0..G.n-1) {
			* x10.io.Console.OUT.println("[ " + (G.numEdges as Rail[types.LONG_T]!)(i) + "] " );
		* }
		* 
		* for ((i) in 0..G.n-1) {
			* val lo = (G.numEdges as Rail[types.LONG_T]!)(i);
			* val hi = (G.numEdges as Rail[types.LONG_T]!)(i+1);
			* for ((j) in  lo..hi-1) {
				* x10.io.Console.OUT.println("[ " + i + " " + (G.endV as Rail[types.VERT_T]!)(j) +  " " + (G.weight as Rail[types.LONG_T]!)(j) + "] " );
			* }
		* } */
		
		return Pair[Double,defs.graph](time, G);
	}
};
