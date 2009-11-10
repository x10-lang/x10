package FT;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("c++", "x10aux::ref<Comm>", "Comm", null)
final public class Comm {

    @Native("c++", "Comm::world()")
    public native static def WORLD():Comm!;
    
    @Native("c++", "(*#0).barrier()")
    public native def barrier():Void;
}
