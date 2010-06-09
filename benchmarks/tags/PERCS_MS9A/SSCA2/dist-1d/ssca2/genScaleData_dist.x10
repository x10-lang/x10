package ssca2;

import x10.util.*;
import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;

 @NativeCPPInclude("iostream") {}
 @NativeCPPInclude("algorithm") {}
 @NativeCPPInclude("sprng.h") {}



public class genScaleData_dist  {

	
	public static def sprng_wrapper(val stream: Long): Double {
		var tmp: Double = -1.0;
	{@Native("c++", "tmp = sprng((int*) stream);") {} }
	return tmp; 
	}
	public static def init_sprng_wrapper(val pid: Int, val nplaces: Int, val seed: Int): Long{
		var tmp: Long = -1;
	{@Native("c++", "tmp = (long) init_sprng(SPRNG_LCG64, pid, nplaces, seed, SPRNG_DEFAULT);") {} }
	return tmp;
	}
	public static def compute(val GLOBALS: runtime_consts, pid: Int, world: Comm!, RVertices: PlaceLocalHandle[HashMap[types.VERT_T, types.VERT_T]], Out_pairs: PlaceLocalHandle[GrowableRail[types.UVPair]], val NOSELF: Boolean, val ALLGATHER: Boolean) {
		
		val nplaces = Place.MAX_PLACES;
		val n = GLOBALS.N;
		val m  = GLOBALS.M;
		
		
		val seed = 985456376;
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
			
			val   stream = init_sprng_wrapper(here.id, Place.MAX_PLACES, seed);
			for ((i) in 0..chunkSize_m-1) {
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
					//	x10.io.Console.ERR.println("u v " + pid + " " +  u + " " + v + " " + p1 + " " + GLOBALS.SCALE);
					}
				} while (NOSELF && u == v);
				
				src(i)= u-1;
				dest(i) = v-1;
			}
			
		 	
			//x10.io.Console.ERR.println(src + " " + dest);
			
		
                        val rpairs = Rail.make[types.UVPair](chunkSize_n);	
                        var k: Int = 0;
			for ((i) in  pid*chunkSize_n..(pid+1)*chunkSize_n-1) {
				val j = (n*sprng_wrapper(stream)) as types.VERT_T;
                                rpairs(k++)= types.UVPair(j,i);
			}

	
                        val size = rpairs.length();
	                {@Native("c++", "std::sort((std::pair<int, int>*) (rpairs->raw()), ((std::pair<int,int>*)(rpairs->raw())) + size);") {} }


                        val out_pairs = Out_pairs();
                        val tmp_pairs = new GrowableRail[types.UVPair](0);
                        world.usort[types.UVPair](rpairs, (i:types.UVPair)=>(i.first/(n/Place.MAX_PLACES)), tmp_pairs);


                        val size2 = tmp_pairs.length();
	                //{@Native("c++", "mysort( tmp_pairs->raw(), size2);") {} }
	                {@Native("c++", "std::sort((std::pair<int, int>*) (tmp_pairs->raw()), ((std::pair<int,int>*)(tmp_pairs->raw())) + size2);") {} }

                        val etriplets = Rail.make[types.UVWTriplet](chunkSize_m);	
                     
                        if (ALLGATHER) { 
                          val out_pairs2 = world.allgatherv[types.UVPair](tmp_pairs, tmp_pairs.length());
			  for ((i) in  0..chunkSize_m-1) {
			       val wt = (1 + GLOBALS.MaxIntWeight * sprng_wrapper(stream)) as types.WEIGHT_T;
                               val offset = (n/Place.MAX_PLACES)*here.id;
                               val u_new =  out_pairs2(src(i)).second;
                               val v_new =  out_pairs2(dest(i)).second;
			       etriplets(i) = types.UVWTriplet(u_new, v_new, wt);
                          }
                        } else {

                         val sizes = world.allgather[Int](Rail.make[Int](1, (i:Int)=>tmp_pairs.length()), 1);
                        val cum_sizes = Rail.make[Int](Place.MAX_PLACES);
                         cum_sizes(0) = 0;
                         for ((i) in 1..sizes.length()-1) {
                             cum_sizes(i) = sizes(i-1) + cum_sizes(i-1);            
                             //x10.io.Console.ERR.println(cum_sizes(i));
                         } 

                        for ((i) in 0..tmp_pairs.length()-1){ 
                           tmp_pairs(i) = types.UVPair(i + cum_sizes(here.id), tmp_pairs(i).second);
                           //x10.io.Console.ERR.println(tmp_pairs(i));
                        } 

                         world.usort[types.UVPair](tmp_pairs.toRail(), (i:types.UVPair)=>(i.first/(n/Place.MAX_PLACES)), out_pairs); 

                         val N = Rail.make[HashSet[types.VERT_T]!](Place.MAX_PLACES, (i:Int)=>new HashSet[types.VERT_T]());

                         val owner = (i:Int)=>i/(n/Place.MAX_PLACES);
			 for ((i) in  0..chunkSize_m-1) {
 
                         if (owner(src(i)) != here.id) N(owner(src(i))).add(src(i));
                          if (src(i) == dest(i)) continue;
                         if (owner(dest(i)) != here.id) N(owner(dest(i))).add(dest(i));
                        }

                       
                        finish for ((i) in 0..Place.MAX_PLACES-1) {
                            val v_iter = N(i).iterator() as Iterator[Int]!;
                            val v_clone = ValRail.make[types.VERT_T](N(i).size(), (n:Int)=>v_iter.next());
                            val origin = here;
                            async (Place.places(i)) {
                              val out_pairs = Out_pairs();
                              val offset = (n/Place.MAX_PLACES)*here.id;

                         //     for ((i) in 0..v_clone.length()-1) x10.io.Console.ERR.println(v_clone(i) + " " + out_pairs(v_clone(i)-offset));

                              val out_val = ValRail.make[types.UVPair](v_clone.length(), (n:Int)=>(types.UVPair(v_clone(n), (out_pairs as GrowableRail[types.UVPair]!)(v_clone(n)-offset).second)));
                               async (origin) {
                                   for ((k) in 0..out_val.length()-1) {
                                      val rvertices = RVertices();
                                      atomic rvertices.put(out_val(k).first, out_val(k).second);
                                   }
                               } 
                            }
                        } 
 
                          val rvertices = RVertices();
			for ((i) in  0..chunkSize_m-1) {
			       val wt = (1 + GLOBALS.MaxIntWeight * sprng_wrapper(stream)) as types.WEIGHT_T;

                               val offset = (n/Place.MAX_PLACES)*here.id;
                               val u_new = owner(src(i)) == here.id ? out_pairs(src(i)-offset).second : rvertices.get(src(i))();
                               val v_new = owner(dest(i)) == here.id ? out_pairs(dest(i)-offset).second : rvertices.get(dest(i))();
			       etriplets(i) = types.UVWTriplet(u_new, v_new, wt);
                        }

                      }
                        //x10.io.Console.ERR.println ("after " + pid + " "  +  etriplets);

			
			//free_sprng(stream(pid);
			
		
		return new defs.graphSDG_dist(m, n, etriplets);
	}
}; 

