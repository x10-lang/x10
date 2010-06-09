package ssca2;

import x10.util.*;

public class util {
	public static val thisInstance: util! = new util();
	
	public static def srand48(val seed: types.LONG_T) { thisInstance.rand = new Random(seed); }
	public static def lrand48(): types.LONG_T { return thisInstance.rand.nextInt(defs.container.globals.N) as types.LONG_T; }
	public static def drand48(): double { return thisInstance.rand.nextDouble(); } 
	public static def get_seconds() { return x10.util.Timer.nanoTime()*1e-9; }
	public static def x10_get_wtime() { return x10.util.Timer.nanoTime()*1e-9; }
	
	var nthreads: Int;
	var rand: Random!;
	public static def x10_set_num_threads(THREADS: int) {
		thisInstance.nthreads = THREADS;
	}
	public static def x10_get_num_threads(): Int {
		return thisInstance.nthreads;
	}
	
	public def this() {}
	public static def prefix_sums(input:Rail[types.LONG_T]!, result:Rail[types.LONG_T]!, p: Rail[types.LONG_T]!, val n: types.LONG_T, tid: int){
		
		val nthreads =  x10_get_num_threads();
		val r = n/nthreads;
		
		result(0) = 0;
		
		
		for ((i) in x10.lang.Math.max(1,tid*r)..(tid+1)*r-1) {
			result(i) = input(i-1);
		}
		
		next;
		
		val start =  tid*r + 1;
		val end   = tid == nthreads-1? n+1 : (tid+1)*r;
		
		for ((j) in start..end-1){
			result(j) = input(j-1) + result(j-1);
		}
		p(tid) = result(end-1);
		
		next;
		
		if (tid == 0) {
			for ((j) in 1..nthreads-1){
				p(j) += p(j-1);
			}
		}
		
		next;
		
		if (tid>0) {
			val add_value=p(tid-1);
			for (var j: Int=start-1; j<end; j++)
				result(j) += add_value;
		}
		
		next;
	}
	
};
