package x10.compiler.ws;

import x10.compiler.Header;
import x10.compiler.Inline;
import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.compiler.NativeCPPInclude;
import x10.compiler.NativeCPPCompilationUnit;
import x10.compiler.Uninitialized;
import x10.util.Random;

public abstract class Frame {
    @Native("java", "((#4) #7)")
    @Native("c++", "static_cast<#U >(#x)")
    public native static def cast[T,U](x:T):U;

    @Native("java", "((#4) #7)")
    @Native("c++", "(#x)")
    public native static def upcast[T,U](x:T):U;

    @Native("java", "null")
    @Native("c++", "NULL")
    public native static def NULL[T]():T;

    @Native("java", "(#1 == #2)")
    @Native("c++", "(#x == #y)")
    public native static def eq(x:Frame, y:FinishFrame):Boolean;

    public val up:Frame;

    // constructor
    @Header public def this(up:Frame) {
        this.up = up;
    }

    // copy methods
    public abstract def remap():Frame;

    public def realloc() = remap();

    public def back(worker:Worker, frame:Frame) {}

    public def resume(worker:Worker) {}
}
