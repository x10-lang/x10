package ssca2;

import x10.util.*;
import x10.compiler.Native;

public class genScaleData  {
	
	public static def sprng_wrapper(val stream: Long): Double {
		var tmp: Double = -1.0;
	{@Native("c++", "tmp = sprng((int*) stream);") {} }
	return tmp; 
	}
	public static def init_sprng_wrapper(val tid: Int, val nthreads: Int, val seed: Int): Long{
		var tmp: Long = -1;
	{@Native("c++", "tmp = (long) init_sprng(0, tid, nthreads, seed, SPRNG_DEFAULT);") {} }
	return tmp;
	}
	public static def compute():  Pair[Double, defs.graphSDG] {
		
		val GLOBALS = at (defs.container) (defs.container.globals);
		
		val nthreads = 1; //  util.x10_get_num_threads();
		val n = GLOBALS.N;
		val m  = GLOBALS.M;
		
		val src = Rail.make[types.VERT_T](m);
		val dest = Rail.make[types.VERT_T](m);
		val  permV = Rail.make[types.VERT_T](n);
		val wt = Rail.make[types.WEIGHT_T](m);
		
		val seed = 2387;
		var elapsed_time: Double = util.get_seconds();
		val stream = Rail.make[Long](nthreads);
		
		finish  {
			
			val c: Clock = Clock.make();
		
		
		foreach((tid) in 0..nthreads-1) {
			
			stream(tid) = init_sprng_wrapper(tid, nthreads, seed);
			
			next;
			
			val chunkSize_m = m/nthreads;
			val chunkSize_n = n/nthreads;
			var u : Int;
			var v : Int;
			var step: Int;
			var av: Double;
			var bv: Double;
			var cv: Double;
			var dv: Double;
			
			for ((i) in tid*chunkSize_m..(tid+1)*chunkSize_m-1) {
				do{ 
					u = 1;
					v = 1;
					step = n/2;
					av = defs.A;
					bv = defs.B;
					cv = defs.C;
					dv = defs.D;
					
					val p = sprng_wrapper(stream(tid));
					if (p < av) {
						/* Do nothing */
					} else if ((p >= av) && (p < av+bv)) {
						v += step;
					} else if ((p >= av+bv) && (p < av+bv+cv)) {
						u += step;
					} else {
						u += step;
						v += step;
					}
					
					for ((j) in 1..GLOBALS.SCALE-1) {
						step = step/2;
						
						/* Vary a,b,c,d by up to 10% */
						val vary = 0.1;
						av *= 0.95 + vary * sprng_wrapper(stream(tid));
						bv *= 0.95 + vary * sprng_wrapper(stream(tid));
						cv *= 0.95 + vary * sprng_wrapper(stream(tid));
						dv *= 0.95 + vary * sprng_wrapper(stream(tid));
						
						val S = av + bv + cv + dv;
						av = av/S;
						bv = bv/S;
						cv = cv/S;
						dv = dv/S;
						
						/* Choose partition */
						val p1 = sprng_wrapper(stream(tid));
						if (p1 < av) {
							/* Do nothing */
						} else if ((p1 >= av) && (p1 < av+bv)) {
							v += step;
						} else if ((p1 >= av+bv) && (p1 < av+bv+cv)) {
							u += step;
						} else {
							u += step;
							v += step;
						}
						//x10.io.Console.OUT.println("u v " + tid + " " +  u + " " + v + " " + p1 + " " + GLOBALS.SCALE);
					}
				} while (u == v);
				
				src(i)= u-1;
				dest(i) = v-1;
			}
			
			next;
			
			//x10.io.Console.OUT.println(src + " " + dest);
			for ((i) in tid*chunkSize_n..(tid+1)*chunkSize_n-1) {
				permV(i) = i;
			}
			
			next;
			
			for ((i) in tid*chunkSize_n..(tid+1)*chunkSize_n-1) {
				val j = (n*sprng_wrapper(stream(tid))) as types.VERT_T;
				if (i != j) {
					atomic {
						val tmpVal = permV(i);
						permV(i) = permV(j);
						permV(j) = tmpVal;
					}
				}
			}
			
			next; 
			
			for ((i) in tid*chunkSize_m..(tid+1)*chunkSize_m-1) {
				src(i) = permV(src(i));
				dest(i) = permV(dest(i));
			}
			// x10.io.Console.OUT.println("srcs " + src + "dests" + dest);
			
			next;
			
			for ((i) in   tid*chunkSize_m..(tid+1)*chunkSize_m-1) {
				wt(i) = (1 + GLOBALS.MaxIntWeight * sprng_wrapper(stream(tid))) as types.WEIGHT_T;
			} 
			
			next;
			
			//free_sprng(stream(tid);
			
			next;
		}
		
		c.drop();
		}
		
		elapsed_time = util.get_seconds() - elapsed_time;
		return Pair[Double, defs.graphSDG](elapsed_time, defs.graphSDG(m, n, src, dest, wt));
	}
}; 

