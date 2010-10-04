package rc7;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("c++", "x10aux::ref<rc7::Comm>", "rc7::Comm", null)
final public class Comm {

    @Native("c++", "rc7::Comm::world()")
    public native static def WORLD():Comm!;
    
    @Native("c++", "(*#0).split(#1, #2)")
    public native def split(color:Int, rank:Int):Comm!;

    @Native("c++", "(*#0).barrier()")
    public native def barrier():Void;

    @Native("c++", "(*#0).broadcast((#1)->raw(), #2, (#1)->FMGL(length))")
    public native def broadcast(a:Rail[Int]!, rootRank:Int):Void;

    @Native("c++", "(*#0).broadcast((#1)->raw(), #2, (#1)->FMGL(length))")
    public native def broadcast_d(a:Rail[Double]!, rootRank:Int):Void;

    @Native("c++", "(*#0).reduce(#1, PGASRT_OP_ADD, PGASRT_DT_int)")
    public native def sum(i:Int):Int;
    
    @Native("c++", "(*#0).reduce(#1, PGASRT_OP_ADD, PGASRT_DT_dbl)")
    public native def sum(d:Double):Double;
    
    @Native("c++", "(*#0).reduce(#1, PGASRT_OP_MIN, PGASRT_DT_int)")
    public native def min(i:Int):Int;

    @Native("c++", "(*#0).reduce(#1, PGASRT_OP_MIN, PGASRT_DT_dbl)")
    public native def min(d:Double):Double;

    @Native("c++", "(*#0).reduce(#1, PGASRT_OP_MAX, PGASRT_DT_dbl)")
    public native def max(d:Double):Double;

    @Native("c++", "(*#0).reduce(#1, PGASRT_OP_MAX, PGASRT_DT_int)")
    public native def max(i:Int):Int;
    
    @Native("c++", "(*#0).reduce_di(fabs(#1), #2, PGASRT_OP_MAX, PGASRT_DT_dblint)")
    public native def indexOfAbsMax(d:Double, i:Int):Int;
}
