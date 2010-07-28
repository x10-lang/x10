package ssca2;

import x10.util.*;
import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;

@NativeCPPInclude("iostream") {}
@NativeCPPInclude("algorithm") {}
@NativeCPPInclude("sprng.h") {}

public class GenScaleDataSeq  {
	
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
	public static def compute(val NOSELF:Boolean) { 
		
		
		/* val rand = PTimer.make("random");
		 * val sort = PTimer.make("sort");
		 * val perm = PTimer.make("permute");
		 * val assign = PTimer.make("assign"); */
		 
		 val GLOBALS = at (Defs.container) (Defs.container.globals);
		 
		 val nthreads = 1; //  util.x10_get_num_threads();
		 val n = GLOBALS.N;
		 val m  = GLOBALS.M;
		 
		 val src = Rail.make[Types.VERT_T](m);
		 val dest = Rail.make[Types.VERT_T](m);
		 val wt = Rail.make[Types.WEIGHT_T](m);
		 
		 val seed = 2387; 
		 
		 val stream = init_sprng_wrapper(0, 1, seed);
		 var u : Int;
		 var v : Int;
		 var step: Int;
		 var av: Double;
		 var bv: Double;
		 var cv: Double;
		 var dv: Double;
		 
		 for (var i: Int = 0; i < m ;i ++) {
			 do {
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
				 
				 //for ((j) in 1..GLOBALS.SCALE-1) 
					 for (var j : Int = 1; j < GLOBALS.SCALE; j++) {
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
			 } while (u==v && NOSELF);
			 
			 src(i)= u-1;
			 dest(i) = v-1;
		 }
		 
		 //rand.stop();
		 
		 
		 //x10.io.Console.ERR.println(src + " " + dest);
		 
		 //perm.start(); 
		 val permV = Rail.make[Types.VERT_T](n, (i:Int)=>i);
		 for (var i: Int =  0; i < n; i++) {
			 val j = (n*sprng_wrapper(stream)) as Types.VERT_T;
			 if (i != j) {
				 val tmp = permV(i);
				 permV(i) = permV(j);
				 permV(j) = tmp;
			 }
		 } 
		 //perm.stop();
		 
		 //assign.start();		
		 //x10.io.Console.ERR.println("perm "  +  permV);
		 val edges = ValRail.make[Types.EDGE_T](m, 
				 (i:Int)=>Types.EDGE_T(permV(src(i)), permV(dest(i)), 
						 (1 + GLOBALS.MaxIntWeight * sprng_wrapper(stream)) as Types.WEIGHT_T));
		 
		 
		 Runtime.deallocObject(permV);
		 Runtime.deallocObject(src);
		 Runtime.deallocObject(dest);
		 return edges;
	}
}; 

