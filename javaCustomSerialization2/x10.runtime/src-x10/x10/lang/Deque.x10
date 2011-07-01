package x10.lang;

import x10.compiler.NativeRep;

/**
 * Native deque. Only to be used in the runtime implementation.
 * Temporarily public to enable use in WS runtime classes.
 * 
 * @author tardieu
 */
@NativeRep("java", "x10.runtime.impl.java.Deque", null, "x10.runtime.impl.java.Deque.$RTT")
@NativeRep("c++", "x10aux::ref<x10::lang::Deque>", "x10::lang::Deque", null)
public final class Deque {
    public native def size():Int;

    public native def poll():Object;

    public native def push(t:Object):void;

    public native def steal():Object;
}
