package ssca2;

import x10.compiler.Native;
import x10.util.*;

final public class Comm {

    public static type KVPair = Pair[Int, Int];
    public static type KVVTriplet = Triplet[Int, Int, Int];
    private global val my_id:Int;

    private static class Integer {
        var value:Int;
        
        def this(i:Int) { value = i; }
    }

    private const world = PlaceLocalHandle.make[Comm](Dist.makeUnique(), ()=>new Comm(0));;

    private const last_id = PlaceLocalHandle.make[Integer](Dist.makeUnique(), ()=>new Integer(0));

    private def this(new_id:Int) {
        my_id = new_id;
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

    public def allgatherv[T](A: Rail[T]!, my_size: long) {
      val nplaces = Place.MAX_PLACES;
      val dstSize = Rail.make[long](nplaces);
      val srcSize = Rail.make[long](nplaces);
     
     { @Native ("c++",
               "void* r = __pgasrt_tspcoll_iallgather(FMGL(my_id), &my_size, dstSize->raw(), sizeof(long)); " +
               "x10::lang::Runtime::increaseParallelism();" + 
               "while(!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" + 
               "x10::lang::Runtime::decreaseParallelism(1);"){} }

      x10.io.Console.OUT.println(dstSize);

      var size: Int = 0; 
      for((i) in 0..nplaces-1) {
         size += dstSize(i);
      } 
      val B: Rail[T]! = Rail.make[T](size);

       { @Native ("c++",
               " for (int i =0 ;i < nplaces; i++)" +
                      "{" + 
                      "dstSize->raw()[i] *= sizeof(FMGL(T));" + 
                      //"printf(\"hii %d\\n\", dstSize->raw()[i]);" +
                      "}"  + 
               "void* r = __pgasrt_tspcoll_iallgatherv(FMGL(my_id), (void*) A->raw(), (void*) B->raw(), (unsigned long*) dstSize->raw());" +
               "x10::lang::Runtime::increaseParallelism();" +
               "while(!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
               "x10::lang::Runtime::decreaseParallelism(1);"){} } 


      return B;
    }


    public def alltoallv[T] (A: Rail[GrowableRail[T]!]!) {
      val nplaces = A.length();
      val srcSize = Rail.make[long](nplaces, (i:Int)=>A(i).length() as Long); 
      val dstSize = Rail.make[long](nplaces, (i:Int)=>0l);
      var dummy: Int;
      //x10.io.Console.OUT.println(srcSize + " " + dstSize);

      barrier();
     { @Native ("c++",
               "void* r = __pgasrt_tspcoll_ialltoall(FMGL(my_id), srcSize->raw(), dstSize->raw(), sizeof(long)); " +
               "x10::lang::Runtime::increaseParallelism();" + 
               "while(!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" + 
               "x10::lang::Runtime::decreaseParallelism(1);"){} }


      //x10.io.Console.OUT.println(srcSize + " " + dstSize);

      var size: Int = 0; 
      for((i) in 0..nplaces-1) {
         size += dstSize(i);
      } 

      val B = Rail.make[T](size);     


      { @Native ("c++",
               "FMGL(T)** dstOffset = new FMGL(T)*[nplaces];" + 
               "FMGL(T)** srcOffset = new FMGL(T)*[nplaces];" +
               "FMGL(T)* offset = B->raw();" + 
               " for (int i =0 ;i < nplaces; i++)" +
                      "{dstOffset[i] = offset;" + 
                      "srcOffset[i] =  (A->raw())[i]->_array->raw();" + 
                      "offset += dstSize->raw()[i];" + 
                      "dstSize->raw()[i] *= sizeof(FMGL(T));" + 
                      "srcSize->raw()[i] *= sizeof(FMGL(T));" + 
                      //"printf(\"hello%d\\n\", ((A->raw()[i])->_array->raw())[0]);" +
                      "}" + 
               "void* r = __pgasrt_tspcoll_ialltoallv(FMGL(my_id), (const void**) srcOffset, (const unsigned long*) srcSize->raw(), (void**) dstOffset, (const unsigned long*) dstSize->raw());" + 
                "x10::lang::Runtime::increaseParallelism();" + 
                "while(!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" + 
                "x10::lang::Runtime::decreaseParallelism(1);")  {} }
               
                 
     
      //if(false) x10.io.Console.OUT.println("end of alltoall");

      return B;
    }

      public def usort[T](values: Rail[T]!, map: (T)=>Int) {
          val tmp: Rail[GrowableRail[T]!]! = Rail.make[GrowableRail[T]!](Place.MAX_PLACES, (i:Int)=>new GrowableRail[T](0));
          for ((i) in 0..values.length()-1) {
             tmp(map(values(i))).add(values(i));
          }
          val out_pairs = this.alltoallv[T](tmp);
          return out_pairs;
        } 


      /* public def usort(pairs: Rail[KVPair]!, map: (Int)=>Int) {
          val tmp: Rail[GrowableRail[KVPair]!]! = Rail.make[GrowableRail[KVPair]!](Place.MAX_PLACES, (i:Int)=>new GrowableRail[KVPair](0));
          for ((i) in 0..pairs.length()-1) {
             tmp(map(pairs(i).first)).add(pairs(i));
          }
          val out_pairs = this.alltoallv[KVPair](tmp);
          return out_pairs;
        } 

      public def usort2(triplets: Rail[KVVTriplet]!, map: (Int)=>Int) {
          val tmp: Rail[GrowableRail[KVVTriplet]!]! = Rail.make[GrowableRail[KVVTriplet]!](Place.MAX_PLACES, (i:Int)=>new GrowableRail[KVVTriplet](0));
          for ((i) in 0..triplets.length()-1) {
             tmp(map(triplets(i).first)).add(triplets(i));
          }
          val out_triplets = this.alltoallv[KVVTriplet](tmp);
          return out_triplets;
        }  */


}
