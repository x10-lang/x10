package ssca2;

import x10.util.*;
import x10.compiler.Native;

public class genScaleData_dist  {

	
	public static def sprng_wrapper(val stream: Long): Double {
		var tmp: Double = -1.0;
	{@Native("c++", "tmp = sprng((int*) stream);") {} }
	return tmp; 
	}
	public static def init_sprng_wrapper(val pid: Int, val nplaces: Int, val seed: Int): Long{
		var tmp: Long = -1;
	{@Native("c++", "tmp = (long) init_sprng(SPRNG_LCG64, pid, nplaces, 985456376, SPRNG_DEFAULT);") {} }
	return tmp;
	}
	public static def compute(val GLOBALS: runtime_consts, pid: Int, world: Comm!) {
		
		val nplaces = Place.MAX_PLACES;
		val n = GLOBALS.N;
		val m  = GLOBALS.M;
		
		
		val seed = 2387;
		var elapsed_time: Double = util.get_seconds();
		
		val chunkSize_m = m/nplaces;
		val chunkSize_n = n/nplaces;
		val src = Rail.make[types.VERT_T](chunkSize_m);
		val dest = Rail.make[types.VERT_T](chunkSize_m);
			
			
			var u : Int;
			var v : Int;
			var step: Int;
			var av: Double;
			var bv: Double;
			var cv: Double;
			var dv: Double;
			
			for ((i) in 0..chunkSize_m-1) {
			      val   stream = init_sprng_wrapper(i+pid*chunkSize_m, m, seed);
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
						x10.io.Console.OUT.println("u v " + pid + " " +  u + " " + v + " " + p1 + " " + GLOBALS.SCALE);
					}
				} while (u == v);
				
				src(i)= u-1;
				dest(i) = v-1;
			}
			
		 	
			x10.io.Console.OUT.println(src + " " + dest);
			
		
                        val rpairs = Rail.make[types.UVPair](chunkSize_n);	
                        var k: Int = 0;
			for ((i) in  pid*chunkSize_n..(pid+1)*chunkSize_n-1) {
			        val   stream = init_sprng_wrapper(i, n, seed);
				val j = (n*sprng_wrapper(stream)) as types.VERT_T;
                                rpairs(k++)= types.UVPair(j,i);
			}

                        for ((i) in 0..rpairs.length()-1) {
                           for ((j) in i+1..rpairs.length()-1) {
                             if (rpairs(i).first > rpairs(j).first) {
                                val tmp = rpairs(i);
                                rpairs(i) = rpairs(j);
                                rpairs(j) =  tmp;
                             }
                        }}
		

                         val out_pairs: Rail[types.UVPair]! = world.usort[types.UVPair](rpairs, (i:types.UVPair)=>(i.first/(n/Place.MAX_PLACES)));
                        //sort out_pairs -- TODO: use integer sort
                        for ((i) in 0..out_pairs.length()-1) {
                           for ((j) in i+1..out_pairs.length()-1) {
                             if (out_pairs(i).first > out_pairs(j).first) {
                                val tmp = out_pairs(i);
                                out_pairs(i) = out_pairs(j);
                                out_pairs(j) =  tmp;
                             }
                        }}

                        val out_pairs2 = world.allgatherv[types.UVPair](out_pairs, out_pairs.length());
                        //val out_pairs2 = rpairs;

			/* for ((i) in  0..chunkSize_m-1) {
                          if (owner(src(i)) != here.id) rvertices.add(src(i));
                          if (src(i) == dest(i) continue;
                          if (owner(dest(i)) != here.id) rvertices.add(dest(i));
                        }

                        val inrvertices: Rail[types.UVPair]! = world.usort[types.VERT_T](rvertices, (i:types.VERT_T)=>(i/(n/Place.MAX_PLACES))); */
                        
                        x10.io.Console.OUT.println ("permute " + out_pairs2);
                        val etriplets = Rail.make[types.UVWTriplet](chunkSize_m);	
			for ((i) in  0..chunkSize_m-1) {
			       val   stream = init_sprng_wrapper(i+pid*chunkSize_m, m, seed);
			       val wt = (1 + GLOBALS.MaxIntWeight * sprng_wrapper(stream)) as types.WEIGHT_T;
			       etriplets(i) = types.UVWTriplet(out_pairs2(src(i)).second, out_pairs2(dest(i)).second, wt);
                        }
                        //x10.io.Console.OUT.println ("after " + pid + " "  +  etriplets);

			
			//free_sprng(stream(pid);
			
		
		return new defs.graphSDG_dist(m, n, etriplets);
	}
}; 

