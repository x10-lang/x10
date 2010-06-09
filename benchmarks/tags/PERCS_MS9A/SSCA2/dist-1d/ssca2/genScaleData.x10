package ssca2;

import x10.util.*;
import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;

 @NativeCPPInclude("iostream") {}
 @NativeCPPInclude("algorithm") {}
 @NativeCPPInclude("sprng.h") {}

public class genScaleData  {
	
	public static def sprng_wrapper(val stream: Long): Double {
		var tmp: Double = -1.0;
	{@Native("c++", "tmp = sprng((int*) stream);") {} }
	return tmp; 
	}
	public static def init_sprng_wrapper(val tid: Int, val nthreads: Int, val seed: Int): Long{
		var tmp: Long = -1;
	{@Native("c++", "tmp = (long) init_sprng(SPRNG_LCG64, tid, nthreads, seed, SPRNG_DEFAULT);") {} }
	return tmp;
	}
	public static def compute():  Pair[Double, defs.graphSDG] {


		/* val rand = PTimer.make("random");
		val sort = PTimer.make("sort");
		val perm = PTimer.make("permute");
		val assign = PTimer.make("assign"); */
		
		val GLOBALS = at (defs.container) (defs.container.globals);
		
		val nthreads = 1; //  util.x10_get_num_threads();
		val n = GLOBALS.N;
		val m  = GLOBALS.M;
		
		val src = Rail.make[types.VERT_T](m);
		val dest = Rail.make[types.VERT_T](m);
		val wt = Rail.make[types.WEIGHT_T](m);
		
		val seed = 985456376;
		var elapsed_time: Double = util.get_seconds();
		
		val stream = init_sprng_wrapper(0, 1, seed);
		finish  {
			
		
                //rand.start();
		for((tid) in 0..nthreads-1) {
			
			val chunkSize_m = m/nthreads;
			val chunkSize_n = n/nthreads;
			var u : Int;
			var v : Int;
			var step: Int;
			var av: Double;
			var bv: Double;
			var cv: Double;
			var dv: Double;
			
			//for ((i) in tid*chunkSize_m..(tid+1)*chunkSize_m-1) {
			for (var i: Int = tid*chunkSize_m; i < (tid+1)*chunkSize_m; i++) {

				do{ 
					u = 1;
					v = 1;
					step = n/2;
					av = defs.A;
					bv = defs.B;
					cv = defs.C;
					dv = defs.D;
					
					val p = sprng_wrapper(stream);
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
						av *= 0.95 + vary * sprng_wrapper(stream);
						bv *= 0.95 + vary * sprng_wrapper(stream);
						cv *= 0.95 + vary * sprng_wrapper(stream);
						dv *= 0.95 + vary * sprng_wrapper(stream);
						
						val S = av + bv + cv + dv;
						av = av/S;
						bv = bv/S;
						cv = cv/S;
						dv = dv/S;
						
						/* Choose partition */
						val p1 = sprng_wrapper(stream);
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
						//x10.io.Console.ERR.println("u v " + tid + " " +  u + " " + v + " " + p1 + " " + GLOBALS.SCALE);
					}
				} while (u == v);
				
				src(i)= u-1;
				dest(i) = v-1;
			}

                        //rand.stop();
			
			
			//x10.io.Console.ERR.println(src + " " + dest);
          
                         //perm.start(); 
		         val kv = Rail.make[types.UVPair](n);
			// for ((i) in tid*chunkSize_n..(tid+1)*chunkSize_n-1) {
			for (var i: Int = tid*chunkSize_n; i < (tid+1)*chunkSize_n; i++) {
                                val j = (n*sprng_wrapper(stream)) as types.VERT_T;
				kv(i) = types.UVPair(j, i);
			} 
                         //perm.stop();
			
		/*	
			  for ((i) in tid*chunkSize_n..(tid+1)*chunkSize_n-1) {
                                for ((j) in (i+1)..(tid+1)*chunkSize_n-1) {
                                   atomic {
                                     if (kv(i).first > kv(j).first) {
                                        val tmp = kv(i);
                                        kv(i) = kv(j);
                                        kv(j) = tmp;      
                                      }
                                    }
                                }
                         } */ 

                	//sort.start(); 
			val size = kv.length();
			{@Native("c++", "std::sort((std::pair<int, int>*) (kv->raw()), ((std::pair<int,int>*)(kv->raw())) + size);") {} }

                        //{@Native("c++", "mysort( (kv->raw()), size);") {} }
			//sort.stop();

	
			//assign.start();		
			 //x10.io.Console.ERR.println("perm "  +  " " + key + " " + value);
			//for ((i) in tid*chunkSize_m..(tid+1)*chunkSize_m-1) {
			for (var i: Int = tid*chunkSize_m; i < (tid+1)*chunkSize_m; i++) {
				src(i) = kv(src(i)).second;
				dest(i) = kv(dest(i)).second;
				wt(i) = (1 + GLOBALS.MaxIntWeight * sprng_wrapper(stream)) as types.WEIGHT_T;
			}
			//assign.stop();
			
			//free_sprng(stream);
			
		}
		
		}
		
		elapsed_time = util.get_seconds() - elapsed_time;
		return Pair[Double, defs.graphSDG](elapsed_time, defs.graphSDG(m, n, src, dest, wt));
	}
}; 

