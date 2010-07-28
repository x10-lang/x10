package ssca2;

import x10.util.*;
import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;

@NativeCPPInclude("iostream") {}
@NativeCPPInclude("algorithm") {}
@NativeCPPInclude("sprng.h") {}
@NativeCPPInclude("ValueSort.h") {}



public class GenScaleData  {
	
	
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
	public static def compute(val GLOBALS: Consts, pid: Int, world: Comm!, RVertices: PlaceLocalHandle[HashMap[Types.VERT_T, Types.VERT_T]], Out_pairs: PlaceLocalHandle[GrowableRail[Types.UVPair]], val NOSELF: Boolean, val ALLGATHER: Boolean) {
		
		val nplaces = Place.MAX_PLACES;
		val n = GLOBALS.N;
		val m  = GLOBALS.M;
		
		
		val seed = 985456376;
		
		val chunkSize_m = m/nplaces;
		val chunkSize_n = n/nplaces;
		val src = Rail.make[Types.VERT_T](chunkSize_m);
		val dest = Rail.make[Types.VERT_T](chunkSize_m);
		
		
		var u : Int;
		var v : Int;
		var step: Int;
		var av: Double;
		var bv: Double;
		var cv: Double;
		var dv: Double;
		
		//val   stream = init_sprng_wrapper(here.id, Place.MAX_PLACES, seed);
		
		val streams = nplaces > 256 ? 
				Rail.make[Long](1, (i: Int)=>init_sprng_wrapper(here.id, Place.MAX_PLACES, seed)) :
					Rail.make[Long](256, (i: Int)=>init_sprng_wrapper(i, 256, seed));
				
				val nstreams = streams.length();
				
				val streamSize_m = m / nstreams;
				var k : Int = 0;
				//for ((i) in 0..chunkSize_m-1) 
					for (var i: Int = here.id; i < m; i+= nplaces) {
						val stream = streams(i % nstreams);
						do{ 
							u = 1;
							v = 1;
							step = n/2;
							av = Defs.A;
							bv = Defs.B;
							cv = Defs.C;
							dv = Defs.D;
							
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
							
							//for ((j) in 1..GLOBALS.SCALE-1) {
								for (var j: Int = 1; j < GLOBALS.SCALE; j++) {
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
									//	x10.io.Console.OUT.println("u v " + pid + " " +  u + " " + v + " " + p1 + " " + GLOBALS.SCALE);
								}
							} while (NOSELF && u == v);
							
							src(k)= u-1;
							dest(k) = v-1;
							k++;
						}
						
						
						//x10.io.Console.OUT.println(src + " " + dest);
						
						val streamSize_n = n / nstreams;
						
						val rpairs = Rail.make[Types.UVPair](chunkSize_n);	
						k = 0;
						//for ((i) in  pid*chunkSize_n..(pid+1)*chunkSize_n-1) 
							for (var i: Int = here.id; i < n; i+= nplaces) {
								val stream = streams(i % nstreams);
								//val stream = streams(here.id + nplaces * (i/streamSize_n));
								val j = (n*sprng_wrapper(stream)) as Types.VERT_T;
								rpairs(k++)= Types.UVPair(j, i);
							}
						
						
						
						val out_pairs = Out_pairs();
						val tmp_pairs = new GrowableRail[Types.UVPair](0);
						world.usort[Types.UVPair](rpairs, (i:Types.UVPair)=>(i.first/(n/Place.MAX_PLACES)), tmp_pairs);
						
						
						//{@Native("c++", "struct ValueSort { bool operator() (std::pair<int, int> a, std::pair<int, int> b) {return a.second < b.second;}};" +
						val size2 = tmp_pairs.length();
						//{@Native("c++", "mysort( tmp_pairs->raw(), size2);") {} }
						{@Native("c++", "std::sort((std::pair<int, int>*) (tmp_pairs->raw()), ((std::pair<int,int>*)(tmp_pairs->raw())) + size2, ValueSort());") {} }
						{@Native("c++", "std::stable_sort((std::pair<int, int>*) (tmp_pairs->raw()), ((std::pair<int,int>*)(tmp_pairs->raw())) + size2);") {} }
						
						val edges = Rail.make[Types.EDGE_T](chunkSize_m);	
						
						if (ALLGATHER) { 
							val out_pairs2 = world.allgatherv[Types.UVPair](tmp_pairs, tmp_pairs.length());
							//x10.io.Console.OUT.println(out_pairs2);
							k = 0;
							//for ((i) in  0..chunkSize_m-1) 
								for (var i: Int = here.id; i < m; i += nplaces) {
									val stream = streams(i%nstreams);
									val wt = (1 + GLOBALS.MaxIntWeight * sprng_wrapper(stream)) as Types.WEIGHT_T;
									val u_new =  out_pairs2(src(k)).second;
									val v_new =  out_pairs2(dest(k)).second;
									edges(k) = Types.EDGE_T(u_new, v_new, wt);
									k++;
								}
							
							Runtime.deallocObject(out_pairs2);
						} else {
							
							val sizes = world.allgather[Int](Rail.make[Int](1, (i:Int)=>tmp_pairs.length()), 1);
							val cum_sizes = Rail.make[Int](Place.MAX_PLACES);
							cum_sizes(0) = 0;
							for ((i) in 1..sizes.length()-1) {
								cum_sizes(i) = sizes(i-1) + cum_sizes(i-1);            
								//x10.io.Console.OUT.println(cum_sizes(i));
							} 
							
							for ((i) in 0..tmp_pairs.length()-1){ 
								tmp_pairs(i) = Types.UVPair(i + cum_sizes(here.id), tmp_pairs(i).second);
								//x10.io.Console.OUT.println(tmp_pairs(i));
							} 
							
							world.usort[Types.UVPair](tmp_pairs.toRail(), (i:Types.UVPair)=>(i.first/(n/Place.MAX_PLACES)), out_pairs); 
							
							val N = Rail.make[HashSet[Types.VERT_T]!](Place.MAX_PLACES, (i:Int)=>new HashSet[Types.VERT_T]());
							
							val owner = (i:Int)=>i/(n/Place.MAX_PLACES);
							// for ((i) in  0..chunkSize_m-1) 
								for (var i: Int = 0; i < chunkSize_m; i++) {
									
									if (owner(src(i)) != here.id) N(owner(src(i))).add(src(i));
									if (src(i) == dest(i)) continue;
									if (owner(dest(i)) != here.id) N(owner(dest(i))).add(dest(i));
								}
							
							
							//finish for ((i) in 0..Place.MAX_PLACES-1) 
								finish for (var i: Int = 0; i < Place.MAX_PLACES; i++) {
									val v_iter = N(i).iterator() as Iterator[Int]!;
									val v_clone = ValRail.make[Types.VERT_T](N(i).size(), (n:Int)=>v_iter.next());
									val origin = here;
									async (Place.places(i)) {
										val out_pairs = Out_pairs();
										val offset = (n/Place.MAX_PLACES)*here.id;
										
										//     for ((i) in 0..v_clone.length()-1) x10.io.Console.OUT.println(v_clone(i) + " " + out_pairs(v_clone(i)-offset));
										
										val out_val = ValRail.make[Types.UVPair](v_clone.length(), (n:Int)=>(Types.UVPair(v_clone(n), (out_pairs as GrowableRail[Types.UVPair]!)(v_clone(n)-offset).second)));
										async (origin) {
											for ((k) in 0..out_val.length()-1) {
												val rvertices = RVertices();
												atomic rvertices.put(out_val(k).first, out_val(k).second);
											}
										} 
									}
									
									//Runtime.deallocObject(v_clone);
								} 
							
							val rvertices = RVertices();
							//for ((i) in  0..chunkSize_m-1) 
								k = 0;
							val offset = (n/Place.MAX_PLACES)*here.id;
							for (var i: Int = here.id; i < m; i += nplaces) {
								val stream = streams(i%nstreams);
								val wt = (1 + GLOBALS.MaxIntWeight * sprng_wrapper(stream)) as Types.WEIGHT_T;
								
								val u_new = owner(src(k)) == here.id ? out_pairs(src(k)-offset).second : rvertices.get(src(k))();
								val v_new = owner(dest(k)) == here.id ? out_pairs(dest(k)-offset).second : rvertices.get(dest(k))();
								edges(k) = Types.EDGE_T(u_new, v_new, wt);
								k++;
							}
							
						}
						//x10.io.Console.OUT.println ("after " + pid + " "  +  edges);
						
						
						//free_sprng(stream(pid);
						
						
						
						world.barrier();
						//x10.io.Console.OUT.println ("after genScalData"); 
						
						
						Runtime.deallocObject(rpairs);
						Runtime.deallocObject(tmp_pairs);
						Runtime.deallocObject(src);
						Runtime.deallocObject(dest);
						Runtime.deallocObject(Out_pairs());
						Runtime.deallocObject(RVertices());
						return edges;
						}
					}; 
					
