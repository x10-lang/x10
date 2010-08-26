package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.runtime.impl.java.Deque", null, null)
@NativeRep("c++", "x10aux::ref<x10::lang::Deque>", "x10::lang::Deque", null)
public final class Deque {
    @Native("java", "#0.size()")
    @Native("c++", "(#0)->size()")
    public native def size():Int;

    @Native("java", "#0.poll()")
    @Native("c++", "(#0)->poll()")
    public native def poll():Object;

    @Native("java", "#0.push(#1)")
    @Native("c++", "(#0)->push(#1)")
    public native def push(t:Object):Void;

    @Native("java", "#0.steal()")
    @Native("c++", "(#0)->steal()")
    public native def steal():Object;
}
