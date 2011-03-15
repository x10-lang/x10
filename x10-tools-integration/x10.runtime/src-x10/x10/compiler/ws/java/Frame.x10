package x10.compiler.ws.java;

import x10.compiler.Native;

abstract public class Frame {
    @Native("java", "((#4) #7)")
    @Native("c++", "static_cast<#U >(#x)")
    public native static def cast[T,U](x:T):U;

    public val up:Frame;

    public def this(up:Frame) {
        this.up = up;
    }

    public def back(worker:Worker, frame:Frame) {}

    public def resume(worker:Worker) {}
}
