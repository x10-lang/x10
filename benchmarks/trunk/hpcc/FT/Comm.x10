package FT;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;

@NativeRep("c++", "x10aux::ref<FT::Comm>", "FT::Comm", null)
@NativeCPPInclude("Comm.h")
@NativeCPPCompilationUnit("Comm.cc")
final public class Comm {

    @Native("c++", "FT::Comm::world()")
    public native static def WORLD():Comm!;
    
    @Native("c++", "(*#0).barrier()")
    public native def barrier():Void;

    @Native("c++", "(*#0).alltoall((#1)->raw(), (#2)->raw(), sizeof(double)*(#3))")
    public native def alltoall(A:Rail[Double]!, B:Rail[Double]!, chunk:Int):Void;
}
