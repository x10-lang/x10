package util;

import x10.compiler.*;

@NativeCPPInclude("pgas_collectives.h")
final public class Comm {

    private val my_id:Int;

    private static class Integer {
        var value:Int;
        
        def this(i:Int) { value = i; }
    }

    private static world = PlaceLocalHandle.make[Comm](Dist.makeUnique(), ()=>new Comm(0));;

    private static last_id = PlaceLocalHandle.make[Integer](Dist.makeUnique(), ()=>new Integer(0));

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

    public def broadcast(a:Array[Int], rootRank:Int) {
        @Native("c++",
            "void* buf = a->raw()->raw();" +
            "unsigned len = a->FMGL(size);" +
            "void *r = __pgasrt_tspcoll_ibcast(FMGL(my_id),  rootRank, buf, buf, len*sizeof(x10_int));" +
            "x10::lang::Runtime::increaseParallelism();" +
            "while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
            "x10::lang::Runtime::decreaseParallelism(1);") {}
    }

    public def broadcast_d(a:Array[Double], rootRank:Int) {
        @Native("c++",
            "void* buf = a->raw()->raw();" +
            "unsigned len = a->FMGL(size);" +
            "void *r = __pgasrt_tspcoll_ibcast(FMGL(my_id),  rootRank, buf, buf, len*sizeof(x10_double));" +
            "x10::lang::Runtime::increaseParallelism();" +
            "while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();" +
            "x10::lang::Runtime::decreaseParallelism(1);") {}
    }

    public def alltoall(a:Array[Double], b:Array[Double], chunkSize:Int) {
        @Native("c++",
            "void *r = __pgasrt_tspcoll_ialltoall(FMGL(my_id),  a->raw()->raw(), b->raw()->raw(), chunkSize*sizeof(x10_double));" +
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
}
