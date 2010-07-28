package ssca2;

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;
import x10.util.*;

@NativeCPPInclude("pgasrt.h") {} 

final public class Comm {
	
	private global val my_id:Int;

// buffer used in alltoallv
global val srcSizes : Rail[long]!;
global val dstSizes : Rail[long]!;

private static class Integer {
	var value:Int;

def this(i:Int) { value = i; }
}

private const world = PlaceLocalHandle.make[Comm](Dist.makeUnique(), ()=>new Comm(0));;

private const last_id = PlaceLocalHandle.make[Integer](Dist.makeUnique(), ()=>new Integer(0));

private def this(new_id:Int) {
	my_id = new_id;
	val nplaces = Place.MAX_PLACES;
	srcSizes = Rail.make[long](nplaces, (i:Int)=>0l);
	dstSizes = Rail.make[long](nplaces, (i:Int)=>0l);
}

public static def WORLD() = world();

public def split(color:Int, rank:Int) {
	val new_id = ++last_id().value;
	{ @Native("c++", "__pgasrt_tspcoll_comm_split(FMGL(my_id), new_id, color, rank);") {} }
	return new Comm(new_id);
}

public def barrier() {
	@Native("c++",
			"void *r = __pgasrt_tspcoll_ibarrier(FMGL(my_id));" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);") {}
}

public def broadcast(a:Rail[Int]!, rootRank:Int) {
	@Native("c++",
			"void* buf = a->raw();" +
			"unsigned len = a->FMGL(length);" +
			"void *r = __pgasrt_tspcoll_ibcast(FMGL(my_id),  rootRank, buf, buf, len*sizeof(x10_int));" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);") {}
}

public def broadcast_d(a:Rail[Double]!, rootRank:Int) {
	@Native("c++",
			"void* buf = a->raw();" +
			"unsigned len = a->FMGL(length);" +
			"void *r = __pgasrt_tspcoll_ibcast(FMGL(my_id),  rootRank, buf, buf, len*sizeof(x10_double));" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);") {}
}

public def sum(i:Long): Long {
	@Native("c++",
			"x10_long val2;" +
			"void *r = __pgasrt_tspcoll_iallreduce(FMGL(my_id), &i, &val2, PGASRT_OP_ADD, PGASRT_DT_llg, 1);" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);" +
			"return val2;") { return i; }
}

public def sum(i:Int):Int {
	@Native("c++",
			"x10_int val2;" +
			"void *r = __pgasrt_tspcoll_iallreduce(FMGL(my_id), &i, &val2, PGASRT_OP_ADD, PGASRT_DT_int, 1);" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);" +
			"return val2;") { return i; }
}

public def sum(d:Double):Double {
	@Native("c++",
			"x10_double val2;" +
			"void *r = __pgasrt_tspcoll_iallreduce(FMGL(my_id), &d, &val2, PGASRT_OP_ADD, PGASRT_DT_dbl, 1);" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);" +
			"return val2;") { return d; }
}

public def min(i:Int):Int {
	@Native("c++",
			"x10_int val2;" +
			"void *r = __pgasrt_tspcoll_iallreduce(FMGL(my_id), &i, &val2, PGASRT_OP_MIN, PGASRT_DT_int, 1);" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);" +
			"return val2;") { return i; }
}

public def min(d:Double):Double {
	@Native("c++",
			"x10_double val2;" +
			"void *r = __pgasrt_tspcoll_iallreduce(FMGL(my_id), &d, &val2, PGASRT_OP_MIN, PGASRT_DT_dbl, 1);" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);" +
			"return val2;") { return d; }
}

public def max(i:Int):Int {
	@Native("c++",
			"x10_int val2;" +
			"void *r = __pgasrt_tspcoll_iallreduce(FMGL(my_id), &i, &val2, PGASRT_OP_MAX, PGASRT_DT_int, 1);" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);" +
			"return val2;") { return i; }
}

public def max(d:Double):Double {
	@Native("c++",
			"x10_double val2;" +
			"void *r = __pgasrt_tspcoll_iallreduce(FMGL(my_id), &d, &val2, PGASRT_OP_MAX, PGASRT_DT_dbl, 1);" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);" +
			"return val2;") { return d; }
}

public def indexOfAbsMax(d:Double, i:Int):Int {
	@Native("c++",
			"struct {double d; int i;} val = { fabs(d), i};" +
			"void *r = __pgasrt_tspcoll_iallreduce(FMGL(my_id),  &val, &val, PGASRT_OP_MAX, PGASRT_DT_dblint, 1);" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);" +
			"return val.i;") { return i; }
}

public def maxPair(d:Pair[Double,Int]): Pair[Double,int] {
	val pair =  Pair[Double, Int](0.0, 0);
	{@Native("c++",
			"void *r = __pgasrt_tspcoll_iallreduce(FMGL(my_id),  &d, &pair, PGASRT_OP_MAX, PGASRT_DT_dblint, 1);" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);" ) {} }
	return pair;
}

public def minPair(d:Pair[Double,Int]): Pair[Double,int] {
	val pair =  Pair[Double, Int](0.0, 0);
	{ @Native("c++",
			"void *r = __pgasrt_tspcoll_iallreduce(FMGL(my_id),  &d, &pair, PGASRT_OP_MIN, PGASRT_DT_dblint, 1);" +
			"x10::lang::Runtime::increaseParallelism();" +
			"while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);" ) {} }
	return pair;
}




public def allgather[T](A: Rail[T]!, my_size: long) {
	val nplaces = Place.MAX_PLACES;
	val B = Rail.make[T](nplaces*my_size as Int);
	
	{ @Native ("c++",
			"void* r = __pgasrt_tspcoll_iallgather(FMGL(my_id), A->raw(), B->raw(), my_size*sizeof(FMGL(T))); " +
			"x10::lang::Runtime::increaseParallelism();" +
			"while(!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);"){} }
	
	return B;
}

public def allgatherv[T](A: GrowableRail[T]!, my_size: long) {
	val nplaces = Place.MAX_PLACES;
	
	{ @Native ("c++",
			"void* r = __pgasrt_tspcoll_iallgather(FMGL(my_id), &my_size, FMGL(dstSizes)->raw(), sizeof(long)); " +
			"x10::lang::Runtime::increaseParallelism();" +
			"while(!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
			"x10::lang::Runtime::decreaseParallelism(1);"){} }
	
	//      x10.io.Console.ERR.println(dstSizes);
	
	var size: Int = 0;
	for((i) in 0..nplaces-1) {
		size += dstSizes(i);
	}
	val B: Rail[T]! = Rail.make[T](size);
	
	{ @Native ("c++",
			"size_t* destSizes = new size_t[nplaces];" +
			"for (int i =0 ;i < nplaces; i++)" +
			"{" +
				"destSizes[i] = FMGL(dstSizes)->raw()[i] * sizeof(FMGL(T));" +
				//"printf(\"hii %d\\n\", FMGL(dstSizes)->raw()[i]);" +
				"}"  +
				"void* r = __pgasrt_tspcoll_iallgatherv(FMGL(my_id), (void*) A->raw(), (void*) B->raw(), destSizes);" +
				"x10::lang::Runtime::increaseParallelism();" +
				"while(!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
				"x10::lang::Runtime::decreaseParallelism(1);" +
				"delete [] destSizes;"
	){} }
	
	
	return B;
}


public def alltoallv[T] (A: Rail[GrowableRail[T]]!, B: GrowableRail[T]!) {
	val nplaces = A.length();
	var dummy: Int;
	
	//for ((i) in 0..Place.MAX_PLACES-1) {
		for (var i: Int = 0; i < Place.MAX_PLACES; i++) {
			dstSizes(i) = 0;
			srcSizes(i) = A(i).length();
		}
		
		//x10.io.Console.ERR.println(srcSizes + " " + dstSizes);
		
		barrier();
		{ @Native ("c++",
				"void* r = __pgasrt_tspcoll_ialltoall(FMGL(my_id), FMGL(srcSizes)->raw(), FMGL(dstSizes)->raw(), sizeof(long)); " +
				"x10::lang::Runtime::increaseParallelism();" +
				"while(!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
				"x10::lang::Runtime::decreaseParallelism(1);"){} }
		
		
		//x10.io.Console.ERR.println(srcSizes + " " + dstSizes);
		
		var size: Int = 0;
		//for((i) in 0..nplaces-1) {
			for (var i: Int = 0; i < nplaces; i++) {
				size += dstSizes(i);
			}
			
			B.setLength(size);
			
			barrier();
			
			{ @Native ("c++",
					"FMGL(T)** dstOffset = new FMGL(T)*[nplaces];" +
					"FMGL(T)** srcOffset = new FMGL(T)*[nplaces];" +
					"size_t* srcsSizes = new size_t[nplaces];" +
					"size_t* destSizes = new size_t[nplaces];" +
					"FMGL(T)* offset = B->raw();" +
					"for (int i =0 ;i < nplaces; i++)" +
					"{dstOffset[i] = offset;" +
					"srcOffset[i] =  (A->raw())[i]->raw();" +
					"offset += FMGL(dstSizes)->raw()[i];" +
					"destSizes[i] = FMGL(dstSizes)->raw()[i] * sizeof(FMGL(T));" +
					"srcsSizes[i] = FMGL(srcSizes)->raw()[i] * sizeof(FMGL(T));" +
					//"printf(\"hello%d\\n\", ((A->raw()[i])->raw())[0]);" +
					"}" +
					"void* r = __pgasrt_tspcoll_ialltoallv(FMGL(my_id), (const void**) srcOffset, srcsSizes, (void**) dstOffset, destSizes);" +
					"x10::lang::Runtime::increaseParallelism();" +
					"while(!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
					"x10::lang::Runtime::decreaseParallelism(1);" +
					"delete [] dstOffset;" +
					"delete [] srcOffset;" +
					"delete [] destSizes;" +
					"delete [] srcsSizes;"
			)  {} }
			
			
			
			//x10.io.Console.ERR.println("end of alltoall");
			
		}
		
		public def usort[T](values: Rail[T]!, map: (T)=>Int): GrowableRail[T]! {
			val tmp: Rail[GrowableRail[T]]! = Rail.make[GrowableRail[T]](Place.MAX_PLACES, (i:Int)=>new GrowableRail[T](0));
		for ((i) in 0..values.length()-1) {
			tmp(map(values(i))).add(values(i));
		}
		val out_pairs = new GrowableRail[T](0);
		this.alltoallv[T](tmp, out_pairs);
		return out_pairs;
		}
		
		public def usort[T](values: Rail[T]!, map: (T)=>Int,g:GrowableRail[T]!) {
			val tmp: Rail[GrowableRail[T]]! = Rail.make[GrowableRail[T]](Place.MAX_PLACES, (i:Int)=>new GrowableRail[T](0));
		for ((i) in 0..values.length()-1) {
			tmp(map(values(i))).add(values(i));
		}
		this.alltoallv[T](tmp, g);
		}
		
		
		public def usort_val[V](values: Rail[V]!, map: (Int)=>Int,g:GrowableRail[V]!) {
			val tmp: Rail[GrowableRail[V]]! = Rail.make[GrowableRail[V]](Place.MAX_PLACES, (i:Int)=>new GrowableRail[V](0));
		for ((i) in 0..values.length()-1) {
			tmp(map(i)).add(values(i));
		}
		this.alltoallv[V](tmp, g);
		}
		
		public def usort[T](values: GrowableRail[T]!, map: (T)=>Int) {
			val tmp: Rail[GrowableRail[T]]! = Rail.make[GrowableRail[T]](Place.MAX_PLACES, (i:Int)=>new GrowableRail[T](0));
		for ((i) in 0..values.length()-1) {
			tmp(map(values(i))).add(values(i));
		}
		val g = new GrowableRail[T](0);
		this.alltoallv[T](tmp, g);
		return g;
		}
		
		
	}
