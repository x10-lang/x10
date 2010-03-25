package ssca2;

import x10.compiler.Native;
import x10.util.*;

final public class Comm {

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

    

    /* public def alltoallv (A: Rail[GrowableRail[types.VERT_T]!]!) {
      val nplaces = A.length();
      val srcSize = Rail.make[int](nplaces, (i:Int)=>A(i).length()); 
      val dstSize = Rail.make[int](nplaces, (i:Int)=>0);

      for((i) 0..nplaces-1) {
        A(i).toRail().copyTo(0, Place.places(i), ()=>{Buf(). 
      } 
     return B;
    } */

    public def alltoallv (A: Rail[GrowableRail[types.VERT_T]!]!) {
      val nplaces = A.length();
      val srcSize = Rail.make[int](nplaces, (i:Int)=>A(i).length()); 
      val dstSize = Rail.make[int](nplaces, (i:Int)=>0);
      var dummy: Int;

     { @Native ("c++",
               "MPI_Alltoall(srcSize->raw(), 1, MPI_INTEGER, dstSize->raw(), 1, MPI_INTEGER, MPI_COMM_WORLD);") {} }


      //if(false) x10.io.Console.OUT.println(srcSize + " " + dstSize);

      var size: Int = 0; 
      for((i) in 0..nplaces-1) {
         size += dstSize(i);
      } 

      val B = Rail.make[types.VERT_T](size);     

      { @Native ("c++",
               //"#include <mpi.h>\n" + 
               "int* dstOffset = new int[nplaces];" + 
               "int* srcOffset = new int[nplaces];" +
               "int* offset = B->raw();" + 
               " for (int i =0 ;i < nplaces; i++)" +
                      "{dstOffset[i] = (int) (offset - (int*) MPI_BOTTOM);" +
                      "srcOffset[i] =  (int) ((int*) (A->raw())[i]->_array->raw() - (int*) MPI_BOTTOM);" +  
                      "offset += dstSize->raw()[i];" + 
                      //"printf(\"hello%d\\n\", ((A->raw()[i])->_array->raw())[0]);" +
                      "}" + 
               "MPI_Alltoallv(MPI_BOTTOM, srcSize->raw(), (int*) srcOffset,  MPI_INTEGER, MPI_BOTTOM, dstSize->raw(), (int*) dstOffset, MPI_INTEGER, MPI_COMM_WORLD);" ) {}         }
     
      //if(false) x10.io.Console.OUT.println("end of alltoall");

      return B;
    }

}
